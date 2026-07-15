<template>
  <div class="system-admin-login">
    <div class="login-container">
      <div class="login-header">
        <div class="header-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <circle cx="12" cy="12" r="3"/>
            <path d="M19.4 15a1.65 1.65 0 00.33 1.82l.06.06a2 2 0 01-2.83 2.83l-.06-.06a1.65 1.65 0 00-1.82-.33 1.65 1.65 0 00-1 1.51V21a2 2 0 01-4 0v-.09A1.65 1.65 0 009 19.4a1.65 1.65 0 00-1.82.33l-.06.06a2 2 0 01-2.83-2.83l.06-.06A1.65 1.65 0 004.68 15a1.65 1.65 0 00-1.51-1H3a2 2 0 010-4h.09A1.65 1.65 0 004.6 9a1.65 1.65 0 00-.33-1.82l-.06-.06a2 2 0 012.83-2.83l.06.06A1.65 1.65 0 009 4.68a1.65 1.65 0 001-1.51V3a2 2 0 014 0v.09a1.65 1.65 0 001 1.51 1.65 1.65 0 001.82-.33l.06-.06a2 2 0 012.83 2.83l-.06.06A1.65 1.65 0 0019.4 9a1.65 1.65 0 001.51 1H21a2 2 0 010 4h-.09a1.65 1.65 0 00-1.51 1z"/>
          </svg>
        </div>
        <h1>系统管理员登录</h1>
        <p>System Administration</p>
      </div>
      
      <el-form :model="loginForm" :rules="rules" ref="loginForm" class="login-form">
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="el-icon-user"
            size="large"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="el-icon-lock"
            size="large"
            @keyup.enter.native="handleLogin"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="submit-button"
            :loading="loading"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="login-footer">
        <el-link @click="$router.push('/mobile/login')">学生端入口</el-link>
        <span class="divider">|</span>
        <el-link @click="$router.push('/admin/login')">图书馆管理员入口</el-link>
      </div>
    </div>
  </div>
</template>

<script>
import { login } from '@/api/auth'

export default {
  name: 'SystemAdminLogin',
  data() {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ]
      },
      loading: false
    }
  },
  
  methods: {
    async handleLogin() {
      try {
        await this.$refs.loginForm.validate()
        
        this.loading = true
        const res = await login(this.loginForm)
        
        if (res.data.userType !== '系统管理员') {
          this.$message.error('该账号不是系统管理员')
          this.$store.dispatch('clearUser')
          return
        }
        
        this.$store.dispatch('setUser', res.data)
        this.$message.success('登录成功')
        
        this.$router.replace('/system-admin/dashboard/users').catch(err => {
          if (err.name !== 'NavigationDuplicated') {
            throw err
          }
        })
      } catch (error) {
        this.$message.error(error.message || '登录失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.system-admin-login {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #F5F5F7;
}

.login-container {
  width: 400px;
  padding: 40px;
  background: #FFFFFF;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(0, 0, 0, 0.06);
}

.login-header {
  text-align: center;
  margin-bottom: 36px;
}

.header-icon {
  width: 48px;
  height: 48px;
  margin: 0 auto 16px;
  background: #F5F5F7;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.header-icon svg {
  width: 24px;
  height: 24px;
  color: #8E8E93;
}

.login-header h1 {
  margin: 0 0 6px;
  color: #1D1D1F;
  font-size: 24px;
  font-weight: 700;
  letter-spacing: -0.3px;
}

.login-header p {
  margin: 0;
  color: #86868B;
  font-size: 14px;
  letter-spacing: 0.3px;
}

.submit-button {
  width: 100%;
  height: 44px;
  font-size: 15px;
  font-weight: 600;
  border-radius: 8px;
}

.login-footer {
  margin-top: 28px;
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid rgba(0, 0, 0, 0.06);
}

.login-footer .el-link {
  font-size: 13px;
  color: #86868B;
}

.divider {
  margin: 0 8px;
  color: #D2D2D7;
}
</style>
