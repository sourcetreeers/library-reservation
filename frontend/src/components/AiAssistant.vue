<template>
  <div class="ai-assistant-container">
    <!-- 浮动按钮 - 收起状态 -->
    <transition name="fade">
      <div v-if="!isOpen" class="ai-float-button" @click="toggleChat" title="AI智能助手">
        <i class="el-icon-chat-dot-round"></i>
        <span class="badge" v-if="unreadCount > 0">{{ unreadCount > 99 ? '99+' : unreadCount }}</span>
      </div>
    </transition>

    <!-- 聊天窗口 -->
    <transition name="slide-up">
      <div v-if="isOpen" class="chat-window">
        <!-- 头部 -->
        <div class="chat-header">
          <div class="header-left">
            <div class="avatar">
              <img src="@/assets/ai-avatar.png" alt="AI" v-if="false" />
              <i class="el-icon-cpu" v-else></i>
            </div>
            <div class="title-area">
              <h3>🤖 AI 智能助手</h3>
              <span class="status" :class="{ online: isOnline }">
                {{ isOnline ? '● 在线' : '● 离线' }}
              </span>
            </div>
          </div>
          <div class="header-actions">
            <el-button 
              size="mini" 
              icon="el-icon-delete" 
              circle 
              @click="clearHistory"
              title="清空对话"></el-button>
            <el-button 
              size="mini" 
              icon="el-icon-close" 
              circle 
              @click="toggleChat"
              title="关闭"></el-button>
          </div>
        </div>

        <!-- 消息列表区域 -->
        <div class="chat-messages" ref="messageContainer">
          <!-- 欢迎消息 -->
          <div v-if="messages.length === 0" class="welcome-message">
            <div class="welcome-card">
              <h4>👋 您好！我是图书馆系统的 AI 智能助手</h4>
              <p>我可以帮助您完成以下两类服务：</p>
              <div class="quick-actions">
                <div class="action-group">
                  <span class="group-title">📖 知识问答</span>
                  <el-button 
                    size="mini" 
                    type="success" 
                    plain 
                    round
                    @click="sendQuickMessage('预约规则是什么？')">预约规则</el-button>
                  <el-button 
                    size="mini" 
                    type="success" 
                    plain 
                    round
                    @click="sendQuickMessage('违规会怎么处理？')">违规处理</el-button>
                  <el-button 
                    size="mini" 
                    type="success" 
                    plain 
                    round
                    @click="sendQuickMessage('座位有哪些类型？')">座位类型</el-button>
                  <el-button 
                    size="mini" 
                    type="success" 
                    plain 
                    round
                    @click="sendQuickMessage('开放时间是什么时候？')">开放时间</el-button>
                  <el-button 
                    size="mini" 
                    type="success" 
                    plain 
                    round
                    @click="sendQuickMessage('忘记签到怎么办？')">常见问题</el-button>
                </div>
                <div class="action-group">
                  <span class="group-title">📋 数据查询</span>
                  <el-button 
                    size="mini" 
                    type="primary" 
                    plain 
                    round
                    @click="sendQuickMessage('查询所有用户列表')">查询用户</el-button>
                  <el-button 
                    size="mini" 
                    type="primary" 
                    plain 
                    round
                    @click="sendQuickMessage('查看今天的预约记录')">预约记录</el-button>
                  <el-button 
                    size="mini" 
                    type="primary" 
                    plain 
                    round
                    @click="sendQuickMessage('查询违规记录')">违规记录</el-button>
                </div>
                <div class="action-group">
                  <span class="group-title">✏️ 数据操作</span>
                  <el-button 
                    size="mini" 
                    type="warning" 
                    plain 
                    round
                    @click="sendQuickMessage('创建一条新公告，标题是测试')">创建公告</el-button>
                  <el-button 
                    size="mini" 
                    type="danger" 
                    plain 
                    round
                    @click="sendQuickMessage('禁用用户ID为1的账号')">禁用用户</el-button>
                </div>
              </div>
              <p class="tip">💡 提示：直接输入自然语言即可，例如"帮我查一下admin用户的详细信息"或"预约最多几小时？"</p>
            </div>
          </div>

          <!-- 消息列表 -->
          <div 
            v-for="(msg, index) in messages" 
            :key="index" 
            class="message-item"
            :class="msg.role">
            <!-- 用户消息 -->
            <div v-if="msg.role === 'user'" class="message-content user-message">
              <div class="bubble">{{ msg.content }}</div>
              <span class="time">{{ msg.time }}</span>
            </div>

            <!-- AI消息 -->
            <div v-if="msg.role === 'assistant'" class="message-content ai-message">
              <div class="avatar-small"><i class="el-icon-cpu"></i></div>
              <div class="bubble">
                <!-- 文本消息 -->
                <div v-if="msg.type === 'text'" class="text-content" v-html="formatMessage(msg.content)"></div>
                
                <!-- 操作结果 -->
                <div v-else-if="msg.type === 'operation_result'" class="operation-result">
                  <div class="result-text" v-html="formatMessage(msg.reply || msg.content)"></div>
                  <div v-if="msg.data" class="result-data">
                    <el-collapse v-if="hasDataRecords(msg.data)">
                      <el-collapse-item title="📊 查看详细数据" name="data">
                        <pre class="data-pre">{{ formatDataDisplay(msg.data) }}</pre>
                      </el-collapse-item>
                    </el-collapse>
                  </div>
                  <div class="function-tag">
                    <el-tag size="mini" type="info">🔧 {{ msg.functionCalled || '系统操作' }}</el-tag>
                  </div>
                </div>

                <!-- 需要确认的操作 -->
                <div v-else-if="msg.type === 'confirmation_required'" class="confirmation-box">
                  <div class="confirm-text" v-html="formatMessage(msg.reply || msg.content)"></div>
                  <div class="confirm-actions">
                    <el-button 
                      type="danger" 
                      size="small" 
                      :loading="isConfirming"
                      @click="confirmOperation(msg.operationId)">
                      ✅ 确认执行
                    </el-button>
                    <el-button 
                      size="small" 
                      @click="cancelOperation(index)">
                      ❌ 取消操作
                    </el-button>
                  </div>
                  <div class="function-tag">
                    <el-tag size="mini" type="warning">⚠️ 待确认: {{ msg.functionCalled }}</el-tag>
                  </div>
                </div>

                <!-- 错误消息 -->
                <div v-else-if="msg.type === 'error'" class="error-message">
                  {{ msg.content }}
                </div>
                
                <!-- 默认显示 -->
                <div v-else class="text-content" v-html="formatMessage(msg.content)"></div>
              </div>
              <span class="time">{{ msg.time }}</span>
            </div>

            <!-- 加载动画 -->
            <div v-if="isLoading && index === messages.length - 1" class="message-item assistant">
              <div class="message-content ai-message">
                <div class="avatar-small"><i class="el-icon-cpu"></i></div>
                <div class="bubble typing-indicator">
                  <span></span><span></span><span></span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 输入区域 -->
        <div class="chat-input-area">
          <div class="input-wrapper">
            <el-input
              v-model="inputMessage"
              type="textarea"
              :rows="2"
              placeholder="输入您想要执行的操作...（如：查询所有禁用的用户）"
              @keydown.enter.native.exact="sendMessage"
              :disabled="isLoading"
              resize="none">
            </el-input>
          </div>
          <div class="input-actions">
            <el-button 
              type="primary" 
              :loading="isLoading"
              :disabled="!inputMessage.trim()"
              @click="sendMessage"
              icon="el-icon-s-promotion"
              round>
              发送
            </el-button>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script>
