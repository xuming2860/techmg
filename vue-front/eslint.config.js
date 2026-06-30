import pluginVue from 'eslint-plugin-vue'
import vuePrettier from '@vue/eslint-config-prettier'
export default [
  ...pluginVue.configs['flat/essential'],
  vuePrettier,
  { rules: { 'vue/multi-word-component-names': 'off', 'no-unused-vars': 'warn' } }
]
