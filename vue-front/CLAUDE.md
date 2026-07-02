# Frontend 规约 — techmg

Vue 3.3.9 / Vite 5.0.4 / Element Plus 2.5.0 / Pinia 2.1.4 / Vue Router 4.2.5 / Axios 1.7.2 / crypto-js 4.2.0

## 功能模块 + 页面清单

```
首页 (上下布局)
  └── Dashboard                               ✅

技术管理 (上左右布局)
  ├── 技改任务管理
  │   ├── 任务总览    /tech-reform/overview     ✅完成
  │   └── 子任务管理   /tech-reform/subtask      ✅完成 (双Tab)
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

登录    /login                                   ✅自动登录 / SSO
404     /:pathMatch(.*)*                         ✅
```

> 技改任务统计 `/tech-reform/stats` 已移除 (并入任务总览)。

## 目录结构

```
vue-front/src/
├── api/                          # API 层
│   ├── auth.js                   # 登录/登出/SSO/认证模式查询
│   ├── tech-reform.js            #技改任务 22 个 API
│   └── system/{user,role,menu}.js
├── router/index.js               # 静态路由 + componentMap + beforeEach(三层检查)
├── store/
│   ├── user.js                   # token/userInfo(AES加密)/roles/permissions/menus + loadRoutes
│   └── app.js                    # sidebarCollapsed (预留)
├── utils/
│   ├── request.js                # Axios: Bearer + R<T>解包 + Blob + 错误分类
│   └── crypto.js                 #AES 加密/解密 userInfo
├── layout/
│   ├── AppLayout.vue             # route.meta.layout → Default/TopOnly
│   ├── DefaultLayout.vue         # TopBar + Sidebar + content
│   ├── TopOnlyLayout.vue         # TopBar + content
│   ├── TopBar.vue                # logo + 模块tabs + 头像(读userStore.menus,无独立请求)
│   ├── Sidebar.vue               # 按模块动态子菜单(读userStore.menus,无独立请求)
│   └── MenuItem.vue              # 递归 el-sub-menu / el-menu-item
├── components/TechReform/        #新增
│   ├── TaskFormDialog.vue        # 父任务新增/编辑弹窗
│   ├── SubtaskFormDialog.vue     # 子任务新增/编辑弹窗 (含条件字段+远程搜索)
│   └── ImportWizard.vue          # 文件导入4步向导 (上传→模式→映射→预览)
├── views/
│   ├── login/index.vue           #自动登录(mock)/SSO登录
│   ├── dashboard/index.vue       # 首页
│   ├── error/404.vue             # 404 页面
│   ├── system/{user,role,menu}/
│   ├── tech-reform/
│   │   ├── overview/index.vue    #任务总览 CRUD + 筛选 + 角色权限
│   │   └── subtask/
│   │       ├── index.vue         #双Tab (子任务管理 + 治理清单)
│   │       └── GovernanceDetail.vue #治理清单详情 (行内编辑+下载+批量)
│   ├── db-manage/{dba,app-db,inspection,slow-sql}/
│   ├── tech-stack/{overview,issues}/
│   ├── asset/{health,app-important,app-scenario,dev-paradigm}/
│   ├── production/
│   └── digital-eco/{level1,level2,design-review,code-review}/
├── directives/permission.js      # v-permission
└── styles/index.scss             # CSS reset + 主题变量 + 滚动条
```

## 编码规约（强制约束）

### 1. 架构范式与 API 风格（核心红线）

- **强制 `<script setup lang="ts">`**：所有 `.vue` 组件必须使用组合式 API + TypeScript，严禁 Options API（`export default { data(), methods: {} }`）
- **逻辑抽离**：可复用逻辑抽取为 Composables，置于 `composables/` 目录，`use` 前缀命名（如 `useUserAuth`）
- **组件行数限制**：组件内部业务逻辑不超过 200 行，复杂逻辑下沉至 Composables 或 `utils/`
- **TypeScript 强制**：所有 `.vue`、`.ts` 文件必须使用 TypeScript（`lang="ts"`），严禁纯 JavaScript

