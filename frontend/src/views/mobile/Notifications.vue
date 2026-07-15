<template>
  <div class="notifications-page">
    <van-nav-bar title="消息通知" left-arrow @click-left="$router.back()">
      <template #right>
        <van-icon name="replay" @click="loadData" />
      </template>
    </van-nav-bar>
    
    <!-- 全部已读按钮 -->
    <div class="header-actions" v-if="notifications.length > 0 && unreadCount > 0">
      <van-button size="small" round plain hairline type="info" @click="markAllRead">
        全部标为已读
      </van-button>
    </div>
    
    <!-- 通知列表 -->
    <van-list
      v-model="loading"
      :finished="finished"
      finished-text="没有更多了"
      @load="onLoad"
    >
      <div class="notification-list">
        <div
          v-for="item in notifications"
          :key="item.id"
          class="notification-item"
          :class="{ unread: item.isRead === 0 }"
          @click="onNotificationClick(item)"
        >
          <div class="notification-icon-wrapper" :class="getIconBgClass(item.type)">
            <van-icon
              :name="getIconName(item.type)"
              size="20"
            />
          </div>
          <div class="notification-content">
            <div class="notification-title">
              <span class="title-text">{{ item.title }}</span>
              <span class="notification-type-tag" :class="getTypeClass(item.type)">{{ item.type }}</span>
            </div>
            <div class="notification-body">{{ item.content }}</div>
            <div class="notification-footer">
              <span class="notification-time">{{ formatTime(item.createTime) }}</span>
              <span v-if="item.isRead === 0" class="unread-dot"></span>
            </div>
          </div>
        </div>
        
        <div v-if="notifications.length === 0 && !loading" class="empty-state">
          <div class="empty-icon-wrap">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.3">
              <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/>
              <path d="M13.73 21a2 2 0 0 1-3.46 0"/>
            </svg>
          </div>
          <p>暂无消息通知</p>
        </div>
      </div>
    </van-list>
  </div>
</template>

<script>
import { getNotificationList, markNotificationRead, markAllNotificationsRead } from '@/api/notification'

export default {
  name: 'Notifications',
  data() {
    return {
      notifications: [],
      loading: false,
      finished: false,
      current: 1,
      unreadCount: 0
    }
  },
  methods: {
    async onLoad() {
      try {
        const res = await getNotificationList({ current: this.current, size: 15 })
        const data = res.data
        const records = data.records || []
        
        this.notifications = this.current === 1 ? records : [...this.notifications, ...records]
        this.finished = this.notifications.length >= data.total
        this.current++
        
        this.unreadCount = this.notifications.filter(n => n.isRead === 0).length
      } catch (error) {
        this.$toast.fail('加载失败')
      } finally {
        this.loading = false
      }
    },
    
    async loadData() {
      this.current = 1
      this.notifications = []
      this.finished = false
      this.loading = true
      await this.onLoad()
    },
    
    async onNotificationClick(item) {
      if (item.isRead === 0) {
        try {
          await markNotificationRead(item.id)
          item.isRead = 1
          this.unreadCount--
        } catch (e) { console.error('标记已读失败', e) }
      }
      
      if (item.linkUrl) {
        this.$router.push(item.linkUrl)
      }
    },
    
    async markAllRead() {
      try {
        await markAllNotificationsRead()
        this.notifications.forEach(n => { n.isRead = 1 })
        this.unreadCount = 0
        this.$toast.success('已全部标为已读')
      } catch (error) {
        this.$toast.fail('操作失败')
      }
    },
    
    getIconName(type) {
      const map = {
        '预约提醒': 'clock-o',
        '扣分通知': 'fail',
        '加分通知': 'success',
        '申诉结果': 'chat-o',
        '系统公告': 'volume-o'
      }
      return map[type] || 'bell-o'
    },
    
    getIconBgClass(type) {
      const map = {
        '预约提醒': 'bg-reminder',
        '扣分通知': 'bg-deduct',
        '加分通知': 'bg-add',
        '申诉结果': 'bg-appeal',
        '系统公告': 'bg-system'
      }
      return map[type] || 'bg-system'
    },

    getTypeClass(type) {
      const map = {
        '预约提醒': 'type-reminder',
        '扣分通知': 'type-deduct',
        '加分通知': 'type-add',
        '申诉结果': 'type-appeal',
        '系统公告': 'type-system'
      }
      return map[type] || ''
    },
    
    formatTime(time) {
      if (!time) return ''
      const date = new Date(time)
      const now = new Date()
      const diff = now - date
      
      if (diff < 60000) return '刚刚'
      if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
      if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
      
      const y = date.getFullYear()
      const m = String(date.getMonth() + 1).padStart(2, '0')
      const d = String(date.getDate()).padStart(2, '0')
      const h = String(date.getHours()).padStart(2, '0')
      const min = String(date.getMinutes()).padStart(2, '0')
      
      if (y === now.getFullYear()) return `${m}-${d} ${h}:${min}`
      return `${y}-${m}-${d} ${h}:${min}`
    }
  }
}
</script>

