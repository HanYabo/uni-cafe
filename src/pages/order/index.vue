<script setup>
import { computed, onMounted, ref } from 'vue';

// 分类数据
const categories = ref([
  { id: 1, name: '当季限定' },
  { id: 2, name: '每日鲜食' },
  { id: 3, name: '喜茶制冰' },
  { id: 4, name: '水果家族' },
  { id: 5, name: '茗茶波波家族' },
  { id: 6, name: '喜茶瓶装' },
  { id: 7, name: '周边/茶叶' },
  { id: 8, name: '加点小料' }
])

// 商品数据
const products = ref([
  {
    id: 1,
    name: '冷萃不知寒',
    desc: '大师监制高定系列，茶中别有韵，香极不知寒',
    price: 23,
    image: '/static/order/cart.png',
    tags: ['支持配送', '含乳制品']
  },
  {
    id: 2,
    name: '冷萃不知寒',
    desc: '大师监制高定系列，茶中别有韵，香极不知寒',
    price: 23,
    image: '/static/order/cart.png',
    tags: ['支持配送', '含乳制品']
  },
  {
    id: 3,
    name: '冷萃不知寒',
    desc: '大师监制高定系列，茶中别有韵，香极不知寒',
    price: 23,
    image: '/static/order/cart.png',
    tags: ['支持配送', '含乳制品']
  },
  {
    id: 4,
    name: '冷萃不知寒',
    desc: '大师监制高定系列，茶中别有韵，香极不知寒',
    price: 23,
    image: '/static/order/cart.png',
    tags: ['支持配送', '含乳制品']
  },
  {
    id: 5,
    name: '冷萃不知寒',
    desc: '大师监制高定系列，茶中别有韵，香极不知寒',
    price: 23,
    image: '/static/order/cart.png',
    tags: ['支持配送', '含乳制品']
  }
])

const currentCategory = ref(1)
const statusBarHeight = ref(0)
const navBarHeight = ref(44)
const menuButtonInfo = ref(null)
const safeAreaInsets = ref({ top: 0, bottom: 0 })

// 添加折叠状态控制
const isNoticeExpanded = ref(false)

// 添加配送方式状态控制
const deliveryType = ref('自取') // '自取' | '外卖'

// 添加购物袋状态控制
const isCartPanelVisible = ref(false);

// 添加全选状态
const allSelected = ref(true);

onMounted(() => {
  try {
    // 获取系统信息
    const sysInfo = wx.getSystemInfoSync()
    // 状态栏高度
    statusBarHeight.value = sysInfo.statusBarHeight || 20
    safeAreaInsets.value = {
      top: sysInfo.safeAreaInsets?.top || 0,
      bottom: sysInfo.safeAreaInsets?.bottom || 0
    }

    // 获取胶囊按钮信息
    const menuInfo = uni.getMenuButtonBoundingClientRect()
    menuButtonInfo.value = menuInfo

    // 计算导航栏高度
    if (menuInfo) {
      // 计算实际的导航栏高度
      const navHeight = (menuInfo.top - sysInfo.statusBarHeight) * 2 + menuInfo.height
      navBarHeight.value = navHeight || 44
    }
  } catch (e) {
    console.error('获取系统信息失败:', e)
    statusBarHeight.value = 20
    navBarHeight.value = 44
  }
})

// 计算店铺信息的位置和高度
const shopInfoStyle = computed(() => {
  const menuButton = wx.getMenuButtonBoundingClientRect()
  const navHeight = menuButton.height + (menuButton.top - statusBarHeight.value) * 2
  return {
    top: `${statusBarHeight.value + navHeight}px`,
    height: '120px',
    paddingTop: '12px',
    paddingBottom: '12px',
    boxSizing: 'border-box'
  }
})

// 计算主内容区域的样式
const contentStyle = computed(() => {
  const menuButton = wx.getMenuButtonBoundingClientRect()
  const navHeight = menuButton.height + (menuButton.top - statusBarHeight.value) * 2
  const shopInfoHeight = 120 // 店铺信息高度
  const topOffset = statusBarHeight.value + navHeight + shopInfoHeight
  const cartHeight = 50 // 购物车高度
  const bottomSafeArea = safeAreaInsets.value.bottom || 0

  return {
    top: `${topOffset}px`,
    height: `calc(100vh - ${topOffset}px - ${cartHeight}px - ${bottomSafeArea}px)`,
    boxSizing: 'border-box'
  }
})

