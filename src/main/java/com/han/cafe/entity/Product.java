package com.han.cafe.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 商品主表
 * @TableName product
 */
@TableName(value ="product")
@Data
public class Product {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer productId;

    /**
     * 
     */
    private Integer categoryId;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 基础价格（小杯价格）
     */
    private BigDecimal basePrice;

    /**
     * 主图URL
     */
    private String mainImage;

    /**
     * 详情图数组
     */
    private Object detailImages;

    /**
     * 月销量
     */
    private Integer monthSales;

    /**
     * 0-下架 1-上架
     */
    private Integer status;

    /**
     * 分类内排序
     */
    private Integer sortOrder;

    /**
     * 规格配置（前端展示逻辑）
     */
    private Object specConfig;

    /**
     * 
     */
    private Date createdAt;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Product other = (Product) that;
        return (this.getProductId() == null ? other.getProductId() == null : this.getProductId().equals(other.getProductId()))
            && (this.getCategoryId() == null ? other.getCategoryId() == null : this.getCategoryId().equals(other.getCategoryId()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getBasePrice() == null ? other.getBasePrice() == null : this.getBasePrice().equals(other.getBasePrice()))
            && (this.getMainImage() == null ? other.getMainImage() == null : this.getMainImage().equals(other.getMainImage()))
            && (this.getDetailImages() == null ? other.getDetailImages() == null : this.getDetailImages().equals(other.getDetailImages()))
            && (this.getMonthSales() == null ? other.getMonthSales() == null : this.getMonthSales().equals(other.getMonthSales()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getSortOrder() == null ? other.getSortOrder() == null : this.getSortOrder().equals(other.getSortOrder()))
            && (this.getSpecConfig() == null ? other.getSpecConfig() == null : this.getSpecConfig().equals(other.getSpecConfig()))
            && (this.getCreatedAt() == null ? other.getCreatedAt() == null : this.getCreatedAt().equals(other.getCreatedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getProductId() == null) ? 0 : getProductId().hashCode());
        result = prime * result + ((getCategoryId() == null) ? 0 : getCategoryId().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getBasePrice() == null) ? 0 : getBasePrice().hashCode());
        result = prime * result + ((getMainImage() == null) ? 0 : getMainImage().hashCode());
        result = prime * result + ((getDetailImages() == null) ? 0 : getDetailImages().hashCode());
        result = prime * result + ((getMonthSales() == null) ? 0 : getMonthSales().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getSortOrder() == null) ? 0 : getSortOrder().hashCode());
        result = prime * result + ((getSpecConfig() == null) ? 0 : getSpecConfig().hashCode());
        result = prime * result + ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", productId=").append(productId);
        sb.append(", categoryId=").append(categoryId);
        sb.append(", name=").append(name);
        sb.append(", description=").append(description);
        sb.append(", basePrice=").append(basePrice);
        sb.append(", mainImage=").append(mainImage);
        sb.append(", detailImages=").append(detailImages);
        sb.append(", monthSales=").append(monthSales);
        sb.append(", status=").append(status);
        sb.append(", sortOrder=").append(sortOrder);
        sb.append(", specConfig=").append(specConfig);
        sb.append(", createdAt=").append(createdAt);
        sb.append("]");
        return sb.toString();
    }
}