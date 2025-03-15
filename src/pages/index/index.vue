<script setup>
import { getCouponListAPI } from '@/api/coupon'
import { onShow } from '@dcloudio/uni-app'
import { onMounted, ref } from "vue"

const userInfo = ref(null)
const statusBarHeight = ref(0)
const showMemberCode = ref(false)
const couponList = ref([])
const currentSwiperIndex = ref(0)

// 获取优惠券列表
const getCouponList = async () => {
  // 只有token存在时，才获取优惠券列表
  const token = wx.getStorageSync('token')
  if (token) {
    const res = await getCouponListAPI()
    couponList.value = res.data
  }
}

// 轮播图数据
const bannerList = ref([
  { image: '/static/index/banner1.jpeg' },
  { image: '/static/index/banner2.jpg' }
])

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
  
  // 隐藏原生导航栏
  uni.hideNavigationBarLoading()
  // 设置导航栏为透明
  uni.setNavigationBarColor({
    frontColor: '#ffffff',
    backgroundColor: 'transparent'
  })
})

// 每次页面显示时都重新获取用户信息
onShow(async () => {
  getUserInfo(),
  getCouponList()
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

const handleShowCouponList = () => {
  uni.navigateTo({
    url: '/pages/coupon/index'
  })
}

// 处理轮播图变化
const handleSwiperChange = (e) => {
  currentSwiperIndex.value = e.detail.current
}

</script>

<template>
  <view class="layout">
    <!-- 轮播图部分 - 完全覆盖顶部 -->
    <view class="swiper-container">
      <swiper 
        class="swiper" 
        circular 
        autoplay 
        interval="3000" 
        duration="500" 
        @change="handleSwiperChange"
        :indicator-dots="true"
        indicator-color="rgba(150, 150, 150, 0.5)"
        indicator-active-color="#ffffff"
      >
        <swiper-item v-for="(item, index) in bannerList" :key="index">
          <image :src="item.image" mode="aspectFill" class="swiper-image" />
        </swiper-item>
      </swiper>
    </view>
    
    <!-- 渐变遮罩 - 覆盖导航栏区域 -->
    <view class="gradient-overlay"></view>
    
    <!-- 状态栏占位 - 完全透明 -->
    <view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
    
    <!-- 自定义导航栏 - 透明背景，仅包含右侧按钮 -->
    <view class="custom-nav-bar">
      <view class="right-placeholder"></view> <!-- 占位，确保导航区域高度合适 -->
    </view>

    <!-- 内容区域 - 去掉白色背景 -->
    <view class="content-wrapper">
      <!-- 用户信息或登录提示 -->
      <view class="user-section" v-if="userInfo">
        <image :src="userInfo.avatarUrl || '/static/mine/avatar.png'" class="avatar" />
        <view class="text">
          <text class="username">{{ userInfo.nickName }}</text>
          <text class="remind" @tap="handleShowCouponList" v-if="couponList.length > 0">有{{ couponList.length }}张优惠券未使用，立即查看></text>
          <text class="remind" v-else>暂无优惠券可用哦</text>
        </view>
        <view class="line1"></view>
        <view class="code" @tap="handleShowMemberCode">
          <image src="/static/index/qrcode.svg" class="qr" />
          <text class="member">会员码</text>
        </view>
      </view>
      <view v-else class="login-section">
        <text class="slogan">Hi，欢迎来到uni-cafe</text>
        <view class="login-btn" @tap="handleLogin">登录/注册</view>
      </view>

      <view class="menu-section">
        <view class="menu-item" @tap="goToBuy">
          <view class="icon-wrapper">
            <image src="/static/index/coffee1.png" class="picture" />
          </view>
          <text class="title">门店自取</text>
          <text class="subtitle">下单免排队</text>
        </view>
        <view class="menu-item" @tap="goToBuy">
          <view class="icon-wrapper">
            <image src="/static/index/delivery.png" class="picture" />
          </view>
          <text class="title">外卖闪送</text>
          <text class="subtitle">配送到您家</text>
        </view>
      </view>

      <view class="feature-section">
        <view class="feature-item">
          <view class="icon-wrapper">
            <image src="/static/index/market.png" class="icon" />
          </view>
          <text class="title">百货</text>
          <text class="subtitle">百货优惠</text>
        </view>
        <view class="feature-item">
          <view class="icon-wrapper">
            <image src="/static/index/group.png" class="icon" />
          </view>
          <text class="title">团餐</text>
          <text class="subtitle">企业欢聚享福利</text>
        </view>
        <view class="feature-item">
          <view class="icon-wrapper">
            <image src="/static/index/gift.png" class="icon" />
          </view>
          <text class="title">送礼</text>
          <text class="subtitle">送礼更有面子</text>
        </view>
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
  min-height: 100vh;
  background: transparent;
  box-sizing: border-box;
  position: relative;
}

.status-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 10;
  background: transparent;
}

.custom-nav-bar {
  position: fixed;
  top: v-bind('statusBarHeight + "px"');
  left: 0;
  right: 0;
  height: 44px;
  z-index: 10;
  pointer-events: none; /* 阻止拦截点击事件 */
  
  .right-placeholder {
    height: 32px;
  }
}

.swiper-container {
  width: 100%;
  height: 580rpx;
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1;
}

