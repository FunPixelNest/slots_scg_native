"""Wolf: night forest — midnight blue, silver moon and pine green."""
from symkit import P, letter, medallion, scatter, sparkle, star, wild

MID = P("#B4D0FF", "#2E5AC0", "#0E1E5A", "#060C2A")
SILVER = P("#FFFFFF", "#B4C4DC", "#4A5A7A", "#121A2E")
PINE = P("#C0F4C8", "#2E9A5A", "#0A3A22", "#041A10")
MOON = P("#FFF6C8", "#F2C84A", "#8A5A10", "#2A1A04")
DUSK = P("#D8C8FF", "#6A4AC8", "#22105A", "#100630")
FOREST = P("#B8E8D0", "#2A7A6A", "#0A2E2E", "#041818")

INK = "#0A1020"

DEFS = ('<defs>'
        '<linearGradient id="fur" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#E4ECF8"/><stop offset=".5" stop-color="#9AAAC4"/><stop offset="1" stop-color="#54627E"/></linearGradient>'
        '<linearGradient id="bark" x1="0" y1="0" x2="1" y2="1"><stop offset="0" stop-color="#D4985A"/><stop offset=".55" stop-color="#9A5A26"/><stop offset="1" stop-color="#4E2A0C"/></linearGradient>'
        '<radialGradient id="cap" cx=".4" cy=".3" r=".8"><stop offset="0" stop-color="#FF8A70"/><stop offset=".55" stop-color="#E0201A"/><stop offset="1" stop-color="#6A0806"/></radialGradient>'
        '<linearGradient id="stem" x1="0" y1="0" x2="1" y2="0"><stop offset="0" stop-color="#D8D0C0"/><stop offset=".45" stop-color="#FFFCF2"/><stop offset="1" stop-color="#B8AC98"/></linearGradient>'
        '<linearGradient id="brass" x1="0" y1="0" x2="1" y2="1"><stop offset="0" stop-color="#FFF0B0"/><stop offset=".45" stop-color="#D49A30"/><stop offset="1" stop-color="#6A4008"/></linearGradient>'
        '<radialGradient id="dial" cx=".45" cy=".4" r=".7"><stop offset="0" stop-color="#F4F8FF"/><stop offset="1" stop-color="#B8C8E4"/></radialGradient>'
        '<linearGradient id="ivory" x1="0" y1="0" x2="1" y2="0"><stop offset="0" stop-color="#E8DCC0"/><stop offset=".4" stop-color="#FFFCF0"/><stop offset="1" stop-color="#C8B48E"/></linearGradient>'
        '<radialGradient id="moonG" cx=".4" cy=".35" r=".7"><stop offset="0" stop-color="#FFFFF0"/><stop offset=".7" stop-color="#FFF0B0"/><stop offset="1" stop-color="#E8C860"/></radialGradient>'
        '<linearGradient id="needle" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#8AE89A"/><stop offset="1" stop-color="#1A6A3A"/></linearGradient>'
        '</defs>')


# ---------------------------------------------------------------- letter accents (top-right corner)
def tree(x, y, s=1.0):
    return (f'<g transform="translate({x},{y}) scale({s})">'
            '<path d="M0,-30 L16,-6 L8,-6 L20,12 L-20,12 L-8,-6 L-16,-6Z" fill="url(#needle)" stroke="#041A10" stroke-width="4" stroke-linejoin="round"/>'
            '<rect x="-4" y="12" width="8" height="10" fill="#6A3A12" stroke="#041A10" stroke-width="3"/></g>')


def crescent(x, y, s=1.0):
    return (f'<g transform="translate({x},{y}) scale({s})">'
            '<path d="M6,-24 C-14,-22 -24,-4 -20,10 C-16,22 -2,28 12,22 C-4,18 -10,4 -8,-6 C-6,-16 0,-22 6,-24Z" fill="url(#moonG)" stroke="#2A1A04" stroke-width="4" stroke-linejoin="round"/></g>')


def paw(x, y, s=1.0):
    return (f'<g transform="translate({x},{y}) scale({s})" fill="#E4ECF8" stroke="#121A2E" stroke-width="3.5">'
            '<ellipse cx="0" cy="8" rx="13" ry="11"/><ellipse cx="-15" cy="-6" rx="6" ry="8"/><ellipse cx="-5" cy="-16" rx="6" ry="8"/>'
            '<ellipse cx="7" cy="-16" rx="6" ry="8"/><ellipse cx="17" cy="-5" rx="6" ry="8"/></g>')


