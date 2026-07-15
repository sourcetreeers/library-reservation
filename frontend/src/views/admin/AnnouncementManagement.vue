<template>
  <div class="announcement-management">
    <div class="page-hero">
      <div>
        <span class="page-kicker">Announcements</span>
        <h2>公告管理</h2>
        <p>发布和管理系统公告、维修通知与紧急信息。</p>
      </div>
      <el-button type="primary" icon="el-icon-plus" round @click="showAddDialog">发布公告</el-button>
    </div>

    <!-- 搜索栏 -->
    <div class="content-card search-area">

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="标题">
          <el-input v-model="searchForm.title" placeholder="请输入标题搜索" clearable />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="searchForm.type" placeholder="全部类型" clearable>
            <el-option label="普通公告" value="普通公告" />
            <el-option label="维修通知" value="维修通知" />
            <el-option label="紧急通知" value="紧急通知" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable>
            <el-option label="发布中" value="发布中" />
            <el-option label="已下架" value="已下架" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="loadData">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格 -->
    <div class="content-card data-card">
    <el-table :data="tableData" border v-loading="loading">
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip>
          <template slot-scope="scope">
            <span class="title-cell" :class="{ 'is-top': scope.row.priority >= 80 }">
              {{ scope.row.title }}
              <i v-if="scope.row.priority >= 80" class="el-icon-top" title="高优先级"></i>
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="110" align="center">
          <template slot-scope="scope">
            <el-tag :type="getTypeColor(scope.row.type)" size="small" effect="light">{{ scope.row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" width="85" align="center">
          <template slot-scope="scope">
            <el-rate
              :value="Math.min(scope.row.priority / 20, 5)"
              disabled
              :max="5"
              show-score
              :score-template="'{value}'"
            />
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template slot-scope="scope">
            <el-switch
              v-model="scope.row.status"
              active-value="发布中"
              inactive-value="已下架"
              active-color="#67c23a"
              @change="(val) => handleToggleStatus(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="发布时间/过期时间" width="180" align="center">
          <template slot-scope="scope">
            <div class="time-cell">
              <div>{{ formatTime(scope.row.publishTime) }}</div>
              <div v-if="scope.row.expireTime" class="expire-text">
                至 {{ formatTime(scope.row.expireTime) }}
                <el-tag
                  v-if="isExpired(scope.row.expireTime)"
                  size="mini"
                  type="danger"
                  effect="plain"
                  style="margin-left:4px"
                >已过期</el-tag>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="createBy" label="发布人" width="100" align="center" />
        <el-table-column label="操作" width="170" fixed="right" align="center">
          <template slot-scope="scope">
            <el-button type="text" icon="el-icon-view" @click="showPreviewDialog(scope.row)">预览</el-button>
            <el-button type="text" icon="el-icon-edit" @click="showEditDialog(scope.row)">编辑</el-button>
            <el-button
              type="text"
              icon="el-icon-delete"
              style="color: #f56c6c"
              @click="handleDelete(scope.row)"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        :current-page="pagination.current"
        :page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        style="margin-top: 20px; text-align: right"
      />
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="640px"
      @close="resetForm"
      :close-on-click-modal="false"
    >
      <el-form :model="form" :rules="rules" ref="announcementForm" label-width="90px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入公告标题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="类型" prop="type">
              <el-select v-model="form.type" placeholder="选择类型" style="width:100%">
                <el-option label="普通公告" value="普通公告" />
                <el-option label="维修通知" value="维修通知" />
                <el-option label="紧急通知" value="紧急通知" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="优先级" prop="priority">
              <el-input-number v-model="form.priority" :min="0" :max="100" controls-position="right" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="8"
            placeholder="请输入公告内容&#10;&#10;提示：支持换行，移动端会自动格式化显示"
            maxlength="2000"
            show-word-limit
            resize="vertical"
          />
        </el-form-item>
        <el-form-item label="过期时间">
          <el-date-picker
            v-model="form.expireTime"
            type="datetime"
            placeholder="不设置则永久有效（可选）"
            value-format="yyyy-MM-dd HH:mm:ss"
            style="width:100%"
          >
          </el-date-picker>
        </el-form-item>
        <el-alert
          v-if="form.expireTime && isExpired(form.expireTime)"
          title="该过期时间已早于当前时间，公告将显示为已过期状态"
          type="warning"
          :closable="false"
          show-icon
          style="margin-bottom:8px"
        />
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false" plain>取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitLoading">
          {{ form.id ? '保存修改' : '发布公告' }}
        </el-button>
      </div>
    </el-dialog>

    <!-- 公告预览弹窗 -->
    <el-dialog
      title="公告预览"
      :visible.sync="previewVisible"
      width="520px"
      append-to-body
    >
      <div v-if="previewData" class="preview-container">
        <div class="preview-header">
          <h3 class="preview-title">{{ previewData.title }}</h3>
          <div class="preview-meta">
            <el-tag :type="getTypeColor(previewData.type)" size="small">{{ previewData.type }}</el-tag>
            <el-tag size="small" :type="previewData.status === '发布中' ? 'success' : 'info'">
              {{ previewData.status }}
            </el-tag>
          </div>
        </div>
        <el-divider></el-divider>
        <div class="preview-content">
          <p v-for="(line, idx) in previewContentLines" :key="idx" class="content-line">{{ line }}</p>
        </div>
        <el-divider></el-divider>
        <div class="preview-footer">
          <span><i class="el-icon-user"></i>{{ previewData.createBy || '系统' }}</span>
          <span><i class="el-icon-time"></i>{{ formatTime(previewData.publishTime) }}</span>
          <span v-if="previewData.expireTime"><i class="el-icon-alarm-clock"></i>
            过期：{{ formatTime(previewData.expireTime) }}
            <el-tag v-if="isExpired(previewData.expireTime)" size="mini" type="danger" style="margin-left:4px">已过期</el-tag>
          </span>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  getAnnouncementPage,
  addAnnouncement,
  updateAnnouncement,
  deleteAnnouncement,
  toggleAnnouncementStatus
} from '@/api/announcement'

export default {
  name: 'AnnouncementManagement',
  data() {
    return {
      loading: false,
      submitLoading: false,
      tableData: [],
      pagination: { current: 1, size: 10, total: 0 },
      searchForm: { title: '', type: '', status: '' },
      dialogVisible: false,
      dialogTitle: '发布公告',
      form: {
        id: null,
        title: '',
        type: '普通公告',
        priority: 0,
        content: '',
        expireTime: null
      },
      rules: {
        title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
        type: [{ required: true, message: '请选择类型', trigger: 'change' }],
        content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
      },
      // 预览相关
      previewVisible: false,
      previewData: null
    }
  },

  computed: {
    previewContentLines() {
      if (!this.previewData || !this.previewData.content) return []
      return this.previewData.content.split('\n').filter(l => l.trim())
    }
  },

  created() { this.loadData() },

  methods: {
    async loadData() {
      this.loading = true
      try {
        const params = {
          current: this.pagination.current,
          size: this.pagination.size,
          ...this.searchForm
        }
        const res = await getAnnouncementPage(params)
        this.tableData = res.data.records || []
        this.pagination.total = res.data.total || 0
      } catch (e) {
        this.$message.error('加载失败')
      } finally {
        this.loading = false
      }
    },

    showAddDialog() {
      this.dialogTitle = '发布公告'
      this.dialogVisible = true
    },

    showEditDialog(row) {
      this.dialogTitle = '编辑公告'
      this.form = { ...row }
      this.dialogVisible = true
    },

    resetForm() {
      this.form = {
        id: null, title: '', type: '普通公告', priority: 0, content: '', expireTime: null
      }
      this.$refs.announcementForm && this.$refs.announcementForm.clearValidate()
    },

    async submitForm() {
      try {
        await this.$refs.announcementForm.validate()
        this.submitLoading = true

        if (this.form.id) {
          await updateAnnouncement(this.form)
          this.$message.success('更新成功')
        } else {
          await addAnnouncement(this.form)
          this.$message.success('发布成功')
        }

        this.dialogVisible = false
        this.loadData()
      } catch (error) {
        if (error !== false) this.$message.error('操作失败')
      } finally {
        this.submitLoading = false
      }
    },

    async handleToggleStatus(row) {
      try {
        await toggleAnnouncementStatus(row.id)
        this.$message.success((row.status === '发布中' ? '已上架' : '已下架'))
        this.loadData()
      } catch (e) {
        this.$message.error('操作失败')
        row.status = row.status === '发布中' ? '已下架' : '发布中'
      }
    },

    async handleDelete(row) {
      try {
        await this.$confirm('确定要删除该公告吗？删除后不可恢复。', '警告', { type: 'warning' })
        await deleteAnnouncement(row.id)
        this.$message.success('删除成功')
        this.loadData()
      } catch (e) {
        if (e !== 'cancel') this.$message.error('删除失败')
      }
    },

    // 预览
    showPreviewDialog(row) {
      this.previewData = row
      this.previewVisible = true
    },

    // 工具方法
    getTypeColor(type) {
      const map = { '普通公告': '', '维修通知': 'warning', '紧急通知': 'danger' }
      return map[type] || ''
    },
    isExpired(expireTime) {
      if (!expireTime) return false
      return new Date(expireTime).getTime() < Date.now()
    },
    formatTime(time) {
      if (!time) return '-'
      return time.replace(/T/, ' ').substring(0, 16)
    },

    resetSearch() {
      this.searchForm = { title: '', type: '', status: '' }
      this.pagination.current = 1
      this.loadData()
    },
    handleSizeChange(size) {
      this.pagination.size = size
      this.pagination.current = 1
      this.loadData()
    },
    handleCurrentChange(current) {
      this.pagination.current = current
      this.loadData()
    }
  }
}
</script>

<style scoped>
.announcement-management { display: grid; gap: 18px; }

.page-hero,
.content-card {
  background: #FFFFFF;
  border-radius: 14px;
  border: 1px solid rgba(0, 0, 0, 0.06);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.page-hero { display: flex; align-items: center; justify-content: space-between; padding: 24px 28px; }

.page-kicker {
  display: inline-block;
  margin-bottom: 10px;
  color: #FF9500;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.8px;
  text-transform: uppercase;
}
.page-hero h2 { margin: 0 0 6px; color: #1D1D1F; font-size: 30px; }
.page-hero p { margin: 0; color: #86868B; font-size: 14px; }

.search-area { padding: 20px 24px 4px; background: #F5F5F7; }
.data-card { padding: 16px 16px 20px; }

.search-form { margin-bottom: 16px; }

/* 标题单元格 */
.title-cell { display: inline-flex; align-items: center; gap: 4px; }
.title-cell.is-top { color: #e6a23c; font-weight: 500; }
.title-cell .el-icon-top { font-size: 14px; }

/* 时间单元格 */
.time-cell { line-height: 1.6; font-size: 12px; color: #606266; }
.expire-text { color: #909399; }

/* 对话框 */
.dialog-footer { text-align: right; }

/* 预览容器 */
.preview-container { padding: 4px 0; }
.preview-header { display: flex; justify-content: space-between; align-items: flex-start; gap: 12px; flex-wrap: wrap; }
.preview-title { margin: 0; font-size: 18px; color: #303133; flex: 1; min-width: 0; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.preview-meta { display: flex; gap: 8px; flex-shrink: 0; }

.preview-content {
  background: #fafafa;
  border-radius: 6px;
  padding: 16px 20px;
  min-height: 60px;
  max-height: 300px;
  overflow-y: auto;
}
.content-line {
  margin: 0 0 8px 0;
  line-height: 1.7;
  color: #4a4a4a;
  font-size: 14px;
  white-space: pre-wrap;
  word-break: break-all;
}
.content-line:last-child { margin-bottom: 0; }

.preview-footer {
  display: flex; gap: 24px; flex-wrap: wrap;
  font-size: 13px; color: #909399;
}
.preview-footer span { display: inline-flex; align-items: center; gap: 4px; }
.preview-footer i { vertical-align: middle; }
</style>
