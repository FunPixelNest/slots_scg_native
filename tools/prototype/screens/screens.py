# Full screen set in the approved BingoFlash-style design. Usage: python3 screens.py <outdir>
import base64, io, json, math, random, sys
from PIL import Image
OUT = sys.argv[1]
SRC = open(OUT + '/neonblue.py').read().split('\ndef lobby(')[0]
exec(SRC.replace("OUT = sys.argv[1]", "OUT = sys.argv[1]"))  # CSS, STAR, IC, rb, coinbar, logo, A, L, T, AV, img()
NB = open(OUT + '/neonblue.py').read()
exec(NB[NB.index('STEP = '):NB.index('\ndef game(')])  # STEP, footer, GOLDB, jackpots

U = '/home/user/slots_android_new/Assets/HolidaySlotAsset/'
IM, BGS = {}, {}


def reg(k, uri):
    IM[k] = uri
    return k


def I(k, style='', cls=''):
    return f'<img data-k="{k}" class="{cls}" style="{style}" alt="">'


for tid, th in T.items():
    for n, u in enumerate(th['sym']):
        reg(f's:{tid}:{n}', u)
    BGS[tid] = th['bg']
for tid, u in L.items():
    reg(f'c:{tid}', u)
for n, u in enumerate(AV):
    reg(f'a:{n}', u)
reg('chestO', img('10_CandyTheme/Sprites_Candy/PopUps/Chest/ChestOpened.png', 160))
reg('chestC', img('10_CandyTheme/Sprites_Candy/PopUps/Chest/ChestClosed.png', 120))
reg('chestP', img('11_Pirates/Sprites_Pirates/Pop Ups/Other/Chest Closed.png', 110))
reg('coins', img('20_GreekTheme/Sprites_Greek/Pop Ups/Panels/Coins Jackpot.png', 260))
reg('coinsS', img('20_GreekTheme/Sprites_Greek/Pop Ups/Panels/Coins Big.png', 120))

XCSS = r"""
.gal{display:flex;flex-wrap:wrap;gap:40px 34px}
.cell{flex:none}.cap2{color:#dfe6ff;font:800 15px Nunito;margin:12px 4px 0;max-width:390px}.cap2 small{display:block;color:#8f9bd0;font-weight:700;font-size:12.5px;margin-top:2px}
h2.sec{color:#fff;font:900 30px Nunito;margin:46px 0 18px;padding-top:20px;border-top:1px solid #2a2a5a;width:100%}
.ov{position:absolute;inset:0;z-index:30;background:radial-gradient(circle at 50% 40%,rgba(60,40,160,.55),rgba(4,4,30,.88) 70%)}
.close{position:absolute;width:38px;height:38px;border-radius:50%;display:grid;place-items:center;background:radial-gradient(circle at 50% 35%,#ff8a8a,#e2182a 60%,#a00818);border:2.5px solid #5a0008;box-shadow:inset 0 0 0 2px #ffb0b0,0 3px 0 #4a0006;color:#fff;font:900 18px Nunito;z-index:5}
.rays{position:absolute;pointer-events:none;background:repeating-conic-gradient(from 0deg at 50% 50%,rgba(190,130,255,.4) 0 4deg,transparent 4deg 12deg);-webkit-mask:radial-gradient(circle at 50% 50%,#000 12%,transparent 62%)}
.hd{border-radius:20px;background:linear-gradient(180deg,#1e1ea8,#0c0c62);border:2px solid #6ab4ff;box-shadow:0 0 10px rgba(90,170,255,.6),inset 0 2px 0 rgba(200,230,255,.3)}
.tok{width:58px;height:58px;border-radius:50%;display:grid;place-items:center;background:radial-gradient(circle at 40% 32%,#fff6b0,#ffc21a 50%,#d07000);border:3px solid #ffeb8a;box-shadow:0 0 0 2px #8a4a00,0 0 14px rgba(255,190,40,.9);font:400 15px Lilita One;color:#5a2000;text-shadow:0 1px 0 rgba(255,255,255,.6);line-height:1;text-align:center}
.prog{height:16px;border-radius:8px;background:#070840;border:1.5px solid #6ab4ff;overflow:hidden;box-shadow:inset 0 2px 4px rgba(0,0,0,.6)}
.prog i{display:block;height:100%;background:linear-gradient(180deg,#b4ff8a,#2fc93a);box-shadow:0 0 8px #3fd14a}
.row2{display:flex;align-items:center;gap:10px}
"""
for tid, u in BGS.items():
    XCSS += f'.bg-{tid}{{background:url({u}) center/cover}}\n'
XCSS += '.bg-gems,.bg-neon{}'

SB = '<div class="sb"><span>9:41</span><span>●●● ▮</span></div>'
AD = '<div class="ad">Banner ad space</div>'


def starsvg(size, color='#ffe46a'):
    return STAR.replace('<svg', f'<svg style="width:{size}px;height:{size}px;fill:{color};filter:drop-shadow(0 0 6px #ffb400)"')


def sparkles(seed, n=10, y0=60, y1=700):
    random.seed(seed)
    return ''.join(f'<span class="ab" style="left:{random.randint(8, 360)}px;top:{random.randint(y0, y1)}px">{starsvg(random.choice([9, 12, 16, 20]))}</span>' for _ in range(n))


def level_xp(lv=6, pct=62):
    return f'''<div class="fx" style="flex:1;min-width:0"><div style="position:relative;width:40px;height:40px;flex:none;z-index:2"><svg viewBox="0 0 24 24" style="position:absolute;inset:0;width:40px;height:40px;fill:url(#lvg);stroke:#7a3a00;stroke-width:1"><path d="M12 1.8l3.1 6.3 6.9.9-5 4.8 1.2 6.9L12 17.4l-6.2 3.3L7 13.8 2 9l6.9-.9z"/></svg><span class="ab t" style="inset:0;display:grid;place-items:center;padding-top:3px;font:400 15px Lilita One;text-shadow:0 1px 0 #7a3a00">{lv}</span></div><div style="flex:1;max-width:70px;height:14px;margin-left:-8px;border-radius:0 7px 7px 0;background:#070840;border:1.5px solid #6ab4ff;overflow:hidden;box-shadow:0 0 6px rgba(90,170,255,.6)"><div style="width:{pct}%;height:100%;background:linear-gradient(180deg,#b4ff8a,#2fc93a)"></div></div></div>'''


def topbar(left='back', coins='12,497,500'):
    lead = rb('back', 42) if left == 'back' else f'<div style="position:relative;width:44px;height:44px;flex:none">{I("a:0", "width:44px;height:44px;border-radius:50%;border:2.5px solid #8ab8ff;box-shadow:0 0 10px rgba(90,150,255,.8);display:block")}</div>'
    tail = rb('menu', 42) if left == 'back' else rb('gear', 42)
    return f'<div class="ab fx" style="left:14px;right:14px;top:40px;gap:10px;z-index:10">{lead}{level_xp()}{coinbar(coins, 178)}{tail}</div>'


def phone(inner, bg='space', extra=''):
    bgd = '<div class="space"></div>' if bg == 'space' else f'<div class="ab bg-{bg}" style="inset:0"></div><div class="ab" style="inset:0;background:linear-gradient(180deg,rgba(20,16,120,.85),rgba(20,16,120,.25) 22%,rgba(10,10,80,.2) 55%,rgba(8,8,56,.96) 78%)"></div>'
    return f'<div class="ph">{bgd}{inner}{extra}{SB}{AD}</div>'


