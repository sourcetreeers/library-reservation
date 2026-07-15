import request from '@/utils/request'

/**
 * 分页查询公告列表
 */
export function getAnnouncementPage(params) {
  return request({
    url: '/admin/announcement/page',
    method: 'get',
    params
  })
}

/**
 * 新增公告
 */
export function addAnnouncement(data) {
  return request({
    url: '/admin/announcement',
    method: 'post',
    data
  })
}

/**
 * 更新公告
 */
export function updateAnnouncement(data) {
  return request({
    url: '/admin/announcement',
    method: 'put',
    data
  })
}

/**
 * 删除公告
 */
export function deleteAnnouncement(id) {
  return request({
    url: `/admin/announcement/${id}`,
    method: 'delete'
  })
}

/**
 * 上下架公告
 */
export function toggleAnnouncementStatus(id) {
  return request({
    url: `/admin/announcement/${id}/toggle-status`,
    method: 'put'
  })
}
