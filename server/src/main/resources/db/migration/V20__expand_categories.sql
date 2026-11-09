-- 扩充一级品类：补齐主材大类（幂等，重名不再插入）

INSERT INTO category (name, parent_id, sort, status)
SELECT t.name, 0, t.sort_no, 1 FROM (
  SELECT '门窗' AS name, 9 AS sort_no
  UNION ALL SELECT '地板', 10
  UNION ALL SELECT '全屋定制', 11
  UNION ALL SELECT '吊顶', 12
  UNION ALL SELECT '石材', 13
  UNION ALL SELECT '电工', 14
) t
WHERE NOT EXISTS (SELECT 1 FROM category c WHERE c.name = t.name);
