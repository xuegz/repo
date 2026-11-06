-- 用户与商户的站内会话：一个 openid 与一个商户只有一条会话
CREATE TABLE chat_conversation (
  id                BIGINT       AUTO_INCREMENT PRIMARY KEY,
  openid            VARCHAR(64)  NOT NULL,
  merchant_id       BIGINT       NOT NULL,
  user_unread       INT          NOT NULL DEFAULT 0,
  merchant_unread   INT          NOT NULL DEFAULT 0,
  last_content      VARCHAR(120),
  last_content_type VARCHAR(16),
  last_message_at   DATETIME,
  create_time       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uk_chat_conversation_openid_merchant ON chat_conversation (openid, merchant_id);
CREATE INDEX idx_chat_conversation_merchant ON chat_conversation (merchant_id, last_message_at);

-- 消息正文：text 存原文，image 存图片地址，product 存商品快照 JSON
CREATE TABLE chat_message (
  id              BIGINT       AUTO_INCREMENT PRIMARY KEY,
  conversation_id BIGINT       NOT NULL,
  sender_type     TINYINT      NOT NULL,
  content_type    VARCHAR(16)  NOT NULL,
  content         TEXT         NOT NULL,
  create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_chat_message_conversation ON chat_message (conversation_id, id);
