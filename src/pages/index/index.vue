<script setup>
import { getCouponListAPI } from '@/api/coupon'
import { onShow } from '@dcloudio/uni-app'
import { onMounted, ref } from "vue"

const userInfo = ref(null)
const statusBarHeight = ref(0)
const showMemberCode = ref(false)
const couponList = ref([])
const isLoading = ref(true)

// 获取优惠券列表
const getCouponList = async () => {
  const res = await getCouponListAPI()
  couponList.value = res.data
  if(res.code === 200){
    isLoading.value = false
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
  isLoading.value = true
  
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

</script>

<template>
  <view class="layout">
    <!-- 骨架屏组件 -->
    <view class="skeleton" v-if="isLoading">
      <!-- 骨架屏轮播图 -->
      <view class="skeleton-swiper"></view>
      
      <!-- 骨架屏内容区域 -->
      <view class="skeleton-content">
        <!-- 骨架屏用户信息 -->
        <view class="skeleton-user">
          <view class="skeleton-avatar"></view>
          <view class="skeleton-info">
            <view class="skeleton-line skeleton-name"></view>
            <view class="skeleton-line skeleton-text"></view>
          </view>
          <view class="skeleton-qrcode"></view>
        </view>
        
        <!-- 骨架屏菜单区域 -->
        <view class="skeleton-menu">
          <view class="skeleton-menu-item">
            <view class="skeleton-icon"></view>
            <view class="skeleton-line skeleton-title"></view>
            <view class="skeleton-line skeleton-subtitle"></view>
          </view>
          <view class="skeleton-menu-item">
            <view class="skeleton-icon"></view>
            <view class="skeleton-line skeleton-title"></view>
            <view class="skeleton-line skeleton-subtitle"></view>
          </view>
        </view>
        
        <!-- 骨架屏功能区域 -->
        <view class="skeleton-feature">
          <view class="skeleton-feature-item">
            <view class="skeleton-icon-small"></view>
            <view class="skeleton-line skeleton-title-small"></view>
            <view class="skeleton-line skeleton-subtitle-small"></view>
          </view>
          <view class="skeleton-feature-item">
            <view class="skeleton-icon-small"></view>
            <view class="skeleton-line skeleton-title-small"></view>
            <view class="skeleton-line skeleton-subtitle-small"></view>
          </view>
          <view class="skeleton-feature-item">
            <view class="skeleton-icon-small"></view>
            <view class="skeleton-line skeleton-title-small"></view>
            <view class="skeleton-line skeleton-subtitle-small"></view>
          </view>
        </view>
      </view>
    </view>

    <!-- 正常内容 -->
    <template v-else>
      <!-- 轮播图部分 - 完全覆盖顶部 -->
      <swiper class="swiper" circular autoplay interval="3000" duration="500">
        <swiper-item v-for="(item, index) in bannerList" :key="index">
          <image :src="item.image" mode="aspectFill" class="swiper-image" />
        </swiper-item>
      </swiper>
      
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
            <text class="remind"  @tap="handleShowCouponList" v-if="couponList.length > 0">有{{ couponList.length }}张优惠券未使用，立即查看></text>
            <text class="remind"  v-else>暂无优惠券可用哦</text>
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
    </template>
  </view>
</template>

<style scoped lang="scss">
/* 骨架屏样式 */
@keyframes shimmer {
  0% {
    background-position: -400rpx 0;
  }
  100% {
    background-position: 400rpx 0;
  }
}

.skeleton {
  width: 100%;
  height: 100vh;
  overflow: hidden;
  box-sizing: border-box;
  position: relative;
  display: flex;
  flex-direction: column;
  background: #f8f8f8;
  
  .skeleton-swiper {
    width: 100%;
    height: 580rpx;
    background: linear-gradient(to right, #eee 8%, #ddd 18%, #eee 33%);
    background-size: 800rpx 100%;
    animation: shimmer 1.5s infinite linear;
  }
  
  .skeleton-content {
    flex: 1;
    padding: 20rpx 30rpx 30rpx;
    margin-top: -100rpx;
    position: relative;
    
    .skeleton-user {
      display: flex;
      flex-direction: row;
      align-items: center;
      background: #fff;
      height: 150rpx;
      border-radius: 16rpx;
      margin-bottom: 30rpx;
      padding: 30rpx;
      box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
      box-sizing: border-box;
      
      .skeleton-avatar {
        width: 80rpx;
        height: 80rpx;
        border-radius: 50%;
        margin-right: 20rpx;
        background: linear-gradient(to right, #eee 8%, #ddd 18%, #eee 33%);
        background-size: 800rpx 100%;
        animation: shimmer 1.5s infinite linear;
      }
      
      .skeleton-info {
        flex: 1;
        
        .skeleton-line {
          background: linear-gradient(to right, #eee 8%, #ddd 18%, #eee 33%);
          background-size: 800rpx 100%;
          animation: shimmer 1.5s infinite linear;
          border-radius: 4rpx;
        }
        
        .skeleton-name {
          width: 180rpx;
          height: 32rpx;
          margin-bottom: 10rpx;
        }
        
        .skeleton-text {
          width: 240rpx;
          height: 24rpx;
        }
      }
      
      .skeleton-qrcode {
        width: 80rpx;
        height: 80rpx;
        border-radius: 8rpx;
        background: linear-gradient(to right, #eee 8%, #ddd 18%, #eee 33%);
        background-size: 800rpx 100%;
        animation: shimmer 1.5s infinite linear;
      }
    }
    
    .skeleton-menu {
      display: flex;
      background: #fff;
      border-radius: 16rpx;
      padding: 30rpx 0;
      margin-bottom: 30rpx;
      box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
      
      .skeleton-menu-item {
        flex: 1;
        display: flex;
        flex-direction: column;
        align-items: center;
        
        .skeleton-icon {
          width: 120rpx;
          height: 120rpx;
          border-radius: 50%;
          background: linear-gradient(to right, #eee 8%, #ddd 18%, #eee 33%);
          background-size: 800rpx 100%;
          animation: shimmer 1.5s infinite linear;
          margin-bottom: 20rpx;
        }
        
        .skeleton-line {
          background: linear-gradient(to right, #eee 8%, #ddd 18%, #eee 33%);
          background-size: 800rpx 100%;
          animation: shimmer 1.5s infinite linear;
          border-radius: 4rpx;
        }
        
        .skeleton-title {
          width: 100rpx;
          height: 32rpx;
          margin-bottom: 8rpx;
        }
        
        .skeleton-subtitle {
          width: 140rpx;
          height: 24rpx;
        }
      }
    }
    
    .skeleton-feature {
      display: flex;
      background: #fff;
      border-radius: 16rpx;
      padding: 30rpx 0;
      box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
      
      .skeleton-feature-item {
        flex: 1;
        display: flex;
        flex-direction: column;
        align-items: center;
        
        .skeleton-icon-small {
          width: 90rpx;
          height: 90rpx;
          border-radius: 50%;
          background: linear-gradient(to right, #eee 8%, #ddd 18%, #eee 33%);
          background-size: 800rpx 100%;
          animation: shimmer 1.5s infinite linear;
          margin-bottom: 16rpx;
        }
        
        .skeleton-line {
          background: linear-gradient(to right, #eee 8%, #ddd 18%, #eee 33%);
          background-size: 800rpx 100%;
          animation: shimmer 1.5s infinite linear;
          border-radius: 4rpx;
        }
        
        .skeleton-title-small {
          width: 80rpx;
          height: 28rpx;
          margin-bottom: 6rpx;
        }
        
        .skeleton-subtitle-small {
          width: 120rpx;
          height: 22rpx;
        }
      }
    }
  }
}

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

.swiper {
  width: 100%;
  height: 580rpx;
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1;
  
  .swiper-image {
    width: 100%;
    height: 100%;
  }
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
  margin-top: 480rpx;
  position: relative;
  z-index: 3;
}

/* 用户信息区域 - 调整为更贴近图片的效果 */
.login-section {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  background: #ffffff;
  height: 100rpx;
  border-radius: 16rpx;
  margin-bottom: 30rpx;
  padding: 0 24rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
  position: relative;
  box-sizing: border-box;

  .slogan {
    font-size: 28rpx;
    font-weight: 500;
    color: #333;
  }

  .login-btn {
    min-width: 140rpx;
    height: 60rpx;
    background: linear-gradient(135deg, #1296db, #0f85c7);
    border-radius: 30rpx;
    padding: 0 24rpx;
    font-size: 24rpx;
    display: flex;
    justify-content: center;
    align-items: center;
    color: #fff;
    font-weight: 500;
    box-shadow: 0 4rpx 12rpx rgba(18, 150, 219, 0.2);
    transition: all 0.3s ease;

    &:active {
      transform: scale(0.98);
      box-shadow: 0 2rpx 6rpx rgba(18, 150, 219, 0.2);
    }
  }
}

/* 用户信息区域 */
.user-section {
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