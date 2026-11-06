package com.jiancai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiancai.common.BusinessException;
import com.jiancai.entity.Inquiry;
import com.jiancai.entity.Merchant;
import com.jiancai.entity.Product;
import com.jiancai.mapper.MerchantMapper;
import com.jiancai.mapper.ProductMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 微信小程序订阅消息：access_token 缓存 + 模板消息发送
 * 模板「咨询回复通知」：回复人、回复时间、回复内容、咨询人、咨询内容
 * 字段名可通过 jiancai.wx.subscribe.fields.* 覆盖，默认 name1/date2/thing3/name4/thing5
 */
@Slf4j
@Service
public class WxSubscribeService {

    private static final String TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token";
    private static final String SEND_URL = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send";
    private static final String TEMPLATE_LIST_URL = "https://api.weixin.qq.com/wxaapi/newtmpl/gettemplate";
    private static final String MSG_SEC_CHECK_URL = "https://api.weixin.qq.com/wxa/msg_sec_check";
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Value("${jiancai.wx.appid:}")
    private String appid;

    @Value("${jiancai.wx.app-id:}")
    private String appIdAlias;

    @Value("${jiancai.wx.appId:}")
    private String appIdCamel;

    @Value("${jiancai.wx.secret:}")
    private String secret;

    @Value("${jiancai.wx.app-secret:}")
    private String secretAlias;

    @Value("${jiancai.wx.appSecret:}")
    private String secretCamel;

    @Value("${jiancai.wx.subscribe.quote-template-id:}")
    private String quoteTemplateId;

    @Value("${jiancai.wx.subscribe.quote-page:pages/inquiry/inquiry}")
    private String quotePage;

    @Value("${jiancai.wx.subscribe.miniprogram-state:formal}")
    private String miniprogramState;

    @Value("${jiancai.wx.subscribe.fields.replier:name1}")
    private String fieldReplier;

    @Value("${jiancai.wx.subscribe.fields.reply-time:date2}")
    private String fieldReplyTime;

    @Value("${jiancai.wx.subscribe.fields.reply-content:thing3}")
    private String fieldReplyContent;

    @Value("${jiancai.wx.subscribe.fields.asker:name4}")
    private String fieldAsker;

    @Value("${jiancai.wx.subscribe.fields.ask-content:thing5}")
    private String fieldAskContent;

    private volatile String accessToken;
    private volatile long accessTokenExpiresAt;

    /** 小程序端授权时需要的模板 ID；模板未配置时 enabled=false */
    public Map<String, Object> publicConfig() {
        Map<String, Object> map = new HashMap<>(4);
        map.put("enabled", isQuoteEnabled());
        map.put("templateId", templateId());
        map.put("page", quotePage == null ? "" : quotePage.trim());
        return map;
    }

    public boolean isQuoteEnabled() {
        return !templateId().isEmpty() && !resolveAppid().isEmpty() && !resolveSecret().isEmpty();
    }

    /** 商户报价后通知询价用户；任何失败只记日志，不影响报价主流程 */
    public void sendQuoteNotice(Inquiry inquiry) {
        if (inquiry == null || !isQuoteEnabled()) {
            return;
        }
        String openid = inquiry.getOpenid();
        if (openid == null || openid.isBlank()) {
            return;
        }
        try {
            String productName = "询价商品";
            if (inquiry.getProductId() != null) {
                Product product = productMapper.selectById(inquiry.getProductId());
                if (product != null && product.getName() != null && !product.getName().isBlank()) {
                    productName = product.getName();
                }
            }
            String merchantName = "商户";
            if (inquiry.getMerchantId() != null) {
                Merchant merchant = merchantMapper.selectById(inquiry.getMerchantId());
                if (merchant != null && merchant.getName() != null && !merchant.getName().isBlank()) {
                    merchantName = merchant.getName();
                }
            }
            String replyContent = replyContent(inquiry);
            String asker = maskContact(inquiry.getContact());
            sendMessage(openid, templateId(), quotePage, quoteData(merchantName, replyContent, asker, productName), false);
        } catch (Exception e) {
            log.warn("订阅消息发送失败 inquiryId={}, err={}", inquiry.getId(), e.getMessage());
        }
    }

