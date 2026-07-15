import request from '@/utils/request'

export function getViolationRecordPage(params) {
  return request({ url: '/admin/violation-record/page', method: 'get', params })
}

export function getViolationRecordDetail(id) {
  return request({ url: `/admin/violation-record/${id}`, method: 'get' })
}

export function deleteViolationRecord(id) {
  return request({ url: `/admin/violation-record/${id}`, method: 'delete' })
}
