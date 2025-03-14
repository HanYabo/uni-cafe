<script setup>
import { cancelOrderAPI, deleteOrderAPI, getHistoryOrderAPI } from '@/api/order';
import { formatTime } from '@/utils/format';
import { onLoad, onShow } from '@dcloudio/uni-app';
import { computed, nextTick, onUnmounted, ref, watch } from 'vue';
import { payOrderAPI } from '../../api/order';

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

// 添加侦听器，深度监听orders的变化
watch(orders, (newOrders) => {
  // 检查是否有订单状态发生变化
  const hasStatusChange = newOrders.some((order, index) => {
    const oldOrder = orders.value[index]
    return oldOrder && order.status !== oldOrder.status
  })
  
  // 如果有状态变化，重新获取订单列表
  if (hasStatusChange) {
    getHistoryOrderList()
  }
}, { deep: true })

// 倒计时Map
const countdownMap = ref(new Map())

// 当前选中的状态
const currentStatus = ref(OrderStatus.ALL)

// 状态标签列表
const statusTabs = [
  { label: '全部订单', value: OrderStatus.ALL },
  { label: '待支付', value: OrderStatus.WAITING_PAY },
  { label: '已支付', value: OrderStatus.PROCESSING },
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

// 设置倒计时
const setupCountdown = (orderId, createdAt) => {
  const orderTime = new Date(createdAt).getTime()
  const now = new Date().getTime()
  const timeLeft = Math.max(0, 600000 - (now - orderTime)) // 10分钟 = 600000毫秒

  if (timeLeft > 0) {
    const timer = setInterval(async () => {
      const currentTime = new Date().getTime()
      const remaining = Math.max(0, 600000 - (currentTime - orderTime))
      
      if (remaining <= 0) {
        clearInterval(timer)
        countdownMap.value.delete(orderId)
        // 更新订单状态为已取消
        const orderIndex = orders.value.findIndex(order => order.orderId === orderId)
        if (orderIndex !== -1) {
          orders.value[orderIndex] = {
            ...orders.value[orderIndex],
            status: OrderStatus.CANCELLED
          }
        }
        // 调用取消订单接口
        const result = await cancelOrderAPI(orderId)
        if(result.code === 200) {
          uni.showToast({
            title: '订单已超时自动取消',
            icon: 'none'
          })
          
          // 发送事件通知其他页面
          uni.$emit('orderStatusChanged', {
            orderId: orderId,
            status: OrderStatus.CANCELLED
          })
        }
      } else {
        const minutes = Math.floor(remaining / 60000)
        const seconds = Math.floor((remaining % 60000) / 1000)
        countdownMap.value.set(orderId, `${minutes}:${seconds.toString().padStart(2, '0')}`)
      }
    }, 1000)

    // 初始设置
    const minutes = Math.floor(timeLeft / 60000)
    const seconds = Math.floor((timeLeft % 60000) / 1000)
    countdownMap.value.set(orderId, `${minutes}:${seconds.toString().padStart(2, '0')}`)
  }
}

// 监听订单状态变化事件
const handleOrderStatusChange = (data) => {
  // 当收到订单状态变化的通知时，更新本地订单列表
  const orderIndex = orders.value.findIndex(order => order.orderId === data.orderId)
  if (orderIndex !== -1) {
    orders.value[orderIndex] = {
      ...orders.value[orderIndex],
      status: data.status
    }
  }
}

// 设置和清理事件监听
onLoad(() => {
  // 添加订单状态变化事件监听
  uni.$on('orderStatusChanged', handleOrderStatusChange)
})

onUnmounted(() => {
  countdownMap.value.clear()
  // 移除事件监听
  uni.$off('orderStatusChanged', handleOrderStatusChange)
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
        userId: order.userId,
        couponId: order.couponId,
        status: order.status,
        createdAt: order.createdAt,
        updatedAt: order.updatedAt,
        payTime: order.payTime,
        payType: order.payType,
        remark: order.remark,
        totalAmount: order.totalAmount,
        discountAmount: order.discountAmount,
        payAmount: order.payAmount,
        items: order.items.map(item => ({
          productName: item.productName,
          specs: Array.isArray(item.specs) ? item.specs : [],
          quantity: item.quantity,
          basePrice: item.basePrice || 0,
          actualPrice: item.actualPrice || 0,
          subtotal: item.subtotal || 0
        }))
      }))

      // 为待支付订单设置倒计时
      orders.value.forEach(order => {
        if (order.status === OrderStatus.WAITING_PAY) {
          setupCountdown(order.orderId, order.createdAt)
        }
      })
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

