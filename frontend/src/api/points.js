import request from '@/utils/request'

/**
 * 获取当前用户积分信息
 */
export function getMyPoints() {
  return request({
    url: '/points/my-points',
    method: 'get'
  })
}

/**
 * 获取指定用户积分信息（管理员）
 */
export function getUserPoints(userId) {
  return request({
    url: `/points/user/${userId}`,
    method: 'get'
  })
}

/**
 * 获取积分变动记录
 */
export function getPointsRecords(params) {
  return request({
    url: '/points/records',
    method: 'get',
    params
  })
}

/**
 * 手动调整积分（管理员）
 */
export function adjustPoints(data) {
  return request({
    url: '/points/adjust',
    method: 'post',
    params: data
  })
}