# ---------------------------------------------------------------- reels
def cellhtml(tid, idx, mark=None):
    glow = ''
    over = ''
    imgst = 'position:relative;width:64px;height:64px;object-fit:contain;filter:drop-shadow(0 3px 3px rgba(0,0,30,.6))'
    if mark == 'win':
        glow = '<div class="ab" style="inset:2px;border-radius:50%;background:radial-gradient(circle,rgba(255,220,90,.75),rgba(255,170,0,.25) 55%,transparent 72%)"></div>'
        imgst += ' drop-shadow(0 0 6px #ffd23a)'
    elif mark == 'burst':
        glow = '<div class="ab" style="inset:-6px;background:repeating-conic-gradient(from 0deg,rgba(255,240,150,.9) 0 6deg,transparent 6deg 30deg);-webkit-mask:radial-gradient(circle,#000 20%,transparent 65%);border-radius:50%"></div><div class="ab" style="inset:8px;border-radius:50%;background:radial-gradient(circle,#fff,rgba(255,120,240,.6) 45%,transparent 70%)"></div>'
        imgst += ';opacity:.55;transform:scale(1.15)'
    elif mark == 'dim':
        imgst += ';opacity:.35'
    elif mark == 'drop':
        imgst += ';transform:translateY(-14px)'
        over = '<div class="ab" style="left:50%;top:-6px;width:3px;height:22px;transform:translateX(-50%);background:linear-gradient(rgba(150,220,255,0),rgba(150,220,255,.9));border-radius:2px"></div>'
    elif mark and mark.startswith('coin:'):
        v = mark[5:]
        return f'<div style="display:grid;place-items:center;position:relative"><div class="tok" style="width:56px;height:56px;font-size:{15 if len(v) < 5 else 12}px">{v}</div></div>'
    elif mark == 'diamond':
        over = '<div class="ab" style="right:4px;top:4px;width:22px;height:22px;filter:drop-shadow(0 0 5px #6ae8ff)"><svg viewBox="0 0 24 24"><path d="M6 3h12l4 6-10 12L2 9z" fill="#7ae8ff" stroke="#fff" stroke-width="1.2"/><path d="M2 9h20M8 3l4 18M16 3l-4 18" stroke="#2a9bd8" stroke-width="1" fill="none"/></svg></div>'
    elif mark == 'collector':
        glow = '<div class="ab" style="inset:2px;border-radius:14px;background:radial-gradient(circle,rgba(120,255,140,.6),transparent 70%);border:2px solid #6aff7a;box-shadow:0 0 14px #3fd14a"></div>'
        over = '<span class="ab pill G" style="left:50%;bottom:0;transform:translateX(-50%);height:16px;padding:0 6px;font-size:9px">COLLECT</span>'
    elif mark == 'scatter':
        glow = '<div class="ab" style="inset:2px;border-radius:50%;background:radial-gradient(circle,rgba(200,120,255,.8),rgba(140,60,255,.25) 55%,transparent 72%)"></div>'
        imgst += ' drop-shadow(0 0 8px #c46aff)'
    return f'<div style="display:grid;place-items:center;position:relative">{glow}{I(f"s:{tid}:{idx}", imgst)}{over}</div>'


def grid_html(tid, g, marks=None, top=266, rowh=74):
    marks = marks or {}
    cells = ''.join(cellhtml(tid, g[r][w], marks.get((r, w))) for w in range(3) for r in range(5))
    sep = 'linear-gradient(90deg,' + ','.join(f'transparent calc({p}% - 1px),rgba(106,180,255,.35) {p}%,transparent calc({p}% + 1px)' for p in (20, 40, 60, 80)) + ')'
    return f'<div class="ab pn" style="left:10px;right:10px;top:{top}px;padding:8px;border-radius:24px;background:linear-gradient(180deg,rgba(16,14,110,.92),rgba(8,8,60,.95))"><div style="position:relative;display:grid;grid-template-columns:repeat(5,1fr);grid-template-rows:repeat(3,{rowh}px);gap:3px;background:{sep}">{cells}</div></div>'


def rgrid(seed, n=8, wild=None):
    random.seed(seed)
    g = [[random.randrange(n) for _ in range(3)] for _ in range(5)]
    return g


def title(name, feat, top=192):
    return f'''<div class="ab" style="left:0;right:0;top:{top}px;text-align:center"><span class="dg" style="font-size:34px">{name}</span></div>
 <div class="ab" style="left:50%;top:{top + 40}px;transform:translateX(-50%)"><span class="pill O" style="height:26px;padding:0 14px;font-size:13px">★ {feat} ★</span></div>
 <div class="ab" style="right:18px;top:{top + 38}px"><div class="rb" style="width:30px;height:30px;background:radial-gradient(circle at 50% 35%,#d79aff,#9a3aff 55%,#5a12c8);border:2px solid #2a0670;box-shadow:inset 0 0 0 1.5px #e0b8ff,0 2px 0 #1a0450,0 0 8px rgba(170,80,255,.6)"><span class="t" style="position:relative;z-index:1;font:400 17px Lilita One">i</span></div></div>'''


def winbar(v='48,750', lbl='WIN', top=532):
    return f'<div class="ab pn fx" style="left:70px;right:70px;top:{top}px;height:44px;border-radius:22px;justify-content:center;gap:10px"><span class="lb" style="font-size:13px;letter-spacing:.1em">{lbl}</span><span class="dg" style="font-size:26px">{v}</span></div>'


def bigspin(label='SPIN', sub='HOLD FOR AUTO', color='G'):
    c = {'G': ('#b4ff8a', '#3ad44a', '#1aa02a', '#0f7a1a', '#0a5a10'), 'O': ('#ffe07a', '#ffa21a', '#e35a00', '#b84000', '#7a2a00'), 'X': ('#9aa4c8', '#5a6490', '#3a4470', '#2a3258', '#1a2040')}[color]
    return f'<div class="ab" style="right:12px;top:14px;width:146px;height:100px;border-radius:22px;padding:5px;background:linear-gradient(180deg,#fff3a0,#ffc21a 35%,#e07a00 60%,#ffe46a);box-shadow:0 0 0 2px #3a0a8a,0 5px 0 2px #24065e,0 0 22px rgba(255,190,40,.85)"><div style="width:100%;height:100%;border-radius:17px;position:relative;overflow:hidden;background:linear-gradient(180deg,{c[0]},{c[1]} 45%,{c[2]} 55%,{c[3]});border:2px solid {c[4]};box-shadow:inset 0 -6px 0 rgba(0,0,0,.18),inset 0 0 0 2px rgba(255,255,255,.4);display:flex;flex-direction:column;align-items:center;justify-content:center"><div style="position:absolute;left:8%;right:8%;top:4px;height:40%;border-radius:14px;background:linear-gradient(rgba(255,255,255,.65),rgba(255,255,255,0))"></div><div class="t" style="position:relative;font:400 {40 if len(label) < 6 else 28}px Lilita One;line-height:1;text-shadow:0 3px 0 {c[4]};letter-spacing:.04em">{label}</div><div class="t" style="position:relative;font-size:9px;margin-top:3px;letter-spacing:.08em;text-shadow:0 1px 0 {c[4]}">{sub}</div></div></div>'


def foot_base():
    return footer('C')


def foot_info(left_html, spin_label='SPIN', spin_sub='HOLD FOR AUTO', color='G'):
    return f'<div class="ab pn" style="left:10px;right:10px;bottom:62px;height:128px;border-radius:26px"><div class="ab" style="left:14px;top:14px;width:184px;height:100px;display:flex;flex-direction:column;justify-content:center;gap:8px">{left_html}</div>{bigspin(spin_label, spin_sub, color)}</div>'


def gp(tid, name, feat, header, grid, below=None, foot=None, extra=''):
    inner = f'''{topbar()}{header}{title(name, feat)}{grid}{below if below is not None else winbar()}{foot if foot is not None else foot_base()}'''
    return phone(inner, tid, extra)


def panelhdr(html, top=88, h=96):
    return f'<div class="ab pn" style="left:14px;right:14px;top:{top}px;height:{h}px;border-radius:22px">{html}</div>'


# ---------------------------------------------------------------- gameplay screens
def g_hold():
    g = rgrid(3, 9)
    for r in (1, 2, 3):
        g[r][1] = 8
    return gp('pirates', 'PIRATES', 'HOLD &amp; SPIN', jackpots('D', ''), grid_html('pirates', g, {(1, 1): 'win', (2, 1): 'win', (3, 1): 'win'}))


def g_free():
    steps = ''.join(f'<div style="flex:1;text-align:center"><div class="pill {"O" if m == "x3" else "B"}" style="height:{40 if m == "x3" else 32}px;font-size:{20 if m == "x3" else 15}px;{"box-shadow:inset 0 2px 0 rgba(255,255,255,.55),0 3px 0 #7a2a00,0 0 18px rgba(255,170,40,.9)" if m == "x3" else ("opacity:.55" if m in ("x5", "x10") else "")}">{m}</div></div>' for m in ['x1', 'x2', 'x3', 'x5', 'x10'])
    hdr = panelhdr(f'''<div class="ab" style="left:0;right:0;top:10px;text-align:center;font:900 11px Nunito;letter-spacing:.2em;color:#ffd98a">FREE SPINS MULTIPLIER</div>
  <div class="ab fx" style="left:12px;right:12px;top:30px;gap:6px">{steps}</div>
  <div class="ab" style="left:0;right:0;top:72px;text-align:center"><span class="lb" style="font-size:11px">Multiplier grows with every winning free spin</span></div>''')
    g = rgrid(11, 8)
    g[0][1] = g[1][1] = g[2][1] = 3
    left = '''<div class="lb" style="font-size:11px;letter-spacing:.12em;text-align:center">FREE SPINS</div><div style="text-align:center"><span class="dg" style="font-size:40px">7</span><span class="t" style="font:400 18px Lilita One"> / 10</span></div><div class="lb" style="text-align:center;font-size:11px">TOTAL WIN <b style="color:#ffe46a">125,000</b></div>'''
    return gp('christmas_eve', 'CHRISTMAS EVE', 'FREE SPINS x10', hdr, grid_html('christmas_eve', g, {(0, 1): 'win', (1, 1): 'win', (2, 1): 'win'}),
              winbar('36,000', 'WIN x3'), foot_info(left, 'AUTO', 'SPINNING…', 'O'))


