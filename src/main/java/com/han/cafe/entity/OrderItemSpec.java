package com.han.cafe.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("order_item_spec")
public class OrderItemSpec {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer itemId;
    private Integer specId;
    private String specName;
    private Integer specValueId;
    private String specValue;
    private BigDecimal extraPrice;
    private LocalDateTime createdAt;
} 