package com.han.cafe.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class OrderResponse {
    private String orderId;
    private BigDecimal totalAmount;
    private BigDecimal actualAmount;
    private Integer status;
    private String statusText;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;
}
