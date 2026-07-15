<template>
  <div class="banner-management">
    <div class="page-hero">
      <div>
        <span class="page-kicker">Banners</span>
        <h2>轮播图管理</h2>
        <p>管理首页轮播图的展示内容。</p>
      </div>
      <el-button type="primary" icon="el-icon-plus" round @click="showAddDialog">添加轮播图</el-button>
    </div>

    <div class="content-card search-area">

      <!-- 提示信息 -->
      <el-alert
        title="轮播图建议尺寸：750 x 360 像素，支持 jpg/png 格式，大小不超过 2MB"
        type="info"
        :closable="false"
        show-icon
        style="margin-bottom: 16px"
      />

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="标题">
          <el-input v-model="searchForm.title" placeholder="请输入标题" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="启用" value="启用" />
            <el-option label="禁用" value="禁用" />
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
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
        <el-table-column label="图片预览" width="160">
          <template slot-scope="scope">
            <el-image
              :src="scope.row.imageUrl"
              :preview-src-list="[scope.row.imageUrl]"
              style="width: 120px; height: 58px; border-radius: 4px"
              fit="cover"
              :z-index="3000"
            >
              <div slot="error" class="image-error">
                <i class="el-icon-picture-outline"></i>
              </div>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80" align="center">
          <template slot-scope="scope">
            <el-tag size="mini">{{ scope.row.sortOrder }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template slot-scope="scope">
            <el-switch
              v-model="scope.row.status"
              active-value="启用"
              inactive-value="禁用"
              active-color="#67c23a"
              @change="(val) => handleToggleStatus(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="150" fixed="right" align="center">
          <template slot-scope="scope">
            <el-button
              type="text"
              icon="el-icon-edit"
              @click="showEditDialog(scope.row)"
            >编辑</el-button>
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
      width="560px"
      @close="resetForm"
      :close-on-click-modal="false"
    >
      <el-form :model="form" :rules="rules" ref="bannerForm" label-width="90px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入轮播图标题（选填，用于后台识别）" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="上传图片" prop="imageUrl" required>
          <el-upload
            class="banner-uploader"
            action=""
            :auto-upload="false"
            :show-file-list="false"
            accept="image/jpeg,image/jpg,image/png,image/webp"
            :on-change="handleImageChange"
            :before-upload="beforeImageUpload"
          >
            <div v-if="form.imageUrl || previewUrl" class="upload-preview-wrapper">
              <img :src="previewUrl || form.imageUrl" class="preview-image" />
              <div class="preview-mask">
                <i class="el-icon-zoom-in" @click.stop="handlePreviewImage"></i>
                <i class="el-icon-delete" @click.stop="handleRemoveImage"></i>
              </div>
            </div>
            <div v-else class="upload-placeholder">
              <i class="el-icon-plus uploader-icon"></i>
              <div class="uploader-text">点击上传图片</div>
              <div class="uploader-hint">建议 750x360 px</div>
            </div>
          </el-upload>
          <div class="upload-tip">支持 jpg/png/webp 格式，不超过 2MB</div>
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" :max="999" controls-position="right" />
          <span class="field-hint">数字越小越靠前</span>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio label="启用">
              <i class="el-icon-open" style="color:#67c23a;margin-right:4px"></i>启用
            </el-radio>
            <el-radio label="禁用">
              <i class="el-icon-turn-off" style="color:#c0c4cc;margin-right:4px"></i>禁用
            </el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false" plain>取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitLoading">
          {{ form.id ? '保存修改' : '添加' }}
        </el-button>
      </div>
    </el-dialog>

    <!-- 图片大图预览 -->
    <el-dialog title="图片预览" :visible.sync="previewDialogVisible" width="600px" append-to-body>
      <div style="text-align:center">
        <img :src="previewImageUrl" style="max-width:100%;border-radius:8px" />
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  getBannerPage,
  addBanner,
  updateBanner,
  deleteBanner,
  toggleBannerStatus
} from '@/api/banner'

