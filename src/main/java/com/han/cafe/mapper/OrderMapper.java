package com.han.cafe.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.han.cafe.entity.Order;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
} 