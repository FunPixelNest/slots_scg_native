"""
Drawing kit for original slot symbols (SVG, 256x256 canvas, rendered to WEBP by render.py).

Every symbol is built from the same parts so all 23 themes share one quality bar:
  letter()     J Q K A 9 10 in chunky Lilita One with a theme gradient, dark outline, shine
  suit()       glossy heart / diamond / club / spade with a gold rim
  medallion()  framed badge (circle, round square, shield, hexagon, diamond) holding an icon
  wild()       emblem with a gold "WILD" ribbon
  scatter()    gold starburst with an emblem and a "BONUS" ribbon
Small helpers (gem, coin, star, sparkle, glossy) are for drawing the icons themselves.

Icons are drawn in the 256x256 space, centred around (128,128), roughly within 40..216.
Use pal["c1"] (light), pal["c2"] (mid), pal["c3"] (dark), pal["ink"] (outline) and the shared
gradients: url(#gold), url(#goldH), url(#main), url(#mainR), url(#shine), url(#silver).
"""
import math

FONT_FAMILY = "Lilita One"


def P(c1, c2, c3, ink="#2B0A3D", accent="#FFD24A"):
    """Palette: light, mid, dark, outline, accent."""
    return {"c1": c1, "c2": c2, "c3": c3, "ink": ink, "accent": accent}


def lg(id_, stops, x2="0", y2="1"):
    s = "".join(f'<stop offset="{o}" stop-color="{c}"/>' for o, c in stops)
    return f'<linearGradient id="{id_}" x1="0" y1="0" x2="{x2}" y2="{y2}">{s}</linearGradient>'


def rg(id_, stops, cx=".5", cy=".5", r=".5", fx=None, fy=None):
    s = "".join(f'<stop offset="{o}" stop-color="{c}"/>' for o, c in stops)
    f = f' fx="{fx}" fy="{fy}"' if fx else ""
    return f'<radialGradient id="{id_}" cx="{cx}" cy="{cy}" r="{r}"{f}>{s}</radialGradient>'


def defs(p):
    return (
        '<filter id="drop" x="-30%" y="-30%" width="160%" height="160%"><feDropShadow dx="0" dy="6" stdDeviation="5" flood-color="#000" flood-opacity=".45"/></filter>'
        '<filter id="glow" x="-50%" y="-50%" width="200%" height="200%"><feGaussianBlur stdDeviation="8"/></filter>'
        '<filter id="soft" x="-30%" y="-30%" width="160%" height="160%"><feGaussianBlur stdDeviation="2.5"/></filter>'
        + lg("gold", [(0, "#FFF7C4"), (.35, "#FFD54A"), (.7, "#E08A00"), (1, "#FFE27A")])
        + lg("goldH", [(0, "#B86B00"), (.5, "#FFF0A0"), (1, "#B86B00")], "1", "0")
        + lg("silver", [(0, "#FFFFFF"), (.45, "#C9D3E6"), (.75, "#7F8BA6"), (1, "#E3E9F5")])
        + lg("main", [(0, p["c1"]), (.55, p["c2"]), (1, p["c3"])])
        + rg("mainR", [(0, p["c1"]), (.6, p["c2"]), (1, p["c3"])], cx=".4", cy=".35", r=".7")
        + rg("shine", [(0, "#FFFFFF"), (.35, "#FFFFFF66"), (1, "#FFFFFF00")], cx=".35", cy=".25", r=".6")
        + lg("hl", [(0, "#FFFFFFCC"), (1, "#FFFFFF00")])
    )


def svg(inner, p, extra_defs=""):
    return (f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 256 256" width="256" height="256">'
            f'<defs>{defs(p)}{extra_defs}</defs>{inner}</svg>')


# ------------------------------------------------------------------ small helpers
def star_pts(cx, cy, r, n=5, inner=.45, rot=-90):
    pts = []
    for i in range(n * 2):
        a = math.radians(rot + i * 180 / n)
        rr = r if i % 2 == 0 else r * inner
        pts.append(f"{cx + rr * math.cos(a):.1f},{cy + rr * math.sin(a):.1f}")
    return " ".join(pts)


