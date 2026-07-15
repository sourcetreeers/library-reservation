const puppeteer = require('puppeteer');
const path = require('path');
const fs = require('fs');
const config = require('./config');

const { runLoginTests } = require('./tests/login.test');
const { runRegisterTests } = require('./tests/register.test');
const { runReservationTests } = require('./tests/reservation.test');
const { runMyReservationsTests } = require('./tests/my-reservations.test');

const results = [];

function ensureDir(dir) {
  if (!fs.existsSync(dir)) {
    fs.mkdirSync(dir, { recursive: true });
  }
}

function addResult(suite, name, passed, detail) {
  results.push({ suite, name, passed, detail: detail || '' });
  const status = passed ? '✓ PASS' : '✗ FAIL';
  console.log(`  [${status}] ${name}${detail ? ' - ' + detail : ''}`);
}

function generateReport() {
  const screenshotsDir = path.resolve(config.screenshotsDir);
  let screenshots = [];
  try {
    screenshots = fs.readdirSync(screenshotsDir)
      .filter(f => f.endsWith('.png'))
      .sort();
  } catch (e) { /* ignore */ }

  const totalTests = results.length;
  const passedTests = results.filter(r => r.passed).length;
  const failedTests = totalTests - passedTests;

  const rows = results.map(r => `
    <tr class="${r.passed ? 'pass' : 'fail'}">
      <td>${r.suite}</td>
      <td>${r.name}</td>
      <td><strong>${r.passed ? 'PASS' : 'FAIL'}</strong></td>
      <td>${r.detail || ''}</td>
    </tr>`).join('');

  const screenshotCards = screenshots.map(s => `
    <div class="screenshot-card">
      <img src="${s}" alt="${s}" loading="lazy" onclick="this.classList.toggle('zoomed')" />
      <div class="caption">${s}</div>
    </div>`).join('');

  const html = `<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>图书馆预约系统 - 自动化测试报告</title>
  <style>
    * { margin: 0; padding: 0; box-sizing: border-box; }
    body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #f5f7fa; color: #333; line-height: 1.6; }
    .container { max-width: 1200px; margin: 0 auto; padding: 20px; }
    header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 40px 20px; text-align: center; border-radius: 12px; margin-bottom: 30px; }
    header h1 { font-size: 28px; margin-bottom: 10px; }
    header .meta { opacity: 0.85; font-size: 14px; }

    .summary { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 30px; }
    .summary-card { background: white; border-radius: 10px; padding: 24px; text-align: center; box-shadow: 0 2px 8px rgba(0,0,0,0.08); }
    .summary-card .number { font-size: 36px; font-weight: 700; }
    .summary-card .label { font-size: 14px; color: #666; margin-top: 4px; }
    .summary-card.total .number { color: #667eea; }
    .summary-card.passed .number { color: #52c41a; }
    .summary-card.failed .number { color: #ff4d4f; }
    .summary-card.rate .number { font-size: 24px; color: #faad14; }

    .section-title { font-size: 20px; font-weight: 600; margin: 30px 0 16px; padding-bottom: 8px; border-bottom: 2px solid #e8e8e8; }

    table { width: 100%; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.08); border-collapse: collapse; }
    th { background: #fafafa; padding: 14px 16px; text-align: left; font-weight: 600; font-size: 14px; border-bottom: 2px solid #e8e8e8; }
    td { padding: 12px 16px; font-size: 14px; border-bottom: 1px solid #f0f0f0; }
    tr.pass { border-left: 3px solid #52c41a; }
    tr.fail { border-left: 3px solid #ff4d4f; background: #fff2f0; }

    .screenshots { display: grid; grid-template-columns: repeat(auto-fill, minmax(320px, 1fr)); gap: 16px; margin-top: 16px; }
    .screenshot-card { background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.08); transition: transform 0.2s; }
    .screenshot-card:hover { transform: translateY(-2px); }
    .screenshot-card img { width: 100%; display: block; cursor: pointer; }
    .screenshot-card img.zoomed { position: fixed; top: 50%; left: 50%; transform: translate(-50%, -50%); width: auto; max-width: 90vw; max-height: 90vh; z-index: 1000; box-shadow: 0 8px 40px rgba(0,0,0,0.3); border-radius: 4px; }
    .screenshot-card .caption { padding: 10px 14px; font-size: 13px; color: #666; background: #fafafa; }

    footer { text-align: center; padding: 40px 0 20px; color: #999; font-size: 13px; }
  </style>
</head>
<body>
  <div class="container">
    <header>
      <h1>图书馆预约系统 - 自动化测试报告</h1>
      <p class="meta">生成时间: ${new Date().toLocaleString('zh-CN')} | 测试地址: ${config.baseUrl}</p>
    </header>

    <div class="summary">
      <div class="summary-card total">
        <div class="number">${totalTests}</div>
        <div class="label">测试总数</div>
      </div>
      <div class="summary-card passed">
        <div class="number">${passedTests}</div>
        <div class="label">通过</div>
      </div>
      <div class="summary-card failed">
        <div class="number">${failedTests}</div>
        <div class="label">失败</div>
      </div>
      <div class="summary-card rate">
        <div class="number">${totalTests > 0 ? Math.round(passedTests / totalTests * 100) : 0}%</div>
        <div class="label">通过率</div>
      </div>
    </div>

    <h2 class="section-title">测试用例结果</h2>
    <table>
      <thead>
        <tr><th>测试套件</th><th>测试用例</th><th>结果</th><th>详情</th></tr>
      </thead>
      <tbody>${rows}</tbody>
    </table>

    <h2 class="section-title">测试截图 (${screenshots.length} 张)</h2>
    <div class="screenshots">${screenshotCards}</div>

    <footer><p>图书馆预约系统 E2E 自动化测试 &copy; ${new Date().getFullYear()}</p></footer>
  </div>
</body>
</html>`;

  const reportPath = path.join(__dirname, 'report.html');
  fs.writeFileSync(reportPath, html, 'utf-8');
  console.log(`\n测试报告已生成: ${reportPath}`);
  console.log(`截图目录: ${screenshotsDir}`);
  console.log(`总计: ${totalTests} | 通过: ${passedTests} | 失败: ${failedTests} | 通过率: ${totalTests > 0 ? Math.round(passedTests/totalTests*100) : 0}%`);

  return reportPath;
}

