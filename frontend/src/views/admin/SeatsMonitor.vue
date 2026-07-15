<template>
  <div class="seats-monitor">
    <div class="page-hero">
      <div>
        <span class="page-kicker">Monitor</span>
        <h2>实时座位监控</h2>
        <p>实时查看各图书室座位使用状态。</p>
      </div>
      <div class="header-actions">
        <el-select v-model="selectedLibraryId" placeholder="选择图书室" @change="onLibraryChange" size="medium">
          <el-option
            v-for="lib in libraries"
            :key="lib.id"
            :label="lib.name"
            :value="lib.id"
          />
        </el-select>
        <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="refreshData">刷新</el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div v-if="seats.length > 0" class="stats-row">
      <div class="stat-card idle">
        <div class="stat-value">{{ stats.idle }}</div>
        <div class="stat-label">空闲</div>
      </div>
      <div class="stat-card reserved">
        <div class="stat-value">{{ stats.reserved }}</div>
        <div class="stat-label">已预约</div>
      </div>
      <div class="stat-card occupied">
        <div class="stat-value">{{ stats.occupied }}</div>
        <div class="stat-label">使用中</div>
      </div>
      <div class="stat-card temp-leave">
        <div class="stat-value">{{ stats.tempLeave }}</div>
        <div class="stat-label">暂离</div>
      </div>
      <div class="stat-card disabled">
        <div class="stat-value">{{ stats.disabled }}</div>
        <div class="stat-label">停用/维修</div>
      </div>
      <div class="stat-card total">
        <div class="stat-value">{{ stats.total }}</div>
        <div class="stat-label">总计</div>
      </div>
    </div>

    <!-- 图例 -->
    <div class="legend-bar">
      <span class="legend-item"><span class="dot dot-idle"></span>空闲</span>
      <span class="legend-item"><span class="dot dot-reserved"></span>已预约</span>
      <span class="legend-item"><span class="dot dot-occupied"></span>使用中</span>
      <span class="legend-item"><span class="dot dot-temp-leave"></span>暂离</span>
      <span class="legend-item"><span class="dot dot-disabled"></span>维修/停用</span>
      <span class="legend-item"><span class="dot dot-selected"></span>选中</span>
    </div>

    <!-- 加载中 -->
    <div v-if="loading && !seats.length" class="loading-wrapper">
      <i class="el-icon-loading"></i> 加载中...
    </div>

    <!-- 无数据提示 -->
    <el-empty v-if="!loading && seats.length === 0 && selectedLibraryId" description="暂无座位数据" />

    <!-- 请选择图书室 -->
    <el-empty v-if="!selectedLibraryId" description="请先选择一个图书室" />

    <!-- 座位平面图 -->
    <div v-if="seats.length > 0" class="seat-grid-wrapper">
      <div class="seat-grid">
        <div v-for="row in seatsByRow" :key="row[0].rowNum" class="grid-row">
          <div class="row-label">{{ displayRowNum(row[0].rowNum) }}排</div>
          <div class="row-seats">
            <div
              v-for="seat in row"
              :key="seat.id"
              class="seat-cell"
              :class="getSeatMonitorClass(seat)"
              @click="onSeatClick(seat)"
              :title="getSeatTitle(seat)"
            >
              <span class="seat-num">{{ seat.seatNumber }}</span>
              <span class="seat-type-tag">{{ getSeatStatusLabel(seat) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 座位详情弹窗 -->
    <el-dialog
      title="座位详情"
      :visible.sync="detailVisible"
      width="400px"
      :close-on-click-modal="true"
    >
      <div v-if="detailSeat" class="seat-detail">
        <div class="detail-info">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="座位编号">{{ detailSeat.seatNumber }}</el-descriptions-item>
            <el-descriptions-item label="座位类型">{{ detailSeat.seatType }}</el-descriptions-item>
            <el-descriptions-item label="当前状态">
              <el-tag :type="detailStatusTag" size="small">{{ detailStatusText }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="排/列">
              {{ displayRowNum(detailSeat.rowNum) }}排 / {{ detailSeat.colNum }}列
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getLibraryList } from '@/api/library'
import { getSeatMonitorLayout } from '@/api/seat'
import wsManager from '@/utils/websocket'

export default {
  name: 'SeatsMonitor',
  data() {
    return {
      selectedLibraryId: null,
      libraries: [],
      seats: [],
      loading: false,
      detailVisible: false,
      detailSeat: null,
      detailReservation: null,
      loadingDetail: false,
      // WebSocket 监听清理函数
      wsCleanups: []
    }
  },
  computed: {
    // 按行号分组
    seatsByRow() {
      const groups = {}
      this.seats.forEach(seat => {
        const row = seat.rowNum || 0
        if (!groups[row]) groups[row] = []
        groups[row].push(seat)
      })
      return Object.keys(groups)
        .sort((a, b) => Number(a) - Number(b))
        .map(key => groups[key])
    },
    // 统计数据
    stats() {
      let idle = 0, reserved = 0, occupied = 0, tempLeave = 0, disabled = 0
      this.seats.forEach(seat => {
        const status = this.getMonitorStatus(seat)
        if (status === 'idle') idle++
        else if (status === 'reserved') reserved++
        else if (status === 'occupied') occupied++
        else if (status === 'tempLeave') tempLeave++
        else disabled++
      })
      return { idle, reserved, occupied, tempLeave, disabled, total: this.seats.length }
    },
    detailStatusText() {
      if (!this.detailSeat) return ''
      const s = this.getMonitorStatus(this.detailSeat)
      const map = { idle: '空闲', reserved: '已预约', occupied: '使用中', tempLeave: '暂离', disabled: '维修/停用' }
      return map[s] || '未知'
    },
    detailStatusTag() {
      const s = this.getMonitorStatus(this.detailSeat)
      const map = { idle: 'success', reserved: 'warning', occupied: 'danger', tempLeave: 'info', disabled: 'info' }
      return map[s] || 'info'
    }
  },
  async created() {
    await this.loadLibraries()
    this.setupWebSocket()
  },
  beforeDestroy() {
    this.cleanupWebSocket()
  },
  methods: {
    async loadLibraries() {
      try {
        const res = await getLibraryList()
        this.libraries = res.data || []
      } catch (e) {
        this.$message.error('加载图书室列表失败')
      }
    },
    async onLibraryChange() {
      this.seats = []
      await this.refreshData()
      // 订阅该图书馆的座位变更
      if (this.selectedLibraryId) {
        wsManager.subscribe(this.selectedLibraryId)
      }
    },
    async refreshData() {
      if (!this.selectedLibraryId) return
      this.loading = true
      try {
        const res = await getSeatMonitorLayout({ libraryId: this.selectedLibraryId })
        this.seats = res.data || []
      } catch (e) {
        this.$message.error('加载座位数据失败')
      } finally {
        this.loading = false
      }
    },
    setupWebSocket() {
      // 监听增量座位变更
      const cleanup1 = wsManager.on('seatChange', (data) => {
        if (data.libraryId === this.selectedLibraryId && data.seat) {
          this.applySeatChange(data.seat)
        }
      })
      this.wsCleanups.push(cleanup1)

      // 监听刷新信号
      const cleanup2 = wsManager.on('libraryUpdate', (data) => {
        if (data.libraryId === this.selectedLibraryId) {
          this.refreshData()
        }
      })
      this.wsCleanups.push(cleanup2)

      wsManager.connect()
    },
    cleanupWebSocket() {
      this.wsCleanups.forEach(fn => fn())
      this.wsCleanups = []
      wsManager.unsubscribe()
    },
    // 增量更新座位
    applySeatChange(seatUpdate) {
      if (!seatUpdate || !seatUpdate.id) return
      const index = this.seats.findIndex(s => s.id === seatUpdate.id)
      if (index !== -1) {
        this.$set(this.seats, index, { ...this.seats[index], ...seatUpdate })
      }
    },
    // 获取监控状态（基于当前预约状态和座位自身状态）
    getMonitorStatus(seat) {
      if (!seat) return 'disabled'
      if (seat.status === '维修' || seat.status === '停用') return 'disabled'
      // 优先使用后端返回的精确预约状态
      if (seat.reservationStatus === '已使用') return 'occupied'
      if (seat.reservationStatus === '暂离') return 'tempLeave'
      if (seat.isReserved || seat.reservationStatus === '已预约') return 'reserved'
      return 'idle'
    },
    getSeatMonitorClass(seat) {
      const status = this.getMonitorStatus(seat)
      const classes = [`status-${status}`]
      if (this.detailSeat && this.detailSeat.id === seat.id) {
        classes.push('selected')
      }
      if (seat.seatType === '电脑座位') classes.push('type-computer')
      else if (seat.seatType === '静音座位') classes.push('type-quiet')
      return classes.join(' ')
    },
    getSeatStatusLabel(seat) {
      const status = this.getMonitorStatus(seat)
      const map = { idle: '空闲', reserved: '已预约', occupied: '使用中', tempLeave: '暂离', disabled: seat.status === '维修' ? '维修' : '停用' }
      return map[status] || ''
    },
    getSeatTitle(seat) {
      return `${seat.seatNumber} - ${seat.seatType}\n当前: ${this.getSeatStatusLabel(seat)}`
    },
    displayRowNum(rowNum) {
      return rowNum != null ? String(rowNum) : '?'
    },
    async onSeatClick(seat) {
      this.detailSeat = seat
      this.detailReservation = null
      this.detailVisible = true
    }
  }
}
</script>

<style scoped>
.seats-monitor { display: grid; gap: 18px; }

.page-hero {
  background: #FFFFFF;
  border-radius: 14px;
  border: 1px solid rgba(0, 0, 0, 0.06);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 28px;
  flex-wrap: wrap;
  gap: 12px;
}

.page-kicker {
  display: inline-block; margin-bottom: 10px; color: #34C759; font-size: 12px; font-weight: 600; letter-spacing: 0.8px; text-transform: uppercase;
}
.page-hero h2 { margin: 0 0 6px; color: #1D1D1F; font-size: 28px; }
.page-hero p { margin: 0; color: #86868B; font-size: 14px; }

.header-actions { display: flex; gap: 12px; align-items: center; }

/* 统计卡片 */
.stats-row {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 12px;
}

.stat-card {
  text-align: center;
  padding: 20px 12px;
  background: #FFFFFF;
  border-radius: 14px;
  border: 1px solid rgba(0, 0, 0, 0.06);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
  transition: transform 0.2s ease;
}

.stat-card:hover { transform: translateY(-2px); }

.stat-value { font-size: 28px; font-weight: 700; line-height: 1.2; letter-spacing: -0.5px; }
.stat-label { font-size: 13px; color: #86868B; margin-top: 4px; }

.stat-card.idle .stat-value { color: #34C759; }
.stat-card.reserved .stat-value { color: #FF9500; }
.stat-card.occupied .stat-value { color: #FF3B30; }
.stat-card.temp-leave .stat-value { color: #8E8E93; }
.stat-card.disabled .stat-value { color: #C7C7CC; }
.stat-card.total .stat-value { color: #007AFF; }

/* 图例 */
.legend-bar {
  background: white;
  border-radius: 14px;
  border: 1px solid rgba(0, 0, 0, 0.06);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
  display: flex;
  gap: 16px;
  padding: 14px 20px;
  flex-wrap: wrap;
  align-items: center;
}

.legend-item { display: flex; align-items: center; gap: 6px; font-size: 13px; color: #606266; }

.dot { width: 12px; height: 12px; border-radius: 3px; display: inline-block; }

.dot-idle { background: rgba(52,199,89,0.15); border: 2px solid #34C759; }
.dot-reserved { background: rgba(255,149,0,0.12); border: 2px solid #FF9500; }
.dot-occupied { background: rgba(255,59,48,0.12); border: 2px solid #FF3B30; }
.dot-temp-leave { background: rgba(142,142,147,0.1); border: 2px solid #8e8e93; }
.dot-disabled { background: #f5f5f5; border: 2px dashed #C7C7CC; }
.dot-selected { background: #007AFF; border: 2px solid #005bb5; }

/* 加载 */
.loading-wrapper { text-align: center; padding: 60px 0; color: #86868B; font-size: 14px; }
.loading-wrapper i { font-size: 24px; margin-right: 8px; }

/* 座位网格 */
.seat-grid-wrapper {
  background: #fff;
  border-radius: 14px;
  padding: 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
  border: 1px solid rgba(0, 0, 0, 0.06);
}

.seat-grid {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.grid-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.row-label {
  flex-shrink: 0;
  width: 48px;
  text-align: center;
  font-size: 12px;
  color: #909399;
  font-weight: 500;
  writing-mode: vertical-lr;
  letter-spacing: 2px;
}

.row-seats {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.seat-cell {
  width: 72px;
  height: 60px;
  border-radius: 6px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
  font-size: 11px;
  border: 2px solid transparent;
  user-select: none;
}

.seat-cell:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.seat-num {
  font-size: 14px;
  font-weight: 700;
  line-height: 1.3;
}

.seat-type-tag {
  font-size: 10px;
  opacity: 0.85;
  line-height: 1;
  margin-top: 2px;
}

/* 状态颜色 */
.seat-cell.status-idle {
  background: #e8f5e9;
  border-color: #67c23a;
  color: #2e7d32;
}

.seat-cell.status-reserved {
  background: #fff3e0;
  border-color: #e6a23c;
  color: #e65100;
}

.seat-cell.status-occupied {
  background: #fce4ec;
  border-color: #f56c6c;
  color: #c62828;
  opacity: 0.9;
}

.seat-cell.status-disabled {
  background: #f5f5f5;
  border-color: #dcdfe6;
  color: #c0c4cc;
  cursor: not-allowed;
}

/* 类型微调 */
.seat-cell.type-computer.status-idle {
  background: #e3f2fd;
  border-color: #42a5f5;
  color: #1565c0;
}

.seat-cell.type-quiet.status-idle {
  background: #f3e5f5;
  border-color: #ab47bc;
  color: #7b1fa2;
}

/* 选中 */
.seat-cell.selected {
  border-color: #409eff !important;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.3);
  transform: scale(1.05);
}

/* 座位详情弹窗 */
.seat-detail {
  padding: 8px 0;
}

.detail-info {
  margin-bottom: 16px;
}
</style>
