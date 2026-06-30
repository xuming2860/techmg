<template>
  <div class="login-page">
    <div class="login-box">
      <div class="login-header">
        <span class="logo">ICBC</span>
        <h2>技术管理平台</h2>
        <p>工商银行上海研发基地</p>
      </div>
      <el-form :model="form" size="large">
        <el-form-item>
          <el-input v-model="form.authNo" placeholder="统一认证号" prefix-icon="User" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" show-password
            @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width:100%;height:44px;font-size:15px" @click="handleLogin">
            登 录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login, getUserInfo } from '@/api/auth'
import { getUserMenuTree } from '@/api/system/menu'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()
const form = reactive({ authNo: '', password: '' })

async function handleLogin() {
  if (!form.authNo || !form.password) {
    ElMessage.warning('请输入认证号和密码')
    return
  }
  try {
    const data = await login(form)
    userStore.setToken(data.token)
    try { const info = await getUserInfo(); userStore.setUserInfo(info.userInfo||info); userStore.setRoles(info.roles||[]); userStore.setPermissions(info.permissions||[]) } catch {}
    try { const tree = await getUserMenuTree(); userStore.setMenus(tree||[]) } catch {}
    ElMessage.success('登录成功')
    router.push('/')
  } catch {}
}
</script>

<style lang="scss" scoped>
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #f0f2f5 0%, #e8eaef 100%);
}
.login-box {
  width: 380px;
  padding: 40px 36px 32px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 16px rgba(0,0,0,.06);
}
.login-header {
  text-align: center;
  margin-bottom: 32px;
  .logo {
    display: inline-block;
    font-size: 13px;
    font-weight: 700;
    color: #fff;
    background: #3370ff;
    padding: 3px 10px;
    border-radius: 4px;
    margin-bottom: 16px;
  }
  h2 { font-size: 20px; font-weight: 600; color: #1f2329; margin: 0 0 6px; }
  p { font-size: 13px; color: #8f959e; margin: 0; }
}
</style>
