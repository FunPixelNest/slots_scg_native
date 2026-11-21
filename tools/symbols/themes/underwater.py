"""Underwater: ocean blues, coral pink, bubbles."""
import math

from symkit import P, letter, medallion, scatter, sparkle, star_pts, wild

INK = "#071A3A"
TEAL = P("#C8FFF2", "#16C8B0", "#04504A", INK)
CORALP = P("#FFD6E2", "#FF5A8A", "#7A0A3A", INK)
DEEP = P("#C8E8FF", "#2A7AE0", "#0A2A6A", INK)
PURP = P("#EAD6FF", "#8A4AE0", "#2A0A6A", INK)
SEA = P("#B8F0FF", "#1E9BD0", "#06305A", INK)
SAND = P("#FFF0C8", "#E8B860", "#7A4A10", INK)
KELP = P("#D8FFC8", "#4AB86A", "#0A4A2A", INK)
LAGOON = P("#C8F8FF", "#22B0D8", "#0A3A6A", INK)


def lin(id_, stops, x2=0, y2=1):
    s = "".join(f'<stop offset="{o}" stop-color="{c}"/>' for o, c in stops)
    return f'<linearGradient id="{id_}" x1="0" y1="0" x2="{x2}" y2="{y2}">{s}</linearGradient>'


def rad(id_, stops, cx=.38, cy=.32, r=.75):
    s = "".join(f'<stop offset="{o}" stop-color="{c}"/>' for o, c in stops)
    return f'<radialGradient id="{id_}" cx="{cx}" cy="{cy}" r="{r}">{s}</radialGradient>'


def defs(*items):
    return "<defs>" + "".join(items) + "</defs>"


def bubble(x, y, r):
    return (f'<circle cx="{x}" cy="{y}" r="{r}" fill="#BFF4FF" fill-opacity=".35" stroke="#E8FCFF" stroke-width="{max(2, r * .16):.1f}"/>'
            f'<ellipse cx="{x - r * .35:.1f}" cy="{y - r * .38:.1f}" rx="{r * .32:.1f}" ry="{r * .2:.1f}" fill="#fff" opacity=".9" transform="rotate(-35 {x - r * .35:.1f} {y - r * .38:.1f})"/>')


def eyes(x1, x2, y, r=9):
    out = ""
    for x in (x1, x2):
        out += (f'<circle cx="{x}" cy="{y}" r="{r}" fill="#fff" stroke="{INK}" stroke-width="3.5"/>'
                f'<circle cx="{x + r * .15:.1f}" cy="{y + r * .1:.1f}" r="{r * .55:.1f}" fill="{INK}"/>'
                f'<circle cx="{x - r * .05:.1f}" cy="{y - r * .2:.1f}" r="{r * .2:.1f}" fill="#fff"/>')
    return out


def starfish(x, y, r, c1="#FFB46A", c2="#F06A2A"):
    return (f'<g filter="url(#drop)"><polygon points="{star_pts(x, y, r, 5, .5)}" fill="{c2}" stroke="{INK}" stroke-width="{r * .22:.1f}" stroke-linejoin="round"/>'
            f'<polygon points="{star_pts(x, y - r * .05, r * .8, 5, .5)}" fill="{c1}" stroke-linejoin="round"/>'
            + "".join(f'<circle cx="{x + r * .45 * math.cos(math.radians(-90 + 72 * i)):.1f}" cy="{y + r * .45 * math.sin(math.radians(-90 + 72 * i)):.1f}" r="{r * .08:.1f}" fill="#fff" opacity=".8"/>' for i in range(5))
            + '</g>')


def shell(x, y, r, c1="#FFE0EC", c2="#FF8AB0"):
    ribs = "".join(f'<path d="M{x},{y + r * .7:.1f} L{x + r * .95 * math.cos(math.radians(a)):.1f},{y + r * .95 * math.sin(math.radians(a)) * .9 - r * .1:.1f}" stroke="{c2}" stroke-width="{r * .1:.1f}"/>'
                   for a in range(-160, -10, 25))
    return (f'<g filter="url(#drop)"><path d="M{x - r},{y} C{x - r},{y - r * 1.1:.1f} {x + r},{y - r * 1.1:.1f} {x + r},{y} L{x},{y + r * .7:.1f}Z" fill="{c1}" stroke="{INK}" stroke-width="{r * .14:.1f}" stroke-linejoin="round"/>'
            f'{ribs}<path d="M{x - r * .3:.1f},{y + r * .6:.1f} L{x + r * .3:.1f},{y + r * .6:.1f} L{x + r * .2:.1f},{y + r * .9:.1f} L{x - r * .2:.1f},{y + r * .9:.1f}Z" fill="{c2}" stroke="{INK}" stroke-width="{r * .1:.1f}" stroke-linejoin="round"/></g>')


