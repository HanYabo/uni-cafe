<script setup>
import { onMounted, reactive, ref } from "vue";

const userInfo = reactive(null)

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

// 跳转到点单页面
const goToBuy = () => {
  uni.switchTab({
    url: '/pages/order/index'
  })
}

</script>

<template>
  <view class="layout" :style="{ paddingTop: statusBarHeight + 'px' }">
    <view class="portfolio" v-if="userInfo">
      <img src="/static/index/qrcode.svg" alt="头像" class="avatar" />
      <view class="text">
        <text class="username">{{ userInfo.name }}</text>
        <text class="remind">有1张优惠券未使用，立即查看></text>
      </view>
      <view class="line1"></view>
      <view class="code">
        <img src="/static/index/qrcode.svg" class="qr" />
        <text class="member">会员码</text>
      </view>
    </view>
    <view v-else class="user-mode">
      <text class="slogan">Hi，欢迎来到uni-cafe</text>
      <view class="login-btn" @tap="handleLogin">登录/注册</view>
    </view>

    <view class="panel">
      <view class="pl" @tap="goToBuy">
        <view class="icon-wrapper">
          <image src="/static/index/coffee1.png" class="picture" />
        </view>
        <text class="l1">门店自取</text>
        <text class="l2">下单免排队</text>
      </view>
      <view class="line2"></view>
      <view class="pr" @tap="goToBuy">
        <view class="icon-wrapper">
          <image src="/static/index/delivery.png" class="picture" />
        </view>
        <text class="r1">外卖闪送</text>
        <text class="r2">配送到您家</text>
      </view>
    </view>

    <view class="banner">
      <view class="banner-item">
        <view class="icon-wrapper">
          <image src="/static/index/market.png" class="icon" />
        </view>
        <text class="t1">百货</text>
        <text class="t2">百货优惠</text>
      </view>
      <view class="line3"></view>
      <view class="banner-item">
        <view class="icon-wrapper">
          <image src="/static/index/group.png" class="icon" />
        </view>
        <text class="t1">团餐</text>
        <text class="t2">企业欢聚享福利</text>
      </view>
      <view class="line3"></view>
      <view class="banner-item">
        <view class="icon-wrapper">
          <image src="/static/index/gift.png" class="icon" />
        </view>
        <text class="t1">送礼</text>
        <text class="t2">送礼更有面子</text>
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
  padding: 30rpx;
  box-sizing: border-box;
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

  .avatar {
    width: 80rpx;
    height: 80rpx;
    border-radius: 50%;
    margin-left: 30rpx;
    z-index: 1;
  }

  .text {
    margin-left: 20rpx;
    z-index: 1;
    .username {
      font-size: 32rpx;
      font-weight: 600;
      color: #333;
      display: block;
      margin-bottom: 6rpx;
    }
    .remind {
      font-size: 24rpx;
      color: #666;
      opacity: 0.8;
    }
  }

  .line1 {
    width: 2rpx;
    height: 60rpx;
    background: rgba(0, 0, 0, 0.1);
    margin-left: 60rpx;
    z-index: 1;
  }

  .code {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-left: 75rpx;
    z-index: 1;
    
    .qr {
      width: 60rpx;
      height: 60rpx;
      margin-bottom: 8rpx;
    }
    
    .member {
      font-size: 24rpx;
      color: #666;
    }
  }
}

.user-mode {
  width: 100%;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  background: #ffffff;
  height: 150rpx;
  border-radius: 16rpx;
  margin-bottom: 30rpx;
  padding: 40rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
  position: relative;
  overflow: hidden;
  box-sizing: border-box;

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

  .slogan {
    font-size: 32rpx;
    font-weight: 600;
    color: #333;
    z-index: 1;
  }

  .login-btn {
    min-width: 160rpx;
    height: 70rpx;
    background: linear-gradient(135deg, #1296db, #0f85c7);
    border-radius: 35rpx;
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

.panel {
  width: 100%;
  background: #ffffff;
  border-radius: 16rpx;
  padding: 40rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-sizing: border-box;

  .pl, .pr {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;

    .icon-wrapper {
      width: 120rpx;
      height: 120rpx;
      background: rgba(18, 150, 219, 0.1);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: 20rpx;
      transition: all 0.3s ease;

      &:active {
        transform: scale(0.95);
        background: rgba(18, 150, 219, 0.15);
      }

      .picture {
        width: 60rpx;
        height: 60rpx;
      }
    }

    .l1, .r1 {
      font-size: 32rpx;
      font-weight: 600;
      color: #333;
      margin-bottom: 8rpx;
    }

    .l2, .r2 {
      font-size: 24rpx;
      color: #666;
    }
  }

  .line2 {
    width: 2rpx;
    height: 160rpx;
    background: rgba(0, 0, 0, 0.1);
    margin: 0 40rpx;
  }
}

.banner {
  width: 100%;
  background: #ffffff;
  border-radius: 16rpx;
  padding: 30rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-sizing: border-box;

  .banner-item {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;

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

    .t1 {
      font-size: 28rpx;
      font-weight: 600;
      color: #333;
      margin-bottom: 6rpx;
    }

    .t2 {
      font-size: 22rpx;
      color: #666;
    }
  }

  .line3 {
    width: 2rpx;
    height: 80rpx;
    background: rgba(0, 0, 0, 0.1);
    margin: 0 20rpx;
  }
}
</style>