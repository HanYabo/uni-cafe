<script setup>

</script>

<template>
  <view class="layout">
    <view class="portfolio">
      <img src="/static/index/qrcode.svg" alt="头像" class="avatar" />
      <view class="text">
        <text class="username">张三</text>
        <br>
        <text class="remind">有1张优惠券未使用，立即查看></text>
      </view>
      <view class="line1">
      </view>
      <view class="code">
        <img src="/static/index/qrcode.svg" class="qr" />
        <text class="member">会员码</text>
      </view>
    </view>
    <view class="panel">
      <view class="pl">
        <image src="/static/index/qrcode.svg" alt="" class="picture1" />
        <text class="l1">门店自取</text>
        <text class="l2">下单免排队</text>
      </view>
      <view class="line2"></view>
      <view class="pr">
        <image src="/static/index/qrcode.svg" alt="" class="picture2" />
        <text class="r1">外卖闪送</text>
        <text class="r2">配送到您家</text>
      </view>
    </view>
    <view class="banner">
      <view class="banner-1">
        <text class="t1">百货</text>
        <text class="t2">百货优惠</text>
      </view>
      <view class="line3"></view>
      <view class="banner-1">
        <text class="t1">团餐</text>
        <text class="t2">企业欢聚享福利</text>
      </view>
      <view class="line3"></view>
      <view class="banner-1">
        <text class="t1">送礼</text>
        <text class="t2">送礼更有面子</text>
      </view>
    </view>
  </view>
</template>

<style scoped lang="scss">
.layout {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background: rgba(216, 216, 216, 1);


  .portfolio {
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
    background: rgba(254, 255, 254, 1);
    width: 92%;
    height: 150rpx;
    border-radius: 10px;
    margin-top: 150rpx;

    .avatar {
      width: 70rpx;
      height: 70rpx;
      border-radius: 50%;
      margin-left: 30rpx;
    }

    .text {
      .username {
        font-size: 1rem;
        font-weight: bold;
      }
      .remind {
        font-size: 0.75rem;
        opacity: 0.5;
      }

    }

    .line1 {
        width: 1px;
        height:50rpx;
        background-color: gainsboro;
        opacity: 0.5;
        margin-left: 20rpx;
        margin-right: 20rpx;
      }

    .code {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      margin-right: 30rpx;
      .qr {
        width: 50rpx;
        height: 50rpx;
        margin-bottom: 10rpx;
      }
      .member {
        font-size: 0.8rem;
        opacity: 0.5;
        font-weight: bold;
      }
    }
  }

  .panel {
    background-color: rgba(254, 255, 254, 1);
    width: 92%;
    height: 300rpx;
    border-radius: 10px;
    margin-top: 20rpx;
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: space-evenly;
    
    .pl {
      display: flex;
      flex-direction: column;
      align-items: center;
      .picture1 {
        width: 120rpx;
        height: 120rpx;
        border-radius: 50%;
        margin-bottom: 20rpx;
      }
      .l1 {
        font-size: 1rem;
        font-weight: bold;
        margin-bottom: 10rpx;
      }
      .l2 {
        font-size: 0.75rem;
        font-weight: bold;
        opacity: 0.5;
      }
    }
    .line2 {
      width: 1px;
      height: 220rpx;
      background-color: gainsboro;
      opacity: 0.5;
    }
    .pr {
      display: flex;
      flex-direction: column;
      align-items: center;
      .picture2 {
        width: 120rpx;
        height: 120rpx;
        border-radius: 50%;
        margin-bottom: 20rpx;
      }
      .r1 {
        font-size: 1rem;
        font-weight: bold;
        margin-bottom: 10rpx;
      }
      .r2 {
        font-size: 0.75rem;
        font-weight: bold;
        opacity: 0.5;
      }
    }
  }

  .banner {
    background-color: rgba(254, 255, 254, 1);
    width: 92%;
    height: 150rpx;
    border-radius: 10px;
    margin-top: 20rpx;
    display: flex;
    flex-direction: row; 
    align-items: center;
    justify-content: space-evenly;

    .banner-1 {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      width: 180rpx;
      .t1 {
        font-size: 1rem;
        font-weight: bold;
        margin-bottom: 10rpx;
      }
      .t2 {
        font-size: 0.75rem;
        font-weight: bold;
        opacity: 0.5;
      }
    }
    
    .line3 {
      width: 1px;
      height: 100rpx;
      background-color: gainsboro;
      opacity: 0.5;
    }
  }
}
</style>