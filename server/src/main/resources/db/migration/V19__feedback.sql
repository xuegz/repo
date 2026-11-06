-- 用户意见反馈与会话举报：平台端集中处理，处理结果回显给用户
CREATE TABLE feedback (
  id              BIGINT       AUTO_INCREMENT PRIMARY KEY,
  source          TINYINT      NOT NULL DEFAULT 1,
  openid          VARCHAR(64),
  merchant_id     BIGINT,
  merchant_name   VARCHAR(128),
  conversation_id BIGINT,
  category        VARCHAR(32),
  content         VARCHAR(1000) NOT NULL,
  contact         VARCHAR(64),
  status          TINYINT      NOT NULL DEFAULT 0,
  reply           VARCHAR(500),
  handle_time     DATETIME,
  create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_feedback_status ON feedback (status, create_time);
CREATE INDEX idx_feedback_openid ON feedback (openid, create_time);
CREATE INDEX idx_feedback_source ON feedback (source, create_time);
