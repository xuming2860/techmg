# Backend 规约 — techmg

Spring Boot 3.2.3 / Spring 6.1.4 / Maven / MyBatis-Plus 3.5.5 / JDK 17

## 模块架构

```
techmg-admin                  # 启动装配 (Spring Boot + Security + DataInit + 登录配置)
  ├── techmg-system           # RBAC: 用户/角色/菜单/部门/日志/字典
  ├── techmg-business-service # 业务: Health + Excel + 技改任务管理(父任务/子任务/治理条目)
  │     └── techmg-business-api   # 业务接口定义 (占位)
  └── techmg-framework        # 框架: Web/MyBatis/Security/Log/Excel (Redis已移除)
        └── techmg-common     # 公共: BaseEntity/R<T>/ResultCode/枚举/事件/TreeUtil
```

## API 清单 (57 端点)

### Auth (6) — v2.0 扩展
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/auth/login` | 登录 (mock 模式: 跳过密码返回配置用户; SSO 模式: ticket 验证) |
| POST | `/api/auth/logout` | 登出 |
| GET | `/api/auth/userinfo` | 当前用户信息 (含 AD/机构/NotesID 等扩展字段) |
| GET | `/api/auth/config` | 查询认证模式 {ssoEnabled, loginUrl} |
| GET | `/api/auth/sso/login-url` | SSO 登录地址 |
| POST | `/api/auth/sso/login` | SSO ticket 登录 |

### 用户管理 (7)
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/system/user/list` | 分页列表 (keyword搜索) |
| GET/POST/PUT/DELETE | `/api/system/user` `/{id}` | CRUD |
| POST/GET | `/api/system/user/{userId}/roles` | 分配/查询角色 |

### 角色管理 (7)
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/system/role/list` | 分页列表 |
| GET/POST/PUT/DELETE | `/api/system/role` `/{id}` | CRUD |
| GET/POST | `/api/system/role/{roleId}/menus` | 查询/分配菜单 |

### 菜单管理 (6)
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/system/menu/tree` | 全部菜单树 |
| GET | `/api/system/menu/user-tree` | 按角色过滤的用户菜单树 |
| GET/POST/PUT/DELETE | `/api/system/menu` `/{id}` | CRUD |

### 部门管理 (5)
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/system/dept/tree` | 部门树 |
| GET/POST/PUT/DELETE | `/api/system/dept` `/{id}` | CRUD |

### 操作日志 (2)
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/system/operation-log/list` | 分页列表 (keyword/模块/时间范围) |
| GET | `/api/system/operation-log/{id}` | 详情 |

### 数据字典 (8) — v2.0: @Cacheable/@CacheEvict 已移除，直读 DB
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/system/dict/type/list` | 字典类型列表 |
| POST/GET/PUT/DELETE | `/api/system/dict/type` `/{id}` | CRUD |
| GET | `/api/system/dict/data/{dictType}` | 按类型获取 |
| POST/GET/PUT/DELETE | `/api/system/dict/data` `/{id}` | CRUD |

### 技改任务管理 (19) — v2.0 新增

#### 父任务 (6)
| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/tech-reform/task/list` | 分页+筛选 | 登录用户 |
| GET | `/api/tech-reform/task/{id}` | 详情 | 登录用户 |
| POST | `/api/tech-reform/task` | 新增 | PLATFORM_ADMIN |
| PUT | `/api/tech-reform/task/{id}` | 编辑 | PLATFORM_ADMIN |
| PUT | `/api/tech-reform/task/{id}/status` | 启停 | PLATFORM_ADMIN |
| DELETE | `/api/tech-reform/task/{id}` | 逻辑删除 | PLATFORM_ADMIN |

#### 子任务 (6)
| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/tech-reform/subtask/list` | 分页+筛选 | 登录用户 |
| GET | `/api/tech-reform/subtask/{id}` | 详情 | 登录用户 |
| POST | `/api/tech-reform/subtask` | 新增 | ADMIN/DEPT_ADMIN |
| PUT | `/api/tech-reform/subtask/{id}` | 编辑 | ADMIN/DEPT_ADMIN |
| PUT | `/api/tech-reform/subtask/{id}/status` | 状态流转 | ADMIN/DEPT_ADMIN |
| DELETE | `/api/tech-reform/subtask/{id}` | 删除 | PLATFORM_ADMIN |

#### 治理条目 (7)
| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/tech-reform/item/list` | 分页+部门过滤 | 登录用户 |
| POST | `/api/tech-reform/item` | 新增 | ADMIN/DEPT_ADMIN/DEPT_DBA |
| PUT | `/api/tech-reform/item/{id}` | 更新 | ADMIN/DEPT_ADMIN/DEPT_DBA |
| DELETE | `/api/tech-reform/item/{id}` | 删除 | PLATFORM_ADMIN |
| POST | `/api/tech-reform/item/upload` | 批量导入(覆盖/Merge) | ADMIN/DEPT_ADMIN |
| GET | `/api/tech-reform/item/export` | 导出 Excel | 登录用户 |
| POST | `/api/tech-reform/item/batch-update` | 批量登记上传 | ADMIN/DEPT_ADMIN/DEPT_DBA |

