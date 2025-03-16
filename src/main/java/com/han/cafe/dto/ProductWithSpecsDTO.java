package com.han.cafe.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.han.cafe.vo.ProductSpecVO;

import lombok.Data;

/**
 * 带规格信息的商品DTO，用于热销商品排行榜接口返回
 */
@Data
public class ProductWithSpecsDTO {
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
    // 商品规格信息
    private List<ProductSpecVO> specs;
} 