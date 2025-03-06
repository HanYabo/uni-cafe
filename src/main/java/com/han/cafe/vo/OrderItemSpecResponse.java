package com.han.cafe.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemSpecResponse {
    private String specName;
    private String specValue;
    private BigDecimal extraPrice;
}