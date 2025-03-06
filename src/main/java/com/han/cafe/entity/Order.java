package com.han.cafe.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("orders")
public class Order {
    @TableId(type = IdType.INPUT)
    private String orderId;
    private Integer userId;
    private BigDecimal totalAmount;
    private BigDecimal actualAmount;
    private Integer status;
    private Integer payType;
    private LocalDateTime payTime;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 