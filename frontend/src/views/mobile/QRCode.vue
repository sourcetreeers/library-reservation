<template>
  <div class="qrcode-page">
    <van-nav-bar
      title="预约二维码"
      left-text="返回"
      left-arrow
      @click-left="$router.go(-1)"
    />
    
    <div class="content" v-if="reservation">
      <div class="success-hero">
        <span class="eyebrow">Reservation Confirmed</span>
        <h1>预约成功</h1>
        <p>保留此二维码，在签到时向管理员出示</p>
      </div>

      <div class="reservation-info">
        <div class="info-item">
          <span class="label">流水号</span>
          <span class="value">{{ reservation.orderNo }}</span>
        </div>
        <div class="info-item">
          <span class="label">图书馆</span>
          <span class="value">{{ libraryName }}</span>
        </div>
        <div class="info-item">
          <span class="label">座位号</span>
          <span class="value">{{ seatNumber }}</span>
        </div>
        <div class="info-item">
          <span class="label">预约时间</span>
          <span class="value">{{ formatTime(reservation.startTime) }} - {{ formatTime(reservation.endTime) }}</span>
        </div>
        <div class="info-item">
          <span class="label">状态</span>
          <span class="value" :class="getStatusClass(reservation.status)">{{ reservation.status }}</span>
        </div>
      </div>
      
      <div class="qrcode-container">
        <canvas ref="qrcode" class="qrcode"></canvas>
        <p class="qrcode-tip">请向管理员出示此二维码进行签到</p>
        <p class="qrcode-hint">💡 可提前20分钟签到</p>
      </div>
      
      <!-- 学生操作按钮 -->
      <div class="action-section" v-if="reservation.status === '已预约'">
        <van-button type="primary" round block @click="showCheckInDialog">
          扫码签到
        </van-button>
      </div>
      
      <div class="action-section" v-if="reservation.status === '已使用'">
        <van-button type="warning" round block @click="handleTempLeave">
          标记暂离
        </van-button>
        <van-button type="success" round block @click="handleCheckOut" style="margin-top: 10px;">
          提前签退
        </van-button>
      </div>
      
      <div class="action-section" v-if="reservation.status === '暂离'">
        <van-button type="primary" round block @click="handleCancelTempLeave">
          返回座位
        </van-button>
        <p class="tip-text">⚠️ 暂离超过30分钟将自动签退并扣3分</p>
      </div>
      
      <div class="button-container">
        <van-button type="info" round block @click="goToMyReservations">
          查看我的预约
        </van-button>
      </div>
    </div>
    
    <!-- 扫码签到对话框 -->
    <van-popup v-model="showCheckIn" position="bottom" :style="{ height: '80%' }">
      <div class="scanner-container">
        <van-nav-bar
          title="扫码签到"
          left-text="取消"
          left-arrow
          @click-left="showCheckIn = false"
        />
        
        <div class="scanner-box">
          <div class="scanner-frame">
            <div class="corner corner-tl"></div>
            <div class="corner corner-tr"></div>
            <div class="corner corner-bl"></div>
            <div class="corner corner-br"></div>
            <div class="scan-line"></div>
          </div>
          <p class="scanner-tip">请将二维码放入扫描框内</p>
          <p class="scanner-hint">💡 可提前20分钟签到</p>
        </div>
        
        <div class="manual-input">
          <van-field
            v-model="checkInCode"
            placeholder="或手动输入流水号"
            clearable
          />
          <van-button type="primary" block @click="confirmCheckIn" style="margin-top: 15px;">
            确认签到
          </van-button>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script>
import { getReservationDetailByOrderNo, studentCheckIn, studentCheckOut, markTempLeave, cancelTempLeave } from '@/api/reservation'
import QRCode from 'qrcode'
import { formatTime as formatTimeUtil } from '@/utils/time'

