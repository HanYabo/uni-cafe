package com.han.cafe.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderItemResponse {
    private String productName;
    private BigDecimal basePrice;
    private Integer quantity;
    private BigDecimal actualPrice;
    private BigDecimal subtotal;
    private List<OrderItemSpecResponse> specs;
}