def g_cascade():
    chain = ''
    for n, m in enumerate(['x1', 'x2', 'x3', 'x5']):
        on = m == 'x2'
        done = m == 'x1'
        chain += f'<div class="pill {"P" if on else "B"}" style="width:56px;height:{44 if on else 36}px;font-size:{22 if on else 16}px;{"box-shadow:inset 0 2px 0 rgba(255,255,255,.55),0 3px 0 #2a0670,0 0 18px rgba(255,80,240,.9)" if on else ("" if done else "opacity:.5")}">{m}</div>'
        if n < 3:
            chain += '<span class="t" style="font-size:18px;color:#ff8af0">›</span>'
    hdr = panelhdr(f'''<div class="ab" style="left:0;right:0;top:10px;text-align:center;font:900 11px Nunito;letter-spacing:.2em;color:#ffb0f4">CASCADE MULTIPLIER</div>
  <div class="ab fx" style="left:0;right:0;top:30px;justify-content:center;gap:8px">{chain}</div>
  <div class="ab" style="left:0;right:0;top:74px;text-align:center"><span class="lb" style="font-size:11px">Winning symbols explode · new ones drop in</span></div>''')
    g = rgrid(5, 8)
    g[0][1] = g[1][1] = g[2][1] = g[3][1] = 2
    marks = {(0, 1): 'burst', (1, 1): 'burst', (2, 1): 'burst', (3, 1): 'burst', (0, 0): 'drop', (1, 0): 'drop', (2, 0): 'drop', (3, 0): 'drop'}
    return gp('neon', 'NEON NIGHTS', 'CASCADE', hdr, grid_html('neon', g, marks), winbar('22,500', 'CASCADE x2'))


def diamond_svg(s):
    return f'<svg viewBox="0 0 24 24" style="width:{s}px;height:{s}px;filter:drop-shadow(0 0 8px #6ae8ff)"><path d="M6 3h12l4 6-10 12L2 9z" fill="url(#dg1)" stroke="#fff" stroke-width="1"/><path d="M2 9h20M8 3l-1 6 5 12 5-12-1-6M7 9l5-6 5 6" stroke="rgba(255,255,255,.7)" stroke-width=".8" fill="none"/><defs><linearGradient id="dg1" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#d8fbff"/><stop offset=".5" stop-color="#5ad8ff"/><stop offset="1" stop-color="#1a7ad8"/></linearGradient></defs></svg>'


def g_diamond():
    hdr = panelhdr(f'''<div class="ab" style="left:12px;top:12px">{diamond_svg(70)}</div>
  <div class="ab" style="left:92px;right:14px;top:14px"><div class="row2" style="justify-content:space-between"><span style="font:900 11px Nunito;letter-spacing:.18em;color:#9ae8ff">DIAMOND COLLECTION</span><span class="t" style="font:400 18px Lilita One">34<span style="opacity:.6"> / 50</span></span></div>
   <div class="prog" style="margin-top:8px;height:20px"><i style="width:68%;background:linear-gradient(180deg,#d8fbff,#3ab8ff);box-shadow:0 0 10px #3ab8ff"></i></div>
   <div class="lb" style="font-size:11px;margin-top:8px">Fill the meter for a <b style="color:#ffe46a">SUPER BONUS</b></div></div>''')
    g = rgrid(8, 8)
    return gp('gems', 'GEMS', 'DIAMOND COLLECT', hdr, grid_html('gems', g, {(1, 0): 'diamond', (3, 2): 'diamond', (4, 1): 'diamond'}), winbar('+3 DIAMONDS', ''))


def g_coin():
    hdr = panelhdr(f'''<div class="ab fx" style="left:14px;top:14px;gap:10px"><div class="tok" style="width:48px;height:48px;font-size:12px">5K</div><span class="t" style="font-size:22px">+</span><div style="width:52px;height:52px;border-radius:14px;border:2px solid #6aff7a;box-shadow:0 0 14px #3fd14a;display:grid;place-items:center;background:#0a3a1a">{I("s:farm:8", "width:46px;height:46px;object-fit:contain")}</div></div>
  <div class="ab" style="left:150px;right:14px;top:14px"><div style="font:900 11px Nunito;letter-spacing:.16em;color:#9aff9a">COIN COLLECTOR</div><div class="lb" style="font-size:12px;margin-top:4px;line-height:1.3">Land the Collector to win<br>every coin on the reels</div><div class="t" style="font:400 16px Lilita One;margin-top:4px;color:#ffe46a">ON SCREEN: 27,500</div></div>''')
    g = rgrid(4, 8)
    g[4][1] = 8
    marks = {(0, 0): 'coin:5K', (2, 2): 'coin:12.5K', (1, 1): 'coin:2.5K', (3, 0): 'coin:7.5K', (4, 1): 'collector'}
    return gp('farm', 'HAPPY FARM', 'COIN COLLECTOR', hdr, grid_html('farm', g, marks), winbar('27,500', 'COLLECTED'))


def g_wheel():
    g = rgrid(9, 8)
    g[0][2] = g[2][0] = 9
    return gp('modern', 'MODERN SUITS', 'BONUS WHEEL', jackpots('D', ''), grid_html('modern', g, {(0, 2): 'scatter', (2, 0): 'scatter'}),
              winbar('2 OF 3 BONUS', ''))


def g_pick():
    slots = ''.join(f'<div style="width:54px;height:54px;border-radius:50%;display:grid;place-items:center;border:2px solid {"#c46aff" if n < 2 else "#3a3a8a"};background:{"radial-gradient(circle,#5a12c8,#1a0450)" if n < 2 else "#0a0a40"};box-shadow:{"0 0 14px #b04aff" if n < 2 else "inset 0 2px 4px rgba(0,0,0,.6)"}">{I("s:witch:9", "width:44px;height:44px;object-fit:contain") if n < 2 else "<span class=t style=font-size:22px;opacity:.4>?</span>"}</div>' for n in range(3))
    hdr = panelhdr(f'''<div class="ab" style="left:0;right:0;top:9px;text-align:center;font:900 11px Nunito;letter-spacing:.2em;color:#e0b0ff">PICK BONUS · LAND 3 SCATTERS</div>
  <div class="ab fx" style="left:0;right:0;top:30px;justify-content:center;gap:14px">{slots}</div>''')
    g = rgrid(6, 8)
    g[0][0] = g[3][2] = 9
    return gp('witch', "WITCH'S CAULDRON", 'PICK BONUS', hdr, grid_html('witch', g, {(0, 0): 'scatter', (3, 2): 'scatter'}), winbar('ONE MORE!', ''))


# ---------------------------------------------------------------- bonus rounds
def b_hold():
    random.seed(21)
    vals = {(0, 0): '5K', (2, 0): '12.5K', (4, 0): 'MINI', (1, 1): '7.5K', (3, 1): '25K', (0, 2): 'MAJOR', (2, 2): '5K', (4, 2): '10K'}
    cells = ''
    for w in range(3):
        for r in range(5):
            v = vals.get((r, w))
            if v in ('MINI', 'MAJOR'):
                col = {'MINI': ('#5ae06a', '#0f7a1a'), 'MAJOR': ('#c46aff', '#5a12c8')}[v]
                cells += f'<div style="display:grid;place-items:center"><div class="tok" style="background:radial-gradient(circle at 40% 32%,#fff,{col[0]} 45%,{col[1]});border-color:#fff;color:#fff;text-shadow:0 1px 0 rgba(0,0,0,.5);font-size:12px;box-shadow:0 0 0 2px {col[1]},0 0 16px {col[0]}">{v}</div></div>'
            elif v:
                cells += f'<div style="display:grid;place-items:center"><div class="tok">{v}</div></div>'
            else:
                cells += '<div style="margin:4px;border-radius:14px;background:rgba(0,0,30,.55);box-shadow:inset 0 0 0 1.5px rgba(106,180,255,.35)"></div>'
    grid = f'<div class="ab pn" style="left:10px;right:10px;top:266px;padding:8px;border-radius:24px;background:linear-gradient(180deg,rgba(16,14,110,.92),rgba(8,8,60,.95))"><div style="display:grid;grid-template-columns:repeat(5,1fr);grid-template-rows:repeat(3,74px);gap:3px">{cells}</div></div>'
    dots = ''.join(f'<div style="width:30px;height:30px;border-radius:50%;background:{"radial-gradient(circle at 40% 35%,#b4ff8a,#2fc93a)" if n < 3 else "#0a0a40"};border:2px solid {"#d4ffb8" if n < 3 else "#3a3a8a"};box-shadow:{"0 0 12px #3fd14a" if n < 3 else "none"}"></div>' for n in range(3))
    left = f'<div class="lb" style="font-size:11px;letter-spacing:.12em;text-align:center">RESPINS LEFT</div><div class="fx" style="justify-content:center;gap:10px">{dots}</div><div class="lb" style="text-align:center;font-size:11px">Coin lands = respins reset to 3</div>'
    return gp('pirates', 'HOLD &amp; SPIN', '8 COINS LOCKED', jackpots('D', ''), grid, winbar('560,000', 'TOTAL'), foot_info(left, 'RESPIN', 'AUTO', 'O'))


