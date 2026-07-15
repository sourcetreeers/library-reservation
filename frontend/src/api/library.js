import request from '@/utils/request'

/**
 * 图书馆相关API
 */

// 获取图书馆列表
export function getLibraryList() {
  return request({
    url: '/library/list',
    method: 'get'
  })
}

// 分页查询图书馆
export function getLibraryPage(params) {
  return request({
    url: '/library/page',
    method: 'get',
    params
  })
}

// 根据ID获取图书馆
export function getLibraryById(id) {
  return request({
    url: `/library/${id}`,
    method: 'get'
  })
}

// 新增图书馆
export function createLibrary(data) {
  return request({
    url: '/library',
    method: 'post',
    data
  })
}

// 更新图书馆
export function updateLibrary(id, data) {
  return request({
    url: `/library/${id}`,
    method: 'put',
    data
  })
}

// 删除图书馆
export function deleteLibrary(id) {
  return request({
    url: `/library/${id}`,
    method: 'delete'
  })
}