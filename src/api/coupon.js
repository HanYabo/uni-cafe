// 获取优惠券列表
export const getCouponListAPI = () => {
  return request({
    url: '/coupons/user',
    method: 'GET'
  })
}
