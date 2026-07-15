<template>
  <div class="admin-login">
    <div class="login-shell">
      <div class="brand-panel">
        <div class="brand-badge">Admin Console</div>
        <h1>座位预订系统<br/>管理后台</h1>
        <p>面向管理员的统一管理入口，聚合图书室、座位、预约与用户数据。</p>
        <div class="brand-features">
          <div class="feature-item">
            <svg viewBox="0 0 24 24" fill="none" stroke="#007AFF" stroke-width="1.5"><path d="M22 11.08V12a10 10 0 11-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>
            <div>
              <strong>高效管理</strong>
              <span>快速处理预约与签到验证</span>
            </div>
          </div>
          <div class="feature-item">
            <svg viewBox="0 0 24 24" fill="none" stroke="#007AFF" stroke-width="1.5"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
            <div>
              <strong>实时掌控</strong>
              <span>统一维护图书室与座位状态</span>
            </div>
          </div>
        </div>
      </div>

      <div class="login-container">
        <div class="login-form">
          <div class="form-header">
            <span class="eyebrow">Secure Access</span>
            <h2>管理员登录</h2>
            <p>请输入管理员账号信息以继续访问系统。</p>
          </div>

          <el-form ref="loginForm" :model="form" :rules="rules" @submit.native.prevent="handleLogin">
            <el-form-item prop="username">
              <el-input
                v-model="form.username"
                placeholder="请输入用户名"
                prefix-icon="el-icon-user"
                size="large"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="form.password"
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
            <el-link type="info" @click="$router.push('/mobile/login')">学生端入口</el-link>
            <span class="divider">|</span>
            <el-link type="info" @click="$router.push('/system-admin/login')">系统管理员入口</el-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { login } from '@/api/auth'

export default {
  name: 'AdminLogin',
  data() {
    return {
      form: {
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
    handleLogin() {
      this.$refs.loginForm.validate(async (valid) => {
        if (valid) {
          this.loading = true
          try {
            const res = await login(this.form)
            
            if (res.data.userType !== '图书馆管理员') {
              this.$message.error('只有图书馆管理员可以登录在线图书馆座位预订系统')
              return
            }
            
            this.$store.dispatch('setUser', res.data)
            this.$message.success('登录成功')
            this.$router.replace('/admin/dashboard/libraries').catch(err => {
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
      })
    }
  }
}
</script>

<style scoped>
.admin-login {
  min-height: 100vh;
  background: #F5F5F7;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32px;
}

.login-shell {
  width: min(960px, 100%);
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: 0;
  background: #FFFFFF;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08);
}

.brand-panel {
  background: #F5F5F7;
  color: #1D1D1F;
  padding: 48px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.brand-badge {
  display: inline-flex;
  align-self: flex-start;
  padding: 6px 12px;
  margin-bottom: 24px;
  border-radius: 6px;
  background: #FFFFFF;
  border: 1px solid rgba(0, 0, 0, 0.06);
  color: #007AFF;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.5px;
  text-transform: uppercase;
}

.brand-panel h1 {
  font-size: 36px;
  line-height: 1.2;
  font-weight: 700;
  letter-spacing: -0.5px;
  margin-bottom: 16px;
  color: #1D1D1F;
}

.brand-panel p {
  max-width: 400px;
  color: #86868B;
  font-size: 15px;
  line-height: 1.6;
}

.brand-features {
  display: grid;
  gap: 16px;
  margin-top: 32px;
  max-width: 360px;
}

.feature-item {
  display: flex;
  gap: 14px;
  align-items: flex-start;
  padding: 16px;
  border-radius: 10px;
  background: #FFFFFF;
  border: 1px solid rgba(0, 0, 0, 0.04);
}

.feature-item svg {
  width: 22px;
  height: 22px;
  flex-shrink: 0;
  margin-top: 2px;
}

.feature-item strong {
  display: block;
  margin-bottom: 4px;
  font-size: 14px;
  font-weight: 600;
  color: #1D1D1F;
}

.feature-item span {
  color: #86868B;
  font-size: 13px;
}

.login-container {
  padding: 48px 36px;
  display: flex;
  align-items: center;
}

.login-form {
  width: 100%;
}

.form-header {
  margin-bottom: 28px;
}

.eyebrow {
  display: inline-block;
  margin-bottom: 10px;
  color: #007AFF;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.5px;
  text-transform: uppercase;
}

.login-form h2 {
  margin-bottom: 8px;
  color: #1D1D1F;
  font-size: 24px;
  font-weight: 700;
  letter-spacing: -0.3px;
}

.form-header p {
  color: #86868B;
  font-size: 14px;
  line-height: 1.5;
}

.el-form-item {
  margin-bottom: 22px;
}

.submit-button {
  width: 100%;
  height: 44px;
  font-size: 15px;
  font-weight: 600;
  border-radius: 8px;
}

.login-footer {
  margin-top: 24px;
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid rgba(0, 0, 0, 0.06);
}

.login-footer .el-link {
  margin: 0 8px;
  font-size: 13px;
  color: #86868B;
}

.divider {
  color: #D2D2D7;
  margin: 0 4px;
}

@media (max-width: 768px) {
  .login-shell {
    grid-template-columns: 1fr;
  }

  .brand-panel {
    padding: 32px 24px;
  }

  .brand-panel h1 {
    font-size: 28px;
  }

  .brand-features {
    display: none;
  }
}

@media (max-width: 520px) {
  .admin-login {
    padding: 16px;
  }

  .login-container {
    padding: 28px 20px;
  }
}
</style>
