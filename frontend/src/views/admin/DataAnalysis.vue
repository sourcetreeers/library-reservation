<template>
  <div class="data-analysis">
    <div class="page-hero">
      <div>
        <span class="page-kicker">Analytics</span>
        <h2>数据分析</h2>
        <p>实时监控图书馆座位使用情况与预约趋势，辅助运营决策。</p>
      </div>
      <div class="header-right">
        <el-select v-model="selectedLibrary" placeholder="选择图书室" clearable style="width: 200px">
          <el-option label="全部图书室" :value="null" />
          <el-option
            v-for="lib in libraryOptions"
            :key="lib.id"
            :label="lib.name"
            :value="lib.id"
          />
        </el-select>
        <el-button type="primary" icon="el-icon-search" @click="loadAllData">查询</el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon" style="background: rgba(0, 122, 255, 0.1)">
          <i class="el-icon-office-building" style="color: #007AFF"></i>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ libraryUsage.length }}</div>
          <div class="stat-label">图书室总数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: rgba(52, 199, 89, 0.1)">
          <i class="el-icon-chair" style="color: #34C759"></i>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ totalSeats }}</div>
          <div class="stat-label">座位总数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: rgba(255, 149, 0, 0.1)">
          <i class="el-icon-tickets" style="color: #FF9500"></i>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ todayReservations }}</div>
          <div class="stat-label">今日预约</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: rgba(255, 59, 48, 0.1)">
          <i class="el-icon-data-analysis" style="color: #FF3B30"></i>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ avgUsageRate }}%</div>
          <div class="stat-label">平均使用率</div>
        </div>
      </div>
    </div>
    
    <!-- 图表区域 -->
    <div class="content-card chart-card">
      <div class="chart-header">各图书室使用情况</div>
      <div ref="libraryUsageChart" style="height: 400px"></div>
    </div>
    
    <div class="chart-row">
      <div class="content-card chart-card">
        <div class="chart-header">座位类型分布</div>
        <div ref="seatTypeChart" style="height: 350px"></div>
      </div>
      <div class="content-card chart-card">
        <div class="chart-header">预约状态统计</div>
        <div ref="reservationStatusChart" style="height: 350px"></div>
      </div>
    </div>
    
    <div class="content-card chart-card">
      <div class="chart-header">近7天预约趋势</div>
      <div ref="reservationTrendChart" style="height: 350px"></div>
    </div>

    <div class="chart-row">
      <div class="content-card chart-card">
        <div class="chart-header">高峰时段分布</div>
        <div ref="peakHoursChart" style="height: 350px"></div>
      </div>
      <div class="content-card chart-card">
        <div class="chart-header">用户履约排行榜</div>
        <div ref="userBehaviorChart" style="height: 350px"></div>
      </div>
    </div>

    <div class="content-card chart-card">
      <div class="chart-header">近12个月预约趋势</div>
      <div ref="monthlyTrendChart" style="height: 350px"></div>
    </div>

    <div class="content-card chart-card">
      <div class="chart-header-row">
        <span>最受欢迎座位 TOP10</span>
        <el-tag size="small" effect="plain" style="color: #FF9500; border-color: rgba(255, 149, 0, 0.3); background: rgba(255, 149, 0, 0.06);">按累计有效预约次数排序</el-tag>
      </div>
      <div v-if="popularSeatsData.length > 0" ref="popularSeatsChart" style="height: 420px"></div>
      <el-empty v-else description="暂无座位预约数据"></el-empty>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import {
  getLibraryUsage,
  getSeatTypeDistribution,
  getReservationStatus,
  getReservationTrend,
  getPeakHours,
  getUserBehavior,
  getMonthlyTrend,
  getPopularSeats
} from '@/api/statistics'
import { getLibraryList } from '@/api/library'

