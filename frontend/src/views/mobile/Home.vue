<template>
  <div class="mobile-home">
    <van-nav-bar title="在线图书馆座位预订系统" fixed>
      <template #right>
        <van-icon name="user-o" @click="showUserMenu = true" />
      </template>
    </van-nav-bar>
    
    <div class="content">
      <!-- 轮播图 -->
      <div v-if="banners.length > 0" class="banner-container">
        <van-swipe class="banner-swipe" :autoplay="3000" indicator-color="white">
          <van-swipe-item v-for="banner in banners" :key="banner.id">
            <div class="banner-item">
              <van-image :src="banner.imageUrl" width="100%" height="180" fit="cover" />
              <div v-if="banner.title" class="banner-title">{{ banner.title }}</div>
            </div>
          </van-swipe-item>
        </van-swipe>
      </div>
      
      <!-- 公告栏 -->
      <div v-if="announcements.length > 0" class="announcement-container">
        <div class="announcement-fixed">
          <div class="announcement-header">
            <van-icon name="volume-o" color="#007AFF" />
            <span class="announcement-title">公告信息</span>
          </div>
          <div class="announcement-list">
            <div
              v-for="item in announcements"
              :key="item.id"
              class="announcement-item"
              @click="showAnnouncementDetail(item)"
            >
              <span class="announcement-type-tag" :class="getTypeClass(item.type)">{{ item.type }}</span>
              <span class="announcement-text">{{ item.title }}</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 预约步骤 -->
      <van-steps :active="currentStep" active-color="#007AFF">
        <van-step>选择时间</van-step>
        <van-step>选择图书室</van-step>
        <van-step>选择座位</van-step>
      </van-steps>
      
      <!-- 步骤1：选择时间 -->
      <div v-if="currentStep === 0" class="step-container">
        <!-- 用户权限提示 -->
        <div class="permission-tip">
          <van-notice-bar
            left-icon="info-o"
            :scrollable="false"
          >
            当前信用等级：{{ userPermissions.creditLevel }} | 最多可预约{{ userPermissions.maxHours }}小时 | 每日{{ userPermissions.maxCount }}次
          </van-notice-bar>
        </div>
        
        <van-cell-group class="time-selection-group">
          <van-field
            v-model="reservationDate"
            readonly
            label="预约日期"
            placeholder="选择预约日期"
            @click="showDatePicker = true"
            is-link
          />
          <van-field
            v-model="startTime"
            readonly
            label="开始时间"
            placeholder="选择开始时间"
            @click="showStartTimePicker = true"
            is-link
          />
          <van-field
            v-model="endTime"
            readonly
            label="结束时间"
            placeholder="选择结束时间"
            @click="showEndTimePicker = true"
            is-link
          />
        </van-cell-group>
        
        <div class="button-container">
          <van-button 
            type="info" 
            block 
            :disabled="!isTimeValid" 
            @click="nextStep"
          >
            下一步
          </van-button>
        </div>
      </div>
      
      <!-- 步骤2：选择图书馆 -->
      <div v-if="currentStep === 1" class="step-container">
        <!-- 搜索框 -->
        <div class="search-container">
          <van-search
            v-model="librarySearchKeyword"
            placeholder="搜索图书室名称、地址"
            show-action
            @search="onLibrarySearch"
            @clear="onLibrarySearchClear"
            @input="onLibrarySearchInput"
          >
            <template #action>
              <div @click="onLibrarySearch">搜索</div>
            </template>
          </van-search>
        </div>
        
        <!-- 搜索结果统计 -->
        <div v-if="librarySearchKeyword" class="search-result-info">
          找到 {{ filteredLibraries.length }} 个图书室
        </div>
        
        <!-- 图书室列表 -->
        <van-list>
          <van-cell
            v-for="library in filteredLibraries"
            :key="library.id"
            is-link
            @click="selectLibrary(library)"
          >
            <template #title>
              <div v-html="highlightText(library.name, librarySearchKeyword)"></div>
            </template>
            <template #label>
              <div v-html="highlightText(library.address, librarySearchKeyword)"></div>
            </template>
          </van-cell>
        </van-list>
        
        <!-- 无搜索结果提示 -->
        <div v-if="librarySearchKeyword && filteredLibraries.length === 0" class="no-result-container">
          <van-icon name="search" size="48" color="#c8c9cc" />
          <h3>未找到相关图书室</h3>
          <p>请尝试其他关键词</p>
        </div>
        
        <div class="button-container">
          <van-button plain @click="prevStep">上一步</van-button>
        </div>
      </div>
      
      <!-- 步骤3：选择座位 -->
      <div v-if="currentStep === 2" class="step-container">
        <!-- 图书室信息卡片 -->
        <div class="room-info-card">
          <div class="room-header">
            <div class="room-name-block">
              <van-icon name="location-o" class="room-icon" />
              <div class="room-text">
                <h3>{{ selectedLibrary.name }}</h3>
                <p>{{ getTimeRange() }}</p>
              </div>
            </div>
          </div>
          <div class="room-divider"></div>
          <!-- 座位状态图例 -->
          <div class="seat-legend">
            <div class="legend-item">
              <div class="legend-icon seat-available"></div>
              <span>空闲</span>
            </div>
            <div class="legend-item">
              <div class="legend-icon seat-reserved"></div>
              <span>已预约</span>
            </div>
            <div class="legend-item">
              <div class="legend-icon seat-occupied"></div>
              <span>占用中</span>
            </div>
            <div class="legend-item" v-if="hotSeatIds.length > 0">
              <div class="legend-icon seat-hot-legend"></div>
              <span>热门</span>
            </div>
            <div class="legend-item">
              <div class="legend-icon seat-selected-legend"></div>
              <span>选中</span>
            </div>
          </div>
        </div>

        <!-- 热门推荐提示条 -->
        <div v-if="hotSeatIds.length > 0 && allSeats.length > 0" class="hot-tip-bar">
          <van-icon name="fire-o" color="#FF6B3D" />
          <span>以下座位为同学们最爱选的热门座位，手慢无~</span>
        </div>

        <!-- 座位类型筛选 -->
        <div class="seat-filter-bar">
          <span
            v-for="ft in filterTypes"
            :key="ft.value"
            class="filter-btn"
            :class="{ active: seatTypeFilter === ft.value }"
            @click="setSeatTypeFilter(ft.value)"
          >
            <van-icon :name="ft.icon" v-if="ft.icon" />
            {{ ft.label }}
          </span>
        </div>

        <!-- 加载中状态 -->
        <div v-if="loadingSeats" class="loading-container">
          <van-loading size="24px" vertical>正在查询可用座位...</van-loading>
        </div>
        
        <!-- 座位平面布局 -->
        <div v-else-if="allSeats.length > 0" class="seat-layout-wrapper">
          <div class="layout-header">
            <van-icon name="arrow-left" class="layout-nav" @click="scrollLayout(-1)" v-if="maxCol > 6" />
            <span class="layout-title">座位平面图</span>
            <van-icon name="arrow-right" class="layout-nav" @click="scrollLayout(1)" v-if="maxCol > 6" />
          </div>
          <div class="visual-layout" ref="visualLayout">
            <div class="seats-grid">
              <div v-for="row in seatsByRow" :key="row[0].rowNum" class="row-container">
                <!-- 行标签 -->
                <div class="row-label-block">
                  <span class="row-text">{{ row[0].rowNum - minRowNum + 1 }}排</span>
                </div>
                <!-- 该行的所有座位 -->
                <div class="row-seats">
                  <div
                    v-for="seat in row"
                    :key="seat.id"
                    class="seat-block"
                    :class="getSeatClass(seat)"
                    :style="getSeatFilterStyle(seat)"
                    @click="onSeatClick(seat)"
                  >
                    <!-- 热门角标 -->
                    <div v-if="hotSeatIds.includes(seat.id) && !seat.isReserved && seat.status === '正常'" class="hot-badge">
                      🔥
                    </div>
                    <span class="seat-number">{{ seat.seatNumber }}</span>
                    <span class="seat-status-label">{{ getSeatStatusLabel(seat) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 无可用座位时显示满员提示 -->
        <div v-else class="no-seats-container">
          <van-icon name="smile-comment-o" size="48" color="#c8c9cc" />
          <h3>该时间段该图书室已满员</h3>
          <p>请尝试选择其他时间段或其他图书室</p>
          <van-button round type="primary" class="bottom-button" @click="prevStep">
            重新选择时间
          </van-button>
        </div>
        
        <div v-if="allSeats.length > 0" class="button-container">
          <van-button plain @click="prevStep">上一步</van-button>
          <van-button type="info" :disabled="!selectedSeat" @click="confirmReservation">
            确认预约
          </van-button>
        </div>
        
        <div v-else class="button-container">
          <van-button plain @click="prevStep">上一步</van-button>
        </div>
      </div>
    </div>
    
    <!-- 底部导航 -->
    <van-tabbar v-model="activeTab" @change="onTabChange">
      <van-tabbar-item icon="home-o">首页</van-tabbar-item>
      <van-tabbar-item icon="orders-o">我的预约</van-tabbar-item>
      <van-tabbar-item icon="contact-o">我的</van-tabbar-item>
    </van-tabbar>
    
    <!-- 日期选择器 -->
    <van-popup v-model="showDatePicker" position="bottom">
      <van-datetime-picker
        v-model="currentDate"
        type="date"
        title="选择预约日期"
        :min-date="minDate"
        @confirm="onDateConfirm"
        @cancel="showDatePicker = false"
      />
    </van-popup>
    
    <!-- 开始时间选择器 -->
    <van-popup v-model="showStartTimePicker" position="bottom">
      <van-picker
        show-toolbar
        title="选择开始时间"
        :columns="startTimeColumns"
        @confirm="onStartTimeConfirm"
        @cancel="showStartTimePicker = false"
      />
    </van-popup>
    
    <!-- 结束时间选择器 -->
    <van-popup v-model="showEndTimePicker" position="bottom">
      <van-picker
        show-toolbar
        title="选择结束时间"
        :columns="endTimeColumns"
        @confirm="onEndTimeConfirm"
        @cancel="showEndTimePicker = false"
      />
    </van-popup>
    
    <!-- 座位详情弹窗 -->
    <van-action-sheet v-model="showSeatDetail" :title="detailSeat ? '座位 ' + detailSeat.seatNumber : '座位详情'" round>
      <div v-if="detailSeat" class="seat-detail-sheet">
        <div class="detail-info">
          <van-cell-group>
            <van-cell title="座位编号" :value="detailSeat.seatNumber" />
            <van-cell title="座位类型" :value="detailSeat.seatType" />
            <van-cell title="当前状态" :value="getDetailStatusText(detailSeat)" />
            <van-cell title="所在位置" :value="(displayRowLabel(detailSeat.rowNum)) + ' / ' + (detailSeat.colNum || '?') + '列'" />
          </van-cell-group>
        </div>
        <div v-if="canSelectSeat(detailSeat)" class="detail-actions">
          <van-button type="primary" block round @click="confirmSeatSelection">确认选择该座位</van-button>
        </div>
      </div>
    </van-action-sheet>

    <!-- 用户菜单 -->
    <van-action-sheet v-model="showUserMenu" :actions="userActions" @select="onUserAction" />
  </div>
</template>

<script>
import { getLibraryList } from '@/api/library'
import { getSeatsLayout } from '@/api/seat'
import { createReservation, getAvailableTimeSlots } from '@/api/reservation'
import { logout } from '@/api/auth'
import { getMyPoints } from '@/api/points'
import { getAnnouncements, getBanners, getPopularSeats } from '@/api/common'
import { formatDate, combineDateTime } from '@/utils/time'
import wsManager from '@/utils/websocket'

export default {
  name: 'MobileHome',
  data() {
    return {
      currentStep: 0,
      activeTab: 0,
      
      // 轮播图和公告
      banners: [],
      announcements: [],
      
      // 用户权限
      userPermissions: {
        maxHours: 2,
        maxCount: 2,
        creditLevel: '一般'
      },
      
      // 时间选择
      reservationDate: '',
      startTime: '',
      endTime: '',
      showDatePicker: false,
      showStartTimePicker: false,
      showEndTimePicker: false,
      currentDate: new Date(),
      minDate: new Date(),
      
      // 可预约时间段（从后端动态加载）
      timeSlots: [],
      
      // 开始时间选择器列配置（动态生成）
      startTimeColumns: [],
      
      // 结束时间选择器列配置（动态生成）
      endTimeColumns: [],
      
      // 图书馆和座位
      libraries: [],
      librarySearchKeyword: '',
      selectedLibrary: null,
      availableSeats: [],
      allSeats: [], // 存储所有座位及其状态
      selectedSeat: null,
      loadingSeats: false,
      
      // 用户菜单
      showUserMenu: false,
      userActions: [
        { name: '退出登录', color: '#ee0a24' }
      ],
      
      // WebSocket
      wsCleanup: null,
      
      // 座位类型筛选
      seatTypeFilter: '全部',
      filterTypes: [
        { value: '全部', label: '全部', icon: '' },
        { value: '普通座位', label: '普通', icon: 'records-o' },
        { value: '电脑座位', label: '电脑', icon: 'desktop-o' },
        { value: '静音座位', label: '静音', icon: 'volume-o' }
      ],
      
      // 座位详情弹窗
      showSeatDetail: false,
      detailSeat: null,

      // 热门推荐座位ID列表
      hotSeatIds: []
    }
  },
  
  computed: {
    isTimeValid() {
      // 简单验证：只要三个字段都有值且结束时间晚于开始时间至少30分钟
      const hasAllFields = this.reservationDate && this.startTime && this.endTime
      
      if (!hasAllFields) {
        return false
      }
      
      // 比较时间字符串（支持HH:MM格式）
      const [startHour, startMinute] = this.startTime.split(':').map(Number)
      const [endHour, endMinute] = this.endTime.split(':').map(Number)
      
      // 转换为分钟数进行比较
      const startTotalMinutes = startHour * 60 + startMinute
      const endTotalMinutes = endHour * 60 + endMinute
      
      // 结束时间必须比开始时间至少晚30分钟
      const isTimeOrderValid = endTotalMinutes - startTotalMinutes >= 30
      
      // 校验时长限制（以小时为单位）
      const durationMinutes = endTotalMinutes - startTotalMinutes
      const durationHours = durationMinutes / 60
      const isDurationValid = durationHours <= this.userPermissions.maxHours
      
      return hasAllFields && isTimeOrderValid && isDurationValid
    },
    
    filteredLibraries() {
      if (!this.librarySearchKeyword) {
        return this.libraries
      }
      
      const keyword = this.librarySearchKeyword.toLowerCase().trim()
      if (!keyword) {
        return this.libraries
      }
      
      return this.libraries.filter(library => {
        const name = library.name.toLowerCase()
        const address = library.address ? library.address.toLowerCase() : ''
        const description = library.description ? library.description.toLowerCase() : ''
        
        // 支持多个关键词搜索（空格分隔）
        const keywords = keyword.split(/\s+/)
        
        return keywords.every(kw => 
          name.includes(kw) || 
          address.includes(kw) || 
          description.includes(kw)
        )
      })
    },

    // 计算布局行列数
    maxCol() {
      if (this.allSeats.length === 0) return 0
      return Math.max(...this.allSeats.map(s => s.colNum || 0))
    },
    // 当前图书室最小行号（用于归一化显示，每间图书室从1排开始）
    minRowNum() {
      if (this.allSeats.length === 0) return 1
      return Math.min(...this.allSeats.map(s => s.rowNum || 0))
    },
    // 座位按行分组
    seatsByRow() {
      const groups = {}
      this.allSeats.forEach(seat => {
        const row = seat.rowNum || 0
        if (!groups[row]) groups[row] = []
        groups[row].push(seat)
      })
      // 按行号排序
      return Object.keys(groups).sort((a, b) => Number(a) - Number(b)).map(key => groups[key])
    }
  },
  
  async created() {
    await Promise.all([
      this.loadLibraries(),
      this.loadUserPermissions(),
      this.loadBanners(),
      this.loadAnnouncements(),
      this.loadTimeSlots()
    ])
    // 初始化当前日期
    this.reservationDate = formatDate(new Date())
    // 初始化日期选择器为今天
    this.currentDate = new Date()
    
    // 连接 WebSocket 监听座位状态变更
    this.setupWebSocket()
  },
  
  beforeDestroy() {
    if (this.wsCleanup) {
      this.wsCleanup()
      this.wsCleanup = null
    }
  },
  
  methods: {
    setupWebSocket() {
      wsManager.connect()
      
      // 监听增量座位变更
      this.wsCleanup = wsManager.on('seatChange', (data) => {
        if (!data.seat) return
        if (this.currentStep === 2 && data.libraryId === this.selectedLibrary?.id) {
          this.applySeatChange(data.seat)
        }
      })
      
      // 监听全量刷新信号（批量变更后触发）
      wsManager.on('libraryUpdate', (data) => {
        if (this.currentStep === 2 && data.libraryId === this.selectedLibrary?.id) {
          this.loadAvailableSeats()
        }
      })
    },
    
    /**
     * 增量更新某个座位（避免全量 HTTP 拉取）
     */
    applySeatChange(seatUpdate) {
      if (!seatUpdate || !seatUpdate.id) return
      const index = this.allSeats.findIndex(s => s.id === seatUpdate.id)
      if (index !== -1) {
        // 更新现有座位
        this.$set(this.allSeats, index, { ...this.allSeats[index], ...seatUpdate })
        // 同步更新可用座位列表
        const availIndex = this.availableSeats.findIndex(s => s.id === seatUpdate.id)
        const nowFree = seatUpdate.status === '正常' && !seatUpdate.isReserved
        if (nowFree) {
          if (availIndex === -1) {
            this.availableSeats.push(this.allSeats[index])
          }
        } else {
          if (availIndex !== -1) {
            this.availableSeats.splice(availIndex, 1)
          }
        }
      }
    },
    async loadLibraries() {
      try {
        const res = await getLibraryList()
        this.libraries = res.data
      } catch (error) {
        this.$toast.fail('加载图书馆列表失败')
      }
    },
    
    async loadUserPermissions() {
      try {
        const res = await getMyPoints()
        const points = res.data
        this.userPermissions = {
          maxHours: points.maxReservationHours || 2,
          maxCount: points.maxReservationCount || 2,
          creditLevel: points.creditLevel || '一般'
        }
      } catch (error) {
        console.error('加载用户权限失败', error)
      }
    },
    
    async loadBanners() {
      try {
        const res = await getBanners()
        this.banners = res.data || []
      } catch (error) {
        console.error('加载轮播图失败', error)
      }
    },
    
    async loadAnnouncements() {
      try {
        const res = await getAnnouncements()
        this.announcements = res.data || []
      } catch (error) {
        console.error('加载公告失败', error)
      }
    },
    
    // 从后端动态加载可预约时间段
    async loadTimeSlots() {
      try {
        const res = await getAvailableTimeSlots()
        this.timeSlots = res.data || []
        this.initTimeColumns()
      } catch (error) {
        console.error('加载可预约时间段失败', error)
        // 加载失败时使用默认值
        this.timeSlots = [{ start: '08:00', end: '22:00' }]
        this.initTimeColumns()
      }
    },
    
    // 根据时间段初始化时间选择器
    initTimeColumns() {
      // 计算所有可用小时
      const hours = new Set()
      this.timeSlots.forEach(slot => {
        const startH = parseInt(slot.start.split(':')[0])
        const endH = parseInt(slot.end.split(':')[0])
        for (let h = startH; h < endH; h++) {
          hours.add(h)
        }
      })
      const sortedHours = Array.from(hours).sort((a, b) => a - b)
      const hourStrs = sortedHours.map(h => String(h).padStart(2, '0'))
      
      if (hourStrs.length === 0) return
      
      this.startTimeColumns = [
        { values: hourStrs, defaultIndex: 0 },
        { values: [':'] },
        { values: Array.from({ length: 60 }, (_, i) => String(i).padStart(2, '0')), defaultIndex: 0 }
      ]
      
      const defaultEndIdx = Math.min(1, hourStrs.length - 1)
      this.endTimeColumns = [
        { values: hourStrs, defaultIndex: defaultEndIdx },
        { values: [':'] },
        { values: Array.from({ length: 60 }, (_, i) => String(i).padStart(2, '0')), defaultIndex: 0 }
      ]
      
      // 设置默认时间：开始时间为第一个可用时段起点，结束时间为开始时间+30分钟
      const defaultStart = this.timeSlots[0].start || hourStrs[0] + ':00'
      this.startTime = defaultStart
      const [sH, sM] = defaultStart.split(':').map(Number)
      const defaultEndMin = sH * 60 + sM + 30
      const endH = Math.floor(defaultEndMin / 60)
      const endM = defaultEndMin % 60
      this.endTime = String(endH).padStart(2, '0') + ':' + String(endM).padStart(2, '0')
    },
    
    showAnnouncementDetail(announcement) {
      this.$dialog.alert({
        title: announcement.title,
        message: `<div style="text-align: left; padding: 10px 0;">
          <div style="margin-bottom: 10px; color: #666; font-size: 12px;">
            类型：${announcement.type} | 发布时间：${this.formatTime(announcement.publishTime)}
          </div>
          <div style="line-height: 1.6; color: #333;">${announcement.content}</div>
        </div>`,
        confirmButtonText: '我知道了'
      })
    },
    
    formatTime(timeStr) {
      if (!timeStr) return ''
      return timeStr.replace('T', ' ').substring(0, 16)
    },
    
    getTypeClass(type) {
      const classMap = {
        '普通公告': 'type-normal',
        '维修通知': 'type-maintenance',
        '紧急通知': 'type-urgent'
      }
      return classMap[type] || 'type-normal'
    },
    
    onLibrarySearch() {
      // 搜索功能已通过计算属性 filteredLibraries 实现
      // 这里可以添加搜索统计或其他逻辑
      if (this.filteredLibraries.length === 0 && this.librarySearchKeyword) {
        this.$toast('未找到相关图书室')
      }
    },
    
    onLibrarySearchClear() {
      this.librarySearchKeyword = ''
    },
    
    onLibrarySearchInput() {
      // 实时搜索，无需额外处理，计算属性会自动更新
    },
    
    highlightText(text, keyword) {
      if (!keyword || !text) return text
      
      const keywords = keyword.toLowerCase().trim().split(/\s+/)
      let result = text
      
      keywords.forEach(kw => {
        if (kw) {
          const regex = new RegExp(`(${kw})`, 'gi')
          result = result.replace(regex, '<span class="highlight">$1</span>')
        }
      })
      
      return result
    },
    
    async loadAvailableSeats() {
      this.loadingSeats = true
      // 安全机制：15秒超时自动取消加载状态
      const safetyTimer = setTimeout(() => {
        if (this.loadingSeats) {
          this.loadingSeats = false
          this.$toast.fail('座位信息加载超时，请重试')
        }
      }, 15000)
      try {
        const startDateTime = combineDateTime(this.reservationDate, this.startTime)
        const endDateTime = combineDateTime(this.reservationDate, this.endTime)
        
        const res = await getSeatsLayout({
          libraryId: this.selectedLibrary.id,
          startTime: startDateTime,
          endTime: endDateTime
        })
        
        this.allSeats = res.data || []
        // 过滤出可用座位
        this.availableSeats = this.allSeats.filter(seat => seat.status === '正常' && !seat.isReserved)
        
        if (this.availableSeats.length === 0 && this.allSeats.length > 0) {
          this.$toast('该时间段该图书室已满员')
        }
        
      } catch (error) {
        console.error('加载座位信息失败:', error)
        const errorMsg = error.response?.data?.message || error.message || '未知错误'
        this.$toast.fail('加载座位信息失败: ' + errorMsg)
        this.prevStep()
      } finally {
        clearTimeout(safetyTimer)
        this.loadingSeats = false
      }
    },

    // 加载热门推荐座位
    async loadHotSeats() {
      try {
        const res = await getPopularSeats({ libraryId: this.selectedLibrary.id, topN: 5 })
        this.hotSeatIds = (res.data && res.data.hotSeatIds) || []
      } catch (error) {
        console.error('加载热门座位失败:', error)
        this.hotSeatIds = []
      }
    },

    // 获取座位的 CSS 类名
    getSeatClass(seat) {
      if (!seat) return ''
      const classes = []
      if (seat.seatType === '电脑座位') classes.push('type-computer')
      else if (seat.seatType === '静音座位') classes.push('type-quiet')
      else classes.push('type-normal')

      // 热门标记（空闲时才显示热门效果）
      const isHot = this.hotSeatIds.includes(seat.id)
      if (isHot && !seat.isReserved && seat.status === '正常') {
        classes.push('hot-seat')
      }

      // 先判断座位自身状态（维修/停用），再判断预约状态
      if (seat.status === '维修') {
        classes.push('maintenance')
      } else if (seat.status === '停用' || seat.status !== '正常') {
        classes.push('disabled')
      } else if (this.selectedSeat && this.selectedSeat.id === seat.id) {
        classes.push('selected')
      } else if (seat.isReserved) {
        classes.push('reserved')
      } else {
        classes.push('available')
      }

      return classes.join(' ')
    },

    // 获取座位状态文本
    getSeatStatusLabel(seat) {
      if (!seat) return ''
      // 先判断座位自身状态
      if (seat.status === '维修') return '维修'
      if (seat.status === '停用') return '停用'
      if (seat.isReserved && ['已使用', '已完成'].includes(seat.status)) return ''
      if (seat.isReserved) return '已预约'
      return seat.seatType
    },

    // 点击座位 - 打开详情弹窗
    onSeatClick(seat) {
      if (!seat) return
      this.detailSeat = seat
      this.showSeatDetail = true
    },

    // 从详情弹窗确认选择
    confirmSeatSelection() {
      if (this.detailSeat && this.canSelectSeat(this.detailSeat)) {
        this.selectSeat(this.detailSeat)
        this.showSeatDetail = false
      }
    },

    // 判断座位是否可被选中预约
    canSelectSeat(seat) {
      return seat && !seat.isReserved && seat.status === '正常'
    },

    // 获取详情弹窗中的状态文本
    getDetailStatusText(seat) {
      if (!seat) return ''
      if (seat.status === '维修') return '维修中'
      if (seat.status === '停用') return '已停用'
      if (seat.isReserved && ['已使用', '已完成'].includes(seat.status)) return ''
      if (seat.isReserved) return '已被他人预约'
      return '空闲可预约'
    },

    // 显示行标签
    displayRowLabel(rowNum) {
      return rowNum != null ? (rowNum - this.minRowNum + 1) + '排' : '?排'
    },

    // 设置座位类型筛选
    setSeatTypeFilter(type) {
      this.seatTypeFilter = type
    },

    // 根据筛选状态决定座位的显示样式（隐藏不符合筛选的座位）
    getSeatFilterStyle(seat) {
      if (this.seatTypeFilter === '全部') return {}
      if (seat.seatType === this.seatTypeFilter) return {}
      return { opacity: '0.25', pointerEvents: 'none' }
    },

    // 选择座位（原方法保留）
    selectSeat(seat) {
      this.selectedSeat = seat
    },

    // 横向滚动布局
    scrollLayout(direction) {
      const el = this.$refs.visualLayout
      if (el) {
        el.scrollLeft += direction * 200
      }
    },
    
    nextStep() {
      if (this.currentStep < 2) {
        this.currentStep++
      }
    },
    
    prevStep() {
      if (this.currentStep > 0) {
        this.currentStep--
        // 离开选座步骤时取消 WebSocket 订阅
        if (this.currentStep < 2) {
          wsManager.unsubscribe()
        }
      }
    },
    
    async selectLibrary(library) {
      this.selectedLibrary = library
      this.selectedSeat = null // 清除之前选择的座位
      wsManager.subscribe(library.id) // 订阅该图书馆的座位变更
      await Promise.all([this.loadAvailableSeats(), this.loadHotSeats()])
      this.nextStep()
    },
    
    async confirmReservation() {
      try {
        const startDateTime = combineDateTime(this.reservationDate, this.startTime)
        const endDateTime = combineDateTime(this.reservationDate, this.endTime)
        
        const reservationData = {
          libraryId: this.selectedLibrary.id,
          seatId: this.selectedSeat.id,
          startTime: startDateTime,
          endTime: endDateTime
        }
        
        const res = await createReservation(reservationData)
        this.$toast.success('预约成功')
        
        // 跳转到二维码页面
        this.$router.push(`/mobile/qrcode/${res.data.orderNo}`)
      } catch (error) {
        this.$toast.fail(error.message || '预约失败')
      }
    },
    
    onDateConfirm(date) {
      this.reservationDate = formatDate(date)
      this.showDatePicker = false
    },
    
    onStartTimeConfirm(values) {
      // values 格式: [小时, ':', 分钟]
      const hour = values[0]
      const minute = values[2]
      this.startTime = `${hour}:${minute}`
      this.showStartTimePicker = false
      
      // 更新结束时间的可选范围
      this.updateEndTimeColumns()
    },
    
    onEndTimeConfirm(values) {
      // values 格式: [小时, ':', 分钟]
      const hour = values[0]
      const minute = values[2]
      this.endTime = `${hour}:${minute}`
      this.showEndTimePicker = false
    },
    
    // 根据开始时间更新结束时间的列配置
    updateEndTimeColumns() {
      if (!this.startTime || this.timeSlots.length === 0) return
      
      const [startHour, startMinute] = this.startTime.split(':').map(Number)
      // 结束时间至少比开始时间晚30分钟
      const startTotalMinutes = startHour * 60 + startMinute
      const minEndTotalMinutes = startTotalMinutes + 30
      const minEndHour = Math.floor(minEndTotalMinutes / 60)
      const minEndMinute = minEndTotalMinutes % 60
      
      // 计算所有可用小时
      const allHours = new Set()
      this.timeSlots.forEach(slot => {
        const sH = parseInt(slot.start.split(':')[0])
        const eH = parseInt(slot.end.split(':')[0])
        for (let h = sH; h < eH; h++) {
          allHours.add(h)
        }
      })
      
      // 找出比minEndHour大的所有可用小时
      const availableEndHours = Array.from(allHours)
        .filter(h => h >= minEndHour)
        .sort((a, b) => a - b)
      
      if (availableEndHours.length === 0) return
      
      // 动态计算分钟选项：如果选中的小时等于最小结束小时，只显示>=minEndMinute的分钟
      const firstHour = availableEndHours[0]
      const firstHourMinutes = firstHour === minEndHour
        ? Array.from({ length: 60 - minEndMinute }, (_, i) => String(i + minEndMinute).padStart(2, '0'))
        : Array.from({ length: 60 }, (_, i) => String(i).padStart(2, '0'))
      
      // 重新生成结束时间的小时选项
      this.endTimeColumns = [
        {
          values: availableEndHours.map(h => String(h).padStart(2, '0')),
          defaultIndex: 0
        },
        {
          values: [':']
        },
        {
          values: firstHourMinutes,
          defaultIndex: 0
        }
      ]
    },
    
    getTimeRange() {
      return `${this.reservationDate} ${this.startTime} - ${this.endTime}`
    },
    
    onTabChange(index) {
      if (index === 1) {
        this.$router.push('/mobile/my-reservations')
      } else if (index === 2) {
        this.$router.push('/mobile/profile')
      }
    },
    
    async onUserAction(action) {
      if (action.name === '退出登录') {
        try {
          await logout()
          this.$store.dispatch('clearUser')
          this.$router.push('/mobile/login')
        } catch (error) {
          this.$toast.fail('退出登录失败')
        }
      }
      this.showUserMenu = false
    }
  }
}
</script>

<style scoped>
.mobile-home {
  padding-top: 46px;
  padding-bottom: 50px;
  min-height: 100vh;
  background-color: #f2f2f7;
}

.content {
  padding: 14px;
}

.step-container {
  margin-top: 16px;
}

.button-container {
  margin-top: 30px;
  display: flex;
  gap: 10px;
}

.button-container .van-button {
  flex: 1;
}

/* 时间选择区域样式 */
.time-selection-group {
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 122, 255, 0.08);
  margin-bottom: 20px;
  background: linear-gradient(135deg, #f0f7ff 0%, #ffffff 100%);
  border: 1.5px solid #d9e8ff;
}

::v-deep .van-cell {
  background: transparent;
  padding: 16px;
  font-size: 15px;
}

::v-deep .van-field__label {
  color: #005bb5;
  font-weight: 600;
  width: 80px;
}

::v-deep .van-field__body {
  color: #323233;
}

::v-deep .van-field__control {
  color: #007AFF;
  font-weight: 500;
}

/* 时间选择器弹窗样式 */
::v-deep .van-popup--bottom {
  border-radius: 20px 20px 0 0;
}

/* 工具栏样式 */
::v-deep .van-picker__toolbar {
  padding: 16px 20px;
  background: linear-gradient(135deg, #f0f7ff 0%, #ffffff 100%);
  border-bottom: 1.5px solid #d9e8ff;
}

::v-deep .van-picker__title {
  font-weight: 600;
  color: #005bb5;
}

::v-deep .van-picker__confirm {
  color: #007AFF;
  font-weight: 600;
  font-size: 15px;
}

::v-deep .van-picker__cancel {
  color: #969799;
  font-size: 15px;
}

/* 轮播图样式 */
.banner-container {
  margin-bottom: 16px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.banner-swipe {
  height: 180px;
}

.banner-item {
  position: relative;
}

.banner-title {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 12px;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.6));
  color: white;
  font-size: 14px;
  font-weight: 500;
}

/* 公告栏样式 */
.announcement-container {
  margin-bottom: 16px;
}

.announcement-fixed {
  background: white;
  border-radius: 12px;
  padding: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.announcement-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #d9e8ff;
}

.announcement-title {
  font-size: 14px;
  font-weight: 500;
  color: #323233;
}

.announcement-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  max-height: 150px;
  overflow-y: auto;
}

.announcement-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 8px;
  cursor: pointer;
  transition: background 0.2s;
  border-radius: 6px;
}

