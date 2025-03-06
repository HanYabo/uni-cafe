package com.han.cafe.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.han.cafe.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.han.cafe.entity.Order;
import com.han.cafe.entity.OrderItem;
import com.han.cafe.entity.OrderItemSpec;
import com.han.cafe.entity.Product;
import com.han.cafe.entity.Spec;
import com.han.cafe.entity.SpecValue;
import com.han.cafe.exception.BusinessException;
import com.han.cafe.mapper.OrderItemMapper;
import com.han.cafe.mapper.OrderItemSpecMapper;
import com.han.cafe.mapper.OrderMapper;
import com.han.cafe.mapper.ProductMapper;
import com.han.cafe.mapper.SpecMapper;
import com.han.cafe.mapper.SpecValueMapper;
import com.han.cafe.service.OrderService;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
    @Resource
    private ProductMapper productMapper;
    @Resource
    private SpecMapper specMapper;
    @Resource
    private SpecValueMapper specValueMapper;
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private OrderItemMapper orderItemMapper;
    @Resource
    private OrderItemSpecMapper orderItemSpecMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse createOrder(Integer userId, CreateOrderRequest request) {
        String orderId = generateOrderId(userId);
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        
        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productMapper.selectById(itemRequest.getProductId());
            if (product == null || product.getStatus() != 1) {
                throw new BusinessException("商品不存在或已下架");
            }
            
            BigDecimal actualPrice = product.getBasePrice();
            List<OrderItemSpec> specs = new ArrayList<>();
            
            for (OrderItemSpecRequest specRequest : itemRequest.getSpecs()) {
                Spec spec = specMapper.selectById(specRequest.getSpecId());
                SpecValue specValue = specValueMapper.selectById(specRequest.getSpecValueId());
                
                if (spec == null || specValue == null || !specValue.getSpecId().equals(spec.getSpecId())) {
                    throw new BusinessException("商品规格不正确");
                }
                
                actualPrice = actualPrice.add(specValue.getExtraPrice());
                
                OrderItemSpec itemSpec = new OrderItemSpec();
                itemSpec.setSpecId(spec.getSpecId());
                itemSpec.setSpecName(spec.getName());
                itemSpec.setSpecValueId(specValue.getSpecValueId());
                itemSpec.setSpecValue(specValue.getValue());
                itemSpec.setExtraPrice(specValue.getExtraPrice());
                specs.add(itemSpec);
            }
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(orderId);
            orderItem.setProductId(product.getProductId());
            orderItem.setProductName(product.getName());
            orderItem.setBasePrice(product.getBasePrice());
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setActualPrice(actualPrice);
            orderItem.setSubtotal(actualPrice.multiply(new BigDecimal(itemRequest.getQuantity())));
            orderItem.setSpecs(specs);
            
            orderItems.add(orderItem);
            totalAmount = totalAmount.add(orderItem.getSubtotal());
        }
        
        Order order = new Order();
        order.setOrderId(orderId);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setActualAmount(totalAmount);
        order.setStatus(0);
        order.setRemark(request.getRemark());
        
        orderMapper.insert(order);
        
        for (OrderItem orderItem : orderItems) {
            orderItemMapper.insert(orderItem);
            for (OrderItemSpec spec : orderItem.getSpecs()) {
                spec.setItemId(orderItem.getItemId());
                orderItemSpecMapper.insert(spec);
            }
        }
        
        return getOrder(orderId);
    }

    @Override
    public OrderResponse getOrder(String orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        List<OrderItem> items = orderItemMapper.selectByOrderId(orderId);
        for (OrderItem item : items) {
            item.setSpecs(orderItemSpecMapper.selectByItemId(item.getItemId()));
        }
        
        return buildOrderResponse(order, items);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse payOrder(String orderId, Integer payType) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException("订单状态不正确");
        }
        
        order.setStatus(1);
        order.setPayType(payType);
        order.setPayTime(LocalDateTime.now());
        orderMapper.updateById(order);
        
        return getOrder(orderId);
    }
    
    private String generateOrderId(Integer userId) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String userIdSuffix = String.format("%04d", userId % 10000);
        String random = String.format("%04d", new Random().nextInt(10000));
        return timestamp + userIdSuffix + random;
    }
    
    private OrderResponse buildOrderResponse(Order order, List<OrderItem> items) {
        OrderResponse response = new OrderResponse();
        BeanUtils.copyProperties(order, response);
        response.setStatusText(getStatusText(order.getStatus()));
        
        List<OrderItemResponse> itemResponses = items.stream().map(item -> {
            OrderItemResponse itemResponse = new OrderItemResponse();
            BeanUtils.copyProperties(item, itemResponse);
            
            List<OrderItemSpecResponse> specResponses = item.getSpecs().stream().map(spec -> {
                OrderItemSpecResponse specResponse = new OrderItemSpecResponse();
                BeanUtils.copyProperties(spec, specResponse);
                return specResponse;
            }).collect(Collectors.toList());
            
            itemResponse.setSpecs(specResponses);
            return itemResponse;
        }).collect(Collectors.toList());
        
        response.setItems(itemResponses);
        return response;
    }
    
    private String getStatusText(Integer status) {
        return switch (status) {
            case 0 -> "待支付";
            case 1 -> "已支付";
            case 2 -> "已完成";
            case 3 -> "已取消";
            default -> "未知状态";
        };
    }
} 