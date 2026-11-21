"""China: lucky red and gold."""
import math

from symkit import P, gem, lg, letter, medallion, rg, scatter, sparkle, star, wild

INK = "#3A0606"
RED = P("#FFA89A", "#E0202A", "#7A0408", "#340204")
GOLD = P("#FFF4B0", "#FFB81A", "#B05A00", "#4A2004")
JADE = P("#C8FFD8", "#24B060", "#0A5A2A", "#042A12")
ROYAL = P("#D8C8FF", "#6A4AE0", "#2A1690", "#120848")


def cn_coin(cx, cy, r, hole="#5A0A0A"):
    """Round coin with a square hole and four raised marks."""
    h = r * .3
    marks = ""
    for a in (0, 90, 180, 270):
        x = cx + r * .58 * math.cos(math.radians(a - 90))
        y = cy + r * .58 * math.sin(math.radians(a - 90))
        w, hh = (r * .26, r * .1) if a in (0, 180) else (r * .1, r * .26)
        marks += f'<rect x="{x - w / 2:.1f}" y="{y - hh / 2:.1f}" width="{w:.1f}" height="{hh:.1f}" rx="{r * .03:.1f}" fill="#B86A00"/>'
    return (f'<circle cx="{cx}" cy="{cy}" r="{r}" fill="url(#gold)" stroke="{INK}" stroke-width="{max(3, r * .1):.1f}"/>'
            f'<circle cx="{cx}" cy="{cy}" r="{r * .82:.1f}" fill="none" stroke="#B86A00" stroke-width="{r * .06:.1f}"/>'
            f'<rect x="{cx - h * 1.5:.1f}" y="{cy - h * 1.5:.1f}" width="{h * 3:.1f}" height="{h * 3:.1f}" fill="none" stroke="#B86A00" stroke-width="{r * .06:.1f}"/>'
            f'{marks}'
            f'<rect x="{cx - h:.1f}" y="{cy - h:.1f}" width="{h * 2:.1f}" height="{h * 2:.1f}" fill="{hole}" stroke="{INK}" stroke-width="{max(2, r * .06):.1f}"/>'
            f'<path d="M{cx - r * .7:.1f},{cy - r * .2:.1f} A{r * .72:.1f},{r * .72:.1f} 0 0 1 {cx - r * .2:.1f},{cy - r * .7:.1f}" fill="none" stroke="#fff" stroke-width="{r * .1:.1f}" stroke-linecap="round" opacity=".8"/>')


# ------------------------------------------------------------------ coins
coins = (
    cn_coin(156, 90, 46)
    # red cord through both holes
    + '<path d="M156,90 C150,110 130,120 112,130" fill="none" stroke="' + INK + '" stroke-width="12" stroke-linecap="round"/>'
    '<path d="M156,90 C150,110 130,120 112,130" fill="none" stroke="#E0202A" stroke-width="6" stroke-linecap="round"/>'
    + cn_coin(110, 130, 56)
    + '<path d="M110,130 C108,150 112,166 120,176" fill="none" stroke="' + INK + '" stroke-width="12" stroke-linecap="round"/>'
    '<path d="M110,130 C108,150 112,166 120,176" fill="none" stroke="#E0202A" stroke-width="6" stroke-linecap="round"/>'
    # knot and tassel
    '<path d="M120,166 L134,176 L120,188 L106,176Z" fill="#E0202A" stroke="' + INK + '" stroke-width="5" stroke-linejoin="round"/>'
    '<path d="M112,188 L128,188 L134,212 L106,212Z" fill="#C0101A" stroke="' + INK + '" stroke-width="5" stroke-linejoin="round"/>'
    '<path d="M114,194 L112,208 M120,194 L120,208 M126,194 L128,208" stroke="#FF8A8A" stroke-width="2"/>'
    '<rect x="110" y="186" width="20" height="6" rx="2" fill="url(#gold)" stroke="' + INK + '" stroke-width="3"/>'
    + sparkle(196, 150, .8) + sparkle(70, 70, .6)
)

