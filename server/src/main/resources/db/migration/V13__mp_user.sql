-- 小程序用户资料（按 openid 保存昵称与头像）
CREATE TABLE mp_user (
  id          BIGINT       AUTO_INCREMENT PRIMARY KEY,
  openid      VARCHAR(64)  NOT NULL,
  nickname    VARCHAR(64),
  avatar      VARCHAR(500),
  create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uk_mp_user_openid ON mp_user (openid);