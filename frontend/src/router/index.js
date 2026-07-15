import Vue from 'vue'
import VueRouter from 'vue-router'
import { isMobile } from '@/utils/device'
import { isLoggedIn, isAdmin, isStudent } from '@/utils/auth'
import store from '@/store'

Vue.use(VueRouter)

// 移动端路由
const mobileRoutes = [
  {
    path: '/mobile',
    redirect: '/mobile/login'
  },
  {
    path: '/mobile/login',
    name: 'MobileLogin',
    component: () => import('@/views/mobile/Login.vue')
  },
  {
    path: '/mobile/register',
    name: 'MobileRegister',
    component: () => import('@/views/mobile/Register.vue')
  },
  {
    path: '/mobile/home',
    name: 'MobileHome',
    component: () => import('@/views/mobile/Home.vue'),
    meta: { requiresAuth: true, requiresStudent: true }
  },

  {
    path: '/mobile/my-reservations',
    name: 'MobileMyReservations',
    component: () => import('@/views/mobile/MyReservations.vue'),
    meta: { requiresAuth: true, requiresStudent: true }
  },
  {
    path: '/mobile/qrcode/:orderNo',
    name: 'MobileQRCode',
    component: () => import('@/views/mobile/QRCode.vue'),
    meta: { requiresAuth: true, requiresStudent: true }
  },
  {
    path: '/mobile/my-points',
    name: 'MobileMyPoints',
    component: () => import('@/views/mobile/MyPoints.vue'),
    meta: { requiresAuth: true, requiresStudent: true }
  },
  {
    path: '/mobile/profile',
    name: 'MobileProfile',
    component: () => import('@/views/mobile/Profile.vue'),
    meta: { requiresAuth: true, requiresStudent: true }
  },
  {
    path: '/mobile/notifications',
    name: 'MobileNotifications',
    component: () => import('@/views/mobile/Notifications.vue'),
    meta: { requiresAuth: true, requiresStudent: true }
  },
  {
    path: '/mobile/my-feedbacks',
    name: 'MobileMyFeedbacks',
    component: () => import('@/views/mobile/MyFeedbacks.vue'),
    meta: { requiresAuth: true, requiresStudent: true }
  }
]

// PC端路由
const adminRoutes = [
  {
    path: '/admin',
    redirect: '/admin/login'
  },
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('@/views/admin/Login.vue')
  },
  {
    path: '/admin/dashboard',
    name: 'AdminDashboard',
    component: () => import('@/views/admin/Dashboard.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: 'libraries',
        name: 'LibraryManagement',
        component: () => import('@/views/admin/LibraryManagement.vue')
      },
      {
        path: 'seats',
        name: 'SeatManagement',
        component: () => import('@/views/admin/SeatManagement.vue')
      },
      {
        path: 'reservations',
        name: 'ReservationManagement',
        component: () => import('@/views/admin/ReservationManagement.vue')
      },
      {
        path: 'users',
        name: 'UserManagement',
        component: () => import('@/views/admin/UserManagement.vue')
      },
      {
        path: 'announcements',
        name: 'AnnouncementManagement',
        component: () => import('@/views/admin/AnnouncementManagement.vue')
      },
      {
        path: 'banners',
        name: 'BannerManagement',
        component: () => import('@/views/admin/BannerManagement.vue')
      },
      {
        path: 'seats-monitor',
        name: 'SeatsMonitor',
        component: () => import('@/views/admin/SeatsMonitor.vue')
      },
      {
        path: 'data-analysis',
        name: 'DataAnalysis',
        component: () => import('@/views/admin/DataAnalysis.vue')
      },
      {
        path: 'scanner',
        name: 'QRScanner',
        component: () => import('@/views/admin/QRScanner.vue')
      },
      {
        path: 'disputes',
        name: 'DisputeManagement',
        component: () => import('@/views/admin/DisputeManagement.vue')
      },
      {
        path: 'violations',
        name: 'ViolationManagement',
        component: () => import('@/views/admin/ViolationManagement.vue')
      },
      {
        path: 'feedbacks',
        name: 'FeedbackManagement',
        component: () => import('@/views/admin/FeedbackManagement.vue')
      }
    ]
  }
]

