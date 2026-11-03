-- 演示数据：不写死主键，避免 MySQL 与 H2 自增序列行为差异
-- 默认账号（admin / merchant）由后端首次启动时创建，此处不写入

INSERT INTO category (name, parent_id, sort, status) VALUES
 ('卫浴', 0, 1, 1),
 ('防水', 0, 2, 1),
 ('油漆涂料', 0, 3, 1),
 ('管道', 0, 4, 1),
 ('瓷砖', 0, 5, 1),
 ('板材', 0, 6, 1),
 ('灯具', 0, 7, 1),
 ('五金', 0, 8, 1);

INSERT INTO merchant (name, contact_name, phone, market, province, city, address, logo, intro, category_id, status, views, forwards, latitude, longitude)
VALUES ('示例建材商行', '张老板', '13800138000', '大明宫建材城', '陕西省', '西安市', '北二环大明宫建材市场 A 区 1 排 2 号', '/demo/s01.jpg', '主营卫浴洁具，箭牌、九牧一级代理，本地现货充足，可送货安装', (SELECT id FROM category WHERE name = '卫浴'), 1, 128, 36, 34.308600, 108.940200);

INSERT INTO merchant (name, contact_name, phone, market, province, city, address, logo, intro, category_id, status, views, forwards, latitude, longitude)
VALUES ('西部管业批发', '李经理', '13900139000', '大明宫建材城', '陕西省', '西安市', '北二环大明宫建材市场 B 区 3 排 8 号', '/demo/s02.jpg', '专注给排水管材管件批发零售，联塑、伟星、日丰均有库存', (SELECT id FROM category WHERE name = '管道'), 1, 86, 21, 34.309400, 108.941100);

INSERT INTO merchant (name, contact_name, phone, market, province, city, address, logo, intro, category_id, status, views, forwards, latitude, longitude)
VALUES ('三秦板材总汇', '王老板', '13700137000', '西部建材城', '陕西省', '西安市', '三桥西部建材城 C 栋 12 号', '/demo/s03.jpg', '板材、瓷砖整包批发，可议价，工程量大可送货上门', (SELECT id FROM category WHERE name = '板材'), 1, 54, 12, 34.290500, 108.828800);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards) VALUES
 ((SELECT id FROM merchant WHERE name = '示例建材商行'), (SELECT id FROM category WHERE name = '卫浴'), '连体坐便器', '箭牌', '广东佛山', '305mm 坑距', '马桶', 899.00, '件', '/demo/p01.jpg,/demo/p01-2.jpg', '虹吸式冲水，静音节水，含安装配件', 1, 45, 12);
INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards) VALUES
 ((SELECT id FROM merchant WHERE name = '示例建材商行'), (SELECT id FROM category WHERE name = '卫浴'), '淋浴花洒套装', '九牧', '福建南安', '三出水', '花洒', 568.00, '套', '/demo/p02.jpg', '主体全铜，质保 5 年，带顶喷', 1, 32, 8);
INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards) VALUES
 ((SELECT id FROM merchant WHERE name = '示例建材商行'), (SELECT id FROM category WHERE name = '瓷砖'), '通体大理石瓷砖', '马可波罗', '广东佛山', '800*800', '地砖', 58.00, '平方米', '/demo/p03.jpg,/demo/p03-2.jpg', '整箱 4 片，适用于客厅、卧室', 1, 67, 15);
INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards) VALUES
 ((SELECT id FROM merchant WHERE name = '示例建材商行'), (SELECT id FROM category WHERE name = '管道'), 'PPR 给水管', '伟星', '浙江临海', 'DN25', '水管', 12.50, '米', '/demo/p04.jpg', '热水管，抗菌内壁，耐压 25 公斤', 1, 23, 5);
INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards) VALUES
 ((SELECT id FROM merchant WHERE name = '示例建材商行'), (SELECT id FROM category WHERE name = '板材'), '生态免漆板', '兔宝宝', '浙江德清', '1220*2440*18mm', '板材', 268.00, '张', '/demo/p05.jpg,/demo/p05-2.jpg', 'E0 级环保，可用于衣柜、橱柜', 1, 18, 4);
INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards) VALUES
 ((SELECT id FROM merchant WHERE name = '示例建材商行'), (SELECT id FROM category WHERE name = '灯具'), 'LED 平板灯', '欧普', '广东中山', '600*600 48W', '灯具', 96.00, '个', '/demo/p06.jpg', '办公吊顶适用，含驱动电源', 1, 11, 2);
INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards) VALUES
 ((SELECT id FROM merchant WHERE name = '西部管业批发'), (SELECT id FROM category WHERE name = '管道'), 'PE 给水管', '联塑', '广东顺德', 'DN110', '水管', 86.00, '米', '/demo/p07.jpg', '市政给水工程常用，整根 6 米', 1, 12, 3);
INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards) VALUES
 ((SELECT id FROM merchant WHERE name = '西部管业批发'), (SELECT id FROM category WHERE name = '管道'), 'PVC 排水管', '联塑', '广东顺德', 'DN50', '水管', 32.00, '米', '/demo/p08.jpg', '楼层排水专用，含胶圈', 1, 7, 1);
INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards) VALUES
 ((SELECT id FROM merchant WHERE name = '示例建材商行'), (SELECT id FROM category WHERE name = '灯具'), '筒灯射灯', '雷士', '广东惠州', '7W 开孔 75mm', '筒灯', 26.00, '个', '/demo/p09.jpg', '三色变光，含变压器', 0, 0, 0);
INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards) VALUES
 ((SELECT id FROM merchant WHERE name = '示例建材商行'), (SELECT id FROM category WHERE name = '防水'), 'K11 防水涂料', '德高', '广东广州', '18kg', '防水涂料', 320.00, '桶', '/demo/p10.jpg', '刚柔并济，卫生间专用', 3, 9, 2);
INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards) VALUES
 ((SELECT id FROM merchant WHERE name = '三秦板材总汇'), (SELECT id FROM category WHERE name = '板材'), '生态免漆板', '鹏鸿', '河北文安', '1220*2440*18mm', '板材', 255.00, '张', '/demo/p11.jpg', 'E1 级环保板材，整包 30 张起批', 1, 20, 6);
INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards) VALUES
 ((SELECT id FROM merchant WHERE name = '西部管业批发'), (SELECT id FROM category WHERE name = '管道'), 'PPR 给水管', '日丰', '广东佛山', 'DN25', '水管', 11.80, '米', '/demo/p12.jpg', '冷热水通用，厂家质保 50 年', 1, 31, 9);
INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards) VALUES
 ((SELECT id FROM merchant WHERE name = '三秦板材总汇'), (SELECT id FROM category WHERE name = '卫浴'), '连体坐便器', '恒洁', '广东潮州', '305mm 坑距', '马桶', 860.00, '件', '/demo/p13.jpg', '超漩冲刷，静音缓降盖板', 1, 18, 5);
INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards) VALUES
 ((SELECT id FROM merchant WHERE name = '三秦板材总汇'), (SELECT id FROM category WHERE name = '瓷砖'), '通体大理石瓷砖', '东鹏', '广东佛山', '800*800', '地砖', 52.00, '平方米', '/demo/p14.jpg', '整箱 4 片，工程量大可议价', 1, 26, 7);

INSERT INTO banner (title, image, link, sort, status) VALUES
 ('建材厂家直供，同城调货', '/demo/b01.jpg', '', 3, 1),
 ('免费入驻，限时开放', '/demo/b02.jpg', '', 2, 1),
 ('多商户比价，一键询价', '/demo/b03.jpg', '', 1, 1);

INSERT INTO ad (position, title, image, link, sort, status) VALUES
 ('home', '大明宫建材城专区', '/demo/a01.jpg', '', 2, 1),
 ('home', '板材整包起批，可议价', '/demo/a02.jpg', '', 1, 1);

INSERT INTO notice (title, content, sort, status) VALUES
 ('平台公测期，商户入驻全程免费', '公测期间入驻免收年度服务费，发布产品不限量，欢迎各市场档口入驻。', 3, 1),
 ('新功能上线：同款比价 + 电话询价', '产品详情页已支持查看同城其他商户报价，支持一键电话询价与在线留言。', 2, 1),
 ('温馨提示：报价请以电话确认为准', '平台展示价格为商户自主报价，实际成交请以电话沟通及现场看样为准。', 1, 1);
