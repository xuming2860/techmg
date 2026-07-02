<template>
  <div class="error-page">
    <div class="error-card">
      <div class="error-code">404</div>
      <h2>页面不存在</h2>
      <p>您访问的页面不存在或已被移除</p>
      <div class="error-actions">
        <el-button type="primary" @click="$router.push({ name: '首页' })">返回首页</el-button>
        <el-button @click="$router.go(-1)">返回上页</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts"></script>

<style lang="scss" scoped>
.error-page {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
}
.error-card {
  text-align: center;
  padding: 48px;
}
.error-code {
  font-size: 120px;
  font-weight: 800;
  line-height: 1;
  background: linear-gradient(135deg, #3370ff 0%, #5c8dff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 16px;
  letter-spacing: -4px;
}
h2 {
  font-size: 22px;
  color: #1f2329;
  margin: 0 0 8px;
  font-weight: 600;
}
p {
  font-size: 14px;
  color: #8f959e;
  margin: 0 0 32px;
}
.error-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}
</style>