def wheel_svg(size, segs, colors, hub='SPIN'):
    n = len(segs)
    R = 100
    parts = ''
    for i, (lab, c) in enumerate(zip(segs, colors)):
        a0 = math.radians(i * 360 / n - 90 - 180 / n)
        a1 = math.radians((i + 1) * 360 / n - 90 - 180 / n)
        x0, y0, x1, y1 = R * math.cos(a0), R * math.sin(a0), R * math.cos(a1), R * math.sin(a1)
        parts += f'<path d="M0 0L{x0:.2f} {y0:.2f}A{R} {R} 0 0 1 {x1:.2f} {y1:.2f}Z" fill="{c}" stroke="#ffe9a0" stroke-width="1.5"/>'
        am = i * 360 / n
        parts += f'<text transform="rotate({am}) translate(0 -64) rotate(90)" text-anchor="middle" dominant-baseline="middle" style="font:400 {13 if len(lab) < 5 else 10}px Lilita One;fill:#fff;paint-order:stroke;stroke:rgba(0,0,0,.45);stroke-width:2.5px">{lab}</text>'
    bulbs = ''.join(f'<circle cx="{109 * math.cos(math.radians(k * 15)):.1f}" cy="{109 * math.sin(math.radians(k * 15)):.1f}" r="3.2" fill="#fff8d0" style="filter:drop-shadow(0 0 3px #ffb400)"/>' for k in range(24))
    return f'''<svg viewBox="-120 -120 240 240" style="width:{size}px;height:{size}px;overflow:visible"><defs><linearGradient id="rimg" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#fff3a0"/><stop offset=".5" stop-color="#e0a020"/><stop offset="1" stop-color="#8a4a00"/></linearGradient></defs>
<circle r="117" fill="url(#rimg)" stroke="#3a0a8a" stroke-width="3"/><circle r="102" fill="#3a0a8a"/>{parts}{bulbs}
<circle r="30" fill="url(#rimg)" stroke="#3a0a8a" stroke-width="3"/><circle r="24" fill="#2fc93a" stroke="#d4ffb8" stroke-width="2"/><text text-anchor="middle" dominant-baseline="middle" y="1" style="font:400 13px Lilita One;fill:#fff">{hub}</text>
<path d="M0 -128l-13 -16h26z" transform="translate(0 6)" fill="#ff3040" stroke="#fff" stroke-width="2.5" stroke-linejoin="round"/></svg>'''


def b_wheel():
    segs = ['GRAND', '50K', '100K', 'MINI', '250K', '75K', 'MAJOR', '150K', '500K', 'MINOR', '200K', '1M']
    cols = ['#d4123a', '#2a6bff', '#9a3aff', '#2fa83a', '#ff8a00', '#1aa6c8', '#c41a9a', '#2a6bff', '#e0a000', '#1a8ad0', '#9a3aff', '#d4123a']
    inner = f'''{topbar()}<div class="rays" style="left:-60px;right:-60px;top:110px;height:520px"></div>{sparkles(4, 12, 90, 650)}
 <div class="ab" style="left:0;right:0;top:96px;text-align:center"><span class="dg" style="font-size:44px">BONUS WHEEL</span></div>
 <div class="ab lb" style="left:0;right:0;top:150px;text-align:center;font-size:13px">Spin to win coins or a JACKPOT!</div>
 <div class="ab" style="left:20px;top:192px">{wheel_svg(350, segs, cols)}</div>
 <div class="ab pn fx" style="left:40px;right:40px;top:584px;height:50px;border-radius:25px;justify-content:center;gap:10px"><span class="lb" style="font-size:12px;letter-spacing:.1em">BET</span><span class="t" style="font:400 18px Lilita One">25,000</span><span class="lb" style="font-size:12px;letter-spacing:.1em;margin-left:10px">SPINS</span><span class="dg" style="font-size:22px">1</span></div>
 <div class="ab" style="left:70px;right:70px;top:656px"><span class="pill G" style="height:62px;font-size:30px">SPIN!</span></div>'''
    return phone(inner, 'modern')


def b_pick():
    vals = [('chestO', '75K'), ('chestC', ''), ('chestC', ''), ('chestO', '150K'), ('chestC', ''), ('chestC', ''), ('chestC', ''), ('chestO', '2X'), ('chestC', ''), ('chestC', ''), ('chestC', ''), ('chestC', '')]
    cells = ''
    for k, v in vals:
        op = k == 'chestO'
        cells += f'''<div class="pn{" mg" if op else ""}" style="height:108px;border-radius:18px;position:relative;display:grid;place-items:center">{I(k, "width:86px;" + ("" if op else "filter:drop-shadow(0 4px 4px rgba(0,0,0,.5))"))}
 {f'<span class="ab dg" style="left:0;right:0;bottom:4px;text-align:center;font-size:22px">{v}</span>' if op else ''}</div>'''
    inner = f'''{topbar()}<div class="rays" style="left:-40px;right:-40px;top:60px;height:260px"></div>
 <div class="ab" style="left:0;right:0;top:100px;text-align:center"><span class="dg" style="font-size:40px">PICK A CHEST!</span></div>
 <div class="ab fx" style="left:20px;right:20px;top:156px;justify-content:space-between"><div class="hd fx" style="height:40px;padding:0 14px;gap:8px"><span class="lb" style="font-size:12px">PICKS LEFT</span><span class="dg" style="font-size:22px">2</span></div><div class="hd fx" style="height:40px;padding:0 14px;gap:8px"><span class="lb" style="font-size:12px">TOTAL</span><span class="dg" style="font-size:22px">450,000</span></div></div>
 <div class="ab" style="left:16px;right:16px;top:214px;display:grid;grid-template-columns:repeat(3,1fr);gap:12px">{cells}</div>
 <div class="ab lb" style="left:0;right:0;top:730px;text-align:center;font-size:12px">2X doubles everything you have picked</div>'''
    return phone(inner, 'witch')


# ---------------------------------------------------------------- lobby & splash
def card(i, name, feat, btn, sel=False, lock=False, badge=None):
    cls, txt = btn
    return f'''<div class="pn{' mg' if sel else ''}" style="position:relative;padding:4px;border-radius:20px">
 <div style="border-radius:15px;overflow:hidden;position:relative">{I("c:" + i, "width:100%;aspect-ratio:600/430;object-fit:cover;object-position:top;display:block;" + ("filter:grayscale(.6) brightness(.6)" if lock else ""))}
 {'<div class="ab" style="inset:0;display:grid;place-items:center"><svg viewBox="0 0 24 24" style="width:40px;fill:#e8ecff;filter:drop-shadow(0 3px 0 rgba(0,0,0,.5))"><path d="M7 10V8a5 5 0 0110 0v2h1.5v11h-13V10zM9.5 10h5V8a2.5 2.5 0 00-5 0z"/></svg></div>' if lock else ''}</div>
 <div style="padding:6px 6px 8px;text-align:center"><div class="t" style="font-size:15px;line-height:1.1">{name}</div><div class="lb" style="font-size:11px;margin:2px 0 7px">{feat}</div>
 <span class="pill {cls}" style="height:30px;font-size:15px">{txt}</span></div>
 {f'<div class="ab pill O" style="top:-8px;right:-6px;padding:1px 9px;font-size:11px">{badge}</div>' if badge else ''}</div>'''


