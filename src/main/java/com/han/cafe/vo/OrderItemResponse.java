package com.han.cafe.vo;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class OrderItemResponse {
    private Integer itemId;
    private Integer productId;
    private String productName;
    private BigDecimal basePrice;
    private Integer quantity;
    private BigDecimal actualPrice;
    private BigDecimal subtotal;
    private String mainImage;
    private List<OrderItemSpecResponse> specs;
}