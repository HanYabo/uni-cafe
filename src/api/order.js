import { request } from '@/utils/request';

// 创建订单
export const createOrder = (data) => {
  return request({
    url: '/api/orders',
    method: 'POST',
    data
  })
}

// 获取用户历史订单列表
export const getHistoryOrderAPI = (userId) => {
  return request({
    url: `/api/orders/history/${userId}`,
    method: 'GET'
  })
}

