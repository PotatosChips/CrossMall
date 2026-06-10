## 心路历程

### 各个表的属性名尽量不相同
    marchant,product,category的名字军用name导致前端传递值的时候分不清
    ->改成了marchant_name product_name category_name的形式
### insert Cart
    插入cart时，应该先查看是否有，如果有则update cart。不然会违反唯一约束
### 更新，删除，查询Cart
    应该用cart.getId()或者productId+userId，而不是productId（没有限定用户）
### userId
    获取userId应该session.getAttribute("user")，而不是前端数据
### 更新订单需要订单号，用户id，状态都相同
    没有用户id->知道别人订单号就可能改别人的订单。
    重复操作 / 并发，典型场景：用户连点两次「支付」->更新两次库存等
### 防止并发操作
    生成订单时try catch订单号是否存在
### status不能让前端传递参数
    否则别人可以直接改status，不安全

---

## addOrder 下单（order + order_item）

### order_item 必须先有 orderId
    order_item.order_id 依赖 order.id，必须先 insertOrder 再插明细。
    但「校验购物车」应写在 insertOrder **之前**（只读），避免空购物车/库存不足时留下无明细的废订单。

### 空购物车判断
    错误：`carts == null && carts.isEmpty()` → null 时 NPE；空列表时条件为 false 会继续下单。
    正确：`carts == null || carts.isEmpty()`。

### 总价必须由后端计算
    不要让前端传 amount，易被篡改或与购物车不一致。
    循环 cart：`totalAmount += price × quantity`（只加单价会少算数量）。

### 扣库存用 deductStock，不是 updateStock
    ProductMapper.updateStock 第二个参数是**扣减后的剩余库存**，不是购买数量。
    应调 ProductService.deductStock(id, quantity)，内部会校验并 `currentStock - quantity`。

### 清空购物车
    错误：`cartMapper.selectCartByUserId(userId)` 只是查询，没有删除。
    正确：`deleteCartByUserId(userId)` 或循环 `deleteCartById`；建议在扣库存全部成功后再清 cart。

### 商品下架 / 不存在
    selectProductById 可能返回 null（下架），直接 getProductName() 会 NPE。
    insertOrder 前应判空；第二遍循环插 item 时也可再判（极端并发下架）。

### 库存预检
    第一遍循环用 selectStockById 与 quantity 比较，失败则不要 insertOrder。
    注意：预检与 deductStock 之间仍有并发窗口，deductStock 返回 0 时仍要抛异常。

### @Transactional 与 catch
    加了 @Transactional 后，只有异常**逃出** Service 方法才会回滚。
    Service 里 catch 住 RuntimeException 且不 throw → 不会回滚，可能脏数据。
    Controller 的 catch 仍有意义：回滚后把异常转成 Result.fail，避免 500。
    catch DuplicateKeyException 用于订单号冲突重试：在 insertOrder 失败时吞掉并重试，一般无脏数据。

### 订单号冲突重试
    insertOrder 订单号唯一，并发时可能 DuplicateKeyException，try-catch 重新 generateOrderNo。
    若整段 addOrder 包在一个 @Transactional 里且重试逻辑复杂，可考虑单次下单抽到独立 Service 每次新事务。

### 删订单要删 order_item
    deleteOrderByOrderNo 只删 order 表会留下孤儿 order_item，应按 order_id 先删明细再删订单头。

### 订单详情用 OrderVO
    GET /api/order/{orderNo} 应返回 OrderVO（订单头 + items + logistics + tracks），不要只返回 Order。
    items 通过 selectOrderItemByOrderId(order.getId()) 查询。

### 添加订单失败
    添加订单时，自动返回自增 ID，但 Mapper 接口是多参数（不是实体类），MyBatis 不知道把自增 ID 赋值给谁
    去除自动返回自增 ID

### 删除订单没有处理其他数据
    删除订单时，还需要删除订单详情，更新库存
#### 没有登录加入购物车，登录之后加入购物车的操作被保留

---

## 支付与订单状态（order.status / payment.status）

### 两套 status 不要混

| 表 | 字段 | 含义 |
|----|------|------|
| `order` | `status` | 订单生命周期：0待支付 1已支付 2已发货 3已完成 4已取消 |
| `payment` | `status` | 支付单结果：0待支付 1支付成功 2支付失败（**2 当前代码未实现**） |

