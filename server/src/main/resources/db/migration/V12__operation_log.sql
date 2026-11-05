-- 管理端关键操作留痕
CREATE TABLE operation_log (
  id          BIGINT       AUTO_INCREMENT PRIMARY KEY,
  user_id     BIGINT,
  username    VARCHAR(64),
  real_name   VARCHAR(64),
  role        INT,
  action      VARCHAR(32)  NOT NULL,
  target_type VARCHAR(32),
  target_id   BIGINT,
  detail      VARCHAR(500),
  ip          VARCHAR(64),
  create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_operation_log_action ON operation_log (action);
CREATE INDEX idx_operation_log_create_time ON operation_log (create_time);