def pearl(x, y, r):
    return (f'<g filter="url(#drop)"><circle cx="{x}" cy="{y}" r="{r}" fill="url(#pearl)" stroke="{INK}" stroke-width="{max(2.5, r * .14):.1f}"/>'
            f'<ellipse cx="{x - r * .3:.1f}" cy="{y - r * .35:.1f}" rx="{r * .32:.1f}" ry="{r * .2:.1f}" fill="#fff" transform="rotate(-35 {x - r * .3:.1f} {y - r * .35:.1f})"/></g>')


PEARL = rad("pearl", [(0, "#FFFFFF"), (.6, "#F0E6FF"), (1, "#B8A8D8")])

# ------------------------------------------------------------------ crab
legs = "".join(
    f'<path d="M{128 + sx * 40},{150 + i * 10} C{128 + sx * 66},{146 + i * 14} {128 + sx * 78},{160 + i * 14} {128 + sx * 82},{178 + i * 10}" stroke="{INK}" stroke-width="15" fill="none" stroke-linecap="round"/>'
    f'<path d="M{128 + sx * 40},{150 + i * 10} C{128 + sx * 66},{146 + i * 14} {128 + sx * 78},{160 + i * 14} {128 + sx * 82},{178 + i * 10}" stroke="#E8462A" stroke-width="8" fill="none" stroke-linecap="round"/>'
    for sx in (-1, 1) for i in range(3))


def claw(flip):
    t = f' transform="translate(256 0) scale(-1 1)"' if flip else ""
    return (f'<g{t}>'
            '<path d="M96,140 C86,128 80,120 76,112" stroke="#071A3A" stroke-width="18" fill="none" stroke-linecap="round"/>'
            '<path d="M96,140 C86,128 80,120 76,112" stroke="#E8462A" stroke-width="10" fill="none" stroke-linecap="round"/>'
            '<path d="M74,124 C48,122 38,92 50,72 C56,62 66,56 76,58 L70,80 L94,72 C100,96 94,122 74,124Z" fill="url(#crab)" stroke="#071A3A" stroke-width="6" stroke-linejoin="round"/>'
            '<path d="M56,78 C54,92 58,106 66,112" stroke="#fff" stroke-width="4" fill="none" opacity=".5" stroke-linecap="round"/></g>')


crab = (
    defs(rad("crab", [(0, "#FFB08A"), (.55, "#F04A2A"), (1, "#8A1408")]))
    + legs + claw(False) + claw(True)
    + '<path d="M112,112 L108,90 M144,112 L148,90" stroke="#071A3A" stroke-width="11" stroke-linecap="round"/>'
    '<path d="M112,112 L108,90 M144,112 L148,90" stroke="#E8462A" stroke-width="5" stroke-linecap="round"/>'
    '<ellipse cx="128" cy="146" rx="58" ry="40" fill="url(#crab)" stroke="#071A3A" stroke-width="7"/>'
    '<ellipse cx="110" cy="128" rx="22" ry="9" fill="#fff" opacity=".4" transform="rotate(-12 110 128)"/>'
    + eyes(108, 148, 86, 11)
    + '<path d="M114,156 C122,166 134,166 142,156" stroke="#071A3A" stroke-width="5" fill="none" stroke-linecap="round"/>'
    '<circle cx="98" cy="150" r="6" fill="#FF8A9A" opacity=".7"/><circle cx="158" cy="150" r="6" fill="#FF8A9A" opacity=".7"/>'
)

# ------------------------------------------------------------------ sea urchin (hedgehog)
spikes = ""
for i in range(28):
    a = math.radians(i * 360 / 28 + 6)
    L = 82 if i % 2 == 0 else 70
    w = math.radians(7.5)
    x0, y0 = 128 + 40 * math.cos(a - w), 132 + 40 * math.sin(a - w)
    x1, y1 = 128 + 40 * math.cos(a + w), 132 + 40 * math.sin(a + w)
    xt, yt = 128 + L * math.cos(a), 132 + L * math.sin(a)
    spikes += f'<polygon points="{x0:.1f},{y0:.1f} {xt:.1f},{yt:.1f} {x1:.1f},{y1:.1f}" fill="url(#spk)" stroke="#071A3A" stroke-width="3" stroke-linejoin="round"/>'
dots = "".join(f'<circle cx="{128 + 32 * math.cos(math.radians(a)):.1f}" cy="{132 + 32 * math.sin(math.radians(a)):.1f}" r="3" fill="#E8B8FF" opacity=".7"/>'
               for a in range(0, 360, 30))
