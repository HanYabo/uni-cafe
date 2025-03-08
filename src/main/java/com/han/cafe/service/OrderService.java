package com.han.cafe.service;

import java.util.List;

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
} 