"""Gems: large faceted gems (each a different cut) plus gold and silver treasure chests."""
import math

from symkit import P, coin, gem, sparkle, star_pts, svg, wild

L = (-0.55, -0.83)  # light direction (from top-left)


def hx(c):
    c = c.lstrip("#")
    return tuple(int(c[i:i + 2], 16) for i in (0, 2, 4))


def mix(a, b, t):
    a, b = hx(a), hx(b)
    return "#%02X%02X%02X" % tuple(round(a[i] + (b[i] - a[i]) * t) for i in range(3))


def ramp(cols, t):
    t = max(0.0, min(1.0, t))
    n = len(cols) - 1
    i = min(int(t * n), n - 1)
    return mix(cols[i], cols[i + 1], t * n - i)


def pts_s(pts):
    return " ".join(f"{x:.1f},{y:.1f}" for x, y in pts)


def scale(pts, c, k, dy=0.0):
    return [(c[0] + (x - c[0]) * k, c[1] + (y - c[1]) * k + dy) for x, y in pts]


def shade(poly, c, cols, bias=0.0, jitter=0.0):
    mx = sum(p[0] for p in poly) / len(poly) - c[0]
    my = sum(p[1] for p in poly) / len(poly) - c[1]
    d = math.hypot(mx, my) or 1
    t = 0.5 + 0.5 * (mx / d * L[0] + my / d * L[1]) + bias + jitter
    return ramp(cols, t)


def cut(id_, outer, c, cols, ink, rings=(0.72, 0.46), table_dy=0.0, star_lines=True):
    """Faceted gem from an outline polygon: concentric rings of facets + table."""
    out = [f'<polygon points="{pts_s(outer)}" fill="{ink}" stroke="{ink}" stroke-width="14" stroke-linejoin="round" filter="url(#drop)"/>']
    levels = [outer] + [scale(outer, c, k, table_dy * (1 - k)) for k in rings]
    n = len(outer)
    for r in range(len(levels) - 1):
        a, b = levels[r], levels[r + 1]
        for i in range(n):
            j = (i + 1) % n
            poly = [a[i], a[j], b[j], b[i]]
            jit = (0.12 if (i + r) % 2 else -0.06)
            out.append(f'<polygon points="{pts_s(poly)}" fill="{shade(poly, c, cols, -0.08 * r, jit)}" stroke="{ink}" stroke-opacity=".35" stroke-width="1.2" stroke-linejoin="round"/>')
    tb = levels[-1]
    out.append(f'<defs><linearGradient id="{id_}T" x1="0" y1="0" x2="1" y2="1"><stop offset="0" stop-color="{cols[-1]}"/><stop offset=".5" stop-color="{cols[-2]}"/><stop offset="1" stop-color="{cols[1]}"/></linearGradient></defs>')
    out.append(f'<polygon points="{pts_s(tb)}" fill="url(#{id_}T)" stroke="{ink}" stroke-opacity=".4" stroke-width="1.5" stroke-linejoin="round"/>')
    if star_lines:
        out.append("".join(f'<line x1="{tb[i][0]:.1f}" y1="{tb[i][1]:.1f}" x2="{c[0]:.1f}" y2="{c[1] + table_dy * (1 - rings[-1]):.1f}" stroke="#fff" stroke-opacity=".18" stroke-width="1.5"/>' for i in range(0, n, 2)))
    out.append(f'<polygon points="{pts_s(outer)}" fill="none" stroke="{ink}" stroke-width="6" stroke-linejoin="round"/>')
    return "".join(out)


def backdrop(col, glow):
    return (f'<defs><radialGradient id="bgG"><stop offset="0" stop-color="{glow}" stop-opacity=".9"/><stop offset=".55" stop-color="{col}" stop-opacity=".45"/><stop offset="1" stop-color="{col}" stop-opacity="0"/></radialGradient></defs>'
            f'<circle cx="128" cy="128" r="124" fill="url(#bgG)"/>'
            f'<polygon points="{star_pts(128, 128, 122, 12, .25)}" fill="#fff" opacity=".18"/>')


def shine_streak(x, y, w, h, rot):
    return f'<ellipse cx="{x}" cy="{y}" rx="{w}" ry="{h}" fill="#fff" opacity=".75" transform="rotate({rot} {x} {y})"/>'


