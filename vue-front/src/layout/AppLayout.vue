<template>
  <component :is="layoutComponent" />
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import DefaultLayout from './DefaultLayout.vue'
import TopOnlyLayout from './TopOnlyLayout.vue'

const route = useRoute()
const layoutComponent = computed(() => {
  // Check current route's meta.layout, default to 'side'
  return route.meta.layout === 'top' ? TopOnlyLayout : DefaultLayout
})
</script>
