package com.jiancai.chat;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jiancai.entity.MpUser;
import com.jiancai.mapper.MpUserMapper;
import com.jiancai.security.JwtUtil;
import com.jiancai.security.LoginUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

/**
 * 聊天长连接：/ws/chat?role=user（Authorization: Bearer 用户 token）
 * 或 /ws/chat?role=merchant&token=商户 token；只负责接收推送，发消息仍走 REST 接口
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChatSocketHandler extends TextWebSocketHandler {

    private final ChatSocketRegistry registry;
    private final MpUserMapper mpUserMapper;
    private final JwtUtil jwtUtil;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Map<String, List<String>> params = UriComponentsBuilder.fromUri(session.getUri())
                .build().getQueryParams();
        String role = first(params, "role");
        if ("merchant".equals(role)) {
            LoginUser user;
            try {
                user = jwtUtil.parseToUser(first(params, "token"));
            } catch (Exception e) {
                session.close(CloseStatus.NOT_ACCEPTABLE.withReason("登录已过期"));
                return;
            }
            if (user.getMerchantId() == null) {
                session.close(CloseStatus.NOT_ACCEPTABLE.withReason("非商户账号"));
                return;
            }
            registry.addMerchant(user.getMerchantId(), session);
            return;
        }

        String token = first(params, "token");
        if (token.isEmpty()) {
            String auth = session.getHandshakeHeaders().getFirst("Authorization");
            if (auth != null && auth.trim().startsWith("Bearer ")) {
                token = auth.trim().substring(7).trim();
            }
        }
        String openid;
        try {
            openid = jwtUtil.parseUserOpenid(token);
        } catch (Exception e) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("登录已过期"));
            return;
        }
        if (openid.length() > 64
                || mpUserMapper.selectCount(Wrappers.<MpUser>lambdaQuery().eq(MpUser::getOpenid, openid)) == 0) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("用户不存在"));
            return;
        }
        registry.addUser(openid, session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        if ("ping".equals(message.getPayload())) {
            session.sendMessage(new TextMessage("pong"));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        registry.remove(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.debug("聊天连接异常 sessionId={}, err={}", session.getId(), exception.getMessage());
        registry.remove(session);
    }

    private static String first(Map<String, List<String>> params, String key) {
        List<String> values = params.get(key);
        if (values == null || values.isEmpty() || values.get(0) == null) {
            return "";
        }
        return values.get(0).trim();
    }
}