# ------------------------------------------------------------------ fan
def _fan():
    cx, cy, R, r0 = 128, 178, 92, 28
    a0, a1, n = -158, -22, 12
    segs = ""
    for i in range(n):
        t0 = math.radians(a0 + (a1 - a0) * i / n)
        t1 = math.radians(a0 + (a1 - a0) * (i + 1) / n)
        p = [(cx + R * math.cos(t0), cy + R * math.sin(t0)), (cx + R * math.cos(t1), cy + R * math.sin(t1)),
             (cx + r0 * math.cos(t1), cy + r0 * math.sin(t1)), (cx + r0 * math.cos(t0), cy + r0 * math.sin(t0))]
        col = "#E8303A" if i % 2 == 0 else "#B8101E"
        segs += f'<polygon points="{" ".join(f"{x:.1f},{y:.1f}" for x, y in p)}" fill="{col}"/>'
    ta, tb = math.radians(a0), math.radians(a1)
    outline = (f'M{cx + R * math.cos(ta):.1f},{cy + R * math.sin(ta):.1f} A{R},{R} 0 0 1 {cx + R * math.cos(tb):.1f},{cy + R * math.sin(tb):.1f} '
               f'L{cx + 6 * math.cos(tb):.1f},{cy + 6 * math.sin(tb):.1f} L{cx + 6 * math.cos(ta):.1f},{cy + 6 * math.sin(ta):.1f}Z')
    ribs = ""
    for i in range(n + 1):
        t = math.radians(a0 + (a1 - a0) * i / n)
        ribs += f'<line x1="{cx}" y1="{cy}" x2="{cx + r0 * math.cos(t):.1f}" y2="{cy + r0 * math.sin(t):.1f}" stroke="#7A3A10" stroke-width="5" stroke-linecap="round"/>'
    gold_arc = f'M{cx + (R - 9) * math.cos(ta):.1f},{cy + (R - 9) * math.sin(ta):.1f} A{R - 9},{R - 9} 0 0 1 {cx + (R - 9) * math.cos(tb):.1f},{cy + (R - 9) * math.sin(tb):.1f}'
    blossoms = ""
    for (bx, by, s) in [(100, 112, 1), (150, 102, .8), (174, 136, .7), (78, 146, .7), (128, 132, .6)]:
        for k in range(5):
            a = math.radians(k * 72 - 90)
            blossoms += f'<circle cx="{bx + 7 * s * math.cos(a):.1f}" cy="{by + 7 * s * math.sin(a):.1f}" r="{6 * s:.1f}" fill="#FFD0DC"/>'
        blossoms += f'<circle cx="{bx}" cy="{by}" r="{3.5 * s:.1f}" fill="#FFC21A"/>'
    branch = '<path d="M62,160 C88,140 104,126 118,104 C128,90 148,96 160,90" fill="none" stroke="#4A1A0A" stroke-width="5" stroke-linecap="round" opacity=".85"/>'
    return (f'<path d="{outline}" fill="{INK}" stroke="{INK}" stroke-width="12" stroke-linejoin="round"/>'
            + segs + f'<path d="{gold_arc}" fill="none" stroke="url(#gold)" stroke-width="7"/>' + branch + blossoms + ribs
            + f'<path d="{outline}" fill="url(#shine)" opacity=".35"/>'
            + f'<circle cx="{cx}" cy="{cy}" r="10" fill="url(#gold)" stroke="{INK}" stroke-width="5"/>'
            + f'<path d="M{cx},{cy + 8} C{cx + 4},{cy + 16} {cx + 10},{cy + 18} {cx + 14},{cy + 20}" fill="none" stroke="#E0202A" stroke-width="5" stroke-linecap="round"/>'
            + f'<path d="M{cx + 10},{cy + 18} L{cx + 22},{cy + 18} L{cx + 24},{cy + 32} L{cx + 10},{cy + 32}Z" fill="#E0202A" stroke="{INK}" stroke-width="3.5" stroke-linejoin="round"/>')