### 2. 目录结构与模块化

```
src/
├── api/                # API 接口定义（按模块划分）
│   ├── request.ts      # 统一 Axios 封装（拦截器、baseURL）
│   └── types/          # API 请求/响应的 TS 类型定义
├── assets/             # 静态资源（图片、字体、全局样式）
├── components/         # 公共通用组件
│   └── common/
├── composables/        # 组合式函数（useTable, usePagination, useAuth）
├── layouts/            # 布局组件（DefaultLayout, TopOnlyLayout）
├── router/             # 路由配置
├── stores/             # Pinia 状态管理（按模块划分）
├── types/              # 全局 TypeScript 类型定义（*.d.ts）
├── utils/              # 工具函数（格式化、校验、常量、storage）
└── views/              # 页面级组件（按业务模块划分）
    └── user/
        ├── UserList.vue
        ├── UserDetail.vue
        └── components/ # 页面内部私有组件
```

### 3. 组件编码与命名规范

| 维度 | 规范 | 示例 |
|------|------|------|
| 文件名 | PascalCase | `UserProfile.vue` |
| 模板引用 | PascalCase 或 kebab-case（保持一致） | `<UserProfile />` 或 `<user-profile />` |
| Props | TypeScript 泛型 + `withDefaults` | 见下方代码 |
| Emits | TypeScript 泛型声明 | 见下方代码 |
| 私有组件 | 页面目录下 `components/`，禁止放入全局 `components/` | `views/user/components/UserForm.vue` |

```vue
<script setup lang="ts">
interface Props {
  title: string;
  count?: number;
  data: UserInfo;
}
const props = withDefaults(defineProps<Props>(), {
  count: 0
});

const emit = defineEmits<{
  (e: 'update', value: string): void;
  (e: 'delete', id: number): void;
}>();
</script>
```

### 4. 状态管理（Pinia）规约

- **Setup Store 语法**：必须使用 `defineStore` + setup 函数风格，禁止 Options Store

```typescript
// stores/user.ts
export const useUserStore = defineStore('user', () => {
  const userInfo = ref<UserInfo | null>(null);
  const isLoggedIn = computed(() => !!userInfo.value);

  async function login(credentials: LoginParams) {
    // ...
  }

  return { userInfo, isLoggedIn, login };
});
```

- **解构约束**：响应式属性（state/getters）必须用 `storeToRefs` 解构，actions 可直接解构

```typescript
import { storeToRefs } from 'pinia';
const userStore = useUserStore();
const { userInfo, isLoggedIn } = storeToRefs(userStore); // 保持响应式
const { login } = userStore; // action 直接解构
```

- **持久化封装**：localStorage/sessionStorage 操作统一封装至 `utils/storage.ts`，禁止 Store 中直接操作原生 API

### 5. 路由管理（Vue Router）规约

- **命名路由强制**：跳转必须使用 `{ name: 'UserDetail', params: { id: 1 } }`，严禁硬编码字符串路径 `'/user/1'`
- **守卫抽离**：全局 `beforeEach` 置于 `router/index.ts`，业务权限校验逻辑抽离至 `composables/useAuth.ts`，禁止在守卫文件中编写大量业务逻辑
- **路由懒加载**：所有页面级组件必须使用动态导入 `() => import()`

### 6. 网络请求（Axios）封装规约

- **统一入口**：所有 HTTP 请求必须通过 `api/request.ts` 的 Axios 实例，严禁原生 `fetch` 或未封装第三方库
- **拦截器职责**：
  - 请求拦截器：统一添加 Token、请求时间戳
  - 响应拦截器：统一处理 401（登录态过期）、错误码提示
