"""Witch: spooky purple, toxic green and orange glow."""
from symkit import P, gem, medallion, scatter, sparkle, star, wild

PURPLE = P("#E4CCFF", "#7A3AC8", "#2A0A5A", "#14062E")
TOXIC = P("#E4FFB4", "#5AC82A", "#1A5A0A", "#0A2604")
ORANGE = P("#FFE0A0", "#FF8A1A", "#8A3004", "#2E1004")
NIGHT = P("#B8C4F0", "#3A3A96", "#120E3E", "#08061E")
PLUM = P("#FFC8F0", "#C03AA0", "#4E0A4A", "#22041E")
TEAL = P("#C8FFF0", "#2AA890", "#0A4440", "#041E1C")

INK = "#14062E"

DEFS = ('<defs>'
        '<linearGradient id="wood" x1="0" y1="0" x2="1" y2="0"><stop offset="0" stop-color="#5A3010"/><stop offset=".5" stop-color="#A86A30"/><stop offset="1" stop-color="#4A240A"/></linearGradient>'
        '<linearGradient id="cork" x1="0" y1="0" x2="1" y2="0"><stop offset="0" stop-color="#8A5A2A"/><stop offset=".5" stop-color="#D8A060"/><stop offset="1" stop-color="#7A4A1A"/></linearGradient>'
        '<linearGradient id="straw" x1="0" y1="0" x2="1" y2="0"><stop offset="0" stop-color="#B8801E"/><stop offset=".5" stop-color="#FFE07A"/><stop offset="1" stop-color="#B0761A"/></linearGradient>'
        '<linearGradient id="parch" x1="0" y1="0" x2="1" y2="1"><stop offset="0" stop-color="#FFF6DA"/><stop offset=".6" stop-color="#F0D8A0"/><stop offset="1" stop-color="#C89A58"/></linearGradient>'
        '<linearGradient id="wax" x1="0" y1="0" x2="1" y2="0"><stop offset="0" stop-color="#E8DCC8"/><stop offset=".45" stop-color="#FFFBF0"/><stop offset="1" stop-color="#C8B8A0"/></linearGradient>'
        '<linearGradient id="hatG" x1="0" y1="0" x2="1" y2="1"><stop offset="0" stop-color="#9A6AE0"/><stop offset=".55" stop-color="#4E1E96"/><stop offset="1" stop-color="#220A4A"/></linearGradient>'
        '<radialGradient id="flame" cx=".5" cy=".7" r=".6"><stop offset="0" stop-color="#FFFFE0"/><stop offset=".45" stop-color="#FFD040"/><stop offset="1" stop-color="#FF6A00"/></radialGradient>'
        '<radialGradient id="orb" cx=".38" cy=".32" r=".75"><stop offset="0" stop-color="#F0E4FF"/><stop offset=".5" stop-color="#9A6AF0"/><stop offset="1" stop-color="#2A0A6A"/></radialGradient>'
        '<radialGradient id="pot" cx=".4" cy=".3" r=".8"><stop offset="0" stop-color="#6A6A80"/><stop offset=".6" stop-color="#2A2A3A"/><stop offset="1" stop-color="#101018"/></radialGradient>'
        '</defs>')


