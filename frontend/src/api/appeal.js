import request from '@/utils/request'

/**
 * 申诉相关API
 */

// 发起申诉
export function createAppeal(data) {
  return request({
    url: '/appeal',
    method: 'post',
    data
  })
}

// 查看自己的申诉记录
export function getMyAppeals(params) {
  return request({
    url: '/appeal/my',
    method: 'get',
    params
  })
}

// 管理员分页查询所有申诉
export function getAppealPage(params) {
  return request({
    url: '/appeal/page',
    method: 'get',
    params
  })
}

// 管理员处理申诉
export function handleAppeal(id, data) {
  return request({
    url: `/appeal/${id}/handle`,
    method: 'put',
    data
  })
}