def gem_symbol(outer, c, cols, ink, glow, extra="", **kw):
    p = P(cols[-1], cols[2], cols[0], ink)
    return svg(backdrop(cols[2], glow) + cut("g", outer, c, cols, ink, **kw) + extra, p)


def poly_ellipse(cx, cy, rx, ry, n=16, rot=0):
    return [(cx + rx * math.cos(math.radians(rot + 360 * i / n)), cy + ry * math.sin(math.radians(rot + 360 * i / n))) for i in range(n)]


# ---------------------------------------------------------------- outlines
def emerald_outline():
    x0, x1, y0, y1, k = 50, 206, 64, 192, 28
    return [(x0 + k, y0), (x1 - k, y0), (x1, y0 + k), (x1, y1 - k), (x1 - k, y1), (x0 + k, y1), (x0, y1 - k), (x0, y0 + k)]


def heart_outline(n=24):
    pts = []
    for i in range(n):
        t = 2 * math.pi * i / n
        x = 16 * math.sin(t) ** 3
        y = 13 * math.cos(t) - 5 * math.cos(2 * t) - 2 * math.cos(3 * t) - math.cos(4 * t)
        pts.append((128 + x * 5.6, 124 - y * 5.6))
    return pts


def pear_outline(n=20):
    pts = []
    for i in range(n):
        t = 2 * math.pi * i / n
        # teardrop: point at top
        x = math.sin(t) * (math.sin(t / 2) ** 1.3)
        y = -math.cos(t)
        pts.append((128 + x * 78, 138 + y * 88))
    return pts


def trillion_outline():
    pts = []
    corners = [(128, 34), (218, 192), (38, 192)]
    for i in range(3):
        a, b = corners[i], corners[(i + 1) % 3]
        pts.append(a)
        mx, my = (a[0] + b[0]) / 2, (a[1] + b[1]) / 2
        dx, dy = mx - 128, my - 140
        d = math.hypot(dx, dy)
        pts.append((mx + dx / d * 10, my + dy / d * 10))
    # round the corners slightly by splitting each corner
    out = []
    for i, p in enumerate(pts):
        if i % 2 == 0:
            prv, nxt = pts[i - 1], pts[(i + 1) % len(pts)]
            out.append((p[0] + (prv[0] - p[0]) * .1, p[1] + (prv[1] - p[1]) * .1))
            out.append((p[0] + (nxt[0] - p[0]) * .1, p[1] + (nxt[1] - p[1]) * .1))
        else:
            out.append(p)
    return out


def brilliant_side(cols, ink):
    """Classic diamond profile: crown + pavilion."""
    tl, tr, gl, gr, cy_, gy, ty = 88, 168, 40, 216, 216, 104, 56
    out = [f'<polygon points="{tl},{ty} {tr},{ty} {gr},{gy} 128,{cy_} {gl},{gy}" fill="{ink}" stroke="{ink}" stroke-width="14" stroke-linejoin="round" filter="url(#drop)"/>']
    # crown points along table and girdle
    table = [(tl + (tr - tl) * i / 4, ty) for i in range(5)]
    gird = [(gl + (gr - gl) * i / 6, gy) for i in range(7)]
    crown = []
    # crown facets: triangles alternating between table and girdle
    crown_mid = [(gl + 10, ty + 24), (gl + (gr - gl) * .25, gy - 14), (128, gy - 18), (gl + (gr - gl) * .75, gy - 14), (gr - 10, ty + 24)]
    polys = [
        ([(tl, ty), (gl, gy), (gl + (gr - gl) / 6, gy)], .9), ([(tl, ty), table[1], gird[1]], .8), ([table[1], gird[1], gird[2]], .65),
        ([table[1], table[2], gird[2]], .95), ([table[2], gird[2], gird[3]], .7), ([table[2], gird[3], gird[4]], .55),
        ([table[2], table[3], gird[4]], .75), ([table[3], gird[4], gird[5]], .45), ([table[3], tr], .0),
        ([table[3], (tr, ty), gird[5]], .6), ([(tr, ty), gird[5], gird[6]], .35),
    ]
    for poly, t in polys:
        if len(poly) < 3:
            continue
        crown.append(f'<polygon points="{pts_s(poly)}" fill="{ramp(cols, t)}" stroke="{ink}" stroke-opacity=".35" stroke-width="1.5" stroke-linejoin="round"/>')
    pav = []
    culet = (128, cy_)
    shades = [.8, .55, .9, .35, .6, .2]
    for i in range(6):
        poly = [gird[i], gird[i + 1], culet]
        pav.append(f'<polygon points="{pts_s(poly)}" fill="{ramp(cols, shades[i])}" stroke="{ink}" stroke-opacity=".35" stroke-width="1.5" stroke-linejoin="round"/>')
    # inner pavilion kites
    for i in range(1, 6, 2):
        mid = ((gird[i][0] + culet[0]) / 2, (gird[i][1] + culet[1]) / 2 - 6)
        poly = [gird[i - 1], gird[i], gird[i + 1], mid]
        pav.append(f'<polygon points="{pts_s(poly)}" fill="{ramp(cols, shades[i - 1] + .15)}" opacity=".7"/>')
    outline = f'<polygon points="{tl},{ty} {tr},{ty} {gr},{gy} 128,{cy_} {gl},{gy}" fill="none" stroke="{ink}" stroke-width="6" stroke-linejoin="round"/>'
    girdle = f'<line x1="{gl}" y1="{gy}" x2="{gr}" y2="{gy}" stroke="{ink}" stroke-width="3"/>'
    return "".join(out + crown + pav) + girdle + outline


