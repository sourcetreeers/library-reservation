const config = require('../config');
const { sleep, screenshot, waitForIdle } = require('../helpers');

const SUITE = '注册流程';

async function runRegisterTests(browser, addResult) {
  console.log('\n========== 注册流程测试 ==========');

  const page = await browser.newPage();
  await page.setViewport(config.viewport);

  try {
    // --- 测试1: 打开注册页面 ---
    console.log('[Test 1] 打开注册页面');
    await page.goto(`${config.baseUrl}/#/mobile/register`, { waitUntil: 'networkidle2' });
    await waitForIdle(page);
    await screenshot(page, 'register-page');

    const hasUser = await page.$('input[name="username"]') !== null;
    const hasPass = await page.$('input[type="password"]') !== null;
    const hasName = await page.$('input[name="realName"]') !== null;
    const allFields = hasUser && hasPass && hasName;
    addResult(SUITE, '注册页面表单加载', allFields, '必填字段存在');

    // --- 测试2: 填写注册表单 ---
    const testName = 'testuser_' + Date.now().toString(36);
    console.log('[Test 2] 填写注册表单');
    await page.type('input[name="username"]', testName, { delay: 30 });
    await page.type('input[type="password"]', '123456', { delay: 30 });
    await page.type('input[name="realName"]', '测试用户', { delay: 30 });

    await screenshot(page, 'register-form-filled');
    addResult(SUITE, '注册表单填写', true, `用户名: ${testName}`);

    // --- 测试3: 提交注册 ---
    console.log('[Test 3] 提交注册');
    const buttons = await page.$$('button');
    for (const btn of buttons) {
      const text = await page.evaluate(el => el.textContent, btn);
      if (text.includes('注册')) {
        await btn.click();
        break;
      }
    }
    await sleep(2000);
    await screenshot(page, 'register-result');

    const url = page.url();
    const navigated = !url.includes('/register') || url.includes('/mobile/home');
    addResult(SUITE, '注册提交并跳转', navigated, `当前URL: ${url}`);

  } finally {
    await page.close();
  }
}

module.exports = { runRegisterTests };
