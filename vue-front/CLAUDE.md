# Frontend 规约 — techmg v1.0

Vue 3.3.9 / Vite 5.0.4 / Element Plus 2.5.0 / Pinia 2.1.4 / Vue Router 4.2.5 / Axios 1.7.2

## v1.0 功能页面

| 页面 | 路径 | 状态 |
|------|------|------|
| 登录 | `/login` | ✅ 完整 (login API + JWT + 动态路由加载) |
| 首页 Dashboard | `/dashboard` | ✅ (统计卡片 + 待办列表 + 图表占位) |
| 用户管理 | `/system/user` | ✅ CRUD + 搜索 + 角色分配 + 部门选择 |
| 角色管理 | `/system/role` | ✅ CRUD + 菜单树分配 |
| 菜单管理 | `/system/menu` | ✅ 树形表格 + CRUD + 类型切换 |
| 部门管理 | `/system/dept` | ✅ 树形表格 + CRUD |
| 技术治理 | `/tech-governance` | 占位 |
| 数据库治理 | `/db-governance` | 占位 |
| 数据库巡检 | `/db-inspection` | 占位 |
| 资产管理 (4页) | `/asset/*` | 占位 |
| 生产管理 | `/production` | 占位 |

## 五大模块 + 布局

```
首页 (上下布局, 无侧边栏)  — 宽屏, 适合报表和待办
技术管理 (上左右)          — 技术治理/DB治理/DB巡检
资产管理 (上左右)          — 应用/参数/代码/存储过程
生产管理 (上左右)          — 占位
设置 (上左右, 仅管理员)     — 用户/角色/菜单/部门 CRUD
```

## 核心设计

### API 层 (`src/api/`)
- 5 文件, 28 具名导出函数
- 页面禁止直接 axios, 必须调用 api 层函数

### 请求封装 (`utils/request.js`)
- 请求拦截: Bearer token 注入
- 响应拦截: R\<T\> 解包 (code=200→data), 错误分类 (401/403/500)
- Blob: `responseType:'blob'` 不做解包

### 动态路由
- 登录后调用 `getUserMenuTree()` → `generateRoutes()` → `router.addRoute()`
- 页面刷新时 `beforeEach` 检测 `routesLoaded`, 未加载则先初始化路由
- 布局由 `route.meta.layout` 决定: `'top'` (首页) / `'side'` (其他)

### 布局组件
- `AppLayout.vue`: 读 `route.meta.layout` 动态选择布局
- `TopBar.vue`: 白色顶栏, ICBC 蓝色 logo + 模块 tabs + 用户 + 退出
- `Sidebar.vue`: 浅灰侧栏, 按活跃模块显示子菜单
- `MenuItem.vue`: 递归菜单组件

### Pinia Store
- `user.js`: token/userInfo/roles/permissions/menus + `loadRoutes()` + `generateRoutes()`
- `app.js`: sidebarCollapsed (预留)

### 权限
- `v-permission` 指令已注册
- 菜单按角色过滤 (后端 `/api/system/menu/user-tree`)

## 构建

```bash
cd vue-front && npm install && npm run dev    # 开发 (5173→8080)
npm run build:test && npm run build:prod       # 构建
```
