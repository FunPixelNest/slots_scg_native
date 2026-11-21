"""Candy land: bright pastel sweets."""
import math

from symkit import FONT_FAMILY, P, SUITS, lg, letter, medallion, rg, scatter, sparkle, star, wild

INK = "#4A0A3A"
PINK = P("#FFD0EC", "#FF5AB4", "#B0106A", "#4A0630")
MINT = P("#D8FFF0", "#3AD8A8", "#0A7A5E", "#063A2E")
LEMON = P("#FFFAC0", "#FFC23A", "#D06A00", "#5A2604")
BERRY = P("#D8E4FF", "#5A8AFF", "#2A3AB8", "#101A5A")
CHOC = P("#F0C89A", "#B06A36", "#4A2410", "#24100A")
LILAC = P("#F4E0FF", "#B07AF0", "#5A2AA8", "#2A0E5A")
CHERRY = P("#FFC0C8", "#FF3A5A", "#A00A2A", "#4A0414")


def stripes(ch, color="#FFFFFF", op=".55"):
    """Diagonal candy-cane stripes clipped to the letter glyph."""
    cid = f"clip{ch}"
    bars = "".join(f'<rect x="{x}" y="-40" width="14" height="360" fill="{color}"/>' for x in range(-60, 320, 40))
    return (f'<defs><clipPath id="{cid}"><text x="128" y="196" font-family="{FONT_FAMILY}" font-size="190" text-anchor="middle">{ch}</text></clipPath></defs>'
            f'<g clip-path="url(#{cid})" opacity="{op}"><g transform="rotate(35 128 128)">{bars}</g></g>')


def mini_swirl(cx, cy, r, c="#FF5AB4"):
    pts = []
    for i in range(0, 61):
        t = i / 60 * 2.6 * 2 * math.pi
        rr = r * .9 * i / 60
        pts.append(f"{cx + rr * math.cos(t):.1f},{cy + rr * math.sin(t):.1f}")
    return (f'<circle cx="{cx}" cy="{cy}" r="{r + 3}" fill="{INK}"/><circle cx="{cx}" cy="{cy}" r="{r}" fill="#fff"/>'
            f'<polyline points="{" ".join(pts)}" fill="none" stroke="{c}" stroke-width="{r * .28:.1f}" stroke-linecap="round"/>'
            f'<ellipse cx="{cx - r * .35:.1f}" cy="{cy - r * .4:.1f}" rx="{r * .3:.1f}" ry="{r * .16:.1f}" fill="#fff" opacity=".8"/>')


def mini_heart(cx, cy, s, fill):
    return (f'<g transform="translate({cx},{cy}) scale({s}) translate(-128,-128)">'
            f'<path d="{SUITS["heart"]}" fill="{fill}" stroke="{INK}" stroke-width="22" stroke-linejoin="round"/>'
            f'<ellipse cx="90" cy="90" rx="26" ry="16" fill="#fff" opacity=".7" transform="rotate(-30 90 90)"/></g>')


def mini_wrap(cx, cy, s, fill):
    return (f'<g transform="translate({cx},{cy}) scale({s})">'
            f'<path d="M-20,0 L-40,-16 L-36,0 L-40,16Z M20,0 L40,-16 L36,0 L40,16Z" fill="{fill}" stroke="{INK}" stroke-width="5" stroke-linejoin="round"/>'
            f'<ellipse rx="24" ry="17" fill="{fill}" stroke="{INK}" stroke-width="5"/>'
            f'<ellipse cx="-7" cy="-7" rx="9" ry="4" fill="#fff" opacity=".8"/></g>')


# ------------------------------------------------------------------ spiral helper
def spiral(cx, cy, r, turns, n=120):
    pts = []
    for i in range(n + 1):
        t = i / n
        a = t * turns * 2 * math.pi - math.pi / 2
        rr = r * t
        pts.append(f"{cx + rr * math.cos(a):.1f},{cy + rr * math.sin(a):.1f}")
    return " ".join(pts)