import { chat, confirmOperation as apiConfirmOperation, clearHistory as apiClearHistory } from '@/api/ai-assistant'

export default {
  name: 'AiAssistant',
  
  data() {
    return {
      isOpen: false,
      inputMessage: '',
      messages: [],
      isLoading: false,
      isConfirming: false,
      isOnline: true,
      unreadCount: 0
    }
  },
  
  mounted() {
    // 可以在这里初始化一些配置或检查连接状态
    this.checkConnection()
  },

  methods: {
    /**
     * 切换聊天窗口显示/隐藏
     */
    toggleChat() {
      this.isOpen = !this.isOpen
      if (this.isOpen) {
        this.unreadCount = 0
        this.$nextTick(() => {
          this.scrollToBottom()
        })
      }
    },

    /**
     * 发送消息
     */
    async sendMessage() {
      const message = this.inputMessage.trim()
      if (!message || this.isLoading) return

      // 添加用户消息到列表
      this.addMessage({
        role: 'user',
        content: message,
        time: this.getCurrentTime(),
        type: 'text'
      })

      this.inputMessage = ''
      this.isLoading = true

      try {
        const response = await chat({ message })
        
        if (response.code === 200 && response.data) {
          const data = response.data
          this.addMessage({
            role: 'assistant',
            content: data.reply || data.message || '',
            time: this.getCurrentTime(),
            type: data.type || 'text',
            data: data.data || null,
            functionCalled: data.functionCalled || null,
            operationId: data.operationId || null
          })
          
          // 如果需要确认且窗口未打开，增加未读计数
          if (data.type === 'confirmation_required' && !this.isOpen) {
            this.unreadCount++
          }
        } else {
          this.addMessage({
            role: 'assistant',
            content: response.msg || '请求失败，请稍后重试',
            time: this.getCurrentTime(),
            type: 'error'
          })
        }
      } catch (error) {
        console.error('发送消息失败:', error)
        this.addMessage({
          role: 'assistant',
          content: '网络异常，请检查网络连接后重试',
          time: this.getCurrentTime(),
          type: 'error'
        })
      } finally {
        this.isLoading = false
        this.$nextTick(() => {
          this.scrollToBottom()
        })
      }
    },

    /**
     * 确认敏感操作
     */
    async confirmOperation(operationId) {
      if (!operationId || this.isConfirming) return
      
      this.isConfirming = true
      
      try {
        const response = await apiConfirmOperation({ operationId })
        
        if (response.code === 200 && response.data) {
          const data = response.data
          // 更新最后一条消息为结果（或者追加新消息）
          this.addMessage({
            role: 'assistant',
            content: data.reply || data.message || '操作已执行',
            time: this.getCurrentTime(),
            type: data.type || 'operation_result',
            data: data.data || null,
            functionCalled: data.functionCalled || null
          })
        } else {
          this.addMessage({
            role: 'assistant',
            content: response.msg || '操作执行失败',
            time: this.getCurrentTime(),
            type: 'error'
          })
        }
      } catch (error) {
        console.error('确认操作失败:', error)
        this.addMessage({
          role: 'assistant',
          content: '操作确认失败，请重试',
          time: this.getCurrentTime(),
          type: 'error'
        })
      } finally {
        this.isConfirming = false
        this.$nextTick(() => {
          this.scrollToBottom()
        })
      }
    },

    /**
     * 取消操作
     */
    cancelOperation(messageIndex) {
      this.messages[messageIndex].type = 'cancelled'
      this.messages[messageIndex].content = '❌ 操作已取消'
      this.$forceUpdate()
      
      this.addMessage({
        role: 'assistant',
        content: '好的，该操作已取消。如果您还有其他需求，请随时告诉我！',
        time: this.getCurrentTime(),
        type: 'text'
      })
    },

    /**
     * 快捷发送预设消息
     */
    sendQuickMessage(message) {
      this.inputMessage = message
      this.sendMessage()
    },

    /**
     * 清空对话历史
     */
    async clearHistory() {
      try {
        await apiClearHistory()
        this.messages = []
        this.$message.success('对话历史已清空')
      } catch (error) {
        console.error('清空失败:', error)
        this.$message.error('清空失败')
      }
    },

    /**
     * 检查连接状态（可选实现）
     */
    checkConnection() {
      // TODO: 可以实现心跳检测机制
      this.isOnline = navigator.onLine
    },

    // ==================== 辅助方法 ====================

    addMessage(message) {
      this.messages.push(message)
    },

    getCurrentTime() {
      const now = new Date()
      return `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`
    },

    scrollToBottom() {
      const container = this.$refs.messageContainer
      if (container) {
        container.scrollTop = container.scrollHeight
      }
    },

    formatMessage(content) {
      if (!content) return ''
      // 简单的换行处理
      return content.replace(/\n/g, '<br/>')
    },

    hasDataRecords(data) {
      return data && (data.records || data.total !== undefined)
    },

    formatDataDisplay(data) {
      if (!data) return ''
      try {
        return JSON.stringify(data, null, 2)
      } catch (e) {
        return String(data)
      }
    }
  }
}
</script>

