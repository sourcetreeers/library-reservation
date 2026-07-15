import request from '@/utils/request'

/**
 * 获取公告列表
 */
export function getAnnouncements() {
  return request({
    url: '/common/announcements',
    method: 'get'
  })
}

/**
 * 获取轮播图列表
 */
export function getBanners() {
  return request({
    url: '/common/banners',
    method: 'get'
  })
}

/**
 * 获取热门推荐座位
 */
export function getPopularSeats(params) {
  return request({
    url: '/common/popular-seats',
    method: 'get',
    params
  })
}
