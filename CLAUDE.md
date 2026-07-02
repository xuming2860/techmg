# CLAUDE.md — techmg

技术管理平台，工商银行上海研发基地内部系统。提供技术治理、资产管理、数智生态等线上化管理。

## 项目结构

```
techmg/
├── CLAUDE.md              # 本文件 — 总纲（跨端全局约束 + 索引）
├── backend/               # Spring Boot 3.2.3 多模块 Maven
│   └── CLAUDE.md          # 后端规约（架构分层、API、测试、SQL 等）
├── vue-front/             # Vue 3.3.9 + Vite 5.0.4
│   └── CLAUDE.md          # 前端规约（编码规范、Element Plus、核心设计）
├── techmgdb/              # 数据库变更 SQL（独立于后端代码）
│   └── polardb/
│       ├── README.md       # SQL 命名与版本管理规约
│       ├── v1.0/{ddl,dml,rollback}/
│       └── v2.0/{ddl,dml,rollback}/
├── docs/                 # 需求文档（版本化）+ 设计文档
│   ├── requirements/
│   └── superpowers/
└── scripts/              # 开发调试脚本
    ├── lib/common.sh     # 公共变量与环境检查
    ├── start-backend.sh  # 启动后端
    ├── start-frontend.sh # 启动前端
    ├── start-all.sh      # 一键启动
    ├── stop-all.sh       # 停止开发服务
    ├── check-status.sh   # 检查服务状态
    ├── build-backend.sh  # 构建后端
    └── build-frontend.sh # 构建前端
```

## 版本演进

| 版本 | 日期 | 内容 |
|------|------|------|
| v1.0 | 2025-06 | 平台初版: RBAC + 菜单 + 技术治理占位 |
| **v2.0** | **2026-06** | **登录改造 + ApiAccessLog 优化 + 技改任务管理** |

## 技术栈（精确版本）

| 维度 | 选型 | 版本 |
|------|------|------|
| 后端 | Spring Boot / Spring | 3.2.3 / 6.1.4 |
| JDK | Java 17 | Maven 多模块 |
| ORM | MyBatis-Plus | 3.5.5 |
| DB | PolarDB (MySQL 兼容) | 标准版 |
| 连接池 | Druid | 1.2.20 |
| 缓存 | 已移除 (待行内 NOS) | — |
| JSON | Gson | 2.10.1 |
| 日志 | Log4j2 | 2.17.1 |
| 安全 | Spring Security 6 + JWT | jjwt 0.12.3 |
| 前端 | Vue 3 / Vite | 3.3.9 / 5.0.4 |
| UI | Element Plus | 2.5.0 |
| CSS | Sass | 1.66.1 |

## 功能模块

```
首页          — Dashboard 工作台, 上下布局
技术管理       — 技改任务管理 ✅ / 数据库管理 / 技术栈管理
资产管理       — 应用资产健康度 / 应用信息管理 / 基地研发范式管理
生产管理       — 生产管理 (占位)
数智生态       — 一级领域 / 二级领域 / 详细设计审核 / 代码评审任务
设置           — 菜单管理 / 角色管理 / 用户管理 (仅平台管理员)
```

## 数据库连接

- 库名: `tmvp` / 用户: `techmg` / 密码: `Tech@123++**`
- PolarDB 标准版: `47.99.179.180:33066`
- 字符集: utf8mb4 + Druid `connection-init-sqls`
- 表结构详见 [backend/CLAUDE.md](backend/CLAUDE.md)

## 跨端全局约束

> 仅包含同时约束前端和后端的规则。各端专属规约见对应 CLAUDE.md。