- **API 分层**：每个业务模块独立文件（如 `api/user.ts`），函数命名清晰（`getUserList`、`updateUser`），含完整 TS 类型（入参 Params、出参 Response）
- **Loading 防抖**：涉及提交/查询的请求，配合 `loading` 响应式状态控制重复提交

### 7. 样式（CSS）约束

- **样式隔离**：所有组件样式必须 `scoped`，严禁全局样式污染
- **深度选择器**：修改 Element Plus 等第三方组件内部样式，必须用 `:deep()`，禁止已弃用的 `::v-deep`
- **全局变量**：主题色/字体/间距定义在 `assets/styles/variables.scss`，通过 `v-bind` 或 `@use` 引入，禁止硬编码颜色值（如 `#FF0000`）
- **布局优先**：优先 Flexbox / Grid，禁止滥用 `!important`

### 8. 性能优化强制项

| 场景 | 规范 |
|------|------|
| 非首屏大组件 | `defineAsyncComponent` 异步加载（弹窗、抽屉、富文本编辑器） |
| `v-for` 列表 | 绑定稳定唯一 `key`（优先 `id`，**严禁 `index`**） |
| 大数据对象 | 使用 `shallowRef` / `markRaw` 避免深度响应式开销 |
| 组件卸载 | `onBeforeUnmount` 中清除事件监听、定时器、WebSocket |

### 9. 错误处理与日志

- **全局捕获**：`main.ts` 中挂载 `app.config.errorHandler` 统一捕获 Vue 异常
- **API 异常**：`try-catch` 捕获后 `console.error` 输出详情 + 用户友好 Toast 提示
- **禁止空 catch**：严禁 `catch {}` 空块，必须记录错误或重新抛出

### AI 代码生成强制检查清单

在输出 Vue 3 代码之前，必须逐项校验：

1. ✅ 组件是否使用 `<script setup lang="ts">` 而非 Options API？
2. ✅ 复杂逻辑是否抽离至 composables 或 utils，组件代码是否控制在 200 行内？
3. ✅ Props 和 Emits 是否拥有完整的 TypeScript 类型定义？
4. ✅ 路由跳转是否使用了 `name` 而非硬编码字符串路径？
5. ✅ API 请求是否经过 `request.ts` 统一封装，并带有完整的类型声明？
6. ✅ 所有 `v-for` 是否绑定了稳定且唯一的 `key`？
7. ✅ 样式是否添加了 `scoped`，且没有 `!important` 滥用？
8. ✅ 是否处理了组件销毁前的内存清理（事件/定时器/WebSocket）？

### 安全防御（强制）

- **XSS 防护**：严禁使用 `v-html` 直接渲染用户输入内容。如需渲染 HTML，必须经过 DOMPurify 净化
- **输入校验**：所有用户输入表单必须配合 `el-form` 的 `rules` 校验，禁止裸提交

### 前端 AI 检查清单

- [ ] 是否存在 `v-html` 渲染用户输入？
- [ ] 是否存在 `System.out` / `console.log` 遗留调试代码（`console.error` 允许）？

### 10. Element Plus 专项规约

> **前置依赖**：本规约建立在 Vue 3 + TypeScript 基础约束之上，AI 生成代码时必须同时遵守。

#### 10.1 组件引入与注册（核心红线）

- **按需自动导入（强制）**：项目必须配置 `unplugin-vue-components` + `unplugin-auto-import` 实现按需导入，**禁止**手动 `import { ElButton } from 'element-plus'`
- **完整引入禁止**：严禁 `app.use(ElementPlus)` 完整引入
- **Volar 类型**：`tsconfig.json` 的 `compilerOptions.types` 中添加 `"element-plus/global"`

#### 10.2 表单（Form）组件规约

| 规则 | 说明 |
|------|------|
| 结构 | 必须 `<el-form>` + `<el-form-item>` 容器 |
| 数据绑定 | `model` 绑定响应式对象 |
| 验证规则 | `rules` 属性传入，`prop` 对应字段名，必填项 `required: true`，明确 `trigger` |
| 数字输入 | 使用 `v-model.number` 修饰符 |
| 提交 | `@submit.prevent` 阻止默认提交 |
| 校验 | `formRef.value?.validate()` 通过后方可提交 |

