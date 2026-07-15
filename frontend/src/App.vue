<template>
  <div id="app">
    <router-view/>
    
    <!-- AI智能助手 - 仅管理员登录后才显示 -->
    <ai-assistant v-if="showAiAssistant" />
  </div>
</template>

<script>
import AiAssistant from '@/components/AiAssistant.vue'
import { getCurrentUser } from '@/utils/auth'

// 不需要AI助手的页面（登录/注册/入口等未认证页面)
const EXCLUDE_ROUTES = [
  'Portal',           // 入口选择页
  'MobileLogin',      // 学生端登录
  'MobileRegister',   // 学生端注册
  'AdminLogin',       // 图书管理员登录
  'SystemAdminLogin'  // 系统管理员登录
]

// 合法的管理员角色值（覆盖中英文）
const ADMIN_ROLES = ['librarian', 'admin', '图书馆管理员', '系统管理员']

export default {
  name: 'App',
  
  components: {
    AiAssistant
  },
  
  data() {
    return {
      showAiAssistant: false
    }
  },
  
  created() {
    this.checkPermission()
    window.addEventListener('storage', this.checkPermission)
  },
  
  beforeDestroy() {
    window.removeEventListener('storage', this.checkPermission)
  },
  
  methods: {
    checkPermission() {
      // 1. 当前是否在登录/入口/注册页
      const routeName = this.$route.name
      if (EXCLUDE_ROUTES.includes(routeName)) {
        this.showAiAssistant = false
        return
      }
      
      // 2. 是否有有效登录用户
      const user = getCurrentUser()
      if (!user || !user.id || !user.userType) {
        this.showAiAssistant = false
        return
      }
      
      // 3. 是否是管理员角色
      this.showAiAssistant = ADMIN_ROLES.includes(user.userType)
    }
  },
  
  watch: {
    '$route'() {
      this.checkPermission()
    }
  }
}
</script>

<style>
/* ═══════════════════════════════════════════════════════════════
   Apple HIG Design System — Global Theme
   ═══════════════════════════════════════════════════════════════ */
:root {
  /* Apple Blue — Primary */
  --apple-blue:        #007AFF;
  --apple-blue-hover:  #0066CC;
  --apple-blue-active: #0055B3;
  --apple-blue-light:  #E8F0FE;
  --apple-blue-50:     #EBF5FF;

  /* Apple Neutral Palette */
  --gray-50:   #F5F5F7;
  --gray-100:  #E8E8ED;
  --gray-200:  #D2D2D7;
  --gray-300:  #AEAEB2;
  --gray-400:  #8E8E93;
  --gray-500:  #636366;
  --gray-600:  #48484A;
  --gray-700:  #3A3A3C;
  --gray-800:  #2C2C2E;
  --gray-900:  #1D1D1F;

  /* Apple Semantic Colors */
  --apple-success:  #34C759;
  --apple-warning:  #FF9500;
  --apple-danger:   #FF3B30;
  --apple-purple:   #AF52DE;

  /* Shadows & Borders */
  --apple-shadow:         0 1px 3px rgba(0, 0, 0, 0.06), 0 1px 2px rgba(0, 0, 0, 0.04);
  --apple-shadow-md:      0 4px 12px rgba(0, 0, 0, 0.08);
  --apple-shadow-lg:      0 12px 40px rgba(0, 0, 0, 0.1);
  --apple-border:         1px solid rgba(0, 0, 0, 0.06);
  --apple-border-input:   1px solid #D2D2D7;

  /* Radius */
  --radius-sm:  6px;
  --radius-md:  10px;
  --radius-lg:  14px;

  /* Backward-compatible aliases */
  --pink-50:  var(--gray-50);
  --pink-100: var(--gray-100);
  --pink-200: var(--gray-200);
  --pink-300: var(--gray-300);
  --pink-400: var(--apple-blue-hover);
  --pink-500: var(--apple-blue);
  --pink-600: var(--apple-blue-hover);
  --pink-700: var(--gray-900);
  --rose-shadow: var(--apple-shadow);
  --soft-border: var(--apple-border);
}

#app {
  font-family: -apple-system, BlinkMacSystemFont, "SF Pro Display", "SF Pro Text", "Helvetica Neue", "PingFang SC", "Microsoft YaHei", sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #1D1D1F;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  background: #F5F5F7;
  color: #1D1D1F;
}

