"""Christmas Eve: red, green and snowy blue."""
import math

from symkit import P, lg, letter, medallion, rg, scatter, sparkle, star, suit, wild

INK = "#1E0A0A"
RED = P("#FFB0A8", "#E0202E", "#7A0410", "#360206")
GREEN = P("#C8FFB8", "#2EA84A", "#0A4A1E", "#04240E")
GOLD = P("#FFF6C0", "#FFC21A", "#B86200", "#4A2204")
ICE = P("#E8FAFF", "#6AC8FF", "#1A5AB8", "#0A2458")
NIGHT = P("#A8D0FF", "#2A5AC8", "#0C1E5E", "#060E30")


def leaf(x, y, rot, s=1.0):
    """Spiky holly leaf, base at (x,y), pointing along [rot] degrees."""
    d = "M0,0 Q8,-13 16,-9 Q20,-20 30,-13 Q37,-21 48,-10 Q52,0 48,10 Q37,21 30,13 Q20,20 16,9 Q8,13 0,0Z"
    return (f'<g transform="translate({x},{y}) rotate({rot}) scale({s})">'
            f'<path d="{d}" fill="url(#holly)" stroke="{INK}" stroke-width="{4 / s:.1f}" stroke-linejoin="round"/>'
            f'<path d="M3,0 L42,0" stroke="#0A3A14" stroke-width="{2.5 / s:.1f}"/></g>')


def berry(x, y, r=7):
    return (f'<circle cx="{x}" cy="{y}" r="{r}" fill="#E0202E" stroke="{INK}" stroke-width="3"/>'
            f'<circle cx="{x - r * .3:.1f}" cy="{y - r * .35:.1f}" r="{r * .3:.1f}" fill="#fff" opacity=".85"/>')


def holly(x, y, s=1.0):
    return (f'<g transform="translate({x},{y}) scale({s})">' + leaf(0, 0, -160) + leaf(0, 0, -20) + leaf(0, 0, 80, .8)
            + berry(-7, -4) + berry(7, -4) + berry(0, 6) + '</g>')


HOLLY_DEF = lg("holly", [(0, "#7AE07A"), (.6, "#1E8A3A"), (1, "#0A4A1E")])


def snowflake(x, y, r, c="#FFFFFF", sw=3):
    arms = ""
    for k in range(6):
        a = math.radians(k * 60)
        ex, ey = x + r * math.cos(a), y + r * math.sin(a)
        mx, my = x + r * .55 * math.cos(a), y + r * .55 * math.sin(a)
        arms += f'<line x1="{x}" y1="{y}" x2="{ex:.1f}" y2="{ey:.1f}"/>'
        for d in (-40, 40):
            b = a + math.radians(d)
            arms += f'<line x1="{mx:.1f}" y1="{my:.1f}" x2="{mx + r * .3 * math.cos(b):.1f}" y2="{my + r * .3 * math.sin(b):.1f}"/>'
    return (f'<g stroke="#0A2458" stroke-width="{sw + 4}" stroke-linecap="round">{arms}</g>'
            f'<g stroke="{c}" stroke-width="{sw}" stroke-linecap="round">{arms}</g>')


def bauble(x, y, r, c1, c2, c3):
    return (f'<rect x="{x - r * .3:.1f}" y="{y - r * 1.25:.1f}" width="{r * .6:.1f}" height="{r * .4:.1f}" rx="2" fill="url(#gold)" stroke="{INK}" stroke-width="2.5"/>'
            f'<circle cx="{x}" cy="{y}" r="{r}" fill="{c2}" stroke="{INK}" stroke-width="{max(2.5, r * .14):.1f}"/>'
            f'<path d="M{x - r},{y} C{x - r * .5},{y + r * .4} {x + r * .5},{y + r * .4} {x + r},{y}" fill="none" stroke="{c1}" stroke-width="{r * .2:.1f}"/>'
            f'<ellipse cx="{x - r * .35:.1f}" cy="{y - r * .4:.1f}" rx="{r * .3:.1f}" ry="{r * .18:.1f}" fill="#fff" opacity=".85"/>')


