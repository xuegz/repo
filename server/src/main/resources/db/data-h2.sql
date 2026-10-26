-- H2 演示数据（账号由应用首次启动时自动创建，密码统一 BCrypt 加密）

DELETE FROM view_record;
DELETE FROM forward_record;
DELETE FROM product;
DELETE FROM merchant;
DELETE FROM category;

INSERT INTO category (id, name, parent_id, sort, status) VALUES
 (1,'卫浴',0,1,1),
 (2,'防水',0,2,1),
 (3,'油漆涂料',0,3,1),
 (4,'管道',0,4,1),
 (5,'瓷砖',0,5,1),
 (6,'板材',0,6,1),
 (7,'灯具',0,7,1),
 (8,'五金',0,8,1);

INSERT INTO merchant (id, name, contact_name, phone, market, province, city, address, category_id, status, views, forwards) VALUES
 (1,'示例建材商行','张老板','13800138000','大明宫建材城','陕西省','西安市','北二环大明宫建材市场 A 区 1 排 2 号',1,1,128,36),
 (2,'西部管业批发','李经理','13900139000','大明宫建材城','陕西省','西安市','北二环大明宫建材市场 B 区 3 排 8 号',4,0,0,0),
 (3,'三秦板材总汇','王老板','13700137000','西部建材城','陕西省','西安市','三桥西部建材城 C 栋 12 号',6,0,0,0);

INSERT INTO product (id, merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards) VALUES
 (1,1,1,'连体坐便器','箭牌','广东佛山','305mm 坑距','马桶',899.00,'件','','虹吸式冲水，静音节水，含安装配件',1,45,12),
 (2,1,1,'淋浴花洒套装','九牧','福建南安','三出水','花洒',568.00,'套','','主体全铜，质保 5 年，带顶喷',1,32,8),
 (3,1,5,'通体大理石瓷砖','马可波罗','广东佛山','800*800','地砖',58.00,'平方米','','整箱 4 片，适用于客厅、卧室',1,67,15),
 (4,1,4,'PPR 给水管','伟星','浙江临海','DN25','水管',12.50,'米','','热水管，抗菌内壁，耐压 25 公斤',1,23,5),
 (5,1,6,'生态免漆板','兔宝宝','浙江德清','1220*2440*18mm','板材',268.00,'张','','E0 级环保，可用于衣柜、橱柜',1,18,4),
 (6,1,7,'LED 平板灯','欧普','广东中山','600*600 48W','灯具',96.00,'个','','办公吊顶适用，含驱动电源',1,11,2),
 (7,2,4,'PE 给水管','联塑','广东顺德','DN110','水管',86.00,'米','','市政给水工程常用，整根 6 米',1,12,3),
 (8,2,4,'PVC 排水管','联塑','广东顺德','DN50','水管',32.00,'米','','楼层排水专用，含胶圈',1,7,1),
 (9,1,7,'筒灯射灯','雷士','广东惠州','7W 开孔 75mm','筒灯',26.00,'个','','三色变光，含变压器',0,0,0),
 (10,1,2,'K11 防水涂料','德高','广东广州','18kg','防水涂料',320.00,'桶','','刚柔并济，卫生间专用',3,9,2);

ALTER TABLE category ALTER COLUMN id RESTART WITH 100;
ALTER TABLE merchant ALTER COLUMN id RESTART WITH 100;
ALTER TABLE product ALTER COLUMN id RESTART WITH 100;
