import request from '@/utils/request'

/**
 * 座位相关API
 */

// 根据图书馆ID获取座位列表
export function getSeatsByLibraryId(libraryId) {
  return request({
    url: `/seat/library/${libraryId}`,
    method: 'get'
  })
}

// 获取可用座位
export function getAvailableSeats(params) {
  return request({
    url: '/seat/available',
    method: 'get',
    params
  })
}

// 获取所有座位及其预约状态
export function getAllSeatsWithStatus(params) {
  return request({
    url: '/seat/all-with-status',
    method: 'get',
    params
  })
}

// 分页查询座位
export function getSeatPage(params) {
  return request({
    url: '/seat/page',
    method: 'get',
    params
  })
}

// 根据ID获取座位
export function getSeatById(id) {
  return request({
    url: `/seat/${id}`,
    method: 'get'
  })
}

// 新增座位
export function createSeat(data) {
  return request({
    url: '/seat',
    method: 'post',
    data
  })
}

// 更新座位
export function updateSeat(id, data) {
  return request({
    url: `/seat/${id}`,
    method: 'put',
    data
  })
}

// 删除座位
export function deleteSeat(id) {
  return request({
    url: `/seat/${id}`,
    method: 'delete'
  })
}

// 获取座位布局（含行列号）
export function getSeatsLayout(params) {
  return request({
    url: '/seat/layout',
    method: 'get',
    params
  })
}

// 获取实时监控座位布局（管理员用）
export function getSeatMonitorLayout(params) {
  return request({
    url: '/seat/monitor/layout',
    method: 'get',
    params
  })
}