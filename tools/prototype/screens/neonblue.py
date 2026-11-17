# Slots mockup in the BingoFlash style: deep-space navy, glowing blue panel rims, glossy pills/balls, gold display type.
import base64, io, json, random, sys
from PIL import Image
OUT = sys.argv[1]
U = '/home/user/slots_android_new/Assets/HolidaySlotAsset/'
A = json.loads(open('/home/user/slots_scg_native/tools/prototype/assets.js').read()[len('window.ASSETS='):-2])
L = {e['id']: e['cover'] for e in A['lobby']}
T = A['themes']; AV = A['avatars']


def img(path, w):
    im = Image.open(U + path).convert('RGBA')
    if im.width > w * 2:
        im = im.resize((w * 2, round(im.height * w * 2 / im.width)), Image.LANCZOS)
    b = io.BytesIO(); im.save(b, 'WEBP', quality=88)
    return 'data:image/webp;base64,' + base64.b64encode(b.getvalue()).decode()


CHEST = img('10_CandyTheme/Sprites_Candy/PopUps/Chest/ChestOpened.png', 150)
COINS = img('20_GreekTheme/Sprites_Greek/Pop Ups/Panels/Coins Jackpot.png', 260)

CSS = r"""
*{box-sizing:border-box}
body{margin:0;background:#0d0b1e;padding:28px 34px 34px;font-family:Nunito,sans-serif}
.cap{color:#fff;font:900 24px Nunito;margin:0 0 4px}.sub{color:#a9b4e8;font:700 14px Nunito;margin:0 0 18px}
.row{display:flex;gap:34px}
.ph{width:390px;height:844px;border-radius:46px;overflow:hidden;position:relative;box-shadow:0 0 0 10px #000,0 0 0 12px #333;color:#fff;flex:none}
.ab{position:absolute}.fx{display:flex;align-items:center}
.sb{position:absolute;left:0;right:0;top:0;height:30px;display:flex;justify-content:space-between;align-items:center;padding:0 30px;font:900 13px Nunito;z-index:60}
.ad{position:absolute;left:0;right:0;bottom:0;height:50px;z-index:50;background:#fff;color:#666;display:grid;place-items:center;font:800 12px Nunito}
/* space background */
.space{position:absolute;inset:0;background:radial-gradient(ellipse 70% 40% at 50% 12%,rgba(140,90,255,.55),transparent 70%),repeating-conic-gradient(from 0deg at 50% 8%,rgba(150,120,255,.10) 0 4deg,transparent 4deg 13deg),radial-gradient(ellipse 90% 30% at 50% 100%,rgba(60,120,255,.45),transparent 70%),linear-gradient(180deg,#2a1aa8,#14107a 35%,#0a0a52 70%,#070838)}
.space::after{content:"";position:absolute;inset:0;background:radial-gradient(circle,#fff 0 1px,transparent 1.6px) 0 0/47px 53px,radial-gradient(circle,rgba(255,220,120,.9) 0 1.3px,transparent 2.2px) 13px 27px/83px 71px,radial-gradient(circle,rgba(170,200,255,.8) 0 1px,transparent 1.8px) 31px 9px/61px 97px;opacity:.75}
/* display type */
.dg{font-family:'Lilita One',sans-serif;background:linear-gradient(180deg,#fffbd0,#ffe23a 40%,#ffa800 70%,#ff7a00);-webkit-background-clip:text;background-clip:text;color:transparent;filter:drop-shadow(2px 0 0 #3a0a8a) drop-shadow(-2px 0 0 #3a0a8a) drop-shadow(0 2px 0 #3a0a8a) drop-shadow(0 -2px 0 #3a0a8a) drop-shadow(0 5px 0 #24065e) drop-shadow(0 0 10px rgba(255,180,40,.45))}
.t{font-weight:900;color:#fff;text-shadow:0 2px 0 rgba(10,10,60,.7)}
.lb{font-weight:800;color:#9fc4ff}
/* glow panel */
.pn{border-radius:22px;background:linear-gradient(180deg,#1e1ea8,#0c0c62 60%,#090a50);border:2.5px solid #6ab4ff;box-shadow:0 0 0 1.5px #1a2690,0 0 14px rgba(90,170,255,.75),inset 0 0 14px rgba(90,170,255,.35),inset 0 3px 0 rgba(200,230,255,.35)}
.pn.mg{border-color:#ff5cf4;box-shadow:0 0 0 1.5px #5a0a8a,0 0 16px rgba(255,80,240,.9),0 0 30px rgba(255,80,240,.45),inset 0 0 12px rgba(255,80,240,.35)}
/* glossy pill */
.pill{display:grid;place-items:center;white-space:nowrap;border-radius:999px;font-family:'Lilita One',sans-serif;color:#fff;text-shadow:0 2px 0 rgba(0,0,0,.35);border:2px solid var(--d);background:linear-gradient(180deg,var(--a),var(--b) 55%,var(--c));box-shadow:inset 0 2px 0 rgba(255,255,255,.55),0 3px 0 var(--d),0 0 10px var(--g)}
.pill:not(.ab){position:relative}.pill::after{content:"";position:absolute;left:10%;right:10%;top:3px;height:34%;border-radius:999px;background:linear-gradient(rgba(255,255,255,.55),rgba(255,255,255,0))}
.G{--a:#8cff6a;--b:#2fc93a;--c:#0f8a1a;--d:#0a5a10;--g:rgba(80,255,90,.35)}
.R{--a:#ff9a8a;--b:#ff3040;--c:#b00818;--d:#6a0008;--g:rgba(255,60,80,.35)}
.O{--a:#ffe07a;--b:#ffa21a;--c:#e35a00;--d:#7a2a00;--g:rgba(255,160,40,.4)}
.B{--a:#8ad0ff;--b:#2a7bff;--c:#1238c8;--d:#0a1a7a;--g:rgba(60,140,255,.45)}
.P{--a:#d79aff;--b:#9a3aff;--c:#5a12c8;--d:#2a0670;--g:rgba(170,80,255,.45)}
/* glossy round button */
.rb{border-radius:50%;display:grid;place-items:center;position:relative;background:radial-gradient(circle at 50% 35%,#5aa0ff,#2a5aff 55%,#1430b8);border:3px solid #0c1a80;box-shadow:inset 0 0 0 2px #7ab8ff,0 3px 0 #08105a,0 0 12px rgba(80,150,255,.6)}
.rb::after{content:"";position:absolute;left:18%;right:18%;top:8%;height:36%;border-radius:50%;background:linear-gradient(rgba(255,255,255,.6),rgba(255,255,255,0))}
.rb svg{width:55%;height:55%;fill:#fff;position:relative;z-index:1;filter:drop-shadow(0 1px 0 rgba(0,0,0,.3))}
/* coin bar */
.cbar{height:38px;border-radius:19px;background:linear-gradient(180deg,#4a6aff,#2a3ad8 55%,#1a22a8);border:2px solid #8ab8ff;box-shadow:0 0 10px rgba(90,150,255,.6),inset 0 2px 0 rgba(255,255,255,.4);padding:0 4px 0 48px;position:relative}
.coin{position:absolute;left:-6px;top:50%;transform:translateY(-50%);width:46px;height:46px;border-radius:50%;background:radial-gradient(circle at 40% 35%,#fff3a0,#ffc21a 50%,#e07a00);border:3px solid #ffeb8a;box-shadow:0 0 0 2px #a04a00,0 3px 6px rgba(0,0,0,.4);display:grid;place-items:center}
.coin svg{width:60%;fill:#ffe46a;stroke:#c46a00;stroke-width:1.5}
/* jackpot ball */
.ball{width:46px;height:46px;border-radius:50%;flex:none;display:grid;place-items:center;background:radial-gradient(circle at 50% 45%,#fff 0 46%,var(--r) 48%,var(--r2) 100%);box-shadow:0 0 0 2px rgba(0,0,0,.3),0 0 10px var(--r),inset 0 3px 3px rgba(255,255,255,.6);font:900 8.5px Nunito;color:#1a1a5a;letter-spacing:-.02em}
"""
STAR = '<svg viewBox="0 0 24 24"><path d="M12 2.5l2.9 6 6.6.8-4.9 4.5 1.3 6.5L12 17l-5.9 3.3 1.3-6.5-4.9-4.5 6.6-.8z"/></svg>'
IC = {'gear': '<svg viewBox="0 0 24 24"><path d="M19.4 13a7.6 7.6 0 000-2l2.1-1.6-2-3.5-2.5 1a7.5 7.5 0 00-1.7-1L15 3.3h-4l-.4 2.6a7.5 7.5 0 00-1.7 1l-2.5-1-2 3.5L6.6 11a7.6 7.6 0 000 2l-2.2 1.6 2 3.5 2.5-1a7.5 7.5 0 001.7 1l.4 2.6h4l.4-2.6a7.5 7.5 0 001.7-1l2.5 1 2-3.5zM13 15.5a3.5 3.5 0 110-7 3.5 3.5 0 010 7z"/></svg>',
      'back': '<svg viewBox="0 0 24 24"><path d="M14.5 5l-7 7 7 7 2-2-5-5 5-5z"/></svg>',
      'menu': '<svg viewBox="0 0 24 24"><rect x="4" y="5" width="16" height="3" rx="1.5"/><rect x="4" y="10.5" width="16" height="3" rx="1.5"/><rect x="4" y="16" width="16" height="3" rx="1.5"/></svg>',
      'gift': '<svg viewBox="0 0 24 24"><path d="M3 9h18v4H3zM4 13h7v8H4zM13 13h7v8h-7zM11 9h2v12h-2zM12 9C9 4 5 5 7 8M12 9c3-5 7-4 5-1"/></svg>',
      'cart': '<svg viewBox="0 0 24 24"><path d="M3 4h3l2.5 10h10L21 7H8"/><circle cx="10" cy="19" r="1.8"/><circle cx="17" cy="19" r="1.8"/></svg>',
      'trophy': '<svg viewBox="0 0 24 24"><path d="M7 3h10v6a5 5 0 01-10 0zM5 5H3a4 4 0 004 5zM19 5h2a4 4 0 01-4 5zM11 14h2v4h-2zM8 19h8v2H8z"/></svg>',
      'user': '<svg viewBox="0 0 24 24"><circle cx="12" cy="8" r="4.5"/><path d="M3.5 21c1-5 4.5-7 8.5-7s7.5 2 8.5 7z"/></svg>',
      'task': '<svg viewBox="0 0 24 24"><path d="M6 3h12v18H6z" opacity=".35"/><path d="M8.5 8l1.5 1.5L13 6.5M8.5 13.5l1.5 1.5 3-3M15 8h1.5M15 14h1.5" stroke="#fff" stroke-width="2" fill="none"/></svg>',
      'wheel': '<svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="9" opacity=".35"/><path d="M12 3v18M3 12h18M5.6 5.6l12.8 12.8M18.4 5.6L5.6 18.4" stroke="#fff" stroke-width="1.8"/><circle cx="12" cy="12" r="2.5"/></svg>',
      'house': '<svg viewBox="0 0 24 24"><path d="M12 3l9 8h-3v9h-4.5v-6h-3v6H6v-9H3z"/></svg>'}