.swiper {
  width: 100%;
  height: 100%;
  
  &::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 120rpx;
    background: linear-gradient(to top, 
      rgba(246, 246, 246, 1) 5%, 
      rgba(246, 246, 246, 0.8) 25%, 
      rgba(246, 246, 246, 0.4) 50%, 
      rgba(246, 246, 246, 0)
    );
    z-index: 2;
  }
}

.swiper-image {
  width: 100%;
  height: 100%;
}

/* 调整轮播图指示器样式 - 微信小程序 */
:deep(.wx-swiper-dots) {
  bottom: 100rpx !important;
}

:deep(.wx-swiper-dot) {
  width: 30rpx !important;
  height: 6rpx !important;
  border-radius: 3rpx !important;
  margin: 0 6rpx !important;
}

:deep(.wx-swiper-dot-active) {
  width: 40rpx !important;
}

/* 调整轮播图指示器样式 - uni-app通用 */
:deep(.uni-swiper-dots) {
  bottom: 100rpx !important;
}

:deep(.uni-swiper-dot) {
  width: 30rpx !important;
  height: 6rpx !important;
  border-radius: 3rpx !important;
  margin: 0 6rpx !important;
}

:deep(.uni-swiper-dot-active) {
  width: 40rpx !important;
}

.gradient-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 240rpx; /* 增加高度覆盖导航栏和状态栏 */
  background: linear-gradient(to bottom, rgba(0,0,0,0.5), rgba(0,0,0,0));
  z-index: 5;
  pointer-events: none;
}

.content-wrapper {
  flex: 1;
  padding: 20rpx 30rpx 30rpx;
  margin-top: 490rpx;
  position: relative;
  z-index: 3;
  background: linear-gradient(
    to bottom, 
    rgba(246, 246, 246, 0), 
    rgba(246, 246, 246, 1) 60rpx, 
    rgba(246, 246, 246, 1)
  );
}

/* 用户信息区域 - 调整为更贴近图片的效果 */
.login-section {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  background: #ffffff;
  height: 150rpx;
  border-radius: 16rpx;
  margin-top: 20rpx;
  margin-bottom: 30rpx;
  padding: 40rpx;
  box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.08);
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
    background: rgba(18, 150, 219, 0.05);
    border-radius: 50%;
    z-index: 0;
  }

  .slogan {
    font-size: 32rpx;
    font-weight: 600;
    color: #333;
    position: relative;
    z-index: 1;
  }

  .login-btn {
    min-width: 140rpx;
    background: linear-gradient(135deg, #1296db, #0f85c7);
    border-radius: 999rpx;
    padding: 16rpx 42rpx;
    font-size: 28rpx;
    color: #fff;
    font-weight: 500;
    box-shadow: 0 6rpx 16rpx rgba(18, 150, 219, 0.15);
    transition: all 0.3s ease;
    line-height: 1.4;
    letter-spacing: 2rpx;
    display: flex;
    justify-content: center;
    align-items: center;
    position: relative;
    z-index: 1;

    &:active {
      transform: scale(0.97);
      box-shadow: 0 2rpx 8rpx rgba(18, 150, 219, 0.2);
      opacity: 0.92;
    }
  }
}

/* 用户信息区域 */
.user-section {
  display: flex;
  flex-direction: row;
  align-items: center;
  background: #ffffff;
  height: 150rpx; /* 确保与login-section一致 */
  border-radius: 16rpx;
  margin-top: 20rpx; /* 确保与login-section一致 */
  margin-bottom: 30rpx;
  padding: 40rpx;
  box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.08); /* 与login-section一致 */
  position: relative;
  overflow: hidden;
  box-sizing: border-box;

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

/* 菜单区域 */
.menu-section {
  display: flex;
  background: #ffffff;
  border-radius: 16rpx;
  padding: 30rpx 0;
  margin-bottom: 30rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
  position: relative;
  box-sizing: border-box;
  width: 100%;

  .menu-item {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    position: relative;
    
    &:not(:last-child)::after {
      content: '';
      position: absolute;
      right: 0;
      top: 10rpx;
      bottom: 10rpx;
      width: 1rpx;
      background: rgba(0, 0, 0, 0.1);
    }

    .icon-wrapper {
      width: 120rpx;
      height: 120rpx;
      background: rgba(230, 244, 255, 0.8);
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
        width: 52rpx;
        height: 52rpx;
      }
    }

    .title {
      font-size: 32rpx;
      font-weight: 600;
      color: #222;
      margin-bottom: 8rpx;
      text-align: center;
      width: 100%;
    }

    .subtitle {
      font-size: 24rpx;
      font-weight: 500;
      color: #555;
      text-align: center;
      width: 100%;
    }
  }
}

/* 功能区域 */
.feature-section {
  display: flex;
  background: #ffffff;
  border-radius: 16rpx;
  padding: 30rpx 0;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
  position: relative;
  box-sizing: border-box;
  width: 100%;

  .feature-item {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    position: relative;
    
    &:not(:last-child)::after {
      content: '';
      position: absolute;
      right: 0;
      top: 10rpx;
      bottom: 10rpx;
      width: 1rpx;
      background: rgba(0, 0, 0, 0.1);
    }

    .icon-wrapper {
      width: 90rpx;
      height: 90rpx;
      background: rgba(230, 244, 255, 0.8);
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

    .title {
      font-size: 28rpx;
      font-weight: 600;
      color: #222;
      margin-bottom: 6rpx;
      text-align: center;
      width: 100%;
    }

    .subtitle {
      font-size: 22rpx;
      font-weight: 500;
      color: #555;
      text-align: center;
      width: 100%;
    }
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