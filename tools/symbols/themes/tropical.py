"""Tropical: sunny beach bar. Turquoise, coral, lime, sunshine yellow."""
from symkit import P, medallion, scatter, sparkle, star_pts, svg, wild

INK = "#2A1430"
TURQ = P("#C8FFF6", "#22C8C0", "#06505A", INK)
CORAL = P("#FFD8C8", "#FF6E5A", "#8A1A2A", INK)
LIME = P("#F0FFB8", "#8EDC2E", "#2E5A0A", INK)
SUN = P("#FFF6B8", "#FFC23A", "#A85A06", INK)
SKY = P("#D8F6FF", "#38B8F0", "#0A4A8A", INK)


def lin(id_, stops, x2=0, y2=1):
    s = "".join(f'<stop offset="{o}" stop-color="{c}"/>' for o, c in stops)
    return f'<linearGradient id="{id_}" x1="0" y1="0" x2="{x2}" y2="{y2}">{s}</linearGradient>'


def rad(id_, stops, cx=.38, cy=.32, r=.75):
    s = "".join(f'<stop offset="{o}" stop-color="{c}"/>' for o, c in stops)
    return f'<radialGradient id="{id_}" cx="{cx}" cy="{cy}" r="{r}">{s}</radialGradient>'


LEAF = lin("leaf", [(0, "#C8FF7A"), (.5, "#3AB84A"), (1, "#0B5A2A")], 1, 1)


def defs(*items):
    return "<defs>" + "".join(items) + "</defs>"


def leaf(x, y, ang, L, w, fill="url(#leaf)", sw=5):
    return (f'<path transform="translate({x} {y}) rotate({ang})" d="M0,0 C{w},{-L * .35:.1f} {w * .5:.1f},{-L * .8:.1f} 0,{-L} C{-w * .5:.1f},{-L * .8:.1f} {-w},{-L * .35:.1f} 0,0Z" '
            f'fill="{fill}" stroke="{INK}" stroke-width="{sw}" stroke-linejoin="round"/>'
            f'<path transform="translate({x} {y}) rotate({ang})" d="M0,-4 L0,{-L * .8:.1f}" stroke="#0B5A2A" stroke-width="2.5" opacity=".6"/>')


def umbrella(cx, cy, r, rot, c1, c2):
    segs = ""
    xs = [-r, -r * .5, 0, r * .5, r]
    for i in range(4):
        col = c1 if i % 2 == 0 else c2
        segs += f'<path d="M0,{-r * .7:.1f} L{xs[i]:.1f},0 Q{(xs[i] + xs[i + 1]) / 2:.1f},{-r * .12:.1f} {xs[i + 1]:.1f},0Z" fill="{col}"/>'
    return (f'<g transform="translate({cx} {cy}) rotate({rot})">'
            f'<path d="M0,{-r * .6:.1f} L0,{r * 1.5:.1f}" stroke="{INK}" stroke-width="7" stroke-linecap="round"/>'
            f'<path d="M0,{-r * .6:.1f} L0,{r * 1.5:.1f}" stroke="#F4E0B0" stroke-width="3" stroke-linecap="round"/>'
            f'<path d="M{-r},0 C{-r * .9:.1f},{-r * .9:.1f} {r * .9:.1f},{-r * .9:.1f} {r},0Z" fill="{INK}" stroke="{INK}" stroke-width="9" stroke-linejoin="round"/>'
            f'{segs}<path d="M{-r * .7:.1f},{-r * .22:.1f} C{-r * .5:.1f},{-r * .62:.1f} {-r * .1:.1f},{-r * .7:.1f} 0,{-r * .68:.1f}" stroke="#fff" stroke-width="3" fill="none" opacity=".6"/>'
            f'<circle cx="0" cy="{-r * .74:.1f}" r="4" fill="{c2}" stroke="{INK}" stroke-width="2.5"/></g>')


def citrus_wheel(cx, cy, r, rind, flesh, pith="#FFF8E0"):
    segs = "".join(
        f'<path transform="translate({cx} {cy}) rotate({a})" d="M0,0 L{-r * .26:.1f},{-r * .7:.1f} Q0,{-r * .8:.1f} {r * .26:.1f},{-r * .7:.1f}Z" fill="{flesh}"/>'
        for a in range(0, 360, 45))
    return (f'<circle cx="{cx}" cy="{cy}" r="{r}" fill="{rind}" stroke="{INK}" stroke-width="5"/>'
            f'<circle cx="{cx}" cy="{cy}" r="{r * .84:.1f}" fill="{pith}"/>{segs}'
            f'<circle cx="{cx}" cy="{cy}" r="{r * .1:.1f}" fill="{pith}"/>')


