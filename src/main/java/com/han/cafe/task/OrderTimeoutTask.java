package com.han.cafe.task;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.han.cafe.entity.Order;
import com.han.cafe.service.OrderService;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OrderTimeoutTask {
    
    @Resource
    private OrderService orderService;
    
    /**
     * 每分钟检查一次是否有超时订单
     * cron表达式：秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 */1 * * * *")
    public void cancelTimeoutOrders() {
        log.info("开始检查超时订单...");
        try {
            // 获取所有超时的待支付订单
            orderService.cancelTimeoutOrders();
            log.info("超时订单处理完成");
        } catch (Exception e) {
            log.error("处理超时订单时发生错误: {}", e.getMessage(), e);
        }
    }
} 