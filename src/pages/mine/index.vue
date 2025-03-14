<script setup>
import { getCouponListAPI } from '@/api/coupon'
import { formatTime } from '@/utils/format'
import { onLoad, onShow, onUnload } from '@dcloudio/uni-app'
import { onMounted, ref } from 'vue'

const statusBarHeight = ref(0)
const userInfo = ref(null)

const getCouponList = async () => {
  const res = await getCouponListAPI()
  if (res.code === 200) {
    coupons.value = res.data
  } else {
    uni.showToast({
      title: '优惠券信息获取失败',
      icon: 'error'
    })
  }
}

onMounted(() => {
  const systemInfo = uni.getSystemInfoSync()
  statusBarHeight.value = systemInfo.statusBarHeight || 0
})

// 修改为一个变量，不是计算属性，这样更容易被手动更新
const isLogin = ref(false)

// 检查登录状态
const checkLoginStatus = () => {
  const info = uni.getStorageSync('userInfo')
  const token = uni.getStorageSync('token')
  userInfo.value = info || null
  
  // 更新登录状态变量
  isLogin.value = !!token
  
  // 只有在有token的情况下才获取优惠券
  if (token) {
    getCouponList()
  } else {
    coupons.value = []
  }
}

// 页面显示时检查登录状态并获取信息
onShow(() => {
  checkLoginStatus()
})

// 监听登录成功事件
const refreshAfterLogin = () => {
  // 立即更新登录状态并获取优惠券
  checkLoginStatus()
}

// 页面加载时添加事件监听
onLoad(() => {
  uni.$on('loginSuccess', refreshAfterLogin)
})

// 页面卸载时移除事件监听，避免内存泄漏
onUnload(() => {
  uni.$off('loginSuccess', refreshAfterLogin)
})

const handleLogin = () => {
  wx.navigateTo({
    url: '/pages/login/index'
  })
}

const handleToAddress = () => {
  uni.navigateTo({
    url: '/pages/address/index'
  })
}

// 优惠券数据
const coupons = ref([])

// 无门槛优惠券优惠金额

const handleAddressClick = () => {
  if (!isLogin.value) {
    uni.showToast({
      title: '请先登录',
      icon: 'none'
    })
    return
  }
  uni.navigateTo({
    url: '/pages/address/index'
  })
}

// 跳转到优惠券页面
const goToCouponPage = () => {
  if (!isLogin.value) {
    uni.showToast({
      title: '请先登录',
      icon: 'none'
    })
    return
  }
  uni.navigateTo({
    url: '/pages/coupon/index'
  })
}
</script>

<template>
  <view class="layout" :style="{ paddingTop: statusBarHeight + 'px' }">
    <view class="safe-area">
      <!-- 头像模块 -->
      <view class="portfolio">
        <view class="user-info">
          <view class="avatar-wrap">
            <image 
              :src="userInfo ? userInfo.avatarUrl : '/static/mine/avatar.png'" 
              alt="头像" 
              class="avatar" 
            />
            <view class="member-tag" v-if="userInfo">
              <text class="member-text">普通会员</text>
            </view>
          </view>
          <view class="text">
            <text class="username">{{ userInfo ? userInfo.nickname : 'Hello!' }}</text>
            <text class="remind" v-if="!userInfo">登录享受更多精彩服务</text>
            <text class="welcome" v-else>欢迎回来，祝您用餐愉快~</text>
          </view>
        </view>
        <view class="right-area">
          <view class="qr-code" v-if="userInfo">
            <image src="/static/index/qrcode.svg" class="qr-icon" />
          </view>
          <view v-if="!userInfo" class="btn" @tap="handleLogin">登录/注册</view>
        </view>
      </view>
      
      <!-- 优惠券模块 -->
      <view class="coupon-section">
        <view class="coupon-header">
          <text class="coupon-title">我的优惠券</text>
          <text class="coupon-more" @tap="goToCouponPage" v-show="coupons.length === 0">查看全部 ></text>
        </view>
        <view class="coupon-content">
          <text v-if="!isLogin" class="login-tip">登录后查看优惠券</text>
          <!-- 无优惠券时显示的空状态 -->
          <view class="coupon-list empty-state" v-else-if="coupons.length === 0">
            <view class="coupon-item empty">
              <view class="coupon-left">
                <view class="amount-wrap">
                  <text class="icon-ticket">🎫</text>
                </view>
              </view>
              <view class="coupon-right">
                <text class="empty-title">暂无可用优惠券</text>
                <text class="empty-desc">您目前没有任何优惠券</text>
              </view>
            </view>
          </view>
          <view class="coupon-list" :class="{ 'not-login': !isLogin }" v-else>
            <view class="coupon-item" v-for="coupon in coupons.slice(0, 2)" :key="coupon.couponId">
              <view class="coupon-left">
                <view class="amount-wrap">
                  <text class="symbol" v-if="coupon.type === 1">¥</text>
                  <text class="amount" v-if="coupon.type === 1">{{ coupon.amount }}</text>
                  <text class="amount" v-else>{{ (coupon.discount / 10).toFixed(1) }}</text>
                  <text class="unit" v-if="coupon.type === 2">折</text>
                </view>
                <text class="condition">{{ coupon.threshold === 0.00 ? '无门槛' : `满${coupon.threshold}可用`  }}</text>
              </view>
              <view class="coupon-right">
                <text class="type">{{ coupon.type === 1 ?  '满减券' : '折扣券' }}</text>
                <text class="date">有效期至：{{ formatTime(coupon.endTime) }}</text>
                <view class="use-btn">立即使用</view>
              </view>
            </view>
            <!-- 当优惠券数量大于2时显示更多提示 -->
            <view class="more-tip" v-if="coupons.length > 2" @tap="goToCouponPage">
              <text>查看更多优惠券 (共{{ coupons.length }}张)</text>
            </view>
          </view>
        </view>
      </view>
      
      <view class="util">
        <view class="util-header">
          <text class="util-title">常用功能</text>
        </view>
        <view class="util-grid">
          <view class="grid-item" @tap="handleAddressClick">
            <view class="icon-wrapper">
              <image src="/static/mine/address.png" class="icon" />
            </view>
            <text class="text">地址管理</text>
          </view>
          <view class="grid-item">
            <view class="icon-wrapper">
              <image src="/static/mine/invoice.png" class="icon" />
            </view>
            <text class="text">发票管理</text>
          </view>
          <view class="grid-item">
            <view class="icon-wrapper">
              <image src="/static/mine/setting.png" class="icon" />
            </view>
            <text class="text">设置</text>
          </view>
          <view class="grid-item">
            <view class="icon-wrapper">
              <image src="/static/mine/wechat.png" class="icon" />
            </view>
            <text class="text">关注公众号</text>
          </view>
          <view class="grid-item">
            <view class="icon-wrapper">
              <image src="/static/mine/service.png" class="icon" />
            </view>
            <text class="text">在线客服</text>
          </view>
          <view class="grid-item">
            <view class="icon-wrapper">
              <image src="/static/mine/terms.png" class="icon" />
            </view>
            <text class="text">条款与证明</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<style scoped lang="scss">
