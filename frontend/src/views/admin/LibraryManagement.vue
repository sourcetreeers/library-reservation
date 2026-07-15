<template>
  <div class="library-management">
    <div class="page-hero">
      <div>
        <span class="page-kicker">Library Spaces</span>
        <h2>图书室管理</h2>
        <p>统一维护图书室信息、状态与基础展示资料。</p>
      </div>
      <el-button type="primary" @click="showAddDialog">新增图书室</el-button>
    </div>
    
    <div class="content-card search-area">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="图书室名称">
          <el-input v-model="searchForm.name" placeholder="请输入图书室名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <div class="content-card data-card">
      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="图书室名称" />
        <el-table-column prop="address" label="地址" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === '正常' ? 'success' : 'danger'">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <el-button size="mini" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="mini" type="danger" @click="handleDelete(scope.row)">删除</el-button>
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
    
    <!-- 新增/编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="500px"
      @close="resetForm"
    >
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="图书室名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入图书室名称" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入描述"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option label="正常" value="正常" />
            <el-option label="停用" value="停用" />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getLibraryPage, createLibrary, updateLibrary, deleteLibrary } from '@/api/library'

export default {
  name: 'LibraryManagement',
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
        name: ''
      },
      dialogVisible: false,
      dialogTitle: '',
      isEdit: false,
      submitLoading: false,
      form: {
        id: null,
        name: '',
        address: '',
        description: '',
        status: '正常'
      },
      rules: {
        name: [
          { required: true, message: '请输入图书室名称', trigger: 'blur' }
        ],
        status: [
          { required: true, message: '请选择状态', trigger: 'change' }
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
          name: this.searchForm.name
        }
        const res = await getLibraryPage(params)
        console.log('图书室列表响应:', res)
        console.log('res.data:', res.data)
        this.tableData = res.data.records || []
        this.pagination.total = res.data.total || 0
      } catch (error) {
        console.error('加载图书室失败:', error)
        this.$message.error('加载数据失败: ' + error.message)
      } finally {
        this.loading = false
      }
    },
    
    handleSearch() {
      this.pagination.current = 1
      this.loadData()
    },
    
    handleReset() {
      this.searchForm.name = ''
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
    
    showAddDialog() {
      this.dialogTitle = '新增图书室'
      this.isEdit = false
      this.dialogVisible = true
    },
    
    handleEdit(row) {
      this.dialogTitle = '编辑图书室'
      this.isEdit = true
      this.form = { ...row }
      this.dialogVisible = true
    },
    
    async handleDelete(row) {
      try {
        await this.$confirm('确定要删除这个图书室吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        await deleteLibrary(row.id)
        this.$message.success('删除成功')
        this.loadData()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('删除失败')
        }
      }
    },
    
    handleSubmit() {
      this.$refs.form.validate(async (valid) => {
        if (valid) {
          this.submitLoading = true
          try {
            if (this.isEdit) {
              await updateLibrary(this.form.id, this.form)
              this.$message.success('更新成功')
            } else {
              await createLibrary(this.form)
              this.$message.success('新增成功')
            }
            this.dialogVisible = false
            this.loadData()
          } catch (error) {
            this.$message.error(this.isEdit ? '更新失败' : '新增失败')
          } finally {
            this.submitLoading = false
          }
        }
      })
    },
    
    resetForm() {
      this.form = {
        id: null,
        name: '',
        address: '',
        description: '',
        status: '正常'
      }
      if (this.$refs.form) {
        this.$refs.form.resetFields()
      }
    }
  }
}
</script>

<style scoped>
.library-management {
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