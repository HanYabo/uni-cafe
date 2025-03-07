<script setup>
import { getHistoryOrderAPI } from '@/api/order';
import { onShow } from '@dcloudio/uni-app';
import { computed, ref } from 'vue';

// 订单状态枚举
const OrderStatus = {
  ALL: -1,          // 全部订单
  WAITING_PAY: 0,   // 待支付
  PROCESSING: 1,    // 已支付
  COMPLETED: 2,     // 已完成
  CANCELLED: 3      // 已取消
}

// 订单数据
const orders = ref([])
// 当前选中的状态
const currentStatus = ref(OrderStatus.ALL)

// 状态标签列表
const statusTabs = [
  { label: '全部订单', value: OrderStatus.ALL },
  { label: '待支付', value: OrderStatus.WAITING_PAY },
  { label: '进行中', value: OrderStatus.PROCESSING },
  { label: '已完成', value: OrderStatus.COMPLETED }
]

// 切换状态
const switchStatus = (status) => {
  currentStatus.value = status
}

// 过滤后的订单列表
const filteredOrders = computed(() => {
  if (currentStatus.value === OrderStatus.ALL) {
    return orders.value
  }
  return orders.value.filter(order => order.status === currentStatus.value)
})

// 获取订单列表
const getHistoryOrderList = async () => {
  // 重新获取用户信息，确保是最新的
  const userInfo = uni.getStorageSync('userInfo')
  try {
    const res = await getHistoryOrderAPI(userInfo.userId)
    if(res.code === 200) {
      // 确保数据结构正确
      orders.value = res.data.map(order => ({
        orderId: order.orderId,
        status: order.status,
        createTime: order.createTime,
        totalPrice: order.totalPrice,
        items: order.items.map(item => ({
          productName: item.productName,
          specs: item.specs || [{ specValue: '' }],
          quantity: item.quantity,
          price: item.price || 0  // 确保有价格字段，默认为0
        }))
      }))
    } else {
      uni.showToast({
        title: res.message,
        icon: 'none'
      })
    }
  } catch (error) {
    console.error('获取订单列表失败：', error)
    uni.showToast({
      title: '获取订单列表失败',
      icon: 'none'
    })
  }
}

// 获取订单状态文本
const getStatusText = (status) => {
  switch(status) {
    case OrderStatus.WAITING_PAY: return '待支付';
    case OrderStatus.PROCESSING: return '已支付';
    case OrderStatus.COMPLETED: return '已完成';
    case OrderStatus.CANCELLED: return '已取消';
    default: return '';
  }
}

// 获取订单状态样式类
const getStatusClass = (status) => {
  switch(status) {
    case OrderStatus.WAITING_PAY: return 'status-waiting';
    case OrderStatus.PROCESSING: return 'status-processing';
    case OrderStatus.COMPLETED: return 'status-completed';
    case OrderStatus.CANCELLED: return 'status-cancelled';
    default: return '';
  }
}

// 跳转订单详情
const goToDetail = (id) => {
  uni.navigateTo({
    url: `/pages/record/detail?id=${id}`
  })
}

onShow(() => {
  // 每次显示页面时重新获取用户信息
  const userInfo = uni.getStorageSync('userInfo')
  // 判断用户登陆状态 然后弹窗让用户选择去登录
  if(!userInfo) {
    uni.showModal({
      title: '温馨提示',
      content: '请先登录',
      success: (res) => {
        if (res.confirm) {
          uni.navigateTo({
            url: '/pages/login/index'
          })
        }
      }
    })
  } else {
    getHistoryOrderList()
  }
})

</script>

