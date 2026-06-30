<template>
  <div class="layout-side">
    <TopBar />
    <div class="layout-body">
      <Sidebar />
      <main class="layout-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import TopBar from './TopBar.vue'
import Sidebar from './Sidebar.vue'
</script>

<style lang="scss" scoped>
.layout-side {
  display: flex;
  flex-direction: column;
  height: 100vh;
}
.layout-body {
  display: flex;
  flex: 1;
  overflow: hidden;
}
.layout-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #f5f7fa;
}
</style>
