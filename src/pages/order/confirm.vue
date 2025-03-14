<script setup>
import { getCouponListAPI } from '@/api/coupon';
import { createOrder, payOrderAPI } from '@/api/order';
import { formatTime } from '@/utils/format';
import { onShow } from '@dcloudio/uni-app';
import { computed, onMounted, ref } from 'vue';

const baseUrl = 'http://localhost:9000';  // 本地后端服务地址

// TODO: 后续需要修改
const orderInfo = ref({
  products: [],
  shop: {
    name: '郑州正弘城店',
    distance: '0.2公里',
  },
  address: {
    name: '',
    phone: '',
    address: '',
    tag: '公司'
  },
  deliveryTime: '尽快送达（预计14:30送达）',
  pickupTime: '前面1杯/杯制作中，预计32分钟取餐',
  payMethod: '微信支付',
  note: ''
});

// 优惠券数据
const coupons = ref([])

// 获取优惠券信息
const getCouponList = async () => {
  const res = await getCouponListAPI();
  if (res.code === 200) {
    coupons.value = res.data;
  }else {
    uni.showToast({
      title: '优惠券信息获取失败',
      icon: 'error'
    })
  }
} 

onShow(() => {
  getCouponList();
})

// 配送方式
const deliveryType = ref('自取');

// 计算总价
const totalPrice = computed(() => {
  if (!Array.isArray(orderInfo.value.products)) return 0;
  return orderInfo.value.products.reduce((total, item) => {
    return total + item.price * item.quantity;
  }, 0);
});


// 选中的优惠券
const selectedCoupon = ref(null)

// 优惠券选择面板显示状态
const isCouponPanelVisible = ref(false)

// 显示优惠券选择面板
const showCouponPanel = () => {
  isCouponPanelVisible.value = true
}

// 隐藏优惠券选择面板
const hideCouponPanel = () => {
  isCouponPanelVisible.value = false
}

// 选择优惠券
const selectCoupon = (coupon) => {
  // 如果优惠券状态为1或者商品总价未达到使用门槛，则不可选择
  if (coupon.status === 1 || totalPrice.value < coupon.threshold) return
  selectedCoupon.value = coupon
  hideCouponPanel()
}

// 计算优惠金额
const discountAmount = computed(() => {
  if (!selectedCoupon.value) return 0
  
  const total = totalPrice.value
  if (selectedCoupon.value.type === 1) {
    // 满减券
    return total >= selectedCoupon.value.threshold ? selectedCoupon.value.amount : 0
  } else {
    // 折扣券
    return total >= selectedCoupon.value.threshold ? 
      Math.floor(total * (100 - selectedCoupon.value.discount)) / 100 : 0
  }
})

// 实际支付金额
const actualPrice = computed(() => {
  return Math.max(0, totalPrice.value - discountAmount.value)
})

// 备注输入组件显示状态
const isNoteEditorVisible = ref(false);

// 临时备注内容
const tempNote = ref('');

// 备注快捷选项
const quickNotes = ref([
  { id: 1, text: '无接触配送' },
  { id: 2, text: '请勿敲门' },
  { id: 3, text: '多放餐具' },
  { id: 4, text: '少放糖' },
  { id: 5, text: '不要吸管' },
  { id: 6, text: '不要餐具' }
]);

// 已选择的快捷备注
const selectedQuickNotes = ref([]);

// 显示备注编辑器
const showNoteEditor = () => {
  tempNote.value = orderInfo.value.note;
  
  // 根据当前备注解析已选择的快捷备注
  selectedQuickNotes.value = [];
  quickNotes.value.forEach(item => {
    if (tempNote.value.includes(item.text)) {
      selectedQuickNotes.value.push(item.id);
    }
  });
  
  isNoteEditorVisible.value = true;
};