# ------------------------------------------------------------------ brown: chocolate bonbon
bonbon = (
    '<defs>' + rg("choc", [(0, "#B87A4A"), (.55, "#6A3418"), (1, "#2E1408")], cx=".38", cy=".3", r=".75")
    + lg("cup", [(0, "#FF9AC8"), (1, "#C02A7A")]) + '</defs>'
    # paper cup with pleats
    '<path d="M62,138 L194,138 L178,200 L78,200Z" fill="url(#cup)" stroke="' + INK + '" stroke-width="7" stroke-linejoin="round"/>'
    + "".join(f'<path d="M{x},142 L{x + (128 - x) * .12:.1f},198" stroke="#8A0A4A" stroke-width="3" opacity=".6"/>' for x in range(78, 190, 14))
    # truffle body
    + '<path d="M58,146 C50,96 86,62 128,62 C170,62 206,96 198,146 C190,160 66,160 58,146Z" fill="url(#choc)" stroke="#1E0A04" stroke-width="7" stroke-linejoin="round"/>'
    # drizzle
    '<path d="M72,108 C84,94 92,120 104,102 C114,88 120,116 132,98 C142,84 150,112 160,96 C170,84 178,104 186,100" fill="none" stroke="#FFE8F4" stroke-width="8" stroke-linecap="round" stroke-linejoin="round"/>'
    '<path d="M80,94 C92,80 100,74 120,70" fill="none" stroke="#fff" stroke-width="5" stroke-linecap="round" opacity=".55"/>'
    # cherry on top
    '<path d="M134,58 C138,44 148,36 160,32" fill="none" stroke="#3A6A1A" stroke-width="5" stroke-linecap="round"/>'
    '<circle cx="128" cy="62" r="16" fill="#E8203A" stroke="' + INK + '" stroke-width="5"/>'
    '<ellipse cx="122" cy="56" rx="5" ry="3.5" fill="#fff" opacity=".9"/>'
    + sparkle(186, 74, .7)
)

# ------------------------------------------------------------------ lily: lilac wrapped candy
wrapped = (
    '<defs>' + rg("lil", [(0, "#F6E6FF"), (.55, "#B47AF4"), (1, "#5A22A8")], cx=".38", cy=".32", r=".75")
    + lg("lilw", [(0, "#E8D0FF"), (1, "#8A4ACC")])
    + '<clipPath id="wrapBody"><ellipse cx="128" cy="128" rx="54" ry="42"/></clipPath></defs>'
    # wrapper ends
    '<path d="M80,128 L42,88 C34,96 40,106 36,114 C30,122 40,130 34,138 C38,148 32,158 42,168Z" fill="url(#lilw)" stroke="' + INK + '" stroke-width="7" stroke-linejoin="round"/>'
    '<path d="M176,128 L214,88 C222,96 216,106 220,114 C226,122 216,130 222,138 C218,148 224,158 214,168Z" fill="url(#lilw)" stroke="' + INK + '" stroke-width="7" stroke-linejoin="round"/>'
    '<path d="M48,100 L76,126 M44,156 L76,132 M208,100 L180,126 M212,156 L180,132" stroke="#6A2AA8" stroke-width="3" opacity=".7"/>'
    # twist knots
    '<path d="M70,112 L84,118 L84,138 L70,144Z M186,112 L172,118 L172,138 L186,144Z" fill="#9A5AE0" stroke="' + INK + '" stroke-width="6" stroke-linejoin="round"/>'
    # body
    '<ellipse cx="128" cy="128" rx="54" ry="42" fill="url(#lil)" stroke="' + INK + '" stroke-width="7"/>'
    '<g clip-path="url(#wrapBody)" opacity=".7"><g transform="rotate(-30 128 128)">'
    + "".join(f'<rect x="{x}" y="60" width="10" height="140" fill="#fff"/>' for x in range(70, 200, 26))
    + '</g></g>'
    '<ellipse cx="108" cy="108" rx="18" ry="9" fill="#fff" opacity=".85" transform="rotate(-20 108 108)"/>'
    + sparkle(176, 74, .8) + sparkle(82, 184, .55)
)

