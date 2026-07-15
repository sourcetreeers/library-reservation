<template>
  <div class="my-reservations">
    <van-nav-bar title="我的预约" fixed>
      <template #left>
        <van-icon name="arrow-left" @click="$router.go(-1)" />
      </template>
      <template #right>
        <van-icon name="replay" @click="refreshData" />
      </template>
    </van-nav-bar>

    <div class="content">
      <!-- 状态筛选 -->
      <van-tabs v-model="activeTab" @change="onTabChange" sticky swipeable>
        <van-tab title="全部" name=""></van-tab>
        <van-tab title="已预约" name="已预约"></van-tab>
        <van-tab title="已使用" name="已使用"></van-tab>
        <van-tab title="已取消" name="已取消"></van-tab>
        <van-tab title="爽约" name="爽约"></van-tab>
      </van-tabs>

      <!-- 预约列表 -->
      <van-list
        v-model="loading"
        :finished="finished"
        finished-text="没有更多了"
        :offset="50"
        :immediate-check="false"
        @load="onLoad"
      >
        <div
          v-for="item in reservations"
          :key="item.id"
          class="reservation-item"
        >
          <div class="item-header">
            <div class="header-left">
              <span class="order-no">{{ item.orderNo }}</span>
              <span class="status" :class="getStatusClass(item.status)">
                {{ item.status }}
              </span>
            </div>
            <span class="time-label">{{ formatTime(item.createTime) }}</span>
          </div>

          <div class="item-body">
            <div class="info-row">
              <svg class="info-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
                <polyline points="9,22 9,12 15,12 15,22"/>
              </svg>
              <span class="value">{{ item.libraryName }}</span>
            </div>
            <div class="info-row">
              <svg class="info-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="4" y="4" width="16" height="16" rx="2"/>
                <path d="M9 9h6v6H9z"/>
              </svg>
              <span class="value">座位 {{ item.seatNumber }}</span>
            </div>
            <div class="info-row">
              <svg class="info-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="10"/><polyline points="12,6 12,12 16,14"/>
              </svg>
              <span class="value">{{ formatTimeRange(item.startTime, item.endTime) }}</span>
            </div>
          </div>

          <div class="item-actions">
            <van-button
              v-if="item.status === '已预约'"
              size="small"
              round
              type="info"
              @click="showQRCode(item)"
            >
              二维码
            </van-button>
            <van-button
              v-if="item.status === '已预约'"
              size="small"
              round
              plain
              hairline
              type="danger"
              @click="cancelReservation(item)"
            >
              取消预约
            </van-button>
            <van-button
              v-if="item.status === '爽约'"
              size="small"
              round
              plain
              hairline
              type="warning"
              @click="showComplaintDialog(item)"
            >
              举报占座
            </van-button>
          </div>
        </div>
      </van-list>
    </div>

    <!-- 举报占座对话框 -->
    <van-dialog
      v-model="complaintDialogVisible"
      title="举报占座"
      show-cancel-button
      :before-close="submitComplaint"
    >
      <div class="complaint-form">
        <div v-if="complaintTarget" class="complaint-info">
          <p><strong>图书室：</strong>{{ complaintTarget.libraryName }}</p>
          <p><strong>座位号：</strong>{{ complaintTarget.seatNumber }}</p>
          <p><strong>预约时间：</strong>{{ formatTimeRange(complaintTarget.startTime, complaintTarget.endTime) }}</p>
        </div>
        <van-field
          v-model="complaintDescription"
          type="textarea"
          rows="4"
          maxlength="500"
          placeholder="请描述占座情况（选填，最多500字）"
          show-word-limit
        />
        <div class="complaint-uploader">
          <p class="uploader-label">上传证据照片（选填）：</p>
          <van-uploader
            v-model="complaintImages"
            :max-count="3"
            :max-size="5 * 1024 * 1024"
            :before-read="beforeImageRead"
            @oversize="onImageOversize"
            multiple
            :after-read="afterImageRead"
            :deleteable="!uploadingImage"
          />
        </div>
      </div>
    </van-dialog>

    <!-- 底部导航 -->
    <van-tabbar v-model="bottomTab" @change="onBottomTabChange">
      <van-tabbar-item icon="home-o" to="/mobile/home">首页</van-tabbar-item>
      <van-tabbar-item icon="orders-o" to="/mobile/my-reservations">我的预约</van-tabbar-item>
      <van-tabbar-item icon="contact-o" to="/mobile/profile">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script>
import { getReservationPage, cancelReservation } from '@/api/reservation'
import { createComplaint, uploadImage } from '@/api/complaint'
import { formatTime as formatTimeUtil, formatTimeRange as formatTimeRangeUtil } from '@/utils/time'