def rb(k, s):
    return f'<div class="rb" style="width:{s}px;height:{s}px">{IC[k]}</div>'


def coinbar(n, w=None):
    return f'<div class="cbar fx" style="{"width:%dpx" % w if w else "flex:1"}"><div class="coin">{STAR}</div><span class="t" style="flex:1;font-size:{16 if w else 18}px;font-family:Lilita One">{n}</span><span class="pill G" style="width:28px;height:28px;font-size:20px">+</span></div>'


def logo(top):
    return f'''<div class="ab" style="left:0;right:0;top:{top}px;height:80px">
 <div class="ab" style="left:50%;top:-10px;width:300px;height:120px;transform:translateX(-50%);background:repeating-conic-gradient(from 210deg at 50% 100%,rgba(190,120,255,.55) 0 4deg,transparent 4deg 11deg);-webkit-mask:radial-gradient(ellipse 50% 100% at 50% 100%,#000 30%,transparent 72%);clip-path:polygon(0 0,100% 0,100% 100%,0 100%)"></div>
 <div class="ab dg" style="left:0;right:0;top:4px;text-align:center;font-size:44px;line-height:.9;letter-spacing:.02em">FUNPIXEL</div>
 <div class="ab" style="left:50%;top:46px;transform:translateX(-50%) rotate(-3deg);padding:2px 18px;border-radius:12px;background:linear-gradient(180deg,#ff6ad8,#c41aa8);border:2px solid #ffd0f4;box-shadow:0 0 14px rgba(255,80,220,.8);font:400 22px Lilita One;color:#fff;text-shadow:0 2px 0 #5a0a5a;letter-spacing:.12em">SLOTS</div>
 {''.join(f'<span class="ab" style="left:{x}px;top:{y}px;width:{s}px;height:{s}px;color:#ffe46a">{STAR.replace("<svg", "<svg style=fill:#ffe46a;filter:drop-shadow(0_0_6px_#ffb400)")}</span>' for x, y, s in [(70, 6, 16), (300, 2, 20), (52, 52, 12), (320, 50, 14)])}
</div>'''