# ---------------------------------------------------------------- fly agaric
agaric = (
    DEFS
    + '<path d="M108,128 C104,160 100,186 96,196 L160,196 C156,186 152,160 148,128Z" fill="url(#stem)" stroke="#2A1A10" stroke-width="6" stroke-linejoin="round"/>'
    + '<path d="M104,150 C116,158 140,158 152,150 L156,164 C140,172 116,172 100,164Z" fill="#FFFCF2" stroke="#2A1A10" stroke-width="4" stroke-linejoin="round"/>'
    + '<path d="M60,130 C56,88 90,58 128,58 C166,58 200,88 196,130 C170,140 86,140 60,130Z" fill="url(#cap)" stroke="#2A0404" stroke-width="7" stroke-linejoin="round"/>'
    + '<path d="M66,128 C98,138 158,138 190,128 L188,134 C160,144 96,144 68,134Z" fill="#F4E4C8" stroke="#2A0404" stroke-width="3"/>'
    + "".join(f'<ellipse cx="{x}" cy="{y}" rx="{rx}" ry="{ry}" fill="#FFFCF2" stroke="#6A0806" stroke-width="2"/>'
              for x, y, rx, ry in ((128, 76, 10, 7), (98, 92, 9, 7), (158, 94, 10, 7), (78, 116, 7, 6), (180, 114, 7, 6), (118, 108, 7, 5), (144, 116, 6, 5)))
    + '<path d="M82,90 C92,74 108,66 120,64" stroke="#fff" stroke-width="5" fill="none" stroke-linecap="round" opacity=".6"/>'
    + '<path d="M76,198 C90,186 104,194 112,198 C124,186 140,192 150,198 C160,188 176,192 182,200Z" fill="#3A9A4A" stroke="#041A10" stroke-width="4" stroke-linejoin="round"/>'
)

# ---------------------------------------------------------------- compass
compass = (
    DEFS
    + '<circle cx="128" cy="58" r="12" fill="none" stroke="#2A1A04" stroke-width="10"/><circle cx="128" cy="58" r="12" fill="none" stroke="url(#brass)" stroke-width="5"/>'
    + '<rect x="118" y="64" width="20" height="14" rx="4" fill="url(#brass)" stroke="#2A1A04" stroke-width="4"/>'
    + '<circle cx="128" cy="136" r="64" fill="url(#brass)" stroke="#2A1A04" stroke-width="7"/>'
    + '<circle cx="128" cy="136" r="50" fill="url(#dial)" stroke="#2A1A04" stroke-width="4"/>'
    + "".join(f'<path d="M128,90 L128,98" stroke="#2A3A5A" stroke-width="3" transform="rotate({a} 128 136)"/>' for a in range(0, 360, 30))
    + '<polygon points="128,92 138,136 128,180 118,136" fill="#E8EEF8" stroke="#121A2E" stroke-width="3" stroke-linejoin="round" transform="rotate(45 128 136)"/>'
    + '<polygon points="128,94 140,136 116,136" fill="#E02A2A" stroke="#4A0606" stroke-width="3" stroke-linejoin="round"/>'
    + '<polygon points="128,178 140,136 116,136" fill="#2A3A6A" stroke="#0A1020" stroke-width="3" stroke-linejoin="round"/>'
    + '<circle cx="128" cy="136" r="7" fill="url(#brass)" stroke="#2A1A04" stroke-width="3"/>'
    + '<path d="M90,108 C98,96 110,90 122,88" stroke="#fff" stroke-width="5" fill="none" stroke-linecap="round" opacity=".85"/>'
)

# ---------------------------------------------------------------- pine cone
def cone_scales():
    out = []
    rows = [(78, [128]), (94, [114, 142]), (112, [100, 128, 156]), (130, [96, 124, 152, 168]), (148, [100, 128, 156]), (166, [110, 138]), (182, [124])]
    for y, xs in rows:
        for x in xs:
            out.append(f'<path d="M{x - 15},{y - 6} C{x - 14},{y + 12} {x + 14},{y + 12} {x + 15},{y - 6} C{x + 6},{y - 2} {x - 6},{y - 2} {x - 15},{y - 6}Z" '
                       f'fill="url(#bark)" stroke="#2A1406" stroke-width="4" stroke-linejoin="round"/>'
                       f'<path d="M{x - 8},{y + 2} C{x - 4},{y + 6} {x + 4},{y + 6} {x + 8},{y + 2}" stroke="#F0C080" stroke-width="2.5" fill="none" opacity=".8"/>')
    return "".join(out)


