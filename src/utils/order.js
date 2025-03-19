import { getOrderDetailAPI } from '@/api/order'

/**
 * 再来一单功能 - 将订单商品添加到购物袋
 * @param {string} orderId 订单ID
 */
export const reorderItems = async (orderId) => {
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
            
            uni.showToast({
              title: '已添加到购物袋',
              icon: 'success'
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