# ---------------------------------------------------------------- palettes (dark -> light -> highlight)
CYAN = ["#04425A", "#0A8AB0", "#2AD0F0", "#9AF4FF", "#FFFFFF"]
GREEN = ["#033A1A", "#0A8A3A", "#2AD060", "#A8FFB8", "#FFFFFF"]
RED = ["#4A020E", "#A80A24", "#F0284A", "#FFA0B0", "#FFFFFF"]
VIOLET = ["#24064A", "#5A1AA8", "#9A4AF0", "#DAB0FF", "#FFFFFF"]
WHITE = ["#6A7A9A", "#A8B8D8", "#DCE6F8", "#F4F8FF", "#FFFFFF"]
YELLOW = ["#6A3A02", "#C88A06", "#FFC820", "#FFF09A", "#FFFFFF"]

SPK = lambda pts: "".join(sparkle(x, y, s) for x, y, s in pts)

gemc = gem_symbol(pear_outline(), (128, 142), CYAN, "#022030", "#6AE8FF",
                  shine_streak(104, 112, 16, 7, -60) + SPK([(184, 70, 1.1), (66, 186, .8), (150, 196, .6)]), rings=(.7, .42), table_dy=-6)
gemg = gem_symbol(emerald_outline(), (128, 128), GREEN, "#02240E", "#5AF08A",
                  shine_streak(86, 90, 18, 6, -35) + SPK([(206, 56, 1.1), (52, 200, .8)]), rings=(.8, .6), star_lines=False)
gemr = gem_symbol(heart_outline(), (128, 120), RED, "#2A0008", "#FF5A7A",
                  shine_streak(90, 86, 18, 8, -40) + SPK([(200, 52, 1.1), (60, 190, .7), (176, 176, .6)]), rings=(.7, .4))
gemv = gem_symbol(poly_ellipse(128, 128, 70, 92, 18, 90), (128, 128), VIOLET, "#140230", "#C07AFF",
                  shine_streak(104, 84, 16, 7, -55) + SPK([(196, 58, 1.1), (66, 196, .8)]), rings=(.72, .44))
gemw = svg(backdrop("#A8C8F0", "#FFFFFF") + brilliant_side(WHITE, "#1A2A4A")
           + shine_streak(104, 78, 14, 5, -10) + SPK([(206, 56, 1.2), (52, 70, .8), (176, 180, .7), (128, 104, .6)]),
           P("#FFFFFF", "#DCE6F8", "#6A7A9A", "#1A2A4A"))
gemy = gem_symbol(trillion_outline(), (128, 140), YELLOW, "#3A1C00", "#FFE060",
                  shine_streak(110, 92, 14, 6, -60) + SPK([(200, 70, 1.1), (60, 92, .7), (128, 214, .6)]), rings=(.66, .36), table_dy=4)