// 选择快捷备注
const toggleQuickNote = (noteId) => {
  const index = selectedQuickNotes.value.indexOf(noteId);
  const noteItem = quickNotes.value.find(item => item.id === noteId);
  
  if (index > -1) {
    // 移除已选择的快捷备注
    selectedQuickNotes.value.splice(index, 1);
    
    // 从备注内容中移除
    tempNote.value = tempNote.value.replace(noteItem.text + '，', '').replace('，' + noteItem.text, '').replace(noteItem.text, '');
    // 清理连续的逗号
    tempNote.value = tempNote.value.replace(/，+/g, '，').replace(/^，|，$/g, '');
  } else {
    // 添加到已选择的快捷备注
    selectedQuickNotes.value.push(noteId);
    
    // 添加到备注内容
    if (tempNote.value) {
      if (!tempNote.value.endsWith('，')) {
        tempNote.value += '，';
      }
      tempNote.value += noteItem.text;
    } else {
      tempNote.value = noteItem.text;
    }
  }
};

// 保存备注内容
const saveNote = () => {
  orderInfo.value.note = tempNote.value;
  isNoteEditorVisible.value = false;
};

// 取消编辑备注
const cancelNote = () => {
  isNoteEditorVisible.value = false;
};

// 在页面加载时获取订单数据
onMounted(() => {
  const orderData = uni.getStorageSync('orderData');
  if (orderData) {
    // 如果是单个商品（立即购买），将其转换为数组形式
    if (orderData.products && !Array.isArray(orderData.products)) {
      orderInfo.value.products = [orderData.products];
    } else if (Array.isArray(orderData.products)) {
      orderInfo.value.products = orderData.products;
    }
  }
});

// 自动填写手机号
const autoFill = () => {
  const userInfo = uni.getStorageSync('userInfo');
  if (userInfo) {
    orderInfo.value.address.phone = userInfo.mobile;
  }
};

// 模拟下单接口
const payOrder = async (orderId) => {
  const res = await payOrderAPI(orderId);
  if (res.code === 200) {
    // 提示用户支付成功并跳转到订单页面
    uni.showToast({
      title: '支付成功',
      icon: 'success',
    })
    // 清空购物车数据
    uni.removeStorageSync('orderItems');
    uni.removeStorageSync('orderData');
    
    // 发送清空购物袋事件
    uni.$emit('clearShoppingCart');

    uni.showToast({
      title: '支付成功',
      icon: 'success',
    })
    // 跳转到订单详情
    setTimeout(() => {
      uni.switchTab({
        url: '/pages/record/index'
      })
    }, 1000)
  }else {
    uni.showToast({
      title: res.message,
      icon: 'error',
    })
  }
}

// 取消支付，订单状态为待支付
const cancelOrder = () => {
  uni.showToast({
    title: '取消支付',
    icon: 'none',
    mask: true
  })
  // 直接返回到订单详情
  uni.switchTab({
    url: '/pages/record/index'
  })
}

// 提交订单
const submitOrder = async () => {
  // 实际的订单提交逻辑
  // 创建后端所需要参数
  const items = uni.getStorageSync('orderItems');
  const data = {
    items: items,
    remark: orderInfo.value.note,
    couponId: selectedCoupon.value?.couponId
  }
  // 异步接口
  const result = await createOrder(data);
  // 如果成功则跳转到支付页面
  if (result.code === 200) {
    // 显示模态框，让用户进行支付
    uni.showModal({
      title: '模拟支付',
      content: '是否确认支付？',
      success: (res) => {
        if (res.confirm) {
          // 执行模拟支付
          if (!result.data || !result.data.orderId) {
            uni.showToast({
              title: '订单ID获取失败',
              icon: 'none'
            });
            return;
          }
          payOrder(result.data.orderId); 
          // 清除购物袋
          uni.removeStorageSync('orderItems');
          uni.removeStorageSync('orderData');
          
          // 发送清空购物袋事件
          uni.$emit('clearShoppingCart');

          uni.showToast({
            title: '支付成功',
            icon: 'success',
          })
          // 跳转到订单详情
          setTimeout(() => {
            uni.switchTab({
              url: '/pages/record/index'
            })
          }, 1000)
        }
        // 用户点击取消
        if (res.cancel) {
          // 触发取消订单方法
          cancelOrder(result.data.orderId);
        }
      }
    });
  }
}
</script>