def card(i, name, feat, btn, sel=False, lock=False):
    cls, txt = btn
    return f'''<div class="pn{' mg' if sel else ''}" style="position:relative;padding:4px;border-radius:20px">
 <div style="border-radius:15px;overflow:hidden;position:relative"><img src="{L[i]}" style="width:100%;aspect-ratio:600/430;object-fit:cover;object-position:top;display:block;{'filter:grayscale(.6) brightness(.6)' if lock else ''}">
 {'<div class="ab" style="inset:0;display:grid;place-items:center"><svg viewBox="0 0 24 24" style="width:40px;fill:#e8ecff;filter:drop-shadow(0 3px 0 rgba(0,0,0,.5))"><path d="M7 10V8a5 5 0 0110 0v2h1.5v11h-13V10zM9.5 10h5V8a2.5 2.5 0 00-5 0z"/></svg></div>' if lock else ''}</div>
 <div style="padding:6px 6px 8px;text-align:center"><div class="t" style="font-size:15px;line-height:1.1">{name}</div><div class="lb" style="font-size:11px;margin:2px 0 7px">{feat}</div>
 <span class="pill {cls}" style="height:30px;font-size:15px">{txt}</span></div>
 {'<div class="ab pill O" style="top:-8px;right:-6px;padding:1px 9px;font-size:11px">HOT</div>' if sel else ''}</div>'''


