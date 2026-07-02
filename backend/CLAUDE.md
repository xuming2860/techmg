# Backend 规约 — techmg

Spring Boot 3.2.3 / Spring 6.1.4 / Maven / MyBatis-Plus 3.5.5 / JDK 17

## 模块架构（当前）

```
techmg-admin                  # 启动装配 (Spring Boot + Security + DataInit + 登录配置)
  ├── techmg-system           # RBAC: 用户/角色/菜单/部门/日志/字典
  ├── techmg-business-service # 业务: Health + Excel + 技改任务管理(父任务/子任务/治理条目)
  │     └── techmg-business-api   # 业务接口定义 (占位)
  └── techmg-framework        # 框架: Web/MyBatis/Security/Log/Excel (Redis已移除)
        └── techmg-common     # 公共: BaseEntity/R<T>/ResultCode/枚举/事件/TreeUtil
```

### 模块依赖（当前）

```
techmg-admin
  ├── techmg-system
  ├── techmg-business-service
  │     └── techmg-business-api (空)
  └── techmg-framework
        └── techmg-common
```

---

## 参考：企业级模块分层体系（目标架构）

> 以下为 Spring Boot 3 多模块项目标准分层规范。当前项目为中等规模，采用简化架构（6 模块）。随着业务扩展，可逐步演进至标准分层。

| 层次 | 标准模块 | 当前对应 | 职责 |
|:---|:---|:---|:---|
| **部署层** | `application` | `techmg-admin` ✅ | 启动类、配置文件、全局异常处理 |
| **业务层** | `*-service` | `techmg-business-service` ⚠️ | Controller + VO，应轻薄透传 |
| **API 定义** | `api` | `techmg-business-api` ⚠️ | Feign/REST 接口定义（当前为空占位） |
| **核心层** | `core` | 分散在 `techmg-system` + `business-service` | 领域模型 + 领域服务 + 业务逻辑 |
| **安全** | `security` | 内嵌在 `techmg-framework/security/` | JWT、认证过滤器 |
| **基础设施** | `infrastructure` | `techmg-framework` (混合) | MyBatis/数据源/日志/Excel |
| **基础层** | `common` | `techmg-common` ✅ | 工具类、统一返回、异常、枚举 |

### 标准依赖方向

```
application → *-service → core → {infrastructure, security} → common
```

**核心红线**：单向依赖，禁止反向或跨层。

### 当前偏差与演进建议

| 偏差 | 说明 | 优先级 |
|------|------|--------|
| `techmg-framework` 混合安全+基础设施+Web | 随模块增长可拆分为 `techmg-security` + `techmg-infrastructure` | 低 |
| `techmg-business-api` 为空占位 | 待有跨服务调用需求时启用 | 低 |
| Controller 在 `*-service` 而非独立模块 | 符合 `*-service` 定位，无需拆 | — |
| 缺少独立 `core` 模块 | 当前 project 规模适中，`techmg-system` 可视为 core 角色 | 低 |

### 模块间依赖规则（适用当前项目）

| 依赖方 ↓ → 被依赖方 | common | framework | system | business-api | business-service | admin |
|:---|:---:|:---:|:---:|:---:|:---:|:---:|
| **common** | — | ❌ | ❌ | ❌ | ❌ | ❌ |
| **framework** | ✅ | — | ❌ | ❌ | ❌ | ❌ |
| **system** | ✅ | ✅ | — | ❌ | ❌ | ❌ |
| **business-api** | ✅ | ❌ | ❌ | — | ❌ | ❌ |
| **business-service** | ✅ | ✅ | ✅ | ✅ | — | ❌ |
| **admin** | ✅ | ✅ | ✅ | ❌ | ✅ | — |

> ❌ = 禁止依赖，✅ = 允许依赖

---

## API 清单 (57 端点)