// 店铺信息
const shopInfo = {
  name: '郑州正弘城店',
  distance: '125.2km',
  notice: '黑松露炒蛋可颂上新，浓郁黑松露酱包蛋糕...'
}

// 切换分类
const selectCategory = (id) => {
  currentCategory.value = id
}

// 添加导航栏相关的计算属性
const navStyle = computed(() => {
  const menuButton = wx.getMenuButtonBoundingClientRect()
  return {
    paddingTop: `${statusBarHeight.value}px`,
    height: `${menuButton.height + (menuButton.top - statusBarHeight.value) * 2}px`
  }
})

const navContentStyle = computed(() => {
  const menuButton = wx.getMenuButtonBoundingClientRect()
  return {
    height: `${menuButton.height}px`,
    lineHeight: `${menuButton.height}px`,
    top: `${menuButton.top - statusBarHeight.value}px`,
    paddingRight: `${menuButton.width + 12}px`
  }
})

// 修改主内容区域的marginTop计算
const mainContentStyle = computed(() => {
  const menuButton = wx.getMenuButtonBoundingClientRect()
  const navHeight = menuButton.height + (menuButton.top - statusBarHeight.value) * 2
  return {
    marginTop: `${statusBarHeight.value + navHeight + 76}px`
  }
})

// 切换配送方式
const switchDeliveryType = (type) => {
  deliveryType.value = type
}

// 添加搜索按钮位置计算
const searchBtnStyle = computed(() => {
  const menuButton = wx.getMenuButtonBoundingClientRect()
  return {
    right: `${menuButton.width + 24}px` // 24px 的安全距离
  }
})

// 切换购物袋面板显示状态
const toggleCartPanel = () => {
  isCartPanelVisible.value = !isCartPanelVisible.value;
};

// 关闭购物袋面板
const closeCartPanel = () => {
  isCartPanelVisible.value = false;
};

// 模拟购物袋数据
const cartItems = ref([
  {
    id: 1,
    name: '雪山多肉青提',
    desc: 'PLA可降解吸管(推荐),需要配2个(备选果肉)',
    price: 96,
    quantity: 1,
    image: '/static/order/cart.png',
    checked: true
  }
]);

// 计算总价
const totalPrice = computed(() => {
  return cartItems.value.reduce((total, item) => total + item.price * item.quantity, 0);
});

// 修改商品数量
const changeQuantity = (item, change) => {
  const newQuantity = item.quantity + change;
  if (newQuantity < 1) return;
  item.quantity = newQuantity;
};

// 切换全选状态
const toggleSelectAll = () => {
  allSelected.value = !allSelected.value;
  cartItems.value.forEach(item => {
    item.checked = allSelected.value;
  });
};

// 清空购物袋
const clearCart = () => {
  cartItems.value = [];
  closeCartPanel();
};
</script>

