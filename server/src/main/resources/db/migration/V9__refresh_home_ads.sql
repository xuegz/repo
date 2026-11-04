-- 首页广告位配图与跳转更新
UPDATE ad SET image = '/demo/ad-market.jpg', title = '大明宫建材城专区', link = '/pages/shops/shops' WHERE id = 1;
UPDATE ad SET image = '/demo/ad-plywood.jpg', title = '板材整包起批，可议价', link = 'product?keyword=板材' WHERE id = 2;

INSERT INTO ad (position, title, image, link, sort, status)
SELECT 'home', '品牌卫浴 · 灯具专区', '/demo/ad-brand.jpg', 'product?keyword=卫浴', 3, 1
WHERE NOT EXISTS (SELECT 1 FROM ad WHERE image = '/demo/ad-brand.jpg');
