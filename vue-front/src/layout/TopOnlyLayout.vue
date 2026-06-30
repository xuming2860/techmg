<template>
  <div class="layout-top">
    <TopBar />
    <main class="layout-content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import TopBar from './TopBar.vue'
</script>

<style lang="scss" scoped>
.layout-top {
  display: flex;
  flex-direction: column;
  height: 100vh;
}
.layout-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #f5f7fa;
}
</style>
