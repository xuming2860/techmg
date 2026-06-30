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
  'views/tech-governance/index': () => import('@/views/tech-governance/index.vue'),
  'views/db-governance/index': () => import('@/views/db-governance/index.vue'),
  'views/db-inspection/index': () => import('@/views/db-inspection/index.vue'),
  'views/asset/app/index': () => import('@/views/asset/app/index.vue'),
  'views/asset/param/index': () => import('@/views/asset/param/index.vue'),
  'views/asset/code/index': () => import('@/views/asset/code/index.vue'),
  'views/asset/procedure/index': () => import('@/views/asset/procedure/index.vue'),
  'views/production/index': () => import('@/views/production/index.vue')
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
