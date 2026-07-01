<template>
  <div class="login-page">
    <div class="login-box">
      <div class="login-header">
        <span class="logo">ICBC</span>
        <h2>技术管理平台</h2>
        <p>工商银行上海研发基地</p>
      </div>

      <!-- SSO 模式 -->
      <template v-if="authMode === 'sso'">
        <el-button type="primary" size="large" style="width: 100%; height: 44px; font-size: 15px"
          :loading="loading" @click="handleSsoLogin">
          统一认证登录
        </el-button>
      </template>

      <!-- Mock 模式 -->
      <template v-else-if="authMode === 'mock'">
        <el-button type="primary" size="large" style="width: 100%; height: 44px; font-size: 15px"
          :loading="loading" @click="handleMockLogin">
          一键登录（模拟模式）
        </el-button>
      </template>

      <!-- 加载中 -->
      <template v-else>
        <div style="text-align: center; padding: 20px; color: #8f959e;">正在获取认证配置...</div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login, getAuthConfig, ssoLoginUrl, ssoLogin } from '@/api/auth'
import { getUserMenuTree } from '@/api/system/menu'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const authMode = ref('')        // 'sso' | 'mock'
const loading = ref(false)

// ========== 通用：登录成功后处理 ==========

async function afterLogin(data) {
  userStore.setToken(data.token)
  userStore.setUserInfo(data.userInfo)

  try {
    const tree = await getUserMenuTree()
    userStore.setMenus(tree || [])
  } catch {}

  ElMessage.success('登录成功')
  router.replace('/')
}

// ========== Mock 模式 ==========

async function handleMockLogin() {
  loading.value = true
  try {
    const data = await login({ authNo: '', password: '' })
    await afterLogin(data)
  } catch {
    ElMessage.error('登录失败，请重试')
  } finally {
    loading.value = false
  }
}

// ========== SSO 模式 ==========

async function handleSsoLogin() {
  loading.value = true
  try {
    const res = await ssoLoginUrl()
    if (res.loginUrl) {
      window.location.href = res.loginUrl
    } else {
      ElMessage.warning('SSO 登录地址未配置')
    }
  } catch {
    ElMessage.error('获取 SSO 登录地址失败')
  } finally {
    loading.value = false
  }
}

// ========== 初始化：查询认证模式 + 处理 SSO 回调 ==========

onMounted(async () => {
  // 1. 查询后端认证模式
  try {
    const config = await getAuthConfig()
    if (config.ssoEnabled) {
      authMode.value = 'sso'
    } else {
      authMode.value = 'mock'
    }
  } catch {
    // 查询失败，默认走 mock 模式
    authMode.value = 'mock'
  }

  // 2. 处理 SSO 回调（URL 带 ticket 参数）
  const urlParams = new URLSearchParams(window.location.search)
  const ticket = urlParams.get('ticket')
  if (ticket) {
    loading.value = true
    try {
      const res = await ssoLogin(ticket)
      await afterLogin(res)
      return // afterLogin 里会 router.replace('/')
    } catch (e) {
      console.error('SSO login failed:', e)
      ElMessage.error('统一认证登录失败')
      window.history.replaceState(null, '', window.location.pathname)
    } finally {
      loading.value = false
    }
  }
})
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
  box-shadow: 0 2px 16px rgba(0, 0, 0, 0.06);
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
  h2 {
    font-size: 20px;
    font-weight: 600;
    color: #1f2329;
    margin: 0 0 6px;
  }
  p {
    font-size: 13px;
    color: #8f959e;
    margin: 0;
  }
}
</style>
