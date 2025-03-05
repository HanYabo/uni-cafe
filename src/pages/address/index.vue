# 创建新的地址管理页面
<script setup>
import { onShow } from '@dcloudio/uni-app'
import { ref } from 'vue'

const statusBarHeight = ref(0)
const navBarHeight = ref(44) // 导航栏固定高度
const addressList = ref([
  {
    id: 1,
    isDefault: true,
    city: '贵阳市',
    address: '贵州省贵阳市花溪区数字经济产业园3号楼2楼阿里创新中心',
    contact: '廖先生',
    phone: '15085968569'
  },
  {
    id: 2,
    isDefault: false,
    city: '贵阳市',
    address: '贵州省贵阳市花溪区数字经济产业园3号楼2楼阿里创新中心',
    contact: '廖先生',
    phone: '15085968569'
  },
  {
    id: 3,
    isDefault: false,
    city: '贵阳市',
    address: '贵州省贵阳市花溪区数字经济产业园3号楼2楼阿里创新中心',
    contact: '廖先生',
    phone: '15085968569'
  }
])

onShow(() => {
  const systemInfo = uni.getSystemInfoSync()
  statusBarHeight.value = systemInfo.statusBarHeight || 0
})

const handleAddAddress = () => {
  uni.navigateTo({
    url: '/pages/address/edit'
  })
}

const handleEditAddress = (id) => {
  uni.navigateTo({
    url: `/pages/address/edit?id=${id}`
  })
}
</script>

<template>
  <view class="address-container">
    <!-- 状态栏占位 -->
    <view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
    
    <!-- 导航栏 -->
    <view class="nav-bar" :style="{ height: navBarHeight + 'px' }">
      <view class="back" @tap="() => uni.navigateBack()">
        <text class="back-icon">〈</text>
      </view>
      <text class="title">收货地址</text>
      <view class="placeholder"></view>
    </view>

    <!-- 地址列表 -->
    <scroll-view scroll-y class="address-list">
      <view class="address-item" v-for="(address, index) in addressList" :key="index">
        <view class="address-content">
          <view class="location-info">
            <view class="tag" v-if="address.isDefault">默认</view>
            <text class="address-text">{{ address.city }} | {{ address.address }}</text>
          </view>
          <view class="contact-info">
            <text class="name">{{ address.contact }}</text>
            <text class="phone">{{ address.phone }}</text>
          </view>
        </view>
        <view class="edit-btn" @tap="() => handleEditAddress(address.id)">
          <text>修改</text>
        </view>
      </view>
    </scroll-view>

    <!-- 底部按钮组 -->
    <view class="bottom-buttons">
      <button class="wechat-btn">微信导入</button>
      <button class="add-btn" @tap="handleAddAddress">新增地址</button>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.address-container {
  min-height: 100vh;
  background: #f7f7f7;
  position: relative;
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

.address-list {
  padding: 12px 16px;
  box-sizing: border-box;
  height: calc(100vh - 44px - var(--status-bar-height) - 80px);

  .address-item {
    background: #fff;
    border-radius: 8px;
    padding: 16px;
    margin-bottom: 12px;
    display: flex;
    align-items: center;
    min-height: 80px;
    
    .address-content {
      flex: 1;
      margin-right: 12px;
      
      .location-info {
        display: flex;
        align-items: center;
        gap: 8px;
        
        .tag {
          font-size: 10px;
          color: #1296db;
          background: rgba(18, 150, 219, 0.1);
          padding: 2px 6px;
          border-radius: 4px;
          flex-shrink: 0;
        }
        
        .address-text {
          font-size: 14px;
          color: #333;
          line-height: 1.4;
        }
      }
      
      .contact-info {
        font-size: 12px;
        color: #999;
        display: flex;
        align-items: center;
        margin-top: 8px;
        
        .name {
          margin-right: 12px;
        }
        
        .phone {
          color: #999;
        }
      }
    }
    
    .edit-btn {
      padding: 4px 12px;
      font-size: 12px;
      color: #666;
      align-self: center;
      flex-shrink: 0;
      border: 1px solid #ddd;
      border-radius: 12px;
    }
  }
}

.bottom-buttons {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 16px;
  padding-bottom: calc(16px + env(safe-area-inset-bottom));
  background: #fff;
  display: flex;
  gap: 12px;
  box-shadow: 0 -4px 16px rgba(0, 0, 0, 0.04);

  .wechat-btn, .add-btn {
    flex: 1;
    height: 40px;
    border-radius: 20px;
    font-size: 14px;
    font-weight: 500;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0;
    margin: 0;
    border: none;
    box-sizing: border-box;
  }

  .wechat-btn {
    background: #f8f8f8;
    color: #333;
    position: relative;

    &::after {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      width: 200%;
      height: 200%;
      border: 1px solid rgba(0, 0, 0, 0.1);
      border-radius: 40px;
      transform: scale(0.5);
      transform-origin: 0 0;
      box-sizing: border-box;
    }
  }

  .add-btn {
    background: #1296db;
    color: #fff;
  }

  .btn-hover {
    opacity: 0.8;
  }
}
</style> 