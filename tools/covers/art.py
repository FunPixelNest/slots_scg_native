"""Original vector illustrations for the lobby icons (no game art). One SVG scene per theme, 600x504."""
import math

W, H = 600, 504

DEFS = """
<filter id="glow" x="-50%" y="-50%" width="200%" height="200%"><feGaussianBlur stdDeviation="10"/></filter>
<filter id="soft" x="-50%" y="-50%" width="200%" height="200%"><feGaussianBlur stdDeviation="4"/></filter>
<filter id="shadow" x="-30%" y="-30%" width="160%" height="160%"><feDropShadow dx="0" dy="10" stdDeviation="10" flood-color="#000" flood-opacity=".45"/></filter>
<linearGradient id="gold" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#FFF4B8"/><stop offset=".45" stop-color="#FFD24A"/><stop offset="1" stop-color="#C77A0A"/></linearGradient>
<linearGradient id="goldH" x1="0" y1="0" x2="1" y2="0"><stop offset="0" stop-color="#C77A0A"/><stop offset=".5" stop-color="#FFE58A"/><stop offset="1" stop-color="#C77A0A"/></linearGradient>
<radialGradient id="shine" cx=".35" cy=".3" r=".7"><stop offset="0" stop-color="#fff" stop-opacity=".9"/><stop offset=".25" stop-color="#fff" stop-opacity=".25"/><stop offset="1" stop-color="#fff" stop-opacity="0"/></radialGradient>
"""


def lg(id_, c1, c2, vertical=True):
    x2, y2 = ("0", "1") if vertical else ("1", "0")
    return f'<linearGradient id="{id_}" x1="0" y1="0" x2="{x2}" y2="{y2}"><stop offset="0" stop-color="{c1}"/><stop offset="1" stop-color="{c2}"/></linearGradient>'


def rg(id_, c1, c2, cx=".5", cy=".5", r=".5"):
    return f'<radialGradient id="{id_}" cx="{cx}" cy="{cy}" r="{r}"><stop offset="0" stop-color="{c1}"/><stop offset="1" stop-color="{c2}"/></radialGradient>'


def sky(c1, c2, glow=None, gy=200):
    s = f'<rect width="{W}" height="{H}" fill="url(#sky)"/>'
    d = lg("sky", c1, c2)
    if glow:
        d += rg("halo", glow, glow + "00")
        s += f'<ellipse cx="300" cy="{gy}" rx="300" ry="230" fill="url(#halo)" opacity=".85"/>'
    return d, s


