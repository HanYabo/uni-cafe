package com.han.cafe.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.*;

import lombok.Data;

@Data
@TableName("`orders`")
public class Order {
    /**
     * 订单ID
     */
    @TableId(type = IdType.INPUT)
    private String orderId;
    private Integer userId;
    private Integer couponId;  // 使用的优惠券ID
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;  // 优惠金额
    private BigDecimal payAmount;  // 实付金额
    private Integer status;  // 0-待支付 1-已支付 2-已完成 3-已取消
    private Integer payType;  // 1-微信支付 2-支付宝
    private LocalDateTime payTime;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * 是否删除：0-未删除 1-已删除
     */
    private Integer isDeleted;
} 