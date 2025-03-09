package com.han.cafe.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.han.cafe.entity.Coupon;
import com.han.cafe.entity.Order;
import com.han.cafe.entity.OrderItem;
import com.han.cafe.entity.OrderItemSpec;
import com.han.cafe.entity.Product;
import com.han.cafe.entity.Spec;
import com.han.cafe.entity.SpecValue;
import com.han.cafe.entity.UserCoupon;
import com.han.cafe.exception.BusinessException;
import com.han.cafe.mapper.OrderItemMapper;
import com.han.cafe.mapper.OrderItemSpecMapper;
import com.han.cafe.mapper.OrderMapper;
import com.han.cafe.mapper.ProductMapper;
import com.han.cafe.mapper.SpecMapper;
import com.han.cafe.mapper.SpecValueMapper;
import com.han.cafe.mapper.UserCouponMapper;
import com.han.cafe.service.CouponService;
import com.han.cafe.service.OrderService;
import com.han.cafe.vo.CreateOrderRequest;
import com.han.cafe.vo.OrderItemRequest;
import com.han.cafe.vo.OrderItemResponse;
import com.han.cafe.vo.OrderItemSpecRequest;
import com.han.cafe.vo.OrderItemSpecResponse;
import com.han.cafe.vo.OrderResponse;

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
    @Resource
    private CouponService couponService;
    @Resource
    private UserCouponMapper userCouponMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse createOrder(Integer userId, CreateOrderRequest request) {
        String orderId = generateOrderId(userId);
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        
        // 计算订单总金额
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
        
        // 处理优惠券
        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal payAmount = totalAmount;
        
        if (request.getCouponId() != null) {
            // 检查优惠券是否可用
            Coupon coupon = couponService.checkCoupon(userId, request.getCouponId(), totalAmount);
            if (coupon != null) {
                // 计算优惠金额
                if (coupon.getType() == 1) {  // 满减券
                    discountAmount = coupon.getAmount();
                } else if (coupon.getType() == 2) {  // 折扣券
                    BigDecimal discount = new BigDecimal(coupon.getDiscount()).divide(new BigDecimal(100));
                    discountAmount = totalAmount.multiply(BigDecimal.ONE.subtract(discount));
                    // 检查是否超过最大优惠金额
                    if (coupon.getMaxDiscount() != null && discountAmount.compareTo(coupon.getMaxDiscount()) > 0) {
                        discountAmount = coupon.getMaxDiscount();
                    }
                }
                payAmount = totalAmount.subtract(discountAmount);
            }
        }
        
        // 创建订单
        Order order = new Order();
        order.setOrderId(orderId);
        order.setUserId(userId);
        order.setCouponId(request.getCouponId());
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(discountAmount);
        order.setPayAmount(payAmount);
        order.setStatus(0);
        order.setRemark(request.getRemark());
        
        orderMapper.insert(order);
        
        // 保存订单项
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
    
    @Override
    public List<OrderResponse> getOrderHistory(Integer userId) {
        log.info("查询用户历史订单列表，userId: {}", userId);
        
        // 构建查询条件
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getUserId, userId)
                   .orderByDesc(Order::getCreatedAt);  // 按创建时间倒序排列
        
        // 查询订单列表
        List<Order> orders = orderMapper.selectList(queryWrapper);
        log.info("查询到{}条订单记录", orders.size());
        
        // 转换为响应对象
        return orders.stream().map(order -> {
            // 查询订单项
            List<OrderItem> items = orderItemMapper.selectByOrderId(order.getOrderId());
            
            // 查询每个订单项的规格
            items.forEach(item -> {
                List<OrderItemSpec> specs = orderItemSpecMapper.selectByItemId(item.getItemId());
                item.setSpecs(specs);
            });
            
            // 构建响应对象
            return buildOrderResponse(order, items);
        }).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse cancelOrder(String orderId, Integer userId) {
        log.info("开始取消订单，orderId: {}, userId: {}", orderId, userId);
        
        // 查询订单
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        // 验证订单所属权
        if (!order.getUserId().equals(userId)) {
            log.error("用户 {} 尝试取消不属于他的订单 {}", userId, orderId);
            throw new BusinessException("无权操作此订单");
        }
        
        // 验证订单状态
        if (order.getStatus() != 0) {
            String statusText = getStatusText(order.getStatus());
            log.error("订单 {} 状态为 {}，无法取消", orderId, statusText);
            throw new BusinessException("当前订单状态为" + statusText + "，无法取消");
        }
        
        // 更新订单状态为已取消
        order.setStatus(3);  // 3-已取消
        order.setUpdatedAt(LocalDateTime.now());
        orderMapper.updateById(order);
        
        // 如果订单使用了优惠券，退回优惠券
        if (order.getCouponId() != null) {
            try {
                // 查找用户优惠券记录
                LambdaQueryWrapper<UserCoupon> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(UserCoupon::getUserId, userId)
                          .eq(UserCoupon::getCouponId, order.getCouponId())
                          .eq(UserCoupon::getOrderId, orderId);
                
                UserCoupon userCoupon = userCouponMapper.selectOne(queryWrapper);
                if (userCoupon != null) {
                    // 重置优惠券状态为未使用
                    userCoupon.setStatus(0);  // 0-未使用
                    userCoupon.setOrderId(null);
                    userCoupon.setUsedTime(null);
                    userCoupon.setUpdatedAt(LocalDateTime.now());
                    userCouponMapper.updateById(userCoupon);
                    
                    log.info("订单 {} 使用的优惠券 {} 已退回", orderId, order.getCouponId());
                }
            } catch (Exception e) {
                log.error("退回优惠券时发生错误", e);
                throw new BusinessException("退回优惠券失败：" + e.getMessage());
            }
        }
        
        log.info("订单 {} 已成功取消", orderId);
        
        return getOrder(orderId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOrder(String orderId, Integer userId) {
        log.info("开始删除订单，orderId: {}, userId: {}", orderId, userId);
        
        // 查询订单
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        // 验证订单所属权
        if (!order.getUserId().equals(userId)) {
            log.error("用户 {} 尝试删除不属于他的订单 {}", userId, orderId);
            throw new BusinessException("无权操作此订单");
        }
        
        // 只允许删除已完成或已取消的订单
        if (order.getStatus() != 2 && order.getStatus() != 3) {
            String statusText = getStatusText(order.getStatus());
            log.error("订单 {} 状态为 {}，无法删除", orderId, statusText);
            throw new BusinessException("只能删除已完成或已取消的订单");
        }
        
        try {
            // 1. 查询并删除订单项规格
            List<OrderItem> orderItems = orderItemMapper.selectByOrderId(orderId);
            for (OrderItem item : orderItems) {
                // 删除订单项规格
                LambdaQueryWrapper<OrderItemSpec> specWrapper = new LambdaQueryWrapper<>();
                specWrapper.eq(OrderItemSpec::getItemId, item.getItemId());
                orderItemSpecMapper.delete(specWrapper);
            }
            
            // 2. 删除订单项
            LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
            itemWrapper.eq(OrderItem::getOrderId, orderId);
            orderItemMapper.delete(itemWrapper);
            
            // 3. 删除订单主表
            orderMapper.deleteById(orderId);
            
            log.info("订单 {} 及其关联数据已成功删除", orderId);
            
        } catch (Exception e) {
            log.error("删除订单时发生错误", e);
            throw new BusinessException("删除订单失败：" + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelTimeoutOrders() {
        // 设置超时时间为10分钟
        LocalDateTime timeoutTime = LocalDateTime.now().minusMinutes(10);
        
        // 查询超时的待支付订单
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getStatus, 0)  // 待支付状态
                   .le(Order::getCreatedAt, timeoutTime);  // 创建时间小于等于10分钟前
        
        List<Order> timeoutOrders = orderMapper.selectList(queryWrapper);
        log.info("找到{}个超时待支付订单", timeoutOrders.size());
        
        // 批量更新订单状态
        for (Order order : timeoutOrders) {
            try {
                log.info("开始处理超时订单: {}", order.getOrderId());
                
                // 更新订单状态为已取消
                order.setStatus(3);  // 3-已取消
                order.setUpdatedAt(LocalDateTime.now());
                orderMapper.updateById(order);
                
                // 如果订单使用了优惠券，退回优惠券
                if (order.getCouponId() != null) {
                    // 查找用户优惠券记录
                    LambdaQueryWrapper<UserCoupon> couponQueryWrapper = new LambdaQueryWrapper<>();
                    couponQueryWrapper.eq(UserCoupon::getUserId, order.getUserId())
                                    .eq(UserCoupon::getCouponId, order.getCouponId())
                                    .eq(UserCoupon::getOrderId, order.getOrderId());
                    
                    UserCoupon userCoupon = userCouponMapper.selectOne(couponQueryWrapper);
                    if (userCoupon != null) {
                        // 重置优惠券状态为未使用
                        userCoupon.setStatus(0);  // 0-未使用
                        userCoupon.setOrderId(null);
                        userCoupon.setUsedTime(null);
                        userCoupon.setUpdatedAt(LocalDateTime.now());
                        userCouponMapper.updateById(userCoupon);
                        
                        log.info("超时订单 {} 使用的优惠券 {} 已退回", order.getOrderId(), order.getCouponId());
                    }
                }
                
                log.info("订单 {} 已自动取消", order.getOrderId());
            } catch (Exception e) {
                log.error("处理超时订单 {} 时发生错误: {}", order.getOrderId(), e.getMessage(), e);
                // 继续处理下一个订单
            }
        }
    }
    
    private String generateOrderId(Integer userId) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String userIdSuffix = String.format("%04d", userId % 10000);
        String random = String.format("%04d", new Random().nextInt(10000));
        return timestamp + userIdSuffix + random;
    }
    
    private OrderResponse buildOrderResponse(Order order, List<OrderItem> items) {
        OrderResponse response = new OrderResponse();
        // 复制基本字段
        response.setOrderId(order.getOrderId());
        response.setUserId(order.getUserId());
        response.setCouponId(order.getCouponId());
        response.setTotalAmount(order.getTotalAmount());
        response.setDiscountAmount(order.getDiscountAmount());
        response.setPayAmount(order.getPayAmount());
        response.setStatus(order.getStatus());
        response.setStatusText(getStatusText(order.getStatus()));
        response.setPayType(order.getPayType());
        response.setPayTime(order.getPayTime());
        response.setRemark(order.getRemark());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        
        // 处理订单项
        List<OrderItemResponse> itemResponses = items.stream().map(item -> {
            OrderItemResponse itemResponse = new OrderItemResponse();
            BeanUtils.copyProperties(item, itemResponse);
            
            // 查询商品信息获取主图
            Product product = productMapper.selectById(item.getProductId());
            if (product != null) {
                // 确保图片路径以/images/开头
                String mainImage = product.getMainImage();
                if (mainImage != null && !mainImage.startsWith("/images/")) {
                    mainImage = "/images/" + mainImage;
                }
                itemResponse.setMainImage(mainImage);
            }
            
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