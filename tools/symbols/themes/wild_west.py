"""Wild West: leather brown, sunset orange and turquoise."""
import math

from symkit import P, medallion, outlined_text, scatter, sparkle, star, star_pts, suit, wild

LEATHER = P("#F4D2A0", "#A8682C", "#4A2A0E", "#24140A")
SUNSET = P("#FFDCA0", "#F27A2A", "#8A2A0A", "#3A1006")
TURQ = P("#C8FFF6", "#2FC0B4", "#0A5A5A", "#04282A")
DUSTRED = P("#FFBCA4", "#D4402A", "#6A1208", "#300804")
SAND = P("#FFF0C8", "#E0B060", "#7A4A18", "#30180A")
DUSK = P("#FFC8B0", "#C0507A", "#4A1640", "#200A20")

INK = "#2A1406"

DEFS = ('<defs>'
        '<linearGradient id="wood" x1="0" y1="0" x2="1" y2="1"><stop offset="0" stop-color="#C88A4A"/><stop offset=".5" stop-color="#8A4E1E"/><stop offset="1" stop-color="#4E2A0C"/></linearGradient>'
        '<linearGradient id="leather" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#D49A5A"/><stop offset=".55" stop-color="#9A5A24"/><stop offset="1" stop-color="#5A300E"/></linearGradient>'
        '<linearGradient id="felt" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#C8905A"/><stop offset=".6" stop-color="#8A5428"/><stop offset="1" stop-color="#4E2A10"/></linearGradient>'
        '<linearGradient id="burlap" x1="0" y1="0" x2="1" y2="1"><stop offset="0" stop-color="#F2DCA8"/><stop offset=".55" stop-color="#C8A066"/><stop offset="1" stop-color="#7A5A2E"/></linearGradient>'
        '<linearGradient id="gun" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#F4F8FF"/><stop offset=".45" stop-color="#A8B4C8"/><stop offset="1" stop-color="#4A5468"/></linearGradient>'
        '<linearGradient id="turq" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#9AF8EC"/><stop offset="1" stop-color="#1A9A94"/></linearGradient>'
        '</defs>')

# ---------------------------------------------------------------- money bag
bag = (
    DEFS
    + '<path d="M100,98 C58,122 52,198 128,202 C204,198 198,122 156,98Z" fill="url(#burlap)" stroke="#3A2408" stroke-width="7" stroke-linejoin="round"/>'
    + '<path d="M100,98 C66,124 62,186 104,198 C82,176 82,130 112,102Z" fill="#fff" opacity=".25"/>'
    + '<path d="M104,98 C92,82 94,62 108,68 C114,56 142,56 148,68 C162,62 164,82 152,98Z" fill="url(#burlap)" stroke="#3A2408" stroke-width="6" stroke-linejoin="round"/>'
    + '<path d="M96,96 C116,106 140,106 160,96" stroke="#7A2A10" stroke-width="12" fill="none" stroke-linecap="round"/>'
    + '<path d="M96,96 C116,106 140,106 160,96" stroke="#D04A2A" stroke-width="6" fill="none" stroke-linecap="round"/>'
    + '<path d="M150,102 C162,116 168,122 176,120" stroke="#D04A2A" stroke-width="5" fill="none" stroke-linecap="round"/>'
    + '<path d="M84,170 L92,168 M168,150 L176,152 M110,186 L114,192" stroke="#7A5A2E" stroke-width="3" stroke-linecap="round"/>'
    + outlined_text("$", 128, 182, 82, "url(#gold)", "#3A1A00", 13)
)

# ---------------------------------------------------------------- hip flask
flask = (
    DEFS
    + '<rect x="110" y="56" width="36" height="22" rx="4" fill="url(#gun)" stroke="#1A2030" stroke-width="5"/>'
    + '<path d="M114,62 L142,62 M114,70 L142,70" stroke="#5A6478" stroke-width="2.5"/>'
    + '<path d="M80,90 C80,76 176,76 176,90 L184,184 C184,202 72,202 72,184Z" fill="url(#gun)" stroke="#1A2030" stroke-width="7" stroke-linejoin="round"/>'
    + '<path d="M90,92 C90,86 108,84 120,84 L112,188 C96,188 84,186 84,180Z" fill="#fff" opacity=".45"/>'
    + '<path d="M77,118 L179,118 L182,164 L74,164Z" fill="url(#leather)" stroke="#2A1406" stroke-width="5" stroke-linejoin="round"/>'
    + '<path d="M82,125 L174,125 M80,157 L176,157" stroke="#F4D2A0" stroke-width="2.5" stroke-dasharray="6 5"/>'
    + star(128, 141, 17, "url(#gold)", "#5A2A00", 3)
    + '<circle cx="128" cy="141" r="4" fill="url(#turq)" stroke="#04282A" stroke-width="1.5"/>'
)

