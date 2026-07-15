import request from '@/utils/request'

/**
 * 通知消息相关API
 */

// 分页查询通知列表
export function getNotificationList(params) {
  return request({
    url: '/notification/list',
    method: 'get',
    params
  })
}

// 获取未读通知数量
export function getUnreadCount() {
  return request({
    url: '/notification/unread-count',
    method: 'get'
  })
}

// 标记通知为已读
export function markNotificationRead(id) {
  return request({
    url: `/notification/${id}/read`,
    method: 'put'
  })
}

// 标记所有通知为已读
export function markAllNotificationsRead() {
  return request({
    url: '/notification/read-all',
    method: 'put'
  })
}