    /** 拉取账号下的订阅消息模板，用于核对模板字段名 */
    public List<Map<String, Object>> listTemplates() {
        try {
            JsonNode result = getJson(TEMPLATE_LIST_URL, false);
            int errcode = result.path("errcode").asInt(0);
            if (errcode == 40001 || errcode == 42001) {
                result = getJson(TEMPLATE_LIST_URL, true);
                errcode = result.path("errcode").asInt(0);
            }
            if (errcode != 0) {
                throw new BusinessException("获取模板失败(" + errcode + "): " + result.path("errmsg").asText(""));
            }
            List<Map<String, Object>> list = new ArrayList<>();
            for (JsonNode item : result.path("data")) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("templateId", item.path("priTmplId").asText(""));
                row.put("title", item.path("title").asText(""));
                row.put("type", item.path("type").asInt(0));
                row.put("content", item.path("content").asText(""));
                row.put("example", item.path("example").asText(""));
                list.add(row);
            }
            return list;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("获取模板失败：" + e.getMessage());
        }
    }

    private JsonNode getJson(String url, boolean refreshToken) throws IOException, InterruptedException {
        String token = getAccessToken(refreshToken);
        HttpRequest request = HttpRequest.newBuilder(URI.create(url + "?access_token=" + token))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readTree(response.body());
    }

    /** 用户文本内容安全检测；接口异常时放行并记日志，避免聊天整体不可用 */
    public boolean isTextSafe(String openid, String content) {
        if (openid == null || openid.isBlank() || content == null || content.isBlank()) {
            return true;
        }
        try {
            Map<String, Object> body = new HashMap<>(6);
            body.put("openid", openid);
            body.put("scene", 2);
            body.put("version", 2);
            body.put("content", content);
            JsonNode result = postJson(MSG_SEC_CHECK_URL, body, false);
            int errcode = result.path("errcode").asInt(0);
            if (errcode == 40001 || errcode == 42001) {
                result = postJson(MSG_SEC_CHECK_URL, body, true);
                errcode = result.path("errcode").asInt(0);
            }
            if (errcode != 0) {
                log.warn("内容安全检测失败 errcode={}, errmsg={}", errcode, result.path("errmsg").asText(""));
                return true;
            }
            return !"risky".equals(result.path("result").path("suggest").asText("pass"));
        } catch (Exception e) {
            log.warn("内容安全检测异常：{}", e.getMessage());
            return true;
        }
    }

    /** 商户在会话中回复后提醒用户；未授权或额度用尽时静默跳过 */
    public void sendChatNotice(String openid, Merchant merchant, String summary, String asker, String page) {
        if (openid == null || openid.isBlank() || !isQuoteEnabled()) {
            return;
        }
        try {
            String merchantName = merchant == null || merchant.getName() == null || merchant.getName().isBlank()
                    ? "商户" : merchant.getName();
            sendMessage(openid, templateId(), page,
                    quoteData(merchantName, summary, asker, "在线咨询"), false);
        } catch (Exception e) {
            log.warn("会话提醒发送失败 openid={}, err={}", mask(openid), e.getMessage());
        }
    }

    /** 平台端测试发送：模板未配置或微信返回错误时抛出可读提示 */
    public void sendTest(String openid) {
        if (!isQuoteEnabled()) {
            throw new BusinessException("订阅消息未配置：请先填写模板 ID");
        }
        try {
            sendMessage(openid, templateId(), quotePage,
                    quoteData("测试商户", "单价100元，总价100元", "微信用户", "测试商品"), true);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("订阅消息发送失败：" + e.getMessage());
        }
    }

    private Map<String, Object> quoteData(String merchantName, String replyContent, String asker, String productName) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put(fieldReplier, value(clip(merchantName, 10)));
        data.put(fieldReplyTime, value(TIME_FORMAT.format(LocalDateTime.now())));
        data.put(fieldReplyContent, value(clip(replyContent, 20)));
        data.put(fieldAsker, value(clip(asker, 10)));
        data.put(fieldAskContent, value(clip(productName, 20)));
        return data;
    }

    /** 回复内容取报价摘要，只有备注时退回备注 */
    private static String replyContent(Inquiry inquiry) {
        StringBuilder text = new StringBuilder();
        if (inquiry.getQuotePrice() != null) {
            text.append("单价").append(inquiry.getQuotePrice().stripTrailingZeros().toPlainString()).append("元");
        }
        if (inquiry.getQuoteTotal() != null) {
            if (text.length() > 0) {
                text.append("，");
            }
            text.append("总价").append(inquiry.getQuoteTotal().stripTrailingZeros().toPlainString()).append("元");
        }
        if (text.length() == 0) {
            String remark = inquiry.getQuoteRemark();
            return remark == null || remark.isBlank() ? "商家已回复，点击查看" : remark.trim();
        }
        return text.toString();
    }

    /** 咨询人展示为脱敏联系方式 */
    private static String maskContact(String contact) {
        if (contact == null || contact.isBlank()) {
            return "微信用户";
        }
        String text = contact.trim();
        if (text.length() < 7) {
            return text;
        }
        return text.substring(0, 3) + "****" + text.substring(text.length() - 4);
    }

    private void sendMessage(String openid, String templateId, String page, Map<String, Object> data,
                             boolean throwOnNoAuth) {
        Map<String, Object> body = new HashMap<>(8);
        body.put("touser", openid);
        body.put("template_id", templateId);
        if (page != null && !page.isBlank()) {
            body.put("page", page.trim());
        }
        body.put("miniprogram_state", miniprogramState == null ? "formal" : miniprogramState.trim());
        body.put("lang", "zh_CN");
        body.put("data", data);

        JsonNode result;
        try {
            result = postSend(body, false);
        } catch (IOException e) {
            throw new BusinessException("微信接口请求失败，请稍后重试");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("微信接口请求被中断");
        }
        int errcode = result.path("errcode").asInt(0);
        if (errcode == 40001 || errcode == 42001) {
            try {
                result = postSend(body, true);
            } catch (IOException e) {
                throw new BusinessException("微信接口请求失败，请稍后重试");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new BusinessException("微信接口请求被中断");
            }
            errcode = result.path("errcode").asInt(0);
        }
        if (errcode == 43101) {
            log.info("用户未订阅该模板消息 openid={}", mask(openid));
            if (throwOnNoAuth) {
                throw new BusinessException("该用户尚未订阅此消息，请先在小程序内点击授权");
            }
            return;
        }
        if (errcode != 0) {
            String errmsg = result.path("errmsg").asText("");
            throw new BusinessException("订阅消息发送失败(" + errcode + "): " + errmsg);
        }
    }

    private JsonNode postSend(Map<String, Object> body, boolean refreshToken)
            throws IOException, InterruptedException {
        return postJson(SEND_URL, body, refreshToken);
    }

    private JsonNode postJson(String url, Map<String, Object> body, boolean refreshToken)
            throws IOException, InterruptedException {
        String token = getAccessToken(refreshToken);
        HttpRequest request = HttpRequest.newBuilder(URI.create(url + "?access_token=" + token))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readTree(response.body());
    }

    private synchronized String getAccessToken(boolean forceRefresh) throws IOException, InterruptedException {
        if (!forceRefresh && accessToken != null && System.currentTimeMillis() < accessTokenExpiresAt) {
            return accessToken;
        }
        String resolvedAppid = resolveAppid();
        String resolvedSecret = resolveSecret();
        if (resolvedAppid.isEmpty() || resolvedSecret.isEmpty()) {
            throw new BusinessException("微信登录未配置，无法发送订阅消息");
        }
        String url = UriComponentsBuilder.fromHttpUrl(TOKEN_URL)
                .queryParam("grant_type", "client_credential")
                .queryParam("appid", resolvedAppid)
                .queryParam("secret", resolvedSecret)
                .encode()
                .build()
                .toUriString();
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode body = objectMapper.readTree(response.body());
        String token = body.path("access_token").asText("");
        if (token.isBlank()) {
            int errcode = body.path("errcode").asInt(-1);
            throw new BusinessException("获取微信 access_token 失败(" + errcode + ")");
        }
        long expiresIn = body.path("expires_in").asLong(7200);
        accessToken = token;
        accessTokenExpiresAt = System.currentTimeMillis() + Math.max(60, expiresIn - 300) * 1000L;
        return token;
    }

    private String templateId() {
        return quoteTemplateId == null ? "" : quoteTemplateId.trim();
    }

    private String resolveAppid() {
        return firstNonBlank(appid, appIdAlias, appIdCamel);
    }

    private String resolveSecret() {
        return firstNonBlank(secret, secretAlias, secretCamel);
    }

    private static Map<String, Object> value(String text) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("value", text == null ? "" : text);
        return map;
    }

    private static String clip(String text, int max) {
        if (text == null) {
            return "";
        }
        String trimmed = text.trim();
        return trimmed.length() <= max ? trimmed : trimmed.substring(0, max);
    }

    private static String mask(String openid) {
        if (openid == null || openid.length() <= 8) {
            return "***";
        }
        return openid.substring(0, 4) + "***" + openid.substring(openid.length() - 4);
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return "";
    }
}
