# CLAUDE.md — techmg

技术管理平台，工商银行上海研发基地内部系统。提供技术治理、资产管理、数智生态等线上化管理。

## 项目结构

```
techmg/
├── CLAUDE.md              # 本文件 — 总纲
├── backend/               # Spring Boot 3.2.3 多模块 Maven
│   └── CLAUDE.md          # 后端规约
├── vue-front/             # Vue 3.3.9 + Vite 5.0.4
│   └── CLAUDE.md          # 前端规约
└── docs/
    ├── requirements/       # 需求文档 (版本化)
    │   └── v1.0.md         #   v1.0 需求文档
    └── superpowers/        # 设计文档 + 实施计划
```

## 版本演进

| 版本 | 日期 | 内容 |
|------|------|------|
| v1.0 | 2025-06 | 平台初版: RBAC + 菜单 + 技术治理占位 |
| **v2.0** | **2026-06** | **登录改造 + ApiAccessLog 优化 + 技改任务管理** |

## 技术栈 (精确版本)

| 维度 | 选型 | 版本 |
|------|------|------|
| 后端 | Spring Boot / Spring | 3.2.3 / 6.1.4 |
| JDK | Java 17 | Maven 多模块 |
| ORM | MyBatis-Plus | 3.5.5 |
| DB | PolarDB (MySQL 兼容) | tmvp |
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

## 数据库

- 库名: `tmvp` / 用户: `techmg` / 密码: `Tech@123++**`
- **23 表** (9 系统表 + 10 业务表 + 4 v2 新表)
- PolarDB: `47.99.179.180:33066`
- utf8mb4 + Druid `connection-init-sqls`

## v2.0 新增表

| 表 | 说明 |
|------|------|
| `sys_user_branch` | 用户多机构关联 |
| `tech_reform_task` | 技改父任务 |
| `tech_reform_subtask` | 技改子任务 |
| `tech_reform_item` | 治理清单条目 |

## 全局约束

1. Java 17+ / 包名 `com.icbc.sh.techmg`
2. 配置 `.yml` (禁止 `.properties`), 前端 `.env.{environment}`
3. 所有 API → `R<T>` (code/message/data)
4. MyBatis: 禁止 `@Select/@Update/@Insert/@Delete` 注解
5. JSON: Gson, `extendMessageConverters` (保留 String 转换器)
6. 日志: Log4j2 2.17.1, 排除 Logback
7. **`@RequestParam` / `@PathVariable` 必须显式 `name`/`value`** (PolarDB 环境不支持 `-parameters`)
8. MySQL: 客户端 `--default-character-set=utf8mb4`
9. 跨模块通信: Spring Event 解耦 (参考 OperationLogEvent)
10. 跨域: 后端 CORS + Vite proxy

## 详细规约

| 文档 | 内容 |
|------|------|
| [backend/CLAUDE.md](backend/CLAUDE.md) | 模块架构、57 API、框架组件、13 铁律、数据库 |
| [vue-front/CLAUDE.md](vue-front/CLAUDE.md) | 页面清单、登录流程、路由守卫、布局设计、API层 |
| [docs/superpowers/specs/2026-06-30-v2-design.md](docs/superpowers/specs/2026-06-30-v2-design.md) | v2.0 设计文档 |

## 详细规约

| 文档 | 内容 |
|------|------|
| [backend/CLAUDE.md](backend/CLAUDE.md) | 模块架构、API清单、框架组件、12铁律、包结构、数据库 |
| [vue-front/CLAUDE.md](vue-front/CLAUDE.md) | 页面清单、路由系统、布局设计、API层、构建命令 |
