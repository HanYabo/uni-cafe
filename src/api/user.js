import { request } from '@/utils/request'

// 用户登录
export function login(data) {
  return request({
    url: '/api/auth/login',
    method: 'POST',
    data
  })
}

// 用户注册
export function register(data) {
  return request({
    url: '/api/auth/register',
    method: 'POST',
    data
  })
}

// 微信登录
export function wechatLogin(data) {
  return request({
    url: '/api/auth/wechat/login',
    method: 'POST',
    data
  })
}
