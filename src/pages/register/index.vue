<script setup>
import { ref } from 'vue'

// 响应式状态
const phone = ref('')
const password = ref('')
const confirmPassword = ref('')

// 方法
const handleRegister = () => {
  if (!phone.value || !password.value || !confirmPassword.value) {
    uni.showToast({
      title: '请填写完整信息',
      icon: 'none'
    })
    return
  }

  if (password.value !== confirmPassword.value) {
    uni.showToast({
      title: '两次输入的密码不一致',
      icon: 'none'
    })
    return
  }

  // TODO: 实现注册逻辑
  console.log('注册', phone.value, password.value)
}

const goToLogin = () => {
  uni.navigateTo({
    url: '/pages/login/index'
  })
}
</script>

<template>
  <view class="register-container">
    <view class="register-box">
      <view class="register-header">
        <view class="register-title">欢迎加入</view>
        <view class="register-subtitle">创建您的账号</view>
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
        <view class="input-wrapper">
          <text class="iconfont icon-lock input-icon"></text>
          <input 
            type="password" 
            v-model="confirmPassword"
            placeholder="请确认密码"
            class="input-item"
          />
        </view>
      </view>

      <view class="btn-group">
        <button @click="handleRegister" class="register-btn primary-btn" hover-class="button-hover">注册</button>
      </view>

      <view class="additional-links">
        <text class="link-text" @click="goToLogin">已有账号？立即登录</text>
      </view>
    </view>

    <view class="register-footer">
      <text class="footer-text">注册即代表同意</text>
      <text class="footer-link">用户协议</text>
      <text class="footer-text">和</text>
      <text class="footer-link">隐私政策</text>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.register-container {
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

.register-box {
  width: 100%;
  max-width: 600rpx;
  padding: 60rpx 40rpx;
  border-radius: 24rpx;
  background-color: #fff;
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.08);
}

.register-header {
  text-align: center;
  margin-bottom: 60rpx;
}

.register-title {
  font-size: 48rpx;
  color: $uni-text-color;
  font-weight: bold;
  margin-bottom: 16rpx;
}

.register-subtitle {
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

.register-btn {
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
  background: linear-gradient(135deg, #1296db, #0f85c7);
  color: #fff;
  box-shadow: 0 4rpx 12rpx rgba(18, 150, 219, 0.2);
  
  &.button-hover {
    opacity: 0.9;
    transform: translateY(2rpx);
  }
}

.additional-links {
  display: flex;
  justify-content: center;
  padding: 0 20rpx;
}

.link-text {
  font-size: 26rpx;
  color: $uni-color-primary;
}

.register-footer {
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