.layout {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-height: 100vh;
  background: rgba(246, 246, 246, 1);
}

.safe-area {
  width: 92%;
  padding: 20rpx 0;
}

.portfolio {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24rpx 30rpx;
  background: linear-gradient(135deg, #ffffff, #f8f9fa);
  border-radius: 16rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
  overflow: hidden;
  width: 100%;
  box-sizing: border-box;
  margin-bottom: 30rpx;
  
  &::before {
    content: '';
    position: absolute;
    right: -60rpx;
    top: -60rpx;
    width: 200rpx;
    height: 200rpx;
    border-radius: 50%;
    background: linear-gradient(135deg, rgba(18, 150, 219, 0.1), rgba(18, 150, 219, 0.05));
    z-index: 0;
  }
  
  &::after {
    content: '';
    position: absolute;
    left: 40%;
    bottom: -80rpx;
    width: 160rpx;
    height: 160rpx;
    border-radius: 50%;
    background: linear-gradient(135deg, rgba(18, 150, 219, 0.08), rgba(18, 150, 219, 0.03));
    z-index: 0;
  }

  .user-info {
    flex: 1;
    display: flex;
    flex-direction: row;
    align-items: center;
    position: relative;
    z-index: 1;

    .avatar-wrap {
      position: relative;
      margin-right: 20rpx;
      
      .avatar {
        width: 80rpx;
        height: 80rpx;
        border-radius: 50%;
        background: #fff;
        border: 2rpx solid rgba(18, 150, 219, 0.2);
        box-shadow: 0 4rpx 12rpx rgba(18, 150, 219, 0.1);
        object-fit: cover;
      }
      
      .member-tag {
        position: absolute;
        bottom: -10rpx;
        left: 52%;
        transform: translateX(-50%) scale(0.85);
        transform-origin: center bottom;
        background: #1296db;
        border: 2rpx solid #fff;
        border-radius: 12rpx;
        padding: 4rpx 12rpx;
        box-shadow: 0 2rpx 8rpx rgba(18, 150, 219, 0.2);
        white-space: nowrap;
        z-index: 2;
        
        .member-text {
          font-size: 20rpx;
          color: #fff;
          line-height: 1.2;
          display: block;
        }
      }
    }

    .text {
      flex: 1;
      z-index: 1;
      padding-left: 10rpx;

      .username {
        font-size: 32rpx;
        font-weight: 600;
        color: #333;
        display: block;
        margin-bottom: 6rpx;
      }
      
      .remind {
        font-size: 24rpx;
        color: #666;
        display: block;
      }

      .welcome {
        font-size: 24rpx;
        color: #1296db;
        display: block;
        opacity: 0.85;
      }
    }
  }

  .right-area {
    display: flex;
    align-items: center;
    position: relative;
    z-index: 1;

    .qr-code {
      margin-right: 20rpx;

      .qr-icon {
        width: 40rpx;
        height: 40rpx;
        opacity: 0.8;
        transition: all 0.3s ease;
        
        &:active {
          opacity: 0.6;
          transform: scale(0.95);
        }
      }
    }

    .btn {
      background: linear-gradient(135deg, #1296db, #0f85c7);
      border-radius: 28rpx;
      padding: 10rpx 28rpx;
      font-size: 26rpx;
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
}

.coupon-section {
  width: 100%;
  background: #ffffff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
  box-sizing: border-box;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    right: -60rpx;
    top: -60rpx;
    width: 200rpx;
    height: 200rpx;
    background: rgba(18, 150, 219, 0.1);
    border-radius: 50%;
    z-index: 0;
  }

  .coupon-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20rpx;
    position: relative;
    z-index: 1;

    .coupon-title {
      font-size: 32rpx;
      font-weight: 600;
      color: #333;
    }

    .coupon-more {
      font-size: 24rpx;
      color: #666;
    }
  }

  .coupon-content {
    position: relative;
    min-height: 200rpx;

    .login-tip {
      position: absolute;
      left: 50%;
      top: 50%;
      transform: translate(-50%, -50%);
      z-index: 2;
      background: #fff;
      padding: 16rpx 32rpx;
      border-radius: 30rpx;
      box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
      font-size: 28rpx;
      color: #333;
      white-space: nowrap;
    }

    .coupon-list {
      position: relative;
      z-index: 1;

      &.not-login {
        filter: blur(6px);
        opacity: 0.3;
        pointer-events: none;
      }
      
      &.empty-state {
        opacity: 1;
        filter: none;
      }

      .coupon-item {
        display: flex;
        align-items: center;
        height: 160rpx;
        background: linear-gradient(45deg, rgba(18, 150, 219, 0.05), rgba(18, 150, 219, 0.1));
        border-radius: 12rpx;
        margin-bottom: 20rpx;
        position: relative;
        overflow: hidden;
        
        &.empty {
          background: linear-gradient(45deg, rgba(240, 240, 240, 0.6), rgba(250, 250, 250, 0.8));
          border: 1px dashed #e0e0e0;
          
          .icon-ticket {
            font-size: 48rpx;
            color: #cccccc;
          }
          
          .empty-title {
            font-size: 28rpx;
            font-weight: 500;
            color: #999;
            margin-bottom: 8rpx;
          }
          
          .empty-desc {
            font-size: 22rpx;
            color: #bbb;
          }
        }

        &::after {
          content: '';
          position: absolute;
          left: 220rpx;
          top: 0;
          bottom: 0;
          width: 2rpx;
          background: rgba(255, 255, 255, 0.8);
          border-right: 2rpx dashed rgba(18, 150, 219, 0.2);
        }

        .coupon-left {
          width: 220rpx;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;

          .amount-wrap {
            display: flex;
            align-items: baseline;

            .symbol {
              font-size: 32rpx;
              color: #1296db;
              margin-right: 4rpx;
            }

            .amount {
              font-size: 60rpx;
              font-weight: 600;
              color: #1296db;
              line-height: 1;
            }

            .unit {
              font-size: 28rpx;
              color: #1296db;
              margin-left: 4rpx;
            }
          }

          .condition {
            font-size: 24rpx;
            color: #666;
            margin-top: 10rpx;
          }
        }

        .coupon-right {
          flex: 1;
          padding: 20rpx 30rpx;
          display: flex;
          flex-direction: column;
          justify-content: center;

          .type {
            font-size: 28rpx;
            font-weight: 500;
            color: #333;
            margin-bottom: 8rpx;
          }

          .date {
            font-size: 22rpx;
            color: #999;
            margin-bottom: 16rpx;
          }

          .use-btn {
            width: fit-content;
            padding: 8rpx 24rpx;
            background: linear-gradient(135deg, #1296db, #0f85c7);
            border-radius: 24rpx;
            color: #fff;
            font-size: 24rpx;
            box-shadow: 0 4rpx 12rpx rgba(18, 150, 219, 0.2);
            transition: all 0.3s ease;

            &:active {
              transform: scale(0.98);
              box-shadow: 0 2rpx 6rpx rgba(18, 150, 219, 0.2);
            }
          }
        }
      }

      .more-tip {
        text-align: center;
        padding: 16rpx 0;
        color: #1296db;
        font-size: 26rpx;
        position: relative;
        
        &::before {
          content: '';
          position: absolute;
          left: 0;
          right: 0;
          top: 50%;
          height: 1px;
          background: #eee;
          z-index: 0;
        }
        
        text {
          position: relative;
          z-index: 1;
          background: #fff;
          padding: 0 20rpx;
          opacity: 0.85;
        }
      }
    }
  }
}

.util {
  width: 100%;
  background: #ffffff;
  border-radius: 16rpx;
  padding: 30rpx;
  box-sizing: border-box;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);

  .util-header {
    margin-bottom: 30rpx;
    
    .util-title {
      font-size: 32rpx;
      font-weight: 600;
      color: #333;
    }
  }

  .util-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 30rpx;
    
    .grid-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 20rpx 0;
      
      .icon-wrapper {
        width: 80rpx;
        height: 80rpx;
        background: rgba(18, 150, 219, 0.1);
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
      
      .text {
        font-size: 26rpx;
        color: #333;
      }
    }
  }
}
</style>
