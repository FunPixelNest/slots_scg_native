"""
Google Play store art: app icons (512x512) and screenshots for phone (1080x1920) and tablet (1600x2560).

  python3 tools/store/store.py <shots_dir> <out_dir>

<shots_dir> holds full-size renders of the app screens (NN.png, 1170x2532, from the screen
mockups in tools/prototype/screens). Writes <out_dir>/*.html; render them with render.js.
"""
import base64, os, sys

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", ".."))
FONT = base64.b64encode(open(os.path.join(ROOT, "app/src/main/res/font/lilitaone_regular.ttf"), "rb").read()).decode()
SYM = os.path.join(ROOT, "themes/theme_{t}/src/main/assets/themes/{t}/symbols_v2/{k}.webp")

# (shot index, headline, subline, floating symbols)
SHOTS = [
    ("01", "23 AMAZING<br>SLOT MACHINES", "Pirates, Egypt, Vikings, Candy &amp; more", [("pirates", "wild"), ("candy", "scatter"), ("viking", "blondeman")]),
    ("02", "WIN HUGE<br>JACKPOTS", "Grand, Major, Minor &amp; Mini", [("gems", "chestgold"), ("pirates", "bag"), ("gems", "gemr")]),
    ("09", "HOLD &amp; SPIN<br>BONUS", "Lock the coins, fill the board!", [("gold_mine", "gold"), ("china", "sycee"), ("most_wanted", "bag")]),
    ("12", "FREE SPINS<br>UP TO x10", "Multipliers grow with every win", [("christmas_eve", "wild"), ("christmas_eve", "bell"), ("christmas_eve", "sock")]),
    ("10", "SPIN THE<br>BONUS WHEEL", "Coins and jackpots on every spin", [("modern", "wild"), ("modern", "scatter"), ("neon", "wild")]),
    ("04", "CASCADING<br>WINS", "Symbols burst, wins keep coming", [("neon", "heart"), ("candy", "pink"), ("tropical", "cherry")]),
    ("15", "DAILY BONUS<br>&amp; FREE COINS", "Come back every day for more", [("egypt", "wild"), ("farm", "wild"), ("witch", "scatter")]),
    ("17", "BIG WINS<br>EVERY DAY", "Mega and Huge wins await", [("arabian", "wild"), ("wolf", "wild"), ("underwater", "scatter")]),
]

