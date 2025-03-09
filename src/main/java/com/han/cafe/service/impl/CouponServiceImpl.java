package com.han.cafe.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.han.cafe.entity.Coupon;
import com.han.cafe.entity.UserCoupon;
import com.han.cafe.exception.BusinessException;
import com.han.cafe.mapper.CouponMapper;
import com.han.cafe.mapper.UserCouponMapper;
import com.han.cafe.service.CouponService;
import com.han.cafe.vo.UserCouponVO;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CouponServiceImpl implements CouponService {

    @Resource
    private CouponMapper couponMapper;
    
    @Resource
    private UserCouponMapper userCouponMapper;
    
    @Override
    public List<UserCouponVO> getUserCoupons(Integer userId) {
        // 查询用户的优惠券
        List<UserCoupon> userCoupons = userCouponMapper.selectList(
            new LambdaQueryWrapper<UserCoupon>()
                .eq(UserCoupon::getUserId, userId)
                .eq(UserCoupon::getStatus, 0)  // 未使用的
        );
        
        // 获取优惠券详情
        return userCoupons.stream().map(uc -> {
            Coupon coupon = couponMapper.selectById(uc.getCouponId());
            if (coupon == null) {
                return null;
            }
            
            UserCouponVO vo = new UserCouponVO();
            vo.setCouponId(coupon.getCouponId());
            vo.setName(coupon.getName());
            vo.setType(coupon.getType());
            vo.setThreshold(coupon.getThreshold());
            vo.setAmount(coupon.getAmount());
            vo.setDiscount(coupon.getDiscount());
            vo.setMaxDiscount(coupon.getMaxDiscount());
            vo.setStartTime(coupon.getStartTime());
            vo.setEndTime(coupon.getEndTime());
            vo.setStatus(uc.getStatus());
            
            return vo;
        })
        .filter(vo -> vo != null)
        .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void receiveCoupon(Integer userId, Integer couponId) {
        // 检查优惠券是否存在且有效
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null || coupon.getStatus() != 1) {
            throw new BusinessException("优惠券不存在或已下架");
        }
        
        // 检查优惠券是否在有效期内
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getStartTime()) || now.isAfter(coupon.getEndTime())) {
            throw new BusinessException("优惠券不在有效期内");
        }
        
        // 检查用户是否已领取过该优惠券
        int count = userCouponMapper.countUserCoupon(userId, couponId);
        if (count > 0) {
            throw new BusinessException("您已领取过该优惠券");
        }
        
        // 保存用户优惠券记录
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(couponId);
        userCoupon.setStatus(0);  // 未使用
        userCouponMapper.insert(userCoupon);
    }
    
    @Override
    public Coupon checkCoupon(Integer userId, Integer couponId, BigDecimal orderAmount) {
        if (couponId == null) {
            return null;
        }
        
        // 查询用户优惠券
        UserCoupon userCoupon = userCouponMapper.selectOne(
            new LambdaQueryWrapper<UserCoupon>()
                .eq(UserCoupon::getUserId, userId)
                .eq(UserCoupon::getCouponId, couponId)
                .eq(UserCoupon::getStatus, 0)
        );
        
        if (userCoupon == null) {
            throw new BusinessException("优惠券不存在或已使用");
        }
        
        // 查询优惠券信息
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null || coupon.getStatus() != 1) {
            throw new BusinessException("优惠券不可用");
        }
        
        // 检查优惠券有效期
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getStartTime()) || now.isAfter(coupon.getEndTime())) {
            throw new BusinessException("优惠券已过期");
        }
        
        // 检查使用门槛
        if (orderAmount.compareTo(coupon.getThreshold()) < 0) {
            throw new BusinessException("订单金额未达到优惠券使用门槛");
        }
        
        return coupon;
    }
} 