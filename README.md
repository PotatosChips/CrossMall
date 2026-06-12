# CrossMall

CrossMall 是一个**跨境电商平台**的后端服务，提供 REST API，支持买家购物、卖家经营与管理员后台等完整业务流程。

前端项目请见：[CrossMall-Vue](https://github.com/PotatosChips/CrossMall-Vue)

## 技术栈

- Java 17 · Spring Boot 4 · MyBatis · MySQL 8
- Spring Security（Session 鉴权）
- Redis（Session 外置 + 分类/地区缓存）

## 主要功能

- 用户注册 / 登录（买家、卖家、管理员）
- 商品浏览、分类筛选、多地区店铺
- 购物车、下单、模拟支付、物流跟踪
- 商品评价、售后处理
- 卖家商品管理、订单发货
- 管理员分类与用户管理

## 快速启动

```bash
# 需先准备 MySQL、Redis，并导入 sql.sql、data.sql
./mvnw spring-boot:run
