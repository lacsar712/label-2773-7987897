# Employee Management System

## 项目简介
这是一个基于 Spring Boot 和 Vue 3 的全栈员工管理系统。该项目支持员工信息的增删改查（CRUD），并提供了现代化、响应式的用户界面。

## 技术栈

### Backend
- **Framework**: Spring Boot 3
- **Database**: MySQL 8.0
- **ORM**: MyBatis-Plus
- **Migration**: Flyway
- **Build Tool**: Maven

### Frontend
- **Framework**: Vue 3 (Composition API)
- **UI Library**: Ant Design Vue
- **State Management**: Pinia
- **Build Tool**: Vite

### Infrastructure
- **Containerization**: Docker & Docker Compose

## 目录结构
```
.
├── backend/                                  # 后端 Spring Boot 项目
│   ├── src/
│   │   └── main/
│   │       ├── java/com/example/employee/
│   │       └── resources/
│   │           └── db/migration/             # Flyway 数据库迁移脚本
│   │               ├── V1__init_schema.sql   # 建表脚本
│   │               └── V1.1__seed_data.sql   # 种子数据
│   ├── Dockerfile
│   └── pom.xml
├── frontend/                                 # 前端 Vue 项目
│   ├── Dockerfile
│   └── vite.config.ts
├── docker-compose.yml                        # 生产环境 Docker 编排
├── docker-compose.dev.yml                    # 开发环境 Docker 编排（热重载）
└── README.md                                 # 项目说明文档
```

## 快速开始

### 前置要求
- Docker Desktop (或 Docker Engine + Docker Compose)

### 启动项目

#### 方式一：生产环境（推荐部署使用）
1. 在项目根目录下运行以下命令构建并启动所有服务：
   ```bash
   docker compose up --build -d
   ```

2. 等待服务启动完成。你可以通过以下命令查看日志：
   ```bash
   docker compose logs -f
   ```

#### 方式二：开发环境（源码挂载 + 热重载）
开发模式下，后端和前端的源码目录会被挂载到容器中，修改代码后会自动重新编译/刷新：

1. 启动开发环境：
   ```bash
   docker compose -f docker-compose.dev.yml up
   ```

2. 首次启动会自动安装 Maven 依赖和 npm 依赖，需要等待几分钟。

3. 开发模式特性：
   - 后端：Spring Boot DevTools 自动热重启（修改 Java 文件后自动编译重启）
   - 前端：Vite HMR 热模块替换（修改 Vue/TS 文件后浏览器自动刷新）
   - 数据库：独立的开发环境数据卷 `db_dev_data`，不与生产环境冲突

4. 停止并清理开发环境（保留数据）：
   ```bash
   docker compose -f docker-compose.dev.yml down
   ```

5. 彻底清理（包含数据库数据）：
   ```bash
   docker compose -f docker-compose.dev.yml down -v
   ```

### 访问应用
- **前端页面**: [http://localhost:3000](http://localhost:3000)
- **后端 API**: [http://localhost:8000](http://localhost:8000)
- **Swagger API 文档**: [http://localhost:8000/docs](http://localhost:8000/docs)

## 数据库迁移（Flyway）

本项目使用 Flyway 管理数据库版本迁移。

### 迁移文件位置
所有迁移脚本位于 `backend/src/main/resources/db/migration/` 目录下。

### 命名规范
Flyway 迁移文件命名格式：
```
V<版本号>__<描述>.sql
```
示例：
- `V1__init_schema.sql` - 初始化数据库表结构
- `V1.1__seed_data.sql` - 插入种子数据
- `V2__add_new_table.sql` - 版本 2 新增表

### 执行方式

#### 1. 应用启动自动执行
Spring Boot 启动时会自动检测并执行未执行的迁移脚本，无需手动干预。

#### 2. Maven 命令手动执行
在 `backend/` 目录下执行：

```bash
# 查看迁移状态（显示已执行和待执行的迁移）
mvn flyway:info

# 执行所有待执行的迁移
mvn flyway:migrate

# 验证迁移脚本是否完整（校验 checksum）
mvn flyway:validate

# 清空数据库（危险操作，仅限开发环境）
mvn flyway:clean

# 修复 flyway_schema_history 表（解决 checksum 不匹配等问题）
mvn flyway:repair
```

> 注意：执行 Maven Flyway 命令前，需要确保数据库服务已启动并可访问。
> 如果使用 Docker 启动数据库，可执行：
> ```bash
> docker compose up -d db
> ```

### 迁移流程
1. 首次启动时，Flyway 会自动创建 `flyway_schema_history` 表用于记录迁移历史
2. 按版本号顺序执行 `db/migration/` 下所有未执行的 SQL 脚本
3. 执行成功后，记录会写入 `flyway_schema_history` 表
4. 后续启动时，只会执行版本号大于已记录最大版本的新脚本

### 新增迁移的最佳实践
1. 不要修改已执行的迁移文件（会导致 checksum 校验失败）
2. 如需修改，新建一个更高版本的迁移脚本
3. 开发阶段如确需修改历史脚本，可先执行 `mvn flyway:clean` 清理后重新迁移

## 本地开发（不使用 Docker）

### 后端本地启动
1. 确保本地 JDK 17 和 Maven 已安装
2. 确保本地 MySQL 8.0 已启动，并创建数据库：
   ```sql
   CREATE DATABASE IF NOT EXISTS employee_db DEFAULT CHARACTER SET utf8mb4;
   ```
3. 修改 `backend/src/main/resources/application.yml` 中的数据库连接配置，指向本地 MySQL
4. 在 `backend/` 目录下执行：
   ```bash
   mvn spring-boot:run
   ```

### 前端本地启动
1. 确保本地 Node.js 20+ 已安装
2. 在 `frontend/` 目录下执行：
   ```bash
   npm install
   npm run dev
   ```
3. 前端默认通过 Vite 代理 `/api` 请求到 `http://localhost:8000`

## 功能特性
- **员工列表**: 展示员工信息，支持分页。
- **添加员工**: 弹出模态框填写员工信息，包含表单验证。
- **编辑员工**: 复用模态框编辑现有员工信息。
- **删除员工**: 带确认提示的删除操作。
- **响应式设计**: 适配桌面端和移动端，移动端表格支持横向滚动。
- **现代化 UI**: 采用渐变背景、卡片阴影、交互动画等现代设计元素。

## 注意事项
- 数据库迁移由 Flyway 统一管理，启动 Spring Boot 应用时会自动执行迁移。
- 后端服务依赖数据库服务，Docker Compose 已配置健康检查（`healthcheck`）确保启动顺序。
- 开发环境使用 `docker-compose.dev.yml`，生产环境使用默认的 `docker-compose.yml`，两者数据卷相互独立。
