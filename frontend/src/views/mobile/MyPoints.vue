<template>
  <div class="points-page">
    <van-nav-bar title="我的积分" left-arrow @click-left="$router.back()" />
    
    <div class="content">
      <!-- 积分卡片 - Apple 蓝色渐变 -->
      <div class="points-card">
        <div class="card-bg"></div>
        <div class="card-inner">
          <div class="points-header">
            <div class="points-info">
              <div class="points-value">{{ userPoints.currentPoints || 0 }}</div>
              <div class="points-label">当前积分</div>
            </div>
            <div class="credit-badge" :class="getCreditLevelClass(userPoints.creditLevel)">
              {{ userPoints.creditLevel || '未评定' }}
            </div>
          </div>

          <div class="points-details">
            <div class="detail-item">
              <span class="label">有效积分</span>
              <span class="value">{{ userPoints.currentPoints || 0 }}</span>
            </div>
            <div class="detail-item">
              <span class="label">最大时长</span>
              <span class="value">{{ userPoints.maxReservationHours || 0 }}h</span>
            </div>
            <div class="detail-item">
              <span class="label">每日次数</span>
              <span class="value">{{ userPoints.maxReservationCount || 0 }}次</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 信用等级说明 -->
      <div class="section-card">
        <div class="section-title">信用等级说明</div>
        <div class="level-list">
          <div class="level-item">
            <div class="level-badge excellent">极好</div>
            <div class="level-desc">&ge;90分 &middot; 6小时 &middot; 4次/天</div>
          </div>
          <div class="level-item">
            <div class="level-badge good">良好</div>
            <div class="level-desc">75-89分 &middot; 4小时 &middot; 3次/天</div>
          </div>
          <div class="level-item">
            <div class="level-badge normal">一般</div>
            <div class="level-desc">60-74分 &middot; 2小时 &middot; 2次/天</div>
          </div>
          <div class="level-item">
            <div class="level-badge poor">较差</div>
            <div class="level-desc">40-59分 &middot; 1小时 &middot; 1次/天</div>
          </div>
          <div class="level-item">
            <div class="level-badge suspended">暂停服务</div>
            <div class="level-desc">&lt;40分 &middot; 禁止预约</div>
          </div>
        </div>
      </div>

      <!-- 积分规则 -->
      <div class="section-card">
        <div class="section-title">积分规则</div>
        <div class="rule-section">
          <div class="rule-header add"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20,6 9,17 4,12"/></svg> 加分项</div>
          <ul>
            <li>正常履约签到 +1分/次</li>
            <li>提前签退 +1分/次</li>
            <li><em>每日加分上限 +2分</em></li>
          </ul>
        </div>
        <div class="rule-divider"></div>
        <div class="rule-section">
          <div class="rule-header sub"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="5" y1="12" x2="19" y2="12"/></svg> 扣分项</div>
          <ul>
            <li>爽约（未签到） &minus;6分/次</li>
            <li>暂离超时 &minus;3分/次</li>
          </ul>
        </div>
        <div class="rule-divider"></div>
        <div class="rule-section rule-note">
          <p>近期行为权重高，历史违约可被良好记录覆盖</p>
        </div>
      </div>
      
      <!-- 积分记录 -->
      <div class="section-card records-card">
        <div class="section-title">积分变动记录</div>
        <van-list
          v-model="loading"
          :finished="finished"
          finished-text="没有更多了"
          @load="onLoad"
        >
          <div
            v-for="record in records"
            :key="record.id"
            class="record-item"
          >
            <div class="record-icon-wrap" :class="record.pointsChange > 0 ? 'add' : 'deduct'">
              <van-icon :name="record.pointsChange > 0 ? 'success' : 'fail'" />
            </div>
            <div class="record-body">
              <div class="record-top">
                <span class="record-type">{{ record.changeType }}</span>
                <span class="record-change" :class="record.pointsChange > 0 ? 'add' : 'deduct'">
                  {{ record.pointsChange > 0 ? '+' : '' }}{{ record.pointsChange }}
                </span>
              </div>
              <div class="record-desc">{{ record.description }}</div>
              <div class="record-time">{{ formatTime(record.createTime) }}</div>
            </div>
            <van-button
              v-if="record.pointsChange < 0 && record.changeType !== '申诉返还'"
              size="mini"
              round
              plain
              hairline
              type="warning"
              @click="showAppealDialog(record)"
            >
              申诉
            </van-button>
          </div>
        </van-list>
      </div>
    </div>

    <!-- 申诉对话框 -->
    <van-dialog v-model="showAppeal" title="发起申诉" show-cancel-button
      @confirm="submitAppeal" :before-close="onAppealBeforeClose">
      <div class="appeal-dialog-content">
        <div class="appeal-record-info" v-if="appealTarget">
          <van-tag type="danger" size="small">{{ appealTarget.changeType }}</van-tag>
          <span style="margin-left: 8px; color: #666; font-size: 13px;">
            扣分：{{ Math.abs(appealTarget.pointsChange) }}分
          </span>
          <div style="color: #999; font-size: 12px; margin-top: 4px;">
            {{ formatTime(appealTarget.createTime) }}
          </div>
        </div>
        <van-field
          v-model="appealReason"
          type="textarea"
          rows="3"
          maxlength="500"
          placeholder="请说明申诉理由..."
          show-word-limit
          autosize
        />
        <div class="appeal-uploader">
          <p class="uploader-label">上传证据照片（选填）：</p>
          <van-uploader
            v-model="appealImages"
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
    <van-tabbar v-model="bottomTab">
      <van-tabbar-item icon="home-o" to="/mobile/home">首页</van-tabbar-item>
      <van-tabbar-item icon="orders-o" to="/mobile/my-reservations">我的预约</van-tabbar-item>
      <van-tabbar-item icon="contact-o" to="/mobile/profile">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script>