def star(cx, cy, r, fill="url(#gold)", stroke="#7A3A00", sw=3, n=5, inner=.45):
    return f'<polygon points="{star_pts(cx, cy, r, n, inner)}" fill="{fill}" stroke="{stroke}" stroke-width="{sw}" stroke-linejoin="round"/>'


def sparkle(x, y, s=1.0, c="#FFFFFF"):
    return (f'<g transform="translate({x},{y}) scale({s})"><path d="M0,-14 L2.6,-2.6 L14,0 L2.6,2.6 L0,14 L-2.6,2.6 L-14,0 L-2.6,-2.6Z" fill="{c}"/>'
            f'<circle r="2.6" fill="#fff"/></g>')


def gem(cx, cy, r, c1, c2, c3, ink="#1A0A2A"):
    """Round brilliant seen from above: octagon with facets."""
    pts = [(cx + r * math.cos(math.radians(22.5 + 45 * i)), cy + r * math.sin(math.radians(22.5 + 45 * i))) for i in range(8)]
    ip = [(cx + r * .55 * math.cos(math.radians(22.5 + 45 * i)), cy + r * .55 * math.sin(math.radians(22.5 + 45 * i))) for i in range(8)]
    o = " ".join(f"{x:.1f},{y:.1f}" for x, y in pts)
    i_ = " ".join(f"{x:.1f},{y:.1f}" for x, y in ip)
    facets = "".join(
        f'<polygon points="{pts[k][0]:.1f},{pts[k][1]:.1f} {pts[(k + 1) % 8][0]:.1f},{pts[(k + 1) % 8][1]:.1f} {ip[(k + 1) % 8][0]:.1f},{ip[(k + 1) % 8][1]:.1f} {ip[k][0]:.1f},{ip[k][1]:.1f}" '
        f'fill="{[c1, c2, c3, c2][k % 4]}" opacity=".9"/>' for k in range(8))
    return (f'<polygon points="{o}" fill="{c2}" stroke="{ink}" stroke-width="{max(2, r * .08):.1f}" stroke-linejoin="round"/>{facets}'
            f'<polygon points="{i_}" fill="{c1}" opacity=".85"/>'
            f'<ellipse cx="{cx - r * .25:.1f}" cy="{cy - r * .3:.1f}" rx="{r * .28:.1f}" ry="{r * .16:.1f}" fill="#fff" opacity=".8" transform="rotate(-30 {cx - r * .25:.1f} {cy - r * .3:.1f})"/>')


def coin(cx, cy, r, mark="star"):
    inner = star(cx, cy, r * .45, "#FFE680", "#B86B00", max(1.5, r * .05)) if mark == "star" else ""
    return (f'<circle cx="{cx}" cy="{cy}" r="{r}" fill="#8A4A00"/><circle cx="{cx}" cy="{cy - r * .04:.1f}" r="{r * .94:.1f}" fill="url(#gold)"/>'
            f'<circle cx="{cx}" cy="{cy}" r="{r * .72:.1f}" fill="none" stroke="#B86B00" stroke-width="{r * .08:.1f}"/>{inner}'
            f'<ellipse cx="{cx - r * .3:.1f}" cy="{cy - r * .4:.1f}" rx="{r * .35:.1f}" ry="{r * .18:.1f}" fill="#fff" opacity=".6"/>')


def glossy(shape_svg_with_fill_placeholder, p, ink=None):
    """Draw [shape] three times: dark outline, gradient body, shine. Use {F} where the fill goes and {S} for stroke attrs."""
    ink = ink or p["ink"]
    a = shape_svg_with_fill_placeholder.replace("{F}", ink).replace("{S}", f'stroke="{ink}" stroke-width="16" stroke-linejoin="round"')
    b = shape_svg_with_fill_placeholder.replace("{F}", "url(#main)").replace("{S}", "")
    c = shape_svg_with_fill_placeholder.replace("{F}", "url(#shine)").replace("{S}", "")
    return f'<g filter="url(#drop)">{a}</g>{b}<g opacity=".75">{c}</g>'


