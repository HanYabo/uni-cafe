package com.han.cafe.service;

import com.han.cafe.vo.CreateOrderRequest;
import com.han.cafe.vo.OrderResponse;

public interface OrderService {
    OrderResponse createOrder(Integer userId, CreateOrderRequest request);
    OrderResponse getOrder(String orderId);
    OrderResponse payOrder(String orderId, Integer payType);
} 