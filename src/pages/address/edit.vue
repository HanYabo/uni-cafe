<script setup>
import { ref } from 'vue'

const statusBarHeight = ref(0)
const navBarHeight = ref(44)

const formData = ref({
  contact: '',
  gender: '先生',
  phone: '',
  address: '',
  detail: '',
  isDefault: false
})

onShow(() => {
  const systemInfo = uni.getSystemInfoSync()
  statusBarHeight.value = systemInfo.statusBarHeight || 0
})

const handleSave = () => {
  // TODO: 表单验证和保存逻辑
  uni.navigateBack()
}

const handleClearPhone = () => {
  formData.value.phone = ''
}

const handleSelectAddress = () => {
  // TODO: 调用地址选择
}
</script>

<template>
  <view class="edit-container">
    <!-- 状态栏占位 -->
    <view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>

    <!-- 表单内容 -->
    <view class="form-content">
      <!-- 联系人 -->
      <view class="form-item">
        <text class="label">联系人</text>
        <input 
          class="input" 
          type="text" 
          v-model="formData.contact"
          placeholder="收货人姓名"
          placeholder-class="placeholder"
        />
      </view>

      <!-- 性别选择 -->
      <view class="form-item">
        <text class="label">性别</text>
        <view class="gender-group">
          <view 
            class="gender-option" 
            :class="{ active: formData.gender === '先生' }"
            @tap="formData.gender = '先生'"
          >
            先生
          </view>
          <view 
            class="gender-option" 
            :class="{ active: formData.gender === '女士' }"
            @tap="formData.gender = '女士'"
          >
            女士
          </view>
        </view>
      </view>

      <!-- 手机号 -->
      <view class="form-item">
        <text class="label">手机号</text>
        <view class="input-wrapper">
          <input 
            class="input" 
            type="number" 
            v-model="formData.phone"
            placeholder="收货人手机号"
            placeholder-class="placeholder"
            maxlength="11"
          />
          <text v-if="formData.phone" class="clear-btn" @tap="handleClearPhone">×</text>
        </view>
      </view>

      <!-- 收货地址 -->
      <view class="form-item" @tap="handleSelectAddress">
        <text class="label">收货地址</text>
        <view class="address-select">
          <text class="address-text">{{ formData.address || '选择收货地址' }}</text>
          <text class="arrow">></text>
        </view>
      </view>

      <!-- 门牌号 -->
      <view class="form-item">
        <text class="label">门牌号</text>
        <input 
          class="input" 
          type="text" 
          v-model="formData.detail"
          placeholder="例如：B座6楼602室"
          placeholder-class="placeholder"
        />
      </view>

      <!-- 设为默认 -->
      <view class="form-item default-item">
        <text class="label">设为默认地址</text>
        <switch 
          :checked="formData.isDefault" 
          @change="e => formData.isDefault = e.detail.value"
          color="#1296db"
        />
      </view>
    </view>

    <!-- 保存按钮 -->
    <view class="save-btn" @tap="handleSave">保存</view>
  </view>
</template>

<style lang="scss" scoped>
.edit-container {
  min-height: 100vh;
  background: #f7f7f7;
}

.status-bar {
  background: #fff;
  width: 100%;
}

.nav-bar {
  background: #fff;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  box-sizing: border-box;
  position: relative;
  z-index: 100;
  box-shadow: 0 1px 0 rgba(0, 0, 0, 0.05);

  .back {
    width: 32px;
    height: 32px;
    display: flex;
    align-items: center;
    justify-content: center;

    .back-icon {
      font-size: 18px;
      color: #333;
    }
  }

  .title {
    font-size: 16px;
    font-weight: 500;
    color: #333;
  }

  .placeholder {
    width: 32px;
  }
}

.form-content {
  padding: 12px 0;
  background: #fff;

  .form-item {
    display: flex;
    align-items: center;
    padding: 15px 16px;
    position: relative;

    &:not(:last-child)::after {
      content: '';
      position: absolute;
      left: 16px;
      right: 16px;
      bottom: 0;
      height: 0.5px;
      background: #f0f0f0;
      transform: scaleY(0.5);
    }

    .label {
      width: 76px;
      font-size: 15px;
      color: #333;
      flex-shrink: 0;
    }

    &.default-item {
      display: flex;
      align-items: center;
      padding-right: 12px;

      .label {
        width: auto;
        white-space: nowrap;
      }

      switch {
        margin-left: auto;
      }
    }

    .input {
      flex: 1;
      font-size: 14px;
      color: #333;
    }

    .input-wrapper {
      flex: 1;
      display: flex;
      align-items: center;

      .clear-btn {
        padding: 4px 8px;
        color: #999;
        font-size: 16px;
      }
    }

    .gender-group {
      flex: 1;
      display: flex;
      gap: 16px;

      .gender-option {
        padding: 6px 20px;
        border: 1px solid #ddd;
        border-radius: 16px;
        font-size: 14px;
        color: #333;

        &.active {
          background: rgba(18, 150, 219, 0.1);
          border-color: #1296db;
          color: #1296db;
        }
      }
    }

    .address-select {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: space-between;

      .address-text {
        font-size: 14px;
        color: #999;
      }

      .arrow {
        color: #999;
        font-size: 14px;
        margin-left: 4px;
      }
    }
  }
}

.placeholder {
  color: #999;
}

.save-btn {
  position: fixed;
  left: 16px;
  right: 16px;
  bottom: calc(16px + env(safe-area-inset-bottom));
  height: 44px;
  background: #1296db;
  color: #fff;
  font-size: 16px;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 22px;
}
</style> 