# CrossMall Agent 速查

> 给 Cursor Agent 看的项目上下文。换对话时先读此文件，避免重复扫仓库。  
> 详细需求见同目录 `request.md`，建表见 `sql.sql`，测试数据见 `data.sql`。

## 项目组成

| 项目 | 路径 | 说明 |
|------|------|------|
| 后端 | `cross-mall/` | Spring Boot REST API |
| 前端 | `../crossmall-vue/` | Vue 3 + Vite SPA |

两个目录同级，均在 `Desktop/java/` 下。

## 技术栈

**后端（cross-mall）**

- Java 17，Spring Boot **4.0.6**
- MyBatis 4.0.1 + MySQL 8
- Spring Security（CSRF 关闭；开发期 `/api/**` 主要接口 permitAll，鉴权在 Controller Session）
- Redis 依赖已在 `pom.xml`，**尚未使用**
- Lombok、Validation

**前端（crossmall-vue）**

- Vue 3.5 + Vite 8 + Vue Router 5 + Pinia 3
- Element Plus + Axios
- Node `^20.19.0 || >=22.12.0`

## 启动方式

```bash
# 后端（默认 8080，需 MySQL 已建库并导入 sql.sql + data.sql）
./mvnw spring-boot:run          # 或 IDE 运行 CrossMallApplication

# 前端（默认 5173）
cd ../crossmall-vue
npm install
npm run dev
```

访问商城：`http://localhost:5173/products`  
开发时 API 走 Vite 代理，**不要**直接改 axios baseURL 为 8080。

## 数据库

- 库名：`cross-mall`
- 连接：`application.yml` → `localhost:3306`，用户 `root`，密码 `123456`
- 建表：`sql.sql`（12 张表）
- 测试数据：`data.sql`

**关键列名（与 POJO 驼峰对应）**

| 表 | 列名 | Java 字段 |
|----|------|-----------|
| merchant | `merchant_name` | `merchantName` |
| category | `category_name` | `categoryName` |
| product | `product_name` | `productName` |
| order | 表名 `` `order` `` | `Order`（MySQL 保留字） |

**测试账号**（密码均为 `123456`，当前为**明文**比对，未加密）：

| 用户名 | role | 角色 |
|--------|------|------|
| buyer1, buyer2 | 0 | 买家 |
| seller_jp, seller_us, seller_eu | 1 | 卖家 |
| admin | 2 | 管理员 |

**商品测试数据**：11 条上架 + 1 条下架（id=12）；`image` 为 picsum.photos 外链占位图，非本地上传。

## MVP 进度

| 步骤 | 模块 | 状态 |
|------|------|------|
| 1 | 用户注册 / 登录 | ✅ 已完成 |
| 2 | 商品展示（分类、列表、详情、多地区） | ✅ 已完成 |
| 3 | 购物车 | ✅ 前后端已完成 |
| 4 | 下单（cart → order + order_item） | ✅ 前后端已完成 |
| 5 | 买家订单列表 / 详情 / 改收货 / 删待支付单 | ✅ 前后端已完成 |
| 6 | 模拟支付 + 卖家订单 + 发货/物流 | ✅ 前后端已完成 |
| 7 | 评价 | ✅ 前后端已完成 |
| 8 | 卖家商品 CRUD | ✅ 前后端已完成 |
| 9 | 店铺列表 / 详情 | ✅ 前后端已完成 |
| 10 | 售后 | ✅ 前后端已完成 |
| 11 | 管理员后台 | ✅ 已完成（分类 CRUD + 用户封禁/解封） |

## 当前实现进度

### 已实现

**后端 — 用户**

- `POST /api/userLogin`、`POST /api/userRegister`
- `UserMapper`、`MerchantMapper`、`UserService`、`MerchantService`
- Session 登录（`session.setAttribute("user", login)`）

**后端 — 商品展示**

