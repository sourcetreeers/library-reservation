<template>
  <div class="operation-logs">
    <div class="page-hero">
      <div>
        <span class="page-kicker">Logs</span>
        <h2>操作日志</h2>
        <p>记录系统管理员的操作行为</p>
      </div>
    </div>

    <!-- 筛选栏 -->
    <div class="content-card search-area">
      <el-form :inline="true" :model="query" size="small">
        <el-form-item label="操作人">
          <el-input v-model="query.operateUser" placeholder="输入管理员姓名" clearable style="width:160px" />
        </el-form-item>
        <el-form-item label="操作类型">
          <el-select v-model="query.operateType" placeholder="全部" clearable style="width:140px">
            <el-option label="登录" value="登录" />
            <el-option label="新增" value="新增" />
            <el-option label="修改" value="修改" />
            <el-option label="删除" value="删除" />
            <el-option label="导出" value="导出" />
            <el-option label="查询" value="查询" />
            <el-option label="取消" value="取消" />
            <el-option label="签到" value="签到" />
            <el-option label="处理" value="处理" />
            <el-option label="豁免" value="豁免" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
            style="width:240px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="fetchData">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 数据表格 -->
    <div class="content-card data-card">
      <el-table :data="tableData" v-loading="loading" stripe border style="width:100%">
        <el-table-column prop="id" label="#" width="60" align="center" />
        <el-table-column prop="operateUser" label="操作人" width="110" align="center" show-overflow-tooltip />
        <el-table-column prop="operateType" label="操作类型" width="90" align="center">
          <template slot-scope="{row}">
            <el-tag :type="typeTagMap[row.operateType] || ''" size="mini">{{ row.operateType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="targetType" label="操作对象" width="110" align="center" show-overflow-tooltip />
        <el-table-column prop="ipAddress" label="IP地址" width="140" align="center" font-size="12px" />
        <el-table-column prop="createTime" label="操作时间" width="160" align="center" sortable />
        <el-table-column label="详情" min-width="200" show-overflow-tooltip>
          <template slot-scope="{row}">
            <el-link type="primary" @click="showDetail(row)">查看详情</el-link>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrap">
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

    <!-- 详情抽屉 -->
    <el-drawer :visible.sync="drawerVisible" size="480px" :with-header="false" custom-class="log-detail-drawer">
      <div class="drawer-header">
        <div class="header-icon"><i class="el-icon-document"></i></div>
        <div>
          <h3>操作日志详情</h3>
          <p class="header-sub">记录 #{{ currentLog ? currentLog.id : '' }}</p>
        </div>
      </div>

      <div class="detail-body" v-if="currentLog">
        <!-- 操作者信息 -->
        <section class="detail-section">
          <div class="section-title"><i class="el-icon-user"></i> 操作者信息</div>
          <div class="info-grid">
            <div class="info-item">
              <i class="el-icon-s-custom"></i>
              <span class="info-label">操作人</span>
              <span class="info-value highlight">{{ currentLog.operateUser }}</span>
            </div>
            <div class="info-item">
              <i class="el-icon-time"></i>
              <span class="info-label">操作时间</span>
              <span class="info-value">{{ currentLog.createTime }}</span>
            </div>
          </div>
        </section>

        <!-- 操作内容 -->
        <section class="detail-section">
          <div class="section-title"><i class="el-icon-edit-outline"></i> 操作内容</div>
          <div class="tag-group">
            <span class="detail-tag type-tag" :class="'type-' + tagType(currentLog.operateType)">
              <i class="el-icon-price-tag"></i>{{ currentLog.operateType }}
            </span>
            <span v-if="currentLog.targetType" class="detail-tag target-tag">
              <i class="el-icon-folder-opened"></i>{{ currentLog.targetType }}
            </span>
            <span v-if="currentLog.targetId" class="detail-tag id-tag">
              <i class="el-icon-key"></i>ID: {{ currentLog.targetId }}
            </span>
          </div>
        </section>

        <!-- 操作描述 -->
        <section class="detail-section">
          <div class="section-title"><i class="el-icon-info"></i> 操作描述</div>
          <div class="desc-card">
            <p v-html="parseDetailText(currentLog.detail, currentLog)"></p>
          </div>
        </section>

        <!-- 技术信息 -->
        <section class="detail-section tech-section">
          <div class="section-title"><i class="el-icon-monitor"></i> 技术信息</div>
          <div class="tech-grid">
            <div class="tech-item">
              <span class="tech-label"><i class="el-icon-location-information"></i> IP 地址</span>
              <span class="tech-value mono">{{ currentLog.ipAddress || '-' }}</span>
            </div>
          </div>
          <div class="tech-item ua-wrap">
            <span class="tech-label"><i class="el-icon-cpu"></i> 浏览器信息</span>
            <div class="ua-box">
              <span class="browser-icon" :class="browserIconClass(currentLog.userAgent)">
                {{ browserIconEmoji(currentLog.userAgent) }}
              </span>
              <span class="ua-text">{{ parseUserAgent(currentLog.userAgent) }}</span>
            </div>
          </div>
        </section>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { getOperationLogPage } from '@/api/operation-log'

export default {
  name: 'OperationLogs',
  data() {
    return {
      loading: false,
      tableData: [],
      query: { operateUser: '', operateType: '' },
      dateRange: null,
      pagination: { current: 1, size: 10, total: 0 },
      drawerVisible: false,
      currentLog: null,
      typeTagMap: { '登录': '', '新增': 'success', '修改': 'warning', '删除': 'danger', '导出': 'info' }
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    async fetchData() {
      this.loading = true
      try {
        const params = {
          current: this.pagination.current,
          size: this.pagination.size,
          operateUser: this.query.operateUser || undefined,
          operateType: this.query.operateType || undefined,
          startTime: this.dateRange ? this.dateRange[0] : undefined,
          endTime: this.dateRange ? this.dateRange[1] : undefined
        }
        const res = await getOperationLogPage(params)
        if (res.code === 200) {
          this.tableData = res.data.records
          this.pagination.total = res.data.total
        }
      } finally {
        this.loading = false
      }
    },
    resetQuery() {
      this.query = { operateUser: '', operateType: '' }
      this.dateRange = null
      this.pagination.current = 1
      this.pagination.size = 10
      this.fetchData()
    },
    handleSizeChange(val) {
      this.pagination.size = val
      this.fetchData()
    },
    handleCurrentChange(val) {
      this.pagination.current = val
      this.fetchData()
    },
    showDetail(row) {
      this.currentLog = row
      this.drawerVisible = true
    },
    tagType(type) {
      const map = { '登录': 'login', '新增': 'add', '修改': 'edit', '删除': 'delete', '导出': 'export', '查询': 'query', '取消': 'cancel', '签到': 'checkin', '处理': 'handle', '豁免': 'exempt', '其他': 'other' }
      return map[type] || 'other'
    },
    parseDetailText(detail, log) {
      if (!detail) return '<span class="desc-empty">暂无详细描述</span>'
      try {
        const obj = typeof detail === 'string' ? JSON.parse(detail) : detail
        return this.describeAction(obj, log)
      } catch (e) {
        return detail
      }
    },
    describeAction(obj, log) {
      if (!obj || typeof obj !== 'object') return log.operateType || '-'
      const parts = []
      // 操作方法描述
      if (obj.method) {
        const methodMap = {
          login: '用户登录系统',
          logout: '退出登录',
          add: '新增数据',
          update: '修改数据',
          delete: '删除数据',
          toggleStatus: obj.success ? '切换状态：启用' : '切换状态：禁用',
          export: '导出数据',
          query: '查询数据',
          cancel: '取消操作',
          checkIn: '签到操作',
          handle: '处理申诉',
          exempt: '豁免处罚'
        }
        parts.push(methodMap[obj.method] || `执行操作：${obj.method}`)
      }
      // 操作结果
      if (obj.success === true) {
        parts.push('<span class="desc-success">操作成功</span>')
      } else if (obj.success === false) {
        parts.push('<span class="desc-fail">操作失败</span>')
      }
      // 参数描述
      if (obj.params && Object.keys(obj.params).length > 0) {
        const paramDesc = this.describeParams(obj.params)
        if (paramDesc) parts.push(`<span class="desc-param">参数：${paramDesc}</span>`)
      }
      // 描述信息
      if (obj.description) {
        parts.push(obj.description)
      }
      return parts.length > 0 ? parts.join(' · ') : `<span class="desc-empty">无详细描述</span>`
    },
    describeParams(params) {
      const items = []
      for (const [, v] of Object.entries(params)) {
        if (v === null || v === undefined) continue
        let display = v
        if (Array.isArray(v)) display = v.length > 0 ? `${v.length}项` : '空'
        else if (typeof v === 'object') display = JSON.stringify(v)
        items.push(display)
      }
      return items.join(', ') || '无'
    },
    parseUserAgent(ua) {
      if (!ua || ua === '-') return '未知浏览器'
      let result = ''
      if (ua.includes('Chrome')) result += 'Chrome '
      else if (ua.includes('Firefox')) result += 'Firefox '
      else if (ua.includes('Safari')) result += 'Safari '
      else if (ua.includes('Edge')) result += 'Edge '
      else if (ua.includes('MicroMessenger')) result += '微信内置浏览器 '
      else { result += '未知浏览器 ' }

      if (ua.includes('Windows NT 10')) result += '/ Windows 10/11'
      else if (ua.includes('Windows NT 6.3')) result += '/ Windows 8.1'
      else if (ua.includes('Windows')) result += '/ Windows'
      else if (ua.includes('Mac OS X')) result += '/ macOS'
      else if (ua.includes('Android')) result += '/ Android'
      else if (ua.includes('iPhone')) result += '/ iOS'

      return result.trim()
    },
    browserIconClass(ua) {
      if (!ua) return 'unknown'
      if (ua.includes('Chrome')) return 'chrome'
      if (ua.includes('Firefox')) return 'firefox'
      if (ua.includes('Edge')) return 'edge'
      if (ua.includes('Safari') && !ua.includes('Chrome')) return 'safari'
      if (ua.includes('MicroMessenger')) return 'wechat'
      return 'unknown'
    },
    browserIconEmoji(ua) {
      if (!ua) return '?'
      if (ua.includes('Chrome')) return 'C'
      if (ua.includes('Firefox')) return 'F'
      if (ua.includes('Edge')) return 'E'
      if (ua.includes('Safari') && !ua.includes('Chrome')) return 'S'
      if (ua.includes('MicroMessenger')) return 'W'
      return 'B'
    }
  }
}
</script>

<style scoped>
.operation-logs {
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
  color: #AF52DE;
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

.pagination-wrap {
  text-align: right;
  margin-top: 16px;
}

/* ========== 抽屉样式（美化）========== */

/* 抽屉头部 */
.drawer-header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 28px 24px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}
.header-icon {
  width: 52px; height: 52px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  display: flex; align-items: center; justify-content: center;
  font-size: 24px;
}
.drawer-header h3 {
  margin: 0; font-size: 20px; font-weight: 700; color: #fff;
}
.header-sub { margin: 4px 0 0; font-size: 13px; opacity: 0.8; }

/* 抽屉主体 */
.detail-body { padding: 0 20px 28px; }

/* 分区 */
.detail-section {
  margin-top: 20px;
  padding: 18px;
  background: #FAFBFC;
  border-radius: 12px;
  border: 1px solid #E8EAED;
}
.section-title {
  display: flex; align-items: center; gap: 6px;
  font-size: 13px; font-weight: 600;
  color: #5F6368; text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 14px;
  padding-bottom: 10px;
  border-bottom: 1px solid rgba(0,0,0,0.06);
}

/* 信息网格 */
.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}
.info-item {
  display: flex; flex-direction: column; gap: 3px;
  padding: 10px 12px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #E8EAED;
}
.info-item i { font-size: 16px; color: #667eea; }
.info-label { font-size: 11px; color: #9AA0A6; }
.info-value { font-size: 14px; font-weight: 500; color: #202124; }
.info-value.highlight { color: #667eea; font-weight: 700; }

/* 标签组 */
.tag-group { display: flex; flex-wrap: wrap; gap: 8px; }
.detail-tag {
  display: inline-flex; align-items: center; gap: 4px;
  padding: 6px 12px; border-radius: 20px;
  font-size: 13px; font-weight: 500;
}
.detail-tag i { font-size: 12px; }

.type-tag.login      { background: #E8F0FE; color: #1967D2; }
.type-tag.add       { background: #E6F4EA; color: #137333; }
.type-tag.edit      { background: #FEF7E0; color: #B06000; }
.type-tag.delete    { background: #FCE8E6; color: #C5221F; }
.type-tag.export    { background: #E8EAED; color: #5F6368; }
.type-tag.query     { background: #E8F0FE; color: #1967D2; }
.type-tag.cancel    { background: #FEF7E0; color: #B06000; }
.type-tag.checkin   { background: #E6F4EA; color: #137333; }
.type-tag.handle    { background: #F3E8FD; color: #8E24AA; }
.type-tag.exempt    { background: #FFF3E0; color: #E65100; }
.type-tag.other     { background: #F1F3F4; color: #80868B; }

.target-tag { background: #EEF6FF; color: #0366D6; }
.id-tag     { background: #F6F8FA; color: #586069; font-family: monospace; font-size: 12px; }

/* 描述卡片 */
.desc-card {
  padding: 14px 16px;
  background: #fff;
  border-radius: 8px;
  border: 1px dashed #CDD2DA;
  line-height: 1.7;
  font-size: 14px; color: #3C4043;
}
.desc-card p { margin: 0; }
.desc-empty { color: #BCC0C4; font-style: italic; }
.desc-success { color: #137333; font-weight: 600; }
.desc-fail { color: #C5221F; font-weight: 600; }
.desc-param { color: #5F6368; font-family: monospace; font-size: 12px; background: #F1F3F4; padding: 1px 5px; border-radius: 4px; }

/* 技术信息区 */
.tech-section .section-title { border-bottom-color: transparent; }
.tech-grid { margin-bottom: 12px; }
.tech-item { margin-bottom: 10px; }
.tech-label {
  display: flex; align-items: center; gap: 5px;
  font-size: 11px; color: #9AA0A6; font-weight: 500;
  margin-bottom: 4px; text-transform: uppercase;
}
.tech-label i { color: #80868B; font-size: 13px; }
.tech-value { font-size: 14px; color: #202124; }
.tech-value.mono { font-family: 'SF Mono', Consolas, monospace; font-size: 13px; letter-spacing: 0.3px; }

.ua-wrap { margin-top: 12px; }
.ua-box {
  display: flex; align-items: center; gap: 10px;
  padding: 10px 12px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #E8EAED;
}
.browser-icon {
  width: 32px; height: 32px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-weight: 800; font-size: 14px; color: #fff; flex-shrink: 0;
}
.browser-icon.chrome { background: #4285F4; }
.browser-icon.firefox { background: #FF7139; }
.browser-icon.edge    { background: #0078D7; }
.browser-icon.safari  { background: #007AFF; }
.browser-icon.wechat  { background: #07C160; }
.browser-icon.unknown { background: #9AA0A6; }
.ua-text { font-size: 12px; color: #5F6368; word-break: break-all; }
</style>
