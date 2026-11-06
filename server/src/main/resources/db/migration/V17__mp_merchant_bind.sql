-- 小程序内商户身份绑定：一个微信 openid 绑定一个商户账号，一个商户可绑定多个微信
CREATE TABLE mp_merchant_bind (
  id          BIGINT       AUTO_INCREMENT PRIMARY KEY,
  openid      VARCHAR(64)  NOT NULL,
  user_id     BIGINT       NOT NULL,
  merchant_id BIGINT       NOT NULL,
  create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uk_mp_merchant_bind_openid ON mp_merchant_bind (openid);
CREATE INDEX idx_mp_merchant_bind_merchant ON mp_merchant_bind (merchant_id);

-- 商户账号以手机号作为登录名，补充索引便于按手机号匹配
CREATE INDEX idx_sys_user_phone ON sys_user (phone);
