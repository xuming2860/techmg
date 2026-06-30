# techmg 技术管理平台 — 项目骨架搭建实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 搭建 techmg 技术管理平台完整项目骨架，包括 Maven 多模块后端（6 个子模块）和 Vue 3 + Vite 前端，全技术栈集成可运行。

**Architecture:** 后端采用 DDD 分层多模块架构，common 做公共底座 → framework 做横切能力 → system 做 RBAC → business-api/service 做业务分离；前端采用 Vue 3 + Element Plus + Pinia，按模块拆分视图和 API 层。

**Tech Stack:** Spring Boot 3.2.3 / Spring 6.1.4 / MyBatis-Plus 3.5+ / Druid 1.2+ / Log4j2 2.17.1 / Gson 2.10+ / POI 4.1.2 / Lombok 1.18+ / Vue 3.3.9 / Vite 5.0.4 / Element Plus 2.x / Pinia 2.1.4 / Axios 1.7.2 / Sass 1.66.1 / ECharts 5.x

## Global Constraints

- Java 17+ (Spring Boot 3.2.x 最低要求)
- 包名: `com.icbc.sh.techmg`
- 配置文件必须使用 `.yml` 格式
- MyBatis-Plus SQL 规范: 简单 CRUD 用 API，复杂 SQL 走 XML Mapper，**禁止 @Select/@Update 注解写 SQL**
- 配置文件: `.env.development` / `.env.test` / `.env.production`
- 前端默认布局: 上左右（顶部导航 + 左侧菜单 + 右侧内容）
- 前端 Sass 1.66.1

---

### Task 1: 创建父 POM 与项目根结构

**Files:**
- Create: `backend/pom.xml`
- Create: `.gitignore` (更新)

**Interfaces:**
- Produces: Maven 父 POM，定义所有模块、版本号、依赖管理

- [ ] **Step 1: 编写父 POM**

在项目根目录创建 `backend/pom.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.icbc.sh</groupId>
    <artifactId>techmg</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>pom</packaging>
    <name>techmg</name>
    <description>技术管理平台 - 上海研发基地</description>

    <modules>
        <module>techmg-common</module>
        <module>techmg-framework</module>
        <module>techmg-system</module>
        <module>techmg-business-api</module>
        <module>techmg-business-service</module>
        <module>techmg-admin</module>
    </modules>

    <properties>
        <java.version>17</java.version>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>

        <spring-boot.version>3.2.3</spring-boot.version>
        <spring.version>6.1.4</spring.version>
        <mybatis-plus.version>3.5.5</mybatis-plus.version>
        <druid.version>1.2.20</druid.version>
        <dynamic-ds.version>4.2.0</dynamic-ds.version>
        <log4j2.version>2.17.1</log4j2.version>
        <gson.version>2.10.1</gson.version>
        <poi.version>4.1.2</poi.version>
        <lombok.version>1.18.30</lombok.version>
        <springdoc.version>2.3.0</springdoc.version>
        <jjwt.version>0.12.3</jjwt.version>
        <mapstruct.version>1.5.5.Final</mapstruct.version>
        <hutool.version>5.8.25</hutool.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <!-- Spring Boot BOM -->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${spring-boot.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>

            <!-- MyBatis-Plus -->
            <dependency>
                <groupId>com.baomidou</groupId>
                <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
                <version>${mybatis-plus.version}</version>
            </dependency>

            <!-- Druid -->
            <dependency>
                <groupId>com.alibaba</groupId>
                <artifactId>druid-spring-boot-3-starter</artifactId>
                <version>${druid.version}</version>
            </dependency>

            <!-- Dynamic Datasource -->
            <dependency>
                <groupId>com.baomidou</groupId>
                <artifactId>dynamic-datasource-spring-boot3-starter</artifactId>
                <version>${dynamic-ds.version}</version>
            </dependency>

            <!-- Gson -->
            <dependency>
                <groupId>com.google.code.gson</groupId>
                <artifactId>gson</artifactId>
                <version>${gson.version}</version>
            </dependency>

            <!-- POI -->
            <dependency>
                <groupId>org.apache.poi</groupId>
                <artifactId>poi</artifactId>
                <version>${poi.version}</version>
            </dependency>
            <dependency>
                <groupId>org.apache.poi</groupId>
                <artifactId>poi-ooxml</artifactId>
                <version>${poi.version}</version>
            </dependency>

            <!-- SpringDoc OpenAPI -->
            <dependency>
                <groupId>org.springdoc</groupId>
                <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
                <version>${springdoc.version}</version>
            </dependency>

            <!-- JWT -->
            <dependency>
                <groupId>io.jsonwebtoken</groupId>
                <artifactId>jjwt-api</artifactId>
                <version>${jjwt.version}</version>
            </dependency>
            <dependency>
                <groupId>io.jsonwebtoken</groupId>
                <artifactId>jjwt-impl</artifactId>
                <version>${jjwt.version}</version>
                <scope>runtime</scope>
            </dependency>
            <dependency>
                <groupId>io.jsonwebtoken</groupId>
                <artifactId>jjwt-jackson</artifactId>
                <version>${jjwt.version}</version>
                <scope>runtime</scope>
            </dependency>

            <!-- MapStruct -->
            <dependency>
                <groupId>org.mapstruct</groupId>
                <artifactId>mapstruct</artifactId>
                <version>${mapstruct.version}</version>
            </dependency>

            <!-- Hutool -->
            <dependency>
                <groupId>cn.hutool</groupId>
                <artifactId>hutool-all</artifactId>
                <version>${hutool.version}</version>
            </dependency>

            <!-- Lombok -->
            <dependency>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>${lombok.version}</version>
            </dependency>

            <!-- Jakarta Validation -->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-validation</artifactId>
                <version>${spring-boot.version}</version>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <pluginManagement>
            <plugins>
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                    <version>${spring-boot.version}</version>
                </plugin>
            </plugins>
        </pluginManagement>
    </build>
</project>
```

- [ ] **Step 2: 更新 .gitignore**

```gitignore
# Maven
target/
*.jar
*.war
*.ear

# IDE
.idea/
*.iml
.vscode/
.settings/
.project
.classpath

# Log
*.log
logs/

# Node
node_modules/
dist/

# OS
.DS_Store
Thumbs.db

# Env
.env.local
.env.*.local
```

