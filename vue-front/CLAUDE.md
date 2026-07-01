# Frontend 规约 — techmg

Vue 3.3.9 / Vite 5.0.4 / Element Plus 2.5.0 / Pinia 2.1.4 / Vue Router 4.2.5 / Axios 1.7.2 / crypto-js 4.2.0

## 功能模块 + 页面清单

```
首页 (上下布局)
  └── Dashboard                               ✅

技术管理 (上左右布局)
  ├── 技改任务管理
  │   ├── 任务总览    /tech-reform/overview     ✅ v2.0 完成
  │   └── 子任务管理   /tech-reform/subtask      ✅ v2.0 完成 (双Tab)
  ├── 数据库管理
  │   ├── DBA人员管理          /db-manage/dba        占位
  │   ├── 应用数据库管理        /db-manage/app-db     占位
  │   ├── 巡检任务管理          /db-manage/inspection 占位
  │   └── 慢SQL和长事务管理     /db-manage/slow-sql   占位
  └── 技术栈管理
      ├── 整体视图    /tech-stack/overview       占位
      └── 问题明细    /tech-stack/issues         占位

资产管理 (上左右布局)
  ├── 应用资产健康度       /asset/health          占位
  ├── 应用信息管理
  │   ├── 重保应用信息管理     /asset/app-important   占位
  │   └── 应用重要场景信息管理  /asset/app-scenario    占位
  └── 基地研发范式管理      /asset/dev-paradigm     占位

生产管理 (上左右布局)
  └── 生产管理            /production            占位

数智生态 (上左右布局)
  ├── 一级领域             /digital-eco/level1      占位
  ├── 二级领域             /digital-eco/level2      占位
  ├── 详细设计审核          /digital-eco/design-review 占位
  └── 代码评审任务          /digital-eco/code-review   占位

设置 (上左右布局, 仅PLATFORM_ADMIN)
  ├── 菜单管理            /system/menu           ✅
  ├── 角色管理            /system/role           ✅
  └── 用户管理            /system/user           ✅

登录    /login                                   ✅ v2.0: 自动登录 / SSO
404     /:pathMatch(.*)*                         ✅
```

> 技改任务统计 `/tech-reform/stats` 已移除 (v2.0 并入任务总览)。

## 目录结构

```
vue-front/src/
├── api/                          # API 层
│   ├── auth.js                   # 登录/登出/SSO/认证模式查询
│   ├── tech-reform.js            # v2.0: 技改任务 22 个 API
│   └── system/{user,role,menu,dept}.js
├── router/index.js               # 静态路由 + componentMap + beforeEach(三层检查)
├── store/
│   ├── user.js                   # token/userInfo(AES加密)/roles/permissions/menus + loadRoutes
│   └── app.js                    # sidebarCollapsed (预留)
├── utils/
│   ├── request.js                # Axios: Bearer + R<T>解包 + Blob + 错误分类
│   └── crypto.js                 # v2.0: AES 加密/解密 userInfo
├── layout/
│   ├── AppLayout.vue             # route.meta.layout → Default/TopOnly
│   ├── DefaultLayout.vue         # TopBar + Sidebar + content
│   ├── TopOnlyLayout.vue         # TopBar + content
│   ├── TopBar.vue                # logo + 模块tabs + 头像(读userStore.menus,无独立请求)
│   ├── Sidebar.vue               # 按模块动态子菜单(读userStore.menus,无独立请求)
│   └── MenuItem.vue              # 递归 el-sub-menu / el-menu-item
├── components/TechReform/        # v2.0 新增
│   ├── TaskFormDialog.vue        # 父任务新增/编辑弹窗
│   ├── SubtaskFormDialog.vue     # 子任务新增/编辑弹窗 (含条件字段+远程搜索)
│   └── ImportWizard.vue          # 文件导入4步向导 (上传→模式→映射→预览)
├── views/
│   ├── login/index.vue           # v2.0: 自动登录(mock)/SSO登录
│   ├── dashboard/index.vue       # 首页
│   ├── error/404.vue             # 404 页面
│   ├── system/{user,role,menu,dept}/
│   ├── tech-reform/
│   │   ├── overview/index.vue    # v2.0: 任务总览 CRUD + 筛选 + 角色权限
│   │   └── subtask/
│   │       ├── index.vue         # v2.0: 双Tab (子任务管理 + 治理清单)
│   │       └── GovernanceDetail.vue # v2.0: 治理清单详情 (行内编辑+下载+批量)
│   ├── db-manage/{dba,app-db,inspection,slow-sql}/
│   ├── tech-stack/{overview,issues}/
│   ├── asset/{health,app-important,app-scenario,dev-paradigm}/
│   ├── production/
│   └── digital-eco/{level1,level2,design-review,code-review}/
├── directives/permission.js      # v-permission
└── styles/index.scss             # CSS reset + 主题变量 + 滚动条
```

