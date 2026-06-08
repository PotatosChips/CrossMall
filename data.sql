-- ============================================================
--
-- 测试账号（密码均为 123456，当前登录为明文比对）
-- | 用户名      | 角色     | 说明           |
-- |-------------|----------|----------------|
-- | admin       | 管理员   | 平台管理员     |
-- | buyer1      | 买家     | 普通消费者     |
-- | buyer2      | 买家     | 普通消费者     |
-- | seller_jp   | 卖家     | 日本商家账号   |
-- | seller_us   | 卖家     | 美国商家账号   |
-- | seller_eu   | 卖家     | 欧洲商家账号   |
-- ============================================================

USE `cross-mall`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 重复导入前先清空测试数据（按依赖顺序）
TRUNCATE TABLE after_sale;
TRUNCATE TABLE review;
TRUNCATE TABLE logistics_track;
TRUNCATE TABLE logistics;
TRUNCATE TABLE payment;
TRUNCATE TABLE order_item;
TRUNCATE TABLE `order`;
TRUNCATE TABLE cart;
TRUNCATE TABLE product;
TRUNCATE TABLE category;
TRUNCATE TABLE merchant;
TRUNCATE TABLE `user`;

-- ===================== 用户 =====================
INSERT INTO `user` (id, username, password, nickname, phone, role, status, create_time) VALUES
(1, 'admin',     '123456', '系统管理员', '13800000001', 2, 1, '2025-01-01 10:00:00'),
(2, 'buyer1',    '123456', '张三',       '13800000002', 0, 1, '2025-01-02 09:30:00'),
(3, 'buyer2',    '123456', '李四',       '13800000003', 0, 1, '2025-01-03 14:20:00'),
(4, 'seller_jp', '123456', '田中太郎',   '13800000004', 1, 1, '2025-01-04 11:00:00'),
(5, 'seller_us', '123456', 'John Smith', '13800000005', 1, 1, '2025-01-05 16:45:00'),
(6, 'seller_eu', '123456', 'Hans Mueller','13800000006', 1, 1, '2025-01-06 08:15:00');

-- ===================== 商家 =====================
INSERT INTO merchant (id, merchant_name, region, description, user_id, create_time) VALUES
(1, '东京潮流服饰', '日本', '专注日系街头与通勤女装，支持直邮中国。', 4, '2025-01-04 12:00:00'),
(2, 'American Style', '美国', '美式休闲与运动品牌集合店，洛杉矶直发。', 5, '2025-01-05 17:00:00'),
(3, 'Euro Home Living', '德国', '欧洲家居与生活美学选品，品质生活首选。', 6, '2025-01-06 09:00:00');

-- ===================== 分类 =====================
INSERT INTO category (id, category_name, sort, create_time) VALUES
(1, '服装', 1, '2025-01-01 10:00:00'),
(2, '鞋类', 2, '2025-01-01 10:00:00'),
(3, '配件', 3, '2025-01-01 10:00:00'),
(4, '家居', 4, '2025-01-01 10:00:00');

