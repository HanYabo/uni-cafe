package com.han.cafe.vo;

import java.util.List;
import lombok.Data;

@Data
public class CreateOrderRequest {
    private List<OrderItemRequest> items;
    private String remark;
}