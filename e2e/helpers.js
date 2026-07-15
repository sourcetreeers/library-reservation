const path = require('path');
const config = require('./config');

let screenshotIndex = 0;

function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

async function screenshot(page, name) {
  screenshotIndex++;
  const filename = `${String(screenshotIndex).padStart(2, '0')}-${name}.png`;
  const filepath = path.join(config.screenshotsDir, filename);
  await page.screenshot({ path: filepath, fullPage: false });
  console.log(`  [Screenshot] ${filename}`);
  return { filename, filepath };
}

async function loginViaStorage(page, userData) {
  // Step 1: 导航到登录页面加载前端应用
  await page.goto(`${config.baseUrl}/#/mobile/login`, { waitUntil: 'networkidle2' });
  await sleep(1000);

  // Step 2: 通过 API 登录获取 session cookie（防止后续 API 调用返回 401）
  const loginResult = await page.evaluate(async (u) => {
    const resp = await fetch('/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username: u.username, password: u.password })
    });
    return await resp.json();
  }, userData);

  if (loginResult.code !== 200) {
    console.log(`  [Warning] API 登录失败: ${loginResult.message}`);
  }

  // Step 3: 设置 localStorage（前端路由守卫使用）
  await page.evaluate((user) => {
    const userStr = JSON.stringify(user);
    localStorage.setItem('library_user', userStr);
    localStorage.setItem('user', userStr);
  }, userData);

  // Step 4: 导航到首页
  await page.evaluate(() => { window.location.hash = '#/mobile/home'; });
  await sleep(2000);
}

async function loginViaUI(page, username, password) {
  await page.goto(`${config.baseUrl}/#/mobile/login`, { waitUntil: 'networkidle2' });
  await sleep(1000);

  // 通过 Vue 实例设置数据，确保 Vant 组件响应式更新
  const found = await page.evaluate((u, p) => {
    const app = document.querySelector('#app').__vue__;
    function findLoginVM(vm) {
      if (!vm) return false;
      if (vm.form && vm.form.username !== undefined && vm.form.password !== undefined) {
        vm.form.username = u;
        vm.form.password = p;
        return true;
      }
      if (vm.$children) {
        for (const child of vm.$children) {
          if (findLoginVM(child)) return true;
        }
      }
      return false;
    }
    return findLoginVM(app);
  }, username, password);

  if (!found) {
    // 回退：DOM 操作 + 事件模拟
    await page.$eval('input[name="username"]', (el, val) => {
      const nativeInputValueSetter = Object.getOwnPropertyDescriptor(
        window.HTMLInputElement.prototype, 'value'
      ).set;
      nativeInputValueSetter.call(el, val);
      el.dispatchEvent(new Event('input', { bubbles: true }));
    }, username);
    await sleep(100);
    await page.$eval('input[type="password"]', (el, val) => {
      const nativeInputValueSetter = Object.getOwnPropertyDescriptor(
        window.HTMLInputElement.prototype, 'value'
      ).set;
      nativeInputValueSetter.call(el, val);
      el.dispatchEvent(new Event('input', { bubbles: true }));
    }, password);
    await sleep(200);
  }

  await screenshot(page, 'login-form-filled');

  // 查找登录按钮并点击
  await page.evaluate(() => {
    const buttons = document.querySelectorAll('button');
    for (const btn of buttons) {
      if (btn.textContent.includes('登录')) {
        btn.click();
        return;
      }
    }
  });

  await sleep(2500);
}

async function waitForElement(page, selector, timeout = config.timeout.element) {
  try {
    await page.waitForSelector(selector, { timeout });
    return true;
  } catch {
    return false;
  }
}

async function waitForIdle(page) {
  await sleep(1500);
}

async function clearStorage(page) {
  await page.evaluate(() => {
    localStorage.clear();
  });
}

module.exports = {
  sleep,
  screenshot,
  loginViaStorage,
  loginViaUI,
  waitForElement,
  waitForIdle,
  clearStorage
};