# ------------------------------------------------------------------ pink: swirl lollipop
lolly = (
    '<defs>' + lg("stick", [(0, "#FFFFFF"), (1, "#D8D0E8")], "1", "0") + '</defs>'
    '<rect x="120" y="140" width="16" height="70" rx="7" fill="url(#stick)" stroke="' + INK + '" stroke-width="6"/>'
    '<path d="M110,150 L146,150 L140,164 L116,164Z" fill="#FF8AC8" stroke="' + INK + '" stroke-width="5" stroke-linejoin="round"/>'
    '<circle cx="128" cy="100" r="60" fill="' + INK + '"/>'
    '<circle cx="128" cy="100" r="54" fill="#FFE4F4"/>'
    f'<polyline points="{spiral(128, 100, 50, 3.2)}" fill="none" stroke="#FF3A9A" stroke-width="13" stroke-linecap="round" stroke-linejoin="round"/>'
    f'<polyline points="{spiral(128, 100, 50, 3.2)}" fill="none" stroke="#FF8ACA" stroke-width="5" stroke-linecap="round" stroke-linejoin="round"/>'
    '<circle cx="128" cy="100" r="54" fill="url(#shine)" opacity=".55"/>'
    '<ellipse cx="104" cy="74" rx="16" ry="8" fill="#fff" opacity=".85" transform="rotate(-35 104 74)"/>'
    + sparkle(184, 52, .7)
)

# ------------------------------------------------------------------ red: heart gummy
gummy = (
    '<defs>' + rg("gum", [(0, "#FFB0BC"), (.45, "#FF2A48"), (1, "#8A0418")], cx=".38", cy=".3", r=".75") + '</defs>'
    '<g transform="translate(128,132) scale(.62) translate(-128,-128)">'
    f'<path d="{SUITS["heart"]}" fill="{INK}" stroke="{INK}" stroke-width="22" stroke-linejoin="round"/>'
    f'<path d="{SUITS["heart"]}" fill="url(#gum)"/>'
    '<path d="M56,96 C56,72 74,62 92,64" fill="none" stroke="#fff" stroke-width="12" stroke-linecap="round" opacity=".85"/>'
    '<ellipse cx="170" cy="84" rx="14" ry="8" fill="#fff" opacity=".6"/>'
    '</g>'
    # sugar crystals
    + "".join(f'<rect x="{x}" y="{y}" width="5" height="5" rx="1" fill="#fff" opacity=".85" transform="rotate(30 {x} {y})"/>'
              for x, y in [(96, 120), (116, 140), (150, 116), (164, 134), (132, 164), (108, 104), (140, 150), (120, 182), (172, 112)])
    + sparkle(80, 82, .8) + sparkle(184, 178, .55)
)

# ------------------------------------------------------------------ wild: cupcake
cupcake = (
    '<defs>' + lg("frost", [(0, "#FFF4FA"), (.6, "#FFB4DC"), (1, "#E85AA8")]) + lg("wrapper", [(0, "#7AE8FF"), (1, "#1A8AC8")], "1", "0") + '</defs>'
    '<path d="M82,118 L174,118 L162,166 L94,166Z" fill="url(#wrapper)" stroke="' + INK + '" stroke-width="7" stroke-linejoin="round"/>'
    + "".join(f'<path d="M{x},120 L{x + (128 - x) * .14:.1f},164" stroke="#0A5A8A" stroke-width="4" opacity=".6"/>' for x in range(96, 172, 14))
    # frosting tiers
    + '<path d="M72,122 C60,116 64,98 80,98 C86,88 170,88 176,98 C192,98 196,116 184,122Z" fill="url(#frost)" stroke="' + INK + '" stroke-width="7" stroke-linejoin="round"/>'
    '<path d="M86,100 C76,90 84,74 98,76 C106,64 150,64 158,76 C172,74 180,90 170,100Z" fill="url(#frost)" stroke="' + INK + '" stroke-width="7" stroke-linejoin="round"/>'
    '<path d="M102,78 C100,62 114,52 128,52 C142,52 156,62 154,78Z" fill="url(#frost)" stroke="' + INK + '" stroke-width="7" stroke-linejoin="round"/>'
    # sprinkles
    + "".join(f'<rect x="{x}" y="{y}" width="10" height="4" rx="2" fill="{c}" transform="rotate({a} {x} {y})"/>'
              for x, y, c, a in [(90, 108, "#3AD8A8", 20), (116, 104, "#FFC23A", -30), (146, 108, "#5A8AFF", 40), (168, 110, "#FF3A5A", -10),
                                 (108, 84, "#5A8AFF", -20), (138, 86, "#3AD8A8", 30), (122, 66, "#FF3A5A", 10)])
    # cherry
    + '<path d="M132,42 C136,30 146,24 156,22" fill="none" stroke="#3A6A1A" stroke-width="5" stroke-linecap="round"/>'
    '<circle cx="128" cy="44" r="13" fill="#E8203A" stroke="' + INK + '" stroke-width="5"/>'
    '<ellipse cx="123" cy="40" rx="4" ry="3" fill="#fff" opacity=".9"/>'
)