### 其他 (2)
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/health` | 健康检查 (无需认证) |
| GET | `/api/excel/template/{type}` | Excel 模板下载 |

## 框架组件

| 组件 | 路径 | 说明 |
|------|------|------|
| WebConfig | framework/web/ | CORS + ResponseBodyAdvice (幂等拦截器已移除) |
| GsonConfig | framework/web/ | extendMessageConverters |
| MyBatisPlusConfig | framework/mybatis/ | 分页 |
| MyMetaObjectHandler | framework/mybatis/ | 自动填充 |
| JwtTokenProvider | framework/security/ | JWT 生成/验证 (jjwt 0.12.3) |
| JwtAuthenticationFilter | framework/security/ | OncePerRequestFilter |
| SsoAuthProvider | framework/security/ | SSO 接口 (mock 实现待替换) |
| ExcelUtil | framework/excel/ | POI 4.1.2 |
| ApiAccessLog | framework/log/ | AOP 结构化日志 + Spring Event 写库 |
| TraceIdFilter | framework/web/ | 请求链路追踪 |
| SecurityConfig | admin/config/ | 无状态JWT, `/api/auth/**` 放行 |
| DataInitializer | admin/config/ | 启动创建 admin 用户 |
| LoginMockProperties | admin/config/ | Mock 登录配置 (sso.enabled=false 时生效) |

## 公共模块 (common)

| 组件 | 说明 |
|------|------|
| BaseEntity | 抽象基类 |
| R\<T\> | 统一响应体 |
| ResultCode | 返回码枚举 |
| BusinessException | 业务异常 |
| GlobalExceptionHandler | 全局异常 (含 Security 异常) |
| OperationLogEvent | 操作日志 Spring Event |
| TreeUtil | 泛型树形构建器 |
| ReformStatus | 治理状态枚举 (PENDING/IN_PROGRESS/COMPLETED/CLOSED) |
| GovernanceStatus | 治理状态枚举 |
| DbTypeEnum | 数据库类型枚举 |
| InspectionTypeEnum | 巡检类型枚举 |

## 12 条铁律 (v2.0 修订)

1. 配置 `.yml`, 禁止 `.properties`
2. 简单CRUD用API, 复杂SQL用XML Mapper, 禁止注解SQL
3. Gson `extendMessageConverters`
4. 所有接口 → `R<T>`
5. 抛 `BusinessException` → GlobalExceptionHandler
6. `@ApiAccessLog` → Spring Event → DB
7. `@DS("master")` 默认, `@DS("slave")` 从库读
8. 逻辑删除 `deleted`, `@TableLogic`
9. 自动填充 createTime/createBy/updateTime/updateBy
10. Log4j2 2.17.1, 排除 Logback
11. Security 无状态JWT, `/api/auth/**` 放行
12. **`@RequestParam` / `@PathVariable` 必须显式 `name`/`value`**（PolarDB 环境不支持 `-parameters`）
13. BCrypt 密码（已移除 spring-boot-starter-data-redis，待行内 NOS 封装后恢复）

## 登录模式

| 配置 | 行为 |
|------|------|
| `sso.enabled: false` | Mock 自动登录 — 前端无感知，后端返回 `login.mock` 配置的用户 |
| `sso.enabled: true` | SSO 统一认证 — ticket 验证 → 外部 API 查用户 → JWT |

Mock 配置示例 (`application.yml`):
```yaml
sso:
  enabled: false
login:
  mock:
    auth-no: "admin"
    real-name: "平台管理员"
    branch-id: "12092342"
    branch-name: "上海技术部"
    roles:
      - "ROLE_PLATFORM_ADMIN"
```

## 数据库 (tmvp) — PolarDB

- **23 表**: 9 系统表 + 10 业务表 + 4 v2 新表 (`sys_user_branch`, `tech_reform_task/subtask/item`)
- 5 角色: PLATFORM_ADMIN/DEPT_ADMIN/DEPT_DBA/NORMAL_USER/GUEST
- 25 菜单: 首页 / 技术管理(8) / 资产管理(4) / 生产管理(1) / 数智生态(4) / 设置(3)
- utf8mb4 + Druid `connection-init-sqls`
- 数据源: `jdbc:mysql://47.99.179.180:33066/tmvp` (PolarDB)

## 构建

```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home
cd backend && mvn clean compile && mvn spring-boot:run -pl techmg-admin
```