<template>
  <view class="order-container">
    <!-- 顶部导航栏 -->
    <view class="nav-bar" :style="navStyle">
      <view class="nav-content" :style="navContentStyle">
        <!-- 修改左侧logo为胶囊形状 -->
        <view class="logo-capsule">
          <image src="/static/order/logo.png" mode="aspectFit" />
          <text style="font-size: 14px">一起喝</text>
        </view>
        <!-- 右侧搜索按钮 -->
        <view class="nav-right" :style="searchBtnStyle">
          <view class="search-btn">
            <image src="/static/order/search.png" mode="aspectFit" />
          </view>
        </view>
      </view>
    </view>

    <!-- 店铺信息 -->
    <view class="shop-info" :style="shopInfoStyle">
      <view class="shop-header">
        <view class="left">
          <view class="shop-name">
            <view class="name-wrap">
              <image src="/static/order/star.png" class="star-icon" mode="aspectFit" />
              <text class="name">{{ shopInfo.name }}</text>
              <text class="arrow">></text>
            </view>
            <text class="distance">距离您{{ shopInfo.distance }}</text>
          </view>
          <view class="notice-wrap">
            <text class="notice" :class="{ 'expanded': isNoticeExpanded }">{{ shopInfo.notice }}</text>
            <text class="more" @tap="isNoticeExpanded = !isNoticeExpanded">
              {{ isNoticeExpanded ? '收起' : '展开' }}
            </text>
          </view>
        </view>
        <view class="right">
          <view class="delivery-switch">
            <text 
              :class="['switch-item', { active: deliveryType === '自取' }]"
              @tap="switchDeliveryType('自取')"
            >自取</text>
            <text 
              :class="['switch-item', { active: deliveryType === '外卖' }]"
              @tap="switchDeliveryType('外卖')"
            >外卖</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 主内容区 -->
    <view class="main-content" :style="contentStyle">
      <!-- 左侧分类导航 -->
      <scroll-view scroll-y class="category-list">
        <view
          v-for="item in categories"
          :key="item.id"
          class="category-item"
          :class="{ active: currentCategory === item.id }"
          @tap="selectCategory(item.id)"
        >
          {{ item.name }}
        </view>
      </scroll-view>

      <!-- 右侧商品列表 - 使用遮罩层隐藏滚动条 -->
      <view class="product-list-wrapper">
        <scroll-view 
          scroll-y 
          class="product-list" 
          :show-scrollbar="false"
          enhanced
        >
          <view class="section-title">当季限定</view>
          <view class="product-item" v-for="product in products" :key="product.id">
            <image :src="product.image" class="product-image" mode="aspectFill" />
            <view class="product-info">
              <text class="product-name">{{ product.name }}</text>
              <text class="product-desc">{{ product.desc }}</text>
              <view class="tags">
                <text v-for="(tag, index) in product.tags" :key="index" class="tag">{{ tag }}</text>
              </view>
              <view class="product-bottom">
                <view class="price">
                  <text class="symbol">¥</text>
                  <text class="number">{{ product.price }}</text>
                </view>
                <view class="select-btn">选规格</view>
              </view>
            </view>
          </view>
        </scroll-view>
        <!-- 添加遮罩层覆盖滚动条 -->
        <view class="scrollbar-mask"></view>
      </view>
    </view>

    <!-- 购物车栏 -->
    <view 
      class="cart-bar"
      :style="{ 
        paddingBottom: safeAreaInsets.bottom + 'px'
      }"
    >
      <view class="cart-left">
        <view class="cart-icon" @tap="toggleCartPanel">
          <image src="/static/order/cart.png" mode="aspectFit" />
          <text class="badge">{{ cartItems.length }}</text>
        </view>
        <text class="total">¥{{ totalPrice }}</text>
      </view>
      <view class="checkout-btn">结算</view>
    </view>

    <!-- 购物袋弹出面板 -->
    <view class="cart-panel-container" :class="{ visible: isCartPanelVisible }" @tap="closeCartPanel">
      <view class="cart-panel" @tap.stop>
        <view class="panel-header">
          <view class="select-all" @tap="toggleSelectAll">
            <view class="checkbox" :class="{ checked: allSelected }">
              <text class="check-icon" v-if="allSelected">✓</text>
            </view>
            <text>全选</text>
          </view>
          <view class="clear-cart" @tap="clearCart">
            <text>清空购物袋</text>
          </view>
        </view>
        
        <scroll-view scroll-y class="cart-items">
          <view class="cart-item" v-for="item in cartItems" :key="item.id">
            <image class="product-image" :src="item.image" mode="aspectFill" />
            <view class="product-info">
              <text class="product-name">{{ item.name }}</text>
              <text class="product-desc">{{ item.desc }}</text>
              <view class="price-quantity">
                <text class="price">¥{{ item.price }}</text>
                <view class="quantity-control">
                  <text class="minus" @tap="changeQuantity(item, -1)">－</text>
                  <text class="quantity">{{ item.quantity }}</text>
                  <text class="plus" @tap="changeQuantity(item, 1)">＋</text>
                </view>
              </view>
            </view>
          </view>
        </scroll-view>

        <view class="panel-footer">
          <view class="total-price">
            <text>合计：</text>
            <text class="price">¥{{ totalPrice }}</text>
          </view>
          <view class="checkout-btn">结算</view>
        </view>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.order-container {
  min-height: 100vh;
  background: #f7f7f7;
  position: relative;
  box-sizing: border-box;
}

