<template>
  <div class="mobile-register">
    <van-nav-bar
      title="用户注册"
      left-text="返回"
      left-arrow
      @click-left="$router.go(-1)"
    />
    
    <!-- Apple 风格头部 -->
    <div class="register-header">
      <div class="header-icon-wrap">
        <svg class="header-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
          <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/>
          <circle cx="9" cy="7" r="4"/>
          <line x1="19" y1="8" x2="19" y2="14"/>
          <line x1="22" y1="11" x2="16" y2="11"/>
        </svg>
      </div>
      <h2 class="header-title">创建账户</h2>
      <p class="header-subtitle">加入图书馆座位预约系统</p>
    </div>

    <div class="form-container">
      <van-form @submit="handleRegister" class="apple-form">
        <van-field
          v-model="form.username"
          name="username"
          label="用户名"
          placeholder="请输入用户名"
          :rules="[{ required: true, message: '请输入用户名' }]"
        />
        <van-field
          v-model="form.password"
          type="password"
          name="password"
          label="密码"
          placeholder="请输入密码"
          :rules="[{ required: true, message: '请输入密码' }]"
        />
        <van-field
          v-model="form.realName"
          name="realName"
          label="真实姓名"
          placeholder="请输入真实姓名"
          :rules="[{ required: true, message: '请输入真实姓名' }]"
        />
        <van-field
          v-model="form.phone"
          name="phone"
          label="手机号"
          placeholder="请输入手机号（选填）"
        />
        <van-field
          v-model="form.email"
          name="email"
          label="邮箱"
          placeholder="请输入邮箱（选填）"
        />

        <div class="button-group">
          <van-button round block type="info" native-type="submit" :loading="loading" class="submit-btn">
            注册
          </van-button>
        </div>

        <div class="login-link">
          已有账号？<span @click="$router.push('/mobile/login')">立即登录</span>
        </div>
      </van-form>
    </div>
  </div>
</template>

<script>
import { register } from '@/api/auth'

export default {
  name: 'MobileRegister',
  data() {
    return {
      form: {
        username: '',
        password: '',
        realName: '',
        phone: '',
        email: ''
      },
      loading: false
    }
  },
  methods: {
    async handleRegister() {
      this.loading = true
      try {
        await register(this.form)
        this.$toast.success('注册成功，请登录')
        this.$router.push('/mobile/login')
      } catch (error) {
        this.$toast.fail(error.message || '注册失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.mobile-register {
  min-height: 100vh;
  background-color: #f2f2f7;
}

/* 头部区域 */
.register-header {
  background: linear-gradient(180deg, #007AFF 0%, #5ac8fa 100%);
  padding: 40px 24px 48px;
  text-align: center;
  color: white;
}

.header-icon-wrap {
  width: 72px;
  height: 72px;
  margin: 0 auto 16px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  justify-content: center;
}

.header-icon {
  width: 36px;
  height: 36px;
  color: white;
}

.header-title {
  font-size: 26px;
  font-weight: 700;
  margin: 0 0 8px;
  letter-spacing: -0.5px;
}

.header-subtitle {
  font-size: 15px;
  opacity: 0.85;
  margin: 0;
  font-weight: 400;
}

/* 表单容器 */
.form-container {
  margin-top: -28px;
  padding: 0 20px 30px;
  position: relative;
  z-index: 1;
}

.apple-form {
  background: white;
  border-radius: 14px;
  padding: 20px 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

/* 覆盖 Vant 字段为圆角风格 */
.apple-form >>> .van-cell {
  padding: 13px 12px;
  border-radius: 10px;
  margin-bottom: 8px;
  background: #f2f2f7 !important;
  transition: all 0.2s ease;
}

.apple-form >>> .van-cell::after {
  display: none;
}

.apple-form >>> .van-cell:active {
  background: #e5e5ea !important;
}

.apple-form >>> .van-field__label {
  color: #3c3c43;
  font-weight: 500;
  width: auto;
  min-width: 60px;
  margin-right: 12px;
}

.apple-form >>> .van-field__control {
  color: #1d1d1f;
  font-size: 15px;
}

.apple-form >>> .van-field__control::placeholder {
  color: #c7c7cc;
}

/* 提交按钮 */
.button-group {
  margin-top: 28px;
}

.submit-btn {
  height: 50px;
  font-size: 17px;
  font-weight: 600;
  letter-spacing: 0.5px;
  background: linear-gradient(180deg, #007AFF 0%, #0066D6 100%) !important;
  border: none !important;
  box-shadow: 0 4px 14px rgba(0, 122, 255, 0.35);
  transition: all 0.25s ease;
}

.submit-btn:active {
  transform: scale(0.98);
  box-shadow: 0 2px 8px rgba(0, 122, 255, 0.25);
}

/* 登录链接 */
.login-link {
  text-align: center;
  margin-top: 18px;
  font-size: 14px;
  color: #8e8e93;
}

.login-link span {
  color: #007AFF;
  font-weight: 500;
  cursor: pointer;
}
</style>
