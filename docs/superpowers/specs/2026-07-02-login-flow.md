# techmg 登录流程 — 内网适配改造指南

**日期**: 2026-07-02  
**用途**: 拿到行内环境后进行统一认证改造的参考文档

---

## 1. 登录全链路流程图

```
┌─────────────────────────────────────────────────────────────────────┐
│                          用户打开平台                                 │
│                      http://localhost:5173                           │
└─────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────┐
│  前端 router.beforeEach                                              │
│  ├── /login? → isLoggedIn && userInfo? → redirect /                 │
│  └── 其他? → !isLoggedIn || !userInfo? → redirect /login            │
└─────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────┐
│  前端 login/index.vue  onMounted()                                   │
│  GET /api/auth/config                                                │
└─────────────────────────────────────────────────────────────────────┘
                                    │
                    ┌───────────────┴───────────────┐
                    ▼                               ▼
        ┌──────────────────┐             ┌──────────────────┐
        │ ssoEnabled: false │             │ ssoEnabled: true  │
        │   (Mock 模式)     │             │   (SSO 统一认证)   │
        └──────────────────┘             └──────────────────┘
                    │                               │
                    ▼                               ▼
┌──────────────────────────────┐    ┌──────────────────────────────┐
│ 自动调用                      │    │ 显示「统一认证登录」按钮       │
│ POST /api/auth/login          │    │ GET /api/auth/sso/login-url  │
│ {authNo:"", password:""}      │    │ → 重定向到行内SSO页面         │
└──────────────────────────────┘    └──────────────────────────────┘
                    │                               │
                    ▼                               ▼
┌──────────────────────────────┐    ┌──────────────────────────────┐
│ 后端 mockLogin():             │    │ SSO回调 ?ticket=xxx          │
│ 1. 读 login.mock 配置         │    │ POST /api/auth/sso/login     │
│ 2. syncUserInfo → sys_user    │    │ {ticket: "xxx"}              │
│ 3. JWT 生成(admin+roles)     │    │                              │
│ 4. 返回 {token, userInfo}     │    └──────────────────────────────┘
└──────────────────────────────┘                    │
                    │                               ▼
                    │               ┌──────────────────────────────┐
                    │               │ 后端 ssoLogin():              │
                    │               │ 1. SsoAuthProvider.authenticate│
                    │               │    (调用行内SSO验证ticket)     │
                    │               │ 2. getUserInfo(userId)        │
                    │               │    (调用外部API查用户信息)     │
                    │               │ 3. syncUserInfo → sys_user    │
                    │               │ 4. JWT 生成 + 返回            │
                    │               └──────────────────────────────┘
                    │                               │
                    └───────────────┬───────────────┘
                                    ▼
┌─────────────────────────────────────────────────────────────────────┐
│  前端 afterLogin():                                                  │
│  1. setToken(token)           → localStorage "token"                │
│  2. setUserInfo(userInfo)     → AES加密存 localStorage "userInfo"   │
│  3. getUserMenuTree()         → setMenus() → generateRoutes()       │
│  4. router.replace('/')       → 进入首页                             │
└─────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────┐
│  后续请求:                                                           │
│  axios interceptor → Authorization: Bearer {token}                  │
│  JwtAuthenticationFilter → validateToken → set SecurityContext      │
│  Controller → @PreAuthorize 权限校验 → 业务逻辑                       │
│                                                                     │
│  token 过期 → JwtAuthenticationFilter 直接返回 401 JSON              │
│  → 前端 handleAuthExpired() → 清 token/userInfo → 跳 /login         │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 2. 认证模式切换

只需要改一个配置项：

```yaml
# application.yml
sso:
  enabled: false   # false=Mock自动登录, true=统一认证