export default {
  name: 'MyReservations',
  data() {
    return {
      activeTab: '',
      bottomTab: 1,
      loading: false,
      finished: false,
      reservations: [],
      pagination: {
        current: 1,
        size: 10
      },
      complaintDialogVisible: false,
      complaintTarget: null,
      complaintDescription: '',
      complaintImages: [],
      uploadingImage: false,
      uploadedImageUrl: ''
    }
  },
  
  created() {},
  
  mounted() {
    this.loadReservations()
  },
  
  methods: {
    async loadReservations() {
      if (this.finished || this.loading) return
      
      this.loading = true

      try {
        const params = {
          current: this.pagination.current,
          size: this.pagination.size,
          status: this.activeTab
        }

        const res = await getReservationPage(params)
        const newRecords = res.data.records || []

        if (this.pagination.current === 1) {
          this.reservations = newRecords
        } else {
          this.reservations.push(...newRecords)
        }
        
        const returnedCount = newRecords.length
        const requestedSize = this.pagination.size
        const totalCount = res.data.total || 0
        const loadedCount = this.reservations.length
        
        this.finished = returnedCount < requestedSize || (totalCount > 0 && loadedCount >= totalCount)
        this.pagination.current++

      } catch (error) {
        this.$toast.fail('加载预约记录失败')
        this.finished = true
      } finally {
        this.loading = false
      }
    },

    onLoad() {
      this.loadReservations()
    },

    onTabChange() {
      this.resetList()
      this.loadReservations()
    },

    resetList() {
      this.reservations = []
      this.pagination.current = 1
      this.finished = false
      this.loading = false
    },

    async cancelReservation(item) {
      try {
        await this.$dialog.confirm({ title: '确认取消', message: '确定要取消这个预约吗？' })
        await cancelReservation(item.id)
        this.$toast.success('取消成功')
        item.status = '已取消'
      } catch (error) {
        if (error !== 'cancel') {
          this.$toast.fail('取消失败')
        }
      }
    },

    showQRCode(item) {
      this.$router.push(`/mobile/qrcode/${item.orderNo}`)
    },

    formatTime(timeStr) {
      return formatTimeUtil(timeStr)
    },

    formatTimeRange(startTime, endTime) {
      return formatTimeRangeUtil(startTime, endTime)
    },

    getStatusClass(status) {
      const statusMap = {
        '已预约': 'status-reserved',
        '已使用': 'status-used',
        '爽约': 'status-missed',
        '已取消': 'status-cancelled'
      }
      return statusMap[status] || ''
    },

    onBottomTabChange(index) {
      if (index === 0) this.$router.push('/mobile/home')
    },

    showComplaintDialog(item) {
      this.complaintTarget = item
      this.complaintDescription = ''
      this.complaintImages = []
      this.uploadedImageUrl = ''
      this.complaintDialogVisible = true
    },

    beforeImageRead(file) {
      const isImage = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/gif'
      if (!isImage) { this.$toast.fail('仅支持 JPG/PNG/GIF 格式的图片'); return false }
      return true
    },

    onImageOversize() {
      this.$toast.fail('图片大小不能超过5MB')
    },

    async afterImageRead(file) {
      this.uploadingImage = true
      try {
        const res = await uploadImage(file.file)
        this.uploadedImageUrl = res.data
        this.$toast.success('图片上传成功')
      } catch (error) {
        this.$toast.fail('图片上传失败')
        this.complaintImages = []
      } finally {
        this.uploadingImage = false
      }
    },

    async submitComplaint(action, done) {
      if (action === 'confirm') {
        try {
          await createComplaint({
            reservationId: this.complaintTarget.id,
            description: (this.complaintDescription || '').trim(),
            imageUrl: this.uploadedImageUrl || ''
          })
          this.$toast.success('举报已提交，请等待管理员审核')
          this.complaintDialogVisible = false
          done()
        } catch (error) {
          this.$toast.fail('举报提交失败')
          done(false)
        }
      } else {
        done()
      }
    },

    refreshData() {
      this.resetList()
      this.loadReservations()
    }
  }
}
</script>

<style scoped>
.my-reservations {
  padding-top: 46px;
  padding-bottom: 50px;
  min-height: 100vh;
  background-color: #f2f2f7;
}

.content {
  padding: 0;
}

/* ====== 预约卡片 - Apple iOS 列表风格 ====== */
.reservation-item {
  background: white;
  margin: 10px 14px;
  border-radius: 13px;
  padding: 16px;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.04);
  transition: transform 0.15s ease;
}

.reservation-item:active {
  transform: scale(0.99);
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.order-no {
  font-size: 13px;
  font-weight: 600;
  color: #8e8e93;
  font-family: 'SF Mono', 'Menlo', monospace;
  letter-spacing: -0.3px;
}

.status {
  padding: 2px 10px;
  border-radius: 10px;
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.3px;
}

.status-reserved {
  background: rgba(0, 122, 255, 0.1);
  color: #007AFF;
}

.status-used {
  background: rgba(52, 199, 89, 0.1);
  color: #34C759;
}

.status-missed {
  background: rgba(255, 59, 48, 0.1);
  color: #FF3B30;
}

.status-cancelled {
  background: rgba(142, 142, 147, 0.12);
  color: #8e8e93;
}

.time-label {
  font-size: 12px;
  color: #c7c7cc;
}

/* ====== 信息行 - 带图标的列表项风格 ====== */
.item-body {
  margin-bottom: 14px;
  padding: 10px 0;
  border-top: 1px solid #f2f2f7;
  border-bottom: 1px solid #f2f2f7;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 5px 0;
}

.info-icon {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
  color: #8e8e93;
}

.info-row .value {
  font-size: 14px;
  color: #1d1d1f;
  font-weight: 400;
}

/* ====== 操作按钮 ====== */
.item-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.item-actions .van-button {
  height: 32px;
  font-size: 13px;
  font-weight: 500;
}

/* ====== 举报对话框 ====== */
.complaint-form {
  padding: 16px;
}

.complaint-info {
  background: #f2f2f7;
  border-radius: 10px;
  padding: 12px;
  margin-bottom: 12px;
}

.complaint-info p {
  margin: 4px 0;
  font-size: 13px;
  color: #3c3c43;
}

.complaint-info strong {
  color: #8e8e93;
}

.complaint-uploader {
  margin-top: 12px;
  padding: 0 16px;
}

.uploader-label {
  font-size: 13px;
  color: #8e8e93;
  margin-bottom: 8px;
}
</style>
