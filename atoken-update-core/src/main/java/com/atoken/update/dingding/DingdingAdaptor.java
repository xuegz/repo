package com.atoken.update.dingding;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.MessageFormat;

/**
 * @description:
 * @author: atoken-team
 * @date: 2020/4/22 18:48
 **/
@Component
public class DingdingAdaptor {
    private Logger log = LoggerFactory.getLogger(DingdingAdaptor.class);

    @Value("${dingding.message.url}")
    private String dingdingRobotUrl;

    @Value("${dingding.message.atMobiles}")
    private String atMobiles;

    @Value("${spring.application.name}")
    private String appName;

    @Value("${dingding.message.isAtAll}")
    private String isAtAll;

    @Value("${spring.profiles.active:default}")
    private String profilesActive;


    public void sendMessage(String message, Object... arguments) {
        sendMessage(MessageFormat.format(message, arguments));
    }

    @Async
    public void asyncSendMessage(String message) {
        sendMessage(message);
    }

    /**
     * @param message
     */
    public void sendMessage(String message) {
        try {
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost httpPost = new HttpPost(dingdingRobotUrl);
            httpPost.addHeader("Content-Type", "application/json;charset=" + Charset.defaultCharset());

            JSONObject dingMessage = new JSONObject();
            dingMessage.put("msgtype", "text");
            JSONObject textMessage = new JSONObject();
            textMessage.put("content", "[" + appName + "]".concat("[" + profilesActive + "]").concat(message));
            dingMessage.put("text", textMessage);

            JSONObject atMessage = new JSONObject();
            atMessage.put("atMobiles", atMobiles.split(","));
            atMessage.put("isAtAll", isAtAll);
            dingMessage.put("at", atMessage);

            httpPost.setEntity(new ByteArrayEntity(dingMessage.toString().getBytes(Charset.defaultCharset())));
            HttpResponse response = httpClient.execute(httpPost);
            int status = response.getStatusLine().getStatusCode();
            if (status != HttpStatus.SC_OK) {
                String result = EntityUtils.toString(response.getEntity(), Charset.defaultCharset());
                log.info("钉钉消息发送失败, status:{}, result:{}", status, result);
            }
        } catch (IOException e) {
            log.error("钉钉消息发送异常", e);
        }
    }
}
