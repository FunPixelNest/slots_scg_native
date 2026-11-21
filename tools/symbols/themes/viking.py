"""Viking: steel blue, icy white, bronze and fire orange."""
from symkit import P, medallion, scatter, sparkle, star, wild

STEEL = P("#CFE4F7", "#4F7FAE", "#1A3556", "#0A1828")
ICE = P("#FFFFFF", "#9FD4F0", "#2E6A9A", "#0C2236")
FIRE = P("#FFE0A0", "#F28A1E", "#8A2A04", "#341004")
BRONZE = P("#FFE2B0", "#C8843A", "#5E3210", "#2A1406")
NIGHT = P("#B8D0F0", "#3A5A96", "#14203E", "#080E20")
TEAL = P("#C8FFF4", "#2FA8A0", "#0A4A50", "#04222A")

INK = "#1A1008"

DEFS = ('<defs>'
        '<linearGradient id="bronze" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#FFE2A8"/><stop offset=".45" stop-color="#D08A3A"/><stop offset="1" stop-color="#6A3410"/></linearGradient>'
        '<linearGradient id="steel" x1="0" y1="0" x2="1" y2="1"><stop offset="0" stop-color="#FFFFFF"/><stop offset=".4" stop-color="#B8C8DA"/><stop offset="1" stop-color="#5A6A82"/></linearGradient>'
        '<radialGradient id="stone" cx=".4" cy=".35" r=".75"><stop offset="0" stop-color="#C8CDD4"/><stop offset=".6" stop-color="#868E9A"/><stop offset="1" stop-color="#4A505C"/></radialGradient>'
        '<linearGradient id="horn" x1="0" y1="0" x2="1" y2="1"><stop offset="0" stop-color="#FFFBEA"/><stop offset=".6" stop-color="#EAD8A8"/><stop offset="1" stop-color="#A88A50"/></linearGradient>'
        '<radialGradient id="skin" cx=".45" cy=".35" r=".75"><stop offset="0" stop-color="#FFE6D2"/><stop offset=".7" stop-color="#F6C4A0"/><stop offset="1" stop-color="#D89070"/></radialGradient>'
        '<linearGradient id="blond" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#FFF4B0"/><stop offset=".5" stop-color="#F6CE4A"/><stop offset="1" stop-color="#C08A18"/></linearGradient>'
        '<linearGradient id="dark" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#7A5A48"/><stop offset=".5" stop-color="#4A3226"/><stop offset="1" stop-color="#24160E"/></linearGradient>'
        '<linearGradient id="ginger" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#FFB070"/><stop offset=".5" stop-color="#E8601E"/><stop offset="1" stop-color="#9A2E08"/></linearGradient>'
        '<linearGradient id="wood" x1="0" y1="0" x2="1" y2="0"><stop offset="0" stop-color="#7A4A20"/><stop offset=".5" stop-color="#B8783A"/><stop offset="1" stop-color="#6A3A14"/></linearGradient>'
        '</defs>')


# ---------------------------------------------------------------- rune stones
def rune_stone(d, glow, core):
    stone = ('<path d="M74,72 C88,52 150,46 178,62 C200,76 204,130 196,168 C188,198 150,208 120,206 C86,204 60,186 58,150 C56,118 60,90 74,72Z" '
             'fill="url(#stone)" stroke="#20242C" stroke-width="7" stroke-linejoin="round"/>'
             '<path d="M78,78 C92,62 140,56 166,66 C150,70 110,72 84,96Z" fill="#fff" opacity=".35"/>'
             '<path d="M180,150 L170,164 L176,178 M70,124 L80,132" stroke="#3A404A" stroke-width="3" fill="none" stroke-linecap="round"/>'
             '<circle cx="168" cy="92" r="4" fill="#5A606C"/><circle cx="86" cy="176" r="3" fill="#5A606C"/>')
    return (DEFS + stone
            + f'<path d="{d}" stroke="{glow}" stroke-width="26" fill="none" stroke-linecap="round" stroke-linejoin="round" filter="url(#glow)" opacity=".95"/>'
            + f'<path d="{d}" stroke="#14181E" stroke-width="20" fill="none" stroke-linecap="round" stroke-linejoin="round"/>'
            + f'<path d="{d}" stroke="{glow}" stroke-width="12" fill="none" stroke-linecap="round" stroke-linejoin="round"/>'
            + f'<path d="{d}" stroke="{core}" stroke-width="5" fill="none" stroke-linecap="round" stroke-linejoin="round"/>')


FEHU = "M112,72 L112,186 M112,110 L150,80 M112,144 L154,112"
GEBO = "M90,82 L166,180 M166,82 L90,180"
MANNAZ = "M96,74 L96,184 M160,74 L160,184 M96,76 L160,128 M160,76 L96,128"
OTHALA = "M94,182 L160,112 L128,72 L96,112 L162,182"


