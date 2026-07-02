import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 30000,
  withCredentials: true  // 跨域请求时发送 cookie（SSO 场景需要）
})

// 防止 token 过期时多个并发请求重复跳转登录页
let isRedirecting = false

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 1. 附加 JWT Token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }

    // 2. 附加加密的用户信息到请求头（内网 SSO 场景，后端可能需要）
    const userInfo = localStorage.getItem('userInfo')
    if (userInfo) {
      try {
        config.headers['X-User-Info'] = userInfo
      } catch (e) {
        // ignore
      }
    }

    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    // Blob 文件下载，直接返回
    if (response.config.responseType === 'blob') {
      return response.data
    }

    const res = response.data
    if (res.code === 200) {
      return res.data
    }

    // SSO 重定向响应（code=302）→ 跳转到 SSO 登录页
    if (res.code === 302 && res.data?.needRedirect) {
      handleAuthExpired()
      return Promise.reject(new Error('Need SSO redirect'))
    }

    // 按错误码分类提示
    switch (res.code) {
      case 401:
        // 401 = token 过期/无效 → 重新登录
        handleAuthExpired()
        break
      case 403:
        // 403 = 无权限（token 有效但无此资源权限）→ 仅提示，不跳登录
        // 如果跳登录会导致死循环：login → getUserMenuTree → 403 → login → ...
        ElMessage.error(res.message || '无权限访问')
        break
      case 404:
        ElMessage.error('请求的资源不存在')
        break
      case 500:
        ElMessage.error('服务器内部错误')
        break
      default:
        ElMessage.error(res.message || '请求失败')
    }
    return Promise.reject(new Error(res.message || '请求失败'))
  },
  error => {
    // HTTP 401 → token 过期
    if (error.response && error.response.status === 401) {
      handleAuthExpired()
    } else if (error.response && error.response.status === 403) {
      // HTTP 403 → 仅提示，不跳登录（避免死循环）
      ElMessage.error(error.response.data?.message || '无权限访问')
    } else {
      ElMessage.error(error.message || '网络异常')
    }
    return Promise.reject(error)
  }
)

/**
 * Token 过期 / 需要重新认证。
 * 清除本地状态，跳转到 /login 页。
 */
function handleAuthExpired() {
  if (isRedirecting) return
  isRedirecting = true

  // 已经在 /login 页 → 不再跳转，避免死循环
  if (window.location.pathname === '/login') {
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    return
  }

  ElMessage.error('登录已过期，请重新登录')
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  // 保存当前页面 URL，登录后恢复
  localStorage.setItem('TARGET_URL', window.location.pathname + window.location.search)
  window.location.href = '/login'
}

export default request
