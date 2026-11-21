"""Gold mine: earthy brown, warm gold, lantern glow."""
from symkit import P, coin, gem, letter, medallion, scatter, sparkle, star, wild


def lgd(id_, *cols, x2="0", y2="1"):
    n = len(cols) - 1
    s = "".join(f'<stop offset="{i / n:.2f}" stop-color="{c}"/>' for i, c in enumerate(cols))
    return f'<defs><linearGradient id="{id_}" x1="0" y1="0" x2="{x2}" y2="{y2}">{s}</linearGradient></defs>'


def rgd(id_, *cols, cx=".38", cy=".32", r=".75"):
    n = len(cols) - 1
    s = "".join(f'<stop offset="{i / n:.2f}" stop-color="{c}"/>' for i, c in enumerate(cols))
    return f'<defs><radialGradient id="{id_}" cx="{cx}" cy="{cy}" r="{r}">{s}</radialGradient></defs>'


INK = "#241004"
STEEL = P("#D8ECF4", "#5A9AB8", "#1A4660", "#081E2C")
COPPER = P("#FFD0A8", "#D86A2A", "#702606", "#2E0E02")
AMBER = P("#FFF2B0", "#F2B52A", "#8A5204", "#341A02")
RUBY = P("#FFB8C0", "#D8283E", "#6A0614", "#2A0208")
EARTH = P("#F0D2A8", "#A8703A", "#4A2A10", "#1E0E04")
CAVE = P("#C8B8A8", "#6A5444", "#2A1E16", "#120A06")
LAMP = P("#FFF0C0", "#E89A2A", "#6A3006", "#2A1002")

NUG = rgd("nug", "#FFFBD0", "#FFD23A", "#C27A00", "#7A4200", cx=".35", cy=".3", r=".8")


def nugget(pts, hl=None):
    d = "M" + " L".join(f"{x},{y}" for x, y in pts) + "Z"
    out = f'<path d="{d}" fill="url(#nug)" stroke="#4A2400" stroke-width="6" stroke-linejoin="round"/>'
    if hl:
        out += f'<path d="M{hl}" fill="#fff" opacity=".6"/>'
    return out


def nug_at(cx, cy, s, rot=0, hl=True):
    base = [(-30, -6), (-18, -24), (6, -28), (26, -16), (32, 6), (20, 24), (-6, 28), (-26, 18)]
    return (f'<g transform="translate({cx} {cy}) rotate({rot}) scale({s})">'
            + nugget(base, "-18,-14 L-6,-20 L4,-18 L-10,-8Z" if hl else None)
            + '<path d="M-6,-4 L6,-12 L14,2 L2,10Z" fill="#FFE680" opacity=".7"/></g>')


gold = (
    NUG
    + nug_at(84, 166, 1.25, -10) + nug_at(176, 166, 1.2, 20) + nug_at(128, 176, 1.35, 5)
    + nug_at(104, 114, 1.25, 30) + nug_at(154, 112, 1.15, -25) + nug_at(130, 78, 1.0, 10)
    + sparkle(78, 92, .9) + sparkle(186, 76, 1.1) + sparkle(146, 146, .6)
)

lantern = (
    rgd("glowL", "#FFFFFF", "#FFE680", "#FFB020", "#FF7A0000", cx=".5", cy=".5", r=".5")
    + lgd("glass", "#FFF6C0", "#FFC83A", "#FF8A1A")
    + '<circle cx="128" cy="140" r="78" fill="url(#glowL)" opacity=".8"/>'
    '<path d="M104,62 C104,36 152,36 152,62" fill="none" stroke="#1A1006" stroke-width="13" stroke-linecap="round"/>'
    '<path d="M104,62 C104,36 152,36 152,62" fill="none" stroke="url(#silver)" stroke-width="6" stroke-linecap="round"/>'
    '<path d="M90,84 L100,64 L156,64 L166,84Z" fill="#B8341A" stroke="#1A1006" stroke-width="6" stroke-linejoin="round"/>'
    '<rect x="84" y="82" width="88" height="14" rx="5" fill="#8A240E" stroke="#1A1006" stroke-width="5"/>'
    '<path d="M94,96 L162,96 L168,176 L88,176Z" fill="url(#glass)" stroke="#1A1006" stroke-width="6" stroke-linejoin="round"/>'
    '<path d="M128,166 C110,160 110,140 120,128 C122,138 126,140 128,130 C130,118 136,110 140,104 C140,120 150,130 148,146 C146,160 138,166 128,166Z" fill="#FF6A1A" stroke="#8A2A00" stroke-width="3"/>'
    '<path d="M128,164 C120,160 120,148 126,140 C128,148 132,146 134,140 C138,150 136,160 128,164Z" fill="#FFF6B0"/>'
    '<path d="M100,100 L104,170" stroke="#fff" stroke-width="6" opacity=".6" stroke-linecap="round"/>'
    '<path d="M110,96 L106,176 M146,96 L150,176" stroke="#1A1006" stroke-width="5"/>'
    '<rect x="80" y="174" width="96" height="16" rx="5" fill="#B8341A" stroke="#1A1006" stroke-width="6"/>'
    '<path d="M84,190 L172,190 L164,204 L92,204Z" fill="#8A240E" stroke="#1A1006" stroke-width="6" stroke-linejoin="round"/>'
    '<path d="M90,178 L170,178" stroke="#fff" stroke-width="3" opacity=".4"/>'
    + sparkle(180, 92, .7)
)