- `CategoryMapper/Service`：`selectAllCategories`、`selectCategoryNameById`、`selectCategoryIdByName`、`countCategories`
- `ProductMapper/Service`：`selectProductList`（分页+筛选+排序+**merchantId**）、`countProducts`、`selectProductById`、`selectStockById`、`deductStock`/`addStock`
- `ProductVO`（含 `categoryName`、`merchantName`、`region`）
- `Result` 统一响应（`success`、`massage`、`data`、`list`、`total`）
- `CategoryController`：`GET /api/categories`
- `ProductController`：`GET /api/products`、`GET /api/products/{productId}`

**后端 — 地区 / 店铺**

- `MerchantMapper.selectAllRegions`、`selectShopList`、`countShops`、`selectShopById`
- `MerchantVO`（含 `productCount`，不含 `user_id`）
- `RegionController`：`GET /api/regions`（`merchant` 表 DISTINCT region，供商城筛选与卖家注册下拉）
- `ShopController`：`GET /api/shops`、`GET /api/shops/{shopId}`

**后端 — 卖家商品**

- `SellerController`（`/api/seller`，`sellerAuth()` 校验 `role==1`）
- `ProductService`：`insertProduct`、`selectMyProductList`、`countMyProducts`、`selectMyProductById`、`updateProduct`（含上下架 `status`）
- 本店商品 SQL 带 `merchant_id` 鉴权；`insertProduct` XML 占位符用驼峰

**后端 — 购物车**

- `CartMapper/Service`：增删改查；加购时同商品累加数量（先查 `selectByUserIdAndProductId`）
- `CartController`：`POST /api/cart`、`GET /api/cart`、`PUT /api/cart/{id}`、`DELETE /api/cart/{id}`
- 鉴权：Controller 内 `session.getAttribute("user")`，`userId` 不从前端传

**后端 — 订单（含下单 + order_item + 支付 + 物流）**

- `OrderMapper/Service`：买家/卖家列表与详情、改收货、确认收货、删待支付单（级联删明细+回库存）、模拟支付、卖家发货/更新物流/追加轨迹
- **已移除** `POST /cancel`（待支付放弃改由 `DELETE` 删单）
- `OrderItemMapper`：`insertOrderItem`、`selectOrderItemByOrderId`（买家）、`selectOrderItemForSeller`（卖家本店明细）、`deleteOrderItemById`
- `OrderVO`：订单详情 = 订单头 + `items` + `logistics` + `tracks`
- `Order.logisticsStatus`：买家/卖家列表 SQL `LEFT JOIN logistics` 填充，供前端区分运输中/已送达
- **卖家查单**：`order → order_item → product → merchant`，用 `merchant.user_id` 鉴权（非 order 表 merchant_id）
- **`addOrder` 流程**（`OrderServiceImpl`，`@Transactional`）：
  1. 查 cart → 判空
  2. 遍历校验商品上架、库存；后端算 `totalAmount`（`price × quantity`）
  3. `insertOrder` → 拿 `order.id`
  4. 遍历 cart → `insertOrderItem`（快照名/价）→ `deductStock`
  5. `deleteCartByUserId` 清空购物车
  6. 订单号冲突 `DuplicateKeyException` 最多重试 3 次
- 踩坑记录见 `error.md`「addOrder 下单」小节

**后端 — 物流业务规则（卖家）**

- 首次发货 `POST /ship`：订单 `1→2`，创建 logistics（`status=1` 运输中），自动生成运单号
- 更新物流 `PUT /logistics`：卖家**只能**设 `status=1`（运输中）或 `3`（送到）；**不可**设 `0`（待发货）或 `2`（已签收）
- 标记「送到」`status=3` 后：禁止再改物流、禁止追加轨迹；自动写一条轨迹
- 买家确认收货 `POST /confirm`：订单 `2→3`，同步 logistics `→2`（已签收）

**后端 — 评价**

- `ReviewMapper/Service`：`insertReview`、`selectReviewsByProductId`、`selectReviewsByOrderId`
- `ReviewController`：`POST /api/reviews`；`GET /api/reviews?productId=`（公开）；`GET /api/reviews?orderNo=`（买家查本单已评）
- 规则：仅 `order.status=3`；商品须在订单明细内；同一 `order_id + product_id` 不可重复评；`score` 1–5

