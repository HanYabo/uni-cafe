<template>
  <view class="login-container">
    <view class="login-box">
      <view class="login-header">
        <view class="login-title">欢迎回来</view>
        <view class="login-subtitle">请登录您的账号</view>
      </view>
      
      <view class="input-group">
        <view class="input-wrapper">
          <text class="iconfont icon-phone input-icon"></text>
          <input 
            type="number" 
            v-model="phone" 
            maxlength="11"
            placeholder="请输入手机号"
            class="input-item"
          />
        </view>
        <view class="input-wrapper">
          <text class="iconfont icon-lock input-icon"></text>
          <input 
            type="password" 
            v-model="password"
            placeholder="请输入密码"
            class="input-item"
          />
        </view>
      </view>

      <view class="btn-group">
        <button @click="handleLogin" class="login-btn primary-btn" hover-class="button-hover">登录</button>
        <button @click="handleWechatLogin" class="login-btn wechat-btn" hover-class="button-hover">
          <text class="iconfont icon-wechat"></text>
          微信一键登录
        </button>
      </view>

      <view class="additional-links">
        <text class="link-text">忘记密码</text>
        <text class="link-text" @click="handleRegister">注册账号</text>
      </view>
    </view>

    <view class="login-footer">
      <text class="footer-text">登录即代表同意</text>
      <text class="footer-link">用户协议</text>
      <text class="footer-text">和</text>
      <text class="footer-link">隐私政策</text>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'

// 响应式状态
const phone = ref('')
const password = ref('')

// 登录
const handleLogin = () => {
  if (!phone.value || !password.value) {
    uni.showToast({
      title: '请输入手机号和密码',
      icon: 'none'
    })
    return
  }
  // TODO: 实现登录逻辑
  console.log('登录', phone.value, password.value)
}

// 注册
const handleRegister = () => {
  uni.navigateTo({
    url: '/pages/register/index'
  })
}

const handleWechatLogin = () => {
  // TODO: 实现微信登录逻辑
  console.log('微信登录')
  uni.login({
    provider: 'weixin',
    success: (res) => {
      console.log('微信登录成功', res)
      // 调用微信接口获取用户信息
      uni.getUserInfo({
        provider: 'weixin',
        success: (res) => {
          console.log('微信用户信息', res)
        }
      })
    },
    fail: (err) => {
      console.log('微信登录失败', err)
    }
  })
}
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  background-color: $uni-bg-color;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40rpx;
  box-sizing: border-box;
  position: relative;
}

.login-box {
  width: 100%;
  max-width: 600rpx;
  padding: 60rpx 40rpx;
  border-radius: 24rpx;
  background-color: #fff;
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.08);
}

.login-header {
  text-align: center;
  margin-bottom: 60rpx;
}

.login-title {
  font-size: 48rpx;
  color: $uni-text-color;
  font-weight: bold;
  margin-bottom: 16rpx;
}

.login-subtitle {
  font-size: 28rpx;
  color: $uni-text-color-grey;
}

.input-group {
  margin-bottom: 50rpx;
}

.input-wrapper {
  position: relative;
  margin-bottom: 24rpx;
  display: flex;
  align-items: center;
  background-color: $uni-bg-color-grey;
  border-radius: 12rpx;
  padding: 0 30rpx;
  transition: all 0.3s ease;

  &:focus-within {
    background-color: #fff;
    box-shadow: 0 0 0 2rpx $uni-color-primary;
  }
}

.input-icon {
  font-size: 36rpx;
  color: $uni-text-color-grey;
  margin-right: 20rpx;
}

.input-item {
  flex: 1;
  height: 90rpx;
  font-size: 28rpx;
  background: transparent;
  box-sizing: border-box;
  
  &::placeholder {
    color: $uni-text-color-placeholder;
  }
}

.btn-group {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
  margin-bottom: 40rpx;
}

.login-btn {
  width: 100%;
  height: 90rpx;
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  border: none;
  transition: all 0.3s ease;
}

.primary-btn {
  background-color: $uni-color-primary;
  color: #fff;
  
  &.button-hover {
    opacity: 0.9;
    transform: translateY(2rpx);
  }
}

.wechat-btn {
  background-color: #07c160;
  color: #fff;
  
  &.button-hover {
    opacity: 0.9;
    transform: translateY(2rpx);
  }
  
  .icon-wechat {
    margin-right: 12rpx;
    font-size: 36rpx;
  }
}

.additional-links {
  display: flex;
  justify-content: space-between;
  padding: 0 20rpx;
}

.link-text {
  font-size: 26rpx;
  color: $uni-color-primary;
}

.login-footer {
  position: absolute;
  bottom: 60rpx;
  text-align: center;
  font-size: 24rpx;
}

.footer-text {
  color: $uni-text-color-grey;
}

.footer-link {
  color: $uni-color-primary;
  margin: 0 8rpx;
}
</style> 