.nav-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  background: #fff;
  z-index: 100;

  .nav-content {
    position: relative;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 16px;

    .logo-capsule {
      display: flex;
      align-items: center;
      height: 36px; // 增加胶囊高度
      background: #f8f8f8;
      border-radius: 18px; // 保持圆角为高度的一半
      padding: 0 16px; // 增加内边距
      border: 1px solid #e5e5e5;
      
      image {
        width: 30px; // 增加 logo 尺寸
        height: 30px;
        margin-right: 8px; // 增加右侧间距
      }
      
      text {
        font-size: 16px; // 增加文字大小
        color: #333;
      }
    }

    .nav-right {
      position: absolute;
      display: flex;
      align-items: center;

      .search-btn {
        width: 36px; // 保持与 logo 胶囊一致
        height: 36px;
        display: flex;
        align-items: center;
        justify-content: center;
        border: 1px solid #e5e5e5;
        border-radius: 50%;
        background: #fff;

        image {
          width: 20px; // 搜索图标也相应调大
          height: 20px;
        }
      }
    }
  }
}

.shop-info {
  position: fixed;
  left: 0;
  right: 0;
  background: #fff;
  z-index: 99;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);

  .shop-header {
    height: 100%;
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    padding: 0 16px;

    .left {
      flex: 1;
      padding-right: 16px;

      .shop-name {
        margin-bottom: 8px;

        .name-wrap {
          display: flex;
          align-items: center;
          margin-bottom: 4px;

          .star-icon {
            width: 16px;
            height: 16px;
            margin-right: 4px;
          }

          .name {
            font-size: 16px;
            font-weight: 500;
            color: #333;
          }

          .arrow {
            font-size: 12px;
            color: #999;
            margin-left: 4px;
          }
        }

        .distance {
          font-size: 12px;
          color: #999;
          display: block;
        }
      }

      .notice-wrap {
        position: relative;
        padding-right: 80px; // 增加右侧预留空间

        .notice {
          font-size: 12px;
          color: #666;
          line-height: 1.4;
          display: -webkit-box;
          -webkit-box-orient: vertical;
          -webkit-line-clamp: 1;
          overflow: hidden;
          padding-right: 10px; // 与展开按钮保持一定间距

          &.expanded {
            -webkit-line-clamp: unset;
          }
        }

        .more {
          position: absolute;
          right: 24px; // 调整到更右侧的位置
          bottom: 0;
          font-size: 12px;
          color: #999;
          background: #fff;
          cursor: pointer;
          display: flex;
          align-items: center;
          white-space: nowrap;
          padding: 0 20px 0 10px; // 调整内边距
          
          &::after {
            content: '';
            position: absolute;
            right: 0; // 箭头紧贴文字
            top: 50%;
            transform: translateY(-50%) rotate(0deg);
            width: 12px;
            height: 12px;
            background: url('/static/order/arrow-down.png') no-repeat center/contain;
            transition: transform 0.3s;
          }
        }
      }

      // 展开状态下箭头旋转
      .notice.expanded + .more::after {
        transform: translateY(-50%) rotate(180deg);
      }
    }

    .right {
      .delivery-switch {
        display: flex;
        background: #f5f5f5;
        border-radius: 20px;
        padding: 2px;

        .switch-item {
          padding: 4px 16px;
          font-size: 13px;
          color: #666;
          border-radius: 16px;
          transition: all 0.3s;
          cursor: pointer;

          &.active {
            background: #333;
            color: #fff;
          }
        }
      }
    }
  }
}