def lobby(dim=False):
    cards = ''.join([card('pirates', 'Pirates', 'Hold &amp; Spin · Jackpots', ('G', 'PLAY'), sel=True), card('christmas_eve', 'Christmas Eve', 'Free Spins up to x10', ('G', 'PLAY')),
                     card('neon', 'Neon Nights', 'Cascade wins', ('O', '▶ UNLOCK'), lock=True), card('gems', 'Gems', 'Diamond Collect', ('P', 'LEVEL 5'), lock=True)])
    tabs = ''.join(f'<span class="pill {c}" style="height:30px;padding:0 13px;font-size:13px">{t}</span>' for t, c in [('ALL', 'O'), ('JACKPOT', 'B'), ('FREE SPINS', 'B'), ('BONUS', 'B')])
    navs = [('cart', 'STORE'), ('task', 'TASKS'), None, ('trophy', 'EVENTS'), ('user', 'PROFILE')]
    nav = ''.join('<div style="width:86px;height:86px;margin-top:-44px;border-radius:50%;padding:4px;background:conic-gradient(from 0deg,#ffe46a,#ff9a00,#ffe46a,#ff9a00,#ffe46a);box-shadow:0 0 18px rgba(255,190,40,.9)"><div class="rb" style="width:100%;height:100%;background:radial-gradient(circle at 50% 35%,#8cff6a,#2fc93a 55%,#0f8a1a);border-color:#0a5a10;box-shadow:inset 0 0 0 2px #b4ff9a">' + IC['house'] + '</div></div>' if n is None else
                  f'<div class="fx" style="flex-direction:column;gap:3px">{rb(n[0], 44)}<span class="t" style="font-size:10px;letter-spacing:.04em">{n[1]}</span></div>' for n in navs)
    return f'''<div class="ph"><div class="space"></div><div class="ab" style="inset:0;{'filter:brightness(.35) blur(2px)' if dim else ''}">
 <div class="ab fx" style="left:16px;right:14px;top:40px;gap:12px">
  <div style="position:relative;width:46px;height:46px;flex:none"><img src="{AV[0]}" style="width:46px;height:46px;border-radius:50%;border:2.5px solid #8ab8ff;box-shadow:0 0 10px rgba(90,150,255,.8);display:block"><span class="ab pill P" style="left:50%;transform:translateX(-50%);bottom:-8px;padding:0 7px;font-size:10px">LV 6</span></div>
  {coinbar('12,500,000')}{rb('gear', 42)}</div>
 {logo(96)}
 <div class="ab pn fx" style="left:14px;right:14px;top:196px;height:84px;padding:0 12px 0 96px">
  <img class="ab" src="{CHEST}" style="left:-6px;top:-24px;width:110px;filter:drop-shadow(0 0 12px rgba(255,200,60,.7))">
  <div style="flex:1"><div class="dg" style="font-size:22px">DAILY BONUS</div><div class="lb" style="font-size:12px">Day 4 streak · 375,000 coins</div></div>
  <span class="pill G" style="height:38px;padding:0 16px;font-size:16px">COLLECT</span></div>
 <div class="ab fx" style="left:14px;right:14px;top:298px;gap:7px">{tabs}</div>
 <div class="ab" style="left:14px;right:14px;top:344px;display:grid;grid-template-columns:1fr 1fr;gap:14px 12px">{cards}</div>
 <div class="ab fx" style="left:0;right:0;bottom:50px;height:78px;z-index:20;justify-content:space-around;padding:0 6px;background:linear-gradient(180deg,#1a1a96,#0a0a52);border-top:2.5px solid #6ab4ff;box-shadow:0 -4px 16px rgba(90,170,255,.5)">{nav}</div>
 </div><div class="sb"><span>9:41</span><span>●●● ▮</span></div><div class="ad">Banner ad space</div></div>'''



def spinball(size, top=None):
    pos = f"left:50%;top:{top}px;transform:translateX(-50%);" if top is not None else ""
    return f'<div class="ab" style="{pos}width:{size}px;height:{size}px;border-radius:50%;padding:6px;background:conic-gradient(from 0deg,#ffe46a,#ff9a00,#fff3a0,#ff9a00,#ffe46a,#ff9a00,#ffe46a);box-shadow:0 0 24px rgba(255,190,40,.9),0 0 0 3px #3a0a8a"><div class="rb" style="width:100%;height:100%;background:radial-gradient(circle at 50% 35%,#a8ff7a,#2fc93a 55%,#0f7a1a);border-color:#0a5a10;box-shadow:inset 0 0 0 3px #c4ffb0,inset 0 -8px 0 rgba(0,0,0,.18)"><div style="position:relative;z-index:1;text-align:center;line-height:1"><div class="t" style="font:400 {size//4}px Lilita One;text-shadow:0 3px 0 #0a5a10">SPIN</div><div class="t" style="font-size:9px;margin-top:3px;letter-spacing:.06em">HOLD FOR AUTO</div></div></div></div>'

def spinrect(w, h, top):
    return f'<div class="ab" style="left:50%;top:{top}px;transform:translateX(-50%);width:{w}px;height:{h}px;border-radius:22px;padding:5px;background:linear-gradient(180deg,#fff3a0,#ffc21a 35%,#e07a00 60%,#ffe46a);box-shadow:0 0 0 2px #3a0a8a,0 5px 0 2px #24065e,0 0 22px rgba(255,190,40,.85)"><div style="width:100%;height:100%;border-radius:17px;position:relative;overflow:hidden;background:linear-gradient(180deg,#b4ff8a,#3ad44a 45%,#1aa02a 55%,#0f7a1a);border:2px solid #0a5a10;box-shadow:inset 0 -6px 0 rgba(0,0,0,.18),inset 0 0 0 2px rgba(200,255,180,.6);display:flex;flex-direction:column;align-items:center;justify-content:center"><div style="position:absolute;left:8%;right:8%;top:4px;height:40%;border-radius:14px;background:linear-gradient(rgba(255,255,255,.65),rgba(255,255,255,0))"></div><div class="t" style="position:relative;font:400 {int(h*0.42)}px Lilita One;line-height:1;text-shadow:0 3px 0 #0a5a10,0 0 8px rgba(0,80,0,.4);letter-spacing:.04em">SPIN</div><div class="t" style="position:relative;font-size:9px;margin-top:2px;letter-spacing:.08em;text-shadow:0 1px 0 #0a5a10">HOLD FOR AUTO</div></div></div>'

STEP = '<div class="fx" style="gap:2px;justify-content:space-between;background:#070840;border-radius:20px;padding:3px;box-shadow:inset 0 2px 4px rgba(0,0,0,.6)"><span class="pill R" style="width:30px;height:30px;font-size:20px">−</span><span class="t" style="font:400 16px Lilita One">2,500</span><span class="pill G" style="width:30px;height:30px;font-size:20px">+</span></div>'

