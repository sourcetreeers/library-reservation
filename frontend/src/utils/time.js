 import moment from 'moment'

/**
 * 时间工具函数
 */

/**
 * 格式化日期为 YYYY-MM-DD
 */
export function formatDate(date) {
  return moment(date).format('YYYY-MM-DD')
}

/**
 * 格式化时间为 MM-DD HH:mm
 */
export function formatTime(time) {
  return moment(time).format('MM-DD HH:mm')
}

/**
 * 格式化完整时间为 YYYY-MM-DD HH:mm:ss
 */
export function formatDateTime(dateTime) {
  return moment(dateTime).format('YYYY-MM-DD HH:mm:ss')
}

/**
 * 组合日期和时间为完整的日期时间字符串
 */
export function combineDateTime(date, time) {
  // 如果时间已经是完整格式 (HH:mm:ss)，直接组合
  if (time && time.split(':').length === 3) {
    return `${date} ${time}`
  }
  // 如果时间是 HH:mm 格式，补充 :00
  if (time && time.split(':').length === 2) {
    return `${date} ${time}:00`
  }
  // 默认情况
  return `${date} ${time || '00:00'}:00`
}

/**
 * 格式化时间范围显示
 */
export function formatTimeRange(startTime, endTime) {
  const start = moment(startTime).format('MM-DD HH:mm')
  const end = moment(endTime).format('HH:mm')
  return `${start} - ${end}`
}

/**
 * 检查时间是否有效
 */
export function isValidTime(date, startTime, endTime) {
  if (!date || !startTime || !endTime) {
    return false
  }
  
  const start = moment(`${date} ${startTime}`)
  const end = moment(`${date} ${endTime}`)
  const now = moment()
  
  // 检查是否是今天
  const isToday = moment(date).isSame(now, 'day')
  
  // 如果是今天，检查开始时间不能是过去时间
  if (isToday && start.isBefore(now)) {
    return false
  }
  
  // 检查结束时间必须晚于开始时间
  if (end.isSameOrBefore(start)) {
    return false
  }
  
  return true
}

/**
 * 生成时间选项（8:00-22:00，每分钟一个选项）
 */
export function generateTimeOptions() {
  const times = []
  for (let hour = 8; hour <= 22; hour++) {
    const h = hour.toString().padStart(2, '0')
    // 添加每分钟的时间点
    for (let minute = 0; minute < 60; minute++) {
      // 22:00之后的时间不添加（除了22:00本身）
      if (hour === 22 && minute > 0) {
        break
      }
      const m = minute.toString().padStart(2, '0')
      times.push(`${h}:${m}`)
    }
  }
  return times
}

/**
 * 生成小时选项（8-22）
 */
export function generateHourOptions() {
  const hours = []
  for (let i = 8; i <= 22; i++) {
    hours.push(i.toString().padStart(2, '0'))
  }
  return hours
}

/**
 * 生成分钟选项（00-59，每分钟）
 */
export function generateMinuteOptions() {
  const minutes = []
  for (let i = 0; i < 60; i++) {
    minutes.push(i.toString().padStart(2, '0'))
  }
  return minutes
}