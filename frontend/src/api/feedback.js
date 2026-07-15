import request from '@/utils/request'

// 管理员接口
export function getFeedbackPage(params) {
  return request({ url: '/admin/feedback/page', method: 'get', params })
}

export function replyFeedback(id, data) {
  return request({ url: `/admin/feedback/${id}/reply`, method: 'put', data })
}

// 学生接口
export function submitFeedback(data) {
  return request({ url: '/mobile/feedback/submit', method: 'post', data })
}

export function getMyFeedbacks(params) {
  return request({ url: '/mobile/feedback/my', method: 'get', params })
}
