module.exports = {
  baseUrl: 'http://localhost:3000',
  apiBaseUrl: 'http://localhost:3000/api',

  screenshotsDir: './screenshots',

  viewport: {
    width: 375,
    height: 812,
    isMobile: true,
    hasTouch: true
  },

  // 测试用户凭据 (wangwu 密码是 123456)
  testUser: {
    id: 4,
    username: 'wangwu',
    password: '123456',
    realName: '王五'
  },

  // 超时配置 (ms)
  timeout: {
    navigation: 10000,
    element: 5000,
    action: 3000
  },

  // 浏览器配置
  browser: {
    headless: 'new',
    args: [
      '--no-sandbox',
      '--disable-setuid-sandbox',
      '--disable-dev-shm-usage'
    ]
  }
};