def lobby_inner(scroll=0):
    cards = ''.join([card('pirates', 'Pirates', 'Hold &amp; Spin · Jackpots', ('G', 'PLAY'), sel=True, badge='HOT'), card('christmas_eve', 'Christmas Eve', 'Free Spins up to x10', ('G', 'PLAY')),
                     card('neon', 'Neon Nights', 'Cascade wins', ('O', '▶ UNLOCK'), lock=True), card('gems', 'Gems', 'Diamond Collect', ('P', 'LEVEL 5'), lock=True)])
    tabs = ''.join(f'<span class="pill {c}" style="height:30px;padding:0 13px;font-size:13px">{t}</span>' for t, c in [('ALL', 'O'), ('JACKPOT', 'B'), ('FREE SPINS', 'B'), ('BONUS', 'B')])
    navs = [('cart', 'STORE'), ('task', 'TASKS'), None, ('trophy', 'EVENTS'), ('user', 'PROFILE')]
    nav = ''.join('<div style="width:86px;height:86px;margin-top:-44px;border-radius:50%;padding:4px;background:conic-gradient(from 0deg,#ffe46a,#ff9a00,#ffe46a,#ff9a00,#ffe46a);box-shadow:0 0 18px rgba(255,190,40,.9)"><div class="rb" style="width:100%;height:100%;background:radial-gradient(circle at 50% 35%,#8cff6a,#2fc93a 55%,#0f8a1a);border-color:#0a5a10;box-shadow:inset 0 0 0 2px #b4ff9a">' + IC['house'] + '</div></div>' if n is None else
                  f'<div class="fx" style="flex-direction:column;gap:3px">{rb(n[0], 44)}<span class="t" style="font-size:10px;letter-spacing:.04em">{n[1]}</span></div>' for n in navs)
    return f'''{topbar('avatar', '12,500,000')}
 {logo(96)}
 <div class="ab pn fx" style="left:14px;right:14px;top:196px;height:84px;padding:0 12px 0 96px">{I("chestO", "position:absolute;left:-6px;top:-24px;width:110px;filter:drop-shadow(0 0 12px rgba(255,200,60,.7))")}
  <div style="flex:1"><div class="dg" style="font-size:22px">DAILY BONUS</div><div class="lb" style="font-size:12px">Day 4 streak · 375,000 coins</div></div>
  <span class="pill G" style="height:38px;padding:0 16px;font-size:16px">COLLECT</span></div>
 <div class="ab fx" style="left:14px;right:14px;top:298px;gap:7px">{tabs}</div>
 <div class="ab" style="left:14px;right:14px;top:344px;display:grid;grid-template-columns:1fr 1fr;gap:14px 12px">{cards}</div>
 <div class="ab fx" style="left:0;right:0;bottom:50px;height:78px;z-index:20;justify-content:space-around;padding:0 6px;background:linear-gradient(180deg,#1a1a96,#0a0a52);border-top:2.5px solid #6ab4ff;box-shadow:0 -4px 16px rgba(90,170,255,.5)">{nav}</div>'''


def s_lobby():
    return phone(lobby_inner())


def s_splash():
    inner = f'''<div class="rays" style="left:-80px;right:-80px;top:120px;height:560px"></div>{sparkles(7, 16, 80, 760)}
 <div class="ab" style="left:0;right:0;top:260px;transform:scale(1.35);transform-origin:50% 0">{logo(0)}</div>
 <div class="ab" style="left:40px;right:40px;top:640px"><div class="prog" style="height:22px;border-radius:11px"><i style="width:72%;border-radius:11px"></i></div><div class="t" style="text-align:center;margin-top:10px;font-size:14px">Loading… 72%</div></div>
 <div class="ab lb" style="left:0;right:0;top:740px;text-align:center;font-size:11px;letter-spacing:.2em">FUNPIXELNEST</div>'''
    return f'<div class="ph"><div class="space"></div>{inner}{SB}</div>'


# ---------------------------------------------------------------- popups
def modal(ttl, body, top=150, sub=None, close=True, ttl_size=34):
    return f'''<div class="ov"></div>
 <div class="rays" style="left:-60px;right:-60px;top:{top - 190}px;height:380px;z-index:31"></div>
 <div class="ab pn" style="left:18px;right:18px;top:{top}px;z-index:32;padding:46px 16px 18px;border-radius:26px">
  <div class="ab" style="left:0;right:0;top:-24px;text-align:center"><span class="dg" style="font-size:{ttl_size}px;white-space:nowrap">{ttl}</span></div>
  {f'<div class="close" style="right:-10px;top:-12px">✕</div>' if close else ''}
  {f'<div class="lb" style="text-align:center;font-size:13px;margin:-14px 0 12px">{sub}</div>' if sub else ''}{body}</div>'''


def over_lobby(m):
    return phone(f'<div class="ab" style="inset:0;filter:blur(2px) brightness(.6)">{lobby_inner()}</div>{m}')


def over_game(m, tid='pirates'):
    g = rgrid(3, 9)
    inner = f'<div class="ab" style="inset:0;filter:blur(2px) brightness(.6)">{topbar()}{jackpots("D", "") if tid == "pirates" else ""}{title(T[tid]["name"].upper(), "")}{grid_html(tid, g)}{foot_base()}</div>'
    return phone(inner + m, tid)


def coinline(v, size=40):
    return f'<div class="fx" style="justify-content:center;gap:8px"><div class="coin" style="position:relative;left:0;transform:none;width:{size}px;height:{size}px">{STAR}</div><span class="dg" style="font-size:{size + 6}px">{v}</span></div>'


BURST = 'polygon(50% 0,61% 12%,76% 4%,79% 20%,95% 21%,89% 36%,100% 50%,89% 64%,95% 79%,79% 80%,76% 96%,61% 88%,50% 100%,39% 88%,24% 96%,21% 80%,5% 79%,11% 64%,0 50%,11% 36%,5% 21%,21% 20%,24% 4%,39% 12%)'


def watch2x(dbl):
    """Rewarded-video button: magenta glossy bar, play disc, two-line label, gold x2 burst badge."""
    return f'''<div style="position:relative;height:62px;margin-top:6px;border-radius:20px;padding:3px;background:linear-gradient(90deg,#ffe46a,#ff9af0,#8ad0ff,#ffe46a);background-size:200% 100%;box-shadow:0 0 0 2px #3a0a5a,0 5px 0 2px #24063e,0 0 22px rgba(255,90,230,.75)">
 <div style="height:100%;border-radius:17px;position:relative;overflow:hidden;display:flex;align-items:center;gap:10px;padding:0 70px 0 8px;background:linear-gradient(180deg,#ff8af0,#e03ad0 45%,#b012b8 55%,#7a0a9a);box-shadow:inset 0 -5px 0 rgba(0,0,0,.18)">
  <div style="position:absolute;left:6%;right:30%;top:3px;height:42%;border-radius:12px;background:linear-gradient(rgba(255,255,255,.55),rgba(255,255,255,0))"></div>
  <div style="position:absolute;top:0;bottom:0;left:58%;width:26px;transform:skewX(-20deg);background:linear-gradient(90deg,transparent,rgba(255,255,255,.45),transparent)"></div>
  <div style="position:relative;width:42px;height:42px;flex:none;border-radius:50%;background:radial-gradient(circle at 40% 35%,#fff,#ffe0fa);box-shadow:0 0 0 3px rgba(255,255,255,.35),0 2px 4px rgba(60,0,60,.5);display:grid;place-items:center"><svg viewBox="0 0 24 24" style="width:22px;height:22px;margin-left:3px"><path d="M7 4.5v15l12-7.5z" fill="#c41ab8"/></svg></div>
  <div style="position:relative;line-height:1.02"><div style="white-space:nowrap;font:900 11px Nunito;letter-spacing:.1em;color:#fff0fc;text-shadow:0 1px 2px rgba(60,0,80,.55)">WATCH VIDEO &amp; GET <span style="display:inline-block;vertical-align:1px;margin-left:3px;font:900 8px Nunito;letter-spacing:.08em;color:#fff;-webkit-text-stroke:0;text-shadow:none;background:#5a0a6a;padding:1px 4px;border-radius:4px">AD</span></div><div class="t" style="font:400 22px Lilita One;text-shadow:0 2px 0 #5a0a6a">{dbl}</div></div>
 </div>
 <div style="position:absolute;right:-12px;top:-16px;width:76px;height:76px;transform:rotate(12deg);filter:drop-shadow(0 3px 0 #7a3a00) drop-shadow(0 0 10px rgba(255,190,40,.9))"><div style="width:100%;height:100%;clip-path:{BURST};background:radial-gradient(circle at 45% 40%,#fff6b0,#ffc21a 55%,#e07a00);display:grid;place-items:center"><span style="font:400 30px Lilita One;color:#fff;-webkit-text-stroke:5px #a03a00;paint-order:stroke fill;line-height:1">x2</span></div></div>
</div>'''


def btns(primary='COLLECT', secondary='▶ x2 WATCH', dbl=None):
    if not secondary:
        return f'<div style="margin-top:16px"><span class="pill G" style="height:48px;font-size:20px">{primary}</span></div>'
    return f'<div style="display:flex;flex-direction:column;gap:12px;margin-top:18px">{watch2x(dbl)}<span class="pill G" style="height:42px;font-size:17px;margin:0 22px">{primary}</span></div>'


def p_welcome():
    body = f'''<div style="text-align:center">{I("chestO", "width:170px;margin:-6px auto 0;filter:drop-shadow(0 0 20px rgba(255,200,60,.7))")}</div>
 <div class="lb" style="text-align:center;font-size:13px;letter-spacing:.1em">YOUR WELCOME GIFT</div>{coinline("5,000,000")}
 <div class="lb" style="text-align:center;font-size:12px;margin-top:6px">+ 2 machines unlocked · Daily bonus every day</div>{btns("CLAIM", None)}'''
    return phone(f'{sparkles(2, 14)}' + modal('WELCOME!', body, 200, close=False))