def glass(d, extra=""):
    return f'<path d="{d}" fill="#E8FAFF" fill-opacity=".35" stroke="{INK}" stroke-width="6" stroke-linejoin="round"{extra}/>'


# ------------------------------------------------------------------ fruits (standalone)
def fruit(inner, extra_defs=""):
    return svg(f'<g filter="url(#drop)">{inner}</g>', SUN, extra_defs)


pine_cross = "".join(f'<path d="M{x},96 L{x + 140},236" stroke="#9A4A06" stroke-width="4"/>' for x in range(-40, 230, 24)) + \
             "".join(f'<path d="M{x},96 L{x - 140},236" stroke="#9A4A06" stroke-width="4"/>' for x in range(30, 300, 24))
pine_spikes = "".join(f'<path d="M{x - 3},{y + 3} L{x},{y - 4} L{x + 3},{y + 3}" fill="#FFF3A0" stroke="#9A4A06" stroke-width="1.5" stroke-linejoin="round"/>'
                      for y, xs in [(114, range(98, 170, 24)), (126, range(74, 190, 24)), (138, range(98, 170, 24)), (150, range(74, 190, 24)),
                                    (162, range(98, 170, 24)), (174, range(74, 190, 24)), (186, range(98, 170, 24)), (198, range(86, 180, 24)), (210, range(98, 170, 24))]
                      for x in xs)
ananas = fruit(
    leaf(128, 98, -62, 62, 20) + leaf(128, 98, 62, 62, 20)
    + leaf(128, 98, -36, 74, 20) + leaf(128, 98, 36, 74, 20)
    + leaf(128, 98, -15, 80, 19) + leaf(128, 98, 15, 80, 19) + leaf(128, 98, 0, 86, 18)
    + '<ellipse cx="128" cy="156" rx="66" ry="70" fill="url(#pine)" stroke="#2A1430" stroke-width="7"/>'
    '<g clip-path="url(#pineClip)">' + pine_cross + pine_spikes + '</g>'
    '<ellipse cx="98" cy="128" rx="18" ry="32" fill="#fff" opacity=".3" transform="rotate(20 104 128)"/>'
    '<ellipse cx="128" cy="156" rx="66" ry="70" fill="none" stroke="#2A1430" stroke-width="7"/>'
    + sparkle(182, 118, .8),
    LEAF + rad("pine", [(0, "#FFF08A"), (.55, "#F7B030"), (1, "#B8620A")])
    + '<clipPath id="pineClip"><ellipse cx="128" cy="158" rx="63" ry="67"/></clipPath>')

cherry = fruit(
    '<path d="M94,134 C98,98 122,66 148,46 M162,122 C162,90 156,66 148,46" stroke="#2A1430" stroke-width="13" fill="none" stroke-linecap="round"/>'
    '<path d="M94,134 C98,98 122,66 148,46 M162,122 C162,90 156,66 148,46" stroke="#5AA02A" stroke-width="6" fill="none" stroke-linecap="round"/>'
    '<path d="M148,46 C166,22 204,24 218,38 C200,62 168,64 148,46Z" fill="url(#leaf)" stroke="#2A1430" stroke-width="5" stroke-linejoin="round"/>'
    '<path d="M154,46 C174,40 196,38 212,38" stroke="#0B5A2A" stroke-width="2.5" fill="none" opacity=".6"/>'
    '<circle cx="168" cy="160" r="44" fill="url(#chR)" stroke="#2A1430" stroke-width="7"/>'
    '<circle cx="90" cy="172" r="46" fill="url(#chR)" stroke="#2A1430" stroke-width="7"/>'
    '<path d="M86,132 C92,138 98,138 102,132 M160,120 C166,126 172,126 176,120" stroke="#6A0010" stroke-width="4" fill="none" stroke-linecap="round"/>'
    '<ellipse cx="72" cy="152" rx="14" ry="9" fill="#fff" opacity=".75" transform="rotate(-35 72 152)"/>'
    '<ellipse cx="152" cy="140" rx="13" ry="8" fill="#fff" opacity=".75" transform="rotate(-35 152 140)"/>'
    '<circle cx="64" cy="170" r="4" fill="#fff" opacity=".6"/>'
    + sparkle(196, 196, .7),
    LEAF + rad("chR", [(0, "#FF9A9A"), (.5, "#E8142E"), (1, "#6A0014")]))

