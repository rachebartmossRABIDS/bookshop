# Bookshop 书城系统

一个前后端分离的在线书城项目，包含图书浏览、搜索、书店详情、评论、订单、用户登录与改密等功能。

## 技术栈

| 端 | 技术 |
|---|---|
| 后端 | Java 8+ · Spring 5.3 · Spring MVC · MyBatis 3.5 · Druid · MySQL 8 · PageHelper · Logback |
| 前端 | Vue 3 · Vite 5 · Vant 4 · Pinia（持久化） · Vue Router · Axios |
| 部署 | 内嵌 Tomcat 7 插件（开发） / war 包外置 Tomcat 9（可选） |

## 项目结构

```
bookshop/
├── javaEEProject4/          # 后端（Maven war 项目）
│   └── src/main/java/cn/edu/hbpu/
│       ├── controller/      # REST 接口：user/book/kind/shop/comment/order
│       ├── service/         # 业务层
│       ├── mapper/          # MyBatis Mapper（接口 + XML）
│       ├── pojo/            # 实体与统一返回 Result
│       ├── configuration/   # Spring/MyBatis/数据源 Java 配置
│       └── advice/          # 全局异常处理
├── vue/                     # 前端（Vite + Vue3 SPA）
├── picture/                 # 图书/书店/头像图片，开发时挂载为 /pic 静态服务
└── bookshop.sql             # 数据库初始化脚本（含演示数据）
```

## 环境要求

- JDK 8 或 17（已在 JDK 17 下验证）
- Maven 3.6+
- MySQL 8.x，需存在 `bookshop` 数据库
- Node.js 18+

## 快速开始

### 1. 初始化数据库

```bash
mysql -uroot -p < bookshop.sql
```

若你的数据库账号密码不是 `root/123456`，修改后端配置：
`javaEEProject4/src/main/resources/jdbc.properties`

### 2. 启动后端（端口 8090，上下文 /project1）

```bash
cd javaEEProject4
mvn tomcat7:run
```

- API 根路径：`http://localhost:8090/project1`
- 图片静态服务：`http://localhost:8090/pic`（由插件将仓库根目录 `picture/` 挂载为 `/pic`，
  如目录位置不同请修改 `pom.xml` 中 tomcat7-maven-plugin 的 `staticContextDocbase`）

### 3. 启动前端

```bash
cd vue
npm install
npm run dev
```

浏览器访问 Vite 输出的地址（默认 `http://localhost:5173`）。
前端通过代理访问后端：`/api → http://localhost:8090/project1`、`/pic → http://localhost:8090/pic`。

### 演示账号

| 用户名 | 密码 | 说明 |
|---|---|---|
| testuser | 123456 | 普通用户 |
| admin | 123456 | 管理员 |

## API 概览

| 模块 | 接口示例 |
|---|---|
| 用户 | `POST /user/login`、`GET /user/{uid}`、`POST /user/updatePassword` |
| 图书 | `GET /book/list/{kid}`、`GET /book/detail?bid=`、`POST /book/search`（分页） |
| 分类 | `GET /kind/list` |
| 书店 | `GET /shop/list/{kid}`、`GET /shop/sdetail?sid=` |
| 评论 | `GET /comment/listByBid`、`POST /comment/save`、`POST /comment/remove`（分页） |
| 订单 | `GET /order/listByUid`（分页）、`POST /order/save`、`POST /order/remove` |

统一返回格式：`{ "code": 1成功/0失败, "msg": "...", "data": ... }`

## 说明

- 密码使用 MD5 存储（教学演示用途，生产环境请改用 BCrypt 等加盐哈希）。
- 项目为纯 REST 后端 + SPA 前端，`index.jsp` 仅为服务状态提示页。

## License

[MIT](LICENSE)
