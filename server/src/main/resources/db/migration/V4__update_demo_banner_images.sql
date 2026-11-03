-- 演示轮播图由占位图替换为 OSS 横版实拍图
-- 仅更新仍为占位图（/demo/*）的记录

UPDATE banner SET image = 'https://cailutong.oss-cn-beijing.aliyuncs.com/jiancai/2026/11/221ce8fa1e17433db18cdf9cc37bd9c4.jpg' WHERE id = 1 AND image LIKE '/demo/%';
UPDATE banner SET image = 'https://cailutong.oss-cn-beijing.aliyuncs.com/jiancai/2026/11/d2ceab7db01c433184a2c14caa6273a6.jpg' WHERE id = 2 AND image LIKE '/demo/%';
UPDATE banner SET image = 'https://cailutong.oss-cn-beijing.aliyuncs.com/jiancai/2026/11/4ff6a938653e452eb00d4a8e3a2ee7ca.jpg' WHERE id = 3 AND image LIKE '/demo/%';
