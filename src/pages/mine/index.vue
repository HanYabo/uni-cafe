<script setup>
import { onMounted, ref } from 'vue'

const statusBarHeight = ref(0)

onMounted(() => {
  const systemInfo = uni.getSystemInfoSync()
  statusBarHeight.value = systemInfo.statusBarHeight || 0
})

const handleLogin = () => {
  uni.navigateTo({
    url: '/pages/login/index'
  })
}

const handleToAddress = () => {
  uni.navigateTo({
    url: '/pages/address/index'
  })
}

// 优惠券数据
const coupons = ref([
  {
    id: 1,
    type: '满减券',
    amount: 10,
    condition: '满30可用',
    expireDate: '2024-04-30'
  },
  {
    id: 2,
    type: '折扣券',
    amount: 8.8,
    condition: '无门槛',
    expireDate: '2024-04-15'
  }
])
</script>

<template>
  <view class="layout" :style="{ paddingTop: statusBarHeight + 'px' }">
    <view class="safe-area">
      <!-- 头像模块 -->
      <view class="portfolio">
        <image src="/static/my/avatar.png" alt="头像" class="avatar" />
        <view class="text">
          <text class="username">Hello!</text> <br>
          <text class="remind">登录享受更多精彩服务</text>
        </view>
        <view class="btn" @click="handleLogin">登录/注册</view>
      </view>
      
      <!-- 优惠券模块 -->
      <view class="coupon-section">
        <view class="coupon-header">
          <text class="coupon-title">我的优惠券</text>
          <text class="coupon-more">查看全部 ></text>
        </view>
        <view class="coupon-list">
          <view class="coupon-item" v-for="coupon in coupons" :key="coupon.id">
            <view class="coupon-left">
              <view class="amount-wrap">
                <text class="symbol" v-if="coupon.type === '满减券'">¥</text>
                <text class="amount">{{ coupon.amount }}</text>
                <text class="unit" v-if="coupon.type === '折扣券'">折</text>
              </view>
              <text class="condition">{{ coupon.condition }}</text>
            </view>
            <view class="coupon-right">
              <text class="type">{{ coupon.type }}</text>
              <text class="date">有效期至：{{ coupon.expireDate }}</text>
              <view class="use-btn">立即使用</view>
            </view>
          </view>
        </view>
      </view>
      
      <view class="util">
        <view class="util-header">
          <text class="util-title">常用功能</text>
        </view>
        <view class="util-grid">
          <view class="grid-item" @tap="handleToAddress">
            <view class="icon-wrapper">
              <image src="/static/mine/address.png" class="icon" />
            </view>
            <text class="text">地址管理</text>
          </view>
          <view class="grid-item">
            <view class="icon-wrapper">
              <image src="/static/mine/invoice.png" class="icon" />
            </view>
            <text class="text">发票管理</text>
          </view>
          <view class="grid-item">
            <view class="icon-wrapper">
              <image src="/static/mine/setting.png" class="icon" />
            </view>
            <text class="text">设置</text>
          </view>
          <view class="grid-item">
            <view class="icon-wrapper">
              <image src="/static/mine/wechat.png" class="icon" />
            </view>
            <text class="text">关注公众号</text>
          </view>
          <view class="grid-item">
            <view class="icon-wrapper">
              <image src="/static/mine/service.png" class="icon" />
            </view>
            <text class="text">在线客服</text>
          </view>
          <view class="grid-item">
            <view class="icon-wrapper">
              <image src="/static/mine/terms.png" class="icon" />
            </view>
            <text class="text">条款与证明</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<style scoped lang="scss">
.layout {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-height: 100vh;
  background: rgba(246, 246, 246, 1);
}

.safe-area {
  width: 92%;
  padding: 20rpx 0;
}

