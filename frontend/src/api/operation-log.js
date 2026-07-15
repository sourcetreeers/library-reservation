import request from '@/utils/request'

export function getOperationLogPage(params) {
  return request({ url: '/system-admin/operation-log/page', method: 'get', params })
}
