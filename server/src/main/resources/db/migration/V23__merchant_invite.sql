-- 商户邀请同行入驻：邀请码为商户专属，被邀请商户在入驻申请时填写
-- inviter_id 记录邀请关系，仅用于平台拉新统计，不自动发放奖励
ALTER TABLE merchant ADD COLUMN invite_code VARCHAR(16);
ALTER TABLE merchant ADD COLUMN inviter_id BIGINT;
ALTER TABLE merchant ADD COLUMN invite_time DATETIME;

CREATE UNIQUE INDEX uk_merchant_invite_code ON merchant (invite_code);