- [ ] **Step 3: 验证 Maven 结构**

```bash
mvn validate
```
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add pom.xml .gitignore
git commit -m "chore: init Maven parent POM with all dependency versions"
```

---

### Task 2: 创建 techmg-common 模块

**Files:**
- Create: `backend/techmg-common/pom.xml`
- Create: `backend/techmg-common/src/main/java/com/icbc/sh/techmg/common/base/BaseEntity.java`
- Create: `backend/techmg-common/src/main/java/com/icbc/sh/techmg/common/constant/ResultCode.java`
- Create: `backend/techmg-common/src/main/java/com/icbc/sh/techmg/common/model/R.java`
- Create: `backend/techmg-common/src/main/java/com/icbc/sh/techmg/common/exception/BusinessException.java`
- Create: `backend/techmg-common/src/main/java/com/icbc/sh/techmg/common/exception/GlobalExceptionHandler.java`

**Interfaces:**
- Produces:
  - `BaseEntity`: 抽象基类，含 id/createTime/createBy/updateTime/updateBy/deleted 字段
  - `ResultCode`: 枚举，SUCCESS(200)/PARAM_ERROR(400)/UNAUTHORIZED(401)/FORBIDDEN(403)/NOT_FOUND(404)/INTERNAL_ERROR(500)
  - `R<T>`: 统一响应体，静态工厂方法 `R.ok(T data)` / `R.fail(ResultCode code, String msg)`
  - `BusinessException`: 继承 RuntimeException，携带 ResultCode
  - `GlobalExceptionHandler`: `@RestControllerAdvice`，处理 BusinessException 和兜底 Exception

- [ ] **Step 1: 创建模块 POM**

`backend/techmg-common/pom.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.icbc.sh</groupId>
        <artifactId>techmg</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>
    <artifactId>techmg-common</artifactId>
    <name>techmg-common</name>
    <description>公共模块 - 基础实体、常量、异常、响应模型、工具类</description>

    <dependencies>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
        </dependency>
    </dependencies>
</project>
```

- [ ] **Step 2: 创建 BaseEntity**

`backend/techmg-common/src/main/java/com/icbc/sh/techmg/common/base/BaseEntity.java`：

```java
package com.icbc.sh.techmg.common.base;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public abstract class BaseEntity implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
```

- [ ] **Step 3: 创建 ResultCode**

`backend/techmg-common/src/main/java/com/icbc/sh/techmg/common/constant/ResultCode.java`：

```java
package com.icbc.sh.techmg.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_ERROR(500, "系统内部错误"),
    BUSINESS_ERROR(1001, "业务异常");

    private final int code;
    private final String message;
}
```

- [ ] **Step 4: 创建统一响应模型 R<T>**

`backend/techmg-common/src/main/java/com/icbc/sh/techmg/common/model/R.java`：

```java
package com.icbc.sh.techmg.common.model;

import com.icbc.sh.techmg.common.constant.ResultCode;
import lombok.Data;

@Data
public class R<T> {

    private int code;
    private String message;
    private T data;

    private R() {}

    public static <T> R<T> ok() {
        return ok(null);
    }

    public static <T> R<T> ok(T data) {
        R<T> r = new R<>();
        r.code = ResultCode.SUCCESS.getCode();
        r.message = ResultCode.SUCCESS.getMessage();
        r.data = data;
        return r;
    }

    public static <T> R<T> fail(ResultCode code) {
        return fail(code, code.getMessage());
    }

    public static <T> R<T> fail(ResultCode code, String message) {
        R<T> r = new R<>();
        r.code = code.getCode();
        r.message = message;
        return r;
    }

    public static <T> R<T> fail(int code, String message) {
        R<T> r = new R<>();
        r.code = code;
        r.message = message;
        return r;
    }
}
```

- [ ] **Step 5: 创建 BusinessException**

`backend/techmg-common/src/main/java/com/icbc/sh/techmg/common/exception/BusinessException.java`：

```java
package com.icbc.sh.techmg.common.exception;

import com.icbc.sh.techmg.common.constant.ResultCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
}
```

- [ ] **Step 6: 创建 GlobalExceptionHandler**

`backend/techmg-common/src/main/java/com/icbc/sh/techmg/common/exception/GlobalExceptionHandler.java`：

```java
package com.icbc.sh.techmg.common.exception;

import com.icbc.sh.techmg.common.constant.ResultCode;
import com.icbc.sh.techmg.common.model.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public R<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return R.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return R.fail(ResultCode.INTERNAL_ERROR);
    }
}
```

- [ ] **Step 7: 验证编译**

```bash
mvn compile -pl techmg-common
```
Expected: BUILD SUCCESS

- [ ] **Step 8: Commit**

```bash
git add techmg-common/
git commit -m "feat(common): add BaseEntity, ResultCode, R<T>, BusinessException, GlobalExceptionHandler"
```

---

### Task 3: 创建 techmg-framework 模块 — 核心配置

**Files:**
- Create: `backend/techmg-framework/pom.xml`
- Create: `backend/techmg-framework/src/main/java/com/icbc/sh/techmg/framework/web/WebConfig.java`
- Create: `backend/techmg-framework/src/main/java/com/icbc/sh/techmg/framework/web/GsonConfig.java`
- Create: `backend/techmg-framework/src/main/java/com/icbc/sh/techmg/framework/mybatis/MyBatisPlusConfig.java`
- Create: `backend/techmg-framework/src/main/java/com/icbc/sh/techmg/framework/mybatis/MyMetaObjectHandler.java`
- Create: `backend/techmg-framework/src/main/java/com/icbc/sh/techmg/framework/datasource/DruidConfig.java`
- Create: `backend/techmg-framework/src/main/java/com/icbc/sh/techmg/framework/log/ApiAccessLog.java`
- Create: `backend/techmg-framework/src/main/java/com/icbc/sh/techmg/framework/log/ApiAccessLogAspect.java`
- Create: `backend/techmg-framework/src/main/resources/log4j2.xml`

**Interfaces:**
- Produces:
  - `WebConfig`: 跨域 `addCorsMappings`，`ResponseBodyAdvice` 自动包装 `R<T>`
  - `GsonConfig`: `HttpMessageConverter` 替换 Jackson 为 Gson
  - `MyBatisPlusConfig`: 分页插件 `PaginationInnerInterceptor`
  - `MyMetaObjectHandler`: 自动填充 `createTime/createBy/updateTime/updateBy`
  - `@ApiAccessLog`: 自定义注解，记录接口 IP/耗时/参数/返回值/异常
  - `ApiAccessLogAspect`: AOP 切面实现
  - `DruidConfig`: 占位配置（Task 7 在 admin 中配置多数据源）

- [ ] **Step 1: 创建模块 POM**

`backend/techmg-framework/pom.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.icbc.sh</groupId>
        <artifactId>techmg</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>
    <artifactId>techmg-framework</artifactId>
    <name>techmg-framework</name>
    <description>框架模块 - Security, Redis, MyBatis, Datasource, Excel, Web, Log</description>

    <dependencies>
        <dependency>
            <groupId>com.icbc.sh</groupId>
            <artifactId>techmg-common</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid-spring-boot-3-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>dynamic-datasource-spring-boot3-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
    </dependencies>
