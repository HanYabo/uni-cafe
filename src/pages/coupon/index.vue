<template>
  <view class="coupon-page">
    <!-- 顶部状态栏空白占位 -->
    <view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
    
    <!-- 顶部标题 -->
    <view class="page-header">
      <view class="back-icon" @tap="goBack">
        <text class="iconfont icon-back">〈</text>
      </view>
      <text class="title">我的优惠券</text>
      <view class="placeholder"></view>
    </view>
    
    <!-- 优惠券分类 -->
    <view class="tab-bar">
      <view 
        class="tab-item" 
        :class="{ active: activeTab === 0 }" 
        @tap="activeTab = 0"
      >
        可使用 ({{ availableCoupons.length }})
      </view>
      <view 
        class="tab-item" 
        :class="{ active: activeTab === 1 }" 
        @tap="activeTab = 1"
      >
        已过期 ({{ expiredCoupons.length }})
      </view>
      <view 
        class="tab-item" 
        :class="{ active: activeTab === 2 }" 
        @tap="activeTab = 2"
      >
        已使用 ({{ usedCoupons.length }})
      </view>
    </view>
    
    <!-- 优惠券列表 -->
    <scroll-view class="coupon-list" scroll-y>
      <!-- 无优惠券时的提示 -->
      <view class="empty-tip" v-if="currentCoupons.length === 0">
        <image src="/static/mine/empty.png" class="empty-icon" />
        <text class="empty-text">暂无优惠券</text>
      </view>
      
      <!-- 优惠券列表 -->
      <view 
        class="coupon-item" 
        v-for="coupon in currentCoupons" 
        :key="coupon.couponId"
        :class="{ 'disabled': activeTab !== 0 }"
      >
        <!-- 优惠券左侧金额信息 -->
        <view class="coupon-left">
          <view class="amount-wrap">
            <text class="symbol" v-if="coupon.type === 1">¥</text>
            <text class="amount" v-if="coupon.type === 1">{{ coupon.amount }}</text>
            <text class="amount" v-else>{{ (coupon.discount / 10).toFixed(1) }}</text>
            <text class="unit" v-if="coupon.type === 2">折</text>
          </view>
          <text class="condition">{{ coupon.threshold === 0 ? '无门槛' : `满${coupon.threshold}元可用` }}</text>
        </view>
        
        <!-- 中间分隔线 -->
        <view class="coupon-divider">
          <view class="circle top"></view>
          <view class="dashed-line"></view>
          <view class="circle bottom"></view>
        </view>
        
        <!-- 优惠券右侧描述信息 -->
        <view class="coupon-right">
          <text class="type">{{ coupon.type === 1 ? '满减券' : '折扣券' }}</text>
          <text class="scope">全场通用</text>
          <text class="date">有效期至: {{ formatTime(coupon.endTime) }}</text>
          
          <!-- 仅在可用券上显示使用按钮 -->
          <view 
            v-if="activeTab === 0" 
            class="use-btn"
            @tap="goToOrder"
          >
            立即使用
          </view>
          
          <!-- 已使用或过期的水印 -->
          <view class="status-watermark" v-if="activeTab === 1">已过期</view>
          <view class="status-watermark" v-if="activeTab === 2">已使用</view>
        </view>
      </view>
    </scroll-view>
    
    <!-- 底部按钮 -->
    <view class="bottom-btn" @tap="goToOrder" v-if="currentCoupons.length > 0">
      <text>去使用</text>
    </view>
  </view>
</template>

<script setup>
import { getCouponListAPI } from '@/api/coupon';
import { formatTime } from '@/utils/format';
import { onShow } from '@dcloudio/uni-app';
import { computed, onMounted, ref } from 'vue';

// 获取状态栏高度
const statusBarHeight = ref(0)

// 优惠券分类标签
const activeTab = ref(0)

// 优惠券数据
const coupons = ref([])

// 获取状态栏高度
onMounted(() => {
  const systemInfo = uni.getSystemInfoSync()
  statusBarHeight.value = systemInfo.statusBarHeight || 0
})

// 获取优惠券列表
const getCouponList = async () => {
  const res = await getCouponListAPI()
  if (res.code === 200) {
    coupons.value = res.data
  } else {
    uni.showToast({
      title: '优惠券信息获取失败',
      icon: 'error'
    })
  }
}

// 根据状态过滤优惠券
const availableCoupons = computed(() => {
  const now = new Date().getTime()
  return coupons.value.filter(coupon => {
    return coupon.status === 0 && new Date(coupon.endTime).getTime() > now
  })
})

const expiredCoupons = computed(() => {
  const now = new Date().getTime()
  return coupons.value.filter(coupon => {
    return new Date(coupon.endTime).getTime() <= now
  })
})

const usedCoupons = computed(() => {
  return coupons.value.filter(coupon => coupon.status === 1)
})

// 当前显示的优惠券
const currentCoupons = computed(() => {
  if (activeTab.value === 0) return availableCoupons.value
  if (activeTab.value === 1) return expiredCoupons.value
  return usedCoupons.value
})

// 返回上一页
const goBack = () => {
  uni.navigateBack()
}

// 跳转到点单页面
const goToOrder = () => {
  uni.switchTab({
    url: '/pages/menu/index'
  })
}

// 页面加载和显示时获取优惠券
onMounted(() => {
  getCouponList()
})

onShow(() => {
  getCouponList()
})
</script>

<style lang="scss" scoped>
.coupon-page {
  min-height: 100vh;
  background-color: #f6f6f6;
  display: flex;
  flex-direction: column;
  position: relative;
}

