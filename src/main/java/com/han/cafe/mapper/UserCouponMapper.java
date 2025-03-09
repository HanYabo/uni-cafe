package com.han.cafe.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.han.cafe.entity.UserCoupon;

@Mapper
public interface UserCouponMapper extends BaseMapper<UserCoupon> {
    
    @Select("SELECT COUNT(*) FROM user_coupon WHERE user_id = #{userId} AND coupon_id = #{couponId}")
    int countUserCoupon(Integer userId, Integer couponId);
} 