</project>
```

- [ ] **Step 2: 创建 WebConfig（跨域 + 响应包装）**

`backend/techmg-framework/src/main/java/com/icbc/sh/techmg/framework/web/WebConfig.java`：

```java
package com.icbc.sh.techmg.framework.web;

import com.icbc.sh.techmg.common.model.R;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@Configuration
public class WebConfig implements WebMvcConfigurer, ResponseBodyAdvice<Object> {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    // ResponseBodyAdvice — auto-wrap non-R responses

    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        return !returnType.getParameterType().equals(R.class);
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                   MethodParameter returnType,
                                   MediaType selectedContentType,
                                   Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                   ServerHttpRequest request,
                                   ServerHttpResponse response) {
        if (body instanceof String) {
            return body; // String 特殊处理，由 Gson 序列化时不再二次包装
        }
        return R.ok(body);
    }
}
```

- [ ] **Step 3: 创建 GsonConfig**

`backend/techmg-framework/src/main/java/com/icbc/sh/techmg/framework/web/GsonConfig.java`：

```java
package com.icbc.sh.techmg.framework.web;

import com.google.gson.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Configuration
public class GsonConfig implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>)
                        (src, type, context) ->
                                new JsonPrimitive(src.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
                .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>)
                        (json, type, context) ->
                                LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .create();

        GsonHttpMessageConverter converter = new GsonHttpMessageConverter();
        converter.setGson(gson);
        converters.add(0, converter);
    }
}
```

- [ ] **Step 4: 创建 MyBatisPlusConfig**

`backend/techmg-framework/src/main/java/com/icbc/sh/techmg/framework/mybatis/MyBatisPlusConfig.java`：

```java
package com.icbc.sh.techmg.framework.mybatis;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyBatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
```

- [ ] **Step 5: 创建 MyMetaObjectHandler**

`backend/techmg-framework/src/main/java/com/icbc/sh/techmg/framework/mybatis/MyMetaObjectHandler.java`：

```java
package com.icbc.sh.techmg.framework.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "deleted", Integer.class, 0);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}
```

- [ ] **Step 6: 创建 @ApiAccessLog 注解**

`backend/techmg-framework/src/main/java/com/icbc/sh/techmg/framework/log/ApiAccessLog.java`：

```java
package com.icbc.sh.techmg.framework.log;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiAccessLog {
    String value() default "";
}
```

- [ ] **Step 7: 创建 ApiAccessLogAspect**

`backend/techmg-framework/src/main/java/com/icbc/sh/techmg/framework/log/ApiAccessLogAspect.java`：

```java
package com.icbc.sh.techmg.framework.log;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Slf4j
@Aspect
@Component
public class ApiAccessLogAspect {

    private final Gson gson = new Gson();

    @Around("@annotation(com.icbc.sh.techmg.framework.log.ApiAccessLog)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

        String url = request != null ? request.getRequestURI() : "";
        String method = request != null ? request.getMethod() : "";
        String ip = request != null ? request.getRemoteAddr() : "";

        MethodSignature signature = (MethodSignature) point.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();

        long start = System.currentTimeMillis();
        try {
            Object result = point.proceed();
            long cost = System.currentTimeMillis() - start;
            log.info("[API] {} {} | {}.{} | IP:{} | cost:{}ms | result:{}",
                    method, url, className, methodName, ip, cost,
                    result != null ? gson.toJson(result) : "null");
            return result;
        } catch (Throwable e) {
            long cost = System.currentTimeMillis() - start;
            log.error("[API] {} {} | {}.{} | IP:{} | cost:{}ms | error:{}",
                    method, url, className, methodName, ip, cost, e.getMessage(), e);
            throw e;
        }
    }
}
```

- [ ] **Step 8: 创建 log4j2.xml**

`backend/techmg-framework/src/main/resources/log4j2.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN" monitorInterval="30">
    <Properties>
        <Property name="LOG_PATTERN">%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n</Property>
        <Property name="LOG_PATH">logs</Property>
    </Properties>

    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="${LOG_PATTERN}"/>
        </Console>

        <RollingFile name="RollingFile" fileName="${LOG_PATH}/techmg.log"
                     filePattern="${LOG_PATH}/techmg-%d{yyyy-MM-dd}-%i.log.gz">
            <PatternLayout pattern="${LOG_PATTERN}"/>
            <Policies>
                <TimeBasedTriggeringPolicy interval="1"/>
                <SizeBasedTriggeringPolicy size="100 MB"/>
            </Policies>
            <DefaultRolloverStrategy max="30"/>
        </RollingFile>

        <RollingFile name="ErrorFile" fileName="${LOG_PATH}/techmg-error.log"
                     filePattern="${LOG_PATH}/techmg-error-%d{yyyy-MM-dd}-%i.log.gz">
            <PatternLayout pattern="${LOG_PATTERN}"/>
            <ThresholdFilter level="ERROR" onMatch="ACCEPT" onMismatch="DENY"/>
            <Policies>
                <TimeBasedTriggeringPolicy interval="1"/>
                <SizeBasedTriggeringPolicy size="100 MB"/>
            </Policies>
            <DefaultRolloverStrategy max="30"/>
        </RollingFile>
    </Appenders>

    <Loggers>
        <Logger name="com.icbc.sh.techmg" level="DEBUG" additivity="false">
            <AppenderRef ref="RollingFile"/>
            <AppenderRef ref="ErrorFile"/>
            <AppenderRef ref="Console"/>
        </Logger>
        <Root level="INFO">
            <AppenderRef ref="Console"/>
            <AppenderRef ref="RollingFile"/>
            <AppenderRef ref="ErrorFile"/>
        </Root>
    </Loggers>