cone = (
    DEFS
    + '<g transform="rotate(-18 128 130)">'
    + '<path d="M128,64 C160,70 184,110 178,148 C172,180 150,200 128,202 C106,200 84,180 78,148 C72,110 96,70 128,64Z" fill="#5A300E" stroke="#2A1406" stroke-width="7" stroke-linejoin="round"/>'
    + cone_scales()
    + '<path d="M128,64 L124,48" stroke="#5A300E" stroke-width="8" stroke-linecap="round"/>'
    + '</g>'
    + "".join(f'<path d="M122,58 L{122 + dx},{58 + dy}" stroke="#041A10" stroke-width="9" stroke-linecap="round"/>' for dx, dy in ((-30, -14), (-18, -26), (34, -10), (20, -24), (2, -30)))
    + "".join(f'<path d="M122,58 L{122 + dx},{58 + dy}" stroke="url(#needle)" stroke-width="5" stroke-linecap="round"/>' for dx, dy in ((-30, -14), (-18, -26), (34, -10), (20, -24), (2, -30)))
)

# ---------------------------------------------------------------- fang necklace
def fang(x, y, h, rot=0):
    w = h * .42
    return (f'<g transform="rotate({rot} {x} {y})">'
            f'<path d="M{x - w / 2:.1f},{y} C{x - w / 2:.1f},{y + h * .5:.1f} {x - w * .1:.1f},{y + h * .8:.1f} {x + w * .15:.1f},{y + h} C{x + w * .25:.1f},{y + h * .6:.1f} {x + w / 2:.1f},{y + h * .35:.1f} {x + w / 2:.1f},{y}Z" '
            f'fill="url(#ivory)" stroke="#2A1A10" stroke-width="5" stroke-linejoin="round"/>'
            f'<path d="M{x - w * .2:.1f},{y + 6} C{x - w * .2:.1f},{y + h * .4:.1f} {x - w * .05:.1f},{y + h * .6:.1f} {x + w * .05:.1f},{y + h * .72:.1f}" stroke="#fff" stroke-width="3" fill="none" stroke-linecap="round"/>'
            f'<rect x="{x - w / 2 - 3:.1f}" y="{y - 8}" width="{w + 6:.1f}" height="11" rx="4" fill="url(#silver)" stroke="#121A2E" stroke-width="3.5"/></g>')


tooth = (
    DEFS
    + '<path d="M58,64 C70,124 96,140 128,142 C160,140 186,124 198,64" stroke="#2A1406" stroke-width="10" fill="none" stroke-linecap="round"/>'
    + '<path d="M58,64 C70,124 96,140 128,142 C160,140 186,124 198,64" stroke="#A8682C" stroke-width="5" fill="none" stroke-linecap="round"/>'
    + "".join(f'<circle cx="{x}" cy="{y}" r="7" fill="{c}" stroke="#0A1020" stroke-width="3"/>' for x, y, c in ((70, 100, "#3AC8C0"), (186, 100, "#3AC8C0"), (82, 118, "#E0402A"), (174, 118, "#E0402A")))
    + fang(100, 130, 50, 18) + fang(156, 130, 50, -18)
    + fang(128, 146, 66)
    + sparkle(176, 168, .6)
)