.portfolio {
  width: 100%;
  display: flex;
  flex-direction: row;
  align-items: center;
  background: #ffffff;
  height: 150rpx;
  border-radius: 16rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    right: -60rpx;
    top: -60rpx;
    width: 200rpx;
    height: 200rpx;
    background: rgba(18, 150, 219, 0.1);
    border-radius: 50%;
    z-index: 0;
  }

  &::after {
    content: '';
    position: absolute;
    left: 40%;
    bottom: -80rpx;
    width: 160rpx;
    height: 160rpx;
    background: rgba(18, 150, 219, 0.05);
    border-radius: 50%;
    z-index: 0;
  }

  .avatar {
    width: 80rpx;
    height: 80rpx;
    border-radius: 50%;
    margin-left: 30rpx;
    background: linear-gradient(45deg, rgba(18, 150, 219, 0.1), rgba(18, 150, 219, 0.2));
    padding: 15rpx;
    z-index: 1;
  }

  .text {
    margin-left: 20rpx;
    flex: 1;
    z-index: 1;
    
    .username {
      font-size: 32rpx;
      font-weight: 600;
      color: #333;
      margin-bottom: 6rpx;
      display: block;
    }
    
    .remind {
      font-size: 24rpx;
      color: #666;
      opacity: 0.8;
      display: block;
      margin-top: 6rpx;
    }
  }

  .btn {
    min-width: 160rpx;
    height: 70rpx;
    background: linear-gradient(135deg, #1296db, #0f85c7);
    border-radius: 35rpx;
    margin-right: 30rpx;
    padding: 0 30rpx;
    font-size: 26rpx;
    display: flex;
    justify-content: center;
    align-items: center;
    color: #fff;
    font-weight: 500;
    z-index: 1;
    box-shadow: 0 4rpx 12rpx rgba(18, 150, 219, 0.2);
    transition: all 0.3s ease;

    &:active {
      transform: scale(0.98);
      box-shadow: 0 2rpx 6rpx rgba(18, 150, 219, 0.2);
    }
  }
}

.coupon-section {
  width: 100%;
  background: #ffffff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
  box-sizing: border-box;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    right: -60rpx;
    top: -60rpx;
    width: 200rpx;
    height: 200rpx;
    background: rgba(18, 150, 219, 0.1);
    border-radius: 50%;
    z-index: 0;
  }

  .coupon-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20rpx;
    position: relative;
    z-index: 1;

    .coupon-title {
      font-size: 32rpx;
      font-weight: 600;
      color: #333;
    }

    .coupon-more {
      font-size: 24rpx;
      color: #666;
    }
  }

  .coupon-list {
    position: relative;
    z-index: 1;

    .coupon-item {
      display: flex;
      align-items: center;
      height: 160rpx;
      background: linear-gradient(45deg, rgba(18, 150, 219, 0.05), rgba(18, 150, 219, 0.1));
      border-radius: 12rpx;
      margin-bottom: 20rpx;
      position: relative;
      overflow: hidden;

      &::after {
        content: '';
        position: absolute;
        left: 220rpx;
        top: 0;
        bottom: 0;
        width: 2rpx;
        background: rgba(255, 255, 255, 0.8);
        border-right: 2rpx dashed rgba(18, 150, 219, 0.2);
      }

      .coupon-left {
        width: 220rpx;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;

        .amount-wrap {
          display: flex;
          align-items: baseline;

          .symbol {
            font-size: 32rpx;
            color: #1296db;
            margin-right: 4rpx;
          }

          .amount {
            font-size: 60rpx;
            font-weight: 600;
            color: #1296db;
            line-height: 1;
          }

          .unit {
            font-size: 28rpx;
            color: #1296db;
            margin-left: 4rpx;
          }
        }

        .condition {
          font-size: 24rpx;
          color: #666;
          margin-top: 10rpx;
        }
      }

      .coupon-right {
        flex: 1;
        padding: 20rpx 30rpx;
        display: flex;
        flex-direction: column;
        justify-content: center;

        .type {
          font-size: 28rpx;
          font-weight: 500;
          color: #333;
          margin-bottom: 8rpx;
        }

        .date {
          font-size: 22rpx;
          color: #999;
          margin-bottom: 16rpx;
        }

        .use-btn {
          width: fit-content;
          padding: 8rpx 24rpx;
          background: linear-gradient(135deg, #1296db, #0f85c7);
          border-radius: 24rpx;
          color: #fff;
          font-size: 24rpx;
          box-shadow: 0 4rpx 12rpx rgba(18, 150, 219, 0.2);
          transition: all 0.3s ease;

          &:active {
            transform: scale(0.98);
            box-shadow: 0 2rpx 6rpx rgba(18, 150, 219, 0.2);
          }
        }
      }
    }
  }
}

.util {
  width: 100%;
  background: #ffffff;
  border-radius: 16rpx;
  padding: 30rpx;
  box-sizing: border-box;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);

  .util-header {
    margin-bottom: 30rpx;
    
    .util-title {
      font-size: 32rpx;
      font-weight: 600;
      color: #333;
    }
  }

  .util-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 30rpx;
    
    .grid-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 20rpx 0;
      
      .icon-wrapper {
        width: 80rpx;
        height: 80rpx;
        background: rgba(18, 150, 219, 0.1);
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-bottom: 16rpx;
        transition: all 0.3s ease;
        
        &:active {
          transform: scale(0.95);
          background: rgba(18, 150, 219, 0.15);
        }
        
        .icon {
          width: 40rpx;
          height: 40rpx;
        }
      }
      
      .text {
        font-size: 26rpx;
        color: #333;
      }
    }
  }
}
</style>