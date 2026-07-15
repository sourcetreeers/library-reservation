import request from '@/utils/request'

/**
 * 预约相关API
 */

// 创建预约
export function createReservation(data) {
  return request({
    url: '/reservation',
    method: 'post',
    data
  })
}

// 取消预约
export function cancelReservation(id) {
  return request({
    url: `/reservation/${id}/cancel`,
    method: 'put'
  })
}

// 扫码签到
export function checkIn(orderNo) {
  return request({
    url: '/reservation/checkin',
    method: 'post',
    params: { orderNo }
  })
}

// 分页查询预约记录
export function getReservationPage(params) {
  return request({
    url: '/reservation/page',
    method: 'get',
    params
  })
}

// 根据流水号查询预约记录
export function getReservationByOrderNo(orderNo) {
  return request({
    url: `/reservation/order/${orderNo}`,
    method: 'get'
  })
}

// 根据流水号查询预约记录详情
export function getReservationDetailByOrderNo(orderNo) {
  return request({
    url: `/reservation/detail/${orderNo}`,
    method: 'get'
  })
}

// 学生扫码签到
export function studentCheckIn(data) {
  return request({
    url: '/reservation/student/checkin',
    method: 'post',
    data
  })
}

// 学生提前签退
export function studentCheckOut() {
  return request({
    url: '/reservation/student/checkout',
    method: 'post'
  })
}

// 标记暂离
export function markTempLeave() {
  return request({
    url: '/reservation/student/temp-leave',
    method: 'post'
  })
}

// 取消暂离
export function cancelTempLeave() {
  return request({
    url: '/reservation/student/cancel-temp-leave',
    method: 'post'
  })
}

// 获取可预约时间段
export function getAvailableTimeSlots() {
  return request({
    url: '/reservation-rule/time-slots',
    method: 'get'
  })
}