# ---------------------------------------------------------------- portraits
def eyes(y, iris="#3A7AD0"):
    out = ""
    for x in (110, 146):
        out += (f'<ellipse cx="{x}" cy="{y}" rx="11" ry="13" fill="#fff" stroke="{INK}" stroke-width="3.5"/>'
                f'<circle cx="{x + 1}" cy="{y + 2}" r="8" fill="{iris}"/><circle cx="{x + 1}" cy="{y + 2}" r="4.2" fill="#10141C"/>'
                f'<circle cx="{x + 4}" cy="{y - 2}" r="3" fill="#fff"/>')
    return out


def face(y, iris="#3A7AD0", girl=False, mouth=True, brow="#5A3A1A"):
    lash = ""
    if girl:
        lash = (f'<path d="M98,{y - 8} L94,{y - 13} M101,{y - 12} L99,{y - 17}" stroke="{INK}" stroke-width="3" stroke-linecap="round"/>'
                f'<path d="M158,{y - 8} L162,{y - 13} M155,{y - 12} L157,{y - 17}" stroke="{INK}" stroke-width="3" stroke-linecap="round"/>')
    brows = (f'<path d="M100,{y - 20} Q110,{y - 26} 120,{y - 21}" stroke="{brow}" stroke-width="5" fill="none" stroke-linecap="round"/>'
             f'<path d="M136,{y - 21} Q146,{y - 26} 156,{y - 20}" stroke="{brow}" stroke-width="5" fill="none" stroke-linecap="round"/>')
    m = ""
    if mouth:
        m = (f'<path d="M112,{y + 26} Q128,{y + 44} 144,{y + 26} Q128,{y + 32} 112,{y + 26}Z" fill="#9A2030" stroke="{INK}" stroke-width="3.5" stroke-linejoin="round"/>'
             f'<path d="M120,{y + 33} Q128,{y + 39} 136,{y + 33}" stroke="#FF8A96" stroke-width="3" fill="none"/>')
    return (eyes(y, iris) + lash + brows
            + f'<ellipse cx="98" cy="{y + 20}" rx="9" ry="5" fill="#FF7A8A" opacity=".5"/><ellipse cx="158" cy="{y + 20}" rx="9" ry="5" fill="#FF7A8A" opacity=".5"/>'
            + f'<path d="M127,{y + 8} Q122,{y + 18} 131,{y + 18}" stroke="#B0603C" stroke-width="3.5" fill="none" stroke-linecap="round"/>'
            + m)


def head(cy=128, rx=44, ry=48):
    return (f'<ellipse cx="{128 - rx + 2}" cy="{cy + 4}" rx="9" ry="12" fill="url(#skin)" stroke="{INK}" stroke-width="5"/>'
            f'<ellipse cx="{128 + rx - 2}" cy="{cy + 4}" rx="9" ry="12" fill="url(#skin)" stroke="{INK}" stroke-width="5"/>'
            f'<ellipse cx="128" cy="{cy}" rx="{rx}" ry="{ry}" fill="url(#skin)" stroke="{INK}" stroke-width="6"/>')


def tunic(color, trim="url(#bronze)"):
    return (f'<path d="M82,200 C84,184 102,174 128,174 C154,174 172,184 174,200 Q128,210 82,200Z" fill="{color}" stroke="{INK}" stroke-width="6" stroke-linejoin="round"/>'
            f'<path d="M104,178 L128,198 L152,178" stroke="{trim}" stroke-width="6" fill="none" stroke-linejoin="round"/>'
            f'<rect x="120" y="164" width="16" height="16" fill="url(#skin)" stroke="{INK}" stroke-width="4"/>')


def braid(x, y0, y1, fill, tie):
    out = ""
    n = 5
    step = (y1 - y0) / n
    for i in range(n):
        out += f'<ellipse cx="{x}" cy="{y0 + step * (i + .5):.1f}" rx="11" ry="{step * .62:.1f}" fill="{fill}" stroke="{INK}" stroke-width="4.5"/>'
    out += (f'<rect x="{x - 9}" y="{y1 - 4}" width="18" height="9" rx="3" fill="{tie}" stroke="{INK}" stroke-width="3.5"/>'
            f'<path d="M{x - 8},{y1 + 5} L{x - 10},{y1 + 18} L{x},{y1 + 13} L{x + 10},{y1 + 18} L{x + 8},{y1 + 5}Z" fill="{fill}" stroke="{INK}" stroke-width="3.5" stroke-linejoin="round"/>')
    return out


