"""Most Wanted: wild-west wanted poster. Parchment, leather brown, rusty red."""
import math

from symkit import P, medallion, outlined_text, scatter, sparkle, star, star_pts, wild

INK = "#2A1206"
PARCH = P("#FFF4D6", "#E0BE82", "#8A5E2A", INK)
LEATHER = P("#F0C08A", "#9A5424", "#3A1806", INK)
RUST = P("#FFB896", "#B8442A", "#4A1206", INK)
SAGE = P("#E4EDC6", "#8AA060", "#2E3E1A", INK)
SLATE = P("#D6E2EE", "#5A7894", "#1A2A3E", INK)
WINE = P("#F0A8A0", "#8A2A2A", "#3A0808", INK)
TAN = P("#FFE6B0", "#C88A3A", "#5A3006", INK)
DUSK = P("#E8C8F0", "#7A4A8A", "#2A1238", INK)

SKIN = "#E9B27C"
SKIN_D = "#C27E4A"


def grads(*items):
    return "<defs>" + "".join(items) + "</defs>"


def lin(id_, stops, x2=0, y2=1):
    s = "".join(f'<stop offset="{o}" stop-color="{c}"/>' for o, c in stops)
    return f'<linearGradient id="{id_}" x1="0" y1="0" x2="{x2}" y2="{y2}">{s}</linearGradient>'


# ------------------------------------------------------------------ bag
bag = (
    grads(lin("burlap", [(0, "#F2D49A"), (.5, "#C9974E"), (1, "#7A4E1E")], 1, 1))
    + '<path d="M92,106 C62,130 54,170 70,190 C86,206 170,206 186,190 C202,170 194,130 164,106Z" fill="url(#burlap)" stroke="#2A1206" stroke-width="7" stroke-linejoin="round"/>'
    '<path d="M92,106 C66,130 60,168 74,186 C84,196 98,198 108,198 C88,180 84,140 104,108Z" fill="#FFE8B8" opacity=".35"/>'
    '<path d="M100,106 C96,90 88,72 100,60 L116,74 L128,56 L140,74 L156,60 C168,72 160,90 156,106Z" fill="url(#burlap)" stroke="#2A1206" stroke-width="6" stroke-linejoin="round"/>'
    '<path d="M90,104 C110,114 146,114 166,104 L168,116 C146,126 110,126 88,116Z" fill="#8A2A1A" stroke="#2A1206" stroke-width="5" stroke-linejoin="round"/>'
    '<path d="M150,116 C160,130 166,138 176,140 M150,116 C152,132 150,142 156,150" stroke="#8A2A1A" stroke-width="6" fill="none" stroke-linecap="round"/>'
    '<path d="M150,116 C160,130 166,138 176,140 M150,116 C152,132 150,142 156,150" stroke="#2A1206" stroke-width="1.5" fill="none"/>'
    + outlined_text("$", 126, 186, 80, "#5FD06A", "#123A12", 13)
    + '<path d="M78,150 L84,156 M178,160 L172,166 M96,130 L100,136" stroke="#7A4E1E" stroke-width="3" stroke-linecap="round"/>'
    + sparkle(176, 92, .8)
)


# ------------------------------------------------------------------ bandit 1: bandana mask
def hat(fill, band, cy=96, w=74):
    return (f'<path d="M86,{cy} C84,{cy - 40} 96,{cy - 52} 112,{cy - 48} C120,{cy - 46} 124,{cy - 40} 128,{cy - 40} C132,{cy - 40} 136,{cy - 46} 144,{cy - 48} C160,{cy - 52} 172,{cy - 40} 170,{cy}Z" fill="{fill}" stroke="{INK}" stroke-width="6" stroke-linejoin="round"/>'
            f'<path d="M96,{cy - 4} C94,{cy - 34} 102,{cy - 44} 112,{cy - 42}" stroke="#fff" stroke-width="5" fill="none" opacity=".25" stroke-linecap="round"/>'
            f'<path d="M87,{cy - 14} C110,{cy - 8} 146,{cy - 8} 169,{cy - 14} L170,{cy - 2} C146,{cy + 4} 110,{cy + 4} 86,{cy - 2}Z" fill="{band}" stroke="{INK}" stroke-width="4" stroke-linejoin="round"/>'
            f'<path d="M{128 - w},{cy + 4} C{128 - w + 20},{cy - 14} {128 + w - 20},{cy - 14} {128 + w},{cy + 4} C{128 + w - 14},{cy + 18} {128 - w + 14},{cy + 18} {128 - w},{cy + 4}Z" fill="{fill}" stroke="{INK}" stroke-width="6" stroke-linejoin="round"/>'
            f'<path d="M{128 - w + 14},{cy + 2} C{128 - w + 34},{cy - 8} {128 + w - 34},{cy - 8} {128 + w - 14},{cy + 2}" stroke="#fff" stroke-width="3" fill="none" opacity=".25"/>')


