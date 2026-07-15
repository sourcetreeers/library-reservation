import request from '@/utils/request'

/**
 * AI智能助手 API
 * 
 * 提供与AI助手交互的所有接口：
 * - 发送消息（自然语言）
 * - 确认敏感操作
 * - 获取可用函数列表
 * - 清空对话历史
 */

/**
 * 发送消息给AI助手
 * @param {Object} data - { message: string }
 * @returns {Promise} AI响应结果
 */
export function chat(data) {
  return request({
    url: '/system-admin/ai-assistant/chat',
    method: 'post',
    data
  })
}

/**
 * 确认敏感操作（二次确认）
 * @param {Object} data - { operationId: string }
 * @returns {Promise} 操作执行结果
 */
export function confirmOperation(data) {
  return request({
    url: '/system-admin/ai-assistant/confirm',
    method: 'post',
    data
  })
}

/**
 * 获取可用的Function列表
 * @returns {Promise} 函数定义列表
 */
export function getAvailableFunctions() {
  return request({
    url: '/system-admin/ai-assistant/functions',
    method: 'get'
  })
}

/**
 * 清空对话历史（重置AI上下文）
 * @returns {Promise}
 */
export function clearHistory() {
  return request({
    url: '/system-admin/ai-assistant/clear-history',
    method: 'post'
  })
}
