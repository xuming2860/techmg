# Frontend 规约 — techmg

Vue 3.3.9 / Vite 5.0.4 / Element Plus 2.5.0 / Pinia 2.1.4 / Vue Router 4.2.5 / Axios 1.7.2

## 功能模块 + 页面清单

```
首页 (上下布局)
  └── Dashboard                               ✅

技术管理 (上左右布局)
  ├── 技改任务管理
  │   ├── 任务总览    /tech-reform/overview     占位
  │   ├── 子任务管理   /tech-reform/subtask      占位
  │   └── 技改任务统计  /tech-reform/stats        占位
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

登录    /login                                   ✅
404     /:pathMatch(.*)*                         ✅
```

## 目录结构

```
vue-front/src/
├── api/                          # API 层 (28 具名导出)
│   ├── auth.js
│   └── system/{user,role,menu,dept}.js
├── router/index.js               # 静态路由 + componentMap + beforeEach
├── store/
│   ├── user.js                   # token/userInfo/roles/permissions/menus + loadRoutes/generateRoutes
│   └── app.js                    # sidebarCollapsed (预留)
├── utils/request.js              # Axios: Bearer + R<T>解包 + Blob + 错误分类
├── layout/
│   ├── AppLayout.vue             # route.meta.layout → Default/TopOnly
│   ├── DefaultLayout.vue         # TopBar + Sidebar + content
│   ├── TopOnlyLayout.vue         # TopBar + content
│   ├── TopBar.vue                # logo + 模块tabs + 头像下拉(退出)
│   ├── Sidebar.vue               # 按模块动态子菜单
│   └── MenuItem.vue              # 递归 el-sub-menu / el-menu-item
├── views/
│   ├── login/index.vue           # 登录页
│   ├── dashboard/index.vue       # 首页
│   ├── error/404.vue             # 404 页面
│   ├── system/{user,role,menu,dept}/
│   ├── tech-reform/{overview,subtask,stats}/
│   ├── db-manage/{dba,app-db,inspection,slow-sql}/
│   ├── tech-stack/{overview,issues}/
│   ├── asset/{health,app-important,app-scenario,dev-paradigm}/
│   ├── production/
│   └── digital-eco/{level1,level2,design-review,code-review}/
├── directives/permission.js      # v-permission
└── styles/index.scss             # CSS reset + 主题变量 + 滚动条
```

## 核心设计

### 布局系统
- 路由级布局: `route.meta.layout = 'top' | 'side'`
- 首页 → `TopOnlyLayout` (宽屏无侧栏)
- 其他 → `DefaultLayout` (顶栏+侧栏)
- `AppLayout.vue` 读 `route.meta.layout` 动态渲染

### 动态路由流程
```
登录 → login() → setToken() → getUserInfo() + getUserMenuTree()
→ setMenus() → generateRoutes() → addRoute 所有菜单
→ addRoute 404 兜底 → routesLoaded=true → push('/')

刷新 → beforeEach: isLoggedIn & !routesLoaded
→ loadRoutes() → API → setMenus() → routesLoaded=true
→ next({...to, replace:true})
```

### TopBar
- 左侧: ICBC logo 蓝标 + 平台名
- 中间: 模块 tabs (从 `getUserMenuTree()` 加载)
- 右侧: 圆形头像(用户名首字) + 下拉菜单(退出登录)

### Sidebar
- 按活跃模块显示子菜单 (从 `getUserMenuTree()` 过滤)
- 浅灰主题, 蓝色激活态, 递归 MenuItem

### 请求封装
- 请求拦截: `Authorization: Bearer {token}`
- 响应拦截: `R<T>` 解包 (code=200→data)
- Blob: `responseType:'blob'` 直通
- 错误: 401→跳登录, 403→无权限, 5xx→服务器错误

### 权限
- 路由守卫: `beforeEach` 检查 token
- 菜单过滤: 后端 `/api/system/menu/user-tree` 按角色返回
- 按钮级: `v-permission` 指令

## 构建

```bash
cd vue-front
npm install && npm run dev           # 开发 localhost:5173 → proxy /api → 8080
npm run build:test                   # 测试构建
npm run build:prod                   # 生产构建
```