**后端 — 售后**

- `AfterSaleMapper/Service`：买家申请、列表；卖家列表与处理
- `AfterSaleController`：`/api/after-sales`（`GET /seller` 须在 `GET /{id}` 前）
- **粒度**：整单售后（`order_id`），非 `order_item` 级
- **type**：`1` 退货退款 · `2` 换货 · `3` 投诉 · `4` 仅退款
- **status**：`0` 待处理 · `1` 处理中 · `2` 已完成 · `3` 已拒绝
- **申请规则**：进行中（0/1）不可重复申请；`payment.status=2` 后不可再申请退货/仅退款
- **结案副作用**：退货退款 → `addStock`（整单明细）+ `payment 1→2`；换货 → `OrderService.reshipForExchange`（同条 logistics 补发）；仅退款 → `payment 1→2`；投诉 → 仅 `reply`
- `payment.status`：`2` 表示已退款（模拟）

**后端 — 管理员**

- `AdminController`：`/api/admin`，`adminAuth()` 校验 `role==2`
- 分类：`POST/PUT/DELETE /api/admin/categories`（删前查关联商品）
- 用户：`GET /api/admin/users`、`PUT /api/admin/users/{id}/status`（0 封禁 / 1 解封）
- 登录：`ApiloginController` 对 `user.status=0` 返回「账号已被禁用」

**后端 — 配置**

- `CorsConfig`：`/api/**` 允许 `5173`
- `SecurityConfig` permitAll（开发期）：登录注册、`/api/categories/**`、`/api/regions/**`、`/api/shops/**`、`/api/products/**`、`/api/cart/**`、`/api/order/**`、`/api/seller/**`、`/api/reviews/**`、`/api/after-sales/**`、`/api/admin/**`

**前端**

- 登录/注册：`AuthDialog` 弹窗 + `useUser` / `useAuthDialog` composables
- 商城 `/products`、`/products/:id`（分类/地区从后端拉取；商品详情含评价列表、商家链到店铺）
- 店铺 `/shops`、`/shops/:id`（店铺商品用 `merchantId` 筛 `/api/products`）
- 购物车 `/cart`、结算 `/checkout`（**导航购物车仅登录显示**）
- 买家订单 `/orders`、`/orders/:orderNo`（列表/详情状态结合 `logisticsStatus`：`buyerOrderStatusLabel`；已完成订单可评价；已支付单可申请售后）
- 买家售后 `/after-sales`；订单详情内售后记录 + 申请弹窗
- 卖家：`/seller/orders`、`/seller/orders/:orderNo`；`/seller/products`；`/seller/after-sales`（受理/完成/拒绝）
- `api/`：`request.js`、`product.js`、`cart.js`、`order.js`、`seller.js`、`shop.js`、`review.js`、`afterSale.js`
- `utils/`：`orderMeta.js`、`productMeta.js`、`afterSaleMeta.js`
- 管理员：`/admin/categories`、`/admin/users`
- `api/admin.js`、`utils/adminMeta.js`
- `App.vue` 导航：首页 / 商城 / 店铺 / 购物车(登录) / 我的订单 / 我的售后(登录) / 店铺订单+店铺售后+商品管理(卖家) / 分类管理+用户管理(管理员)

### 未实现

- 全局异常处理（见下文说明，**未做**）
- Redis 实际接入（见下文说明，**未做**）
- JWT（可选替代 Session，未做）
- Session 接入 Spring Security（见下文「已知缺口」）
- 商品图片上传（仍用 picsum 占位）
- 首页仍为脚手架
- 一单多商家时卖家列表金额仍显示整单 `totalAmount`（详情已按本店明细重算）

## 管理员模块（已实现）

测试账号 `admin` / `123456`，`user.role = 2`。`user.status`：`0` 已封禁 · `1` 正常。

### 已实现职责

