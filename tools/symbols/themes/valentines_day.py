"""Valentine's Day: romantic pink, red, gold and white."""
import math

from symkit import P, gem, letter, medallion, scatter, sparkle, star, suit, wild

PINK = P("#FFD0E6", "#FF5FA2", "#9A0F55", "#3A0420")
RED = P("#FFB0B0", "#E8203A", "#7A0616", "#3A0408")
ROSE = P("#FFE0EE", "#F28AB8", "#A8306A", "#40081F")
PLUM = P("#F0C8FF", "#B04AD6", "#4E0E72", "#22063A")
MAGENTA = P("#FFC0F0", "#E03AB4", "#6E0A56", "#30042A")
CREAM = P("#FFF4F0", "#F7C6CF", "#C25A78", "#4A0E22")

INK = "#3A0A1E"
HEART_D = "M0,30 C-22,14 -34,2 -34,-12 C-34,-24 -25,-32 -15,-32 C-8,-32 -3,-28 0,-22 C3,-28 8,-32 15,-32 C25,-32 34,-24 34,-12 C34,2 22,14 0,30Z"


def heart(cx, cy, s, fill="#E8203A", ink=INK, sw=None):
    sw = sw if sw is not None else 6 / s
    return (f'<g transform="translate({cx},{cy}) scale({s})"><path d="{HEART_D}" fill="{fill}" stroke="{ink}" stroke-width="{sw:.1f}" stroke-linejoin="round"/>'
            f'<ellipse cx="-14" cy="-16" rx="9" ry="5" fill="#fff" opacity=".6" transform="rotate(-35 -14 -16)"/></g>')


def petals(cx, cy, n, length, width, fill, ink, sw, rot=0, base=0):
    out = []
    for i in range(n):
        a = rot + i * 360 / n
        out.append(f'<ellipse cx="{cx}" cy="{cy - base - length / 2:.1f}" rx="{width / 2:.1f}" ry="{length / 2:.1f}" fill="{fill}" stroke="{ink}" stroke-width="{sw}" transform="rotate({a:.1f} {cx} {cy})"/>')
    return "".join(out)


def leaf(x, y, ang, s=1.0):
    return (f'<g transform="translate({x},{y}) rotate({ang}) scale({s})"><path d="M0,0 C14,-18 44,-20 62,0 C44,20 14,18 0,0Z" fill="url(#leafG)" stroke="#0E3A12" stroke-width="5" stroke-linejoin="round"/>'
            f'<path d="M4,0 L54,0" stroke="#0E3A12" stroke-width="3" fill="none" opacity=".6"/></g>')


LEAF_DEFS = ('<defs><linearGradient id="leafG" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#9BE87A"/><stop offset="1" stop-color="#2A8A3A"/></linearGradient>'
             '<radialGradient id="ctrG" cx=".4" cy=".35" r=".7"><stop offset="0" stop-color="#FFF6A0"/><stop offset=".6" stop-color="#FFC21A"/><stop offset="1" stop-color="#C46A00"/></radialGradient>'
             '<linearGradient id="petA" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#F6D8FF"/><stop offset="1" stop-color="#A65AE0"/></linearGradient>'
             '<linearGradient id="petB" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#FFFFFF"/><stop offset="1" stop-color="#D78AF0"/></linearGradient>'
             '<linearGradient id="calla" x1="0" y1="0" x2="1" y2="1"><stop offset="0" stop-color="#FFFFFF"/><stop offset=".6" stop-color="#FFE0EC"/><stop offset="1" stop-color="#F08AB4"/></linearGradient>'
             '<linearGradient id="callaIn" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#FFB8D4"/><stop offset="1" stop-color="#E05A92"/></linearGradient>'
             '<linearGradient id="lilyP" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#FFFFFF"/><stop offset=".5" stop-color="#FFC6DE"/><stop offset="1" stop-color="#F0508E"/></linearGradient>'
             '<radialGradient id="roseG" cx=".45" cy=".35" r=".75"><stop offset="0" stop-color="#FF8A8A"/><stop offset=".55" stop-color="#E01A34"/><stop offset="1" stop-color="#7A0414"/></radialGradient>'
             '</defs>')

# ---------------------------------------------------------------- aster
aster = (
    LEAF_DEFS
    + leaf(128, 182, 25, 1.0) + leaf(128, 182, 155, 1.0)
    + petals(128, 122, 20, 62, 18, "url(#petA)", "#3E0A5A", 4, 0, 20)
    + petals(128, 122, 20, 46, 13, "url(#petB)", "#5A1A7A", 3, 9, 16)
    + '<circle cx="128" cy="122" r="26" fill="url(#ctrG)" stroke="#5A2A00" stroke-width="5"/>'
    + "".join(f'<circle cx="{128 + 12 * math.cos(math.radians(a)):.1f}" cy="{124 + 12 * math.sin(math.radians(a)):.1f}" r="2.6" fill="#B85A00"/>' for a in range(0, 360, 45))
    + '<ellipse cx="120" cy="116" rx="8" ry="5" fill="#fff" opacity=".7"/>'
)