BASE_CSS = f"""
@font-face{{font-family:"Lilita One";src:url(data:font/ttf;base64,{FONT})}}
*{{box-sizing:border-box}}html,body{{margin:0}}
.c{{position:relative;overflow:hidden;font-family:"Lilita One",sans-serif;color:#fff;
  background:radial-gradient(ellipse 70% 32% at 50% 14%,rgba(160,100,255,.65),transparent 70%),
  radial-gradient(ellipse 90% 30% at 50% 100%,rgba(60,120,255,.5),transparent 70%),
  linear-gradient(180deg,#2a1aa8,#14107a 40%,#0a0a52 75%,#070838)}}
.rays{{position:absolute;left:50%;transform:translateX(-50%);border-radius:50%;
  background:repeating-conic-gradient(from 0deg,rgba(200,150,255,.20) 0 5deg,transparent 5deg 13deg);
  -webkit-mask:radial-gradient(circle,#000 10%,transparent 62%)}}
.stars{{position:absolute;inset:0;background:
  radial-gradient(circle,#fff 0 1.6px,transparent 2.4px) 0 0/71px 79px,
  radial-gradient(circle,rgba(255,220,120,.95) 0 2px,transparent 3px) 23px 41px/127px 113px,
  radial-gradient(circle,rgba(170,200,255,.8) 0 1.4px,transparent 2.2px) 47px 13px/97px 151px;opacity:.7}}
.h{{position:absolute;left:0;right:0;text-align:center;line-height:.92}}
.h .t{{position:relative;display:inline-block}}
.h .t span{{display:block}}
.h .ink{{position:absolute;inset:0;color:#3a0a8a;-webkit-text-stroke:var(--sw) #3a0a8a;paint-order:stroke fill;transform:translateY(var(--dy))}}
.h .gold{{position:relative;background:linear-gradient(180deg,#fffbd0,#ffe23a 40%,#ffa800 70%,#ff7a00);-webkit-background-clip:text;background-clip:text;color:transparent;
  filter:drop-shadow(0 0 18px rgba(255,180,40,.45))}}
.sub{{position:absolute;left:0;right:0;text-align:center;color:#fff;text-shadow:0 3px 0 rgba(10,10,60,.8)}}
.sub b{{display:inline-block;padding:.18em .9em;border-radius:999px;font-weight:400;
  background:linear-gradient(180deg,#ff8af0,#e03ad0 50%,#a012b0);border:4px solid #3a0a5a;box-shadow:0 6px 0 #24063e,0 0 26px rgba(255,90,230,.6)}}
.dev{{position:absolute;left:50%;transform:translateX(-50%)}}
.dev .bez{{position:absolute;inset:0;border-radius:var(--r);background:linear-gradient(160deg,#4a4a6a,#141428 40%,#2a2a44);
  box-shadow:0 0 0 4px #6ab4ff,0 0 60px rgba(90,170,255,.8),0 40px 80px rgba(0,0,30,.7)}}
.dev img.scr{{position:absolute;border-radius:var(--ri);object-fit:cover;object-position:top}}
.fl{{position:absolute;filter:drop-shadow(0 18px 18px rgba(0,0,40,.6))}}
.sp{{position:absolute;width:var(--s);height:var(--s);background:#fff;clip-path:polygon(50% 0,60% 40%,100% 50%,60% 60%,50% 100%,40% 60%,0 50%,40% 40%);
  filter:drop-shadow(0 0 10px #ffd84a)}}
.legal{{position:absolute;left:0;right:0;bottom:0!important;padding:.55em 0;background:linear-gradient(transparent,rgba(4,4,30,.92) 35%);text-align:center;font-family:Arial,sans-serif;font-weight:700;color:rgba(220,230,255,.75);letter-spacing:.04em}}
"""


def headline(text, top, size):
    lines = text.split("<br>")
    inner = "".join(f"<span>{l}</span>" for l in lines)
    return (f'<div class="h" style="top:{top}px;font-size:{size}px;--sw:{size * .2:.0f}px;--dy:{size * .07:.0f}px">'
            f'<div class="t"><div class="ink">{inner}</div><div class="gold">{inner}</div></div></div>')


