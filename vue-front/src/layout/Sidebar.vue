<template>
  <aside class="sidebar">
    <el-menu
      :default-active="activeMenu"
      background-color="#304156"
      text-color="#bfcbd9"
      active-text-color="#409EFF"
      router
    >
      <el-menu-item index="/dashboard">
        <el-icon><HomeFilled /></el-icon>
        <span>首页工作台</span>
      </el-menu-item>
    </el-menu>
  </aside>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const activeMenu = computed(() => route.path)
</script>

<style lang="scss" scoped>
.sidebar {
  width: 220px;
  min-height: calc(100vh - 56px);
  background-color: #304156;
  overflow-y: auto;
  .el-menu {
    border-right: none;
  }
}
</style>
