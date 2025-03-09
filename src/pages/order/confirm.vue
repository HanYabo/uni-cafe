<script setup>
import { createOrder, payOrderAPI } from '@/api/order';
import { computed, onMounted, ref } from 'vue';

const baseUrl = 'http://localhost:9000';  // 本地后端服务地址

const orderInfo = ref({
  products: [],
  shop: {
    name: '郑州正弘城店',
    distance: '0.2公里',
    phone: '15085968569'
  },
  address: {
    name: '张三',
    phone: '138****8888',
    address: '广州市天河区融创购物中心1层',
    tag: '公司'
  },
  deliveryTime: '尽快送达（预计14:30送达）',
  pickupTime: '前面1杯/杯制作中，预计32分钟取餐',
  payMethod: '微信支付',
  note: ''
});



// 配送方式
const deliveryType = ref('自取');

// 计算总价
const totalPrice = computed(() => {
  if (!Array.isArray(orderInfo.value.products)) return 0;
  return orderInfo.value.products.reduce((total, item) => {
    return total + item.price * item.quantity;
  }, 0);
});

// 优惠券选中状态
const isCouponSelected = ref(false);

// 优惠券金额
const couponAmount = ref(5);

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

// 实际支付金额
const actualPrice = computed(() => {
  let total = totalPrice.value;
  // 如果选中了优惠券，减去优惠券金额
  if (isCouponSelected.value) {
    total = Math.max(0, total - couponAmount.value);
  }
  return total;
});

// 切换优惠券选中状态
const toggleCoupon = () => {
  isCouponSelected.value = !isCouponSelected.value;
};

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
    // 1.5秒后跳转到订单页面
    setTimeout(() => {
      uni.switchTab({
        url: '/pages/record/index'
      })
    }, 1500)
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
          <text class="phone-number">{{ orderInfo.shop.phone }}</text>
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
        <view class="fee-item">
          <text class="fee-name">优惠券</text>
          <view class="fee-right">
            <text class="fee-tag discount">满20减{{ couponAmount }}元</text>
            <view class="checkbox" :class="{ checked: isCouponSelected }" @tap="toggleCoupon"></view>
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
        
        .fee-tag {
          font-size: 12px;
          color: #999999;
          background: #f5f5f5;
          padding: 2px 6px;
          border-radius: 4px;
          margin-right: 8px;
          
          &.discount {
            color: #f0ad4e;
            background: #fff8e8;
          }
        }
        
        .fee-price {
          font-size: 14px;
          color: #333333;
        }
        
        .checkbox {
          width: 18px;
          height: 18px;
          border-radius: 50%;
          border: 1px solid #ddd;
          position: relative;
          
          &.checked {
            border-color: #1296db;
            background-color: #1296db;
            
            &::after {
              content: '';
              position: absolute;
              left: 5px;
              top: 2px;
              width: 6px;
              height: 10px;
              border: solid white;
              border-width: 0 2px 2px 0;
              transform: rotate(45deg);
            }
          }
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
    padding: 16px;
    
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
</style> 