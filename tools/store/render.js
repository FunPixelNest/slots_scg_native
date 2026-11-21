// node tools/store/render.js <dir>: renders <dir>/*.html to PNG at the canvas size (icons 512, phone 1080x1920, tablet 1600x2560)
const { chromium } = require('playwright');
const fs = require('fs'), path = require('path');
(async () => {
  const dir = path.resolve(process.argv[2]);
  const b = await chromium.launch();
  for (const f of fs.readdirSync(dir).filter(f => f.endsWith('.html')).sort()) {
    const [w, h] = f.startsWith('icon') ? [512, 512] : f.startsWith('tablet') ? [1600, 2560] : [1080, 1920];
    const pg = await b.newPage({ viewport: { width: w, height: h }, deviceScaleFactor: 1 });
    await pg.goto('file://' + path.join(dir, f));
    await pg.evaluate(() => document.fonts.ready);
    await pg.waitForTimeout(400);
    await pg.screenshot({ path: path.join(dir, f.replace('.html', '.png')) });
    await pg.close();
  }
  await b.close();
})();
