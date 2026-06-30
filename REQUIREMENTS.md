# 需求文档索引

技术管理平台 (techmg) 需求文档归档。

## 版本历史

| 版本 | 日期 | 状态 | 文档 |
|------|------|------|------|
| v1.0 | 2026-06-30 | 初版完成 | [docs/requirements/v1.0.md](docs/requirements/v1.0.md) |

## 文档结构

```
docs/
├── requirements/              # 需求文档 (版本化)
│   └── v1.0.md                #   初版: 6模块 26菜单 34API
├── superpowers/
│   ├── specs/                 #   设计规约
│   │   └── 2026-06-30-techmg-platform-design.md
│   └── plans/                 #   实施计划
│       └── 2026-06-30-techmg-scaffold.md
├── CLAUDE.md                  # 项目总纲
├── backend/CLAUDE.md          # 后端规约
└── vue-front/CLAUDE.md        # 前端规约
```

## v1.0 功能概要

- 6 大功能模块 (首页/技术管理/资产管理/生产管理/数智生态/设置)
- 26 个菜单项
- 5 种用户角色 (平台管理员/部门管理员/部门DBA/普通用户/游客)
- 34 个后端 API 端点
- 19 张数据库表
- JWT + Spring Security 6 认证
- 动态菜单路由 + 角色级权限控制
- 操作日志 / 数据字典 / 幂等性 / Trace ID / 定时任务