def p_daily():
    days = ''
    for d, amt, st in [(1, '125K', 'ok'), (2, '188K', 'ok'), (3, '250K', 'ok'), (4, '375K', 'now'), (5, '500K', ''), (6, '750K', ''), (7, '1.25M', 'big')]:
        sty = {'ok': 'opacity:.6', 'now': 'border-color:#ffc21a;box-shadow:0 0 0 1.5px #5a2a00,0 0 16px rgba(255,190,40,.9),inset 0 0 12px rgba(255,190,40,.4);transform:scale(1.06)', '': '', 'big': 'grid-column:span 2;border-color:#ff5cf4;box-shadow:0 0 14px rgba(255,80,240,.8)'}[st]
        ic = I('chestC', 'width:56px;display:block;margin:0 auto') if st == 'big' else f'<div class="coin" style="position:relative;left:0;top:0;transform:none;width:30px;height:30px;margin:2px auto">{STAR}</div>'
        days += f'<div class="hd" style="position:relative;padding:6px 2px;text-align:center;border-radius:14px;{sty}"><div class="t" style="font-size:11px">{"TODAY" if st == "now" else "DAY " + str(d)}</div>{ic}<div class="t" style="font:400 15px Lilita One;color:#ffe46a">{amt}</div>{"<span class=ab style=right:4px;top:2px;color:#6aff7a;font:900 16px_Nunito>✔</span>".replace("_", " ") if st == "ok" else ""}</div>'
    body = f'<div style="display:grid;grid-template-columns:repeat(4,1fr);gap:8px">{days}</div>{btns("COLLECT 375K", "x2", "750,000 COINS")}'
    return over_lobby(modal('DAILY BONUS', body, 190, 'Come back every day for bigger rewards'))


def p_lucky():
    segs = ['50K', '100K', '25K', '250K', '75K', '1M', '50K', '150K']
    cols = ['#2a6bff', '#9a3aff', '#1aa6c8', '#ff8a00', '#2fa83a', '#d4123a', '#2a6bff', '#c41a9a']
    body = f'<div style="display:grid;place-items:center;margin-top:-6px">{wheel_svg(290, segs, cols, "SPIN")}</div><div class="lb" style="text-align:center;font-size:12px;margin-top:8px">1 free spin every day · next in 23:59:12</div>{btns("SPIN FREE", None)}'
    return over_lobby(modal('LUCKY WHEEL', body, 150))


def p_bigwin():
    m = f'''<div class="ov"></div><div class="ab" style="inset:0;z-index:31">
  <div class="rays" style="left:-40px;right:-40px;top:60px;height:420px"></div>{sparkles(5, 16, 120, 640)}
  <div class="ab dg" style="left:0;right:0;top:150px;text-align:center;font-size:76px;line-height:.86">BIG<br>WIN!</div>
  {I("coins", "position:absolute;left:75px;top:300px;width:240px;filter:drop-shadow(0 0 20px rgba(255,190,40,.6))")}
  <div class="ab pn" style="left:18px;right:18px;top:456px;padding:18px 16px;border-radius:24px"><div class="lb" style="text-align:center;font-size:13px;letter-spacing:.1em">YOU WON</div>{coinline("487,500")}{btns("COLLECT 487,500", "x2", "975,000 COINS")}</div></div>'''
    return over_game(m)


def p_jackpot():
    digs = ''.join(f'<span style="display:inline-block;width:{8 if ch == "," else 28}px;height:42px;line-height:42px;text-align:center;{"" if ch == "," else "border-radius:6px;background:linear-gradient(180deg,#1a0430,#3a0a5a 48%,#12021f 52%,#2a0646);box-shadow:inset 0 0 0 1px rgba(255,200,80,.5);"}font:400 {18 if ch == "," else 32}px Lilita One;color:#ffe46a;text-shadow:0 0 8px rgba(255,190,40,.9)">{ch}</span>' for ch in '2,502,125')
    m = f'''<div class="ov" style="background:radial-gradient(circle at 50% 40%,rgba(200,30,80,.55),rgba(20,0,20,.9) 70%)"></div><div class="ab" style="inset:0;z-index:31">
  <div class="rays" style="left:-60px;right:-60px;top:40px;height:520px;background:repeating-conic-gradient(from 0deg at 50% 50%,rgba(255,200,80,.45) 0 4deg,transparent 4deg 12deg)"></div>{sparkles(8, 22, 80, 700)}
  <div class="ab" style="left:0;right:0;top:150px;text-align:center;font-size:54px">♛</div>
  <div class="ab dg" style="left:0;right:0;top:214px;text-align:center;font-size:64px;line-height:.9">GRAND<br>JACKPOT!</div>
  <div class="ab" style="left:24px;right:24px;top:370px;height:80px;border-radius:18px;border:3px solid transparent;background:radial-gradient(ellipse at 50% 0,#c4123a,#5a0420 80%) padding-box,{GOLDB} border-box;box-shadow:0 0 0 2px #3a0a00,0 0 30px rgba(255,170,40,.8)">
   <div class="ab" style="inset:2px;border-radius:14px;background:radial-gradient(circle,#fff8d0 0 1.6px,#ffcc33 2.2px,transparent 3.2px) 0 0/12px 12px;-webkit-mask:linear-gradient(#000 0 0) content-box,linear-gradient(#000 0 0);-webkit-mask-composite:xor;padding:4px"></div>
   <div class="ab fx" style="left:0;right:0;top:16px;justify-content:center;gap:3px">{digs}</div></div>
  {I("coins", "position:absolute;left:85px;top:470px;width:220px")}
  <div class="ab" style="left:50px;right:50px;top:660px"><span class="pill G" style="height:56px;font-size:24px">COLLECT</span></div></div>'''
    return over_game(m)


def p_freespins():
    m = f'''<div class="ov"></div><div class="ab" style="inset:0;z-index:31"><div class="rays" style="left:-40px;right:-40px;top:80px;height:440px"></div>{sparkles(9, 14, 120, 600)}
  <div class="ab dg" style="left:0;right:0;top:170px;text-align:center;font-size:58px;line-height:.9">FREE<br>SPINS!</div>
  <div class="ab" style="left:0;right:0;top:300px;text-align:center"><span class="dg" style="font-size:120px;line-height:1">10</span></div>
  <div class="ab pn" style="left:40px;right:40px;top:450px;padding:16px;border-radius:22px;text-align:center"><div class="t" style="font-size:15px">Multiplier grows on every win</div><div class="fx" style="justify-content:center;gap:6px;margin-top:10px">{"".join(f'<span class="pill B" style="padding:0 10px;height:30px;font-size:15px">{m}</span>' for m in ["x1", "x2", "x3", "x5", "x10"])}</div></div>
  <div class="ab" style="left:60px;right:60px;top:600px"><span class="pill G" style="height:58px;font-size:26px">START</span></div></div>'''
    return over_game(m, 'christmas_eve')


def p_fs_summary():
    body = f'''<div class="lb" style="text-align:center;font-size:13px;letter-spacing:.1em">YOU WON IN 10 FREE SPINS</div>{coinline("1,250,000")}
 <div class="fx" style="justify-content:center;gap:10px;margin-top:12px"><div class="hd" style="padding:8px 14px;text-align:center"><div class="lb" style="font-size:11px">BEST MULTIPLIER</div><div class="dg" style="font-size:24px">x5</div></div><div class="hd" style="padding:8px 14px;text-align:center"><div class="lb" style="font-size:11px">WINNING SPINS</div><div class="dg" style="font-size:24px">7</div></div></div>{btns("COLLECT 1,250,000", "x2", "2,500,000 COINS")}'''
    return over_game(f'{sparkles(3, 12)}' + modal('FEATURE WIN', body, 250, close=False), 'christmas_eve')


def p_levelup():
    body = f'''<div style="position:relative;height:120px;display:grid;place-items:center;margin-top:-8px"><svg viewBox="0 0 24 24" style="width:130px;height:130px;fill:url(#lvg);stroke:#7a3a00;stroke-width:.8;filter:drop-shadow(0 0 16px rgba(255,190,40,.9))"><path d="M12 1.8l3.1 6.3 6.9.9-5 4.8 1.2 6.9L12 17.4l-6.2 3.3L7 13.8 2 9l6.9-.9z"/></svg><span class="ab t" style="inset:0;display:grid;place-items:center;padding-top:10px;font:400 44px Lilita One;text-shadow:0 3px 0 #7a3a00">7</span></div>
 <div class="lb" style="text-align:center;font-size:13px;letter-spacing:.1em;margin-top:6px">REWARDS</div>
 <div class="fx" style="gap:10px;margin-top:8px"><div class="hd" style="flex:1;padding:10px;text-align:center"><div class="coin" style="position:relative;left:0;top:0;transform:none;width:36px;height:36px;margin:0 auto">{STAR}</div><div class="dg" style="font-size:20px;margin-top:4px">250,000</div></div>
  <div class="hd" style="flex:1;padding:6px;text-align:center"><div style="border-radius:10px;overflow:hidden">{I("c:farm", "width:100%;height:52px;object-fit:cover")}</div><div class="t" style="font-size:12px;margin-top:4px">Happy Farm</div><div class="lb" style="font-size:10px">UNLOCKED!</div></div></div>{btns("COLLECT", None)}'''
    return over_game(f'{sparkles(6, 14)}' + modal('LEVEL UP!', body, 190, close=False, ttl_size=40))


