<template>
  <div class="profile-page">
    <van-nav-bar title="个人中心" left-arrow @click-left="$router.back()" />
    
    <div class="content">
      <!-- 用户信息卡片 - Apple 蓝色渐变风格 -->
      <div class="user-card">
        <div class="card-bg-decoration"></div>
        <div class="card-content">
          <div class="avatar-section">
            <div class="avatar-wrap">
              <van-image
                round
                width="58"
                height="58"
                :src="userAvatar"
              />
            </div>
            <div class="user-info">
              <div class="username">{{ userInfo.realName || userInfo.username }}</div>
              <div class="credit-badge" :class="getCreditLevelClass(userPoints.creditLevel)">
                {{ userPoints.creditLevel || '未评定' }}
              </div>
            </div>
          </div>
          
          <div class="points-section">
            <div class="points-item">
              <div class="points-value">{{ userPoints.currentPoints || 0 }}</div>
              <div class="points-label">当前积分</div>
            </div>
            <div class="points-divider"></div>
            <div class="points-item">
              <div class="points-value">{{ userPoints.maxReservationHours || 0 }}h</div>
              <div class="points-label">最大时长</div>
            </div>
            <div class="points-divider"></div>
            <div class="points-item">
              <div class="points-value">{{ userPoints.maxReservationCount || 0 }}次</div>
              <div class="points-label">每日次数</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 功能菜单 - iOS 分组列表风格 -->
      <div class="menu-section">
        <div class="section-label">常用功能</div>
        <van-cell-group class="menu-group" :border="false">
          <van-cell title="个人信息" icon="contact" is-link @click="showEditDialog = true" />
          <van-cell title="预约二维码" icon="qr" is-link @click="goToQRCode" />
          <van-cell title="消息通知" icon="volume-o" is-link @click="$router.push('/mobile/notifications')">
            <template #right-icon>
              <span v-if="unreadCount > 0" class="unread-badge">{{ unreadCount > 99 ? '99+' : unreadCount }}</span>
              <van-icon name="arrow" class="van-cell__right-icon" />
            </template>
          </van-cell>
        </van-cell-group>
      </div>

      <div class="menu-section">
        <div class="section-label">更多服务</div>
        <van-cell-group class="menu-group" :border="false">
          <van-cell title="积分明细" icon="balance-list-o" is-link @click="$router.push('/mobile/my-points')" />
          <van-cell title="我的预约" icon="orders-o" is-link @click="$router.push('/mobile/my-reservations')" />
          <van-cell title="意见反馈" icon="chat-o" is-link @click="$router.push('/mobile/my-feedbacks')" />
        </van-cell-group>
      </div>
      
      <!-- 退出登录 -->
      <div class="logout-section">
        <van-button plain block hairline type="danger" class="logout-btn" @click="handleLogout">退出登录</van-button>
      </div>
    </div>
    
    <!-- 底部导航 -->
    <van-tabbar v-model="bottomTab">
      <van-tabbar-item icon="home-o" to="/mobile/home">首页</van-tabbar-item>
      <van-tabbar-item icon="orders-o" to="/mobile/my-reservations">我的预约</van-tabbar-item>
      <van-tabbar-item icon="contact-o" to="/mobile/profile">我的</van-tabbar-item>
    </van-tabbar>
    
    <!-- 编辑信息对话框 -->
    <van-dialog
      v-model="showEditDialog"
      title="编辑个人信息"
      show-cancel-button
      @confirm="saveUserInfo"
    >
      <van-form @submit="saveUserInfo">
        <van-field
          v-model="editForm.username"
          label="用户名"
          disabled
        />
        <van-field
          v-model="editForm.realName"
          label="真实姓名"
          placeholder="请输入真实姓名"
          :rules="[{ required: true, message: '请输入真实姓名' }]"
        />
        <van-field
          v-model="editForm.phone"
          label="手机号"
          placeholder="请输入手机号"
          :rules="[{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号' }]"
        />
        <van-field
          v-model="editForm.email"
          label="邮箱"
          placeholder="请输入邮箱"
          :rules="[{ pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/, message: '请输入正确的邮箱' }]"
        />
      </van-form>
    </van-dialog>
  </div>
</template>

<script>
import { getMyPoints } from '@/api/points'
import { updateUserInfo } from '@/api/user'
import { logout } from '@/api/auth'
import { getReservationPage } from '@/api/reservation'
import { getUnreadCount } from '@/api/notification'