// 系统管理员路由
const systemAdminRoutes = [
  {
    path: '/system-admin',
    redirect: '/system-admin/login'
  },
  {
    path: '/system-admin/login',
    name: 'SystemAdminLogin',
    component: () => import('@/views/system-admin/Login.vue')
  },
  {
    path: '/system-admin/dashboard',
    name: 'SystemAdminDashboard',
    component: () => import('@/views/system-admin/Dashboard.vue'),
    meta: { requiresAuth: true, requiresSystemAdmin: true },
    children: [
      {
        path: 'users',
        name: 'SystemUserManagement',
        component: () => import('@/views/system-admin/UserManagement.vue')
      },
      {
        path: 'points-records',
        name: 'PointsRecordsOverview',
        component: () => import('@/views/system-admin/PointsRecordsOverview.vue')
      },
      {
        path: 'rules',
        name: 'ReservationRules',
        component: () => import('@/views/system-admin/ReservationRules.vue')
      },
      {
        path: 'violation-rules',
        name: 'ViolationRules',
        component: () => import('@/views/system-admin/ViolationRules.vue')
      },
      {
        path: 'operation-logs',
        name: 'OperationLogs',
        component: () => import('@/views/system-admin/OperationLogs.vue')
      }
    ]
  }
]

const routes = [
  {
    path: '/',
    name: 'Portal',
    component: () => import('@/views/Portal.vue')
  },
  {
    path: '/auto',
    redirect: () => {
      return isMobile() ? '/mobile' : '/admin'
    }
  },
  ...mobileRoutes,
  ...adminRoutes,
  ...systemAdminRoutes
]

const router = new VueRouter({
  mode: 'hash',
  base: process.env.BASE_URL,
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  console.log('[RouteGuard] 导航:', from.path, '->', to.path)
  
  // 检查是否需要系统管理员权限（优先检查）
  if (to.matched.some(record => record.meta.requiresSystemAdmin)) {
    const user = store.state.user || JSON.parse(localStorage.getItem('user') || '{}')
    console.log('[RouteGuard] 系统管理员检查, userType:', user?.userType)
    if (!user || user.userType !== '系统管理员') {
      next('/system-admin/login')
      return
    }
    // 是系统管理员，直接放行，不再执行后续检查
    next()
    return
  }
  
  // 检查是否需要登录（非系统管理员路由）
  if (to.matched.some(record => record.meta.requiresAuth)) {
    const loggedIn = isLoggedIn()
    console.log('[RouteGuard] 登录检查:', loggedIn)
    if (!loggedIn) {
      // 未登录，跳转到登录页（始终跳转到移动端登录页，因为这是移动端入口）
      next('/mobile/login')
      return
    }
  }
  
  // 检查是否需要管理员权限
  if (to.matched.some(record => record.meta.requiresAdmin)) {
    const adminCheck = isAdmin()
    console.log('[RouteGuard] 管理员检查:', adminCheck)
    if (!adminCheck) {
      // 非管理员：如果已登录则是学生，跳转学生首页；否则跳登录页
      if (isLoggedIn()) {
        next('/mobile/home')
      } else {
        next('/mobile/login')
      }
      return
    }
  }
  
  // 检查是否需要学生权限
  if (to.matched.some(record => record.meta.requiresStudent)) {
    const studentCheck = isStudent()
    const currentUser = store.state.user || JSON.parse(localStorage.getItem('user') || '{}')
    console.log('[RouteGuard] 学生检查:', studentCheck, 'userType:', currentUser?.userType)
    if (!studentCheck) {
      // 非学生：如果已登录则是管理员，跳转到管理后台；否则跳登录页
      if (isLoggedIn()) {
        const user = store.state.user || JSON.parse(localStorage.getItem('user') || '{}')
        if (user.userType === '系统管理员') {
          next('/system-admin/dashboard')
        } else {
          next('/admin/dashboard/libraries')
        }
      } else {
        next('/mobile/login')
      }
      return
    }
  }
  
  console.log('[RouteGuard] 放行:', to.path)
  next()
})

export default router