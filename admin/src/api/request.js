import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',
  timeout: 20000
})

request.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = token
  }
  return config
})

request.interceptors.response.use(
  (response) => {
    const body = response.data
    // 后端统一返回 { code, msg, data }
    if (body && body.code === 200) {
      return body
    }
    const msg = (body && body.msg) || '请求失败'
    ElMessage.error(msg)
    if (body && (body.code === 401 || body.code === 403)) {
      localStorage.clear()
      setTimeout(() => {
        window.location.href = '/login'
      }, 500)
    }
    return Promise.reject(new Error(msg))
  },
  (error) => {
    ElMessage.error('网络异常，请检查后端服务是否已启动')
    return Promise.reject(error)
  }
)

export default request
