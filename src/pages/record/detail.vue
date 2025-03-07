<template>
  <view class="order-detail">
    <!-- 订单状态 -->
    <view class="status-card">
      <view class="status-header">
        <text class="status-text">订单已完成</text>
        <text class="status-desc">感谢您的惠顾</text>
      </view>
      <view class="delivery-info" v-if="order.deliveryType === 'delivery'">
        <text class="iconfont icon-delivery"></text>
      </view>
    </view>

    <!-- 商品信息 -->
    <view class="info-card">
      <view class="card-title">
        <text class="iconfont icon-goods"></text>
        <text>商品信息</text>
      </view>
      <view class="goods-list">
        <view class="goods-item" v-for="(item, index) in order.goods" :key="index">
          <image class="goods-img" :src="item.image" mode="aspectFill" />
          <view class="goods-info">
            <text class="goods-name">{{ item.name }}</text>
            <text class="goods-spec">{{ item.spec }}</text>
          </view>
          <view class="goods-price">
            <text class="price">¥{{ item.price }}</text>
            <text class="count">x{{ item.count }}</text>
          </view>
        </view>
      </view>
      <view class="price-detail">
        <view class="price-item">
          <text>商品总价</text>
          <text>¥{{ order.totalPrice }}</text>
        </view>
        <view class="price-item">
          <text>配送费</text>
          <text>¥{{ order.deliveryFee }}</text>
        </view>
        <view class="price-item total">
          <text>实付金额</text>
          <text class="total-price">¥{{ order.actualPrice }}</text>
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
          <text class="value">{{ order.orderNo }}</text>
        </view>
        <view class="info-item">
          <text class="label">下单时间：</text>
          <text class="value">{{ order.createTime }}</text>
        </view>
        <view class="info-item">
          <text class="label">支付方式：</text>
          <text class="value">{{ order.paymentMethod }}</text>
        </view>
        <view class="info-item">
          <text class="label">备注：</text>
          <text class="value">{{ order.remark || '无' }}</text>
        </view>
      </view>
    </view>

    <!-- 底部按钮 -->
    <view class="bottom-btns">
      <button class="btn" @tap="handleContact">联系商家</button>
      <button class="btn primary" @tap="handleReorder">再来一单</button>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'

// 模拟订单数据
const order = ref({
  status: 'completed',
  deliveryType: 'delivery',
  address: {
    name: '张三',
    phone: '13800138000',
    fullAddress: '广东省广州市天河区天河路123号'
  },
  store: {
    name: '天河店',
    address: '广东省广州市天河区天河路123号',
    phone: '020-12345678'
  },
  goods: [
    {
      image: '/static/images/goods1.jpg',
      name: '香煎牛排',
      spec: '七分熟',
      price: 88,
      count: 1
    },
    {
      image: '/static/images/goods2.jpg',
      name: '凯撒沙拉',
      spec: '标准份',
      price: 28,
      count: 1
    }
  ],
  totalPrice: 116,
  deliveryFee: 5,
  actualPrice: 121,
  orderNo: 'DD20230615001',
  createTime: '2023-06-15 12:30:45',
  paymentMethod: '微信支付',
  remark: '不要辣'
})


// 联系商家
const handleContact = () => {
  wx.makePhoneCall({
    phoneNumber: order.value.store.phone
  })
}

// 再来一单
const handleReorder = () => {
  wx.switchTab({
    url: '/pages/index/index'
  })
}
</script>

<style lang="scss" scoped>
.order-detail {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 24rpx;
  box-sizing: border-box;
}

.status-card {
  background: linear-gradient(135deg, #1296db, #0f85c2);
  color: #fff;
  padding: 40rpx 30rpx;
  border-radius: 16rpx;
  margin-bottom: 24rpx;

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
  }
}
</style> 