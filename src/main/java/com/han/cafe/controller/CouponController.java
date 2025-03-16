package com.han.cafe.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.han.cafe.entity.User;
import com.han.cafe.exception.BusinessException;
import com.han.cafe.mapper.UserMapper;
import com.han.cafe.service.CouponService;
import com.han.cafe.utils.JwtTokenUtil;
import com.han.cafe.utils.Result;
import com.han.cafe.vo.UserCouponVO;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/coupons")
public class CouponController {
    
    @Resource
    private CouponService couponService;
    
    @Resource
    private JwtTokenUtil jwtTokenUtil;
    
    @Resource
    private UserMapper userMapper;
    
    @Resource
    private HttpServletRequest request;
    
    /**
     * 获取用户优惠券列表
     */
    @GetMapping("/user")
    public Result<List<UserCouponVO>> getUserCoupons() {
        String token = request.getHeader("Authorization");
        if (token == null) {
            throw new BusinessException("用户未登录");
        }
        
        try {
            // 从token中获取用户标识
            String userIdentifier = jwtTokenUtil.getUsernameFromToken(token);
            log.info("从token中解析出的用户标识: {}", userIdentifier);
            
            // 查找当前登录用户
            User currentUser = null;
            
            // 尝试通过手机号查找
            currentUser = userMapper.selectByMobile(userIdentifier);
            
            // 如果没找到，尝试通过openid查找
            if (currentUser == null) {
                currentUser = userMapper.selectByWechatOpenid(userIdentifier);
            }
            
            // 如果还没找到，尝试通过unionid查找
            if (currentUser == null) {
                currentUser = userMapper.selectByWechatUnionid(userIdentifier);
            }
            
            if (currentUser == null) {
                throw new BusinessException("用户未登录或登录已过期");
            }
            
            List<UserCouponVO> coupons = couponService.getUserCoupons(currentUser.getUserId());
            return Result.success(coupons);
            
        } catch (Exception e) {
            log.error("获取用户优惠券列表时发生错误: {}", e.getMessage(), e);
            throw new BusinessException("获取优惠券列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 领取优惠券
     */
    @PostMapping("/{couponId}/receive")
    public Result<Void> receiveCoupon(@PathVariable Integer couponId) {
        String token = request.getHeader("Authorization");
        if (token == null) {
            throw new BusinessException("用户未登录");
        }
        
        try {
            // 从token中获取用户标识
            String userIdentifier = jwtTokenUtil.getUsernameFromToken(token);
            log.info("从token中解析出的用户标识: {}", userIdentifier);
            
            // 查找当前登录用户
            User currentUser = null;
            
            // 尝试通过手机号查找
            currentUser = userMapper.selectByMobile(userIdentifier);
            
            // 如果没找到，尝试通过openid查找
            if (currentUser == null) {
                currentUser = userMapper.selectByWechatOpenid(userIdentifier);
            }
            
            // 如果还没找到，尝试通过unionid查找
            if (currentUser == null) {
                currentUser = userMapper.selectByWechatUnionid(userIdentifier);
            }
            
            if (currentUser == null) {
                throw new BusinessException("用户未登录或登录已过期");
            }
            
            couponService.receiveCoupon(currentUser.getUserId(), couponId);
            return Result.success();
            
        } catch (Exception e) {
            log.error("领取优惠券时发生错误: {}", e.getMessage(), e);
            throw new BusinessException("领取优惠券失败：" + e.getMessage());
        }
    }
} 