a {
  color: var(--apple-blue);
  text-decoration: none;
}
a:hover {
  text-decoration: underline;
}

/* ═══════════════════════════════════════════════════════════════
   Vant Theme Override — Apple Style
   ═══════════════════════════════════════════════════════════════ */
body,
#app {
  --van-primary-color: #007AFF;
  --van-info-color: #007AFF;
  --van-success-color: #34C759;
  --van-danger-color: #FF3B30;
  --van-warning-color: #FF9500;
  --van-background: #F5F5F7;
  --van-background-2: #FFFFFF;
  --van-text-color: #1D1D1F;
  --van-text-color-2: #86868B;
  --van-text-color-3: #AEAEB2;
  --van-border-color: rgba(0, 0, 0, 0.08);
  --van-active-color: rgba(0, 122, 255, 0.06);
  --van-nav-bar-background: rgba(255, 255, 255, 0.88);
  --van-nav-bar-title-text-color: #1D1D1F;
  --van-nav-bar-icon-color: #007AFF;
  --van-tabbar-item-active-color: #007AFF;
  --van-tabbar-item-text-color: #8E8E93;
  --van-tabs-bottom-bar-color: #007AFF;
  --van-search-content-background: #FFFFFF;
  --van-field-label-color: #86868B;
  --van-cell-background: #FFFFFF;
}

.van-nav-bar,
.van-tabbar,
.van-action-sheet,
.van-popup,
.van-dialog,
.van-cell-group,
.van-search,
.van-picker,
.van-tabs {
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
}

.van-nav-bar {
  box-shadow: 0 0.5px 0 rgba(0, 0, 0, 0.08);
}

.van-tabbar {
  box-shadow: 0 -0.5px 0 rgba(0, 0, 0, 0.08);
}

.van-dialog {
  box-shadow: var(--apple-shadow-lg);
  border-radius: var(--radius-lg);
}

.van-button--info,
.van-button--primary {
  border: none;
  background: #007AFF;
  border-radius: var(--radius-sm);
}
.van-button--info:active,
.van-button--primary:active {
  background: #0066CC;
}

.van-button--default,
.van-button--plain.van-button--info {
  color: #007AFF;
  border-color: rgba(0, 122, 255, 0.3);
  background: #FFFFFF;
}

.van-cell,
.van-search,
.van-tabs__nav,
.van-tabbar {
  background: #FFFFFF;
}

.van-hairline--top-bottom::after,
.van-hairline-unset--top-bottom::after,
.van-hairline--surround::after,
.van-hairline--top::after,
.van-hairline--bottom::after {
  border-color: rgba(0, 0, 0, 0.08) !important;
}

/* ═══════════════════════════════════════════════════════════════
   Element UI Theme Override — Apple Style
   ═══════════════════════════════════════════════════════════════ */

/* — Buttons — */
.el-button--primary {
  border-color: #007AFF;
  background: #007AFF;
  border-radius: var(--radius-sm);
  font-weight: 500;
  transition: background 0.15s ease;
}
.el-button--primary:hover,
.el-button--primary:focus {
  border-color: #0066CC;
  background: #0066CC;
}
.el-button--primary:active {
  background: #0055B3;
}

.el-button--success {
  background: #34C759;
  border-color: #34C759;
  border-radius: var(--radius-sm);
}
.el-button--success:hover,
.el-button--success:focus {
  background: #2DB84D;
  border-color: #2DB84D;
}

.el-button--danger {
  background: #FF3B30;
  border-color: #FF3B30;
  border-radius: var(--radius-sm);
}
.el-button--danger:hover,
.el-button--danger:focus {
  background: #E0342B;
  border-color: #E0342B;
}

.el-button--warning {
  background: #FF9500;
  border-color: #FF9500;
  border-radius: var(--radius-sm);
}

.el-button--default {
  border-radius: var(--radius-sm);
  border-color: #D2D2D7;
  color: #1D1D1F;
}
.el-button--default:hover,
.el-button--default:focus {
  color: #007AFF;
  border-color: #007AFF;
  background: #FFFFFF;
}

.el-dropdown-menu__item:focus,
.el-dropdown-menu__item:not(.is-disabled):hover {
  color: #007AFF;
  background: #F5F5F7;
}

