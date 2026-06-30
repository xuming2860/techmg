<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2>技术管理平台</h2>
      <el-form :model="form" label-width="80px">
        <el-form-item label="认证号">
          <el-input v-model="form.authNo" placeholder="请输入统一认证号" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width:100%" @click="handleLogin">登 录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { ElMessage } from 'element-plus'

const form = reactive({ authNo: '', password: '' })

function handleLogin() {
  // TODO: 对接后端登录接口
  ElMessage.info('登录功能待对接后端')
}
</script>

<style lang="scss" scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f7fa;
}
.login-card {
  width: 400px;
  h2 {
    text-align: center;
    margin-bottom: 24px;
  }
}
</style>
