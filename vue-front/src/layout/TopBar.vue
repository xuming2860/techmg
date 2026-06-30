<template>
  <header class="topbar">
    <div class="topbar-left">
      <h1 class="topbar-title">技术管理平台</h1>
    </div>
    <div class="topbar-right">
      <span class="user-info">{{ userStore.userInfo?.realName || '未登录' }}</span>
      <el-button text @click="handleLogout">退出</el-button>
    </div>
  </header>
</template>

<script setup>
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

function handleLogout() {
  userStore.logout()
  window.location.href = '/login'
}
</script>

<style lang="scss" scoped>
.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 56px;
  padding: 0 20px;
  background-color: #fff;
  border-bottom: 1px solid #e4e7ed;
  .topbar-title {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
  }
  .user-info {
    margin-right: 12px;
    color: #606266;
  }
}
</style>