</Configuration>
```

- [ ] **Step 9: 验证编译**

```bash
mvn compile -pl techmg-framework
```
Expected: BUILD SUCCESS

- [ ] **Step 10: Commit**

```bash
git add techmg-framework/
git commit -m "feat(framework): add WebConfig, GsonConfig, MyBatisPlus, ApiAccessLog AOP, Log4j2"
```

---

### Task 4: 创建 techmg-system 模块（用户/角色/菜单/部门骨架）

**Files:**
- Create: `backend/techmg-system/pom.xml`
- Create: `backend/techmg-system/src/main/java/com/icbc/sh/techmg/system/entity/SysUser.java`
- Create: `backend/techmg-system/src/main/java/com/icbc/sh/techmg/system/entity/SysRole.java`
- Create: `backend/techmg-system/src/main/java/com/icbc/sh/techmg/system/entity/SysMenu.java`
- Create: `backend/techmg-system/src/main/java/com/icbc/sh/techmg/system/entity/SysDept.java`
- Create: `backend/techmg-system/src/main/java/com/icbc/sh/techmg/system/mapper/SysUserMapper.java`
- Create: `backend/techmg-system/src/main/java/com/icbc/sh/techmg/system/mapper/SysRoleMapper.java`
- Create: `backend/techmg-system/src/main/java/com/icbc/sh/techmg/system/mapper/SysMenuMapper.java`
- Create: `backend/techmg-system/src/main/java/com/icbc/sh/techmg/system/mapper/SysDeptMapper.java`
- Create: `backend/techmg-system/src/main/java/com/icbc/sh/techmg/system/service/SysUserService.java`
- Create: `backend/techmg-system/src/main/java/com/icbc/sh/techmg/system/service/impl/SysUserServiceImpl.java`

**Interfaces:**
- Produces:
  - 4 个实体类继承 `BaseEntity`，映射 `sys_user`/`sys_role`/`sys_menu`/`sys_dept` 表
  - 4 个 Mapper 接口继承 `BaseMapper<T>`
  - `SysUserService` 接口 + `SysUserServiceImpl` 实现

- [ ] **Step 1: 创建模块 POM**

`backend/techmg-system/pom.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.icbc.sh</groupId>
        <artifactId>techmg</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>
    <artifactId>techmg-system</artifactId>
    <name>techmg-system</name>
    <description>系统管理模块 - 用户、角色、权限、菜单、部门</description>

    <dependencies>
        <dependency>
            <groupId>com.icbc.sh</groupId>
            <artifactId>techmg-framework</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
            <scope>provided</scope>
        </dependency>
    </dependencies>
</project>
```

- [ ] **Step 2: 创建实体类 SysUser**

`backend/techmg-system/src/main/java/com/icbc/sh/techmg/system/entity/SysUser.java`：

```java
package com.icbc.sh.techmg.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.icbc.sh.techmg.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {
    private String authNo;
    private String username;
    private String realName;
    private String email;
    private String phone;
    private Long deptId;
    private Integer status;  // 0-禁用, 1-启用
}
```

- [ ] **Step 3: 创建 SysRole**

`backend/techmg-system/src/main/java/com/icbc/sh/techmg/system/entity/SysRole.java`：

```java
package com.icbc.sh.techmg.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.icbc.sh.techmg.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {
    private String roleName;
    private String roleCode;
    private String description;
    private Integer sort;
    private Integer status;
}
```

- [ ] **Step 4: 创建 SysMenu**

`backend/techmg-system/src/main/java/com/icbc/sh/techmg/system/entity/SysMenu.java`：

```java
package com.icbc.sh.techmg.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.icbc.sh.techmg.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
public class SysMenu extends BaseEntity {
    private Long parentId;
    private String menuName;
    private String path;
    private String component;
    private String icon;
    private Integer type;    // 0-目录, 1-菜单, 2-按钮
    private String perms;
    private Integer sort;
    private Integer status;
    private Integer visible;
}
```

- [ ] **Step 5: 创建 SysDept**

`backend/techmg-system/src/main/java/com/icbc/sh/techmg/system/entity/SysDept.java`：

```java
package com.icbc.sh.techmg.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.icbc.sh.techmg.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dept")
public class SysDept extends BaseEntity {
    private String deptName;
    private String deptCode;
    private Long parentId;
    private String ancestors;
    private Integer sort;
    private Integer status;
}
```

- [ ] **Step 6: 创建 Mapper 接口**

`backend/techmg-system/src/main/java/com/icbc/sh/techmg/system/mapper/SysUserMapper.java`：

```java
package com.icbc.sh.techmg.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.icbc.sh.techmg.system.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
```

`backend/techmg-system/src/main/java/com/icbc/sh/techmg/system/mapper/SysRoleMapper.java`：

```java
package com.icbc.sh.techmg.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.icbc.sh.techmg.system.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {
}
```

`backend/techmg-system/src/main/java/com/icbc/sh/techmg/system/mapper/SysMenuMapper.java`：

```java
package com.icbc.sh.techmg.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.icbc.sh.techmg.system.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {
}
```

`backend/techmg-system/src/main/java/com/icbc/sh/techmg/system/mapper/SysDeptMapper.java`：

```java
package com.icbc.sh.techmg.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.icbc.sh.techmg.system.entity.SysDept;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysDeptMapper extends BaseMapper<SysDept> {
}
```

- [ ] **Step 7: 创建 Service 接口和实现**

`backend/techmg-system/src/main/java/com/icbc/sh/techmg/system/service/SysUserService.java`：

```java
package com.icbc.sh.techmg.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.icbc.sh.techmg.system.entity.SysUser;