def footer(fv):
    if fv == 'A':  # symmetric: bet column | SPIN | auto/max column, all vertically centred
        return f"""<div class="ab pn" style="left:10px;right:10px;bottom:62px;height:140px;border-radius:26px">
  <div class="ab" style="left:10px;top:20px;width:100px;height:100px;display:flex;flex-direction:column;justify-content:center;gap:8px;text-align:center"><div class="lb" style="font-size:11px;letter-spacing:.1em">TOTAL BET</div>{STEP}<span class="pill O" style="height:30px;font-size:13px">MAX BET</span></div>
  {spinrect(134, 96, 22)}
  <div class="ab" style="right:10px;top:20px;width:100px;height:100px;display:flex;flex-direction:column;justify-content:center;gap:10px"><span class="pill B" style="height:38px;font-size:16px">AUTO</span><span class="pill P" style="height:38px;font-size:16px">PAYTABLE</span></div>
 </div>"""
    if fv == 'B':  # two rows: bet bar on top, round buttons + SPIN below
        return f"""<div class="ab pn" style="left:10px;right:10px;bottom:62px;height:178px;border-radius:26px">
  <div class="ab fx" style="left:14px;right:14px;top:12px;gap:10px"><div class="lb" style="font-size:11px;letter-spacing:.08em;width:46px;line-height:1.1">TOTAL<br>BET</div><div style="flex:1">{STEP}</div><span class="pill O" style="height:36px;padding:0 14px;font-size:14px">MAX BET</span></div>
  <div class="ab" style="left:14px;right:14px;top:58px;height:1px;background:linear-gradient(90deg,transparent,#6ab4ff,transparent)"></div>
  <div class="ab fx" style="left:20px;top:84px;flex-direction:column;gap:3px"><div class="rb" style="width:56px;height:56px;background:radial-gradient(circle at 50% 35%,#8ad0ff,#2a7bff 55%,#1238c8)"><span class="t" style="position:relative;z-index:1;font:400 14px Lilita One">AUTO</span></div></div>
  {spinrect(176, 84, 74)}
  <div class="ab fx" style="right:20px;top:84px;flex-direction:column;gap:3px"><div class="rb" style="width:56px;height:56px;background:radial-gradient(circle at 50% 35%,#d79aff,#9a3aff 55%,#5a12c8);border-color:#2a0670;box-shadow:inset 0 0 0 2px #e0b8ff,0 3px 0 #1a0450,0 0 12px rgba(170,80,255,.6)"><span class="t" style="position:relative;z-index:1;font:400 26px Lilita One">i</span></div></div>
 </div>"""
    if fv == 'C':
        return f"""<div class="ab pn" style="left:10px;right:10px;bottom:62px;height:128px;border-radius:26px">
  <div class="ab" style="left:14px;top:14px;width:184px;display:flex;flex-direction:column;gap:8px">
   <div class="lb" style="font-size:11px;letter-spacing:.12em;text-align:center">TOTAL BET</div>{STEP.replace('font:400 16px','font:400 19px').replace('width:30px;height:30px','width:34px;height:34px')}
   <span class="pill O" style="height:34px;font-size:15px">MAX BET</span></div>
  <div class="ab" style="right:12px;top:14px;width:146px;height:100px;border-radius:22px;padding:5px;background:linear-gradient(180deg,#fff3a0,#ffc21a 35%,#e07a00 60%,#ffe46a);box-shadow:0 0 0 2px #3a0a8a,0 5px 0 2px #24065e,0 0 22px rgba(255,190,40,.85)"><div style="width:100%;height:100%;border-radius:17px;position:relative;overflow:hidden;background:linear-gradient(180deg,#b4ff8a,#3ad44a 45%,#1aa02a 55%,#0f7a1a);border:2px solid #0a5a10;box-shadow:inset 0 -6px 0 rgba(0,0,0,.18),inset 0 0 0 2px rgba(200,255,180,.6);display:flex;flex-direction:column;align-items:center;justify-content:center"><div style="position:absolute;left:8%;right:8%;top:4px;height:40%;border-radius:14px;background:linear-gradient(rgba(255,255,255,.65),rgba(255,255,255,0))"></div><div class="t" style="position:relative;font:400 40px Lilita One;line-height:1;text-shadow:0 3px 0 #0a5a10;letter-spacing:.04em">SPIN</div><div class="t" style="position:relative;font-size:9px;margin-top:3px;letter-spacing:.08em;text-shadow:0 1px 0 #0a5a10">HOLD FOR AUTO</div></div></div>
 </div>"""
    return ''

GOLDB = 'linear-gradient(180deg,#fff7c9,#ffd24a 28%,#b86b00 55%,#ffdc6e 80%,#8a4a00)'

