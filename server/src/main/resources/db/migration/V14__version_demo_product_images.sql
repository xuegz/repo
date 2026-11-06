-- 演示商品图切换到带版本号的目录，绕过客户端对旧占位图的长缓存
UPDATE product
SET images = REPLACE(images, '/demo/brand/', '/demo/brand/v20261105/')
WHERE images LIKE '%/demo/brand/%'
  AND images NOT LIKE '%/demo/brand/v20261105/%';
