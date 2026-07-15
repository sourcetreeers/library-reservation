<template>
  <div class="dispute-management">
    <div class="page-hero">
      <div>
        <span class="page-kicker">Disputes</span>
        <h2>争议处理</h2>
        <p>统一管理占座举报和违约申诉，在一个页面完成所有审核操作。</p>
      </div>
    </div>

    <div class="content-card search-area">
      <el-form :inline="true">
        <el-form-item label="类型">
          <el-select v-model="searchType" placeholder="全部" clearable style="width: 130px;" @change="handleSearch">
            <el-option label="全部" value="" />
            <el-option label="举报" value="complaint" />
            <el-option label="申诉" value="appeal" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchStatus" placeholder="全部" clearable style="width: 150px;" @change="handleSearch">
            <el-option label="全部" value="" />
            <el-option v-for="s in statusOptions" :key="s.value" :label="s.label" :value="s.value" />
          </el-select>
        </el-form-item>
      </el-form>
    </div>

    <div class="content-card data-card">
      <el-table :data="tableData" v-loading="loading" border size="small">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="类型" width="80" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.type === 'complaint' ? 'danger' : 'warning'" size="small">
              {{ scope.row.type === 'complaint' ? '举报' : '申诉' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发起人" width="100">
          <template slot-scope="scope">
            <span>{{ scope.row.reporterName || scope.row.reporterId }}</span>
          </template>
        </el-table-column>
        <el-table-column label="关联对象" width="130">
          <template slot-scope="scope">
            <span v-if="scope.row.type === 'complaint'">
              {{ scope.row.targetName || (scope.row.seatNumber ? `座${scope.row.seatNumber}` : '-') }}
            </span>
            <span v-else>
              {{ scope.row.pointsRecordId ? `扣分记录#${scope.row.pointsRecordId}` : '-' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="位置/信息" width="120">
          <template slot-scope="scope">
            <span v-if="scope.row.type === 'complaint'" style="font-size:12px;">
              {{ scope.row.libraryName || '' }} {{ scope.row.seatNumber || '-' }}
            </span>
            <span v-else style="color:#999;font-size:12px;">-</span>
          </template>
        </el-table-column>
        <el-table-column label="预约时间" width="180">
          <template slot-scope="scope">
            <span v-if="scope.row.type === 'complaint'" style="font-size:12px;line-height:1.5;">
              <div v-if="scope.row.reporterReservationStartTime" style="color:#409EFF;">
                举报人: {{ formatTime(scope.row.reporterReservationStartTime) }}<br>
                ~{{ formatTime(scope.row.reporterReservationEndTime) }}
              </div>
              <div v-if="scope.row.occupantReservationStartTime" style="color:#E6A23C;margin-top:2px;">
                占座者: {{ formatTime(scope.row.occupantReservationStartTime) }}<br>
                ~{{ formatTime(scope.row.occupantReservationEndTime) }}
              </div>
            </span>
            <span v-else style="font-size:12px;line-height:1.5;">
              <div v-if="scope.row.reporterReservationStartTime" style="color:#67C23A;">
                预约时间: {{ formatTime(scope.row.reporterReservationStartTime) }}<br>
                ~{{ formatTime(scope.row.reporterReservationEndTime) }}
              </div>
              <span v-else style="color:#ccc;">-</span>
            </span>
          </template>
        </el-table-column>
        <el-table-column label="内容描述" min-width="160" show-overflow-tooltip>
          <template slot-scope="scope">
            <span style="font-size:12px;">{{ scope.row.content }}</span>
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
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reply" label="回复" min-width="120" show-overflow-tooltip>
          <template slot-scope="scope">
            <span class="reply-text" style="font-size:12px;">{{ scope.row.reply || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="155">
          <template slot-scope="scope">
            <span style="font-size:12px;">{{ scope.row.createTime }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template slot-scope="scope">
            <!-- 举报的操作按钮 -->
            <el-button-group v-if="scope.row.type === 'complaint' && isPending(scope.row)">
              <el-button type="success" size="mini" @click="handleAction(scope.row, '已确认')">
                确认占座
              </el-button>
              <el-button type="warning" size="mini" @click="handleAction(scope.row, '仅返还')">
                仅返还
              </el-button>
              <el-button type="danger" size="mini" @click="showRejectDialog(scope.row)">
                驳回
              </el-button>
            </el-button-group>
            <!-- 申诉的操作按钮 -->
            <el-button-group v-if="scope.row.type === 'appeal' && isPending(scope.row)">
              <el-button type="success" size="mini" @click="handleAction(scope.row, '已通过')">
                通过
              </el-button>
              <el-button type="danger" size="mini" @click="showRejectDialog(scope.row)">
                驳回
              </el-button>
            </el-button-group>
            <!-- 已处理 -->
            <el-tag v-if="!isPending(scope.row)" type="info" size="small">已处理</el-tag>
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

    <!-- 驳回对话框（通用） -->
    <el-dialog title="驳回争议" :visible.sync="rejectDialogVisible" width="500px">
      <div style="margin-bottom:16px;padding:12px;background:#F5F5F7;border-radius:8px;font-size:13px;color:#1D1D1F;">
        类型：<strong>{{ rejectTarget && rejectTarget.type === 'complaint' ? '举报' : '申诉' }}</strong>
        &nbsp;&nbsp; 发起人：<strong>{{ rejectTarget ? (rejectTarget.reporterName || rejectTarget.reporterId) : '' }}</strong>
      </div>
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
import { getDisputePage, handleDispute } from '@/api/dispute'

export default {
  name: 'DisputeManagement',
  data() {
    return {
      loading: false,
      tableData: [],
      pagination: {
        current: 1,
        size: 10,
        total: 0
      },
      searchType: '',
      searchStatus: '',
      rejectDialogVisible: false,
      rejectTarget: null,
      rejectReply: ''
    }
  },
  computed: {
    statusOptions() {
      // 根据类型返回不同的状态选项
      if (this.searchType === 'complaint') {
        return [
          { label: '待处理', value: '待处理' },
          { label: '已确认', value: '已确认' },
          { label: '仅返还', value: '仅返还' },
          { label: '已驳回', value: '已驳回' }
        ]
      } else if (this.searchType === 'appeal') {
        return [
          { label: '待审核', value: '待审核' },
          { label: '已通过', value: '已通过' },
          { label: '已驳回', value: '已驳回' }
        ]
      }
      // 全部类型时，显示所有状态选项
      return [
        { label: '待处理', value: '待处理' },
        { label: '待审核', value: '待审核' },
        { label: '已确认', value: '已确认' },
        { label: '仅返还', value: '仅返还' },
        { label: '已通过', value: '已通过' },
        { label: '已驳回', value: '已驳回' }
      ]
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
          type: this.searchType || undefined,
          status: this.searchStatus || undefined
        }
        const res = await getDisputePage(params)
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

    formatTime(timeStr) {
      if (!timeStr) return '-'
      const d = new Date(timeStr)
      const pad = n => String(n).padStart(2, '0')
      return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
    },

    handleSizeChange(val) {
      this.pagination.size = val
      this.loadData()
    },

    handleCurrentChange(val) {
      this.pagination.current = val
      this.loadData()
    },

    isPending(row) {
      if (row.type === 'complaint') {
        return row.status === '待处理'
      }
      return row.status === '待审核'
    },

    getStatusType(status) {
      const map = {
        '待处理': 'warning',
        '待审核': 'warning',
        '已确认': 'success',
        '已通过': 'success',
        '仅返还': 'info',
        '已驳回': 'danger'
      }
      return map[status] || 'info'
    },

    async handleAction(row, status) {
      let confirmMsg = ''
      if (row.type === 'complaint') {
        if (status === '已确认') {
          confirmMsg = '确定确认此占座举报？确认后将自动对占座者扣分，并返还举报者被误扣的积分。'
        } else if (status === '仅返还') {
          confirmMsg = '仅返还举报者积分，不对占座者扣分。确定吗？'
        }
      } else if (row.type === 'appeal') {
        if (status === '已通过') {
          confirmMsg = '确定通过此申诉？通过后将自动返还扣减的积分。'
        }
      }

      try {
        await this.$confirm(confirmMsg, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await handleDispute(row.id, { type: row.type, status })
        this.$message.success('处理成功')
        this.loadData()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error(error.message || '操作失败')
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
        await handleDispute(this.rejectTarget.id, {
          type: this.rejectTarget.type,
          status: '已驳回',
          reply: this.rejectReply
        })
        this.$message.success('已驳回')
        this.rejectDialogVisible = false
        this.loadData()
      } catch (error) {
        this.$message.error(error.message || '操作失败')
      }
    }
  }
}
</script>

<style scoped>
.dispute-management {
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

.search-area {
  padding: 20px 24px 4px;
  background: #F5F5F7;
}

.data-card {
  padding: 16px 16px 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.reply-text {
  color: #999;
  font-size: 13px;
}
</style>
