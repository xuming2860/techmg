<template>
  <div v-if="error" class="error-boundary">
    <div class="error-boundary-card">
      <el-icon :size="48" color="#e6a23c"><WarningFilled /></el-icon>
      <h2>页面出现异常</h2>
      <p>{{ error.message || '未知错误' }}</p>
      <el-button type="primary" @click="retry">重新加载</el-button>
    </div>
  </div>
  <slot v-else />
</template>

<script setup lang="ts">
import { ref, onErrorCaptured } from 'vue'

const error = ref(null)

onErrorCaptured(err => {
  error.value = err
  console.error('[ErrorBoundary]', err)
  return false // prevent propagation
})

function retry() {
  error.value = null
}
</script>

<style lang="scss" scoped>
.error-boundary {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  &-card {
    text-align: center;
    padding: 48px;
    h2 {
      font-size: 20px;
      color: #1f2329;
      margin: 16px 0 8px;
    }
    p {
      color: #8f959e;
      margin-bottom: 24px;
      font-size: 14px;
    }
  }
}
</style>
