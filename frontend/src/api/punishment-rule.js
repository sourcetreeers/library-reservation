import request from '@/utils/request'

export function getPunishmentRulePage(params) {
  return request({ url: '/system-admin/punishment-rule/page', method: 'get', params })
}

export function getPunishmentRuleList() {
  return request({ url: '/system-admin/punishment-rule/list', method: 'get' })
}

export function addPunishmentRule(data) {
  return request({ url: '/system-admin/punishment-rule', method: 'post', data })
}

export function updatePunishmentRule(data) {
  return request({ url: '/system-admin/punishment-rule', method: 'put', data })
}

export function deletePunishmentRule(id) {
  return request({ url: `/system-admin/punishment-rule/${id}`, method: 'delete' })
}

export function togglePunishmentRuleActive(id) {
  return request({ url: `/system-admin/punishment-rule/${id}/toggle`, method: 'put' })
}
