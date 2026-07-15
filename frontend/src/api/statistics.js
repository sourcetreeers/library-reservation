import request from '@/utils/request'

/**
 * 获取各图书室使用情况
 */
export function getLibraryUsage(params) {
  return request({
    url: '/admin/statistics/library-usage',
    method: 'get',
    params
  })
}

/**
 * 获取座位类型分布
 */
export function getSeatTypeDistribution(params) {
  return request({
    url: '/admin/statistics/seat-type-distribution',
    method: 'get',
    params
  })
}

/**
 * 获取预约状态统计
 */
export function getReservationStatus(params) {
  return request({
    url: '/admin/statistics/reservation-status',
    method: 'get',
    params
  })
}

/**
 * 获取近7天预约趋势
 */
export function getReservationTrend(params) {
  return request({
    url: '/admin/statistics/reservation-trend',
    method: 'get',
    params
  })
}

/**
 * 获取高峰时段分布
 */
export function getPeakHours(params) {
  return request({
    url: '/admin/statistics/peak-hours',
    method: 'get',
    params
  })
}

/**
 * 获取用户履约排行
 */
export function getUserBehavior(params) {
  return request({
    url: '/admin/statistics/user-behavior',
    method: 'get',
    params
  })
}

/**
 * 获取月度预约趋势
 */
export function getMonthlyTrend(params) {
  return request({
    url: '/admin/statistics/monthly-trend',
    method: 'get',
    params
  })
}

/**
 * 获取最受欢迎座位排行
 */
export function getPopularSeats(params) {
  return request({
    url: '/admin/statistics/popular-seats',
    method: 'get',
    params
  })
}