def p_store():
    packs = [('500,000', '$0.99', '', 'coinsS'), ('1,500,000', '$2.99', '+20%', 'coinsS'), ('4,000,000', '$4.99', 'POPULAR', 'coinsS'), ('10,000,000', '$9.99', 'BEST VALUE', 'coins'), ('30,000,000', '$19.99', '+50%', 'coins')]
    rows = ''
    for amt, pr, tag, ic in packs:
        hot = tag in ('POPULAR', 'BEST VALUE')
        rows += f'''<div class="hd fx" style="position:relative;height:64px;padding:0 10px 0 6px;gap:8px;{"border-color:#ff5cf4;box-shadow:0 0 12px rgba(255,80,240,.7)" if hot else ""}">{I(ic, "width:62px;height:50px;object-fit:contain")}<span class="dg" style="flex:1;font-size:22px">{amt}</span><span class="pill G" style="width:84px;height:38px;font-size:17px">{pr}</span>
  {f'<span class="ab pill {"P" if hot else "O"}" style="left:12px;top:-9px;height:20px;padding:0 9px;font-size:11px">{tag}</span>' if tag else ''}</div>'''
    body = f'''<div class="fx" style="gap:8px;margin-bottom:12px">{"".join(f'<span class="pill {c}" style="flex:1;height:32px;font-size:14px">{t}</span>' for t, c in [("COINS", "O"), ("DEALS", "B"), ("FREE", "B")])}</div>
 <div style="display:flex;flex-direction:column;gap:12px">{rows}</div>
 <div class="hd fx" style="margin-top:14px;height:56px;padding:0 10px;gap:10px;border-color:#5ae06a;box-shadow:0 0 12px rgba(90,230,110,.6)"><span class="t" style="font-size:24px">▶</span><div style="flex:1"><div class="t" style="font-size:14px">FREE COINS</div><div class="lb" style="font-size:11px">Watch a short video</div></div><span class="pill O" style="padding:0 12px;height:34px;font-size:15px">+50,000</span></div>'''
    return over_lobby(modal('STORE', body, 110))


def pig_svg(s):
    return f'''<svg viewBox="0 0 120 100" style="width:{s}px;filter:drop-shadow(0 0 16px rgba(255,120,200,.7))"><defs><radialGradient id="pg" cx=".4" cy=".35" r=".8"><stop offset="0" stop-color="#ffd6ec"/><stop offset=".55" stop-color="#ff8ac4"/><stop offset="1" stop-color="#d43a8a"/></radialGradient></defs>
<path d="M30 30l-6-18 18 10z" fill="#ff6ab0" stroke="#8a1a5a" stroke-width="2"/><path d="M78 26l10-16 2 20z" fill="#ff6ab0" stroke="#8a1a5a" stroke-width="2"/>
<ellipse cx="58" cy="54" rx="46" ry="36" fill="url(#pg)" stroke="#8a1a5a" stroke-width="2.5"/><rect x="26" y="80" width="12" height="14" rx="4" fill="#ff6ab0" stroke="#8a1a5a" stroke-width="2"/><rect x="74" y="80" width="12" height="14" rx="4" fill="#ff6ab0" stroke="#8a1a5a" stroke-width="2"/>
<ellipse cx="98" cy="56" rx="12" ry="10" fill="#ffb0d8" stroke="#8a1a5a" stroke-width="2"/><circle cx="95" cy="56" r="2.2" fill="#8a1a5a"/><circle cx="101" cy="56" r="2.2" fill="#8a1a5a"/>
<circle cx="80" cy="42" r="4" fill="#3a0a2a"/><circle cx="81.5" cy="40.5" r="1.4" fill="#fff"/><rect x="44" y="22" width="26" height="5" rx="2.5" fill="#8a1a5a"/>
<ellipse cx="42" cy="44" rx="12" ry="6" fill="rgba(255,255,255,.45)"/></svg>'''


def p_piggy():
    body = f'''<div style="display:grid;place-items:center;margin-top:-6px">{pig_svg(170)}</div>
 <div class="lb" style="text-align:center;font-size:13px;letter-spacing:.1em">SAVED FROM YOUR SPINS</div>{coinline("2,350,000")}
 <div class="prog" style="margin:12px 6px 4px;height:18px"><i style="width:78%;background:linear-gradient(180deg,#ffd6ec,#ff4fb4);box-shadow:0 0 8px #ff4fb4"></i></div><div class="lb" style="text-align:center;font-size:11px">78% full · fills as you play</div>
 <div class="fx" style="gap:10px;margin-top:16px"><span class="pill G" style="flex:1;height:50px;font-size:20px">BREAK $2.99</span></div>'''
    return over_lobby(modal('PIGGY BANK', body, 180))


def p_tasks():
    items = [('Spin 50 times', 32, 50, '10,000', 'G'), ('Win 3 times in a row', 3, 3, '15,000', 'DONE'), ('Play Christmas Eve', 0, 1, '20,000', 'G'), ('Trigger a Free Spins feature', 0, 1, '30,000', 'G')]
    rows = ''
    for t, a, b, rw, st in items:
        done = st == 'DONE'
        rows += f'''<div class="hd" style="padding:10px 12px;{"border-color:#5ae06a;box-shadow:0 0 12px rgba(90,230,110,.6)" if done else ""}"><div class="row2"><span class="t" style="flex:1;font-size:14px">{t}</span><span class="fx" style="gap:4px"><span class="coin" style="position:relative;left:0;top:0;transform:none;width:22px;height:22px;border-width:2px">{STAR}</span><span class="dg" style="font-size:17px">{rw}</span></span></div>
 <div class="row2" style="margin-top:8px"><div class="prog" style="flex:1"><i style="width:{a * 100 // b}%"></i></div><span class="t" style="font-size:12px;width:44px;text-align:right">{a}/{b}</span>{'<span class="pill G" style="height:28px;padding:0 10px;font-size:13px">CLAIM</span>' if done else ''}</div></div>'''
    tabs = ''.join(f'<span class="pill {c}" style="flex:1;height:32px;font-size:14px">{t}</span>' for t, c in [('DAILY TASKS', 'O'), ('MISSIONS', 'B')])
    body = f'<div class="fx" style="gap:8px;margin-bottom:12px">{tabs}</div><div style="display:flex;flex-direction:column;gap:10px">{rows}</div><div class="lb" style="text-align:center;font-size:11px;margin-top:12px">New tasks in 08:42:10</div>'
    return over_lobby(modal('TASKS', body, 140))


def p_profile():
    avs = ''.join(f'<div style="border-radius:50%;padding:2px;{"background:#ff5cf4;box-shadow:0 0 10px #ff5cf4" if n == 0 else "background:#3a4aaa"}">{I(f"a:{n}", "width:100%;aspect-ratio:1;border-radius:50%;display:block")}</div>' for n in range(8))
    stats = ''.join(f'<div class="hd" style="padding:8px;text-align:center"><div class="lb" style="font-size:10px;letter-spacing:.06em">{k}</div><div class="dg" style="font-size:18px">{v}</div></div>' for k, v in [('SPINS', '1,284'), ('BIGGEST WIN', '2.5M'), ('JACKPOTS', '3'), ('MEMBER', '42 days')])
    body = f'''<div class="row2"><div style="position:relative">{I("a:0", "width:76px;height:76px;border-radius:50%;border:3px solid #ff5cf4;box-shadow:0 0 14px #ff5cf4;display:block")}</div>
  <div style="flex:1"><div class="row2"><span class="t" style="font-size:20px">Player King</span><span class="pill B" style="height:26px;padding:0 10px;font-size:12px">✎ EDIT</span></div><div class="lb" style="font-size:12px;margin-top:2px">Level 6 · 1,240 / 2,000 XP</div><div class="prog" style="margin-top:6px;height:12px"><i style="width:62%"></i></div></div></div>
 <div class="lb" style="font-size:12px;letter-spacing:.1em;margin:14px 0 8px">CHOOSE AVATAR</div><div style="display:grid;grid-template-columns:repeat(8,1fr);gap:5px">{avs}</div>
 <div class="lb" style="font-size:12px;letter-spacing:.1em;margin:14px 0 8px">STATISTICS</div><div style="display:grid;grid-template-columns:1fr 1fr;gap:8px">{stats}</div>'''
    return over_lobby(modal('PROFILE', body, 170))


