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
    address: '贵阳市 | 贵州省贵阳市花溪区数字经济产业园3号楼2楼阿里创新中心',
    contact: '廖先生',
    phone: '15085968569'
  },
  {
    id: 2,
    isDefault: false,
    address: '贵阳市 | 贵州省贵阳市花溪区数字经济产业园3号楼2楼阿里创新中心',
    contact: '廖先生',
    phone: '15085968569'
  },
  {
    id: 3,
    isDefault: false,
    address: '贵阳市 | 贵州省贵阳市花溪区数字经济产业园3号楼2楼阿里创新中心',
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
  <view class="layout">
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

    <!-- 主内容区域 -->
    <view class="content">
      <view class="address-list">
        <view 
          class="address-item" 
          v-for="item in addressList" 
          :key="item.id"
        >
          <view class="main-content">
            <view class="location-tag" v-if="item.isDefault">当前城市</view>
            <view class="address-detail">{{ item.address }}</view>
            <view class="contact-info">
              <text class="name">{{ item.contact }}</text>
              <text class="phone">{{ item.phone }}</text>
            </view>
          </view>
          <view class="edit-btn" @tap="() => handleEditAddress(item.id)">
            <image src="/static/address/edit.png" class="edit-icon" />
          </view>
        </view>
      </view>
    </view>

    <!-- 底部按钮 -->
    <view class="bottom-bar">
      <view class="wechat-import">
        <image src="/static/address/wechat.png" class="wechat-icon" />
        <text>微信导入</text>
      </view>
      <view class="add-btn" @tap="handleAddAddress">
        <text>添加地址</text>
      </view>
    </view>
  </view>
</template>

<style scoped lang="scss">
.layout {
  min-height: 100vh;
  background: #f6f6f6;
  display: flex;
  flex-direction: column;
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
  padding: 0 30rpx;
  box-sizing: border-box;
  position: relative;
  z-index: 100;

  .back {
    width: 60rpx;
    height: 60rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    
    .back-icon {
      font-size: 36rpx;
      color: #333;
    }
  }

  .title {
    font-size: 32rpx;
    font-weight: 600;
    color: #333;
  }

  .placeholder {
    width: 60rpx;
  }
}

.content {
  flex: 1;
  padding: 20rpx 30rpx;
  box-sizing: border-box;
  overflow-y: auto;
}

.address-item {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);

  .main-content {
    flex: 1;
    margin-right: 20rpx;
  }

  .location-tag {
    display: inline-block;
    font-size: 22rpx;
    color: #1296db;
    background: rgba(18, 150, 219, 0.1);
    padding: 4rpx 16rpx;
    border-radius: 20rpx;
    margin-bottom: 16rpx;
  }

  .address-detail {
    font-size: 28rpx;
    color: #333;
    line-height: 1.5;
    margin-bottom: 16rpx;
  }

  .contact-info {
    font-size: 26rpx;
    color: #666;
    
    .name {
      margin-right: 20rpx;
    }
    
    .phone {
      color: #999;
    }
  }

  .edit-btn {
    width: 40rpx;
    height: 40rpx;
    padding: 10rpx;
    
    .edit-icon {
      width: 100%;
      height: 100%;
    }
  }
}

.bottom-bar {
  width: 100%;
  padding: 20rpx 30rpx;
  background: #fff;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-sizing: border-box;

  .wechat-import {
    display: flex;
    align-items: center;
    padding: 20rpx 30rpx;
    background: #f8f8f8;
    border-radius: 40rpx;
    
    .wechat-icon {
      width: 40rpx;
      height: 40rpx;
      margin-right: 10rpx;
    }
    
    text {
      font-size: 28rpx;
      color: #333;
    }
  }

  .add-btn {
    flex: 1;
    margin-left: 20rpx;
    height: 80rpx;
    background: linear-gradient(135deg, #1296db, #0f85c7);
    border-radius: 40rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    font-size: 28rpx;
    box-shadow: 0 4rpx 12rpx rgba(18, 150, 219, 0.2);

    &:active {
      transform: scale(0.98);
      box-shadow: 0 2rpx 6rpx rgba(18, 150, 219, 0.2);
    }
  }
}
</style> 