# ---------------------------------------------------------------- potion bottles
def bottle(d, level, c1, c2, stopper, neck=(116, 140, 58)):
    nx0, nx1, ny = neck
    lid = (f'<defs><linearGradient id="liq" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="{c1}"/><stop offset="1" stop-color="{c2}"/></linearGradient>'
           f'<clipPath id="bclip"><path d="{d}"/></clipPath></defs>')
    wave = f'M40,{level} C64,{level - 8} 88,{level + 8} 112,{level} C136,{level - 8} 160,{level + 8} 184,{level} C200,{level - 6} 210,{level} 220,{level} L220,230 L40,230Z'
    bubbles = "".join(f'<circle cx="{x}" cy="{y}" r="{r}" fill="#fff" opacity=".7"/>' for x, y, r in ((112, level + 30, 6), (140, level + 18, 4), (128, level + 48, 3.5), (150, level + 44, 5)))
    return (DEFS + lid
            + f'<path d="{d}" fill="{c1}" filter="url(#glow)" opacity=".9"/>'
            + f'<path d="{d}" fill="#E8F4FF" fill-opacity=".28" stroke="{INK}" stroke-width="13" stroke-linejoin="round"/>'
            + f'<path d="{d}" fill="#E8F4FF" fill-opacity=".28"/>'
            + f'<g clip-path="url(#bclip)"><path d="{wave}" fill="url(#liq)"/>'
            + f'<path d="M40,{level} C64,{level - 8} 88,{level + 8} 112,{level} C136,{level - 8} 160,{level + 8} 184,{level} C200,{level - 6} 210,{level} 220,{level}" stroke="#fff" stroke-width="4" fill="none" opacity=".75"/>'
            + bubbles + '</g>'
            + f'<path d="{d}" fill="none" stroke="#fff" stroke-opacity=".5" stroke-width="3"/>'
            + f'<path d="M{nx0 + 6},{ny + 10} L{nx0 + 6},{ny + 36}" stroke="#fff" stroke-width="5" stroke-linecap="round" opacity=".8"/>'
            + f'<rect x="{nx0 - 6}" y="{ny - 2}" width="{nx1 - nx0 + 12}" height="12" rx="5" fill="#C8D8F0" stroke="{INK}" stroke-width="5"/>'
            + stopper)


def cork(x0=112, x1=144, y=40):
    return (f'<path d="M{x0},{y} L{x1},{y} L{x1 - 3},{y + 22} L{x0 + 3},{y + 22}Z" fill="url(#cork)" stroke="#2A1406" stroke-width="5" stroke-linejoin="round"/>'
            f'<path d="M{x0 + 6},{y + 8} L{x1 - 8},{y + 8}" stroke="#5A3010" stroke-width="2"/>')


ROUND = "M116,64 L116,104 C90,112 76,132 76,154 C76,184 100,204 128,204 C156,204 180,184 180,154 C180,132 166,112 140,104 L140,64Z"
CONE = "M114,64 L114,108 L76,190 C72,200 78,206 88,206 L168,206 C178,206 184,200 180,190 L142,108 L142,64Z"
HEART = "M116,64 L116,96 C102,84 70,88 70,124 C70,154 104,178 128,206 C152,178 186,154 186,124 C186,88 154,84 140,96 L140,64Z"
TEAR = "M118,64 L118,88 C96,98 86,122 90,152 C94,182 108,202 128,206 C148,202 162,182 166,152 C170,122 160,98 138,88 L138,64Z"

skull_label = ('<rect x="104" y="150" width="48" height="36" rx="6" fill="#F4ECD0" stroke="#1A2A0A" stroke-width="4"/>'
               '<circle cx="128" cy="164" r="10" fill="#1A2A0A"/><rect x="122" y="170" width="12" height="8" fill="#1A2A0A"/>'
               '<circle cx="124" cy="164" r="3" fill="#F4ECD0"/><circle cx="132" cy="164" r="3" fill="#F4ECD0"/>')

blue_bottle = bottle(ROUND, 138, "#8AE8FF", "#1A5AE0", cork()) + sparkle(184, 84, .6)
green_bottle = bottle(CONE, 140, "#C8FF6A", "#2A9A10", cork(), (114, 142, 58)) + skull_label
magenta_bottle = (bottle(HEART, 118, "#FFA0E8", "#D0189A", '<path d="M110,42 C110,30 146,30 146,42 L142,62 L114,62Z" fill="#FF6AC8" stroke="#3A0430" stroke-width="5" stroke-linejoin="round"/>'
                         '<ellipse cx="120" cy="40" rx="6" ry="3" fill="#fff" opacity=".7"/>')
                  + sparkle(70, 72, .6) + sparkle(190, 176, .5))
