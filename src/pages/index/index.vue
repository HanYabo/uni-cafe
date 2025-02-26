<script setup>

</script>

<template>
  <div class="layout">
    <div class="portfolio">
      <img src="/static/tabbar/logo.png" alt="头像" class="avatar" />
      <div class="text">
        <span class="username">张三</span> <br>
        <span class="remind">有1张优惠券未使用，立即查看></span>
      </div>
      <div class="line1">
      </div>
      <div class="code">
        <img src="/static/index/qrcode.svg" class="qr">
        <span class="member">会员码</span>
      </div>
    </div>
    <div class="panel">
      <div class="pl">
        <img src="/static/index/qrcode.svg" alt="" class="picture1">
        <span class="l1">门店自取</span>
        <span class="l2">下单免排队</span>
      </div>
      <div class="line2"></div>
      <div class="pr">
        <img src="/static/index/qrcode.svg" alt="" class="picture2">
        <span class="r1">外卖</span>
        <span class="r2">配送到家</span>
      </div>
    </div>
    <div class="banner">
      <div class="banner-1">
        <span class="t1">百货</span>
        <span class="t2">百货优惠</span>
      </div>
      <div class="line3"></div>
      <div class="banner-1">
        <span class="t1">团餐</span>
        <span class="t2">企业欢聚享福利</span>
      </div>
      <div class="line3"></div>
      <div class="banner-1">
        <span class="t1">送礼</span>
        <span class="t2">送礼更有面子</span>
      </div>
    </div>
  </div>
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
    height: 90px;
    border-radius: 10px;
    margin-top: 130px;

    .avatar {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      margin-left: 20px;
    }

    .text {
      margin-right: 40px;
      .username {
        font-size: 16px;
        font-weight: bold;
      }
      .remind {
        font-size: 12px;
        opacity: 0.5;
      }

    }

    .line1 {
        width: 1px;
        height: 30px;
        background-color: gainsboro;
        opacity: 0.5;
        margin-left: 20px;
        margin-right: 20px;
      }

    .code {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      margin-right: 20px;
      .qr {
        width: 25px;
        height: 25px;
        margin-bottom: 5px;
      }
      .member {
        font-size: 14px;
        opacity: 0.5;
        font-weight: bold;
      }
    }
  }

  .panel {
    background-color: rgba(254, 255, 254, 1);
    width: 92%;
    height: 200px;
    border-radius: 10px;
    margin-top: 12px;
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: space-evenly;
    
    .pl {
      display: flex;
      flex-direction: column;
      align-items: center;
      .picture1 {
        width: 70px;
        height: 70px;
        border-radius: 50%;
        margin-bottom: 10px;
      }
      .l1 {
        font-size: 18px;
        font-weight: bold;
        margin-bottom: 5px;
      }
      .l2 {
        font-size: 12px;
        font-weight: bold;
        opacity: 0.5;
      }
    }
    .line2 {
      width: 1px;
      height: 150px;
      background-color: gainsboro;
      opacity: 0.5;
    }
    .pr {
      display: flex;
      flex-direction: column;
      align-items: center;
      .picture2 {
        width: 70px;
        height: 70px;
        border-radius: 50%;
        margin-bottom: 10px;
      }
      .r1 {
        font-size: 18px;
        font-weight: bold;
        margin-bottom: 5px;
      }
      .r2 {
        font-size: 12px;
        font-weight: bold;
        opacity: 0.5;
      }
    }
  }

  .banner {
    background-color: rgba(254, 255, 254, 1);
    width: 92%;
    height: 130px;
    border-radius: 10px;
    margin-top: 12px;
    display: flex;
    flex-direction: row; 
    align-items: center;
    justify-content: space-evenly;

    .banner-1 {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      // TODO 样式后期可能会更改
      width: 150px;
      .t1 {
        font-size: 18px;
        font-weight: bold;
        margin-bottom: 5px;
      }
      .t2 {
        font-size: 12px;
        font-weight: bold;
        opacity: 0.5;
      }
    }
    
    .line3 {
      width: 1px;
      height: 80px;
      background-color: gainsboro;
      opacity: 0.5;
    }
  }
}
</style>