fan = _fan()

# ------------------------------------------------------------------ scroll
scroll = (
    '<defs>' + lg("paper", [(0, "#FF5A50"), (.5, "#D8141E"), (1, "#8A0410")], "1", "1")
    + lg("rod", [(0, "#5A2A0A"), (.45, "#C8803A"), (1, "#4A1A06")], "1", "0") + '</defs>'
    '<rect x="66" y="74" width="124" height="108" fill="url(#paper)" stroke="' + INK + '" stroke-width="7"/>'
    '<rect x="78" y="84" width="100" height="88" fill="none" stroke="url(#gold)" stroke-width="4"/>'
    # gold diamond plaque with knot ornament
    '<polygon points="128,94 162,128 128,162 94,128" fill="url(#gold)" stroke="' + INK + '" stroke-width="5" stroke-linejoin="round"/>'
    '<polygon points="128,106 150,128 128,150 106,128" fill="#C0101A"/>'
    '<path d="M118,118 L138,118 L138,138 L118,138Z M128,108 L128,148 M108,128 L148,128" fill="none" stroke="url(#gold)" stroke-width="4"/>'
    '<circle cx="128" cy="128" r="5" fill="url(#gold)"/>'
    '<path d="M72,80 L110,80" stroke="#fff" stroke-width="4" stroke-linecap="round" opacity=".5"/>'
    # rollers
    + "".join(
        f'<rect x="{x}" y="62" width="20" height="132" rx="8" fill="url(#rod)" stroke="{INK}" stroke-width="6"/>'
        f'<rect x="{x - 4}" y="50" width="28" height="18" rx="7" fill="url(#gold)" stroke="{INK}" stroke-width="5"/>'
        f'<rect x="{x - 4}" y="188" width="28" height="18" rx="7" fill="url(#gold)" stroke="{INK}" stroke-width="5"/>'
        f'<rect x="{x + 4}" y="70" width="5" height="116" rx="2" fill="#fff" opacity=".35"/>' for x in (52, 184))
    + sparkle(200, 40, .6)
)

# ------------------------------------------------------------------ sycee
sycee = (
    '<defs>' + rg("ingot", [(0, "#FFFBD8"), (.4, "#FFD24A"), (.8, "#D88A00"), (1, "#8A4A00")], cx=".4", cy=".3", r=".8") + '</defs>'
    # dome
    '<path d="M84,132 C80,92 104,74 128,74 C152,74 176,92 172,132Z" fill="url(#ingot)" stroke="' + INK + '" stroke-width="7" stroke-linejoin="round"/>'
    '<path d="M100,110 C100,94 110,84 124,82" fill="none" stroke="#fff" stroke-width="7" stroke-linecap="round" opacity=".85"/>'
    # boat body with upturned ends
    '<path d="M44,116 C58,108 78,116 92,132 L164,132 C178,116 198,108 212,116 C210,160 178,190 128,190 C78,190 46,160 44,116Z" '
    'fill="url(#gold)" stroke="' + INK + '" stroke-width="7" stroke-linejoin="round"/>'
    '<path d="M54,122 C64,118 80,124 90,138 L166,138 C176,124 192,118 202,122" fill="none" stroke="#B86A00" stroke-width="4"/>'
    '<path d="M60,140 C70,166 96,180 128,182" fill="none" stroke="#fff" stroke-width="5" stroke-linecap="round" opacity=".6"/>'
    '<path d="M100,160 C118,168 138,168 156,160" fill="none" stroke="#B86A00" stroke-width="4" opacity=".7"/>'
    + sparkle(176, 80, 1) + sparkle(70, 96, .6) + sparkle(190, 172, .5)
)