export default {
  name: 'BannerManagement',
  data() {
    return {
      loading: false,
      submitLoading: false,
      tableData: [],
      pagination: { current: 1, size: 10, total: 0 },
      searchForm: { title: '', status: '' },
      dialogVisible: false,
      dialogTitle: '添加轮播图',
      previewUrl: '', // 本地预览（未保存时）
      rawFile: null, // 原始文件对象
      previewDialogVisible: false,
      previewImageUrl: '',
      form: {
        id: null, title: '', imageUrl: '', sortOrder: 0, status: '启用'
      },
      rules: {
        imageUrl: [
          { required: true, message: '请上传轮播图片', trigger: 'change', validator: (rule, value, cb) => {
            if (!this.form.imageUrl && !this.previewUrl) cb(new Error('请上传轮播图片'))
            else cb()
          }}
        ]
      }
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
        const res = await getBannerPage(params)
        this.tableData = res.data.records || []
        this.pagination.total = res.data.total || 0
      } catch (e) {
        this.$message.error('加载失败')
      } finally {
        this.loading = false
      }
    },

    showAddDialog() {
      this.dialogTitle = '添加轮播图'
      this.dialogVisible = true
    },

    showEditDialog(row) {
      this.dialogTitle = '编辑轮播图'
      this.form = { ...row }
      this.previewUrl = ''
      this.rawFile = null
      this.dialogVisible = true
    },

    resetForm() {
      this.form = { id: null, title: '', imageUrl: '', sortOrder: 0, status: '启用' }
      this.previewUrl = ''
      this.rawFile = null
      this.$refs.bannerForm && this.$refs.bannerForm.clearValidate()
    },

    // 图片选择变化
    handleImageChange(file) {
      if (file.raw) {
        this.rawFile = file.raw
        this.previewUrl = URL.createObjectURL(file.raw)
        this.$refs.bannerForm && this.$refs.bannerForm.clearValidate()
      }
    },

    beforeImageUpload(file) {
      const isImage = ['image/jpeg', 'image/jpg', 'image/png', 'image/webp'].includes(file.type)
      const isLt2M = file.size / 1024 / 1024 < 2
      if (!isImage) {
        this.$message.error('仅支持 JPG/PNG/WebP 格式')
        return false
      }
      if (!isLt2M) {
        this.$message.error('图片大小不能超过 2MB')
        return false
      }
      return true
    },

    handlePreviewImage() {
      this.previewImageUrl = this.previewUrl || this.form.imageUrl
      this.previewDialogVisible = true
    },

    handleRemoveImage() {
      if (this.previewUrl) URL.revokeObjectURL(this.previewUrl)
      this.previewUrl = ''
      this.rawFile = null
      this.form.imageUrl = ''
    },

    async submitForm() {
      try {
        await this.$refs.bannerForm.validate()
        this.submitLoading = true

        // 如果有新选择的文件，转换为 base64 存储（适配当前无独立文件服务器）
        let submitData = { ...this.form }
        if (this.rawFile) {
          submitData.imageUrl = await this.fileToBase64(this.rawFile)
        }

        if (this.form.id) {
          await updateBanner(submitData)
          this.$message.success('更新成功')
        } else {
          await addBanner(submitData)
          this.$message.success('添加成功')
        }

        this.dialogVisible = false
        this.loadData()
      } catch (error) {
        if (error !== false) this.$message.error('操作失败')
      } finally {
        this.submitLoading = false
      }
    },

    fileToBase64(file) {
      return new Promise((resolve, reject) => {
        const reader = new FileReader()
        reader.onload = () => resolve(reader.result)
        reader.onerror = reject
        reader.readAsDataURL(file)
      })
    },

    async handleToggleStatus(row) {
      try {
        await toggleBannerStatus(row.id)
        this.$message.success((row.status === '启用' ? '已启用' : '已禁用'))
        this.loadData()
      } catch (e) {
        this.$message.error('操作失败')
        row.status = row.status === '启用' ? '禁用' : '启用'
      }
    },

    async handleDelete(row) {
      try {
        await this.$confirm('确定要删除该轮播图吗？', '提示', { type: 'warning' })
        await deleteBanner(row.id)
        this.$message.success('删除成功')
        this.loadData()
      } catch (e) {
        if (e !== 'cancel') this.$message.error('删除失败')
      }
    },

    resetSearch() {
      this.searchForm = { title: '', status: '' }
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
.banner-management { display: grid; gap: 18px; }

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
  color: #007AFF;
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

.image-error {
  width: 120px; height: 58px;
  background: #f5f7fa; border-radius: 4px;
  display: flex; align-items: center; justify-content: center;
  color: #c0c4cc; font-size: 24px;
}

/* 上传组件样式 */
.banner-uploader { display: inline-block; vertical-align: top; }
.banner-uploader :deep(.el-upload) {
  border: 1px dashed #d9d9d9;
  border-radius: 8px; cursor: pointer;
  position: relative; overflow: hidden;
  transition: all .25s;
}
.banner-uploader :deep(.el-upload:hover) { border-color: #409eff; }

.upload-preview-wrapper {
  position: relative; width: 320px; height: 154px; overflow: hidden; border-radius: 8px;
}
.upload-preview-wrapper .preview-image {
  width: 100%; height: 100%; object-fit: cover; display: block;
}
.preview-mask {
  position: absolute; top:0; left:0; right:0; bottom:0;
  background: rgba(0,0,0,.45); opacity: 0;
  display: flex; align-items: center; justify-content: center; gap: 16px;
  transition: opacity .25s;
  color: #fff; font-size: 22px;
}
.upload-preview-wrapper:hover .preview-mask { opacity: 1; }
.preview-mask i { cursor: pointer; padding: 4px; border-radius: 50%; }
.preview-mask i:hover { background: rgba(255,255,255,.2); }

.upload-placeholder {
  width: 320px; height: 154px;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  color: #8c939d;
}
.uploader-icon { font-size: 28px; color: #8c939d; margin-bottom: 6px; }
.uploader-text { font-size: 14px; color: #606266; margin-bottom: 4px; }
.uploader-hint { font-size: 12px; color: #c0c4cc; }

.upload-tip { font-size: 12px; color: #909399; margin-top: 6px; line-height: 1.5; }
.field-hint { margin-left: 10px; color: #999; font-size: 12px; }
.dialog-footer { text-align: right; }
</style>
