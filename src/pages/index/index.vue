<script setup>
import { onShow } from '@dcloudio/uni-app'
import { onMounted, ref } from "vue"

const userInfo = ref(null)
const statusBarHeight = ref(0)
const showMemberCode = ref(false)

// 获取用户信息的方法
const getUserInfo = () => {
  const storedUserInfo = wx.getStorageSync('userInfo')
  if (storedUserInfo) {
    userInfo.value = storedUserInfo
  }
}

onMounted(() => {
  const systemInfo = uni.getSystemInfoSync()
  statusBarHeight.value = systemInfo.statusBarHeight || 0
  getUserInfo()
})

// 每次页面显示时都重新获取用户信息
onShow(() => {
  getUserInfo()
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

const handleShowMemberCode = () => {
  showMemberCode.value = true
}

const handleCloseMemberCode = () => {
  showMemberCode.value = false
}

</script>

<template>
  <view class="layout" :style="{ paddingTop: statusBarHeight + 'px' }">
    <view class="portfolio" v-if="userInfo">
      <image :src="userInfo.avatarUrl || '/static/index/qrcode.svg'" class="avatar" />
      <view class="text">
        <text class="username">{{ userInfo.nickName || '未知用户' }}</text>
        <text class="remind">有1张优惠券未使用，立即查看></text>
      </view>
      <view class="line1"></view>
      <view class="code" @tap="handleShowMemberCode">
        <image src="/static/index/qrcode.svg" class="qr" />
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

    <!-- 会员码弹出层 -->
    <view class="member-popup" v-if="showMemberCode" @tap="handleCloseMemberCode">
      <view class="member-card" @tap.stop>
        <view class="close-btn" @tap="handleCloseMemberCode">
          <text class="close-icon">×</text>
        </view>
        <view class="member-info">
          <image :src="userInfo?.avatarUrl" class="member-avatar" mode="aspectFill" />
          <text class="member-name">{{ userInfo?.nickName }}</text>
        </view>
        <view class="qrcode-container">
          <image src="/static/index/qrcode.svg" class="member-qrcode" />
          <text class="scan-text">扫码支付</text>
        </view>
        <view class="member-footer">
          <text class="member-tip">出示此码可在门店内享受会员权益</text>
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

  .avatar {
    width: 80rpx;
    height: 80rpx;
    border-radius: 50%;
    margin-right: 20rpx;
    z-index: 1;
  }

  .text {
    flex: 1;
    display: flex;
    flex-direction: column;
    z-index: 1;
    
    .username {
      font-size: 32rpx;
      font-weight: 600;
      color: #333;
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
    margin: 0 24rpx;
    z-index: 1;
  }

  .code {
    display: flex;
    flex-direction: column;
    align-items: center;
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

.member-popup {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}

.member-card {
  width: 600rpx;
  background: #ffffff;
  border-radius: 24rpx;
  padding: 40rpx;
  position: relative;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.1);
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from {
    transform: translateY(50rpx);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.close-btn {
  position: absolute;
  top: 20rpx;
  right: 20rpx;
  width: 60rpx;
  height: 60rpx;
  display: flex;
  justify-content: center;
  align-items: center;
  
  .close-icon {
    font-size: 48rpx;
    color: #999;
    line-height: 1;
  }
}

.member-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 40rpx;
  
  .member-avatar {
    width: 120rpx;
    height: 120rpx;
    border-radius: 50%;
    margin-bottom: 20rpx;
    border: 4rpx solid rgba(18, 150, 219, 0.1);
  }
  
  .member-name {
    font-size: 32rpx;
    font-weight: 600;
    color: #333;
  }
}

.qrcode-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40rpx 0;
  
  .member-qrcode {
    width: 400rpx;
    height: 400rpx;
    margin-bottom: 20rpx;
  }
  
  .scan-text {
    font-size: 28rpx;
    color: #666;
  }
}

.member-footer {
  text-align: center;
  margin-top: 20rpx;
  
  .member-tip {
    font-size: 24rpx;
    color: #999;
  }
}
</style>