def outlined_text(txt, x, y, size, fill="url(#gold)", ink="#3A0A00", sw=None, spacing=0):
    sw = sw if sw is not None else size * .16
    ls = f' letter-spacing="{spacing}"' if spacing else ""
    base = f'x="{x}" y="{y}" font-family="{FONT_FAMILY}" font-size="{size}" text-anchor="middle"{ls}'
    return (f'<text {base} fill="{ink}" stroke="{ink}" stroke-width="{sw:.1f}" stroke-linejoin="round" filter="url(#drop)">{txt}</text>'
            f'<text {base} fill="{fill}">{txt}</text>')


# ------------------------------------------------------------------ symbol families
def letter(ch, p, accent=""):
    """Card rank (J Q K A 9 10) as a chunky glossy letter. [accent]: extra SVG drawn on top (small icon)."""
    size = 190 if len(ch) == 1 else 150
    y = 196 if len(ch) == 1 else 186
    base = f'x="128" y="{y}" font-family="{FONT_FAMILY}" font-size="{size}" text-anchor="middle"'
    inner = (
        f'<text {base} fill="{p["ink"]}" stroke="{p["ink"]}" stroke-width="26" stroke-linejoin="round" filter="url(#drop)">{ch}</text>'
        f'<text {base} fill="#FFFFFF" stroke="#FFFFFF" stroke-width="10" stroke-linejoin="round" opacity=".55">{ch}</text>'
        f'<text {base} fill="url(#main)">{ch}</text>'
        f'<text {base} fill="url(#hl)" opacity=".55" clip-path="url(#topHalf)">{ch}</text>'
    )
    return svg(inner + accent, p, '<clipPath id="topHalf"><rect x="0" y="0" width="256" height="118"/></clipPath>')


SUITS = {
    "heart": "M128,214 C70,170 34,140 34,98 C34,66 58,44 86,44 C106,44 120,56 128,72 C136,56 150,44 170,44 C198,44 222,66 222,98 C222,140 186,170 128,214Z",
    "diamond": "M128,30 L210,128 L128,226 L46,128Z",
    "spade": "M128,32 C160,74 216,104 216,148 C216,176 194,194 170,194 C154,194 142,186 136,176 C138,196 146,210 160,222 L96,222 C110,210 118,196 120,176 C114,186 102,194 86,194 C62,194 40,176 40,148 C40,104 96,74 128,32Z",
    "club": "M128,34 C154,34 172,54 172,78 C172,90 168,98 162,106 C170,102 178,100 186,100 C210,100 228,120 228,144 C228,168 210,188 186,188 C164,188 148,176 140,160 C140,186 148,206 164,222 L92,222 C108,206 116,186 116,160 C108,176 92,188 70,188 C46,188 28,168 28,144 C28,120 46,100 70,100 C78,100 86,102 94,106 C88,98 84,90 84,78 C84,54 102,34 128,34Z",
}


def suit(kind, p, accent=""):
    d = SUITS[kind]
    inner = (f'<g filter="url(#drop)"><path d="{d}" fill="{p["ink"]}" stroke="{p["ink"]}" stroke-width="20" stroke-linejoin="round"/></g>'
             f'<path d="{d}" fill="url(#gold)" stroke="url(#gold)" stroke-width="10" stroke-linejoin="round"/>'
             f'<path d="{d}" fill="url(#mainR)"/>'
             f'<path d="{d}" fill="url(#shine)" opacity=".8"/>')
    return svg(inner + accent, p)


def _shape(shape, inset=0):
    a, b = 18 + inset, 238 - inset
    if shape == "circle":
        return f'<circle cx="128" cy="128" r="{110 - inset}" {{A}}/>'
    if shape == "square":
        return f'<rect x="{a}" y="{a}" width="{b - a}" height="{b - a}" rx="{44 - inset * .6:.0f}" {{A}}/>'
    if shape == "shield":
        return f'<path d="M128,{a} L{b},{a + 28} C{b},{140} {190 - inset * .4:.0f},{200 - inset * .2:.0f} 128,{b} C{66 + inset * .4:.0f},{200 - inset * .2:.0f} {a},140 {a},{a + 28}Z" {{A}}/>'
    if shape == "hex":
        r = 112 - inset
        pts = " ".join(f"{128 + r * math.cos(math.radians(30 + 60 * i)):.1f},{128 + r * math.sin(math.radians(30 + 60 * i)):.1f}" for i in range(6))
        return f'<polygon points="{pts}" stroke-linejoin="round" {{A}}/>'
    if shape == "diamond":
        r = 118 - inset
        return f'<polygon points="128,{128 - r} {128 + r},128 128,{128 + r} {128 - r},128" stroke-linejoin="round" {{A}}/>'
    raise ValueError(shape)