```

| 配置 | 登录方式 | 用户来源 | 适用场景 |
|------|---------|---------|---------|
| `sso.enabled: false` | Mock 自动登录 | `login.mock` 配置的用户 | 本地开发 / 内网调试 |
| `sso.enabled: true` | SSO 统一认证 | 行内 SSO + 外部用户 API | 生产环境 |

---

## 3. 文件清单与职责

### 3.1 后端 — 核心文件 (10 个)

| # | 文件 | 职责 | 内网改造 |
|---|------|------|---------|
| 1 | `SecurityConfig.java` | SecurityFilterChain, 放行 `/api/auth/**` | 无需改动 |
| 2 | `AuthController.java` | `/login` `/logout` `/userinfo` `/config` `/sso/*` | 无需改动 |
| 3 | `JwtTokenProvider.java` | JWT 生成/验证/解析 | 无需改动 |
| 4 | `JwtAuthenticationFilter.java` | 请求拦截, token 验证, 过期直接 401 | 无需改动 |
| 5 | `SsoAuthProvider.java` | **SSO 接口定义** | **按行内SSO协议实现** |
| 6 | `SsoAuthProviderImpl.java` | **SSO Mock 实现** | **替换为真实实现** |
| 7 | `LoginMockProperties.java` | Mock 用户配置属性 | 无需改动 |
| 8 | `UserDetailsServiceImpl.java` | Spring Security 用户加载 | 无需改动 |
| 9 | `SysUserServiceImpl.java` | syncUserInfo (upsert 用户+角色+机构) | 无需改动 |
| 10 | `SysUser.java` | 用户实体 (含 AD/机构/NotesID) | 无需改动 |

### 3.2 后端 — 配置文件

| # | 文件 | 关键配置 |
|---|------|---------|
| 1 | `application.yml` | `sso.enabled`, `login.mock.*`, `jwt.secret`, `jwt.expiration` |

### 3.3 前端 — 核心文件 (6 个)

| # | 文件 | 职责 | 内网改造 |
|---|------|------|---------|
| 1 | `views/login/index.vue` | 登录页: 查 config → Mock自动 / SSO按钮 | 无需改动 |
| 2 | `api/auth.js` | auth API 封装 | 无需改动 |
| 3 | `store/user.js` | token/userInfo(AES)/roles/menus 状态管理 | 无需改动 |
| 4 | `utils/request.js` | axios 拦截: Bearer头 + 401重定向 | 无需改动 |
| 5 | `utils/crypto.js` | AES 加解密 userInfo | 无需改动 |
| 6 | `router/index.js` | 路由守卫: token+userInfo 三层检查 | 无需改动 |

---

## 4. 关键文件内容

### 4.1 SsoAuthProvider.java — SSO 接口 (内网改造重点)

```java
package com.icbc.sh.techmg.framework.security;
import java.util.Map;

public interface SsoAuthProvider {
    /** 验证SSO ticket，返回统一认证号(authNo) */
    String authenticate(String ssoToken);

    /** 是否启用SSO模式 */
    default boolean enabled() { return false; }

    /** SSO登录页URL（前端重定向用） */
    default String getLoginUrl() { return null; }

    /** SSO登出URL */
    default String getLogoutUrl() { return null; }

    /** 查询用户扩展信息（调用行内用户API） */
    default Map<String, Object> getUserInfo(String userId) { return null; }
}
```

**改造要点**: 实现 `authenticate()` 和 `getUserInfo()`，接入行内统一认证和用户信息服务。

### 4.2 SsoAuthProviderImpl.java — 当前 Mock 实现 (需替换)

```java
@Component
@ConditionalOnProperty(name = "sso.enabled", havingValue = "true")
public class SsoAuthProviderImpl implements SsoAuthProvider {

    @Override
    public String authenticate(String ssoToken) {
        // TODO: 调用行内SSO验证ticket，返回userId
        return ssoToken;
    }

    @Override
    public boolean enabled() { return true; }

    @Override
    public String getLoginUrl() {
        // TODO: 返回行内统一认证登录页URL
        return null;
    }

    @Override
    public Map<String, Object> getUserInfo(String userId) {
        // TODO: 调用行内用户API查询扩展信息
        // 返回 Map 包含: authNo, tellername, ad, branchId, branchName, notesId, branchIdList
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("authNo", "0011491111");
        map.put("tellername", "徐敏");
        map.put("ad", "");
        map.put("branchId", "12092342");
        map.put("branchName", "上海技术部");
        map.put("notesId", "0011491111@sdc.com");
        map.put("branchIdList", List.of(Map.of("branchId","12092342","branchName","上海技术部")));
        return map;
    }
}
```

### 4.3 application.yml — 登录配置

```yaml
# JWT
jwt:
  secret: techmg-jwt-secret-key-2024-shanghai-icbc-research-base
  expiration: 86400000  # 24小时

# SSO 开关
sso:
  enabled: false
  login-url: ""
  logout-url: ""

# Mock 登录用户（sso.enabled=false 时生效）
login:
  mock:
    auth-no: "admin"
    real-name: "平台管理员"
    branch-id: "12092342"
    branch-name: "上海技术部"
    notes-id: "admin@sdc.com"
    ad-account: ""
    roles:
      - "ROLE_PLATFORM_ADMIN"
