<template>
  <view class="login-container">
    <view class="login-box">
      <view class="login-title">欢迎登录</view>
      
      <view class="input-group">
        <input 
          type="number" 
          v-model="phone" 
          maxlength="11"
          placeholder="请输入手机号"
          class="input-item"
        />
        <input 
          type="password" 
          v-model="password"
          placeholder="请输入密码"
          class="input-item"
        />
      </view>

      <view class="btn-group">
        <button @click="handleLogin" class="login-btn primary-btn">登录</button>
        <button @click="handleWechatLogin" class="login-btn wechat-btn">
          <text class="iconfont icon-wechat"></text>
          微信一键登录
        </button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'

// 响应式状态
const phone = ref('')
const password = ref('')

// 方法
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

const handleWechatLogin = () => {
  // TODO: 实现微信登录逻辑
  console.log('微信登录')
}
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  background-color: $uni-bg-color;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 30rpx;
  box-sizing: border-box;
}

.login-box {
  width: 100%;
  background-color: #fff;
  border-radius: 20rpx;
  padding: 40rpx 30rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
}

.login-title {
  font-size: 40rpx;
  color: $uni-text-color;
  font-weight: bold;
  text-align: center;
  margin-bottom: 50rpx;
}

.input-group {
  margin-bottom: 40rpx;
}

.input-item {
  width: 100%;
  height: 90rpx;
  background-color: $uni-bg-color-grey;
  border-radius: 12rpx;
  padding: 0 30rpx;
  margin-bottom: 20rpx;
  font-size: 28rpx;
  box-sizing: border-box;
}

.btn-group {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
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
}

.primary-btn {
  background-color: $uni-color-primary;
  color: #fff;
}

.wechat-btn {
  background-color: #07c160;
  color: #fff;
  
  .icon-wechat {
    margin-right: 10rpx;
  }
}
</style> 