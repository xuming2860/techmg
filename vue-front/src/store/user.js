import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { router, componentMap, getLayout } from '@/router'
import { getUserMenuTree } from '@/api/system/menu'
import { encrypt, decrypt } from '@/utils/crypto'

function loadUserInfoFromStorage() {
  const cached = localStorage.getItem('userInfo')
  if (!cached) return null
  // Try AES decrypt first, fallback to plain JSON
  try {
    const info = decrypt(cached)
    if (info) return info
  } catch (err) {}
  try {
    return JSON.parse(cached)
  } catch (err) {
    return null
  }
}

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const savedInfo = loadUserInfoFromStorage()
  const userInfo = ref(savedInfo)
  const roles = ref(savedInfo?.roles || [])
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
    roles.value = info.roles || []
    // AES encrypt before localStorage, fallback to plain JSON
    try {
      localStorage.setItem('userInfo', encrypt(info))
    } catch (e) {
      console.warn('[UserStore] encrypt failed, storing plain:', e)
      localStorage.setItem('userInfo', JSON.stringify(info))
    }
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
    const existingPaths = new Set(router.getRoutes().map(r => r.path))
    for (const menu of menuList) {
      if (menu.type === 0 && menu.children && menu.children.length > 0) {
        generateRoutes(menu.children)
        continue
      }
      if (menu.type === 1 && menu.path && menu.component) {
        // 避免与已存在的静态路由（如 /dashboard）重复注册
        if (existingPaths.has(menu.path)) {
          continue
        }
        const comp = componentMap[menu.component]
        if (comp) {
          existingPaths.add(menu.path)
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
    if (!router.hasRoute('NotFound')) {
      router.addRoute('main', {
        path: '/:pathMatch(.*)*',
        name: 'NotFound',
        component: () => import('@/views/error/404.vue')
      })
    }
  }

  /** Load userInfo from localStorage (called on app init) */
  function loadUserInfo() {
    const cached = localStorage.getItem('userInfo')
    if (cached) {
      const info = decrypt(cached)
      if (info) {
        userInfo.value = info
        roles.value = info.roles || []
      }
    }
  }

  /** Call on page refresh to restore routes if already logged in */
  async function loadRoutes() {
    if (routesLoaded.value) return
    try {
      const tree = await getUserMenuTree()
      if (tree && tree.length > 0) {
        setMenus(tree)
      } else {
        // Empty menu tree → still mark as loaded to prevent infinite retry
        routesLoaded.value = true
      }
    } catch (err) {
      // API fails (e.g. 403) → mark as loaded to prevent router guard infinite retry loop
      console.warn('[UserStore] loadRoutes failed, skipping dynamic routes:', err)
      routesLoaded.value = true
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
    loadUserInfo,
    loadRoutes,
    logout
  }
})
