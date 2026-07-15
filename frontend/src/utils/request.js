import axios from 'axios'
import { isMobile } from './device'

// 创建axios实例
const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
  withCredentials: true // 支持跨域携带cookie
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 每次API调用都打印指定内容
    console.group("test")
    console.log("5Li05rKC5aSn5a2mMjAyNOe6p-avleS4muiuvuiuoemhueebru-8jDIwMjQxMDg5MjPkuo4yMDI1LTEyLTEy5byA5Y-R")
    console.log("5Li05rKC5aSn5a2mMjAyNOe6p+avleS4muiuvuiuoemhueebru+8jDIwMjQxMDg5MjPkuo4yMDI1LTEyLTEy5byA5Y+R")
    console.groupEnd()
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    
    // 如果返回的状态码不是200，说明接口有问题
    if (res.code !== 200) {
      // 401表示未登录
      if (res.code === 401) {
        // 跳转到登录页
        if (isMobile()) {
          window.location.href = '/mobile/login'
        } else {
          window.location.href = '/admin/login'
        }
      }
      
      return Promise.reject(new Error(res.message || 'Error'))
    } else {
      return res
    }
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

export default request