| 功能 | 接口 | 说明 |
|------|------|------|
| **商品分类 CRUD** | `POST/PUT/DELETE /api/admin/categories` | 删前检查 `product.category_id`；公开 `GET /api/categories` 不变 |
| **用户封禁/解封** | `GET /api/admin/users`、`PUT /api/admin/users/{id}/status` | 软禁用；登录时 `status=0` 拒绝；不可封禁管理员或自己 |

### 未做（扩展）

- 地区字典、物流公司维护
- 违规商品强制下架、售后仲裁、全站统计

**后端**：`AdminController`（`adminAuth()` 校验 `role==2`）  
**前端**：`/admin/categories`、`/admin/users`；导航仅 `isAdmin` 显示

## 全局异常处理（未实现 · 说明）

**现状**：各 Controller 内 `try/catch (RuntimeException)` → `Result.fail(e.getMessage())`；Service 抛 `RuntimeException` 传业务文案。未捕获异常可能返回 Spring 默认 500 HTML。

**目标**：一处兜底，Controller 少写重复 catch。

**典型做法**：

```
exception/
  BusinessException.java     # 业务异常，带 message
  GlobalExceptionHandler.java  # @RestControllerAdvice
```

| 异常类型 | 返回 |
|----------|------|
| `BusinessException` | HTTP 200 + `Result.fail(message)` |
| `MethodArgumentNotValidException` | 200 + 参数校验文案 |
| 其他 `Exception` | 200 或 500 + 通用「系统繁忙」（生产环境勿把堆栈给前端） |

Service 改为 `throw new BusinessException("订单不存在")`，Controller 可去掉 try/catch，只保留「未登录」等 Session 判断。

**与当前项目衔接**：继续用 `Result`、`massage` 字段；登录接口 `ApiloginController` 可暂不纳入，或逐步统一。

## Redis（未实现 · 说明）

`pom.xml` 已引入 `spring-boot-starter-data-redis`，`application.yml` **未配置**连接，代码**零使用**。

**在本项目中可考虑的用途**（按优先级）：

| 用途 | 说明 |
|------|------|
| **Session 外置** | 多实例部署时 HttpSession 存 Redis；单体内嵌 Tomcat 开发期非必须 |
| **购物车缓存** | 高频读购物车；需与 MySQL 一致性策略（目前直接写库，更简单） |
| **商品热点缓存** | 首页/列表 `GET /products` 缓存分页结果，分类变更时失效 |
| **库存扣减防超卖** | 下单时对 `productId` 做 Redis 原子减；需与 DB 事务对齐，MVP 用 DB 扣库存即可 |
| **验证码 / 限流** | 登录、注册防刷 |

**若接入**：`application.yml` 增加 `spring.data.redis.host/port`；本地需起 Redis；先从一个只读场景（如分类列表缓存）试点即可。

**答辩表述**：技术栈规划含 Redis；MVP 以 MySQL + Session 为主，Redis 作为性能与扩展方案写入报告「后续优化」。

## 目录速查

### 后端 `src/main/java/edu/cafuc/crossmall/`

```
CrossMallApplication.java
config/
  SecurityConfig.java
  CorsConfig.java
controller/
  ApiloginController.java     # 登录 + 注册
  CategoryController.java
  RegionController.java         # GET /api/regions
  ShopController.java           # GET /api/shops
  ProductController.java
  CartController.java
  OrderController.java          # 买家 + 卖家订单、支付、物流
  SellerController.java         # 卖家商品 CRUD
  ReviewController.java         # 评价
  AfterSaleController.java      # 售后
  AdminController.java          # 管理员
service/ + impl/
  UserService, MerchantService
  CategoryService, ProductService
  CartService, OrderService, OrderItemService
  ReviewService, AfterSaleService
mapper/
  UserMapper, MerchantMapper
  CategoryMapper, ProductMapper
  CartMapper, OrderMapper, OrderItemMapper
  PaymentMapper, LogisticsMapper, LogisticsTrackMapper
  ReviewMapper, AfterSaleMapper
pojo/
  User, Merchant, Category, Product, Cart, Order, OrderItem
  Payment, Logistics, LogisticsTrack, Review, AfterSale
  Result.java
  vo/ProductVO.java, vo/OrderVO.java, vo/MerchantVO.java, vo/ReviewVO.java, vo/AfterSaleVO.java, vo/AdminUserVO.java
```