.main-content {
  display: flex;
  width: 100%;
  position: fixed;
  left: 0;
  right: 0;
  background: #f7f7f7;
  z-index: 1;
  overflow: hidden;

  .category-list {
    width: 90px;
    height: 100%;
    background: #f7f7f7;
    overflow-y: auto;
    -webkit-overflow-scrolling: touch;
    box-sizing: border-box;

    &::-webkit-scrollbar {
      width: 0;
      display: none;
    }
    scrollbar-width: none;
    -ms-overflow-style: none;

    .category-item {
      height: 48px;
      display: flex;
      align-items: center;
      padding: 0 16px;
      font-size: 14px;
      color: #666;
      position: relative;
      box-sizing: border-box;
      white-space: nowrap;

      &.active {
        background: #fff;
        color: #333;
        font-weight: 500;

        &::before {
          content: '';
          position: absolute;
          left: 0;
          top: 50%;
          transform: translateY(-50%);
          width: 4px;
          height: 24px;
          background: #1296db;
          border-radius: 0 2px 2px 0;
        }
      }
    }
  }

  .product-list-wrapper {
    flex: 1;
    height: 100%;
    overflow: hidden;
    position: relative;
    background: #fff;
  }

  .scrollbar-mask {
    position: absolute;
    top: 0;
    right: 0;
    width: 12px; /* 减小遮罩宽度，只覆盖滚动条区域 */
    height: 100%;
    background: #fff;
    z-index: 10;
    pointer-events: none;
  }

  .product-list {
    height: 100%;
    width: calc(100% + 12px); /* 减小额外宽度 */
    padding: 16px;
    padding-right: 28px; /* 减小右侧padding */
    box-sizing: border-box;
    overflow-y: scroll;
    position: absolute;
    top: 0;
    left: 0;
    -webkit-overflow-scrolling: touch;
    
    /* 其他滚动条隐藏样式保持不变 */
    &::-webkit-scrollbar {
      width: 0 !important;
      display: none !important;
      background: transparent !important;
    }
    scrollbar-width: none !important;
    -ms-overflow-style: none !important;
    scrollbar-color: transparent transparent !important;
    
    .section-title {
      font-size: 16px;
      font-weight: 500;
      margin-bottom: 16px;
      position: sticky;
      top: 0;
      background: #fff;
      z-index: 2;
      padding: 8px 0;
    }

    .product-item {
      display: flex;
      margin-bottom: 16px; /* 减小底部间距 */

      .product-image {
        width: 90px; /* 减小图片尺寸 */
        height: 90px; /* 减小图片尺寸 */
        border-radius: 6px; /* 略微减小圆角 */
        margin-right: 10px; /* 减小右侧间距 */
        background: #f5f5f5;
      }

      .product-info {
        flex: 1;
        display: flex;
        flex-direction: column;

        .product-name {
          font-size: 14px; /* 减小名称字体 */
          font-weight: 500;
          margin-bottom: 2px; /* 减小底部间距 */
        }

        .product-desc {
          font-size: 11px; /* 减小描述字体 */
          color: #999;
          margin-bottom: 6px; /* 减小底部间距 */
          line-height: 1.3; /* 减小行高 */
        }

        .tags {
          display: flex;
          gap: 6px; /* 减小标签间距 */
          margin-bottom: 6px; /* 减小底部间距 */

          .tag {
            font-size: 9px; /* 减小标签字体 */
            color: #999;
            background: #f7f7f7;
            padding: 1px 4px; /* 减小内部间距 */
            border-radius: 3px; /* 减小圆角 */
          }
        }

        .product-bottom {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-top: auto;

          .price {
            .symbol {
              font-size: 11px; /* 减小价格符号 */
              color: #333;
            }

            .number {
              font-size: 18px; /* 减小价格数字 */
              font-weight: 500;
              color: #333;
            }
          }

          .select-btn {
            padding: 4px 12px; /* 减小按钮内部间距 */
            background: #1296db;
            color: #fff;
            border-radius: 16px; /* 减小圆角 */
            font-size: 12px; /* 减小按钮字体 */
          }
        }
      }
    }
  }
}

.cart-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 50px;
  background: #fff;
  display: flex;
  align-items: center;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
  z-index: 99;
  box-sizing: border-box;

  .cart-left {
    flex: 1;
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 0 16px;
    min-width: 120px; // 设置最小宽度
    position: relative; // 添加相对定位
    padding-left: 70px; // 为购物车图标腾出更多空间

    .cart-icon {
      position: absolute;
      left: 16px; // 调整位置
      top: -24px; // 进一步向上偏移，使其更加浮在容器上方
      width: 48px; // 尺寸保持不变
      height: 48px; 
      background: #fff; // 白色背景
      border-radius: 50%; // 圆形
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: 0 4px 10px rgba(0, 0, 0, 0.15); // 阴影效果
      border: 1px solid #f0f0f0; // 添加边框
      z-index: 100; // 确保在最上层
      
      image {
        width: 26px;
        height: 26px;
      }

      .badge {
        position: absolute;
        top: -5px;
        right: -5px;
        background: #ff5339;
        color: #fff;
        font-size: 12px;
        min-width: 18px;
        height: 18px;
        line-height: 18px;
        text-align: center;
        border-radius: 10px;
        padding: 0 4px;
        z-index: 101; // 确保徽章在图标上方
      }
    }

    .total {
      font-size: 20px;
      font-weight: 500;
      margin-left: 12px; // 向右挪动价格
    }
  }

  .checkout-btn {
    height: 50px;
    line-height: 50px;
    padding: 0 32px;
    background: #1296db;
    color: #fff;
    font-size: 14px;
    text-align: center;
    min-width: 100px; // 设置最小宽度
  }
}

