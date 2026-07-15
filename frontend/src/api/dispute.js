import request from '@/utils/request'

/**
 * 争议处理相关API（合并申诉和举报）
 */

// 管理员分页查询所有争议
export function getDisputePage(params) {
  return request({
    url: '/admin/disputes',
    method: 'get',
    params
  })
}

// 管理员处理争议
export function handleDispute(id, data) {
  return request({
    url: `/admin/disputes/${id}/handle`,
    method: 'put',
    data
  })
}
