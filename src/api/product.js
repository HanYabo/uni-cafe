import { request } from '@/utils/request'

// 获取热门畅销商品列表
export function getHotProducts() {
  return request({
    url: '/api/products/hot',
    method: 'GET'
  })
} 