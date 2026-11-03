-- 询价闭环：商户报价 + 期望到货时间
ALTER TABLE inquiry ADD COLUMN expect_date VARCHAR(32);
ALTER TABLE inquiry ADD COLUMN quote_price DECIMAL(10,2);
ALTER TABLE inquiry ADD COLUMN quote_total DECIMAL(12,2);
ALTER TABLE inquiry ADD COLUMN quote_remark VARCHAR(500);
ALTER TABLE inquiry ADD COLUMN quote_valid_until DATE;
ALTER TABLE inquiry ADD COLUMN quote_time DATETIME;

-- 原 status=1 表示“已联系”，改为 status=2“跟进中”；新状态 0=待报价 1=已报价
UPDATE inquiry SET status = 2 WHERE status = 1;

CREATE INDEX idx_inquiry_openid ON inquiry (openid);

-- 小程序收藏：target_type 1=产品 2=商户
CREATE TABLE favorite (
  id          BIGINT      AUTO_INCREMENT PRIMARY KEY,
  openid      VARCHAR(64) NOT NULL,
  target_type INT         NOT NULL,
  target_id   BIGINT      NOT NULL,
  create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uk_favorite UNIQUE (openid, target_type, target_id)
);

CREATE INDEX idx_favorite_openid ON favorite (openid);
