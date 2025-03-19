// 根据环境判断使用哪个接口地址
const ENV = {
  development: 'http://localhost:9000',
  production: 'https://your-production-api.com', // 替换成您的正式环境接口地址
}

const BASE_URL = process.env.NODE_ENV === 'development' 
  ? ENV.development
  : ENV.production

// 请求拦截器
const requestInterceptor = (config) => {
  // 在开发环境下，如果是在真机调试，使用本地IP地址
  if (process.env.NODE_ENV === 'development') {
    const systemInfo = uni.getSystemInfoSync()
    // 如果是真机环境
    if (!systemInfo.platform.includes('devtools')) {
      // 替换成电脑的本地IP地址
      config.url = config.url.replace('localhost', '192.168.17.1') // 本机IP
    }
  }

  const token = uni.getStorageSync('token')
  if (token) {
    config.header = {
      ...config.header,
      'Authorization': token
    }
  }
  return config
}

// 响应拦截器
const responseInterceptor = (response) => {
  const { statusCode, data } = response
  if (statusCode === 200) {
    return data
  }
  
  //  处理错误情况
  if (statusCode === 403) {
    uni.showToast({
      title: '登录过期，请重新登录',
      icon: 'none'
    })

    uni.clearStorageSync()
    
    setTimeout(() => {
      uni.navigateTo({
        url: '/pages/login/index'
      })
    }, 1500)
  }
  
  return Promise.reject(data)
}

// 统一请求方法
export const request = (options) => {
  return new Promise((resolve, reject) => {
    const config = requestInterceptor(options)
    
    uni.request({
      ...config,
      url: `${BASE_URL}${config.url}`,
      success: (res) => {
        resolve(responseInterceptor(res))
      },
      fail: (error) => {
        uni.showToast({
          title: '网络错误',
          icon: 'none'
        })
        reject(error)
      }
    })
  })
} 