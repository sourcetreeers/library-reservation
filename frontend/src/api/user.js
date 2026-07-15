import request from '@/utils/request'

/**
 * 分页查询用户列表
 */
export function getUserPage(params) {
  return request({
    url: '/user/page',
    method: 'get',
    params
  })
}

/**
 * 切换用户状态（启用/禁用）
 */
export function toggleUserStatus(userId) {
  return request({
    url: `/user/${userId}/toggle-status`,
    method: 'put'
  })
}

/**
 * 修改用户类型
 */
export function changeUserType(userId, userType) {
  return request({
    url: `/user/${userId}/change-type`,
    method: 'put',
    data: { userType }
  })
}

/**
 * 获取当前用户信息
 */
export function getUserInfo() {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

/**
 * 更新当前用户信息
 */
export function updateUserInfo(data) {
  return request({
    url: '/user/update',
    method: 'put',
    data
  })
}