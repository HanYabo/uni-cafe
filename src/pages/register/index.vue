<script setup>
import { register } from '@/api/user'
import { computed, ref } from 'vue'

const form = ref({
  mobile: '',
  password: '',
  confirmPassword: ''
})

// 添加密码校验相关的响应式变量
const passwordError = ref('')
const showPasswordError = ref(false)

// 计算密码是否匹配
const isPasswordMatch = computed(() => {
  return form.value.password === form.value.confirmPassword
})

// 监听密码变化
const handlePasswordChange = () => {
  if (form.value.confirmPassword) {
    showPasswordError.value = true
    if (!isPasswordMatch.value) {
      passwordError.value = '两次密码输入不一致'
    } else {
      passwordError.value = ''
    }
  }
}

// 监听确认密码变化
const handleConfirmPasswordChange = () => {
  showPasswordError.value = true
  if (!isPasswordMatch.value) {
    passwordError.value = '两次密码输入不一致'
  } else {
    passwordError.value = ''
  }
}

// 表单验证
const validateForm = () => {
  if (!form.value.mobile) {
    uni.showToast({
      title: '请输入手机号',
      icon: 'none'
    })
    return false
  }

  if (!/^1[3-9]\d{9}$/.test(form.value.mobile)) {
    uni.showToast({
      title: '手机号格式不正确',
      icon: 'none'
    })
    return false
  }

  if (!form.value.password) {
    uni.showToast({
      title: '请设置密码',
      icon: 'none'
    })
    return false
  }

  if (form.value.password.length < 6) {
    uni.showToast({
      title: '密码不能少于6位',
      icon: 'none'
    })
    return false
  }

  if (!isPasswordMatch.value) {
    uni.showToast({
      title: '两次密码输入不一致',
      icon: 'none'
    })
    return false
  }

  return true
}

// 提交注册
const handleSubmit = async () => {
  if (!validateForm()) return

    uni.showLoading({
      title: '注册中...'
    })

    const data = {
      mobile: form.value.mobile,
      password: form.value.password
    }

    const res = await register(data)

    if (res.code === 200) {
      uni.setStorageSync('userInfo', res.data)
      uni.setStorageSync('token', res.token)
      uni.hideLoading()
      uni.showToast({
        title: '注册成功',
        icon: 'success'
      })

      // 直接跳转到首页
      setTimeout(() => {
        uni.switchTab({
          url: '/pages/index/index'
        })
      }, 1500)
    }else {
      uni.hideLoading()
      uni.showToast({
        title: res.message || '注册失败',
        icon: 'none'
      })
    }
  } 

// 返回登录页
const handleLogin = () => {
  uni.navigateBack()
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
          <input type="number" v-model="form.mobile" maxlength="11" placeholder="请输入手机号" class="input-item" />
        </view>
        <view class="input-wrapper">
          <text class="iconfont icon-lock input-icon"></text>
          <input 
            type="password" 
            v-model="form.password" 
            placeholder="请设置密码" 
            class="input-item" 
            @input="handlePasswordChange"
          />
        </view>
        <view class="input-wrapper">
          <text class="iconfont icon-lock input-icon"></text>
          <input 
            type="password" 
            v-model="form.confirmPassword" 
            placeholder="请确认密码" 
            class="input-item" 
            @input="handleConfirmPasswordChange"
          />
        </view>
        <!-- 添加错误提示 -->
        <view class="error-message" v-if="showPasswordError && passwordError">
          <text class="error-text">{{ passwordError }}</text>
        </view>
      </view>

      <view class="btn-group">
        <button @tap="handleSubmit" class="register-btn primary-btn" hover-class="button-hover">注册</button>
      </view>

      <view class="additional-links">
        <text class="link-text" @tap="handleLogin">已有账号？立即登录</text>
      </view>

      <view class="register-footer">
        <text class="footer-text">注册即代表同意</text>
        <text class="footer-link">用户协议</text>
        <text class="footer-text">和</text>
        <text class="footer-link">隐私政策</text>
      </view>
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
  margin-bottom: 12rpx; // 减小底部间距，为错误提示留出空间
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
  margin-bottom: 30rpx;
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
  left: 0;
  right: 0;
  bottom: 60rpx;
  text-align: center;
  font-size: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;

  .footer-text {
    color: $uni-text-color-grey;
  }

  .footer-link {
    color: $uni-color-primary;
    margin: 0 8rpx;
  }
}

// 添加错误提示样式
.error-message {
  padding: 8rpx 30rpx;
  margin-bottom: 24rpx;
  
  .error-text {
    font-size: 24rpx;
    color: #ff4d4f;
  }
}
</style>