const config = require('../config');
const { sleep, screenshot, clearStorage } = require('../helpers');

const SUITE = '登录流程';

async function runLoginTests(browser, addResult) {
  console.log('\n========== 登录流程测试 ==========');

  const page = await browser.newPage();
  await page.setViewport(config.viewport);

  try {
    // --- 测试1: 打开登录页面 ---
    console.log('[Test 1] 打开登录页面');
    await page.goto(`${config.baseUrl}/#/mobile/login`, { waitUntil: 'networkidle2' });
    await sleep(1500);
    await screenshot(page, 'login-page');

    const hasUsername = await page.$('input[name="username"]') !== null;
    const hasPassword = await page.$('input[type="password"]') !== null;
    const hasForm = hasUsername && hasPassword;
    addResult(SUITE, '登录页面加载', hasForm, '表单元素存在');

    // --- 测试2: 错误密码登录 ---
    console.log('[Test 2] 错误密码登录');
    await clearStorage(page);
    await page.goto(`${config.baseUrl}/#/mobile/login`, { waitUntil: 'networkidle2' });
    await sleep(1000);

    const usernameInput = await page.$('input[name="username"]');
    await usernameInput.click({ clickCount: 3 });
    await usernameInput.type('student001');
    const passwordInput = await page.$('input[type="password"]');
    await passwordInput.click({ clickCount: 3 });
    await passwordInput.type('wrong_password');

    const buttons = await page.$$('button');
    for (const btn of buttons) {
      const text = await page.evaluate(el => el.textContent, btn);
      if (text.includes('登录')) {
        await btn.click();
        break;
      }
    }
    await sleep(2000);
    await screenshot(page, 'login-error');

    const urlAfterFail = page.url();
    const stillOnLogin = !urlAfterFail.includes('/home');
    addResult(SUITE, '错误密码登录拦截', stillOnLogin, '应留在登录页或显示错误');

    // --- 测试3: 正确登录 ---
    console.log('[Test 3] 正确登录');
    await clearStorage(page);
    await page.goto(`${config.baseUrl}/#/mobile/login`, { waitUntil: 'networkidle2' });
    await sleep(1000);

    const uInput = await page.$('input[name="username"]');
    await uInput.click({ clickCount: 3 });
    await uInput.type(config.testUser.username);
    const pInput = await page.$('input[type="password"]');
    await pInput.click({ clickCount: 3 });
    await pInput.type(config.testUser.password);
    await screenshot(page, 'login-form-filled');

    const btns = await page.$$('button');
    for (const btn of btns) {
      const text = await page.evaluate(el => el.textContent, btn);
      if (text.includes('登录')) {
        await btn.click();
        break;
      }
    }
    await sleep(2500);
    await screenshot(page, 'login-success');

    const currentUrl = page.url();
    const isHome = currentUrl.includes('/home');
    addResult(SUITE, '正确登录跳转首页', isHome, isHome ? '跳转成功' : `当前URL: ${currentUrl}`);

    // --- 测试4: 首页 UI 验证 ---
    if (isHome) {
      console.log('[Test 4] 首页 UI 验证');
      await sleep(1000);
      const hasNavBar = await page.$('.van-nav-bar') !== null;
      addResult(SUITE, '首页导航栏显示', hasNavBar, hasNavBar ? '正常' : 'nav-bar 未找到');
    }

  } finally {
    await page.close();
  }
}

module.exports = { runLoginTests };
