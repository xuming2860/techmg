import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 30000
})

// 防止 token 过期时多个并发请求重复跳转登录页
let isRedirecting = false

// 请求拦截器
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
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

    // 按错误码分类提示
    switch (res.code) {
      case 401:
        handleAuthExpired()
        break
      case 403:
        // 403 也可能是 token 过期（旧版兼容）→ 清除 token 重登
        handleAuthExpired()
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
    }
    ElMessage.error(error.message || '网络异常')
    return Promise.reject(error)
  }
)

function handleAuthExpired() {
  if (isRedirecting) return // 已经在跳转中，不再重复
  isRedirecting = true

  ElMessage.error('登录已过期，请重新登录')
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  window.location.href = '/login'
}

export default request