# ------------------------------------------------------------------ bell
bell = (
    '<defs>' + HOLLY_DEF + rg("bellG", [(0, "#FFFBD8"), (.4, "#FFD24A"), (.8, "#D88400"), (1, "#8A4A00")], cx=".35", cy=".3", r=".8") + '</defs>'
    '<circle cx="128" cy="180" r="13" fill="url(#gold)" stroke="' + INK + '" stroke-width="5"/>'
    '<path d="M128,60 C96,60 84,88 82,118 C80,146 70,158 56,168 L200,168 C186,158 176,146 174,118 C172,88 160,60 128,60Z" '
    'fill="url(#bellG)" stroke="' + INK + '" stroke-width="7" stroke-linejoin="round"/>'
    '<rect x="50" y="160" width="156" height="18" rx="9" fill="url(#gold)" stroke="' + INK + '" stroke-width="6"/>'
    '<path d="M86,132 C112,142 144,142 170,132" fill="none" stroke="#B86A00" stroke-width="5"/>'
    '<path d="M98,120 C98,96 104,80 116,72" fill="none" stroke="#fff" stroke-width="7" stroke-linecap="round" opacity=".8"/>'
    + holly(128, 64, 1.15)
    + sparkle(186, 92, .8) + sparkle(68, 100, .55)
)

# ------------------------------------------------------------------ candle
candle = (
    '<defs>' + HOLLY_DEF + lg("wax", [(0, "#8A0410"), (.35, "#FF5A5A"), (.6, "#E0202E"), (1, "#7A0410")], "1", "0")
    + rg("flame", [(0, "#FFFFFF"), (.35, "#FFF0A0"), (.75, "#FFA01A"), (1, "#E04A00")], cx=".5", cy=".7", r=".6") + '</defs>'
    # glow
    '<circle cx="128" cy="62" r="34" fill="#FFE27A" opacity=".45" filter="url(#glow)"/>'
    # holder
    '<ellipse cx="128" cy="182" rx="56" ry="14" fill="url(#gold)" stroke="' + INK + '" stroke-width="6"/>'
    # body
    '<path d="M102,90 L154,90 L154,180 C154,186 102,186 102,180Z" fill="url(#wax)" stroke="' + INK + '" stroke-width="7" stroke-linejoin="round"/>'
    '<path d="M102,92 C102,84 154,84 154,92 L154,104 C150,104 150,118 146,118 C142,118 142,100 136,100 C130,100 130,126 124,126 C118,126 120,104 114,104 C108,104 108,112 102,112Z" '
    'fill="#FFF4F0" stroke="' + INK + '" stroke-width="5" stroke-linejoin="round"/>'
    '<rect x="110" y="122" width="6" height="52" rx="3" fill="#fff" opacity=".4"/>'
    # wick and flame
    '<path d="M128,88 L128,78" stroke="' + INK + '" stroke-width="4"/>'
    '<path d="M128,34 C140,52 146,62 144,72 C142,82 134,86 128,86 C122,86 114,82 112,72 C110,62 118,52 128,34Z" fill="url(#flame)" stroke="#B83A00" stroke-width="4" stroke-linejoin="round"/>'
    + holly(98, 184, .8)
    + sparkle(176, 50, .7) + sparkle(80, 76, .55)
)

