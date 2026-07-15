import request from '@/utils/request'

/**
 * 举报相关API
 */

// 提交举报
export function createComplaint(data) {
  return request({
    url: '/complaint',
    method: 'post',
    data
  })
}

// 查看自己的举报记录
export function getMyComplaints(params) {
  return request({
    url: '/complaint/my',
    method: 'get',
    params
  })
}

// 管理员分页查询所有举报
export function getComplaintPage(params) {
  return request({
    url: '/complaint/admin/page',
    method: 'get',
    params
  })
}

// 管理员处理举报
export function handleComplaint(id, data) {
  return request({
    url: `/complaint/${id}/handle`,
    method: 'put',
    data
  })
}

// 上传图片
export function uploadImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/upload/image',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