public interface SysUserService extends IService<SysUser> {
    SysUser getByAuthNo(String authNo);
}
```

`backend/techmg-system/src/main/java/com/icbc/sh/techmg/system/service/impl/SysUserServiceImpl.java`：

```java
package com.icbc.sh.techmg.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.icbc.sh.techmg.system.entity.SysUser;
import com.icbc.sh.techmg.system.mapper.SysUserMapper;
import com.icbc.sh.techmg.system.service.SysUserService;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    public SysUser getByAuthNo(String authNo) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getAuthNo, authNo);
        return this.getOne(wrapper);
    }
}
```

- [ ] **Step 8: 验证编译**

```bash
mvn compile -pl techmg-system
```
Expected: BUILD SUCCESS

- [ ] **Step 9: Commit**

```bash
git add techmg-system/
git commit -m "feat(system): add SysUser, SysRole, SysMenu, SysDept entities, mappers, service"
```

---

### Task 5: 创建 techmg-business-api 与 techmg-business-service 模块

**Files:**
- Create: `backend/techmg-business-api/pom.xml`
- Create: `backend/techmg-business-service/pom.xml`
- Create: `backend/techmg-business-service/src/main/java/com/icbc/sh/techmg/business/controller/HealthController.java`

**Interfaces:**
- Produces: 两个模块的 POM 与一个健康检查接口

- [ ] **Step 1: 创建 business-api POM**

`backend/techmg-business-api/pom.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.icbc.sh</groupId>
        <artifactId>techmg</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>
    <artifactId>techmg-business-api</artifactId>
    <name>techmg-business-api</name>
    <description>业务接口定义 - DTO、Service接口、Feign接口</description>

    <dependencies>
        <dependency>
            <groupId>com.icbc.sh</groupId>
            <artifactId>techmg-common</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
</project>
```

- [ ] **Step 2: 创建 business-service POM**

`backend/techmg-business-service/pom.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.icbc.sh</groupId>
        <artifactId>techmg</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>
    <artifactId>techmg-business-service</artifactId>
    <name>techmg-business-service</name>
    <description>业务接口实现 - 技术治理、数据库治理、数据库巡检、资产管理</description>

    <dependencies>
        <dependency>
            <groupId>com.icbc.sh</groupId>
            <artifactId>techmg-framework</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>com.icbc.sh</groupId>
            <artifactId>techmg-business-api</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
</project>
```

- [ ] **Step 3: 创建健康检查接口**

`backend/techmg-business-service/src/main/java/com/icbc/sh/techmg/business/controller/HealthController.java`：

```java
package com.icbc.sh.techmg.business.controller;

import com.icbc.sh.techmg.common.model.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/api/health")
    public R<Map<String, String>> health() {
        return R.ok(Map.of("status", "UP", "service", "techmg"));
    }
}
```

- [ ] **Step 4: 验证编译**

```bash
mvn compile -pl techmg-business-api,techmg-business-service
```
Expected: BUILD SUCCESS

- [ ] **Step 5: Commit**

```bash
git add techmg-business-api/ techmg-business-service/
git commit -m "feat(business): add business-api and business-service modules with health check"
```

---

### Task 6: 创建 techmg-admin 启动模块

**Files:**
- Create: `backend/techmg-admin/pom.xml`
- Create: `backend/techmg-admin/src/main/java/com/icbc/sh/techmg/TechmgApplication.java`
- Create: `backend/techmg-admin/src/main/resources/application.yml`
- Create: `backend/techmg-admin/src/main/resources/application-dev.yml`
- Create: `backend/techmg-admin/src/main/resources/application-test.yml`
- Create: `backend/techmg-admin/src/main/resources/application-prod.yml`

**Interfaces:**
- Produces: Spring Boot 启动类和三个环境配置文件

- [ ] **Step 1: 创建 admin POM**

`backend/techmg-admin/pom.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.icbc.sh</groupId>
        <artifactId>techmg</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>
    <artifactId>techmg-admin</artifactId>
    <name>techmg-admin</name>
    <description>启动模块 - Spring Boot 入口，聚合装配</description>

    <dependencies>
        <dependency>
            <groupId>com.icbc.sh</groupId>
            <artifactId>techmg-system</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>com.icbc.sh</groupId>
            <artifactId>techmg-business-service</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
            <exclusions>
                <exclusion>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-logging</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-log4j2</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 2: 创建启动类**

`backend/techmg-admin/src/main/java/com/icbc/sh/techmg/TechmgApplication.java`：

```java
package com.icbc.sh.techmg;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan({"com.icbc.sh.techmg.system.mapper", "com.icbc.sh.techmg.business.mapper"})
public class TechmgApplication {

    public static void main(String[] args) {
        SpringApplication.run(TechmgApplication.class, args);
    }
}
```

- [ ] **Step 3: 创建 application.yml**

`backend/techmg-admin/src/main/resources/application.yml`：

```yaml
server:
  port: 8080

spring:
  profiles:
    active: dev
  application:
    name: techmg
  autoconfigure:
    exclude: com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure

  # 多数据源配置（dynamic-datasource）
  datasource:
    dynamic:
      primary: master
      strict: false
      datasource:
        master:
          url: jdbc:mysql://localhost:3306/techmg?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false
          username: root
          password: root
          driver-class-name: com.mysql.cj.jdbc.Driver
          type: com.alibaba.druid.pool.DruidDataSource
        slave:
          url: jdbc:mysql://localhost:3306/techmg?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false
          username: root
          password: root
          driver-class-name: com.mysql.cj.jdbc.Driver
          type: com.alibaba.druid.pool.DruidDataSource

  # Druid 连接池配置
  druid:
    stat-view-servlet:
      enabled: false
    web-stat-filter:
      enabled: true
    filter:
      stat:
        enabled: true
        log-slow-sql: true
        slow-sql-millis: 2000
      wall:
        enabled: true

  # Redis
  data:
    redis:
      host: localhost
      port: 6379
      password:
      database: 0
      lettuce:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 0

# MyBatis-Plus
mybatis-plus:
  mapper-locations: classpath*:/mapper/**/*.xml
  type-aliases-package: com.icbc.sh.techmg
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
  configuration:
    log-impl: org.apache.ibatis.logging.slf4j.Slf4jImpl
    map-underscore-to-camel-case: true

# SpringDoc
springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html

# JWT
jwt:
  secret: techmg-jwt-secret-key-2024-shanghai-icbc-research-base
  expiration: 86400000  # 24h
```

