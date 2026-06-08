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
- Spring Security（已引入，登录/注册接口放行）
- Redis 依赖已在 `pom.xml`，**尚未使用**（无 Redis 配置、无代码引用）
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

访问前端：`http://localhost:5173`  
开发时 API 走 Vite 代理，**不要**直接改 axios baseURL 为 8080。

## 数据库

- 库名：`cross-mall`
- 连接：`application.yml` → `localhost:3306`，用户 `root`，密码 `123456`
- 建表：`sql.sql`（12 张表）
- 测试数据：`data.sql`

**测试账号**（密码均为 `123456`，当前为**明文**比对，未加密）：

| 用户名 | role | 角色 |
|--------|------|------|
| buyer1, buyer2 | 0 | 买家 |
| seller_jp, seller_us, seller_eu | 1 | 卖家 |
| admin | 2 | 管理员 |

## 当前实现进度

### 已实现

**后端**

- 登录接口 `POST /api/userLogin`
- 注册接口 `POST /api/userRegister`（买家 + 卖家同一接口）
- 用户查询/插入（MyBatis `UserMapper`）
- 商户插入（MyBatis `MerchantMapper`，卖家注册时写入）
- CORS 配置（允许 `http://localhost:5173`）
- Spring Security 基础配置（CSRF 关闭，`/api/userLogin`、`/api/userRegister` permitAll）

**前端**

- 登录页 `/login`（`LoginView.vue`，含跳转注册链接）
- 注册页 `/register`（`RegisterView.vue`，买家/卖家同一页，角色下拉切换）
- 登录成功页 `/success`（`SuccessView.vue`）
- Axios 封装（`src/api/request.js`）
- 首页 / About 仍为 Vue 脚手架默认页

### 未实现（见 `request.md` 规划）

- 商品/分类/商家 CRUD、购物车、订单、支付、物流、评价、售后
- 统一响应体、全局异常处理
- Redis 缓存、JWT（需求里提到 JWT 或 Session，当前用 Session）
- 前端：商品列表/详情、购物车、下单、个人中心等

## 目录速查

### 后端 `src/main/java/edu/cafuc/crossmall/`

```
CrossMallApplication.java     # 入口
config/
  SecurityConfig.java         # CSRF 关、登录/注册放行、其余 authenticated
  CorsConfig.java             # /api/** CORS
controller/
  ApiloginController.java     # 登录 + 注册
service/ + impl/              # UserService、MerchantService
mapper/                       # UserMapper、MerchantMapper
pojo/User.java                # role: 0买家 1卖家 2管理员
pojo/Merchant.java            # 商家（merchantName, region, user_id）
```

### 后端资源

```
resources/application.yml     # 数据源、MyBatis
resources/mapper/UserMapper.xml
resources/mapper/MerchantMapper.xml
```

### 前端 `crossmall-vue/src/`

```
api/request.js                # axios 实例，baseURL=/api，withCredentials=true
views/LoginView.vue           # 登录页
views/RegisterView.vue        # 注册页（买家/卖家）
views/SuccessView.vue         # 登录成功页
views/HomeView.vue            # 脚手架首页
router/index.js               # /, /login, /register, /success, /about
main.js                       # Pinia + Router + ElementPlus
```

## 登录与鉴权（重要）

### 登录

- 接口：`POST /api/userLogin`
- 参数：**表单** `application/x-www-form-urlencoded`（`username`, `password`）
- **不是 JSON**。前端用 `URLSearchParams`，后端方法参数直接绑定。

成功 `200`：

```json
{ "success": true, "username": "...", "role": 0 }
```

失败 `401`：

```json
{ "success": false, "massage": "用户名与密码不匹配" }
```

- 登录成功：`session.setAttribute("user", login)`，浏览器通过 `JSESSIONID` Cookie 维持会话
- 前端登录成功后跳转 `/success`

### 注册

