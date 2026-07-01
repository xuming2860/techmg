<template>
  <header class="topbar">
    <div class="topbar-left">
      <span class="logo">ICBC</span>
      <span class="title">技术管理平台</span>
    </div>
    <nav class="topbar-nav">
      <template v-for="mod in modules" :key="mod.id">
        <router-link
          v-if="mod.path"
          :to="mod.path"
          class="nav-item"
          :class="{ active: activeModule === mod.id }"
          >{{ mod.menuName }}</router-link
        >
        <router-link
          v-else-if="mod.children?.length"
          :to="getFirstPath(mod)"
          class="nav-item"
          :class="{ active: activeModule === mod.id }"
          >{{ mod.menuName }}</router-link
        >
      </template>
    </nav>
    <div class="topbar-right">
      <el-dropdown trigger="click" @command="handleCommand">
        <span class="user-dropdown">
          <span class="avatar">{{ avatarText }}</span>
          <span class="user-name">{{ userStore.userInfo?.realName || '未登录' }}</span>
          <el-icon class="arrow"><ArrowDown /></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </header>
</template>

<script setup>
import { computed } from 'vue'
import { useUserStore } from '@/store/user'
import { useRoute } from 'vue-router'

const route = useRoute()
const userStore = useUserStore()

const modules = computed(() => userStore.menus || [])

const avatarText = computed(() => {
  const name = userStore.userInfo?.realName || '?'
  return name.charAt(0)
})

const activeModule = computed(() => {
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

/** Find first leaf menu path recursively for directory nodes */
function getFirstPath(mod) {
  if (mod.path) return mod.path
  if (mod.children?.length) {
    for (const child of mod.children) {
      const path = getFirstPath(child)
      if (path) return path
    }
  }
  return '/'
}

function handleCommand(cmd) {
  if (cmd === 'logout') {
    userStore.logout()
    window.location.href = '/login'
  }
}
</script>

<style lang="scss" scoped>
.topbar {
  display: flex;
  align-items: center;
  height: 52px;
  padding: 0 24px;
  background: #fff;
  border-bottom: 1px solid #e5e6eb;
  flex-shrink: 0;
  z-index: 100;
}
.topbar-left {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-right: 36px;
  flex-shrink: 0;
  .logo {
    font-size: 13px;
    font-weight: 700;
    letter-spacing: 0.5px;
    color: #fff;
    background: #3370ff;
    padding: 3px 10px;
    border-radius: 4px;
  }
  .title {
    font-size: 15px;
    font-weight: 500;
    color: #1f2329;
    white-space: nowrap;
  }
}
.topbar-nav {
  display: flex;
  flex: 1;
  gap: 0;
  .nav-item {
    padding: 15px 16px;
    font-size: 14px;
    color: #646a73;
    text-decoration: none;
    border-bottom: 2px solid transparent;
    transition: all 0.15s;
    white-space: nowrap;
    &:hover {
      color: #3370ff;
    }
    &.active {
      color: #3370ff;
      border-bottom-color: #3370ff;
      font-weight: 500;
    }
  }
}
.topbar-right {
  display: flex;
  align-items: center;
  margin-left: auto;
  flex-shrink: 0;
}
.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 6px;
  transition: background 0.15s;
  &:hover {
    background: #f2f3f5;
  }
  .avatar {
    width: 30px;
    height: 30px;
    border-radius: 50%;
    background: #3370ff;
    color: #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 13px;
    font-weight: 600;
    flex-shrink: 0;
  }
  .user-name {
    font-size: 13px;
    color: #1f2329;
  }
  .arrow {
    font-size: 12px;
    color: #8f959e;
    margin-left: 2px;
  }
}
</style>
