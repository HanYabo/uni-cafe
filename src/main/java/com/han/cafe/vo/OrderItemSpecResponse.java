package com.han.cafe.vo;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderItemSpecResponse {
    private Integer specId;
    private String specName;
    private String specValue;
    private Integer specValueId;
    private BigDecimal extraPrice;
}