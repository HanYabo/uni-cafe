package com.han.cafe.service;

import java.util.List;

import com.han.cafe.entity.Coupon;
import com.han.cafe.vo.UserCouponVO;

public interface CouponService {
    /**
     * 获取用户可用的优惠券列表
     */
    List<UserCouponVO> getUserCoupons(Integer userId);
    
    /**
     * 领取优惠券
     */
    void receiveCoupon(Integer userId, Integer couponId);
    
    /**
     * 检查优惠券是否可用
     */
    Coupon checkCoupon(Integer userId, Integer couponId, java.math.BigDecimal orderAmount);
} 