<template>
  <div class="reservation-management">
    <div class="page-hero">
      <div>
        <span class="page-kicker">Reservations</span>
        <h2>预约管理</h2>
        <p>聚合用户预约、状态筛选与后台取消操作，提升处理效率。</p>
      </div>
    </div>
    
    <div class="content-card search-area">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="用户姓名">
          <el-input v-model="searchForm.userName" placeholder="请输入用户姓名" clearable style="width: 150px;" />
        </el-form-item>
        <el-form-item label="图书室">
          <el-select 
            v-model="searchForm.libraryId" 
            placeholder="请选择图书室"
            clearable 
            filterable
            style="width: 150px;"
          >
            <el-option
              v-for="library in libraries"
              :key="library.id"
              :label="library.name"
              :value="library.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="座位号">
          <el-input v-model="searchForm.seatNumber" placeholder="请输入座位号" clearable style="width: 120px;" />
        </el-form-item>
        <el-form-item label="预约状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 120px;">
            <el-option label="已预约" value="已预约" />
            <el-option label="已使用" value="已使用" />
            <el-option label="爽约" value="爽约" />
            <el-option label="已取消" value="已取消" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="warning" plain icon="el-icon-download" @click="exportData">导出Excel</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <div class="content-card data-card">
    <el-table :data="tableData" v-loading="loading" border>
      <el-table-column prop="orderNo" label="流水号" width="150" />
      <el-table-column prop="realName" label="用户姓名" width="100" />
      <el-table-column prop="libraryName" label="图书室" width="120" />
      <el-table-column prop="seatNumber" label="座位号" width="80" />
      <el-table-column label="预约时间" width="300">
        <template slot-scope="scope">
          {{ formatTimeRange(scope.row.startTime, scope.row.endTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ scope.row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="预约时间" width="180" />
      <el-table-column label="操作" width="150">
        <template slot-scope="scope">
          <el-button
            v-if="scope.row.status === '已预约'"
            size="mini"
            type="danger"
            @click="handleCancel(scope.row)"
          >
            取消预约
          </el-button>
          <span v-else>-</span>
        </template>
      </el-table-column>
    </el-table>
    
    <div class="pagination">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.current"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pagination.size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
      />
    </div>
    </div>
  </div>
</template>

<script>
import { getReservationPage, cancelReservation } from '@/api/reservation'
import { getLibraryList } from '@/api/library'
import { formatTimeRange as formatTimeRangeUtil } from '@/utils/time'

export default {
  name: 'ReservationManagement',
  data() {
    return {
      loading: false,
      tableData: [],
      libraries: [],
      pagination: {
        current: 1,
        size: 10,
        total: 0
      },
      searchForm: {
        userName: '',
        libraryId: null,
        seatNumber: '',
        status: ''
      }
    }
  },
  
  async created() {
    await this.loadLibraries()
    this.loadData()
  },
  
  methods: {
    async loadLibraries() {
      try {
        const res = await getLibraryList()
        this.libraries = res.data
      } catch (error) {
        this.$message.error('加载图书馆列表失败')
      }
    },
    
    async loadData() {
      this.loading = true
      try {
        const params = {
          current: this.pagination.current,
          size: this.pagination.size,
          userName: this.searchForm.userName,
          libraryId: this.searchForm.libraryId,
          seatNumber: this.searchForm.seatNumber,
          status: this.searchForm.status
        }
        const res = await getReservationPage(params)
        this.tableData = res.data.records
        this.pagination.total = res.data.total
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
    
    handleReset() {
      this.searchForm.userName = ''
      this.searchForm.libraryId = null
      this.searchForm.seatNumber = ''
      this.searchForm.status = ''
      this.pagination.current = 1
      this.loadData()
    },

    async exportData() {
      try {
        this.$message.info('正在导出，请稍候...')
        const status = this.searchForm.status || ''
        const res = await fetch('/api/admin/export/reservations?status=' + status, { credentials: 'include' })
        if (!res.ok) throw new Error('导出请求失败: ' + res.status)
        const blob = await res.blob()
        if (blob.size === 0) throw new Error('导出文件为空')
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = '预约记录.xlsx'
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
        this.$message.success('导出成功')
      } catch (e) {
        this.$message.error(e.message || '导出失败')
      }
    },
    
    handleSizeChange(val) {
      this.pagination.size = val
      this.loadData()
    },
    
    handleCurrentChange(val) {
      this.pagination.current = val
      this.loadData()
    },
    
    async handleCancel(row) {
      try {
        await this.$confirm('确定要取消这个预约吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        await cancelReservation(row.id)
        this.$message.success('取消成功')
        this.loadData()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('取消失败')
        }
      }
    },
    
    formatTimeRange(startTime, endTime) {
      return formatTimeRangeUtil(startTime, endTime)
    },
    
    getStatusType(status) {
      const statusMap = {
        '已预约': 'primary',
        '已使用': 'success',
        '爽约': 'danger',
        '已取消': 'info'
      }
      return statusMap[status] || 'info'
    }
  }
}
</script>

<style scoped>
.reservation-management {
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

.pagination {
  margin-top: 20px;
  text-align: right;
}

.data-card {
  padding: 16px 16px 20px;
}
</style>