```vue
<template>
  <el-form
    ref="formRef"
    :model="formData"
    :rules="formRules"
    label-width="120px"
    @submit.prevent="handleSubmit"
  >
    <el-form-item label="用户名" prop="username">
      <el-input v-model="formData.username" />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" native-type="submit">提交</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import type { FormInstance, FormRules } from 'element-plus';

const formRef = ref<FormInstance>();
const formData = reactive({ username: '' });
const formRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ]
};

const handleSubmit = async () => {
  await formRef.value?.validate();
  // 提交逻辑
};
</script>
```

#### 10.3 表格（Table）组件规约

| 规则 | 说明 |
|------|------|
| 样式 | 必须 `border` + `stripe`（数据量大时） |
| 溢出 | 长内容必须 `show-overflow-tooltip` |
| 固定 | 纵向多 → `height`/`max-height` 固定表头；横向多 → 操作列 `fixed="right"` |
| 序号 | `type="index"` |
| 自定义列 | `<template #default="scope">` 插槽，通过 `scope.row` 访问行数据 |
| 分页 | 超过 10 条必须配合 `<el-pagination>`，禁止一次性渲染全部 |

```vue
<template>
  <el-table :data="tableData" border stripe height="400">
    <el-table-column type="index" label="序号" width="60" />
    <el-table-column prop="name" label="姓名" show-overflow-tooltip />
    <el-table-column prop="status" label="状态">
      <template #default="scope">
        <el-tag :type="scope.row.status === 'active' ? 'success' : 'danger'">
          {{ scope.row.status }}
        </el-tag>
      </template>
    </el-table-column>
    <el-table-column label="操作" width="150" fixed="right">
      <template #default="scope">
        <el-button type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
      </template>
    </el-table-column>
  </el-table>
</template>
```

#### 10.4 反馈组件规约

**Message 消息提示**：
- 从 `element-plus` 导入 `ElMessage`
- 优先类型化方法：`ElMessage.success()` / `.error()` / `.warning()` / `.info()`
- `duration` 合理值（默认 3000ms），禁止设为 0
- **严禁** `dangerouslyUseHTMLString: true` + 用户输入（防 XSS）
- 位置统一默认（顶部居中），禁止单次调用随意修改 `placement`

**MessageBox 消息弹框**：
- 从 `element-plus` 导入 `ElMessageBox`
- 删除/提交等关键操作必须 `ElMessageBox.confirm()` 二次确认
- 必须 `.catch()` 捕获取消操作，禁止空 catch

```typescript
import { ElMessageBox, ElMessage } from 'element-plus';

const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除该条记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    // 执行删除逻辑
    ElMessage.success('删除成功');
  } catch {
    // 用户取消
  }
};
```

#### 10.5 图标（Icon）使用规约

- 安装 `@element-plus/icons-vue`
- `main.ts` 中全局注册所有图标
- 必须包裹在 `<el-icon>` 中使用
- 命名 PascalCase：`<Edit />`、`<Search />`、`<Delete />`

```typescript
// main.ts — 全局注册
import * as ElementPlusIconsVue from '@element-plus/icons-vue';
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component);
}
```

```vue
<el-button type="primary">
  <el-icon><Search /></el-icon>
  搜索
</el-button>
```

#### 10.6 全局配置规约

```typescript
// main.ts
app.use(ElementPlus, {
  size: 'default',   // 统一尺寸，禁止单组件重复设置
  zIndex: 3000       // 统一层级
});
```

#### 10.7 性能优化强制项

| 场景 | 规范 |
|------|------|
| 表格 >1000 条 | 虚拟滚动（`el-table-v2`）或后端分页，禁止一次性渲染 |
| 弹窗/抽屉 | `v-if` 控制渲染时机，禁止 `v-show` 或始终渲染 |
| `el-select` >100 项 | 开启 `filterable` + `remote` 远程搜索，禁止一次性加载 |