| # | 约束 | 说明 |
|----|------|------|
| 1 | **配置格式** | 后端 `.yml`（禁止 `.properties`），前端 `.env.{environment}` |
| 2 | **统一响应** | 所有 API → `R<T>` (code/message/data) |
| 3 | **跨域** | 后端 CORS + Vite proxy |
| 4 | **字符集** | MySQL 客户端 `--default-character-set=utf8mb4` |
| 5 | **SQL 变更管理** | DDL/DML/Rollback 分离，按版本放入 `techmgdb/polardb/{version}/`，禁止嵌入后端代码 |
| 6 | **测试覆盖率** | 业务代码行覆盖率 ≥ 90%（端各自实现：后端 JaCoCo + Sonar，前端 Vitest） |

## 详细规约索引

| 文档 | 内容 |
|------|------|
| [backend/CLAUDE.md](backend/CLAUDE.md) | 模块架构+企业级分层体系、57 API、框架组件、架构分层规约、17 条铁律、PolarDB SQL 规约、测试与代码质量、构建 |
| [vue-front/CLAUDE.md](vue-front/CLAUDE.md) | 功能模块+页面清单、编码规约（10节+Element Plus专项）、核心设计、构建 |
| [techmgdb/polardb/README.md](techmgdb/polardb/README.md) | SQL 命名规范、版本管理规则 |
| [docs/superpowers/specs/2026-06-30-v2-design.md](docs/superpowers/specs/2026-06-30-v2-design.md) | v2.0 设计文档 |

---

## 开发脚本

常用开发/调试命令已沉淀到 `scripts/` 目录：

```bash
./scripts/start-backend.sh   # 启动后端（http://localhost:8080）
./scripts/start-frontend.sh  # 启动前端（http://localhost:5173）
./scripts/start-all.sh       # 一键启动后端+前端
./scripts/stop-all.sh        # 停止占用 8080/5173 的开发服务
./scripts/check-status.sh    # 检查前后端运行状态
./scripts/build-backend.sh   # 全量构建后端（-DskipTests）
./scripts/build-frontend.sh  # 构建前端测试包
```

详见 [scripts/README.md](scripts/README.md)。

---

## Git 协作与提交规约

### Commit Message 格式（强制）

提交信息必须符合以下格式：
```
<type>(<scope>): <subject>
```
- `feat`: 新功能
- `fix`: Bug修复
- `docs`: 文档修改
- `refactor`: 重构（不改变功能）
- `perf`: 性能优化
- `test`: 测试用例
- 示例：`feat(user): 增加用户积分过期自动扣减逻辑`

### 分支策略

- 新功能开发必须基于 `dev` 分支拉取 `feature/xxx` 分支，禁止直接在 `main` 或 `dev` 上开发
- 修复 Bug 从 `dev` 拉取 `fix/xxx` 分支

---

## 配置与环境管理规约

### 环境隔离（强制）

- 配置文件必须拆分为 `application-dev.yml` / `application-test.yml` / `application-prod.yml`
- 激活环境必须通过 `spring.profiles.active` 指定，禁止硬编码环境

### 敏感配置（强制）

- 数据库密码、密钥等禁止写在配置文件中，必须使用环境变量占位符 `${ENV_VAR}`
- CI/CD 中通过环境变量注入真实值

---

## AI 交互元规约

> 以下规则约束 AI 自身行为，确保生成代码的可用性。

| # | 规则 | 说明 |
|----|------|------|
| 1 | **歧义澄清** | 需求模糊时，必须先向用户提问澄清，禁止擅自臆造业务逻辑 |
| 2 | **变更影响分析** | 修改现有代码时，必须扫描引用点并提示影响范围 |
| 3 | **拒绝过度设计** | 简单 CRUD 禁止引入 DDD/复杂设计模式，保持简单直接 |

### AI 全局检查清单

- [ ] Commit Message 是否符合 `feat/fix/docs` 格式？
- [ ] 敏感密码是否使用 `${ENV}` 占位符而非硬编码？
- [ ] 遇到模糊需求时，是否主动向用户确认而非自行猜测？
- [ ] 修改代码前，是否扫描了所有引用点并评估了影响？
