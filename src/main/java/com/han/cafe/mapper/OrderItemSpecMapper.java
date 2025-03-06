package com.han.cafe.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.han.cafe.entity.OrderItemSpec;

@Mapper
public interface OrderItemSpecMapper extends BaseMapper<OrderItemSpec> {
    @Select("SELECT * FROM order_item_spec WHERE item_id = #{itemId}")
    List<OrderItemSpec> selectByItemId(Integer itemId);
} 