minecart = (
    NUG + lgd("cart", "#9AA6B8", "#5A6478", "#2A3040")
    # rails
    + '<path d="M44,206 L212,206" stroke="#1A1006" stroke-width="12" stroke-linecap="round"/>'
    '<path d="M44,206 L212,206" stroke="#8A8A9A" stroke-width="5" stroke-linecap="round"/>'
    # gold pile
    + nug_at(84, 104, 1.0, -20) + nug_at(172, 104, 1.0, 20) + nug_at(108, 86, 1.05, 15) + nug_at(150, 84, 1.1, -10) + nug_at(128, 104, 1.15, 0)
    + nug_at(128, 70, .85, 30)
    # body
    + '<path d="M50,112 L206,112 L190,182 L66,182Z" fill="url(#cart)" stroke="#10141E" stroke-width="7" stroke-linejoin="round"/>'
    '<rect x="44" y="104" width="168" height="18" rx="5" fill="#B8341A" stroke="#10141E" stroke-width="6"/>'
    '<path d="M60,136 L196,136 M64,160 L192,160" stroke="#10141E" stroke-width="4" opacity=".6"/>'
    '<path d="M92,122 L98,182 M164,122 L158,182" stroke="#10141E" stroke-width="4" opacity=".6"/>'
    '<circle cx="76" cy="146" r="3.5" fill="#10141E"/><circle cx="180" cy="146" r="3.5" fill="#10141E"/>'
    '<path d="M60,128 L196,128" stroke="#fff" stroke-width="4" opacity=".4"/>'
    '<path d="M50,108 L206,108" stroke="#fff" stroke-width="3" opacity=".5"/>'
    # wheels
    '<circle cx="90" cy="190" r="17" fill="#3A3A44" stroke="#10141E" stroke-width="6"/><circle cx="90" cy="190" r="6" fill="url(#gold)"/>'
    '<circle cx="166" cy="190" r="17" fill="#3A3A44" stroke="#10141E" stroke-width="6"/><circle cx="166" cy="190" r="6" fill="url(#gold)"/>'
    + sparkle(196, 70, .9) + sparkle(64, 78, .6)
)

pickaxe = (
    lgd("handle", "#E8B070", "#A86A30", "#5A3410", x2="1", y2="0") + lgd("iron", "#F0F4FA", "#A8B4C8", "#5A6680")
    + '<g transform="translate(128 132) scale(.8) rotate(-40) translate(-128 -128)">'
    '<rect x="116" y="70" width="24" height="152" rx="10" fill="url(#handle)" stroke="#241004" stroke-width="7"/>'
    '<path d="M122,80 L122,210" stroke="#fff" stroke-width="4" opacity=".4" stroke-linecap="round"/>'
    '<rect x="112" y="178" width="32" height="12" rx="4" fill="#5A3410" stroke="#241004" stroke-width="4"/>'
    '<rect x="112" y="196" width="32" height="12" rx="4" fill="#5A3410" stroke="#241004" stroke-width="4"/>'
    '<path d="M30,96 C60,62 100,52 128,52 C156,52 196,62 226,96 C200,100 162,94 128,94 C94,94 56,100 30,96Z" fill="url(#iron)" stroke="#141A2A" stroke-width="7" stroke-linejoin="round"/>'
    '<path d="M50,82 C80,64 110,60 128,60" stroke="#fff" stroke-width="5" fill="none" opacity=".8" stroke-linecap="round"/>'
    '<rect x="110" y="46" width="36" height="44" rx="8" fill="url(#iron)" stroke="#141A2A" stroke-width="6"/>'
    '<rect x="116" y="52" width="10" height="30" rx="4" fill="#fff" opacity=".6"/>'
    '</g>'
    + sparkle(70, 72, .8) + sparkle(194, 186, .6)
)

