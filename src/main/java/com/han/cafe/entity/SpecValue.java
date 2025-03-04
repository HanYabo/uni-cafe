package com.han.cafe.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 规格值表（如：中杯、大杯、热、冰、标准糖等）
 */
@Data
@TableName("spec_value")
public class SpecValue {
    @TableId(type = IdType.AUTO)
    private Integer specValueId;
    
    /**
     * 关联的规格ID
     */
    private Integer specId;
    
    /**
     * 规格值名称
     */
    private String value;
    
    /**
     * 额外价格（正数表示加价，负数表示减价，0表示不调整价格）
     */
    private BigDecimal extraPrice;
    
    /**
     * 排序
     */
    private Integer sortOrder;
    
    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;
    
    /**
     * 是否默认：0-否 1-是
     */
    private Integer isDefault;
    
    private Date createdAt;
} 