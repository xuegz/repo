-- =============================================================
--  建材商户平台 数据库初始化脚本
--  MySQL 8.0 / utf8mb4
--  执行方式：mysql -u root -p < jiancai.sql
-- =============================================================

CREATE DATABASE IF NOT EXISTS jiancai
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

USE jiancai;

-- -------------------------------------------------------------
-- 1. 账号表（平台管理员 + 商户账号，通过 role 区分）
-- -------------------------------------------------------------
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
  id           BIGINT       PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  username     VARCHAR(64)  NOT NULL UNIQUE            COMMENT '登录账号',
  password     VARCHAR(128) NOT NULL                   COMMENT '密码(BCrypt加密)',
  real_name    VARCHAR(64)  DEFAULT NULL               COMMENT '姓名/联系人',
  phone        VARCHAR(20)  DEFAULT NULL               COMMENT '手机号',
  role         TINYINT      NOT NULL DEFAULT 2         COMMENT '角色 1=平台管理员 2=商户',
  merchant_id  BIGINT       DEFAULT NULL               COMMENT '所属商户ID(管理员为空)',
  status       TINYINT      NOT NULL DEFAULT 1         COMMENT '状态 1=启用 0=禁用',
  create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_role (role),
  KEY idx_merchant (merchant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账号表';

-- -------------------------------------------------------------
-- 2. 品类表（卫浴/防水/漆/管道/瓷砖/板材/灯具…支持二级）
-- -------------------------------------------------------------
DROP TABLE IF EXISTS category;
CREATE TABLE category (
  id          BIGINT      PRIMARY KEY AUTO_INCREMENT,
  name        VARCHAR(64) NOT NULL                COMMENT '品类名称',
  parent_id   BIGINT      NOT NULL DEFAULT 0      COMMENT '父级ID 0=一级',
  icon        VARCHAR(255) DEFAULT NULL           COMMENT '图标',
  sort        INT         NOT NULL DEFAULT 0      COMMENT '排序(越小越靠前)',
  status      TINYINT     NOT NULL DEFAULT 1      COMMENT '1=启用 0=停用',
  create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_parent (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='品类表';

-- -------------------------------------------------------------
-- 3. 商户/门店表
-- -------------------------------------------------------------
DROP TABLE IF EXISTS merchant;
CREATE TABLE merchant (
  id           BIGINT       PRIMARY KEY AUTO_INCREMENT,
  name         VARCHAR(128) NOT NULL               COMMENT '商户/门店名称',
  contact_name VARCHAR(64)  DEFAULT NULL           COMMENT '联系人',
  phone        VARCHAR(20)  NOT NULL               COMMENT '联系电话(档口联系)',
  market       VARCHAR(128) DEFAULT NULL           COMMENT '所属建材市场,如 大明宫建材城',
  province     VARCHAR(64)  DEFAULT NULL           COMMENT '省',
  city         VARCHAR(64)  DEFAULT NULL           COMMENT '市',
  address      VARCHAR(255) DEFAULT NULL           COMMENT '详细地址',
  logo         VARCHAR(255) DEFAULT NULL           COMMENT '门头/头像',
  intro        VARCHAR(500) DEFAULT NULL           COMMENT '简介',
  category_id  BIGINT       DEFAULT NULL           COMMENT '主营品类ID',
  status       TINYINT      NOT NULL DEFAULT 0     COMMENT '0=待审核 1=已通过 2=已拒绝',
  views        INT          NOT NULL DEFAULT 0     COMMENT '被浏览次数',
  forwards     INT          NOT NULL DEFAULT 0     COMMENT '被转发次数',
  expire_time  DATETIME     DEFAULT NULL           COMMENT '会员到期时间',
  create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_status (status),
  KEY idx_market (market)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商户表';

-- -------------------------------------------------------------
-- 4. 产品表
-- -------------------------------------------------------------
DROP TABLE IF EXISTS product;
CREATE TABLE product (
  id           BIGINT        PRIMARY KEY AUTO_INCREMENT,
  merchant_id  BIGINT        NOT NULL                COMMENT '所属商户',
  category_id  BIGINT        DEFAULT NULL            COMMENT '品类',
  name         VARCHAR(128)  NOT NULL                COMMENT '产品名称',
  brand        VARCHAR(64)   DEFAULT NULL            COMMENT '品牌',
  origin       VARCHAR(64)   DEFAULT NULL            COMMENT '产地',
  spec         VARCHAR(128)  DEFAULT NULL            COMMENT '规格/型号',
  type_name    VARCHAR(64)   DEFAULT NULL            COMMENT '类型',
  price        DECIMAL(10,2) DEFAULT NULL            COMMENT '价格',
  unit         VARCHAR(16)   DEFAULT NULL            COMMENT '单位',
  images       VARCHAR(1000) DEFAULT NULL            COMMENT '产品图(多张逗号分隔)',
  description  TEXT                                  COMMENT '产品描述',
  status       TINYINT       NOT NULL DEFAULT 0     COMMENT '0=待审核 1=在售 2=拒绝 3=下架',
  reject_reason VARCHAR(200) DEFAULT NULL            COMMENT '拒绝原因',
  views        INT           NOT NULL DEFAULT 0     COMMENT '被浏览',
  forwards     INT           NOT NULL DEFAULT 0     COMMENT '被转发',
  create_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_merchant (merchant_id),
  KEY idx_category (category_id),
  KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品表';

-- -------------------------------------------------------------
-- 5. 首页轮播/头图表
-- -------------------------------------------------------------
DROP TABLE IF EXISTS banner;
CREATE TABLE banner (
  id          BIGINT      PRIMARY KEY AUTO_INCREMENT,
  title       VARCHAR(128) DEFAULT NULL,
  image       VARCHAR(255) NOT NULL                COMMENT '图片地址',
  link        VARCHAR(255) DEFAULT NULL            COMMENT '跳转链接',
  sort        INT         NOT NULL DEFAULT 0,
  status      TINYINT     NOT NULL DEFAULT 1,
  create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='首页轮播表';

-- -------------------------------------------------------------
-- 6. 广告位表
-- -------------------------------------------------------------
DROP TABLE IF EXISTS ad;
CREATE TABLE ad (
  id          BIGINT      PRIMARY KEY AUTO_INCREMENT,
  position    VARCHAR(32) NOT NULL DEFAULT 'home' COMMENT '位置 home=首页 list=列表页',
  title       VARCHAR(128) DEFAULT NULL,
  image       VARCHAR(255) NOT NULL,
  link        VARCHAR(255) DEFAULT NULL,
  sort        INT         NOT NULL DEFAULT 0,
  status      TINYINT     NOT NULL DEFAULT 1,
  create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='广告位表';

-- -------------------------------------------------------------
-- 7. 浏览记录表（小程序端用户浏览/商户被浏览统计来源）
-- -------------------------------------------------------------
DROP TABLE IF EXISTS view_record;
CREATE TABLE view_record (
  id          BIGINT   PRIMARY KEY AUTO_INCREMENT,
  target_type TINYINT  NOT NULL                COMMENT '1=产品 2=商户',
  target_id   BIGINT   NOT NULL                COMMENT '产品ID/商户ID',
  openid      VARCHAR(64) DEFAULT NULL         COMMENT '浏览者标识',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_openid (openid),
  KEY idx_target (target_type, target_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='浏览记录表';

-- -------------------------------------------------------------
-- 8. 转发记录表
-- -------------------------------------------------------------
DROP TABLE IF EXISTS forward_record;
CREATE TABLE forward_record (
  id          BIGINT   PRIMARY KEY AUTO_INCREMENT,
  target_type TINYINT  NOT NULL                COMMENT '1=产品 2=商户',
  target_id   BIGINT   NOT NULL,
  openid      VARCHAR(64) DEFAULT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='转发记录表';

-- -------------------------------------------------------------
-- 9. 缴费/会员订单表（365元/年）
-- -------------------------------------------------------------
DROP TABLE IF EXISTS payment_record;
CREATE TABLE payment_record (
  id           BIGINT        PRIMARY KEY AUTO_INCREMENT,
  merchant_id  BIGINT        NOT NULL,
  amount       DECIMAL(10,2) NOT NULL DEFAULT 365.00 COMMENT '金额',
  years        INT           NOT NULL DEFAULT 1      COMMENT '购买年限',
  pay_type     VARCHAR(32)   DEFAULT NULL            COMMENT '支付方式',
  status       TINYINT       NOT NULL DEFAULT 0      COMMENT '0=待支付 1=已支付',
  pay_time     DATETIME      DEFAULT NULL,
  expire_time  DATETIME      DEFAULT NULL            COMMENT '到期时间',
  create_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='缴费记录表';

-- =============================================================
--  初始化数据
-- =============================================================
INSERT INTO category (id, name, parent_id, sort) VALUES
 (1,'卫浴',0,1),(2,'防水',0,2),(3,'油漆涂料',0,3),(4,'管道',0,4),
 (5,'瓷砖',0,5),(6,'板材',0,6),(7,'灯具',0,7),(8,'五金',0,8);

-- 默认账号（admin/123456、merchant/123456）由后端首次启动时自动创建，密码经 BCrypt 加密后写入

INSERT INTO merchant (id, name, contact_name, phone, market, province, city, address, category_id, status)
VALUES (1, '示例建材商行', '张老板', '13800138000', '大明宫建材城', '陕西省', '西安市', '北二环大明宫建材市场A区1排2号', 1, 1);