订单状态和支付状态**相关但不相同**。例如：`order.status=0` 时，`payment` 可以不存在，也可以有一条 `status=0` 的占位记录（测试数据或历史版本遗留）。

`pay_type`（支付方式）写在 `order` 上，支付时复制到 `payment`：`1` 支付宝 · `2` 微信 · `3` 信用卡。

---

### order.status 何时变、由谁变

| 当前 status | 操作 / 接口 | 新 status | 条件 / 说明 |
|-------------|-------------|-----------|-------------|
| — | `POST /api/order` 下单 | **0** 待支付 | 后端写死 `status=0`，前端不传 status |
| 0 | `POST /api/order/{orderNo}/pay` 模拟支付 | **1** 已支付 | 仅订单本人；见下文 payment 逻辑 |
| 0 | `POST /api/order/{orderNo}/cancel` 取消 | **4** 已取消 | `updateOrderStatus(..., 4, 0)`，仅待支付可取消 |
| 0 | `DELETE /api/order/{orderNo}` 删单 | （删除行） | 仅 `status=0` 可删 |
| 1 | `POST /api/order/{orderNo}/ship` 卖家发货 | **2** 已发货 | 仅卖家；订单须已支付 |
| 2 | `POST /api/order/{orderNo}/confirm` 确认收货 | **3** 已完成 | 买家操作；须已发货 |
| 0 | `PUT /api/order/{orderNo}/receiver` 改收货 | **0** 不变 | 仅待支付可改地址/电话等 |

**不会变的典型情况**

- 已支付（1）不能直接取消为 4，须走售后（未实现）
- 已发货（2）不能回退到 1
- 已完成（3）、已取消（4）不能再支付

**失败返回**（HTTP 200 + `Result.fail`，不是改状态）：

- 支付：订单不存在 / 状态不是 0 / payment 已是成功 / 写入 payment 失败
- 取消 / 确认：SQL `expectedStatus` 不匹配 → 返回「订单不存在或状态不允许」

---

### payment.status 何时变、由谁变

| 场景 | payment 表现 | status 变化 |
|------|--------------|-------------|
| 下单 `addOrder` | **不创建** payment 记录 | — |
| 首次模拟支付，且尚无 payment 行 | `insertPayment` | **1** 支付成功（同时写 `pay_no`、`pay_time`、`amount`） |
| 已有 `status=0` 的待支付记录（如测试数据占位） | `updatePaymentSuccess` | **0 → 1**（补全流水号与时间） |
| 已有 `status=1` | 拒绝支付 | 不变，返回「订单已支付」 |
| 取消订单 / 删待支付单 | 代码**不删不改** payment | 不变（若存在待支付行会留在库里） |

**`pay_no` 规则**（`OrderServiceImpl.generatePayNo`）：`payType` 1→`ALI`、2→`WX`、3→`CC` + 日期 + 4 位序号。

**表约束**：`payment.order_id` 唯一 → 一个订单最多一条支付记录。

---

### order 与 payment 的对应关系（联调对照）

| order.status | 期望 payment | 说明 |
|--------------|--------------|------|
| 0 待支付 | 无记录，或一条 `status=0` | 两种都允许支付；支付成功后 order→1、payment→1 |
| 1 已支付 | 一条 `status=1`，有 `pay_no` | 正常已付 |
| 2/3/4 | 通常 `status=1` | 发货/完成/取消不再走支付接口 |

**测试数据约定**（`data.sql`）：待支付订单（如 `CM202503100001`）**不预插** payment；已支付订单才插 `status=1` 的记录。避免「order 待支付 + payment 待支付占位」与旧代码冲突。

---

### 与支付相关的完整链路（便于对号）

```
下单          order: — → 0          payment: 无
模拟支付      order: 0 → 1          payment: 无→1 或 0→1
卖家发货      order: 1 → 2          logistics: 新建 status=1
确认收货      order: 2 → 3          logistics: 不变（可另接口改 status）
取消(待支付)  order: 0 → 4          payment: 代码未动
```

物流 `logistics.status`（0待发货 1运输中 2已签收）与支付无关；发货时订单变 2，物流初始为 1（运输中）。

