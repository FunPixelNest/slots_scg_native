"""Forest: mossy greens and autumn orange."""
from symkit import P, letter, medallion, scatter, sparkle, star, wild


def lgd(id_, *cols, x2="0", y2="1"):
    n = len(cols) - 1
    s = "".join(f'<stop offset="{i / n:.2f}" stop-color="{c}"/>' for i, c in enumerate(cols))
    return f'<defs><linearGradient id="{id_}" x1="0" y1="0" x2="{x2}" y2="{y2}">{s}</linearGradient></defs>'


def rgd(id_, *cols, cx=".38", cy=".32", r=".75"):
    n = len(cols) - 1
    s = "".join(f'<stop offset="{i / n:.2f}" stop-color="{c}"/>' for i, c in enumerate(cols))
    return f'<defs><radialGradient id="{id_}" cx="{cx}" cy="{cy}" r="{r}">{s}</radialGradient></defs>'


def eye(x, y, r=9):
    return (f'<ellipse cx="{x}" cy="{y}" rx="{r}" ry="{r * 1.15:.1f}" fill="#1A0E06"/>'
            f'<circle cx="{x - r * .3:.1f}" cy="{y - r * .4:.1f}" r="{r * .38:.1f}" fill="#fff"/>')


LEAFG = lgd("fLeaf", "#C8F070", "#5AAE2A", "#1E5A14")
AUTUMN = lgd("fAut", "#FFE07A", "#FF8A1A", "#B0300A")

MOSS = P("#E2FFB0", "#7CC23A", "#2E6A16", "#10300A")
ORANGE = P("#FFE2A8", "#FF9A2A", "#B0420A", "#3E1402")
BARK = P("#F2D8B0", "#B07A44", "#5A3416", "#24120A")
BERRY = P("#FFC0C8", "#E0384A", "#7A0A1E", "#30040C")
PINE = P("#C8F0D8", "#3A9A6A", "#0E4A30", "#062A1A")
DUSK = P("#E6D8FF", "#7A5AC8", "#2E1A6A", "#140A32")
GOLD = P("#FFF4C0", "#F2B23A", "#94520A", "#3A1C02")


def acorn_icon(s=1.0, tx=0, ty=0):
    return (f'<g transform="translate({tx} {ty}) scale({s})">'
            '<path d="M128,110 C170,110 176,150 166,176 C156,200 140,212 128,214 C116,212 100,200 90,176 C80,150 86,110 128,110Z" fill="url(#fNut)" stroke="#2A1206" stroke-width="7" stroke-linejoin="round"/>'
            '<path d="M104,128 C96,144 96,166 104,182 C94,174 88,150 94,132Z" fill="#fff" opacity=".45"/>'
            '<path d="M128,214 L128,222" stroke="#2A1206" stroke-width="7" stroke-linecap="round"/>'
            '<path d="M70,112 C70,80 98,66 128,66 C158,66 186,80 186,112 C186,126 172,130 128,130 C84,130 70,126 70,112Z" fill="url(#fCap)" stroke="#2A1206" stroke-width="7" stroke-linejoin="round"/>'
            '<path d="M84,90 L104,124 M100,76 L130,128 M120,68 L154,126 M144,70 L172,118 M164,80 L182,108 M172,84 L148,128 M154,72 L120,128 M132,68 L94,126 M110,70 L80,116" stroke="#4A2A10" stroke-width="3" opacity=".55"/>'
            '<path d="M128,68 C126,56 130,46 142,40" stroke="#2A1206" stroke-width="13" fill="none" stroke-linecap="round"/>'
            '<path d="M128,68 C126,56 130,46 142,40" stroke="#8A5A2A" stroke-width="6" fill="none" stroke-linecap="round"/>'
            '<path d="M86,96 C96,82 112,76 124,76" stroke="#fff" stroke-width="5" fill="none" opacity=".5" stroke-linecap="round"/>'
            '</g>')


NUTG = rgd("fNut", "#FFD890", "#D88A30", "#7A3A0A") + rgd("fCap", "#C89A6A", "#8A5A30", "#40220C")