# ------------------------------------------------------------------ gingerbread man
GB = [
    '<circle cx="128" cy="72" r="30" {A}/>',
    '<ellipse cx="128" cy="138" rx="36" ry="44" {A}/>',
    '<rect x="62" y="104" width="64" height="28" rx="14" transform="rotate(18 126 118)" {A}/>',
    '<rect x="130" y="104" width="64" height="28" rx="14" transform="rotate(-18 130 118)" {A}/>',
    '<rect x="94" y="150" width="30" height="58" rx="15" transform="rotate(18 109 160)" {A}/>',
    '<rect x="132" y="150" width="30" height="58" rx="15" transform="rotate(-18 147 160)" {A}/>',
]
cookie = (
    '<defs>' + rg("ginger", [(0, "#F0B070"), (.6, "#C0702A"), (1, "#7A3A10")], cx=".4", cy=".3", r=".8") + '</defs>'
    + "".join(g.replace("{A}", f'fill="{INK}" stroke="{INK}" stroke-width="14" stroke-linejoin="round"') for g in GB)
    + "".join(g.replace("{A}", 'fill="url(#ginger)"') for g in GB)
    # icing
    + '<g fill="none" stroke="#fff" stroke-width="4.5" stroke-linecap="round">'
    '<path d="M72,108 C76,114 76,120 72,126 M184,108 C180,114 180,120 184,126"/>'
    '<path d="M86,190 C92,186 100,188 106,192 M150,192 C156,188 164,186 170,190"/>'
    '<path d="M114,82 C120,90 136,90 142,82"/></g>'
    '<circle cx="117" cy="66" r="5" fill="' + INK + '"/><circle cx="139" cy="66" r="5" fill="' + INK + '"/>'
    '<circle cx="105" cy="76" r="4" fill="#FF8A8A" opacity=".8"/><circle cx="151" cy="76" r="4" fill="#FF8A8A" opacity=".8"/>'
    '<path d="M110,104 L128,112 L146,104 L146,120 L128,112 L110,120Z" fill="#E0202E" stroke="' + INK + '" stroke-width="3" stroke-linejoin="round"/>'
    + "".join(f'<circle cx="128" cy="{y}" r="6.5" fill="{c}" stroke="{INK}" stroke-width="3"/><circle cx="126" cy="{y - 2}" r="2" fill="#fff"/>'
              for y, c in [(132, "#2EA84A"), (150, "#E0202E"), (168, "#2EA84A")])
    + '<path d="M106,54 C112,46 122,42 132,42" fill="none" stroke="#fff" stroke-width="5" stroke-linecap="round" opacity=".45"/>'
)

# ------------------------------------------------------------------ stocking
sock = (
    '<g transform="translate(128,128) scale(.92) translate(-128,-118)">'
    '<defs>' + lg("sockR", [(0, "#FF6A6A"), (.6, "#D81A2A"), (1, "#7A0410")], "1", "1")
    + '<clipPath id="sockClip"><path d="M94,80 L164,80 L164,146 C164,178 144,200 112,200 L78,200 C58,200 50,184 54,170 C58,158 70,152 86,152 L94,152Z"/></clipPath></defs>'
    '<path d="M94,80 L164,80 L164,146 C164,178 144,200 112,200 L78,200 C58,200 50,184 54,170 C58,158 70,152 86,152 L94,152Z" '
    'fill="url(#sockR)" stroke="' + INK + '" stroke-width="7" stroke-linejoin="round"/>'
    '<g clip-path="url(#sockClip)">'
    # toe and heel patches, stripes
    '<path d="M40,150 L82,150 C92,166 92,190 84,210 L40,210Z" fill="#2EA84A"/>'
    '<path d="M140,210 C138,186 150,166 170,160 L180,210Z" fill="#2EA84A"/>'
    '<rect x="80" y="104" width="100" height="10" fill="#fff" opacity=".9"/><rect x="80" y="124" width="100" height="10" fill="#2EA84A"/>'
    + "".join(f'<path d="M{x},{y} l4,-7 l4,7 l-4,7Z" fill="#fff"/>' for x, y in [(104, 144), (124, 146), (144, 144), (116, 168), (136, 170)])
    + '</g>'
    '<path d="M100,96 L102,142" stroke="#fff" stroke-width="5" stroke-linecap="round" opacity=".35"/>'
    # fur cuff
    '<rect x="82" y="50" width="94" height="38" rx="16" fill="#FFFFFF" stroke="' + INK + '" stroke-width="7"/>'
    + "".join(f'<circle cx="{x}" cy="{y}" r="5" fill="#D8E8F4"/>' for x, y in [(98, 62), (118, 74), (140, 62), (160, 74), (108, 78), (152, 60)])
    + '<g transform="translate(184,96)">' + snowflake(0, 0, 14) + '</g>'
    + bauble(186, 150, 13, "#FFF0A0", "#FFC21A", "#B86200") + '</g>'
)

