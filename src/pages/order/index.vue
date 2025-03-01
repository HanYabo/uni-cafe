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

// 计算内容区域的样式
const contentStyle = computed(() => {
  // 顶部偏移 = 状态栏 + 导航栏 + 店铺信息高度
  const topOffset = statusBarHeight.value + navBarHeight.value + 120 // 增加店铺信息高度为120px
  const bottomOffset = safeAreaInsets.value.bottom + 50 // 购物车高度50px
  return {
    paddingTop: `${topOffset}px`,
    paddingBottom: `${bottomOffset}px`
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

// 修改店铺信息的top计算
const shopInfoStyle = computed(() => {
  const menuButton = wx.getMenuButtonBoundingClientRect()
  const navHeight = menuButton.height + (menuButton.top - statusBarHeight.value) * 2
  return {
    top: `${statusBarHeight.value + navHeight}px`
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
    <view class="main-content" :style="mainContentStyle">
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

      <!-- 右侧商品列表 -->
      <scroll-view scroll-y class="product-list">
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
    </view>

    <!-- 购物车栏 -->
    <view 
      class="cart-bar"
      :style="{ 
        paddingBottom: safeAreaInsets.bottom + 'px'
      }"
    >
      <view class="cart-left">
        <view class="cart-icon">
          <image src="/static/order/cart.png" mode="aspectFit" />
          <text class="badge">6</text>
        </view>
        <text class="total">¥156</text>
      </view>
      <view class="checkout-btn">结算</view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.order-container {
  min-height: 100vh;
  background: #f7f7f7;
  padding-bottom: 50px;
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
  padding: 16px;
  height: 76px; // 固定店铺信息高度
  display: flex;
  justify-content: space-between;
  z-index: 99;

  .shop-header {
    padding: 12px 16px;
    display: flex;
    justify-content: space-between;
    align-items: flex-start;

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
  height: 100%; // 修改高度设置
  position: relative; // 添加相对定位

  .category-list {
    position: fixed; // 固定左侧分类列表
    left: 0;
    width: 90px;
    background: #f7f7f7;
    height: calc(100% - var(--window-top)); // 使用计算高度
    padding-bottom: 50px; // 为底部购物车留出空间

    .category-item {
      height: 48px;
      display: flex;
      align-items: center;
      padding: 0 16px;
      font-size: 14px;
      color: #666;
      position: relative;

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

  .product-list {
    margin-left: 90px; // 为左侧分类列表留出空间
    flex: 1;
    background: #fff;
    padding: 16px;
    min-height: calc(100vh - var(--window-top) - 50px); // 计算最小高度

    .section-title {
      font-size: 16px;
      font-weight: 500;
      margin-bottom: 16px;
    }

    .product-item {
      display: flex;
      margin-bottom: 20px;

      .product-image {
        width: 120px;
        height: 120px;
        border-radius: 8px;
        margin-right: 12px;
        background: #f5f5f5;
      }

      .product-info {
        flex: 1;
        display: flex;
        flex-direction: column;

        .product-name {
          font-size: 16px;
          font-weight: 500;
          margin-bottom: 4px;
        }

        .product-desc {
          font-size: 12px;
          color: #999;
          margin-bottom: 8px;
        }

        .tags {
          display: flex;
          gap: 8px;
          margin-bottom: 8px;

          .tag {
            font-size: 10px;
            color: #999;
            background: #f7f7f7;
            padding: 2px 6px;
            border-radius: 4px;
          }
        }

        .product-bottom {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-top: auto;

          .price {
            .symbol {
              font-size: 12px;
              color: #333;
            }

            .number {
              font-size: 20px;
              font-weight: 500;
              color: #333;
            }
          }

          .select-btn {
            padding: 6px 16px;
            background: #1296db;
            color: #fff;
            border-radius: 20px;
            font-size: 13px;
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

  .cart-left {
    flex: 1;
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 0 16px;
    min-width: 120px; // 设置最小宽度

    .cart-icon {
      position: relative;
      
      image {
        width: 24px;
        height: 24px;
      }

      .badge {
        position: absolute;
        top: -8px;
        right: -8px;
        background: #ff5339;
        color: #fff;
        font-size: 12px;
        padding: 2px 6px;
        border-radius: 8px;
      }
    }

    .total {
      font-size: 20px;
      font-weight: 500;
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

// 将滚动条样式移到最外层
.category-list,
.product-list {
  &::-webkit-scrollbar {
    display: none !important; // 添加 !important 确保生效
    width: 0 !important;
    height: 0 !important;
  }
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* IE and Edge */
  overflow: -moz-scrollbars-none; /* 老版本 Firefox */
}

// 移除之前嵌套在 .main-content 中的滚动条样式
.main-content {
  // ... 其他样式保持不变
}
</style>