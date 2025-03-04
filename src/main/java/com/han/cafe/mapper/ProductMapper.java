package com.han.cafe.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.han.cafe.entity.Product;

/**
 * 商品表 Mapper 接口
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}




