# 图书馆预约系统 - 自动化测试指南

## 项目概述

本测试套件为图书馆座位预约系统提供完整的自动化测试覆盖，包括：

- **后端 API 测试**：JUnit 5 + Spring Boot Test + MockMvc + H2 内存数据库
- **前端 E2E 测试**：Puppeteer + Node.js，模拟移动端学生用户操作，自动截图

## 目录结构

```
├── backend/
│   ├── pom.xml                          # 已添加 H2 测试依赖
│   └── src/
│       └── test/
│           ├── java/com/library/
│           │   ├── LibraryApplicationTests.java        # 上下文启动测试
│           │   └── controller/
│           │       ├── AuthControllerTest.java          # 登录/注册/登出 (9 用例)
│           │       ├── LibraryControllerTest.java       # 图书馆查询 (5 用例)
│           │       ├── SeatControllerTest.java          # 座位查询 (6 用例)
│           │       └── ReservationControllerTest.java   # 预约操作 (9 用例)
│           └── resources/
│               ├── application-test.yml   # 测试环境配置 (H2)
│               ├── schema.sql             # 建表语句 (H2 兼容)
│               └── data.sql               # 测试种子数据
├── e2e/
│   ├── config.js                          # E2E 配置 (URL, 用户凭据, 超时等)
│   ├── helpers.js                         # 辅助函数 (截图, 登录, 等待)
│   ├── run.js                             # 测试运行器 + HTML 报告生成
│   ├── report.html                        # 生成的测试报告 (运行后)
│   ├── screenshots/                       # 测试截图目录
│   └── tests/
│       ├── login.test.js                  # 登录流程 (4 用例)
│       ├── register.test.js               # 注册流程 (3 用例)
│       ├── reservation.test.js            # 预约核心流程 (6 用例)
│       └── my-reservations.test.js        # 我的预约 (5 用例)
└── TEST_GUIDE.md                          # 本文件
```

---

## 一、后端 API 测试

### 环境要求

| 组件 | 版本 |
|------|------|
| JDK | 1.8+ |
| Maven | 3.6+ |

### 测试配置

测试使用 **H2 内存数据库**（MODE=MySQL），无需本地 MySQL 服务。配置文件位于 `backend/src/test/resources/application-test.yml`。

### 运行测试

```bash
# 运行所有测试
cd backend
mvn test

# 运行指定测试类
mvn test -Dtest=AuthControllerTest

# 运行多个测试类
mvn test -Dtest=AuthControllerTest,ReservationControllerTest
```

### 测试用例清单

#### AuthControllerTest (9 用例)

| # | 测试用例 | 预期结果 |
|---|---------|---------|
| 1 | 登录成功 - 学生用户 | code=200, 返回用户信息(无密码) |
| 2 | 登录失败 - 错误密码 | code=500, 密码错误 |
| 3 | 登录失败 - 不存在用户 | code=500, 用户不存在 |
| 4 | 注册成功 | code=200, 新用户创建 |
| 5 | 注册失败 - 用户名已存在 | code=500 |
| 6 | 获取当前用户 - 已登录 | code=200, 返回用户信息 |
| 7 | 获取当前用户 - 未登录 | code=401 |
| 8 | 登录成功 - 管理员 | code=200, userType=图书馆管理员 |
| 9 | 登出成功 | code=200 |

#### ReservationControllerTest (9 用例)

| # | 测试用例 | 预期结果 |
|---|---------|---------|
| 1 | 创建预约 - 未登录 | code=401 |
| 2 | 创建预约 - 管理员无权限 | code=403 |
| 3 | 创建预约 - 学生成功 | code=200, 返回 orderNo |
| 4 | 查询我的预约列表 | code=200, 分页结果 |
| 5 | 查询预约详情(有效订单号) | code=200 |
| 6 | 查询预约详情(不存在) | code=500 |
| 7 | 取消预约 - 未登录 | code=401 |
| 8 | 取消预约 - 学生取消自己 | code=200 |
| 9 | 管理员查询所有预约 | code=200 |

#### LibraryControllerTest (5 用例)

| # | 测试用例 | 预期结果 |
|---|---------|---------|
| 1 | 获取图书馆列表 | code=200, 3个图书馆 |
| 2 | 根据ID获取图书馆 | code=200 |
| 3 | 获取不存在图书馆 | code=500 |
| 4 | 分页查询 - 学生无权限 | code=403 |
| 5 | 分页查询 - 管理员 | code=200 |

#### SeatControllerTest (6 用例)

| # | 测试用例 | 预期结果 |
|---|---------|---------|
| 1 | 获取图书馆座位列表 | code=200, 4个座位 |
| 2 | 获取可用座位 | code=200 |
| 3 | 根据ID获取座位 | code=200 |
| 4 | 获取不存在座位 | code=500 |
| 5 | 分页查询 - 无权限 | code=403 |
| 6 | 分页查询 - 管理员 | code=200 |

