-- 西安市场/品牌门店/商品补充种子数据
-- 市场、门店资料整理自公开资料；联系电话均为演示号码（138001381xx），上线前需替换为商户真实号码；
-- 坐标按市场地址估算，可在后台修正；图片为公开网络图片裁剪，仅作演示，商用前需替换为商户授权图片。

-- ============ 市场 ============
INSERT INTO market (name, province, city, address, latitude, longitude, logo, intro, sort, status)
SELECT '红星美凯龙西安北二环商场', '陕西省', '西安市', '未央区北辰大道688号（辛家庙立交北200米路西）', 34.323500, 108.996500, '/demo/xian/markets/hxmkl-bei2huan.jpg', '红星美凯龙第八代旗舰卖场，建筑面积约15万平方米，建材与家具同层分区，覆盖卫浴、瓷砖、门窗、定制、软装等品类。', 1, 1
WHERE NOT EXISTS (SELECT 1 FROM market WHERE name = '红星美凯龙西安北二环商场');

INSERT INTO market (name, province, city, address, latitude, longitude, logo, intro, sort, status)
SELECT '红星美凯龙全球家居至尊MALL', '陕西省', '西安市', '雁塔区西沣二路与西沣北路交叉口东南160米', 34.183200, 108.885200, '/demo/xian/markets/hxmkl-zhizun.jpg', '城南高端家居卖场，汇聚国内外一线建材家具品牌，设有进口馆、设计中心与整装体验区。', 2, 1
WHERE NOT EXISTS (SELECT 1 FROM market WHERE name = '红星美凯龙全球家居至尊MALL');

INSERT INTO market (name, province, city, address, latitude, longitude, logo, intro, sort, status)
SELECT '居然之家西安总店', '陕西省', '西安市', '未央区北二环西段明光路口', 34.304200, 108.902000, '/demo/xian/markets/juran-zongdian.jpg', '居然之家在西北开设的早期旗舰店，经营面积6万余平方米，建材、家具、家装多业态一站式。', 3, 1
WHERE NOT EXISTS (SELECT 1 FROM market WHERE name = '居然之家西安总店');

INSERT INTO market (name, province, city, address, latitude, longitude, logo, intro, sort, status)
SELECT '居然之家高新至尊店', '陕西省', '西安市', '雁塔区高新四路9号', 34.216800, 108.891800, '/demo/xian/markets/juran-gaoxin.jpg', '高新区中高端家居卖场，进口卫浴、定制家居、智能家电品牌集中，适合改善型家装采购。', 4, 1
WHERE NOT EXISTS (SELECT 1 FROM market WHERE name = '居然之家高新至尊店');

INSERT INTO market (name, province, city, address, latitude, longitude, logo, intro, sort, status)
SELECT '居然之家曲江店', '陕西省', '西安市', '雁塔区长安南路101号', 34.207200, 108.943500, '/demo/xian/markets/juran-qujiang.jpg', '曲江片区一站式家居卖场，覆盖建材主材、家具软装与全屋定制，周边家装客群集中。', 5, 1
WHERE NOT EXISTS (SELECT 1 FROM market WHERE name = '居然之家曲江店');

INSERT INTO market (name, province, city, address, latitude, longitude, logo, intro, sort, status)
SELECT '居然之家中联至尊店', '陕西省', '西安市', '未央区二环北路西段明光路口东侧', 34.301000, 108.904000, '/demo/xian/markets/juran-zhonglian.jpg', '城北中高端家居卖场，以进口品牌、智能家居和全案设计为特色。', 6, 1
WHERE NOT EXISTS (SELECT 1 FROM market WHERE name = '居然之家中联至尊店');

INSERT INTO market (name, province, city, address, latitude, longitude, logo, intro, sort, status)
SELECT '西安明珠家居商城', '陕西省', '西安市', '未央区北二环未央立交东500米', 34.305500, 108.960000, '/demo/xian/markets/mingzhu-jiaju.jpg', '城北老牌家居商城，家具、建材、灯具、软装品类齐全，批零兼营。', 7, 1
WHERE NOT EXISTS (SELECT 1 FROM market WHERE name = '西安明珠家居商城');

INSERT INTO market (name, province, city, address, latitude, longitude, logo, intro, sort, status)
SELECT '西安同泰灯具城', '陕西省', '西安市', '浐灞生态区绕城新筑口、东北三环交汇处', 34.390500, 109.055500, '/demo/xian/markets/tongtai-dengcheng.jpg', '西北大型照明灯饰采购平台，经营面积约10万平方米，设品牌工程馆、灯饰生活馆与工程亮化街区。', 8, 1
WHERE NOT EXISTS (SELECT 1 FROM market WHERE name = '西安同泰灯具城');

INSERT INTO market (name, province, city, address, latitude, longitude, logo, intro, sort, status)
SELECT '光彩灯具城', '陕西省', '西安市', '雁塔区西影路621号', 34.218000, 108.977000, '/demo/xian/markets/guangcai-dengcheng.jpg', '大雁塔周边专业灯具市场，家居照明、商业照明与电工电料门店集中。', 9, 1
WHERE NOT EXISTS (SELECT 1 FROM market WHERE name = '光彩灯具城');

INSERT INTO market (name, province, city, address, latitude, longitude, logo, intro, sort, status)
SELECT '庆安建材市场', '陕西省', '西安市', '莲湖区团结西路与汉城南路交汇处', 34.254500, 108.890500, '/demo/xian/markets/qingan-jiancai.jpg', '城西综合建材市场，管材、五金、涂料、防水等辅材批发零售集中，辐射土门及西郊片区。', 10, 1
WHERE NOT EXISTS (SELECT 1 FROM market WHERE name = '庆安建材市场');

INSERT INTO market (name, province, city, address, latitude, longitude, logo, intro, sort, status)
SELECT '大明宫建材家居·高新西沣路店', '陕西省', '西安市', '长安区郭杜街道创汇路与创业大道交汇处', 34.158800, 108.867500, '/demo/xian/markets/dmg-gaoxin.jpg', '高新三期家居卖场，覆盖瓷砖、卫浴、定制、家具等品类，服务长安及高新南片区。', 11, 1
WHERE NOT EXISTS (SELECT 1 FROM market WHERE name = '大明宫建材家居·高新西沣路店');

-- ============ 已有市场补充门面图（不覆盖后台已维护图片） ============
UPDATE market SET logo = '/demo/xian/markets/ba3huan-dmg.jpg' WHERE name = '北三环大明宫建材家居批发市场' AND (logo IS NULL OR logo = '');
UPDATE market SET logo = '/demo/xian/markets/dmg-bei2huan.jpg' WHERE name = '大明宫建材家居城·北二环店' AND (logo IS NULL OR logo = '');
UPDATE market SET logo = '/demo/xian/markets/dmg-zuanshi.jpg' WHERE name = '大明宫建材家居·钻石店' AND (logo IS NULL OR logo = '');
UPDATE market SET logo = '/demo/xian/markets/baihua.jpg' WHERE name = '百花门业建材家居城' AND (logo IS NULL OR logo = '');
UPDATE market SET logo = '/demo/xian/markets/sansen.jpg' WHERE name = '三森家居建材城' AND (logo IS NULL OR logo = '');
UPDATE market SET logo = '/demo/xian/markets/hxmkl-longshou.jpg' WHERE name = '红星美凯龙西安龙首商场' AND (logo IS NULL OR logo = '');
UPDATE market SET logo = '/demo/xian/markets/hxmkl-taibai.jpg' WHERE name = '红星美凯龙太白商场' AND (logo IS NULL OR logo = '');
UPDATE market SET logo = '/demo/xian/markets/juran-hangtian.jpg' WHERE name = '居然之家西安航天城店' AND (logo IS NULL OR logo = '');
UPDATE market SET logo = '/demo/xian/markets/huanancheng.jpg' WHERE name = '西安华南城五金机电交易中心' AND (logo IS NULL OR logo = '');
UPDATE market SET logo = '/demo/xian/markets/yuandian.jpg' WHERE name = '中国原点新城' AND (logo IS NULL OR logo = '');
UPDATE market SET logo = '/demo/xian/markets/xibu-jiancai.jpg' WHERE name = '西部建材城' AND (logo IS NULL OR logo = '');
UPDATE market SET logo = '/demo/xian/markets/dmg-sanqiao.jpg' WHERE name = '大明宫建材家居·三桥店' AND (logo IS NULL OR logo = '');
UPDATE market SET logo = '/demo/xian/markets/besite.jpg' WHERE name = '贝斯特·西北五金机电市场' AND (logo IS NULL OR logo = '');
UPDATE market SET logo = '/demo/xian/markets/jinxiu.jpg' WHERE name = '锦绣西北五金机电物流市场' AND (logo IS NULL OR logo = '');
UPDATE market SET logo = '/demo/xian/markets/taihualu.jpg' WHERE name = '太华路建材市场' AND (logo IS NULL OR logo = '');
UPDATE market SET logo = '/demo/xian/markets/yuexing-taiao.jpg' WHERE name = '月星家居太奥店' AND (logo IS NULL OR logo = '');
UPDATE market SET logo = '/demo/xian/markets/heji-wanjia.jpg' WHERE name = '和记万佳建材家居城' AND (logo IS NULL OR logo = '');
UPDATE market SET logo = '/demo/xian/markets/dmg-dongsanhuan.jpg' WHERE name = '大明宫建材家居·东三环店' AND (logo IS NULL OR logo = '');
UPDATE market SET logo = '/demo/xian/markets/dmg-hanguang.jpg' WHERE name = '大明宫建材家居·含光路店' AND (logo IS NULL OR logo = '');

