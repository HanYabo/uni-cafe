package com.han.cafe.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class AdminOrderDetailVO {
    private String orderId;              // 订单ID（varchar(32)）
    private Integer userId;              // 用户ID
    private String nickname;             // 用户昵称
    private String mobile;              // 手机号
    private Integer status;              // 订单状态：0-待支付 1-已支付 2-已完成 3-已取消
    private Integer payType;             // 支付方式：1-微信 2-支付宝
    private LocalDateTime payTime;       // 支付时间
    private BigDecimal totalAmount;      // 订单总金额
    private BigDecimal discountAmount;   // 优惠金额
    private BigDecimal payAmount;        // 实付金额
    private Integer couponId;            // 优惠券ID
    private String remark;               // 备注
    private LocalDateTime createdAt;     // 创建时间
    private LocalDateTime updatedAt;     // 更新时间
    private List<OrderItemDetail> items; // 订单项详情

    @Data
    public static class OrderItemDetail {
        private Integer itemId;          // 订单项ID
        private Integer productId;       // 商品ID
        private String productName;      // 商品名称
        private BigDecimal basePrice;    // 商品基础价格
        private BigDecimal actualPrice;  // 实际单价（含规格价格）
        private Integer quantity;        // 购买数量
        private BigDecimal subtotal;     // 小计金额
        private List<OrderItemSpecDetail> specs; // 规格详情
    }

    @Data
    public static class OrderItemSpecDetail {
        private Integer specId;          // 规格ID
        private String specName;         // 规格名称
        private Integer specValueId;     // 规格值ID
        private String specValue;        // 规格值
        private BigDecimal extraPrice;   // 规格值额外价格
    }
} 