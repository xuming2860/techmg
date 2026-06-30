import request from '@/utils/request'

export function getRoleList(params) {
  return request({ url: '/api/system/role/list', method: 'get', params })
}

export function getRoleById(id) {
  return request({ url: `/api/system/role/${id}`, method: 'get' })
}

export function createRole(data) {
  return request({ url: '/api/system/role', method: 'post', data })
}

export function updateRole(data) {
  return request({ url: '/api/system/role', method: 'put', data })
}

export function deleteRole(id) {
  return request({ url: `/api/system/role/${id}`, method: 'delete' })
}

export function getRoleMenus(roleId) {
  return request({ url: `/api/system/role/${roleId}/menus`, method: 'get' })
}

export function assignRoleMenus(roleId, menuIds) {
  return request({ url: `/api/system/role/${roleId}/menus`, method: 'post', data: menuIds })
}