-- ============ 已有门店补充门面图（不覆盖后台已维护图片） ============
UPDATE merchant SET logo = '/demo/xian/merchants/mkbl.jpg' WHERE name = '马可波罗瓷砖（西安旗舰店）' AND (logo IS NULL OR logo = '');
UPDATE merchant SET logo = '/demo/xian/merchants/dongpeng.jpg' WHERE name = '东鹏瓷砖（西安旗舰店）' AND (logo IS NULL OR logo = '');
UPDATE merchant SET logo = '/demo/xian/merchants/jianpai.jpg' WHERE name = '箭牌卫浴（西安旗舰店）' AND (logo IS NULL OR logo = '');
UPDATE merchant SET logo = '/demo/xian/merchants/jiumu.jpg' WHERE name = '九牧卫浴（西安旗舰店）' AND (logo IS NULL OR logo = '');
UPDATE merchant SET logo = '/demo/xian/merchants/oupoai.jpg' WHERE name = '欧派全屋定制（西安旗舰店）' AND (logo IS NULL OR logo = '');
UPDATE merchant SET logo = '/demo/xian/merchants/suofeiya.jpg' WHERE name = '索菲亚全屋定制（西安旗舰店）' AND (logo IS NULL OR logo = '');
UPDATE merchant SET logo = '/demo/xian/merchants/guanzhu.jpg' WHERE name = '冠珠陶瓷（西安旗舰店）' AND (logo IS NULL OR logo = '');
UPDATE merchant SET logo = '/demo/xian/merchants/hengjie.jpg' WHERE name = '恒洁卫浴（西安旗舰店）' AND (logo IS NULL OR logo = '');
UPDATE merchant SET logo = '/demo/xian/merchants/tubao.jpg' WHERE name = '兔宝宝健康板材（北三环大明宫店）' AND (logo IS NULL OR logo = '');
UPDATE merchant SET logo = '/demo/xian/merchants/gujia.jpg' WHERE name = '顾家家居（西安旗舰店）' AND (logo IS NULL OR logo = '');
UPDATE merchant SET logo = '/demo/xian/merchants/zhihuashi.jpg' WHERE name = '芝华仕头等舱沙发（西安旗舰店）' AND (logo IS NULL OR logo = '');
UPDATE merchant SET logo = '/demo/xian/merchants/qiannianzhou.jpg' WHERE name = '千年舟板材（北三环大明宫店）' AND (logo IS NULL OR logo = '');
UPDATE merchant SET logo = '/demo/xian/merchants/musi.jpg' WHERE name = '慕思寝具（西安旗舰店）' AND (logo IS NULL OR logo = '');
UPDATE merchant SET logo = '/demo/xian/merchants/shengxiang.jpg' WHERE name = '圣象地板（西安旗舰店）' AND (logo IS NULL OR logo = '');
UPDATE merchant SET logo = '/demo/xian/merchants/leishi.jpg' WHERE name = '雷士照明（西安旗舰店）' AND (logo IS NULL OR logo = '');
UPDATE merchant SET logo = '/demo/xian/merchants/fangtai.jpg' WHERE name = '方太厨电（西安旗舰店）' AND (logo IS NULL OR logo = '');
UPDATE merchant SET logo = '/demo/xian/merchants/oupu.jpg' WHERE name = '欧普照明（西安旗舰店）' AND (logo IS NULL OR logo = '');
UPDATE merchant SET logo = '/demo/xian/merchants/laoban.jpg' WHERE name = '老板电器（西安旗舰店）' AND (logo IS NULL OR logo = '');
UPDATE merchant SET logo = '/demo/xian/merchants/libang.jpg' WHERE name = '立邦漆（西安旗舰店）' AND (logo IS NULL OR logo = '');
UPDATE merchant SET logo = '/demo/xian/merchants/weixing.jpg' WHERE name = '伟星管业（西安旗舰店）' AND (logo IS NULL OR logo = '');
UPDATE merchant SET logo = '/demo/xian/merchants/santrees.jpg' WHERE name = '三棵树涂料（西安旗舰店）' AND (logo IS NULL OR logo = '');
UPDATE merchant SET logo = '/demo/xian/merchants/degao.jpg' WHERE name = '德高防水（西安旗舰店）' AND (logo IS NULL OR logo = '');
UPDATE merchant SET logo = '/demo/xian/merchants/yuhong.jpg' WHERE name = '东方雨虹防水（西安旗舰店）' AND (logo IS NULL OR logo = '');
UPDATE merchant SET logo = '/demo/xian/merchants/liansu.jpg' WHERE name = '联塑管业（西安旗舰店）' AND (logo IS NULL OR logo = '');
UPDATE merchant SET logo = '/demo/xian/merchants/rifeng.jpg' WHERE name = '日丰管业（西安旗舰店）' AND (logo IS NULL OR logo = '');

-- ============ 品牌门店 ============
INSERT INTO merchant (name, contact_name, phone, market, market_id, province, city, address, logo, intro, category_id, status, views, forwards, latitude, longitude)
SELECT '皇派门窗（北三环大明宫店）', '门店经理', '13800138101', '北三环大明宫建材家居批发市场', (SELECT id FROM market WHERE name = '北三环大明宫建材家居批发市场'), '陕西省', '西安市', '未央区太华北路延伸线与北三环交汇处（昭远门路）', '/demo/xian/merchants/huangpai.jpg', '皇派门窗西安门店，主营断桥铝系统窗、阳光房与全屋门窗定制，提供上门测量与安装。', (SELECT id FROM category WHERE name = '门窗'), 1, 0, 0, 34.368400, 108.973900
WHERE NOT EXISTS (SELECT 1 FROM merchant WHERE name = '皇派门窗（北三环大明宫店）');

INSERT INTO merchant (name, contact_name, phone, market, market_id, province, city, address, logo, intro, category_id, status, views, forwards, latitude, longitude)
SELECT '梦天木门（北三环大明宫店）', '门店经理', '13800138102', '北三环大明宫建材家居批发市场', (SELECT id FROM market WHERE name = '北三环大明宫建材家居批发市场'), '陕西省', '西安市', '未央区太华北路延伸线与北三环交汇处（昭远门路）', '/demo/xian/merchants/mengtian.jpg', '梦天木门西安门店，经营实木复合门、全屋木作与护墙板，支持整木定制。', (SELECT id FROM category WHERE name = '门窗'), 1, 0, 0, 34.368400, 108.973900
WHERE NOT EXISTS (SELECT 1 FROM merchant WHERE name = '梦天木门（北三环大明宫店）');

INSERT INTO merchant (name, contact_name, phone, market, market_id, province, city, address, logo, intro, category_id, status, views, forwards, latitude, longitude)
SELECT 'TATA木门（百花门业店）', '门店经理', '13800138103', '百花门业建材家居城', (SELECT id FROM market WHERE name = '百花门业建材家居城'), '陕西省', '西安市', '未央区太华北路789号（凤城五路南）', '/demo/xian/merchants/tata.jpg', 'TATA木门西安门店，以静音门、免漆门和全屋木门配套为主，支持到店选样。', (SELECT id FROM category WHERE name = '门窗'), 1, 0, 0, 34.331400, 108.971400
WHERE NOT EXISTS (SELECT 1 FROM merchant WHERE name = 'TATA木门（百花门业店）');