# ---------------------------------------------------------------- chests
def chest(metal, metal_dark, wood, wood_dark, ink, contents, glow):
    d = (f'<defs><linearGradient id="cM" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#FFFFFF"/><stop offset=".3" stop-color="{metal}"/><stop offset=".8" stop-color="{metal_dark}"/><stop offset="1" stop-color="{metal}"/></linearGradient>'
         f'<linearGradient id="cW" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="{wood}"/><stop offset="1" stop-color="{wood_dark}"/></linearGradient>'
         f'<radialGradient id="cGl"><stop offset="0" stop-color="{glow}" stop-opacity=".95"/><stop offset="1" stop-color="{glow}" stop-opacity="0"/></radialGradient></defs>')
    lid = (f'<path d="M46,124 L58,64 C80,40 176,40 198,64 L210,124Z" fill="url(#cW)" stroke="{ink}" stroke-width="7" stroke-linejoin="round"/>'
           f'<path d="M62,118 L70,74 C90,58 166,58 186,74 L194,118Z" fill="{wood_dark}" stroke="{ink}" stroke-width="4" stroke-linejoin="round"/>'
           f'<path d="M58,64 C80,40 176,40 198,64 L194,76 C170,56 86,56 62,76Z" fill="url(#cM)" stroke="{ink}" stroke-width="4" stroke-linejoin="round"/>'
           f'<path d="M76,50 L70,122 M180,50 L186,122" stroke="url(#cM)" stroke-width="12"/>'
           f'<path d="M76,50 L70,122 M180,50 L186,122" stroke="{ink}" stroke-width="2" opacity=".5"/>')
    glowc = '<ellipse cx="128" cy="108" rx="84" ry="40" fill="url(#cGl)"/>'
    body = (f'<path d="M40,130 L216,130 L208,210 L48,210Z" fill="url(#cW)" stroke="{ink}" stroke-width="7" stroke-linejoin="round"/>'
            f'<path d="M50,150 L206,150 M52,174 L204,174 M54,196 L202,196" stroke="{wood_dark}" stroke-width="3" opacity=".7"/>'
            f'<path d="M36,124 L220,124 L218,144 L38,144Z" fill="url(#cM)" stroke="{ink}" stroke-width="5" stroke-linejoin="round"/>'
            f'<path d="M50,210 L48,144 L74,144 L74,210Z" fill="url(#cM)" stroke="{ink}" stroke-width="4" stroke-linejoin="round"/>'
            f'<path d="M206,210 L208,144 L182,144 L182,210Z" fill="url(#cM)" stroke="{ink}" stroke-width="4" stroke-linejoin="round"/>'
            f'<path d="M48,206 L208,206 L206,216 L50,216Z" fill="url(#cM)" stroke="{ink}" stroke-width="4" stroke-linejoin="round"/>'
            f'<path d="M108,140 L148,140 L148,170 C148,184 108,184 108,170Z" fill="url(#cM)" stroke="{ink}" stroke-width="5" stroke-linejoin="round"/>'
            f'<circle cx="128" cy="158" r="6" fill="{ink}"/><path d="M125,160 L131,160 L132,172 L124,172Z" fill="{ink}"/>'
            f'<circle cx="61" cy="160" r="4" fill="{ink}" opacity=".6"/><circle cx="61" cy="194" r="4" fill="{ink}" opacity=".6"/>'
            f'<circle cx="195" cy="160" r="4" fill="{ink}" opacity=".6"/><circle cx="195" cy="194" r="4" fill="{ink}" opacity=".6"/>'
            f'<path d="M44,128 L210,128" stroke="#fff" stroke-width="3" opacity=".7"/>')
    return d + backdrop(metal, glow) + f'<g filter="url(#drop)">{lid}</g>' + glowc + contents + f'<g filter="url(#drop)">{body}</g>'