acorn = (
    NUTG + LEAFG
    + '<path d="M150,70 C170,50 204,56 210,70 C196,92 164,92 150,70Z" fill="url(#fLeaf)" stroke="#14300A" stroke-width="5" stroke-linejoin="round"/>'
    '<path d="M154,70 C172,66 190,66 204,70" stroke="#14300A" stroke-width="3" fill="none"/>'
    + acorn_icon(.92, 8, 6)
)

apple = (
    LEAFG + AUTUMN + rgd("fAppl", "#FFF0A0", "#FFB020", "#C8400A")
    + '<defs><radialGradient id="fBlush" cx=".7" cy=".55" r=".55"><stop offset="0" stop-color="#E8302A" stop-opacity=".95"/><stop offset="1" stop-color="#E8302A" stop-opacity="0"/></radialGradient></defs>'
    '<path d="M128,98 C112,82 70,84 66,122 C62,160 90,202 114,200 C122,199 124,194 128,194 C132,194 134,199 142,200 C166,202 194,160 190,122 C186,84 144,82 128,98Z" fill="url(#fAppl)"/>'
    '<path d="M128,98 C112,82 70,84 66,122 C62,160 90,202 114,200 C122,199 124,194 128,194 C132,194 134,199 142,200 C166,202 194,160 190,122 C186,84 144,82 128,98Z" fill="url(#fBlush)" stroke="#3A1004" stroke-width="7" stroke-linejoin="round"/>'
    '<path d="M86,114 C92,100 104,96 112,100 C100,108 96,120 94,134 C88,132 84,124 86,114Z" fill="#fff" opacity=".75"/>'
    '<circle cx="150" cy="150" r="2.5" fill="#FFE8A0"/><circle cx="160" cy="132" r="2.5" fill="#FFE8A0"/><circle cx="140" cy="170" r="2.5" fill="#FFE8A0"/><circle cx="168" cy="160" r="2.5" fill="#FFE8A0"/>'
    # branch
    '<path d="M128,100 C126,84 116,66 96,56" stroke="#2A1206" stroke-width="14" fill="none" stroke-linecap="round"/>'
    '<path d="M128,100 C126,84 116,66 96,56" stroke="#8A5A2A" stroke-width="7" fill="none" stroke-linecap="round"/>'
    '<path d="M118,70 C126,48 158,44 172,52 C162,76 134,82 118,70Z" fill="url(#fLeaf)" stroke="#14300A" stroke-width="5" stroke-linejoin="round"/>'
    '<path d="M122,70 C138,62 154,56 168,54" stroke="#14300A" stroke-width="3" fill="none"/>'
    '<path d="M104,62 C86,46 62,52 56,62 C70,80 94,78 104,62Z" fill="url(#fAut)" stroke="#3A1004" stroke-width="5" stroke-linejoin="round"/>'
)


def berry(cx, cy, r):
    return (f'<circle cx="{cx}" cy="{cy}" r="{r}" fill="url(#fBlue)" stroke="#0A0A2A" stroke-width="5"/>'
            f'<ellipse cx="{cx - r * .35:.1f}" cy="{cy - r * .38:.1f}" rx="{r * .32:.1f}" ry="{r * .2:.1f}" fill="#fff" opacity=".7" transform="rotate(-30 {cx - r * .35:.1f} {cy - r * .38:.1f})"/>'
            f'<polygon points="{" ".join(f"{cx + r * .3 * (1 if i % 2 == 0 else .4) * __import__("math").cos(__import__("math").radians(-90 + i * 36)):.1f},{cy + r * .25 + r * .3 * (1 if i % 2 == 0 else .4) * __import__("math").sin(__import__("math").radians(-90 + i * 36)):.1f}" for i in range(10))}" fill="#1A1440" stroke="#0A0A2A" stroke-width="2" stroke-linejoin="round"/>')