.announcement-item:hover {
  background: #e8f4fd;
}

.announcement-type-tag {
  flex-shrink: 0;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.announcement-text {
  flex: 1;
  font-size: 13px;
  color: #666;
  line-height: 1.5;
}

.type-normal {
  background: #d9e8ff;
  color: #007AFF;
}

.type-maintenance {
  background: #fff7e6;
  color: #fa8c16;
}

.type-urgent {
  background: #fff1f0;
  color: #ff4d4f;
}

/* 图书室信息卡片 */
.room-info-card {
  background: #FFFFFF;
  border-radius: 14px;
  padding: 16px 18px;
  margin-bottom: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(0, 0, 0, 0.04);
}

.room-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.room-name-block {
  display: flex;
  align-items: center;
  gap: 10px;
}

.room-icon {
  font-size: 22px;
  color: #007AFF;
  background: rgba(0, 122, 255, 0.1);
  border-radius: 8px;
  padding: 6px;
}

.room-text h3 {
  margin: 0 0 4px;
  font-size: 18px;
  font-weight: 700;
  color: #1D1D1F;
  letter-spacing: 0.5px;
}

.room-text p {
  margin: 0;
  font-size: 13px;
  color: #86868B;
  font-weight: 500;
}

.room-divider {
  height: 1px;
  background: #F0F0F2;
  margin: 12px 0;
}

/* 座位布局图例 */
.seat-legend {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-start;
  gap: 10px 18px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #86868B;
  font-weight: 500;
}

.legend-icon {
  width: 16px;
  height: 16px;
  border-radius: 4px;
  flex-shrink: 0;
}

.legend-icon.seat-available {
  background: rgba(52, 199, 89, 0.12);
  border: 2px solid #34C759;
}

.legend-icon.seat-reserved {
  background: rgba(255, 149, 0, 0.12);
  border: 2px solid #FF9500;
}

.legend-icon.seat-occupied {
  background: rgba(255, 59, 48, 0.12);
  border: 2px solid #FF3B30;
}

.legend-icon.seat-selected-legend {
  background: #007AFF;
  border: 2px solid #0066CC;
}

/* 座位平面布局 */
.seat-layout-wrapper {
  background: white;
  border-radius: 12px;
  padding: 12px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.layout-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 12px;
}

.layout-title {
  font-size: 14px;
  font-weight: 600;
  color: #323233;
}

.layout-nav {
  font-size: 18px;
  color: #999;
  cursor: pointer;
  padding: 4px;
}

.layout-nav:active {
  color: #007AFF;
}

.visual-layout {
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
  padding-bottom: 8px;
}

.seats-grid {
  display: flex;
  flex-direction: column;
  gap: 10px;
  min-width: max-content;
}

.row-container {
  display: flex;
  align-items: center;
  gap: 8px;
}

.row-label-block {
  flex-shrink: 0;
  min-width: 36px;
  text-align: center;
}

.row-text {
  font-size: 12px;
  color: #999;
  font-weight: 500;
  writing-mode: vertical-lr;
  letter-spacing: 2px;
}

.row-seats {
  display: flex;
  gap: 8px;
}

.seat-block {
  width: 64px;
  height: 64px;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
  user-select: none;
  font-size: 11px;
  border: 2px solid transparent;
}

/* 可用座位 */
.seat-block.available.type-normal {
  background: rgba(52, 199, 89, 0.12);
  border-color: #34C759;
  color: #248A3D;
}

.seat-block.available.type-computer {
  background: rgba(0, 122, 255, 0.08);
  border-color: #007AFF;
  color: #0055B3;
}

.seat-block.available.type-quiet {
  background: rgba(175, 82, 222, 0.1);
  border-color: #AF52DE;
  color: #8944AB;
}

.seat-block.available:active {
  transform: scale(0.95);
}

/* 已预约 */
.seat-block.reserved {
  background: rgba(255, 149, 0, 0.12);
  border-color: #FF9500;
  color: #C77700;
  cursor: not-allowed;
  opacity: 0.85;
}

/* 占用中 */
.seat-block.occupied {
  background: rgba(255, 59, 48, 0.12);
  border-color: #FF3B30;
  color: #D70015;
  cursor: not-allowed;
  opacity: 0.85;
}

/* 维修/停用 */
.seat-block.maintenance,
.seat-block.disabled {
  background: #f5f5f5;
  border-color: #bdbdbd;
  color: #9e9e9e;
  cursor: not-allowed;
  opacity: 0.6;
}

/* 选中 */
.seat-block.selected {
  background: #007AFF !important;
  border-color: #0066CC !important;
  color: white !important;
  transform: scale(1.08);
  box-shadow: 0 4px 12px rgba(0, 122, 255, 0.4);
}

.seat-number {
  font-size: 13px;
  font-weight: 700;
  line-height: 1.2;
}

.seat-status-label {
  font-size: 9px;
  opacity: 0.8;
  line-height: 1;
  margin-top: 2px;
}

/* 无座位满员提示 */
.no-seats-container {
  background: white;
  border-radius: 8px;
  padding: 40px 20px;
  text-align: center;
  margin-bottom: 20px;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.no-seats-container h3 {
  margin: 0 0 8px 0;
  color: #323233;
  font-size: 16px;
  font-weight: 500;
}

.no-seats-container p {
  margin: 0 0 24px 0;
  color: #969799;
  font-size: 14px;
  line-height: 20px;
}

.bottom-button {
  margin-top: 20px;
}



.loading-container {
  background: white;
  border-radius: 8px;
  padding: 60px 20px;
  text-align: center;
  margin-bottom: 20px;
}

.search-container {
  background: white;
  border-radius: 8px;
  margin-bottom: 15px;
  overflow: hidden;
}

.search-result-info {
  padding: 10px 16px;
  background: #e8f4fd;
  color: #005bb5;
  font-size: 14px;
  border-radius: 4px;
  margin-bottom: 10px;
}

.no-result-container {
  background: white;
  border-radius: 8px;
  padding: 40px 20px;
  text-align: center;
  margin-bottom: 20px;
}

.no-result-container .empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.no-result-container h3 {
  margin: 0 0 8px 0;
  color: #323233;
  font-size: 16px;
  font-weight: 500;
}

.no-result-container p {
  margin: 0;
  color: #969799;
  font-size: 14px;
  line-height: 20px;
}

.highlight {
  background-color: #bfe0ff;
  color: #005bb5;
  padding: 1px 2px;
  border-radius: 2px;
  font-weight: 500;
}

/* 座位类型筛选栏 */
.seat-filter-bar {
  display: flex;
  gap: 8px;
  padding: 8px 0;
  margin-bottom: 8px;
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
}

.filter-btn {
  flex-shrink: 0;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 12px;
  background: #f5f5f5;
  color: #666;
  border: 1px solid #eee;
  transition: all 0.2s;
  cursor: pointer;
}

.filter-btn.active {
  background: #d9e8ff;
  color: #005bb5;
  border-color: #99ccff;
  font-weight: 500;
}

.filter-btn:active {
  transform: scale(0.95);
}

/* 座位详情弹窗 */
.seat-detail-sheet {

/* 热门推荐样式 */
.hot-tip-bar {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  margin-bottom: 12px;
  background: linear-gradient(135deg, #FFF0E8, #FFF5F2);
  border-radius: 20px;
  font-size: 12px;
  color: #FF6B3D;
  border: 1px solid rgba(255, 107, 61, 0.15);
}

.legend-icon.seat-hot-legend {
  background: linear-gradient(135deg, #ff6b6b, #f56c6c);
  border: 2px solid #f56c6c;
  animation: pulse-hot 1.5s ease-in-out infinite;
}

@keyframes pulse-hot {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.08); }
}

/* 热门座位块 */
.seat-block.hot-seat.available {
  position: relative;
  box-shadow: 0 2px 10px rgba(245, 108, 108, 0.35);
  animation: glow-hot 2s ease-in-out infinite alternate;
}

@keyframes glow-hot {
  from { box-shadow: 0 2px 10px rgba(245, 108, 108, 0.25); }
  to { box-shadow: 0 2px 16px rgba(245, 108, 108, 0.55), 0 0 4px rgba(245, 108, 108, 0.3); }
}

.hot-badge {
  position: absolute;
  top: -6px;
  right: -4px;
  width: 22px;
  height: 22px;
  line-height: 18px;
  text-align: center;
  font-size: 11px;
  z-index: 2;
}
  padding: 0 16px 20px;
}

.detail-info {
  margin-bottom: 16px;
}

.detail-actions {
  padding: 0 16px 20px;
}

.detail-actions .van-button {
  margin-top: 8px;
}
</style>