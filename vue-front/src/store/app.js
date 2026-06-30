import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  // 'side' = 上左右布局, 'top' = 上下布局
  const layout = ref(localStorage.getItem('layout') || 'side')

  function setLayout(mode) {
    layout.value = mode
    localStorage.setItem('layout', mode)
  }

  return { layout, setLayout }
})