pores = "".join(f'<circle cx="{x}" cy="{y}" r="2" fill="#C0500A" opacity=".35"/>' for x, y in
                [(90, 150), (110, 176), (140, 190), (170, 170), (186, 140), (160, 120), (100, 120), (132, 150), (150, 200), (80, 180),
                 (116, 204), (180, 190), (196, 166), (70, 140), (124, 120), (150, 150), (104, 100), (176, 104)])
orrange = fruit(
    '<circle cx="128" cy="146" r="80" fill="url(#orR)" stroke="#2A1430" stroke-width="7"/>'
    + pores
    + '<ellipse cx="96" cy="108" rx="26" ry="16" fill="#fff" opacity=".55" transform="rotate(-35 96 108)"/>'
    '<circle cx="78" cy="132" r="5" fill="#fff" opacity=".5"/>'
    '<path d="M120,70 C122,58 130,50 138,46" stroke="#2A1430" stroke-width="11" fill="none" stroke-linecap="round"/>'
    '<path d="M120,70 C122,58 130,50 138,46" stroke="#7A4A1E" stroke-width="5" fill="none" stroke-linecap="round"/>'
    + leaf(130, 60, 62, 72, 24) + leaf(126, 62, -48, 52, 18)
    + '<circle cx="124" cy="68" r="6" fill="#7A9A2A" stroke="#2A1430" stroke-width="3"/>'
    + sparkle(184, 112, .8),
    LEAF + rad("orR", [(0, "#FFE48A"), (.5, "#FF9A1A"), (1, "#C04A08")]))

seeds = "".join(
    f'<ellipse cx="{x}" cy="{y}" rx="3.2" ry="5" fill="#FFE27A" stroke="#8A3A00" stroke-width="1.5"/>'
    for y, xs in [(108, range(82, 190, 22)), (130, range(71, 200, 22)), (152, range(82, 190, 22)), (174, range(93, 170, 22)),
                  (196, range(104, 160, 22))] for x in xs)
strawberry = fruit(
    '<path d="M128,226 C92,214 50,172 50,120 C50,86 84,74 128,78 C172,74 206,86 206,120 C206,172 164,214 128,226Z" fill="url(#stR)" stroke="#2A1430" stroke-width="7" stroke-linejoin="round"/>'
    + seeds
    + '<ellipse cx="88" cy="128" rx="14" ry="26" fill="#fff" opacity=".35" transform="rotate(25 88 128)"/>'
    f'<polygon points="{star_pts(128, 86, 54, 7, .36)}" fill="url(#leaf)" stroke="#2A1430" stroke-width="5" stroke-linejoin="round"/>'
    '<path d="M128,82 C126,64 130,50 140,40" stroke="#2A1430" stroke-width="12" fill="none" stroke-linecap="round"/>'
    '<path d="M128,82 C126,64 130,50 140,40" stroke="#4AA83A" stroke-width="6" fill="none" stroke-linecap="round"/>'
    + sparkle(176, 132, .8),
    LEAF + rad("stR", [(0, "#FF9A8A"), (.5, "#F0203A"), (1, "#7A0418")]))

# ------------------------------------------------------------------ cocktails (in medallions)
blue_drink = (
    defs(lin("bDr", [(0, "#8AF0FF"), (.6, "#1E9BFF"), (1, "#0A3AA8")]))
    + glass("M60,74 L196,74 L128,144Z")
    + '<path d="M72,86 L184,86 L128,138Z" fill="url(#bDr)"/>'
    '<ellipse cx="128" cy="86" rx="56" ry="6" fill="#C8FAFF" opacity=".8"/>'
    + glass("M60,74 L196,74 L128,144Z", ' fill="none"')
    + '<rect x="122" y="142" width="12" height="44" fill="#E8FAFF" fill-opacity=".6" stroke="#2A1430" stroke-width="5"/>'
    '<ellipse cx="128" cy="190" rx="38" ry="9" fill="#E8FAFF" fill-opacity=".7" stroke="#2A1430" stroke-width="5"/>'
    '<path d="M76,80 L118,128" stroke="#fff" stroke-width="5" opacity=".7" stroke-linecap="round"/>'
    + citrus_wheel(186, 76, 22, "#8EDC2E", "#D8FF8A")
    + umbrella(96, 62, 34, -24, "#FF5A8A", "#FFE04A")
)

