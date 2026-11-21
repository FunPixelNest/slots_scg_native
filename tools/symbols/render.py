"""
Render the original symbol sets to WEBP.

  python3 tools/symbols/render.py [theme ...]        # default: every theme in tools/symbols/themes/

For each theme module tools/symbols/themes/<id>.py (must define SYMBOLS: dict key -> svg string):
  - writes themes/theme_<id>/src/main/assets/themes/<id>/symbols_v2/<key>.webp (240x240, transparent)
  - writes tools/symbols/out/<id>.png, a contact sheet for review
The game's original symbols are never touched (see apply.py for switching theme.json).
"""
import base64, importlib, json, os, subprocess, sys, tempfile

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", ".."))
HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
FONT = os.path.join(ROOT, "app/src/main/res/font/lilitaone_regular.ttf")
OUT = os.path.join(HERE, "out")
SIZE = 240


def theme_ids(argv):
    if argv:
        return argv
    return sorted(f[:-3] for f in os.listdir(os.path.join(HERE, "themes")) if f.endswith(".py") and not f.startswith("_"))


def main(argv):
    from PIL import Image, ImageDraw
    os.makedirs(OUT, exist_ok=True)
    font64 = base64.b64encode(open(FONT, "rb").read()).decode()
    jobs = []
    tmp = tempfile.mkdtemp(prefix="sym")
    for tid in theme_ids(argv):
        mod = importlib.import_module(f"themes.{tid}")
        importlib.reload(mod)
        for key, s in mod.SYMBOLS.items():
            html = (f'<html><head><style>@font-face{{font-family:"Lilita One";src:url(data:font/ttf;base64,{font64})}}'
                    f'html,body{{margin:0;background:transparent}}svg{{display:block;width:{SIZE}px;height:{SIZE}px}}</style></head><body>{s}</body></html>')
            hp = os.path.join(tmp, f"{tid}__{key}.html")
            open(hp, "w").write(html)
            jobs.append((tid, key, hp, hp[:-5] + ".png"))
    js = os.path.join(tmp, "shoot.js")
    open(js, "w").write("""
const { chromium } = require('playwright');
const jobs = JSON.parse(require('fs').readFileSync(process.argv[2], 'utf8'));
(async () => {
  const b = await chromium.launch();
  const pg = await b.newPage({ viewport: { width: %d, height: %d } });
  for (const [html, png] of jobs) {
    await pg.goto('file://' + html);
    await pg.evaluate(() => document.fonts.ready);
    await pg.screenshot({ path: png, omitBackground: true, clip: { x: 0, y: 0, width: %d, height: %d } });
  }
  await b.close();
})();
""" % (SIZE, SIZE, SIZE, SIZE))
    jp = os.path.join(tmp, "jobs.json")
    json.dump([[h, p] for _, _, h, p in jobs], open(jp, "w"))
    subprocess.run(["node", js, jp], check=True)

    by_theme = {}
    for tid, key, _, png in jobs:
        dst = os.path.join(ROOT, f"themes/theme_{tid}/src/main/assets/themes/{tid}/symbols_v2")
        os.makedirs(dst, exist_ok=True)
        im = Image.open(png).convert("RGBA")
        im.save(os.path.join(dst, f"{key}.webp"), "WEBP", quality=90, method=6)
        by_theme.setdefault(tid, []).append((key, im))
    for tid, items in by_theme.items():
        cols = 5
        rows = (len(items) + cols - 1) // cols
        sheet = Image.new("RGBA", (cols * 260, rows * 280), (30, 26, 90, 255))
        d = ImageDraw.Draw(sheet)
        for i, (key, im) in enumerate(items):
            x, y = (i % cols) * 260 + 10, (i // cols) * 280 + 10
            sheet.alpha_composite(im, (x, y))
            d.text((x + 4, y + 244), key, fill=(220, 230, 255, 255))
        sheet.save(os.path.join(OUT, f"{tid}.png"))
    print("rendered", len(jobs), "symbols for", ", ".join(by_theme))


if __name__ == "__main__":
    main(sys.argv[1:])
