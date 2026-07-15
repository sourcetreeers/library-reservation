<template>
  <div class="system-admin-dashboard">
    <el-container>
      <!-- macOS Style Sidebar -->
      <el-aside width="220px" class="sidebar">
        <div class="sidebar-logo">
          <svg class="logo-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <circle cx="12" cy="12" r="3"/>
            <path d="M19.4 15a1.65 1.65 0 00.33 1.82l.06.06a2 2 0 01-2.83 2.83l-.06-.06a1.65 1.65 0 00-1.82-.33 1.65 1.65 0 00-1 1.51V21a2 2 0 01-4 0v-.09A1.65 1.65 0 009 19.4a1.65 1.65 0 00-1.82.33l-.06.06a2 2 0 01-2.83-2.83l.06-.06A1.65 1.65 0 004.68 15a1.65 1.65 0 00-1.51-1H3a2 2 0 010-4h.09A1.65 1.65 0 004.6 9a1.65 1.65 0 00-.33-1.82l-.06-.06a2 2 0 012.83-2.83l.06.06A1.65 1.65 0 009 4.68a1.65 1.65 0 001-1.51V3a2 2 0 014 0v.09a1.65 1.65 0 001 1.51 1.65 1.65 0 001.82-.33l.06-.06a2 2 0 012.83 2.83l-.06.06A1.65 1.65 0 0019.4 9a1.65 1.65 0 001.51 1H21a2 2 0 010 4h-.09a1.65 1.65 0 00-1.51 1z"/>
          </svg>
          <span class="logo-text">系统管理</span>
        </div>
        
        <el-menu
          :default-active="$route.path"
          router
          background-color="transparent"
          text-color="#48484A"
          active-text-color="#007AFF"
          class="sidebar-menu"
        >
          <el-menu-item index="/system-admin/dashboard/users">
            <i class="el-icon-user"></i>
            <span>用户管理</span>
          </el-menu-item>
          
          <el-menu-item index="/system-admin/dashboard/points-records">
            <i class="el-icon-document"></i>
            <span>积分记录</span>
          </el-menu-item>
          
          <el-menu-item index="/system-admin/dashboard/rules">
            <i class="el-icon-setting"></i>
            <span>预约规则配置</span>
          </el-menu-item>
          
          <el-menu-item index="/system-admin/dashboard/violation-rules">
            <i class="el-icon-warning-outline"></i>
            <span>违规规则配置</span>
          </el-menu-item>
          
          <el-menu-item index="/system-admin/dashboard/operation-logs">
            <i class="el-icon-notebook-2"></i>
            <span>操作日志</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      
      <!-- Main Content -->
      <el-container>
        <!-- macOS Style Header -->
        <el-header class="top-bar">
          <div class="header-content">
            <div class="breadcrumb">
              <i class="el-icon-s-home"></i>
              <span>系统管理员控制台</span>
            </div>
            
            <div class="user-info">
              <el-dropdown @command="handleCommand">
                <span class="user-dropdown">
                  <span class="user-avatar">{{ (currentUser.realName || currentUser.username || 'S').charAt(0) }}</span>
                  <span class="user-name">{{ currentUser.realName || currentUser.username }}</span>
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
  name: 'SystemAdminDashboard',
  
  computed: {
    currentUser() {
      return this.$store.state.user || {}
    }
  },
  
  methods: {
    async handleCommand(command) {
      if (command === 'logout') {
        try {
          await logout()
          this.$store.dispatch('clearUser')
          this.$message.success('退出成功')
          this.$router.replace('/system-admin/login').catch(err => {
            if (err.name !== 'NavigationDuplicated') {
              throw err
            }
          })
        } catch (error) {
          this.$message.error('退出失败')
        }
      }
    }
  }
}
</script>

<style scoped>
.system-admin-dashboard {
  height: 100vh;
  overflow: hidden;
}

.system-admin-dashboard >>> .el-container {
  height: 100%;
}

.system-admin-dashboard >>> .el-container > .el-container {
  display: flex;
  flex-direction: column;
  height: 100%;
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
  width: 20px;
  height: 20px;
  color: #8E8E93;
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
  flex-shrink: 0;
}

.header-content {
  height: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 15px;
  font-weight: 600;
  color: #1D1D1F;
  letter-spacing: -0.2px;
}

.breadcrumb i {
  color: #007AFF;
  font-size: 18px;
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
  background: #8E8E93;
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
  flex: 1;
  overflow: auto;
}
</style>
