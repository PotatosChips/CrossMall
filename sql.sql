-- 建库
CREATE DATABASE IF NOT EXISTS `cross-mall` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `cross-mall`;

-- User 用户（User 是 MySQL 保留字，需反引号）
CREATE TABLE `user` (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    username    VARCHAR(50)  NOT NULL UNIQUE COMMENT '用户名',
    password    VARCHAR(100) NOT NULL COMMENT '密码（加密后）',
    nickname    VARCHAR(50)  COMMENT '昵称',
    phone       VARCHAR(20)  COMMENT '手机号',
    role        TINYINT DEFAULT 0 COMMENT '0买家 1卖家 2管理员',
    status      TINYINT DEFAULT 1 COMMENT '0禁用 1正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '用户表';

-- Merchant 商家
CREATE TABLE merchant (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    merchant_name VARCHAR(100) NOT NULL COMMENT '商家名称',
    region      VARCHAR(50)  NOT NULL COMMENT '所在地区（跨境）',
    description VARCHAR(500) COMMENT '商家简介',
    user_id     BIGINT COMMENT '关联卖家账号',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '商家表';

-- category 分类
CREATE TABLE category (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_name        VARCHAR(50) NOT NULL COMMENT '分类名：服装/鞋类/配件/家居',
    sort        INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '商品分类表';

-- product 商品
CREATE TABLE product (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_name VARCHAR(200) NOT NULL COMMENT '商品名称',
    category_id BIGINT NOT NULL,
    merchant_id BIGINT NOT NULL,
    price       DECIMAL(10,2) NOT NULL,
    stock       INT DEFAULT 0 COMMENT '库存',
    image       VARCHAR(500) COMMENT '主图URL',
    description TEXT COMMENT '商品描述',
    status      TINYINT DEFAULT 1 COMMENT '0下架 1上架',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category_id),
    INDEX idx_merchant (merchant_id)
) COMMENT '商品表';

-- cart 购物车
CREATE TABLE cart (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id     BIGINT NOT NULL,
    product_id  BIGINT NOT NULL,
    quantity    INT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_product (user_id, product_id)
) COMMENT '购物车表';

-- order 订单
CREATE TABLE `order` (
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no       VARCHAR(32) NOT NULL UNIQUE COMMENT '订单号',
    user_id        BIGINT NOT NULL,
    total_amount   DECIMAL(10,2) NOT NULL,
    pay_type       TINYINT COMMENT '1支付宝 2微信 3信用卡',
    logistics_type TINYINT COMMENT '1标准 2加急',
    status         TINYINT DEFAULT 0 COMMENT '0待支付 1已支付 2已发货 3已完成 4已取消',
    address        VARCHAR(300) COMMENT '收货地址',
    receiver_name  VARCHAR(50) COMMENT '收货人',
    receiver_phone VARCHAR(20) COMMENT '收货电话',
    create_time    DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user (user_id)
) COMMENT '订单表';

-- order_item 订单明细
CREATE TABLE order_item (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id     BIGINT NOT NULL,
    product_id   BIGINT NOT NULL,
    product_name VARCHAR(200) NOT NULL COMMENT '冗余，防商品改名',
    price        DECIMAL(10,2) NOT NULL COMMENT '下单时单价',
    quantity     INT NOT NULL,
    INDEX idx_order (order_id)
) COMMENT '订单明细表';

-- payment 支付记录（模拟第三方支付）
CREATE TABLE payment (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id    BIGINT NOT NULL UNIQUE COMMENT '一个订单对应一条支付记录',
    pay_no      VARCHAR(64) COMMENT '第三方支付流水号（模拟）',
    pay_type    TINYINT NOT NULL COMMENT '1支付宝 2微信 3信用卡',
    amount      DECIMAL(10,2) NOT NULL,
    status      TINYINT DEFAULT 0 COMMENT '0待支付 1支付成功 2支付失败',
    pay_time    DATETIME COMMENT '支付完成时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '支付记录表';

-- logistics 物流信息
CREATE TABLE logistics (
    id                BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id          BIGINT NOT NULL UNIQUE COMMENT '一个订单对应一条物流记录',
    company           VARCHAR(50) COMMENT '物流公司',
    tracking_no       VARCHAR(64) COMMENT '运单号',
    status            TINYINT DEFAULT 0 COMMENT '0待发货 1运输中 2已签收',
    estimated_arrival DATETIME COMMENT '预计到达时间',
    create_time       DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time       DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '物流信息表';

-- logistics_track 物流轨迹（可选，用于展示物流节点）
CREATE TABLE logistics_track (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    logistics_id BIGINT NOT NULL,
    content      VARCHAR(200) NOT NULL COMMENT '轨迹描述',
    track_time   DATETIME NOT NULL COMMENT '节点时间',
    create_time  DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_logistics (logistics_id)
) COMMENT '物流轨迹表';

-- review 商品评价
CREATE TABLE review (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id     BIGINT NOT NULL,
    product_id  BIGINT NOT NULL,
    order_id    BIGINT NOT NULL COMMENT '关联订单，防止未购买评价',
    score       TINYINT NOT NULL COMMENT '评分 1-5',
    content     VARCHAR(500) COMMENT '评价内容',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_product (product_id)
) COMMENT '商品评价表';

-- after_sale 售后申请
CREATE TABLE after_sale (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id    BIGINT NOT NULL,
    user_id     BIGINT NOT NULL,
    type        TINYINT NOT NULL COMMENT '1退货 2换货 3投诉',
    reason      VARCHAR(500) COMMENT '申请原因',
    status      TINYINT DEFAULT 0 COMMENT '0待处理 1处理中 2已完成 3已拒绝',
    reply       VARCHAR(500) COMMENT '商家/平台回复',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_order (order_id),
    INDEX idx_user (user_id)
) COMMENT '售后申请表';
