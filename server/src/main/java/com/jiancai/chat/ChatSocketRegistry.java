package com.jiancai.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 在线连接登记：用户按 openid、商户按 merchantId 分组，用于把新消息推给在线的另一端
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChatSocketRegistry {

    public static final String ATTR_TYPE = "chatType";
    public static final String ATTR_KEY = "chatKey";

    private final ObjectMapper objectMapper;

    private final Map<String, Set<WebSocketSession>> users = new ConcurrentHashMap<>();
    private final Map<Long, Set<WebSocketSession>> merchants = new ConcurrentHashMap<>();

    public void addUser(String openid, WebSocketSession session) {
        session.getAttributes().put(ATTR_TYPE, "user");
        session.getAttributes().put(ATTR_KEY, openid);
        users.computeIfAbsent(openid, k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    public void addMerchant(Long merchantId, WebSocketSession session) {
        session.getAttributes().put(ATTR_TYPE, "merchant");
        session.getAttributes().put(ATTR_KEY, merchantId);
        merchants.computeIfAbsent(merchantId, k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    public void remove(WebSocketSession session) {
        Object type = session.getAttributes().get(ATTR_TYPE);
        Object key = session.getAttributes().get(ATTR_KEY);
        if ("user".equals(type) && key != null) {
            removeFrom(users, key.toString(), session);
        } else if ("merchant".equals(type) && key instanceof Long merchantId) {
            removeFrom(merchants, merchantId, session);
        }
    }

    public void pushUser(String openid, Object payload) {
        push(users.get(openid), payload);
    }

    public void pushMerchant(Long merchantId, Object payload) {
        push(merchants.get(merchantId), payload);
    }

    private <K> void removeFrom(Map<K, Set<WebSocketSession>> group, K key, WebSocketSession session) {
        Set<WebSocketSession> sessions = group.get(key);
        if (sessions == null) {
            return;
        }
        sessions.remove(session);
        if (sessions.isEmpty()) {
            group.remove(key, sessions);
        }
    }

    private void push(Set<WebSocketSession> sessions, Object payload) {
        if (sessions == null || sessions.isEmpty()) {
            return;
        }
        String text;
        try {
            text = objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            log.warn("聊天消息序列化失败 {}", e.getMessage());
            return;
        }
        TextMessage message = new TextMessage(text);
        for (WebSocketSession session : Collections.unmodifiableSet(sessions)) {
            if (!session.isOpen()) {
                sessions.remove(session);
                continue;
            }
            try {
                synchronized (session) {
                    session.sendMessage(message);
                }
            } catch (Exception e) {
                log.debug("聊天消息推送失败 sessionId={}, err={}", session.getId(), e.getMessage());
                sessions.remove(session);
            }
        }
    }
}
