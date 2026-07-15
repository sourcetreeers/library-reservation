<template>
  <div class="admin-dashboard">
    <el-container>
      <!-- macOS Style Sidebar -->
      <el-aside width="220px" class="sidebar">
        <div class="sidebar-logo">
          <svg class="logo-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z"/>
            <polyline points="9 22 9 12 15 12 15 22"/>
          </svg>
          <span class="logo-text">管理系统</span>
        </div>
        
        <el-menu
          :default-active="$route.path"
          router
          background-color="transparent"
          text-color="#48484A"
          active-text-color="#007AFF"
          class="sidebar-menu"
        >
          <el-menu-item index="/admin/dashboard/libraries">
            <i class="el-icon-office-building"></i>
            <span>图书室管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/dashboard/seats">
            <i class="el-icon-s-grid"></i>
            <span>座位管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/dashboard/seats-monitor">
            <i class="el-icon-monitor"></i>
            <span>座位监控</span>
          </el-menu-item>
          <el-menu-item index="/admin/dashboard/reservations">
            <i class="el-icon-tickets"></i>
            <span>预约管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/dashboard/users">
            <i class="el-icon-user"></i>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/dashboard/announcements">
            <i class="el-icon-bell"></i>
            <span>公告管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/dashboard/banners">
            <i class="el-icon-picture"></i>
            <span>轮播图管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/dashboard/data-analysis">
            <i class="el-icon-data-analysis"></i>
            <span>数据分析</span>
          </el-menu-item>
          <el-menu-item index="/admin/dashboard/scanner">
            <i class="el-icon-camera"></i>
            <span>扫码验证</span>
          </el-menu-item>
          <el-menu-item index="/admin/dashboard/disputes">
            <i class="el-icon-document-checked"></i>
            <span>争议处理</span>
          </el-menu-item>
          <el-menu-item index="/admin/dashboard/violations">
            <i class="el-icon-warning"></i>
            <span>违规管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/dashboard/feedbacks">
            <i class="el-icon-chat-dot-round"></i>
            <span>反馈管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      
      <!-- Main Content -->
      <el-container>
        <!-- macOS Style Header -->
        <el-header class="top-bar">
          <div class="header-content">
            <span class="page-title">在线图书馆座位预订系统</span>
            <div class="user-info">
              <el-dropdown @command="handleCommand">
                <span class="user-dropdown">
                  <span class="user-avatar">{{ user.realName ? user.realName.charAt(0) : 'A' }}</span>
                  <span class="user-name">{{ user.realName }}</span>
                  <i class="el-icon-arrow-down"></i>
                </span>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item command="logout">
                    <i class="el-icon-switch-button"></i> 退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </div>
          </div>
        </el-header>
        
        <!-- Content Area -->
        <el-main class="main-content">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import { logout } from '@/api/auth'

export default {
  name: 'AdminDashboard',
  computed: {
    user() {
      return this.$store.state.user
    }
  },
  methods: {
    async handleCommand(command) {
      if (command === 'logout') {
        try {
          await logout()
          this.$store.dispatch('clearUser')
          this.$message.success('退出登录成功')
          this.$router.replace('/admin/login').catch(err => {
            if (err.name !== 'NavigationDuplicated') {
              throw err
            }
          })
        } catch (error) {
          this.$message.error('退出登录失败')
        }
      }
    }
  }
}
</script>

<style scoped>
.admin-dashboard {
  height: 100vh;
}

/* ── macOS Sidebar ── */
.sidebar {
  background: rgba(246, 246, 246, 0.85);
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  border-right: 1px solid rgba(0, 0, 0, 0.1);
  height: 100vh;
  overflow-y: auto;
  overflow-x: hidden;
}

.sidebar-logo {
  height: 52px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 0 16px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.logo-icon {
  width: 22px;
  height: 22px;
  color: #007AFF;
  flex-shrink: 0;
}

.logo-text {
  font-size: 15px;
  font-weight: 600;
  color: #1D1D1F;
  letter-spacing: -0.2px;
}

.sidebar-menu {
  padding: 8px;
}

.sidebar-menu .el-menu-item {
  height: 36px;
  line-height: 36px;
  margin: 1px 0;
  border-radius: 6px;
  font-size: 13px;
  transition: background 0.15s ease, color 0.15s ease;
}

.sidebar-menu .el-menu-item i {
  font-size: 16px;
  margin-right: 8px;
}

.sidebar-menu .el-menu-item:hover {
  background-color: rgba(0, 0, 0, 0.04) !important;
}

.sidebar-menu .el-menu-item.is-active {
  background: #FFFFFF !important;
  color: #007AFF !important;
  border-radius: 6px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

/* ── Top Bar ── */
.top-bar {
  background: rgba(255, 255, 255, 0.72);
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  padding: 0 24px;
  height: 52px !important;
}

.header-content {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.page-title {
  font-size: 15px;
  font-weight: 600;
  color: #1D1D1F;
  letter-spacing: -0.2px;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 6px;
  transition: background 0.15s ease;
}

.user-dropdown:hover {
  background: rgba(0, 0, 0, 0.04);
}

.user-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: #007AFF;
  color: #FFFFFF;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 600;
}

.user-name {
  font-size: 14px;
  color: #1D1D1F;
  font-weight: 500;
}

.user-dropdown .el-icon-arrow-down {
  font-size: 12px;
  color: #8E8E93;
}

/* ── Main Content ── */
.main-content {
  background: #F5F5F7;
  padding: 24px;
  height: 0;
  flex: 1;
  overflow-y: auto;
}

.el-container {
  height: 100vh;
}

.el-container .el-container {
  height: 100%;
}
</style>
