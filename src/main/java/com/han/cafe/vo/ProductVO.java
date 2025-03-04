package com.han.cafe.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class ProductVO {
    private Integer productId;
    private Integer categoryId;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private String mainImage;
    private Object detailImages;
    private Integer monthSales;
    private Integer status;
    private Integer sortOrder;
    private Date createdAt;
    private List<ProductSpecVO> specs;
} 