INSERT INTO merchant (name, contact_name, phone, market, market_id, province, city, address, logo, intro, category_id, status, views, forwards, latitude, longitude)
SELECT '王力安全门（百花门业店）', '门店经理', '13800138104', '百花门业建材家居城', (SELECT id FROM market WHERE name = '百花门业建材家居城'), '陕西省', '西安市', '未央区太华北路789号（凤城五路南）', '/demo/xian/merchants/wangli.jpg', '王力安全门西安门店，主营甲级防盗门、智能门锁与入户门配套。', (SELECT id FROM category WHERE name = '门窗'), 1, 0, 0, 34.331400, 108.971400
WHERE NOT EXISTS (SELECT 1 FROM merchant WHERE name = '王力安全门（百花门业店）');

INSERT INTO merchant (name, contact_name, phone, market, market_id, province, city, address, logo, intro, category_id, status, views, forwards, latitude, longitude)
SELECT '大自然地板（北三环大明宫店）', '门店经理', '13800138105', '北三环大明宫建材家居批发市场', (SELECT id FROM market WHERE name = '北三环大明宫建材家居批发市场'), '陕西省', '西安市', '未央区太华北路延伸线与北三环交汇处（昭远门路）', '/demo/xian/merchants/daziran.jpg', '大自然地板西安门店，经营实木复合、强化复合与地暖专用地板。', (SELECT id FROM category WHERE name = '地板'), 1, 0, 0, 34.368400, 108.973900
WHERE NOT EXISTS (SELECT 1 FROM merchant WHERE name = '大自然地板（北三环大明宫店）');

INSERT INTO merchant (name, contact_name, phone, market, market_id, province, city, address, logo, intro, category_id, status, views, forwards, latitude, longitude)
SELECT '德尔地板（大明宫三桥店）', '门店经理', '13800138106', '大明宫建材家居·三桥店', (SELECT id FROM market WHERE name = '大明宫建材家居·三桥店'), '陕西省', '西安市', '未央区三桥街道西部车城旁', '/demo/xian/merchants/deer.jpg', '德尔地板西安门店，主打无醛添加地板与地热地板，提供铺装服务。', (SELECT id FROM category WHERE name = '地板'), 1, 0, 0, 34.288400, 108.825400
WHERE NOT EXISTS (SELECT 1 FROM merchant WHERE name = '德尔地板（大明宫三桥店）');

INSERT INTO merchant (name, contact_name, phone, market, market_id, province, city, address, logo, intro, category_id, status, views, forwards, latitude, longitude)
SELECT '生活家地板（月星家居太奥店）', '门店经理', '13800138107', '月星家居太奥店', (SELECT id FROM market WHERE name = '月星家居太奥店'), '陕西省', '西安市', '莲湖区西二环丰禾路太奥广场', '/demo/xian/merchants/shenghuojia.jpg', '生活家地板西安门店，经营实木复合、三层实木与进口木地板。', (SELECT id FROM category WHERE name = '地板'), 1, 0, 0, 34.280400, 108.890400
WHERE NOT EXISTS (SELECT 1 FROM merchant WHERE name = '生活家地板（月星家居太奥店）');

INSERT INTO merchant (name, contact_name, phone, market, market_id, province, city, address, logo, intro, category_id, status, views, forwards, latitude, longitude)
SELECT '志邦家居（居然之家航天城店）', '门店经理', '13800138108', '居然之家西安航天城店', (SELECT id FROM market WHERE name = '居然之家西安航天城店'), '陕西省', '西安市', '长安区航天西路67号', '/demo/xian/merchants/zhibang.jpg', '志邦家居西安门店，提供整体橱柜、定制衣柜与全屋木作方案。', (SELECT id FROM category WHERE name = '全屋定制'), 1, 0, 0, 34.166400, 108.969400
WHERE NOT EXISTS (SELECT 1 FROM merchant WHERE name = '志邦家居（居然之家航天城店）');

INSERT INTO merchant (name, contact_name, phone, market, market_id, province, city, address, logo, intro, category_id, status, views, forwards, latitude, longitude)
SELECT '尚品宅配（红星美凯龙龙首商场）', '门店经理', '13800138109', '红星美凯龙西安龙首商场', (SELECT id FROM market WHERE name = '红星美凯龙西安龙首商场'), '陕西省', '西安市', '未央区未央路80号', '/demo/xian/merchants/spzp.jpg', '尚品宅配西安门店，主打全屋定制套餐、榻榻米与多功能空间设计。', (SELECT id FROM category WHERE name = '全屋定制'), 1, 0, 0, 34.299400, 108.944900
WHERE NOT EXISTS (SELECT 1 FROM merchant WHERE name = '尚品宅配（红星美凯龙龙首商场）');

INSERT INTO merchant (name, contact_name, phone, market, market_id, province, city, address, logo, intro, category_id, status, views, forwards, latitude, longitude)
SELECT '好莱客（红星美凯龙太白商场）', '门店经理', '13800138110', '红星美凯龙太白商场', (SELECT id FROM market WHERE name = '红星美凯龙太白商场'), '陕西省', '西安市', '碑林区太白北路1号', '/demo/xian/merchants/haolaike.jpg', '好莱客西安门店，经营原态板衣柜、阳台柜与全屋定制。', (SELECT id FROM category WHERE name = '全屋定制'), 1, 0, 0, 34.236900, 108.928900
WHERE NOT EXISTS (SELECT 1 FROM merchant WHERE name = '好莱客（红星美凯龙太白商场）');

INSERT INTO merchant (name, contact_name, phone, market, market_id, province, city, address, logo, intro, category_id, status, views, forwards, latitude, longitude)
SELECT '金牌厨柜（居然之家西安总店）', '门店经理', '13800138111', '居然之家西安总店', (SELECT id FROM market WHERE name = '居然之家西安总店'), '陕西省', '西安市', '未央区北二环西段明光路口', '/demo/xian/merchants/jinpai.jpg', '金牌厨柜西安门店，主营整体厨房、不锈钢橱柜与厨餐一体化设计。', (SELECT id FROM category WHERE name = '全屋定制'), 1, 0, 0, 34.304600, 108.902400
WHERE NOT EXISTS (SELECT 1 FROM merchant WHERE name = '金牌厨柜（居然之家西安总店）');

INSERT INTO merchant (name, contact_name, phone, market, market_id, province, city, address, logo, intro, category_id, status, views, forwards, latitude, longitude)
SELECT '友邦吊顶（月星家居太奥店）', '门店经理', '13800138112', '月星家居太奥店', (SELECT id FROM market WHERE name = '月星家居太奥店'), '陕西省', '西安市', '莲湖区西二环丰禾路太奥广场', '/demo/xian/merchants/youbang.jpg', '友邦吊顶西安门店，经营集成吊顶、蜂窝大板与厨卫电器。', (SELECT id FROM category WHERE name = '吊顶'), 1, 0, 0, 34.280400, 108.890400
WHERE NOT EXISTS (SELECT 1 FROM merchant WHERE name = '友邦吊顶（月星家居太奥店）');

INSERT INTO merchant (name, contact_name, phone, market, market_id, province, city, address, logo, intro, category_id, status, views, forwards, latitude, longitude)
SELECT '奥普家居（和记万佳建材家居城）', '门店经理', '13800138113', '和记万佳建材家居城', (SELECT id FROM market WHERE name = '和记万佳建材家居城'), '陕西省', '西安市', '雁塔区幸福南路666号', '/demo/xian/merchants/aopu.jpg', '奥普家居西安门店，主营风暖浴霸、集成吊顶与智能卫浴电器。', (SELECT id FROM category WHERE name = '吊顶'), 1, 0, 0, 34.228400, 108.980400
WHERE NOT EXISTS (SELECT 1 FROM merchant WHERE name = '奥普家居（和记万佳建材家居城）');

INSERT INTO merchant (name, contact_name, phone, market, market_id, province, city, address, logo, intro, category_id, status, views, forwards, latitude, longitude)
SELECT '玉宫石材奢石馆（红星美凯龙北二环店）', '门店经理', '13800138114', '红星美凯龙西安北二环商场', (SELECT id FROM market WHERE name = '红星美凯龙西安北二环商场'), '陕西省', '西安市', '未央区北辰大道688号（辛家庙立交北200米路西）', '/demo/xian/merchants/yugong.jpg', '玉宫石材奢石馆，经营天然大理石、奢石背景墙与石材台面定制。', (SELECT id FROM category WHERE name = '石材'), 1, 0, 0, 34.323900, 108.996900
WHERE NOT EXISTS (SELECT 1 FROM merchant WHERE name = '玉宫石材奢石馆（红星美凯龙北二环店）');