### Auth (6) — v2.0 扩展 (SSIC 改造)
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/auth/login` | SSIC 模式: 302 重定向到 SSO 登录页; Mock 模式: 返回错误 |
| POST | `/api/auth/login` | SSIC 模式: 验证 SSIAuth/SSI_SIGN 参数 → JWT; Mock 模式: 自动登录 |
| POST | `/api/auth/logout` | 登出 |
| GET | `/api/auth/userinfo` | 当前用户信息 (含 AD/机构/NotesID 等扩展字段) |
| GET | `/api/auth/config` | 查询认证模式 {ssoEnabled, ssicEnabled, loginUrl} |
| GET | `/api/auth/sso/login-url` | SSO 登录地址 (兼容) |
| POST | `/api/auth/sso/login` | SSO ticket 登录 (已废弃，使用 POST /login 替代) |

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
| SsoAuthProvider | framework/security/ | SSIC 统一认证接口 (authenticate/queryUserInfo) |
| SsicUser | framework/security/ | SSIC 用户信息 POJO |
| SsicEncryptUtils | framework/security/ | SM2/SM4 加解密适配 (TODO: 替换行内 SDK) |
| ExcelUtil | framework/excel/ | POI 4.1.2 |
| ApiAccessLog | framework/log/ | AOP 结构化日志 + Spring Event 写库 |
| TraceIdFilter | framework/web/ | 请求链路追踪 |
| SecurityConfig | admin/config/ | 无状态JWT + CORS, `/api/auth/**` 放行 |
| DataInitializer | admin/config/ | 启动创建 admin 用户 |
| LoginMockProperties | admin/config/ | Mock 登录配置 (ssic.enabled=false 时生效) |
| SsicProperties | common/config/ | SSIC 配置属性 (ip/keyName/version/smPublicKey等) |

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

## 架构分层规约

### 1. 分层隔离与依赖方向（核心红线）

依赖规则：严格遵循 **单向依赖**，方向为 Controller → Service → Repository。

| 禁止行为 | 说明 |
|---------|------|
| ❌ Controller 注入 Repository | Controller 仅依赖 Service |
| ❌ Service 注入 HttpServletRequest/Response | Service 不感知 Web 层 |
| ❌ 下层依赖上层 | Repository 不得引用 Service |
| ❌ 跨层调用 | Controller 不得直接操作数据库 |

包结构约束：

```
com.icbc.sh.techmg
├── admin/           # 启动装配 (Spring Boot + Security + 配置)
├── system/          # RBAC 模块
│   ├── controller/  # 表现层
│   ├── service/     # 业务接口
│   │   └── impl/    # 业务实现
│   ├── repository/  # 数据访问 (Mapper)
│   ├── entity/      # 数据库实体
│   ├── dto/         # 数据传输对象
│   └── vo/          # 视图对象
├── business/        # 业务模块 (同上结构)
├── framework/       # 框架基础设施
└── common/          # 公共模块 (BaseEntity/R/ResultCode/枚举/异常)
```

### 2. 数据模型对象（POJO）使用规约

| 对象 | 包位置 | 职责 | 约束 |
|------|--------|------|------|
| **Entity** | `entity/` | 与数据库表一一映射，携带 `@TableName` | **仅限 Repository 层使用**，禁止返回给 Controller 或前端 |
| **DTO** | `dto/` | Service ↔ Controller 之间数据传递 | 命名 `XxxRequestDTO`（入参）/ `XxxResponseDTO`（出参），含校验注解 |
| **VO** | `vo/` | Controller 返回给前端的最终响应 | 可按前端需求灵活裁剪字段，聚合多表数据或脱敏 |

对象转换约束：
- 转换必须通过 MapStruct / BeanUtils / 手动 Setter
- **禁止** Controller 层编写 Entity ↔ DTO 转换逻辑 — 该逻辑下沉至 Service 层或 Assembler 类

### 3. 事务管理与数据库操作约束

- `@Transactional` **仅限 Service 层**公开方法，推荐 `@Transactional(rollbackFor = Exception.class)`
- ❌ 禁止 Controller 或 Repository 层添加事务注解
- Repository (Mapper) 仅负责 CRUD 与复杂查询封装，**禁止包含业务逻辑判断**（如 if-else 判断状态）
- 多表关联/复杂统计 → Repository 层用 XML Mapper，Service 层组装结果
- **严禁 N+1 查询**：禁止循环中多次查询数据库

### 4. 依赖注入（DI）规约

强制使用**构造器注入**（Constructor Injection）：

```java
// ✅ 正确
@RestController
@RequiredArgsConstructor  // Lombok
public class UserController {
    private final UserService userService;
}

// ❌ 禁止 — 字段注入不利于单元测试和不可变性
@Autowired
private UserService userService;
```

Service 层必须面向接口编程：
- 接口：`XxxService`
- 实现：`XxxServiceImpl`（置于 `impl/` 子包）

### 5. 异常处理统一约束

| 层 | 规则 |
|----|------|
| **Service** | 抛 `BusinessException`（具有业务语义的自定义运行时异常），禁止抛 `SQLException`/`NullPointerException` 等底层异常 |
| **Controller** | **禁止** try-catch 包裹业务逻辑 |
| **全局** | `GlobalExceptionHandler`（`@ControllerAdvice` + `@ExceptionHandler`）统一处理，返回标准化 `R<T>` 响应 |

### 6. 命名与代码风格规范

| 维度 | 规范 | 示例 |
|------|------|------|
| API 路径 | RESTful 风格，复数名词 | `/api/users`、`/api/tech-reform/task/list` |
| Controller 方法 | getXxx / queryXxx / createXxx / updateXxx / deleteXxx | `getUserById`、`queryTaskList`、`createSubtask` |
| Service 方法 | findXxx / saveXxx / removeXxx | `findUserByAuthNo`、`saveReformTask`、`removeSubtask` |
| 日志 | Service 层记录关键业务日志；Controller 层不记录冗余入参出参（AOP 统一处理除外） | — |

### AI 代码生成强制检查清单

在输出代码之前，必须逐项校验：

1. ✅ Controller 是否只注入了 Service，而没有注入 Repository？
2. ✅ Service 方法上是否标注了 `@Transactional`（若涉及写操作）？
3. ✅ 返回给前端的数据是否使用了 DTO 或 VO，而非直接暴露 Entity？
4. ✅ 所有依赖是否均使用 `final` + 构造器注入（`@RequiredArgsConstructor`）？
5. ✅ 业务异常是否均由 `BusinessException` 抛出，并由 `GlobalExceptionHandler` 统一返回？
6. ✅ 包路径是否正确？（controller 下没有放 service 的实现类）
7. ✅ `@RequestParam` / `@PathVariable` 是否显式指定 `name`/`value`？
8. ✅ 是否存在 N+1 查询（循环中调用 Mapper）？

---

## 项目铁律（共 17 条，v2.0 修订 + 架构规约 + 测试质量）

1. 配置 `.yml`, 禁止 `.properties`
2. 简单CRUD用API, 复杂SQL用XML Mapper, 禁止注解SQL — 详见 [架构规约 §3](#3-事务管理与数据库操作约束)
3. Gson `extendMessageConverters`
4. 所有接口 → `R<T>`
5. 抛 `BusinessException` → `GlobalExceptionHandler` — 详见 [架构规约 §5](#5-异常处理统一约束)
6. `@ApiAccessLog` → Spring Event → DB
7. `@DS("master")` 默认, `@DS("slave")` 从库读
8. 逻辑删除 `deleted`, `@TableLogic`
9. 自动填充 createTime/createBy/updateTime/updateBy
10. Log4j2 2.17.1, 排除 Logback
11. Security 无状态JWT, `/api/auth/**` 放行
12. **`@RequestParam` / `@PathVariable` 必须显式 `name`/`value`**（PolarDB 环境不支持 `-parameters`）
13. BCrypt 密码
14. **依赖注入：构造器注入 + `@RequiredArgsConstructor`，禁止 `@Autowired` 字段注入** — 详见 [架构规约 §4](#4-依赖注入di-规约)
15. **分层隔离：Controller → Service → Repository 单向依赖，Entity 不暴露给前端** — 详见 [架构规约 §1](#1-分层隔离与依赖方向核心红线)
16. **单元测试：业务代码行覆盖率 ≥ 90%，TDD 先写测试后实现** — 详见 [测试与代码质量](#测试与代码质量)
17. **SQL 变更：DDL/DML/Rollback 分离，按版本放入 `techmgdb/polardb/` 目录** — 详见 [项目总纲 CLAUDE.md](CLAUDE.md)

---

## AI 编码补充规约（后端）

### Lombok 使用（强制）

- Entity/DTO/VO 类必须使用 `@Data`，禁止手写 Getter/Setter
- 日志必须使用 `@Slf4j`，严禁 `System.out.println()`

### 判空与防御（强制）

- 链式调用（如 `user.getAddress().getCity()`）必须使用 `Optional` 或显式判空
- 方法返回值若可能为 `null`，必须在 JavaDoc 中注明 `@return null if ...`

### 常量与魔法值（强制）

- 业务状态码、错误码必须定义为常量或枚举，禁止硬编码数字/字符串
- 示例：`if (status == 1)` → `if (OrderStatusEnum.PAID.getCode() == status)`

### SQL 注入防护（强制）

- MyBatis 必须使用 `#{}` 传参，严禁 `${}` 拼接用户输入
- 动态表名/字段名场景需白名单校验后方可使用 `${}`

### 敏感信息脱敏（强制）

- 日志中严禁打印明文密码、身份证号、手机号
- VO 中敏感字段使用 `@JsonIgnore` 或 Gson `@Expose(serialize = false)` 排除

### API 设计规约

| 规则 | 说明 |
|------|------|
| **RESTful 风格** | URL 使用名词复数 `/api/users`，禁止动词 `/getUser` |
| **HTTP 方法语义** | GET=查询、POST=新增、PUT=全量修改、DELETE=删除 |
| **Swagger 注解** | 所有 Controller 必须 `@Operation(summary="...")`，DTO 字段加 `@Schema` |

### 后端 AI 检查清单

- [ ] 是否存在 `System.out.println()` 或硬编码魔法值？
- [ ] 是否存在 `${}` 拼接 SQL 参数？
- [ ] 日志中是否打印了明文敏感信息？
- [ ] 所有 Controller 是否有 `@Operation` Swagger 注解？

## 登录模式

| 配置 | 行为 |
|------|------|
| `ssic.enabled: false` | Mock 自动登录 — 前端无感知，后端返回 `login.mock` 配置的用户 |
| `ssic.enabled: true` | SSIC 统一认证 — GET 重定向 SSO → 回调 SSIAuth/SSI_SIGN → 验证 → AAM 查询用户 → JWT |

### Mock 模式

Mock 配置示例 (`application.yml`):
```yaml
ssic:
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

### SSIC 统一认证模式

SSIC 配置示例 (`application.yml`):
```yaml
ssic:
  enabled: true
  ip: aam.icbc                              # 统一认证服务器地址
  server-key-name: SsIC                     # 统一认证公钥名称
  version: SM2                              # 统一认证版本
  client-key-name: techmanageplatform       # 应用密钥名称
  sm-public-key: ""                         # 3.0非对称密钥 (部署时填写)
  client-site-url: http://localhost:5173/#/ # 应用登录回调 URL
```

### SSIC 登录流程

```
前端无 token → GET /api/auth/login → 302 重定向到 SSO 登录页
  → 用户在 SSO 页登录 → 回跳 client.site.url?SSIAuth=xxx&SSI_SIGN=xxx
  → 前端截取 ? 后参数 → POST /api/auth/login (config=原始参数字符串)
  → 后端 serverSideAuth.execute(req, resp, ssiAuth, ssiSign) [TODO: 内网 SDK]
  → 获取 SSIcUser → queryUserInfoAAM() [TODO: 内网 API]
  → syncUserInfo (upsert sys_user) → 生成 JWT → 返回 {token, userInfo}
```

### 内网部署适配清单

部署到内网后，需替换以下 TODO 标记处：
1. `SsoAuthProviderImpl.authenticate()` — 替换为 `serverSideAuth.execute()`
2. `SsoAuthProviderImpl.getLoginRedirectUrl()` — 替换为实际 SSO URL
3. `SsoAuthProviderImpl.queryUserInfo()` — 替换为 `queryUserInfoAAM()` 真实调用
4. `SsicEncryptUtils` — 替换为行内 `EncryptUtils` SDK
5. `application.yml` — 填写 `ssic.sm-public-key` 和 `ssic.client-site-url` 真实值

## 数据库 (tmvp) — PolarDB 标准版

- 数据源: `jdbc:mysql://47.99.179.180:33066/tmvp` / 用户: `techmg`
- **23 表**: 9 系统表 + 10 业务表 + 4 v2 新表

### v2.0 新增表

| 表 | 说明 |
|------|------|
| `sys_user_branch` | 用户多机构关联 |
| `tech_reform_task` | 技改父任务 |
| `tech_reform_subtask` | 技改子任务 |
| `tech_reform_item` | 治理清单条目 |

### 角色与菜单

- 5 角色: PLATFORM_ADMIN / DEPT_ADMIN / DEPT_DBA / NORMAL_USER / GUEST
- 25 菜单: 首页 / 技术管理(8) / 资产管理(4) / 生产管理(1) / 数智生态(4) / 设置(3)
- utf8mb4 + Druid `connection-init-sqls`

## PolarDB SQL 编码规约

> **适用说明**：本项目使用 PolarDB 标准版（100% 兼容 MySQL）。以下约束中，§1-2 通用规范全部适用；§3-5（PolarDB-X 分布式版限制）作为未来扩展参考，标准版可忽略。

### 1. 通用 SQL 语法规范

- **标识符**：以字母或下划线 `_` 开头，后续可用字母/下划线/数字/`$`，统一**小写**命名
- **语句终结**：每个 SQL 命令必须以分号 `;` 结尾

### 2. 表结构设计约束

#### 2.1 CHECK 约束

```sql
-- ✅ 列级约束
CREATE TABLE t1 (
    c1 INT CHECK (c1 > 10),
    c2 INT CONSTRAINT c2_positive CHECK (c2 > 0),
    c3 INT,
    CHECK (c1 <> c2),                     -- 表级约束
    CONSTRAINT c1_nonzero CHECK (c1 <> 0) -- 命名表级约束
);
```

- 列级约束仅引用该列，表级约束可引用多列
- 未指定 `[NOT] ENFORCED` 时默认生效
- 未显式命名时系统自动生成 `TableName_chk_1,2,3...`

#### 2.2 约束通用原则

- 违反约束的 INSERT/UPDATE 将抛出错误
- 表达式为布尔类型，结果 `TRUE`/`FALSE`/`UNKNOWN`（NULL 时）

### 3. 索引创建规范

| 规则 | 说明 |
|------|------|
| 目的 | 提高查询性能，支持多列索引 |
| 统计信息 | 创建后定期运行 `ANALYZE` 更新统计信息 |
| 表达式索引 | **禁止**（PolarDB 无法统一评估跨分区表达式） |
| 查询建议 | 加入可提前过滤的谓词条件（如时间范围），扫描大表确保命中索引 |

### 4. SQL 性能优化约束

| 优化项 | 规范 |
|--------|------|
| 子查询 | 避免不必要的嵌套子查询；开启 `loose_simplify_subq_mode` 自动重写 |
| 关联查询 | 分片 Key 与 Join Key 对齐；小表设为广播表；或使用全局二级索引 |
| 数据扫描 | 加入时间范围等谓词条件减少扫描量 |
| IN 子句 | 支持基于 IN 值数量的动态分区裁剪 |

### 5. PolarDB-X 分布式版使用限制（参考）

> 以下限制仅适用于 PolarDB-X 分布式版，本项目标准版不受影响。

| 类别 | 禁止项 |
|------|--------|
| **SQL 大类** | 存储过程、触发器、游标、临时表、外键、自定义数据类型/函数 |
| **复合语句** | `BEGIN…END`、`LOOP`、`WHILE`、`IF` 等流程控制 |
| **DDL** | 拆分表不支持 `CREATE TABLE ... LIKE/SELECT`；不支持 ALTER 拆分字段 |
| **DML** | 不支持 `SELECT INTO`、`INSERT DELAYED`、UPDATE SET 子查询 |
| **子查询** | 不支持 HAVING/JOIN ON 中的子查询 |

### 6. 其他约束

- 每表最多 **8192** 个分区
- 单表/分区最大存储 **64 TB**
- INTERVAL RANGE 分区：分区键仅单列（数字/日期），间隔为正整数；批量插入最多自动创建 **30** 个新分区；分区名禁用 `_p` 前缀

### PolarDB SQL 生成检查清单

- [ ] 标识符是否以小写字母/下划线开头，语句是否以 `;` 结尾？
- [ ] 是否使用了存储过程、触发器、游标、临时表、外键？（标准版可用，X 版禁止）
- [ ] 是否在 UPDATE SET 中使用了子查询？
- [ ] 分区表索引是否使用了表达式索引？（禁止）
- [ ] 关联查询是否考虑了分片 Key 对齐或广播表？
- [ ] 慢 SQL 是否包含了可提前过滤的谓词条件？

---

## 测试与代码质量

### 单元测试（强制）

- **覆盖率红线**：业务代码（Service/Repository）行覆盖率 **≥ 90%**，分支覆盖率 ≥ 80%
- **测试框架**：JUnit 4 + Mockito + Spring Boot Test
- **测试范围**：
  - Service 层：Mock 所有依赖，验证核心逻辑 + 边界条件 + 异常路径
  - Repository 层：使用 `@MybatisPlusTest` 或 H2 内存数据库，验证 SQL 正确性
  - Controller 层：使用 `@WebMvcTest` + MockMvc，验证请求映射 + 参数校验 + 响应结构
- **测试类命名**：`XxxServiceTest` / `XxxRepositoryTest` / `XxxControllerTest`，与被测类同包
- **TDD 流程**：编码前先写测试 → 红灯 → 实现 → 绿灯 → 重构
- **禁止行为**：
  - ❌ 禁止跳过测试提交代码
  - ❌ 禁止使用 `@Ignore` 绕过失败测试（除非有明确 Issue 跟踪）
  - ❌ 禁止仅覆盖 Happy Path，必须覆盖异常和边界

```java
// ✅ 正确示例 — Service 单元测试（JUnit 4）
@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {
    @Mock private SysUserMapper userMapper;
    @InjectMocks private UserServiceImpl userService;

    @Test
    public void shouldReturnUserWhenAuthNoExists() { ... }

    @Test
    public void shouldThrowBusinessExceptionWhenUserNotFound() { ... }
}
```

### 代码质量检查（强制）

- **Sonar 门禁**：编码完成后必须运行 SonarQube 扫描，确保：
  - **0 Bugs**（阻断/严重级别）
  - **0 Vulnerabilities**
  - **代码异味（Code Smells）**：新增代码 ≤ 0
  - **重复率**：< 3%
  - **行覆盖率**：≥ 90%
- **检查时机**：每次 `mvn compile` 后，提交 PR 前
- **配置**：`sonar-project.properties` 置于 backend 根目录

### 测试与质量检查清单

- [ ] 所有 Service/Repository 是否有对应单元测试？
- [ ] 测试覆盖率是否 ≥ 90%（通过 JaCoCo 报告验证）？
- [ ] 异常路径和边界条件是否覆盖？
- [ ] Sonar 扫描是否通过（0 Bugs / 0 Vulnerabilities）？
- [ ] 是否有被 `@Ignore` 跳过的测试？如有，是否有 Issue 跟踪？

---

## 构建

```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home
cd backend && mvn clean compile && mvn spring-boot:run -pl techmg-admin
```
