-- 已有库升级：merchant.name / product.name → merchant_name / product_name
-- 新库请直接执行 sql.sql，无需本脚本
USE `cross-mall`;

ALTER TABLE merchant CHANGE COLUMN name merchant_name VARCHAR(100) NOT NULL COMMENT '商家名称';
ALTER TABLE product CHANGE COLUMN name product_name VARCHAR(200) NOT NULL COMMENT '商品名称';