violet_bottle = (bottle(TEAR, 120, "#E0B0FF", "#7A1AE0", f'<rect x="122" y="46" width="12" height="16" fill="url(#gold)" stroke="#3A1A00" stroke-width="3"/>' + star(128, 40, 16, "url(#gold)", "#5A2A00", 4), (118, 138, 58))
                 + sparkle(176, 86, .6))

# ---------------------------------------------------------------- candles
def candle(x, top, w, h_flame=1.0):
    bottom = 186
    drip = (f'M{x - w / 2},{top + 6} L{x - w / 2},{top + 26} C{x - w / 2},{top + 34} {x - w / 2 + 8},{top + 34} {x - w / 2 + 8},{top + 26} '
            f'L{x - w / 2 + 8},{top + 14} C{x - w / 2 + 10},{top + 10} {x + 2},{top + 10} {x + 4},{top + 16} L{x + 4},{top + 44} C{x + 4},{top + 52} {x + 12},{top + 52} {x + 12},{top + 44} '
            f'L{x + 12},{top + 18} C{x + 14},{top + 12} {x + w / 2},{top + 12} {x + w / 2},{top + 20} L{x + w / 2},{top + 6}Z')
    return (f'<ellipse cx="{x}" cy="{top - 26}" rx="{14 * h_flame:.0f}" ry="{22 * h_flame:.0f}" fill="#FFB020" filter="url(#glow)" opacity=".9"/>'
            f'<rect x="{x - w / 2}" y="{top}" width="{w}" height="{bottom - top}" rx="4" fill="url(#wax)" stroke="{INK}" stroke-width="5"/>'
            f'<ellipse cx="{x}" cy="{top + 2}" rx="{w / 2}" ry="5" fill="#FFFBF0" stroke="{INK}" stroke-width="4"/>'
            f'<path d="{drip}" fill="#FFFBF0" stroke="{INK}" stroke-width="3.5" stroke-linejoin="round"/>'
            f'<path d="M{x},{top - 2} L{x},{top - 10}" stroke="#2A1406" stroke-width="3"/>'
            f'<path d="M{x},{top - 12} C{x - 10 * h_flame},{top - 16} {x - 6 * h_flame},{top - 32 * h_flame} {x},{top - 44 * h_flame} C{x + 6 * h_flame},{top - 32 * h_flame} {x + 10 * h_flame},{top - 16} {x},{top - 12}Z" fill="url(#flame)" stroke="#8A3004" stroke-width="3" stroke-linejoin="round"/>')


candles = (
    DEFS
    + candle(90, 120, 30, .9) + candle(166, 132, 28, .85) + candle(128, 96, 36, 1.0)
    + '<ellipse cx="128" cy="192" rx="76" ry="16" fill="#3A3A50" stroke="#0A0A14" stroke-width="6"/>'
    + '<ellipse cx="128" cy="188" rx="68" ry="10" fill="#6A6A86"/>'
    + '<path d="M198,190 C214,188 216,204 200,202" stroke="#0A0A14" stroke-width="6" fill="none"/>'
)

# ---------------------------------------------------------------- broom
broom = (
    DEFS
    + '<g transform="rotate(38 128 128)">'
    + '<rect x="121" y="30" width="14" height="126" rx="6" fill="url(#wood)" stroke="#2A1406" stroke-width="5"/>'
    + '<path d="M126,40 L126,150" stroke="#E0A060" stroke-width="3" opacity=".7"/>'
    + '<path d="M112,150 L144,150 C160,176 172,204 170,222 C150,228 106,228 86,222 C84,204 96,176 112,150Z" fill="url(#straw)" stroke="#3A2006" stroke-width="6" stroke-linejoin="round"/>'
    + '<path d="M118,160 L104,220 M128,160 L124,224 M138,160 L148,222 M112,170 L94,218 M146,172 L162,218" stroke="#8A5A0A" stroke-width="3" stroke-linecap="round"/>'
    + '<rect x="108" y="148" width="40" height="14" rx="4" fill="#7A2AC8" stroke="#14062E" stroke-width="4"/>'
    + '<rect x="110" y="166" width="36" height="10" rx="3" fill="#7A2AC8" stroke="#14062E" stroke-width="4"/>'
    + '</g>'
    + star(72, 82, 12, "url(#gold)", "#5A2A00", 3) + sparkle(196, 70, .7) + sparkle(64, 150, .5)
)