/* — Input / Textarea — */
.el-input__inner,
.el-textarea__inner {
  border-radius: var(--radius-sm);
  border-color: #D2D2D7;
  background: #FFFFFF;
  color: #1D1D1F;
  transition: border-color 0.15s ease, box-shadow 0.15s ease;
}
.el-input__inner:focus,
.el-textarea__inner:focus {
  border-color: #007AFF;
  box-shadow: 0 0 0 3px rgba(0, 122, 255, 0.12);
}

/* — Card / Dialog / Table / Pagination — */
.el-card {
  border-radius: var(--radius-lg);
  border: var(--apple-border);
  box-shadow: var(--apple-shadow);
}

.el-dialog,
.el-message-box {
  border-radius: var(--radius-lg);
  overflow: hidden;
  border: none;
  box-shadow: var(--apple-shadow-lg);
}

.el-table,
.el-pagination {
  border-radius: 0;
}

.el-table,
.el-table th.el-table__cell,
.el-table tr,
.el-table__expanded-cell {
  background: #FFFFFF;
}

.el-table th.el-table__cell {
  color: #86868B;
  background: #F5F5F7;
  font-weight: 500;
  font-size: 13px;
  letter-spacing: 0.3px;
}

.el-table td.el-table__cell,
.el-table th.el-table__cell,
.el-table--border,
.el-table--group {
  border-color: rgba(0, 0, 0, 0.06);
}

.el-table--enable-row-hover .el-table__body tr:hover > td.el-table__cell {
  background: #F5F5F7;
}

.el-table::before {
  background-color: rgba(0, 0, 0, 0.06);
}

/* — Menu (Sidebar) — */
.el-menu {
  border-right: none;
}

.el-menu-item:focus,
.el-menu-item:hover {
  background-color: rgba(0, 0, 0, 0.04) !important;
}

.el-menu-item.is-active {
  background: #FFFFFF !important;
  color: #007AFF !important;
  border-radius: var(--radius-sm);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

/* — Pagination — */
.el-pagination.is-background .el-pager li:not(.disabled).active {
  background-color: #007AFF;
  border-radius: var(--radius-sm);
}

.el-pagination button:hover,
.el-pagination .el-pager li:hover {
  color: #007AFF;
}

/* — Tags — */
.el-tag {
  border-radius: var(--radius-sm);
  font-weight: 500;
}

.el-tag--success {
  background: rgba(52, 199, 89, 0.1);
  border-color: rgba(52, 199, 89, 0.2);
  color: #248A3D;
}

.el-tag--danger {
  background: rgba(255, 59, 48, 0.1);
  border-color: rgba(255, 59, 48, 0.2);
  color: #D70015;
}

.el-tag--warning {
  background: rgba(255, 149, 0, 0.1);
  border-color: rgba(255, 149, 0, 0.2);
  color: #C77700;
}

.el-tag--info {
  background: rgba(0, 122, 255, 0.08);
  border-color: rgba(0, 122, 255, 0.16);
  color: #007AFF;
}

/* — Form Item — */
.el-form-item__label {
  color: #1D1D1F;
  font-weight: 500;
}

/* — Select Dropdown — */
.el-select-dropdown__item.selected {
  color: #007AFF;
  font-weight: 500;
}

/* — Dialog — */
.el-dialog__header {
  padding: 20px 24px 12px;
}
.el-dialog__title {
  font-weight: 600;
  font-size: 17px;
  color: #1D1D1F;
}
.el-dialog__body {
  padding: 12px 24px 20px;
  color: #1D1D1F;
}
.el-dialog__footer {
  padding: 12px 24px 20px;
}

/* — Message Box — */
.el-message-box__title {
  font-weight: 600;
  color: #1D1D1F;
}

/* — Loading — */
.el-loading-spinner .el-loading-text {
  color: #86868B;
}
.el-loading-spinner .path {
  stroke: #007AFF;
}

/* — Breadcrumb — */
.el-breadcrumb__inner {
  color: #86868B;
}
.el-breadcrumb__inner.is-link:hover {
  color: #007AFF;
}

/* — Switch — */
.el-switch.is-checked .el-switch__core {
  border-color: #007AFF;
  background-color: #007AFF;
}
</style>
