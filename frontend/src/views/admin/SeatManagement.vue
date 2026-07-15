<template>
  <div class="seat-management">
    <div class="page-hero">
      <div>
        <span class="page-kicker">Seats</span>
        <h2>座位管理</h2>
        <p>维护座位类型、状态与批量生成规则，保证配置清晰一致。</p>
      </div>
      <div class="header-buttons">
        <el-button type="primary" @click="showAddDialog">新增座位</el-button>
        <el-button type="warning" @click="showVisualAddDialog">可视化新增座位</el-button>
        <el-button type="success" @click="showBatchAddDialog">批量新增座位</el-button>
        <el-button type="danger" @click="showRenumberDialog" plain>重排编号</el-button>
        <el-button type="danger" @click="handleBatchDelete" :disabled="selectedRows.length === 0" plain>
          批量删除 ({{ selectedRows.length }})
        </el-button>
      </div>
    </div>
    
    <div class="content-card search-area">
      <el-form :inline="true" :model="searchForm">
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
        <el-form-item label="座位编号">
          <el-input v-model="searchForm.seatNumber" placeholder="请输入座位编号" clearable style="width: 120px;" />
        </el-form-item>
        <el-form-item label="座位类型">
          <el-select v-model="searchForm.seatType" placeholder="请选择类型" clearable style="width: 120px;">
            <el-option label="普通座位" value="普通座位" />
            <el-option label="电脑座位" value="电脑座位" />
            <el-option label="静音座位" value="静音座位" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 100px;">
            <el-option label="正常" value="正常" />
            <el-option label="维修" value="维修" />
            <el-option label="停用" value="停用" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <div class="content-card data-card">
    <el-table :data="tableData" v-loading="loading" border @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" />
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="libraryId" label="图书馆" width="150">
        <template slot-scope="scope">
          {{ getLibraryName(scope.row.libraryId) }}
        </template>
      </el-table-column>
      <el-table-column prop="seatNumber" label="座位编号" />
      <el-table-column prop="seatType" label="座位类型" width="120">
        <template slot-scope="scope">
          <el-tag 
            :type="getSeatTypeTagType(scope.row.seatType)"
            :icon="getSeatTypeIcon(scope.row.seatType)"
          >
            {{ scope.row.seatType }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="rowNum" label="行号" width="80" />
      <el-table-column prop="colNum" label="列号" width="80" />
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
        <el-form-item label="图书馆" prop="libraryId">
          <el-select v-model="form.libraryId" placeholder="请选择图书馆" filterable>
            <el-option
              v-for="library in libraries"
              :key="library.id"
              :label="library.name"
              :value="library.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="座位编号" prop="seatNumber">
          <el-input v-model="form.seatNumber" placeholder="请输入座位编号" />
        </el-form-item>
        <el-form-item label="座位类型" prop="seatType">
          <el-select v-model="form.seatType" placeholder="请选择座位类型">
            <el-option label="普通座位" value="普通座位" />
            <el-option label="电脑座位" value="电脑座位" />
            <el-option label="静音座位" value="静音座位" />
          </el-select>
        </el-form-item>
        <el-form-item label="行号" prop="rowNum">
          <el-input-number v-model="form.rowNum" :min="0" :max="99" placeholder="行号" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="列号" prop="colNum">
          <el-input-number v-model="form.colNum" :min="0" :max="99" placeholder="列号" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option label="正常" value="正常" />
            <el-option label="维修" value="维修" />
            <el-option label="停用" value="停用" />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </div>
    </el-dialog>
    
    <!-- 批量新增对话框 -->
    <el-dialog
      title="批量新增座位"
      :visible.sync="batchDialogVisible"
      width="600px"
      @close="resetBatchForm"
    >
      <el-form ref="batchForm" :model="batchForm" :rules="batchRules" label-width="120px">
        <el-form-item label="图书馆" prop="libraryId">
          <el-select v-model="batchForm.libraryId" placeholder="请选择图书馆" filterable style="width: 100%;">
            <el-option
              v-for="library in libraries"
              :key="library.id"
              :label="library.name"
              :value="library.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="座位编号区间">
          <el-row :gutter="10">
            <el-col :span="11">
              <el-form-item prop="startNumber">
                <el-input-number 
                  v-model="batchForm.startNumber" 
                  :min="1" 
                  :max="9999"
                  placeholder="起始编号"
                  style="width: 100%;"
                  @change="updatePreview"
                />
              </el-form-item>
            </el-col>
            <el-col :span="2" style="text-align: center; line-height: 40px;">
              至
            </el-col>
            <el-col :span="11">
              <el-form-item prop="endNumber">
                <el-input-number 
                  v-model="batchForm.endNumber" 
                  :min="batchForm.startNumber + 1" 
                  :max="9999"
                  placeholder="结束编号"
                  style="width: 100%;"
                  @change="updatePreview"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form-item>
        
        <el-form-item label="座位类型" prop="seatType">
          <el-select v-model="batchForm.seatType" placeholder="请选择座位类型" style="width: 100%;">
            <el-option label="普通座位" value="普通座位" />
            <el-option label="电脑座位" value="电脑座位" />
            <el-option label="静音座位" value="静音座位" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-select v-model="batchForm.status" placeholder="请选择状态" style="width: 100%;">
            <el-option label="正常" value="正常" />
            <el-option label="维修" value="维修" />
            <el-option label="停用" value="停用" />
          </el-select>
        </el-form-item>
        
        <!-- 预览区域 -->
        <el-form-item label="预览" v-if="previewSeats.length > 0">
          <div class="preview-area">
            <div class="preview-info">
              <span>将生成 {{ previewSeats.length }} 个座位：</span>
            </div>
            <div class="preview-seats">
              <el-tag 
                v-for="seat in previewSeats.slice(0, 10)" 
                :key="seat"
                size="small"
                style="margin: 2px;"
              >
                {{ seat }}
              </el-tag>
              <span v-if="previewSeats.length > 10" class="more-indicator">
                ... 还有 {{ previewSeats.length - 10 }} 个
              </span>
            </div>
          </div>
        </el-form-item>
      </el-form>
      
      <div slot="footer">
        <el-button @click="batchDialogVisible = false">取消</el-button>
        <el-button 
          type="primary" 
          @click="handleBatchSubmit" 
          :loading="batchSubmitLoading"
          :disabled="previewSeats.length === 0"
        >
          批量新增 ({{ previewSeats.length }}个)
        </el-button>
      </div>
    </el-dialog>

    <!-- 重排编号对话框 -->
    <el-dialog
      title="重排座位编号"
      :visible.sync="renumberDialogVisible"
      width="480px"
      @close="resetRenumberForm"
    >
      <div class="renumber-container">
        <el-alert
          title="重排后，该图书室所有座位将按行列顺序从 001 开始重新编号（不可撤销）"
          type="warning"
          :closable="false"
          show-icon
          style="margin-bottom: 18px;"
        />
        <el-form :model="renumberForm" label-width="100px">
          <el-form-item label="选择图书室" required>
            <el-select v-model="renumberForm.libraryId" placeholder="请选择要重排的图书室" filterable style="width: 100%;">
              <el-option
                v-for="library in libraries"
                :key="library.id"
                :label="library.name"
                :value="library.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="编号前缀">
            <el-input v-model="renumberForm.prefix" placeholder="留空则使用纯数字如 001、002" maxlength="5">
              <template slot="append">可选</template>
            </el-input>
          </el-form-item>
          <el-form-item label="预览">
            <div class="renumber-preview" v-if="renumberPreview.length > 0">
              <div class="preview-row" v-for="(row, idx) in renumberPreview" :key="idx">
                <span class="preview-row-label">{{ idx + 1 }}排：</span>
                <el-tag size="mini" v-for="(seat, ci) in row" :key="ci" style="margin:2px;" :type="seat.changed ? 'danger' : 'info'">
                  {{ seat.oldNumber }} &rarr; {{ seat.newNumber }}
                </el-tag>
              </div>
              <div style="margin-top:8px;font-size:12px;color:#999;">
                共 {{ renumberTotalCount }} 个座位，其中 <strong style="color:#f56c6c">{{ renumberChangedCount }}</strong> 个编号会变更
              </div>
            </div>
            <div v-else style="color:#999;font-size:13px;padding:10px 0;">
              请先选择图书室查看预览
            </div>
          </el-form-item>
        </el-form>
      </div>
      <div slot="footer">
        <el-button @click="renumberDialogVisible = false">取消</el-button>
        <el-button @click="loadRenumberPreview" type="primary" plain :loading="renumberPreviewLoading" :disabled="!renumberForm.libraryId">刷新预览</el-button>
        <el-button type="danger" @click="handleRenumberSubmit" :loading="renumberSubmitLoading" :disabled="renumberPreview.length === 0 || !renumberForm.libraryId || renumberChangedCount === 0">
          确认重排 ({{ renumberChangedCount }}个)
        </el-button>
      </div>
    </el-dialog>

    <!-- 可视化新增座位对话框 -->
    <el-dialog
      title="可视化新增座位"
      :visible.sync="visualDialogVisible"
      width="860px"
      @close="resetVisualForm"
    >
      <div class="visual-add-container">
        <!-- 配置区域 -->
        <div class="visual-config">
          <el-form ref="visualForm" :model="visualForm" label-width="100px" size="small">
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="图书室" required>
                  <el-select v-model="visualForm.libraryId" placeholder="请选择图书室" filterable style="width:100%;" @change="onVisualLibraryChange">
                    <el-option
                      v-for="library in libraries"
                      :key="library.id"
                      :label="library.name"
                      :value="library.id"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="默认类型">
                  <el-select v-model="visualForm.seatType" style="width:100%;">
                    <el-option label="普通座位" value="普通座位" />
                    <el-option label="电脑座位" value="电脑座位" />
                    <el-option label="静音座位" value="静音座位" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="编号前缀">
                  <el-input v-model="visualForm.numberPrefix" placeholder="如 A、空" maxlength="5" style="width:100%;" />
                </el-form-item>
              </el-col>
            </el-row>

            <div class="visual-actions-bar">
              <el-button size="mini" icon="el-icon-plus" type="primary" plain @click="addRow">+ 增加一排</el-button>
              <el-button size="mini" icon="el-icon-minus" type="danger" plain @click="removeRow" :disabled="hasAnyExistingSeat">- 减少一排</el-button>
              <div style="flex:1;"></div>
              <el-button size="mini" type="success" plain icon="el-icon-check" @click="selectAllSeats">全选</el-button>
              <el-button size="mini" type="warning" plain icon="el-icon-close" @click="deselectAllSeats">全不选</el-button>
              <span class="selected-count">
                已选 <strong>{{ visualSelectedCount }}</strong> / 共 {{ visualTotalCount }} 格
              </span>
              <el-switch
                v-model="showExistingSeats"
                active-text="显示已有座位"
                inactive-text=""
                size="small"
                @change="loadExistingSeats"
              />
            </div>
          </el-form>
        </div>
        
        <!-- 分隔线 -->
        <div class="visual-divider">
          <span>座位平面预览 — 每行可单独调整列数（点击格子切换是否生成）</span>
        </div>

        <!-- 网格预览区域（可变行列） -->
        <div class="visual-grid-wrapper" ref="visualGridWrapper">
          <div class="visual-grid-scroll">
            <!-- 每一行 -->
            <div v-for="(rowCells, ri) in visualGrid" :key="'r'+ri" class="vgrid-row">
              <!-- 行标签 + 列数控制 -->
              <div class="vgrid-row-header">
                <span class="vgrid-row-label">{{ ri + 1 }}排</span>
                <div class="vgrid-row-controls">
                  <el-button size="mini" circle icon="el-icon-minus" @click="changeRowCols(ri, -1)" :disabled="getRowColCount(ri) <= 1 || rowHasExistingSeat(ri)"></el-button>
                  <span class="col-count-badge">{{ getRowColCount(ri) }}列</span>
                  <el-button size="mini" circle icon="el-icon-plus" @click="changeRowCols(ri, 1)" :disabled="getRowColCount(ri) >= 15"></el-button>
                </div>
              </div>
              
              <!-- 该行的所有格子 -->
              <div class="vgrid-cells">
                <div
                  v-for="(cell, ci) in rowCells"
                  :key="'c'+ri+'-'+ci"
                  class="vgrid-cell"
                  :class="getCellClass(ri, ci)"
                  :title="getCellTitle(ri, ci)"
                  @click="toggleCell(ri, ci)"
                >
                  <span class="cell-number">{{ getCellNumber(ri, ci) }}</span>
                  <span v-if="isExistingSeat(ri, ci)" class="cell-badge existing-badge">已有</span>
                  <span v-if="isSelected(ri, ci) && !isExistingSeat(ri, ci)" class="cell-badge select-badge">新增</span>
                </div>
                
                <!-- 占位：保持最小宽度对齐 -->
                <div v-if="getRowColCount(ri) === 0" class="vgrid-cell-empty-hint">该排暂无座位</div>
              </div>
            </div>
            
            <!-- 空状态 -->
            <div v-if="visualGrid.length === 0" class="vgrid-empty">
              <i class="el-icon-grid" style="font-size:36px;color:#ddd;margin-bottom:10px;"></i>
              <p>暂无排数，点击上方「+ 增加一排」开始配置</p>
            </div>
          </div>
        </div>
        
        <!-- 已有座位提示 -->
        <div v-if="existingSeatsList.length > 0" class="existing-seats-hint">
          <i class="el-icon-info"></i>
          该图书室已存在 <strong>{{ existingSeatsList.length }}</strong> 个座位，灰色标记为已有座位（不可重复创建）
        </div>
      </div>
      
      <div slot="footer">
        <el-button @click="visualDialogVisible = false">取消</el-button>
        <el-button 
          type="warning" 
          @click="handleVisualSubmit" 
          :loading="visualSubmitLoading"
          :disabled="visualSelectedCount === 0 || !visualForm.libraryId"
        >
          确认新增 ({{ visualSelectedCount }}个)
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getSeatPage, createSeat, updateSeat, deleteSeat, getSeatsByLibraryId } from '@/api/seat'
import { getLibraryList } from '@/api/library'