# ------------------------------------------------------------------ wild: red lantern
lantern = (
    '<defs>' + rg("lant", [(0, "#FFB08A"), (.5, "#E8202A"), (1, "#7A0408")], cx=".4", cy=".35", r=".7") + '</defs>'
    '<path d="M128,26 L128,46" stroke="' + INK + '" stroke-width="6"/>'
    '<circle cx="128" cy="28" r="6" fill="none" stroke="url(#gold)" stroke-width="4"/>'
    '<ellipse cx="128" cy="104" rx="64" ry="50" fill="url(#lant)" stroke="' + INK + '" stroke-width="7"/>'
    '<path d="M128,54 L128,154 M100,57 C84,80 84,128 100,151 M156,57 C172,80 172,128 156,151 M78,68 C62,90 62,118 78,140 M178,68 C194,90 194,118 178,140" '
    'fill="none" stroke="#8A0410" stroke-width="3.5"/>'
    '<path d="M86,78 C94,66 106,60 118,58" fill="none" stroke="#fff" stroke-width="6" stroke-linecap="round" opacity=".7"/>'
    '<rect x="98" y="44" width="60" height="14" rx="5" fill="url(#gold)" stroke="' + INK + '" stroke-width="5"/>'
    '<rect x="98" y="150" width="60" height="14" rx="5" fill="url(#gold)" stroke="' + INK + '" stroke-width="5"/>'
    + cn_coin(128, 104, 24, "#8A0410")
)

# ------------------------------------------------------------------ scatter: firecrackers
def cracker(rot, h=80):
    return (f'<g transform="rotate({rot} 128 160)">'
            f'<path d="M128,{150 - h} C136,{140 - h} 148,{136 - h} 152,{124 - h}" fill="none" stroke="#3A2A1A" stroke-width="4" stroke-linecap="round"/>'
            + star(152, 124 - h, 9, "#FFF6B0", "#E86A00", 2, 8, .4) +
            f'<rect x="113" y="{150 - h}" width="30" height="{h}" rx="5" fill="url(#crk)" stroke="{INK}" stroke-width="6"/>'
            f'<rect x="113" y="{150 - h}" width="30" height="10" rx="3" fill="url(#gold)" stroke="{INK}" stroke-width="4"/>'
            f'<rect x="113" y="140" width="30" height="10" rx="3" fill="url(#gold)" stroke="{INK}" stroke-width="4"/>'
            f'<rect x="118" y="{164 - h}" width="5" height="{h - 28}" rx="2" fill="#fff" opacity=".4"/>'
            f'</g>')


firecrackers = (
    '<defs>' + lg("crk", [(0, "#FF6A5A"), (.5, "#E0141E"), (1, "#7A0408")], "1", "0") + '</defs>'
    '<g transform="translate(128,124) scale(1.22) translate(-128,-124)">'
    + cracker(-26, 74) + cracker(26, 74) + cracker(0, 88)
    + '<path d="M96,132 C110,140 146,140 160,132 L158,146 C146,152 110,152 98,146Z" fill="url(#gold)" stroke="' + INK + '" stroke-width="5" stroke-linejoin="round"/></g>'
    + sparkle(74, 72, .8) + sparkle(188, 140, .6)
)


def small(icon, cx, cy, s, ox=128, oy=128):
    return f'<g transform="translate({cx},{cy}) scale({s}) translate({-ox},{-oy})">{icon}</g>'


SYMBOLS = {
    "j": letter("J", RED, cn_coin(198, 52, 22)),
    "q": letter("Q", GOLD, small(lantern, 198, 54, .32, 128, 100)),
    "k": letter("K", JADE, small(sycee, 196, 54, .3, 128, 134)),
    "a": letter("A", ROYAL, gem(198, 52, 18, "#FFB0B0", "#E0202A", "#7A0408", INK)),
    "coins": medallion("circle", RED, coins),
    "fan": medallion("hex", JADE, fan),
    "scroll": medallion("square", P("#A8E0FF", "#1A6AB0", "#08285A", "#04122E"), scroll),
    "sycee": medallion("shield", RED, sycee),
    "wild": wild(RED, lantern),
    "scatter": scatter(JADE, firecrackers, "BONUS"),
}