hedgehog = (
    defs(rad("urch", [(0, "#D8A0FF"), (.55, "#7A2AC8"), (1, "#2A0A5A")]),
         lin("spk", [(0, "#FFE0FF"), (.5, "#C070F0"), (1, "#6A20B0")], 1, 1))
    + spikes
    + '<circle cx="128" cy="132" r="46" fill="url(#urch)" stroke="#071A3A" stroke-width="7"/>'
    + dots
    + '<ellipse cx="112" cy="110" rx="16" ry="9" fill="#fff" opacity=".45" transform="rotate(-30 112 110)"/>'
    + eyes(114, 142, 130, 10)
    + '<path d="M120,150 C125,156 131,156 136,150" stroke="#071A3A" stroke-width="4.5" fill="none" stroke-linecap="round"/>'
    + bubble(190, 72, 9) + bubble(204, 52, 6)
)

# ------------------------------------------------------------------ jellyfish
tent = ""
for i, x in enumerate([86, 104, 122, 140, 158, 172]):
    sw = -1 if i % 2 else 1
    d = f"M{x},130 C{x + sw * 14},148 {x - sw * 14},166 {x + sw * 4},184 C{x + sw * 12},194 {x - sw * 4},204 {x},210"
    tent += (f'<path d="{d}" stroke="#071A3A" stroke-width="12" fill="none" stroke-linecap="round"/>'
             f'<path d="{d}" stroke="{["#FF8AD0", "#C88AFF"][i % 2]}" stroke-width="6" fill="none" stroke-linecap="round"/>')
frill = "M64,126 C64,58 192,58 192,126 C182,136 172,126 160,134 C150,126 140,136 128,130 C116,136 106,126 96,134 C84,126 74,136 64,126Z"
jellyfish = (
    defs(rad("jel", [(0, "#FFE0F6"), (.5, "#FF7AC8"), (1, "#8A2AA8")], .4, .3, .8))
    + '<g transform="translate(128 132) scale(.96) translate(-128 -132)">'
    + tent
    + '<path d="M100,128 C104,150 96,168 104,186 M128,130 C124,152 134,170 126,192 M154,128 C152,148 160,166 152,182" stroke="#FFD6F2" stroke-width="10" fill="none" stroke-linecap="round" opacity=".7"/>'
    + f'<path d="{frill}" fill="url(#jel)" stroke="#071A3A" stroke-width="7" stroke-linejoin="round"/>'
    '<path d="M80,104 C84,80 104,68 124,66" stroke="#fff" stroke-width="7" fill="none" opacity=".6" stroke-linecap="round"/>'
    '<circle cx="152" cy="82" r="5" fill="#fff" opacity=".6"/><circle cx="168" cy="96" r="3.5" fill="#fff" opacity=".6"/>'
    + eyes(112, 144, 106, 10)
    + '<path d="M120,120 C125,126 131,126 136,120" stroke="#071A3A" stroke-width="4.5" fill="none" stroke-linecap="round"/>'
    '<circle cx="98" cy="118" r="5" fill="#FF4A9A" opacity=".6"/><circle cx="158" cy="118" r="5" fill="#FF4A9A" opacity=".6"/></g>'
    + bubble(194, 150, 8) + bubble(62, 64, 6)
)

# ------------------------------------------------------------------ octopus
arms = [
    "M100,140 C80,160 56,150 58,176 C60,192 80,190 78,178",
    "M112,148 C104,176 86,186 94,204 C100,214 114,206 108,196",
    "M128,150 C128,178 120,192 128,206 C134,214 146,204 138,196",
    "M144,148 C152,176 170,186 162,204 C156,214 142,206 148,196",
    "M156,140 C176,160 200,150 198,176 C196,192 176,190 178,178",
]
oct_arms = "".join(f'<path d="{d}" stroke="#071A3A" stroke-width="24" fill="none" stroke-linecap="round" stroke-linejoin="round"/>' for d in arms) + \
           "".join(f'<path d="{d}" stroke="url(#octA)" stroke-width="15" fill="none" stroke-linecap="round" stroke-linejoin="round"/>' for d in arms)
suckers = "".join(f'<circle cx="{x}" cy="{y}" r="3" fill="#FFD0B8" stroke="#A82A3A" stroke-width="1.5"/>' for x, y in
                  [(70, 160), (62, 176), (98, 186), (126, 186), (130, 172), (158, 186), (186, 160), (194, 176)])
