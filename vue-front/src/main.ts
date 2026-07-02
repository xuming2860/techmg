import { createApp } from 'vue'
import { createPinia } from 'pinia'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'
import router from './router'
import permission from './directives/permission'
import './styles/index.scss'

const app = createApp(App)

// 注册所有 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 全局 Vue 异常捕获
app.config.errorHandler = (err, instance, info) => {
  console.error('[Vue Error]', err, { instance, info })
}

app.use(createPinia())
app.use(router)
app.directive('permission', permission)
app.mount('#app')
