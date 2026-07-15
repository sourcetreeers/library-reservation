<template>
  <div class="qr-scanner">
    <div class="page-hero">
      <div>
        <span class="page-kicker">Scanner</span>
        <h2>扫码验证</h2>
        <p>扫描学生二维码或手动输入流水号完成签到验证。</p>
      </div>
    </div>
    
    <div class="content-card scanner-container">
      <div class="scanner-area">
        <div class="scanner-box">
          <div class="scanner-line"></div>
          <div class="corner top-left"></div>
          <div class="corner top-right"></div>
          <div class="corner bottom-left"></div>
          <div class="corner bottom-right"></div>
        </div>
        <p class="scanner-tip">请将二维码放入扫描框内</p>
        <p class="scanner-hint">💡 学生可提前20分钟签到</p>
      </div>
      
      <div class="manual-input">
        <h3>手动输入流水号</h3>
        <el-form :inline="true" @submit.native.prevent="handleManualCheck">
          <el-form-item>
            <el-input
              v-model="orderNo"
              placeholder="请输入预约流水号"
              style="width: 300px"
              @keyup.enter.native="handleManualCheck"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleManualCheck" :loading="loading">
              验证
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
    
    <!-- 验证结果 -->
    <div v-if="verificationResult" class="content-card result-container">
      <div class="result-content">
        <div class="result-status" :class="resultStatusClass">
          <i :class="resultIcon"></i>
          <span>{{ resultMessage }}</span>
        </div>
        
        <div v-if="reservationInfo" class="reservation-details">
          <div class="detail-item">
            <span class="label">流水号</span>
            <span class="value">{{ reservationInfo.orderNo }}</span>
          </div>
          <div class="detail-item">
            <span class="label">用户</span>
            <span class="value">{{ reservationInfo.realName }}</span>
          </div>
          <div class="detail-item">
            <span class="label">图书室</span>
            <span class="value">{{ reservationInfo.libraryName }}</span>
          </div>
          <div class="detail-item">
            <span class="label">座位</span>
            <span class="value">{{ reservationInfo.seatNumber }}</span>
          </div>
          <div class="detail-item">
            <span class="label">预约时间</span>
            <span class="value">{{ formatTimeRange(reservationInfo.startTime, reservationInfo.endTime) }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { checkIn, getReservationDetailByOrderNo } from '@/api/reservation'
import { formatTimeRange as formatTimeRangeUtil } from '@/utils/time'

export default {
  name: 'QRScanner',
  data() {
    return {
      orderNo: '',
      loading: false,
      verificationResult: null,
      reservationInfo: null,
      resultMessage: '',
      resultStatusClass: '',
      resultIcon: ''
    }
  },
  
  methods: {
    async handleManualCheck() {
      if (!this.orderNo.trim()) {
        this.$message.warning('请输入流水号')
        return
      }
      
      this.loading = true
      try {
        // 先获取预约信息
        const reservationRes = await getReservationDetailByOrderNo(this.orderNo)
        this.reservationInfo = reservationRes.data
        
        // 执行签到
        await checkIn(this.orderNo)
        
        this.showSuccessResult('签到成功')
        
        // 清空输入框
        this.orderNo = ''
      } catch (error) {
        this.showErrorResult(error.message || '验证失败')
      } finally {
        this.loading = false
      }
    },
    
    showSuccessResult(message) {
      this.verificationResult = true
      this.resultMessage = message
      this.resultStatusClass = 'success'
      this.resultIcon = 'el-icon-success'
    },
    
    showErrorResult(message) {
      this.verificationResult = true
      this.resultMessage = message
      this.resultStatusClass = 'error'
      this.resultIcon = 'el-icon-error'
      this.reservationInfo = null
    },
    
    formatTimeRange(startTime, endTime) {
      if (!startTime || !endTime) return ''
      return formatTimeRangeUtil(startTime, endTime)
    }
  }
}
</script>

<style scoped>
.qr-scanner {
  display: grid;
  gap: 18px;
}

.page-hero,
.content-card {
  background: #FFFFFF;
  border-radius: 14px;
  border: 1px solid rgba(0, 0, 0, 0.06);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.page-hero {
  padding: 24px 28px;
}

.page-kicker {
  display: inline-block;
  margin-bottom: 10px;
  color: #007AFF;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.8px;
  text-transform: uppercase;
}

.page-hero h2 {
  margin: 0 0 8px;
  color: #1D1D1F;
  font-size: 30px;
}

.page-hero p {
  color: #86868B;
  font-size: 14px;
}

.scanner-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 28px;
}

.scanner-area {
  text-align: center;
  margin-bottom: 32px;
}

.scanner-box {
  position: relative;
  width: 280px;
  height: 280px;
  margin: 0 auto 20px;
  background: #F5F5F7;
  border: 2px dashed rgba(0, 0, 0, 0.1);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.scanner-line {
  width: 180px;
  height: 2px;
  background: #007AFF;
  animation: scan 2s linear infinite;
}

@keyframes scan {
  0% {
    transform: translateY(-100px);
    opacity: 0;
  }
  50% {
    opacity: 1;
  }
  100% {
    transform: translateY(100px);
    opacity: 0;
  }
}

.corner {
  position: absolute;
  width: 24px;
  height: 24px;
  border: 3px solid #007AFF;
}

.corner.top-left {
  top: 12px;
  left: 12px;
  border-right: none;
  border-bottom: none;
  border-radius: 4px 0 0 0;
}

.corner.top-right {
  top: 12px;
  right: 12px;
  border-left: none;
  border-bottom: none;
  border-radius: 0 4px 0 0;
}

.corner.bottom-left {
  bottom: 12px;
  left: 12px;
  border-right: none;
  border-top: none;
  border-radius: 0 0 0 4px;
}

.corner.bottom-right {
  bottom: 12px;
  right: 12px;
  border-left: none;
  border-top: none;
  border-radius: 0 0 4px 0;
}

.scanner-tip {
  color: #86868B;
  font-size: 14px;
  margin: 0;
}

.scanner-hint {
  color: #FF9500;
  font-size: 13px;
  margin: 10px 0 0;
  font-weight: 500;
}

.manual-input {
  text-align: center;
  padding: 24px;
  background: #F5F5F7;
  border-radius: 10px;
}

.manual-input h3 {
  margin: 0 0 20px 0;
  color: #1D1D1F;
  font-size: 16px;
  font-weight: 600;
}

.result-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 28px;
}

.result-content {
  text-align: center;
}

.result-status {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 20px;
  padding: 16px;
  border-radius: 10px;
}

.result-status.success {
  background: rgba(52, 199, 89, 0.1);
  color: #34C759;
}

.result-status.error {
  background: rgba(255, 59, 48, 0.1);
  color: #FF3B30;
}

.result-status i {
  margin-right: 8px;
  font-size: 20px;
}

.reservation-details {
  text-align: left;
  background: #F5F5F7;
  padding: 20px;
  border-radius: 10px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  padding: 8px 0;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.detail-item:last-child {
  border-bottom: none;
  margin-bottom: 0;
}

.label {
  color: #86868B;
  font-weight: 500;
  font-size: 14px;
}

.value {
  color: #1D1D1F;
  font-weight: 500;
}
</style>