# ---------------------------------------------------------------- scroll
scroll = (
    DEFS
    + '<path d="M80,72 L176,72 L176,184 L80,184Z" fill="url(#parch)" stroke="#3A2006" stroke-width="6" stroke-linejoin="round"/>'
    + '<rect x="66" y="58" width="124" height="22" rx="11" fill="url(#parch)" stroke="#3A2006" stroke-width="6"/>'
    + '<ellipse cx="66" cy="69" rx="7" ry="11" fill="#C89A58" stroke="#3A2006" stroke-width="5"/><ellipse cx="190" cy="69" rx="7" ry="11" fill="#C89A58" stroke="#3A2006" stroke-width="5"/>'
    + '<rect x="66" y="176" width="124" height="22" rx="11" fill="url(#parch)" stroke="#3A2006" stroke-width="6"/>'
    + '<ellipse cx="66" cy="187" rx="7" ry="11" fill="#C89A58" stroke="#3A2006" stroke-width="5"/><ellipse cx="190" cy="187" rx="7" ry="11" fill="#C89A58" stroke="#3A2006" stroke-width="5"/>'
    # crescent moon + star sigil
    + '<path d="M120,94 C102,98 96,122 110,134 C120,142 136,140 142,130 C130,134 116,128 114,116 C112,106 116,98 120,94Z" fill="#7A2AC8" stroke="#2A0A5A" stroke-width="3"/>'
    + star(140, 106, 9, "#7A2AC8", "#2A0A5A", 2)
    + '<path d="M94,152 C104,146 112,158 122,152 C132,146 140,158 150,152 C156,148 160,150 164,152" stroke="#5A3A1A" stroke-width="4" fill="none" stroke-linecap="round"/>'
    + '<path d="M94,166 C104,160 112,172 122,166 C128,162 134,164 140,166" stroke="#5A3A1A" stroke-width="4" fill="none" stroke-linecap="round"/>'
    + '<circle cx="164" cy="174" r="15" fill="#C8202A" stroke="#4A0408" stroke-width="4"/>'
    + '<circle cx="164" cy="174" r="8" fill="none" stroke="#7A0A10" stroke-width="3"/>'
    + '<path d="M156,186 L152,204 L160,198 L164,206 L166,188Z" fill="#C8202A" stroke="#4A0408" stroke-width="3" stroke-linejoin="round"/>'
    + '<ellipse cx="159" cy="168" rx="4" ry="2.5" fill="#fff" opacity=".7"/>'
)

# ---------------------------------------------------------------- witch hat
hat = (
    DEFS
    + '<ellipse cx="128" cy="172" rx="88" ry="24" fill="url(#hatG)" stroke="#0A0418" stroke-width="7"/>'
    + '<path d="M84,166 C96,130 110,96 120,72 C126,58 140,46 160,44 C178,44 190,54 196,66 C182,58 168,58 160,66 C152,76 156,120 172,166 C150,178 106,178 84,166Z" fill="url(#hatG)" stroke="#0A0418" stroke-width="7" stroke-linejoin="round"/>'
    + '<path d="M110,110 C116,92 122,78 130,68" stroke="#C8A0FF" stroke-width="5" fill="none" stroke-linecap="round" opacity=".7"/>'
    + '<path d="M88,150 C110,160 146,160 168,150 L172,166 C150,178 106,178 84,166Z" fill="#FF8A1A" stroke="#0A0418" stroke-width="5" stroke-linejoin="round"/>'
    + '<rect x="114" y="146" width="28" height="26" rx="4" fill="none" stroke="#0A0418" stroke-width="10"/>'
    + '<rect x="114" y="146" width="28" height="26" rx="4" fill="none" stroke="url(#gold)" stroke-width="5"/>'
    + '<rect x="125" y="150" width="6" height="18" fill="#0A0418"/>'
    + star(196, 70, 9, "url(#gold)", "#5A2A00", 2.5) + sparkle(62, 104, .6)
)

