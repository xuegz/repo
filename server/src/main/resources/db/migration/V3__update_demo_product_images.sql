-- 演示商品图片由占位图替换为 OSS 实拍图
-- 仅更新仍为占位图（/demo/*）的记录，避免覆盖商户后台上传的真实图片

UPDATE product SET images = 'https://cailutong.oss-cn-beijing.aliyuncs.com/jiancai/2026/11/2a77efd6785d4457b615ffbf049f380c.jpg' WHERE id = 1 AND images LIKE '/demo/%';
UPDATE product SET images = 'https://cailutong.oss-cn-beijing.aliyuncs.com/jiancai/2026/11/051a8982d99048b3a919f525416d369f.jpg' WHERE id = 2 AND images LIKE '/demo/%';
UPDATE product SET images = 'https://cailutong.oss-cn-beijing.aliyuncs.com/jiancai/2026/11/d1ba43cc6ad14773af4ef1055f2d8904.jpg' WHERE id = 3 AND images LIKE '/demo/%';
UPDATE product SET images = 'https://cailutong.oss-cn-beijing.aliyuncs.com/jiancai/2026/11/f1eb21bb870a4238a185688b2b93bea2.jpg' WHERE id = 4 AND images LIKE '/demo/%';
UPDATE product SET images = 'https://cailutong.oss-cn-beijing.aliyuncs.com/jiancai/2026/11/6a711c45e6d04b7fa68e7207cb1c03ec.jpg' WHERE id = 5 AND images LIKE '/demo/%';
UPDATE product SET images = 'https://cailutong.oss-cn-beijing.aliyuncs.com/jiancai/2026/11/595af2a5b61f42c0a7f77b2ba1e3cc44.jpg' WHERE id = 6 AND images LIKE '/demo/%';
UPDATE product SET images = 'https://cailutong.oss-cn-beijing.aliyuncs.com/jiancai/2026/11/fcdc5b98d51f4d4f86c770161cb653a1.jpg' WHERE id = 7 AND images LIKE '/demo/%';
UPDATE product SET images = 'https://cailutong.oss-cn-beijing.aliyuncs.com/jiancai/2026/11/e7ccb5b2e64a409aba5805b495473865.jpg' WHERE id = 8 AND images LIKE '/demo/%';
UPDATE product SET images = 'https://cailutong.oss-cn-beijing.aliyuncs.com/jiancai/2026/11/4730f4d1f1ee4108a82657068a7852c7.jpg' WHERE id = 9 AND images LIKE '/demo/%';
UPDATE product SET images = 'https://cailutong.oss-cn-beijing.aliyuncs.com/jiancai/2026/11/a676383b2a98449e93237c40cfb4ed2d.jpg' WHERE id = 10 AND images LIKE '/demo/%';
UPDATE product SET images = 'https://cailutong.oss-cn-beijing.aliyuncs.com/jiancai/2026/11/69d2c77843b243be91a1eb54028ba1f4.jpg' WHERE id = 11 AND images LIKE '/demo/%';
UPDATE product SET images = 'https://cailutong.oss-cn-beijing.aliyuncs.com/jiancai/2026/11/f84047480383468c9dda631d0f79204c.jpg' WHERE id = 12 AND images LIKE '/demo/%';
UPDATE product SET images = 'https://cailutong.oss-cn-beijing.aliyuncs.com/jiancai/2026/11/69620ed9d5bb4ef4bbfade997f984065.jpg' WHERE id = 13 AND images LIKE '/demo/%';
UPDATE product SET images = 'https://cailutong.oss-cn-beijing.aliyuncs.com/jiancai/2026/11/db79d88fdd2d4ebb983ff075b3cdede6.jpg' WHERE id = 14 AND images LIKE '/demo/%';