<style scoped>
.ai-assistant-container {
  position: fixed;
  z-index: 9999;
  bottom: 20px;
  right: 20px;
}

/* ====== 浮动按钮样式 ====== */
.ai-float-button {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
  transition: all 0.3s ease;
  position: relative;
}

.ai-float-button:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
}

.ai-float-button i {
  font-size: 28px;
}

.ai-float-button .badge {
  position: absolute;
  top: -5px;
  right: -5px;
  background: #f56c6c;
  color: white;
  border-radius: 10px;
  padding: 2px 6px;
  font-size: 12px;
  min-width: 18px;
  text-align: center;
}

/* ====== 聊天窗口样式 ====== */
.chat-window {
  width: 400px;
  height: 600px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 头部 */
.chat-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar i {
  font-size: 22px;
}

.title-area h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.status {
  font-size: 12px;
  opacity: 0.9;
}

.status.online {
  color: #67c23a;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.header-actions .el-button {
  background: rgba(255, 255, 255, 0.2);
  border-color: transparent;
  color: white;
}

.header-actions .el-button:hover {
  background: rgba(255, 255, 255, 0.3);
}

/* 消息区域 */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #f7f8fc;
}

.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: #dcdfe6;
  border-radius: 3px;
}

/* 欢迎消息 */
.welcome-message {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100% - 100px);
}

