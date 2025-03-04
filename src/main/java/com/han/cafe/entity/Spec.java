package com.han.cafe.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 商品规格表（如：杯型、温度、糖度）
 */
@Data
@TableName("spec")
public class Spec {
    @TableId(type = IdType.AUTO)
    private Integer specId;
    
    /**
     * 规格名称
     */
    private String name;
    
    /**
     * 排序
     */
    private Integer sortOrder;
    
    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;
    
    private Date createdAt;
}