def face(cy=142, rx=42, ry=50, skin=SKIN):
    return (f'<circle cx="{128 - rx}" cy="{cy - 4}" r="11" fill="{skin}" stroke="{INK}" stroke-width="5"/>'
            f'<circle cx="{128 + rx}" cy="{cy - 4}" r="11" fill="{skin}" stroke="{INK}" stroke-width="5"/>'
            f'<ellipse cx="128" cy="{cy}" rx="{rx}" ry="{ry}" fill="{skin}" stroke="{INK}" stroke-width="6"/>'
            f'<ellipse cx="114" cy="{cy - 22}" rx="16" ry="10" fill="#fff" opacity=".25"/>')


bandana_dots = "".join(f'<circle cx="{x}" cy="{y}" r="3.2" fill="#FFF2DC"/>' for x, y in
                       [(102, 156), (118, 166), (138, 166), (154, 156), (128, 182), (112, 180), (144, 180), (128, 156), (96, 170), (160, 170)])
bandit1 = (
    face(144)
    + '<path d="M100,124 L120,130 M156,124 L136,130" stroke="#2A1206" stroke-width="6" stroke-linecap="round"/>'
    '<ellipse cx="111" cy="137" rx="9" ry="6" fill="#fff" stroke="#2A1206" stroke-width="3"/><ellipse cx="145" cy="137" rx="9" ry="6" fill="#fff" stroke="#2A1206" stroke-width="3"/>'
    '<circle cx="113" cy="137" r="4" fill="#2A1206"/><circle cx="143" cy="137" r="4" fill="#2A1206"/>'
    '<path d="M80,146 C100,154 156,154 176,146 L172,172 C164,190 146,200 128,204 C110,200 92,190 84,172Z" fill="#C8302A" stroke="#2A1206" stroke-width="6" stroke-linejoin="round"/>'
    '<path d="M86,150 C100,156 120,158 128,158 L128,200 C110,196 96,186 88,172Z" fill="#FF7A6A" opacity=".35"/>'
    + bandana_dots
    + '<path d="M172,150 L196,144 L190,160Z M172,154 L192,172 L176,174Z" fill="#C8302A" stroke="#2A1206" stroke-width="4" stroke-linejoin="round"/>'
    + hat("#7A4A22", "#2A1206", 104, 76)
)

# ------------------------------------------------------------------ bandit 2: black hat + mustache
bandit2 = (
    face(146, 44, 50, "#E2A472")
    + '<path d="M90,170 C96,196 160,196 166,170 C160,188 96,188 90,170Z" fill="#6A4A3A" opacity=".35"/>'
    + "".join(f'<circle cx="{x}" cy="{y}" r="1.8" fill="#5A3A2A"/>' for x, y in
              [(104, 182), (112, 188), (122, 190), (134, 190), (144, 188), (152, 182), (98, 174), (158, 174)])
    + '<path d="M98,126 L120,122 M136,122 L158,126" stroke="#2A1206" stroke-width="7" stroke-linecap="round"/>'
    '<path d="M104,136 C108,132 116,132 120,136" stroke="#2A1206" stroke-width="5" fill="none" stroke-linecap="round"/>'
    '<ellipse cx="146" cy="137" rx="8" ry="6" fill="#fff" stroke="#2A1206" stroke-width="3"/><circle cx="145" cy="137" r="4" fill="#2A1206"/>'
    '<path d="M146,118 L156,150" stroke="#9A4A3A" stroke-width="3" stroke-linecap="round"/>'
    '<path d="M122,138 C120,150 116,156 120,160 C124,162 132,162 136,160 C140,156 134,150 132,138" fill="#C27E4A" stroke="#2A1206" stroke-width="4" stroke-linejoin="round"/>'
    '<path d="M128,162 C118,158 104,160 94,170 C86,178 74,176 70,166 C74,184 92,188 106,180 C116,176 122,172 128,172 C134,172 140,176 150,180 C164,188 182,184 186,166 C182,176 170,178 162,170 C152,160 138,158 128,162Z" fill="#4A2A16" stroke="#1A0A04" stroke-width="5" stroke-linejoin="round"/>'
    '<path d="M110,166 C102,168 96,172 90,174" stroke="#8A5A3A" stroke-width="3" fill="none" stroke-linecap="round"/>'
    + hat("#2A2A32", "url(#silver)", 106, 80)
    + star(128, 92, 7, "url(#silver)", INK, 2)
)

