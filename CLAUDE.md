# CLAUDE.md — techmg v1.0

技术管理平台，工商银行上海研发基地内部系统。v1.0 完成项目骨架、认证安全、RBAC、动态菜单路由、设置管理。

## 快速导航

- 后端规约 → [backend/CLAUDE.md](backend/CLAUDE.md)
- 前端规约 → [vue-front/CLAUDE.md](vue-front/CLAUDE.md)

## 技术栈

| 维度 | 选型 | 版本 |
|------|------|------|
| 后端 | Spring Boot 3.2.3 / Spring 6.1.4 / JDK 17 | Maven 多模块 |
| ORM | MyBatis-Plus 3.5.5 | 简单CRUD用API, 复杂SQL用XML |
| 数据库 | MySQL 8 (Docker) | tmvp 库, utf8mb4 |
| 连接池 | Druid 1.2.20 | 多数据源 + SQL监控 |
| 缓存 | Redis 7 | RedisTemplate + CacheManager |
| JSON | Gson 2.10.1 | extendMessageConverters |
| 日志 | Log4j2 2.17.1 | 100MB/30天滚动 |
| 安全 | Spring Security 6 + jjwt 0.12.3 | 无状态JWT |
| 前端 | Vue 3.3.9 / Vite 5.0.4 | Element Plus 2.5.0 |
| CSS | Sass 1.66.1 | 现代浅色主题 #3370ff |

## 数据库连接

- 库名: `tmvp` / 用户: `techmg` / 密码: `Tech@123++**`
- Docker: `docker exec -i mysql mysql -uroot -p --default-character-set=utf8mb4 tmvp`
- JDBC: `jdbc:mysql://localhost:3306/tmvp?...&allowPublicKeyRetrieval=true`
- 17 张表 (7 系统表 + 10 业务表), 5 角色, 17 菜单

## 全局约束

1. Java 17+ / 包名 `com.icbc.sh.techmg`
2. 配置 `.yml` (禁止 `.properties`), 前端 `.env.{environment}`
3. 所有 API → `R<T>` 格式
4. MyBatis: 禁止 `@Select/@Update/@Insert/@Delete` 注解
5. JSON: Gson, `extendMessageConverters` (保留 String 转换器)
6. 日志: Log4j2 2.17.1, 排除 Logback
7. 跨域: 后端 CORS + Vite proxy
8. 编译: `maven-compiler-plugin` 必须启用 `<parameters>true</parameters>`
9. 字符集: MySQL 客户端必须 `--default-character-set=utf8mb4`
