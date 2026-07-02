/// <reference types="vite/client" />

declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}

declare module 'element-plus/global' {}

interface ImportMetaEnv {
  readonly VITE_API_BASE_URL: string
  readonly VITE_USERINFO_ENCRYPT_KEY: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
