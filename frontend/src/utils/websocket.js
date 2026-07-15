/**
 * WebSocket 连接管理
 * 连接后端 /ws/seat-status 端点，支持按图书馆分组订阅，增量更新
 */

const WS_BASE_URL = process.env.VUE_APP_WS_URL || `ws://${window.location.hostname}:8080`

class WebSocketManager {
  constructor() {
    this.ws = null
    this.reconnectTimer = null
    this.reconnectInterval = 5000
    this.maxReconnectAttempts = 3 // 减少重连次数，避免无意义刷屏
    this.reconnectAttempts = 0
    this.listeners = {}
    this.isConnected = false
    this.subscribedLibraryId = null
    this._enabled = true // 可外部关闭以阻止连接
    this._hasEverConnected = false // 是否曾经成功连接过
  }

  /**
   * 启用/禁用自动连接（用于不需要实时更新的场景）
   */
  setEnabled(enabled) {
    this._enabled = enabled
    if (!enabled) {
      this.disconnect()
    }
  }

  connect() {
    if (!this._enabled) return

    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      return
    }

    try {
      const url = `${WS_BASE_URL}/ws/seat-status`
      this.ws = new WebSocket(url)

      this.ws.onopen = () => {
        console.log('[WebSocket] 已连接')
        this.isConnected = true
        this._hasEverConnected = true
        this.reconnectAttempts = 0
        this._emit('connected')
        if (this.subscribedLibraryId) {
          this._sendSubscribe(this.subscribedLibraryId)
        }
      }

      this.ws.onmessage = (event) => {
        try {
          const data = JSON.parse(event.data)
          switch (data.type) {
            case 'SEAT_CHANGE':
              this._emit('seatChange', data)
              break
            case 'LIBRARY_UPDATE':
              this._emit('libraryUpdate', data)
              break
            default:
              this._emit('message', data)
          }
        } catch (e) {
          // 静默处理解析异常
        }
      }

      this.ws.onclose = () => {
        this.isConnected = false
        this._emit('disconnected')
        this._tryReconnect()
      }

      this.ws.onerror = () => {
        // 静默处理错误，不输出堆栈对象
        this._emit('error')
      }
    } catch (error) {
      this._tryReconnect()
    }
  }

  disconnect() {
    this._clearReconnectTimer()
    if (this.ws) {
      this.ws.onclose = null
      this.ws.close()
      this.ws = null
    }
    this.isConnected = false
    this.subscribedLibraryId = null
  }

  subscribe(libraryId) {
    this.subscribedLibraryId = libraryId
    if (this.isConnected) {
      this._sendSubscribe(libraryId)
    }
  }

  unsubscribe() {
    this.subscribedLibraryId = null
    if (this.isConnected) {
      this._sendUnsubscribe()
    }
  }

  on(event, callback) {
    if (!this.listeners[event]) {
      this.listeners[event] = []
    }
    this.listeners[event].push(callback)
    return () => {
      this.listeners[event] = this.listeners[event].filter(cb => cb !== callback)
    }
  }

  off(event, callback) {
    if (this.listeners[event]) {
      this.listeners[event] = this.listeners[event].filter(cb => cb !== callback)
    }
  }

  send(data) {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      this.ws.send(JSON.stringify(data))
    }
  }

  _sendSubscribe(libraryId) {
    this.send({ type: 'SUBSCRIBE', libraryId })
  }

  _sendUnsubscribe() {
    this.send({ type: 'UNSUBSCRIBE' })
  }

  _emit(event, data) {
    if (this.listeners[event]) {
      this.listeners[event].forEach(callback => {
        try {
          callback(data)
        } catch (e) {
          // 静默处理回调异常
        }
      })
    }
  }

  _tryReconnect() {
    // 如果从未成功连接过且已达最大重连次数，不再尝试（说明服务端不可用）
    if (!this._hasEverConnected && this.reconnectAttempts >= this.maxReconnectAttempts) {
      return
    }
    // 如果之前成功连接过，断线后可以多试几次
    if (this._hasEverConnected && this.reconnectAttempts >= this.maxReconnectAttempts + 2) {
      return
    }

    this._clearReconnectTimer()
    this.reconnectAttempts++
    this.reconnectTimer = setTimeout(() => {
      this.connect()
    }, this.reconnectInterval)
  }

  _clearReconnectTimer() {
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer)
      this.reconnectTimer = null
    }
  }
}

const wsManager = new WebSocketManager()

export default wsManager