# ---------------------------------------------------------------- wild: cauldron
cauldron = (
    DEFS
    + '<ellipse cx="128" cy="78" rx="56" ry="26" fill="#9AFF4A" filter="url(#glow)" opacity=".9"/>'
    + '<circle cx="110" cy="58" r="9" fill="#B4FF6A" stroke="#1A5A0A" stroke-width="3"/><circle cx="146" cy="50" r="12" fill="#B4FF6A" stroke="#1A5A0A" stroke-width="3"/><circle cx="130" cy="38" r="6" fill="#B4FF6A" stroke="#1A5A0A" stroke-width="3"/>'
    + '<path d="M68,92 C60,130 80,160 128,160 C176,160 196,130 188,92Z" fill="url(#pot)" stroke="#06060C" stroke-width="7" stroke-linejoin="round"/>'
    + '<path d="M84,104 C80,124 90,140 106,148" stroke="#9A9AB4" stroke-width="5" fill="none" stroke-linecap="round" opacity=".7"/>'
    + '<path d="M90,154 L82,168 M166,154 L174,168" stroke="#06060C" stroke-width="9" stroke-linecap="round"/>'
    + '<ellipse cx="128" cy="90" rx="66" ry="16" fill="#3A3A4E" stroke="#06060C" stroke-width="7"/>'
    + '<ellipse cx="128" cy="88" rx="54" ry="9" fill="#7AE82A"/>'
    + '<path d="M84,88 C96,80 108,96 122,86 C134,78 146,94 160,86 C166,82 170,84 172,88" stroke="#D4FF8A" stroke-width="4" fill="none"/>'
    + '<path d="M118,84 C114,94 120,104 118,114" stroke="#7AE82A" stroke-width="8" fill="none" stroke-linecap="round"/>'
)

# ---------------------------------------------------------------- scatter: crystal ball
crystal = (
    DEFS
    + '<path d="M92,156 L164,156 L176,180 L80,180Z" fill="url(#gold)" stroke="#3A1A00" stroke-width="6" stroke-linejoin="round"/>'
    + '<circle cx="128" cy="108" r="54" fill="#B080FF" filter="url(#glow)" opacity=".8"/>'
    + '<circle cx="128" cy="108" r="50" fill="url(#orb)" stroke="#14062E" stroke-width="6"/>'
    + star(128, 112, 16, "#FFF6C0", "#7A3A00", 2) + sparkle(104, 128, .6) + sparkle(152, 92, .5) + sparkle(110, 92, .4)
    + '<path d="M96,84 C104,72 116,66 128,66" stroke="#fff" stroke-width="7" fill="none" stroke-linecap="round" opacity=".8"/>'
    + '<rect x="96" y="152" width="64" height="10" rx="4" fill="url(#gold)" stroke="#3A1A00" stroke-width="4"/>'
)

def fit(icon, k=.86, cy=134):
    return f'<g transform="translate(128,{cy}) scale({k}) translate(-128,-{cy})">{icon}</g>'


SYMBOLS = {
    "bluebottle": medallion("circle", NIGHT, blue_bottle),
    "greenbottle": medallion("shield", PURPLE, green_bottle),
    "margentebottle": medallion("hex", TEAL, magenta_bottle),
    "violetbottle": medallion("diamond", ORANGE, fit(violet_bottle)),
    "candles": medallion("square", PLUM, candles),
    "broom": medallion("circle", TEAL, broom),
    "scroll": medallion("shield", NIGHT, scroll),
    "hat": medallion("hex", TOXIC, hat),
    "wild": wild(TOXIC, cauldron),
    "scatter": scatter(PURPLE, crystal, "BONUS"),
}