- [ ] **Step 4: 创建 application-dev.yml**

`backend/techmg-admin/src/main/resources/application-dev.yml`：

```yaml
server:
  port: 8080

spring:
  datasource:
    dynamic:
      datasource:
        master:
          url: jdbc:mysql://localhost:3306/techmg?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false
          username: root
          password: root
        slave:
          url: jdbc:mysql://localhost:3306/techmg?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false
          username: root
          password: root

logging:
  level:
    com.icbc.sh.techmg: DEBUG
```

- [ ] **Step 5: 创建 application-test.yml**

`backend/techmg-admin/src/main/resources/application-test.yml`：

```yaml
server:
  port: 8080

spring:
  datasource:
    dynamic:
      datasource:
        master:
          url: jdbc:mysql://test-db:3306/techmg?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false
          username: techmg_user
          password: ${DB_PASSWORD}
        slave:
          url: jdbc:mysql://test-db:3306/techmg?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false
          username: techmg_user
          password: ${DB_PASSWORD}

logging:
  level:
    com.icbc.sh.techmg: INFO
```

- [ ] **Step 6: 创建 application-prod.yml**

`backend/techmg-admin/src/main/resources/application-prod.yml`：

```yaml
server:
  port: 8080

spring:
  datasource:
    dynamic:
      datasource:
        master:
          url: jdbc:mysql://prod-db:3306/techmg?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=true
          username: techmg_user
          password: ${DB_PASSWORD}
        slave:
          url: jdbc:mysql://prod-db-slave:3306/techmg?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=true
          username: techmg_user
          password: ${DB_PASSWORD}

  druid:
    stat-view-servlet:
      enabled: false

logging:
  level:
    com.icbc.sh.techmg: INFO
```

- [ ] **Step 7: 验证编译**

```bash
mvn compile -pl techmg-admin
```
Expected: BUILD SUCCESS

- [ ] **Step 8: Commit**

```bash
git add techmg-admin/
git commit -m "feat(admin): add Spring Boot entry point with dev/test/prod configs"
```

---

### Task 7: 创建 SQL 初始化脚本

**Files:**
- Create: `backend/techmg-admin/src/main/resources/sql/init.sql`

**Interfaces:**
- Produces: 完整 DDL + 初始角色 DML

- [ ] **Step 1: 创建 init.sql**

`backend/techmg-admin/src/main/resources/sql/init.sql`：

```sql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS techmg DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE techmg;

-- 部门表
CREATE TABLE IF NOT EXISTS `sys_dept` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `dept_name` VARCHAR(100) NOT NULL COMMENT '部门名称',
    `dept_code` VARCHAR(50) NOT NULL COMMENT '部门编码',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父部门ID',
    `ancestors` VARCHAR(500) DEFAULT '' COMMENT '祖级列表',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态 0禁用 1启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `create_by` VARCHAR(64) DEFAULT '',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by` VARCHAR(64) DEFAULT '',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除 0正常 1删除',
    PRIMARY KEY (`id`),
    INDEX `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- 用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `auth_no` VARCHAR(50) NOT NULL COMMENT '统一认证号',
    `username` VARCHAR(64) NOT NULL COMMENT '用户名',
    `real_name` VARCHAR(64) DEFAULT '' COMMENT '真实姓名',
    `email` VARCHAR(128) DEFAULT '' COMMENT '邮箱',
    `phone` VARCHAR(20) DEFAULT '' COMMENT '手机号',
    `dept_id` BIGINT DEFAULT NULL COMMENT '部门ID',
    `status` TINYINT DEFAULT 1 COMMENT '状态 0禁用 1启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `create_by` VARCHAR(64) DEFAULT '',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by` VARCHAR(64) DEFAULT '',
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_auth_no` (`auth_no`),
    INDEX `idx_dept_id` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS `sys_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `role_name` VARCHAR(64) NOT NULL COMMENT '角色名称',
    `role_code` VARCHAR(50) NOT NULL COMMENT '角色编码',
    `description` VARCHAR(200) DEFAULT '' COMMENT '描述',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `create_by` VARCHAR(64) DEFAULT '',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by` VARCHAR(64) DEFAULT '',
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS `sys_user_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 菜单表
CREATE TABLE IF NOT EXISTS `sys_menu` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `parent_id` BIGINT DEFAULT 0 COMMENT '父菜单ID',
    `menu_name` VARCHAR(50) NOT NULL COMMENT '菜单名称',
    `path` VARCHAR(200) DEFAULT '' COMMENT '路由地址',
    `component` VARCHAR(255) DEFAULT '' COMMENT '组件路径',
    `icon` VARCHAR(100) DEFAULT '' COMMENT '图标',
    `type` TINYINT DEFAULT 1 COMMENT '类型 0目录 1菜单 2按钮',
    `perms` VARCHAR(100) DEFAULT '' COMMENT '权限标识',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态',
    `visible` TINYINT DEFAULT 1 COMMENT '是否可见 0隐藏 1可见',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `create_by` VARCHAR(64) DEFAULT '',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by` VARCHAR(64) DEFAULT '',
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    INDEX `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- 角色菜单关联表
CREATE TABLE IF NOT EXISTS `sys_role_menu` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `menu_id` BIGINT NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (`id`),
    INDEX `idx_role_id` (`role_id`),
    INDEX `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- 操作日志表
CREATE TABLE IF NOT EXISTS `sys_operation_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `module` VARCHAR(100) DEFAULT '' COMMENT '模块',
    `operation` VARCHAR(100) DEFAULT '' COMMENT '操作类型',
    `operator` VARCHAR(64) DEFAULT '' COMMENT '操作人',
    `request_param` TEXT COMMENT '请求参数',
    `response_result` TEXT COMMENT '响应结果',
    `ip` VARCHAR(50) DEFAULT '' COMMENT 'IP地址',
    `cost_time` BIGINT DEFAULT 0 COMMENT '耗时(ms)',
    `status` TINYINT DEFAULT 1 COMMENT '状态 0失败 1成功',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 初始角色数据