---

## 二、前端 E2E 测试

### 环境要求

| 组件 | 版本 |
|------|------|
| Node.js | 18.x+ |
| Puppeteer | 24.x (已安装于根目录 node_modules) |
| Chrome/Chromium | Puppeteer 自动下载 |

### 前置条件

测试依赖前端开发服务器和后端 API 服务运行：

```bash
# 终端 1: 启动后端 (Spring Boot)
cd backend
mvn spring-boot:run

# 终端 2: 启动前端 (Vue CLI)
cd frontend
npm run serve
```

验证服务状态：
- 前端: `curl http://localhost:3000` → 200
- 后端: `curl http://localhost:8080/api/auth/current` → 200

### 运行测试

```bash
# 从项目根目录运行
cd e2e
node run.js
```

### 测试输出

- **控制台**：每个测试用例的 PASS/FAIL 结果
- **截图**：保存在 `e2e/screenshots/` 目录
- **HTML 报告**：运行后生成 `e2e/report.html`（内嵌截图，点击可放大查看）

### 测试用例清单 (18 用例)

#### 登录流程 (4 用例)

| # | 测试用例 | 截图 |
|---|---------|------|
| 1 | 登录页面加载 | `01-login-page.png` |
| 2 | 错误密码登录拦截 | `02-login-error.png` |
| 3 | 正确登录跳转首页 | `03-login-form-filled.png`, `04-login-success.png` |
| 4 | 首页导航栏显示 | - |

#### 注册流程 (3 用例)

| # | 测试用例 | 截图 |
|---|---------|------|
| 1 | 注册页面表单加载 | `05-register-page.png` |
| 2 | 注册表单填写 | `06-register-form-filled.png` |
| 3 | 注册提交并跳转 | `07-register-result.png` |

#### 预约流程 (6 用例)

| # | 测试用例 | 截图 |
|---|---------|------|
| 1 | 首页三步向导显示 | `08-step0-initial.png` |
| 2 | 选择预约日期时间 | `09-step0-date-selected.png` |
| 3 | 进入图书馆选择页 | `10-step1-library-list.png` |
| 4 | 选择图书馆进入座位页 | `11-step2-seat-grid.png` |
| 5 | 座位布局加载 | - |
| 6 | 选择座位并确认预约 | `12-step2-seat-selected.png`, `13-reservation-confirmed.png` |

#### 我的预约 (5 用例)

| # | 测试用例 | 截图 |
|---|---------|------|
| 1 | 我的预约页面加载 | `14-my-reservations.png` |
| 2 | 状态Tab切换 | `15-my-reservations-tab-reserved.png` |
| 3 | 二维码查看 | `16-qrcode-page.png` |
| 4 | 取消预约操作 | `17-cancel-dialog.png`, `18-cancel-confirmed.png` |
| 5 | 底部导航栏显示 | - |

---

## 三、测试配置说明

### 后端测试用户凭据

测试数据通过 `data.sql` 预置，使用 MD5 加密：

| 用户名 | 密码 | 类型 | 积分 |
|--------|------|------|------|
| admin | admin123 | 图书馆管理员 | - |
| student001 | 123456 | 学生 | 80 (良好) |
| system_admin | 123456 | 系统管理员 | - |

### E2E 测试用户

配置在 `e2e/config.js` 中，默认使用 `wangwu / 123456`（学生用户）。

### 修改测试配置

- **后端**：编辑 `backend/src/test/resources/application-test.yml`
- **前端 E2E**：编辑 `e2e/config.js`（修改 `baseUrl`, `testUser`, `viewport` 等）

---

## 四、常见问题

### 后端测试失败

1. **H2 数据库兼容性**：确保 `pom.xml` 中 H2 依赖在 test scope 中存在
2. **Mapper XML 路径**：确保 `classpath*:/mapper/**/*.xml` 指向正确
3. **Quartz/WebSocket 冲突**：测试使用 `@ActiveProfiles("test")` 隔离

### E2E 测试失败

1. **浏览器启动失败**：运行 `npx puppeteer install` 安装 Chromium
2. **页面 404**：确保前端 dev server (`npm run serve`) 在 3000 端口运行
3. **API 调用 401**：确保后端服务在 8080 端口运行
4. **截图全白/黑**：检查 `headless` 模式和 viewport 配置

### 调试 E2E 测试

- 将 `e2e/config.js` 中的 `headless: 'new'` 改为 `headless: false` 查看浏览器操作
- 增加 `sleep()` 等待时间以适应慢速网络
- 查看截图目录下的 PNG 文件了解页面状态

---

## 五、测试报告示例

运行 `node e2e/run.js` 后生成的 `e2e/report.html` 包含：

- 测试摘要卡片（总数、通过、失败、通过率）
- 测试用例结果表格
- 所有截图缩略图（点击可放大）

用浏览器直接打开 `e2e/report.html` 即可查看。

---

*测试文档生成时间: 2026-06-28*
