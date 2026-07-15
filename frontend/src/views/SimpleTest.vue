<template>
  <div style="padding: 20px; text-align: center;">
    <h1>在线图书馆座位预订系统</h1>
    <p>系统正在运行中...</p>
    <div style="margin: 20px 0;">
      <button @click="goToMobile" style="margin: 10px; padding: 10px 20px;">移动端登录</button>
      <button @click="goToAdmin" style="margin: 10px; padding: 10px 20px;">管理端登录</button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SimpleTest',
  methods: {
    goToMobile() {
      this.$router.push('/mobile/login')
    },
    goToAdmin() {
      this.$router.push('/admin/login')
    }
  }
}
</script>