import request from '@/utils/request'

/**
 * 分页查询轮播图列表
 */
export function getBannerPage(params) {
  return request({
    url: '/admin/banner/page',
    method: 'get',
    params
  })
}

/**
 * 新增轮播图
 */
export function addBanner(data) {
  return request({
    url: '/admin/banner',
    method: 'post',
    data
  })
}

/**
 * 更新轮播图
 */
export function updateBanner(data) {
  return request({
    url: '/admin/banner',
    method: 'put',
    data
  })
}

/**
 * 删除轮播图
 */
export function deleteBanner(id) {
  return request({
    url: `/admin/banner/${id}`,
    method: 'delete'
  })
}

/**
 * 启用/禁用轮播图
 */
export function toggleBannerStatus(id) {
  return request({
    url: `/admin/banner/${id}/toggle-status`,
    method: 'put'
  })
}