# ------------------------------------------------------------------ sheriff badge (cuper)
tips = "".join(
    f'<circle cx="{128 + 76 * math.cos(math.radians(-90 + 60 * i)):.1f}" cy="{128 + 76 * math.sin(math.radians(-90 + 60 * i)):.1f}" r="10" fill="url(#gold)" stroke="#5A2A00" stroke-width="5"/>'
    for i in range(6))
cuper = (
    f'<polygon points="{star_pts(128, 128, 74, 6, .56)}" fill="#5A2A00" stroke="#5A2A00" stroke-width="12" stroke-linejoin="round"/>'
    + tips
    + f'<polygon points="{star_pts(128, 128, 74, 6, .56)}" fill="url(#gold)" stroke="#5A2A00" stroke-width="4" stroke-linejoin="round"/>'
    + f'<polygon points="{star_pts(128, 128, 60, 6, .56)}" fill="none" stroke="#B86B00" stroke-width="3" stroke-linejoin="round"/>'
    '<circle cx="128" cy="128" r="28" fill="url(#goldH)" stroke="#5A2A00" stroke-width="4"/>'
    + star(128, 129, 18, "#FFF2B0", "#8A4600", 3)
    + '<path d="M96,92 L112,104 L104,82Z" fill="#fff" opacity=".55"/>'
    + sparkle(178, 80, .9)
)


# ------------------------------------------------------------------ dice
PIPS = {1: [(.5, .5)], 2: [(.27, .27), (.73, .73)], 3: [(.27, .27), (.5, .5), (.73, .73)],
        4: [(.27, .27), (.73, .27), (.27, .73), (.73, .73)],
        5: [(.27, .27), (.73, .27), (.5, .5), (.27, .73), (.73, .73)],
        6: [(.27, .25), (.73, .25), (.27, .5), (.73, .5), (.27, .75), (.73, .75)]}


def pips(n, m, col):
    return f'<g transform="matrix({m})">' + "".join(f'<circle cx="{u}" cy="{v}" r=".1" fill="{col}"/>' for u, v in PIPS[n]) + '</g>'


def die(x, y, s, body, side, top, pipc, nf, nt, ns):
    d = s * .3
    front = f'M{x},{y} L{x + s},{y} L{x + s},{y + s} L{x},{y + s}Z'
    topf = f'M{x},{y} L{x + d},{y - d} L{x + s + d},{y - d} L{x + s},{y}Z'
    sidef = f'M{x + s},{y} L{x + s + d},{y - d} L{x + s + d},{y + s - d} L{x + s},{y + s}Z'
    out = f'<path d="M{x},{y} L{x + d},{y - d} L{x + s + d},{y - d} L{x + s + d},{y + s - d} L{x + s},{y + s} L{x},{y + s}Z" fill="{INK}" stroke="{INK}" stroke-width="12" stroke-linejoin="round"/>'
    out += f'<path d="{front}" fill="{body}"/><path d="{topf}" fill="{top}"/><path d="{sidef}" fill="{side}"/>'
    out += pips(nf, f"{s},0,0,{s},{x},{y}", pipc) + pips(nt, f"{s},0,{d},{-d},{x},{y + 0.001}", pipc).replace(f"{x},{y + 0.001}", f"{x},{y}")
    out += pips(ns, f"{d},{-d},0,{s},{x + s},{y}", pipc)
    out += f'<path d="{front}" fill="none" stroke="{INK}" stroke-width="3"/><path d="{topf}" fill="none" stroke="{INK}" stroke-width="3" stroke-linejoin="round"/>'
    out += f'<rect x="{x + 6}" y="{y + 6}" width="{s * .3:.1f}" height="{s * .12:.1f}" rx="4" fill="#fff" opacity=".45"/>'
    return out


# top-face pips: unit square (u,v) maps to (x,y)+u*(s,0)+v*(d,-d); keep v in [0,1]
dice = (
    grads(lin("dRed", [(0, "#FF6A5A"), (1, "#A8201A")], 1, 1), lin("dIvo", [(0, "#FFFFFF"), (1, "#E8D8B8")], 1, 1))
    + die(120, 82, 60, "url(#dIvo)", "#C8B48A", "#FFFFFF", "#2A1206", 5, 2, 3)
    + die(56, 118, 76, "url(#dRed)", "#7A1410", "#FF8A7A", "#FFF6E6", 6, 3, 1)
    + sparkle(188, 162, .8)
)


