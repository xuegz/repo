import { computed } from 'vue'

/**
 * el-upload 的动态鉴权头。
 * 不能在模块加载时固化 token（重新登录后旧 token 会导致上传 401），
 * 用 computed 保证每次取到 localStorage 里的最新值。
 */
export function useUploadHeaders() {
  return computed(() => ({ Authorization: localStorage.getItem('token') || '' }))
}