# ---------------------------------------------------------------- revolver
revolver = (
    DEFS
    + '<g transform="rotate(-14 128 128) translate(-6,6)">'
    # grip
    + '<path d="M72,104 L104,124 C94,146 92,170 100,192 C92,204 64,204 56,190 C50,160 56,128 72,104Z" fill="url(#wood)" stroke="#2A1406" stroke-width="6" stroke-linejoin="round"/>'
    + '<path d="M68,124 C62,146 62,170 66,186" stroke="#E0A868" stroke-width="4" fill="none" stroke-linecap="round" opacity=".8"/>'
    + '<circle cx="80" cy="160" r="5" fill="url(#gold)" stroke="#2A1406" stroke-width="2"/>'
    # trigger guard + trigger
    + '<path d="M100,124 C98,152 128,154 130,128" stroke="#1A2030" stroke-width="10" fill="none" stroke-linecap="round"/>'
    + '<path d="M100,124 C98,152 128,154 130,128" stroke="url(#gold)" stroke-width="5" fill="none" stroke-linecap="round"/>'
    + '<path d="M112,124 C110,134 112,140 118,142" stroke="#1A2030" stroke-width="5" fill="none" stroke-linecap="round"/>'
    # hammer
    + '<path d="M80,96 L64,80 L72,74 L92,92Z" fill="url(#gun)" stroke="#1A2030" stroke-width="5" stroke-linejoin="round"/>'
    # barrel + rod
    + '<rect x="126" y="134" width="0" height="0"/>'
    + '<rect x="132" y="112" width="64" height="10" rx="3" fill="#7A869C" stroke="#1A2030" stroke-width="4"/>'
    + '<rect x="120" y="94" width="92" height="20" rx="4" fill="url(#gun)" stroke="#1A2030" stroke-width="6"/>'
    + '<path d="M204,94 L206,84 L212,84 L212,94Z" fill="#7A869C" stroke="#1A2030" stroke-width="3" stroke-linejoin="round"/>'
    # frame
    + '<path d="M74,90 L128,90 L132,126 L100,130 L72,108Z" fill="url(#gun)" stroke="#1A2030" stroke-width="6" stroke-linejoin="round"/>'
    # cylinder
    + '<rect x="94" y="90" width="42" height="38" rx="9" fill="url(#gun)" stroke="#1A2030" stroke-width="6"/>'
    + '<path d="M106,94 L106,124 M118,94 L118,124" stroke="#4A5468" stroke-width="4"/>'
    + '<path d="M98,98 L132,98" stroke="#fff" stroke-width="3" opacity=".8"/>'
    + '<path d="M124,100 L208,100" stroke="#fff" stroke-width="4" opacity=".8" stroke-linecap="round"/>'
    + '</g>'
)

# ---------------------------------------------------------------- cowboy hat
hat = (
    DEFS
    + '<path d="M34,146 C40,172 216,172 222,146 C226,128 206,116 194,128 C172,146 84,146 62,128 C50,116 30,128 34,146Z" fill="url(#felt)" stroke="#24140A" stroke-width="7" stroke-linejoin="round"/>'
    + '<path d="M80,134 C74,96 84,64 104,60 C116,58 120,72 128,72 C136,72 140,58 152,60 C172,64 182,96 176,134 C150,146 106,146 80,134Z" fill="url(#felt)" stroke="#24140A" stroke-width="7" stroke-linejoin="round"/>'
    + '<path d="M128,74 C126,92 126,110 128,124" stroke="#4E2A10" stroke-width="4" fill="none" stroke-linecap="round"/>'
    + '<path d="M96,72 C88,84 86,100 88,118" stroke="#E8B880" stroke-width="5" fill="none" stroke-linecap="round" opacity=".8"/>'
    + '<path d="M80,118 C106,128 150,128 176,118 L177,134 C150,146 106,146 79,134Z" fill="url(#turq)" stroke="#24140A" stroke-width="5" stroke-linejoin="round"/>'
    + '<circle cx="128" cy="132" r="10" fill="url(#gold)" stroke="#5A2A00" stroke-width="3.5"/>'
    + '<circle cx="128" cy="132" r="3.5" fill="#D4402A"/>'
    + '<path d="M50,150 C80,164 176,164 206,150" stroke="#E8B880" stroke-width="4" fill="none" stroke-linecap="round" opacity=".6"/>'
)