export default {
  name: 'SeatManagement',
  data() {
    return {
      loading: false,
      tableData: [],
      libraries: [],
      selectedRows: [],  // 批量删除选中的行
      pagination: {
        current: 1,
        size: 10,
        total: 0
      },
      searchForm: {
        libraryId: null,
        seatNumber: '',
        seatType: '',
        status: ''
      },
      dialogVisible: false,
      dialogTitle: '',
      isEdit: false,
      submitLoading: false,
      form: {
        id: null,
        libraryId: null,
        seatNumber: '',
        seatType: '普通座位',
        status: '正常',
        rowNum: 0,
        colNum: 0
      },
      
      // 批量新增相关
      batchDialogVisible: false,
      batchSubmitLoading: false,
      batchForm: {
        libraryId: null,
        startNumber: 1,
        endNumber: null,
        seatType: '普通座位',
        status: '正常'
      },
      previewSeats: [],
      
      // 可视化新增相关
      visualDialogVisible: false,
      visualSubmitLoading: false,
      visualForm: {
        libraryId: null,
        seatType: '普通座位',
        numberPrefix: ''
      },
      visualGrid: [],          // 二维数组（可变长度每行）：每个格子 { selected, existing }
      existingSeatsList: [],
      existingSeatNumbers: new Set(),  // 已有座位的编号集合，用于去重
      showExistingSeats: true,
      globalCounter: 1,        // 全局座位编号计数器

      // 重排编号相关
      renumberDialogVisible: false,
      renumberSubmitLoading: false,
      renumberPreviewLoading: false,
      renumberForm: {
        libraryId: null,
        prefix: ''
      },
      renumberPreview: [],     // 预览数据：按行分组的 [{oldNumber, newNumber, changed}, ...]
      renumberTotalCount: 0,
      renumberChangedCount: 0,
      rules: {
        libraryId: [
          { required: true, message: '请选择图书室', trigger: 'change' }
        ],
        seatNumber: [
          { required: true, message: '请输入座位编号', trigger: 'blur' }
        ],
        seatType: [
          { required: true, message: '请选择座位类型', trigger: 'change' }
        ],
        status: [
          { required: true, message: '请选择状态', trigger: 'change' }
        ]
      },
      
      // 批量新增验证规则
      batchRules: {
        libraryId: [
          { required: true, message: '请选择图书室', trigger: 'change' }
        ],
        startNumber: [
          { required: true, message: '请输入起始编号', trigger: 'blur' },
          { type: 'number', min: 1, message: '起始编号不能小于1', trigger: 'blur' }
        ],
        endNumber: [
          { required: true, message: '请输入结束编号', trigger: 'blur' },
          { 
            validator: (rule, value, callback) => {
              if (!value) {
                callback(new Error('请输入结束编号'))
              } else if (value <= this.batchForm.startNumber) {
                callback(new Error('结束编号必须大于起始编号'))
              } else {
                callback()
              }
            }, 
            trigger: 'blur' 
          }
        ],
        seatType: [
          { required: true, message: '请选择座位类型', trigger: 'change' }
        ],
        status: [
          { required: true, message: '请选择状态', trigger: 'change' }
        ]
      }
    }
  },
  
  computed: {
    // 总格子数（所有行的列数之和）
    visualTotalCount() {
      let total = 0
      for (let r = 0; r < this.visualGrid.length; r++) {
        total += (this.visualGrid[r] || []).length
      }
      return total
    },
    // 已选中且非已有的格子数
    visualSelectedCount() {
      let count = 0
      for (let r = 0; r < this.visualGrid.length; r++) {
        for (let c = 0; c < (this.visualGrid[r] || []).length; c++) {
          if (this.visualGrid[r][c].selected && !this.visualGrid[r][c].existing) count++
        }
      }
      return count
    },
    // 是否有任何行包含已有座位（用于禁用"减少一排"按钮）
    hasAnyExistingSeat() {
      for (let r = 0; r < this.visualGrid.length; r++) {
        if (this.rowHasExistingSeat(r)) return true
      }
      return false
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
          libraryId: this.searchForm.libraryId,
          seatNumber: this.searchForm.seatNumber,
          seatType: this.searchForm.seatType,
          status: this.searchForm.status
        }
        const res = await getSeatPage(params)
        console.log('座位列表响应:', res)
        this.tableData = res.data.records || []
        this.pagination.total = res.data.total || 0
      } catch (error) {
        console.error('加载座位失败:', error)
        this.$message.error('加载数据失败: ' + error.message)
      } finally {
        this.loading = false
      }
    },
    
    getLibraryName(libraryId) {
      const library = this.libraries.find(lib => lib.id === libraryId)
      return library ? library.name : '未知图书室'
    },
    
    handleSearch() {
      this.pagination.current = 1
      this.loadData()
    },
    
    handleReset() {
      this.searchForm.libraryId = null
      this.searchForm.seatNumber = ''
      this.searchForm.seatType = ''
      this.searchForm.status = ''
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
      this.dialogTitle = '新增座位'
      this.isEdit = false
      this.dialogVisible = true
    },
    
    handleEdit(row) {
      this.dialogTitle = '编辑座位'
      this.isEdit = true
      this.form = { ...row }
      this.dialogVisible = true
    },
    
    async handleDelete(row) {
      try {
        await this.$confirm('确定要删除这个座位吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        await deleteSeat(row.id)
        this.$message.success('删除成功')
        this.loadData()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('删除失败')
        }
      }
    },
    
    // 表格选择变化
    handleSelectionChange(selection) {
      this.selectedRows = selection
    },
    
    // 批量删除
    async handleBatchDelete() {
      if (this.selectedRows.length === 0) return
      const names = this.selectedRows.map(r => r.seatNumber || r.id).join('、')
      try {
        await this.$confirm(
          `确定要删除选中的 <strong>${this.selectedRows.length}</strong> 个座位吗？<br><br><span style="color:#666;font-size:12px;">${names}</span>`,
          '批量删除确认',
          { confirmButtonText: '确定删除', cancelButtonText: '取消', type: 'error', dangerouslyUseHTMLString: true }
        )
        
        const batchLoading = this.$loading({ text: '正在批量删除...' })
        let successCount = 0
        let failCount = 0
        
        for (let i = 0; i < this.selectedRows.length; i += 10) {
          const batch = this.selectedRows.slice(i, i + 10)
          try {
            const promises = batch.map(row => deleteSeat(row.id))
            await Promise.all(promises)
            successCount += batch.length
          } catch (e) {
            failCount += batch.length
            console.error('批次删除失败:', e)
          }
        }
        
        batchLoading.close()
        
        if (failCount > 0) {
          this.$message.warning(`成功删除 ${successCount} 个，失败 ${failCount} 个`)
        } else {
          this.$message.success(`成功删除 ${successCount} 个座位`)
        }
        
        this.selectedRows = []
        this.loadData()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('批量删除失败: ' + (error.message || '未知错误'))
        }
      }
    },
    
    handleSubmit() {
      this.$refs.form.validate(async (valid) => {
        if (valid) {
          this.submitLoading = true
          try {
            if (this.isEdit) {
              await updateSeat(this.form.id, this.form)
              this.$message.success('更新成功')
            } else {
              await createSeat(this.form)
              this.$message.success('新增成功')
            }
            this.dialogVisible = false
            this.loadData()
          } catch (error) {
            this.$message.error(error.message || (this.isEdit ? '更新失败' : '新增失败'))
          } finally {
            this.submitLoading = false
          }
        }
      })
    },
    
    resetForm() {
      this.form = {
        id: null,
        libraryId: null,
        seatNumber: '',
        seatType: '普通座位',
        status: '正常',
        rowNum: 0,
        colNum: 0
      }
      if (this.$refs.form) {
        this.$refs.form.resetFields()
      }
    },
    
    // ========== 可视化新增座位（可变行列） ==========
    
    showVisualAddDialog() {
      this.visualDialogVisible = true
      this.initDefaultGrid()
    },
    
    onVisualLibraryChange() {
      this.initDefaultGrid()
    },
    
    resetVisualForm() {
      this.visualForm = {
        libraryId: null,
        seatType: '普通座位',
        numberPrefix: ''
      }
      this.visualGrid = []
      this.existingSeatsList = []
      this.existingSeatNumbers = new Set()
      this.showExistingSeats = true
      this.globalCounter = 1
    },
    
    initDefaultGrid() {
      this.visualGrid = []
      this.globalCounter = 1
      // 默认 4 行，每行 4 列（与学生端座位平面图一致）
      for (let r = 0; r < 4; r++) {
        const rowArr = []
        for (let c = 0; c < 4; c++) {
          rowArr.push({ selected: true, existing: false })
        }
        this.visualGrid.push(rowArr)
      }
      if (this.visualForm.libraryId) {
        this.loadExistingSeats()
      } else {
        this.existingSeatsList = []
      }
    },
    
    addRow() {
      if (this.visualGrid.length >= 20) return
      const newRow = []
      for (let c = 0; c < 4; c++) newRow.push({ selected: true, existing: false })
      this.visualGrid.push(newRow)
    },
    
    removeRow() {
      if (this.visualGrid.length <= 1) return
      this.visualGrid.pop()
    },
    
    getRowColCount(ri) {
      return (this.visualGrid[ri] || []).length
    },

    // 判断某一行是否包含已有座位
    rowHasExistingSeat(ri) {
      const row = this.visualGrid[ri]
      if (!row) return false
      return row.some(cell => cell && cell.existing)
    },
    
    changeRowCols(ri, delta) {
      if (!this.visualGrid[ri]) return
      const newCount = this.visualGrid[ri].length + delta
      if (newCount < 1 || newCount > 15) return
      if (delta > 0) {
        this.visualGrid[ri].push({ selected: true, existing: false })
      } else {
        this.visualGrid[ri].pop()
      }
      // 使用 $set 触发响应式更新，避免 $forceUpdate 全量重渲染
      this.$set(this.visualGrid, ri, this.visualGrid[ri].slice())
    },
    
    async loadExistingSeats() {
      if (!this.showExistingSeats || !this.visualForm.libraryId) {
        // 使用 $set 清除已有座位标记，确保 Vue 2 响应式更新
        for (let r = 0; r < this.visualGrid.length; r++) {
          const newRow = (this.visualGrid[r] || []).map(cell => ({
            selected: cell.selected,
            existing: false
          }))
          this.$set(this.visualGrid, r, newRow)
        }
        this.existingSeatsList = []
        return
      }

      try {
        const res = await getSeatsByLibraryId(this.visualForm.libraryId)
        const seats = res.data || []
        console.log('[可视化] 加载已有座位原始数据:', seats.length, '条', seats)
        this.existingSeatsList = seats

        if (seats.length === 0) {
          // 无已有座位，清除所有 existing 标记
          for (let r = 0; r < this.visualGrid.length; r++) {
            const newRow = (this.visualGrid[r] || []).map(cell => ({
              selected: cell.selected,
              existing: false
            }))
            this.$set(this.visualGrid, r, newRow)
          }
          this.existingSeatNumbers = new Set()
          return
        }

        // 收集已有座位编号
        this.existingSeatNumbers = new Set(seats.map(s => s.seatNumber).filter(Boolean))

        // ========== 策略：直接按网格位置逐一匹配 ==========
        // 不再依赖 rowNum/colNum（可能异常），而是用更可靠的方式

        // 先统计有效的座位数据用于确定需要的行列数
        let maxRow = 0
        let maxCol = 0
        const validSeats = []
        for (const seat of seats) {
          const rn = parseInt(seat.rowNum, 10)
          const cn = parseInt(seat.colNum, 10)
          // 只接受合理的行列范围 (row 1-30, col 1-15)
          if (rn >= 1 && rn <= 30 && cn >= 1 && cn <= 15) {
            validSeats.push({ ...seat, _rn: rn, _cn: cn })
            if (rn > maxRow) maxRow = rn
            if (cn > maxCol) maxCol = cn
          } else {
            console.warn('[可视化] 座位数据异常被跳过:', seat.seatNumber, 'rowNum=', seat.rowNum, 'colNum=', seat.colNum)
          }
        }

        console.log('[可视化] 有效座位:', validSeats.length, '条, maxRow=', maxRow, 'maxCol=', maxCol)

        // 如果有效座位为0但有总座位数，说明数据全异常 — 用顺序填充兜底
        if (validSeats.length === 0 && seats.length > 0) {
          console.warn('[可视化] 所有座位行列数据异常，使用顺序填充')
          let idx = 0
          for (let r = 0; r < this.visualGrid.length && idx < seats.length; r++) {
            const row = this.visualGrid[r]
            if (!row) continue
            for (let c = 0; c < row.length && idx < seats.length; c++) {
              const newRow = [...this.visualGrid[r]]
              newRow[c] = { selected: false, existing: true, seatNumber: seats[idx].seatNumber }
              this.$set(this.visualGrid, r, newRow)
              idx++
            }
          }
          return
        }

        // 确保网格行数足够
        while (this.visualGrid.length < maxRow && this.visualGrid.length < 30) {
          const nr = []
          for (let c = 0; c < Math.max(4, maxCol); c++) nr.push({ selected: true, existing: false })
          this.visualGrid.push(nr)
        }
        // 同时确保每行的列数足够
        for (let r = 0; r < this.visualGrid.length; r++) {
          const neededCols = Math.max(this.visualGrid[r].length, maxCol)
          while (this.visualGrid[r].length < neededCols) {
            this.visualGrid[r].push({ selected: true, existing: false })
          }
        }

        // 先重置所有格子的 existing 标记
        for (let r = 0; r < this.visualGrid.length; r++) {
          const newRow = (this.visualGrid[r] || []).map(cell => ({
            selected: cell.selected,
            existing: false,
            seatNumber: undefined
          }))
          this.$set(this.visualGrid, r, newRow)
        }

        // 按位置标记已有座位
        for (const seat of validSeats) {
          const ri = seat._rn - 1  // 转 0-based
          const ci = seat._cn - 1
          if (ri >= 0 && ri < this.visualGrid.length && ci >= 0 && ci < this.visualGrid[ri].length) {
            const newRow = [...this.visualGrid[ri]]
            newRow[ci] = {
              selected: false,
              existing: true,
              seatNumber: seat.seatNumber
            }
            this.$set(this.visualGrid, ri, newRow)
          } else {
            console.warn('[可视化] 座位超出网格范围:', seat.seatNumber, 'pos=(', seat._rn, ',', seat._cn, ')', 'grid=', this.visualGrid.length, 'x', this.visualGrid[seat._rn - 1]?.length)
          }
        }

        // 验证：统计实际标记成功的数量
        let markedCount = 0
        for (let r = 0; r < this.visualGrid.length; r++) {
          for (let c = 0; c < (this.visualGrid[r] || []).length; c++) {
            if (this.visualGrid[r][c].existing) markedCount++
          }
        }
        console.log('[可视化] 标记完成: 总座位', seats.length, ', 有效', validSeats.length, ', 实际标记', markedCount)

      } catch (error) {
        console.error('加载已有座位失败:', error)
      }
    },
    
    getCellNumber(ri, ci) {
      const cell = this.visualGrid[ri] && this.visualGrid[ri][ci]
      // 已有座位返回数据库中的真实编号（固定不变，不随布局偏移）
      if (cell && cell.existing && cell.seatNumber) {
        return cell.seatNumber
      }
      
      // 新格子：从已有最大编号之后按顺序分配，避免与任何已有编号冲突
      const prefix = (this.visualForm.numberPrefix || '').trim()
      let maxNum = 0
      if (this.existingSeatNumbers.size > 0) {
        for (const num of this.existingSeatNumbers) {
          const stripped = prefix ? num.replace(new RegExp('^' + this.escapeRegExp(prefix)), '') : num
          const n = parseInt(stripped, 10)
          if (!isNaN(n) && n > maxNum) maxNum = n
        }
      }
      
      // 计算当前新格子是第几个非已有格子（按行优先顺序）
      const newIdx = this.getNewCellIndex(ri, ci)
      const number = maxNum + newIdx + 1
      return prefix ? `${prefix}${number}` : String(number).padStart(3, '0')
    },
    
    // 计算当前格子是第几个"非已有"格子（用于新编号分配）
    getNewCellIndex(ri, ci) {
      let idx = 0
      for (let r = 0; r < this.visualGrid.length; r++) {
        const row = this.visualGrid[r]
        if (!row) continue
        for (let c = 0; c < row.length; c++) {
          if (r === ri && c === ci) return idx
          if (!row[c].existing) idx++  // 只数非已有格子
        }
      }
      return idx
    },
    
    isExistingSeat(ri, ci) {
      return !!(this.visualGrid[ri] && this.visualGrid[ri][ci] && this.visualGrid[ri][ci].existing)
    },
    
    isSelected(ri, ci) {
      return !!(this.visualGrid[ri] && this.visualGrid[ri][ci] && this.visualGrid[ri][ci].selected)
    },
    
    toggleCell(ri, ci) {
      if (this.isExistingSeat(ri, ci)) {
        this.$message.warning('该位置已存在座位，不可重复创建')
        return
      }
      if (this.visualGrid[ri] && this.visualGrid[ri][ci]) {
        const row = [...this.visualGrid[ri]]
        row[ci] = { ...row[ci], selected: !row[ci].selected }
        this.$set(this.visualGrid, ri, row)
      }
    },
    
    selectAllSeats() {
      for (let r = 0; r < this.visualGrid.length; r++) {
        const newRow = (this.visualGrid[r] || []).map(cell => ({
          selected: !cell.existing,   // 非已有座位才选中
          existing: cell.existing
        }))
        this.$set(this.visualGrid, r, newRow)
      }
    },

    deselectAllSeats() {
      for (let r = 0; r < this.visualGrid.length; r++) {
        const newRow = (this.visualGrid[r] || []).map(cell => ({
          selected: false,
          existing: cell.existing
        }))
        this.$set(this.visualGrid, r, newRow)
      }
    },
    
    getCellClass(ri, ci) {
      const cell = (this.visualGrid[ri] || [])[ci]
      if (!cell) return 'vgrid-cell'
      if (cell.existing) return 'vgrid-cell cell-existing'
      if (cell.selected) return 'vgrid-cell cell-selected'
      return 'vgrid-cell cell-empty'
    },
    
    getCellTitle(ri, ci) {
      const number = this.getCellNumber(ri, ci)
      if (this.isExistingSeat(ri, ci)) return `${number} - 已存在`
      return this.isSelected(ri, ci) ? `${number} - 将创建（点击取消）` : `${number} - 未选中（点击添加）`
    },
    
    async handleVisualSubmit() {
      if (!this.visualForm.libraryId) { this.$message.warning('请先选择图书室'); return }
      if (this.visualSelectedCount === 0) { this.$message.warning('请至少选择一个位置'); return }
      
      try {
        await this.$confirm(
          `确定要在「${this.getLibraryName(this.visualForm.libraryId)}」新增 ${this.visualSelectedCount} 个座位吗？`,
          '可视化新增确认',
          { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
        )
        
        this.visualSubmitLoading = true
        const seatsToCreate = []

        // 直接使用 getCellNumber 生成编号（与界面显示完全一致）
        for (let r = 0; r < this.visualGrid.length; r++) {
          for (let c = 0; c < (this.visualGrid[r] || []).length; c++) {
            const cell = this.visualGrid[r][c]
            if (cell.selected && !cell.existing) {
              seatsToCreate.push({
                libraryId: this.visualForm.libraryId,
                seatNumber: this.getCellNumber(r, c),
                seatType: this.visualForm.seatType,
                status: '正常',
                rowNum: r + 1,
                colNum: c + 1
              })
            }
          }
        }
        
        await this.batchCreateSeats(seatsToCreate)
        this.$message.success(`成功新增 ${seatsToCreate.length} 个座位`)
        this.visualDialogVisible = false
        this.loadData()
        
      } catch (error) {
        if (error !== 'cancel') this.$message.error('可视化新增失败：' + (error.message || '未知错误'))
      } finally {
        this.visualSubmitLoading = false
      }
    },

    // 批量新增相关方法
    showBatchAddDialog() {
      this.batchDialogVisible = true
      this.updatePreview()
    },
    
    resetBatchForm() {
      this.batchForm = {
        libraryId: null,
        startNumber: 1,
        endNumber: null,
        seatType: '普通座位',
        status: '正常'
      }
      this.previewSeats = []
      if (this.$refs.batchForm) {
        this.$refs.batchForm.resetFields()
      }
    },
    
    updatePreview() {
      this.previewSeats = []
      
      if (this.batchForm.startNumber && this.batchForm.endNumber && 
          this.batchForm.endNumber > this.batchForm.startNumber) {
        
        const seats = []
        for (let i = this.batchForm.startNumber; i <= this.batchForm.endNumber; i++) {
          // 格式化座位编号为三位数，如 001, 002, 010
          const seatNumber = String(i).padStart(3, '0')
          seats.push(seatNumber)
        }
        this.previewSeats = seats
      }
    },
    
    async handleBatchSubmit() {
      this.$refs.batchForm.validate(async (valid) => {
        if (valid) {
          if (this.previewSeats.length === 0) {
            this.$message.warning('请设置正确的座位编号区间')
            return
          }
          
          // 确认批量新增
          try {
            await this.$confirm(
              `确定要批量新增 ${this.previewSeats.length} 个座位吗？`, 
              '批量新增确认', 
              {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
              }
            )
            
            this.batchSubmitLoading = true
            
            // 构建批量新增的座位数据
            const seats = this.previewSeats.map(seatNumber => ({
              libraryId: this.batchForm.libraryId,
              seatNumber: seatNumber,
              seatType: this.batchForm.seatType,
              status: this.batchForm.status
            }))
            
            // 调用批量新增API
            await this.batchCreateSeats(seats)
            
            this.$message.success(`成功新增 ${seats.length} 个座位`)
            this.batchDialogVisible = false
            this.loadData()
            
          } catch (error) {
            if (error !== 'cancel') {
              this.$message.error('批量新增失败：' + (error.message || '未知错误'))
            }
          } finally {
            this.batchSubmitLoading = false
          }
        }
      })
    },
    
    async batchCreateSeats(seats) {
      // 分批处理，避免一次性创建太多座位导致请求超时
      const batchSize = 10
      const batches = []
      
      for (let i = 0; i < seats.length; i += batchSize) {
        batches.push(seats.slice(i, i + batchSize))
      }
      
      let successCount = 0
      let errorCount = 0
      
      for (const batch of batches) {
        try {
          // 并发创建当前批次的座位
          const promises = batch.map(seat => createSeat(seat))
          await Promise.all(promises)
          successCount += batch.length
        } catch (error) {
          errorCount += batch.length
          console.error('批次创建失败:', error)
        }
      }
      
      if (errorCount > 0) {
        throw new Error(`成功创建 ${successCount} 个，失败 ${errorCount} 个`)
      }
    },

    // 获取座位类型标签类型
    getSeatTypeTagType(seatType) {
      switch (seatType) {
        case '电脑座位':
          return 'success'
        case '静音座位':
          return 'warning'
        case '普通座位':
        default:
          return 'primary'
      }
    },

    // 转义正则特殊字符
    escapeRegExp(str) {
      return str.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
    },

    // 获取座位类型图标
    getSeatTypeIcon(seatType) {
      switch (seatType) {
        case '电脑座位':
          return 'el-icon-monitor'
        case '静音座位':
          return 'el-icon-bell'
        case '普通座位':
        default:
          return 'el-icon-reading'
      }
    },

    // ========== 重排编号功能 ==========

    showRenumberDialog() {
      this.renumberDialogVisible = true
    },

    resetRenumberForm() {
      this.renumberForm = { libraryId: null, prefix: '' }
      this.renumberPreview = []
      this.renumberTotalCount = 0
      this.renumberChangedCount = 0
    },

    async loadRenumberPreview() {
      if (!this.renumberForm.libraryId) return

      this.renumberPreviewLoading = true
      try {
        const res = await getSeatsByLibraryId(this.renumberForm.libraryId)
        const seats = res.data || []

        if (seats.length === 0) {
          this.$message.warning('该图书室暂无座位')
          this.renumberPreview = []
          this.renumberTotalCount = 0
          this.renumberChangedCount = 0
          return
        }

        // 按 rowNum、colNum 排序
        const sorted = [...seats].sort((a, b) => {
          if ((a.rowNum || 0) !== (b.rowNum || 0)) return (a.rowNum || 0) - (b.rowNum || 0)
          return (a.colNum || 0) - (b.colNum || 0)
        })

        const prefix = (this.renumberForm.prefix || '').trim()
        const rowsMap = {}
        let changedCount = 0
        let seq = 1

        for (const seat of sorted) {
          const rn = seat.rowNum || 1
          if (!rowsMap[rn]) rowsMap[rn] = []
          const newNumber = prefix ? `${prefix}${seq}` : String(seq).padStart(3, '0')
          const changed = seat.seatNumber !== newNumber
          if (changed) changedCount++
          rowsMap[rn].push({
            id: seat.id,
            oldNumber: seat.seatNumber,
            newNumber: newNumber,
            changed: changed,
            rowNum: rn,
            colNum: seat.colNum || 1
          })
          seq++
        }

        // 转换为数组按行号排序
        this.renumberPreview = Object.keys(rowsMap)
          .map(Number)
          .sort((a, b) => a - b)
          .map(rn => rowsMap[rn])
        this.renumberTotalCount = seats.length
        this.renumberChangedCount = changedCount
      } catch (error) {
        console.error('加载重排预览失败:', error)
        this.$message.error('加载预览失败: ' + error.message)
      } finally {
        this.renumberPreviewLoading = false
      }
    },

    async handleRenumberSubmit() {
      if (!this.renumberForm.libraryId) { this.$message.warning('请先选择图书室'); return }
      if (this.renumberPreview.length === 0) { this.$message.warning('请先点击刷新预览'); return }
      if (this.renumberChangedCount === 0) { this.$message.info('所有座位编号无需变更'); return }

      try {
        await this.$confirm(
          `确定要将「${this.getLibraryName(this.renumberForm.libraryId)}」的 ${this.renumberTotalCount} 个座位重新编号吗？\n其中 ${this.renumberChangedCount} 个座位的编号会发生变化（不可撤销）`,
          '确认重排',
          { confirmButtonText: '确定重排', cancelButtonText: '取消', type: 'error' }
        )

        this.renumberSubmitLoading = true
        let successCount = 0
        let failCount = 0

        // 收集所有需要变更的项，批量更新
        const allChanges = []
        for (const row of this.renumberPreview) {
          for (const item of row) {
            if (item.changed) allChanges.push(item)
          }
        }

        for (let i = 0; i < allChanges.length; i += 10) {
          const batch = allChanges.slice(i, i + 10)
          try {
            const promises = batch.map(item =>
              updateSeat(item.id, { seatNumber: item.newNumber })
            )
            await Promise.all(promises)
            successCount += batch.length
          } catch (e) {
            failCount += batch.length
            console.error('批次更新编号失败:', e)
          }
        }

        if (failCount > 0) {
          this.$message.warning(`成功更新 ${successCount} 个，失败 ${failCount} 个`)
        } else {
          this.$message.success(`成功将 ${successCount} 个座位重新编号`)
        }

        this.renumberDialogVisible = false
        this.loadData()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('重排编号失败: ' + (error.message || '未知错误'))
        }
      } finally {
        this.renumberSubmitLoading = false
      }
    }
  }
}
</script>
<style scoped>
.seat-management {
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

.pagination {
  margin-top: 20px;
  text-align: right;
}

.header-buttons {
  display: flex;
  gap: 10px;
}

.data-card {
  padding: 16px 16px 20px;
}

.preview-area {
  border: 1px solid rgba(0, 0, 0, 0.06);
  border-radius: 14px;
  padding: 15px;
  background-color: #F5F5F7;
}

.preview-info {
  margin-bottom: 10px;
  font-weight: 500;
  color: #86868B;
}

.preview-seats {
  max-height: 120px;
  overflow-y: auto;
}

.more-indicator {
  color: #86868B;
  font-size: 12px;
  margin-left: 5px;
}
</style>

<!-- ========== 对话框内部样式（el-dialog 渲染在 body，必须非 scoped） ========== -->
<style>
/* 可视化新增座位样式 */
.visual-add-container { display:flex; flex-direction:column; gap:12px; }
.visual-config { background:#fafafa; border-radius:12px; padding:16px 20px 8px; }
.visual-actions-bar { display:flex; align-items:center; gap:10px; margin-top:8px; padding-top:10px; border-top:1px solid rgba(0,0,0,0.06); }
.selected-count { font-size:13px; color:#86868B; }
.selected-count strong { color:#007AFF; font-size:15px; }

.visual-divider { text-align:center; position:relative; color:#86868B; font-size:13px; }
.visual-divider::before,.visual-divider::after { content:''; display:inline-block; width:28%; height:1px; background:linear-gradient(transparent,rgba(0,0,0,0.08),transparent); vertical-align:middle; margin:0 12px; }

/* 可变网格区域 */
.visual-grid-wrapper { max-height:420px; overflow-y:auto; border:2px dashed rgba(0,0,0,0.12); border-radius:14px; padding:16px 20px; background:#FFFFFF; }
.visual-grid-scroll { min-width:fit-content; }

.vgrid-row { display:flex; align-items:flex-start; gap:12px; margin-bottom:14px; padding-bottom:4px; }
.vgrid-row:last-child { margin-bottom:0; }

.vgrid-row-header { display:flex; flex-direction:column; align-items:center; min-width:52px; padding-top:2px; }
.vgrid-row-label { font-size:13px; font-weight:700; color:#007AFF; line-height:24px; white-space:nowrap; }
.vgrid-row-controls { display:flex; align-items:center; gap:3px; }
.col-count-badge { font-size:11px; color:#999; min-width:26px; text-align:center; }

.vgrid-cells { display:flex; gap:5px; flex-wrap:nowrap; }

/* 格子 */
.vgrid-cell {
  width:54px !important;
  height:50px !important;
  border-radius:10px !important;
  display:inline-flex !important;
  flex-direction:column !important;
  align-items:center !important;
  justify-content:center !important;
  cursor:pointer !important;
  transition:all .15s ease !important;
  position:relative !important;
  user-select:none !important;
  border:2px solid transparent !important;
  flex-shrink:0 !important;
  box-sizing:border-box !important;
}

.cell-number { font-size:11px; font-weight:700; letter-spacing:-0.3px; line-height:1.2; text-align:center; }
.cell-badge { font-size:9px; padding:1px 5px; border-radius:8px; font-weight:500; transform:scale(0.88); display:inline-block; }

.cell-empty { background:#F5F5F7 !important; border-color:#E8E8ED !important; }
.cell-empty:hover { background:#E8E8ED !important; border-color:#D2D2D7 !important; }
.cell-empty .cell-number { color:#bbb !important; }

.cell-selected {
  background:#E8F4FF !important;
  border-color:#007AFF !important;
  box-shadow:0 2px 8px rgba(0,122,255,.18) !important;
}
.cell-selected:hover { transform:translateY(-1px) !important; box-shadow:0 4px 12px rgba(0,122,255,.28) !important; }
.cell-selected .cell-number { color:#007AFF !important; }
.cell-selected .select-badge { background:#007AFF !important; color:#fff !important; }

.cell-existing { background:#f0eceb !important; border-color:#d4ccc9 !important; cursor:not-allowed!important; opacity:.65 !important; }
.cell-existing .cell-number { color:#999 !important; }
.cell-existing .existing-badge { background:#ddd !important; color:#777 !important; }

/* 已有座位提示 */
.existing-seats-hint { display:flex; align-items:center; gap:6px; font-size:12px; color:#86868B; padding:8px 14px; background:#F5F5F7; border-radius:8px; border:1px solid rgba(0,0,0,0.06); }
.existing-seats-hint i { color:#007AFF; }

.vgrid-cell-empty-hint { display:flex; align-items:center; justify-content:center; width:120px; height:40px; border-radius:8px; border:1px dashed #ddd; color:#ccc; font-size:12px; }
.vgrid-empty { display:flex; flex-direction:column; align-items:center; justify-content:center; padding:40px 0; color:#ccc; }

/* ========== 重排编号样式（dialog 内也需要非 scoped） ========== */
.renumber-container { padding: 0 4px; }
.renumber-preview { border:1px solid rgba(0,0,0,0.06); border-radius:14px; padding:14px; background:#FFFFFF; max-height:280px; overflow-y:auto; }
.preview-row { display:flex; align-items:center; flex-wrap:wrap; gap:2px; margin-bottom:8px; padding-bottom:8px; border-bottom:1px dashed rgba(0,0,0,0.06); }
.preview-row:last-child { border-bottom:none; margin-bottom:0; }
.preview-row-label { font-size:12px; font-weight:700; color:#007AFF; min-width:36px; flex-shrink:0; }
</style>