async function main() {
  console.log('='.repeat(60));
  console.log('  图书馆预约系统 - E2E 自动化测试');
  console.log('  测试地址: ' + config.baseUrl);
  console.log('  启动时间: ' + new Date().toLocaleString('zh-CN'));
  console.log('='.repeat(60));

  // 清理旧截图
  ensureDir(config.screenshotsDir);
  try {
    const oldFiles = fs.readdirSync(config.screenshotsDir);
    for (const f of oldFiles) {
      fs.unlinkSync(path.join(config.screenshotsDir, f));
    }
  } catch (e) { /* ignore */ }

  let browser;
  try {
    browser = await puppeteer.launch(config.browser);
  } catch (err) {
    console.error('浏览器启动失败:', err.message);
    console.log('\n请确保:');
    console.log('  1. 前端开发服务器已启动 (npm run serve 在 frontend/ 目录)');
    console.log('  2. 后端服务已启动');
    console.log('  3. Puppeteer 已正确安装 (npm install puppeteer)');
    process.exit(1);
  }

  try {
    await runLoginTests(browser, addResult);
    await runRegisterTests(browser, addResult);
    await runReservationTests(browser, addResult);
    await runMyReservationsTests(browser, addResult);

    console.log('\n' + '='.repeat(60));
    console.log('  生成测试报告');
    console.log('='.repeat(60));
    generateReport();

  } catch (err) {
    console.error('测试执行出错:', err.message);
    console.error(err.stack);
  } finally {
    await browser.close();
    console.log('\n浏览器已关闭。');
  }
}

main().catch(console.error);
