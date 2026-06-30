# Backend 规约 — techmg v1.0

Spring Boot 3.2.3 / Spring 6.1.4 / Maven / MyBatis-Plus 3.5.5 / JDK 17

## 模块架构

```
techmg-admin                  # 启动装配 (Spring Boot + Security + 配置)
  ├── techmg-system           # RBAC: 用户/角色/菜单/部门 CRUD
  ├── techmg-business-service # 业务: HealthController + ExcelController
  │     └── techmg-business-api   # 业务接口定义 (占位)
  └── techmg-framework        # 框架: Web/MyBatis/Security/Redis/Log/Excel
        └── techmg-common     # 公共: BaseEntity/R<T>/ResultCode/异常
```

## v1.0 已实现 API (27 端点)

| 模块 | 端点 | 数量 |
|------|------|------|
| Auth | `/api/auth/login` `logout` `userinfo` | 3 |
| User | `/api/system/user/list` `{id}` `POST` `PUT` `DELETE` `{userId}/roles` | 7 |
| Role | `/api/system/role/list` `{id}` `POST` `PUT` `DELETE` `{roleId}/menus` | 7 |
| Menu | `/api/system/menu/tree` `user-tree` `{id}` `POST` `PUT` `DELETE` | 6 |
| Dept | `/api/system/dept/tree` `{id}` `POST` `PUT` `DELETE` | 5 |
| Health | `/api/health` | 1 |
| Excel | `/api/excel/template/{type}` | 1 |

## 框架组件

| 组件 | 路径 | 说明 |
|------|------|------|
| WebConfig | framework/web/ | CORS + ResponseBodyAdvice 自动包装 R\<T\> |
| GsonConfig | framework/web/ | extendMessageConverters 替换 Jackson |
| MyBatisPlusConfig | framework/mybatis/ | 分页插件 DbType.MYSQL |
| MyMetaObjectHandler | framework/mybatis/ | 自动填充 createTime/updateBy/deleted |
| JwtTokenProvider | framework/security/ | JJWT 0.12.3 生成/验证 |
| JwtAuthenticationFilter | framework/security/ | OncePerRequestFilter Bearer 提取 |
| RedisConfig | framework/redis/ | RedisTemplate + CacheManager |
| RedisUtil | framework/redis/ | set/get/delete/expire 封装 |
| ExcelUtil | framework/excel/ | POI 4.1.2 Workbook 创建/解析/模板 |
| ApiAccessLog | framework/log/ | @ApiAccessLog 注解 + AOP 切面 |
| SecurityConfig | admin/config/ | 无状态JWT + BCrypt + 放行规则 |
| DataInitializer | admin/config/ | 启动创建 admin/admin123 |
| OpenApiConfig | admin/config/ | SpringDoc OpenAPI 3 |

## 数据库 (tmvp)

- 17 表: 7 系统表 (sys_*) + 10 业务表
- 5 角色: PLATFORM_ADMIN/DEPT_ADMIN/DEPT_DBA/NORMAL_USER/GUEST
- 17 菜单: 首页/技术管理/资产管理/生产管理/设置
- 字符集: utf8mb4, Druid connection-init-sqls: `SET NAMES utf8mb4`

## 12 条铁律

1. 配置 `.yml`, 禁止 `.properties`
2. 简单CRUD用API, 复杂SQL用XML Mapper, 禁止注解SQL
3. Gson `extendMessageConverters` (保留 String 转换器)
4. 所有接口返回 `R<T>`, ResponseBodyAdvice 自动包装
5. 抛 `BusinessException` → GlobalExceptionHandler
6. `@ApiAccessLog` 注解 AOP 日志
7. `@DS("master")` 默认, `@DS("slave")` 从库
8. 逻辑删除 `deleted`, `@TableLogic`
9. 自动填充 `createTime/createBy/updateTime/updateBy`
10. Log4j2 2.17.1, 排除 Logback, 100MB/30天
11. Security 无状态JWT, `/api/auth/**` 放行
12. BCrypt 密码, maven-compiler-plugin 必须 `-parameters`

## 构建

```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home
cd backend && mvn clean compile && mvn spring-boot:run -pl techmg-admin
```
