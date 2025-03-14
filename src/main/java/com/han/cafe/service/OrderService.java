package com.han.cafe.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.han.cafe.vo.AdminOrderDetailVO;
import com.han.cafe.vo.CreateOrderRequest;
import com.han.cafe.vo.OrderResponse;

public interface OrderService {
    OrderResponse createOrder(Integer userId, CreateOrderRequest request);
    OrderResponse getOrder(String orderId);
    OrderResponse payOrder(String orderId, Integer payType);
    
    /**
     * 获取用户历史订单列表
     * @param userId 用户ID
     * @return 订单列表
     */
    List<OrderResponse> getOrderHistory(Integer userId);
    
    /**
     * 取消订单
     * @param orderId 订单ID
     * @param userId 用户ID（用于验证权限）
     * @return 更新后的订单信息
     */
    OrderResponse cancelOrder(String orderId, Integer userId);
    
    /**
     * 删除订单
     * @param orderId 订单ID
     * @param userId 用户ID（用于验证权限）
     */
    void deleteOrder(String orderId, Integer userId);
    
    /**
     * 处理超时未支付的订单
     */
    void cancelTimeoutOrders();

    /**
     * 管理员分页查询订单列表
     */
    IPage<AdminOrderDetailVO> getAdminOrderPage(Integer page, Integer size, Integer status);

    /**
     * 管理员查询订单详情
     */
    AdminOrderDetailVO getAdminOrderDetail(String orderId);

    /**
     * 管理员更新订单状态
     */
    boolean updateOrderStatus(String orderId, Integer status);
} 