<template>
  <div class="violation-management">
    <div class="page-hero">
      <div class="hero-left">
        <span class="page-kicker">Violations</span>
        <h2>违规管理</h2>
        <p>系统自动记录学生违规行为（爽约 / 暂离超时 / 恶意占座），扣分与封禁即时生效。</p>
      </div>
      <div class="hero-tip">
        <i class="el-icon-info"></i>
        <span>如需撤销违规记录，请引导学生通过「申诉流程」提交申请</span>
      </div>
    </div>

    <!-- 筛选栏 -->
    <div class="content-card search-area">
      <el-form :inline="true" :model="query" size="small">
        <el-form-item label="用户/学号">
          <el-input v-model="query.keyword" placeholder="姓名或学号" clearable prefix-icon="el-icon-search" style="width:170px" />
        </el-form-item>
        <el-form-item label="违规类型">
          <el-select v-model="query.violationType" placeholder="全部类型" clearable style="width:140px">
            <el-option label="爽约" value="爽约">
              <span>爽约</span>
              <span style="float:right;color:#909399;font-size:12px">-6分</span>
            </el-option>
            <el-option label="暂离超时" value="暂离超时">
              <span>暂离超时</span>
              <span style="float:right;color:#909399;font-size:12px">-3分</span>
            </el-option>
            <el-option label="恶意占座" value="恶意占座">
              <span>恶意占座</span>
              <span style="float:right;color:#909399;font-size:12px">举报确认</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
            style="width:240px"
          />
        </el-form-item>
        <el-form-item class="btn-group">
          <el-button type="primary" icon="el-icon-search" @click="fetchData">查询</el-button>
          <el-button plain icon="el-icon-refresh" @click="resetQuery">重置</el-button>
          <el-button type="warning" plain icon="el-icon-download" @click="exportData">导出报表</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 数据表格 -->
    <div class="content-card data-card">
      <div class="table-header-bar">
        <span class="table-title">
          <i class="el-icon-document"></i>
          违规记录列表
        </span>
        <span class="table-count">共 <b>{{ pagination.total }}</b> 条记录</span>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe border style="width:100%" empty-text="暂无违规记录">
        <el-table-column prop="id" label="#" width="50" align="center" />
        <el-table-column prop="userName" label="学生" min-width="100" align="center" show-overflow-tooltip>
          <template slot-scope="{row}">
            <div class="user-cell">
              <span class="user-name">{{ row.userName }}</span>
              <span class="user-no">{{ row.studentNo }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="violationType" label="违规类型" width="110" align="center">
          <template slot-scope="{row}">
            <el-tag :type="vTypeMap[row.violationType]" effect="dark" size="small">{{ row.violationType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="处罚结果" width="130" align="center">
          <template slot-scope="{row}">
            <div class="penalty-cell">
              <span class="pts" :class="{ 'pts-ded': row.pointsDeducted < 0 }">
                {{ row.pointsDeducted > 0 ? '+' : '' }}{{ row.pointsDeducted }} 分
              </span>
              <span v-if="row.banDays > 0" class="ban-tag">{{ row.banDays }}天封禁</span>
              <span v-else class="ban-none">未封禁</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="violationTime" label="发生时间" width="165" align="center" sortable />
        <el-table-column prop="handledByName" label="来源" width="85" align="center">
          <template slot-scope="{row}">
            <span v-if="row.handledByName" class="src-manual">{{ row.handledByName }}</span>
            <span v-else class="src-auto">自动</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="110" align="center" fixed="right">
          <template slot-scope="{row}">
            <el-button type="text" size="small" icon="el-icon-view" @click="showDetail(row)">详情</el-button>
            <el-popconfirm title="确定删除此条违规记录？<br><span style='color:#F56C6C;font-size:12px'>删除后不可恢复</span>" confirm-button-text="确定删除" cancel-button-text="取消" icon="el-icon-warning" icon-color="#F56C6C" @confirm="deleteRecord(row)">
              <el-button slot="reference" type="text" size="small" icon="el-icon-delete" style="color:#F56C6C;margin-left:4px"></el-button>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrap">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pagination.current"
          :page-sizes="[10, 20, 50]"
          :page-size="pagination.size"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
        />
      </div>
    </div>

    <!-- 查看详情弹窗 -->
    <el-dialog title="违规详情" :visible.sync="detailVisible" width="520px" custom-class="detail-dialog">
      <div class="detail-body" v-if="currentRecord">
        <div class="detail-section">
          <h4 class="sec-title"><i class="el-icon-user"></i> 学生信息</h4>
          <div class="info-grid">
            <div class="info-item"><label>姓名</label><span>{{ currentRecord.userName }}</span></div>
            <div class="info-item"><label>学号</label><span>{{ currentRecord.studentNo }}</span></div>
          </div>
        </div>

        <div class="detail-section">
          <h4 class="sec-title"><i class="el-icon-warning"></i> 违规信息</h4>
          <div class="info-grid">
            <div class="info-item"><label>违规类型</label><span><el-tag :type="vTypeMap[currentRecord.violationType]" size="small">{{ currentRecord.violationType }}</el-tag></span></div>
            <div class="info-item"><label>发生时间</label><span>{{ currentRecord.violationTime }}</span></div>
            <div class="info-item"><label>扣分数值</label><span class="text-red bold">{{ currentRecord.pointsDeducted }} 分</span></div>
            <div class="info-item"><label>封禁天数</label><span>{{ currentRecord.banDays > 0 ? currentRecord.banDays + ' 天' : '不封禁' }}</span></div>
          </div>
          <div class="info-row" v-if="currentRecord.orderNo || currentRecord.ruleId">
            <label>关联信息</label>
            <span>{{ currentRecord.orderNo ? ('预约单号：' + currentRecord.orderNo) : (currentRecord.ruleId ? ('规则ID：' + currentRecord.ruleId) : '-') }}</span>
          </div>
        </div>

        <div class="detail-section" v-if="currentRecord.handleReply || currentRecord.status">
          <h4 class="sec-title"><i class="el-icon-notebook-2"></i> 处理信息</h4>
          <div class="info-row" v-if="currentRecord.status"><label>状态</label><span>{{ currentRecord.status }}</span></div>
          <div class="info-row" v-if="currentRecord.handleReply"><label>备注</label><span>{{ currentRecord.handleReply }}</span></div>
          <div class="info-row" v-if="currentRecord.handledByName"><label>处理人</label><span>{{ currentRecord.handledByName }}</span></div>
          <div class="info-row" v-if="currentRecord.handleTime"><label>时间</label><span>{{ currentRecord.handleTime }}</span></div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailVisible=false" icon="el-icon-close">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getViolationRecordPage, deleteViolationRecord } from '@/api/violation-record'

export default {
  name: 'ViolationManagement',
  data() {
    return {
      loading: false,
      tableData: [],
      query: { keyword: '', violationType: '' },
      dateRange: null,
      pagination: { current: 1, size: 10, total: 0 },
      detailVisible: false,
      currentRecord: null,
      vTypeMap: { '爽约': 'danger', '暂离超时': 'warning', '恶意占座': 'danger' }
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    async fetchData() {
      this.loading = true
      try {
        const params = {
          current: this.pagination.current,
          size: this.pagination.size,
          userName: this.query.keyword ? this.query.keyword : undefined,
          studentNo: this.query.keyword ? this.query.keyword : undefined,
          violationType: this.query.violationType || undefined,
          startTime: this.dateRange ? this.dateRange[0] : undefined,
          endTime: this.dateRange ? this.dateRange[1] : undefined
        }
        const res = await getViolationRecordPage(params)
        if (res.code === 200) {
          this.tableData = res.data.records
          this.pagination.total = res.data.total
        }
      } finally {
        this.loading = false
      }
    },
    resetQuery() {
      this.query = { keyword: '', violationType: '' }
      this.dateRange = null
      this.pagination.current = 1
      this.fetchData()
    },
    handleSizeChange(val) { this.pagination.size = val; this.fetchData() },
    handleCurrentChange(val) { this.pagination.current = val; this.fetchData() },

    showDetail(row) { this.currentRecord = row; this.detailVisible = true },

    async deleteRecord(row) {
      try {
        const res = await deleteViolationRecord(row.id)
        if (res.code === 200) {
          this.$message.success('删除成功')
          this.fetchData()
        } else {
          this.$message.error(res.message || '删除失败')
        }
      } catch (e) {
        this.$message.error('删除失败')
      }
    },

    async exportData() {
      try {
        this.$message.info('正在导出，请稍候...')
        const res = await fetch('/api/admin/export/violations', { credentials: 'include' })
        if (!res.ok) throw new Error('导出请求失败: ' + res.status)
        const blob = await res.blob()
        if (blob.size === 0) throw new Error('导出文件为空')
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = '违规记录.xlsx'
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
        this.$message.success('导出成功')
      } catch (e) {
        this.$message.error(e.message || '导出失败')
      }
    }
  }
}
</script>

<style scoped>
.violation-management {
  display: grid;
  gap: 18px;
}

/* ========== Hero 区域 ========== */
.page-hero {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  padding: 24px 28px;
  background: #FFFFFF;
  border-radius: 14px;
  border: 1px solid rgba(0, 0, 0, 0.06);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.hero-left { flex: 1; }

.page-kicker {
  display: inline-block;
  margin-bottom: 8px;
  color: #FF3B30;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 1.5px;
  text-transform: uppercase;
}
.page-hero h2 { margin: 0 0 6px; color: #1D1D1F; font-size: 26px; font-weight: 700; }
.page-hero p { color: #86868B; font-size: 13px; margin: 0; line-height: 1.5; }

.hero-tip {
  display: flex;
  align-items: center;
  gap: 7px;
  background: #FFF8E1;
  border: 1px solid #FFE082;
  border-radius: 10px;
  padding: 10px 16px;
  max-width: 320px;
  flex-shrink: 0;
  margin-left: 20px;
}
.hero-tip i { color: #FF9800; font-size: 15px; }
.hero-tip span { color: #E65100; font-size: 12px; line-height: 1.5; }

/* ========== 筛选栏 ========== */
.content-card {
  background: #FFFFFF;
  border-radius: 14px;
  border: 1px solid rgba(0, 0, 0, 0.06);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}
.search-area { padding: 18px 24px 6px; background: #FAFAFA; }
.search-area .btn-group { margin-left: 4px; }

/* ========== 数据卡片 ========== */
.data-card { padding: 16px 20px 20px; }

.table-header-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
  padding-bottom: 12px;
  border-bottom: 1px solid #F0F0F0;
}
.table-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}
.table-title i { margin-right: 6px; color: #409EFF; }
.table-count { font-size: 13px; color: #909399; }
.table-count b { color: #409EFF; font-weight: 600; }

/* ========== 表格单元格样式 ========== */
.user-cell { display: flex; flex-direction: column; align-items: center; gap: 2px; }
.user-name { font-weight: 500; color: #303133; font-size: 13px; }
.user-no { font-size: 11px; color: #909399; }

.penalty-cell { display: flex; flex-direction: column; align-items: center; gap: 3px; }
.pts { font-size: 13px; font-weight: 500; color: #606266; }
.pts-ded { color: #FF3B30; font-weight: 700; font-size: 14px; }
.ban-tag {
  font-size: 11px;
  color: #fff;
  background: #F56C6C;
  padding: 1px 8px;
  border-radius: 10px;
}
.ban-none { font-size: 11px; color: #C0C4CC; }

.src-manual { font-size: 12px; color: #606266; }
.src-auto {
  font-size: 11px;
  color: #67C23A;
  background: #f0f9eb;
  padding: 1px 8px;
  border-radius: 10px;
}

.text-red { color: #FF3B30; }
.bold { font-weight: 600; }

/* ========== 分页 ========== */
.pagination-wrap { text-align: right; margin-top: 16px; padding-top: 12px; border-top: 1px solid #F0F0F0; }

/* ========== 详情弹窗 ========== */
.detail-body { padding: 4px 0; }
.detail-section { margin-bottom: 18px; }
.detail-section:last-child { margin-bottom: 0; }

.sec-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 12px;
  padding-bottom: 8px;
  border-bottom: 1px dashed #EBEEF5;
}
.sec-title i { color: #409EFF; font-size: 16px; }

.info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 8px 16px; }
.info-item { display: flex; align-items: center; font-size: 13px; }
.info-item label { color: #909399; width: 64px; flex-shrink: 0; font-size: 12px; }
.info-item span { color: #303133; font-weight: 500; }

.info-row {
  display: flex;
  align-items: flex-start;
  font-size: 13px;
  margin-top: 6px;
  padding: 8px 10px;
  background: #FAFAFA;
  border-radius: 6px;
}
.info-row label { color: #909399; width: 56px; flex-shrink: 0; font-size: 12px; }
.info-row span { color: #303133; word-break: break-all; }

.dialog-footer { text-align: center; }
</style>
