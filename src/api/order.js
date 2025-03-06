import { request } from '@/utils/request';

// 创建订单
export const createOrder = (data) => {
  return request({
    url: '/api/orders',
    method: 'POST',
    data
  })
}

