package com.han.cafe.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("order_item")
public class OrderItem {
    @TableId(type = IdType.AUTO)
    private Integer itemId;
    private String orderId;
    private Integer productId;
    private String productName;
    private BigDecimal basePrice;
    private Integer quantity;
    private BigDecimal actualPrice;
    private BigDecimal subtotal;
    private LocalDateTime createdAt;
    
    @TableField(exist = false)
    private List<OrderItemSpec> specs;
} 