```

### 4.4 AuthController.java — 登录端点

| 端点 | 方法 | 说明 |
|------|------|------|
| `/api/auth/config` | GET | 返回 `{ssoEnabled, loginUrl}`，前端据此决定登录模式 |
| `/api/auth/login` | POST | 统一入口: sso.enabled=false→mockLogin, true→ssoLogin |
| `/api/auth/sso/login` | POST | SSO ticket 验证→查询用户→JWT |
| `/api/auth/sso/login-url` | GET | 获取 SSO 登录地址 |
| `/api/auth/userinfo` | GET | 当前用户信息(含扩展字段) |
| `/api/auth/logout` | POST | 退出 |

**mockLogin 流程**:
```
接收任意 authNo+password → 读 login.mock 配置
→ sysUserService.syncUserInfo(extInfo)  // upsert到sys_user + 记录last_login_time
→ jwtTokenProvider.generateToken(mockAuthNo, roles)  // 生成JWT
→ 返回 {token, userInfo: {authNo, realName, branchId, branchName, ...}}
```

**ssoLogin 流程**:
```
接收 {ticket}
→ ssoAuthProvider.authenticate(ticket)  // 验证SSO ticket
→ ssoAuthProvider.getUserInfo(authNo)   // 查询用户扩展信息
→ sysUserService.syncUserInfo(extInfo)  // upsert到sys_user
→ loadRolesForUser() → JWT生成
→ 返回 {token, userInfo}
```

### 4.5 JwtAuthenticationFilter.java — Token 校验过滤器

```
请求进入 → extractToken(Authorization header)
  ├── 无 token → 放行 (公共端点 / 未认证请求)
  ├── 有 token + 验证通过 → 设置 SecurityContext
  └── 有 token + 验证失败(过期/篡改) → 直接返回 401 JSON，中断链路
```

### 4.6 前端登录页流程 (login/index.vue)

```javascript
onMounted:
  1. 处理 SSO 回调: URL 有 ?ticket=xxx → ssoLogin(ticket) → afterLogin → 首页
  2. 查询认证模式: GET /api/auth/config
     ├── ssoEnabled=false → autoMockLogin() 
     │   → POST /api/auth/login {authNo:"", password:""}
     │   → afterLogin({token, userInfo}) → 首页
     │   用户体验: 瞬时加载 → 直接进入首页
     │
     └── ssoEnabled=true → 显示"统一认证登录"按钮
         → handleSsoLogin() → GET /api/auth/sso/login-url → window.location.href

afterLogin(data):
  userStore.setToken(data.token)          // localStorage "token"
  userStore.setUserInfo(data.userInfo)    // AES加密 → localStorage "userInfo"
  getUserMenuTree() → setMenus()          // 动态路由注册
  router.replace('/')                     // 跳转首页
```

### 4.7 前端路由守卫 (router/index.js)

```javascript
router.beforeEach:
  /login → isLoggedIn && userInfo 都有? → redirect /
           否则 → allow (让登录页自动登录)
  
  其他页面 → !isLoggedIn || !userInfo? → logout() → redirect /login
            !routesLoaded? → loadRoutes() → retry
            全部通过 → next()
```

### 4.8 Auth API 列表

| 函数 | 端点 | 说明 |
|------|------|------|
| `getAuthConfig()` | `GET /api/auth/config` | 查询认证模式 |
| `login(data)` | `POST /api/auth/login` | 登录 |
| `logout()` | `POST /api/auth/logout` | 登出 |
| `getUserInfo()` | `GET /api/auth/userinfo` | 当前用户信息 |
| `ssoLoginUrl()` | `GET /api/auth/sso/login-url` | SSO 地址 |
| `ssoLogin(ticket)` | `POST /api/auth/sso/login` | SSO 登录 |

---

## 5. 内网适配改造清单

接入行内统一认证需要做的事情：

| 步骤 | 文件 | 改造内容 |
|------|------|---------|
| 1 | `application.yml` | `sso.enabled: true` + `sso.login-url` 填写行内 SSO 地址 |
| 2 | `SsoAuthProviderImpl.java` | `authenticate()` — 调用行内 SSO API 验证 ticket |
| 3 | `SsoAuthProviderImpl.java` | `getLoginUrl()` — 返回行内统一认证登录页 URL |
| 4 | `SsoAuthProviderImpl.java` | `getUserInfo(userId)` — 调用行内用户信息 API |
| 5 | `SsoAuthProviderImpl.java` | `getLogoutUrl()` — 返回行内 SSO 登出 URL (可选) |
| 6 | `application.yml` | `login.mock` — 修改 `auth-no` 为你的测试账号 |

**其余文件无需改动**，Mock 模式已实现完整的用户同步、JWT 生成、前端缓存等闭环逻辑。
