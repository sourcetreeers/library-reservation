<template>
  <div class="reservation-rules">
    <div class="page-hero">
      <div>
        <span class="page-kicker">Rules</span>
        <h2>预约规则配置</h2>
        <p>管理预约时段、暂离超时等全局规则参数。</p>
      </div>
      <el-button type="primary" @click="handleRefresh" :loading="loading">
        <i class="el-icon-refresh"></i> 刷新
      </el-button>
    </div>

    <div class="content-card rules-card" v-loading="loading">
      <!-- ====== 全局规则 ====== -->
      <div class="section">
        <div class="section-title">
          <i class="el-icon-s-operation"></i> 全局规则
          <span class="section-desc">对所有用户统一生效</span>
        </div>
        <div class="global-rules-grid">
          <div
            v-for="rule in globalRules"
            :key="rule.id"
            class="global-rule-item"
          >
            <div class="global-rule-left">
              <div class="global-rule-name">{{ rule.ruleName }}</div>
              <div class="global-rule-desc">{{ rule.description }}</div>
            </div>
            <div class="global-rule-right">
              <el-tag class="global-rule-value">{{ rule.ruleValue }}</el-tag>
              <el-button size="mini" type="primary" plain @click="handleEdit(rule)">
                <i class="el-icon-edit"></i>
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑对话框 -->
    <el-dialog
      title="编辑规则"
      :visible.sync="dialogVisible"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="editForm" label-width="110px" v-if="editForm">
        <el-form-item label="规则名称">
          <el-input v-model="editForm.ruleName" disabled />
        </el-form-item>
        <el-form-item label="当前值">
          <el-input
            v-model="editForm.ruleValue"
            :placeholder="getPlaceholder(editForm.ruleKey)"
            :type="getInputType(editForm.ruleKey) === 'number' ? 'text' : 'text'"
          />
          <div class="rule-tip">{{ getRuleTip(editForm.ruleKey) }}</div>
        </el-form-item>
        <el-form-item label="说明">
          <el-input v-model="editForm.description" disabled type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getRules, updateRule } from '@/api/systemAdmin'

export default {
  name: 'ReservationRules',
  data() {
    return {
      loading: false,
      saving: false,
      allRules: [],
      dialogVisible: false,
      editForm: null
    }
  },
  computed: {
    globalRules() {
      const keys = ['reservation_time_slots', 'temp_leave_timeout']
      return this.allRules.filter(r => keys.includes(r.ruleKey))
    }
  },
  created() {
    this.loadRules()
  },
  methods: {
    async loadRules() {
      this.loading = true
      try {
        const res = await getRules()
        const excludeKeys = ['max_daily_reservations', 'max_reservation_hours']
        this.allRules = (res.data || []).filter(item => !excludeKeys.includes(item.ruleKey))
      } catch (error) {
        this.$message.error('加载规则列表失败: ' + (error.message || ''))
      } finally {
        this.loading = false
      }
    },
    handleRefresh() {
      this.loadRules()
    },
    handleEdit(row) {
      this.editForm = { ...row }
      this.dialogVisible = true
    },
    async handleSave() {
      if (!this.editForm.ruleValue || this.editForm.ruleValue.trim() === '') {
        this.$message.warning('规则值不能为空')
        return
      }
      this.saving = true
      try {
        await updateRule(this.editForm.ruleKey, this.editForm.ruleValue.trim())
        this.$message.success('更新成功')
        this.dialogVisible = false
        this.loadRules()
      } catch (error) {
        this.$message.error('更新失败: ' + (error.message || ''))
      } finally {
        this.saving = false
      }
    },
    getPlaceholder(ruleKey) {
      const map = {
        'temp_leave_timeout': '例如: 20',
        'reservation_time_slots': '例如: 08:00-12:00,14:00-22:00',
        'excellent_max_hours': '例如: 6',
        'excellent_max_count': '例如: 4',
        'good_max_hours': '例如: 4',
        'good_max_count': '例如: 3',
        'average_max_hours': '例如: 2',
        'average_max_count': '例如: 2',
        'poor_max_hours': '例如: 1',
        'poor_max_count': '例如: 1',
        'suspended_max_hours': '例如: 0',
        'suspended_max_count': '例如: 0'
      }
      return map[ruleKey] || '请输入规则值'
    },
    getInputType(ruleKey) {
      const numberKeys = [
        'temp_leave_timeout',
        'excellent_max_hours', 'excellent_max_count',
        'good_max_hours', 'good_max_count',
        'average_max_hours', 'average_max_count',
        'poor_max_hours', 'poor_max_count',
        'suspended_max_hours', 'suspended_max_count'
      ]
      if (numberKeys.includes(ruleKey)) return 'number'
      return 'text'
    },
    getRuleTip(ruleKey) {
      const tips = {
        'temp_leave_timeout': '单位：分钟。暂离超过此时间自动签退并扣分',
        'reservation_time_slots': '多个时间段用英文逗号分隔，格式：HH:mm-HH:mm',
        'excellent_max_hours': '极好信用等级用户最大预约时长（小时）',
        'excellent_max_count': '极好信用等级用户每日最大预约次数',
        'good_max_hours': '良好信用等级用户最大预约时长（小时）',
        'good_max_count': '良好信用等级用户每日最大预约次数',
        'average_max_hours': '一般信用等级用户最大预约时长（小时）',
        'average_max_count': '一般信用等级用户每日最大预约次数',
        'poor_max_hours': '较差信用等级用户最大预约时长（小时）',
        'poor_max_count': '较差信用等级用户每日最大预约次数',
        'suspended_max_hours': '暂停服务等级用户最大预约时长（小时）',
        'suspended_max_count': '暂停服务等级用户每日最大预约次数'
      }
      return tips[ruleKey] || ''
    }
  }
}
</script>

<style scoped>
.reservation-rules {
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
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.rules-card {
  padding: 24px 28px;
}

/* ===== 分区标题 ===== */
.section {
  margin-bottom: 32px;
}

.section:last-child {
  margin-bottom: 0;
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #1D1D1F;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-title i {
  font-size: 18px;
  color: #007AFF;
}

.section-desc {
  font-size: 12px;
  font-weight: 400;
  color: #86868B;
  margin-left: 4px;
}

/* ===== 全局规则 ===== */
.global-rules-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.global-rule-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  background: #F5F5F7;
  border-radius: 10px;
  border: 1px solid rgba(0, 0, 0, 0.04);
  transition: all 0.2s;
}

.global-rule-item:hover {
  background: #EDEDF0;
  border-color: rgba(0, 122, 255, 0.2);
}

.global-rule-left {
  flex: 1;
}

.global-rule-name {
  font-size: 14px;
  font-weight: 600;
  color: #1D1D1F;
  margin-bottom: 4px;
}

.global-rule-desc {
  font-size: 12px;
  color: #86868B;
}

.global-rule-right {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.global-rule-value {
  font-size: 14px;
  font-weight: 600;
  padding: 0 16px;
  height: 32px;
  line-height: 32px;
}

/* ===== 编辑对话框 ===== */
.rule-tip {
  margin-top: 4px;
  font-size: 12px;
  color: #86868B;
  line-height: 1.4;
}
</style>