## 核心设计

### 登录流程 (v2.0)

```
用户打开平台 → GET /api/auth/config
  ├── ssoEnabled=false → autoMockLogin: POST /api/auth/login → 后端返回配置用户
  │   → setToken + setUserInfo(AES加密) → getUserMenuTree → router.replace('/')
  │   用户体验: 瞬时加载 → 直接进入首页，无感知
  │
  └── ssoEnabled=true → 显示"统一认证登录"按钮 → 重定向 SSO → ?ticket=xxx 回调
      → POST /api/auth/sso/login → setToken + setUserInfo → 进入首页
```

### 布局系统
- 路由级布局: `route.meta.layout = 'top' | 'side'`
- 首页 → `TopOnlyLayout` (宽屏无侧栏)
- 其他 → `DefaultLayout` (顶栏+侧栏)
- `AppLayout.vue` 读 `route.meta.layout` 动态渲染

### 动态路由流程 (v2.0)
```
登录 → login() → setToken() + setUserInfo(AES加密存localStorage)
→ getUserMenuTree() → setMenus() → generateRoutes() → addRoute 所有菜单
→ addRoute 404 兜底 → routesLoaded=true → push('/')

刷新 → beforeEach: 检查 token + userInfo + routesLoaded 三层
→ isLoggedIn && userInfo && routesLoaded → 放行
→ 任一缺失 → logout() → 强制跳 /login
```

### 路由守卫 (v2.0 三层检查)
```
router.beforeEach:
  1. /login? → isLoggedIn && userInfo → redirect / ; else allow
  2. !isLoggedIn || !userInfo → logout() → redirect /login
  3. !routesLoaded → loadRoutes() → retry navigation
  4. 全部通过 → next()
```

### 用户状态存储 (v2.0)
- `token` → localStorage 明文
- `userInfo` → localStorage AES 加密 (密钥: `VITE_USERINFO_ENCRYPT_KEY`)
- `roles` → 初始化时从 userInfo.roles 恢复 (修复刷新丢失)
- `menus` → 内存 (刷新后通过 loadRoutes 重载)

### TopBar
- 左侧: ICBC logo 蓝标 + 平台名
- 中间: 模块 tabs (从 `userStore.menus` 读取，无独立 API 请求)
- 右侧: 圆形头像(用户名首字) + 下拉菜单(退出登录)

### Sidebar (v2.0: 不再独立请求菜单)
- 按活跃模块显示子菜单 (从 `userStore.menus` computed，零延迟)
- 浅灰主题, 蓝色激活态, 递归 MenuItem
- 切换模块时不再闪烁 (复用 store 中已加载的菜单树)

### 请求封装
- 请求拦截: `Authorization: Bearer {token}`
- 响应拦截: `R<T>` 解包 (code=200→data)
- Blob: `responseType:'blob'` 直通
- 错误: 401→跳登录, 403→无权限, 5xx→服务器错误

### 权限
- 路由守卫: `beforeEach` 三层检查 (token + userInfo + routesLoaded)
- 菜单过滤: 后端 `/api/system/menu/user-tree` 按角色返回
- 按钮级: `v-if="isPlatformAdmin"` 基于 `userStore.roles`

## 环境变量

| 变量 | 说明 | 示例 |
|------|------|------|
| `VITE_API_BASE_URL` | 后端 API 地址 | `http://localhost:8080` |
| `VITE_USERINFO_ENCRYPT_KEY` | userInfo AES 加密密钥 | `techmg-dev-encrypt-key-2024` |
| `VITE_SSO_ENABLED` | (已废弃) SSO 由后端 `/api/auth/config` 决定 | — |

## 构建

```bash
cd vue-front
npm install && npm run dev           # 开发 localhost:5173 → proxy /api → 8080
npm run build:test                   # 测试构建
npm run build:prod                   # 生产构建
```
