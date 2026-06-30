import request from '@/utils/request'

export function getUserList(params) {
  return request({ url: '/api/system/user/list', method: 'get', params })
}

export function getUserById(id) {
  return request({ url: `/api/system/user/${id}`, method: 'get' })
}

export function createUser(data) {
  return request({ url: '/api/system/user', method: 'post', data })
}

export function updateUser(data) {
  return request({ url: '/api/system/user', method: 'put', data })
}

export function deleteUser(id) {
  return request({ url: `/api/system/user/${id}`, method: 'delete' })
}

export function assignUserRoles(userId, roleIds) {
  return request({ url: `/api/system/user/${userId}/roles`, method: 'post', data: roleIds })
}

export function getUserRoles(userId) {
  return request({ url: `/api/system/user/${userId}/roles`, method: 'get' })
}