def girl(hair, iris, tunic_c, tie, extra=""):
    back = f'<path d="M78,132 C74,82 100,64 128,64 C156,64 182,82 178,132 L178,160 L78,160Z" fill="{hair}" stroke="{INK}" stroke-width="6" stroke-linejoin="round"/>'
    fringe = (f'<path d="M84,126 C80,86 104,72 128,72 C152,72 176,86 172,126 C166,106 154,96 140,94 C128,104 110,108 96,104 C90,110 86,118 84,126Z" '
              f'fill="{hair}" stroke="{INK}" stroke-width="5" stroke-linejoin="round"/>'
              '<path d="M100,84 C110,76 126,74 138,78" stroke="#fff" stroke-width="5" fill="none" stroke-linecap="round" opacity=".55"/>')
    return (DEFS + back + tunic(tunic_c) + head(126, 42, 46) + fringe
            + braid(84, 118, 186, hair, tie) + braid(172, 118, 186, hair, tie)
            + face(124, iris, True) + extra)


def horn(mirror=False):
    g = ('<path d="M88,96 C64,96 48,80 50,48 C60,64 74,72 96,76Z" fill="url(#horn)" stroke="#2A1A08" stroke-width="5" stroke-linejoin="round"/>'
         '<path d="M70,85 C76,87 82,87 88,85 M60,70 C64,74 70,76 76,76" stroke="#A88A50" stroke-width="3" fill="none"/>')
    return f'<g transform="translate(256,0) scale(-1,1)">{g}</g>' if mirror else g


def helmet(horns):
    h = (horn() + horn(True)) if horns else ''
    return (h + '<path d="M80,106 C80,66 104,52 128,52 C152,52 176,66 176,106Z" fill="url(#steel)" stroke="#141A24" stroke-width="6" stroke-linejoin="round"/>'
            '<path d="M128,54 L128,104" stroke="url(#bronze)" stroke-width="12"/><path d="M128,54 L128,104" stroke="#141A24" stroke-width="2" opacity=".4"/>'
            '<path d="M92,76 C102,64 116,60 124,60" stroke="#fff" stroke-width="6" fill="none" stroke-linecap="round" opacity=".8"/>'
            '<rect x="74" y="98" width="108" height="16" rx="7" fill="url(#bronze)" stroke="#141A24" stroke-width="5"/>'
            + "".join(f'<circle cx="{x}" cy="106" r="3.2" fill="#FFF0C0" stroke="#5A3010" stroke-width="1.5"/>' for x in (88, 108, 148, 168)))


def bearded_man(hair, iris, tunic_c, horns, brow):
    beard = (f'<path d="M84,136 C82,180 102,206 128,208 C154,206 174,180 172,136 C166,156 150,166 128,166 C106,166 90,156 84,136Z" fill="{hair}" stroke="{INK}" stroke-width="6" stroke-linejoin="round"/>'
             f'<path d="M110,190 L114,200 M128,194 L128,204 M146,190 L142,200" stroke="{INK}" stroke-width="3" opacity=".5" stroke-linecap="round"/>')
    stache = (f'<path d="M128,160 C118,150 100,152 94,164 C108,160 118,164 128,166 C138,164 148,160 162,164 C156,152 138,150 128,160Z" fill="{hair}" stroke="{INK}" stroke-width="4" stroke-linejoin="round"/>')
    mouth = (f'<path d="M116,168 Q128,182 140,168Z" fill="#9A2030" stroke="{INK}" stroke-width="3.5" stroke-linejoin="round"/>')
    side = (f'<path d="M80,110 L80,146 C84,146 88,140 88,132 L88,118Z" fill="{hair}" stroke="{INK}" stroke-width="4" stroke-linejoin="round"/>'
            f'<path d="M176,110 L176,146 C172,146 168,140 168,132 L168,118Z" fill="{hair}" stroke="{INK}" stroke-width="4" stroke-linejoin="round"/>')
    return (DEFS + tunic(tunic_c) + head(134, 44, 46) + side + beard + face(138, iris, False, False, brow) + mouth + stache + helmet(horns))


# ---------------------------------------------------------------- wild: shield with crossed axes
def axe(rot, flip=False):
    fl = ' translate(256,0) scale(-1,1)' if flip else ''
    return (f'<g transform="rotate({rot} 128 108){fl}">'
            '<rect x="122" y="36" width="12" height="140" rx="5" fill="url(#wood)" stroke="#2A1406" stroke-width="5"/>'
            '<path d="M134,46 C160,36 178,44 184,52 C176,68 176,84 184,100 C170,104 150,96 134,82Z" fill="url(#steel)" stroke="#141A24" stroke-width="5" stroke-linejoin="round"/>'
            '<path d="M180,56 C174,70 174,84 180,96" stroke="#fff" stroke-width="3" fill="none" opacity=".8"/>'
            '</g>')


