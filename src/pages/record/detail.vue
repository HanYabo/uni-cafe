<template>
  <view class="order-detail">
    <!-- 订单状态 -->
    <view class="status-card" :class="{
      'status-waiting': order.status === 0,
      'status-processing': order.status === 1,
      'status-completed': order.status === 2,
      'status-cancelled': order.status === 3
    }">
      <view class="status-header">
        <text class="status-text">{{ getStatusText(order.status) }}</text>
        <text class="status-desc">{{ getStatusDesc(order.status) }}</text>
      </view>
    </view>

    <!-- 商品信息 -->
    <view class="info-card">
      <view class="card-title">
        <text class="iconfont icon-goods"></text>
        <text>商品信息</text>
      </view>
      <view class="goods-list">
        <view class="goods-item" v-for="(item, index) in order.items" :key="index">
          <image class="goods-img" :src="baseUrl.concat(item.mainImage)" mode="aspectFill" />
          <view class="goods-info">
            <text class="goods-name">{{ item.productName }}</text>
            <text class="goods-spec" v-for="(spec, index) in item.specs" :key="index">{{ spec.specValue }}</text>
          </view>
          <view class="goods-price">
            <text class="price">¥{{ item.subtotal }}</text>
            <text class="count">x{{ item.quantity }}</text>
          </view>
        </view>
      </view>
      <view class="price-detail">
        <view class="price-item">
          <text>商品总价</text>
          <text>¥{{ order.totalAmount }}</text>
        </view>
        <view class="price-item total">
          <text>实付金额</text>
          <text class="total-price">¥{{ order.payAmount }}</text>
        </view>
      </view>
    </view>

    <!-- 订单信息 -->
    <view class="info-card">
      <view class="card-title">
        <text class="iconfont icon-order"></text>
        <text>订单信息</text>
      </view>
      <view class="order-info">
        <view class="info-item">
          <text class="label">订单编号：</text>
          <text class="value">{{ order.orderId }}</text>
        </view>
        <view class="info-item">
          <text class="label">下单时间：</text>
          <text class="value">{{ order.status === 0 ? '--' : formatTime(order.createdAt) }}</text>
        </view>
        <view class="info-item">
          <text class="label">支付方式：</text>
          <text class="value">{{ order.payTime === null ? '未支付' : order.payType === 1 ? '微信支付' : '支付宝支付' }}</text>
        </view>
        <view class="info-item">
          <text class="label">用户备注：</text>
          <text class="value">{{ order.remark || '无' }}</text>
        </view>
      </view>
    </view>

    <!-- 底部按钮 -->
    <view class="bottom-btns">
      <button 
        class="btn" 
        @tap="order.status === 0 ? handleCancel() : handleContact()"
      >
        {{ order.status === 0 ? '取消订单' : '联系商家' }}
      </button>
      <button 
        class="btn primary" 
        :class="{ 'btn-pay': order.status === 0 }"
        @tap="order.status === 0 ? handlePay() : handleReorder()"
      >
        {{ order.status === 0 ? '立即支付' : '再来一单' }}
      </button>
    </view>
  </view>
</template>

<script setup>
import { getOrderDetailAPI } from '@/api/order'
import { formatTime } from '@/utils/format'
import { onLoad } from '@dcloudio/uni-app'
import { ref } from 'vue'

const baseUrl = 'http://localhost:9000'

// 模拟订单数据
const order = ref({})

// 定义获取订单详情方法
const getOrderDetail = async (orderId) => {
  const res = await getOrderDetailAPI(orderId)
  if(res.code === 200) {
    order.value = res.data
  }else {
    uni.showToast({
      title: '订单详情获取失败',
      icon: 'error',
      mask: true
    })
  }
}


// 联系商家
const handleContact = () => {
  uni.showToast({
    title: '该功能开发中',
    icon: 'none',
    mask: true
  })
}

// 再来一单
const handleReorder = () => {
  uni.showToast({
    title: '该功能开发中',
    icon: 'none',
    mask: true
  })
}

// 立即支付
const handlePay = () => {
  // TODO: 处理支付逻辑
  uni.showToast({
    title: '正在前往支付...',
    icon: 'none'
  })
}

// 取消订单
const handleCancel = () => {
  uni.showModal({
    title: '提示',
    content: '确定要取消该订单吗？',
    success: async (res) => {
      if (res.confirm) {
        // TODO: 调用取消订单API
        uni.showToast({
          title: '订单已取消',
          icon: 'success'
        })
      }
    }
  })
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    0: '订单待支付',
    1: '订单已支付',
    2: '订单已完成',
    3: '订单已取消'
  }
  return statusMap[status] || '未知状态'
}

// 获取状态描述
const getStatusDesc = (status) => {
  const descMap = {
    0: '请尽快支付订单',
    1: '请耐心等待制作',
    2: '感谢您的惠顾',
    3: '期待您下次光临'
  }
  return descMap[status] || ''
}

