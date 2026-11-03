-- 建材市场独立实体：市场信息与商户解耦
-- 1. 新建 market 表，2. 从现有商户数据回填市场，3. 商户通过 market_id 关联

CREATE TABLE market (
  id          BIGINT       AUTO_INCREMENT PRIMARY KEY,
  name        VARCHAR(128) NOT NULL,
  province    VARCHAR(64),
  city        VARCHAR(64),
  address     VARCHAR(255),
  latitude    DECIMAL(10,6),
  longitude   DECIMAL(10,6),
  logo        VARCHAR(255),
  intro       VARCHAR(500),
  sort        INT          NOT NULL DEFAULT 0,
  status      INT          NOT NULL DEFAULT 1,
  create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uk_market_name UNIQUE (name)
);

-- 历史演示数据中的“大明宫建材城”统一归并到北二环店
UPDATE merchant SET market = '大明宫建材家居城·北二环店' WHERE market = '大明宫建材城';

-- 之前临时录入市场时的占位商户，其资料即为市场资料
INSERT INTO market (name, province, city, address, latitude, longitude, intro, sort, status)
SELECT name, province, city, address, latitude, longitude, intro, 0, 1
FROM merchant WHERE contact_name = '市场服务台';

ALTER TABLE merchant ADD COLUMN market_id BIGINT;

UPDATE merchant
SET market_id = (SELECT id FROM market WHERE market.name = merchant.market)
WHERE market IS NOT NULL AND market <> '';

CREATE INDEX idx_merchant_market_id ON merchant(market_id);

-- 市场已成为独立实体，移除占位商户，避免市场出现在商户列表
DELETE FROM merchant WHERE contact_name = '市场服务台' AND market_id IS NOT NULL;
