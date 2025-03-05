import { request } from '@/utils/request'

// 获取分类以及分类下的商品
export function getCategoryWithProducts() {
  return request({
    url: '/api/categories/with-products',
    method: 'GET'
  })
}