# ------------------------------------------------------------------ scatter: gumball machine
balls = [(110, 96, "#FF3A5A"), (134, 104, "#3AD8A8"), (152, 90, "#FFC23A"), (118, 74, "#5A8AFF"), (142, 72, "#FF5AB4"),
         (100, 114, "#FFC23A"), (124, 120, "#B07AF0"), (150, 116, "#FF3A5A"), (130, 88, "#FFFFFF")]
gumball = (
    '<defs><clipPath id="globe"><circle cx="128" cy="96" r="42"/></clipPath></defs>'
    '<path d="M92,134 L164,134 L172,170 L84,170Z" fill="#E8203A" stroke="' + INK + '" stroke-width="7" stroke-linejoin="round"/>'
    '<path d="M98,138 L110,138 L104,166 L92,166Z" fill="#fff" opacity=".35"/>'
    '<rect x="116" y="146" width="24" height="14" rx="4" fill="url(#silver)" stroke="' + INK + '" stroke-width="4"/>'
    '<rect x="122" y="150" width="12" height="5" rx="2" fill="' + INK + '"/>'
    '<rect x="100" y="126" width="56" height="12" rx="4" fill="#B0102A" stroke="' + INK + '" stroke-width="5"/>'
    '<circle cx="128" cy="96" r="42" fill="#E8F8FF" stroke="' + INK + '" stroke-width="7"/>'
    '<g clip-path="url(#globe)">'
    + "".join(f'<circle cx="{x}" cy="{y}" r="11" fill="{c}" stroke="{INK}" stroke-width="2.5"/><circle cx="{x - 3}" cy="{y - 4}" r="3" fill="#fff" opacity=".8"/>' for x, y, c in balls)
    + '</g>'
    '<path d="M100,76 C106,64 116,58 126,56" fill="none" stroke="#fff" stroke-width="6" stroke-linecap="round" opacity=".9"/>'
    '<path d="M110,56 L146,56 L142,48 L114,48Z" fill="#E8203A" stroke="' + INK + '" stroke-width="5" stroke-linejoin="round"/>'
    '<circle cx="128" cy="44" r="7" fill="#E8203A" stroke="' + INK + '" stroke-width="4"/>'
    + sparkle(180, 70, .7) + sparkle(78, 140, .55)
)

SYMBOLS = {
    "j": letter("J", PINK, stripes("J") + mini_swirl(196, 50, 20)),
    "q": letter("Q", MINT, stripes("Q") + mini_heart(198, 50, .2, "#FF3A5A")),
    "k": letter("K", LEMON, stripes("K") + star(198, 52, 22, "#FF8AC8", INK, 4)),
    "a": letter("A", BERRY, stripes("A") + mini_wrap(194, 52, .8, "#B07AF0")),
    "brown": medallion("circle", P("#FFE4C4", "#E8A86A", "#8A4A1A", "#3A1A08"), bonbon),
    "lily": medallion("square", P("#FFF0FA", "#FFB4DC", "#C0508A", "#4A0A3A"), wrapped),
    "pink": medallion("hex", MINT, lolly),
    "red": medallion("shield", LEMON, gummy),
    "wild": wild(PINK, cupcake),
    "scatter": scatter(BERRY, gumball, "BONUS"),
}