INSERT INTO `sys_role` (`role_name`, `role_code`, `description`, `sort`, `status`) VALUES
('平台管理员', 'PLATFORM_ADMIN', '全局管理所有用户/角色/权限/菜单/部门配置', 1, 1),
('部门管理员', 'DEPT_ADMIN', '本部门用户管理、本部门资产管理', 2, 1),
('部门DBA', 'DEPT_DBA', '数据库治理/巡检任务操作、数据库参数管理', 3, 1),
('普通用户', 'NORMAL_USER', '填写反馈、确认治理任务、查看本部门数据', 4, 1),
('游客', 'GUEST', '查看公开报表、治理情况概览（只读）', 5, 1);
```

- [ ] **Step 2: Commit**

```bash
git add techmg-admin/src/main/resources/sql/
git commit -m "feat(sql): add init DDL with 5 default roles"
```

---

### Task 8: 创建前端项目骨架

**Files:**
- Create: `vue-front/package.json`
- Create: `vue-front/vite.config.js`
- Create: `vue-front/index.html`
- Create: `vue-front/.env.development`
- Create: `vue-front/.env.test`
- Create: `vue-front/.env.production`
- Create: `vue-front/src/main.js`
- Create: `vue-front/src/App.vue`
- Create: `vue-front/src/utils/request.js`
- Create: `vue-front/src/store/user.js`
- Create: `vue-front/src/router/index.js`

- [ ] **Step 1: 创建 package.json**

`vue-front/package.json`：

```json
{
  "name": "techmg-web",
  "version": "1.0.0",
  "private": true,
  "type": "module",
  "scripts": {
    "dev": "vite --mode development",
    "build:test": "vite build --mode test",
    "build:prod": "vite build --mode production",
    "preview": "vite preview"
  },
  "dependencies": {
    "vue": "3.3.9",
    "vue-router": "4.2.5",
    "pinia": "2.1.4",
    "axios": "1.7.2",
    "element-plus": "2.5.0",
    "@element-plus/icons-vue": "2.3.1",
    "echarts": "5.4.3"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "4.5.2",
    "vite": "5.0.4",
    "sass": "1.66.1"
  }
}
```

- [ ] **Step 2: 创建 vite.config.js**

`vue-front/vite.config.js`：

```javascript
import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd())
  return {
    plugins: [vue()],
    resolve: {
      alias: {
        '@': path.resolve(__dirname, 'src')
      }
    },
    server: {
      port: 5173,
      proxy: {
        '/api': {
          target: env.VITE_API_BASE_URL || 'http://localhost:8080',
          changeOrigin: true
        }
      }
    }
  }
})
```

- [ ] **Step 3: 创建 index.html**

`vue-front/index.html`：

```html
<!DOCTYPE html>
<html lang="zh-CN">
  <head>
    <meta charset="UTF-8" />
    <link rel="icon" type="image/svg+xml" href="/vite.svg" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>技术管理平台</title>
  </head>
  <body>
    <div id="app"></div>
    <script type="module" src="/src/main.js"></script>
  </body>
</html>
```

- [ ] **Step 4: 创建环境变量文件**

`.env.development`：

```env
VITE_API_BASE_URL=http://localhost:8080
VITE_APP_TITLE=技术管理平台(开发)
```

`.env.test`：

```env
VITE_API_BASE_URL=http://test-server:8080
VITE_APP_TITLE=技术管理平台(测试)
```

`.env.production`：

```env
VITE_API_BASE_URL=http://prod-server:8080
VITE_APP_TITLE=技术管理平台
```

- [ ] **Step 5: 创建 src/main.js**

`vue-front/src/main.js`：

```javascript
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'
import router from './router'
import './styles/index.scss'

const app = createApp(App)

// 注册所有 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(createPinia())
app.use(router)
app.use(ElementPlus)
app.mount('#app')
```

- [ ] **Step 6: 创建 App.vue**

`vue-front/src/App.vue`：

```vue
<template>
  <router-view />
</template>

<script setup>
</script>
```

- [ ] **Step 7: 创建 axios 请求封装**

`vue-front/src/utils/request.js`：

```javascript
import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 30000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    // Blob 文件下载，直接返回
    if (response.config.responseType === 'blob') {
      return response.data
    }

    const res = response.data
    if (res.code === 200) {
      return res.data
    }

    // 按错误码分类提示
    switch (res.code) {
      case 401:
        ElMessage.error('登录已过期，请重新登录')
        localStorage.removeItem('token')
        window.location.href = '/login'
        break
      case 403:
        ElMessage.error('无权限访问')
        break
      case 404:
        ElMessage.error('请求的资源不存在')
        break
      case 500:
        ElMessage.error('服务器内部错误')
        break
      default:
        ElMessage.error(res.message || '请求失败')
    }
    return Promise.reject(new Error(res.message || '请求失败'))
  },
  error => {
    ElMessage.error(error.message || '网络异常')
    return Promise.reject(error)
  }
)

export default request
```

- [ ] **Step 8: 创建 Pinia user store**

`vue-front/src/store/user.js`：

```javascript
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))
  const roles = ref([])
  const permissions = ref([])

  const isLoggedIn = computed(() => !!token.value)

  function setToken(val) {
    token.value = val
    localStorage.setItem('token', val)
  }

  function setUserInfo(info) {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    roles.value = []
    permissions.value = []
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  return { token, userInfo, roles, permissions, isLoggedIn, setToken, setUserInfo, logout }
})
```

- [ ] **Step 9: 创建路由**

`vue-front/src/router/index.js`：

```javascript
import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/layout/DefaultLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页工作台', icon: 'HomeFilled' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  if (to.path !== '/login' && !userStore.isLoggedIn) {
    next('/login')
  } else {
    next()
  }
})

