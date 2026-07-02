<template>
  <div class="layout-top">
    <TopBar />
    <main class="layout-content">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
import TopBar from './TopBar.vue'
</script>

<style lang="scss" scoped>
.layout-top { display: flex; flex-direction: column; height: 100vh; background: #f5f6f8; }
.layout-content { flex: 1; padding: 20px 24px; overflow-y: auto; }
</style>