- 接口：`POST /api/userRegister`
- 参数：**表单** `application/x-www-form-urlencoded`（**不是 JSON**）

| 参数 | 必填 | 说明 |
|------|------|------|
| `username` | 是 | 用户名，唯一 |
| `password` | 是 | 密码（明文存储，与登录一致） |
| `nickname` | 否 | 昵称 |
| `role` | 是 | `0` 买家，`1` 卖家 |
| `merchantName` | 卖家必填 | 商户名称（绑 `Merchant.merchantName`；DB 列 `merchant_name`） |
| `region` | 卖家必填 | 地区（如 中国/日本/美国/欧洲） |
| `description` | 否 | 商户简介（500 字以内） |

**注意**：表单字段用 `merchantName`，不要用泛化的 `name`（与商品 `productName` 区分，避免 Spring 绑定混淆）。

**流程**：校验（`validateUserAndMerchant`）→ `user.status=1` → 插入 `user`（`useGeneratedKeys` 回填 `id`）→ 若 `role=1` 再插入 `merchant`（`user_id` 关联新用户）。

成功 `200`：

```json
{ "success": true, "username": "..." }
```

卖家额外返回 `merchantName`。失败 `400`：

```json
{ "success": false, "massage": "用户名已存在" }
```

- 注册成功**不**写 Session，前端跳转 `/login`
- 管理员（`role=2`）不开放注册

### 通用响应约定

注意字段名是 **`massage`**（拼写错误），前后端已统一用这个 key。

### Session 机制

- 前端：`withCredentials: true`（必须）
- Vite 代理：`/api` → `http://localhost:8080`

### 已知设计缺口（后续加受保护接口时注意）

登录把 User 放进 HttpSession，但 **Spring Security 的 `SecurityContext` 未写入 Authentication**。  
`SecurityConfig` 里 `anyRequest().authenticated()` 检查的是 Security 体系，不读 `session.getAttribute("user")`。

**现状**：网页登录/注册可用。  
**风险**：新增需要登录的 `/api/**` 接口时，可能出现「登录 200 但其他接口 401/403」。届时需把 Session 登录接入 Spring Security，或改 JWT 方案。

## 前端联调要点

```js
// vite.config.js
proxy: { '/api': { target: 'http://localhost:8080', changeOrigin: true } }

// request.js
baseURL: '/api', withCredentials: true
```

开发时必须同时启动后端 8080 和前端 5173，通过 5173 访问页面。

## 排查清单

| 现象 | 优先检查 |
|------|----------|
| 登录失败 / 网络错误 | 后端是否在 8080；MySQL 是否可用；是否通过 5173 访问 |
| POST 返回 403 | SecurityConfig 是否生效（改配置后需重启）；CSRF 是否关闭 |
| 401 用户名密码错误 | 是否导入 data.sql；参数是否为 form 而非 JSON |
| 400 注册失败 | 用户名是否重复；卖家是否传了 **merchantName**、region |
| 卖家注册后 merchant 无 user_id | insertUser 是否配置了 useGeneratedKeys；是否先插 user 再插 merchant |
| 登录成功但后续接口失败 | Session 与 Spring Security 未打通（见上） |
| CORS 问题 | 仅在前端直连 8080 时出现；走 Vite 代理通常无 CORS |

## 编码约定

- 包名：`edu.cafuc.crossmall`
- API 前缀：`/api`
- MyBatis：`mapper/*.xml`，驼峰映射已开
- 密码：当前明文存储/比对，注释里提到后续加密
- 新接口建议遵循 `ApiloginController` 的 `{ success, massage? }` 风格，或按 `request.md` 规划做统一响应

## 修改此文档的时机

- 新增控制器 / 前端页面 / 鉴权方案变更
- 端口、代理、数据库配置变更
- 确认或推翻某个「已知问题」（例如 Session 是否已接入 Security）
- MVP 某个模块完成时，更新「当前实现进度」
