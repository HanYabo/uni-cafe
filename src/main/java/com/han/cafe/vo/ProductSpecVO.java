package com.han.cafe.vo;

import java.util.List;

import lombok.Data;

@Data
public class ProductSpecVO {
    private Integer specId;
    private String name;
    private Integer sortOrder;
    private Integer required;
    private List<SpecValueVO> values;
} 