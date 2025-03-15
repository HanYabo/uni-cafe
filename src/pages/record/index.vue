<script setup>
import { cancelOrderAPI, deleteOrderAPI, getHistoryOrderAPI, getOrderDetailAPI } from '@/api/order';
import { formatTime } from '@/utils/format';
import { onLoad, onShow } from '@dcloudio/uni-app';
import { computed, onUnmounted, ref, watch } from 'vue';
import { payOrderAPI } from '../../api/order';

// 订单状态枚举
const OrderStatus = {
  ALL: -1,          // 全部订单
  WAITING_PAY: 0,   // 待支付
  PROCESSING: 1,    // 已支付
  COMPLETED: 2,     // 已完成
  CANCELLED: 3      // 已取消
}

// 添加刷新状态控制
const refreshing = ref(false)

// 添加触底加载相关状态
const isLoadingMore = ref(false)
const hasMoreData = ref(true)
const currentPage = ref(1)
const pageSize = ref(10)

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
const getHistoryOrderList = async (isRefresh = false) => {
  // 添加加载状态指示
  if (!isRefresh) {
    uni.showLoading({
      title: '加载中...'
    })
  }
  
  try {
    // 重新获取用户信息，确保是最新的
    const userInfo = uni.getStorageSync('userInfo')
    if (!userInfo) {
      // 用户未登录，提前返回并关闭加载状态
      if (!isRefresh) {
        uni.hideLoading()
      }
      return
    }
    
    const res = await getHistoryOrderAPI(userInfo.userId, {
      page: currentPage.value,
      pageSize: pageSize.value
    })
    
    if(res.code === 200) {
      // 处理返回的订单数据
      const newOrders = res.data.map(order => ({
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

      // 如果是刷新，则替换整个列表，否则追加
      if (isRefresh) {
        orders.value = newOrders
      } else {
        orders.value = [...orders.value, ...newOrders]
      }

      // 判断是否还有更多数据
      hasMoreData.value = newOrders.length === pageSize.value

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
  } finally {
    // 隐藏加载状态 - 确保无论成功失败都关闭loading
    if (!isRefresh) {
      uni.hideLoading()
    }
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
          try {
            uni.showLoading({ title: '处理中...' })
            const result = await cancelOrderAPI(orderId)
            if(result.code === 200) {
              // 直接更新本地订单状态，提供即时反馈
              const orderIndex = orders.value.findIndex(order => order.orderId === orderId)
              if (orderIndex !== -1) {
                orders.value[orderIndex] = {
                  ...orders.value[orderIndex],
                  status: OrderStatus.CANCELLED
                }
              }
              
              uni.showToast({
                title: '订单取消成功',
                icon: 'success'
              })
              
              // 发送事件通知其他页面
              uni.$emit('orderStatusChanged', {
                orderId: orderId,
                status: OrderStatus.CANCELLED
              })
              
              // 重新获取订单列表以确保数据一致性
              await getHistoryOrderList(true)
            } else {
              uni.showToast({
                title: '取消订单失败', 
                icon: 'error'
              })
            }
          } catch (error) {
            console.error('取消订单失败:', error)
            uni.showToast({
              title: '操作失败，请重试',
              icon: 'none'
            })
          } finally {
            // 确保无论成功失败都关闭loading
            uni.hideLoading()
          }
        }
      },
      // 添加fail回调处理弹窗失败情况
      fail: () => {
        console.error('取消订单对话框显示失败')
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
          try {
            uni.showLoading({ title: '处理中...' })
            const result = await deleteOrderAPI(orderId)
            if(result.code === 200) {
              // 直接从本地列表中移除订单，提供即时反馈
              orders.value = orders.value.filter(order => order.orderId !== orderId)
              
              uni.showToast({
                title: '订单删除成功',
                icon: 'success'
              })
              
              // 重新获取订单列表以确保数据一致性
              await getHistoryOrderList(true)
            } else {
              uni.showToast({
                title: '订单删除失败',
                icon: 'error'
              }) 
            }
          } catch (error) {
            console.error('删除订单失败:', error)
            uni.showToast({
              title: '操作失败，请重试',
              icon: 'none'
            })
          } finally {
            // 确保无论成功失败都关闭loading
            uni.hideLoading()
          }
        }
      },
      // 添加fail回调处理弹窗失败情况
      fail: () => {
        console.error('删除订单对话框显示失败')
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
          try {
            uni.showLoading({ title: '处理中...' })
            // 执行支付操作
            const result = await payOrderAPI(orderId)
            if(result.code === 200) {
              // 直接更新本地订单状态，提供即时反馈
              const orderIndex = orders.value.findIndex(order => order.orderId === orderId)
              if (orderIndex !== -1) {
                orders.value[orderIndex] = {
                  ...orders.value[orderIndex],
                  status: OrderStatus.PROCESSING,
                  payTime: new Date().toISOString()
                }
              }
              
              // 发送清空购物袋事件
              uni.$emit('clearShoppingCart')
              
              // 发送事件通知其他页面
              uni.$emit('orderStatusChanged', {
                orderId: orderId,
                status: OrderStatus.PROCESSING
              })
              
              uni.showToast({
                title: '支付成功',
                icon: 'success'
              })
              
              uni.removeStorageSync('orderData')
              uni.removeStorageSync('orderItems')
              
              // 重新获取订单列表以确保数据一致性
              await getHistoryOrderList(true)
            } else {
              uni.showToast({
                title: '支付失败',
                icon: 'error'
              })
            }
          } catch (error) {
            console.error('支付订单失败:', error)
            uni.showToast({
              title: '操作失败，请重试',
              icon: 'none'
            })
          } finally {
            // 确保无论成功失败都关闭loading
            uni.hideLoading()
          }
        }
      },
      // 添加fail回调处理弹窗失败情况
      fail: () => {
        console.error('支付订单对话框显示失败')
      }
    })
  } else {
    // 再来一单功能
    reorderItems(orderId)
  }
}

