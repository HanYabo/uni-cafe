package com.han.cafe.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.han.cafe.entity.Order;
import com.han.cafe.vo.AdminOrderDetailVO;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    /**
     * 管理员分页查询订单列表
     */
    IPage<AdminOrderDetailVO> selectAdminOrderPage(IPage<AdminOrderDetailVO> page, @Param("status") Integer status);

    /**
     * 管理员查询订单详情
     */
    AdminOrderDetailVO selectAdminOrderDetail(@Param("orderId") String orderId);
} 