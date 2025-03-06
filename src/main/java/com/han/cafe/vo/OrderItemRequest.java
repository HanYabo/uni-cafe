package com.han.cafe.vo;

import lombok.Data;

import java.util.List;

@Data
public class OrderItemRequest {
    private Integer productId;
    private Integer quantity;
    private List<OrderItemSpecRequest> specs;
}