// 实现再来一单功能
const reorderItems = async (orderId) => {
  uni.showLoading({ title: '正在加载订单...' })
  try {
    // 获取订单详情
    const res = await getOrderDetailAPI(orderId)
    if (res.code === 200) {
      const orderDetail = res.data
      
      // 清空当前购物袋
      uni.$emit('clearShoppingCart')
      
      // 跳转到点单页面
      uni.switchTab({
        url: '/pages/order/index',
        success: () => {
          // 使用延时确保页面加载完成后再添加商品
          setTimeout(() => {
            // 将订单中的商品添加到购物袋
            uni.$emit('addItemsToCart', {
              items: orderDetail.items.map(item => ({
                productId: item.productId,
                name: item.productName,
                desc: item.specs.map(spec => spec.specValue).join('，') || '',
                unitPrice: item.actualPrice, // 单价
                price: item.basePrice,
                quantity: item.quantity,
                image: item.mainImage,
                specs: item.specs || [],
                selected: true // 默认选中
              }))
            })
          }, 500)
        }
      })
    } else {
      uni.showToast({
        title: '获取订单信息失败',
        icon: 'error'
      })
    }
  } catch (error) {
    console.error('再来一单失败:', error)
    uni.showToast({
      title: '操作失败，请重试',
      icon: 'error'
    })
  } finally {
    // 确保无论成功失败都关闭loading
    uni.hideLoading()
  }
}

// 跳转订单详情
const goToDetail = (orderId) => {
  uni.navigateTo({
    url: `/pages/record/detail?orderId=${orderId}`
  })
}

// 添加处理下拉刷新的方法
const handleRefresh = async () => {
  refreshing.value = true
  try {
    // 重置分页参数
    currentPage.value = 1
    hasMoreData.value = true
    await getHistoryOrderList(true)
  } catch (error) {
    console.error('刷新订单列表失败:', error)
  } finally {
    // 延迟结束刷新状态，提供更好的用户体验
    setTimeout(() => {
      refreshing.value = false
    }, 500)
  }
}

// 添加触底加载更多方法
const handleLoadMore = async () => {
  // 如果正在加载或没有更多数据，则不执行加载
  if (isLoadingMore.value || !hasMoreData.value) return
  
  isLoadingMore.value = true
  try {
    // 页码加1
    currentPage.value += 1
    await getHistoryOrderList(false)
  } catch (error) {
    console.error('加载更多订单失败:', error)
    // 加载失败时，页码恢复
    currentPage.value -= 1
  } finally {
    // 延迟结束加载状态
    setTimeout(() => {
      isLoadingMore.value = false
    }, 500)
  }
}

onShow(() => {
  // 每次显示页面时获取用户信息并判断登录状态
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
    // 重置页面并刷新数据
    currentPage.value = 1
    getHistoryOrderList(true)
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
    <scroll-view 
      scroll-y 
      class="order-list"
      refresher-enabled
      :refresher-triggered="refreshing"
      @refresherrefresh="handleRefresh"
      :show-scrollbar="false"
      enhanced
      @scrolltolower="handleLoadMore"
      lower-threshold="100"
    >
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
      
      <!-- 底部加载状态 -->
      <view class="loading-more" v-if="filteredOrders.length > 0">
        <view v-if="isLoadingMore" class="loading">
          <view class="loading-spinner"></view>
          <text>加载中...</text>
        </view>
        <view v-else-if="!hasMoreData" class="no-more">
          <text>已经到底啦~</text>
        </view>
        <view v-else class="pull-tip">
          <text>上拉加载更多</text>
        </view>
      </view>
      
      <!-- 无订单状态 -->
      <view v-if="filteredOrders.length === 0" class="empty-state">
        <image src="/static/record/empty.png" mode="aspectFit" class="empty-image" />
        <text class="empty-text">暂无相关订单</text>
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
    -webkit-overflow-scrolling: touch;
    
    /* 隐藏滚动条但保持可滚动 */
    &::-webkit-scrollbar {
      width: 0 !important;
      display: none !important;
      background: transparent !important;
    }
    scrollbar-width: none !important;
    -ms-overflow-style: none !important;
    scrollbar-color: transparent transparent !important;
    
    /* 下拉刷新相关样式 */
    .uni-scroll-view-refresh {
      background-color: #f7f7f7 !important;
    }
    
    .uni-scroll-view-refresh__spinner {
      color: #1296db !important;
    }

    /* 底部加载状态样式 */
    .loading-more {
      padding: 20rpx 0;
      text-align: center;
      
      .loading {
        display: flex;
        justify-content: center;
        align-items: center;
        
        .loading-spinner {
          width: 30rpx;
          height: 30rpx;
          border: 3rpx solid #1296db;
          border-radius: 50%;
          border-top-color: transparent;
          animation: spin 0.8s linear infinite;
          margin-right: 10rpx;
        }
        
        text {
          font-size: 24rpx;
          color: #666;
        }
      }
      
      .no-more, .pull-tip {
        font-size: 24rpx;
        color: #999;
        line-height: 40rpx;
      }
    }
    
    /* 空状态样式 */
    .empty-state {
      padding: 100rpx 0;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      
      .empty-image {
        width: 180rpx;
        height: 180rpx;
        margin-bottom: 20rpx;
        opacity: 0.7;
      }
      
      .empty-text {
        font-size: 28rpx;
        color: #999;
      }
    }

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

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>