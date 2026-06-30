# Backend 规约 — techmg

Spring Boot 3.2.3 / Spring 6.1.4 / Maven / MyBatis-Plus 3.5.5 / JDK 17

## 模块架构

```
techmg-admin                  # 启动装配 (Spring Boot + Security + DataInit)
  ├── techmg-system           # RBAC: 用户/角色/菜单/部门/日志/字典
  ├── techmg-business-service # 业务: Health + Excel 模板下载
  │     └── techmg-business-api   # 业务接口定义 (占位)
  └── techmg-framework        # 框架: Web/MyBatis/Security/Redis/Log/Excel
        └── techmg-common     # 公共: BaseEntity/R<T>/ResultCode/枚举/事件/TreeUtil
```

## API 清单 (34 端点)

### Auth (3)
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/auth/login` | 登录 (authNo+password → JWT+userInfo) |
| POST | `/api/auth/logout` | 登出 |
| GET | `/api/auth/userinfo` | 当前用户信息 |

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

### 数据字典 (8)
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/system/dict/type/list` | 字典类型列表 |
| POST/GET/PUT/DELETE | `/api/system/dict/type` `/{id}` | CRUD |
| GET | `/api/system/dict/data/{dictType}` | 按类型获取 (@Cacheable) |
| POST/GET/PUT/DELETE | `/api/system/dict/data` `/{id}` | CRUD (@CacheEvict) |

### 其他 (2)
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/health` | 健康检查 (无需认证) |
| GET | `/api/excel/template/{type}` | Excel 模板下载 |

## 框架组件

| 组件 | 路径 | 说明 |
|------|------|------|
| WebConfig | framework/web/ | CORS + ResponseBodyAdvice |
| GsonConfig | framework/web/ | extendMessageConverters |
| MyBatisPlusConfig | framework/mybatis/ | 分页 |
| MyMetaObjectHandler | framework/mybatis/ | 自动填充 |
| JwtTokenProvider | framework/security/ | JWT 生成/验证 (jjwt 0.12.3) |
| JwtAuthenticationFilter | framework/security/ | OncePerRequestFilter |
| RedisConfig | framework/redis/ | RedisTemplate + CacheManager |
| RedisUtil | framework/redis/ | get/set/delete 封装 |
| ExcelUtil | framework/excel/ | POI 4.1.2 |
| ApiAccessLog | framework/log/ | AOP + Spring Event 写库 |
| SecurityConfig | admin/config/ | 无状态JWT + BCrypt |
| DataInitializer | admin/config/ | 启动创建 admin/admin123 |

## 公共模块 (common)

| 组件 | 说明 |
|------|------|
| BaseEntity | 抽象基类 |
| R\<T\> | 统一响应体 |
| ResultCode | 返回码枚举 |
| BusinessException | 业务异常 |
| GlobalExceptionHandler | 全局异常 (含 Security 异常) |
| CacheConstants | Redis 缓存键 |
| OperationLogEvent | 操作日志 Spring Event |
| TreeUtil | 泛型树形构建器 |
| GovernanceStatus | 治理状态枚举 |
| DbTypeEnum | 数据库类型枚举 |
| InspectionTypeEnum | 巡检类型枚举 |

## 12 条铁律

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
12. BCrypt 密码, compiler `-parameters` 必须

## 数据库 (tmvp)

- 19 表: 9 系统表 + 10 业务表
- 5 角色: PLATFORM_ADMIN/DEPT_ADMIN/DEPT_DBA/NORMAL_USER/GUEST
- 26 菜单: 首页 / 技术管理(9) / 资产管理(4) / 生产管理(1) / 数智生态(4) / 设置(3)
- utf8mb4 + `SET NAMES utf8mb4` + Druid `connection-init-sqls`

## 构建

```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home
cd backend && mvn clean compile && mvn spring-boot:run -pl techmg-admin
```