// 取消订单或删除订单操作
const handleCancelOrDelete = (orderId, status) => {
  if(status === OrderStatus.WAITING_PAY) {
    uni.showModal({
      title: '温馨提示',
      content: '确定要取消订单吗？',
      success: async (res) => {
        if(res.confirm) {
          const result = await cancelOrderAPI(orderId)
          if(result.code === 200) {
            uni.showToast({
              title: '订单取消成功',
              icon: 'success'
            })
            // 取消成功后，等待DOM更新后重新获取订单列表
            await nextTick()
            getHistoryOrderList()
          }else {
            uni.showToast({
              title: '取消订单失败', 
              icon: 'error'
            })
          }
        }
      }
    })
  } else if(status === OrderStatus.PROCESSING) {
    uni.showToast({
      title: '订单已支付，请等待订单完成',
      icon: 'none'
    })
  } else {
    uni.showModal({
      title: '温馨提示',
      content: '确定要删除订单吗？',
      success: async (res) => {
        if(res.confirm) {
          const result = await deleteOrderAPI(orderId)
          if(result.code === 200) {
            uni.showToast({
              title: '订单删除成功',
              icon: 'success'
            })
            // 删除成功后，等待DOM更新后重新获取订单列表
            await nextTick()
            getHistoryOrderList()
          }else {
            uni.showToast({
              title: '订单删除失败',
              icon: 'error'
            }) 
          }
        }
      }
    })
  }
}

// 立即支付或者再来一单订单操作
const handlePayOrAgain = (orderId, status) => {
  if(status === OrderStatus.WAITING_PAY) {
    // 执行支付操作
    uni.showModal({
      title: '温馨提示',
      content: '确定要支付订单吗？',
      success: async (res) => {
        if(res.confirm) {
          // 执行支付操作
          const result = await payOrderAPI(orderId)
          if(result.code === 200) {
            // 发送清空购物袋事件
            uni.$emit('clearShoppingCart');
            
            uni.showToast({
              title: '支付成功',
              icon: 'success'
            })

            uni.removeStorageSync('orderData')
            uni.removeStorageSync('orderItems')

            getHistoryOrderList()
          }else {
            uni.showToast({
              title: '支付失败',
              icon: 'error'
            })
          }
        }
      }
    })
  }
  // TODO: 再来一单
  
}

// 跳转订单详情
const goToDetail = (orderId) => {
  uni.navigateTo({
    url: `/pages/record/detail?orderId=${orderId}`
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
            <text v-if="order.status === OrderStatus.WAITING_PAY" class="countdown">{{ countdownMap.get(order.orderId) }}</text>
            {{ getStatusText(order.status) }}
          </view>
        </view>

        <!-- 订单内容 -->
        <view class="order-content" @tap="goToDetail(order.orderId)">
          <view class="product-list">
            <view class="product-item" v-for="(item, index) in order.items" :key="index">
              <view class="product-name">
                {{ item.productName }}
                <text class="product-size" v-for="(spec, index) in item.specs" :key="index">{{ spec.specValue }}</text>
              </view>
              <view class="product-count">x{{ item.quantity }}</view>
            </view>
          </view>
          
          <view class="order-total">
            共{{ order.items.reduce((sum, item) => sum + item.quantity, 0) }}件商品
            <text>合计 ¥{{ order.payAmount }}</text>
          </view>
        </view>

        <!-- 订单底部 -->
        <view class="order-footer">
          <view class="order-time">{{ formatTime(order.createdAt) }}</view>
          <view class="order-actions">
            <view class="btn btn-outline" @tap="handleCancelOrDelete(order.orderId, order.status)">
              {{ order.status === OrderStatus.WAITING_PAY ? '取消订单' : '删除订单' }}
            </view>
            <view class="btn btn-primary" :class="{ 'btn-pay': order.status === OrderStatus.WAITING_PAY }" @tap="handlePayOrAgain(order.orderId, order.status)">
              {{ order.status === OrderStatus.WAITING_PAY ? '立即支付' : '再来一单' }}
            </view>
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
          display: flex;
          align-items: center;
          
          .countdown {
            margin-right: 8rpx;
            color: #ff6b00;
          }
          
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

.btn-pay {
  background: #ff6b00 !important;
}
</style>