def stars(n=24, seed=3, color="#fff", ymax=300):
    out, x = [], seed
    for i in range(n):
        x = (x * 1103515245 + 12345) % 2 ** 31
        px, py, r = x % W, (x // W) % ymax, 1 + (x % 3) * .7
        out.append(f'<circle cx="{px}" cy="{py}" r="{r:.1f}" fill="{color}" opacity="{.4 + (x % 5) / 8:.2f}"/>')
    return "".join(out)


def sparkle(x, y, s=1.0, c="#fff"):
    return (f'<g transform="translate({x},{y}) scale({s})" opacity=".95"><path d="M0,-16 L3,-3 L16,0 L3,3 L0,16 L-3,3 L-16,0 L-3,-3Z" fill="{c}"/>'
            f'<circle r="3" fill="#fff"/></g>')


def heart(cx, cy, s, fill, extra=""):
    return (f'<path transform="translate({cx},{cy}) scale({s})" d="M0,30 C-40,0 -60,-20 -45,-42 C-32,-60 -8,-55 0,-38 '
            f'C8,-55 32,-60 45,-42 C60,-20 40,0 0,30Z" fill="{fill}" {extra}/>')


def star5(cx, cy, r, fill, extra="", inner=.45):
    pts = []
    for i in range(10):
        a = -math.pi / 2 + i * math.pi / 5
        rr = r if i % 2 == 0 else r * inner
        pts.append(f"{cx + rr * math.cos(a):.1f},{cy + rr * math.sin(a):.1f}")
    return f'<polygon points="{" ".join(pts)}" fill="{fill}" {extra}/>'


def coin(cx, cy, r):
    return (f'<g filter="url(#shadow)"><circle cx="{cx}" cy="{cy}" r="{r}" fill="url(#gold)"/>'
            f'<circle cx="{cx}" cy="{cy}" r="{r * .72}" fill="none" stroke="#B66A06" stroke-width="{r * .1}"/>'
            f'<circle cx="{cx}" cy="{cy}" r="{r}" fill="url(#shine)"/></g>')


# ------------------------------------------------------------------ scenes
def christmas_eve():
    d, s = sky("#0B1E4A", "#2A5DB0", "#7FB2FF", 210)
    d += lg("tree", "#2ECC71", "#0E6B3A") + lg("snow", "#FFFFFF", "#CFE3FF") + lg("giftR", "#FF4D6D", "#B5122F") + lg("giftB", "#4DA3FF", "#1D4FB8")
    s += stars(40)
    s += '<path d="M0,400 Q150,340 300,380 T600,360 L600,504 L0,504Z" fill="url(#snow)"/>'
    tree = ""
    for i, (y, w) in enumerate([(120, 90), (175, 130), (235, 170), (300, 210)]):
        tree += f'<path d="M300,{y - 70} L{300 + w},{y + 30} Q300,{y + 50} {300 - w},{y + 30}Z" fill="url(#tree)"/>'
        tree += f'<path d="M{300 - w * .8},{y + 18} Q300,{y + 45} {300 + w * .8},{y + 18}" stroke="url(#goldH)" stroke-width="5" fill="none"/>'
    s += f'<rect x="282" y="320" width="36" height="50" fill="#6B3E1F"/><g filter="url(#shadow)">{tree}</g>'
    for x, y, c in [(255, 150, "#FF4D6D"), (335, 190, "#FFD24A"), (240, 245, "#4DA3FF"), (350, 260, "#FF4D6D"), (300, 300, "#FFD24A"), (210, 305, "#FF4D6D"), (395, 315, "#4DA3FF"), (300, 215, "#FF4D6D")]:
        s += f'<circle cx="{x}" cy="{y}" r="11" fill="{c}"/><circle cx="{x - 3}" cy="{y - 4}" r="4" fill="#fff" opacity=".7"/>'
    s += f'<g filter="url(#glow)">{star5(300, 48, 40, "#FFE680")}</g>{star5(300, 50, 30, "url(#gold)")}'
    s += ('<g filter="url(#shadow)"><rect x="150" y="330" width="80" height="70" rx="6" fill="url(#giftR)"/><rect x="183" y="330" width="14" height="70" fill="#FFD24A"/>'
          '<rect x="150" y="355" width="80" height="12" fill="#FFD24A"/>'
          '<rect x="380" y="345" width="70" height="56" rx="6" fill="url(#giftB)"/><rect x="408" y="345" width="13" height="56" fill="#fff"/><rect x="380" y="366" width="70" height="11" fill="#fff"/></g>')
    return d, s


def valentines_day():
    d, s = sky("#5B0E4B", "#FF4F9A", "#FFB3D6", 200)
    d += lg("h", "#FF6FA8", "#C8104E") + lg("h2", "#FFB0CF", "#FF4F9A")
    for x, y, sc in [(80, 120, .5), (520, 90, .45), (110, 330, .35), (500, 300, .4), (60, 230, .25), (550, 200, .3)]:
        s += heart(x, y, sc, "url(#h2)", 'opacity=".75"')
    s += f'<g filter="url(#glow)" opacity=".8">{heart(300, 210, 2.6, "#FF8AC0")}</g>'
    s += f'<g filter="url(#shadow)">{heart(300, 210, 2.4, "url(#h)")}</g>'
    s += f'<path transform="translate(300,210) scale(2.4)" d="M-30,-40 C-20,-50 -8,-46 -4,-38" stroke="#fff" stroke-width="5" stroke-linecap="round" fill="none" opacity=".75"/>'
    s += ('<g filter="url(#shadow)"><line x1="90" y1="330" x2="510" y2="110" stroke="url(#goldH)" stroke-width="10" stroke-linecap="round"/>'
          '<polygon points="510,110 470,112 488,140" fill="url(#gold)"/>'
          '<path d="M90,330 l-20,-6 l10,14 l-16,4 l18,8Z" fill="#fff"/></g>')
    s += sparkle(200, 90) + sparkle(420, 330, .8)
    return d, s


def witch():
    d, s = sky("#140A30", "#5A2C9A", "#9B6BFF", 160)
    d += rg("moon", "#FFF6C8", "#F5C451") + lg("pot", "#3B3B4F", "#0E0E16") + rg("brew", "#B6FF7A", "#1BC47D") + lg("hat", "#6B3FD1", "#2A1060")
    s += stars(30)
    s += '<circle cx="430" cy="120" r="85" fill="url(#moon)"/><circle cx="430" cy="120" r="85" fill="url(#shine)" opacity=".4"/>'
    for x, y in [(120, 90), (190, 130), (510, 230)]:
        s += f'<path transform="translate({x},{y})" d="M0,0 q-12,-14 -28,-6 q8,2 10,10 q-6,-4 -12,0 q10,4 14,12 q4,-6 16,-6 q12,0 16,6 q4,-8 14,-12 q-6,-4 -12,0 q2,-8 10,-10 q-16,-8 -28,6z" fill="#120720"/>'
    s += '<path d="M0,430 Q300,380 600,430 L600,504 L0,504Z" fill="#120720"/>'
    s += f'<g filter="url(#glow)"><ellipse cx="300" cy="250" rx="140" ry="30" fill="#7CFF8A" opacity=".8"/></g>'
    s += ('<g filter="url(#shadow)"><path d="M150,250 Q140,420 300,420 Q460,420 450,250Z" fill="url(#pot)"/>'
          '<ellipse cx="300" cy="250" rx="155" ry="34" fill="#2A2A3A"/><ellipse cx="300" cy="252" rx="138" ry="26" fill="url(#brew)"/></g>')
    for x, y, r in [(260, 230, 14), (320, 215, 10), (350, 240, 8), (290, 195, 7), (240, 180, 5)]:
        s += f'<circle cx="{x}" cy="{y}" r="{r}" fill="#B6FF7A" opacity=".85"/><circle cx="{x - r / 3}" cy="{y - r / 3}" r="{r / 3}" fill="#fff" opacity=".7"/>'
    s += ('<g filter="url(#shadow)" transform="rotate(-14 160 200)"><ellipse cx="160" cy="215" rx="85" ry="16" fill="#2A1060"/>'
          '<path d="M110,212 L175,70 Q190,60 196,80 L215,212Z" fill="url(#hat)"/><rect x="117" y="190" width="94" height="16" fill="#F5C451"/></g>')
    return d, s


def modern():
    d, s = sky("#120712", "#5A0F22", "#E5394F", 220)
    d += lg("card", "#FFFFFF", "#E6E6EE") + lg("r", "#FF4D6D", "#B5122F") + lg("k", "#3A3A4A", "#0B0B12")
    cards = [(-24, "♠", "url(#k)"), (0, "♥", "url(#r)"), (24, "♦", "url(#r)")]
    g = ""
    for ang, suit, fill in cards:
        g += (f'<g transform="rotate({ang} 300 420)"><rect x="215" y="90" width="170" height="240" rx="18" fill="url(#card)" stroke="#F5C451" stroke-width="5"/>'
              f'<text x="300" y="250" font-size="130" text-anchor="middle" fill="{fill}" font-family="DejaVu Sans">{suit}</text>'
              f'<text x="236" y="135" font-size="34" fill="{fill}" font-family="DejaVu Sans">{suit}</text></g>')
    s += f'<g filter="url(#shadow)">{g}</g>'
    s += coin(120, 380, 34) + coin(480, 370, 40) + coin(440, 420, 26)
    s += sparkle(140, 110) + sparkle(470, 90, .8)
    return d, s


def forest():
    d, s = sky("#06261E", "#1E7A4A", "#8CFFB0", 220)
    d += lg("cap", "#FF5A5A", "#B3122A") + lg("stem", "#FFF8E8", "#E2CDA8") + lg("grass", "#3BB36A", "#145A33")
    for x in (40, 120, 480, 560):
        s += f'<path d="M{x},380 L{x - 50},380 L{x},150 L{x + 50},380Z" fill="#0B3A28" opacity=".8"/>'
    s += '<path d="M0,380 Q300,340 600,380 L600,504 L0,504Z" fill="url(#grass)"/>'
    def shroom(cx, base, sc):
        return (f'<g transform="translate({cx},{base}) scale({sc})" filter="url(#shadow)"><path d="M-30,0 Q-36,-90 -22,-120 L22,-120 Q36,-90 30,0Z" fill="url(#stem)"/>'
                f'<path d="M-120,-110 Q-110,-230 0,-235 Q110,-230 120,-110 Q0,-80 -120,-110Z" fill="url(#cap)"/>'
                f'<circle cx="-55" cy="-170" r="16" fill="#fff"/><circle cx="10" cy="-200" r="13" fill="#fff"/><circle cx="60" cy="-150" r="18" fill="#fff"/>'
                f'<circle cx="-15" cy="-140" r="10" fill="#fff"/><circle cx="85" cy="-190" r="8" fill="#fff"/>'
                f'<path d="M-90,-170 Q-60,-215 -10,-222" stroke="#fff" stroke-width="8" fill="none" opacity=".35" stroke-linecap="round"/></g>')
    s += shroom(300, 395, 1.15) + shroom(130, 400, .55) + shroom(470, 405, .45)
    for x, y in [(90, 160), (200, 90), (420, 130), (520, 200), (380, 60), (160, 260)]:
        s += f'<circle cx="{x}" cy="{y}" r="9" fill="#EFFF8A" filter="url(#soft)"/><circle cx="{x}" cy="{y}" r="3" fill="#fff"/>'
    return d, s


def neon():
    d, s = sky("#05061A", "#1B0B3A")
    s += '<g stroke="#38D6FF" stroke-width="1" opacity=".25">' + "".join(f'<line x1="{x}" y1="330" x2="{300 + (x - 300) * 3}" y2="504"/>' for x in range(0, 601, 50)) + \
         "".join(f'<line x1="0" y1="{y}" x2="600" y2="{y}"/>' for y in (340, 360, 390, 430, 480)) + '</g>'
    h = heart(300, 215, 2.2, "none", 'stroke="{c}" stroke-width="{w}"')
    s += f'<g filter="url(#glow)">{h.format(c="#FF3D8B", w=14)}</g>{h.format(c="#FF8FC0", w=7)}{h.format(c="#fff", w=2)}'
    s += f'<g filter="url(#glow)">{star5(120, 110, 40, "none", "stroke=\"#38D6FF\" stroke-width=\"10\"")}</g>{star5(120, 110, 40, "none", "stroke=\"#C8F4FF\" stroke-width=\"4\"")}'
    s += (f'<g filter="url(#glow)"><text x="485" y="215" font-size="130" font-weight="900" fill="none" stroke="#FFD24A" stroke-width="8" text-anchor="middle" font-family="DejaVu Sans">7</text></g>'
          f'<text x="485" y="215" font-size="130" font-weight="900" fill="none" stroke="#FFF2B0" stroke-width="3" text-anchor="middle" font-family="DejaVu Sans">7</text>')
    return d, s


def underwater():
    d, s = sky("#0A6E8A", "#062A4A", "#7FF0FF", 120)
    d += lg("fish", "#FFB341", "#FF5A1F") + lg("fish2", "#7FE7FF", "#2E8BE6") + lg("weed", "#3BD68A", "#0E6B4A")
    s += '<g opacity=".18" fill="#fff">' + "".join(f'<polygon points="{x},0 {x + 40},0 {x + 140},504 {x + 60},504"/>' for x in (60, 220, 380)) + '</g>'
    s += '<path d="M0,430 Q150,400 300,430 T600,420 L600,504 L0,504Z" fill="#E8C98A"/>'
    for x, h in [(60, 160), (100, 110), (520, 170), (560, 120)]:
        s += f'<path d="M{x},440 Q{x - 30},{440 - h / 2} {x},{440 - h} Q{x + 30},{440 - h / 2} {x + 8},440Z" fill="url(#weed)"/>'
    def fish(cx, cy, sc, fill, flip=False):
        f = -1 if flip else 1
        return (f'<g transform="translate({cx},{cy}) scale({sc * f},{sc})" filter="url(#shadow)"><path d="M-90,0 Q-20,-80 70,0 Q-20,80 -90,0Z" fill="{fill}"/>'
                f'<path d="M60,0 L120,-50 Q105,0 120,50Z" fill="{fill}"/><path d="M-30,-55 L-5,0 L-30,55" stroke="#fff" stroke-width="10" fill="none" opacity=".8"/>'
                f'<circle cx="-55" cy="-10" r="12" fill="#fff"/><circle cx="-58" cy="-10" r="6" fill="#111"/></g>')
    s += fish(300, 230, 1.5, "url(#fish)") + fish(470, 120, .6, "url(#fish2)", True) + fish(130, 330, .55, "url(#fish2)")
    for x, y, r in [(220, 120, 10), (240, 80, 6), (380, 330, 12), (400, 290, 7), (90, 200, 8), (520, 330, 9)]:
        s += f'<circle cx="{x}" cy="{y}" r="{r}" fill="none" stroke="#DFFBFF" stroke-width="2.5" opacity=".8"/><circle cx="{x - r / 3}" cy="{y - r / 3}" r="{r / 4}" fill="#fff"/>'
    return d, s


def wolf():
    d, s = sky("#030A1E", "#1D3D7A")
    d += rg("moon", "#FFFFFF", "#BFD7FF")
    s += stars(50)
    s += '<g filter="url(#glow)"><circle cx="300" cy="190" r="150" fill="#9EC3FF" opacity=".6"/></g><circle cx="300" cy="190" r="140" fill="url(#moon)"/>'
    s += '<circle cx="250" cy="150" r="22" fill="#C9DBFA" opacity=".6"/><circle cx="350" cy="230" r="30" fill="#C9DBFA" opacity=".5"/><circle cx="330" cy="130" r="12" fill="#C9DBFA" opacity=".6"/>'
    s += '<path d="M150,504 L150,380 Q230,300 330,330 L460,400 L460,504Z" fill="#071226"/>'
    # howling wolf silhouette: sitting, side view, snout raised to the moon
    s += ('<path fill="#071226" transform="translate(-40,-20) scale(1.15)" d="M230,352 C200,356 170,346 178,328 C186,314 212,326 232,330 '
          'C236,300 246,262 262,232 C266,214 268,200 272,188 L266,160 L284,178 L292,160 L298,180 C312,170 330,150 346,126 '
          'L356,124 L354,136 C346,156 332,178 320,196 C318,214 322,232 330,256 C336,280 338,320 340,346 L352,350 L352,356 Z"/>')
    for x in (40, 95, 520, 570):
        s += f'<path d="M{x},460 L{x - 40},460 L{x},250 L{x + 40},460Z" fill="#06101F"/>'
    s += '<path d="M0,460 L600,460 L600,504 L0,504Z" fill="#06101F"/>'
    return d, s


def candy():
    d, s = sky("#FF8FC8", "#FFD36E", "#FFFFFF", 200)
    d += lg("stick", "#FFFFFF", "#E8E0F0")
    s += '<rect x="290" y="250" width="20" height="230" rx="8" fill="url(#stick)" filter="url(#shadow)"/>'
    sw = ""
    cols = ["#FF3D8B", "#FFFFFF", "#38D6FF", "#FFFFFF", "#FFD24A", "#FFFFFF"]
    for i, r in enumerate(range(140, 10, -22)):
        sw += f'<circle cx="300" cy="200" r="{r}" fill="{cols[i % len(cols)]}"/>'
    s += f'<g filter="url(#shadow)">{sw}</g><circle cx="300" cy="200" r="140" fill="url(#shine)" opacity=".6"/>'
    s += f'<path d="M300,200 m0,-120 a120,120 0 0,1 0,240" stroke="#fff" stroke-width="4" fill="none" opacity=".5"/>'
    def wrapped(cx, cy, c, rot):
        return (f'<g transform="translate({cx},{cy}) rotate({rot})" filter="url(#shadow)"><path d="M-38,0 L-70,-26 L-66,26Z" fill="{c}"/><path d="M38,0 L70,-26 L66,26Z" fill="{c}"/>'
                f'<ellipse rx="44" ry="30" fill="{c}"/><ellipse rx="44" ry="30" fill="url(#shine)"/></g>')
    s += wrapped(100, 380, "#7B3FE4", -20) + wrapped(500, 370, "#22C55E", 18)
    s += ('<g filter="url(#shadow)"><path d="M470,440 L470,250 Q470,200 520,200 Q570,200 570,250" stroke="#E5394F" stroke-width="22" fill="none" stroke-linecap="round"/>'
          '<path d="M470,440 L470,250 Q470,200 520,200 Q570,200 570,250" stroke="#fff" stroke-width="22" stroke-dasharray="18 18" fill="none"/></g>')
    return d, s


def pirates():
    d, s = sky("#2A0F3A", "#FF8A3D", "#FFD27A", 300)
    d += lg("sea", "#1E5BA8", "#0A2348") + lg("wood", "#9A5B2E", "#5A2F12")
    s += '<circle cx="300" cy="330" r="120" fill="#FFE08A" opacity=".9"/>'
    s += '<rect y="330" width="600" height="174" fill="url(#sea)"/>'
    s += '<g stroke="#9CC8FF" stroke-width="3" opacity=".5">' + "".join(f'<line x1="{x}" y1="{y}" x2="{x + 60}" y2="{y}"/>' for x, y in [(60, 360), (420, 370), (200, 390), (480, 400)]) + '</g>'
    # crossed swords
    for a in (-35, 35):
        s += (f'<g transform="rotate({a} 300 250)" filter="url(#shadow)"><rect x="294" y="60" width="12" height="240" rx="5" fill="#DDE6F0"/>'
              f'<rect x="270" y="290" width="60" height="12" rx="5" fill="url(#gold)"/><rect x="295" y="300" width="10" height="40" fill="#5A2F12"/></g>')
    # chest
    s += ('<g filter="url(#shadow)"><path d="M180,300 L420,300 L420,440 L180,440Z" fill="url(#wood)"/>'
          '<path d="M180,300 Q300,200 420,300Z" fill="url(#wood)"/>'
          '<rect x="180" y="300" width="240" height="18" fill="url(#gold)"/><rect x="180" y="420" width="240" height="14" fill="url(#gold)"/>'
          '<rect x="200" y="300" width="16" height="140" fill="url(#gold)"/><rect x="384" y="300" width="16" height="140" fill="url(#gold)"/>'
          '<rect x="282" y="330" width="36" height="44" rx="6" fill="url(#gold)"/><circle cx="300" cy="350" r="6" fill="#5A2F12"/></g>')
    s += coin(220, 300, 26) + coin(270, 280, 30) + coin(330, 278, 28) + coin(385, 296, 24) + coin(300, 262, 22)
    s += sparkle(150, 200) + sparkle(460, 230, .8)
    return d, s


def tropical():
    d, s = sky("#16C1E8", "#FFE29A", "#FFFFFF", 120)
    d += lg("sea", "#2EC4D6", "#0B7FA8") + lg("sand", "#FFE6A8", "#E8B866") + lg("drink", "#FF8A3D", "#FF3D8B")
    s += '<g filter="url(#glow)"><circle cx="460" cy="110" r="70" fill="#FFF5A8"/></g><circle cx="460" cy="110" r="55" fill="#FFE45A"/>'
    s += '<rect y="300" width="600" height="80" fill="url(#sea)"/><path d="M0,360 Q300,320 600,360 L600,504 L0,504Z" fill="url(#sand)"/>'
    def palm(x, base, lean):
        g = f'<path d="M{x},{base} Q{x + lean / 2},{base - 120} {x + lean},{base - 230}" stroke="#8A5A2B" stroke-width="18" fill="none" stroke-linecap="round"/>'
        tx, ty = x + lean, base - 230
        for a in (-160, -120, -60, -20, 20, -90):
            r = math.radians(a)
            ex, ey = tx + 110 * math.cos(r), ty + 110 * math.sin(r) + 40
            g += f'<path d="M{tx},{ty} Q{(tx + ex) / 2},{min(ty, ey) - 40} {ex:.0f},{ey:.0f} Q{(tx + ex) / 2},{min(ty, ey) - 10} {tx},{ty}Z" fill="#1E9E55"/>'
        return f'<g filter="url(#shadow)">{g}</g>'
    s += palm(110, 420, 40) + palm(500, 430, -50)
    s += ('<g filter="url(#shadow)"><path d="M230,190 L370,190 L310,300 L290,300Z" fill="url(#drink)"/>'
          '<path d="M230,190 L370,190" stroke="#fff" stroke-width="6"/><rect x="295" y="300" width="10" height="90" fill="#E0F7FF"/>'
          '<ellipse cx="300" cy="395" rx="50" ry="10" fill="#E0F7FF"/>'
          '<path d="M330,190 L370,90" stroke="#fff" stroke-width="5"/><path d="M330,110 Q370,60 410,110Z" fill="#FF3D8B"/>'
          '<circle cx="250" cy="185" r="22" fill="#FFD24A"/><circle cx="250" cy="185" r="14" fill="#FF8A3D"/></g>')
    return d, s


def gold_mine():
    d, s = sky("#140A04", "#4A2A12", "#FFB341", 250)
    d += lg("cart", "#7A8899", "#3A4452") + rg("lamp", "#FFF3B0", "#FF9F2E")
    s += '<path d="M0,0 L600,0 L600,90 Q300,40 0,90Z" fill="#0B0502"/><path d="M60,504 L60,90 L100,90 L100,504Z M500,504 L500,90 L540,90 L540,504Z M40,80 L560,80 L560,110 L40,110Z" fill="#5A3416"/>'
    s += '<path d="M0,430 L600,430 L600,504 L0,504Z" fill="#2A1608"/><g stroke="#888" stroke-width="6"><line x1="0" y1="440" x2="600" y2="440"/><line x1="0" y1="470" x2="600" y2="470"/></g>'
    for x, y, r in [(220, 210, 46), (300, 190, 56), (380, 212, 44), (260, 170, 34), (340, 160, 30)]:
        s += (f'<g filter="url(#shadow)"><polygon points="{x - r},{y + 10} {x - r / 2},{y - r * .8} {x + r / 2},{y - r} {x + r},{y} {x + r / 3},{y + r * .7} {x - r / 2},{y + r * .6}" fill="url(#gold)"/>'
              f'<polygon points="{x - r / 2},{y - r * .8} {x + r / 2},{y - r} {x},{y - r / 4}" fill="#FFF6C8" opacity=".7"/></g>')
    s += ('<g filter="url(#shadow)"><path d="M150,230 L450,230 L420,390 L180,390Z" fill="url(#cart)"/>'
          '<rect x="140" y="222" width="320" height="20" rx="6" fill="#9AA8B8"/><g fill="#C0C9D4"><circle cx="175" cy="250" r="5"/><circle cx="425" cy="250" r="5"/></g>'
          '<circle cx="225" cy="410" r="32" fill="#2A2F38"/><circle cx="225" cy="410" r="14" fill="#8A97A8"/>'
          '<circle cx="375" cy="410" r="32" fill="#2A2F38"/><circle cx="375" cy="410" r="14" fill="#8A97A8"/></g>')
    s += ('<g filter="url(#shadow)" transform="rotate(-30 500 260)"><rect x="494" y="160" width="12" height="200" fill="#8A5A2B"/>'
          '<path d="M430,170 Q500,130 570,170 L560,180 Q500,150 440,180Z" fill="#9AA8B8"/></g>')
    s += '<g filter="url(#glow)"><circle cx="110" cy="200" r="40" fill="#FFB341"/></g><rect x="95" y="160" width="30" height="12" fill="#333"/><rect x="92" y="172" width="36" height="50" rx="8" fill="url(#lamp)" stroke="#333" stroke-width="5"/>'
    return d, s


def farm():
    d, s = sky("#4FB6FF", "#CDEFFF", "#FFFFFF", 120)
    d += lg("barn", "#E5394F", "#9E1B2A") + lg("hill", "#7DDB5A", "#2E9E44") + lg("roof", "#6B2F1A", "#3A170A")
    s += '<circle cx="500" cy="80" r="46" fill="#FFE45A"/>'
    for x, y in [(110, 80), (240, 60)]:
        s += f'<g fill="#fff" opacity=".95"><circle cx="{x}" cy="{y}" r="26"/><circle cx="{x + 30}" cy="{y - 10}" r="32"/><circle cx="{x + 62}" cy="{y}" r="24"/><rect x="{x}" y="{y}" width="62" height="24"/></g>'
    s += '<path d="M0,330 Q150,260 320,320 T600,300 L600,504 L0,504Z" fill="url(#hill)"/>'
    s += ('<g filter="url(#shadow)"><path d="M190,200 L300,120 L410,200 L410,400 L190,400Z" fill="url(#barn)"/>'
          '<path d="M175,205 L300,110 L425,205" stroke="url(#roof)" stroke-width="22" fill="none" stroke-linejoin="round"/>'
          '<rect x="250" y="290" width="100" height="110" fill="#fff"/><rect x="258" y="298" width="84" height="102" fill="#B5122F"/>'
          '<path d="M258,298 L342,400 M342,298 L258,400" stroke="#fff" stroke-width="8"/><rect x="275" y="190" width="50" height="44" fill="#fff"/><rect x="281" y="196" width="38" height="32" fill="#6B2F1A"/></g>')
    s += '<g stroke="#fff" stroke-width="8">' + "".join(f'<line x1="{x}" y1="400" x2="{x}" y2="460"/>' for x in range(20, 600, 60)) + '<line x1="0" y1="420" x2="600" y2="420"/><line x1="0" y1="445" x2="600" y2="445"/></g>'
    return d, s


def most_wanted():
    d, s = sky("#3A1E0E", "#C77A3A", "#FFD08A", 200)
    d += lg("paper", "#F7E3B5", "#D9B57A")
    s += '<g opacity=".25" stroke="#2A1206" stroke-width="2">' + "".join(f'<line x1="0" y1="{y}" x2="600" y2="{y}"/>' for y in range(20, 504, 36)) + '</g>'
    s += ('<g filter="url(#shadow)" transform="rotate(-4 300 240)"><rect x="170" y="40" width="260" height="360" fill="url(#paper)"/>'
          '<rect x="170" y="40" width="260" height="360" fill="none" stroke="#8A5A2B" stroke-width="4"/>'
          '<text x="300" y="100" font-size="52" font-weight="900" text-anchor="middle" fill="#3A1E0E" font-family="DejaVu Serif">WANTED</text>'
          '<rect x="215" y="118" width="170" height="150" fill="#E8C98A" stroke="#3A1E0E" stroke-width="3"/>'
          '<path d="M240,268 Q240,215 300,210 Q360,215 360,268Z" fill="#3A1E0E"/><circle cx="300" cy="185" r="34" fill="#3A1E0E"/>'
          '<path d="M240,165 L360,165 L345,150 Q300,120 255,150Z" fill="#3A1E0E"/><rect x="225" y="160" width="150" height="10" rx="4" fill="#3A1E0E"/>'
          '<text x="300" y="315" font-size="22" font-weight="700" text-anchor="middle" fill="#3A1E0E" font-family="DejaVu Serif">DEAD OR ALIVE</text>'
          '<text x="300" y="370" font-size="42" font-weight="900" text-anchor="middle" fill="#8A1E0E" font-family="DejaVu Serif">$ 10,000</text>'
          '<circle cx="190" cy="58" r="6" fill="#555"/><circle cx="410" cy="58" r="6" fill="#555"/></g>')
    s += ('<g filter="url(#shadow)" transform="translate(460,330) rotate(-20)"><rect x="-10" y="-14" width="110" height="26" rx="6" fill="#9AA8B8"/>'
          '<circle cx="10" cy="0" r="24" fill="#7A8899"/><path d="M-10,0 L-60,50 L-30,62 L0,20Z" fill="#6B3E1F"/></g>')
    s += coin(110, 400, 34) + coin(150, 430, 26)
    return d, s


def gems():
    d, s = sky("#16052E", "#5A1E9A", "#C08BFF", 210)
    def gem(cx, cy, r, c1, c2, c3):
        top, mid = cy - r * .55, cy - r * .15
        return (f'<g filter="url(#shadow)"><polygon points="{cx - r * .6},{top} {cx + r * .6},{top} {cx + r},{mid} {cx},{cy + r} {cx - r},{mid}" fill="{c2}"/>'
                f'<polygon points="{cx - r * .6},{top} {cx + r * .6},{top} {cx + r * .35},{mid} {cx - r * .35},{mid}" fill="{c1}"/>'
                f'<polygon points="{cx - r * .6},{top} {cx - r * .35},{mid} {cx - r},{mid}" fill="{c1}" opacity=".8"/>'
                f'<polygon points="{cx + r * .6},{top} {cx + r * .35},{mid} {cx + r},{mid}" fill="{c3}"/>'
                f'<polygon points="{cx - r * .35},{mid} {cx + r * .35},{mid} {cx},{cy + r}" fill="{c1}" opacity=".55"/>'
                f'<polygon points="{cx + r * .35},{mid} {cx + r},{mid} {cx},{cy + r}" fill="{c3}"/></g>')
    s += f'<g filter="url(#glow)"><circle cx="300" cy="210" r="120" fill="#7FE7FF" opacity=".5"/></g>'
    s += gem(300, 200, 150, "#DFFBFF", "#5FD3FF", "#1F8FD6")
    s += gem(110, 340, 60, "#FFB0C8", "#FF3D6E", "#B5122F") + gem(490, 330, 66, "#C9FFC0", "#22C55E", "#0E7A3A") + gem(470, 120, 36, "#F1D6FF", "#B37BFF", "#6B2FD1")
    s += sparkle(250, 110, 1.2) + sparkle(390, 240) + sparkle(130, 250, .7)
    return d, s


def egypt():
    d, s = sky("#3A1A4A", "#FF9F45", "#FFE08A", 260)
    d += lg("pyr", "#F7C66B", "#C7832A") + lg("pyrS", "#B87423", "#7A4A14") + lg("dune", "#E8B866", "#B87A2E")
    s += '<g filter="url(#glow)"><circle cx="300" cy="230" r="110" fill="#FFF0A8"/></g><circle cx="300" cy="230" r="90" fill="#FFE45A"/>'
    for cx, base, w, h in [(300, 400, 230, 260), (120, 410, 120, 140), (480, 410, 130, 150)]:
        s += (f'<g filter="url(#shadow)"><polygon points="{cx},{base - h} {cx + w},{base} {cx},{base}" fill="url(#pyrS)"/>'
              f'<polygon points="{cx},{base - h} {cx - w},{base} {cx},{base}" fill="url(#pyr)"/></g>')
    s += '<path d="M0,400 Q300,370 600,400 L600,504 L0,504Z" fill="url(#dune)"/>'
    s += '<path d="M40,400 Q46,330 30,300 M40,330 q-30,-20 -50,-5 M40,330 q25,-25 50,-10 M40,320 q-10,-30 -35,-35 M40,320 q12,-30 35,-30" stroke="#3A2408" stroke-width="7" fill="none"/>'
    return d, s


def egypt_2():
    d, s = sky("#06142E", "#1E4FA8", "#5FB0FF", 210)
    s += stars(30)
    s += f'<g filter="url(#glow)"><path d="M300,60 m-45,0 a45,45 0 1,0 90,0 a45,45 0 1,0 -90,0 M282,100 L318,100 L318,190 L380,190 L380,225 L318,225 L318,420 L282,420 L282,225 L220,225 L220,190 L282,190Z" fill="#FFD24A" opacity=".55"/></g>'
    s += ('<g filter="url(#shadow)"><path fill-rule="evenodd" d="M300,30 C345,30 370,70 360,115 C352,150 330,170 318,185 L318,190 L400,190 L400,228 L318,228 L318,430 L282,430 L282,228 L200,228 L200,190 L282,190 L282,185 C270,170 248,150 240,115 C230,70 255,30 300,30Z '
          'M300,62 C278,62 266,85 272,112 C278,138 292,155 300,165 C308,155 322,138 328,112 C334,85 322,62 300,62Z" fill="url(#gold)"/></g>')
    for x, c in [(160, "#22C55E"), (440, "#E5394F")]:
        s += f'<circle cx="{x}" cy="209" r="22" fill="{c}" stroke="url(#gold)" stroke-width="6"/><circle cx="{x - 7}" cy="202" r="6" fill="#fff" opacity=".7"/>'
    s += '<circle cx="300" cy="300" r="20" fill="#38D6FF" stroke="#C77A0A" stroke-width="5"/>'
    return d, s


def china():
    d, s = sky("#5A0A0A", "#E84A3A", "#FFC86B", 200)
    d += lg("lan", "#FF5A3A", "#B3120E")
    s += '<path d="M0,420 L80,300 L160,380 L260,270 L360,390 L460,290 L600,420 L600,504 L0,504Z" fill="#6B0E0E" opacity=".7"/>'
    def lantern(cx, cy, sc):
        return (f'<g transform="translate({cx},{cy}) scale({sc})"><line x1="0" y1="-200" x2="0" y2="-110" stroke="#3A0A0A" stroke-width="4"/>'
                f'<g filter="url(#glow)"><ellipse rx="100" ry="85" fill="#FF8A3D" opacity=".7"/></g>'
                f'<g filter="url(#shadow)"><rect x="-40" y="-110" width="80" height="22" rx="6" fill="url(#gold)"/>'
                f'<ellipse rx="95" ry="82" fill="url(#lan)"/>'
                f'<path d="M0,-82 Q-60,0 0,82 M0,-82 Q60,0 0,82 M-60,-60 Q-100,0 -60,60 M60,-60 Q100,0 60,60" stroke="#FFD24A" stroke-width="4" fill="none" opacity=".8"/>'
                f'<rect x="-40" y="76" width="80" height="20" rx="6" fill="url(#gold)"/>'
                f'<path d="M-12,96 L-16,160 M0,96 L0,170 M12,96 L16,160" stroke="#FFD24A" stroke-width="6"/></g>'
                f'<ellipse rx="95" ry="82" fill="url(#shine)" opacity=".5"/></g>')
    s += lantern(300, 215, 1.25) + lantern(105, 160, .6) + lantern(495, 175, .65)
    s += coin(170, 420, 34) + coin(430, 420, 34) + coin(300, 440, 26)
    return d, s


def greek():
    d, s = sky("#2C7BE5", "#BFE3FF", "#FFFFFF", 160)
    d += lg("marble", "#FFFFFF", "#D6DEE8")
    for x, y in [(90, 100), (440, 70)]:
        s += f'<g fill="#fff" opacity=".9"><circle cx="{x}" cy="{y}" r="26"/><circle cx="{x + 30}" cy="{y - 12}" r="32"/><circle cx="{x + 64}" cy="{y}" r="24"/><rect x="{x}" y="{y}" width="64" height="24"/></g>'
    t = '<polygon points="130,180 300,95 470,180" fill="url(#marble)"/><polygon points="170,172 300,110 430,172" fill="#C9D4E2"/><rect x="120" y="180" width="360" height="26" fill="url(#marble)"/>'
    for x in range(145, 461, 64):
        t += f'<rect x="{x}" y="206" width="34" height="170" fill="url(#marble)"/><rect x="{x + 8}" y="206" width="5" height="170" fill="#C9D4E2"/><rect x="{x + 21}" y="206" width="5" height="170" fill="#C9D4E2"/>'
    t += '<rect x="110" y="376" width="380" height="20" fill="url(#marble)"/><rect x="95" y="396" width="410" height="22" fill="#E2E8F0"/>'
    s += f'<g filter="url(#shadow)">{t}</g>'
    s += '<path d="M0,418 L600,418 L600,504 L0,504Z" fill="#3E8E4A"/>'
    s += '<g filter="url(#glow)"><polygon points="330,10 270,120 310,120 270,230 380,95 335,95 370,10" fill="#FFE45A"/></g><polygon points="330,10 270,120 310,120 270,230 380,95 335,95 370,10" fill="#FFF6B0"/>'
    return d, s


def viking():
    d, s = sky("#0E1A2E", "#4A6A8A", "#BFD7F0", 200)
    d += lg("steel", "#E6EDF5", "#7A8899") + lg("horn", "#FFF3D6", "#C9A66B") + lg("shield", "#B5122F", "#6A0A1A")
    s += '<path d="M0,420 L100,300 L200,380 L320,260 L440,370 L520,300 L600,380 L600,504 L0,504Z" fill="#1E2E44"/>'
    s += ('<g filter="url(#shadow)"><circle cx="300" cy="260" r="175" fill="url(#shield)" stroke="url(#gold)" stroke-width="14"/>'
          '<circle cx="300" cy="260" r="40" fill="url(#steel)"/><g stroke="#8A5A2B" stroke-width="10" opacity=".7"><line x1="300" y1="100" x2="300" y2="420"/><line x1="140" y1="260" x2="460" y2="260"/></g></g>')
    s += ('<g filter="url(#shadow)"><path d="M180,250 Q120,180 140,90 Q170,170 230,200Z" fill="url(#horn)"/><path d="M420,250 Q480,180 460,90 Q430,170 370,200Z" fill="url(#horn)"/>'
          '<path d="M200,270 Q200,140 300,135 Q400,140 400,270Z" fill="url(#steel)"/><rect x="195" y="255" width="210" height="26" rx="8" fill="url(#gold)"/>'
          '<rect x="288" y="140" width="24" height="125" fill="url(#gold)"/><path d="M285,281 L315,281 L308,340 L292,340Z" fill="url(#steel)"/></g>')
    return d, s


def india():
    d, s = sky("#5A1A4A", "#FF9F2E", "#FFE08A", 260)
    d += lg("pal", "#FFF6E0", "#E8C98A") + lg("water", "#5FB0FF", "#1E4FA8")
    s += '<circle cx="300" cy="250" r="140" fill="#FFD56B" opacity=".55"/>'
    p = ('<path d="M220,240 Q220,120 300,90 Q380,120 380,240Z" fill="url(#pal)"/><rect x="296" y="60" width="8" height="34" fill="url(#gold)"/>'
         '<rect x="180" y="240" width="240" height="140" fill="url(#pal)"/><path d="M270,380 L270,300 Q300,260 330,300 L330,380Z" fill="#C7832A"/>'
         '<path d="M195,300 L195,260 Q212,240 230,260 L230,300Z M370,300 L370,260 Q388,240 405,260 L405,300Z" fill="#C7832A"/>')
    for x in (120, 480):
        p += f'<rect x="{x - 14}" y="160" width="28" height="220" fill="url(#pal)"/><path d="M{x - 22},165 Q{x},120 {x + 22},165Z" fill="url(#pal)"/><rect x="{x - 2}" y="110" width="4" height="20" fill="url(#gold)"/>'
    for x in (195, 405):
        p += f'<path d="M{x - 30},240 Q{x},190 {x + 30},240Z" fill="url(#pal)"/>'
    s += f'<g filter="url(#shadow)">{p}</g><rect x="0" y="380" width="600" height="124" fill="url(#water)"/>'
    s += '<g opacity=".35" transform="translate(0,760) scale(1,-1)"><rect x="180" y="240" width="240" height="140" fill="#FFF6E0"/><path d="M220,240 Q220,120 300,90 Q380,120 380,240Z" fill="#FFF6E0"/></g>'
    return d, s


def arabian():
    d, s = sky("#120A3A", "#7B3FE4", "#C77DFF", 200)
    d += lg("dune", "#E8A85A", "#9A5A22")
    s += stars(40)
    s += '<path d="M470,60 a70,70 0 1,0 60,110 a55,55 0 1,1 -60,-110Z" fill="#FFE8A0"/>'
    s += '<path d="M0,410 Q150,350 300,400 T600,380 L600,504 L0,504Z" fill="url(#dune)"/>'
    s += '<g filter="url(#glow)"><path d="M380,240 C400,180 340,170 360,110 C380,60 330,40 320,30" stroke="#E0C8FF" stroke-width="22" fill="none" opacity=".7"/></g>'
    s += '<path d="M380,240 C400,180 340,170 360,110 C380,60 330,40 320,30" stroke="#F3E8FF" stroke-width="8" fill="none" opacity=".8"/>'
    s += ('<g filter="url(#shadow)"><path d="M130,300 Q160,250 260,250 L340,250 Q400,250 420,230 L470,215 Q440,250 430,270 Q420,330 340,340 L230,340 Q150,340 130,300Z" fill="url(#gold)"/>'
          '<path d="M130,300 Q90,280 100,240 Q130,270 160,275" stroke="url(#gold)" stroke-width="14" fill="none"/>'
          '<ellipse cx="280" cy="250" rx="70" ry="16" fill="#E8A830"/><path d="M250,250 Q280,200 310,250Z" fill="url(#gold)"/><circle cx="280" cy="208" r="10" fill="url(#gold)"/>'
          '<path d="M230,340 L210,380 L350,380 L330,340Z" fill="#C77A0A"/>'
          '<circle cx="290" cy="300" r="14" fill="#B37BFF" stroke="#FFF4B8" stroke-width="3"/></g>')
    s += '<path d="M170,290 Q260,265 340,275" stroke="#fff" stroke-width="6" fill="none" opacity=".5" stroke-linecap="round"/>'
    return d, s


def wild_west():
    d, s = sky("#3A0E2A", "#FF7A2E", "#FFD27A", 300)
    d += lg("ground", "#C7703A", "#7A3A1A") + lg("cact", "#3BAA5A", "#14612E")
    s += '<circle cx="300" cy="320" r="130" fill="#FFD56B" opacity=".9"/>'
    s += '<path d="M0,330 L80,300 L140,330 L220,290 L300,320 L380,280 L470,320 L600,290 L600,504 L0,504Z" fill="#8A3A2A" opacity=".8"/>'
    s += '<path d="M0,390 Q300,360 600,390 L600,504 L0,504Z" fill="url(#ground)"/>'
    def cactus(x, base, sc):
        return (f'<g transform="translate({x},{base}) scale({sc})" filter="url(#shadow)"><rect x="-22" y="-240" width="44" height="240" rx="22" fill="url(#cact)"/>'
                f'<path d="M-22,-110 L-60,-110 Q-75,-110 -75,-130 L-75,-180" stroke="url(#cact)" stroke-width="30" fill="none" stroke-linecap="round"/>'
                f'<path d="M22,-140 L55,-140 Q70,-140 70,-160 L70,-200" stroke="url(#cact)" stroke-width="30" fill="none" stroke-linecap="round"/></g>')
    s += cactus(90, 420, .75) + cactus(520, 430, .9)
    s += f'<g filter="url(#glow)">{star5(300, 210, 150, "#FFE08A", inner=.55)}</g>'
    star = star5(300, 210, 140, "url(#gold)", 'stroke="#8A4A08" stroke-width="6"', inner=.55)
    tips = ""
    for i in range(5):
        a = -math.pi / 2 + i * 2 * math.pi / 5
        tips += f'<circle cx="{300 + 140 * math.cos(a):.0f}" cy="{210 + 140 * math.sin(a):.0f}" r="16" fill="url(#gold)" stroke="#8A4A08" stroke-width="5"/>'
    s += (f'<g filter="url(#shadow)">{star}{tips}<circle cx="300" cy="210" r="52" fill="none" stroke="#8A4A08" stroke-width="6"/>'
          f'<text x="300" y="224" font-size="22" font-weight="900" text-anchor="middle" fill="#8A4A08" font-family="DejaVu Serif">SHERIFF</text></g>')
    return d, s


SCENES = {f.__name__: f for f in [christmas_eve, valentines_day, witch, modern, forest, neon, underwater, wolf, candy, pirates,
                                   tropical, gold_mine, farm, most_wanted, gems, egypt, egypt_2, china, greek, viking, india,
                                   arabian, wild_west]}


def svg(tid):
    d, s = SCENES[tid]()
    return f'<svg xmlns="http://www.w3.org/2000/svg" width="{W}" height="{H}" viewBox="0 0 {W} {H}"><defs>{DEFS}{d}</defs>{s}</svg>'