blueberry = (
    LEAFG + rgd("fBlue", "#B8C8FF", "#4A5AD8", "#16186A")
    + '<path d="M128,62 C140,44 176,40 196,52 C184,78 150,84 128,62Z" fill="url(#fLeaf)" stroke="#14300A" stroke-width="5" stroke-linejoin="round"/>'
    '<path d="M132,62 C152,54 172,52 192,54" stroke="#14300A" stroke-width="3" fill="none"/>'
    '<path d="M124,64 C108,44 72,42 58,58 C76,82 110,84 124,64Z" fill="url(#fLeaf)" stroke="#14300A" stroke-width="5" stroke-linejoin="round"/>'
    '<path d="M120,64 C104,58 84,56 64,58" stroke="#14300A" stroke-width="3" fill="none"/>'
    '<path d="M126,66 L128,92" stroke="#2A1206" stroke-width="8" stroke-linecap="round"/>'
    + berry(96, 116, 28) + berry(160, 116, 28) + berry(128, 102, 26)
    + berry(78, 160, 28) + berry(178, 160, 28) + berry(128, 140, 32) + berry(104, 186, 26) + berry(152, 186, 26)
)

chanterelle = (
    rgd("fChan", "#FFF6B0", "#FFC23A", "#D07A0A", cx=".4", cy=".15", r=".9") + lgd("fFun", "#E89A1A", "#FFD050") + LEAFG
    + '<path d="M44,204 C80,190 176,190 212,204 C200,216 56,216 44,204Z" fill="#4A8A2A" stroke="#14300A" stroke-width="5"/>'
    # small one behind
    + '<path d="M170,198 C172,184 170,176 164,166 C158,156 150,150 146,142 C152,136 166,140 176,138 C186,140 198,136 204,142 C200,150 192,156 188,166 C184,176 184,186 188,198Z" fill="url(#fChan)" stroke="#3A1E02" stroke-width="5" stroke-linejoin="round"/>'
    '<ellipse cx="175" cy="142" rx="26" ry="6" fill="url(#fFun)" stroke="#3A1E02" stroke-width="3"/>'
    # main funnel
    '<path d="M112,204 C114,186 114,170 108,156 C100,136 76,118 54,96 C50,86 62,78 74,82 C82,74 94,78 102,76 C110,70 120,74 128,72 C136,74 146,70 154,76 C162,78 174,74 182,82 C194,78 206,86 202,96 C180,118 156,136 148,156 C142,170 142,186 144,204Z" fill="url(#fChan)" stroke="#3A1E02" stroke-width="7" stroke-linejoin="round"/>'
    '<path d="M62,92 C70,84 84,88 94,84 C104,80 116,84 128,82 C140,84 152,80 162,84 C172,88 186,84 194,92 C186,104 160,110 128,110 C96,110 70,104 62,92Z" fill="url(#fFun)" stroke="#3A1E02" stroke-width="4" stroke-linejoin="round"/>'
    '<path d="M76,112 C94,128 108,140 116,160 M96,114 C108,128 118,144 122,166 M128,114 L128,170 M160,114 C148,128 138,144 134,166 M180,112 C162,128 148,140 140,160" stroke="#C8740A" stroke-width="3" fill="none" opacity=".8"/>'
    '<path d="M74,92 C90,96 104,98 118,98" stroke="#fff" stroke-width="4" fill="none" opacity=".55" stroke-linecap="round"/>'
    '<path d="M118,176 C118,186 118,194 118,200" stroke="#fff" stroke-width="5" opacity=".5" stroke-linecap="round"/>'
    '<path d="M66,208 C70,196 76,190 80,184 C82,194 84,200 88,208Z" fill="url(#fLeaf)" stroke="#14300A" stroke-width="4" stroke-linejoin="round"/>'
    + sparkle(200, 64, .7)
)

owl = (
    rgd("fOwl", "#E8C08A", "#A0683A", "#4A2810") + rgd("fBelly", "#FFF4D8", "#F2D8A8", "#C8A070")
    + '<path d="M128,50 C150,50 160,58 170,46 L174,76 C190,92 192,120 186,140 C178,166 156,176 128,176 C100,176 78,166 70,140 C64,120 66,92 82,76 L86,46 C96,58 106,50 128,50Z" fill="url(#fOwl)" stroke="#24120A" stroke-width="7" stroke-linejoin="round"/>'
    '<ellipse cx="128" cy="140" rx="36" ry="32" fill="url(#fBelly)" stroke="#24120A" stroke-width="4"/>'
    '<path d="M110,132 l6,6 l6,-6 M128,132 l6,6 l6,-6 M118,148 l6,6 l6,-6 M136,148 l-0,0" stroke="#A0683A" stroke-width="3" fill="none" stroke-linecap="round"/>'
    '<circle cx="104" cy="98" r="22" fill="#FFF4D8" stroke="#24120A" stroke-width="5"/><circle cx="152" cy="98" r="22" fill="#FFF4D8" stroke="#24120A" stroke-width="5"/>'
    '<circle cx="104" cy="98" r="13" fill="#FFB020"/><circle cx="152" cy="98" r="13" fill="#FFB020"/>'
    + eye(104, 98, 8) + eye(152, 98, 8)
    + '<path d="M120,112 L136,112 L128,128Z" fill="#FF9A1A" stroke="#3A1A02" stroke-width="4" stroke-linejoin="round"/>'
    '<path d="M74,120 C62,130 62,150 72,162 M182,120 C194,130 194,150 184,162" stroke="#24120A" stroke-width="5" fill="none" stroke-linecap="round"/>'
    '<path d="M104,172 l0,10 M114,172 l0,10 M142,172 l0,10 M152,172 l0,10" stroke="#FF9A1A" stroke-width="6" stroke-linecap="round"/>'
)

