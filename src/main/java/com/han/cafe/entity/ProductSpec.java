package com.han.cafe.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 商品规格关联表
 */
@Data
@TableName("product_spec")
public class ProductSpec {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 商品ID
     */
    private Integer productId;
    
    /**
     * 规格ID
     */
    private Integer specId;
    
    /**
     * 是否必选：0-否 1-是
     */
    private Integer required;
    
    /**
     * 排序
     */
    private Integer sortOrder;
    
    private Date createdAt;
} 