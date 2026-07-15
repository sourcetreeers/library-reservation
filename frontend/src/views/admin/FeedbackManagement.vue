<template>
  <div class="feedback-management">
    <div class="page-hero">
      <div>
        <span class="page-kicker">Feedback</span>
        <h2>反馈管理</h2>
        <p>查看和处理学生提交的反馈建议。</p>
      </div>
    </div>

    <!-- 筛选栏 -->
    <div class="content-card search-area">
      <el-form :inline="true" :model="query" size="small">
        <el-form-item label="反馈类型">
          <el-select v-model="query.type" placeholder="全部" clearable style="width:130px">
            <el-option label="功能建议" value="建议" />
            <el-option label="环境投诉" value="投诉" />
            <el-option label="设备报修" value="报修" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width:115px">
            <el-option label="待处理" value="待处理" />
            <el-option label="已回复" value="已回复" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始"
            end-placeholder="结束"
            value-format="yyyy-MM-dd"
            style="width:220px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="fetchData">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 数据表格 -->
    <div class="content-card data-card">
      <el-table :data="tableData" v-loading="loading" stripe border style="width:100%">
        <el-table-column prop="id" label="#" width="50" align="center" />
        <el-table-column prop="userName" label="提交人" width="85" align="center" show-overflow-tooltip />
        <el-table-column prop="studentNo" label="学号" width="95" align="center" show-overflow-tooltip />
        <el-table-column prop="type" label="反馈类型" width="95" align="center">
          <template slot-scope="{row}">
            <el-tag :type="typeMap[row.type]" size="mini">{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="160" show-overflow-tooltip />
        <el-table-column prop="createTime" label="提交时间" width="155" align="center" sortable />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template slot-scope="{row}">
            <el-tag :type="fbStatusMap[row.status]" size="mini">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template slot-scope="{row}">
            <el-button v-if="row.status === '待处理'" type="text" size="small" style="color:#409eff" @click="openReply(row)">回复</el-button>
            <el-button v-else type="text" size="small" @click="showDetail(row)">查看</el-button>
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
          layout="total, sizes, prev, pager, next"
          :total="pagination.total"
        />
      </div>
    </div>

    <!-- 回复弹窗 -->
    <el-dialog title="处理反馈" :visible.sync="replyVisible" width="550px" :close-on-click-modal="false">
      <div class="feedback-info" v-if="currentFeedback">
        <div class="fi-row"><label>提交人：</label><span>{{ currentFeedback.userName }} ({{ currentFeedback.studentNo }})</span></div>
        <div class="fi-row"><label>类型：</label><el-tag :type="typeMap[currentFeedback.type]" size="small">{{ currentFeedback.type }}</el-tag></div>
        <div class="fi-row"><label>标题：</label><span style="font-weight:500;">{{ currentFeedback.title }}</span></div>
        <div class="fi-row fi-full"><label>内容：</label><p class="fb-content">{{ currentFeedback.content }}</p></div>
        <div class="fi-row" v-if="currentFeedback.imageUrl"><label>图片：</label><el-image :src="currentFeedback.imageUrl" style="max-height:120px;border-radius:4px;" fit="cover" :preview-src-list="[currentFeedback.imageUrl]"></el-image></div>
      </div>
      
      <el-divider content-position="left">回复内容</el-divider>
      
      <el-form ref="replyFormRef" :model="replyForm" :rules="replyRules" label-width="85px" size="medium">
        <el-form-item label="回复内容" prop="reply">
          <el-input v-model="replyForm.reply" type="textarea" :rows="4" placeholder="请输入回复内容（必填）" maxlength="500" show-word-limit></el-input>
        </el-form-item>
        <el-form-item label="处理结果">
          <el-radio-group v-model="replyForm.handleResult">
            <el-radio label="已采纳">已采纳</el-radio>
            <el-radio label="已驳回">已驳回</el-radio>
            <el-radio label="已转交">已转交</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="replyVisible=false">取消</el-button>
        <el-button type="primary" @click="confirmReply">提交回复</el-button>
      </div>
    </el-dialog>

    <!-- 查看已回复详情弹窗 -->
    <el-dialog title="反馈详情" :visible.sync="detailVisible" width="520px">
      <div class="feedback-info detail-view" v-if="currentFeedback">
        <div class="fi-row"><label>提交人：</label><span>{{ currentFeedback.userName }} ({{ currentFeedback.studentNo }})</span></div>
        <div class="fi-row"><label>类型：</label><el-tag :type="typeMap[currentFeedback.type]" size="small">{{ currentFeedback.type }}</el-tag></div>
        <div class="fi-row"><label>标题：</label><span style="font-weight:500;">{{ currentFeedback.title }}</span></div>
        <div class="fi-row fi-full"><label>内容：</label><p class="fb-content">{{ currentFeedback.content }}</p></div>
        <div class="fi-row" v-if="currentFeedback.imageUrl"><label>图片：</label><el-image :src="currentFeedback.imageUrl" style="max-height:120px;border-radius:4px;" fit="cover" :preview-src-list="[currentFeedback.imageUrl]"></el-image></div>
        
        <el-divider content-position="left">回复信息</el-divider>
        <div class="fi-row fi-full"><label>回复内容：</label><p class="fb-content reply-text">{{ currentFeedback.reply }}</p></div>
        <div class="fi-row"><label>处理结果：</label><el-tag size="small">{{ currentFeedback.handleResult }}</el-tag></div>
        <div class="fi-row"><label>处理人：</label><span>{{ currentFeedback.handlerName }}</span></div>
        <div class="fi-row"><label>处理时间：</label><span>{{ currentFeedback.handleTime }}</span></div>
      </div>
      <div slot="footer">
        <el-button @click="detailVisible=false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getFeedbackPage, replyFeedback } from '@/api/feedback'

export default {
  name: 'FeedbackManagement',
  data() {
    return {
      loading: false,
      tableData: [],
      query: { type: '', status: '' },
      dateRange: null,
      pagination: { current: 1, size: 10, total: 0 },
      replyVisible: false,
      detailVisible: false,
      currentFeedback: null,
      replyForm: { reply: '', handleResult: '已采纳' },
      replyRules: {
        reply: [{ required: true, message: '请输入回复内容', trigger: 'blur' }]
      },
      typeMap: { '建议': '', '投诉': 'warning', '报修': 'danger', '其他': 'info' },
      fbStatusMap: { '待处理': 'warning', '已回复': 'success' }
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
          type: this.query.type || undefined,
          status: this.query.status || undefined,
          startTime: this.dateRange ? this.dateRange[0] : undefined,
          endTime: this.dateRange ? this.dateRange[1] : undefined
        }
        const res = await getFeedbackPage(params)
        if (res.code === 200) {
          this.tableData = res.data.records
          this.pagination.total = res.data.total
        }
      } finally {
        this.loading = false
      }
    },
    resetQuery() {
      this.query = { type: '', status: '' }
      this.dateRange = null
      this.pagination.current = 1
      this.fetchData()
    },
    handleSizeChange(val) { this.pagination.size = val; this.fetchData() },
    handleCurrentChange(val) { this.pagination.current = val; this.fetchData() },

    openReply(row) {
      this.currentFeedback = row
      this.replyForm = { reply: '', handleResult: '已采纳' }
      this.replyVisible = true
      this.$nextTick(() => this.$refs.replyFormRef && this.$refs.replyFormRef.clearValidate())
    },
    async confirmReply() {
      this.$refs.replyFormRef.validate(async valid => {
        if (!valid) return
        const res = await replyFeedback(this.currentFeedback.id, this.replyForm)
        if (res.code === 200) {
          this.$message.success('回复成功')
          this.replyVisible = false
          this.fetchData()
        } else {
          this.$message.error(res.message || '回复失败')
        }
      })
    },

    showDetail(row) {
      this.currentFeedback = row
      this.detailVisible = true
    }
  }
}
</script>

<style scoped>
.feedback-management {
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

.page-hero { display: flex; align-items: center; padding: 24px 28px; }

.page-kicker {
  display: inline-block;
  margin-bottom: 10px;
  color: #007AFF;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.8px;
  text-transform: uppercase;
}
.page-hero h2 { margin: 0 0 8px; color: #1D1D1F; font-size: 30px; }
.page-hero p { color: #86868B; font-size: 14px; margin: 0; }

.search-area { padding: 20px 24px 4px; background: #F5F5F7; }
.data-card { padding: 16px 16px 20px; }
.pagination-wrap { text-align:right; margin-top:16px; }

.feedback-info { font-size:14px; }
.fi-row { display:flex; line-height:28px; margin-bottom:4px; }
.fi-row.fi-full { flex-direction:column; }
.fi-row label { color:#888; width:72px; flex-shrink:0; }
.fb-content { background:#f5f7fa; padding:10px 12px; border-radius:8px; margin:4px 0; font-size:13px; line-height:1.6; white-space:pre-wrap; word-break:break-all; }
.reply-text { background:#f0fdf4; }
</style>