fox = (
    rgd("fFox", "#FFC890", "#F07A1A", "#A0360A")
    + '<path d="M66,58 L104,92 L80,120Z" fill="url(#fFox)" stroke="#2A0E02" stroke-width="6" stroke-linejoin="round"/>'
    '<path d="M190,58 L152,92 L176,120Z" fill="url(#fFox)" stroke="#2A0E02" stroke-width="6" stroke-linejoin="round"/>'
    '<path d="M74,72 L94,92 L82,106Z" fill="#3A1A0A"/><path d="M182,72 L162,92 L174,106Z" fill="#3A1A0A"/>'
    '<path d="M128,82 C160,82 182,96 192,116 C186,140 160,162 128,174 C96,162 70,140 64,116 C74,96 96,82 128,82Z" fill="url(#fFox)" stroke="#2A0E02" stroke-width="6" stroke-linejoin="round"/>'
    '<path d="M70,120 C88,124 108,134 128,174 C148,134 168,124 186,120 C178,146 156,164 128,174 C100,164 78,146 70,120Z" fill="#FFF6E8" stroke="#2A0E02" stroke-width="4" stroke-linejoin="round"/>'
    + eye(106, 118, 8) + eye(150, 118, 8)
    + '<ellipse cx="128" cy="166" rx="10" ry="7" fill="#1A0A04"/><ellipse cx="125" cy="163" rx="3" ry="2" fill="#fff"/>'
    '<ellipse cx="104" cy="96" rx="12" ry="5" fill="#fff" opacity=".4"/>'
)

SYMBOLS = {
    "j": letter("J", MOSS, AUTUMN + '<g transform="rotate(25 206 56)"><path d="M206,70 L206,98" stroke="#3A1004" stroke-width="6" stroke-linecap="round"/><path d="M206,24 L214,42 L232,36 L224,54 L238,62 L220,68 L224,86 L206,76 L188,86 L192,68 L174,62 L188,54 L180,36 L198,42Z" fill="url(#fAut)" stroke="#3A1004" stroke-width="5" stroke-linejoin="round"/><path d="M206,44 L206,74 M206,62 L222,48 M206,62 L190,48" stroke="#B0300A" stroke-width="2.5" fill="none"/></g>'),
    "q": letter("Q", ORANGE, NUTG + acorn_icon(.42, 118, 0)),
    "k": letter("K", BARK, LEAFG + '<path d="M186,80 C180,52 200,30 230,28 C234,58 214,80 186,80Z" fill="url(#fLeaf)" stroke="#14300A" stroke-width="5" stroke-linejoin="round"/><path d="M186,80 C198,64 212,48 226,34" stroke="#14300A" stroke-width="3" fill="none"/>'),
    "a": letter("A", BERRY, (rgd("fBlue", "#B8C8FF", "#4A5AD8", "#16186A") + berry(200, 52, 16) + berry(222, 64, 14) + berry(206, 76, 14))),
    "acorn": medallion("circle", MOSS, acorn),
    "apple": medallion("shield", PINE, apple),
    "blueberry": medallion("hex", ORANGE, blueberry),
    "chanterelle": medallion("square", PINE, chanterelle),
    "wild": wild(MOSS, owl),
    "scatter": scatter(PINE, fox, "BONUS"),
}
