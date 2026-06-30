import request from '@/utils/request'

export function getMenuTree() {
  return request({ url: '/api/system/menu/tree', method: 'get' })
}

export function getMenuById(id) {
  return request({ url: `/api/system/menu/${id}`, method: 'get' })
}

export function createMenu(data) {
  return request({ url: '/api/system/menu', method: 'post', data })
}

export function updateMenu(data) {
  return request({ url: '/api/system/menu', method: 'put', data })
}

export function deleteMenu(id) {
  return request({ url: `/api/system/menu/${id}`, method: 'delete' })
}

export function getUserMenuTree() {
  return request({ url: '/api/system/menu/user-tree', method: 'get' })
}
