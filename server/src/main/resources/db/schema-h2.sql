-- H2 内存库建表脚本（MODE=MySQL），仅用于本地快速演示
-- 正式环境请使用 sql/jiancai.sql 的 MySQL 脚本

CREATE TABLE IF NOT EXISTS sys_user (
  id           BIGINT       AUTO_INCREMENT PRIMARY KEY,
  username     VARCHAR(64)  NOT NULL UNIQUE,
  password     VARCHAR(128) NOT NULL,
  real_name    VARCHAR(64),
  phone        VARCHAR(20),
  role         INT          DEFAULT 2,
  merchant_id  BIGINT,
  status       INT          DEFAULT 1,
  create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP,
  update_time  DATETIME     DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS category (
  id          BIGINT      AUTO_INCREMENT PRIMARY KEY,
  name        VARCHAR(64) NOT NULL,
  parent_id   BIGINT      DEFAULT 0,
  icon        VARCHAR(255),
  sort        INT         DEFAULT 0,
  status      INT         DEFAULT 1,
  create_time DATETIME    DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS merchant (
  id           BIGINT      AUTO_INCREMENT PRIMARY KEY,
  name         VARCHAR(128) NOT NULL,
  contact_name VARCHAR(64),
  phone        VARCHAR(20) NOT NULL,
  market       VARCHAR(128),
  province     VARCHAR(64),
  city         VARCHAR(64),
  address      VARCHAR(255),
  logo         VARCHAR(255),
  intro        VARCHAR(500),
  category_id  BIGINT,
  status       INT      DEFAULT 0,
  views        INT      DEFAULT 0,
  forwards     INT      DEFAULT 0,
  expire_time  DATETIME,
  create_time  DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time  DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS product (
  id            BIGINT       AUTO_INCREMENT PRIMARY KEY,
  merchant_id   BIGINT       NOT NULL,
  category_id   BIGINT,
  name          VARCHAR(128) NOT NULL,
  brand         VARCHAR(64),
  origin        VARCHAR(64),
  spec          VARCHAR(128),
  type_name     VARCHAR(64),
  price         DECIMAL(10,2),
  unit          VARCHAR(16),
  images        VARCHAR(1000),
  description   VARCHAR(2000),
  status        INT      DEFAULT 0,
  reject_reason VARCHAR(200),
  views         INT      DEFAULT 0,
  forwards      INT      DEFAULT 0,
  create_time   DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time   DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS banner (
  id          BIGINT      AUTO_INCREMENT PRIMARY KEY,
  title       VARCHAR(128),
  image       VARCHAR(255) NOT NULL,
  link        VARCHAR(255),
  sort        INT         DEFAULT 0,
  status      INT         DEFAULT 1,
  create_time DATETIME    DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS ad (
  id          BIGINT      AUTO_INCREMENT PRIMARY KEY,
  position    VARCHAR(32) DEFAULT 'home',
  title       VARCHAR(128),
  image       VARCHAR(255) NOT NULL,
  link        VARCHAR(255),
  sort        INT         DEFAULT 0,
  status      INT         DEFAULT 1,
  create_time DATETIME    DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS view_record (
  id          BIGINT   AUTO_INCREMENT PRIMARY KEY,
  target_type INT      NOT NULL,
  target_id   BIGINT   NOT NULL,
  openid      VARCHAR(64),
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS forward_record (
  id          BIGINT   AUTO_INCREMENT PRIMARY KEY,
  target_type INT      NOT NULL,
  target_id   BIGINT   NOT NULL,
  openid      VARCHAR(64),
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS payment_record (
  id           BIGINT       AUTO_INCREMENT PRIMARY KEY,
  merchant_id  BIGINT       NOT NULL,
  amount       DECIMAL(10,2) DEFAULT 365.00,
  years        INT          DEFAULT 1,
  pay_type     VARCHAR(32),
  status       INT          DEFAULT 0,
  pay_time     DATETIME,
  expire_time  DATETIME,
  create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP
);