-- ===================== 商品 =====================
INSERT INTO product (id, product_name, category_id, merchant_id, price, stock, image, description, status, create_time) VALUES
(1,  '日系宽松棉麻衬衫',       1, 1,  299.00, 120, 'https://picsum.photos/seed/shirt1/400/400',   '透气棉麻材质，适合春夏通勤与日常穿搭。', 1, '2025-02-01 10:00:00'),
(2,  '东京限定印花T恤',         1, 1,  159.00,  80, 'https://picsum.photos/seed/tshirt1/400/400',  '限定联名印花，纯棉面料，多色可选。',     1, '2025-02-02 11:30:00'),
(3,  '美式复古牛仔夹克',       1, 2,  459.00,  45, 'https://picsum.photos/seed/jacket1/400/400',  '经典水洗工艺，宽松版型，四季百搭。',     1, '2025-02-03 09:20:00'),
(4,  '运动休闲连帽卫衣',       1, 2,  329.00,  60, 'https://picsum.photos/seed/hoodie1/400/400',  '加绒内里，适合秋冬户外与健身。',         1, '2025-02-04 15:00:00'),
(5,  '轻便跑步鞋',             2, 2,  599.00,  35, 'https://picsum.photos/seed/shoes1/400/400',   '缓震中底，透气网面，日常跑步首选。',     1, '2025-02-05 13:40:00'),
(6,  '经典帆布鞋',             2, 1,  219.00, 100, 'https://picsum.photos/seed/shoes2/400/400',   '日系简约设计，舒适耐穿。',               1, '2025-02-06 10:10:00'),
(7,  '真皮商务腰带',           3, 2,  189.00,  50, 'https://picsum.photos/seed/belt1/400/400',    '头层牛皮，金属扣头，送礼自用皆宜。',     1, '2025-02-07 16:25:00'),
(8,  '极简双肩背包',           3, 1,  269.00,  70, 'https://picsum.photos/seed/bag1/400/400',     '大容量多隔层，适合通勤与短途旅行。',     1, '2025-02-08 14:00:00'),
(9,  '北欧风陶瓷马克杯套装',   4, 3,  128.00, 200, 'https://picsum.photos/seed/mug1/400/400',     '四件套礼盒装，微波炉可用。',             1, '2025-02-09 11:00:00'),
(10, '亚麻餐桌布',             4, 3,  198.00,  90, 'https://picsum.photos/seed/table1/400/400',   '天然亚麻，防污易洗，提升用餐氛围。',     1, '2025-02-10 09:45:00'),
(11, '智能香薰机',             4, 3,  358.00,  40, 'https://picsum.photos/seed/diffuser1/400/400','超声波雾化，静音运行，支持定时。',       1, '2025-02-11 17:30:00'),
(12, '下架测试商品',           1, 1,   99.00,   0, 'https://picsum.photos/seed/off1/400/400',     '用于测试下架/无库存场景。',              0, '2025-02-12 08:00:00');

-- ===================== 购物车 =====================
INSERT INTO cart (id, user_id, product_id, quantity, create_time) VALUES
(1, 2, 1,  2, '2025-03-01 10:00:00'),
(2, 2, 6,  1, '2025-03-01 10:05:00'),
(3, 2, 9,  1, '2025-03-02 14:30:00'),
(4, 3, 5,  1, '2025-03-03 09:15:00'),
(5, 3, 8,  2, '2025-03-03 09:20:00');

-- ===================== 订单 =====================
-- status: 0待支付 1已支付 2已发货 3已完成 4已取消
INSERT INTO `order` (id, order_no, user_id, total_amount, pay_type, logistics_type, status, address, receiver_name, receiver_phone, create_time) VALUES
(1, 'CM202503100001', 2, 777.00, NULL, NULL, 0, '四川省成都市武侯区天府大道100号', '张三', '13800000002', '2025-03-10 10:00:00'),
(2, 'CM202503100002', 2, 599.00, 1,    1,    1, '四川省成都市武侯区天府大道100号', '张三', '13800000002', '2025-03-10 11:30:00'),
(3, 'CM202503120001', 3, 807.00, 2,    2,    2, '北京市朝阳区建国路88号',          '李四', '13800000003', '2025-03-12 09:00:00'),
(4, 'CM202503150001', 2, 128.00, 3,    1,    3, '四川省成都市武侯区天府大道100号', '张三', '13800000002', '2025-03-15 16:20:00'),
(5, 'CM202503160001', 3, 459.00, NULL, NULL, 4, '北京市朝阳区建国路88号',          '李四', '13800000003', '2025-03-16 13:00:00');

-- ===================== 订单明细 =====================
INSERT INTO order_item (id, order_id, product_id, product_name, price, quantity) VALUES
(1,  1, 1, '日系宽松棉麻衬衫',     299.00, 2),
(2,  1, 9, '北欧风陶瓷马克杯套装', 128.00, 1),
(3,  2, 5, '轻便跑步鞋',           599.00, 1),
(4,  3, 5, '轻便跑步鞋',           599.00, 1),
(5,  3, 8, '极简双肩背包',         269.00, 1),
(6,  4, 9, '北欧风陶瓷马克杯套装', 128.00, 1),
(7,  5, 3, '美式复古牛仔夹克',     459.00, 1);

