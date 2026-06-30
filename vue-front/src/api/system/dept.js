import request from '@/utils/request'

export function getDeptTree() {
  return request({ url: '/api/system/dept/tree', method: 'get' })
}

export function getDeptById(id) {
  return request({ url: `/api/system/dept/${id}`, method: 'get' })
}

export function createDept(data) {
  return request({ url: '/api/system/dept', method: 'post', data })
}

export function updateDept(data) {
  return request({ url: '/api/system/dept', method: 'put', data })
}

export function deleteDept(id) {
  return request({ url: `/api/system/dept/${id}`, method: 'delete' })
}