INSERT INTO merchant (name, contact_name, phone, market, market_id, province, city, address, logo, intro, category_id, status, views, forwards, latitude, longitude)
SELECT '环球石材（大明宫建材家居·钻石店）', '门店经理', '13800138115', '大明宫建材家居·钻石店', (SELECT id FROM market WHERE name = '大明宫建材家居·钻石店'), '陕西省', '西安市', '未央区太华北路198号', '/demo/xian/merchants/huanqiu.jpg', '环球石材西安门店，经营石英石台面、花岗岩与工程石材。', (SELECT id FROM category WHERE name = '石材'), 1, 0, 0, 34.314400, 108.975400
WHERE NOT EXISTS (SELECT 1 FROM merchant WHERE name = '环球石材（大明宫建材家居·钻石店）');

INSERT INTO merchant (name, contact_name, phone, market, market_id, province, city, address, logo, intro, category_id, status, views, forwards, latitude, longitude)
SELECT '公牛电工（北三环大明宫店）', '门店经理', '13800138116', '北三环大明宫建材家居批发市场', (SELECT id FROM market WHERE name = '北三环大明宫建材家居批发市场'), '陕西省', '西安市', '未央区太华北路延伸线与北三环交汇处（昭远门路）', '/demo/xian/merchants/gongniu.jpg', '公牛电工西安门店，经营开关插座、轨道插座与家用电工产品。', (SELECT id FROM category WHERE name = '电工'), 1, 0, 0, 34.368400, 108.973900
WHERE NOT EXISTS (SELECT 1 FROM merchant WHERE name = '公牛电工（北三环大明宫店）');

INSERT INTO merchant (name, contact_name, phone, market, market_id, province, city, address, logo, intro, category_id, status, views, forwards, latitude, longitude)
SELECT '施耐德电气（贝斯特五金机电市场）', '门店经理', '13800138117', '贝斯特·西北五金机电市场', (SELECT id FROM market WHERE name = '贝斯特·西北五金机电市场'), '陕西省', '西安市', '新城区长缨西路289号', '/demo/xian/merchants/shinaide.jpg', '施耐德电气西安门店，经营断路器、开关面板与配电箱。', (SELECT id FROM category WHERE name = '电工'), 1, 0, 0, 34.287400, 108.993400
WHERE NOT EXISTS (SELECT 1 FROM merchant WHERE name = '施耐德电气（贝斯特五金机电市场）');

INSERT INTO merchant (name, contact_name, phone, market, market_id, province, city, address, logo, intro, category_id, status, views, forwards, latitude, longitude)
SELECT '正泰电工（西安华南城）', '门店经理', '13800138118', '西安华南城五金机电交易中心', (SELECT id FROM market WHERE name = '西安华南城五金机电交易中心'), '陕西省', '西安市', '灞桥区港兴路中段（国际港务区）', '/demo/xian/merchants/zhengtai.jpg', '正泰电工西安门店，经营配电箱、电线电缆与低压电器。', (SELECT id FROM category WHERE name = '电工'), 1, 0, 0, 34.371900, 109.038400
WHERE NOT EXISTS (SELECT 1 FROM merchant WHERE name = '正泰电工（西安华南城）');

INSERT INTO merchant (name, contact_name, phone, market, market_id, province, city, address, logo, intro, category_id, status, views, forwards, latitude, longitude)
SELECT '诺贝尔瓷砖（三森家居建材城）', '门店经理', '13800138119', '三森家居建材城', (SELECT id FROM market WHERE name = '三森家居建材城'), '陕西省', '西安市', '雁塔区长安南路18号（电视塔南800米）', '/demo/xian/merchants/nuobeier.jpg', '诺贝尔瓷砖西安门店，经营通体大理石瓷砖、岩板与大规格瓷砖。', (SELECT id FROM category WHERE name = '瓷砖'), 1, 0, 0, 34.190900, 108.947400
WHERE NOT EXISTS (SELECT 1 FROM merchant WHERE name = '诺贝尔瓷砖（三森家居建材城）');

INSERT INTO merchant (name, contact_name, phone, market, market_id, province, city, address, logo, intro, category_id, status, views, forwards, latitude, longitude)
SELECT '蒙娜丽莎瓷砖（太华路建材市场）', '门店经理', '13800138120', '太华路建材市场', (SELECT id FROM market WHERE name = '太华路建材市场'), '陕西省', '西安市', '新城区太华南路（含元路以北）', '/demo/xian/merchants/mengnalisha.jpg', '蒙娜丽莎瓷砖西安门店，经营陶瓷大板、仿古砖与工程砖。', (SELECT id FROM category WHERE name = '瓷砖'), 1, 0, 0, 34.296400, 108.972400
WHERE NOT EXISTS (SELECT 1 FROM merchant WHERE name = '蒙娜丽莎瓷砖（太华路建材市场）');

INSERT INTO merchant (name, contact_name, phone, market, market_id, province, city, address, logo, intro, category_id, status, views, forwards, latitude, longitude)
SELECT '科勒卫浴（三森家居建材城）', '门店经理', '13800138121', '三森家居建材城', (SELECT id FROM market WHERE name = '三森家居建材城'), '陕西省', '西安市', '雁塔区长安南路18号（电视塔南800米）', '/demo/xian/merchants/kohler.jpg', '科勒卫浴西安门店，经营智能马桶、浴室柜、花洒与厨卫龙头。', (SELECT id FROM category WHERE name = '卫浴'), 1, 0, 0, 34.190900, 108.947400
WHERE NOT EXISTS (SELECT 1 FROM merchant WHERE name = '科勒卫浴（三森家居建材城）');

INSERT INTO merchant (name, contact_name, phone, market, market_id, province, city, address, logo, intro, category_id, status, views, forwards, latitude, longitude)
SELECT 'TOTO卫浴（红星美凯龙北二环商场）', '门店经理', '13800138122', '红星美凯龙西安北二环商场', (SELECT id FROM market WHERE name = '红星美凯龙西安北二环商场'), '陕西省', '西安市', '未央区北辰大道688号（辛家庙立交北200米路西）', '/demo/xian/merchants/toto.jpg', 'TOTO卫浴西安门店，经营智能马桶、恒温花洒与卫浴五金。', (SELECT id FROM category WHERE name = '卫浴'), 1, 0, 0, 34.323900, 108.996900
WHERE NOT EXISTS (SELECT 1 FROM merchant WHERE name = 'TOTO卫浴（红星美凯龙北二环商场）');

INSERT INTO merchant (name, contact_name, phone, market, market_id, province, city, address, logo, intro, category_id, status, views, forwards, latitude, longitude)
SELECT '摩恩卫浴（居然之家高新至尊店）', '门店经理', '13800138123', '居然之家高新至尊店', (SELECT id FROM market WHERE name = '居然之家高新至尊店'), '陕西省', '西安市', '雁塔区高新四路9号', '/demo/xian/merchants/moen.jpg', '摩恩卫浴西安门店，经营厨卫龙头、淋浴系统与卫浴配件。', (SELECT id FROM category WHERE name = '卫浴'), 1, 0, 0, 34.217200, 108.892200
WHERE NOT EXISTS (SELECT 1 FROM merchant WHERE name = '摩恩卫浴（居然之家高新至尊店）');

INSERT INTO merchant (name, contact_name, phone, market, market_id, province, city, address, logo, intro, category_id, status, views, forwards, latitude, longitude)
SELECT '多乐士（红星美凯龙太白商场）', '门店经理', '13800138124', '红星美凯龙太白商场', (SELECT id FROM market WHERE name = '红星美凯龙太白商场'), '陕西省', '西安市', '碑林区太白北路1号', '/demo/xian/merchants/duoleshi.jpg', '多乐士西安门店，经营内墙乳胶漆、儿童漆与木器漆，支持电脑调色。', (SELECT id FROM category WHERE name = '油漆涂料'), 1, 0, 0, 34.236900, 108.928900
WHERE NOT EXISTS (SELECT 1 FROM merchant WHERE name = '多乐士（红星美凯龙太白商场）');

INSERT INTO merchant (name, contact_name, phone, market, market_id, province, city, address, logo, intro, category_id, status, views, forwards, latitude, longitude)
SELECT '西卡防水（大明宫建材家居·含光路店）', '门店经理', '13800138125', '大明宫建材家居·含光路店', (SELECT id FROM market WHERE name = '大明宫建材家居·含光路店'), '陕西省', '西安市', '雁塔区含光路南段', '/demo/xian/merchants/sika.jpg', '西卡防水西安门店，经营家装防水涂料、瓷砖胶与密封材料。', (SELECT id FROM category WHERE name = '防水'), 1, 0, 0, 34.210400, 108.930400
WHERE NOT EXISTS (SELECT 1 FROM merchant WHERE name = '西卡防水（大明宫建材家居·含光路店）');

