<template>
  <div class="complaint-management">
    <div class="page-hero">
      <div>
        <span class="page-kicker">Complaints</span>
        <h2>举报管理</h2>
        <p>审核学生对占座行为提交的举报，确认后对占座者扣分并返还被影响学生的积分。</p>
      </div>
    </div>

    <div class="content-card search-area">
      <el-form :inline="true">
        <el-form-item label="状态">
          <el-select v-model="searchStatus" placeholder="全部" clearable style="width: 150px;" @change="handleSearch">
            <el-option label="全部" value="" />
            <el-option label="待处理" value="待处理" />
            <el-option label="已确认" value="已确认" />
            <el-option label="仅返还" value="仅返还" />
            <el-option label="已驳回" value="已驳回" />
          </el-select>
        </el-form-item>
      </el-form>
    </div>

    <div class="content-card data-card">
      <el-table :data="tableData" v-loading="loading" border size="small">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="举报人" width="80">
          <template slot-scope="scope">
            <span>{{ scope.row.reporterName || scope.row.reporterId }}</span>
          </template>
        </el-table-column>
        <el-table-column label="占座者" width="80">
          <template slot-scope="scope">
            <span>{{ scope.row.occupantName || scope.row.occupantId || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="图书室" width="110">
          <template slot-scope="scope">
            <span>{{ scope.row.libraryName || scope.row.libraryId }}</span>
          </template>
        </el-table-column>
        <el-table-column label="座位" width="70">
          <template slot-scope="scope">
            <span>{{ scope.row.seatNumber || scope.row.seatId }}</span>
          </template>
        </el-table-column>
        <el-table-column label="举报者预约" width="140">
          <template slot-scope="scope">
            <span v-if="scope.row.reporterReservationStartTime" style="font-size:12px;line-height:1.4;">
              {{ formatTime(scope.row.reporterReservationStartTime) }}<br>
              ~{{ formatTime(scope.row.reporterReservationEndTime) }}
            </span>
            <span v-else style="color:#ccc;">-</span>
          </template>
        </el-table-column>
        <el-table-column label="占座者预约" width="140">
          <template slot-scope="scope">
            <span v-if="scope.row.occupantReservationStartTime" style="font-size:12px;line-height:1.4;">
              {{ formatTime(scope.row.occupantReservationStartTime) }}<br>
              ~{{ formatTime(scope.row.occupantReservationEndTime) }}
            </span>
            <span v-else style="color:#ccc;">-</span>
          </template>
        </el-table-column>
        <el-table-column label="图片" width="70" align="center">
          <template slot-scope="scope">
            <el-image
              v-if="scope.row.imageUrl"
              :src="scope.row.imageUrl"
              fit="cover"
              style="width:40px;height:40px;border-radius:4px;cursor:pointer;"
              :preview-src-list="[scope.row.imageUrl]"
            >
              <div slot="error" class="image-slot">
                <i class="el-icon-picture-outline" style="font-size:20px;color:#ccc;"></i>
              </div>
            </el-image>
            <span v-else style="color:#ccc;font-size:12px;">-</span>
          </template>
        </el-table-column>
        <el-table-column label="描述" min-width="150" show-overflow-tooltip>
          <template slot-scope="scope">
            <span style="font-size:12px;">{{ scope.row.description }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="handlerReply" label="回复" min-width="120" show-overflow-tooltip>
          <template slot-scope="scope">
            <span class="reply-text" style="font-size:12px;">{{ scope.row.handlerReply || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="举报时间" width="140">
          <template slot-scope="scope">
            <span style="font-size:12px;">{{ scope.row.createTime }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template slot-scope="scope">
            <el-button-group v-if="scope.row.status === '待处理'">
              <el-button
                type="success"
                size="mini"
                @click="handleConfirm(scope.row)"
              >
                确认占座
              </el-button>
              <el-button
                type="warning"
                size="mini"
                @click="handleRefundOnly(scope.row)"
              >
                仅返还积分
              </el-button>
              <el-button
                type="danger"
                size="mini"
                @click="showRejectDialog(scope.row)"
              >
                驳回
              </el-button>
            </el-button-group>
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
    <el-dialog title="驳回举报" :visible.sync="rejectDialogVisible" width="500px">
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
import { getComplaintPage, handleComplaint } from '@/api/complaint'

export default {
  name: 'ComplaintManagement',
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
        const res = await getComplaintPage(params)
        this.tableData = res.data.records || []
        this.pagination.total = res.data.total || 0
      } catch (error) {
        this.$message.error('加载数据失败')
      } finally {
        this.loading = false
      }
    },

    formatTime(timeStr) {
      if (!timeStr) return '-'
      const d = new Date(timeStr)
      const pad = n => String(n).padStart(2, '0')
      return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
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
      const map = { '待处理': 'warning', '已确认': 'success', '仅返还': 'info', '已驳回': 'danger' }
      return map[status] || 'info'
    },

    async handleConfirm(row) {
      try {
        await this.$confirm(
          '确定确认此占座举报？确认后将自动对占座者扣3分，并返还举报者被误扣的积分。',
          '提示',
          { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
        )
        await handleComplaint(row.id, { status: '已确认' })
        this.$message.success('处理成功，占座者已扣分，举报者积分已返还')
        this.loadData()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('操作失败')
        }
      }
    },

    async handleRefundOnly(row) {
      try {
        await this.$confirm(
          '仅返还举报者积分，不对占座者扣分。确定吗？',
          '提示',
          { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
        )
        await handleComplaint(row.id, { status: '仅返还' })
        this.$message.success('处理成功，举报者积分已返还，占座者未被扣分')
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
        await handleComplaint(this.rejectTarget.id, { status: '已驳回', reply: this.rejectReply })
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
.complaint-management {
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
