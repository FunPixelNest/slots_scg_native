// node shoot.js <dir> <w> <h>: screenshots every <dir>/*.html to <dir>/*.png
const { chromium } = require('playwright');
const fs = require('fs'), path = require('path');
(async () => {
  const [dir, w, h] = process.argv.slice(2);
  const b = await chromium.launch();
  const pg = await b.newPage({ viewport: { width: +w, height: +h } });
  for (const f of fs.readdirSync(dir).filter(f => f.endsWith('.html'))) {
    await pg.goto('file://' + path.resolve(dir, f));
    await pg.evaluate(() => document.fonts.ready);
    await pg.waitForTimeout(100);
    await pg.screenshot({ path: path.join(dir, f.replace('.html', '.png')) });
  }
  await b.close();
})();