# ------------------------------------------------------------------ wild: Christmas tree
tree = (
    '<defs>' + lg("tree", [(0, "#8AF08A"), (.55, "#1E9A3A"), (1, "#0A4A1E")]) + '</defs>'
    '<rect x="116" y="150" width="24" height="20" fill="#7A3A10" stroke="' + INK + '" stroke-width="5"/>'
    '<path d="M128,92 L196,160 C170,168 86,168 60,160Z" fill="url(#tree)" stroke="' + INK + '" stroke-width="7" stroke-linejoin="round"/>'
    '<path d="M128,66 L180,122 C160,130 96,130 76,122Z" fill="url(#tree)" stroke="' + INK + '" stroke-width="7" stroke-linejoin="round"/>'
    '<path d="M128,42 L164,88 C150,94 106,94 92,88Z" fill="url(#tree)" stroke="' + INK + '" stroke-width="7" stroke-linejoin="round"/>'
    '<path d="M84,118 C110,132 150,130 172,116 M70,154 C104,168 156,166 186,152" fill="none" stroke="url(#gold)" stroke-width="5" stroke-linecap="round"/>'
    + bauble(108, 140, 8, "#FFB0A8", "#E0202E", "#7A0410") + bauble(152, 146, 8, "#A8E0FF", "#2A7AE0", "#0A2A6A")
    + bauble(126, 106, 7, "#FFF0A0", "#FFC21A", "#B86200") + bauble(146, 80, 6, "#FFB0A8", "#E0202E", "#7A0410")
    + bauble(94, 114, 6, "#A8E0FF", "#2A7AE0", "#0A2A6A")
    + star(128, 40, 22, "url(#gold)", "#7A3A00", 4)
    + sparkle(186, 64, .8) + sparkle(70, 80, .6)
)

# ------------------------------------------------------------------ scatter: gift box
gift = (
    '<defs>' + lg("box", [(0, "#FF6A6A"), (.6, "#D81A2A"), (1, "#7A0410")], "1", "1")
    + lg("lid", [(0, "#FF8A8A"), (1, "#B8101E")]) + '</defs>'
    '<rect x="76" y="102" width="104" height="66" rx="4" fill="url(#box)" stroke="' + INK + '" stroke-width="7"/>'
    '<rect x="116" y="102" width="24" height="66" fill="url(#gold)" stroke="' + INK + '" stroke-width="4"/>'
    '<rect x="68" y="84" width="120" height="24" rx="5" fill="url(#lid)" stroke="' + INK + '" stroke-width="7"/>'
    '<rect x="114" y="84" width="28" height="24" fill="url(#gold)" stroke="' + INK + '" stroke-width="4"/>'
    # bow
    '<path d="M128,84 C110,60 80,58 86,76 C90,88 112,88 128,84Z" fill="url(#gold)" stroke="' + INK + '" stroke-width="5" stroke-linejoin="round"/>'
    '<path d="M128,84 C146,60 176,58 170,76 C166,88 144,88 128,84Z" fill="url(#gold)" stroke="' + INK + '" stroke-width="5" stroke-linejoin="round"/>'
    '<circle cx="128" cy="82" r="9" fill="url(#gold)" stroke="' + INK + '" stroke-width="5"/>'
    '<rect x="82" y="110" width="8" height="52" rx="3" fill="#fff" opacity=".35"/>'
    + '<g transform="translate(92,52)">' + snowflake(0, 0, 11, sw=2.5) + '</g>'
    + '<g transform="translate(176,136)">' + snowflake(0, 0, 9, sw=2.5) + '</g>'
    + sparkle(170, 50, .7)
)

SYMBOLS = {
    "j": letter("J", RED, '<g transform="translate(198,52)">' + snowflake(0, 0, 20, sw=4) + '</g>'),
    "q": letter("Q", GREEN, '<defs>' + HOLLY_DEF + '</defs>' + holly(198, 52, .8)),
    "k": letter("K", GOLD, star(198, 52, 22, "#FFFFFF", INK, 4)),
    "a": letter("A", ICE, bauble(198, 56, 16, "#FFB0A8", "#E0202E", "#7A0410")),
    "bell": medallion("circle", GREEN, bell),
    "candle": medallion("shield", NIGHT, candle),
    "cookies": medallion("square", ICE, cookie),
    "sock": medallion("hex", GREEN, sock),
    "wild": wild(RED, tree),
    "scatter": scatter(NIGHT, gift, "BONUS"),
}
