<template>
  <div class="login-page">
    <div class="login-box">
      <div class="login-header">
        <span class="logo">ICBC</span>
        <h2>技术管理平台</h2>
        <p>工商银行上海研发基地</p>
      </div>

      <!-- 加载中 / Mock 自动登录中 -->
      <template v-if="authMode === '' || authMode === 'mock'">
        <div style="text-align: center; padding: 20px; color: #8f959e;">
          <el-icon class="is-loading" :size="20"><Loading /></el-icon>
          <p style="margin-top: 8px;">{{ statusText }}</p>
        </div>
      </template>

      <!-- SSO 模式 — 统一认证登录按钮 -->
      <template v-else-if="authMode === 'sso'">
        <el-button type="primary" size="large" style="width: 100%; height: 44px; font-size: 15px"
          :loading="loading" @click="handleSsoLogin">
          统一认证登录
        </el-button>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import { getAuthConfig, ssoLoginByConfig } from '@/api/auth'
import { getUserMenuTree } from '@/api/system/menu'
import { useUserStore } from '@/store/user'
import axios from 'axios'

const router = useRouter()
const userStore = useUserStore()

const authMode = ref('')        // '' | 'sso' | 'mock'
const loading = ref(false)
const statusText = ref('正在获取认证配置...')

// ========== 通用：登录成功 → 跳转 ==========

async function afterLogin(data: any) {
  console.debug('[Login] afterLogin received:', data)
  userStore.setToken(data.token)
  if (data.userInfo) {
    userStore.setUserInfo(data.userInfo)
  } else {
    userStore.setUserInfo(data)
  }

  try {
    const tree = await getUserMenuTree()
    userStore.setMenus(tree || [])
  } catch (e) {
    console.warn('[Login] getUserMenuTree failed:', e)
  }

  console.debug('[Login] token set:', !!userStore.token, 'userInfo:', userStore.userInfo)

  // 恢复登录前的目标 URL（SSO 重定向前保存的）
  const targetUrl = localStorage.getItem('TARGET_URL')
  const targetQuery = localStorage.getItem('TARGET_QUERY')
  localStorage.removeItem('TARGET_URL')
  localStorage.removeItem('TARGET_QUERY')

  if (targetUrl) {
    const query = targetQuery ? JSON.parse(targetQuery) : {}
    router.replace({ path: targetUrl, query })
  } else {
    router.replace({ name: 'main' })
  }
}

// ========== Mock 自动登录 ==========

async function autoMockLogin() {
  statusText.value = '正在登录...'
  try {
    const data = await axios.post('/api/auth/login', { authNo: '', password: '' })
    // 解包 R<T> 响应
    const res = data.data || data
    if (res.code === 200) {
      await afterLogin(res.data)
    } else {
      throw new Error(res.message || '登录失败')
    }
  } catch (e: any) {
    console.error('[Login] mock login failed:', e)
    statusText.value = '登录失败，请刷新页面重试'
  }
}

// ========== SSO 登录 ==========

/**
 * 点击"统一认证登录"按钮 → GET /api/auth/login → 302 重定向到 SSO 登录页。
 *
 * 对应行内流程:
 *   前端无 token → GET /api/auth/login → 后端 302 → 浏览器跳转到 https://aam.icbc/.../login/aam2/tmvp
 */
async function handleSsoLogin() {
  loading.value = true
  try {
    // 保存当前目标 URL，登录成功后恢复
    const targetUrl = localStorage.getItem('TARGET_URL')
    if (!targetUrl) {
      localStorage.setItem('TARGET_URL', window.location.pathname === '/login' ? '/' : window.location.pathname)
    }
    // 直接 GET 请求 → 浏览器会跟随 302 重定向到 SSO 登录页
    window.location.href = `${import.meta.env.VITE_API_BASE_URL}/api/auth/login`
  } catch (err) {
    ElMessage.error('跳转统一认证失败')
    loading.value = false
  }
}

// ========== 初始化 ==========

onMounted(async () => {
  const url = window.location.href

  // ================================================================
  // 1. 处理 SSO 回调 — URL 带 ?SSIAuth=xxx&SSI_SIGN=xxx
  //    对应行内流程: 统一认证登录成功后回跳 client.site.url?SSIAuth=xxx&SSI_SIGN=xxx
  // ================================================================
  if (url.indexOf('SSIAuth') > 0) {
    loading.value = true
    authMode.value = 'sso'
    statusText.value = '正在验证统一认证...'

    // 截取 ? 后的全部参数作为 config
    const index = url.indexOf('?')
    const config = url.substring(index + 1)

    try {
      const res = await ssoLoginByConfig(config)
      await afterLogin(res)
      return
    } catch (e: any) {
      console.error('[Login] SSO login failed:', e)
      ElMessage.error(e?.response?.data?.message || '统一认证登录失败')

      // 检查是否需要跳转到统一认证报错页
      const errData = e?.response?.data
      if (errData?.path && errData.path.indexOf('uniformattestation') !== -1) {
        const jump = errData.path.replace('userService/', '')
        window.location.href = 'https://' + 'aam.icbc' + jump
        return
      }

      // 清除 URL 中的 SSI 参数，避免重复提交
      window.history.replaceState(null, '', window.location.pathname)
      loading.value = false
      statusText.value = ''
      // 重新初始化 — 显示 SSO 登录按钮
      authMode.value = 'sso'
      return
    }
  }

  // ================================================================
  // 2. 无 SSI 参数 — 查询后端认证模式
  // ================================================================
  try {
    const config = await getAuthConfig()
    if (config.ssoEnabled) {
      // SSO 模式 — 显示"统一认证登录"按钮
      // 用户点击后 GET /api/auth/login → 302 重定向到 SSO 登录页
      authMode.value = 'sso'
    } else {
      // Mock 模式 — 自动登录，用户无感知
      authMode.value = 'mock'
      await autoMockLogin()
    }
  } catch (err) {
    // 查询失败，默认自动 mock 登录
    console.warn('[Login] getAuthConfig failed, fallback to mock:', err)
    authMode.value = 'mock'
    await autoMockLogin()
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