# ---------------------------------------------------------------- wild: wolf head
wolf_head = (
    DEFS
    + '<path d="M128,62 L148,66 L172,30 L184,92 L202,120 L176,134 L154,156 L128,168 L102,156 L80,134 L54,120 L72,92 L84,30 L108,66Z" fill="url(#fur)" stroke="#0A1020" stroke-width="7" stroke-linejoin="round"/>'
    + '<path d="M90,46 L104,72 L82,84Z" fill="#3A3048" stroke="#0A1020" stroke-width="3" stroke-linejoin="round"/>'
    + '<path d="M166,46 L152,72 L174,84Z" fill="#3A3048" stroke="#0A1020" stroke-width="3" stroke-linejoin="round"/>'
    + '<path d="M128,90 C140,90 150,104 156,118 L176,132 L152,148 L128,160 L104,148 L80,132 L100,118 C106,104 116,90 128,90Z" fill="#F4F8FF" stroke="#0A1020" stroke-width="4" stroke-linejoin="round"/>'
    + '<path d="M120,64 L128,96 L136,64" fill="#54627E" opacity=".7"/>'
    # eyes
    + '<path d="M94,104 C100,94 114,94 120,106 C112,112 100,112 94,104Z" fill="#FFD23A" stroke="#0A1020" stroke-width="4" stroke-linejoin="round"/>'
    + '<path d="M162,104 C156,94 142,94 136,106 C144,112 156,112 162,104Z" fill="#FFD23A" stroke="#0A1020" stroke-width="4" stroke-linejoin="round"/>'
    + '<circle cx="109" cy="104" r="4.5" fill="#0A1020"/><circle cx="147" cy="104" r="4.5" fill="#0A1020"/>'
    + '<circle cx="111" cy="102" r="1.8" fill="#fff"/><circle cx="149" cy="102" r="1.8" fill="#fff"/>'
    # nose + mouth
    + '<path d="M116,128 C116,120 140,120 140,128 C140,136 132,140 128,142 C124,140 116,136 116,128Z" fill="#1A1A2A" stroke="#0A1020" stroke-width="3"/>'
    + '<ellipse cx="124" cy="126" rx="5" ry="2.5" fill="#fff" opacity=".6"/>'
    + '<path d="M128,142 L128,150 M116,150 Q128,158 140,150" stroke="#0A1020" stroke-width="4" fill="none" stroke-linecap="round"/>'
)

# ---------------------------------------------------------------- scatter: howling wolf under the full moon
howl = (
    DEFS
    + '<circle cx="138" cy="96" r="46" fill="url(#moonG)" stroke="#8A6A20" stroke-width="4"/>'
    + '<circle cx="124" cy="84" r="8" fill="#E8D488" opacity=".7"/><circle cx="154" cy="112" r="11" fill="#E8D488" opacity=".7"/><circle cx="150" cy="78" r="5" fill="#E8D488" opacity=".7"/>'
    + '<path d="M60,170 L76,128 L70,128 L82,104 L94,128 L88,128 L104,170Z" fill="#0A3A22" stroke="#041A10" stroke-width="3" stroke-linejoin="round"/>'
    + '<path d="M168,170 L180,136 L175,136 L186,112 L197,136 L192,136 L204,170Z" fill="#0A3A22" stroke="#041A10" stroke-width="3" stroke-linejoin="round"/>'
    + '<path d="M78,176 C90,160 110,152 128,154 C150,152 170,162 182,176Z" fill="#1A2440" stroke="#060C2A" stroke-width="4" stroke-linejoin="round"/>'
    # wolf silhouette, sitting, head raised to the right
    + '<path d="M104,160 C100,146 102,128 110,116 L114,104 C112,96 114,88 118,82 L116,68 L126,78 L136,74 L154,58 L156,64 L146,76 L150,78 L140,86 '
      'C136,94 136,102 140,112 C146,124 146,144 142,160 L134,160 L134,140 C130,146 124,152 116,156 L116,160Z" fill="#141C34" stroke="#060C2A" stroke-width="3" stroke-linejoin="round"/>'
    + '<path d="M106,156 C94,156 86,150 82,140 C90,146 98,148 106,146Z" fill="#141C34" stroke="#060C2A" stroke-width="3" stroke-linejoin="round"/>'
    + sparkle(82, 64, .6) + sparkle(190, 74, .5)
)

SYMBOLS = {
    "j": letter("J", PINE, DEFS + tree(204, 56, 1.15)),
    "q": letter("Q", DUSK, DEFS + crescent(210, 52, 1.2)),
    "k": letter("K", SILVER, paw(208, 50, 1.05)),
    "a": letter("A", MOON, star(206, 52, 20, "url(#silver)", "#121A2E", 4)),
    "agaric": medallion("circle", FOREST, agaric),
    "compass": medallion("hex", MID, compass),
    "cone": medallion("shield", PINE, f'<g transform="translate(128,140) scale(1.05) translate(-128,-140)">{cone}</g>'),
    "tooth": medallion("square", DUSK, f'<g transform="translate(128,120) scale(1.12) translate(-128,-120)">{tooth}</g>'),
    "wild": wild(MID, f'<g transform="translate(0,-8)">{wolf_head}</g>'),
    "scatter": scatter(DUSK, howl, "BONUS"),
}
