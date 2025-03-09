package com.han.cafe.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("user_coupon")
public class UserCoupon {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer userId;
    
    private Integer couponId;
    
    private Integer status;
    
    private String orderId;
    
    private LocalDateTime usedTime;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
} 