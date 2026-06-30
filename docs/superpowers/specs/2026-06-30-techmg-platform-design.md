# techmg 技术管理平台设计文档

**日期**: 2026-06-30
**版本**: v1.0
**状态**: 设计中

---

## 1. 项目概述

### 1.1 项目背景

工商银行上海研发基地内部技术管理平台，面向研发部提供技术治理、技术整改等内容的线上化管理。

### 1.2 项目目标

- 替代线下 Excel 驱动的治理流程，实现线上化闭环管理
- 统一管理基地内各应用的技术资产
- 提供多角色权限体系，支撑不同层级用户使用

---

## 2. 技术选型

| 维度 | 选型 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 3.2.3 |
| Spring Framework | Spring | 6.1.4 |
| 构建工具 | Maven（多模块） | 3.6+ |
| 前端框架 | Vue 3 | 3.3.9 |
| 构建工具 | Vite | 5.0.4 |
| UI 组件库 | Element Plus | 2.x |
| 状态管理 | Pinia | 2.1.4 |
| 路由 | Vue Router | 4.x |
| HTTP 客户端 | Axios | 1.7.2 |
| 图表库 | ECharts | 5.x |
| CSS 预处理器 | Sass | 1.66.1 |
| 数据库 | MySQL | 8.0 |
| 缓存 | Redis | 7.x |
| ORM | MyBatis-Plus | 3.5+ |
| 代码生成 | Lombok | 1.18+ |
| API 文档 | SpringDoc OpenAPI | 2.x |
| 安全框架 | Spring Security + JWT | 6.1.4 |
| Excel 处理 | Apache POI | 4.1.2 |
| 连接池 | Druid | 1.2+ |
| 动态数据源 | dynamic-datasource-spring-boot-starter | 4.x |
| 校验 | Jakarta Validation | 3.x |
| JSON 处理 | Gson | 2.10+ |
| 日志框架 | Log4j2 | 2.17.1 |

---

## 3. 后端架构

### 3.1 模块结构

```
techmg/
├── techmg-common/              # 公共模块（零业务依赖）
├── techmg-framework/           # 框架模块（横切关注点）
├── techmg-system/              # 系统管理模块（RBAC底座）
├── techmg-business-api/        # 业务接口定义（DTO、Service接口）
├── techmg-business-service/    # 业务接口实现
└── techmg-admin/               # 启动模块（Spring Boot入口，聚合装配）
```

### 3.2 模块职责

**techmg-common**：
- 基础注解（Excel注解、日志注解等）
- 常量/枚举（治理状态、数据库类型、返回码 `ResultCode`）
- **全局异常处理**：`GlobalExceptionHandler` + 自定义 `BusinessException`，统一异常拦截与返回
- **统一响应模型**：`R<T>` 通用响应体（code/message/data），所有接口统一包装
- **统一返回码**：`ResultCode` 枚举（SUCCESS=200, PARAM_ERROR=400, UNAUTHORIZED=401, FORBIDDEN=403, NOT_FOUND=404, INTERNAL_ERROR=500 等行内标准码）
- 工具类（树形工具、日期工具、字符串工具等）
- `BaseEntity`（id, createTime, createBy, updateTime, updateBy, deleted）

**techmg-framework**：
- `security/` — Spring Security 配置 + JWT 过滤器 + SSO 扩展预留接口
- `redis/` — RedisTemplate 配置、注解式缓存（@Cacheable等）、分布式锁
- `mybatis/` — MyBatis-Plus 分页插件、自动填充（创建人/时间）、逻辑删除
  - **SQL 编写规范**：简单 CRUD 使用 MyBatis-Plus API（selectById、selectList 等），复杂查询走 XML Mapper，**禁止使用注解方式写 SQL**（@Select、@Update 等）
- `datasource/` — 基于 `dynamic-datasource` 的多数据源配置（主库 + 从库），通过 `@DS` 注解声明式切换或 `DynamicDataSourceContextHolder` 编程式切换；Druid 连接池 + SQL 监控（StatFilter）+ 防火墙（WallFilter）
- `excel/` — Apache POI 4.1.2 封装（Workbook 构建器、模板下载、批量导入解析、数据校验、错误行导出）
- `web/` — 全局响应自动包装（ResponseBodyAdvice）、跨域配置、Gson 替换 Jackson（配置 HttpMessageConverter）
- `log/` — Log4j2 2.17.1 配置（排除默认 Logback），按日期+大小滚动拆分（%d{yyyy-MM-dd} + %i，单文件最大 100MB，保留 30 天），`@ApiAccessLog` 注解 + AOP 切面记录接口调用（IP、耗时、参数、返回值、异常）

**techmg-system**：用户管理、角色管理（5种角色）、权限管理（菜单+按钮）、菜单管理（动态路由）、部门管理（树形）、操作日志查询。

**techmg-business-api**：业务 Service 接口定义、DTO/VO 定义、Feign 接口（未来扩展）。

**techmg-business-service**：技术治理任务、数据库治理任务、数据库巡检任务、资产管理。

**techmg-admin**：Spring Boot 启动类、application-{dev,test,prod}.yml、SQL 初始化脚本。

### 3.3 分包规范

每个业务模块内部按层分包：
```
xxx/
├── controller/     # REST 接口
├── service/        # 业务逻辑
│   └── impl/       # 实现类
├── mapper/         # MyBatis-Plus Mapper
├── model/
│   ├── entity/     # 数据库实体
│   ├── dto/        # 请求/响应 DTO
│   └── vo/         # 视图对象
└── converter/      # 对象转换（MapStruct）
```

---

## 4. 前端架构

### 4.1 项目结构