wild_emblem = (
    DEFS + axe(-34, True) + axe(34)
    + '<g transform="translate(128,118) scale(.84) translate(-128,-108)">'
    + '<circle cx="128" cy="108" r="50" fill="#14203E" stroke="#0A0A14" stroke-width="6"/>'
    + '<circle cx="128" cy="108" r="44" fill="url(#bronze)"/>'
    + '<path d="M128,64 L128,152 M84,108 L172,108" stroke="#7A2A08" stroke-width="16"/>'
    + '<path d="M128,64 L128,152 M84,108 L172,108" stroke="#E8601E" stroke-width="10"/>'
    + '<circle cx="128" cy="108" r="44" fill="none" stroke="#5A3010" stroke-width="4"/>'
    + '<circle cx="128" cy="108" r="15" fill="url(#steel)" stroke="#141A24" stroke-width="4"/>'
    + '<ellipse cx="110" cy="84" rx="16" ry="8" fill="#fff" opacity=".45" transform="rotate(-30 110 84)"/></g>'
)

# ---------------------------------------------------------------- scatter: longship
longship = (
    DEFS
    + '<path d="M128,52 L128,128" stroke="#3A2010" stroke-width="7"/>'
    + '<path d="M90,62 L166,62 C170,90 168,110 162,124 L94,124 C88,110 86,90 90,62Z" fill="#F4EEE0" stroke="#2A1406" stroke-width="5" stroke-linejoin="round"/>'
    + '<path d="M104,62 L104,124 M128,62 L128,124 M152,62 L152,124" stroke="#D0402A" stroke-width="12"/>'
    + '<path d="M84,58 L172,58" stroke="#3A2010" stroke-width="7" stroke-linecap="round"/>'
    + '<path d="M60,132 C70,152 90,160 128,160 C166,160 186,152 196,132Z" fill="url(#wood)" stroke="#2A1406" stroke-width="6" stroke-linejoin="round"/>'
    + '<path d="M60,132 C52,120 50,106 58,96 C64,90 72,94 70,102 C66,104 64,100 62,104 C62,114 68,124 76,132Z" fill="url(#wood)" stroke="#2A1406" stroke-width="5" stroke-linejoin="round"/>'
    + '<path d="M196,132 C204,122 206,110 200,102 C196,110 190,118 182,132Z" fill="url(#wood)" stroke="#2A1406" stroke-width="5" stroke-linejoin="round"/>'
    + "".join(f'<circle cx="{x}" cy="142" r="8" fill="url(#bronze)" stroke="#2A1406" stroke-width="3.5"/>' for x in (92, 112, 132, 152, 172))
    + '<path d="M56,166 C70,156 82,172 96,164 C110,156 120,172 134,164 C148,156 158,172 172,164 C186,156 194,168 200,166 L196,178 C150,186 106,186 60,178Z" fill="#7FD0F0" stroke="#0C2236" stroke-width="4" stroke-linejoin="round"/>'
    + sparkle(80, 70, .7) + sparkle(182, 76, .6)
)

def big(icon, k=1.1, cy=128):
    return f'<g transform="translate(128,{cy}) scale({k}) translate(-128,-{cy})">{icon}</g>'


SYMBOLS = {
    "runefehu": medallion("circle", FIRE, rune_stone(FEHU, "#FF9A2A", "#FFF2C0"), rim="url(#bronze)"),
    "runegebo": medallion("hex", ICE, rune_stone(GEBO, "#5AD8FF", "#E8FFFF"), rim="url(#silver)"),
    "runemannaz": medallion("shield", TEAL, rune_stone(MANNAZ, "#4AF0C0", "#EAFFF6"), rim="url(#bronze)"),
    "runeothala": medallion("square", NIGHT, rune_stone(OTHALA, "#FFD24A", "#FFFBE0"), rim="url(#silver)"),
    "blondegirl": medallion("circle", ICE, big(girl("url(#blond)", "#3A86E0", "#3A6AAE", "#3A6AAE"))),
    "blondeman": medallion("square", STEEL, big(bearded_man("url(#blond)", "#3A86E0", "#3A5A8A", True, "#B08A20"), 1.05)),
    "darkman": medallion("square", BRONZE, big(bearded_man("url(#dark)", "#6A4A2A", "#5A2A1A", False, "#2A1A10"), 1.08)),
    "gingergirl": medallion("hex", FIRE, big(girl("url(#ginger)", "#2FA060", "#2E7A50", "#F6CE4A"))),
    "wild": wild(STEEL, wild_emblem),
    "scatter": scatter(ICE, longship, "BONUS"),
}
