package com.jiancai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiancai.common.BusinessException;
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
import java.util.HashMap;
import java.util.Map;

/**
 * 微信小程序登录：code 换 openid
 */
@Slf4j
@Service
public class WxLoginService {

    private static final String JSCODE_URL = "https://api.weixin.qq.com/sns/jscode2session";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    @Value("${jiancai.wx.appid:}")
    private String appid;

    /** 兼容已有配置写法 app-id / appId */
    @Value("${jiancai.wx.app-id:}")
    private String appIdAlias;

    @Value("${jiancai.wx.appId:}")
    private String appIdCamel;

    @Value("${jiancai.wx.secret:}")
    private String secret;

    /** 兼容已有配置写法 app-secret / appSecret */
    @Value("${jiancai.wx.app-secret:}")
    private String secretAlias;

    @Value("${jiancai.wx.appSecret:}")
    private String secretCamel;

    @Autowired
    private ObjectMapper objectMapper;

    public Map<String, Object> code2Session(String code) {
        String resolvedAppid = firstNonBlank(appid, appIdAlias, appIdCamel);
        String resolvedSecret = firstNonBlank(secret, secretAlias, secretCamel);
        if (resolvedAppid.isEmpty() || resolvedSecret.isEmpty()) {
            throw new BusinessException("微信登录未配置，请联系平台");
        }
        String url = UriComponentsBuilder.fromHttpUrl(JSCODE_URL)
                .queryParam("appid", resolvedAppid)
                .queryParam("secret", resolvedSecret)
                .queryParam("js_code", code)
                .queryParam("grant_type", "authorization_code")
                .encode()
                .build()
                .toUriString();

        HttpResponse<String> response;
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("微信登录服务暂不可用，请稍后重试");
        } catch (IOException e) {
            log.warn("微信 code2session 请求失败: {}", e.getMessage());
            throw new BusinessException("微信登录服务暂不可用，请稍后重试");
        }

        JsonNode body;
        try {
            body = objectMapper.readTree(response.body());
        } catch (IOException e) {
            log.warn("微信 code2session 返回内容解析失败");
            throw new BusinessException("微信登录失败，请重试");
        }
        int errcode = body.path("errcode").asInt(0);
        if (errcode != 0) {
            log.warn("微信 code2session 失败: errcode={}, errmsg={}",
                    errcode, body.path("errmsg").asText(""));
            throw new BusinessException("微信登录失败，请重试");
        }
        String openid = body.path("openid").asText("");
        if (openid.isBlank()) {
            throw new BusinessException("微信登录失败，请重试");
        }
        Map<String, Object> result = new HashMap<>(4);
        result.put("openid", openid);
        String unionid = body.path("unionid").asText("");
        if (!unionid.isBlank()) {
            result.put("unionid", unionid);
        }
        return result;
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