import { getMyPoints, getPointsRecords } from '@/api/points'
import { formatDate } from '@/utils/time'
import { createAppeal } from '@/api/appeal'
import { uploadImage } from '@/api/complaint'

export default {
  name: 'MyPoints',
  data() {
    return {
      userPoints: {},
      records: [],
      bottomTab: 2,
      loading: false,
      finished: false,
      currentPage: 1,
      pageSize: 10,

      showAppeal: false,
      appealTarget: null,
      appealReason: '',
      appealImages: [],
      uploadingImage: false,
      uploadedImageUrl: ''
    }
  },
  
  async created() {
    await this.loadUserPoints()
  },
  
  methods: {
    showAppealDialog(record) {
      this.appealTarget = record
      this.appealReason = ''
      this.appealImages = []
      this.uploadedImageUrl = ''
      this.showAppeal = true
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
        this.appealImages = []
      } finally {
        this.uploadingImage = false
      }
    },

    async submitAppeal() {
      if (!this.appealReason.trim()) {
        this.$toast.fail('请填写申诉理由')
        return Promise.reject()
      }
      try {
        await createAppeal({
          pointsRecordId: this.appealTarget.id,
          reason: this.appealReason.trim(),
          imageUrl: this.uploadedImageUrl || ''
        })
        this.$toast.success('申诉提交成功，请等待管理员审核')
        this.showAppeal = false
        this.appealTarget = null
        this.appealReason = ''
        this.appealImages = []
        this.uploadedImageUrl = ''
      } catch (error) {
        this.$toast.fail(error.message || '提交失败')
        return Promise.reject()
      }
    },
    
    onAppealBeforeClose(action, done) {
      if (action === 'confirm') {
        this.submitAppeal().then(() => done()).catch(() => done())
      } else {
        done()
      }
    },
    
    async loadUserPoints() {
      try {
        const res = await getMyPoints()
        this.userPoints = res.data
      } catch (error) {
        this.$toast.fail('加载积分信息失败')
      }
    },
    
    async onLoad() {
      try {
        const res = await getPointsRecords({
          current: this.currentPage,
          size: this.pageSize
        })

        if (this.currentPage === 1) {
          this.records = res.data.records
        } else {
          this.records = [...this.records, ...res.data.records]
        }

        this.loading = false

        if (this.records.length >= res.data.total) {
          this.finished = true
        } else {
          this.currentPage++
        }
      } catch (error) {
        this.loading = false
        this.finished = true
        this.$toast.fail('加载记录失败')
      }
    },
    
    formatTime(time) {
      return formatDate(new Date(time))
    },
    
    getCreditLevelClass(level) {
      const classMap = { '极好': 'excellent', '良好': 'good', '一般': 'normal', '较差': 'poor', '暂停服务': 'suspended' }
      return classMap[level] || ''
    },
    
    getRecordIcon(changeType) {
      if (changeType.includes('履约') || changeType.includes('签退')) return 'success'
      return 'fail'
    }
  }
}
</script>

<style scoped>
.points-page {
  min-height: 100vh;
  background: #f2f2f7;
  padding-bottom: 70px;
}

.content {
  padding: 14px;
}

