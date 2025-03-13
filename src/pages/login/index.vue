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
            v-model="form.mobile" 
            maxlength="11"
            placeholder="请输入手机号"
            class="input-item"
          />
        </view>
        <view class="input-wrapper">
          <text class="iconfont icon-lock input-icon"></text>
          <input 
            type="password" 
            v-model="form.password"
            placeholder="请输入密码"
            class="input-item"
          />
        </view>
      </view>

      <view class="btn-group">
        <button @tap="handleSubmit" class="login-btn primary-btn" hover-class="button-hover">登录</button>
        <button @tap="handleWechatLogin" class="login-btn wechat-btn" hover-class="button-hover">
          <text class="iconfont icon-wechat"></text>
          微信一键登录
        </button>
      </view>

      <view class="additional-links">
        <text class="link-text">忘记密码</text>
        <text class="link-text" @tap="handleRegister">注册账号</text>
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
import { login, wechatLogin } from '@/api/user'
import { reactive } from 'vue'

const form = reactive({
  mobile: '',
  password: '',
  confirmPassword: ''
})


// 表单验证
const validateForm = () => {
  if (!form.mobile) {
    uni.showToast({
      title: '请输入手机号',
      icon: 'none'
    })
    return false
  }
  
  if (!/^1[3-9]\d{9}$/.test(form.mobile)) {
    uni.showToast({
      title: '手机号格式不正确',
      icon: 'none'
    })
    return false
  }
  
  if (!form.password) {
    uni.showToast({
      title: '请输入密码',
      icon: 'none'
    })
    return false
  }
  
  return true
}

// 登录表单提交
const handleSubmit = async () => {
  try {
    if (!validateForm()) return
    
    // 显示加载中
    uni.showLoading({
      title: '登录中...'
    })
    
    // 调用登录API
    const res = await login({
      mobile: form.mobile,
      password: form.password
    })
    
    uni.hideLoading()
    
    // 处理登录成功后的操作
    handleLoginSuccess(res.data)
  } catch (error) {
    uni.hideLoading()
    uni.showToast({
      title: error.message || '登录失败',
      icon: 'none'
    })
  }
}

// 微信登录
const handleWechatLogin = async () => {
  try {
    uni.showLoading({
      title: '登录中...'
    })

    // 1. 获取用户授权和信息
    const { userInfo } = await uni.getUserProfile({
      desc: '用于完善用户资料',
      lang: 'zh_CN'
    })

    // 2. 获取登录凭证
    const { code } = await uni.login()
    
    if (!code) {
      throw new Error('微信登录失败')
    }

    // 3. 调用后端接口，发送code和用户信息
    const res = await wechatLogin({
      code,
      nickName: userInfo.nickName,
      avatarUrl: userInfo.avatarUrl
    })

    uni.hideLoading()

    // 4. 构建完整的用户信息
    const finalUserInfo = {
      ...userInfo,
      ...res.data.userInfo // 合并后端返回的用户信息
    }
    
    // 5. 调用统一的登录成功处理方法
    handleLoginSuccess({
      token: res.data.token,
      userInfo: finalUserInfo
    })
    
  } catch (error) {
    uni.hideLoading()
    console.error('微信登录失败', error)
    
    if (error.errMsg?.includes('getUserProfile:fail')) {
      uni.showToast({
        title: '需要您的授权才能继续',
        icon: 'none'
      })
      return
    }
    
    uni.showToast({
      title: error.message || '微信登录失败',
      icon: 'none'
    })
  }
}

const handleRegister = () => {
  uni.navigateTo({
    url: '/pages/register/index'
  })
}

// 处理登录成功后的操作
const handleLoginSuccess = (result) => {
  // 将用户信息和token保存到本地
  uni.setStorageSync('token', result.token)
  uni.setStorageSync('userInfo', result.userInfo)
  
  console.log('登录成功，发送事件通知其他页面')
  
  // 发送登录成功事件，通知其他页面刷新数据
  uni.$emit('loginSuccess')
  
  // 显示登录成功提示
  uni.showToast({
    title: '登录成功',
    icon: 'success',
    mask: true
  })
  
  // 延迟返回上一页
  setTimeout(() => {
    console.log('登录成功，准备返回上一页')
    uni.navigateBack({
      success: () => {
        console.log('成功返回上一页')
        // 再次通知刷新，确保不会错过
        setTimeout(() => {
          uni.$emit('loginSuccess')
        }, 100)
      }
    })
  }, 1000)
}
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  background-color: #f5f5f5;
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
  color: #333;
  font-weight: bold;
  margin-bottom: 16rpx;
}

.login-subtitle {
  font-size: 28rpx;
  color: #666;
}

.input-group {
  margin-bottom: 50rpx;
}

.input-wrapper {
  position: relative;
  margin-bottom: 24rpx;
  display: flex;
  align-items: center;
  background-color: #f5f5f5;
  border-radius: 12rpx;
  padding: 0 30rpx;
  transition: all 0.3s ease;

  &:focus-within {
    background-color: #fff;
    box-shadow: 0 0 0 2rpx #1296db;
  }
}

.input-icon {
  font-size: 36rpx;
  color: #999;
  margin-right: 20rpx;
}

.input-item {
  flex: 1;
  height: 90rpx;
  font-size: 28rpx;
  background: transparent;
  box-sizing: border-box;
  
  &::placeholder {
    color: #999;
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
  background-color: #1296db;
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
  color: #1296db;
}

.login-footer {
  position: absolute;
  bottom: 60rpx;
  text-align: center;
  font-size: 24rpx;
}

.footer-text {
  color: #999;
}

.footer-link {
  color: #1296db;
  margin: 0 8rpx;
}
</style> 