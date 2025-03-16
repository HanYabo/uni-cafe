package com.han.cafe.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.han.cafe.service.ProductService;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * 商品销量定时任务
 * 定期同步数据库商品销量数据到Redis
 */
@Slf4j
@Component
public class ProductSalesScheduledTask {

    @Resource
    private ProductService productService;
    
    /**
     * 每天凌晨2点执行同步任务
     * 选择凌晨时段是因为系统负载较低
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void syncProductSalesToRedis() {
        log.info("开始执行商品销量同步定时任务");
        try {
            productService.syncProductSalesToRedis();
            log.info("商品销量同步定时任务执行完成");
        } catch (Exception e) {
            log.error("商品销量同步定时任务执行失败", e);
        }
    }
    
    /**
     * 系统启动后延迟5分钟执行一次同步
     * 确保系统启动后Redis中有最新数据
     */
    @Scheduled(initialDelay = 300000, fixedRate = Long.MAX_VALUE)
    public void syncProductSalesAfterStartup() {
        log.info("系统启动后初始化商品销量排行榜");
        try {
            productService.syncProductSalesToRedis();
            log.info("商品销量排行榜初始化完成");
        } catch (Exception e) {
            log.error("商品销量排行榜初始化失败", e);
        }
    }
} 