green_drink = (
    defs(lin("gDr", [(0, "#D8FF8A"), (.5, "#5AD02E"), (1, "#1A7A1A")]))
    + glass("M86,64 L170,64 L162,200 L94,200Z")
    + '<path d="M89,92 L167,92 L162,196 L94,196Z" fill="url(#gDr)"/>'
    '<rect x="100" y="112" width="26" height="24" rx="5" fill="#fff" opacity=".55" stroke="#fff" stroke-width="2" transform="rotate(-14 113 124)"/>'
    '<rect x="132" y="138" width="24" height="22" rx="5" fill="#fff" opacity=".5" stroke="#fff" stroke-width="2" transform="rotate(18 144 149)"/>'
    '<rect x="106" y="160" width="22" height="20" rx="5" fill="#fff" opacity=".45" transform="rotate(8 117 170)"/>'
    + leaf(116, 98, -30, 26, 10) + leaf(122, 98, 20, 24, 9)
    + '<path d="M140,190 L110,36" stroke="#2A1430" stroke-width="12" stroke-linecap="round"/>'
    '<path d="M140,190 L110,36" stroke="#fff" stroke-width="6" stroke-linecap="round"/>'
    '<path d="M140,190 L110,36" stroke="#FF3A6A" stroke-width="6" stroke-dasharray="10 10" stroke-linecap="butt"/>'
    + glass("M86,64 L170,64 L162,200 L94,200Z", ' fill="none"')
    + '<path d="M96,72 L102,190" stroke="#fff" stroke-width="5" opacity=".6" stroke-linecap="round"/>'
    + citrus_wheel(168, 70, 28, "#7ACC22", "#C8FF6A")
)

hurr = "M96,58 C86,92 106,110 106,128 C106,148 82,160 86,180 C90,192 166,192 170,180 C174,160 150,148 150,128 C150,110 170,92 160,58Z"
orange_drink = (
    defs(lin("oDr", [(0, "#FFE85A"), (.45, "#FF9A1A"), (1, "#E8302A")]),
         '<clipPath id="hClip"><path d="' + hurr + '"/></clipPath>')
    + glass(hurr)
    + '<g clip-path="url(#hClip)"><rect x="70" y="80" width="120" height="120" fill="url(#oDr)"/>'
    '<ellipse cx="128" cy="80" rx="40" ry="5" fill="#FFF6B0" opacity=".9"/></g>'
    + glass(hurr, ' fill="none"')
    + '<path d="M98,68 C94,90 110,108 110,128" stroke="#fff" stroke-width="5" fill="none" opacity=".6" stroke-linecap="round"/>'
    '<rect x="122" y="188" width="12" height="10" fill="#E8FAFF" stroke="#2A1430" stroke-width="4"/>'
    '<ellipse cx="128" cy="200" rx="32" ry="7" fill="#E8FAFF" stroke="#2A1430" stroke-width="5"/>'
    + citrus_wheel(158, 62, 24, "#FF8A1A", "#FFC24A")
    + umbrella(100, 50, 32, -18, "#22C8C0", "#FFFFFF")
    + '<circle cx="146" cy="44" r="10" fill="#E8142E" stroke="#2A1430" stroke-width="4"/>'
)

coupe = "M52,84 C56,128 92,142 128,142 C164,142 200,128 204,84Z"
red_drink = (
    defs(lin("rDr", [(0, "#FF9AB0"), (.5, "#F0204A"), (1, "#8A0420")]))
    + glass(coupe)
    + '<path d="M60,96 C68,126 96,136 128,136 C160,136 188,126 196,96Z" fill="url(#rDr)"/>'
    '<ellipse cx="128" cy="96" rx="68" ry="8" fill="#FFC8D4" opacity=".85"/>'
    + glass(coupe, ' fill="none"')
    + "".join(f'<circle cx="{x}" cy="84" r="3" fill="#fff" stroke="#9AA8B8" stroke-width="1"/>' for x in range(60, 200, 10))
    + '<path d="M68,96 C76,118 92,128 108,132" stroke="#fff" stroke-width="5" fill="none" opacity=".7" stroke-linecap="round"/>'
    '<rect x="122" y="140" width="12" height="44" fill="#E8FAFF" fill-opacity=".6" stroke="#2A1430" stroke-width="5"/>'
    '<ellipse cx="128" cy="190" rx="40" ry="9" fill="#E8FAFF" fill-opacity=".7" stroke="#2A1430" stroke-width="5"/>'
    '<path d="M176,84 C178,66 186,56 198,52" stroke="#2A1430" stroke-width="9" fill="none" stroke-linecap="round"/>'
    '<path d="M176,84 C178,66 186,56 198,52" stroke="#5AA02A" stroke-width="4" fill="none" stroke-linecap="round"/>'
    '<circle cx="174" cy="90" r="16" fill="url(#chR)" stroke="#2A1430" stroke-width="5"/>'
    '<ellipse cx="168" cy="84" rx="5" ry="3" fill="#fff" opacity=".8"/>'
    + umbrella(86, 64, 32, -28, "#FFC23A", "#22C8C0")
)
red_drink_defs = rad("chR", [(0, "#FF9A9A"), (.5, "#E8142E"), (1, "#6A0014")])
red_drink = red_drink.replace("<defs>", "<defs>" + red_drink_defs, 1)

