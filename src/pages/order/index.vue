<script setup>
import { getCategoryWithProducts } from '@/api/category'
import { onShow } from '@dcloudio/uni-app'
import { computed, onMounted, onUnmounted, reactive, ref } from 'vue'

// 分类数据
const categories = ref(null)

const baseUrl = 'http://localhost:9000'


// 添加页面返回标记
const isPageReturning = ref(false)

onShow(async () => {
  // 如果是从确认订单页面返回，要确保重置商品详情
  if (isPageReturning.value) {
    resetProductDetail()
    isPageReturning.value = false
  }
  
  await getCategoryWithProducts().then(res => {
    categories.value = res.data
  })
  
})

// 添加重置商品详情的方法
const resetProductDetail = () => {
  // 关闭商品详情面板
  isProductDetailVisible.value = false
  // 重置商品数量
  productQuantity.value = 1
  // 清空当前商品数据
  Object.keys(currentProduct).forEach(key => {
    delete currentProduct[key]
  })
}

const currentCategory = ref(0)
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

// 添加商品详情面板显示状态控制
const isProductDetailVisible = ref(false);
const currentProduct = reactive({});


// 添加商品描述展开状态控制
const isDescriptionExpanded = ref(false);

// 添加商品数量控制
const productQuantity = ref(1);

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

  // 添加购物袋清空事件监听
  uni.$on('clearShoppingCart', handleClearCart)
})

// 添加页面卸载时的清理
onUnmounted(() => {
  // 移除事件监听
  uni.$off('clearShoppingCart', handleClearCart)
})

// 处理清空购物袋的方法
const handleClearCart = () => {
  cartItems.items = []
  isCartPanelVisible.value = false
}

// 计算店铺信息的位置和高度
const shopInfoStyle = computed(() => {
  const defaultStyle = {
    top: `${statusBarHeight.value + navBarHeight.value}px`,
    height: '120px',
    paddingTop: '0',
    paddingBottom: '12px',
    boxSizing: 'border-box'
  }
  
  if (!menuButtonInfo.value?.height) return defaultStyle
  
  const menuButton = menuButtonInfo.value
  const navHeight = menuButton.height + (menuButton.top - statusBarHeight.value) * 2
  return {
    top: `${statusBarHeight.value + navHeight}px`,
    height: '120px',
    paddingTop: '0',
    paddingBottom: '12px',
    boxSizing: 'border-box'
  }
})