def silver_coin(cx, cy, r):
    return (f'<circle cx="{cx}" cy="{cy}" r="{r}" fill="#4A5670"/><circle cx="{cx}" cy="{cy - r * .05:.1f}" r="{r * .93:.1f}" fill="url(#silver)"/>'
            f'<circle cx="{cx}" cy="{cy}" r="{r * .68:.1f}" fill="none" stroke="#7F8BA6" stroke-width="{r * .1:.1f}"/>'
            f'<ellipse cx="{cx - r * .3:.1f}" cy="{cy - r * .4:.1f}" rx="{r * .35:.1f}" ry="{r * .18:.1f}" fill="#fff" opacity=".7"/>')


def pearl(cx, cy, r):
    return (f'<circle cx="{cx}" cy="{cy}" r="{r}" fill="#F4F0FF" stroke="#3A3A5A" stroke-width="3"/>'
            f'<circle cx="{cx - r * .3:.1f}" cy="{cy - r * .3:.1f}" r="{r * .35:.1f}" fill="#fff"/>')


gold_contents = (
    coin(70, 120, 18) + coin(186, 118, 18) + coin(96, 106, 20) + coin(160, 104, 20) + coin(128, 96, 22)
    + gem(106, 120, 16, "#FFA0B0", "#F0284A", "#A80A24") + gem(152, 124, 14, "#9AF4FF", "#2AD0F0", "#0A8AB0")
    + coin(128, 124, 16) + sparkle(128, 70, 1.1) + sparkle(190, 86, .7) + sparkle(64, 92, .6)
)
silver_contents = (
    silver_coin(70, 120, 18) + silver_coin(186, 118, 18) + silver_coin(98, 106, 20) + silver_coin(158, 104, 20)
    + gem(128, 96, 22, "#DAB0FF", "#9A4AF0", "#5A1AA8")
    + pearl(84, 126, 9) + pearl(100, 130, 9) + pearl(116, 132, 9) + pearl(140, 132, 9) + pearl(156, 130, 9) + pearl(172, 126, 9)
    + gem(128, 124, 14, "#9AF4FF", "#2AD0F0", "#0A8AB0") + sparkle(128, 64, 1.1) + sparkle(192, 88, .7) + sparkle(62, 94, .6)
)

chestgold = svg(chest("#FFD54A", "#C07800", "#B0703A", "#5A2E10", "#2A1004", gold_contents, "#FFE07A"),
                P("#FFF4B0", "#FFC83A", "#B06A00", "#2A1004"))
chestsilver = svg(chest("#D0DAEA", "#6A7894", "#5A6A8A", "#26304A", "#0E1426", silver_contents, "#C8E0FF"),
                  P("#F4F8FF", "#B8C8E0", "#4A5878", "#0E1426"))

crown = (
    '<path d="M70,148 L60,74 L96,108 L128,58 L160,108 L196,74 L186,148Z" fill="url(#gold)" stroke="#5A2A00" stroke-width="7" stroke-linejoin="round"/>'
    '<path d="M76,140 L70,92 L96,118 L128,74 L128,140Z" fill="#fff" opacity=".25"/>'
    '<rect x="66" y="138" width="124" height="24" rx="6" fill="url(#gold)" stroke="#5A2A00" stroke-width="6"/>'
    '<circle cx="60" cy="72" r="9" fill="url(#gold)" stroke="#5A2A00" stroke-width="4"/><circle cx="196" cy="72" r="9" fill="url(#gold)" stroke="#5A2A00" stroke-width="4"/>'
    '<circle cx="128" cy="54" r="10" fill="url(#gold)" stroke="#5A2A00" stroke-width="4"/>'
    + gem(128, 110, 18, "#FFA0B0", "#F0284A", "#A80A24") + gem(92, 150, 9, "#9AF4FF", "#2AD0F0", "#0A8AB0")
    + gem(128, 150, 10, "#A8FFB8", "#2AD060", "#0A8A3A") + gem(164, 150, 9, "#DAB0FF", "#9A4AF0", "#5A1AA8")
    + sparkle(176, 60, .8)
)

SYMBOLS = {
    "chestgold": chestgold,
    "chestsilver": chestsilver,
    "gemc": gemc,
    "gemg": gemg,
    "gemr": gemr,
    "gemv": gemv,
    "gemw": gemw,
    "gemy": gemy,
    "wild": wild(P("#E8C8FF", "#9A4AF0", "#3A0A7A", "#1A0436"), crown),
}