# ------------------------------------------------------------------ gold bars
def bar(x, y, w=68):
    return (f'<path d="M{x},{y} L{x + w},{y} L{x + w - 8},{y - 26} L{x + 8},{y - 26}Z" fill="url(#barF)" stroke="#5A2A00" stroke-width="5" stroke-linejoin="round"/>'
            f'<path d="M{x + 8},{y - 26} L{x + w - 8},{y - 26} L{x + w - 18},{y - 40} L{x + 18},{y - 40}Z" fill="url(#barT)" stroke="#5A2A00" stroke-width="5" stroke-linejoin="round"/>'
            f'<rect x="{x + w / 2 - 14}" y="{y - 19}" width="28" height="12" rx="3" fill="none" stroke="#B86B00" stroke-width="2.5"/>'
            f'<path d="M{x + 12},{y - 5} L{x + 20},{y - 22}" stroke="#fff" stroke-width="4" opacity=".6" stroke-linecap="round"/>')


gold = (
    grads(lin("barF", [(0, "#FFE680"), (.6, "#F0A818"), (1, "#B86800")]), lin("barT", [(0, "#FFFBD8"), (1, "#FFD24A")]))
    + '<g transform="translate(0 10)">'
    + bar(54, 190) + bar(134, 190) + bar(94, 150) + '</g>'
    + sparkle(166, 92, 1.1) + sparkle(78, 120, .7)
)


# ------------------------------------------------------------------ silver coins
def stack(cx, base, n, rx=34, ry=11):
    out = ""
    for i in range(n):
        y = base - i * 12
        out += (f'<ellipse cx="{cx}" cy="{y + 6}" rx="{rx}" ry="{ry}" fill="#6A7690" stroke="{INK}" stroke-width="4"/>'
                f'<ellipse cx="{cx}" cy="{y}" rx="{rx}" ry="{ry}" fill="url(#coinT)" stroke="{INK}" stroke-width="4"/>')
    top = base - (n - 1) * 12
    out += f'<ellipse cx="{cx}" cy="{top}" rx="{rx * .68:.1f}" ry="{ry * .6:.1f}" fill="none" stroke="#7F8BA6" stroke-width="2.5"/>'
    return out


silver = (
    grads(lin("coinT", [(0, "#FFFFFF"), (1, "#AEB8CC")]), lin("coinF", [(0, "#FFFFFF"), (.5, "#C9D3E6"), (1, "#6F7B96")], 1, 1))
    + stack(92, 190, 6) + stack(162, 190, 3)
    + '<circle cx="158" cy="118" r="38" fill="#4A5670" stroke="#2A1206" stroke-width="6"/>'
    '<circle cx="155" cy="116" r="34" fill="url(#coinF)"/>'
    '<circle cx="155" cy="116" r="25" fill="none" stroke="#6F7B96" stroke-width="3.5"/>'
    + star(155, 117, 15, "#FFFFFF", "#4A5670", 3)
    + '<ellipse cx="143" cy="100" rx="11" ry="6" fill="#fff" opacity=".7" transform="rotate(-30 143 100)"/>'
    + sparkle(84, 98, .9)
)

# ------------------------------------------------------------------ whiskey
whiskey = (
    grads(lin("glass", [(0, "#8A3A0A"), (.35, "#E08A2A"), (.6, "#C0661A"), (1, "#5A2206")], 1, 0),
          lin("shot", [(0, "#FFC060"), (1, "#B8601A")]))
    + '<g transform="translate(-14 0)">'
    '<rect x="115" y="44" width="26" height="22" rx="5" fill="#B07A3A" stroke="#2A1206" stroke-width="5"/>'
    '<path d="M118,62 L118,92 C118,108 94,110 94,128 L94,188 C94,200 162,200 162,188 L162,128 C162,110 138,108 138,92 L138,62Z" fill="url(#glass)" stroke="#2A1206" stroke-width="6" stroke-linejoin="round"/>'
    '<rect x="116" y="64" width="24" height="10" fill="#C8302A" stroke="#2A1206" stroke-width="3"/>'
    '<path d="M102,126 C102,116 112,112 118,108 L118,190 L104,190 C102,190 102,188 102,186Z" fill="#fff" opacity=".22"/>'
    '<rect x="98" y="136" width="60" height="40" rx="4" fill="#FFF0CC" stroke="#2A1206" stroke-width="4"/>'
    '<rect x="102" y="140" width="52" height="32" rx="2" fill="none" stroke="#B8442A" stroke-width="2"/>'
    + outlined_text("XXX", 128, 166, 24, "#C8302A", "#2A1206", 0)
    + '<path d="M124,70 L124,96" stroke="#fff" stroke-width="4" opacity=".4" stroke-linecap="round"/></g>'
    '<path d="M150,150 L196,150 L190,198 L156,198Z" fill="#E8F2F8" fill-opacity=".55" stroke="#2A1206" stroke-width="5" stroke-linejoin="round"/>'
    '<path d="M153,170 L193,170 L190,194 L156,194Z" fill="url(#shot)"/>'
    '<path d="M156,154 L160,192" stroke="#fff" stroke-width="4" opacity=".7" stroke-linecap="round"/>'
    '<path d="M150,150 L196,150 L190,198 L156,198Z" fill="none" stroke="#2A1206" stroke-width="5" stroke-linejoin="round"/>'
)

