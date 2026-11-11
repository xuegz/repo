-- 系统配置：键值对，当前用于推广期会员有效期等运营参数
CREATE TABLE sys_config (
  id           BIGINT       AUTO_INCREMENT PRIMARY KEY,
  config_key   VARCHAR(64)  NOT NULL,
  config_value VARCHAR(255) NOT NULL,
  remark       VARCHAR(255),
  update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uk_sys_config_key UNIQUE (config_key)
);

INSERT INTO sys_config (config_key, config_value, remark)
VALUES ('membership_months', '12', '推广期入驻赠送会员的有效期（月）');

-- 存量已通过商户按默认 12 个月回填；后台保存「会员设置」时可再次同步到未开通/已到期商户
UPDATE merchant
SET expire_time = TIMESTAMPADD(MONTH, 12, CURRENT_TIMESTAMP)
WHERE status = 1
  AND (expire_time IS NULL OR expire_time < CURRENT_TIMESTAMP);