/* ====== 积分卡片 - Apple Blue 渐变 ====== */
.points-card {
  position: relative;
  border-radius: 16px;
  overflow: hidden;
  margin-bottom: 18px;
  box-shadow: 0 4px 20px rgba(0, 122, 255, 0.15);
}

.card-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #007AFF 0%, #5ac8fa 100%);
}

.card-inner {
  position: relative;
  z-index: 1;
  padding: 22px 18px 18px;
  color: white;
}

.points-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18px;
}

.points-info {
  text-align: left;
}

.points-value {
  font-size: 44px;
  font-weight: 700;
  line-height: 1;
  letter-spacing: -1.5px;
}

.points-label {
  font-size: 13px;
  opacity: 0.85;
  margin-top: 6px;
  font-weight: 400;
}

.credit-badge {
  padding: 6px 14px;
  border-radius: 16px;
  font-size: 13px;
  font-weight: 600;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(8px);
}

/* ====== 详情行 ====== */
.points-details {
  display: flex;
  justify-content: space-around;
  padding-top: 14px;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
}

.detail-item {
  text-align: center;
}

.detail-item .label {
  display: block;
  font-size: 11px;
  opacity: 0.75;
  margin-bottom: 3px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.detail-item .value {
  display: block;
  font-size: 17px;
  font-weight: 600;
}

/* ====== 统一 Section 卡片 ====== */
.section-card {
  background: white;
  border-radius: 14px;
  padding: 16px;
  margin-bottom: 14px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0 0 14px 0;
  letter-spacing: -0.3px;
}

/* ====== 等级列表 ====== */
.level-list {
  /* empty container for level items */
}

.level-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 11px 0;
  border-bottom: 0.5px solid #f2f2f7;
}

.level-item:last-child {
  border-bottom: none;
}

.level-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.level-badge.excellent {
  background: rgba(52, 199, 89, 0.12);
  color: #248A3D;
}

.level-badge.good {
  background: rgba(0, 122, 255, 0.1);
  color: #007AFF;
}

.level-badge.normal {
  background: rgba(255, 149, 0, 0.12);
  color: #C77700;
}

.level-badge.poor {
  background: rgba(255, 59, 48, 0.1);
  color: #FF3B30;
}

.level-badge.suspended {
  background: rgba(142, 142, 147, 0.12);
  color: #636366;
}

.level-desc {
  font-size: 13px;
  color: #8e8e93;
}

/* ====== 规则区域 ====== */
.rule-section ul {
  margin: 6px 0 0 0;
  padding-left: 18px;
}

.rule-section li {
  margin: 5px 0;
  font-size: 13px;
  color: #3c3c43;
  line-height: 1.6;
}

.rule-section li em {
  color: #8e8e93;
  font-style: normal;
  font-size: 12px;
}

.rule-header {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 6px;
}

.rule-header svg {
  width: 16px;
  height: 16px;
}

.rule-header.add { color: #34C759; }
.rule-header.sub { color: #FF3B30; }

.rule-divider {
  height: 0.5px;
  background: #e5e5ea;
  margin: 12px 0;
}

.rule-note p {
  margin: 0;
  font-size: 13px;
  color: #8e8e93;
  line-height: 1.6;
}

/* ====== 记录列表 ====== */
.record-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 0.5px solid #f2f2f7;
}

.record-item:last-child {
  border-bottom: none;
}

.record-icon-wrap {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  margin-top: 2px;
}

.record-icon-wrap.add {
  background: rgba(52, 199, 89, 0.1);
  color: #34C759;
}

.record-icon-wrap.deduct {
  background: rgba(255, 59, 48, 0.08);
  color: #FF3B30;
}

.record-body {
  flex: 1;
  min-width: 0;
}

.record-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2px;
}

.record-type {
  font-size: 14px;
  font-weight: 500;
  color: #1d1d1f;
}

.record-change {
  font-size: 15px;
  font-weight: 700;
  font-family: 'SF Mono', monospace;
}

.record-change.add { color: #34C759; }
.record-change.deduct { color: #FF3B30; }

.record-desc {
  font-size: 12px;
  color: #8e8e93;
  margin-top: 2px;
  line-height: 1.4;
}

.record-time {
  font-size: 11px;
  color: #c7c7cc;
  margin-top: 3px;
}

/* ====== 申诉对话框 ====== */
.appeal-dialog-content {
  padding: 16px;
}

.appeal-record-info {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  padding: 10px 12px;
  background: #fef2f2;
  border-radius: 10px;
  margin-bottom: 12px;
}

.appeal-uploader {
  margin-top: 12px;
}

.uploader-label {
  font-size: 13px;
  color: #8e8e93;
  margin-bottom: 8px;
}
</style>