// 计算主内容区域的样式
const contentStyle = computed(() => {
  const defaultStyle = {
    top: `${statusBarHeight.value + navBarHeight.value + 120}px`,
    height: `calc(100vh - ${statusBarHeight.value + navBarHeight.value + 120 + 50}px)`,
    boxSizing: 'border-box'
  }
  
  if (!menuButtonInfo.value?.height) return defaultStyle
  
  const menuButton = menuButtonInfo.value
  const navHeight = menuButton.height + (menuButton.top - statusBarHeight.value) * 2
  const shopInfoHeight = 120
  const topOffset = statusBarHeight.value + navHeight + shopInfoHeight
  const cartHeight = 50
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
  notice: '悠尼咖啡，美好的一天，从一杯好的咖啡开始！'
}

// 切换分类
const selectCategory = (id) => {
  currentCategory.value = id
}

// 添加导航栏相关的计算属性
const navStyle = computed(() => {
  const defaultStyle = {
    paddingTop: `${statusBarHeight.value}px`,
    height: `${navBarHeight.value}px`
  }
  
  if (!menuButtonInfo.value?.height) return defaultStyle
  
  const menuButton = menuButtonInfo.value
  return {
    paddingTop: `${statusBarHeight.value}px`,
    height: `${menuButton.height + (menuButton.top - statusBarHeight.value) * 2}px`
  }
})

const navContentStyle = computed(() => {
  const defaultStyle = {
    height: `${navBarHeight.value}px`,
    lineHeight: `${navBarHeight.value}px`,
    top: '0',
    paddingRight: '12px'
  }
  
  if (!menuButtonInfo.value?.height) return defaultStyle
  
  const menuButton = menuButtonInfo.value
  return {
    height: `${menuButton.height}px`,
    lineHeight: `${menuButton.height}px`,
    top: `${menuButton.top - statusBarHeight.value}px`,
    paddingRight: `${menuButton.width + 12}px`
  }
})


// 切换配送方式
const switchDeliveryType = (type) => {
  deliveryType.value = type
  if(type === '外卖') {
    isNoticeExpanded.value = false
  }
}

// 添加搜索按钮位置计算
const searchBtnStyle = computed(() => {
  const defaultStyle = {
    right: '12px'
  }
  
  if (!menuButtonInfo.value?.width) return defaultStyle
  
  const menuButton = menuButtonInfo.value
  return {
    right: `${menuButton.width + 24}px`
  }
})

// 切换购物袋面板显示状态
const toggleCartPanel = () => {
  if(cartItems.items.length === 0) {
    uni.showToast({
      title: '购物袋为空',
      icon: 'none'
    })
    return
  }
  isCartPanelVisible.value = !isCartPanelVisible.value;
};

// 关闭购物袋面板
const closeCartPanel = () => {
  isCartPanelVisible.value = false;
};

// 修改购物袋数据结构，添加选中状态
const cartItems = reactive({
  items: []
});

// 计算总价函数，只计算选中的商品
const totalPrice = computed(() => {
  return cartItems.items.reduce((total, item) => total + (item.selected ? item.unitPrice * item.quantity : 0), 0);
});

// 修改全选状态检测函数，确保对应使用selected属性
const isAllSelected = computed(() => {
  return cartItems.items.length > 0 && cartItems.items.every(item => item.selected);
});

// 切换商品选中状态
const toggleItemSelected = (item) => {
  item.selected = !item.selected;
  // 更新全选状态
  allSelected.value = isAllSelected.value;
};

// 切换全选状态
const toggleSelectAll = () => {
  allSelected.value = !allSelected.value;
  cartItems.items.forEach(item => {
    item.selected = allSelected.value;
  });
};

// 修改商品数量
const changeQuantity = (item, change) => {
  const newQuantity = item.quantity + change;
  if (newQuantity < 1) {
    uni.showToast({
      title: '数量不能小于1',
      icon: 'none'
    })
    return
  }
  item.quantity = newQuantity;
};

// 清空购物袋
const clearCart = () => {
  cartItems.items = [];
  closeCartPanel();
};

// 开启商品详情面板
const openProductDetail = (product) => {
  Object.assign(currentProduct, product);
  // 初始化每个规格的选中值
  if (currentProduct.specs) {
    currentProduct.specs.forEach(spec => {
      // 如果没有选中值，则查找默认值
      if (!spec.selectedValueId) {
        const defaultValue = spec.values.find(v => v.isDefault);
        if (defaultValue) {
          spec.selectedValueId = defaultValue.specValueId;
        }
      }
    });
  }
  isProductDetailVisible.value = true;
};

// 关闭商品详情面板
const closeProductDetail = () => {
  isProductDetailVisible.value = false;
  // 重置商品数量，但保留商品数据以便再次打开面板时显示
  productQuantity.value = 1;
};

// 选择规格
const selectOption = (spec, specValueId) => {
  spec.selectedValueId = specValueId;
};

// 计算选中规格的价格
const selectedPrice = computed(() => {
  if (!currentProduct.productId) return 0;
  
  let unitPrice = currentProduct.basePrice || 0;
  
  // 计算所有规格的附加价格
  if (currentProduct.specs) {
    currentProduct.specs.forEach(spec => {
      const selectedValue = spec.values.find(v => 
        v.specValueId === spec.selectedValueId || 
        (!spec.selectedValueId && v.isDefault)
      );
      if (selectedValue && selectedValue.extraPrice) {
        unitPrice += selectedValue.extraPrice;
      }
    });
  }
  
  return unitPrice; // 只返回单价，不乘以数量
});

// 计算选中规格的总价（单价×数量）
const selectedTotalPrice = computed(() => {
  return selectedPrice.value * productQuantity.value;
});

// 计算已选规格文本
const selectedSpecsText = computed(() => {
  if (!currentProduct.productId) return '';
  
  const selectedSpecs = [];
  if (currentProduct.specs) {
    currentProduct.specs.forEach(spec => {
      const selectedValue = spec.values.find(v => 
        v.specValueId === spec.selectedValueId || 
        (!spec.selectedValueId && v.isDefault)
      );
      if (selectedValue) {
        selectedSpecs.push(selectedValue.value);
      }
    });
  }
  
  return selectedSpecs.join('，');
});

// 添加商品到购物车方法
const addToCart = () => {
  if (!currentProduct.productId) return;
  
  // 构造规格参数
  const specs = [];
  if (currentProduct.specs) {
    currentProduct.specs.forEach(spec => {
      const selectedValue = spec.values.find(v => 
        v.specValueId === spec.selectedValueId || 
        (!spec.selectedValueId && v.isDefault)
      );
      if (selectedValue) {
        specs.push({
          specId: spec.specId,
          specName: spec.name,
          specValueId: selectedValue.specValueId,
          specValue: selectedValue.value
        });
      }
    });
  }
  
  // 构造购物车项
  const newItem = {
    productId: currentProduct.productId,
    categoryId: currentProduct.categoryId,
    name: currentProduct.name,
    desc: selectedSpecsText.value,
    unitPrice: selectedPrice.value, // 单价
    price: selectedTotalPrice.value / productQuantity.value, // 保持兼容性
    quantity: productQuantity.value,
    image: currentProduct.mainImage,
    selected: true,
    specs: specs
  };
  
  cartItems.items.push(newItem);
  closeProductDetail();
  productQuantity.value = 1; // 重置数量
};

// 修改立即购买方法
const buyNow = () => {
  if (!currentProduct.productId) return;
  
  // 构造规格参数
  const specs = [];
  if (currentProduct.specs) {
    currentProduct.specs.forEach(spec => {
      const selectedValue = spec.values.find(v => 
        v.specValueId === spec.selectedValueId || 
        (!spec.selectedValueId && v.isDefault)
      );
      if (selectedValue) {
        specs.push({
          specId: spec.specId,
          specName: spec.name,
          specValueId: selectedValue.specValueId,
          specValue: selectedValue.value
        });
      }
    });
  }
  
  // 构造订单数据
  const orderData = {
    products: {
      productId: currentProduct.productId,
      categoryId: currentProduct.categoryId,
      name: currentProduct.name,
      desc: selectedSpecsText.value,
      specs: specs,
      price: selectedPrice.value,
      quantity: productQuantity.value,
      image: currentProduct.mainImage
    }
  };
  
  // 构造orderItems数据结构
  const orderItems = [{
    productId: currentProduct.productId,
    quantity: productQuantity.value,
    specs: currentProduct.specs.map(spec => {
      const selectedValue = spec.values.find(v => 
        v.specValueId === spec.selectedValueId || 
        (!spec.selectedValueId && v.isDefault)
      );
  return {
        specId: spec.specId,
        specValueId: selectedValue.specValueId
      };
    })
  }];
  
  // 将订单数据存储到本地
  uni.setStorageSync('orderData', orderData);
  uni.setStorageSync('orderItems', orderItems);
  
  // 重置商品详情数据
  resetProductDetail();
  
  // 标记页面即将离开，准备后续返回
  isPageReturning.value = true
  
  // 跳转到确认订单页面
  uni.navigateTo({
    url: '/pages/order/confirm'
  });
};

// 切换商品描述展开状态
const toggleDescription = () => {
  isDescriptionExpanded.value = !isDescriptionExpanded.value;
};

// 添加商品数量调整方法
const changeProductQuantity = (change) => {
  const newQuantity = productQuantity.value + change;
  if (newQuantity < 1) {
    uni.showToast({
      title: '数量不能小于1',
      icon: 'none'
    });
    return;
  }
  if (newQuantity > 99) {
    uni.showToast({
      title: '单次最多购买99件',
      icon: 'none'
    });
    return;
  }
  productQuantity.value = newQuantity;
};

// 结算方法，携带购物车参数跳转到confirm页面
const handleCheckout = () => {
  // 进行登录状态判断 如果未登录则提示用户并跳转到登录页面
  const userInfo = uni.getStorageSync('userInfo')
  if(!userInfo) {
    uni.showToast({
      title: '请先登录',
      icon: 'none'
    })
    uni.navigateTo({
      url: '/pages/login/index'
    })
    return
  }
  // 进行购物车非空判断
  if(cartItems.items.length === 0) {
    uni.showToast({
      title: '购物袋为空',
      icon: 'none'
    })
    return
  }

  // 获取选中的商品
  const selectedItems = cartItems.items.filter(item => item.selected);
  
  // 构造订单数据
  const orderData = {
    products: selectedItems.map(item => ({
      productId: item.productId,
      categoryId: item.categoryId,
      name: item.name,
      desc: selectedSpecsText.value,
      specs: item.specs,
      price: item.unitPrice || item.price,
      quantity: item.quantity,
      image: item.image
    }))
  };

  // 构造orderItems数据结构
  const orderItems = selectedItems.map(item => ({
    productId: item.productId,
    quantity: item.quantity,
    specs: item.specs.map(spec => ({
      specId: spec.specId,
      specValueId: spec.specValueId
    }))
  }));

  // 将订单数据存储到本地
  uni.setStorageSync('orderData', orderData);
  uni.setStorageSync('orderItems', orderItems);
  
  // 标记页面即将离开，准备后续返回
  isPageReturning.value = true;
  
  // 跳转到确认订单页面
  uni.navigateTo({
    url: '/pages/order/confirm'
  });
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
              <image src="/static/order/star.png" class="star-icon" mode="aspectFit" v-if="deliveryType === '自取'"/>
              <text class="name" v-if="deliveryType === '自取'">{{ shopInfo.name }}</text>
              <text class="name" v-else>收货地址</text>
              <text class="arrow">></text>
            </view>
            <text class="distance" v-if="deliveryType === '自取'">距离您{{ shopInfo.distance }}</text>
            <view class="address-info" v-else>
              <text class="address">郑州市二七区正弘城店</text>
              <text class="contact">张三 138****8888</text>
          </view>
          </view>
          <view class="notice-wrap" v-show="deliveryType === '自取'">
            <text class="notice" :class="{ 'expanded': isNoticeExpanded }">{{ shopInfo.notice }}</text>
          </view>
        </view>
        <view class="right">
          <view class="delivery-switch">
            <view class="slider-bg" :class="{ 'slide-right': deliveryType === '外卖' }"></view>
            <text 
              :class="['switch-item', { active: deliveryType === '自取' }]"
              @tap="switchDeliveryType('自取')"
            >自取</text>
            <text 
              :class="['switch-item', { active: deliveryType === '外卖' }]"
              @tap="switchDeliveryType('外卖')"
            >外卖</text>
          </view>
          <text class="more" @tap="isNoticeExpanded = !isNoticeExpanded">
            {{ isNoticeExpanded ? '收起' : '展开' }}
          </text>
        </view>
      </view>
    </view>

    <!-- 主内容区 -->
    <view class="main-content" :style="contentStyle">
      <!-- 左侧分类导航 -->
      <scroll-view scroll-y class="category-list">
        <view
          v-for="(item, index) in categories"
          :key="index"
          class="category-item"
          :class="{ active: currentCategory === index }"
          @tap="selectCategory(index)"
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
          <template v-if="categories && categories[currentCategory]">
            <view class="section-title">{{ categories[currentCategory].name }}</view>
            <view class="product-item" v-for="product in categories[currentCategory].products" :key="product.productId" @tap="openProductDetail(product)">
              <image :src="baseUrl.concat(product.mainImage)" class="product-image" mode="aspectFill" />
          <view class="product-info">
            <text class="product-name">{{ product.name }}</text>
                <text class="product-desc">{{ product.description }}</text>
            <view class="tags">
                  <text class="tag">甄选品质</text>
            </view>
            <view class="product-bottom">
              <view class="price">
                <text class="symbol">¥</text>
                    <text class="number">{{ product.basePrice }}</text>
              </view>
                  <view class="select-btn" @tap.stop="openProductDetail(product)">选规格</view>
            </view>
          </view>
            </view>
          </template>
          <view v-else class="loading-state">
            <text>加载中...</text>
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
          <text class="badge">{{ cartItems.items.length }}</text>
        </view>
        <text class="total">¥{{ totalPrice }}</text>
      </view>
      <view class="checkout-btn" @tap="handleCheckout">结算</view>
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
          <view class="cart-item" v-for="item in cartItems.items" :key="item.id">
            <!-- 添加商品勾选框 -->
            <view class="item-checkbox" @tap="toggleItemSelected(item)">
              <view class="checkbox" :class="{ checked: item.selected }">
                <text class="check-icon" v-if="item.selected">✓</text>
              </view>
            </view>
            <image class="product-image" :src="baseUrl.concat(item.image)" mode="aspectFill" />
            <view class="product-info">
              <text class="product-name">{{ item.name }}</text>
              <text class="product-desc">{{ item.desc }}</text>
              <view class="price-quantity">
                <text class="price">¥{{ item.unitPrice || item.price }}</text>
                <view class="quantity-control">
                  <text 
                    class="control-btn minus" 
                    :class="{ disabled: item.quantity <= 1 }"
                    @tap.stop="changeQuantity(item, -1)"
                  >-</text>
                  <text class="quantity">{{ item.quantity }}</text>
                  <text class="control-btn plus" @tap.stop="changeQuantity(item, 1)">+</text>
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
          <view class="checkout-btn" @tap="buyNow">结算</view>
        </view>
      </view>
    </view>

    <!-- 商品详情面板 -->
    <view class="product-detail-container" :class="{ visible: isProductDetailVisible }" @tap="closeProductDetail">
      <view class="product-detail" @tap.stop>
        <view class="detail-header">
          <view class="product-basic">
            <image :src="baseUrl.concat(currentProduct.mainImage)" class="product-image" mode="aspectFill" v-if="currentProduct" />
            <view class="info">
              <text class="name">{{ currentProduct?.name }}</text>
            </view>
          </view>
          <view class="close-btn" @tap="closeProductDetail">×</view>
        </view>
        
        <scroll-view scroll-y class="detail-content">
          <!-- 修改商品描述部分 -->
          <view class="product-description" v-if="currentProduct">
            <text class="description-text" :class="{ expanded: isDescriptionExpanded }">
              {{ currentProduct.description }}
            </text>
            <text class="toggle-btn" :class="{ expanded: isDescriptionExpanded }" @tap="toggleDescription">
              {{ isDescriptionExpanded ? '收起' : '展开' }}
            </text>
          </view>
          
          <!-- 规格展示 -->
          <view class="spec-section" v-for="spec in currentProduct.specs" :key="spec.specId">
            <view class="section-title">{{ spec.name }}</view>
            <view class="options-list">
              <view 
                v-for="value in spec.values" 
                :key="value.specValueId" 
                class="option-item"
                :class="{ active: value.specValueId === spec.selectedValueId || (!spec.selectedValueId && value.isDefault) }"
                @tap="selectOption(spec, value.specValueId)"
              >
                <text>{{ value.value }}</text>
                <text v-if="value.extraPrice > 0" class="extra-price">+{{ value.extraPrice }}元</text>
              </view>
            </view>
          </view>
        </scroll-view>
        
        <view class="detail-footer">
          <view class="selected-specs">
            <view class="specs-content">
              <view class="selected-items">
                <view class="price-wrapper">
                  <text class="symbol">¥</text>
                  <text class="price">{{ selectedPrice }}</text>
                </view>
                <view class="quantity-control">
                  <text 
                    class="control-btn minus" 
                    :class="{ disabled: productQuantity <= 1 }"
                    @tap.stop="changeProductQuantity(-1)"
                  >-</text>
                  <text class="quantity">{{ productQuantity }}</text>
                  <text class="control-btn plus" @tap.stop="changeProductQuantity(1)">+</text>
                </view>
              </view>
              <text class="selected-specs-text">{{ selectedSpecsText }}</text>
            </view>
          </view>
          <view class="footer-btns">
            <view class="add-to-cart-btn" @tap="addToCart">加入购物车</view>
            <view class="buy-now-btn" @tap="buyNow">立即购买</view>
          </view>
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
      height: 32px;
      width: v-bind('menuButtonInfo?.width ? menuButtonInfo.width + "px" : "90px"'); // 添加默认宽度
      background: #f8f8f8;
      border-radius: 16px;
      padding: 0 8px;
      border: 1px solid #e5e5e5;
      box-sizing: border-box;
      
      image {
        width: 20px;
        height: 20px;
        margin-right: 4px;
      }
      
      text {
        font-size: 14px;
        color: #333;
      }
    }

    .nav-right {
      position: absolute;
      display: flex;
      align-items: center;

      .search-btn {
        width: 36px; // 固定一个合适的大小
        height: 36px; // 固定一个合适的大小
        display: flex;
        align-items: center;
        justify-content: center;
        border: 1px solid #e5e5e5;
        border-radius: 50%;
        background: #fff;

        image {
          width: 22px; // 增大图标尺寸
          height: 22px; // 增大图标尺寸
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
    margin-top: 20px;
    height: 100%;
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    padding: 12px 16px;

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
        padding-right: 0; // 移除右侧padding
        margin-bottom: 0;

        .notice {
          font-size: 12px;
          color: #666;
          line-height: 1.4;
          display: -webkit-box;
          -webkit-box-orient: vertical;
          -webkit-line-clamp: 1;
          overflow: hidden;

          &.expanded {
            -webkit-line-clamp: unset;
          }
        }
      }

      .address-info {
        margin-top: 4px;
        
        .address {
          font-size: 12px;
          color: #333;
          margin-bottom: 4px;
          display: block;
        }
        
        .contact {
          font-size: 12px;
          color: #666;
          display: block;
        }
      }
    }

    .right {
      display: flex;
      flex-direction: column;
      align-items: flex-end;
      gap: 8px;

      .delivery-switch {
        display: flex;
        background: #f5f5f5;
        border-radius: 20px;
        padding: 2px;
        order: -1; // 添加 order 属性，确保自取/外卖按钮在上方
        position: relative; // 添加相对定位以便滑块定位

        .slider-bg {
          position: absolute;
          left: 2px;
          top: 2px;
          width: calc(50% - 2px);
          height: calc(100% - 4px);
          background: #333;
          border-radius: 16px;
          transition: transform 0.3s ease;
          z-index: 1;
          
          &.slide-right {
            transform: translateX(100%);
          }
        }

        .switch-item {
          padding: 4px 16px;
          font-size: 13px;
          color: #666;
          border-radius: 16px;
          transition: all 0.3s ease;
          cursor: pointer;
          outline: none; // 添加这一行移除焦点边框
          -webkit-tap-highlight-color: transparent; // 添加这一行移除移动端点击高亮
          position: relative; // 确保文字在滑块上方
          z-index: 2; // 确保文字在滑块上方
          flex: 1; // 确保两个按钮宽度相等
          text-align: center; // 文字居中

          &.active {
            background: transparent; // 移除原来的背景
            color: #fff; // 保持文字颜色为白色
          }
        }
      }

      .more {
        font-size: 12px;
        color: #999;
        padding: 2px 0;
        cursor: pointer;
        display: flex;
        align-items: center;
        white-space: nowrap;
        margin-top: 4px; // 添加顶部间距
        
        &::after {
          content: '';
          display: inline-block;
          width: 6px;
          height: 6px;
          border: solid #999;
          border-width: 0 1px 1px 0;
          transform: rotate(45deg);
          margin-left: 4px;
          transition: transform 0.3s;
          position: relative;
          top: -1px;
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

        .item-checkbox {
          margin-right: 10px;
          display: flex;
          align-items: center;
          
          .checkbox {
            width: 18px;
            height: 18px;
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
        }

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
              gap: 12px;
              
              .control-btn {
                width: 28px;
                height: 28px;
                display: flex;
                align-items: center;
                justify-content: center;
                border: 1px solid #ddd;
                border-radius: 50%;
                color: #1296db;
                font-size: 18px;
                background: #f0f7fc; /* 更改为淡蓝色背景 */
                transition: all 0.3s ease;
                box-shadow: 0 2px 4px rgba(0,0,0,0.05); /* 添加轻微阴影 */
                
                &:not(.disabled):active {
                  background: #e0f0fa;
                  transform: scale(0.95); /* 添加按下效果 */
                }
                
                &.disabled {
                  border-color: #eee;
                  color: #ccc;
                  background: #f8f8f8;
                  box-shadow: none;
                  pointer-events: none; /* 禁用点击 */
                }
              }
              
              .quantity {
                font-size: 16px;
                color: #333;
                min-width: 40px;
                text-align: center;
                font-weight: 500;
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

.product-detail-container {
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

    .product-detail {
      transform: translateY(0);
    }
  }

  .product-detail {
    position: absolute;
    left: 0;
    right: 0;
    bottom: 0;
    background: #fff;
    border-radius: 20px 20px 0 0;
    transform: translateY(100%);
    transition: transform 0.3s ease;
    max-height: calc(90vh - env(safe-area-inset-top) - 44px); // 减去胶囊高度
    padding-top: env(safe-area-inset-top); // 添加顶部安全区域padding
    display: flex;
    flex-direction: column;
    overflow: hidden;
    
    .detail-header {
      position: sticky; // 设置为sticky
      top: env(safe-area-inset-top); // 顶部对齐安全区域
      z-index: 10; // 确保在内容之上
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      padding: 16px;
      background: #fff; // 添加背景色
      border-bottom: 1px solid #f5f5f5;
      
      .product-basic {
        display: flex;
        align-items: center;
        
        .product-image {
          width: 70px;
          height: 70px;
          border-radius: 8px;
          margin-right: 16px;
          background: #f5f5f5;
        }
        
        .info {
          display: flex;
          flex-direction: column;
          
          .name {
            font-size: 18px;
            font-weight: 500;
            color: #333;
            margin-bottom: 8px;
          }
          
          .price {
            display: none;
          }
        }
      }
      
      .close-btn {
        width: 28px;
        height: 28px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 22px;
        color: #999;
        background: #f7f7f7;
        border-radius: 50%;
      }
    }
    
    .detail-content {
      flex: 1;
      overflow-y: auto;
      position: relative;
      padding: 0;
      margin-bottom: calc(180px + env(safe-area-inset-bottom)); // 从140px增加到180px
      -webkit-overflow-scrolling: touch;
      
      /* 隐藏滚动条但保持可滚动 */
  &::-webkit-scrollbar {
        display: none;
      }
      scrollbar-width: none;
      -ms-overflow-style: none;
      
      /* 修改商品描述样式 */
      .product-description {
        margin: 16px 16px 24px 16px;
        padding: 16px;
        background: #f8f8f8;
        border-radius: 12px;
        position: relative;
        width: auto;
        box-sizing: border-box;
        
        .description-text {
          font-size: 14px;
          color: #666;
          line-height: 1.6;
          display: -webkit-box;
          -webkit-box-orient: vertical;
          -webkit-line-clamp: 2;
          overflow: hidden;
          transition: all 0.3s ease;
          padding-right: 40px; /* 为展开按钮预留空间 */
          
          &.expanded {
            -webkit-line-clamp: unset;
          }
        }
        
        .toggle-btn {
          position: absolute;
          right: 16px;
          top: 50%;
          transform: translateY(-50%);
          font-size: 12px;
          color: #1296db;
          padding: 4px 8px;
          cursor: pointer;
          display: flex;
          align-items: center;
          white-space: nowrap;
          
          &::after {
            content: '';
            display: inline-block;
            width: 6px;
            height: 6px;
            border: solid #1296db;
            border-width: 0 1px 1px 0;
            transform: rotate(45deg);
            margin-left: 4px;
            transition: transform 0.3s ease;
            position: relative;
            top: -1px;
          }
          
          &.expanded::after {
            transform: rotate(-135deg);
            top: 1px;
          }
        }
      }
      
      .spec-section {
        margin: 0 16px 8px 16px;
        
        .section-title {
          font-size: 16px;
          font-weight: 500;
          color: #333;
          margin-bottom: 8px;
          position: sticky;
          top: 0;
          background: #fff;
          padding: 8px 0;
          z-index: 1;
        }
        
        .options-list {
          display: flex;
          flex-wrap: wrap;
          gap: 12px;
          padding-bottom: 8px;
          
          .option-item {
            width: 100px; /* 增加默认按钮宽度 */
            height: 36px;
            padding: 0 8px; /* 继续减小内边距 */
            border-radius: 8px;
            background: #f7f7f7;
            color: #333;
            font-size: 14px;
            display: flex;
            align-items: center;
            justify-content: center;
            box-sizing: border-box;
            flex-grow: 0;
            text-align: center;
            white-space: nowrap; /* 防止文字换行 */
            overflow: hidden; /* 防止文字溢出 */
            text-overflow: ellipsis; /* 文字溢出时显示省略号 */
            
            &.active {
              background: #e0f0fa;
              color: #1296db;
              border: 1px solid #1296db;
            }
            
            .extra-price {
              font-size: 12px;
              color: #ff5339;
              margin-left: 4px;
            }
          }
        }
      }
    }
    
    .detail-footer {
      position: absolute;
      left: 0;
      right: 0;
      bottom: 0;
      padding: 24px 16px;
      background: #fff;
      border-top: 1px solid #f5f5f5;
      padding-bottom: calc(24px + env(safe-area-inset-bottom)); // 改为与顶部padding一致
      z-index: 10;
      box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
      
      .selected-specs {
        margin-bottom: 16px; // 改回原来的大小
        padding: 0 4px;
      }
      
      .specs-content {
        display: flex;
        flex-direction: column; /* 修改为纵向排列 */
        
        .selected-items {
          display: flex;
          align-items: center;
          justify-content: space-between; /* 价格与数量控制分两端对齐 */
          margin-bottom: 10px; /* 添加底部间距 */
          
          .price-wrapper {
            display: flex;
            align-items: baseline; /* 对齐文字的基线 */
            margin-right: 12px; /* 添加右侧间距 */
            
            .symbol {
              font-size: 16px;
              color: #ff5339;
            }
            
            .price {
              font-size: 24px;
              font-weight: bold;
              color: #ff5339;
              margin-left: 1px; /* 最小间距 */
            }
          }
          
          &::before {
            content: none; /* 移除伪元素 */
          }
        }
        
        .selected-specs-text {
          font-size: 14px;
          color: #666;
          margin-top: 8px;
          line-height: 1.4;
        }
        
        .quantity-control {
          display: flex;
          align-items: center;
          gap: 12px;
          
          .control-btn {
            width: 28px;
            height: 28px;
            display: flex;
            align-items: center;
            justify-content: center;
            border: 1px solid #ddd;
            border-radius: 50%;
            color: #1296db;
            font-size: 18px;
            background: #f0f7fc; /* 更改为淡蓝色背景 */
            transition: all 0.3s ease;
            box-shadow: 0 2px 4px rgba(0,0,0,0.05); /* 添加轻微阴影 */
            
            &:not(.disabled):active {
              background: #e0f0fa;
              transform: scale(0.95); /* 添加按下效果 */
            }
            
            &.disabled {
              border-color: #eee;
              color: #ccc;
              background: #f8f8f8;
              box-shadow: none;
            }
          }
          
          .quantity {
            font-size: 16px;
            color: #333;
            min-width: 40px;
            text-align: center;
            font-weight: 500;
          }
        }
      }
      
      .footer-btns {
        display: flex;
        gap: 12px;
        margin-top: 6px; /* 添加顶部间距 */
        
        .add-to-cart-btn {
          flex: 1;
          height: 44px;
          line-height: 44px;
          text-align: center;
          background: #fff;
          color: #f0ad4e;
          font-size: 16px;
          border-radius: 22px;
          font-weight: 500;
          border: 1px solid #f0ad4e;
        }
        
        .buy-now-btn {
          flex: 1;
          height: 44px;
          line-height: 44px;
          text-align: center;
          background: #1296db;
          color: #fff;
          font-size: 16px;
          border-radius: 22px;
          font-weight: 500;
        }
      }
    }
  }
}

.loading-state {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
  
  text {
    color: #999;
    font-size: 14px;
  }
}
</style>