### 后端资源

```
resources/application.yml
resources/mapper/
  UserMapper.xml, MerchantMapper.xml
  CategoryMapper.xml, ProductMapper.xml
  CartMapper.xml, OrderMapper.xml, OrderItemMapper.xml
  PaymentMapper.xml, LogisticsMapper.xml, LogisticsTrackMapper.xml
  ReviewMapper.xml
  AfterSaleMapper.xml
```

### 前端 `crossmall-vue/src/`

```
api/request.js, product.js, cart.js, order.js, seller.js, shop.js, review.js, afterSale.js, admin.js
utils/orderMeta.js, productMeta.js, afterSaleMeta.js, adminMeta.js
composables/useUser.js, useAuthDialog.js
components/AuthDialog.vue
components/seller/SellerProductTable.vue, SellerProductFormDialog.vue
views/
  ProductsView.vue, ProductDetailView.vue
  ShopsView.vue, ShopDetailView.vue
  CartView.vue, CheckoutView.vue
  OrdersView.vue, OrderDetailView.vue
  SellerOrdersView.vue, SellerOrderDetailView.vue
  SellerProductsView.vue, SellerAfterSalesView.vue
  AfterSalesView.vue
  AdminCategoriesView.vue, AdminUsersView.vue
  HomeView.vue, SuccessView.vue
router/index.js
App.vue
```

## API 速查

### 统一响应 `Result`

```json
{ "success": true, "list": [], "total": 11 }
{ "success": true, "data": { ... } }
{ "success": false, "massage": "错误信息" }
```

字段名 **`massage`** 为项目约定拼写，勿改。

登录/注册仍用 `ApiloginController` 的 `Map` + `ResponseEntity`，结构与 `Result` 类似（`success` + `massage`）。

### 分类 / 地区 / 店铺（无需登录）

**分类** `GET /api/categories` — `Result.okList(categories, count)`；前端取 `res.data.list`

**地区** `GET /api/regions` — 来自 `merchant` 表 DISTINCT；注册卖家下拉与商城地区筛共用

**店铺列表** `GET /api/shops` — 参数 `region`、`keyword`、`page`、`pageSize`；返回 `MerchantVO`（含 `productCount`）

**店铺详情** `GET /api/shops/{shopId}`

### 商品展示（无需登录）

**商品列表** `GET /api/products` — 参数 `categoryName`、`merchantId`、`region`、`keyword`、`sort`、`page`、`pageSize`

**商品详情** `GET /api/products/{productId}`

`sort` 白名单：`time_desc`（默认）、`time_asc`、`name_asc`、`name_desc`、`price_asc`、`price_desc`

### 卖家商品 `/api/seller`（需 Session + `role=1`）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/seller/products` | 本店商品列表（含下架）；`keyword`、分页 |
| POST | `/api/seller/products` | 新增：`productName`、`categoryName`、`price`、`stock`、`description` |
| GET | `/api/seller/products/{id}` | 本店商品详情 |
| PUT | `/api/seller/products/{id}` | 更新；含 `status`（0 下架 / 1 上架） |

分类下拉复用 `GET /api/categories`，不在 Seller 接口重复提供。

### 评价 `/api/reviews`

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/reviews` | 提交：`orderNo`、`productId`、`score`(1–5)、`content`；需登录 |
| GET | `/api/reviews?productId=` | 商品评价列表（公开，含 `nickname`） |
| GET | `/api/reviews?orderNo=` | 某订单已评记录（买家本人） |

### 售后 `/api/after-sales`（需 Session）

**路由顺序**：`GET /seller` 须在 `GET /{id}` 之前。

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/after-sales` | 买家申请：`orderNo`、`type`、`reason` |
| GET | `/api/after-sales` | 买家我的列表；`orderNo` 参数 → 某订单售后（须本人订单） |
| GET | `/api/after-sales/seller` | 卖家本店相关售后列表；`role=1` |
| PUT | `/api/after-sales/{id}/handle` | 卖家处理：`status`(1受理/2完成/3拒绝)、`reply`；换货完成时 `company` 必填 |

