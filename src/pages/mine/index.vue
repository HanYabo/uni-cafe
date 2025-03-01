<script setup>

</script>

<template>
  <div class="layout">
    <div class="portfolio">
      <img src="/static/index/qrcode.svg" alt="头像" class="avatar" />
      <div class="text">
        <span class="username">Hello!</span> <br>
        <span class="remind">登录享受更多精彩服务</span>
      </div>
      <div class="btn">注册/登录</div>
    </div>
    <div class="util">
      <div>
        <h1>常用功能</h1>
      </div>
      <div class="item">
        <img src="/static/index/qrcode.svg" alt="" class="icon">
        <span class="text">地址管理</span>
      </div>
      <div class="item">
        <img src="/static/index/qrcode.svg" alt="" class="icon">
        <span class="text">发票管理</span>
      </div>
      <div class="item">
        <img src="/static/index/qrcode.svg" alt="" class="icon">
        <span class="text">设置</span>
      </div>
      <div class="item">
        <img src="/static/index/qrcode.svg" alt="" class="icon">
        <span class="text">关注公众号</span>
      </div>
      <div class="item">
        <img src="/static/index/qrcode.svg" alt="" class="icon">
        <span class="text">在线客服</span>
      </div>
      <div class="item">
        <img src="/static/index/qrcode.svg" alt="" class="icon">
        <span class="text">条款与证明</span>
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
      width: 50px;
      height: 50px;
      border-radius: 50%;
      margin-left: 20px;
    }

    .text {
      margin-right: 90px;
      .username {
        font-size: 16px;
        font-weight: bold;
      }
      .remind {
        font-size: 12px;
        opacity: 0.5;
      }
    }

    .btn {
      width: 80px;
      height: 35px;
      background: skyblue;
      border-radius: 20px;
      margin-right: 20px;
      opacity: 0.7;
      font-size: 11px;
      font-weight: bold;
      display: flex;
      justify-content: center;
      align-items: center;
    }
  }

  .util {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 92%;
    
    .item {
      display: flex;
      flex-direction: row;
      align-items: center;
      height: 50px;
      width: 100%;
      background: rgba(254, 255, 254, 1);
      border-radius: 10px;
      margin-top: 10px;

      .icon {
        width: 30px;
        height: 30px;
        margin-left: 20px;
      }

      .text {
        margin-left: 10px;
        font-size: 14px;
      }
    }
  }
}
</style>