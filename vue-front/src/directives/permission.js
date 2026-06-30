import { useUserStore } from '@/store/user'

export default {
  mounted(el, binding) {
    const { value } = binding
    const userStore = useUserStore()
    const permissions = userStore.permissions
    if (value && !permissions.includes(value)) {
      el.parentNode?.removeChild(el)
    }
  }
}