INSERT INTO merchant (name, contact_name, phone, market, market_id, province, city, address, logo, intro, category_id, status, views, forwards, latitude, longitude)
SELECT '金德管业（西部建材城）', '门店经理', '13800138126', '西部建材城', (SELECT id FROM market WHERE name = '西部建材城'), '陕西省', '西安市', '未央区三桥街道（地铁1号线三桥站西侧）', '/demo/xian/merchants/jinde.jpg', '金德管业西安门店，经营 PPR 给水管、PVC 排水管与管件。', (SELECT id FROM category WHERE name = '管道'), 1, 0, 0, 34.290900, 108.829200
WHERE NOT EXISTS (SELECT 1 FROM merchant WHERE name = '金德管业（西部建材城）');

INSERT INTO merchant (name, contact_name, phone, market, market_id, province, city, address, logo, intro, category_id, status, views, forwards, latitude, longitude)
SELECT '金牛管业（庆安建材市场）', '门店经理', '13800138127', '庆安建材市场', (SELECT id FROM market WHERE name = '庆安建材市场'), '陕西省', '西安市', '莲湖区团结西路与汉城南路交汇处', '/demo/xian/merchants/jinniu.jpg', '金牛管业西安门店，经营 PPR 热水管、PE-RT 地暖管与管件。', (SELECT id FROM category WHERE name = '管道'), 1, 0, 0, 34.254900, 108.890900
WHERE NOT EXISTS (SELECT 1 FROM merchant WHERE name = '金牛管业（庆安建材市场）');

INSERT INTO merchant (name, contact_name, phone, market, market_id, province, city, address, logo, intro, category_id, status, views, forwards, latitude, longitude)
SELECT '三雄极光（西安同泰灯具城）', '门店经理', '13800138128', '西安同泰灯具城', (SELECT id FROM market WHERE name = '西安同泰灯具城'), '陕西省', '西安市', '浐灞生态区绕城新筑口、东北三环交汇处', '/demo/xian/merchants/sanxiong.jpg', '三雄极光西安门店，经营家居照明、商业照明与智能照明。', (SELECT id FROM category WHERE name = '灯具'), 1, 0, 0, 34.390900, 109.055900
WHERE NOT EXISTS (SELECT 1 FROM merchant WHERE name = '三雄极光（西安同泰灯具城）');

INSERT INTO merchant (name, contact_name, phone, market, market_id, province, city, address, logo, intro, category_id, status, views, forwards, latitude, longitude)
SELECT '佛山照明（光彩灯具城）', '门店经理', '13800138129', '光彩灯具城', (SELECT id FROM market WHERE name = '光彩灯具城'), '陕西省', '西安市', '雁塔区西影路621号', '/demo/xian/merchants/fsl.jpg', '佛山照明西安门店，经营 LED 球泡、吸顶灯与电工产品。', (SELECT id FROM category WHERE name = '灯具'), 1, 0, 0, 34.218400, 108.977400
WHERE NOT EXISTS (SELECT 1 FROM merchant WHERE name = '佛山照明（光彩灯具城）');

INSERT INTO merchant (name, contact_name, phone, market, market_id, province, city, address, logo, intro, category_id, status, views, forwards, latitude, longitude)
SELECT '飞利浦照明（大明宫建材家居·东三环店）', '门店经理', '13800138130', '大明宫建材家居·东三环店', (SELECT id FROM market WHERE name = '大明宫建材家居·东三环店'), '陕西省', '西安市', '灞桥区东三环（半坡立交南）', '/demo/xian/merchants/philips.jpg', '飞利浦照明西安门店，经营家居照明、智能灯泡与商用照明。', (SELECT id FROM category WHERE name = '灯具'), 1, 0, 0, 34.284400, 109.045400
WHERE NOT EXISTS (SELECT 1 FROM merchant WHERE name = '飞利浦照明（大明宫建材家居·东三环店）');

INSERT INTO merchant (name, contact_name, phone, market, market_id, province, city, address, logo, intro, category_id, status, views, forwards, latitude, longitude)
SELECT '博世电动工具（北三环大明宫店）', '门店经理', '13800138131', '北三环大明宫建材家居批发市场', (SELECT id FROM market WHERE name = '北三环大明宫建材家居批发市场'), '陕西省', '西安市', '未央区太华北路延伸线与北三环交汇处（昭远门路）', '/demo/xian/merchants/bosch.jpg', '博世电动工具西安门店，经营冲击钻、角磨机与专业电动工具。', (SELECT id FROM category WHERE name = '五金'), 1, 0, 0, 34.368400, 108.973900
WHERE NOT EXISTS (SELECT 1 FROM merchant WHERE name = '博世电动工具（北三环大明宫店）');

INSERT INTO merchant (name, contact_name, phone, market, market_id, province, city, address, logo, intro, category_id, status, views, forwards, latitude, longitude)
SELECT '林氏家居（居然之家西安总店）', '门店经理', '13800138132', '居然之家西安总店', (SELECT id FROM market WHERE name = '居然之家西安总店'), '陕西省', '西安市', '未央区北二环西段明光路口', '/demo/xian/merchants/linshi.jpg', '林氏家居西安门店，经营现代简约家具、餐桌椅与客厅配套。', NULL, 1, 0, 0, 34.304600, 108.902400
WHERE NOT EXISTS (SELECT 1 FROM merchant WHERE name = '林氏家居（居然之家西安总店）');

INSERT INTO merchant (name, contact_name, phone, market, market_id, province, city, address, logo, intro, category_id, status, views, forwards, latitude, longitude)
SELECT '喜临门床垫（居然之家航天城店）', '门店经理', '13800138133', '居然之家西安航天城店', (SELECT id FROM market WHERE name = '居然之家西安航天城店'), '陕西省', '西安市', '长安区航天西路67号', '/demo/xian/merchants/xilinmen.jpg', '喜临门床垫西安门店，经营乳胶床垫、弹簧床垫与软床配套。', NULL, 1, 0, 0, 34.166400, 108.969400
WHERE NOT EXISTS (SELECT 1 FROM merchant WHERE name = '喜临门床垫（居然之家航天城店）');

