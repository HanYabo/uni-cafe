package com.han.cafe.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.han.cafe.common.ApiResponse;
import com.han.cafe.entity.User;
import com.han.cafe.exception.BusinessException;
import com.han.cafe.mapper.UserMapper;
import com.han.cafe.service.OrderService;
import com.han.cafe.utils.JwtTokenUtil;
import com.han.cafe.vo.CreateOrderRequest;
import com.han.cafe.vo.OrderResponse;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Resource
    private OrderService orderService;
    
    @Resource
    private JwtTokenUtil jwtTokenUtil;
    
    @Resource
    private UserMapper userMapper;
    
    @Resource
    private HttpServletRequest request;
    
    @PostMapping
    public ApiResponse<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        String token = this.request.getHeader("Authorization");
        log.info("收到的Authorization header: {}", token);
        
        if (token == null) {
            log.error("未提供token");
            throw new BusinessException("用户未登录");
        }
        
        try {
            log.info("开始解析token...");
            // 从token中获取用户标识（可能是手机号或微信openid）
            String userIdentifier = jwtTokenUtil.getUsernameFromToken(token);
            log.info("从token中解析出的用户标识: {}", userIdentifier);
            
            if (userIdentifier == null || userIdentifier.trim().isEmpty()) {
                log.error("从token中解析出的用户标识为空");
                throw new BusinessException("无效的token：未包含用户信息");
            }
            
            // 先尝试通过手机号查找用户
            log.info("尝试通过手机号查找用户: {}", userIdentifier);
            User user = userMapper.selectByMobile(userIdentifier);
            log.info("通过手机号查找结果: {}", user != null ? "找到用户" : "未找到用户");
            
            // 如果通过手机号没找到，尝试通过微信openid查找
            if (user == null) {
                log.info("尝试通过微信openid查找用户: {}", userIdentifier);
                user = userMapper.selectByWechatOpenid(userIdentifier);
                log.info("通过openid查找结果: {}", user != null ? "找到用户" : "未找到用户");
            }
            
            // 如果还是没找到，可能是使用unionid存储的
            if (user == null) {
                log.info("尝试通过微信unionid查找用户: {}", userIdentifier);
                user = userMapper.selectByWechatUnionid(userIdentifier);
                log.info("通过unionid查找结果: {}", user != null ? "找到用户" : "未找到用户");
                
                // 如果所有方法都找不到用户，输出数据库中的用户记录进行调试
                if (user == null) {
                    log.error("所有查找方法都失败，检查数据库中的用户记录");
                    try {
                        User testUser = userMapper.selectById(1);
                        if (testUser != null) {
                            log.info("数据库中的示例用户记录: userId={}, mobile={}, openid={}, unionid={}", 
                                testUser.getUserId(), 
                                testUser.getMobile(), 
                                testUser.getWechatOpenid(),
                                testUser.getWechatUnionid());
                        } else {
                            log.error("数据库中没有任何用户记录");
                        }
                    } catch (Exception e) {
                        log.error("查询示例用户记录时发生错误: {}", e.getMessage());
                    }
                }
            }
            
            if (user == null) {
                log.error("未找到用户，用户标识: {}", userIdentifier);
                throw new BusinessException("用户不存在，请确认是否已登录");
            }
            
            log.info("找到用户信息: userId={}, mobile={}, openid={}, unionid={}, status={}", 
                    user.getUserId(), 
                    user.getMobile(), 
                    user.getWechatOpenid(),
                    user.getWechatUnionid(),
                    user.getStatus());
            
            // 检查用户状态
            if (user.getStatus() != null && user.getStatus() != 1) {
                log.error("用户状态异常: {}", user.getStatus());
                throw new BusinessException("用户状态异常，请联系客服");
            }
            
            OrderResponse order = orderService.createOrder(user.getUserId(), request);
            return ApiResponse.success(order);
        } catch (Exception e) {
            log.error("处理token时发生错误: {}", e.getMessage(), e);
            throw new BusinessException("Token验证失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponse> getOrder(@PathVariable String orderId) {
        OrderResponse order = orderService.getOrder(orderId);
        return ApiResponse.success(order);
    }
    
    @PostMapping("/{orderId}/pay")
    public ApiResponse<OrderResponse> payOrder(
            @PathVariable String orderId,
            @RequestParam Integer payType) {
        OrderResponse order = orderService.payOrder(orderId, payType);
        return ApiResponse.success(order);
    }
} 