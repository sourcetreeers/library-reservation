/**
 * 认证相关工具函数
 */

const USER_KEY = 'library_user'

/**
 * 获取当前用户信息
 */
export function getCurrentUser() {
  const userStr = localStorage.getItem(USER_KEY)
  return userStr ? JSON.parse(userStr) : null
}

/**
 * 设置当前用户信息
 */
export function setCurrentUser(user) {
  localStorage.setItem(USER_KEY, JSON.stringify(user))
}

/**
 * 清除当前用户信息
 */
export function clearCurrentUser() {
  localStorage.removeItem(USER_KEY)
}

/**
 * 检查是否已登录
 */
export function isLoggedIn() {
  return getCurrentUser() !== null
}

/**
 * 检查是否为管理员（图书馆管理员）
 */
export function isAdmin() {
  const user = getCurrentUser()
  return user && (user.userType === '图书馆管理员' || user.userType === '管理员')
}

/**
 * 检查是否为系统管理员
 */
export function isSystemAdmin() {
  const user = getCurrentUser()
  return user && user.userType === '系统管理员'
}

/**
 * 检查是否为学生
 */
export function isStudent() {
  const user = getCurrentUser()
  return user && user.userType === '学生'
}