// 添加安全区域适配
:deep(.uni-page-head) {
  padding-top: constant(safe-area-inset-top);
  padding-top: env(safe-area-inset-top);
}

.cart-panel-container {
  position: fixed;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0);
  z-index: 999;
  visibility: hidden;
  transition: all 0.3s ease;

  &.visible {
    visibility: visible;
    background: rgba(0, 0, 0, 0.5);

    .cart-panel {
      transform: translateY(0);
    }
  }

  .cart-panel {
    position: absolute;
    left: 0;
    right: 0;
    bottom: 0;
    background: #fff;
    border-radius: 20px 20px 0 0;
    transform: translateY(100%);
    transition: transform 0.3s ease;
    padding-bottom: calc(50px + constant(safe-area-inset-bottom));
    padding-bottom: calc(50px + env(safe-area-inset-bottom));

    .panel-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 16px;
      border-bottom: 1px solid #f5f5f5;

      .select-all {
        display: flex;
        align-items: center;
        gap: 8px;

        .checkbox {
          width: 20px;
          height: 20px;
          border-radius: 50%;
          border: 1px solid #ddd;
          display: flex;
          align-items: center;
          justify-content: center;

          &.checked {
            background: #1296db;
            border-color: #1296db;
          }

          .check-icon {
            color: #fff;
            font-size: 12px;
            font-weight: bold;
          }
        }

        text {
          font-size: 14px;
          color: #333;
        }
      }

      .clear-cart {
        font-size: 14px;
        color: #1296db;
        cursor: pointer;
      }
    }

    .cart-items {
      max-height: 60vh;
      padding: 16px;
      padding-right: 16px; // 确保右侧padding不会导致内容溢出
      box-sizing: border-box; // 添加盒模型设置

      .cart-item {
        display: flex;
        margin-bottom: 16px;
        width: 100%; // 确保宽度不超出容器

        .product-image {
          width: 80px;
          height: 80px;
          border-radius: 8px;
          margin-right: 12px;
          background: #f5f5f5;
        }

        .product-info {
          flex: 1;
          display: flex;
          flex-direction: column;
          min-width: 0; // 防止flex子元素溢出

          .product-name {
            font-size: 16px;
            color: #333;
            font-weight: 500;
            margin-bottom: 4px;
          }

          .product-desc {
            font-size: 12px;
            color: #999;
            margin-bottom: 8px;
          }

          .price-quantity {
            display: flex;
            justify-content: space-between;
            align-items: center;
            width: 100%; // 确保宽度填满容器

            .price {
              font-size: 18px;
              color: #333;
              font-weight: bold;
            }

            .quantity-control {
              display: flex;
              align-items: center;
              border: 1px solid #eee;
              border-radius: 4px;
              overflow: hidden;
              margin-left: 8px; // 与价格保持一定距离
              flex-shrink: 0; // 防止被压缩

              .minus, .plus {
                width: 28px;
                height: 28px;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 14px;
                color: #333;
                background: #f7f7f7;
              }

              .quantity {
                width: 40px;
                height: 28px;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 14px;
                color: #333;
                border-left: 1px solid #eee;
                border-right: 1px solid #eee;
                background: #fff; // 添加白色背景
              }
            }
          }
        }
      }
    }

    .panel-footer {
      position: absolute;
      left: 0;
      right: 0;
      bottom: 0;
      height: 50px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 0;  // 移除内边距
      background: #fff;
      border-top: 1px solid #f5f5f5;

      .total-price {
        flex: 1; // 让总价占据剩余空间
        padding-left: 16px; // 单独设置左边距
        font-size: 14px;
        color: #333;

        .price {
          font-size: 20px;
          font-weight: bold;
          color: #333;
        }
      }

      .checkout-btn {
        height: 50px;
        line-height: 50px;
        padding: 0 32px;
        background: #1296db;
        color: #fff;
        font-size: 14px;
        text-align: center;
        min-width: 100px;
        border-radius: 0;
      }
    }
  }
}
</style>