export default {
  name: 'MobileQRCode',
  data() {
    return {
      reservation: null,
      libraryName: '',
      seatNumber: '',
      showCheckIn: false,
      checkInCode: ''
    }
  },
  
  async created() {
    const orderNo = this.$route.params.orderNo
    await this.loadReservation(orderNo)
    await this.generateQRCode(orderNo)
  },
  
  methods: {
    async loadReservation(orderNo) {
      try {
        const res = await getReservationDetailByOrderNo(orderNo)
        this.reservation = res.data

        // 校验预约时间段是否已过，已过期的预约不能进入此页面
        if (this.reservation.endTime) {
          const endTime = new Date(this.reservation.endTime).getTime()
          const now = Date.now()
          if (now > endTime && this.reservation.status !== '已完成') {
            // 预约时间已过且非已完成状态，直接重定向到"已完成"的预约列表也不合适，提示后返回
            this.$toast.fail('该预约已过期，无法查看二维码')
            this.$router.replace('/mobile/my-reservations')
            return
          }
        }

        // 如果状态为"已完成"、"已取消"、"爽约"，也不需要展示二维码
        if (['已完成', '已取消', '爽约'].includes(this.reservation.status)) {
          this.$toast.fail('该预约已结束，无法查看二维码')
          this.$router.replace('/mobile/my-reservations')
          return
        }

        // 现在返回的数据包含完整的图书馆和座位信息
        this.libraryName = this.reservation.libraryName || '未知图书馆'
        this.seatNumber = this.reservation.seatNumber || '未知座位'
      } catch (error) {
        this.$toast.fail('加载预约信息失败')
        this.$router.go(-1)
      }
    },
    
    async generateQRCode(orderNo) {
      try {
        await QRCode.toCanvas(this.$refs.qrcode, orderNo, {
          width: 200,
          margin: 2,
          color: {
            dark: '#000000',
            light: '#FFFFFF'
          }
        })
      } catch (error) {
        console.error('生成二维码失败:', error)
      }
    },
    
    formatTime(timeStr) {
      return formatTimeUtil(timeStr)
    },
    
    getStatusClass(status) {
      const statusMap = {
        '已预约': 'status-reserved',
        '已使用': 'status-used',
        '暂离': 'status-temp-leave',
        '爽约': 'status-missed',
        '已完成': 'status-completed',
        '已取消': 'status-cancelled'
      }
      return statusMap[status] || ''
    },
    
    showCheckInDialog() {
      this.checkInCode = this.reservation.orderNo
      this.showCheckIn = true
    },
    
    async confirmCheckIn() {
      if (!this.checkInCode) {
        this.$toast.fail('请输入流水号或扫描二维码')
        return
      }
      
      try {
        await studentCheckIn({ code: this.checkInCode })
        this.$toast.success('签到成功')
        this.showCheckIn = false
        this.reservation.status = '已使用'
      } catch (error) {
        this.$toast.fail(error.message || '签到失败')
      }
    },
    
    async handleCheckOut() {
      try {
        await this.$dialog.confirm({
          title: '确认签退',
          message: '确定要提前签退吗？'
        })
        
        await studentCheckOut()
        this.$toast.success('签退成功')
        this.reservation.status = '已完成'
        
        setTimeout(() => {
          this.$router.push('/mobile/my-reservations')
        }, 1500)
      } catch (error) {
        if (error !== 'cancel') {
          this.$toast.fail(error.message || '签退失败')
        }
      }
    },
    
    async handleTempLeave() {
      try {
        await this.$dialog.confirm({
          title: '标记暂离',
          message: '暂离超过30分钟未返回将自动签退并扣3分，确定吗？'
        })
        
        await markTempLeave()
        this.$toast.success('已标记为暂离')
        this.reservation.status = '暂离'
      } catch (error) {
        if (error !== 'cancel') {
          this.$toast.fail(error.message || '操作失败')
        }
      }
    },
    
    async handleCancelTempLeave() {
      try {
        await cancelTempLeave()
        this.$toast.success('已返回座位')
        this.reservation.status = '已使用'
      } catch (error) {
        this.$toast.fail(error.message || '操作失败')
      }
    },
    
    goToMyReservations() {
      this.$router.push('/mobile/my-reservations')
    }
  }
}
</script>

