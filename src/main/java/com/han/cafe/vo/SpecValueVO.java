package com.han.cafe.vo;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class SpecValueVO {
    private Integer specValueId;
    private String value;
    private BigDecimal extraPrice;
    private Integer sortOrder;
    private Integer isDefault;
} 