def jackpots(jv, old):
    if jv == 'D':  # bulb marquee GRAND with odometer digits + three coloured plaques
        digs = ''.join(f'<span style="display:inline-block;width:{6 if ch == "," else 21}px;height:32px;line-height:32px;text-align:center;{"" if ch == "," else "border-radius:5px;background:linear-gradient(180deg,#1a0430,#3a0a5a 48%,#12021f 52%,#2a0646);box-shadow:inset 0 0 0 1px rgba(255,200,80,.45),inset 0 2px 3px rgba(0,0,0,.7);"}font:400 {14 if ch == "," else 24}px Lilita One;color:#ffe46a;text-shadow:0 0 6px rgba(255,190,40,.9)">{ch}</span>' for ch in '2,502,125')
        def plaque(l, v, c1, c2, glow):
            return f'<div style="flex:1;position:relative;padding-top:8px"><div style="height:34px;border-radius:12px;border:2px solid {c1};background:linear-gradient(180deg,{c2},#0a0a3a);box-shadow:0 0 10px {glow},inset 0 0 10px {glow};display:grid;place-items:center;padding-top:3px"><span class="t" style="font:400 15px Lilita One;text-shadow:0 0 6px {glow},0 2px 0 rgba(0,0,0,.5)">{v}</span></div><span class="ab" style="left:50%;top:0;transform:translateX(-50%);padding:1px 10px;border-radius:8px;background:linear-gradient(180deg,{c1},{c2});border:1.5px solid rgba(255,255,255,.7);font:900 9px Nunito;letter-spacing:.14em;color:#fff;white-space:nowrap;text-shadow:0 1px 0 rgba(0,0,0,.4)">{l}</span></div>'
        return f"""<div class="ab" style="left:22px;right:22px;top:92px;height:56px;border-radius:16px;border:3px solid transparent;background:radial-gradient(ellipse at 50% 0,#c4123a,#5a0420 80%) padding-box,{GOLDB} border-box;box-shadow:0 0 0 2px #3a0a00,0 0 20px rgba(255,170,40,.7)">
  <div class="ab" style="inset:2px;border-radius:12px;background:radial-gradient(circle,#fff8d0 0 1.5px,#ffcc33 2.1px,transparent 3px) 0 0/11px 11px;-webkit-mask:linear-gradient(#000 0 0) content-box,linear-gradient(#000 0 0);-webkit-mask-composite:xor;padding:4px;filter:drop-shadow(0 0 3px #ffb400)"></div>
  <div class="ab fx" style="left:0;right:0;top:9px;justify-content:center;gap:2px">{digs}</div></div>
 <div class="ab" style="left:50%;top:80px;transform:translateX(-50%);padding:0 18px;height:22px;display:grid;place-items:center;background:linear-gradient(180deg,#ffe46a,#ffb000 55%,#e07a00);clip-path:polygon(0 0,100% 0,94% 50%,100% 100%,0 100%,6% 50%);font:400 14px Lilita One;letter-spacing:.18em;color:#5a0a00">♛ GRAND ♛</div>
 <div class="ab fx" style="left:16px;right:16px;top:150px;gap:8px">{plaque('MAJOR', '500,000', '#c46aff', '#5a12c8', 'rgba(190,100,255,.6)')}{plaque('MINOR', '100,000', '#4ab0ff', '#1238c8', 'rgba(80,170,255,.6)')}{plaque('MINI', '25,000', '#5ae06a', '#0f7a1a', 'rgba(90,230,110,.55)')}</div>"""
    if jv == 'A':  # wide GRAND bar + three slim plaques
        small = ''.join(f'<div class="pill {c}" style="flex:1;height:40px;display:flex;flex-direction:column;justify-content:center;line-height:1.05"><span style="font:900 10px Nunito;letter-spacing:.1em">{l}</span><span style="font-size:15px">{v}</span></div>' for l, v, c in [('MAJOR', '500,000', 'P'), ('MINOR', '100,000', 'G'), ('MINI', '25,000', 'B')])
        return f"""<div class="ab fx" style="left:14px;right:14px;top:88px;height:52px;border-radius:26px;border:3px solid transparent;background:radial-gradient(ellipse at 50% 0,#ff4a6a,#8a0630 75%) padding-box,{GOLDB} border-box;box-shadow:0 0 0 2px #3a0a00,0 0 18px rgba(255,190,60,.6);padding:0 16px 0 6px">
  <span class="pill R" style="height:36px;padding:0 12px;font-size:15px;letter-spacing:.06em">GRAND</span><span class="dg" style="flex:1;text-align:center;font-size:30px">2,502,125</span></div>
 <div class="ab fx" style="left:18px;right:18px;top:146px;gap:8px">{small}</div>"""
    if jv == 'B':  # marquee sign with bulbs and crown
        small = ''.join(f'<div style="flex:1;text-align:center;line-height:1.05"><div style="font:800 10px Nunito;letter-spacing:.12em;color:{c}">{l}</div><div class="t" style="font:400 15px Lilita One">{v}</div></div>' for l, v, c in [('MAJOR', '500,000', '#d79aff'), ('MINOR', '100,000', '#8cff6a'), ('MINI', '25,000', '#8ad0ff')])
        return f"""<div class="ab pn" style="left:14px;right:14px;top:86px;height:100px;border-radius:20px;border-color:#ffc21a;box-shadow:0 0 0 1.5px #5a2a00,0 0 16px rgba(255,190,40,.8),inset 0 0 14px rgba(255,190,40,.3)">
  <div class="ab" style="inset:3px;border-radius:17px;background:radial-gradient(circle,#fff8d0 0 1.6px,#ffcc33 2.2px,transparent 3.2px) 0 0/12px 12px;-webkit-mask:linear-gradient(#000 0 0) content-box,linear-gradient(#000 0 0);-webkit-mask-composite:xor;padding:4px;filter:drop-shadow(0 0 3px #ffb400)"></div>
  <div class="ab" style="left:0;right:0;top:9px;text-align:center"><span style="font:800 10px Nunito;letter-spacing:.3em;color:#ffd98a">♛ GRAND JACKPOT ♛</span></div>
  <div class="ab" style="left:0;right:0;top:21px;text-align:center"><span class="dg" style="font-size:30px">2,502,125</span></div>
  <div class="ab fx" style="left:14px;right:14px;top:58px;border-top:1px solid rgba(255,200,80,.35);padding-top:4px">{small}</div></div>"""
    if jv == 'C':  # four plates with label tabs, GRAND widest
        def plate(l, v, bg, tx, w):
            return f'<div style="flex:{w};position:relative;padding-top:9px"><div style="height:46px;border-radius:14px;background:{bg};border:2px solid rgba(255,255,255,.6);box-shadow:inset 0 3px 0 rgba(255,255,255,.45),inset 0 -4px 0 rgba(0,0,0,.18),0 3px 0 #08084a,0 0 12px rgba(120,160,255,.35);display:grid;place-items:center;padding-top:4px"><span style="font:400 {17 if w > 1 else 14}px Lilita One;color:{tx};text-shadow:0 2px 0 rgba(0,0,0,.3)">{v}</span></div><span class="ab" style="left:50%;top:0;transform:translateX(-50%);padding:1px 8px;border-radius:8px;background:#0a0a52;border:1.5px solid #6ab4ff;font:900 9px Nunito;letter-spacing:.1em;color:#fff;white-space:nowrap">{l}</span></div>'
        return f"""<div class="ab fx" style="left:12px;right:12px;top:96px;gap:6px">{plate('MINI', '25,000', 'linear-gradient(180deg,#8ad0ff,#2a7bff 55%,#1238c8)', '#fff', 1)}{plate('MINOR', '100,000', 'linear-gradient(180deg,#8cff6a,#2fc93a 55%,#0f8a1a)', '#fff', 1)}{plate('MAJOR', '500,000', 'linear-gradient(180deg,#d79aff,#9a3aff 55%,#5a12c8)', '#fff', 1)}{plate('GRAND', '2,502,125', 'linear-gradient(180deg,#fff3a0,#ffc21a 50%,#e07a00)', '#5a1a00', 1.45)}</div>"""
    return f'<div class="ab pn fx" style="left:14px;right:14px;top:96px;height:82px;padding:4px 8px 0;border-radius:41px">{old}</div>'


