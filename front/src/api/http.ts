import axios from 'axios'
import { ElMessage } from 'element-plus'
import type { ApiResult } from '../types/room'

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

http.interceptors.response.use(
  (response) => {
    const res = response.data as ApiResult<any>
    if (res.code !== 0 && res.code !== 200) {
      const msg = res.message || '操作失败'
      ElMessage.error(msg)
      return Promise.reject(new Error(msg))
    }
    return res as any
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/login'
      return Promise.reject(new Error('登录已过期'))
    }
    const message = error.response?.data?.message || '网络请求失败'
    ElMessage.error(message)
    return Promise.reject(new Error(message))
  }
)

export default http as unknown as {
  get<T>(url: string, config?: object): Promise<ApiResult<T>>
  post<T>(url: string, data?: object, config?: object): Promise<ApiResult<T>>
  put<T>(url: string, data?: object, config?: object): Promise<ApiResult<T>>
  delete<T>(url: string, config?: object): Promise<ApiResult<T>>
}