.welcome-card {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  max-width: 100%;
}

.welcome-card h4 {
  margin: 0 0 12px 0;
  color: #303133;
  font-size: 16px;
}

.welcome-card p {
  margin: 8px 0;
  color: #606266;
  font-size: 14px;
}

.quick-actions {
  margin: 20px 0;
}

.action-group {
  margin-bottom: 12px;
}

.group-title {
  display: block;
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
}

.action-group .el-button {
  margin-right: 8px;
  margin-bottom: 8px;
}

.tip {
  font-size: 12px !important;
  color: #b0b3b8 !important;
  font-style: italic;
}

/* 消息项 */
.message-item {
  margin-bottom: 16px;
  display: flex;
}

.message-item.user {
  justify-content: flex-end;
}

.message-content {
  max-width: 85%;
  display: flex;
  flex-direction: column;
}

.message-item.user .message-content {
  align-items: flex-end;
}

.bubble {
  padding: 12px 16px;
  border-radius: 18px;
  word-wrap: break-word;
  line-height: 1.5;
  font-size: 14px;
  position: relative;
}

.user-message .bubble {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-bottom-right-radius: 4px;
}

.ai-message {
  align-items: flex-start;
  gap: 8px;
}

.avatar-small {
  width: 32px;
  height: 32px;
  min-width: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-small i {
  font-size: 16px;
}

.ai-message .bubble {
  background: white;
  color: #303133;
  border: 1px solid #e4e7ed;
  border-bottom-left-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.time {
  font-size: 11px;
  color: #c0c4cc;
  margin-top: 4px;
  padding: 0 8px;
}

/* 文本内容 */
.text-content {
  line-height: 1.6;
}

/* 操作结果 */
.operation-result {
  width: 100%;
}

.result-text {
  margin-bottom: 12px;
  line-height: 1.6;
}

.result-data {
  margin-top: 12px;
}

.data-pre {
  background: #f5f7fa;
  padding: 12px;
  border-radius: 8px;
  font-size: 12px;
  overflow-x: auto;
  line-height: 1.5;
  color: #606266;
  max-height: 200px;
  overflow-y: auto;
}

.function-tag {
  margin-top: 10px;
}

/* 确认框 */
.confirmation-box {
  width: 100%;
}

.confirm-text {
  margin-bottom: 16px;
  line-height: 1.6;
  color: #e6a23c;
  font-weight: 500;
}

.confirm-actions {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}

/* 错误消息 */
.error-message {
  color: #f56c6c;
  font-style: italic;
}

/* 加载动画 */
.typing-indicator {
  display: flex;
  gap: 6px;
  padding: 16px 20px !important;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #909399;
  animation: bounce 1.4s infinite ease-in-out both;
}

.typing-indicator span:nth-child(1) { animation-delay: -0.32s; }
.typing-indicator span:nth-child(2) { animation-delay: -0.16s; }

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}

/* 取消状态 */
.message-item .cancelled .bubble {
  color: #909399;
  text-decoration: line-through;
  opacity: 0.7;
}

/* 输入区域 */
.chat-input-area {
  padding: 16px;
  background: white;
  border-top: 1px solid #e4e7ed;
}

.input-wrapper {
  margin-bottom: 10px;
}

.input-wrapper >>> .el-textarea__inner {
  border-radius: 12px;
  resize: none;
  font-size: 14px;
}

.input-actions {
  display: flex;
  justify-content: flex-end;
}

.input-actions .el-button {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: transparent;
}

.input-actions .el-button:hover {
  opacity: 0.9;
}

.input-actions .el-button.is-disabled {
  background: #c0c4cc;
  border-color: #c0c4cc;
}

/* 过渡动画 */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter, .fade-leave-to {
  opacity: 0;
}

.slide-up-enter-active, .slide-up-leave-active {
  transition: all 0.3s ease;
}

.slide-up-enter, .slide-up-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

/* 响应式适配 */
@media (max-width: 480px) {
  .chat-window {
    width: calc(100vw - 40px);
    height: calc(100vh - 120px);
  }

  .ai-assistant-container {
    right: 10px;
    bottom: 10px;
  }
}
</style>
