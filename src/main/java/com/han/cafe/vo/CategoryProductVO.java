package com.han.cafe.vo;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class CategoryProductVO {
    private Integer categoryId;
    private String name;
    private String icon;
    private Integer sortOrder;
    private Integer status;
    private Date createdAt;
    private List<ProductVO> products;
} 