// 接收url传递的orderId参数
onLoad((options) => {
  getOrderDetail(options.orderId)
})
</script>

<style lang="scss" scoped>
.order-detail {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 24rpx;
  box-sizing: border-box;
}

.status-card {
  padding: 40rpx 30rpx;
  border-radius: 16rpx;
  margin-bottom: 24rpx;
  color: #fff;
  
  // 待支付状态
  &.status-waiting {
    background: linear-gradient(135deg, #ff6b00, #ff8533);
  }
  
  // 已支付状态
  &.status-processing {
    background: linear-gradient(135deg, #1296db, #0f85c2);
  }
  
  // 已完成状态
  &.status-completed {
    background: linear-gradient(135deg, #52c41a, #3eb213);
  }
  
  // 已取消状态
  &.status-cancelled {
    background: linear-gradient(135deg, #999999, #666666);
  }

  .status-header {
    margin-bottom: 20rpx;

    .status-text {
      font-size: 36rpx;
      font-weight: bold;
      margin-bottom: 8rpx;
      display: block;
    }

    .status-desc {
      font-size: 26rpx;
      opacity: 0.9;
    }
  }

  .delivery-info {
    display: flex;
    align-items: center;
    font-size: 28rpx;

    .iconfont {
      margin-right: 12rpx;
      font-size: 32rpx;
    }
  }
}

.info-card {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 24rpx;

  .card-title {
    display: flex;
    align-items: center;
    margin-bottom: 24rpx;
    font-size: 30rpx;
    font-weight: bold;
    color: #333;

    .iconfont {
      margin-right: 12rpx;
      color: #1296db;
      font-size: 32rpx;
    }
  }
}

.info-content {
  .delivery-type {
    margin-bottom: 20rpx;
    font-size: 28rpx;

    .label {
      color: #666;
    }

    .value {
      color: #333;
    }
  }

  .address {
    .name, .phone {
      font-size: 30rpx;
      color: #333;
      margin-right: 20rpx;
    }

    .address-text {
      font-size: 28rpx;
      color: #666;
      margin-top: 8rpx;
    }
  }

  .store-info {
    font-size: 28rpx;
    color: #666;

    .store-name {
      font-size: 30rpx;
      color: #333;
      margin-bottom: 8rpx;
    }

    .store-address, .store-phone {
      margin-top: 8rpx;
    }
  }
}

.goods-list {
  .goods-item {
    display: flex;
    align-items: center;
    padding: 20rpx 0;
    border-bottom: 1rpx solid #f5f5f5;

    &:last-child {
      border-bottom: none;
    }

    .goods-img {
      width: 120rpx;
      height: 120rpx;
      border-radius: 8rpx;
      margin-right: 20rpx;
    }

    .goods-info {
      flex: 1;

      .goods-name {
        font-size: 28rpx;
        color: #333;
        margin-bottom: 8rpx;
      }

      .goods-spec {
        font-size: 24rpx;
        color: #999;
      }
    }

    .goods-price {
      text-align: right;

      .price {
        font-size: 28rpx;
        color: #333;
        font-weight: bold;
        display: block;
      }

      .count {
        font-size: 24rpx;
        color: #999;
        margin-top: 8rpx;
      }
    }
  }
}

.price-detail {
  margin-top: 24rpx;
  padding-top: 24rpx;
  border-top: 1rpx solid #f5f5f5;

  .price-item {
    display: flex;
    justify-content: space-between;
    margin-bottom: 16rpx;
    font-size: 28rpx;
    color: #666;

    &.total {
      margin-top: 20rpx;
      padding-top: 20rpx;
      border-top: 1rpx solid #f5f5f5;
      color: #333;
      font-weight: bold;

      .total-price {
        color: #ff4d4f;
        font-size: 32rpx;
      }
    }
  }
}

.order-info {
  .info-item {
    display: flex;
    margin-bottom: 16rpx;
    font-size: 28rpx;

    &:last-child {
      margin-bottom: 0;
    }

    .label {
      color: #666;
      width: 160rpx;
    }

    .value {
      color: #333;
      flex: 1;
    }
  }
}

.bottom-btns {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #fff;
  padding: 20rpx;
  display: flex;
  justify-content: space-between;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
  padding-left: 100rpx;
  padding-right: 100rpx;

  .btn {
    width: 200rpx;
    height: 80rpx;
    line-height: 80rpx;
    text-align: center;
    border-radius: 40rpx;
    font-size: 28rpx;
    border: 1px solid #ddd;
    background-color: #fff;
    color: #666;
    padding: 0;
    margin: 0;
    
    &::after {
      border: none;
    }
    
    &.primary {
      background-color: #1296db;
      color: #fff;
      border: none;
    }

    &.btn-pay {
      background-color: #ff6b00;
    }
  }
}
</style> 