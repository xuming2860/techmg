import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

const staticRoutes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  }
]

const mainRoute = {
  path: '/',
  name: 'main',
  component: () => import('@/layout/AppLayout.vue'),
  redirect: '/dashboard',
  children: [
    {
      path: '/dashboard',
      name: 'Dashboard',
      component: () => import('@/views/dashboard/index.vue'),
      meta: { title: '首页', layout: 'top' }
    }
  ]
}

const router = createRouter({
  history: createWebHistory(),
  routes: [...staticRoutes, mainRoute]
})

const componentMap = {
  'views/dashboard/index': () => import('@/views/dashboard/index.vue'),
  'views/system/user/index': () => import('@/views/system/user/index.vue'),
  'views/system/role/index': () => import('@/views/system/role/index.vue'),
  'views/system/menu/index': () => import('@/views/system/menu/index.vue'),
  'views/tech-reform/overview/index': () => import('@/views/tech-reform/overview/index.vue'),
  'views/tech-reform/subtask/index': () => import('@/views/tech-reform/subtask/index.vue'),
  'views/db-manage/dba/index': () => import('@/views/db-manage/dba/index.vue'),
  'views/db-manage/app-db/index': () => import('@/views/db-manage/app-db/index.vue'),
  'views/db-manage/inspection/index': () => import('@/views/db-manage/inspection/index.vue'),
  'views/db-manage/slow-sql/index': () => import('@/views/db-manage/slow-sql/index.vue'),
  'views/tech-stack/overview/index': () => import('@/views/tech-stack/overview/index.vue'),
  'views/tech-stack/issues/index': () => import('@/views/tech-stack/issues/index.vue'),
  'views/asset/health/index': () => import('@/views/asset/health/index.vue'),
  'views/asset/app-important/index': () => import('@/views/asset/app-important/index.vue'),
  'views/asset/app-scenario/index': () => import('@/views/asset/app-scenario/index.vue'),
  'views/asset/dev-paradigm/index': () => import('@/views/asset/dev-paradigm/index.vue'),
  'views/production/index': () => import('@/views/production/index.vue'),
  'views/digital-eco/level1/index': () => import('@/views/digital-eco/level1/index.vue'),
  'views/digital-eco/level2/index': () => import('@/views/digital-eco/level2/index.vue'),
  'views/digital-eco/design-review/index': () =>
    import('@/views/digital-eco/design-review/index.vue'),
  'views/digital-eco/code-review/index': () => import('@/views/digital-eco/code-review/index.vue')
}

/**
 * 路由守卫 — 三层检查 + SSO 重定向。
 *
 * 对应行内 SSO 流程:
 *   1. 无 token → 保存目标 URL → 重定向 /login
 *   2. /login 页检测无 SSIAuth → 显示 SSO 登录按钮 → 点击后 GET /api/auth/login → 302 → SSO 页
 *   3. SSO 登录完成 → 回跳 client.site.url?SSIAuth=xxx&SSI_SIGN=xxx
 *   4. /login 页检测 SSIAuth → POST /api/auth/login → 登录成功 → 恢复目标 URL
 */
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()

  // 1. 目标为 /login 页
  if (to.path === '/login') {
    // 已登录 → 直接进入首页
    if (userStore.isLoggedIn && userStore.userInfo) {
      return next('/')
    }

    // 未登录 → 保存目标 URL（从哪个页面跳过来的）
    // 如果 URL 中有 SSIAuth（SSO 回调），不保存目标 URL
    if (!window.location.href.includes('SSIAuth')) {
      // 从非 /login 页跳转过来时保存原目标
      if (from.path !== '/login' && from.path !== '/') {
        localStorage.setItem('TARGET_URL', to.path)
        localStorage.setItem('TARGET_QUERY', JSON.stringify(to.query))
      }
    }

    return next()
  }

  // 2. 未登录或 userInfo 缺失 → 保存目标 URL 并跳转登录页
  if (!userStore.isLoggedIn || !userStore.userInfo) {
    userStore.logout()
    // 保存当前目标 URL，登录成功后恢复
    localStorage.setItem('TARGET_URL', to.path)
    localStorage.setItem('TARGET_QUERY', JSON.stringify(to.query))
    return next('/login')
  }

  // 3. 已登录但动态路由尚未加载（页面刷新场景）
  if (!userStore.routesLoaded) {
    await userStore.loadRoutes()
    return next({ ...to, replace: true })
  }

  next()
})

export function getLayout(menu) {
  if (menu.layout) return menu.layout
  if (menu.component === 'views/dashboard/index') return 'top'
  return 'side'
}

export { router, mainRoute, componentMap }
export default router
