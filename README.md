# 建材商户平台

面向建材市场的三端平台：平台管理后台（管商户）+ 商户端（管门店与产品）+ 微信小程序（找产品/调货）。

核心业务目标：商户入驻（365 元 / 年）→ 发布产品 → 小程序展示 → 同行调货、跨区域价格对比、厂家找渠道。

---

## 一、目录结构

```
jiancai/
├── sql/jiancai.sql        # 数据库初始化脚本（建库 + 建表 + 初始数据）
├── server/                # 后端 Spring Boot 3.2（JDK 17）
│   └── src/main/java/com/jiancai
│       ├── common/        # 统一返回 R、业务异常、全局异常处理
│       ├── config/        # Web 配置（拦截器/跨域/静态资源）、MyBatis-Plus 分页
│       ├── security/      # JWT 工具、登录拦截器、用户上下文
│       ├── entity/        # 9 张表对应实体
│       ├── mapper/        # MyBatis-Plus Mapper
│       ├── service/       # 业务逻辑
│       └── controller/    # 接口
├── admin/                 # 管理后台 Web（Vue3 + Vite + Element Plus）
│   └── src/views          # 平台端页面 + views/merchant（商户端页面，按角色切换）
└── miniprogram/           # 微信小程序（原生）
    ├── pages/index        # 首页：轮播 + 品类 + 广告 + 最新产品
    ├── pages/product      # 找产品：搜索 + 品类筛选 + 分页
    ├── pages/detail       # 产品详情：图片 + 参数 + 档口电话 + 转发
    ├── pages/shop         # 商户主页：门店信息 + 在售产品
    └── pages/mine         # 我的：浏览记录 + 客服 + 教程 + 免责说明
```

## 二、技术选型

| 层 | 技术 |
|---|---|
| 后端 | Spring Boot 3.2 + MyBatis-Plus 3.5.6 + MySQL 8 + JWT(jjwt 0.12) + BCrypt |
| 后台 Web | Vue 3 + Vite 5 + Element Plus 2.7 + Vue Router 4 + Axios |
| 小程序 | 原生微信小程序（无需构建，微信开发者工具直接打开） |

## 三、启动步骤

### 1. 选择数据库模式（二选一）

**方式 A：H2 内存库（默认，开箱即用）**

不需要安装任何数据库，直接启动即可。内置演示数据：8 个品类、3 个商户、10 个产品。
数据保存在内存中，**重启服务后会恢复为初始演示数据**，适合先看功能和演示。

**方式 B：MySQL 8（正式环境）**

1. 安装 MySQL 8 后执行脚本（或用 Navicat / SQLyog 打开执行）：

```sql
source D:/工作1/sql/jiancai.sql
```

2. 编辑 `server/src/main/resources/application.yml`，把

```yaml
spring:
  profiles:
    active: h2
```

改成 `active: mysql`，并在 `application-mysql.yml` 中填写数据库账号密码。

### 2. 配置图片上传目录

编辑 `server/src/main/resources/application.yml`：

```yaml
jiancai:
  upload:
    path: D:/jiancai-upload/   # 图片保存目录，需有写权限
```

### 3. 启动后端（端口 8080）

```powershell
$env:JAVA_HOME='D:\Program Files\Java\jdk-17'
cd D:\工作1\server
mvn spring-boot:run
```

> 本机 Maven 默认指向 JDK 8，必须先切 `JAVA_HOME` 到 JDK 17，否则 Spring Boot 3 无法启动。

### 4. 启动管理后台（端口 5173）

```powershell
cd D:\工作1\admin
npm install
npm run dev
```

浏览器打开 http://localhost:5173 ，接口已通过 Vite 代理转发到 8080。

**默认账号**

| 角色 | 账号 | 密码 | 说明 |
|---|---|---|---|
| 平台管理员 | `admin` | `123456` | 审核商户/产品、管品类、管轮播与广告位 |
| 商户 | `merchant` | `123456` | 管自己的门店与产品、看被浏览/被转发数据 |

> 新商户审核通过后，平台会**自动开通账号：账号=商户手机号，初始密码 123456**。

### 5. 打开小程序

1. 微信开发者工具 → 导入项目 → 目录选 `D:\工作1\miniprogram`，AppID 换成你自己的（或选"测试号"）。
2. 右上角"详情" → 本地设置 → 勾选 **不校验合法域名**（开发阶段）。
3. 把 `miniprogram/utils/request.js` 里的 `BASE_URL` 改成后端地址：
   - 模拟器：`http://localhost:8080/api/mp`
   - 真机调试：`http://192.168.x.x:8080/api/mp`（本机局域网 IP）
   - 线上：必须是 `https://域名/api/mp`

## 四、主要接口

| 前缀 | 说明 | 鉴权 |
|---|---|---|
| `/api/auth/**` | 登录、商户入驻申请 | 否 |
| `/api/mp/**` | 小程序：首页聚合、品类、产品列表/详情、商户主页、浏览/转发上报、浏览记录 | 否 |
| `/api/admin/**` | 平台端：商户管理、产品审核、品类、轮播/广告、数据总览 | 管理员 |
| `/api/merchant/**` | 商户端：门店信息、产品发布/上下架、数据看板 | 商户 |

## 五、业务流转

```
商户提交入驻(/api/auth/register)
      ↓ 状态=待审核
平台审核通过 → 自动开通商户账号（手机号/123456）
      ↓
商户登录商户端 → 维护门店 → 发布产品（状态=待审核）
      ↓
平台审核产品 → 在售
      ↓
小程序首页/搜索展示 → 用户浏览、转发（统计回流到商户端"被浏览/被转发"）
```

## 六、当前未实现（下一阶段可做）

- **真实 openid**：现用本地随机标识，需后端接入 `code2session` 换取真实 openid（登录态、用户体系才完整）。
- **在线缴费**：`payment_record` 表与 365 元/年 的会员体系已设计，尚未对接微信支付与到期自动下架逻辑。
- **省/市/市场三级筛选与价格对比页**：接口已预留 `market` 参数，前端页面未做。
- **厂家入驻与招商页**：按你的第 ④、⑤ 步规划扩展。