export default {
  name: 'DataAnalysis',
  data() {
    return {
      selectedLibrary: null,
      libraryOptions: [],
      libraryUsage: [],
      totalSeats: 0,
      todayReservations: 0,
      avgUsageRate: 0,
      
      libraryUsageChart: null,
      seatTypeChart: null,
      reservationStatusChart: null,
      reservationTrendChart: null,
      peakHoursChart: null,
      userBehaviorChart: null,
      monthlyTrendChart: null,
      popularSeatsChart: null,
      popularSeatsData: []
    }
  },
  
  created() {
    this.loadLibraries()
  },
  
  mounted() {
    this.$nextTick(() => {
      this.initCharts()
      this.loadAllData()
    })
  },
  
  beforeDestroy() {
    // 销毁图表实例
    if (this.libraryUsageChart) this.libraryUsageChart.dispose()
    if (this.seatTypeChart) this.seatTypeChart.dispose()
    if (this.reservationStatusChart) this.reservationStatusChart.dispose()
    if (this.reservationTrendChart) this.reservationTrendChart.dispose()
    if (this.peakHoursChart) this.peakHoursChart.dispose()
    if (this.userBehaviorChart) this.userBehaviorChart.dispose()
    if (this.monthlyTrendChart) this.monthlyTrendChart.dispose()
    if (this.popularSeatsChart) this.popularSeatsChart.dispose()
  },
  
  methods: {
    async loadLibraries() {
      try {
        const res = await getLibraryList()
        this.libraryOptions = res.data || []
      } catch (error) {
        console.error('加载图书室列表失败', error)
      }
    },
    
    async loadAllData() {
      await Promise.all([
        this.loadLibraryUsage(),
        this.loadSeatTypeDistribution(),
        this.loadReservationStatus(),
        this.loadReservationTrend(),
        this.loadPeakHours(),
        this.loadUserBehavior(),
        this.loadMonthlyTrend(),
        this.loadPopularSeats()
      ])
    },
    
    async loadLibraryUsage() {
      try {
        const params = this.selectedLibrary ? { libraryId: this.selectedLibrary } : {}
        const res = await getLibraryUsage(params)
        this.libraryUsage = res.data || []
        
        // 计算统计数据
        this.totalSeats = this.libraryUsage.reduce((sum, item) => sum + item.totalSeats, 0)
        this.todayReservations = this.libraryUsage.reduce((sum, item) => sum + item.todayReservations, 0)
        this.avgUsageRate = this.libraryUsage.length > 0
          ? (this.libraryUsage.reduce((sum, item) => sum + parseFloat(item.usageRate), 0) / this.libraryUsage.length).toFixed(1)
          : 0
        
        this.updateLibraryUsageChart()
      } catch (error) {
        console.error('加载图书室使用情况失败:', error)
        this.$message.error('加载图书室使用情况失败: ' + (error.message || ''))
      }
    },
    
    async loadSeatTypeDistribution() {
      try {
        const params = this.selectedLibrary ? { libraryId: this.selectedLibrary } : {}
        const res = await getSeatTypeDistribution(params)
        this.updateSeatTypeChart(res.data)
      } catch (error) {
        console.error('加载座位类型分布失败:', error)
        this.$message.error('加载座位类型分布失败: ' + (error.message || ''))
      }
    },
    
    async loadReservationStatus() {
      try {
        const params = this.selectedLibrary ? { libraryId: this.selectedLibrary } : {}
        const res = await getReservationStatus(params)
        this.updateReservationStatusChart(res.data)
      } catch (error) {
        console.error('加载预约状态统计失败:', error)
        this.$message.error('加载预约状态统计失败: ' + (error.message || ''))
      }
    },
    
    async loadReservationTrend() {
      try {
        const params = this.selectedLibrary ? { libraryId: this.selectedLibrary } : {}
        const res = await getReservationTrend(params)
        this.updateReservationTrendChart(res.data)
      } catch (error) {
        console.error('加载预约趋势失败:', error)
        this.$message.error('加载预约趋势失败: ' + (error.message || ''))
      }
    },
    
    initCharts() {
      this.libraryUsageChart = echarts.init(this.$refs.libraryUsageChart)
      this.seatTypeChart = echarts.init(this.$refs.seatTypeChart)
      this.reservationStatusChart = echarts.init(this.$refs.reservationStatusChart)
      this.reservationTrendChart = echarts.init(this.$refs.reservationTrendChart)
      this.peakHoursChart = echarts.init(this.$refs.peakHoursChart)
      this.userBehaviorChart = echarts.init(this.$refs.userBehaviorChart)
      this.monthlyTrendChart = echarts.init(this.$refs.monthlyTrendChart)
      if (this.$refs.popularSeatsChart) {
        this.popularSeatsChart = echarts.init(this.$refs.popularSeatsChart)
      }
      
      // 窗口大小改变时重新渲染
      window.addEventListener('resize', () => {
        this.libraryUsageChart && this.libraryUsageChart.resize()
        this.seatTypeChart && this.seatTypeChart.resize()
        this.reservationStatusChart && this.reservationStatusChart.resize()
        this.reservationTrendChart && this.reservationTrendChart.resize()
        this.peakHoursChart && this.peakHoursChart.resize()
        this.userBehaviorChart && this.userBehaviorChart.resize()
        this.monthlyTrendChart && this.monthlyTrendChart.resize()
        this.popularSeatsChart && this.popularSeatsChart.resize()
      })
    },
    
    updateLibraryUsageChart() {
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' }
        },
        legend: {
          data: ['座位总数', '今日预约', '使用率']
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: this.libraryUsage.map(item => item.libraryName)
        },
        yAxis: [
          {
            type: 'value',
            name: '数量'
          },
          {
            type: 'value',
            name: '使用率(%)',
            max: 100
          }
        ],
        series: [
          {
            name: '座位总数',
            type: 'bar',
            data: this.libraryUsage.map(item => item.totalSeats),
            itemStyle: { color: '#007AFF' }
          },
          {
            name: '今日预约',
            type: 'bar',
            data: this.libraryUsage.map(item => item.todayReservations),
            itemStyle: { color: '#34C759' }
          },
          {
            name: '使用率',
            type: 'line',
            yAxisIndex: 1,
            data: this.libraryUsage.map(item => parseFloat(item.usageRate)),
            itemStyle: { color: '#FF9500' },
            lineStyle: { width: 3 }
          }
        ]
      }
      
      this.libraryUsageChart.setOption(option)
    },
    
    updateSeatTypeChart(data) {
      if (!data || !data.distribution) return
      
      const distribution = data.distribution
      const pieData = Object.keys(distribution).map(key => ({
        name: key,
        value: distribution[key]
      }))
      
      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [
          {
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: true,
              formatter: '{b}: {d}%'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '16',
                fontWeight: 'bold'
              }
            },
            data: pieData
          }
        ]
      }
      
      this.seatTypeChart.setOption(option)
    },
    
    updateReservationStatusChart(data) {
      if (!data) return
      
      const pieData = Object.keys(data).map(key => ({
        name: key,
        value: data[key]
      }))
      
      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [
          {
            type: 'pie',
            radius: '60%',
            itemStyle: {
              borderRadius: 8,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: true,
              formatter: '{b}: {c}'
            },
            data: pieData
          }
        ]
      }
      
      this.reservationStatusChart.setOption(option)
    },
    
    updateReservationTrendChart(data) {
      if (!data || data.length === 0) return
      
      const dates = data.map(item => item.date.substring(5)) // 只显示月-日
      const counts = data.map(item => item.count)
      
      const option = {
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: dates
        },
        yAxis: {
          type: 'value',
          name: '预约数'
        },
        series: [
          {
            name: '预约数',
            type: 'line',
            smooth: true,
            data: counts,
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(0, 122, 255, 0.5)' },
                { offset: 1, color: 'rgba(0, 122, 255, 0.05)' }
              ])
            },
            itemStyle: { color: '#007AFF' },
            lineStyle: { width: 3 }
          }
        ]
      }
      
      this.reservationTrendChart.setOption(option)
    },
    
    // ===== 新增图表 =====
    
    async loadPeakHours() {
      try {
        const params = this.selectedLibrary ? { libraryId: this.selectedLibrary } : {}
        const res = await getPeakHours(params)
        this.updatePeakHoursChart(res.data)
      } catch (error) {
        console.error('加载高峰时段数据失败:', error)
      }
    },
    
    async loadUserBehavior() {
      try {
        const params = this.selectedLibrary ? { libraryId: this.selectedLibrary, topN: 10 } : { topN: 10 }
        const res = await getUserBehavior(params)
        this.updateUserBehaviorChart(res.data)
      } catch (error) {
        console.error('加载用户排行数据失败:', error)
      }
    },
    
    async loadMonthlyTrend() {
      try {
        const params = this.selectedLibrary ? { libraryId: this.selectedLibrary } : {}
        const res = await getMonthlyTrend(params)
        this.updateMonthlyTrendChart(res.data)
      } catch (error) {
        console.error('加载月度趋势数据失败:', error)
      }
    },
    
    updatePeakHoursChart(data) {
      if (!data || data.length === 0) return
      
      const hours = data.map(item => item.hour)
      const counts = data.map(item => item.count)
      
      const option = {
        tooltip: { trigger: 'axis' },
        xAxis: {
          type: 'category',
          data: hours,
          axisLabel: { rotate: 45, fontSize: 11 }
        },
        yAxis: { type: 'value', name: '预约数' },
        series: [{
          name: '预约数',
          type: 'bar',
          data: counts,
            itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#FF9500' },
              { offset: 1, color: '#FFB340' }
            ]),
            borderRadius: [4, 4, 0, 0]
          },
          markPoint: {
            data: [
              { type: 'max', name: '峰值' },
              { type: 'min', name: '谷值' }
            ]
          }
        }]
      }
      
      this.peakHoursChart.setOption(option)
    },
    
    updateUserBehaviorChart(data) {
      if (!data || data.length === 0) return
      
      const names = data.map(item => item.realName || ('用户' + item.userId))
      const rates = data.map(item => parseFloat(item.complianceRate))
      
      const option = {
        tooltip: { trigger: 'axis', formatter: '{b}<br/>履约率: {c}%' },
        grid: { left: '3%', right: '8%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', data: names, axisLabel: { rotate: 30 } },
        yAxis: { type: 'value', name: '履约率(%)', max: 100 },
        series: [{
          type: 'bar',
          data: rates,
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#34C759' },
              { offset: 1, color: '#6ED89E' }
            ]),
            borderRadius: [4, 4, 0, 0]
          },
          label: {
            show: true,
            position: 'top',
            formatter: '{c}%',
            fontSize: 11
          }
        }]
      }
      
      this.userBehaviorChart.setOption(option)
    },
    
    updateMonthlyTrendChart(data) {
      if (!data || data.length === 0) return
      
      const months = data.map(item => item.month)
      const counts = data.map(item => item.count)
      
      const option = {
        tooltip: { trigger: 'axis' },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: months,
          axisLabel: { rotate: 30, fontSize: 11 }
        },
        yAxis: { type: 'value', name: '预约数' },
        series: [{
          name: '预约数',
          type: 'line',
          smooth: true,
          data: counts,
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(175, 82, 222, 0.5)' },
              { offset: 1, color: 'rgba(175, 82, 222, 0.05)' }
            ])
          },
          itemStyle: { color: '#AF52DE' },
          lineStyle: { width: 3 }
        }]
      }
      
      this.monthlyTrendChart.setOption(option)
    },

    // ===== 最受欢迎座位排行 =====

    async loadPopularSeats() {
      try {
        const params = this.selectedLibrary ? { libraryId: this.selectedLibrary } : {}
        const res = await getPopularSeats({ ...params, topN: 10 })
        this.popularSeatsData = res.data || []
        if (this.popularSeatsData.length > 0) {
          // 首次有数据时延迟初始化图表（因为 v-if 导致 DOM 之前不存在）
          await this.$nextTick()
          if (!this.popularSeatsChart && this.$refs.popularSeatsChart) {
            this.popularSeatsChart = echarts.init(this.$refs.popularSeatsChart)
            window.addEventListener('resize', () => { this.popularSeatsChart && this.popularSeatsChart.resize() })
          }
          if (this.popularSeatsChart) {
            this.updatePopularSeatsChart()
          }
        }
        if (this.popularSeatsData.length === 0 && this.popularSeatsChart) {
          this.popularSeatsChart.clear()
        }
      } catch (error) {
        console.error('加载热门座位数据失败:', error)
      }
    },

    updatePopularSeatsChart() {
      if (!this.popularSeatsChart || !this.popularSeatsData.length) return

      // 反转顺序：水平条形图从下到上排列，第1名在最上方
      const sorted = [...this.popularSeatsData].reverse()

      const names = sorted.map((item, idx) => {
        const rank = this.popularSeatsData.length - idx
        const libName = item.libraryName || ''
        return `#${rank} ${item.seatNumber || ('座位' + item.seatId)}${libName ? ' (' + libName + ')' : ''}`
      })

      const counts = sorted.map(item => Number(item.reservationCount))

      const colors = [...this.popularSeatsData].reverse().map((_, i) => {
        const rankFromTop = this.popularSeatsData.length - i
        if (rankFromTop === 1) return '#FF3B30'
        if (rankFromTop === 2) return '#FF9500'
        if (rankFromTop === 3) return '#007AFF'
        return '#8E8E93'
      }).reverse()

      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' },
          formatter(params) {
            const d = params[0]
            return `${d.name}<br/>预约次数：<b>${d.value}</b> 次`
          }
        },
        grid: {
          left: '8%',
          right: '16%',
          top: '3%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'value',
          name: '预约次数',
          minInterval: 1
        },
        yAxis: {
          type: 'category',
          data: names,
          axisLabel: {
            width: 220,
            overflow: 'truncate',
            ellipsis: '...',
            fontSize: 12
          }
        },
        series: [{
          type: 'bar',
          data: counts.map((val, i) => ({
            value: val,
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                { offset: 0, color: colors[i] },
                { offset: 1, color: colors[i] + '40' }
              ]),
              borderRadius: [0, 4, 4, 0]
            }
          })),
          barWidth: '60%',
          label: {
            show: true,
            position: 'right',
            formatter: '{c}次',
            fontSize: 12,
            fontWeight: 500,
            color: '#1D1D1F'
          }
        }]
      }

      this.popularSeatsChart.setOption(option, true)
    }
  }
}
</script>

<style scoped>
.data-analysis {
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

.header-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

/* 统计卡片 */
.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 18px;
}

.stat-card {
  background: #FFFFFF;
  border-radius: 14px;
  border: 1px solid rgba(0, 0, 0, 0.06);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
  padding: 20px 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: box-shadow 0.2s;
}

.stat-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.stat-icon {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  flex-shrink: 0;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #1D1D1F;
  margin-bottom: 4px;
  letter-spacing: -0.5px;
}

.stat-label {
  font-size: 14px;
  color: #86868B;
}

/* 图表区域 */
.chart-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 18px;
}

.chart-card {
  padding: 20px 24px;
}

.chart-header {
  font-size: 15px;
  font-weight: 600;
  color: #1D1D1F;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.chart-header-row {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 15px;
  font-weight: 600;
  color: #1D1D1F;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.section-header-row {
  display: flex;
  align-items: center;
  gap: 12px;
}
</style>
