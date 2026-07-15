/**
 * 设备检测工具
 */

/**
 * 判断是否为移动设备
 */
export function isMobile() {
  const userAgent = navigator.userAgent
  const mobileKeywords = ['Mobile', 'Android', 'iPhone', 'iPad', 'Windows Phone']
  
  // 检查屏幕宽度
  if (window.innerWidth <= 768) {
    return true
  }
  
  // 检查User Agent
  return mobileKeywords.some(keyword => userAgent.includes(keyword))
}

/**
 * 获取设备类型
 */
export function getDeviceType() {
  return isMobile() ? 'mobile' : 'desktop'
}