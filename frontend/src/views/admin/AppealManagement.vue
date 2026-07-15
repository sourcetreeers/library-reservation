<template>
  <div class="appeal-management">
    <div class="page-hero">
      <div>
        <span class="page-kicker">Appeals</span>
        <h2>违约申诉管理</h2>
        <p>审核学生对扣分记录发起的申诉，支持通过并返还积分或驳回申诉。</p>
      </div>
    </div>

    <div class="content-card search-area">
      <el-form :inline="true">
        <el-form-item label="状态">
          <el-select v-model="searchStatus" placeholder="全部" clearable style="width: 150px;" @change="handleSearch">
            <el-option label="全部" value="" />
            <el-option label="待审核" value="待审核" />
            <el-option label="已通过" value="已通过" />
            <el-option label="已驳回" value="已驳回" />
          </el-select>
        </el-form-item>
      </el-form>
    </div>

    <div class="content-card data-card">
      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column label="申诉理由" min-width="200">
          <template slot-scope="scope">
            <el-tooltip :content="scope.row.reason" placement="top">
              <span class="reason-text">{{ scope.row.reason }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reply" label="处理回复" min-width="150">
          <template slot-scope="scope">
            <span class="reply-text">{{ scope.row.reply || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申诉时间" width="170" />
        <el-table-column label="操作" width="180" fixed="right">
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.status === '待审核'"
              type="success"
              size="mini"
              @click="handleAppeal(scope.row, '已通过')"
            >
              通过
            </el-button>
            <el-button
              v-if="scope.row.status === '待审核'"
              type="danger"
              size="mini"
              @click="showRejectDialog(scope.row)"
            >
              驳回
            </el-button>
            <el-tag v-else type="info" size="small">已处理</el-tag>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
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

    <!-- 驳回对话框 -->
    <el-dialog title="驳回申诉" :visible.sync="rejectDialogVisible" width="500px">
      <el-form>
        <el-form-item label="回复内容">
          <el-input
            v-model="rejectReply"
            type="textarea"
            rows="3"
            placeholder="请填写驳回理由（可选）"
          />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmReject">确定驳回</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getAppealPage, handleAppeal } from '@/api/appeal'

export default {
  name: 'AppealManagement',
  data() {
    return {
      loading: false,
      tableData: [],
      pagination: {
        current: 1,
        size: 10,
        total: 0
      },
      searchStatus: '',
      rejectDialogVisible: false,
      rejectTarget: null,
      rejectReply: ''
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const params = {
          current: this.pagination.current,
          size: this.pagination.size,
          status: this.searchStatus || undefined
        }
        const res = await getAppealPage(params)
        this.tableData = res.data.records || []
        this.pagination.total = res.data.total || 0
      } catch (error) {
        this.$message.error('加载数据失败')
      } finally {
        this.loading = false
      }
    },

    handleSearch() {
      this.pagination.current = 1
      this.loadData()
    },

    handleSizeChange(val) {
      this.pagination.size = val
      this.loadData()
    },

    handleCurrentChange(val) {
      this.pagination.current = val
      this.loadData()
    },

    getStatusType(status) {
      const map = { '待审核': 'warning', '已通过': 'success', '已驳回': 'danger' }
      return map[status] || 'info'
    },

    async handleAppeal(row, status) {
      try {
        await this.$confirm(
          status === '已通过' ? '确定通过此申诉？将通过后自动返还积分。' : '确定驳回此申诉？',
          '提示',
          { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
        )
        await handleAppeal(row.id, { status })
        this.$message.success('处理成功')
        this.loadData()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('操作失败')
        }
      }
    },

    showRejectDialog(row) {
      this.rejectTarget = row
      this.rejectReply = ''
      this.rejectDialogVisible = true
    },

    async confirmReject() {
      try {
        await handleAppeal(this.rejectTarget.id, { status: '已驳回', reply: this.rejectReply })
        this.$message.success('已驳回')
        this.rejectDialogVisible = false
        this.loadData()
      } catch (error) {
        this.$message.error('操作失败')
      }
    }
  }
}
</script>

<style scoped>
.appeal-management {
  display: grid;
  gap: 18px;
}

.page-hero,
.content-card {
  background: rgba(255, 255, 255, 0.92);
  border-radius: 24px;
  border: 1px solid rgba(236, 72, 153, 0.12);
  box-shadow: 0 14px 36px rgba(219, 39, 119, 0.08);
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
  color: #9a6c82;
  font-size: 14px;
}

.search-area {
  padding: 20px 24px 4px;
  background: #fff3f8;
}

.data-card {
  padding: 16px 16px 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.reason-text {
  display: inline-block;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.reply-text {
  color: #999;
  font-size: 13px;
}
</style>
