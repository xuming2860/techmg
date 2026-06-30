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
  children: []
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
  'views/system/dept/index': () => import('@/views/system/dept/index.vue'),
  'views/tech-reform/overview/index': () => import('@/views/tech-reform/overview/index.vue'),
  'views/tech-reform/subtask/index': () => import('@/views/tech-reform/subtask/index.vue'),
  'views/tech-reform/stats/index': () => import('@/views/tech-reform/stats/index.vue'),
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
  'views/digital-eco/design-review/index': () => import('@/views/digital-eco/design-review/index.vue'),
  'views/digital-eco/code-review/index': () => import('@/views/digital-eco/code-review/index.vue')
}

router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()

  if (to.path === '/login') {
    if (userStore.isLoggedIn) return next('/')
    return next()
  }

  if (!userStore.isLoggedIn) return next('/login')

  // User is logged in but dynamic routes may not be loaded yet (page refresh)
  if (!userStore.routesLoaded) {
    await userStore.loadRoutes()
    // After loading routes, retry the navigation to the originally requested path
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
