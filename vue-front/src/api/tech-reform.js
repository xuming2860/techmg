import request from '@/utils/request'

// === 父任务 ===
export function pageTasks(params) {
  return request.get('/api/tech-reform/task/list', { params })
}
export function getTask(id) {
  return request.get(`/api/tech-reform/task/${id}`)
}
export function createTask(data) {
  return request.post('/api/tech-reform/task', data)
}
export function updateTask(id, data) {
  return request.put(`/api/tech-reform/task/${id}`, data)
}
export function updateTaskStatus(id, status) {
  return request.put(`/api/tech-reform/task/${id}/status`, { status })
}
export function deleteTask(id) {
  return request.delete(`/api/tech-reform/task/${id}`)
}

// === 子任务 ===
export function pageSubtasks(params) {
  return request.get('/api/tech-reform/subtask/list', { params })
}
export function getSubtask(id) {
  return request.get(`/api/tech-reform/subtask/${id}`)
}
export function createSubtask(data) {
  return request.post('/api/tech-reform/subtask', data)
}
export function updateSubtask(id, data) {
  return request.put(`/api/tech-reform/subtask/${id}`, data)
}
export function updateSubtaskStatus(id, status) {
  return request.put(`/api/tech-reform/subtask/${id}/status`, { status })
}
export function deleteSubtask(id) {
  return request.delete(`/api/tech-reform/subtask/${id}`)
}

// === 治理条目 ===
export function pageItems(params) {
  return request.get('/api/tech-reform/item/list', { params })
}
export function createItem(data) {
  return request.post('/api/tech-reform/item', data)
}
export function updateItem(id, data) {
  return request.put(`/api/tech-reform/item/${id}`, data)
}
export function deleteItem(id) {
  return request.delete(`/api/tech-reform/item/${id}`)
}
export function importItems(subtaskId, file, mode) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('mode', mode)
  return request.post(`/api/tech-reform/item/upload?subtaskId=${subtaskId}`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
export function exportItems(subtaskId) {
  return request.get(`/api/tech-reform/item/export?subtaskId=${subtaskId}`, {
    responseType: 'blob'
  })
}
export function batchUpdateItems(subtaskId, file) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post(`/api/tech-reform/item/batch-update?subtaskId=${subtaskId}`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// === 字典 ===
export function getDictByType(dictType) {
  return request.get(`/api/system/dict/data/${dictType}`)
}