/* 状态栏占位 */
.status-bar {
  width: 100%;
  background-color: #fff;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 44px;
  background-color: #fff;
  position: relative;
  z-index: 100;
  
  .back-icon {
    position: absolute;
    left: 0;
    width: 44px;
    height: 44px;
    display: flex;
    align-items: center;
    justify-content: center;
    
    .icon-back {
      font-size: 20px;
      color: #333;
    }
  }
  
  .title {
    position: absolute;
    left: 50%;
    transform: translateX(-50%);
    font-size: 16px;
    color: #333;
    font-weight: 500;
  }
  
  .placeholder {
    width: 44px;
  }
}

.tab-bar {
  display: flex;
  background-color: #fff;
  padding: 12rpx 0;
  border-bottom: 1px solid #f5f5f5;
  
  .tab-item {
    flex: 1;
    text-align: center;
    font-size: 28rpx;
    color: #666;
    padding: 16rpx 0;
    position: relative;
    transition: all 0.3s;
    
    &.active {
      color: #1296db;
      font-weight: 500;
      
      &::after {
        content: '';
        position: absolute;
        bottom: -12rpx;
        left: 50%;
        transform: translateX(-50%);
        width: 30rpx;
        height: 3rpx;
        background-color: #1296db;
        border-radius: 2rpx;
      }
    }
  }
}

.coupon-list {
  flex: 1;
  padding: 0 30rpx;
  height: calc(100vh - 44px - var(--status-bar-height) - 120rpx - 100rpx);
  display: flex;
  flex-direction: column;
  position: relative;
  
  .empty-tip {
    position: absolute;
    top: 40%;
    left: calc(50% - 30rpx);
    transform: translate(-50%, -50%);
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: auto;
    
    .empty-icon {
      width: 240rpx;
      height: 240rpx;
      opacity: 0.6;
      margin-bottom: 30rpx;
    }
    
    .empty-text {
      font-size: 28rpx;
      color: #999;
      font-weight: 400;
      margin-bottom: 40rpx;
    }
  }
}

.coupon-item {
  display: flex;
  height: 200rpx;
  background: #fff;
  border-radius: 16rpx;
  margin-bottom: 30rpx;
  position: relative;
  overflow: hidden;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 8rpx;
    background: linear-gradient(90deg, #1296db, #0f85c7);
    opacity: 0.8;
  }
  
  &.disabled {
    opacity: 0.7;
    
    &::before {
      background: linear-gradient(90deg, #999, #666);
    }
    
    .amount, .symbol, .unit {
      color: #999 !important;
    }
    
    .use-btn {
      display: none;
    }
  }
  
  .coupon-left {
    width: 220rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    background: rgba(18, 150, 219, 0.05);
    
    .amount-wrap {
      display: flex;
      align-items: baseline;
      
      .symbol {
        font-size: 32rpx;
        color: #1296db;
        font-weight: bold;
      }
      
      .amount {
        font-size: 70rpx;
        font-weight: bold;
        color: #1296db;
        line-height: 1;
      }
      
      .unit {
        font-size: 32rpx;
        color: #1296db;
        font-weight: bold;
      }
    }
    
    .condition {
      font-size: 24rpx;
      color: #666;
      margin-top: 12rpx;
      padding: 4rpx 12rpx;
      background: rgba(18, 150, 219, 0.1);
      border-radius: 20rpx;
    }
  }
  
  .coupon-divider {
    display: flex;
    flex-direction: column;
    position: relative;
    
    .circle {
      width: 30rpx;
      height: 30rpx;
      background: #f6f6f6;
      border-radius: 50%;
      position: absolute;
      left: -15rpx;
      
      &.top {
        top: -15rpx;
      }
      
      &.bottom {
        bottom: -15rpx;
      }
    }
    
    .dashed-line {
      position: absolute;
      left: 0;
      top: 15rpx;
      bottom: 15rpx;
      border-left: 2rpx dashed rgba(18, 150, 219, 0.2);
    }
  }
  
  .coupon-right {
    flex: 1;
    padding: 30rpx;
    display: flex;
    flex-direction: column;
    justify-content: center;
    position: relative;
    
    .type {
      font-size: 32rpx;
      font-weight: 600;
      color: #333;
      margin-bottom: 8rpx;
    }
    
    .scope {
      font-size: 24rpx;
      color: #666;
      margin-bottom: 8rpx;
    }
    
    .date {
      font-size: 22rpx;
      color: #999;
      margin-bottom: 12rpx;
    }
    
    .use-btn {
      width: fit-content;
      padding: 8rpx 30rpx;
      background: linear-gradient(135deg, #1296db, #0f85c7);
      border-radius: 30rpx;
      color: #fff;
      font-size: 24rpx;
      box-shadow: 0 4rpx 12rpx rgba(18, 150, 219, 0.2);
      transition: all 0.3s ease;
      
      &:active {
        transform: scale(0.98);
        background: linear-gradient(135deg, #0f85c7, #0c74ae);
      }
    }
    
    .status-watermark {
      position: absolute;
      right: 20rpx;
      top: 50%;
      transform: translateY(-50%) rotate(-15deg);
      font-size: 80rpx;
      color: rgba(153, 153, 153, 0.2);
      font-weight: bold;
      pointer-events: none;
    }
  }
}

.bottom-btn {
  height: 100rpx;
  background: linear-gradient(135deg, #1296db, #0f85c7);
  color: #fff;
  font-size: 32rpx;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 30rpx;
  border-radius: 50rpx;
  box-shadow: 0 8rpx 16rpx rgba(18, 150, 219, 0.2);
  transition: all 0.3s ease;
  margin-bottom: calc(50rpx + env(safe-area-inset-bottom));
  
  &:active {
    transform: scale(0.98);
    box-shadow: 0 4rpx 8rpx rgba(18, 150, 219, 0.2);
  }
}
</style> 