**after_sale.type**：`1` 退货退款 · `2` 换货 · `3` 投诉 · `4` 仅退款

**after_sale.status**：`0` 待处理 · `1` 处理中 · `2` 已完成 · `3` 已拒绝

**payment.status（售后相关）**：`2` = 已退款（模拟）

### 管理员 `/api/admin`（需 Session + `role=2`）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/admin/categories` | 新增：`categoryName`、`sort`（可选） |
| PUT | `/api/admin/categories/{id}` | 更新分类 |
| DELETE | `/api/admin/categories/{id}` | 删除（有关联商品则失败） |
| GET | `/api/admin/users` | 用户列表（无密码） |
| PUT | `/api/admin/users/{id}/status` | `status`：0 封禁 / 1 解封 |

### 登录与注册（表单，非 JSON）

**登录** `POST /api/userLogin` — `username`、`password`

**注册** `POST /api/userRegister` — `username`、`password`、`nickname`、`role`（0 买家 / 1 卖家）、卖家另填 `merchantName`、`region`（来自 `/api/regions`）、`description`

### 购物车（需 Session）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/cart` | 加购：`productId`、`quantity` |
| GET | `/api/cart` | 当前用户购物车 |
| PUT | `/api/cart/{id}` | 改数量 |
| DELETE | `/api/cart/{id}` | 删除一项 |

### 订单 `/api/order`（需 Session；`orderNo` 走路径）

**路由顺序注意**：`GET /seller`、`GET /{orderNo}/seller` 须在 `GET /{orderNo}` **之前**声明，避免 `seller` 被当成订单号。

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/order` | 下单；参数 `payType`、`address`、`receiverName`、`receiverPhone` |
| GET | `/api/order` | 买家订单列表（含 `logisticsStatus`） |
| GET | `/api/order/seller` | **卖家**订单列表（含 `logisticsStatus`）；需 `role=1` |
| GET | `/api/order/{orderNo}/seller` | **卖家**订单详情（`OrderVO`，items 仅本店商品，金额按快照价重算） |
| GET | `/api/order/{orderNo}` | 买家订单详情（`OrderVO`） |
| PUT | `/api/order/{orderNo}/receiver` | 改收货信息（仅 `status=0`） |
| POST | `/api/order/{orderNo}/pay` | 买家模拟支付（`0→1`） |
| POST | `/api/order/{orderNo}/confirm` | 买家确认收货（`2→3`，物流 `→2`） |
| DELETE | `/api/order/{orderNo}` | 删待支付单（`status=0`）；删明细 + 回库存 |
| POST | `/api/order/{orderNo}/ship` | **卖家**首次发货；`company` 必填；运单号自动生成；订单 `1→2` |
| PUT | `/api/order/{orderNo}/logistics` | **卖家**更新物流；`status` 仅允许 `1` 或 `3` |
| POST | `/api/order/{orderNo}/tracks` | **卖家**追加轨迹；`status=3` 或 `2` 后拒绝 |

**订单 `status`**：`0` 待支付 · `1` 已支付 · `2` 已发货 · `3` 已完成 · `4` 已取消（库表保留，**无取消接口**，待支付用 DELETE）

**物流 `logistics.status`**：`0` 待发货 · `1` 运输中 · `2` 已签收（买家确认） · `3` 送到（卖家标记，之后锁定）

**`payType`**：`1` 支付宝 · `2` 微信 · `3` 信用卡

**卖家列表前端状态文案**（`orderMeta.js` → `sellerOrderStatusLabel`）：

| 条件 | 显示 |
|------|------|
| `status=1` | 已支付（筛选项称「待发货」） |
| `status=2` 且 `logisticsStatus≠3` | 运输中 |
| `status=2` 且 `logisticsStatus=3` | 已送至 |
| `status=3` 或 `logisticsStatus=2` | 已签收 |

**买家列表/详情状态文案**（`buyerOrderStatusLabel`）：

| 条件 | 显示 |
|------|------|
| `status=2` 且 `logisticsStatus=1` | 运输中 |
| `status=2` 且 `logisticsStatus=3` | 已送达 |
| `status=3` 或 `logisticsStatus=2` | 已完成 |
| 其他 | `ORDER_STATUS[status]` |

卖家列表筛选：全部 / 待发货(1) / 运输中 / 已送至 / 已签收

物流公司下拉（前端固定）：DHL、FedEx、UPS、EMS、顺丰国际

## 登录与鉴权（重要）

- 前端：`withCredentials: true`；Vite 代理 `/api` → `8080`
- 登录成功写 HttpSession，**未**写入 Spring Security `SecurityContext`
- 卖家接口：`OrderController.sellerAuth()` / `SellerController.sellerAuth()` 校验 `role==1`

**已知缺口**：可能出现「登录 200 但接口 401」。上线前应 Session → Security 打通，并改 `authenticated()`。

## 前端联调要点

```js
// 分类 / 地区（注意 list 在 res.data.list）
const res = await getCategories()
categories.value = res.data?.list || []