export default router
```

- [ ] **Step 10: Commit**

```bash
git add vue-front/
git commit -m "feat(web): init Vue3+Vite project with axios, pinia, router, sass"
```

---

### Task 9: 创建前端布局组件

**Files:**
- Create: `vue-front/src/layout/DefaultLayout.vue`
- Create: `vue-front/src/layout/TopBar.vue`
- Create: `vue-front/src/layout/Sidebar.vue`
- Create: `vue-front/src/layout/TopOnly.layout.vue`
- Create: `vue-front/src/store/app.js`

**Interfaces:**
- Produces: 双布局模式组件，默认上左右布局

- [ ] **Step 1: 创建 app store（布局模式切换）**

`vue-front/src/store/app.js`：

```javascript
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  // 'side' = 上左右布局, 'top' = 上下布局
  const layout = ref(localStorage.getItem('layout') || 'side')

  function setLayout(mode) {
    layout.value = mode
    localStorage.setItem('layout', mode)
  }

  return { layout, setLayout }
})
```

- [ ] **Step 2: 创建 DefaultLayout（上左右布局 - 默认）**

`vue-front/src/layout/DefaultLayout.vue`：

```vue
<template>
  <div class="layout-side">
    <TopBar />
    <div class="layout-body">
      <Sidebar />
      <main class="layout-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import TopBar from './TopBar.vue'
import Sidebar from './Sidebar.vue'
</script>

<style lang="scss" scoped>
.layout-side {
  display: flex;
  flex-direction: column;
  height: 100vh;
}
.layout-body {
  display: flex;
  flex: 1;
  overflow: hidden;
}
.layout-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #f5f7fa;
}
</style>
```

- [ ] **Step 3: 创建 TopBar**

`vue-front/src/layout/TopBar.vue`：

```vue
<template>
  <header class="topbar">
    <div class="topbar-left">
      <h1 class="topbar-title">技术管理平台</h1>
    </div>
    <div class="topbar-right">
      <span class="user-info">{{ userStore.userInfo?.realName || '未登录' }}</span>
      <el-button text @click="handleLogout">退出</el-button>
    </div>
  </header>
</template>

<script setup>
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

function handleLogout() {
  userStore.logout()
  window.location.href = '/login'
}
</script>

<style lang="scss" scoped>
.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 56px;
  padding: 0 20px;
  background-color: #fff;
  border-bottom: 1px solid #e4e7ed;
  .topbar-title {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
  }
  .user-info {
    margin-right: 12px;
    color: #606266;
  }
}
</style>
```

- [ ] **Step 4: 创建 Sidebar**

`vue-front/src/layout/Sidebar.vue`：

```vue
<template>
  <aside class="sidebar">
    <el-menu
      :default-active="activeMenu"
      background-color="#304156"
      text-color="#bfcbd9"
      active-text-color="#409EFF"
      router
    >
      <el-menu-item index="/dashboard">
        <el-icon><HomeFilled /></el-icon>
        <span>首页工作台</span>
      </el-menu-item>
    </el-menu>
  </aside>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const activeMenu = computed(() => route.path)
</script>

<style lang="scss" scoped>
.sidebar {
  width: 220px;
  min-height: calc(100vh - 56px);
  background-color: #304156;
  overflow-y: auto;
  .el-menu {
    border-right: none;
  }
}
</style>
```

- [ ] **Step 5: Commit**

```bash
git add vue-front/src/layout/ techmg-web/src/store/app.js
git commit -m "feat(web): add dual-layout components (side and top modes, default side)"
```

---

### Task 10: 创建前端视图占位页

**Files:**
- Create: `vue-front/src/views/login/index.vue`
- Create: `vue-front/src/views/dashboard/index.vue`
- Create: `vue-front/src/styles/index.scss`

**Interfaces:**
- Produces: 登录页 + 仪表盘首页 + 全局样式

- [ ] **Step 1: 创建登录页**

`vue-front/src/views/login/index.vue`：

```vue
<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2>技术管理平台</h2>
      <el-form :model="form" label-width="80px">
        <el-form-item label="认证号">
          <el-input v-model="form.authNo" placeholder="请输入统一认证号" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width:100%" @click="handleLogin">登 录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { ElMessage } from 'element-plus'

const form = reactive({ authNo: '', password: '' })

function handleLogin() {
  // TODO: 对接后端登录接口
  ElMessage.info('登录功能待对接后端')
}
</script>

<style lang="scss" scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f7fa;
}
.login-card {
  width: 400px;
  h2 {
    text-align: center;
    margin-bottom: 24px;
  }
}
</style>
```

- [ ] **Step 2: 创建仪表盘首页**

`vue-front/src/views/dashboard/index.vue`：

```vue
<template>
  <div class="dashboard">
    <h2>首页工作台</h2>
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card>
          <template #header>技术治理任务</template>
          <div class="stat">--</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <template #header>数据库治理任务</template>
          <div class="stat">--</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <template #header>数据库巡检任务</template>
          <div class="stat">--</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <template #header>资产管理</template>
          <div class="stat">--</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
</script>

<style lang="scss" scoped>
.dashboard { .stat { font-size: 28px; font-weight: bold; color: #409EFF; text-align: center; } }
</style>
```

- [ ] **Step 3: 创建全局样式**

`vue-front/src/styles/index.scss`：

```scss
// Element Plus 主题变量覆盖
$--el-color-primary: #409EFF;
$--el-border-radius-base: 4px;

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body, #app {
  height: 100%;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Microsoft YaHei', sans-serif;
  color: #303133;
}
```

- [ ] **Step 4: Commit**

```bash
git add vue-front/src/views/ techmg-web/src/styles/
git commit -m "feat(web): add login page, dashboard, and global SCSS"
```

---

### Task 11: 全量编译验证

**Files:**
- None (验证阶段)

- [ ] **Step 1: 编译后端所有模块**

```bash
mvn clean compile
```
Expected: BUILD SUCCESS，6 个模块全部编译通过

- [ ] **Step 2: 安装前端依赖**

```bash
cd vue-front && npm install
```
Expected: 依赖安装成功，无报错

- [ ] **Step 3: 验证前端构建**

```bash
cd vue-front && npm run build:test
```
Expected: `dist/` 目录生成成功

- [ ] **Step 4: Commit**

```bash
git add -A
git commit -m "build: full project compilation and frontend build verified"
```
