-- 商户初始密码统一由平台下发，首次登录必须修改
ALTER TABLE sys_user ADD COLUMN must_change_password TINYINT NOT NULL DEFAULT 0;

-- 历史商户账号（导入数据与审核开通）初始密码均为 123456，强制首次改密
-- 演示账号 merchant 保留默认密码，便于演示环境直接登录
UPDATE sys_user SET must_change_password = 1
WHERE role = 2 AND username <> 'merchant';