# ------------------------------------------------------------------ wild: hibiscus
petals = "".join(
    f'<path transform="translate(128 104) rotate({a})" d="M0,0 C-34,-16 -46,-62 -6,-72 C4,-74 6,-66 14,-70 C44,-58 32,-18 0,0Z" fill="url(#hib)" stroke="{INK}" stroke-width="5" stroke-linejoin="round"/>'
    for a in range(0, 360, 72))
hibiscus = (
    defs(LEAF, rad("hib", [(0, "#B8102A"), (.35, "#FF4A5A"), (1, "#FFC8B8")], .5, 1, 1.1))
    + leaf(128, 104, 120, 92, 30) + leaf(128, 104, -130, 86, 28)
    + petals
    + '<circle cx="128" cy="104" r="14" fill="#8A0A1E"/>'
    '<path d="M128,104 C136,88 150,74 160,62" stroke="#2A1430" stroke-width="8" fill="none" stroke-linecap="round"/>'
    '<path d="M128,104 C136,88 150,74 160,62" stroke="#FFD24A" stroke-width="4" fill="none" stroke-linecap="round"/>'
    + "".join(f'<circle cx="{x}" cy="{y}" r="5" fill="#FFE24A" stroke="#2A1430" stroke-width="2.5"/>' for x, y in [(160, 58), (168, 66), (152, 56), (166, 52)])
)

# ------------------------------------------------------------------ scatter: island
island = (
    defs(LEAF, '<clipPath id="isl"><circle cx="128" cy="112" r="76"/></clipPath>',
         lin("sea", [(0, "#4ADCE8"), (1, "#0A6A9A")]), lin("trunk", [(0, "#C88A4A"), (1, "#7A4A1E")], 1, 0))
    + '<g clip-path="url(#isl)">'
    '<circle cx="168" cy="82" r="20" fill="#FFE24A" stroke="#FF9A1A" stroke-width="4"/>'
    '<path d="M40,148 C70,138 90,150 128,142 C166,134 186,148 220,140 L220,200 L40,200Z" fill="url(#sea)" stroke="#2A1430" stroke-width="4"/>'
    '<path d="M58,164 C70,160 80,166 92,162 M150,172 C162,168 172,174 184,170" stroke="#fff" stroke-width="3" fill="none" opacity=".7" stroke-linecap="round"/>'
    '</g>'
    '<path d="M70,150 C80,128 160,128 176,150Z" fill="#FFE2A0" stroke="#2A1430" stroke-width="5" stroke-linejoin="round"/>'
    '<path d="M112,146 C108,120 112,96 128,76 L138,80 C124,98 122,122 126,146Z" fill="url(#trunk)" stroke="#2A1430" stroke-width="5" stroke-linejoin="round"/>'
    '<path d="M112,128 L126,130 M112,112 L126,116 M118,96 L130,100" stroke="#5A3010" stroke-width="2.5"/>'
    + leaf(133, 78, -70, 50, 15, sw=4) + leaf(133, 78, 70, 50, 15, sw=4) + leaf(133, 78, -125, 44, 13, sw=4)
    + leaf(133, 78, 125, 44, 13, sw=4) + leaf(133, 78, -15, 42, 13, sw=4) + leaf(133, 78, 20, 40, 12, sw=4)
    + '<circle cx="128" cy="84" r="6" fill="#6A3A12" stroke="#2A1430" stroke-width="2.5"/><circle cx="139" cy="86" r="6" fill="#6A3A12" stroke="#2A1430" stroke-width="2.5"/>'
)

def fit(icon, k=.9, cy=134):
    return f'<g transform="translate(128 {cy}) scale({k}) translate(-128 -{cy})">{icon}</g>'


SYMBOLS = {
    "ananas": ananas,
    "blueglass": medallion("square", CORAL, fit(blue_drink, .9, 142)),
    "greenglass": medallion("circle", SUN, fit(green_drink, .92, 136)),
    "orangeglass": medallion("shield", TURQ, fit(orange_drink, .86, 150)),
    "redglass": medallion("hex", LIME, fit(red_drink, .92, 142)),
    "cherry": cherry,
    "orrange": orrange,
    "strawberry": strawberry,
    "wild": wild(TURQ, hibiscus),
    "scatter": scatter(SKY, island, "BONUS"),
}
