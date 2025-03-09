package com.han.cafe.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UserCouponVO {
    private Integer couponId;
    private String name;
    private Integer type;
    private BigDecimal threshold;
    private BigDecimal amount;
    private Integer discount;
    private BigDecimal maxDiscount;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status;  // 0-未使用 1-已使用 2-已过期
} 