def toggle(on):
    return f'<div style="width:62px;height:30px;border-radius:15px;position:relative;background:{"linear-gradient(180deg,#8cff6a,#2fc93a)" if on else "#1a1a5a"};border:2px solid {"#0a5a10" if on else "#3a4aaa"};box-shadow:inset 0 2px 3px rgba(0,0,0,.35)"><div style="position:absolute;top:2px;{"right:2px" if on else "left:2px"};width:22px;height:22px;border-radius:50%;background:radial-gradient(circle at 40% 35%,#fff,#cfd8ff);box-shadow:0 2px 3px rgba(0,0,0,.4)"></div></div>'


def p_settings():
    rows = ''.join(f'<div class="hd row2" style="height:52px;padding:0 12px">{rb(k, 34)}<span class="t" style="flex:1;font-size:15px">{t}</span>{toggle(on)}</div>' for k, t, on in [('gear', 'Sound effects', True), ('gear', 'Music', False), ('gear', 'Vibration', True), ('gear', 'Notifications', True)])
    links = ''.join(f'<span class="pill {c}" style="height:40px;font-size:14px">{t}</span>' for t, c in [('HOW TO PLAY', 'B'), ('PRIVACY POLICY', 'B'), ('RATE US ★★★★★', 'O'), ('CONTACT SUPPORT', 'P')])
    body = f'<div style="display:flex;flex-direction:column;gap:10px">{rows}</div><div style="display:grid;grid-template-columns:1fr 1fr;gap:10px;margin-top:16px">{links}</div><div class="lb" style="text-align:center;font-size:11px;margin-top:14px">Version 1.0.0 · For entertainment only · No real money</div>'
    return over_lobby(modal('SETTINGS', body, 170))


def p_nocoins():
    body = f'''<div style="display:grid;place-items:center">{I("coinsS", "width:150px;filter:grayscale(.3)")}</div>
 <div class="t" style="text-align:center;font-size:15px;margin-top:6px">You need more coins for this bet</div>
 <div class="hd" style="margin-top:14px;padding:12px;text-align:center;border-color:#ff5cf4;box-shadow:0 0 14px rgba(255,80,240,.7)"><span class="pill P" style="display:inline-grid;height:22px;padding:0 10px;font-size:12px">SPECIAL OFFER · 300%</span>{coinline("6,000,000", 32)}<span class="pill G" style="height:46px;font-size:20px;margin-top:8px">$4.99</span></div>
 <div class="fx" style="gap:10px;margin-top:12px"><span class="pill B" style="flex:1;height:44px;font-size:15px">▶ FREE 50K</span><span class="pill O" style="flex:1;height:44px;font-size:15px">LOWER BET</span></div>'''
    return over_game(modal('OUT OF COINS', body, 200, ttl_size=32))


def p_unlock():
    body = f'''<div style="border-radius:16px;overflow:hidden;border:2px solid #6ab4ff;box-shadow:0 0 12px rgba(90,170,255,.6)">{I("c:neon", "width:100%;height:170px;object-fit:cover;display:block")}</div>
 <div class="t" style="text-align:center;font-size:20px;margin-top:10px">Neon Nights</div><div class="lb" style="text-align:center;font-size:12px">Cascade wins with multipliers up to x5</div>
 <div class="fx" style="gap:10px;margin-top:16px"><span class="pill O" style="flex:1;height:50px;font-size:17px">▶ WATCH TO UNLOCK</span></div>
 <div class="lb" style="text-align:center;font-size:12px;margin-top:10px">or reach <b style="color:#ffe46a">Level 10</b> to unlock for free</div>'''
    return over_lobby(modal('NEW MACHINE', body, 180))


def p_paytable():
    th = 'pirates'
    pays = [(8, 'WILD', 'Replaces all except Bonus'), (0, '5× 2,000', '4× 400 · 3× 100'), (1, '5× 1,000', '4× 250 · 3× 60'), (2, '5× 500', '4× 120 · 3× 40'), (3, '5× 250', '4× 60 · 3× 20')]
    rows = ''.join(f'<div class="hd row2" style="padding:6px 10px">{I(f"s:{th}:{i}", "width:50px;height:50px;object-fit:contain")}<div style="flex:1"><div class="dg" style="font-size:18px">{a}</div><div class="lb" style="font-size:11px">{b}</div></div></div>' for i, a, b in pays)
    body = f'''<div class="fx" style="gap:8px;margin-bottom:12px">{"".join(f'<span class="pill {c}" style="flex:1;height:30px;font-size:13px">{t}</span>' for t, c in [("PAYS", "O"), ("FEATURE", "B"), ("LINES", "B")])}</div>
 <div style="display:flex;flex-direction:column;gap:8px">{rows}</div><div class="lb" style="text-align:center;font-size:11px;margin-top:10px">Pays shown for a 2,500 total bet</div>'''
    return over_game(modal('PAYTABLE', body, 120))


# ---------------------------------------------------------------- page
SECTIONS = [
    ('Main screens', [(s_splash, 'Splash / loading'), (s_lobby, 'Lobby', 'Theme cards, daily bonus bar, filters, home button')]),
    ('Gameplay: 7 features', [(g_hold, 'Pirates · Hold &amp; Spin', 'GRAND marquee + MAJOR / MINOR / MINI'), (g_free, 'Christmas Eve · Free Spins Multiplier', 'During free spins: counter, multiplier ladder'),
                              (g_cascade, 'Neon Nights · Cascade', 'Winners explode, new symbols drop, multiplier climbs'), (g_diamond, 'Gems · Diamond Collection', 'Diamonds on reels fill the meter'),
                              (g_coin, 'Happy Farm · Coin Collector', 'Coins on reels, Collector grabs them all'), (g_wheel, 'Modern Suits · Bonus Wheel', '3 Bonus symbols open the wheel'),
                              (g_pick, "Witch's Cauldron · Pick Bonus", '3 scatters open the pick game')]),
    ('Bonus rounds', [(b_hold, 'Hold &amp; Spin round', 'Coins lock, respins reset to 3'), (b_wheel, 'Bonus Wheel', 'Coins and jackpots on the wheel'), (b_pick, 'Pick Bonus', 'Pick chests, 2X doubles the total'),
                      (p_freespins, 'Free Spins triggered'), (p_fs_summary, 'Feature win summary')]),
    ('Popups', [(p_welcome, 'Welcome gift'), (p_daily, 'Daily bonus'), (p_lucky, 'Lucky wheel (daily)'), (p_bigwin, 'Big win'), (p_jackpot, 'Grand jackpot win'), (p_levelup, 'Level up'),
                (p_store, 'Store'), (p_piggy, 'Piggy bank'), (p_tasks, 'Tasks &amp; missions'), (p_profile, 'Profile'), (p_settings, 'Settings'), (p_nocoins, 'Out of coins'),
                (p_unlock, 'Unlock machine'), (p_paytable, 'Paytable')]),
]

FONTS = '<link href="https://fonts.googleapis.com/css2?family=Lilita+One&family=Nunito:wght@700;800;900&display=swap" rel="stylesheet">'
DEFS = '<svg width="0" height="0" style="position:absolute"><defs><linearGradient id="lvg" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#fff3a0"/><stop offset=".6" stop-color="#ffc21a"/><stop offset="1" stop-color="#e07a00"/></linearGradient></defs></svg>'


def page(sections, head):
    body = ''
    for name, items in sections:
        body += f'<h2 class="sec">{name}</h2><div class="gal">' + ''.join(
            f'<div class="cell">{fn()}<div class="cap2">{it[1]}{f"<small>{it[2]}</small>" if len(it) > 2 else ""}</div></div>' for it in items for fn in [it[0]]) + '</div>'
    js = '<script>const IM=' + json.dumps(IM) + ';document.querySelectorAll("img[data-k]").forEach(e=>{e.src=IM[e.dataset.k]});</script>'
    pub = ':root{color-scheme:dark;--bg:#0d0b1e}body{background:var(--bg);padding-block:28px 40px;padding-left:max(16px,env(safe-area-inset-left,0px));padding-right:max(16px,env(safe-area-inset-right,0px))}@media(max-width:440px){.cell{zoom:.86}.gal{justify-content:center}}'
    return f'<title>FunPixel Slots Screens</title><meta name="claude:viewport-fit-sides" content="cover">{FONTS}<style>{CSS}{XCSS}{pub}</style>{DEFS}{head}{body}{js}'


WATCH = [('Rewarded video button', [(p_bigwin, 'Big win', 'Watch video button on top, Collect below'), (p_daily, 'Daily bonus'), (p_fs_summary, 'Feature win summary')])]
HEAD = '<p class="cap" style="font-size:34px">FunPixel Slots: full screen set</p><p class="sub">BingoFlash-style UI · gameplay for all 7 features · bonus rounds · popups</p>'
open(OUT + '/screens.html', 'w').write(page(SECTIONS, HEAD))
for n, sec in enumerate(SECTIONS):
    open(OUT + f'/screens_{n}.html', 'w').write(page([sec], ''))
open(OUT + '/watch.html', 'w').write(page(WATCH, ''))
print('ok', len(IM))
