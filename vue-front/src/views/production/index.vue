<template>
  <div class="production">
    <h2>生产管理</h2>
    <el-card>
      <el-empty description="生产管理模块，功能开发中">
        <template #image>
          <el-icon :size="80" color="#909399"><Monitor /></el-icon>
        </template>
      </el-empty>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { Monitor } from '@element-plus/icons-vue'
</script>

<style lang="scss" scoped>
.production {
  h2 {
    margin-bottom: 16px;
  }
}
</style>