# ---------------------------------------------------------------- wild: sheriff star
def sheriff(cx, cy, r):
    pts = star_pts(cx, cy, r, 6, .56)
    balls = "".join(f'<circle cx="{cx + r * math.cos(math.radians(-90 + 60 * i)):.1f}" cy="{cy + r * math.sin(math.radians(-90 + 60 * i)):.1f}" r="{r * .14:.1f}" fill="url(#gold)" stroke="#5A2A00" stroke-width="4"/>' for i in range(6))
    return (f'<polygon points="{pts}" fill="url(#gold)" stroke="#5A2A00" stroke-width="6" stroke-linejoin="round" filter="url(#drop)"/>'
            + balls
            + f'<circle cx="{cx}" cy="{cy}" r="{r * .42:.1f}" fill="url(#turq)" stroke="#5A2A00" stroke-width="4"/>'
            + star(cx, cy, r * .3, "url(#gold)", "#5A2A00", 3)
            + f'<polygon points="{star_pts(cx, cy, r * .8, 6, .56)}" fill="none" stroke="#B86B00" stroke-width="2.5" stroke-linejoin="round"/>')


wild_emblem = DEFS + sheriff(128, 98, 66) + sparkle(64, 50, .8) + sparkle(196, 64, .7)

# ---------------------------------------------------------------- scatter: horseshoe
horseshoe_d = "M90,70 C70,100 74,150 100,168 C116,178 140,178 156,168 C182,150 186,100 166,70 L146,76 C160,100 158,138 142,150 C134,156 122,156 114,150 C98,138 96,100 110,76Z"
scatter_emblem = (
    DEFS
    + f'<path d="{horseshoe_d}" fill="#3A1A00" stroke="#3A1A00" stroke-width="12" stroke-linejoin="round" filter="url(#drop)"/>'
    + f'<path d="{horseshoe_d}" fill="url(#gold)" stroke="#5A2A00" stroke-width="4" stroke-linejoin="round"/>'
    + "".join(f'<rect x="{x - 3}" y="{y - 5}" width="6" height="10" rx="2" fill="#5A2A00"/>' for x, y in ((92, 104), (98, 132), (112, 156), (164, 104), (158, 132), (144, 156)))
    + '<path d="M96,80 C84,104 86,136 100,154" stroke="#fff" stroke-width="5" fill="none" stroke-linecap="round" opacity=".7"/>'
    + '<rect x="86" y="62" width="28" height="16" rx="4" fill="url(#gold)" stroke="#5A2A00" stroke-width="4" transform="rotate(14 100 70)"/>'
    + '<rect x="142" y="62" width="28" height="16" rx="4" fill="url(#gold)" stroke="#5A2A00" stroke-width="4" transform="rotate(-14 156 70)"/>'
    + sparkle(176, 56, .8) + sparkle(76, 150, .6)
)

SYMBOLS = {
    "crosses": suit("club", TURQ),
    "diamonds": suit("diamond", SUNSET),
    "hearts": suit("heart", DUSTRED),
    "spades": suit("spade", LEATHER),
    "bag": medallion("circle", SAND, bag),
    "flask": medallion("shield", TURQ, flask),
    "gun": medallion("square", DUSK, revolver),
    "hat": medallion("hex", SUNSET, hat),
    "wild": wild(LEATHER, wild_emblem),
    "scatter": scatter(TURQ, scatter_emblem, "BONUS"),
}