<style scoped>
.notifications-page {
  min-height: 100vh;
  background: #f2f2f7;
}

.header-actions {
  padding: 10px 16px;
  display: flex;
  justify-content: flex-end;
}

.notification-list {
  padding: 0 14px 20px;
}

/* ====== 通知卡片 ====== */
.notification-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  background: white;
  border-radius: 13px;
  padding: 14px 14px 12px;
  margin-bottom: 10px;
  transition: all 0.2s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.notification-item:active {
  transform: scale(0.99);
}

.notification-item.unread {
  border-left: 3.5px solid #007AFF;
}

/* 图标容器 */
.notification-icon-wrapper {
  flex-shrink: 0;
  width: 38px;
  height: 38px;
  border-radius: 11px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.bg-reminder { background: linear-gradient(135deg, #007AFF, #5ac8fa); }
.bg-deduct { background: linear-gradient(135deg, #FF3B30, #ff6b6b); }
.bg-add { background: linear-gradient(135deg, #34C759, #5dda75); }
.bg-appeal { background: linear-gradient(135deg, #FF9500, #ffb340); }
.bg-system { background: linear-gradient(135deg, #5856D6, #8078e8); }

/* 内容区 */
.notification-content {
  flex: 1;
  min-width: 0;
}

.notification-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  margin-bottom: 4px;
}

.title-text {
  font-size: 15px;
  font-weight: 600;
  color: #1d1d1f;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.notification-type-tag {
  flex-shrink: 0;
  font-size: 10px;
  padding: 2px 8px;
  border-radius: 8px;
  font-weight: 600;
  letter-spacing: 0.3px;
}

.type-reminder { background: rgba(0,122,255,0.08); color: #007AFF; }
.type-deduct { background: rgba(255,59,48,0.08); color: #FF3B30; }
.type-add { background: rgba(52,199,89,0.08); color: #34C759; }
.type-appeal { background: rgba(255,149,0,0.08); color: #C77700; }
.type-system { background: rgba(88,86,214,0.08); color: #5856D6; }

.notification-body {
  font-size: 13px;
  color: #8e8e93;
  line-height: 1.45;
  margin-bottom: 6px;
  word-break: break-all;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.notification-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.notification-time {
  font-size: 12px;
  color: #c7c7cc;
}

.unread-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #007AFF;
  flex-shrink: 0;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 70px 20px;
}

.empty-icon-wrap {
  width: 64px;
  height: 64px;
  margin: 0 auto 14px;
  border-radius: 50%;
  background: #f2f2f7;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #c7c7cc;
}

.empty-icon-wrap svg {
  width: 30px;
  height: 30px;
}

.empty-state p {
  margin: 0;
  font-size: 15px;
  color: #8e8e93;
}
</style>