-- ===================== 支付记录 =====================
-- status: 0待支付 1支付成功 2支付失败
INSERT INTO payment (id, order_id, pay_no, pay_type, amount, status, pay_time, create_time) VALUES
(1, 1, NULL,                    1, 777.00, 0, NULL,                    '2025-03-10 10:00:00'),
(2, 2, 'ALI202503101130001',    1, 599.00, 1, '2025-03-10 11:35:00', '2025-03-10 11:30:00'),
(3, 3, 'WX202503120905001',     2, 807.00, 1, '2025-03-12 09:10:00', '2025-03-12 09:05:00'),
(4, 4, 'CC202503151625001',     3, 128.00, 1, '2025-03-15 16:28:00', '2025-03-15 16:25:00'),
(5, 5, NULL,                    1, 459.00, 0, NULL,                    '2025-03-16 13:00:00');

-- ===================== 物流信息 =====================
-- status: 0待发货 1运输中 2已签收
INSERT INTO logistics (id, order_id, company, tracking_no, status, estimated_arrival, create_time) VALUES
(1, 2, 'DHL',        'DHL1234567890', 0, '2025-03-20 18:00:00', '2025-03-10 11:35:00'),
(2, 3, 'FedEx',      'FX9876543210',  1, '2025-03-18 12:00:00', '2025-03-12 09:10:00'),
(3, 4, '顺丰国际',   'SF20250315001', 2, '2025-03-17 10:00:00', '2025-03-15 16:28:00');

-- ===================== 物流轨迹 =====================
INSERT INTO logistics_track (id, logistics_id, content, track_time) VALUES
(1, 2, '【洛杉矶】包裹已从商家仓库发出',           '2025-03-12 10:00:00'),
(2, 2, '【洛杉矶】已交承运商，等待国际运输',       '2025-03-12 18:30:00'),
(3, 2, '【上海】包裹到达国际转运中心，清关中',     '2025-03-15 09:20:00'),
(4, 2, '【北京】清关完成，发往目的地',             '2025-03-16 14:00:00'),
(5, 3, '【法兰克福】商家已发货',                   '2025-03-15 17:00:00'),
(6, 3, '【成都】包裹已签收，感谢使用跨境商城',     '2025-03-17 10:30:00');

-- ===================== 商品评价 =====================
INSERT INTO review (id, user_id, product_id, order_id, score, content, create_time) VALUES
(1, 2, 9, 4, 5, '马克杯质感很好，包装精美，送礼合适。', '2025-03-18 20:00:00'),
(2, 2, 9, 4, 4, '颜色比图片略浅，但整体满意。',         '2025-03-19 09:30:00');

-- ===================== 售后申请 =====================
-- type: 1退货 2换货 3投诉  status: 0待处理 1处理中 2已完成 3已拒绝
INSERT INTO after_sale (id, order_id, user_id, type, reason, status, reply, create_time) VALUES
(1, 3, 3, 1, '跑步鞋尺码偏小，希望退货退款。',       1, '已收到申请，请保持商品完好并上传照片。', '2025-03-20 11:00:00'),
(2, 4, 2, 3, '物流显示签收但未收到，请协助查询。',   2, '已联系快递核实，包裹由物业代收。',       '2025-03-21 15:40:00');

SET FOREIGN_KEY_CHECKS = 1;

-- 重置自增主键，避免后续插入 ID 冲突
ALTER TABLE `user` AUTO_INCREMENT = 100;
ALTER TABLE merchant AUTO_INCREMENT = 100;
ALTER TABLE category AUTO_INCREMENT = 100;
ALTER TABLE product AUTO_INCREMENT = 100;
ALTER TABLE cart AUTO_INCREMENT = 100;
ALTER TABLE `order` AUTO_INCREMENT = 100;
ALTER TABLE order_item AUTO_INCREMENT = 100;
ALTER TABLE payment AUTO_INCREMENT = 100;
ALTER TABLE logistics AUTO_INCREMENT = 100;
ALTER TABLE logistics_track AUTO_INCREMENT = 100;
ALTER TABLE review AUTO_INCREMENT = 100;
ALTER TABLE after_sale AUTO_INCREMENT = 100;