# ------------------------------------------------------------------ wild: longhorn skull
skull = (
    '<path d="M104,74 C80,78 54,72 34,44 C40,82 70,98 106,94Z" fill="#F4E4C0" stroke="#2A1206" stroke-width="6" stroke-linejoin="round"/>'
    '<path d="M152,74 C176,78 202,72 222,44 C216,82 186,98 150,94Z" fill="#F4E4C0" stroke="#2A1206" stroke-width="6" stroke-linejoin="round"/>'
    '<path d="M60,70 L66,78 M74,78 L78,86 M196,70 L190,78 M182,78 L178,86" stroke="#B89A6A" stroke-width="3" stroke-linecap="round"/>'
    '<path d="M98,64 C100,50 156,50 158,64 L160,98 C160,112 148,120 146,142 C144,158 112,158 110,142 C108,120 96,112 96,98Z" fill="#FFF6E0" stroke="#2A1206" stroke-width="6" stroke-linejoin="round"/>'
    '<path d="M104,66 C108,58 124,56 128,58 L118,100 C110,96 104,88 104,80Z" fill="#fff" opacity=".6"/>'
    '<path d="M104,88 C108,80 122,82 124,94 C120,104 106,102 104,88Z" fill="#2A1206"/>'
    '<path d="M152,88 C148,80 134,82 132,94 C136,104 150,102 152,88Z" fill="#2A1206"/>'
    '<ellipse cx="121" cy="140" rx="4" ry="7" fill="#2A1206"/><ellipse cx="135" cy="140" rx="4" ry="7" fill="#2A1206"/>'
    '<path d="M128,62 L128,76 M122,112 L134,112" stroke="#C8A878" stroke-width="3" stroke-linecap="round"/>'
    + star(128, 34, 12, "url(#gold)", "#5A2A00", 3)
)

# ------------------------------------------------------------------ scatter: lucky horseshoe
hs = "M92,64 C70,108 82,160 128,164 C174,160 186,108 164,64"
nails = "".join(f'<rect x="{x - 3}" y="{y - 5}" width="6" height="10" rx="2" fill="#2A1206" transform="rotate({r} {x} {y})"/>'
                for x, y, r in [(88, 92, 15), (90, 124, 35), (106, 150, 65), (168, 92, -15), (166, 124, -35), (150, 150, -65)])
horseshoe = (
    f'<path d="{hs}" fill="none" stroke="#2A1206" stroke-width="40" stroke-linecap="round"/>'
    f'<path d="{hs}" fill="none" stroke="url(#gold)" stroke-width="28" stroke-linecap="round"/>'
    f'<path d="M88,72 C72,110 84,152 122,156" fill="none" stroke="#fff" stroke-width="5" opacity=".6" stroke-linecap="round"/>'
    + nails
    + sparkle(128, 96, 1.2) + sparkle(186, 60, .7) + sparkle(70, 160, .6)
)

SYMBOLS = {
    "bag": medallion("square", SAGE, bag),
    "bandit1": medallion("shield", RUST, bandit1),
    "bandit2": medallion("circle", SLATE, bandit2),
    "cuper": medallion("circle", LEATHER, cuper),
    "dice": medallion("square", DUSK, dice),
    "gold": medallion("hex", WINE, gold),
    "silver": medallion("hex", PARCH, silver),
    "whiskey": medallion("shield", TAN, whiskey),
    "wild": wild(RUST, skull),
    "scatter": scatter(LEATHER, horseshoe, "BONUS"),
}
