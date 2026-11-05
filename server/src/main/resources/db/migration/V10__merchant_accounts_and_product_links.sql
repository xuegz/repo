-- 1. 为已通过但缺少登录账号的商户补开账号
--    账号 = 手机号，初始密码 = 123456（BCrypt）
INSERT INTO sys_user (username, password, real_name, phone, role, merchant_id, status)
SELECT m.phone, '$2a$10$iLebfIeSRBOL3vABkmkgBuW3MYQpyLUwGenqJpMKOn.5fLu06TLdq',
       m.contact_name, m.phone, 2, m.id, 1
FROM merchant m
WHERE m.status = 1
  AND m.phone IS NOT NULL
  AND m.phone <> ''
  AND NOT EXISTS (SELECT 1 FROM sys_user u WHERE u.username = m.phone);

-- 2. 修正示例商户下与品牌不符的商品归属
--    示例建材商行主营卫浴（箭牌/九牧），瓷砖、管材、板材、灯具转回对应品牌商户
UPDATE product SET merchant_id = (SELECT id FROM merchant WHERE name = '马可波罗瓷砖（西安旗舰店）' LIMIT 1)
WHERE merchant_id = (SELECT id FROM merchant WHERE name = '示例建材商行' LIMIT 1) AND brand = '马可波罗';

UPDATE product SET merchant_id = (SELECT id FROM merchant WHERE name = '伟星管业（西安旗舰店）' LIMIT 1)
WHERE merchant_id = (SELECT id FROM merchant WHERE name = '示例建材商行' LIMIT 1) AND brand = '伟星';

UPDATE product SET merchant_id = (SELECT id FROM merchant WHERE name = '兔宝宝健康板材（北三环大明宫店）' LIMIT 1)
WHERE merchant_id = (SELECT id FROM merchant WHERE name = '示例建材商行' LIMIT 1) AND brand = '兔宝宝';

UPDATE product SET merchant_id = (SELECT id FROM merchant WHERE name = '欧普照明（西安旗舰店）' LIMIT 1)
WHERE merchant_id = (SELECT id FROM merchant WHERE name = '示例建材商行' LIMIT 1) AND brand = '欧普';

-- 三秦板材总汇主营板材/瓷砖（鹏鸿、东鹏），恒洁洁具转回恒洁卫浴
UPDATE product SET merchant_id = (SELECT id FROM merchant WHERE name = '恒洁卫浴（西安旗舰店）' LIMIT 1)
WHERE merchant_id = (SELECT id FROM merchant WHERE name = '三秦板材总汇' LIMIT 1) AND brand = '恒洁';
