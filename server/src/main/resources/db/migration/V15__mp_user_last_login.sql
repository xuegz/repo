-- 用户最近登录时间：登录即记录，保证未设置昵称头像的用户也能被统计
ALTER TABLE mp_user ADD COLUMN last_login_time DATETIME;
UPDATE mp_user SET last_login_time = update_time WHERE last_login_time IS NULL;
CREATE INDEX idx_mp_user_last_login ON mp_user (last_login_time);

-- 历史用户回填：把产生过浏览 / 转发 / 电话 / 询价 / 收藏的真实微信用户补进用户表
INSERT INTO mp_user (openid, create_time, update_time, last_login_time)
SELECT t.openid, MIN(t.create_time), MAX(t.create_time), MAX(t.create_time)
FROM (
    SELECT openid, create_time FROM view_record WHERE openid IS NOT NULL AND openid <> ''
    UNION ALL
    SELECT openid, create_time FROM forward_record WHERE openid IS NOT NULL AND openid <> ''
    UNION ALL
    SELECT openid, create_time FROM call_record WHERE openid IS NOT NULL AND openid <> ''
    UNION ALL
    SELECT openid, create_time FROM inquiry WHERE openid IS NOT NULL AND openid <> ''
    UNION ALL
    SELECT openid, create_time FROM favorite WHERE openid IS NOT NULL AND openid <> ''
) t
LEFT JOIN mp_user u ON u.openid = t.openid
WHERE u.id IS NULL
  AND t.openid NOT LIKE 'mp_%'
GROUP BY t.openid;