<template>
  <view class="record-container">
    <!-- 顶部标签栏 -->
    <view class="tab-bar">
      <view 
        v-for="tab in statusTabs" 
        :key="tab.value"
        class="tab-item"
        :class="{ active: currentStatus === tab.value }"
        @tap="switchStatus(tab.value)"
      >
        {{ tab.label }}
      </view>
    </view>

    <!-- 订单列表 -->
    <scroll-view scroll-y class="order-list">
      <view class="order-item" v-for="(order, index) in filteredOrders" :key="index">
        <!-- 订单头部 -->
        <view class="order-header">
          <view class="store-name">郑州正弘城店</view>
          <view class="order-status" :class="getStatusClass(order.status)">
            {{ getStatusText(order.status) }}
          </view>
        </view>

        <!-- 订单内容 -->
        <view class="order-content">
          <view class="product-list">
            <view class="product-item" v-for="(item, index) in order.items" :key="index">
              <view class="product-name">
                {{ item.productName }}
                <text class="product-size">{{ item.specs[0].specValue }}</text>
              </view>
              <view class="product-count">x{{ item.quantity }}</view>
            </view>
          </view>
          
          <view class="order-total">
            共{{ order.items.reduce((sum, item) => sum + item.quantity, 0) }}件商品
            <text>合计 ¥{{ order.items.reduce((sum, item) => sum + item.subtotal, 0).toFixed(2) }}</text>
          </view>
        </view>

        <!-- 订单底部 -->
        <view class="order-footer">
          <view class="order-time">{{ order.createdAt }}</view>
          <view class="order-actions">
            <view class="btn btn-outline">再来一单</view>
            <view class="btn btn-primary" @tap="goToDetail(order.orderId)">查看详情</view>
          </view>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<style lang="scss" scoped>
.record-container {
  min-height: 100vh;
  background: #f7f7f7;
  
  .tab-bar {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    background: #fff;
    display: flex;
    padding: 20rpx 0;
    z-index: 100;

    .tab-item {
      flex: 1;
      text-align: center;
      font-size: 28rpx;
      color: #666;
      position: relative;
      padding: 20rpx 0;

      &.active {
        color: #1296db;
        font-weight: bold;

        &::after {
          content: '';
          position: absolute;
          bottom: 0;
          left: 50%;
          transform: translateX(-50%);
          width: 40rpx;
          height: 4rpx;
          background: #1296db;
          border-radius: 2rpx;
        }
      }
    }
  }

  .order-list {
    padding-top: 120rpx;
    width: 92%;
    margin: 0 auto;

    .order-item {
      width: 100%;
      margin-bottom: 20rpx;
      background: #fff;
      border-radius: 12px;
      padding: 16px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
      box-sizing: border-box;
      
      &:first-child {
        margin-top: 20rpx;
      }

      .order-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding-bottom: 20rpx;
        border-bottom: 1rpx solid #eee;

        .store-name {
          font-size: 32rpx;
          font-weight: bold;
          color: #333;
        }

        .order-status {
          font-size: 26rpx;
          
          &.status-waiting {
            color: #ff6b00;
          }
          &.status-processing {
            color: #1296db;
          }
          &.status-completed {
            color: #52c41a;
          }
          &.status-cancelled {
            color: #999;
          }
        }
      }

      .order-content {
        padding: 20rpx 0;

        .product-list {
          .product-item {
            display: flex;
            justify-content: space-between;
            margin-bottom: 16rpx;
            
            .product-name {
              color: #333;
              font-size: 28rpx;

              .product-size {
                color: #999;
                font-size: 24rpx;
                margin-left: 10rpx;
              }
            }

            .product-count {
              color: #666;
              font-size: 28rpx;
            }
          }
        }

        .order-total {
          text-align: right;
          font-size: 26rpx;
          color: #666;
          margin-top: 20rpx;

          text {
            color: #333;
            font-weight: bold;
            margin-left: 20rpx;
          }
        }
      }

      .order-footer {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding-top: 20rpx;
        border-top: 1rpx solid #eee;

        .order-time {
          color: #999;
          font-size: 24rpx;
        }

        .order-actions {
          display: flex;
          gap: 20rpx;

          .btn {
            padding: 12rpx 30rpx;
            border-radius: 30rpx;
            font-size: 26rpx;

            &.btn-outline {
              border: 1rpx solid #1296db;
              color: #1296db;
            }

            &.btn-primary {
              background: #1296db;
              color: #fff;
            }
          }
        }
      }
    }
  }
}
</style>