helmet = (
    rgd("helm", "#FFF6B0", "#FFC82A", "#C07600", cx=".4", cy=".25") + rgd("beam", "#FFFFFF", "#FFF6B0", "#FFF6B000", cx=".5", cy=".5", r=".5")
    + '<path d="M52,150 C52,90 86,58 128,58 C170,58 204,90 204,150Z" fill="url(#helm)" stroke="#3A1C00" stroke-width="7" stroke-linejoin="round"/>'
    '<path d="M118,60 C118,90 118,120 118,150 M138,60 C138,90 138,120 138,150" stroke="#C07600" stroke-width="5"/>'
    '<path d="M74,128 C74,96 92,76 112,70" stroke="#fff" stroke-width="7" fill="none" opacity=".6" stroke-linecap="round"/>'
    '<path d="M36,150 C36,140 220,140 220,150 C220,164 36,164 36,150Z" fill="url(#helm)" stroke="#3A1C00" stroke-width="7"/>'
    '<rect x="104" y="86" width="48" height="36" rx="10" fill="#4A4A54" stroke="#141418" stroke-width="6"/>'
    '<circle cx="128" cy="104" r="14" fill="#FFF6C0" stroke="#141418" stroke-width="4"/>'
    '<circle cx="124" cy="100" r="5" fill="#fff"/>'
)

dynamite = (
    lgd("dyn", "#FF8A6A", "#E0302A", "#7A0A08", x2="1", y2="0")
    + rgd("fire", "#FFFFFF", "#FFE060", "#FF8A1A", "#FF3A0000", cx=".5", cy=".5", r=".5")
    + '<circle cx="168" cy="54" r="26" fill="url(#fire)"/>'
    '<path d="M128,92 C130,74 146,62 166,58" stroke="#1A1006" stroke-width="7" fill="none" stroke-linecap="round"/>'
    '<path d="M128,92 C130,74 146,62 166,58" stroke="#C8A070" stroke-width="3" fill="none" stroke-linecap="round" stroke-dasharray="5 3"/>'
    + star(168, 56, 14, "#FFF6B0", "#FF7A00", 3, 8, .4)
    + "".join(f'<rect x="{x}" y="92" width="30" height="88" rx="8" fill="url(#dyn)" stroke="#2A0404" stroke-width="6"/>'
              f'<rect x="{x + 5}" y="98" width="7" height="76" rx="3" fill="#fff" opacity=".4"/>' for x in (82, 113, 144))
    + '<rect x="76" y="120" width="104" height="16" rx="3" fill="#3A2A1A" stroke="#1A0E06" stroke-width="5"/>'
    '<rect x="76" y="152" width="104" height="16" rx="3" fill="#3A2A1A" stroke="#1A0E06" stroke-width="5"/>'
    '<text x="128" y="160" font-family="Lilita One" font-size="13" text-anchor="middle" fill="#FFD24A">TNT</text>'
)

SYMBOLS = {
    "j": letter("J", STEEL, gem(204, 54, 22, "#C8F4FF", "#3AB8E8", "#0A5A8A")),
    "q": letter("Q", COPPER, NUG + nug_at(206, 52, .85, 15)),
    "k": letter("K", AMBER, gem(204, 54, 22, "#C8FFB8", "#3AC860", "#0A6A2A")),
    "a": letter("A", RUBY, NUG + nug_at(206, 52, .85, -20) + sparkle(228, 30, .6)),
    "gold": medallion("circle", EARTH, gold),
    "lantern": medallion("shield", CAVE, lantern),
    "minecart": medallion("square", EARTH, minecart),
    "pickaxe": medallion("hex", STEEL, pickaxe),
    "wild": wild(EARTH, helmet),
    "scatter": scatter(CAVE, dynamite, "BONUS"),
}
