package com.han.cafe.vo;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrderRequest {
    @NotNull(message = "订单项不能为空")
    private List<OrderItemRequest> items;
    
    private Integer couponId;  // 优惠券ID，可选
    
    private String remark;  // 订单备注，可选
}