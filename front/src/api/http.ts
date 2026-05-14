import axios from 'axios'
import type { ApiResult } from '../types/room'

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})

http.interceptors.response.use(
  (response) => response.data,
  (error) => {
    const message = error.response?.data?.message || '网络请求失败'
    return Promise.reject(new Error(message))
  }
)

export default http as unknown as {
  get<T>(url: string, config?: object): Promise<ApiResult<T>>
  post<T>(url: string, data?: object, config?: object): Promise<ApiResult<T>>
  put<T>(url: string, data?: object, config?: object): Promise<ApiResult<T>>
  delete<T>(url: string, config?: object): Promise<ApiResult<T>>
}
