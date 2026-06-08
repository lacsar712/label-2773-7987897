import axios, { type AxiosRequestConfig } from 'axios'
import { message } from 'ant-design-vue'

const service = axios.create({
  baseURL: '',
  timeout: 15000
})

service.interceptors.request.use(
  (config) => {
    return config
  },
  (error) => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  (response) => {
    if (response.config.responseType === 'blob') {
      return response.data
    }
    const res = response.data
    if (res.code !== 200) {
      message.error(res.message || 'Error')
      return Promise.reject(new Error(res.message || 'Error'))
    } else {
      return res
    }
  },
  (error) => {
    console.error('Response error:', error)
    const msg = error.response?.data?.message || error.message || 'Network Error'
    message.error(msg)
    return Promise.reject(error)
  }
)

export default service

export const request = {
  get: <T = any>(url: string, config?: AxiosRequestConfig): Promise<T> =>
    service.get(url, config) as Promise<T>,
  post: <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> =>
    service.post(url, data, config) as Promise<T>,
  put: <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> =>
    service.put(url, data, config) as Promise<T>,
  delete: <T = any>(url: string, config?: AxiosRequestConfig): Promise<T> =>
    service.delete(url, config) as Promise<T>
}
