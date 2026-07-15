<template>
  <div class="feedback-page">
    <van-nav-bar title="意见反馈" left-arrow @click-left="$router.back()" />

    <div class="content">
      <!-- 提交反馈 -->
      <van-form @submit="submitFeedback" class="submit-section">
        <div class="section-title">提交反馈</div>

        <div class="form-card">
          <van-field
            v-model="form.type"
            label="类型"
            placeholder="请选择"
            is-link
            readonly
            :rules="[{ required: true, message: '请选择类型' }]"
            @click="showTypePicker = true"
          />
          <van-popup v-model="showTypePicker" position="bottom" round>
            <van-picker
              :columns="typeOptions"
              show-toolbar
              @confirm="onTypeConfirm"
              @cancel="showTypePicker = false"
            />
          </van-popup>

          <van-field
            v-model="form.title"
            label="标题"
            placeholder="请输入反馈标题（选填）"
          />

          <van-field
            v-model="form.content"
            rows="4"
            autosize
            type="textarea"
            maxlength="500"
            show-word-limit
            placeholder="请详细描述您的建议或问题..."
            :rules="[{ required: true, message: '请输入反馈内容' }]"
          />
        </div>

        <van-button round block type="info" native-type="submit" class="submit-btn">
          提交反馈
        </van-button>
      </van-form>

      <!-- 我的反馈列表 -->
      <div class="list-section">
        <div class="section-title">我的反馈记录</div>
        <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
          <van-list
            v-model="loading"
            :finished="finished"
            finished-text="没有更多了"
            @load="loadData"
          >
            <div v-if="list.length > 0" class="feedback-list">
              <div
                v-for="item in list"
                :key="item.id"
                class="feedback-item"
                @click="showDetail(item)"
              >
                <div class="item-header">
                  <span class="item-type" :class="getTypeClass(item.type)">{{ item.type }}</span>
                  <span class="item-status" :class="getStatusClass(item.status)">{{ item.status }}</span>
                </div>
                <div class="item-title">{{ item.title || '无标题' }}</div>
                <div class="item-content">{{ item.content }}</div>
                <div class="item-footer">
                  <span>{{ formatDate(item.createTime) }}</span>
                  <span v-if="item.replyTime" class="replied">已回复</span>
                </div>

                <!-- 回复内容 -->
                <div v-if="item.reply" class="reply-box">
                  <div class="reply-label">管理员回复</div>
                  <div class="reply-text">{{ item.reply }}</div>
                </div>
              </div>
            </div>
            <van-empty v-else description="暂无反馈记录" image-size="100" />
          </van-list>
        </van-pull-refresh>
      </div>
    </div>

    <!-- 底部导航 -->
    <van-tabbar v-model="activeTab">
      <van-tabbar-item icon="home-o" to="/mobile/home">首页</van-tabbar-item>
      <van-tabbar-item icon="orders-o" to="/mobile/my-reservations">我的预约</van-tabbar-item>
      <van-tabbar-item icon="contact-o" to="/mobile/profile">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script>
import { submitFeedback as apiSubmit, getMyFeedbacks } from '@/api/feedback'

export default {
  name: 'MyFeedbacks',
  data() {
    return {
      activeTab: 2,
      form: { type: '', title: '', content: '' },
      showTypePicker: false,
      typeOptions: ['功能建议', '问题反馈', '投诉举报', '其他'],
      list: [],
      loading: false,
      refreshing: false,
      finished: false,
      pagination: { current: 1, size: 10 }
    }
  },
  methods: {
    onTypeConfirm(value) {
      this.form.type = value
      this.showTypePicker = false
    },
    async submitFeedback() {
      try {
        await apiSubmit(this.form)
        this.$toast.success('提交成功')
        this.form = { type: '', title: '', content: '' }
        this.onRefresh()
      } catch (e) {
        this.$toast.fail(e.message || '提交失败')
      }
    },
    async loadData() {
      try {
        const res = await getMyFeedbacks({
          current: this.pagination.current,
          size: this.pagination.size
        })
        const records = (res.data && res.data.records) || []
        if (this.pagination.current === 1) this.list = records
        else this.list.push(...records)

        if (!records.length || records.length < this.pagination.size) {
          this.finished = true
        } else {
          this.pagination.current++
        }
      } catch (e) {
        console.error(e)
      } finally {
        this.loading = false
        this.refreshing = false
      }
    },
    onRefresh() {
      this.list = []
      this.finished = false
      this.pagination.current = 1
      this.loadData()
    },
    getTypeClass(type) {
      const map = { '功能建议': 'type-suggest', '问题反馈': 'type-bug', '投诉举报': 'type-report', '其他': 'type-other' }
      return map[type] || ''
    },
    getStatusClass(status) {
      const map = { '待处理': 'status-pending', '已采纳': 'status-accepted', '已处理': 'status-done' }
      return map[status] || ''
    },
    formatDate(dateStr) {
      if (!dateStr) return ''
      const d = new Date(dateStr)
      return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')} ${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}`
    },
    showDetail(item) {
      if (item.reply) return
      this.$toast('管理员尚未回复')
    }
  }
}
</script>