<template>
  <view class="confirm-order">
    <!-- 门店信息 -->
    <view class="shop-section">
      <view class="shop-header">
        <view class="shop-info">
          <view class="shop-name-wrap">
            <text class="shop-name">{{ orderInfo.shop.name }}</text>
            <text class="shop-arrow">></text>
          </view>
          <view class="shop-distance">
            <image src="/static/order/location.png" class="location-icon" mode="aspectFit" />
            <text>距您{{ orderInfo.shop.distance }}</text>
          </view>
        </view>
        <view class="delivery-switch">
          <view 
            class="switch-option" 
            :class="{ active: deliveryType === '自取' }"
            @tap="deliveryType = '自取'"
          >自取</view>
          <view 
            class="switch-option" 
            :class="{ active: deliveryType === '外卖' }"
            @tap="deliveryType = '外卖'"
          >外卖</view>
        </view>
      </view>
    </view>
    
    <!-- 联系电话 -->
    <view class="section contact-section">
      <view class="section-item">
        <text class="item-label">联系电话</text>
        <view class="item-content">
          <text class="phone-number">{{ orderInfo.address.phone }}</text>
          <view class="call-btn" @tap="autoFill">自动填写</view>
        </view>
      </view>
    </view>
    
    <!-- 商品信息 -->
    <view class="section product-section">
      <view class="section-header">
        <text class="section-title">商品详情</text>
      </view>
      
      <!-- 商品列表 -->
      <view class="product-list">
        <view class="product-item" v-for="(item, index) in orderInfo.products" :key="index">
          <image :src="baseUrl.concat(item.image)" class="product-image" mode="aspectFill" />
          <view class="product-info">
            <text class="product-name">{{ item.name }}</text>
            <text class="product-specs">{{ item.desc }}</text>
          </view>
          <view class="product-price-qty">
            <text class="product-price">¥{{ item.price }}</text>
            <text class="product-qty">x{{ item.quantity }}</text>
          </view>
        </view>
      </view>
      
      <!-- 额外费用 -->
      <view class="extra-fee-list">
        <view class="fee-item" @click="showCouponPanel">
          <text class="fee-name">优惠券</text>
          <view class="fee-right">
            <text v-if="selectedCoupon" class="selected-coupon">
              {{ selectedCoupon.type === 1 ? 
                `满${selectedCoupon.threshold}减${selectedCoupon.amount}` : 
                (selectedCoupon.threshold > 0 ? `满${selectedCoupon.threshold}打${selectedCoupon.discount / 10}折` : `${selectedCoupon.discount / 10}折`) }}
            </text>
            <text v-else class="no-coupon">未使用</text>
            <text class="arrow">></text>
          </view>
        </view>
      </view>
      
      <!-- 备注 - 优化样式 -->
      <view class="section-item note-item" @tap="showNoteEditor">
        <view class="note-left">
          <text class="item-label">备注</text>
          <view class="note-tag" v-if="orderInfo.note">已填写</view>
        </view>
        <view class="note-right">
          <text class="note-content" v-if="orderInfo.note">{{ orderInfo.note }}</text>
          <text class="note-placeholder" v-else>口味、包装等要求</text>
          <text class="note-arrow">></text>
        </view>
      </view>
      
      <!-- 小计 -->
      <view class="subtotal">
        <text class="subtotal-text">共{{ orderInfo.products.length }}件商品，小计</text>
        <text class="subtotal-price">¥{{ totalPrice }}</text>
      </view>
      
      <!-- 优惠券减免 -->
      <view class="discount-info" v-if="selectedCoupon">
        <view class="discount-item">
          <text class="discount-label">优惠券减免</text>
          <text class="discount-value">-¥{{ discountAmount }}</text>
        </view>
        <view class="final-price">
          <text class="final-label">优惠后</text>
          <text class="final-value">¥{{ actualPrice }}</text>
        </view>
      </view>
    </view>
    
    <!-- 支付方式 -->
    <view class="section payment-section">
      <view class="section-item">
        <text class="item-label">支付方式</text>
        <view class="payment-method">
          <image src="/static/order/wxpay.png" class="payment-icon" mode="aspectFit" />
          <text class="payment-name">{{ orderInfo.payMethod }}</text>
        </view>
      </view>
    </view>
    
    <!-- 底部结算栏 -->
    <view class="footer">
      <view class="total-info">
        <text class="total-label">合计</text>
        <text class="total-price">¥{{ actualPrice }}</text>
      </view>
      <view class="pay-btn" @tap="submitOrder">支付</view>
    </view>
    
    <!-- 备注编辑弹出层 - 优化样式和添加快捷选项 -->
    <view class="note-editor-mask" v-if="isNoteEditorVisible" @tap="cancelNote"></view>
    <view class="note-editor" :class="{ visible: isNoteEditorVisible }">
      <view class="note-editor-header">
        <text class="cancel-btn" @tap="cancelNote">取消</text>
        <text class="title">订单备注</text>
        <text class="save-btn" @tap="saveNote">保存</text>
      </view>
      <view class="note-editor-body">
        <textarea 
          class="note-textarea" 
          v-model="tempNote" 
          placeholder="请输入口味、包装等特殊要求..." 
          maxlength="100"
          auto-height
        />
        <view class="char-count">{{ tempNote.length }}/100</view>
        
        <!-- 快捷选项区域 - 优化样式 -->
        <view class="quick-notes-container">
          <view class="quick-notes-grid">
            <view 
              v-for="item in quickNotes" 
              :key="item.id" 
              class="quick-note-item" 
              :class="{ active: selectedQuickNotes.includes(item.id) }"
              @tap="toggleQuickNote(item.id)"
            >
              {{ item.text }}
            </view>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 优惠券选择面板 -->
    <view class="coupon-panel-mask" v-show="isCouponPanelVisible" @click="hideCouponPanel"></view>
    <view class="coupon-panel" :class="{ visible: isCouponPanelVisible }">
      <view class="panel-header">
        <text class="title">选择优惠券</text>
        <text class="close" @click="hideCouponPanel">×</text>
      </view>
      <scroll-view scroll-y class="coupon-list">
        <!-- 添加空状态提示 -->
        <view class="coupon-empty" v-if="coupons.length === 0">
          <text class="iconfont icon-coupon-empty">🎫</text>
          <text class="empty-text">暂无可用优惠券</text>
        </view>
        <!-- 优惠券列表 -->
        <view 
          v-for="coupon in coupons" 
          :key="coupon.couponId"
          class="coupon-item"
          :class="{ 
            'selected': selectedCoupon?.couponId === coupon.couponId,
            'disabled': coupon.status === 1 || totalPrice < coupon.threshold
          }"
          @click="selectCoupon(coupon)"
        >
          <view class="coupon-left">
            <view class="amount" :class="{ 'discount': coupon.type === 2 }">
              <text v-if="coupon.type === 1">¥{{ coupon.amount }}</text>
              <text v-else>{{ (coupon.discount / 10).toFixed(1) }}折</text>
            </view>
            <text class="threshold" v-if="coupon.threshold > 0">满{{ coupon.threshold }}元可用</text>
            <text class="threshold" v-else>无门槛</text>
          </view>
          <view class="coupon-right">
            <view class="coupon-info">
              <text class="type">{{ coupon.type === 1 ? '满减券' : '折扣券' }}</text>
              <text class="date">有效期至：{{ formatTime(coupon.endTime) }}</text>
            </view>
            <text v-if="coupon.status === 1 || totalPrice < coupon.threshold" class="unusable">
              {{ coupon.status === 1 ? '不可用' : '未满足使用门槛' }}
            </text>
          </view>
        </view>
      </scroll-view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.confirm-order {
  min-height: 100vh;
  background-color: #f7f7f7;
  padding-bottom: calc(60px + env(safe-area-inset-bottom));
}