export default {
  name: 'Profile',
  data() {
    return {
      userInfo: {},
      userPoints: {},
      bottomTab: 2,
      showEditDialog: false,
      unreadCount: 0,
      editForm: {
        username: '',
        realName: '',
        phone: '',
        email: ''
      }
    }
  },
  
  computed: {
    userAvatar() {
      return 'https://img.yzcdn.cn/vant/cat.jpeg'
    }
  },
  
  async created() {
    if (this.$store.state.user) {
      this.userInfo = this.$store.state.user
      this.editForm = {
        username: this.userInfo.username || '',
        realName: this.userInfo.realName || '',
        phone: this.userInfo.phone || '',
        email: this.userInfo.email || ''
      }
    }
    
    await this.loadUserPoints()
    await this.loadUnreadCount()
  },
  
  methods: {
    async loadUnreadCount() {
      try {
        const res = await getUnreadCount()
        this.unreadCount = res.data || 0
      } catch (error) {
        console.error('加载未读数量失败', error)
      }
    },
    async loadUserPoints() {
      try {
        const res = await getMyPoints()
        this.userPoints = res.data
      } catch (error) {
        console.error('加载积分信息失败', error)
      }
    },
    
    async saveUserInfo() {
      try {
        await updateUserInfo(this.editForm)
        this.$toast.success('保存成功')
        this.showEditDialog = false
        
        const updatedUser = { ...this.userInfo, ...this.editForm }
        this.$store.commit('SET_USER', updatedUser)
        this.userInfo = updatedUser
      } catch (error) {
        this.$toast.fail(error.message || '保存失败')
      }
    },
    
    async handleLogout() {
      try {
        await this.$dialog.confirm({
          title: '提示',
          message: '确定要退出登录吗？'
        })
        
        await logout()
        this.$store.dispatch('logout')
        this.$router.replace('/mobile/login')
      } catch (error) {
        if (error !== 'cancel') {
          this.$toast.fail('退出失败')
        }
      }
    },
    
    getCreditLevelClass(level) {
      const classMap = {
        '极好': 'excellent',
        '良好': 'good',
        '一般': 'normal',
        '较差': 'poor',
        '暂停服务': 'suspended'
      }
      return classMap[level] || ''
    },
    
    async goToQRCode() {
      try {
        const res = await getReservationPage({
          pageNum: 1,
          pageSize: 10,
          sortBy: 'createTime',
          sortOrder: 'desc'
        })
        
        const reservations = res.data.records || []
        
        const activeReservation = reservations.find(r => 
          r.status === '已预约' || r.status === '已使用' || r.status === '暂离'
        )
        
        if (activeReservation) {
          this.$router.push(`/mobile/qrcode/${activeReservation.orderNo}`)
        } else {
          this.$toast('当前没有预约')
        }
      } catch (error) {
        console.error('获取预约信息失败', error)
        this.$toast.fail('加载失败')
      }
    }
  }
}
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background: #f2f2f7;
  padding-bottom: 70px;
}

.content {
  padding: 14px 16px;
}

/* ====== 用户卡片 - Apple 蓝色渐变风格 ====== */
.user-card {
  position: relative;
  border-radius: 16px;
  overflow: hidden;
  margin-bottom: 22px;
  box-shadow: 0 4px 20px rgba(0, 122, 255, 0.18);
}

.card-bg-decoration {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #007AFF 0%, #5ac8fa 100%);
}

.card-content {
  position: relative;
  z-index: 1;
  padding: 24px 20px 20px;
  color: white;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 22px;
}

.avatar-wrap {
  flex-shrink: 0;
  width: 62px;
  height: 62px;
  border-radius: 50%;
  border: 2.5px solid rgba(255, 255, 255, 0.35);
  overflow: hidden;
  background: rgba(255, 255, 255, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-info {
  flex: 1;
}

.username {
  font-size: 21px;
  font-weight: 600;
  margin-bottom: 6px;
  letter-spacing: -0.3px;
}

.credit-badge {
  display: inline-block;
  padding: 3px 11px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 500;
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(4px);
}

.points-section {
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding-top: 18px;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
}

.points-item {
  text-align: center;
  flex: 1;
}

.points-value {
  font-size: 23px;
  font-weight: 700;
  margin-bottom: 4px;
  letter-spacing: -0.5px;
}

.points-label {
  font-size: 12px;
  opacity: 0.8;
  font-weight: 400;
}

.points-divider {
  width: 1px;
  height: 32px;
  background: rgba(255, 255, 255, 0.2);
}

/* ====== 菜单分组 ====== */
.menu-section {
  margin-bottom: 18px;
}

.section-label {
  font-size: 13px;
  color: #8e8e93;
  padding: 0 12px;
  margin-bottom: 6px;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.menu-group {
  border-radius: 12px;
  overflow: hidden;
  background: white;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.menu-group >>> .van-cell {
  padding: 14px 16px;
  transition: background 0.15s ease;
}

.menu-group >>> .van-cell:not(:last-child)::after {
  left: 52px;
  right: 16px;
  border-color: #e5e5ea;
}

.menu-group >>> .van-cell:active {
  background: #f2f2f7;
}

.menu-group >>> .van-icon {
  color: #007AFF;
  font-size: 20px;
  margin-right: 10px;
}

.menu-group >>> .van-cell__title {
  font-size: 15px;
  color: #1d1d1f;
  font-weight: 500;
}

.unread-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  background: #FF3B30;
  color: white;
  font-size: 11px;
  border-radius: 9px;
  margin-right: 4px;
  font-weight: 600;
}

/* ====== 退出登录 ====== */
.logout-section {
  margin-top: 28px;
  padding: 0 4px;
}

.logout-btn {
  height: 48px;
  border-radius: 12px;
  border-color: #FF3B30;
  color: #FF3B30;
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 0.3px;
  transition: all 0.2s ease;
}

.logout-btn:active {
  background: rgba(255, 59, 48, 0.08);
}
</style>
