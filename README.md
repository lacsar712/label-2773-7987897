# Employee Management System

## 项目简介
这是一个基于 Spring Boot 和 Vue 3 的全栈员工管理系统。该项目支持员工信息的增删改查（CRUD），并提供了现代化、响应式的用户界面。

## 技术栈

### Backend
- **Framework**: Spring Boot 3
- **Database**: MySQL 8.0
- **ORM**: MyBatis-Plus
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
├── backend/            # 后端 Spring Boot 项目
│   ├── init.sql        # 数据库初始化脚本
│   └── src/            # 后端源代码
├── frontend/           # 前端 Vue 项目
├── docker-compose.yml  # Docker 编排文件
└── README.md           # 项目说明文档
```

## 快速开始

### 前置要求
- Docker Desktop (或 Docker Engine + Docker Compose)

### 启动项目
1. 在项目根目录下运行以下命令构建并启动所有服务：
   ```bash
   docker compose up --build -d
   ```

2. 等待服务启动完成。你可以通过以下命令查看日志：
   ```bash
   docker compose logs -f
   ```

### 访问应用
- **前端页面**: [http://localhost:3000](http://localhost:3000)
- **后端 API**: [http://localhost:8000](http://localhost:8000)

## 功能特性
- **员工列表**: 展示员工信息，支持分页。
- **添加员工**: 弹出模态框填写员工信息，包含表单验证。
- **编辑员工**: 复用模态框编辑现有员工信息。
- **删除员工**: 带确认提示的删除操作。
- **响应式设计**: 适配桌面端和移动端，移动端表格支持横向滚动。
- **现代化 UI**: 采用渐变背景、卡片阴影、交互动画等现代设计元素。

## 注意事项
- 数据库初始化脚本位于 `backend/init.sql`，首次启动时会自动执行。
- 后端服务依赖数据库服务，Docker Compose 已配置健康检查 (`healthcheck`) 确保启动顺序。
