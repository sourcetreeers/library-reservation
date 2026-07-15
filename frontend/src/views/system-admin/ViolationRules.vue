<template>
  <div class="violation-rules">
    <div class="page-hero">
      <div>
        <span class="page-kicker">Violations</span>
        <h2>违规规则配置</h2>
        <p>配置不同违规类型的处罚规则，支持按累计次数分级处罚。</p>
      </div>
      <el-button type="primary" icon="el-icon-plus" @click="openAddDialog">新增规则</el-button>
    </div>

    <!-- 规则表格 -->
    <div class="content-card data-card">
      <el-table :data="tableData" v-loading="loading" stripe border style="width:100%" row-key="id">
        <el-table-column prop="id" label="#" width="55" align="center" />
        <el-table-column prop="violationType" label="违规类型" width="120" align="center">
          <template slot-scope="{row}">
            <el-tag :type="violationTagMap[row.violationType]" effect="plain" size="small">{{ row.violationType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applyTimes" label="适用次数" width="120" align="center">
          <template slot-scope="{row}">
            <span>{{ applyTimesLabel(row.applyTimes) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="pointsDeduct" label="扣分" width="70" align="center">
          <template slot-scope="{row}">
            <span :class="{'text-danger': row.pointsDeduct < 0}">{{ row.pointsDeduct }}分</span>
          </template>
        </el-table-column>
        <el-table-column prop="banDays" label="封禁天数" width="95" align="center">
          <template slot-scope="{row}">
            <span :class="{'text-warning': row.banDays > 0}">{{ row.banDays > 0 ? row.banDays + '天' : '不封禁' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="isActive" label="状态" width="75" align="center">
          <template slot-scope="{row}">
            <el-switch
              v-model="row.isActive"
              active-color="#007AFF"
              inactive-color="#dcdfe6"
              @change="toggleActive(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="140" show-overflow-tooltip />
        <el-table-column label="操作" width="130" align="center" fixed="right">
          <template slot-scope="{row}">
            <el-button type="text" size="small" @click="openEditDialog(row)" icon="el-icon-edit">编辑</el-button>
            <el-button type="text" size="small" style="color:#FF3B30" @click="deleteRule(row)" icon="el-icon-delete">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px" size="medium">
        <el-form-item label="违规类型" prop="violationType">
          <el-select v-model="form.violationType" placeholder="请选择" style="width:100%">
            <el-option label="爽约" value="爽约" />
            <el-option label="暂离超时" value="暂离超时" />
            <el-option label="恶意占座" value="恶意占座" />
          </el-select>
        </el-form-item>
        <el-form-item label="适用次数" prop="applyTimes">
          <el-select v-model="form.applyTimes" placeholder="请选择" style="width:100%">
            <el-option label="第1次" value="FIRST" />
            <el-option label="第2次" value="SECOND" />
            <el-option label="第3次及以后" value="THIRD_PLUS" />
          </el-select>
        </el-form-item>
        <el-form-item label="扣分值" prop="pointsDeduct">
          <el-input-number v-model="form.pointsDeduct" :min="-50" :max="0" step="5" style="width:100%"></el-input-number>
          <div class="form-tip">负数表示扣分，如 -10 表示扣除10分</div>
        </el-form-item>
        <el-form-item label="封禁天数" prop="banDays">
          <el-input-number v-model="form.banDays" :min="0" :max="30" style="width:100%"></el-input-number>
          <div class="form-tip">0表示仅扣分不封禁</div>
        </el-form-item>
        <el-form-item label="是否启用">
          <el-switch v-model="form.isActive" active-color="#007AFF"></el-switch>
        </el-form-item>
        <el-form-item label="规则描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="可选，简要说明此规则" maxlength="100" show-word-limit></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getPunishmentRulePage, addPunishmentRule, updatePunishmentRule, deletePunishmentRule, togglePunishmentRuleActive } from '@/api/punishment-rule'

export default {
  name: 'ViolationRules',
  data() {
    return {
      loading: false,
      tableData: [],
      dialogVisible: false,
      isEdit: false,
      form: {
        id: null, violationType: '', applyTimes: '', pointsDeduct: -10, banDays: 0,
        isActive: true, description: ''
      },
      formRules: {
        violationType: [{ required: true, message: '请选择违规类型', trigger: 'change' }],
        applyTimes: [{ required: true, message: '请选择适用次数', trigger: 'change' }],
        pointsDeduct: [{ required: true, message: '请填写扣分值', trigger: 'blur' }]
      },
      violationTagMap: { '爽约': 'danger', '暂离超时': 'warning', '恶意占座': 'danger' }
    }
  },
  computed: {
    dialogTitle() { return this.isEdit ? '编辑违规规则' : '新增违规规则' }
  },
  created() {
    this.fetchData()
  },
  methods: {
    async fetchData() {
      this.loading = true
      try {
        const res = await getPunishmentRulePage({ current: 1, size: 100 })
        if (res.code === 200) {
          this.tableData = res.data.records || []
        }
      } finally {
        this.loading = false
      }
    },
    openAddDialog() {
      this.isEdit = false
      this.form = { id: null, violationType: '', applyTimes: '', pointsDeduct: -10, banDays: 0, isActive: true, description: '' }
      this.dialogVisible = true
      this.$nextTick(() => this.$refs.formRef && this.$refs.formRef.clearValidate())
    },
    openEditDialog(row) {
      this.isEdit = true
      this.form = { ...row }
      this.dialogVisible = true
      this.$nextTick(() => this.$refs.formRef && this.$refs.formRef.clearValidate())
    },
    async submitForm() {
      this.$refs.formRef.validate(async valid => {
        if (!valid) return
        try {
          let res
          if (this.isEdit) {
            res = await updatePunishmentRule(this.form)
          } else {
            res = await addPunishmentRule(this.form)
          }
          if (res.code === 200) {
            this.$message.success(this.isEdit ? '更新成功' : '新增成功')
            this.dialogVisible = false
            this.fetchData()
          } else {
            this.$message.error(res.message || '操作失败')
          }
        } catch (e) {
          this.$message.error(e.message || '操作失败，请稍后重试')
        }
      })
    },
    async deleteRule(row) {
      try {
        await this.$confirm('确认删除该规则？删除后不可恢复', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
        const res = await deletePunishmentRule(row.id)
        if (res.code === 200) {
          this.$message.success('删除成功')
          this.fetchData()
        }
      } catch (e) {
        if (e !== 'cancel') {
          this.$message.error(e.message || '删除失败，请稍后重试')
        }
      }
    },
    async toggleActive(row) {
      try {
        const res = await togglePunishmentRuleActive(row.id)
        if (res.code !== 200) {
          this.$message.error(res.message || '切换失败')
          row.isActive = !row.isActive // 回滚
        }
      } catch (e) {
        this.$message.error(e.message || '切换失败，请稍后重试')
        row.isActive = !row.isActive // 回滚
      }
    },
    applyTimesLabel(val) {
      const map = { FIRST: '第1次', SECOND: '第2次', THIRD_PLUS: '第3次及以后' }
      return map[val] || val
    }
  }
}
</script>

<style scoped>
.violation-rules {
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
  color: #FF3B30;
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

.data-card {
  padding: 16px 16px 20px;
}

.text-danger {
  color: #FF3B30;
  font-weight: bold;
}

.text-warning {
  color: #FF9500;
  font-weight: bold;
}

.form-tip {
  color: #86868B;
  font-size: 11px;
  line-height: 1.4;
  margin-top: 2px;
}
</style>