-- ============ 商品 ============
INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '皇派门窗（北三环大明宫店）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '门窗'), '断桥铝系统窗', '皇派', '广东佛山', '108 系列', '系统窗', 980.00, '平方米', '/demo/xian/products/huangpai-1.jpg', '108 系列断桥铝系统窗，双层中空玻璃，隔音隔热，含测量安装。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '皇派门窗（北三环大明宫店）' ORDER BY id LIMIT 1) AND name = '断桥铝系统窗');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '皇派门窗（北三环大明宫店）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '门窗'), '阳光房定制', '皇派', '广东佛山', '按平方定制', '阳光房', 1280.00, '平方米', '/demo/xian/products/huangpai-2.jpg', '铝合金阳光房定制，可选夹胶玻璃与遮阳系统，按展开面积计价。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '皇派门窗（北三环大明宫店）' ORDER BY id LIMIT 1) AND name = '阳光房定制');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '梦天木门（北三环大明宫店）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '门窗'), '实木复合门', '梦天', '浙江江山', 'M3 系列', '木门', 2980.00, '樘', '/demo/xian/products/mengtian-1.jpg', '实木复合门，M3 系列，含门套与五金，可上门测量安装。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '梦天木门（北三环大明宫店）' ORDER BY id LIMIT 1) AND name = '实木复合门');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '梦天木门（北三环大明宫店）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '门窗'), '全屋木作套装', '梦天', '浙江江山', '按平方定制', '护墙板', 1680.00, '平方米', '/demo/xian/products/mengtian-2.jpg', '门、墙、柜一体化木作定制，按展开面积计价，含基础五金。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '梦天木门（北三环大明宫店）' ORDER BY id LIMIT 1) AND name = '全屋木作套装');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = 'TATA木门（百花门业店）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '门窗'), '静音木门', 'TATA', '北京', '45°斜口', '木门', 2680.00, '樘', '/demo/xian/products/tata-1.jpg', '45°斜口静音门，桥洞力学板填充，含门套五金与安装。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = 'TATA木门（百花门业店）' ORDER BY id LIMIT 1) AND name = '静音木门');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = 'TATA木门（百花门业店）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '门窗'), '免漆门', 'TATA', '北京', 'PET 饰面', '木门', 1580.00, '樘', '/demo/xian/products/tata-2.jpg', 'PET 饰面免漆门，耐磨易打理，适合卧室与书房。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = 'TATA木门（百花门业店）' ORDER BY id LIMIT 1) AND name = '免漆门');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '王力安全门（百花门业店）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '门窗'), '甲级防盗门', '王力', '浙江永康', '2050*960mm', '防盗门', 3680.00, '樘', '/demo/xian/products/wangli-1.jpg', '甲级防盗门，超 B 级锁芯，含上门测量与安装。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '王力安全门（百花门业店）' ORDER BY id LIMIT 1) AND name = '甲级防盗门');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '王力安全门（百花门业店）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '门窗'), '智能门锁', '王力', '浙江永康', '指纹+人脸', '智能锁', 2599.00, '套', '/demo/xian/products/wangli-2.jpg', '指纹、密码、人脸多种开锁方式，支持临时密码与防撬报警。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '王力安全门（百花门业店）' ORDER BY id LIMIT 1) AND name = '智能门锁');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '大自然地板（北三环大明宫店）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '地板'), '实木复合地板', '大自然', '广东顺德', '910*127*15mm', '地板', 328.00, '平方米', '/demo/xian/products/daziran-1.jpg', '多层实木复合地板，脚感舒适，含防潮垫与踢脚线。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '大自然地板（北三环大明宫店）' ORDER BY id LIMIT 1) AND name = '实木复合地板');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '大自然地板（北三环大明宫店）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '地板'), '强化复合地板', '大自然', '广东顺德', '1218*199*12mm', '地板', 158.00, '平方米', '/demo/xian/products/daziran-2.jpg', '强化复合地板，耐磨等级 AC4，适合客餐厅高流量区域。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '大自然地板（北三环大明宫店）' ORDER BY id LIMIT 1) AND name = '强化复合地板');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '德尔地板（大明宫三桥店）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '地板'), '无醛添加强化地板', '德尔', '江苏苏州', '1220*170*12mm', '地板', 168.00, '平方米', '/demo/xian/products/deer-1.jpg', '无醛添加基材，锁扣铺装，适合儿童房与卧室。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '德尔地板（大明宫三桥店）' ORDER BY id LIMIT 1) AND name = '无醛添加强化地板');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '德尔地板（大明宫三桥店）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '地板'), '实木地热地板', '德尔', '江苏苏州', '910*125*15mm', '地板', 428.00, '平方米', '/demo/xian/products/deer-2.jpg', '地暖专用实木复合地板，稳定性好，含专业铺装。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '德尔地板（大明宫三桥店）' ORDER BY id LIMIT 1) AND name = '实木地热地板');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '生活家地板（月星家居太奥店）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '地板'), '巴洛克实木复合地板', '生活家', '广东中山', '910*127*15mm', '地板', 368.00, '平方米', '/demo/xian/products/shenghuojia-1.jpg', '巴洛克系列实木复合地板，仿古手抓纹，适合美式与法式空间。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '生活家地板（月星家居太奥店）' ORDER BY id LIMIT 1) AND name = '巴洛克实木复合地板');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '生活家地板（月星家居太奥店）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '地板'), '三层实木地板', '生活家', '广东中山', '1900*190*15mm', '地板', 598.00, '平方米', '/demo/xian/products/shenghuojia-2.jpg', '三层实木大板，天然木皮饰面，含防潮垫与安装。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '生活家地板（月星家居太奥店）' ORDER BY id LIMIT 1) AND name = '三层实木地板');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '志邦家居（居然之家航天城店）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '全屋定制'), '整体橱柜', '志邦', '安徽合肥', '按延米计价', '橱柜', 1980.00, '延米', '/demo/xian/products/zhibang-1.jpg', '整体橱柜按延米报价，含地柜、吊柜、台面与基础五金。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '志邦家居（居然之家航天城店）' ORDER BY id LIMIT 1) AND name = '整体橱柜');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '志邦家居（居然之家航天城店）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '全屋定制'), '定制衣柜', '志邦', '安徽合肥', '按投影面积', '衣柜', 1280.00, '平方米', '/demo/xian/products/zhibang-2.jpg', '定制衣柜，多种门板与内配可选，含上门量尺设计。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '志邦家居（居然之家航天城店）' ORDER BY id LIMIT 1) AND name = '定制衣柜');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '尚品宅配（红星美凯龙龙首商场）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '全屋定制'), '全屋定制套餐', '尚品宅配', '广东广州', '22㎡', '全屋定制', 29999.00, '套', '/demo/xian/products/spzp-1.jpg', '22㎡ 全屋定制套餐，含衣柜、鞋柜、电视柜与基础五金。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '尚品宅配（红星美凯龙龙首商场）' ORDER BY id LIMIT 1) AND name = '全屋定制套餐');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '尚品宅配（红星美凯龙龙首商场）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '全屋定制'), '榻榻米定制', '尚品宅配', '广东广州', '按投影面积', '榻榻米', 1180.00, '平方米', '/demo/xian/products/spzp-2.jpg', '榻榻米+书桌+收纳一体化定制，按投影面积计价。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '尚品宅配（红星美凯龙龙首商场）' ORDER BY id LIMIT 1) AND name = '榻榻米定制');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '好莱客（红星美凯龙太白商场）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '全屋定制'), '原态板衣柜', '好莱客', '广东广州', '按投影面积', '衣柜', 1380.00, '平方米', '/demo/xian/products/haolaike-1.jpg', '原态板定制衣柜，环保等级 ENF，含基础五金与安装。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '好莱客（红星美凯龙太白商场）' ORDER BY id LIMIT 1) AND name = '原态板衣柜');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '好莱客（红星美凯龙太白商场）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '全屋定制'), '阳台柜定制', '好莱客', '广东广州', '按延米计价', '阳台柜', 1580.00, '延米', '/demo/xian/products/haolaike-2.jpg', '洗衣收纳一体阳台柜，防水防潮板材，按延米计价。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '好莱客（红星美凯龙太白商场）' ORDER BY id LIMIT 1) AND name = '阳台柜定制');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '金牌厨柜（居然之家西安总店）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '全屋定制'), '整体厨房', '金牌', '福建厦门', '按延米计价', '橱柜', 2280.00, '延米', '/demo/xian/products/jinpai-1.jpg', '整体厨房按延米报价，含地柜、吊柜、石英石台面与五金。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '金牌厨柜（居然之家西安总店）' ORDER BY id LIMIT 1) AND name = '整体厨房');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '金牌厨柜（居然之家西安总店）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '全屋定制'), '不锈钢橱柜', '金牌', '福建厦门', '按延米计价', '橱柜', 2880.00, '延米', '/demo/xian/products/jinpai-2.jpg', '不锈钢柜体，防潮耐腐蚀，适合开放式厨房。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '金牌厨柜（居然之家西安总店）' ORDER BY id LIMIT 1) AND name = '不锈钢橱柜');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '友邦吊顶（月星家居太奥店）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '吊顶'), '集成吊顶', '友邦', '浙江嘉兴', '扣板+电器', '集成吊顶', 268.00, '平方米', '/demo/xian/products/youbang-1.jpg', '集成吊顶套餐，含扣板、龙骨与照明模块，按平方计价。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '友邦吊顶（月星家居太奥店）' ORDER BY id LIMIT 1) AND name = '集成吊顶');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '友邦吊顶（月星家居太奥店）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '吊顶'), '蜂窝大板吊顶', '友邦', '浙江嘉兴', '按平方计价', '大板吊顶', 398.00, '平方米', '/demo/xian/products/youbang-2.jpg', '蜂窝大板吊顶，无缝拼接，适合客厅与开放式厨房。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '友邦吊顶（月星家居太奥店）' ORDER BY id LIMIT 1) AND name = '蜂窝大板吊顶');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '奥普家居（和记万佳建材家居城）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '吊顶'), '风暖浴霸', '奥普', '浙江杭州', '2600W', '浴霸', 1299.00, '台', '/demo/xian/products/aopu-1.jpg', '风暖浴霸，速热恒温，含照明与换气功能，包安装。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '奥普家居（和记万佳建材家居城）' ORDER BY id LIMIT 1) AND name = '风暖浴霸');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '奥普家居（和记万佳建材家居城）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '吊顶'), '集成吊顶套餐', '奥普', '浙江杭州', '8㎡', '集成吊顶', 3999.00, '套', '/demo/xian/products/aopu-2.jpg', '8㎡ 厨卫集成吊顶套餐，含扣板、龙骨、照明与风暖模块。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '奥普家居（和记万佳建材家居城）' ORDER BY id LIMIT 1) AND name = '集成吊顶套餐');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '玉宫石材奢石馆（红星美凯龙北二环店）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '石材'), '天然大理石', '玉宫', '进口', '爵士白 20mm', '大理石', 680.00, '平方米', '/demo/xian/products/yugong-1.jpg', '爵士白天然大理石，适合地面、墙面与楼梯铺贴，含磨边。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '玉宫石材奢石馆（红星美凯龙北二环店）' ORDER BY id LIMIT 1) AND name = '天然大理石');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '玉宫石材奢石馆（红星美凯龙北二环店）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '石材'), '奢石背景墙', '玉宫', '进口', '潘多拉 18mm', '奢石', 1680.00, '平方米', '/demo/xian/products/yugong-2.jpg', '潘多拉奢石背景墙，纹理独特，按实际用料面积计价。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '玉宫石材奢石馆（红星美凯龙北二环店）' ORDER BY id LIMIT 1) AND name = '奢石背景墙');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '环球石材（大明宫建材家居·钻石店）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '石材'), '石英石台面', '环球', '广东东莞', '20mm', '石英石', 580.00, '延米', '/demo/xian/products/huanqiu-1.jpg', '石英石橱柜台面，耐污耐磨，含开孔与安装。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '环球石材（大明宫建材家居·钻石店）' ORDER BY id LIMIT 1) AND name = '石英石台面');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '环球石材（大明宫建材家居·钻石店）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '石材'), '花岗岩踏石', '环球', '广东东莞', '30mm', '花岗岩', 320.00, '平方米', '/demo/xian/products/huanqiu-2.jpg', '花岗岩踏步石，防滑耐候，适合室外台阶与庭院。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '环球石材（大明宫建材家居·钻石店）' ORDER BY id LIMIT 1) AND name = '花岗岩踏石');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '公牛电工（北三环大明宫店）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '电工'), '开关插座', '公牛', '浙江慈溪', 'G12 系列', '开关插座', 18.80, '个', '/demo/xian/products/gongniu-1.jpg', 'G12 系列大板开关插座，阻燃 PC 面板，整箱起批可议价。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '公牛电工（北三环大明宫店）' ORDER BY id LIMIT 1) AND name = '开关插座');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '公牛电工（北三环大明宫店）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '电工'), '轨道插座', '公牛', '浙江慈溪', '8000W', '轨道插座', 399.00, '套', '/demo/xian/products/gongniu-2.jpg', '8000W 明装轨道插座，模块可移动，适合厨房与书房。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '公牛电工（北三环大明宫店）' ORDER BY id LIMIT 1) AND name = '轨道插座');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '施耐德电气（贝斯特五金机电市场）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '电工'), '微型断路器', '施耐德', '法国', 'C63 2P', '断路器', 68.00, '个', '/demo/xian/products/shinaide-1.jpg', 'C63 2P 微型断路器，分断能力 6kA，家用配电常用。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '施耐德电气（贝斯特五金机电市场）' ORDER BY id LIMIT 1) AND name = '微型断路器');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '施耐德电气（贝斯特五金机电市场）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '电工'), '开关面板', '施耐德', '法国', '绎尚系列', '开关面板', 32.00, '个', '/demo/xian/products/shinaide-2.jpg', '绎尚系列开关面板，无框大板设计，多色可选。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '施耐德电气（贝斯特五金机电市场）' ORDER BY id LIMIT 1) AND name = '开关面板');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '正泰电工（西安华南城）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '电工'), '配电箱', '正泰', '浙江乐清', '12 回路', '配电箱', 168.00, '个', '/demo/xian/products/zhengtai-1.jpg', '12 回路暗装配电箱，含空开导轨与零地排。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '正泰电工（西安华南城）' ORDER BY id LIMIT 1) AND name = '配电箱');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '正泰电工（西安华南城）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '电工'), '电线电缆', '正泰', '浙江乐清', 'BV2.5', '电线', 168.00, '卷', '/demo/xian/products/zhengtai-2.jpg', 'BV2.5 铜芯电线，国标足米，家装照明与插座常用。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '正泰电工（西安华南城）' ORDER BY id LIMIT 1) AND name = '电线电缆');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '诺贝尔瓷砖（三森家居建材城）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '瓷砖'), '通体大理石瓷砖', '诺贝尔', '浙江杭州', '750*1500mm', '地砖', 128.00, '平方米', '/demo/xian/products/nuobeier-1.jpg', '750×1500 通体大理石瓷砖，纹理连贯，适合客餐厅通铺。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '诺贝尔瓷砖（三森家居建材城）' ORDER BY id LIMIT 1) AND name = '通体大理石瓷砖');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '诺贝尔瓷砖（三森家居建材城）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '瓷砖'), '岩板', '诺贝尔', '浙江杭州', '1200*2400mm', '岩板', 398.00, '平方米', '/demo/xian/products/nuobeier-2.jpg', '1200×2400 岩板，可用于背景墙、台面与楼梯。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '诺贝尔瓷砖（三森家居建材城）' ORDER BY id LIMIT 1) AND name = '岩板');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '蒙娜丽莎瓷砖（太华路建材市场）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '瓷砖'), '陶瓷大板', '蒙娜丽莎', '广东佛山', '900*1800mm', '大板', 168.00, '平方米', '/demo/xian/products/mengnalisha-1.jpg', '900×1800 陶瓷大板，一石多面，适合现代简约空间。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '蒙娜丽莎瓷砖（太华路建材市场）' ORDER BY id LIMIT 1) AND name = '陶瓷大板');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '蒙娜丽莎瓷砖（太华路建材市场）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '瓷砖'), '仿古砖', '蒙娜丽莎', '广东佛山', '600*600mm', '地砖', 58.00, '平方米', '/demo/xian/products/mengnalisha-2.jpg', '600×600 仿古防滑砖，适合阳台、厨卫与公共区域。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '蒙娜丽莎瓷砖（太华路建材市场）' ORDER BY id LIMIT 1) AND name = '仿古砖');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '科勒卫浴（三森家居建材城）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '卫浴'), '智能马桶', '科勒', '美国', '305mm 坑距', '智能马桶', 6999.00, '台', '/demo/xian/products/kohler-1.jpg', '星朗系列智能马桶，即热冲洗、座圈加热，含安装。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '科勒卫浴（三森家居建材城）' ORDER BY id LIMIT 1) AND name = '智能马桶');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '科勒卫浴（三森家居建材城）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '卫浴'), '浴室柜', '科勒', '美国', '120cm', '浴室柜', 4599.00, '套', '/demo/xian/products/kohler-2.jpg', '120cm 岩板台面浴室柜，含镜柜、陶瓷盆与五金。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '科勒卫浴（三森家居建材城）' ORDER BY id LIMIT 1) AND name = '浴室柜');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = 'TOTO卫浴（红星美凯龙北二环商场）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '卫浴'), '智能马桶', 'TOTO', '日本', '305mm 坑距', '智能马桶', 5999.00, '台', '/demo/xian/products/toto-1.jpg', '即热式智能马桶，温水洗净、暖风烘干，含安装配件。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = 'TOTO卫浴（红星美凯龙北二环商场）' ORDER BY id LIMIT 1) AND name = '智能马桶');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = 'TOTO卫浴（红星美凯龙北二环商场）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '卫浴'), '恒温花洒', 'TOTO', '日本', '三出水', '花洒', 2999.00, '套', '/demo/xian/products/toto-2.jpg', '恒温淋浴花洒套装，三出水模式，防烫安全设计。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = 'TOTO卫浴（红星美凯龙北二环商场）' ORDER BY id LIMIT 1) AND name = '恒温花洒');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '摩恩卫浴（居然之家高新至尊店）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '卫浴'), '抽拉厨房龙头', '摩恩', '美国', '抽拉式', '龙头', 899.00, '个', '/demo/xian/products/moen-1.jpg', '抽拉式厨房龙头，双模式出水，含软管与安装。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '摩恩卫浴（居然之家高新至尊店）' ORDER BY id LIMIT 1) AND name = '抽拉厨房龙头');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '摩恩卫浴（居然之家高新至尊店）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '卫浴'), '淋浴花洒套装', '摩恩', '美国', '恒温三出水', '花洒', 1899.00, '套', '/demo/xian/products/moen-2.jpg', '恒温淋浴花洒套装，增压顶喷，含升降杆与手持花洒。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '摩恩卫浴（居然之家高新至尊店）' ORDER BY id LIMIT 1) AND name = '淋浴花洒套装');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '多乐士（红星美凯龙太白商场）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '油漆涂料'), '竹炭净味乳胶漆', '多乐士', '荷兰', '18L', '乳胶漆', 758.00, '桶', '/demo/xian/products/duoleshi-1.jpg', '竹炭净味内墙乳胶漆，18L 大桶，含基础调色。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '多乐士（红星美凯龙太白商场）' ORDER BY id LIMIT 1) AND name = '竹炭净味乳胶漆');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '多乐士（红星美凯龙太白商场）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '油漆涂料'), '儿童漆', '多乐士', '荷兰', '5L', '乳胶漆', 468.00, '桶', '/demo/xian/products/duoleshi-2.jpg', '儿童房专用乳胶漆，低气味易擦洗，5L 装。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '多乐士（红星美凯龙太白商场）' ORDER BY id LIMIT 1) AND name = '儿童漆');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '西卡防水（大明宫建材家居·含光路店）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '防水'), '防水涂料', '西卡', '瑞士', '20kg', '防水涂料', 458.00, '桶', '/demo/xian/products/sika-1.jpg', '聚合物水泥防水涂料，20kg，适用于厨卫与阳台防水。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '西卡防水（大明宫建材家居·含光路店）' ORDER BY id LIMIT 1) AND name = '防水涂料');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '西卡防水（大明宫建材家居·含光路店）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '防水'), '瓷砖胶', '西卡', '瑞士', '20kg', '瓷砖胶', 78.00, '袋', '/demo/xian/products/sika-2.jpg', 'C2 级瓷砖胶，20kg，适用于大板与墙地砖铺贴。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '西卡防水（大明宫建材家居·含光路店）' ORDER BY id LIMIT 1) AND name = '瓷砖胶');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '金德管业（西部建材城）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '管道'), 'PPR 给水管', '金德', '辽宁沈阳', 'DN25', '水管', 15.80, '米', '/demo/xian/products/jinde-1.jpg', 'PPR 给水管 DN25，冷热水通用，整根 4 米。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '金德管业（西部建材城）' ORDER BY id LIMIT 1) AND name = 'PPR 给水管');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '金德管业（西部建材城）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '管道'), 'PVC 排水管', '金德', '辽宁沈阳', 'DN110', '水管', 32.00, '米', '/demo/xian/products/jinde-2.jpg', 'PVC 排水管 DN110，楼层排水专用，含胶圈配件。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '金德管业（西部建材城）' ORDER BY id LIMIT 1) AND name = 'PVC 排水管');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '金牛管业（庆安建材市场）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '管道'), 'PPR 热水管', '金牛', '湖北武汉', 'DN20', '水管', 12.80, '米', '/demo/xian/products/jinniu-1.jpg', 'PPR 热水管 DN20，耐高温耐压，含管件配套。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '金牛管业（庆安建材市场）' ORDER BY id LIMIT 1) AND name = 'PPR 热水管');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '金牛管业（庆安建材市场）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '管道'), 'PE-RT 地暖管', '金牛', '湖北武汉', 'DN20', '地暖管', 8.80, '米', '/demo/xian/products/jinniu-2.jpg', 'PE-RT 地暖管 DN20，柔韧耐热，适合地暖盘管。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '金牛管业（庆安建材市场）' ORDER BY id LIMIT 1) AND name = 'PE-RT 地暖管');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '三雄极光（西安同泰灯具城）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '灯具'), 'LED 筒灯', '三雄极光', '广东广州', '7W', '筒灯', 39.00, '个', '/demo/xian/products/sanxiong-1.jpg', '7W LED 防眩筒灯，开孔 75mm，适合无主灯设计。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '三雄极光（西安同泰灯具城）' ORDER BY id LIMIT 1) AND name = 'LED 筒灯');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '三雄极光（西安同泰灯具城）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '灯具'), '磁吸轨道灯', '三雄极光', '广东广州', '10 头', '轨道灯', 899.00, '套', '/demo/xian/products/sanxiong-2.jpg', '磁吸轨道灯套装，含 10 头灯具与轨道，可调角度。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '三雄极光（西安同泰灯具城）' ORDER BY id LIMIT 1) AND name = '磁吸轨道灯');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '佛山照明（光彩灯具城）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '灯具'), 'LED 球泡灯', '佛山照明', '广东佛山', '9W', '球泡灯', 12.90, '个', '/demo/xian/products/fsl-1.jpg', '9W LED 球泡灯，E27 螺口，暖白光可选，整箱起批。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '佛山照明（光彩灯具城）' ORDER BY id LIMIT 1) AND name = 'LED 球泡灯');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '佛山照明（光彩灯具城）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '灯具'), '客厅吸顶灯', '佛山照明', '广东佛山', '48W', '吸顶灯', 259.00, '个', '/demo/xian/products/fsl-2.jpg', '48W 客厅吸顶灯，三色调光，适合 15-25㎡ 空间。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '佛山照明（光彩灯具城）' ORDER BY id LIMIT 1) AND name = '客厅吸顶灯');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '飞利浦照明（大明宫建材家居·东三环店）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '灯具'), '护眼吸顶灯', '飞利浦', '荷兰', '60W', '吸顶灯', 699.00, '个', '/demo/xian/products/philips-1.jpg', '60W 全光谱护眼吸顶灯，无频闪，支持遥控调光。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '飞利浦照明（大明宫建材家居·东三环店）' ORDER BY id LIMIT 1) AND name = '护眼吸顶灯');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '飞利浦照明（大明宫建材家居·东三环店）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '灯具'), '智能灯泡', '飞利浦', '荷兰', '9W', '智能灯', 89.00, '个', '/demo/xian/products/philips-2.jpg', '9W 智能彩光灯泡，支持手机 App 与语音控制。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '飞利浦照明（大明宫建材家居·东三环店）' ORDER BY id LIMIT 1) AND name = '智能灯泡');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '博世电动工具（北三环大明宫店）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '五金'), '冲击钻', '博世', '德国', 'GSB 13 RE', '冲击钻', 599.00, '台', '/demo/xian/products/bosch-1.jpg', 'GSB 13 RE 冲击钻，600W，含钻夹头与工具箱。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '博世电动工具（北三环大明宫店）' ORDER BY id LIMIT 1) AND name = '冲击钻');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '博世电动工具（北三环大明宫店）' ORDER BY id LIMIT 1), (SELECT id FROM category WHERE name = '五金'), '角磨机', '博世', '德国', 'GWS 700', '角磨机', 329.00, '台', '/demo/xian/products/bosch-2.jpg', 'GWS 700 角磨机，710W，含护罩与扳手。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '博世电动工具（北三环大明宫店）' ORDER BY id LIMIT 1) AND name = '角磨机');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '林氏家居（居然之家西安总店）' ORDER BY id LIMIT 1), NULL, '实木餐桌椅', '林氏家居', '广东佛山', '1.4m 一桌四椅', '餐桌椅', 2999.00, '套', '/demo/xian/products/linshi-1.jpg', '1.4m 实木餐桌配四椅，原木色，含送货安装。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '林氏家居（居然之家西安总店）' ORDER BY id LIMIT 1) AND name = '实木餐桌椅');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '林氏家居（居然之家西安总店）' ORDER BY id LIMIT 1), NULL, '布艺沙发', '林氏家居', '广东佛山', '三人位', '沙发', 3999.00, '件', '/demo/xian/products/linshi-2.jpg', '三人位布艺沙发，高回弹海绵，可拆洗面料。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '林氏家居（居然之家西安总店）' ORDER BY id LIMIT 1) AND name = '布艺沙发');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '喜临门床垫（居然之家航天城店）' ORDER BY id LIMIT 1), NULL, '乳胶床垫', '喜临门', '浙江绍兴', '1.8*2.0m', '床垫', 4999.00, '张', '/demo/xian/products/xilinmen-1.jpg', '乳胶+独立袋装弹簧床垫，软硬适中，含送货入户。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '喜临门床垫（居然之家航天城店）' ORDER BY id LIMIT 1) AND name = '乳胶床垫');

INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)
SELECT (SELECT id FROM merchant WHERE name = '喜临门床垫（居然之家航天城店）' ORDER BY id LIMIT 1), NULL, '软床套装', '喜临门', '浙江绍兴', '1.8m', '软床', 6999.00, '套', '/demo/xian/products/xilinmen-2.jpg', '1.8m 软床套装，含床架与床头柜，可搭配床垫。', 1, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '喜临门床垫（居然之家航天城店）' ORDER BY id LIMIT 1) AND name = '软床套装');
