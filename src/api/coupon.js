import { request } from '@/utils/request'

// 获取优惠券列表
export const getCouponListAPI = () => {
  return request({
    url: '/api/coupons/user',
    method: 'GET'
  })
}