<style scoped>
.feedback-page {
  min-height: 100vh;
  background: #f2f2f7;
  padding-bottom: 70px;
}

.content {
  padding: 14px;
}

/* ====== 提交区域 ====== */
.submit-section {
  /* container only */
}

.section-title {
  font-size: 17px;
  font-weight: 700;
  color: #1d1d1f;
  margin-bottom: 12px;
  letter-spacing: -0.3px;
}

.form-card {
  background: white;
  border-radius: 14px;
  padding: 4px 16px;
  margin-bottom: 16px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

::v-deep .van-cell {
  padding: 14px 0;
  border-bottom: 0.5px solid #f2f2f7;

  &::after { display: none; }
}

::v-deep .van-field__label {
  color: #3c3c43;
  font-weight: 500;
  width: auto;
  min-width: 48px;
  font-size: 15px;
}

::v-deep .van-field__control {
  color: #1d1d1f;
  font-size: 15px;
}

.submit-btn {
  height: 50px;
  font-size: 17px;
  font-weight: 600;
  background: linear-gradient(180deg, #007AFF 0%, #0066D6 100%) !important;
  border: none !important;
  box-shadow: 0 4px 14px rgba(0, 122, 255, 0.35);
  letter-spacing: 0.5px;
}

/* ====== 列表区域 ====== */
.list-section {
  margin-top: 24px;
}

.feedback-list {
  background: white;
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.feedback-item {
  padding: 16px 16px 14px;
  border-bottom: 0.5px solid #f2f2f7;
  transition: background 0.15s;
}

.feedback-item:last-child {
  border-bottom: none;
}

.feedback-item:active {
  background: #f9f9fb;
}

.item-header {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}

.item-type {
  font-size: 12px;
  font-weight: 600;
  padding: 2px 10px;
  border-radius: 8px;
  letter-spacing: 0.3px;
}

.type-suggest { background: rgba(0,122,255,0.08); color: #007AFF; }
.type-bug { background: rgba(255,149,0,0.1); color: #C77700; }
.type-report { background: rgba(255,59,48,0.08); color: #FF3B30; }
.type-other { background: rgba(142,142,147,0.12); color: #636366; }

.item-status {
  font-size: 12px;
  font-weight: 600;
  padding: 2px 10px;
  border-radius: 8px;
}

.status-pending { background: rgba(255,149,0,0.1); color: #C77700; }
.status-accepted { background: rgba(52,199,89,0.1); color: #248A3D; }
.status-done { background: rgba(142,142,147,0.1); color: #636366; }

.item-title {
  font-size: 15px;
  font-weight: 600;
  color: #1d1d1f;
  margin-bottom: 6px;
}

.item-content {
  font-size: 13px;
  color: #8e8e93;
  line-height: 1.5;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.item-footer {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #c7c7cc;
}

.replied {
  color: #34C759;
  font-weight: 500;
}

/* ====== 回复框 ====== */
.reply-box {
  margin-top: 10px;
  padding: 12px 14px;
  background: linear-gradient(135deg, #f0fdf4, #ecfdf5);
  border-radius: 10px;
  border-left: 3px solid #34C759;
}

.reply-label {
  font-size: 12px;
  color: #248A3D;
  font-weight: 600;
  margin-bottom: 4px;
}

.reply-text {
  font-size: 13px;
  color: #3c3c43;
  line-height: 1.6;
}
</style>
