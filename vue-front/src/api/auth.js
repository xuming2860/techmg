import request from '@/utils/request'

export function login(data) {
  return request({ url: '/api/auth/login', method: 'post', data })
}

export function logout() {
  return request({ url: '/api/auth/logout', method: 'post' })
}

export function getUserInfo() {
  return request({ url: '/api/auth/userinfo', method: 'get' })
}

export function ssoLoginUrl() {
  return request.get('/api/auth/sso/login-url')
}

export function ssoLogin(ticket) {
  return request.post('/api/auth/sso/login', { ticket })
}