<style scoped>
.qrcode-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #e8f4fd 0%, #f2f2f7 100%);
}

.content {
  padding: 20px;
}

.success-hero {
  margin-bottom: 18px;
  padding: 22px 20px;
  border-radius: 22px;
  background: #FFFFFF;
  border: 1px solid rgba(0, 0, 0, 0.06);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.eyebrow {
  display: inline-block;
  margin-bottom: 10px;
  color: #007AFF;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.8px;
  text-transform: uppercase;
}

.success-hero h1 {
  color: #1D1D1F;
  font-size: 28px;
  margin-bottom: 8px;
}

.success-hero p {
  color: #86868B;
  font-size: 14px;
}

.reservation-info {
  background: #FFFFFF;
  border-radius: 14px;
  padding: 20px;
  margin-bottom: 20px;
  border: 1px solid rgba(0, 0, 0, 0.06);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.info-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.info-item:last-child {
  border-bottom: none;
}

.label {
  color: #86868B;
  font-size: 14px;
}

.value {
  color: #1D1D1F;
  font-size: 14px;
  font-weight: 500;
}

.status-reserved {
  color: #007AFF;
}

.status-used {
  color: #34C759;
}

.status-missed {
  color: #e11d48;
}

.status-cancelled {
  color: #86868B;
}

.status-temp-leave {
  color: #f59e0b;
}

.status-completed {
  color: #10b981;
}

.qrcode-container {
  background: #FFFFFF;
  border-radius: 14px;
  padding: 30px;
  text-align: center;
  margin-bottom: 20px;
  border: 1px solid rgba(0, 0, 0, 0.06);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.qrcode {
  display: block;
  margin: 0 auto 15px;
}

.qrcode-tip {
  color: #86868B;
  font-size: 14px;
  margin: 0;
}

.qrcode-hint {
  color: var(--apple-blue-hover);
  font-size: 13px;
  margin: 10px 0 0;
  font-weight: 500;
}

.button-container {
  margin-top: 20px;
}

.action-section {
  margin-top: 15px;
}

.tip-text {
  margin-top: 10px;
  color: #ef4444;
  font-size: 12px;
  text-align: center;
}

.scanner-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #fff;
}

.scanner-box {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.scanner-frame {
  position: relative;
  width: 250px;
  height: 250px;
  border: 2px dashed #ddd;
  border-radius: 12px;
  background: #f5f5f5;
}

.corner {
  position: absolute;
  width: 20px;
  height: 20px;
  border-color: #007AFF;
  border-style: solid;
}

.corner-tl {
  top: -2px;
  left: -2px;
  border-width: 3px 0 0 3px;
}

.corner-tr {
  top: -2px;
  right: -2px;
  border-width: 3px 3px 0 0;
}

.corner-bl {
  bottom: -2px;
  left: -2px;
  border-width: 0 0 3px 3px;
}

.corner-br {
  bottom: -2px;
  right: -2px;
  border-width: 0 3px 3px 0;
}

.scan-line {
  position: absolute;
  top: 50%;
  left: 10%;
  right: 10%;
  height: 2px;
  background: linear-gradient(90deg, transparent, #007AFF, transparent);
  animation: scan 2s ease-in-out infinite;
}

@keyframes scan {
  0%, 100% {
    top: 20%;
  }
  50% {
    top: 80%;
  }
}

.scanner-tip {
  margin-top: 20px;
  color: #666;
  font-size: 14px;
}

.scanner-hint {
  color: var(--apple-blue-hover);
  font-size: 13px;
  margin: 10px 0 0;
  font-weight: 500;
}

.manual-input {
  padding: 20px;
  background: #f7f8fa;
}
</style>