# ---------------------------------------------------------------- calla lily
calla = (
    LEAF_DEFS
    + '<path d="M128,206 C126,180 124,160 118,140" stroke="#0E3A12" stroke-width="16" fill="none" stroke-linecap="round"/>'
    + '<path d="M128,206 C126,180 124,160 118,140" stroke="#4CB04A" stroke-width="8" fill="none" stroke-linecap="round"/>'
    + '<path d="M126,198 C100,196 74,176 70,146 C96,150 118,168 126,198Z" fill="url(#leafG)" stroke="#0E3A12" stroke-width="5" stroke-linejoin="round"/>'
    + '<path d="M130,200 C150,192 170,172 170,150 C150,160 134,178 130,200Z" fill="url(#leafG)" stroke="#0E3A12" stroke-width="5" stroke-linejoin="round"/>'
    # spathe (trumpet)
    + '<path d="M118,150 C92,140 76,110 84,78 C88,60 104,48 122,46 C150,44 178,52 194,70 C176,72 160,82 156,100 C150,124 140,146 118,150Z" fill="url(#calla)" stroke="#5A0A2A" stroke-width="6" stroke-linejoin="round"/>'
    + '<path d="M122,54 C146,52 168,58 184,68 C168,74 154,86 150,100 C144,84 134,66 122,54Z" fill="url(#callaIn)" stroke="#5A0A2A" stroke-width="4" stroke-linejoin="round"/>'
    # spadix
    + '<path d="M120,128 C116,108 124,84 140,72 C146,80 142,106 130,128Z" fill="url(#ctrG)" stroke="#5A2A00" stroke-width="4" stroke-linejoin="round"/>'
    + '<path d="M96,80 C98,66 108,58 118,56" stroke="#fff" stroke-width="6" fill="none" stroke-linecap="round" opacity=".9"/>'
)

# ---------------------------------------------------------------- lily
def lily_petal(a, L=74, W=30):
    return (f'<g transform="rotate({a} 128 124)"><path d="M128,124 C{128 - W},{124 - L * .45:.0f} {128 - W * .5:.0f},{124 - L * .85:.0f} 128,{124 - L} C{128 + W * .5:.0f},{124 - L * .85:.0f} {128 + W},{124 - L * .45:.0f} 128,124Z" '
            f'fill="url(#lilyP)" stroke="#5A0A2A" stroke-width="5" stroke-linejoin="round"/>'
            f'<path d="M128,118 L128,{124 - L * .7:.0f}" stroke="#E0306E" stroke-width="3" fill="none"/>'
            f'<circle cx="122" cy="{124 - L * .4:.0f}" r="2" fill="#B01A50"/><circle cx="134" cy="{124 - L * .35:.0f}" r="2" fill="#B01A50"/></g>')


lily = (
    LEAF_DEFS
    + leaf(128, 150, 50, 1.0) + leaf(128, 150, 130, 1.0)
    + "".join(lily_petal(a) for a in (30, 90, 150, 210, 270, 330))
    + "".join(lily_petal(a, 60, 24) for a in (0, 60, 120, 180, 240, 300))
    + "".join(f'<path d="M128,124 L{128 + 32 * math.cos(math.radians(a)):.1f},{124 + 32 * math.sin(math.radians(a)):.1f}" stroke="#3A7A1A" stroke-width="3"/>'
              f'<ellipse cx="{128 + 34 * math.cos(math.radians(a)):.1f}" cy="{124 + 34 * math.sin(math.radians(a)):.1f}" rx="6" ry="3.5" fill="#C04A10" stroke="#5A1A00" stroke-width="1.5" transform="rotate({a} {128 + 34 * math.cos(math.radians(a)):.1f} {124 + 34 * math.sin(math.radians(a)):.1f})"/>'
              for a in (-110, -80, -50, -130, -150, -30))
    + '<circle cx="128" cy="124" r="9" fill="url(#ctrG)" stroke="#5A2A00" stroke-width="3"/>'
)

