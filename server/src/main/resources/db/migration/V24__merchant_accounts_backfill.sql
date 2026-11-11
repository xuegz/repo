-- 为已通过但缺少登录账号的商户补开账号
-- 背景：V10 只回填了当时已有的商户；V21 新增的 33 家西安演示商户及平台后续代建档商户没有账号，
--       导致手机号 + 初始密码 123456 登录提示「账号或密码错误」
-- 账号 = 手机号，初始密码 = 123456（BCrypt），首次登录强制修改
INSERT INTO sys_user (username, password, real_name, phone, role, merchant_id, status, must_change_password)
SELECT m.phone, '$2a$10$iLebfIeSRBOL3vABkmkgBuW3MYQpyLUwGenqJpMKOn.5fLu06TLdq',
       m.contact_name, m.phone, 2, m.id, 1, 1
FROM merchant m
WHERE m.status = 1
  AND m.phone IS NOT NULL
  AND m.phone <> ''
  AND NOT EXISTS (SELECT 1 FROM sys_user u WHERE u.username = m.phone);
