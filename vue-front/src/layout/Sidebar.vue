<template>
  <aside class="sidebar">
    <div class="sidebar-title">{{ activeModuleName }}</div>
    <el-menu
      :default-active="activeMenu"
      background-color="#fafbfc"
      text-color="#646a73"
      active-text-color="#3370ff"
      router
    >
      <template v-for="menu in sidebarMenus" :key="menu.id">
        <menu-item :menu="menu" />
      </template>
      <el-menu-item v-if="sidebarMenus.length === 0" disabled>
        <span style="color: #bbbfc4">暂无子菜单</span>
      </el-menu-item>
    </el-menu>
  </aside>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'
import MenuItem from './MenuItem.vue'

const route = useRoute()
const userStore = useUserStore()
const activeMenu = computed(() => route.path)

const modules = computed(() => userStore.menus || [])

const activeModuleId = computed(() => {
  const path = route.path
  for (const mod of modules.value) {
    if (mod.path === path) return mod.id
    if (mod.children) {
      for (const child of mod.children) {
        if (child.path === path) return mod.id
        if (child.children) {
          for (const sub of child.children) {
            if (sub.path === path) return mod.id
          }
        }
      }
    }
  }
  return modules.value[0]?.id || 0
})

const activeModuleName = computed(() => {
  const mod = modules.value.find(m => m.id === activeModuleId.value)
  return mod?.menuName || ''
})

const sidebarMenus = computed(() => {
  const mod = modules.value.find(m => m.id === activeModuleId.value)
  return mod?.children || []
})
</script>

<style lang="scss" scoped>
.sidebar {
  width: 220px;
  min-height: calc(100vh - 52px);
  background: #fafbfc;
  border-right: 1px solid #e5e6eb;
  overflow-y: auto;
  flex-shrink: 0;
  .sidebar-title {
    padding: 14px 20px;
    font-size: 12px;
    font-weight: 600;
    color: #8f959e;
    letter-spacing: 1px;
    text-transform: uppercase;
  }
  :deep(.el-menu) {
    border-right: none;
    background: transparent;
  }
  :deep(.el-menu-item) {
    font-size: 14px;
    height: 40px;
    line-height: 40px;
    margin: 2px 8px;
    border-radius: 6px;
    &:hover {
      background: #eef0f4;
    }
    &.is-active {
      background: #e1e9ff;
      color: #3370ff;
    }
  }
  :deep(.el-sub-menu__title) {
    font-size: 14px;
    height: 40px;
    line-height: 40px;
    margin: 2px 8px;
    border-radius: 6px;
    &:hover {
      background: #eef0f4;
    }
  }
}
</style>
