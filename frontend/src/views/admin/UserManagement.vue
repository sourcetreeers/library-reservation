<template>
  <div class="user-management">
    <div class="page-hero">
      <div>
        <span class="page-kicker">Users</span>
        <h2>用户管理</h2>
        <p>查看账户信息、管理权限和账号状态，保持后台用户体系整洁可控。</p>
      </div>
    </div>
    
    <div class="content-card search-area">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable style="width: 150px;" />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="searchForm.realName" placeholder="请输入真实姓名" clearable style="width: 150px;" />
        </el-form-item>
        <el-form-item label="用户类型">
          <el-select v-model="searchForm.userType" placeholder="请选择用户类型" clearable style="width: 120px;" v-if="isSystemAdmin">
            <el-option label="学生" value="学生" />
            <el-option label="图书馆管理员" value="图书馆管理员" />
            <el-option label="系统管理员" value="系统管理员" />
          </el-select>
          <span v-else style="color: #909399;">学生</span>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 120px;">
            <el-option label="正常" value="正常" />
            <el-option label="禁用" value="禁用" />
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
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="realName" label="真实姓名" width="120" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column prop="email" label="邮箱" width="180" />
      <el-table-column prop="userType" label="用户类型" width="100">
        <template slot-scope="scope">
          <el-tag :type="getUserTypeTag(scope.row.userType)">
            {{ scope.row.userType }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === '正常' ? 'success' : 'danger'">
            {{ scope.row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="注册时间" width="180" />
      <el-table-column label="操作" width="220">
        <template slot-scope="scope">
          <el-button
            size="mini"
            :type="scope.row.status === '正常' ? 'danger' : 'success'"
            @click="handleToggleStatus(scope.row)"
            :disabled="!canOperate(scope.row)"
          >
            {{ scope.row.status === '正常' ? '禁用' : '启用' }}
          </el-button>
          <el-button
            v-if="isSystemAdmin"
            size="mini"
            type="warning"
            @click="handleChangeUserType(scope.row)"
          >
            改类型
          </el-button>
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
import { getUserPage, toggleUserStatus, changeUserType } from '@/api/user'

export default {
  name: 'UserManagement',
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
        username: '',
        realName: '',
        userType: '',
        status: ''
      }
    }
  },
  
  computed: {
    isSystemAdmin() {
      const user = this.$store.state.user
      return user && user.userType === '系统管理员'
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
          username: this.searchForm.username,
          realName: this.searchForm.realName,
          userType: this.isSystemAdmin ? this.searchForm.userType : '学生',
          status: this.searchForm.status
        }
        const res = await getUserPage(params)
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
      this.searchForm.username = ''
      this.searchForm.realName = ''
      this.searchForm.userType = ''
      this.searchForm.status = ''
      this.pagination.current = 1
      this.loadData()
    },

    async exportData() {
      try {
        this.$message.info('正在导出，请稍候...')
        const res = await fetch('/api/admin/export/users', { credentials: 'include' })
        if (!res.ok) throw new Error('导出请求失败: ' + res.status)
        const blob = await res.blob()
        if (blob.size === 0) throw new Error('导出文件为空')
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = '用户列表.xlsx'
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
    
    canOperate(row) {
      // 图书馆管理员只能操作学生
      if (!this.isSystemAdmin && row.userType !== '学生') {
        return false
      }
      return true
    },
    
    getUserTypeTag(type) {
      const tagMap = {
        '学生': 'primary',
        '图书馆管理员': 'warning',
        '系统管理员': 'danger'
      }
      return tagMap[type] || 'info'
    },
    
    async handleToggleStatus(row) {
      const action = row.status === '正常' ? '禁用' : '启用'
      try {
        await this.$confirm(`确定要${action}用户"${row.realName}"吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        await toggleUserStatus(row.id)
        this.$message.success(`${action}成功`)
        this.loadData()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error(`${action}失败`)
        }
      }
    },
    
    async handleChangeUserType(row) {
      const newUserType = row.userType === '学生' ? '管理员' : '学生'
      try {
        await this.$confirm(
          `确定要将用户"${row.realName}"的类型修改为【${newUserType}】吗？`, 
          '修改用户类型', 
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        await changeUserType(row.id, newUserType)
        this.$message.success('用户类型修改成功')
        this.loadData()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('用户类型修改失败')
        }
      }
    }
  }
}
</script>

<style scoped>
.user-management {
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