package com.han.cafe.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.han.cafe.entity.User;
import com.han.cafe.exception.BusinessException;
import com.han.cafe.mapper.UserMapper;
import com.han.cafe.service.OrderService;
import com.han.cafe.utils.JwtTokenUtil;
import com.han.cafe.utils.Result;
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

    // 创建订单
    @PostMapping
    public Result<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
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
            return Result.success(order);
        } catch (Exception e) {
            log.error("处理token时发生错误: {}", e.getMessage(), e);
            throw new BusinessException("Token验证失败：" + e.getMessage());
        }
    }

    // 根据OrderId获取单个订单详情
    @GetMapping("/{orderId}")
    public Result<OrderResponse> getOrder(@PathVariable String orderId) {
        OrderResponse order = orderService.getOrder(orderId);
        return Result.success(order);
    }

    // 实现模拟支付功能
    @PostMapping("/{orderId}/pay")
    public Result<OrderResponse> payOrder(
            @PathVariable String orderId,
            @RequestParam(defaultValue = "1") Integer payType) {
        OrderResponse order = orderService.payOrder(orderId, payType);
        return Result.success(order);
    }
    
    /**
     * 取消订单
     */
    @PostMapping("/{orderId}/cancel")
    public Result<OrderResponse> cancelOrder(@PathVariable String orderId) {
        String token = this.request.getHeader("Authorization");
        if (token == null) {
            throw new BusinessException("用户未登录");
        }
        
        try {
            // 从token中获取用户标识
            String userIdentifier = jwtTokenUtil.getUsernameFromToken(token);
            log.info("从token中解析出的用户标识: {}", userIdentifier);
            
            // 查找当前登录用户
            User currentUser = null;
            
            // 尝试通过手机号查找
            currentUser = userMapper.selectByMobile(userIdentifier);
            
            // 如果没找到，尝试通过openid查找
            if (currentUser == null) {
                currentUser = userMapper.selectByWechatOpenid(userIdentifier);
            }
            
            // 如果还没找到，尝试通过unionid查找
            if (currentUser == null) {
                currentUser = userMapper.selectByWechatUnionid(userIdentifier);
            }
            
            if (currentUser == null) {
                throw new BusinessException("用户未登录或登录已过期");
            }
            
            // 取消订单
            OrderResponse order = orderService.cancelOrder(orderId, currentUser.getUserId());
            return Result.success(order);
            
        } catch (Exception e) {
            log.error("取消订单时发生错误: {}", e.getMessage(), e);
            throw new BusinessException("取消订单失败：" + e.getMessage());
        }
    }
    
    // 根据userId查询用户历史订单list
    @GetMapping("/history/{userId}")
    public Result<List<OrderResponse>> getOrderHistory(@PathVariable Integer userId) {
        // 验证当前登录用户是否有权限查看该用户的订单
        String token = this.request.getHeader("Authorization");
        if (token == null) {
            throw new BusinessException("用户未登录");
        }
        
        try {
            // 从token中获取用户标识
            String userIdentifier = jwtTokenUtil.getUsernameFromToken(token);
            log.info("从token中解析出的用户标识: {}", userIdentifier);
            
            // 查找当前登录用户
            User currentUser = null;
            
            // 尝试通过手机号查找
            currentUser = userMapper.selectByMobile(userIdentifier);
            
            // 如果没找到，尝试通过openid查找
            if (currentUser == null) {
                currentUser = userMapper.selectByWechatOpenid(userIdentifier);
            }
            
            // 如果还没找到，尝试通过unionid查找
            if (currentUser == null) {
                currentUser = userMapper.selectByWechatUnionid(userIdentifier);
            }
            
            if (currentUser == null) {
                throw new BusinessException("用户未登录或登录已过期");
            }
            
            // 验证权限（只能查看自己的订单）
            if (!currentUser.getUserId().equals(userId)) {
                throw new BusinessException("无权查看其他用户的订单");
            }
            
            // 获取订单列表
            List<OrderResponse> orders = orderService.getOrderHistory(userId);
            return Result.success(orders);
            
        } catch (Exception e) {
            log.error("查询订单历史时发生错误: {}", e.getMessage(), e);
            throw new BusinessException("查询订单失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除订单
     */
    @PostMapping("/{orderId}/delete")
    public Result<Void> deleteOrder(@PathVariable String orderId) {
        String token = this.request.getHeader("Authorization");
        if (token == null) {
            throw new BusinessException("用户未登录");
        }
        
        try {
            // 从token中获取用户标识
            String userIdentifier = jwtTokenUtil.getUsernameFromToken(token);
            
            // 查找当前登录用户
            User currentUser = null;
            
            // 尝试通过手机号查找
            currentUser = userMapper.selectByMobile(userIdentifier);
            
            // 如果没找到，尝试通过openid查找
            if (currentUser == null) {
                currentUser = userMapper.selectByWechatOpenid(userIdentifier);
            }
            
            // 如果还没找到，尝试通过unionid查找
            if (currentUser == null) {
                currentUser = userMapper.selectByWechatUnionid(userIdentifier);
            }
            
            if (currentUser == null) {
                throw new BusinessException("用户未登录或登录已过期");
            }
            
            // 删除订单
            orderService.deleteOrder(orderId, currentUser.getUserId());
            return Result.success();
            
        } catch (Exception e) {
            log.error("删除订单时发生错误: {}", e.getMessage(), e);
            throw new BusinessException("删除订单失败：" + e.getMessage());
        }
    }
} 