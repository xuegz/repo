-- 建表脚本：同时兼容 MySQL 8 与 H2(MODE=MySQL)
-- 约定：不使用内联 COMMENT / ENGINE / KEY 子句，索引单独创建；时间字段默认值由数据库给出。

CREATE TABLE sys_user (
  id           BIGINT       AUTO_INCREMENT PRIMARY KEY,
  username     VARCHAR(64)  NOT NULL,
  password     VARCHAR(128) NOT NULL,
  real_name    VARCHAR(64),
  phone        VARCHAR(20),
  role         INT          NOT NULL DEFAULT 2,
  merchant_id  BIGINT,
  status       INT          NOT NULL DEFAULT 1,
  create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uk_sys_user_username UNIQUE (username)
);

CREATE TABLE category (
  id          BIGINT      AUTO_INCREMENT PRIMARY KEY,
  name        VARCHAR(64) NOT NULL,
  parent_id   BIGINT      NOT NULL DEFAULT 0,
  icon        VARCHAR(255),
  sort        INT         NOT NULL DEFAULT 0,
  status      INT         NOT NULL DEFAULT 1,
  create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE merchant (
  id           BIGINT       AUTO_INCREMENT PRIMARY KEY,
  name         VARCHAR(128) NOT NULL,
  contact_name VARCHAR(64),
  phone        VARCHAR(20)  NOT NULL,
  market       VARCHAR(128),
  province     VARCHAR(64),
  city         VARCHAR(64),
  address      VARCHAR(255),
  logo         VARCHAR(255),
  intro        VARCHAR(500),
  category_id  BIGINT,
  status       INT          NOT NULL DEFAULT 0,
  views        INT          NOT NULL DEFAULT 0,
  forwards     INT          NOT NULL DEFAULT 0,
  calls        INT          NOT NULL DEFAULT 0,
  latitude     DECIMAL(10,6),
  longitude    DECIMAL(10,6),
  expire_time  DATETIME,
  create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE product (
  id            BIGINT        AUTO_INCREMENT PRIMARY KEY,
  merchant_id   BIGINT        NOT NULL,
  category_id   BIGINT,
  name          VARCHAR(128)  NOT NULL,
  brand         VARCHAR(64),
  origin        VARCHAR(64),
  spec          VARCHAR(128),
  type_name     VARCHAR(64),
  price         DECIMAL(10,2),
  unit          VARCHAR(16),
  images        VARCHAR(1000),
  description   VARCHAR(2000),
  status        INT           NOT NULL DEFAULT 0,
  reject_reason VARCHAR(200),
  views         INT           NOT NULL DEFAULT 0,
  forwards      INT           NOT NULL DEFAULT 0,
  calls         INT           NOT NULL DEFAULT 0,
  create_time   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE banner (
  id          BIGINT      AUTO_INCREMENT PRIMARY KEY,
  title       VARCHAR(128),
  image       VARCHAR(255) NOT NULL,
  link        VARCHAR(255),
  sort        INT         NOT NULL DEFAULT 0,
  status      INT         NOT NULL DEFAULT 1,
  create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ad (
  id          BIGINT      AUTO_INCREMENT PRIMARY KEY,
  position    VARCHAR(32) NOT NULL DEFAULT 'home',
  title       VARCHAR(128),
  image       VARCHAR(255) NOT NULL,
  link        VARCHAR(255),
  sort        INT         NOT NULL DEFAULT 0,
  status      INT         NOT NULL DEFAULT 1,
  create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE view_record (
  id          BIGINT   AUTO_INCREMENT PRIMARY KEY,
  target_type INT      NOT NULL,
  target_id   BIGINT   NOT NULL,
  openid      VARCHAR(64),
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE forward_record (
  id          BIGINT   AUTO_INCREMENT PRIMARY KEY,
  target_type INT      NOT NULL,
  target_id   BIGINT   NOT NULL,
  openid      VARCHAR(64),
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE call_record (
  id          BIGINT   AUTO_INCREMENT PRIMARY KEY,
  target_type INT      NOT NULL,
  target_id   BIGINT   NOT NULL,
  merchant_id BIGINT   NOT NULL,
  openid      VARCHAR(64),
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE inquiry (
  id          BIGINT       AUTO_INCREMENT PRIMARY KEY,
  product_id  BIGINT,
  merchant_id BIGINT       NOT NULL,
  quantity    VARCHAR(64),
  contact     VARCHAR(64)  NOT NULL,
  remark      VARCHAR(500),
  openid      VARCHAR(64),
  status      INT          NOT NULL DEFAULT 0,
  create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE notice (
  id          BIGINT        AUTO_INCREMENT PRIMARY KEY,
  title       VARCHAR(128)  NOT NULL,
  content     VARCHAR(1000),
  sort        INT           NOT NULL DEFAULT 0,
  status      INT           NOT NULL DEFAULT 1,
  create_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE payment_record (
  id           BIGINT        AUTO_INCREMENT PRIMARY KEY,
  merchant_id  BIGINT        NOT NULL,
  amount       DECIMAL(10,2) NOT NULL DEFAULT 365.00,
  years        INT           NOT NULL DEFAULT 1,
  pay_type     VARCHAR(32),
  status       INT           NOT NULL DEFAULT 0,
  pay_time     DATETIME,
  expire_time  DATETIME,
  create_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_sys_user_role ON sys_user (role);
CREATE INDEX idx_sys_user_merchant ON sys_user (merchant_id);
CREATE INDEX idx_category_parent ON category (parent_id);
CREATE INDEX idx_merchant_status ON merchant (status);
CREATE INDEX idx_merchant_market ON merchant (market);
CREATE INDEX idx_product_merchant ON product (merchant_id);
CREATE INDEX idx_product_category ON product (category_id);
CREATE INDEX idx_product_status ON product (status);
CREATE INDEX idx_view_openid ON view_record (openid);
CREATE INDEX idx_view_target ON view_record (target_type, target_id);
CREATE INDEX idx_call_merchant ON call_record (merchant_id);
CREATE INDEX idx_call_target ON call_record (target_type, target_id);
CREATE INDEX idx_inquiry_merchant ON inquiry (merchant_id);