octopus = (
    defs(rad("oct", [(0, "#FFB8A8"), (.55, "#F0506A"), (1, "#8A1036")]), lin("octA", [(0, "#FF7A8A"), (1, "#C82A4A")], 1, 1))
    + oct_arms + suckers
    + '<path d="M76,112 C70,60 102,44 128,44 C154,44 186,60 180,112 C176,140 156,154 128,154 C100,154 80,140 76,112Z" fill="url(#oct)" stroke="#071A3A" stroke-width="7"/>'
    '<ellipse cx="104" cy="72" rx="18" ry="11" fill="#fff" opacity=".45" transform="rotate(-30 104 72)"/>'
    '<circle cx="150" cy="70" r="5" fill="#FFD0C8" opacity=".7"/><circle cx="162" cy="84" r="3.5" fill="#FFD0C8" opacity=".7"/>'
    + eyes(108, 148, 112, 13)
    + '<path d="M118,136 C124,142 132,142 138,136" stroke="#071A3A" stroke-width="5" fill="none" stroke-linecap="round"/>'
    '<circle cx="90" cy="130" r="6" fill="#FF8AB0" opacity=".7"/><circle cx="166" cy="130" r="6" fill="#FF8AB0" opacity=".7"/>'
    + bubble(198, 66, 9) + bubble(210, 46, 6)
)

# ------------------------------------------------------------------ wild: trident
trident = (
    '<path d="M128,20 L140,48 L134,48 L134,160 L122,160 L122,48 L116,48Z" fill="#071A3A" stroke="#071A3A" stroke-width="12" stroke-linejoin="round"/>'
    '<path d="M76,34 C78,62 92,80 122,84 L134,84 C164,80 178,62 180,34 L170,48 C168,64 156,70 140,72 L116,72 C100,70 88,64 86,48Z" fill="#071A3A" stroke="#071A3A" stroke-width="12" stroke-linejoin="round"/>'
    '<path d="M128,20 L140,48 L134,48 L134,160 L122,160 L122,48 L116,48Z" fill="url(#gold)" stroke="#7A3A00" stroke-width="3" stroke-linejoin="round"/>'
    '<path d="M76,34 C78,62 92,80 122,84 L134,84 C164,80 178,62 180,34 L170,48 C168,64 156,70 140,72 L116,72 C100,70 88,64 86,48Z" fill="url(#gold)" stroke="#7A3A00" stroke-width="3" stroke-linejoin="round"/>'
    '<rect x="114" y="96" width="28" height="12" rx="5" fill="url(#goldH)" stroke="#7A3A00" stroke-width="3"/>'
    '<circle cx="128" cy="78" r="9" fill="#22C8E8" stroke="#071A3A" stroke-width="3"/><circle cx="125" cy="75" r="3" fill="#fff"/>'
    '<path d="M126,52 L126,150" stroke="#fff" stroke-width="3" opacity=".6"/>'
    + bubble(66, 110, 12) + bubble(192, 116, 10) + bubble(80, 140, 7) + bubble(176, 146, 7)
)

# ------------------------------------------------------------------ scatter: clam with pearl
ribsT = "".join(f'<path d="M128,112 L{128 + 70 * math.cos(math.radians(a)):.1f},{112 + 66 * math.sin(math.radians(a)):.1f}" stroke="#E07AA0" stroke-width="3.5"/>'
                for a in range(-165, -10, 22))
clam = (
    defs(PEARL, lin("clamT", [(0, "#FFE8F0"), (1, "#FF9ABE")]), lin("clamB", [(0, "#FFB8D0"), (1, "#C8467A")]))
    + '<path d="M58,112 C56,56 200,56 198,112Z" fill="url(#clamT)" stroke="#071A3A" stroke-width="6" stroke-linejoin="round"/>'
    + ribsT
    + '<path d="M58,112 C64,140 92,156 128,156 C164,156 192,140 198,112 C170,124 86,124 58,112Z" fill="url(#clamB)" stroke="#071A3A" stroke-width="6" stroke-linejoin="round"/>'
    '<path d="M70,118 C96,128 160,128 186,118" stroke="#8A1A4A" stroke-width="3" fill="none" opacity=".6"/>'
    '<path d="M64,128 C80,146 104,152 128,152" stroke="#fff" stroke-width="4" fill="none" opacity=".5" stroke-linecap="round"/>'
    + pearl(128, 110, 24)
    + sparkle(160, 92, 1) + sparkle(92, 80, .6)
)

SYMBOLS = {
    "j": letter("J", TEAL, bubble(210, 58, 16) + bubble(228, 30, 9)),
    "q": letter("Q", CORALP, shell(208, 52, 32)),
    "k": letter("K", DEEP, starfish(210, 46, 30)),
    "a": letter("A", PURP, defs(PEARL) + pearl(210, 46, 22) + bubble(232, 82, 8)),
    "crab": medallion("circle", SEA, f'<g transform="translate(128 132) scale(.9) translate(-128 -132)">{crab}</g>'),
    "hedgehog": medallion("hex", SAND, hedgehog),
    "jellyfish": medallion("shield", DEEP, jellyfish),
    "octopus": medallion("square", KELP, octopus),
    "wild": wild(SEA, trident),
    "scatter": scatter(LAGOON, clam, "BONUS"),
}