// 通用卡片样式
.section {
  background-color: #ffffff;
  margin: 12px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.section-item {
  padding: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.item-label {
  font-size: 14px;
  color: #333333;
  font-weight: 500;
}

// 店铺信息样式
.shop-section {
  background-color: #ffffff;
  margin: 12px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  
  .shop-header {
    padding: 16px;
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    
    .shop-info {
      flex: 1;
    }
    
    .shop-name-wrap {
      display: flex;
      align-items: center;
      margin-bottom: 8px;
      
      .shop-name {
        font-size: 16px;
        font-weight: 600;
        color: #333333;
      }
      
      .shop-arrow {
        font-size: 16px;
        color: #999999;
        margin-left: 4px;
      }
    }
    
    .shop-distance {
      display: flex;
      align-items: center;
      font-size: 12px;
      color: #999999;
      
      .location-icon {
        width: 16px;
        height: 16px;
        margin-right: 4px;
      }
    }
    
    .delivery-switch {
      display: flex;
      background-color: #f5f5f5;
      border-radius: 20px;
      padding: 2px;
      
      .switch-option {
        padding: 4px 10px;
        font-size: 12px;
        color: #666;
        border-radius: 20px;
        transition: all 0.3s;
        
        &.active {
          background-color: #1296db;
          color: #ffffff;
        }
      }
    }
  }
}

// 联系电话样式
.contact-section {
  .item-content {
    display: flex;
    align-items: center;
    
    .phone-number {
      font-size: 14px;
      color: #666666;
      margin-right: 12px;
    }
    
    .call-btn {
      padding: 4px 10px;
      font-size: 12px;
      color: #1296db;
      border: 1px solid #1296db;
      border-radius: 12px;
    }
  }
}

// 商品信息样式
.product-section {
  .section-header {
    padding: 16px;
    border-bottom: 1px solid #f5f5f5;
    
    .section-title {
      font-size: 14px;
      color: #333333;
      font-weight: 500;
    }
  }
  
  .product-list {
    padding: 0 16px;
    
    .product-item {
      display: flex;
      align-items: center;
      padding: 16px 0;
      border-bottom: 1px solid #f5f5f5;
      
      .product-image {
        width: 50px;
        height: 50px;
        border-radius: 4px;
        margin-right: 12px;
        background-color: #f5f5f5;
      }
      
      .product-info {
        flex: 1;
        
        .product-name {
          display: block;
          font-size: 14px;
          color: #333333;
          margin-bottom: 4px;
        }
        
        .product-specs {
          display: block;
          font-size: 12px;
          color: #999999;
        }
      }
      
      .product-price-qty {
        text-align: right;
        
        .product-price {
          display: block;
          font-size: 14px;
          color: #333333;
          margin-bottom: 4px;
        }
        
        .product-qty {
          display: block;
          font-size: 12px;
          color: #999999;
        }
      }
    }
  }
  
  .extra-fee-list {
    padding: 0 16px;
    
    .fee-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 12px 0;
      border-bottom: 1px solid #f5f5f5;
      
      .fee-name {
        font-size: 14px;
        color: #333333;
      }
      
      .fee-right {
        display: flex;
        align-items: center;
        
        .selected-coupon {
          font-size: 14px;
          color: #ff6b00;
        }
        
        .no-coupon {
          font-size: 14px;
          color: #999;
        }
        
        .arrow {
          margin-left: 8px;
          font-size: 14px;
          color: #999;
        }
      }
    }
  }
  
  // 修改备注样式
  .note-item {
    border-bottom: 1px solid #f5f5f5;
    
    .note-left {
      display: flex;
      align-items: center;
      
      .note-tag {
        margin-left: 8px;
        font-size: 10px;
        color: #1296db;
        background: rgba(18, 150, 219, 0.1);
        padding: 2px 6px;
        border-radius: 10px;
      }
    }
    
    .note-right {
      display: flex;
      align-items: center;
      
      .note-content {
        font-size: 14px;
        color: #333333;
        margin-right: 4px;
        max-width: 200px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
      
      .note-placeholder {
        font-size: 14px;
        color: #999999;
        margin-right: 4px;
      }
      
      .note-arrow {
        font-size: 14px;
        color: #999999;
      }
    }
  }
  
  .subtotal {
    display: flex;
    justify-content: flex-end;
    align-items: center;
    padding: 16px 16px 8px;  // 减小底部padding
    border-bottom: 1px dashed #eee;  // 添加虚线分隔
    
    .subtotal-text {
      font-size: 12px;
      color: #999999;
      margin-right: 8px;
    }
    
    .subtotal-price {
      font-size: 16px;
      color: #333333;
      font-weight: 500;
    }
  }
  
  // 优惠信息样式
  .discount-info {
    padding: 0 16px 16px;
    
    .discount-item {
      display: flex;
      justify-content: flex-end;
      align-items: center;
      margin-top: 8px;
      
      .discount-label {
        font-size: 12px;
        color: #999999;
        margin-right: 8px;
      }
      
      .discount-value {
        font-size: 14px;
        color: #ff6b00;
        font-weight: 500;
      }
    }
    
    .final-price {
      display: flex;
      justify-content: flex-end;
      align-items: center;
      margin-top: 8px;
      
      .final-label {
        font-size: 12px;
        color: #999999;
        margin-right: 8px;
      }
      
      .final-value {
        font-size: 16px;
        color: #ff6b00;
        font-weight: bold;
      }
    }
  }
}

// 支付方式样式
.payment-section {
  .payment-method {
    display: flex;
    align-items: center;
    
    .payment-icon {
      width: 20px;
      height: 20px;
      margin-right: 8px;
    }
    
    .payment-name {
      font-size: 14px;
      color: #666666;
    }
  }
}

// 备注编辑器样式 - 优化
.note-editor-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 998;
}

.note-editor {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ffffff;
  z-index: 999;
  border-radius: 16px 16px 0 0;
  transform: translateY(100%);
  transition: transform 0.3s ease;
  max-height: 90vh;
  min-height: 400px;
  overflow-y: auto;
  
  &.visible {
    transform: translateY(0);
  }
  
  .note-editor-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px;
    border-bottom: 1px solid #f5f5f5;
    background: #ffffff;
    position: sticky;
    top: 0;
    z-index: 2;
    
    .title {
      font-size: 16px;
      color: #333333;
      font-weight: 500;
    }
    
    .cancel-btn, .save-btn {
      font-size: 14px;
      padding: 6px 12px;
    }
    
    .cancel-btn {
      color: #999999;
    }
    
    .save-btn {
      color: #1296db;
    }
  }
  
  .note-editor-body {
    padding: 16px;
    position: relative;
    
    .note-textarea {
      width: 100%;
      min-height: 100px;
      border: 1px solid #e5e5e5;
      border-radius: 8px;
      padding: 14px;
      font-size: 15px;
      color: #333333;
      box-sizing: border-box;
      background: #f9f9f9;
      margin-bottom: 16px;
    }
    
    .char-count {
      position: absolute;
      right: 24px;
      top: 100px;
      font-size: 12px;
      color: #999999;
    }
    
    // 快捷选项样式 - 每行多个选项
    .quick-notes-container {
      margin-top: 10px;
      padding: 0;
      
      .quick-notes-grid {
        display: flex;
        flex-wrap: wrap;
        margin: -2px;
        
        .quick-note-item {
          width: calc(33.33% - 4px);
          margin: 2px;
          padding: 8px 0;
          background: rgba(18, 150, 219, 0.08);
          border-radius: 4px;
          font-size: 12px;
          color: #1296db;
          text-align: center;
          position: relative;
          box-sizing: border-box;
          
          &.active {
            color: #ffffff;
            background-color: #1296db;
            box-shadow: 0 2px 4px rgba(18, 150, 219, 0.2);
            
            &::after {
              content: '✓';
              position: absolute;
              right: 4px;
              top: 50%;
              transform: translateY(-50%);
              color: #ffffff;
              font-size: 10px;
              font-weight: bold;
            }
          }
        }
      }
    }
  }
}

