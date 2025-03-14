package com.han.cafe.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.han.cafe.common.ApiResponse;
import com.han.cafe.service.OrderService;
import com.han.cafe.vo.AdminOrderDetailVO;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    @Resource
    private OrderService orderService;

    /**
     * 分页查询订单列表
     */
    @GetMapping
    public ApiResponse<IPage<AdminOrderDetailVO>> getOrderPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        try {
            IPage<AdminOrderDetailVO> orderPage = orderService.getAdminOrderPage(page, size, status);
            return ApiResponse.success(orderPage);
        } catch (Exception e) {
            log.error("查询订单列表失败: {}", e.getMessage(), e);
            return ApiResponse.error("查询订单列表失败: " + e.getMessage());
        }
    }

    /**
     * 查询订单详情
     */
    @GetMapping("/{orderId}")
    public ApiResponse<AdminOrderDetailVO> getOrderDetail(@PathVariable String orderId) {
        try {
            AdminOrderDetailVO orderDetail = orderService.getAdminOrderDetail(orderId);
            return ApiResponse.success(orderDetail);
        } catch (Exception e) {
            log.error("查询订单详情失败: {}", e.getMessage(), e);
            return ApiResponse.error("查询订单详情失败: " + e.getMessage());
        }
    }

    /**
     * 更新订单状态
     */
    @PostMapping("/{orderId}/status")
    public ApiResponse<Boolean> updateOrderStatus(
            @PathVariable String orderId,
            @RequestParam(defaultValue = "2") Integer status) {
        try {
            // 如果订单处于已支付状态，则进行出餐处理
            if(orderService.getAdminOrderDetail(orderId).getStatus() == 1){
                boolean result = orderService.updateOrderStatus(orderId, status);
                return ApiResponse.success(result);
            }
            return ApiResponse.error("更新订单状态失败");
        } catch (Exception e) {
            log.error("更新订单状态失败: {}", e.getMessage(), e);
            return ApiResponse.error("更新订单状态失败: " + e.getMessage());
        }
    }

} 