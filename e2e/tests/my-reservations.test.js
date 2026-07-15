const config = require('../config');
const { sleep, screenshot, loginViaStorage, waitForElement, waitForIdle } = require('../helpers');

const SUITE = '我的预约';

async function runMyReservationsTests(browser, addResult) {
  console.log('\n========== 我的预约页面测试 ==========');

  const page = await browser.newPage();
  await page.setViewport(config.viewport);

  try {
    // --- Setup: 登录 ---
    console.log('[Setup] 登录');
    await loginViaStorage(page, {
      id: config.testUser.id,
      username: config.testUser.username,
      userType: '学生',
      realName: config.testUser.realName,
      password: config.testUser.password
    });

    // --- 先通过 API 创建一个预约用于测试 ---
    console.log('[Setup] 创建测试预约');
    const createResult = await page.evaluate(async () => {
      // 使用未来时间创建预约
      const tomorrow = new Date();
      tomorrow.setDate(tomorrow.getDate() + 3);
      const dateStr = tomorrow.toISOString().split('T')[0];
      const resp = await fetch('/api/reservation', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          libraryId: 1,
          seatId: 1,
          startTime: `${dateStr} 08:00:00`,
          endTime: `${dateStr} 10:00:00`
        })
      });
      return await resp.json();
    });
    console.log(`  预约创建: ${createResult.code === 200 ? '成功' : '失败: ' + createResult.message}`);
    addResult(SUITE, '创建测试预约', createResult.code === 200,
      createResult.code === 200 ? createResult.data?.orderNo : createResult.message);

    // --- 测试1: 打开我的预约页面 ---
    console.log('[Test 1] 打开我的预约页面');
    await page.goto(`${config.baseUrl}/#/mobile/my-reservations`, { waitUntil: 'networkidle2' });
    await waitForIdle(page);
    await screenshot(page, 'my-reservations');

    const hasTabs = await page.$('.van-tabs') !== null;
    const hasItems = await page.$('.reservation-item') !== null;
    addResult(SUITE, '我的预约页面加载', hasTabs || hasItems,
      `Tabs: ${hasTabs}, Items: ${hasItems}`);

    // --- 测试2: 状态 Tab 切换 ---
    console.log('[Test 2] 状态 Tab 切换');
    const tabs = await page.$$('.van-tab');
    if (tabs.length > 1) {
      // Click "已预约" tab
      await tabs[1].click();
      await sleep(1000);
      await screenshot(page, 'my-reservations-tab-reserved');
    }
    addResult(SUITE, 'Tab 切换', tabs.length > 0, `找到 ${tabs.length} 个 Tab`);

    // --- 测试3: 二维码查看（在取消之前检查） ---
    console.log('[Test 3] 二维码查看');
    let hasQR = false;
    if (createResult.code === 200) {
      // 切换到"已预约"tab
      await page.evaluate(() => {
        const tabs = document.querySelectorAll('.van-tab');
        for (const t of tabs) {
          if (t.textContent.includes('已预约')) { t.click(); return; }
        }
      });
      await sleep(1000);

      const buttons = await page.$$('button');
      for (const btn of buttons) {
        const text = await page.evaluate(el => el.textContent, btn);
        if (text.includes('二维码')) {
          hasQR = true;
          await btn.click();
          await sleep(1500);
          await screenshot(page, 'qrcode-page');
          break;
        }
      }
    }
    addResult(SUITE, '二维码查看', hasQR, hasQR ? '已打开' : '无二维码可查看(SKIP)');

    // --- 测试4: 取消预约 ---
    console.log('[Test 4] 取消预约操作');
    // 回到我的预约页面
    await page.goto(`${config.baseUrl}/#/mobile/my-reservations`, { waitUntil: 'networkidle2' });
    await waitForIdle(page);

    let hasCancel = false;
    if (createResult.code === 200) {
      // 切换到"已预约"tab
      await page.evaluate(() => {
        const tabs = document.querySelectorAll('.van-tab');
        for (const t of tabs) {
          if (t.textContent.includes('已预约')) { t.click(); return; }
        }
      });
      await sleep(1000);

      const cancelBtns = await page.$$('button');
      for (const btn of cancelBtns) {
        const text = await page.evaluate(el => el.textContent, btn);
        if (text.includes('取消预约')) {
          hasCancel = true;
          await btn.click();
          await sleep(800);
          await screenshot(page, 'cancel-dialog');

          // 确认取消
          const confirmBtn = await page.evaluateHandle(() => {
            const c = document.querySelector('.van-dialog__confirm');
            return c;
          });
          if (confirmBtn) {
            await confirmBtn.click();
            await sleep(1500);
            await screenshot(page, 'cancel-confirmed');
          }
          break;
        }
      }
    }
    addResult(SUITE, '取消预约操作', hasCancel, hasCancel ? '已取消' : '无可取消预约(SKIP)');

    // --- 测试5: 底部导航栏 ---
    console.log('[Test 5] 底部导航栏');
    await page.goto(`${config.baseUrl}/#/mobile/home`, { waitUntil: 'networkidle2' });
    await waitForIdle(page);
    const tabbar = await page.$('.van-tabbar');
    addResult(SUITE, '底部导航栏显示', tabbar !== null, tabbar ? '正常' : '未找到');

    await sleep(500);
    await page.evaluate(() => localStorage.clear());

  } finally {
    await page.close();
  }
}

module.exports = { runMyReservationsTests };
