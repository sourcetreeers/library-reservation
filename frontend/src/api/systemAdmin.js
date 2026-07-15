import request from '@/utils/request'

/**
 * 获取所有用户（系统管理员）
 */
export function getAllUsers(params) {
  return request({
    url: '/system-admin/users',
    method: 'get',
    params
  })
}

/**
 * 更新用户信息（系统管理员）
 */
export function updateUser(userId, data) {
  return request({
    url: `/system-admin/user/${userId}`,
    method: 'put',
    data
  })
}

/**
 * 新增图书馆管理员（系统管理员）
 */
export function createLibrarian(data) {
  return request({
    url: '/system-admin/librarian',
    method: 'post',
    data
  })
}

/**
 * 获取所有积分记录（系统管理员）
 */
export function getAllPointsRecords(params) {
  return request({
    url: '/system-admin/points-records',
    method: 'get',
    params
  })
}

/**
 * 获取所有预约规则（系统管理员）
 */
export function getRules() {
  return request({
    url: '/system-admin/rules',
    method: 'get'
  })
}

/**
 * 更新单条预约规则（系统管理员）
 */
export function updateRule(ruleKey, ruleValue) {
  return request({
    url: '/system-admin/rule',
    method: 'put',
    params: { ruleKey, ruleValue }
  })
}
