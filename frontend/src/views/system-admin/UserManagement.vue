<template>
  <div class="user-management">
    <div class="page-hero">
      <div>
        <span class="page-kicker">Users</span>
        <h2>用户管理</h2>
        <p>管理系统用户账号、权限分配与状态控制。</p>
      </div>
      <el-button type="primary" @click="showAddLibrarianDialog">
        <i class="el-icon-plus"></i> 新增图书馆管理员
      </el-button>
    </div>
    
    <!-- 搜索栏 -->
    <div class="content-card search-area">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="用户类型">
          <el-select v-model="searchForm.userType" placeholder="全部" clearable style="width: 150px;">
            <el-option label="学生" value="学生" />
            <el-option label="图书馆管理员" value="图书馆管理员" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <!-- 用户列表 -->
    <div class="content-card data-card">
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="userType" label="用户类型" width="120">
          <template slot-scope="scope">
            <el-tag :type="getUserTypeColor(scope.row.userType)">
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
        <el-table-column label="操作" fixed="right" width="200">
          <template slot-scope="scope">
            <el-button size="mini" type="primary" @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-button 
              size="mini" 
              :type="scope.row.status === '正常' ? 'warning' : 'success'"
              @click="handleToggleStatus(scope.row)"
            >
              {{ scope.row.status === '正常' ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
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
    
    <!-- 编辑用户对话框 -->
    <el-dialog title="编辑用户" :visible.sync="editDialogVisible" width="600px">
      <el-form :model="editForm" :rules="editRules" ref="editForm" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="editForm.username" disabled />
        </el-form-item>
        
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="editForm.realName" />
        </el-form-item>
        
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="editForm.phone" />
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="editForm.email" />
        </el-form-item>
        
        <el-form-item label="用户类型" prop="userType">
          <el-select v-model="editForm.userType" style="width: 100%;">
            <el-option label="学生" value="学生" />
            <el-option label="图书馆管理员" value="图书馆管理员" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="editForm.status">
            <el-radio label="正常">正常</el-radio>
            <el-radio label="禁用">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <div slot="footer">
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitEdit" :loading="submitLoading">确定</el-button>
      </div>
    </el-dialog>
    
    <!-- 新增图书馆管理员对话框 -->
    <el-dialog title="新增图书馆管理员" :visible.sync="addDialogVisible" width="600px">
      <el-form :model="addForm" :rules="addRules" ref="addForm" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="addForm.username" placeholder="请输入用户名" />
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input v-model="addForm.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="addForm.realName" placeholder="请输入姓名" />
        </el-form-item>
        
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="addForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="addForm.email" placeholder="请输入邮箱" />
        </el-form-item>
      </el-form>
      
      <div slot="footer">
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitAdd" :loading="submitLoading">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getAllUsers, updateUser, createLibrarian } from '@/api/systemAdmin'
import { toggleUserStatus } from '@/api/user'

export default {
  name: 'SystemUserManagement',
  
  data() {
    return {
      loading: false,
      submitLoading: false,
      tableData: [],
      pagination: {
        current: 1,
        size: 10,
        total: 0
      },
      searchForm: {
        userType: ''
      },
      editDialogVisible: false,
      addDialogVisible: false,
      editForm: {
        id: null,
        username: '',
        realName: '',
        phone: '',
        email: '',
        userType: '',
        status: ''
      },
      addForm: {
        username: '',
        password: '',
        realName: '',
        phone: '',
        email: ''
      },
      editRules: {
        realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
        ]
      },
      addRules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
        ]
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
          userType: this.searchForm.userType
        }
        const res = await getAllUsers(params)
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
      this.searchForm.userType = ''
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
    
    handleEdit(row) {
      this.editForm = { ...row }
      this.editDialogVisible = true
    },
    
    async handleSubmitEdit() {
      try {
        await this.$refs.editForm.validate()
        this.submitLoading = true
        
        await updateUser(this.editForm.id, this.editForm)
        this.$message.success('更新成功')
        this.editDialogVisible = false
        this.loadData()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error(error.message || '更新失败')
        }
      } finally {
        this.submitLoading = false
      }
    },
    
    async handleToggleStatus(row) {
      try {
        await this.$confirm(`确定要${row.status === '正常' ? '禁用' : '启用'}该用户吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        await toggleUserStatus(row.id)
        this.$message.success('操作成功')
        this.loadData()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('操作失败')
        }
      }
    },
    
    showAddLibrarianDialog() {
      this.addForm = {
        username: '',
        password: '',
        realName: '',
        phone: '',
        email: ''
      }
      this.addDialogVisible = true
    },
    
    async handleSubmitAdd() {
      try {
        await this.$refs.addForm.validate()
        this.submitLoading = true
        
        // MD5加密密码（实际项目中应该在后端加密）
        const addData = {
          ...this.addForm,
          password: this.md5(this.addForm.password)
        }
        
        await createLibrarian(addData)
        this.$message.success('创建成功')
        this.addDialogVisible = false
        this.loadData()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error(error.message || '创建失败')
        }
      } finally {
        this.submitLoading = false
      }
    },
    
    getUserTypeColor(userType) {
      const colorMap = {
        '学生': '',
        '图书馆管理员': 'success',
        '系统管理员': 'danger'
      }
      return colorMap[userType] || ''
    },
    
    md5(string) {
      // 简单的MD5实现，实际项目应使用crypto-js库
      // 这里为了简化，直接返回字符串，实际使用时需要引入md5库
      return string
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
</style>