def sparkles(w, h, n, seed):
    out, x = [], seed
    for _ in range(n):
        x = (x * 1103515245 + 12345) % 2 ** 31
        px, py, s = x % w, (x // w) % int(h * .55), 18 + x % 34
        out.append(f'<i class="sp" style="left:{px}px;top:{py}px;--s:{s}px"></i>')
    return "".join(out)


def floaters(syms, w, h, size, dev_top, dev_w):
    # left, right and lower-left around the device
    cx = w / 2
    # mostly outside the device so they frame it without hiding the game
    spots = [(cx - dev_w / 2 - size * .82, dev_top + size * .1, -12), (cx + dev_w / 2 - size * .18, dev_top + size * 1.5, 10),
             (cx - dev_w / 2 - size * .8, dev_top + size * 3.1, 8)]
    return "".join(
        f'<img class="fl" src="file://{SYM.format(t=t, k=k)}" style="left:{x:.0f}px;top:{y:.0f}px;width:{size}px;transform:rotate({r}deg)">'
        for (t, k), (x, y, r) in zip(syms, spots))


def shot(idx, title, sub, syms, shots_dir, w, h, n):
    tablet = w > 1200
    head_top, head_size = (110, 150) if tablet else (70, 118)
    sub_top, sub_size = (head_top + head_size * 2 + 40, 50) if tablet else (head_top + head_size * 2 + 22, 40)
    dev_top = sub_top + sub_size * 2.4
    dev_h = h - dev_top + (90 if not tablet else 120)  # runs off the bottom edge
    scr_h = dev_h - 2 * (22 if not tablet else 28)
    scr_w = scr_h * 1170 / 2532
    pad = 22 if not tablet else 28
    dev_w = scr_w + 2 * pad
    r, ri = (90, 70) if not tablet else (110, 84)
    float_size = 220 if not tablet else 380
    body = (f'<div class="rays" style="top:{-w * .35:.0f}px;width:{w * 1.8:.0f}px;height:{w * 1.8:.0f}px"></div><div class="stars"></div>'
            f'{sparkles(w, h, 14, n * 7 + 3)}{headline(title, head_top, head_size)}'
            f'<div class="sub" style="top:{sub_top}px;font-size:{sub_size}px"><b>{sub}</b></div>'
            f'<div class="dev" style="top:{dev_top:.0f}px;width:{dev_w:.0f}px;height:{dev_h:.0f}px;--r:{r}px;--ri:{ri}px">'
            f'<div class="bez"></div><img class="scr" src="file://{shots_dir}/{idx}.png" style="left:{pad}px;top:{pad}px;width:{scr_w:.0f}px;height:{scr_h:.0f}px"></div>'
            f'{floaters(syms, w, h, float_size, dev_top, dev_w)}'
            f'<div class="legal" style="bottom:{14 if not tablet else 20}px;font-size:{20 if not tablet else 26}px;text-shadow:0 2px 6px #000">For entertainment only · No real money gambling</div>')
    return f'<!doctype html><html><head><meta charset="utf-8"><style>{BASE_CSS}</style></head><body><div class="c" style="width:{w}px;height:{h}px">{body}</div></body></html>'


# ------------------------------------------------------------------ icons (512x512)
ICON_DEFS = """
<defs>
<radialGradient id="bg" cx=".5" cy=".42" r=".7"><stop offset="0" stop-color="#7a4aff"/><stop offset=".55" stop-color="#2a1aa8"/><stop offset="1" stop-color="#0a0a52"/></radialGradient>
<radialGradient id="bgR" cx=".5" cy=".4" r=".75"><stop offset="0" stop-color="#ff5ab4"/><stop offset=".5" stop-color="#b012b8"/><stop offset="1" stop-color="#3a0670"/></radialGradient>
<linearGradient id="gold" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#FFF7C4"/><stop offset=".35" stop-color="#FFD54A"/><stop offset=".7" stop-color="#E08A00"/><stop offset="1" stop-color="#FFE27A"/></linearGradient>
<linearGradient id="red" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#FF9A8A"/><stop offset=".5" stop-color="#FF2A3A"/><stop offset="1" stop-color="#A00818"/></linearGradient>
<linearGradient id="reel" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#C8CCE0"/><stop offset=".22" stop-color="#FFFFFF"/><stop offset=".78" stop-color="#FFFFFF"/><stop offset="1" stop-color="#C8CCE0"/></linearGradient>
<linearGradient id="green" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#B4FF8A"/><stop offset=".5" stop-color="#2FC93A"/><stop offset="1" stop-color="#0F7A1A"/></linearGradient>
<filter id="drop" x="-30%" y="-30%" width="160%" height="160%"><feDropShadow dx="0" dy="8" stdDeviation="8" flood-color="#000" flood-opacity=".5"/></filter>
<filter id="glow" x="-50%" y="-50%" width="200%" height="200%"><feGaussianBlur stdDeviation="14"/></filter>
</defs>"""


def rays(cx, cy, n=24, color="#ffffff", op=.13, r=420):
    import math
    out = []
    for i in range(n):
        a0, a1 = math.radians(i * 360 / n), math.radians(i * 360 / n + 360 / n * .45)
        out.append(f'<path d="M{cx},{cy} L{cx + r * math.cos(a0):.1f},{cy + r * math.sin(a0):.1f} L{cx + r * math.cos(a1):.1f},{cy + r * math.sin(a1):.1f}Z" fill="{color}" opacity="{op}"/>')
    return "".join(out)


def star(cx, cy, r, fill, stroke, sw, inner=.45):
    import math
    pts = " ".join(f"{cx + (r if i % 2 == 0 else r * inner) * math.cos(math.radians(-90 + i * 36)):.1f},{cy + (r if i % 2 == 0 else r * inner) * math.sin(math.radians(-90 + i * 36)):.1f}" for i in range(10))
    return f'<polygon points="{pts}" fill="{fill}" stroke="{stroke}" stroke-width="{sw}" stroke-linejoin="round"/>'


def seven(x, y, s):
    t = f'x="{x}" y="{y}" font-family="Lilita One" font-size="{s}" text-anchor="middle"'
    return (f'<text {t} fill="#5a0010" stroke="#5a0010" stroke-width="{s * .16:.0f}" stroke-linejoin="round">7</text>'
            f'<text {t} fill="url(#red)">7</text>')


def ribbon(txt, y, w=380, size=72):
    x0 = 256 - w / 2
    t = f'x="256" y="{y + size * .84:.0f}" font-family="Lilita One" font-size="{size}" text-anchor="middle" letter-spacing="2"'
    return (f'<path d="M{x0 - 22},{y + 14} L{x0 + 10},{y + 14} L{x0 + 10},{y + size + 18} L{x0 - 22},{y + size + 18} L{x0 - 6},{y + size / 2 + 16}Z" fill="#a0128a" stroke="#3a0a5a" stroke-width="6" stroke-linejoin="round"/>'
            f'<path d="M{512 - x0 + 22},{y + 14} L{512 - x0 - 10},{y + 14} L{512 - x0 - 10},{y + size + 18} L{512 - x0 + 22},{y + size + 18} L{512 - x0 + 6},{y + size / 2 + 16}Z" fill="#a0128a" stroke="#3a0a5a" stroke-width="6" stroke-linejoin="round"/>'
            f'<rect x="{x0}" y="{y}" width="{w}" height="{size * 1.05:.0f}" rx="16" fill="url(#gold)" stroke="#3a0a00" stroke-width="7" filter="url(#drop)"/>'
            f'<rect x="{x0 + 10}" y="{y + 6}" width="{w - 20}" height="{size * .36:.0f}" rx="10" fill="#fff" opacity=".4"/>'
            f'<text {t} fill="#3a0a00" stroke="#3a0a00" stroke-width="12" stroke-linejoin="round">{txt}</text><text {t} fill="#fff">{txt}</text>')


def icon_777():
    bulbs = "".join(f'<circle cx="{x}" cy="{y}" r="6" fill="#fff8d0"/>' for x in range(78, 440, 28) for y in (118, 358))
    reels = "".join(
        f'<rect x="{90 + i * 114}" y="150" width="104" height="176" rx="14" fill="url(#reel)" stroke="#2a0a3a" stroke-width="5"/>{seven(142 + i * 114, 290, 150)}'
        for i in range(3))
    return (f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512" width="512" height="512">{ICON_DEFS}'
            f'<rect width="512" height="512" fill="url(#bg)"/>{rays(256, 230)}'
            f'<rect x="56" y="96" width="400" height="284" rx="40" fill="#ffb000" filter="url(#glow)" opacity=".7"/>'
            f'<rect x="56" y="96" width="400" height="284" rx="40" fill="url(#gold)" stroke="#3a0a00" stroke-width="8" filter="url(#drop)"/>'
            f'<rect x="78" y="138" width="356" height="200" rx="24" fill="#1a0a4a" stroke="#3a0a00" stroke-width="5"/>{bulbs}{reels}'
            f'<rect x="78" y="226" width="356" height="8" fill="#ff3a4a" opacity=".85"/>'
            f'{ribbon("SLOTS", 398)}'
            f'{star(82, 70, 26, "url(#gold)", "#7a3a00", 4)}{star(440, 76, 20, "url(#gold)", "#7a3a00", 4)}</svg>')


def icon_crown():
    import math
    coins = ""
    for cx, cy, r in [(140, 380, 70), (372, 380, 70), (256, 400, 82), (196, 330, 58), (316, 330, 58)]:
        coins += (f'<circle cx="{cx}" cy="{cy}" r="{r}" fill="#8a4a00"/><circle cx="{cx}" cy="{cy - 4}" r="{r * .94:.0f}" fill="url(#gold)"/>'
                  f'<circle cx="{cx}" cy="{cy - 4}" r="{r * .72:.0f}" fill="none" stroke="#b86b00" stroke-width="{r * .08:.0f}"/>'
                  + star(cx, cy - 4, r * .42, "#ffe680", "#b86b00", 3))
    crown = ('<path d="M120,250 L96,108 L176,176 L256,74 L336,176 L416,108 L392,250Z" fill="url(#gold)" stroke="#3a0a00" stroke-width="9" stroke-linejoin="round" filter="url(#drop)"/>'
             '<rect x="116" y="236" width="280" height="46" rx="12" fill="url(#gold)" stroke="#3a0a00" stroke-width="8"/>'
             '<circle cx="256" cy="259" r="17" fill="#ff2a5a" stroke="#5a0010" stroke-width="5"/><circle cx="180" cy="259" r="12" fill="#2ab4ff" stroke="#0a2a5a" stroke-width="4"/>'
             '<circle cx="332" cy="259" r="12" fill="#2fc93a" stroke="#0a4a10" stroke-width="4"/>'
             '<circle cx="96" cy="104" r="16" fill="#ff2a5a" stroke="#3a0a00" stroke-width="5"/><circle cx="256" cy="70" r="18" fill="#2ab4ff" stroke="#3a0a00" stroke-width="5"/><circle cx="416" cy="104" r="16" fill="#ff2a5a" stroke="#3a0a00" stroke-width="5"/>'
             '<path d="M150,150 L170,230 M256,110 L256,226" stroke="#fff" stroke-width="10" stroke-linecap="round" opacity=".45"/>')
    return (f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512" width="512" height="512">{ICON_DEFS}'
            f'<rect width="512" height="512" fill="url(#bgR)"/>{rays(256, 200, op=.16)}'
            f'<circle cx="256" cy="200" r="170" fill="#ffd24a" filter="url(#glow)" opacity=".45"/>{crown}{coins}'
            f'{star(72, 60, 22, "#fff", "none", 0)}{star(450, 300, 16, "#fff", "none", 0)}</svg>')


def icon_wild():
    # our Pirates wild emblem on the arcade-night burst, with the SLOTS ribbon
    sym = base64.b64encode(open(SYM.format(t="pirates", k="wild"), "rb").read()).decode()
    return (f'<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" viewBox="0 0 512 512" width="512" height="512">{ICON_DEFS}'
            f'<rect width="512" height="512" fill="url(#bg)"/>{rays(256, 220, op=.15)}'
            f'<circle cx="256" cy="220" r="190" fill="#6ab4ff" filter="url(#glow)" opacity=".45"/>'
            f'<image href="data:image/webp;base64,{sym}" x="56" y="24" width="400" height="400" filter="url(#drop)"/>'
            f'{star(70, 72, 24, "url(#gold)", "#7a3a00", 4)}{star(446, 96, 18, "url(#gold)", "#7a3a00", 4)}{star(430, 420, 14, "#fff", "none", 0)}</svg>')


ICONS = {"icon_777": icon_777, "icon_crown": icon_crown, "icon_wild": icon_wild}


def main(shots_dir, out):
    os.makedirs(out, exist_ok=True)
    for n, (idx, title, sub, syms) in enumerate(SHOTS, 1):
        for name, (w, h) in {"phone": (1080, 1920), "tablet": (1600, 2560)}.items():
            open(os.path.join(out, f"{name}_{n:02d}.html"), "w").write(shot(idx, title, sub, syms, shots_dir, w, h, n))
    for name, fn in ICONS.items():
        open(os.path.join(out, f"{name}.html"), "w").write(
            f'<!doctype html><html><head><meta charset="utf-8"><style>@font-face{{font-family:"Lilita One";src:url(data:font/ttf;base64,{FONT})}}html,body{{margin:0}}svg{{display:block}}</style></head><body>{fn()}</body></html>')
    print("wrote", len(SHOTS) * 2, "screenshots and", len(ICONS), "icons to", out)


if __name__ == "__main__":
    main(os.path.abspath(sys.argv[1]), os.path.abspath(sys.argv[2]))
