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

<script setup lang="ts">
import TopBar from './TopBar.vue'
import Sidebar from './Sidebar.vue'
</script>

<style lang="scss" scoped>
.layout-side { display: flex; flex-direction: column; height: 100vh; background: #f5f6f8; }
.layout-body { display: flex; flex: 1; overflow: hidden; }
.layout-content { flex: 1; padding: 20px 24px; overflow-y: auto; }
</style>
