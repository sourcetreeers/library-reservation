# 图书馆座位预约系统

在线图书馆座位预约管理系统，支持多馆区管理、实时座位监控、扫码签到、积分体系等功能。包含**移动端（学生）**、**管理后台（图书馆管理员）** 和**系统管理后台**三个端。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | Spring Boot 2.7.18 |
| ORM | MyBatis Plus 3.5.3.1 |
| 数据库 | MySQL 8.0 |
| 实时通信 | WebSocket |
| 定时任务 | Quartz |
| 前端框架 | Vue 2.6 + Vue Router + Vuex |
| 移动端 UI | Vant 2 |
| 管理端 UI | Element UI |
| 图表 | ECharts |
| AI 助手 | 阿里通义千问 |

## 功能模块

### 移动端（学生）
- 注册/登录
- 浏览图书馆及楼层，查看实时座位状态
- 按日期、时段预约座位
- 扫码签到/签退
- 我的预约记录（取消、申诉）
- 积分记录查看
- 反馈建议提交
- 通知公告查看

### 管理后台（图书馆管理员）
- 图书馆与楼层管理
- 座位管理（新增、编辑、禁用）
- 实时座位监控大屏（WebSocket 实时刷新）
- 预约记录管理与查询
- 扫码核验（扫码签到确认）
- 用户管理
- 公告与轮播图管理
- 申诉/投诉/纠纷处理
- 违规记录管理
- 数据分析与 Excel 导出

### 系统管理后台
- 全局用户管理
- 预约规则配置（时段、时长、提前预约天数等）
- 违规规则配置（扣分、禁用天数）
- 积分记录全局查看
- 操作日志审计

### AI 助手
- 基于通义千问的智能问答
- 辅助用户了解预约规则和使用帮助

## 快速开始

### 环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 8.0+
- Node.js 16+

### 1. 数据库初始化

```bash
mysql -u root -p < library_reservation.sql
```

默认数据库名为 `library_reservation`，默认用户名/密码为 `root` / `123456`。

### 2. 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端启动后访问地址：`http://localhost:8080/api`

### 3. 启动前端

```bash
cd frontend
npm install
npm run serve
```

前端开发服务器运行在 `http://localhost:3000`

### 4. 访问系统

| 端 | 地址 |
|----|------|
| 入口导航 | `http://localhost:3000` |
| 移动端 | `http://localhost:3000/mobile` |
| 管理后台 | `http://localhost:3000/admin` |
| 系统管理 | `http://localhost:3000/system-admin` |

## 项目结构

```
library-reservation/
├── backend/                    # Spring Boot 后端
│   ├── src/main/java/com/library/
│   │   ├── aspect/             # AOP 切面（操作日志）
│   │   ├── config/             # 配置（WebSocket、定时任务、跨域等）
│   │   ├── controller/         # 控制器层
│   │   ├── dto/                # 数据传输对象
│   │   ├── entity/             # 实体类
│   │   ├── mapper/             # MyBatis Mapper 接口
│   │   ├── service/            # 服务层接口与实现
│   │   ├── task/               # 定时任务
│   │   ├── utils/              # 工具类
│   │   └── vo/                 # 视图对象
│   └── src/main/resources/
│       ├── application.yml     # 应用配置
│       └── mapper/             # MyBatis XML 映射文件
├── frontend/                   # Vue 前端
│   └── src/
│       ├── api/                # API 接口封装
│       ├── components/         # 公共组件（AI 助手）
│       ├── router/             # 路由配置
│       ├── store/              # Vuex 状态管理
│       ├── utils/              # 工具函数
│       └── views/              # 页面视图
│           ├── mobile/         # 移动端页面
│           ├── admin/          # 管理后台页面
│           └── system-admin/   # 系统管理页面
├── e2e/                        # E2E 测试（Puppeteer）
└── library_reservation.sql     # 数据库初始化脚本
```

## 配置说明

主要配置文件为 `backend/src/main/resources/application.yml`：

- 数据库连接：修改 `spring.datasource.url`、`username`、`password`
- AI 助手：修改 `ai.api-key` 为你自己的通义千问 API Key
- 服务端口：默认 `8080`，可通过 `server.port` 修改
- 文件上传：默认最大 `10MB`

## License

MIT
