package com.jiancai.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiancai.chat.ChatSocketRegistry;
import com.jiancai.common.BusinessException;
import com.jiancai.entity.ChatConversation;
import com.jiancai.entity.ChatMessage;
import com.jiancai.entity.Merchant;
import com.jiancai.entity.MpUser;
import com.jiancai.entity.Product;
import com.jiancai.mapper.ChatConversationMapper;
import com.jiancai.mapper.ChatMessageMapper;
import com.jiancai.mapper.MerchantMapper;
import com.jiancai.mapper.MpUserMapper;
import com.jiancai.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户与商户的站内会话：一个用户与一个商户共用一条会话
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private static final int MAX_TEXT_LENGTH = 500;
    private static final int PREVIEW_LENGTH = 40;
    private static final String NOTICE_PAGE = "pages/chat/chat?id=";

    private final ChatConversationMapper conversationMapper;
    private final ChatMessageMapper messageMapper;
    private final MerchantMapper merchantMapper;
    private final MpUserMapper mpUserMapper;
    private final ProductMapper productMapper;
    private final ChatSocketRegistry registry;
    private final WxSubscribeService wxSubscribeService;
    private final OperationLogService operationLogService;
    private final FeedbackService feedbackService;
    private final ObjectMapper objectMapper;

    /** 打开（不存在则创建）与商家的会话 */
    public Map<String, Object> openConversation(String openid, Long merchantId) {
        String target = requireOpenid(openid);
        if (merchantId == null) {
            throw new BusinessException("缺少商户参数");
        }
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
            throw new BusinessException("商户不存在");
        }
        return userConversationView(findOrCreate(target, merchantId), merchant);
    }

    public List<Map<String, Object>> listForUser(String openid) {
        String target = requireOpenid(openid);
        List<ChatConversation> list = conversationMapper.selectList(Wrappers.<ChatConversation>lambdaQuery()
                .eq(ChatConversation::getOpenid, target)
                .isNotNull(ChatConversation::getLastMessageAt)
                .orderByDesc(ChatConversation::getLastMessageAt)
                .last("LIMIT 100"));
        Map<Long, Merchant> merchants = merchantsOf(list);
        List<Map<String, Object>> rows = new ArrayList<>(list.size());
        for (ChatConversation conversation : list) {
            rows.add(userConversationView(conversation, merchants.get(conversation.getMerchantId())));
        }
        return rows;
    }

    public List<Map<String, Object>> listForMerchant(Long merchantId) {
        if (merchantId == null) {
            throw new BusinessException("缺少商户身份");
        }
        List<ChatConversation> list = conversationMapper.selectList(Wrappers.<ChatConversation>lambdaQuery()
                .eq(ChatConversation::getMerchantId, merchantId)
                .isNotNull(ChatConversation::getLastMessageAt)
                .orderByDesc(ChatConversation::getLastMessageAt)
                .last("LIMIT 100"));
        Map<String, MpUser> users = usersOf(list);
        List<Map<String, Object>> rows = new ArrayList<>(list.size());
        for (ChatConversation conversation : list) {
            rows.add(merchantConversationView(conversation, users.get(conversation.getOpenid())));
        }
        return rows;
    }

    /** afterId>0 增量拉取；beforeId>0 上翻历史；都不传时取最近 50 条 */
    public List<Map<String, Object>> messages(Long conversationId, String openid, Long merchantId,
                                              Long afterId, Long beforeId) {
        ChatConversation conversation = requireConversation(conversationId);
        checkAccess(conversation, openid, merchantId);
        List<ChatMessage> list;
        if (afterId != null && afterId > 0) {
            list = messageMapper.selectList(Wrappers.<ChatMessage>lambdaQuery()
                    .eq(ChatMessage::getConversationId, conversationId)
                    .gt(ChatMessage::getId, afterId)
                    .orderByAsc(ChatMessage::getId)
                    .last("LIMIT 100"));
        } else {
            var wrapper = Wrappers.<ChatMessage>lambdaQuery()
                    .eq(ChatMessage::getConversationId, conversationId);
            if (beforeId != null && beforeId > 0) {
                wrapper.lt(ChatMessage::getId, beforeId);
            }
            list = messageMapper.selectList(wrapper
                    .orderByDesc(ChatMessage::getId)
                    .last("LIMIT 50"));
            Collections.reverse(list);
        }
        return list.stream().map(this::messageView).collect(Collectors.toList());
    }

    /** 平台端查看举报关联会话，不做归属校验 */
    public List<Map<String, Object>> adminMessages(Long conversationId) {
        List<ChatMessage> list = messageMapper.selectList(Wrappers.<ChatMessage>lambdaQuery()
                .eq(ChatMessage::getConversationId, conversationId)
                .orderByDesc(ChatMessage::getId)
                .last("LIMIT 100"));
        Collections.reverse(list);
        return list.stream().map(this::messageView).collect(Collectors.toList());
    }

    public Map<String, Object> sendAsUser(String openid, Long conversationId, String type, String content,
                                          Long productId) {
        ChatConversation conversation = requireConversation(conversationId);
        String target = requireOpenid(openid);
        checkAccess(conversation, target, null);
        return send(conversation, 1, type, content, productId, target, null);
    }

    public Map<String, Object> sendAsMerchant(Long merchantId, Long conversationId, String type, String content,
                                              Long productId) {
        ChatConversation conversation = requireConversation(conversationId);
        checkAccess(conversation, null, merchantId);
        Merchant merchant = merchantMapper.selectById(conversation.getMerchantId());
        return send(conversation, 2, type, content, productId, conversation.getOpenid(), merchant);
    }

    public void markReadAsUser(String openid, Long conversationId) {
        ChatConversation conversation = requireConversation(conversationId);
        String target = requireOpenid(openid);
        checkAccess(conversation, target, null);
        conversationMapper.update(null, Wrappers.<ChatConversation>lambdaUpdate()
                .eq(ChatConversation::getId, conversationId)
                .set(ChatConversation::getUserUnread, 0)
                .set(ChatConversation::getUpdateTime, LocalDateTime.now()));
    }

    public void markReadAsMerchant(Long merchantId, Long conversationId) {
        ChatConversation conversation = requireConversation(conversationId);
        checkAccess(conversation, null, merchantId);
        conversationMapper.update(null, Wrappers.<ChatConversation>lambdaUpdate()
                .eq(ChatConversation::getId, conversationId)
                .set(ChatConversation::getMerchantUnread, 0)
                .set(ChatConversation::getUpdateTime, LocalDateTime.now()));
    }

    /** 用户举报会话：写入反馈表供平台处理，同时保留操作日志 */
    public void reportConversation(String openid, Long conversationId, String reason) {
        ChatConversation conversation = requireConversation(conversationId);
        String target = requireOpenid(openid);
        checkAccess(conversation, target, null);
        String detail = reason == null || reason.isBlank() ? "用户举报会话" : reason.trim();
        Merchant merchant = merchantMapper.selectById(conversation.getMerchantId());
        feedbackService.report(target, conversationId, conversation.getMerchantId(),
                merchant == null ? null : merchant.getName(), detail);
        operationLogService.record("chat_report", "conversation", conversationId, detail);
    }

    public int unreadForUser(String openid) {
        String target = requireOpenid(openid);
        return conversationMapper.selectList(Wrappers.<ChatConversation>lambdaQuery()
                        .eq(ChatConversation::getOpenid, target)).stream()
                .mapToInt(item -> item.getUserUnread() == null ? 0 : item.getUserUnread())
                .sum();
    }

    public int unreadForMerchant(Long merchantId) {
        if (merchantId == null) {
            return 0;
        }
        return conversationMapper.selectList(Wrappers.<ChatConversation>lambdaQuery()
                        .eq(ChatConversation::getMerchantId, merchantId)).stream()
                .mapToInt(item -> item.getMerchantUnread() == null ? 0 : item.getMerchantUnread())
                .sum();
    }

    private Map<String, Object> send(ChatConversation conversation, int senderType, String type, String content,
                                     Long productId, String userOpenid, Merchant merchant) {
        String contentType = type == null || type.isBlank() ? "text" : type.trim();
        String stored;
        String preview;
        switch (contentType) {
            case "image":
                stored = content == null ? "" : content.trim();
                if (!stored.startsWith("http://") && !stored.startsWith("https://")
                        && !stored.startsWith("/upload/")) {
                    throw new BusinessException("图片地址不合法");
                }
                if (stored.length() > 500) {
                    throw new BusinessException("图片地址过长");
                }
                preview = "[图片]";
                break;
            case "product":
                if (productId == null) {
                    throw new BusinessException("缺少商品参数");
                }
                Product product = productMapper.selectById(productId);
                if (product == null) {
                    throw new BusinessException("商品不存在");
                }
                stored = productSnapshot(product);
                preview = "[商品] " + clip(product.getName(), PREVIEW_LENGTH);
                break;
            default:
                contentType = "text";
                stored = content == null ? "" : content.trim();
                if (stored.isEmpty()) {
                    throw new BusinessException("消息不能为空");
                }
                if (stored.length() > MAX_TEXT_LENGTH) {
                    throw new BusinessException("消息不能超过 " + MAX_TEXT_LENGTH + " 字");
                }
                if (senderType == 1 && !wxSubscribeService.isTextSafe(userOpenid, stored)) {
                    throw new BusinessException("消息包含不适合的内容，请修改后重发");
                }
                preview = stored;
        }

        // 同一会话里同一商品只自动发一次，反复进入详情直接复用已有卡片
        if ("product".equals(contentType) && senderType == 1) {
            ChatMessage duplicate = findProductCard(conversation.getId(), productId);
            if (duplicate != null) {
                Map<String, Object> view = messageView(duplicate);
                view.put("duplicate", true);
                return view;
            }
        }

        LocalDateTime now = LocalDateTime.now();
        ChatMessage message = new ChatMessage();
        message.setConversationId(conversation.getId());
        message.setSenderType(senderType);
        message.setContentType(contentType);
        message.setContent(stored);
        message.setCreateTime(now);
        messageMapper.insert(message);

        conversationMapper.update(null, Wrappers.<ChatConversation>lambdaUpdate()
                .eq(ChatConversation::getId, conversation.getId())
                .set(ChatConversation::getLastContent, clip(preview, 100))
                .set(ChatConversation::getLastContentType, contentType)
                .set(ChatConversation::getLastMessageAt, now)
                .set(ChatConversation::getUpdateTime, now)
                .setSql(senderType == 1
                        ? "merchant_unread = merchant_unread + 1"
                        : "user_unread = user_unread + 1"));

        Map<String, Object> view = messageView(message);
        Map<String, Object> payload = new HashMap<>(6);
        payload.put("event", "message");
        payload.put("conversationId", conversation.getId());
        payload.put("message", view);
        if (senderType == 1) {
            registry.pushMerchant(conversation.getMerchantId(), payload);
        } else {
            registry.pushUser(conversation.getOpenid(), payload);
            notifyUser(conversation, merchant, preview);
        }
        return view;
    }

    /** 商户回复后给用户发订阅消息；未授权或额度用尽静默跳过 */
    private void notifyUser(ChatConversation conversation, Merchant merchant, String preview) {
        try {
            MpUser user = mpUserMapper.selectOne(Wrappers.<MpUser>lambdaQuery()
                    .eq(MpUser::getOpenid, conversation.getOpenid())
                    .last("LIMIT 1"));
            String asker = user != null && user.getNickname() != null && !user.getNickname().isBlank()
                    ? user.getNickname() : "微信用户";
            wxSubscribeService.sendChatNotice(conversation.getOpenid(), merchant, preview, asker,
                    NOTICE_PAGE + conversation.getId());
        } catch (Exception e) {
            log.warn("会话提醒发送失败 conversationId={}, err={}", conversation.getId(), e.getMessage());
        }
    }

    private ChatConversation findOrCreate(String openid, Long merchantId) {
        ChatConversation existing = conversationMapper.selectOne(Wrappers.<ChatConversation>lambdaQuery()
                .eq(ChatConversation::getOpenid, openid)
                .eq(ChatConversation::getMerchantId, merchantId)
                .last("LIMIT 1"));
        if (existing != null) {
            return existing;
        }
        ChatConversation created = new ChatConversation();
        created.setOpenid(openid);
        created.setMerchantId(merchantId);
        created.setUserUnread(0);
        created.setMerchantUnread(0);
        created.setCreateTime(LocalDateTime.now());
        created.setUpdateTime(LocalDateTime.now());
        try {
            conversationMapper.insert(created);
            return created;
        } catch (Exception e) {
            ChatConversation again = conversationMapper.selectOne(Wrappers.<ChatConversation>lambdaQuery()
                    .eq(ChatConversation::getOpenid, openid)
                    .eq(ChatConversation::getMerchantId, merchantId)
                    .last("LIMIT 1"));
            if (again != null) {
                return again;
            }
            throw e;
        }
    }

    private Map<Long, Merchant> merchantsOf(List<ChatConversation> list) {
        List<Long> ids = list.stream().map(ChatConversation::getMerchantId).distinct().collect(Collectors.toList());
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }
        return merchantMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(Merchant::getId, item -> item, (a, b) -> a));
    }

    private Map<String, MpUser> usersOf(List<ChatConversation> list) {
        List<String> openids = list.stream().map(ChatConversation::getOpenid).distinct().collect(Collectors.toList());
        if (openids.isEmpty()) {
            return Collections.emptyMap();
        }
        return mpUserMapper.selectList(Wrappers.<MpUser>lambdaQuery().in(MpUser::getOpenid, openids)).stream()
                .collect(Collectors.toMap(MpUser::getOpenid, item -> item, (a, b) -> a));
    }

    private Map<String, Object> userConversationView(ChatConversation conversation, Merchant merchant) {
        Map<String, Object> row = baseConversationView(conversation);
        row.put("merchantId", conversation.getMerchantId());
        row.put("merchantName", merchant == null ? "商户" : merchant.getName());
        row.put("merchantLogo", merchant == null ? "" : nullToEmpty(merchant.getLogo()));
        row.put("unread", conversation.getUserUnread() == null ? 0 : conversation.getUserUnread());
        return row;
    }

    private Map<String, Object> merchantConversationView(ChatConversation conversation, MpUser user) {
        Map<String, Object> row = baseConversationView(conversation);
        String nickname = user == null || user.getNickname() == null || user.getNickname().isBlank()
                ? "微信访客" : user.getNickname();
        row.put("openidTail", tail(conversation.getOpenid()));
        row.put("userName", nickname);
        row.put("userAvatar", user == null ? "" : nullToEmpty(user.getAvatar()));
        row.put("unread", conversation.getMerchantUnread() == null ? 0 : conversation.getMerchantUnread());
        return row;
    }

    private Map<String, Object> baseConversationView(ChatConversation conversation) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("conversationId", conversation.getId());
        row.put("lastContent", nullToEmpty(conversation.getLastContent()));
        row.put("lastContentType", nullToEmpty(conversation.getLastContentType()));
        row.put("lastMessageAt", conversation.getLastMessageAt());
        return row;
    }

    private Map<String, Object> messageView(ChatMessage message) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", message.getId());
        row.put("conversationId", message.getConversationId());
        row.put("sender", Integer.valueOf(1).equals(message.getSenderType()) ? "user" : "merchant");
        row.put("type", message.getContentType());
        row.put("content", message.getContent());
        row.put("createTime", message.getCreateTime());
        return row;
    }

    private ChatMessage findProductCard(Long conversationId, Long productId) {
        List<ChatMessage> cards = messageMapper.selectList(Wrappers.<ChatMessage>lambdaQuery()
                .eq(ChatMessage::getConversationId, conversationId)
                .eq(ChatMessage::getContentType, "product")
                .orderByDesc(ChatMessage::getId)
                .last("LIMIT 100"));
        for (ChatMessage card : cards) {
            try {
                JsonNode node = objectMapper.readTree(card.getContent());
                if (node.path("id").asLong() == productId) {
                    return card;
                }
            } catch (Exception ignored) {
                // 历史脏数据跳过
            }
        }
        return null;
    }

    private String productSnapshot(Product product) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("id", product.getId());
        snapshot.put("name", nullToEmpty(product.getName()));
        snapshot.put("price", product.getPrice());
        snapshot.put("unit", nullToEmpty(product.getUnit()));
        List<String> images = ProductService.splitImages(product.getImages());
        snapshot.put("image", images.isEmpty() ? "" : images.get(0));
        try {
            return objectMapper.writeValueAsString(snapshot);
        } catch (Exception e) {
            throw new BusinessException("商品信息序列化失败");
        }
    }

    private ChatConversation requireConversation(Long conversationId) {
        if (conversationId == null) {
            throw new BusinessException("缺少会话参数");
        }
        ChatConversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null) {
            throw new BusinessException("会话不存在");
        }
        return conversation;
    }

    private void checkAccess(ChatConversation conversation, String openid, Long merchantId) {
        if (openid != null && !openid.equals(conversation.getOpenid())) {
            throw new BusinessException(403, "无权查看该会话");
        }
        if (merchantId != null && !merchantId.equals(conversation.getMerchantId())) {
            throw new BusinessException(403, "无权查看该会话");
        }
    }

    private static String requireOpenid(String openid) {
        if (openid == null || openid.isBlank()) {
            throw new BusinessException("缺少用户标识");
        }
        String value = openid.trim();
        if (value.length() > 64) {
            throw new BusinessException("用户标识不合法");
        }
        return value;
    }

    private static String clip(String text, int max) {
        if (text == null) {
            return "";
        }
        return text.length() <= max ? text : text.substring(0, max) + "…";
    }

    private static String tail(String openid) {
        if (openid == null || openid.length() < 4) {
            return "";
        }
        return openid.substring(openid.length() - 4);
    }

    private static String nullToEmpty(String text) {
        return text == null ? "" : text;
    }
}