#### 10.8 TypeScript 类型支持

- 组件实例必须导入对应类型：`FormInstance`、`FormRules`、`TableInstance`
- 禁止使用 `any` 作为 Element Plus 组件 Props 类型

```typescript
import type { FormInstance, FormRules, TableInstance } from 'element-plus';
const formRef = ref<FormInstance>();
const tableRef = ref<TableInstance>();
```

#### Element Plus 专项检查清单

- [ ] 是否配置了按需自动导入，而非完整引入或手动导入？
- [ ] 表单是否包含 `<el-form>` + `<el-form-item>` 结构，并配置了 `rules` 和 `prop`？
- [ ] 表格是否设置了 `border`、`stripe`，长内容是否使用了 `show-overflow-tooltip`？
- [ ] 消息提示是否使用了 `ElMessage.type()` 方法，而非 `ElMessage({ type })`？
- [ ] 图标是否包裹在 `<el-icon>` 中，且已在 `main.ts` 中全局注册？
- [ ] 删除等危险操作是否使用了 `ElMessageBox.confirm()` 二次确认？
- [ ] 是否从 `element-plus` 导入了必要的 TypeScript 类型（如 `FormInstance`）？

---

## 核心设计

### 登录流程 (v2.0 → v2.1 SSIC 改造)

```
用户打开平台 → /login 页 → GET /api/auth/config
  ├── ssoEnabled=false → autoMockLogin: POST /api/auth/login → 后端返回配置用户
  │   → setToken + setUserInfo(AES加密) → getUserMenuTree → router.replace('/')
  │   用户体验: 瞬时加载 → 直接进入首页，无感知
  │
  └── ssoEnabled=true (SSIC 统一认证):
      1. 前端无 token → 显示"统一认证登录"按钮
      2. 点击 → GET /api/auth/login → 302 重定向到 SSO 登录页
      3. SSO 登录完成 → 回跳 client.site.url?SSIAuth=xxx&SSI_SIGN=xxx
      4. 前端截取 URL ? 后参数 → POST /api/auth/login (config=原始参数字符串)
      5. 后端验证 SSI 参数 → JWT → 前端 setToken + setUserInfo → 进入首页
```

### 布局系统
- 路由级布局: `route.meta.layout = 'top' | 'side'`
- 首页 → `TopOnlyLayout` (宽屏无侧栏)
- 其他 → `DefaultLayout` (顶栏+侧栏)
- `AppLayout.vue` 读 `route.meta.layout` 动态渲染

### 动态路由流程
```
登录 → login() → setToken() + setUserInfo(AES加密存localStorage)
→ getUserMenuTree() → setMenus() → generateRoutes() → addRoute 所有菜单
→ addRoute 404 兜底 → routesLoaded=true → push('/')

刷新 → beforeEach: 检查 token + userInfo + routesLoaded 三层
→ isLoggedIn && userInfo && routesLoaded → 放行
→ 任一缺失 → logout() → 强制跳 /login
```

### 路由守卫 (SSO 改造)

```
router.beforeEach:
  1. /login? + isLoggedIn → redirect / ; allow (含 SSIAuth 的 URL 放行)
  2. !isLoggedIn || !userInfo → 保存 TARGET_URL → logout() → redirect /login
  3. !routesLoaded → loadRoutes() → retry navigation
  4. 全部通过 → next()

登录成功后 → 从 localStorage 恢复 TARGET_URL → router.replace()
```
```
router.beforeEach:
  1. /login? → isLoggedIn && userInfo → redirect / ; else allow
  2. !isLoggedIn || !userInfo → logout() → redirect /login
  3. !routesLoaded → loadRoutes() → retry navigation
  4. 全部通过 → next()
```

### 用户状态存储
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