const res = await getRegions()
regions.value = res.data?.list || []

// 店铺
const res = await getShopList({ page: 1, pageSize: 9 })
shops.value = res.data.list

// 本店商品（卖家）
const res = await request.get('/seller/products')

// 商品评价
const res = await getProductReviews(productId)
reviews.value = res.data.list

// 订单内评价（已完成订单）
await submitReview({ orderNo, productId, score: 5, content: '...' })

// 买家订单列表（logisticsStatus + buyerOrderStatusLabel）
const res = await request.get('/order')
```

## 排查清单

| 现象 | 优先检查 |
|------|----------|
| 商品/分类 401/403 | `SecurityConfig` 放行；重启后端 |
| 登录成功但接口 401 | Session 未接入 Security |
| 分类/地区下拉为空 | 前端是否用 `res.data.list` 而非 `res.data` |
| 卖家商品插失败 | `ProductMapper.insertProduct` 占位符是否驼峰 |
| 卖家看到全站商品 | 是否误用公开 `selectProductList` 而非本店 SQL |
| `GET /order/seller` 404 | Controller 路由顺序：`/seller` 在 `/{orderNo}` 前 |
| 买家已送达仍显示已发货 | 列表 SQL 是否 JOIN logistics；是否用 `buyerOrderStatusLabel` |
| 评价失败 | 订单是否 `status=3`；是否重复评同一商品 |
| 删待支付单后库存未回 | `deleteOrderByOrderNo` 是否 `addStock` + 删 `order_item` |
| addOrder 踩坑 | 读 `error.md` |
| 售后申请失败 | 是否已有进行中(0/1)；待支付应删单；已退款不可再退 |
| 换货完成失败 | 卖家是否填 `company`；订单是否已发过货 |
| CORS | 走 5173 代理，勿直连 8080 |

## 编码约定

- 包名：`edu.cafuc.crossmall`
- API 前缀：`/api`
- MyBatis：`mapper/*.xml`，`map-underscore-to-camel-case: true`（`logistics_status` → `logisticsStatus`）
- 新接口优先用 `Result`；错误信息 key 用 `massage`
- Mapper 方法按**业务场景**命名，避免无意义全套 CRUD 模板
- 动态排序用 XML `<choose>` 白名单，禁止 `${sort}` 拼接 SQL
- 卖家订单/商品：更新与查询 SQL 带 `merchant_id` 或 JOIN 鉴权
- 公开浏览与卖家后台接口分离：`ProductController` vs `SellerController`

## 修改此文档的时机

- 新增/完成 Controller、前端页面、鉴权变更
- MVP 某模块完成（如评价、店铺、卖家商品）
- 端口、代理、数据库、Security 放行路径变更
- 确认或修复「已知缺口」
- 管理员模块、全局异常、Redis 接入规划或落地
