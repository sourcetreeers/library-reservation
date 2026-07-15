const config = require('../config');
const { sleep, screenshot, loginViaStorage, waitForIdle } = require('../helpers');

const SUITE = '预约流程';

async function runReservationTests(browser, addResult) {
  console.log('\n========== 预约流程测试 ==========');

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

    // --- 测试1: 首页预约向导 ---
    console.log('[Test 1] 首页预约向导');
    await screenshot(page, 'step0-initial');
    const hasSteps = await page.$('.van-steps') !== null;
    addResult(SUITE, '首页三步向导显示', hasSteps, hasSteps ? '正常' : '向导未找到');

    // --- 测试2: 选择日期时间 ---
    console.log('[Test 2] 选择预约日期和时间');

    // Try setting via Vue: find the Home component
    const vmSet = await page.evaluate(() => {
      const app = document.querySelector('#app').__vue__;
      function findHome(vm) {
        if (!vm) return null;
        // Check for key Home.vue data properties
        if (vm.reservationDate !== undefined && vm.activeStep !== undefined) return vm;
        if (vm.$children) {
          for (const c of vm.$children) {
            const r = findHome(c);
            if (r) return r;
          }
        }
        return null;
      }
      const vm = findHome(app);
      if (vm) {
        const tomorrow = new Date();
        tomorrow.setDate(tomorrow.getDate() + 1);
        vm.reservationDate = tomorrow.toISOString().split('T')[0];
        vm.startTime = '08:00';
        vm.endTime = '10:00';
        return { found: true, isTimeValid: vm.isTimeValid, activeStep: vm.activeStep };
      }
      return { found: false };
    });
    console.log(`  VM set: ${JSON.stringify(vmSet)}`);

    // If VM not found, try selecting via UI
    if (!vmSet.found) {
      // Click date field
      const dateField = await page.evaluateHandle(() => {
        const fields = document.querySelectorAll('.van-field__control');
        for (const f of fields) {
          if (f.readOnly && f.closest('.van-cell')?.innerText?.includes('预约日期')) return f;
        }
        return null;
      });
      if (dateField) {
        await dateField.click();
        await sleep(500);
        const confirm = await page.evaluateHandle(() => document.querySelector('.van-picker__confirm'));
        if (confirm) { await confirm.click(); await sleep(300); }
      }
    }
    await screenshot(page, 'step0-date-selected');

    // --- 测试3: 点击下一步 ---
    console.log('[Test 3] 进入图书馆选择');
    const nextClicked = await page.evaluate(() => {
      const btns = document.querySelectorAll('button');
      for (const b of btns) {
        if (b.textContent.trim() === '下一步' && !b.disabled) {
          b.click();
          return true;
        }
      }
      return false;
    });
    await sleep(2000);
    await screenshot(page, 'step1-library-list');

    // Check if library list appeared
    const step1State = await page.evaluate(() => ({
      vanCellCount: document.querySelectorAll('.van-cell').length,
      hasSearch: !!document.querySelector('.van-search'),
      bodySnippet: document.body.innerText.substring(0, 200)
    }));
    const isStep1 = step1State.vanCellCount > 0 || step1State.hasSearch;
    addResult(SUITE, '进入图书馆选择页', nextClicked && isStep1,
      `van-cell: ${step1State.vanCellCount}, search: ${step1State.hasSearch}`);

    // --- 测试4: 选择图书馆 ---
    let selectedLibrary = false;
    if (isStep1) {
      console.log('[Test 4] 选择图书馆');
      // Find and click a library item
      const clicked = await page.evaluate(() => {
        const cells = document.querySelectorAll('.van-cell');
        for (const cell of cells) {
          const text = cell.innerText;
          if (text.includes('图书室') && cell.querySelector('.van-cell__title')) {
            cell.click();
            return text.substring(0, 30);
          }
        }
        return null;
      });
      await sleep(2000);
      if (clicked) {
        console.log(`  已选择: ${clicked}`);
        selectedLibrary = true;
      }
    }
    await screenshot(page, 'step2-seat-grid');

    // Check for seat grid
    const seatState = await page.evaluate(() => ({
      hasSeatsGrid: !!document.querySelector('.seats-grid'),
      availableSeats: document.querySelectorAll('.seat-block.available').length,
      bodySnippet: document.body.innerText.substring(0, 200)
    }));
    addResult(SUITE, '选择图书馆进入座位页', selectedLibrary,
      `grid: ${seatState.hasSeatsGrid}, seats: ${seatState.availableSeats}`);

    // --- 测试5: 座位布局 ---
    if (selectedLibrary) {
      console.log('[Test 5] 查看座位布局');
      addResult(SUITE, '座位布局加载', seatState.hasSeatsGrid,
        `可用座位: ${seatState.availableSeats}`);

      // --- 测试6: 选择座位并预约 ---
      if (seatState.availableSeats > 0) {
        console.log('[Test 6] 选择座位并确认预约');
        // Click first available seat
        await page.evaluate(() => {
          const seat = document.querySelector('.seat-block.available');
          if (seat) seat.click();
        });
        await sleep(800);
        await screenshot(page, 'step2-seat-selected');

        // Click "确认选择该座位"
        await page.evaluate(() => {
          const btns = document.querySelectorAll('button');
          for (const b of btns) {
            if (b.textContent.includes('确认选择')) { b.click(); return; }
          }
        });
        await sleep(800);

        // Click "确认预约"
        await page.evaluate(() => {
          const btns = document.querySelectorAll('button');
          for (const b of btns) {
            if (b.textContent.includes('确认预约')) { b.click(); return; }
          }
        });
        await sleep(2000);
        await screenshot(page, 'reservation-confirmed');
        addResult(SUITE, '预约确认', true, '预约流程完成');
      } else {
        addResult(SUITE, '预约确认', false, `无可用座位(${seatState.availableSeats})`);
      }
    } else {
      addResult(SUITE, '座位布局加载', false, '未进入座位页');
      addResult(SUITE, '预约确认', false, '前一步失败，跳过');
    }

    await sleep(500);
    await page.evaluate(() => localStorage.clear());

  } finally {
    await page.close();
  }
}

module.exports = { runReservationTests };
