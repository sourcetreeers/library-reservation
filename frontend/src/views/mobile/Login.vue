<template>
  <div class="mobile-login">
    <div class="page-shell">
      <div class="hero-panel">
        <div class="hero-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="#007AFF" stroke-width="1.5">
            <path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z"/>
            <polyline points="9 22 9 12 15 12 15 22"/>
          </svg>
        </div>
        <h1>在线图书馆座位预订系统</h1>
        <p>简洁、高效、可信赖的预约体验</p>
      </div>
      
      <div class="form-container">
        <div class="form-header">
          <h2>欢迎回来</h2>
          <span>使用账号登录后继续预约</span>
        </div>
        <van-form @submit="handleLogin">
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
          <div class="button-group">
            <van-button round block type="info" native-type="submit" :loading="loading">
              登录
            </van-button>
            <van-button round block plain type="info" @click="goRegister">
              注册
            </van-button>
          </div>
        </van-form>
        
        <div class="login-footer">
          <span class="footer-text">其他入口：</span>
          <van-button size="small" plain type="default" @click="goAdminLogin">
            图书馆管理员入口
          </van-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { login } from '@/api/auth'

export default {
  name: 'MobileLogin',
  data() {
    return {
      form: {
        username: '',
        password: ''
      },
      loading: false
    }
  },
  methods: {
    async handleLogin() {
      this.loading = true
      try {
        const res = await login(this.form)

        // 确保 data 存在
        const userData = res.data || res

        // 学生端只允许学生账号登录
        if (userData.userType !== '学生') {
          this.$toast.fail('只有学生可以登录')
          return
        }

        this.$store.dispatch('setUser', userData)

        // 学生直接进入移动端首页
        this.$router.replace('/mobile/home').catch(err => {
          if (err.name !== 'NavigationDuplicated') {
            throw err
          }
        })
      } catch (error) {
        this.$toast.fail(error.message || '登录失败')
      } finally {
        this.loading = false
      }
    },
    
    goRegister() {
      this.$router.push('/mobile/register')
    },
    
    goAdminLogin() {
      this.$router.push('/admin/login')
    }
  }
}
</script>

<style scoped>
.mobile-login {
  min-height: 100vh;
  background: #FFFFFF;
  padding: 24px;
}

.page-shell {
  min-height: calc(100vh - 48px);
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 28px;
  max-width: 420px;
  margin: 0 auto;
}

.hero-panel {
  text-align: center;
  padding: 12px 8px;
}

.hero-icon {
  width: 56px;
  height: 56px;
  margin: 0 auto 20px;
  background: #F5F5F7;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.hero-icon svg {
  width: 28px;
  height: 28px;
}

.hero-panel h1 {
  color: #1D1D1F;
  font-size: 28px;
  font-weight: 700;
  letter-spacing: -0.3px;
}

.hero-panel p {
  margin-top: 10px;
  color: #86868B;
  font-size: 14px;
}

.form-container {
  background: #FFFFFF;
  border-radius: 14px;
  padding: 28px 20px;
  border: 1px solid rgba(0, 0, 0, 0.06);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.form-header {
  margin-bottom: 20px;
}

.form-header h2 {
  color: #1D1D1F;
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 6px;
  letter-spacing: -0.2px;
}

.form-header span {
  color: #86868B;
  font-size: 13px;
}

.button-group {
  margin-top: 28px;
}

.button-group .van-button {
  margin-bottom: 12px;
}

.login-footer {
  margin-top: 20px;
  text-align: center;
  padding-top: 16px;
  border-top: 1px solid rgba(0, 0, 0, 0.06);
}

.footer-text {
  display: block;
  color: #86868B;
  font-size: 13px;
  margin-bottom: 10px;
}

.login-footer .van-button {
  width: 100%;
  margin-top: 6px;
}
</style>
