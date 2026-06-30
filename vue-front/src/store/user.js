import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { router, componentMap, getLayout } from '@/router'
import { getUserMenuTree } from '@/api/system/menu'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))
  const roles = ref([])
  const permissions = ref([])
  const menus = ref([])
  const routesLoaded = ref(false)

  const isLoggedIn = computed(() => !!token.value)

  function setToken(val) {
    token.value = val
    localStorage.setItem('token', val)
  }

  function setUserInfo(info) {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  function setMenus(val) {
    menus.value = val
    generateRoutes(val)
    routesLoaded.value = true
  }

  function setRoles(val) {
    roles.value = val
  }
  function setPermissions(val) {
    permissions.value = val
  }

  function generateRoutes(menuList) {
    if (!menuList || !Array.isArray(menuList)) return
    for (const menu of menuList) {
      if (menu.type === 0 && menu.children && menu.children.length > 0) {
        generateRoutes(menu.children)
        continue
      }
      if (menu.type === 1 && menu.path && menu.component) {
        const comp = componentMap[menu.component]
        if (comp) {
          router.addRoute('main', {
            path: menu.path,
            name: menu.menuName,
            component: comp,
            meta: {
              title: menu.menuName,
              icon: menu.icon || '',
              layout: getLayout(menu)
            }
          })
        }
      }
    }
    // Add catch-all 404 AFTER all dynamic routes
    router.addRoute('main', {
      path: '/:pathMatch(.*)*',
      name: 'NotFound',
      component: () => import('@/views/error/404.vue')
    })
  }

  /** Call on page refresh to restore routes if already logged in */
  async function loadRoutes() {
    if (routesLoaded.value) return
    try {
      const tree = await getUserMenuTree()
      if (tree && tree.length > 0) {
        setMenus(tree)
      }
    } catch {
      // If API fails, routesLoaded stays false so we retry next navigation
      routesLoaded.value = false
    }
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    roles.value = []
    permissions.value = []
    menus.value = []
    routesLoaded.value = false
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  return {
    token,
    userInfo,
    roles,
    permissions,
    menus,
    routesLoaded,
    isLoggedIn,
    setToken,
    setUserInfo,
    setMenus,
    setRoles,
    setPermissions,
    generateRoutes,
    loadRoutes,
    logout
  }
})
