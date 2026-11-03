-- 西安重点品牌示例商品：每家品牌门店 2 个，图片为同风格演示卡片
-- 商品图位于 /demo/brand/<品牌缩写>-<序号>.jpg

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '马可波罗瓷砖（西安旗舰店）'), (SELECT id FROM category WHERE name = '瓷砖'), '通体大理石瓷砖', '马可波罗', '广东佛山', '800*800', '地砖', 68.00, '平方米', '/demo/brand/mkb-1.jpg', '通体大理石纹理，耐磨防污，适合客厅、餐厅大面积铺贴。', 1, 76, 12);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '马可波罗瓷砖（西安旗舰店）'), (SELECT id FROM category WHERE name = '瓷砖'), '岩板背景墙', '马可波罗', '广东佛山', '1200*2400*9mm', '岩板', 368.00, '平方米', '/demo/brand/mkb-2.jpg', '大规格岩板，可做背景墙、台面，支持上楼与铺贴指导。', 1, 54, 9);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '东鹏瓷砖（西安旗舰店）'), (SELECT id FROM category WHERE name = '瓷砖'), '通体大理石瓷砖', '东鹏', '广东佛山', '750*1500', '地砖', 98.00, '平方米', '/demo/brand/dp-1.jpg', '750×1500 大板，纹理连贯，适合现代简约空间。', 1, 63, 10);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '东鹏瓷砖（西安旗舰店）'), (SELECT id FROM category WHERE name = '瓷砖'), '仿古防滑地砖', '东鹏', '广东佛山', '600*600', '地砖', 45.00, '平方米', '/demo/brand/dp-2.jpg', '防滑耐磨，适用于阳台、厨卫及公共区域。', 1, 41, 7);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '冠珠陶瓷（西安旗舰店）'), (SELECT id FROM category WHERE name = '瓷砖'), '通体大理石瓷砖', '冠珠', '广东佛山', '800*800', '地砖', 58.00, '平方米', '/demo/brand/gz-1.jpg', '通体坯体，纹理自然，整箱起批可议价。', 1, 38, 6);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '冠珠陶瓷（西安旗舰店）'), (SELECT id FROM category WHERE name = '瓷砖'), '工程抛光砖', '冠珠', '广东佛山', '600*600', '抛光砖', 32.00, '平方米', '/demo/brand/gz-2.jpg', '工程常用抛光砖，耐磨易清洁，支持大批量供货。', 1, 25, 4);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '箭牌卫浴（西安旗舰店）'), (SELECT id FROM category WHERE name = '卫浴'), '连体坐便器', '箭牌', '广东佛山', '305mm 坑距', '马桶', 1299.00, '件', '/demo/brand/jp-1.jpg', '虹吸式冲水，静音节水，含安装配件。', 1, 71, 11);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '箭牌卫浴（西安旗舰店）'), (SELECT id FROM category WHERE name = '卫浴'), '恒温花洒套装', '箭牌', '广东佛山', '三出水', '花洒', 899.00, '套', '/demo/brand/jp-2.jpg', '恒温出水，三档模式，主体全铜质保 5 年。', 1, 49, 8);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '九牧卫浴（西安旗舰店）'), (SELECT id FROM category WHERE name = '卫浴'), '智能马桶', '九牧', '福建南安', '305mm 坑距', '智能马桶', 2699.00, '件', '/demo/brand/jm-1.jpg', '即热冲洗、座圈加热，支持智能除臭。', 1, 66, 10);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '九牧卫浴（西安旗舰店）'), (SELECT id FROM category WHERE name = '卫浴'), '淋浴花洒套装', '九牧', '福建南安', '三出水', '花洒', 799.00, '套', '/demo/brand/jm-2.jpg', '增压出水，雨淋顶喷，含升降杆与软管。', 1, 45, 7);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '恒洁卫浴（西安旗舰店）'), (SELECT id FROM category WHERE name = '卫浴'), '智能马桶', '恒洁', '广东潮州', '305mm 坑距', '智能马桶', 2899.00, '件', '/demo/brand/hj-1.jpg', '超漩冲刷，静音缓降盖板，适合主卫升级。', 1, 58, 9);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '恒洁卫浴（西安旗舰店）'), (SELECT id FROM category WHERE name = '卫浴'), '浴室柜组合', '恒洁', '广东潮州', '80cm', '浴室柜', 1999.00, '套', '/demo/brand/hj-2.jpg', '岩板台面浴室柜，含镜柜与陶瓷盆，80cm 规格。', 1, 37, 5);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '欧派全屋定制（西安旗舰店）'), (SELECT id FROM category WHERE name = '板材'), '整体橱柜', '欧派', '广东广州', '按延米计价', '橱柜', 1680.00, '延米', '/demo/brand/op-1.jpg', '整体橱柜按延米报价，含地柜、吊柜与台面，可上门测量。', 1, 52, 8);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '欧派全屋定制（西安旗舰店）'), (SELECT id FROM category WHERE name = '板材'), '定制衣柜', '欧派', '广东广州', '按展开面积', '衣柜', 980.00, '平方米', '/demo/brand/op-2.jpg', '环保板材定制衣柜，按展开面积计价，含五金基础配置。', 1, 47, 7);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '索菲亚全屋定制（西安旗舰店）'), (SELECT id FROM category WHERE name = '板材'), '定制衣柜', '索菲亚', '广东广州', '按展开面积', '衣柜', 899.00, '平方米', '/demo/brand/sfy-1.jpg', '定制衣柜，多种门板与内配可选，免费上门量尺。', 1, 43, 6);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '索菲亚全屋定制（西安旗舰店）'), (SELECT id FROM category WHERE name = '板材'), '入户玄关柜', '索菲亚', '广东广州', '定制尺寸', '玄关柜', 1280.00, '平方米', '/demo/brand/sfy-2.jpg', '入户玄关柜定制，可做换鞋凳与挂衣区，按面积计价。', 1, 31, 4);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '兔宝宝健康板材（北三环大明宫店）'), (SELECT id FROM category WHERE name = '板材'), '生态免漆板', '兔宝宝', '浙江德清', '1220*2440*18mm', '板材', 285.00, '张', '/demo/brand/tbb-1.jpg', 'E0 级生态免漆板，适用于衣柜、橱柜与背景墙。', 1, 56, 9);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '兔宝宝健康板材（北三环大明宫店）'), (SELECT id FROM category WHERE name = '板材'), '多层实木板', '兔宝宝', '浙江德清', '1220*2440*18mm', '板材', 198.00, '张', '/demo/brand/tbb-2.jpg', '多层实木基材，握钉力强，适合柜体与门板。', 1, 39, 6);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '千年舟板材（北三环大明宫店）'), (SELECT id FROM category WHERE name = '板材'), '生态板', '千年舟', '浙江杭州', '1220*2440*18mm', '板材', 268.00, '张', '/demo/brand/qnz-1.jpg', '环保生态板，饰面多样，整包 30 张起批。', 1, 42, 6);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '千年舟板材（北三环大明宫店）'), (SELECT id FROM category WHERE name = '板材'), '阻燃板', '千年舟', '浙江杭州', '1220*2440*15mm', '板材', 215.00, '张', '/demo/brand/qnz-2.jpg', '阻燃板，适用于工装与公共空间基层。', 1, 28, 4);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '圣象地板（西安旗舰店）'), (SELECT id FROM category WHERE name = '板材'), '强化复合地板', '圣象', '江苏丹阳', '1215*165*12mm', '地板', 128.00, '平方米', '/demo/brand/sx-1.jpg', '强化复合地板，耐磨易打理，含防潮垫与踢脚线。', 1, 50, 8);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '圣象地板（西安旗舰店）'), (SELECT id FROM category WHERE name = '板材'), '实木复合地板', '圣象', '江苏丹阳', '1210*165*15mm', '地板', 328.00, '平方米', '/demo/brand/sx-2.jpg', '实木复合地板，脚感舒适，适合卧室与客厅。', 1, 34, 5);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '雷士照明（西安旗舰店）'), (SELECT id FROM category WHERE name = '灯具'), 'LED 吸顶灯', '雷士', '广东惠州', '48W', '吸顶灯', 299.00, '个', '/demo/brand/ls-1.jpg', '48W 吸顶灯，三色变光，适用于卧室与小客厅。', 1, 46, 7);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '雷士照明（西安旗舰店）'), (SELECT id FROM category WHERE name = '灯具'), '磁吸轨道灯套装', '雷士', '广东惠州', '10 头', '轨道灯', 699.00, '套', '/demo/brand/ls-2.jpg', '磁吸轨道灯套装，含 10 头灯具与轨道，可调角度。', 1, 33, 5);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '欧普照明（西安旗舰店）'), (SELECT id FROM category WHERE name = '灯具'), 'LED 平板灯', '欧普', '广东中山', '600*600 48W', '平板灯', 129.00, '个', '/demo/brand/oupu-1.jpg', '600×600 平板灯，48W 高亮，办公吊顶常用。', 1, 40, 6);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '欧普照明（西安旗舰店）'), (SELECT id FROM category WHERE name = '灯具'), '客厅吸顶灯', '欧普', '广东中山', '96W', '吸顶灯', 599.00, '个', '/demo/brand/oupu-2.jpg', '96W 客厅吸顶灯，支持遥控调光调色。', 1, 30, 4);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '立邦漆（西安旗舰店）'), (SELECT id FROM category WHERE name = '油漆涂料'), '净味乳胶漆', '立邦', '上海', '18L', '乳胶漆', 680.00, '桶', '/demo/brand/lb-1.jpg', '净味环保乳胶漆，18L 大桶，含调色服务。', 1, 44, 7);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '立邦漆（西安旗舰店）'), (SELECT id FROM category WHERE name = '油漆涂料'), '木器清漆', '立邦', '上海', '5L', '木器漆', 320.00, '桶', '/demo/brand/lb-2.jpg', '木器清漆，5L 装，适用于家具与门板翻新。', 1, 27, 4);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '三棵树涂料（西安旗舰店）'), (SELECT id FROM category WHERE name = '油漆涂料'), '鲜呼吸乳胶漆', '三棵树', '福建莆田', '18L', '乳胶漆', 720.00, '桶', '/demo/brand/sks-1.jpg', '鲜呼吸系列乳胶漆，低气味，18L 装。', 1, 36, 5);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '三棵树涂料（西安旗舰店）'), (SELECT id FROM category WHERE name = '油漆涂料'), '艺术漆', '三棵树', '福建莆田', '5L', '艺术漆', 468.00, '桶', '/demo/brand/sks-2.jpg', '艺术漆，5L 装，可做肌理墙面，含施工指导。', 1, 22, 3);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '德高防水（西安旗舰店）'), (SELECT id FROM category WHERE name = '防水'), 'K11 防水涂料', '德高', '广东广州', '18kg', '防水涂料', 328.00, '桶', '/demo/brand/dg-1.jpg', 'K11 柔韧型防水涂料，18kg，适用于厨卫防水。', 1, 48, 8);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '德高防水（西安旗舰店）'), (SELECT id FROM category WHERE name = '防水'), '瓷砖胶', '德高', '广东广州', '20kg', '瓷砖胶', 58.00, '袋', '/demo/brand/dg-2.jpg', '瓷砖胶 20kg，粘结力强，适用于墙地砖铺贴。', 1, 32, 5);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '东方雨虹防水（西安旗舰店）'), (SELECT id FROM category WHERE name = '防水'), '好仕涂防水涂料', '东方雨虹', '北京', '18kg', '防水涂料', 358.00, '桶', '/demo/brand/dfyh-1.jpg', '好仕涂防水涂料，18kg，家装厨卫与阳台通用。', 1, 35, 5);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '东方雨虹防水（西安旗舰店）'), (SELECT id FROM category WHERE name = '防水'), '美缝剂', '东方雨虹', '北京', '400ml', '美缝剂', 68.00, '支', '/demo/brand/dfyh-2.jpg', '美缝剂 400ml，防水防霉，颜色可选。', 1, 21, 3);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '伟星管业（西安旗舰店）'), (SELECT id FROM category WHERE name = '管道'), 'PPR 给水管', '伟星', '浙江临海', 'DN25', '水管', 13.80, '米', '/demo/brand/wx-1.jpg', 'PPR 给水管 DN25，冷热水通用，厂家质保 50 年。', 1, 51, 8);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '伟星管业（西安旗舰店）'), (SELECT id FROM category WHERE name = '管道'), 'PVC 排水管', '伟星', '浙江临海', 'DN50', '水管', 18.00, '米', '/demo/brand/wx-2.jpg', 'PVC 排水管 DN50，楼层排水专用，含胶圈配件。', 1, 29, 4);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '联塑管业（西安旗舰店）'), (SELECT id FROM category WHERE name = '管道'), 'PPR 给水管', '联塑', '广东顺德', 'DN25', '水管', 11.50, '米', '/demo/brand/liansu-1.jpg', 'PPR 给水管 DN25，整根 4 米，工程家装通用。', 1, 43, 7);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '联塑管业（西安旗舰店）'), (SELECT id FROM category WHERE name = '管道'), 'PE 给水管', '联塑', '广东顺德', 'DN110', '水管', 86.00, '米', '/demo/brand/liansu-2.jpg', 'PE 给水管 DN110，市政与庭院给水常用，整根 6 米。', 1, 26, 4);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '日丰管业（西安旗舰店）'), (SELECT id FROM category WHERE name = '管道'), 'PPR 给水管', '日丰', '广东佛山', 'DN25', '水管', 12.80, '米', '/demo/brand/rf-1.jpg', 'PPR 给水管 DN25，耐压耐温，含管件配套。', 1, 38, 6);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '日丰管业（西安旗舰店）'), (SELECT id FROM category WHERE name = '管道'), '铝塑复合管', '日丰', '广东佛山', 'DN20', '水管', 9.80, '米', '/demo/brand/rf-2.jpg', '铝塑复合管 DN20，易弯曲，适用于暗埋给水。', 1, 24, 3);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '顾家家居（西安旗舰店）'), NULL, '真皮功能沙发', '顾家', '浙江杭州', '三人位', '沙发', 6999.00, '件', '/demo/brand/gj-1.jpg', '真皮功能沙发，三人位，支持电动调节与到店试坐。', 1, 57, 9);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '顾家家居（西安旗舰店）'), NULL, '客厅沙发组合', '顾家', '浙江杭州', '三件套', '沙发', 8999.00, '套', '/demo/brand/gj-2.jpg', '客厅沙发组合三件套，现代简约风格，可搭配茶几。', 1, 41, 6);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '芝华仕头等舱沙发（西安旗舰店）'), NULL, '头等舱功能沙发', '芝华仕', '广东惠州', '单人位', '沙发', 5999.00, '件', '/demo/brand/zhs-1.jpg', '头等舱功能沙发，单人位，电动躺平，支持到店体验。', 1, 48, 8);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '芝华仕头等舱沙发（西安旗舰店）'), NULL, '乳胶床垫', '芝华仕', '广东惠州', '1.8*2.0m', '床垫', 3999.00, '张', '/demo/brand/zhs-2.jpg', '乳胶床垫 1.8×2.0m，独立袋装弹簧，软硬适中。', 1, 33, 5);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '慕思寝具（西安旗舰店）'), NULL, '乳胶床垫', '慕思', '广东东莞', '1.8*2.0m', '床垫', 5999.00, '张', '/demo/brand/ms-1.jpg', '乳胶床垫 1.8×2.0m，分区支撑，含送货入户。', 1, 46, 7);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '慕思寝具（西安旗舰店）'), NULL, '软床套装', '慕思', '广东东莞', '1.8m 床架', '软床', 7999.00, '套', '/demo/brand/ms-2.jpg', '软床套装，1.8m 床架含床头柜，可搭配床垫。', 1, 35, 5);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '方太厨电（西安旗舰店）'), NULL, '欧式油烟机', '方太', '浙江宁波', '26m³/min', '油烟机', 4599.00, '台', '/demo/brand/ft-1.jpg', '欧式油烟机，26m³/min 大吸力，支持上门安装。', 1, 53, 8);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '方太厨电（西安旗舰店）'), NULL, '嵌入式洗碗机', '方太', '浙江宁波', '13 套', '洗碗机', 6299.00, '台', '/demo/brand/ft-2.jpg', '嵌入式洗碗机 13 套，含安装与调试服务。', 1, 31, 4);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '老板电器（西安旗舰店）'), NULL, '侧吸油烟机', '老板', '浙江杭州', '24m³/min', '油烟机', 3899.00, '台', '/demo/brand/laoban-1.jpg', '侧吸油烟机，24m³/min 吸力，挥手智控。', 1, 44, 7);

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
VALUES ((SELECT id FROM merchant WHERE name = '老板电器（西安旗舰店）'), NULL, '燃气灶', '老板', '浙江杭州', '5.0kW', '燃气灶', 2199.00, '台', '/demo/brand/laoban-2.jpg', '燃气灶 5.0kW 大火力，一级能效，含安装。', 1, 30, 4);