// 底部支付栏
.footer {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  height: 60px;
  background: #ffffff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 16px;
  box-shadow: 0 -1px 5px rgba(0, 0, 0, 0.05);
  padding-bottom: env(safe-area-inset-bottom);
  
  .total-info {
    display: flex;
    align-items: baseline;
    
    .total-label {
      font-size: 14px;
      color: #333333;
      margin-right: 8px;
    }
    
    .total-price {
      font-size: 18px;
      font-weight: bold;
      color: #333333;
    }
  }
  
  .pay-btn {
    width: 90px;
    height: 40px;
    line-height: 40px;
    text-align: center;
    background: #1296db;
    color: #ffffff;
    font-size: 14px;
    font-weight: 500;
    border-radius: 20px;
  }
}

// 优惠券选择面板
.coupon-panel-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 999;
}

.coupon-panel {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #fff;
  border-radius: 16px 16px 0 0;
  padding: 20px;
  z-index: 1000;
  transform: translateY(100%);
  transition: transform 0.3s ease-out;
  max-height: 80vh;
  display: flex;
  flex-direction: column;

  &.visible {
    transform: translateY(0);
  }

  .panel-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-bottom: 15px;
    border-bottom: 1px solid #eee;

    .title {
      font-size: 16px;
      font-weight: bold;
      color: #333;
    }

    .close {
      font-size: 24px;
      color: #999;
      padding: 0 10px;
    }
  }

  .coupon-list {
    flex: 1;
    overflow-y: auto;
    padding: 10px 0;
    
    // 空状态样式
    .coupon-empty {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 60px 0;
      
      .icon-coupon-empty {
        font-size: 48px;
        color: #cccccc;
        margin-bottom: 16px;
      }
      
      .empty-text {
        font-size: 16px;
        color: #999999;
        margin-bottom: 24px;
      }
    }

    .coupon-item {
      display: flex;
      margin-bottom: 12px;
      background: #fff;
      border-radius: 12px;
      padding: 12px;
      position: relative;
      border: 1px solid #eee;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.02);
      transition: all 0.3s ease;
      box-sizing: border-box;
      width: 100%;
      
      &:active {
        transform: scale(0.98);
      }
      
      &.selected {
        border-color: #1296db;
        background: rgba(18, 150, 219, 0.05);
        
        &::after {
          content: '✓';
          position: absolute;
          right: 12px;
          top: 50%;
          transform: translateY(-50%);
          width: 20px;
          height: 20px;
          line-height: 20px;
          text-align: center;
          color: #fff;
          background: #1296db;
          border-radius: 50%;
          font-size: 12px;
        }
      }
      
      &.disabled {
        opacity: 0.6;
        background: #f8f8f8;
        pointer-events: none;
      }
      
      .coupon-left {
        width: 100px;
        flex-shrink: 0;
        text-align: center;
        border-right: 1px dashed #ddd;
        padding: 8px;
        margin-right: 12px;
        display: flex;
        flex-direction: column;
        justify-content: center;
        position: relative;
        
        &::before,
        &::after {
          content: '';
          position: absolute;
          right: -6px;
          width: 12px;
          height: 12px;
          border-radius: 50%;
          background: #f7f7f7;
        }
        
        &::before {
          top: -12px;
        }
        
        &::after {
          bottom: -12px;
        }
        
        .amount {
          font-size: 18px;
          font-weight: bold;
          color: #ff6b00;
          line-height: 1.2;
          margin-bottom: 6px;
          
          &.discount {
            font-size: 16px;
          }
        }
        
        .threshold {
          font-size: 10px;
          color: #666;
          background: rgba(18, 150, 219, 0.08);
          padding: 2px 4px;
          border-radius: 10px;
          display: inline-block;
          white-space: nowrap;
        }
      }
      
      .coupon-right {
        flex: 1;
        min-width: 0;
        display: flex;
        flex-direction: column;
        justify-content: center;
        padding-right: 45px;
        position: relative;
        
        .coupon-info {
          width: 100%;
          
          .type {
            display: block;
            font-size: 14px;
            color: #333;
            font-weight: 500;
            margin-bottom: 6px;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
          }
          
          .date {
            display: block;
            font-size: 12px;
            color: #999;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
            
            &::before {
              content: '';
              display: inline-block;
              width: 3px;
              height: 3px;
              background: #ddd;
              border-radius: 50%;
              margin-right: 4px;
              vertical-align: middle;
            }
          }
        }
        
        .unusable {
          position: absolute;
          right: 8px;
          top: 8px;
          transform: none;
          color: #999;
          font-size: 11px;
          background: rgba(0, 0, 0, 0.05);
          padding: 3px 6px;
          border-radius: 10px;
          white-space: nowrap;
        }
      }
    }
  }
}
</style> 