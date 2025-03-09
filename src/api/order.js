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

// 用户支付下单操作
export const payOrderAPI = (orderId) => {
  return request({
    url: `/api/orders/${orderId}/pay`,
    method: 'POST'
  })
}

// 用户取消订单
export const cancelOrderAPI = (orderId) => {
  return request({
    url: `/api/orders/${orderId}/cancel`,
    method: 'POST',
  })
}

// 用户删除订单
export const deleteOrderAPI = (orderId) => {
  return request({
    url: `/api/orders/${orderId}/delete`,
    method: 'POST'
  })
}

// 通过订单id获取订单详情
export const getOrderDetailAPI = (orderId) => {
  return request({
    url: `/api/orders/${orderId}`,
    method: 'GET'
  })
}


