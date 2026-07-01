import request from '@/utils/request'

/** 查询认证模式配置（SSO / Mock） */
export function getAuthConfig() {
  return request.get('/api/auth/config')
}

/** 登录（mock 模式或兼容旧密码模式） */
export function login(data) {
  return request({ url: '/api/auth/login', method: 'post', data })
}

export function logout() {
  return request({ url: '/api/auth/logout', method: 'post' })
}

export function getUserInfo() {
  return request({ url: '/api/auth/userinfo', method: 'get' })
}

/** SSO 登录地址 */
export function ssoLoginUrl() {
  return request.get('/api/auth/sso/login-url')
}

/** SSO ticket 登录 */
export function ssoLogin(ticket) {
  return request.post('/api/auth/sso/login', { ticket })
}
