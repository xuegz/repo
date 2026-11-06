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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 微信小程序订阅消息：access_token 缓存 + 模板消息发送
 * 模板字段名可通过 jiancai.wx.subscribe.fields.* 覆盖，默认对应
 * thing1=商品名称、thing2=商户名称、amount3=报价金额、time4=报价时间、thing5=备注
 */
@Slf4j
@Service
public class WxSubscribeService {

    private static final String TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token";
    private static final String SEND_URL = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send";
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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

    @Value("${jiancai.wx.subscribe.fields.product:thing1}")
    private String fieldProduct;

    @Value("${jiancai.wx.subscribe.fields.merchant:thing2}")
    private String fieldMerchant;

    @Value("${jiancai.wx.subscribe.fields.amount:amount3}")
    private String fieldAmount;

    @Value("${jiancai.wx.subscribe.fields.time:time4}")
    private String fieldTime;

    @Value("${jiancai.wx.subscribe.fields.remark:thing5}")
    private String fieldRemark;

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
            String amount = inquiry.getQuoteTotal() != null ? inquiry.getQuoteTotal().toPlainString()
                    : inquiry.getQuotePrice() != null ? inquiry.getQuotePrice().toPlainString() : "0";
            String remark = inquiry.getQuoteRemark();
            sendMessage(openid, templateId(), quotePage, quoteData(productName, merchantName, amount, remark));
        } catch (Exception e) {
            log.warn("订阅消息发送失败 inquiryId={}, err={}", inquiry.getId(), e.getMessage());
        }
    }

    /** 平台端测试发送：模板未配置或微信返回错误时抛出可读提示 */
    public void sendTest(String openid) {
        if (!isQuoteEnabled()) {
            throw new BusinessException("订阅消息未配置：请先填写模板 ID");
        }
        try {
            sendMessage(openid, templateId(), quotePage,
                    quoteData("测试商品", "测试商户", "100", "这是一条测试消息"));
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("订阅消息发送失败：" + e.getMessage());
        }
    }

    private Map<String, Object> quoteData(String productName, String merchantName, String amount, String remark) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put(fieldProduct, value(clip(productName, 20)));
        data.put(fieldMerchant, value(clip(merchantName, 20)));
        data.put(fieldAmount, value(amount));
        data.put(fieldTime, value(TIME_FORMAT.format(LocalDateTime.now())));
        data.put(fieldRemark, value(clip(remark == null || remark.isBlank() ? "点击查看报价详情" : remark, 20)));
        return data;
    }

    private void sendMessage(String openid, String templateId, String page, Map<String, Object> data) {
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
            return;
        }
        if (errcode != 0) {
            String errmsg = result.path("errmsg").asText("");
            throw new BusinessException("订阅消息发送失败(" + errcode + "): " + errmsg);
        }
    }

    private JsonNode postSend(Map<String, Object> body, boolean refreshToken)
            throws IOException, InterruptedException {
        String token = getAccessToken(refreshToken);
        HttpRequest request;
        try {
            request = HttpRequest.newBuilder(URI.create(SEND_URL + "?access_token=" + token))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                    .build();
        } catch (IOException e) {
            throw new IOException("订阅消息内容序列化失败", e);
        }
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