```
techmg-web/
├── src/
│   ├── api/                     # API 请求层（按模块拆分）
│   ├── views/                   # 页面视图（按模块拆分）
│   ├── router/                  # 路由（动态路由 + 权限守卫）
│   ├── store/                   # Pinia 状态管理
│   ├── components/              # 公共组件
│   ├── layout/                  # 布局组件（上下布局 + 上左右布局，默认上左右）
│   ├── composables/             # 组合式函数
│   ├── utils/                   # 工具函数
│   ├── echarts/                 # ECharts 图表封装（通用图表组件、仪表盘图表）
│   └── styles/                  # 全局样式（Element Plus 主题变量覆盖、SCSS）
├── .env.development
├── .env.test
├── .env.production
├── vite.config.js
└── package.json
```

### 4.2 核心设计

- **多环境打包**：`.env.development` / `.env.test` / `.env.production`，`vite build --mode` 指定环境
- **请求封装**：axios 实例化 + 拦截器统一封装
  - **API 层封装**：每个业务模块导出具名函数（如 `getUserList(params)`、`downloadTemplate()`），页面直接 `import` 调用，无需手写 `axios.get/post`
  - **请求拦截器**：注入 JWT token（从 Pinia/localStorage 获取），无 token 则跳转登录
  - **响应拦截器**：统一解包 `R<T>`，code=200 返回 data；其他按返回码分类提示（401 跳登录、403 无权限、5xx 系统异常等）
  - **Blob 文件处理**：响应拦截器对 `responseType: 'blob'` 不做解包，直接返回二进制流，用于 Excel 导出、模板下载等场景
- **动态路由**：由后端菜单接口根据角色返回可见菜单，前端动态注册路由
- **权限控制**：路由守卫（beforeEach）+ 按钮级 v-permission 指令
- **Excel 三大组件**：批量导入（上传→预览→校验→提交）、模板下载、在线填报（可编辑表格）
- **Element Plus 主题定制**：覆盖 Element Plus 默认 SCSS 变量（主题色、圆角、间距等），统一行内 UI 风格
- **ECharts 图表**：封装通用图表组件，支持治理进度看板、部门统计报表、巡检趋势图等可视化场景
- **双布局模式**：
  - **上左右**（默认）：顶部主导航 + 左侧菜单树 + 右侧内容区，适用于治理任务等深层页面
  - **上下**：顶部导航栏 + 下方内容区，适用于报表仪表盘等宽屏展示
  - 用户可在设置中切换，偏好存储至 localStorage

---

## 5. 数据库设计

### 5.1 系统管理表

| 表名 | 说明 |
|------|------|
| `sys_user` | 用户表（auth_no 唯一索引，姓名、部门、状态） |
| `sys_role` | 角色表（游客/普通用户/平台管理员/部门管理员/部门DBA） |
| `sys_user_role` | 用户-角色关联表 |
| `sys_menu` | 菜单表（组件路径、图标、排序，支持多级） |
| `sys_role_menu` | 角色-菜单关联表 |
| `sys_dept` | 部门表（树形结构） |
| `sys_operation_log` | 操作日志表 |

### 5.2 业务表

| 表名 | 说明 |
|------|------|
| `tech_governance_task` | 技术治理任务主表 |
| `tech_governance_item` | 技术治理明细（应用、治理项、问题、修改版本、负责人） |
| `db_governance_task` | 数据库治理任务主表（MySQL/PolarDB/GaussDB） |
| `db_governance_item` | 数据库治理明细 |
| `db_inspection_task` | 数据库巡检任务主表 |
| `db_inspection_item` | 巡检问题明细 |
| `asset_application` | 应用资产主表 |
| `asset_param` | 应用参数表 |
| `asset_code` | 代码资产表 |
| `asset_procedure` | 存储过程表 |

---

## 6. Redis 缓存设计

| 缓存键 | 用途 | 过期 |
|--------|------|------|
| `token:{userId}` | JWT token 黑名单控制 | 登录有效期 |
| `perm:{roleId}` | 角色权限列表 | 30min |
| `menu:{roleId}` | 角色菜单树 | 30min |
| `dict:{dictType}` | 数据字典 | 1h |
| `excel:task:{taskId}` | Excel 导入异步进度 | 24h |

---

## 7. 角色权限体系

| 角色 | 权限范围 |
|------|----------|
| **游客** | 查看公开报表、治理情况概览（只读） |
| **普通用户** | 游客权限 + 填写反馈、确认治理任务、查看本部门数据 |
| **部门DBA** | 普通用户权限 + 数据库治理/巡检任务操作、数据库参数管理 |
| **部门管理员** | 部门DBA权限 + 本部门用户管理、本部门资产管理 |
| **平台管理员** | 全局管理：所有用户/角色/权限/菜单/部门配置 |

---

## 8. 部署环境

| 环境 | 数据库 | Redis | 端口 | 说明 |
|------|--------|-------|------|------|
| dev | MySQL 8 (本地) | Redis 6+ (本地) | 8080 / 5173 | 本地开发 |
| test | MySQL 8 (测试服务器) | Redis 7 (测试服务器) | 8080 | 联调测试 |
| prod | MySQL 8 (生产集群) | Redis 7 (哨兵) | 8080 | 上线环境 |

---

## 9. 模块依赖关系

```
techmg-admin
  ├── techmg-framework
  ├── techmg-system
  └── techmg-business-service
        └── techmg-business-api
  └── techmg-common  (所有模块的基座)

依赖传递规则：
- common 无任何业务依赖
- framework 依赖 common
- system 依赖 common + framework
- business-api 仅依赖 common
- business-service 依赖 common + framework + business-api
- admin 依赖所有模块（装配）
```