# ---------------------------------------------------------------- rose
rose = (
    LEAF_DEFS
    + '<path d="M128,206 C130,188 128,170 128,156" stroke="#0E3A12" stroke-width="14" fill="none" stroke-linecap="round"/>'
    + '<path d="M128,206 C130,188 128,170 128,156" stroke="#4CB04A" stroke-width="6" fill="none" stroke-linecap="round"/>'
    + leaf(124, 176, 200, 1.0) + leaf(132, 186, -20, .9)
    # outer petals
    + '<path d="M70,104 C62,140 90,170 128,170 C166,170 194,140 186,104 C170,120 150,126 128,126 C106,126 86,120 70,104Z" fill="url(#roseG)" stroke="#3A0408" stroke-width="6" stroke-linejoin="round"/>'
    + '<path d="M76,96 C70,66 94,46 118,52 C112,72 112,96 128,116 C104,118 86,110 76,96Z" fill="url(#roseG)" stroke="#3A0408" stroke-width="6" stroke-linejoin="round"/>'
    + '<path d="M180,96 C186,66 162,46 138,52 C144,72 144,96 128,116 C152,118 170,110 180,96Z" fill="url(#roseG)" stroke="#3A0408" stroke-width="6" stroke-linejoin="round"/>'
    # inner bud
    + '<path d="M96,100 C92,74 112,58 128,58 C144,58 164,74 160,100 C156,124 100,124 96,100Z" fill="url(#roseG)" stroke="#3A0408" stroke-width="6" stroke-linejoin="round"/>'
    + '<path d="M128,92 C118,92 118,80 128,80 C140,80 142,96 132,102 C118,110 104,98 108,84 C112,70 130,64 144,72" fill="none" stroke="#5A0410" stroke-width="5" stroke-linecap="round"/>'
    + '<path d="M106,132 C102,146 106,158 114,164 M150,132 C154,146 150,158 142,164" stroke="#7A0414" stroke-width="4" fill="none" stroke-linecap="round"/>'
    + '<path d="M84,80 C86,66 98,58 108,58" stroke="#fff" stroke-width="5" fill="none" stroke-linecap="round" opacity=".7"/>'
    + '<path d="M106,72 C110,66 118,62 124,62" stroke="#fff" stroke-width="4" fill="none" stroke-linecap="round" opacity=".7"/>'
)

# ---------------------------------------------------------------- wild: winged heart
wing_l = ('<path d="M98,100 C70,70 40,62 22,74 C34,80 38,86 36,92 C24,94 18,100 18,108 C30,106 38,110 40,114 C30,120 28,128 32,134 C52,124 74,128 98,120Z" '
          'fill="#FFFFFF" stroke="#5A0A2A" stroke-width="5" stroke-linejoin="round"/>'
          '<path d="M90,108 C70,96 54,92 40,96 M88,116 C70,112 56,114 44,120" stroke="#F0A0C0" stroke-width="3" fill="none"/>')
wild_emblem = (
    f'<g transform="translate(-14,-6) scale(1.1)">{wing_l}</g>' + f'<g transform="translate(270,-6) scale(-1.1,1.1)">{wing_l}</g>'
    + '<g filter="url(#drop)">' + heart(128, 104, 2.0, "#E8203A", "#3A0408", 3.5) + '</g>'
    + star(128, 98, 16, "url(#gold)", "#7A3A00", 3)
    + sparkle(62, 54, .9) + sparkle(196, 50, .7)
)

# ---------------------------------------------------------------- scatter: love letter
letter_env = (
    '<g transform="rotate(-8 128 116)">'
    '<rect x="70" y="78" width="116" height="78" rx="8" fill="#FFF4F6" stroke="#5A0A2A" stroke-width="6"/>'
    '<path d="M70,152 L118,114 M186,152 L138,114" stroke="#E8A0B8" stroke-width="4" fill="none"/>'
    '<path d="M72,82 L128,124 L184,82" fill="#FFE0EA" stroke="#5A0A2A" stroke-width="6" stroke-linejoin="round"/>'
    + heart(128, 122, .72, "#E8203A", "#3A0408", 6)
    + '</g>'
    + heart(84, 62, .42, "#FF6FA8", "#3A0420", 8) + heart(176, 64, .34, "#FF6FA8", "#3A0420", 8)
    + sparkle(186, 150, .7)
)

SYMBOLS = {
    "astra": medallion("circle", PLUM, aster),
    "kala": medallion("shield", CREAM, calla),
    "lily": medallion("hex", PINK, lily),
    "rose": medallion("square", ROSE, rose),
    "club": suit("club", PLUM),
    "diamond": suit("diamond", MAGENTA),
    "heart": suit("heart", RED),
    "spade": suit("spade", PINK),
    "wild": wild(PINK, wild_emblem),
    "scatter": scatter(PINK, letter_env, "BONUS"),
}
