<template>
  <div class="points-records">
    <div class="page-hero">
      <div>
        <span class="page-kicker">Points</span>
        <h2>积分变动记录总览</h2>
        <p>查看所有用户的积分变动历史，追踪信用分增减详情。</p>
      </div>
    </div>
    
    <!-- 搜索栏 -->
    <div class="content-card search-area">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="用户ID">
          <el-input 
            v-model="searchForm.userId" 
            placeholder="输入用户ID" 
            clearable
            style="width: 150px;"
          />
        </el-form-item>
        
        <el-form-item label="变动类型">
          <el-select v-model="searchForm.changeType" placeholder="全部" clearable style="width: 150px;">
            <el-option label="正常履约" value="正常履约" />
            <el-option label="提前签退" value="提前签退" />
            <el-option label="爽约" value="爽约" />
            <el-option label="暂离超时" value="暂离超时" />
            <el-option label="占座" value="占座" />
            <el-option label="系统调整" value="系统调整" />
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <!-- 记录列表 -->
    <div class="content-card data-card">
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="studentNo" label="学工号" width="120" />
        <el-table-column prop="changeType" label="变动类型" width="120">
          <template slot-scope="scope">
            <el-tag :type="getChangeTypeColor(scope.row.changeType)">
              {{ scope.row.changeType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="pointsChange" label="积分变动" width="100" align="center">
          <template slot-scope="scope">
            <span :class="scope.row.pointsChange > 0 ? 'points-add' : 'points-deduct'">
              {{ scope.row.pointsChange > 0 ? '+' : '' }}{{ scope.row.pointsChange }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="pointsBefore" label="变动前" width="100" align="center" />
        <el-table-column prop="pointsAfter" label="变动后" width="100" align="center" />
        <el-table-column prop="sourceType" label="来源" width="120" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createTime" label="时间" width="180" />
      </el-table>
      
      <!-- 分页 -->
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
import { getAllPointsRecords } from '@/api/systemAdmin'

export default {
  name: 'PointsRecordsOverview',
  
  data() {
    return {
      loading: false,
      tableData: [],
      pagination: {
        current: 1,
        size: 10,
        total: 0
      },
      searchForm: {
        userId: '',
        changeType: ''
      }
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
          userId: this.searchForm.userId || undefined,
          changeType: this.searchForm.changeType || undefined
        }
        
        const res = await getAllPointsRecords(params)
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
      this.searchForm.userId = ''
      this.searchForm.changeType = ''
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
    
    getChangeTypeColor(changeType) {
      if (changeType.includes('履约') || changeType.includes('签退')) {
        return 'success'
      } else if (changeType === '系统调整') {
        return 'warning'
      } else {
        return 'danger'
      }
    }
  }
}
</script>

<style scoped>
.points-records {
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

.points-add {
  color: #34C759;
  font-weight: bold;
}

.points-deduct {
  color: #FF3B30;
  font-weight: bold;
}
</style>