def medallion(shape, p, icon, rim="url(#gold)", bg=None):
    """Framed badge: [shape] in circle|square|shield|hex|diamond, [icon] SVG drawn inside (within ~56..200)."""
    bgfill = bg or "url(#mainR)"
    outer = _shape(shape).replace("{A}", f'fill="{p["ink"]}" stroke="{p["ink"]}" stroke-width="10"')
    ring = _shape(shape, 4).replace("{A}", f'fill="{rim}"')
    body = _shape(shape, 13).replace("{A}", f'fill="{bgfill}" stroke="{p["ink"]}" stroke-width="3"')
    shine = _shape(shape, 13).replace("{A}", 'fill="url(#shine)" opacity=".35"')
    return svg(f'<g filter="url(#drop)">{outer}</g>{ring}{body}{shine}<g filter="url(#drop)">{icon}</g>', p)


def ribbon(txt, y, p, w=200, fill="url(#gold)", text_fill="#FFFFFF", size=46):
    x0, x1 = 128 - w / 2, 128 + w / 2
    h = size * 1.05
    body = (f'<path d="M{x0 - 14},{y + 8} L{x0 + 8},{y + 8} L{x0 + 8},{y + h + 8} L{x0 - 14},{y + h + 8} L{x0 - 2},{y + h / 2 + 8}Z" fill="{p["c3"]}" stroke="{p["ink"]}" stroke-width="4" stroke-linejoin="round"/>'
            f'<path d="M{x1 + 14},{y + 8} L{x1 - 8},{y + 8} L{x1 - 8},{y + h + 8} L{x1 + 14},{y + h + 8} L{x1 + 2},{y + h / 2 + 8}Z" fill="{p["c3"]}" stroke="{p["ink"]}" stroke-width="4" stroke-linejoin="round"/>'
            f'<rect x="{x0}" y="{y}" width="{w}" height="{h}" rx="10" fill="{fill}" stroke="{p["ink"]}" stroke-width="5" filter="url(#drop)"/>'
            f'<rect x="{x0 + 6}" y="{y + 4}" width="{w - 12}" height="{h * .38:.1f}" rx="6" fill="#fff" opacity=".35"/>')
    return body + outlined_text(txt, 128, y + h * .82, size, text_fill, p["ink"], size * .2, 1)


def wild(p, emblem):
    """[emblem]: themed SVG drawn behind, centred around (128,104)."""
    burst = f'<polygon points="{star_pts(128, 110, 112, 14, .78)}" fill="url(#mainR)" stroke="{p["ink"]}" stroke-width="6" stroke-linejoin="round" filter="url(#drop)"/>'
    return svg(burst + f'<polygon points="{star_pts(128, 110, 96, 14, .8)}" fill="url(#shine)" opacity=".35"/>' + emblem + ribbon("WILD", 168, p, 196), p)


def scatter(p, emblem, word="BONUS"):
    burst = (f'<polygon points="{star_pts(128, 112, 116, 16, .72)}" fill="url(#gold)" stroke="#7A3A00" stroke-width="6" stroke-linejoin="round" filter="url(#drop)"/>'
             f'<circle cx="128" cy="112" r="80" fill="url(#mainR)" stroke="{p["ink"]}" stroke-width="5"/>'
             f'<circle cx="128" cy="112" r="80" fill="url(#shine)" opacity=".4"/>')
    return svg(burst + emblem + ribbon(word, 172, p, 206, fill="url(#main)", size=42), p)