def game(pop=False, fv='O', jv='O'):
    th = T['pirates']
    random.seed(3)
    g = [[random.randrange(9) for _ in range(3)] for _ in range(5)]
    for r in (1, 2, 3):
        g[r][1] = 8
    reels = ''
    for r in range(5):
        for w in range(3):
            pass
    cells = ''.join(f'<div style="display:grid;place-items:center;position:relative">{'<div style="position:absolute;inset:2px;border-radius:50%;background:radial-gradient(circle,rgba(255,220,90,.75),rgba(255,170,0,.25) 55%,transparent 72%)"></div>' if (r in (1, 2, 3) and w == 1) else ''}<img src="{th["sym"][g[r][w]]}" style="position:relative;width:64px;height:64px;object-fit:contain;filter:drop-shadow(0 3px 3px rgba(0,0,30,.6)){' drop-shadow(0 0 6px #ffd23a)' if (r in (1, 2, 3) and w == 1) else ''}"></div>'
                    for w in range(3) for r in range(5))
    jps = ''.join(f'<div class="fx" style="flex-direction:column;gap:3px;flex:1"><div class="ball" style="--r:{c};--r2:{c2}">{l}</div><span class="t" style="font-size:12px;font-family:Lilita One">{v}</span></div>'
                  for l, v, c, c2 in [('MINI', '25K', '#2a9bff', '#0a4ad0'), ('MINOR', '100K', '#2fc93a', '#0f7a1a'), ('MAJOR', '500K', '#b04aff', '#5a12c8'), ('GRAND', '2.50M', '#ff3040', '#a00818')])
    jps = jps.replace('font:900 12px', 'font:900 12px')
    popup = ''
    if pop:
        random.seed(5)
        stars = ''.join(f'<span class="ab" style="left:{random.randint(10, 370)}px;top:{random.randint(120, 640)}px;width:{random.choice([10, 14, 18, 24])}px">{STAR.replace("<svg", "<svg style=fill:#ffe46a;filter:drop-shadow(0_0_6px_#ffb400)")}</span>' for _ in range(16))
        popup = f'''<div class="ab" style="inset:0;z-index:30;background:radial-gradient(circle at 50% 36%,rgba(150,90,255,.55),rgba(5,4,40,.9) 65%)"></div>
 <div class="ab" style="inset:0;z-index:31">
  <div class="ab" style="left:-40px;right:-40px;top:60px;height:380px;background:repeating-conic-gradient(from 0deg at 50% 60%,rgba(200,140,255,.35) 0 4deg,transparent 4deg 12deg);-webkit-mask:radial-gradient(circle at 50% 60%,#000 15%,transparent 60%)"></div>{stars}
  <div class="ab dg" style="left:0;right:0;top:150px;text-align:center;font-size:76px;line-height:.86">BIG<br>WIN!</div>
  <img class="ab" src="{COINS}" style="left:75px;top:300px;width:240px;filter:drop-shadow(0 0 20px rgba(255,190,40,.6))">
  <div class="ab pn" style="left:30px;right:30px;top:480px;height:190px;padding:20px 22px">
   <div class="lb" style="text-align:center;font-size:13px;letter-spacing:.1em">YOU WON</div>
   <div class="fx" style="justify-content:center;gap:8px;margin-top:2px"><div class="coin" style="position:relative;left:0;transform:none;width:38px;height:38px">{STAR}</div><span class="dg" style="font-size:46px">487,500</span></div>
   <div class="fx" style="gap:10px;margin-top:16px"><span class="pill B" style="flex:1;height:46px;font-size:16px">▶ x2 WATCH</span><span class="pill G" style="flex:1.2;height:46px;font-size:20px">COLLECT</span></div>
  </div>
 </div>'''
    return f'''<div class="ph"><div class="ab" style="inset:0;background:url({th["bg"]}) center/cover"></div>
 <div class="ab" style="inset:0;background:linear-gradient(180deg,rgba(20,16,120,.85),rgba(20,16,120,.25) 22%,rgba(10,10,80,.2) 55%,rgba(8,8,56,.96) 78%)"></div>
 <div class="ab fx" style="left:14px;right:14px;top:40px;gap:10px">{rb('back', 42)}<div class="fx" style="flex:1;gap:0;min-width:0"><div style="position:relative;width:40px;height:40px;flex:none;z-index:2"><svg viewBox="0 0 24 24" style="position:absolute;inset:0;width:40px;height:40px;fill:url(#lvg);stroke:#7a3a00;stroke-width:1"><defs><linearGradient id="lvg" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#fff3a0"/><stop offset=".6" stop-color="#ffc21a"/><stop offset="1" stop-color="#e07a00"/></linearGradient></defs><path d="M12 1.8l3.1 6.3 6.9.9-5 4.8 1.2 6.9L12 17.4l-6.2 3.3L7 13.8 2 9l6.9-.9z"/></svg><span class="ab t" style="inset:0;display:grid;place-items:center;padding-top:3px;font:400 15px Lilita One;text-shadow:0 1px 0 #7a3a00">6</span></div><div style="flex:1;max-width:70px;height:14px;margin-left:-8px;border-radius:0 7px 7px 0;background:#070840;border:1.5px solid #6ab4ff;overflow:hidden;box-shadow:0 0 6px rgba(90,170,255,.6)"><div style="width:62%;height:100%;background:linear-gradient(180deg,#b4ff8a,#2fc93a)"></div></div></div>{coinbar('12,497,500', 178)}{rb('menu', 42)}</div>
 {jackpots(jv, jps)}
 <div class="ab" style="left:0;right:0;top:192px;text-align:center"><span class="dg" style="font-size:34px">PIRATES</span></div>{'<div class="ab" style="right:18px;top:230px"><div class="rb" style="width:30px;height:30px;background:radial-gradient(circle at 50% 35%,#d79aff,#9a3aff 55%,#5a12c8);border:2px solid #2a0670;box-shadow:inset 0 0 0 1.5px #e0b8ff,0 2px 0 #1a0450,0 0 8px rgba(170,80,255,.6)"><span class="t" style="position:relative;z-index:1;font:400 17px Lilita One">i</span></div></div>' if fv == 'C' else ''}
 <div class="ab" style="left:50%;top:232px;transform:translateX(-50%)"><span class="pill O" style="height:26px;padding:0 14px;font-size:13px;white-space:nowrap">★ HOLD &amp; SPIN ★</span></div>
 <div class="ab pn" style="left:10px;right:10px;top:266px;padding:8px;border-radius:24px;background:linear-gradient(180deg,rgba(16,14,110,.92),rgba(8,8,60,.95))"><div style="position:relative;display:grid;grid-template-columns:repeat(5,1fr);grid-template-rows:repeat(3,74px);gap:3px;background:linear-gradient(90deg,transparent calc(20% - 1px),rgba(106,180,255,.35) 20%,transparent calc(20% + 1px),transparent calc(40% - 1px),rgba(106,180,255,.35) 40%,transparent calc(40% + 1px),transparent calc(60% - 1px),rgba(106,180,255,.35) 60%,transparent calc(60% + 1px),transparent calc(80% - 1px),rgba(106,180,255,.35) 80%,transparent calc(80% + 1px))">{cells}</div></div>
 <div class="ab pn fx" style="left:70px;right:70px;top:532px;height:44px;border-radius:22px;justify-content:center;gap:10px"><span class="lb" style="font-size:13px;letter-spacing:.1em">WIN</span><span class="dg" style="font-size:26px">48,750</span></div>
 {footer(fv)}
 {popup}<div class="sb"><span>9:41</span><span>●●● ▮</span></div><div class="ad">Banner ad space</div></div>'''


FONTS = '<link href="https://fonts.googleapis.com/css2?family=Lilita+One&family=Nunito:wght@700;800;900&display=swap" rel="stylesheet">'
open(OUT + '/neonblue.html', 'w').write(f'<!doctype html><html><head><meta charset="utf-8">{FONTS}<style>{CSS}</style></head><body><p class="cap">BingoFlash-style slots UI</p><p class="sub">Deep-space navy, glowing blue panel rims, glossy pills and balls, gold display type, theme cards like the BingoFlash theme menu.</p><div class="row">{lobby()}{game()}{game(True)}</div></body></html>')
open(OUT + '/footer.html', 'w').write(f'<!doctype html><html><head><meta charset="utf-8">{FONTS}<style>{CSS}</style></head><body><p class="cap">Jackpot display: final design</p><p class="sub">GRAND in a light-bulb marquee with ticking counter digits; MAJOR, MINOR, MINI in coloured plaques below.</p><div class="row">{game(fv="C",jv="D")}</div></body></html>')
print('ok')
