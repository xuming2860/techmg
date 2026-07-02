import request from '@/utils/request'
import axios from 'axios'

/** 查询认证模式配置（SSO / Mock） */
export function getAuthConfig() {
  return request.get('/api/auth/config')
}

/** 登录（mock 模式） */
export function login(data) {
  return request({ url: '/api/auth/login', method: 'post', data })
}

/**
 * SSO 登录 — 传入 URL query string（SSIAuth=xxx&SSI_SIGN=xxx）。
 * 对应行内流程: 前端截取 URL ? 后全部参数 → POST /api/auth/login。
 *
 * 使用独立 axios 实例（不走 request 拦截器），因为此时还没有 token。
 */
export function ssoLoginByConfig(config) {
  return axios.post(
    `${import.meta.env.VITE_API_BASE_URL}/api/auth/login`,
    { config },
    {
      headers: { 'Content-Type': 'application/json' },
      withCredentials: true  // 跨域请求时发送 cookie
    }
  ).then(res => {
    // 解包 R<T>
    const body = res.data
    if (body.code === 200) return body.data
    // 302 need redirect
    if (body.code === 302 && body.data?.needRedirect) {
      window.location.href = body.data.redirectUrl
      return Promise.reject(new Error('redirecting to SSO...'))
    }
    return Promise.reject(new Error(body.message || 'SSO login failed'))
  })
}

/** SSO 登录地址（GET 方式，浏览器跳转用） */
export function ssoLoginUrl() {
  return request.get('/api/auth/sso/login-url')
}

/** SSO ticket 登录（已废弃，保留兼容） */
export function ssoLogin(ticket) {
  return request.post('/api/auth/sso/login', { ticket })
}

export function logout() {
  return request({ url: '/api/auth/logout', method: 'post' })
}

export function getUserInfo() {
  return request({ url: '/api/auth/userinfo', method: 'get' })
}
