"""Egypt II: sand gold, turquoise and deep red."""
from symkit import P, gem, lg, letter, medallion, rg, scatter, sparkle, star, wild

INK = "#1E0C04"
TURQ = P("#C8FFF4", "#1EB8A8", "#065850", "#022A26")
RED = P("#FFB0A0", "#C8202A", "#640410", "#300206")
SAND = P("#FFF4C8", "#E8B04A", "#8A5410", "#3A2004")
LAPIS = P("#B8C8FF", "#3A50D0", "#141E78", "#080C3A")
DUSK = P("#FFD8A8", "#E07A2A", "#6A2408", "#2E0E02")


def eye_mini(cx, cy, s, line="url(#gold)"):
    g = ('<path d="M-30,-22 C-10,-32 16,-32 32,-24" fill="none" stroke="{L}" stroke-width="{W}" stroke-linecap="round"/>'
         '<path d="M-30,0 C-16,-16 14,-18 30,-4 C14,10 -16,10 -30,0Z" fill="{F}" stroke="{L}" stroke-width="{W}" stroke-linejoin="round"/>'
         '<path d="M30,-4 L42,-2 M-10,8 L-12,30 M4,8 C8,22 18,28 24,22" fill="none" stroke="{L}" stroke-width="{W}" stroke-linecap="round"/>')
    return (f'<g transform="translate({cx},{cy}) scale({s})">'
            + g.replace("{L}", INK).replace("{W}", "11").replace("{F}", INK)
            + g.replace("{L}", line).replace("{W}", "5").replace("{F}", "#FFF8E8")
            + f'<circle cx="0" cy="-4" r="8" fill="{INK}"/><circle cx="-3" cy="-7" r="2.5" fill="#fff"/></g>')


# ------------------------------------------------------------------ book of the dead
book = (
    '<defs>' + lg("leather", [(0, "#E04A3A"), (.5, "#A8141E"), (1, "#4A0408")], "1", "1")
    + lg("pages", [(0, "#FFF6DC"), (1, "#D8B880")], "1", "0") + '</defs>'
    # page block
    '<path d="M78,56 L188,56 L196,64 L196,206 L86,206 L78,198Z" fill="url(#pages)" stroke="' + INK + '" stroke-width="6" stroke-linejoin="round"/>'
    '<path d="M190,70 L190,200 M184,64 L184,202" stroke="#B8945A" stroke-width="2"/>'
    # cover
    '<rect x="62" y="48" width="124" height="152" rx="8" fill="url(#leather)" stroke="' + INK + '" stroke-width="7"/>'
    '<rect x="62" y="48" width="18" height="152" rx="6" fill="#5A0408" opacity=".6"/>'
    '<rect x="88" y="60" width="88" height="128" rx="4" fill="none" stroke="url(#gold)" stroke-width="5"/>'
    # gold corners
    + "".join(f'<path d="M{x},{y} l{dx},0 l{-dx},{dy}Z" fill="url(#gold)" stroke="{INK}" stroke-width="4" stroke-linejoin="round"/>'
              for x, y, dx, dy in [(186, 48, -26, 26), (186, 200, -26, -26)])
    # emblem: eye over a winged sun bar
    + '<circle cx="132" cy="124" r="34" fill="#4A0408" stroke="url(#gold)" stroke-width="5"/>'
    + eye_mini(132, 122, .85)
    + '<path d="M100,170 L164,170" stroke="url(#gold)" stroke-width="5" stroke-linecap="round"/>'
    + '<path d="M108,80 L156,80" stroke="url(#gold)" stroke-width="5" stroke-linecap="round"/>'
    + gem(132, 80, 7, "#C8FFF4", "#1EB8A8", "#065850", INK) + gem(132, 170, 7, "#C8FFF4", "#1EB8A8", "#065850", INK)
    + '<path d="M70,60 L70,190" stroke="#fff" stroke-width="4" stroke-linecap="round" opacity=".3"/>'
)

# ------------------------------------------------------------------ jewelled scarab
def wing(side):
    sx = -1 if side == "L" else 1
    path = f"M{128 + sx * 26},{106} C{128 + sx * 52},{82} {128 + sx * 76},{72} {128 + sx * 86},{80} C{128 + sx * 84},{110} {128 + sx * 62},{140} {128 + sx * 28},{144}Z"
    cid = f"wing{side}"
    bands = "".join(
        f'<path d="M{128 + sx * 20},{100 + o} C{128 + sx * 60},{74 + o} {128 + sx * 90},{66 + o} {128 + sx * 110},{72 + o}" fill="none" stroke="{c}" stroke-width="9"/>'
        for o, c in [(10, "#3A50D0"), (24, "#1EB8A8"), (38, "#C8202A"), (52, "#1EB8A8")])
    feathers = "".join(
        f'<line x1="{128 + sx * x}" y1="70" x2="{128 + sx * (x - 14)}" y2="150" stroke="{INK}" stroke-width="2" opacity=".5"/>' for x in range(46, 110, 12))
    return (f'<defs><clipPath id="{cid}"><path d="{path}"/></clipPath></defs>'
            f'<path d="{path}" fill="url(#gold)" stroke="{INK}" stroke-width="6" stroke-linejoin="round"/>'
            f'<g clip-path="url(#{cid})">{bands}{feathers}</g>'
            f'<path d="{path}" fill="none" stroke="{INK}" stroke-width="6" stroke-linejoin="round"/>')


scarab = (
    '<defs>' + rg("shell", [(0, "#B8FFF4"), (.5, "#18A898"), (1, "#04403A")], cx=".38", cy=".3", r=".8")
    + rg("sunD", [(0, "#FFC8A0"), (.5, "#E0302A"), (1, "#7A0A08")], cx=".4", cy=".35", r=".7") + '</defs>'
    + wing("L") + wing("R")
    # legs
    + '<path d="M104,128 L82,140 L76,156 M152,128 L174,140 L180,156 M106,150 L88,170 M150,150 L168,170 M110,170 L100,192 M146,170 L156,192" '
    'fill="none" stroke="' + INK + '" stroke-width="6" stroke-linecap="round" stroke-linejoin="round"/>'
    # sun disk held above
    '<circle cx="128" cy="52" r="18" fill="url(#sunD)" stroke="' + INK + '" stroke-width="5"/>'
    '<ellipse cx="122" cy="46" rx="6" ry="3.5" fill="#fff" opacity=".7"/>'
    # head and thorax
    '<path d="M110,82 C110,66 146,66 146,82Z" fill="url(#gold)" stroke="' + INK + '" stroke-width="5" stroke-linejoin="round"/>'
    '<ellipse cx="128" cy="96" rx="32" ry="17" fill="url(#gold)" stroke="' + INK + '" stroke-width="6"/>'
    # abdomen (two wing cases)
    '<ellipse cx="128" cy="146" rx="36" ry="46" fill="url(#shell)" stroke="' + INK + '" stroke-width="6"/>'
    '<path d="M128,104 L128,190" stroke="url(#gold)" stroke-width="5"/>'
    '<path d="M128,104 L128,190" stroke="' + INK + '" stroke-width="1.5"/>'
    '<path d="M104,124 C100,138 102,156 108,170" fill="none" stroke="#fff" stroke-width="5" stroke-linecap="round" opacity=".7"/>'
    + gem(128, 96, 10, "#FFB0A0", "#E0302A", "#7A0A08", INK)
)

# ------------------------------------------------------------------ papyrus scroll
glyphs = (
    # little bird
    '<path d="M86,104 C90,96 100,96 104,102 L112,100 L106,106 C104,114 92,116 86,104Z M96,114 L94,122 M100,114 L102,122" fill="#7A3A10" stroke="#7A3A10" stroke-width="2" stroke-linejoin="round"/>'
    # water line
    '<path d="M84,136 l6,-5 l6,5 l6,-5 l6,5 l6,-5 l6,5" fill="none" stroke="#1E7A9A" stroke-width="3.5" stroke-linejoin="round"/>'
    # ankh
    '<ellipse cx="136" cy="98" rx="6" ry="8" fill="none" stroke="#7A3A10" stroke-width="3.5"/>'
    '<path d="M126,108 L146,108 M136,108 L136,126" stroke="#7A3A10" stroke-width="4" stroke-linecap="round"/>'
    # eye
    '<path d="M154,102 C160,94 172,94 178,102 C172,108 160,108 154,102Z" fill="none" stroke="#7A3A10" stroke-width="3"/>'
    '<circle cx="166" cy="101" r="3" fill="#7A3A10"/><path d="M160,108 L158,118" stroke="#7A3A10" stroke-width="3"/>'
    # sun and reed
    '<circle cx="156" cy="136" r="7" fill="#C8202A" stroke="#7A3A10" stroke-width="2"/>'
    '<path d="M174,124 L174,148 M174,124 C180,128 182,132 180,136" fill="none" stroke="#2E7A3A" stroke-width="3.5" stroke-linecap="round"/>'
    '<path d="M84,150 L180,150 M84,88 L180,88" stroke="#B88A4A" stroke-width="2"/>'
)
papyrus = (
    '<defs>' + lg("pap", [(0, "#FFF2C8"), (.6, "#F0D490"), (1, "#C8A05A")])
    + lg("roll", [(0, "#C8A05A"), (.4, "#FFF0C0"), (1, "#8A6A2A")], "1", "0") + '</defs>'
    '<g transform="rotate(-8 128 128)">'
    '<rect x="70" y="76" width="118" height="92" fill="url(#pap)" stroke="' + INK + '" stroke-width="6"/>'
    + "".join(f'<line x1="70" y1="{y}" x2="188" y2="{y}" stroke="#D8B878" stroke-width="1.5"/>' for y in range(82, 166, 8))
    + glyphs
    # rolls
    + "".join(
        f'<rect x="{x}" y="66" width="24" height="112" rx="10" fill="url(#roll)" stroke="{INK}" stroke-width="6"/>'
        f'<ellipse cx="{x + 12}" cy="70" rx="12" ry="6" fill="#E8C880" stroke="{INK}" stroke-width="4"/>'
        f'<ellipse cx="{x + 12}" cy="70" rx="4" ry="2" fill="#8A6A2A"/>'
        f'<rect x="{x - 4}" y="112" width="32" height="12" rx="4" fill="url(#gold)" stroke="{INK}" stroke-width="4"/>' for x in (52, 182))
    + '</g>'
    + sparkle(190, 52, .7) + sparkle(70, 196, .55)
)

# ------------------------------------------------------------------ canopic vase
vase = (
    '<defs>' + lg("jar", [(0, "#06504A"), (.35, "#5AE0D0"), (.6, "#18A898"), (1, "#04302C")], "1", "0")
    + rg("vface", [(0, "#FFF4C0"), (.55, "#FFC83A"), (1, "#B86A00")], cx=".4", cy=".35", r=".75")
    + '<clipPath id="lidC"><path d="M90,98 C88,62 104,44 128,44 C152,44 168,62 166,98Z"/></clipPath></defs>'
    # body
    '<path d="M94,104 C70,124 70,176 98,200 L158,200 C186,176 186,124 162,104Z" fill="url(#jar)" stroke="' + INK + '" stroke-width="7" stroke-linejoin="round"/>'
    '<path d="M78,132 C110,140 146,140 178,132 L178,144 C146,152 110,152 78,144Z" fill="url(#gold)" stroke="' + INK + '" stroke-width="4" stroke-linejoin="round"/>'
    '<path d="M80,174 C110,182 146,182 176,174" fill="none" stroke="url(#gold)" stroke-width="6"/>'
    # hieroglyph column
    '<rect x="116" y="150" width="24" height="24" rx="3" fill="#FFF0C8" stroke="' + INK + '" stroke-width="3"/>'
    '<path d="M122,160 l6,-5 l6,5 M128,158 L128,170" fill="none" stroke="#C8202A" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/>'
    '<path d="M88,150 C88,164 92,178 100,188" fill="none" stroke="#fff" stroke-width="5" stroke-linecap="round" opacity=".5"/>'
    '<rect x="92" y="96" width="72" height="12" rx="4" fill="url(#gold)" stroke="' + INK + '" stroke-width="5"/>'
    # head lid with striped headdress
    '<path d="M90,98 C88,62 104,44 128,44 C152,44 168,62 166,98Z" fill="url(#gold)" stroke="' + INK + '" stroke-width="6" stroke-linejoin="round"/>'
    '<g clip-path="url(#lidC)">' + "".join(f'<rect x="80" y="{y}" width="100" height="6" fill="#C8202A"/>' for y in range(52, 100, 12)) + '</g>'
    '<path d="M108,62 C108,56 148,56 148,62 L148,84 C148,96 138,102 128,102 C118,102 108,96 108,84Z" fill="url(#vface)" stroke="' + INK + '" stroke-width="5" stroke-linejoin="round"/>'
    '<path d="M114,74 C118,70 124,70 126,74 M130,74 C132,70 138,70 142,74" fill="none" stroke="' + INK + '" stroke-width="3.5" stroke-linecap="round"/>'
    '<path d="M122,90 C126,92 130,92 134,90" fill="none" stroke="#8A3A00" stroke-width="3" stroke-linecap="round"/>'
    + sparkle(184, 70, .7)
)

# ------------------------------------------------------------------ wild: winged sun
def sun_wing(side):
    sx = -1 if side == "L" else 1
    x = lambda v: 128 + sx * v
    path = (f"M{x(26)},{86} C{x(56)},{66} {x(90)},{62} {x(106)},{66} C{x(100)},{78} {x(96)},{84} {x(84)},{90} "
            f"C{x(96)},{94} {x(98)},{102} {x(92)},{110} C{x(70)},{122} {x(46)},{124} {x(26)},{120}Z")
    cid = f"sw{side}"
    bands = "".join(f'<path d="M{x(20)},{84 + o} C{x(56)},{62 + o} {x(90)},{58 + o} {x(112)},{62 + o}" fill="none" stroke="{c}" stroke-width="8"/>'
                    for o, c in [(12, "#1EB8A8"), (26, "#3A50D0"), (40, "#1EB8A8"), (54, "#C8202A")])
    feathers = "".join(f'<line x1="{x(v)}" y1="60" x2="{x(v - 10)}" y2="130" stroke="{INK}" stroke-width="2" opacity=".45"/>' for v in range(40, 112, 11))
    return (f'<defs><clipPath id="{cid}"><path d="{path}"/></clipPath></defs>'
            f'<path d="{path}" fill="url(#gold)" stroke="{INK}" stroke-width="6" stroke-linejoin="round"/>'
            f'<g clip-path="url(#{cid})">{bands}{feathers}</g>'
            f'<path d="{path}" fill="none" stroke="{INK}" stroke-width="6" stroke-linejoin="round"/>')


winged_sun = (
    '<defs>' + rg("sunW", [(0, "#FFE0B0"), (.5, "#E8302A"), (1, "#7A0A08")], cx=".4", cy=".35", r=".7") + '</defs>'
    + sun_wing("L") + sun_wing("R")
    + ''
    '<circle cx="128" cy="96" r="36" fill="url(#gold)" stroke="' + INK + '" stroke-width="7"/>'
    '<circle cx="128" cy="96" r="27" fill="url(#sunW)" stroke="' + INK + '" stroke-width="3"/>'
    '<ellipse cx="118" cy="86" rx="10" ry="6" fill="#fff" opacity=".7" transform="rotate(-30 118 86)"/>'
    + sparkle(128, 42, .9)
)

# ------------------------------------------------------------------ scatter: Bastet cat
CAT = ("M90,86 C92,78 96,72 100,66 L100,40 L116,58 C120,56 126,56 130,58 L144,40 L144,66 C148,74 148,86 142,96 "
       "C156,108 172,124 174,150 C176,172 168,182 156,184 L94,184 C88,184 88,176 96,174 L100,132 C98,118 100,108 106,102 "
       "C98,100 92,94 90,86Z")
cat = (
    '<defs>' + lg("catG", [(0, "#4A4A6A"), (.5, "#1E1E30"), (1, "#0A0A14")], "1", "1") + '</defs>'
    '<g transform="translate(128,42) scale(.88) translate(-132,-40)">'
    # tail
    '<path d="M156,182 C186,186 196,166 186,150" fill="none" stroke="' + INK + '" stroke-width="18" stroke-linecap="round"/>'
    '<path d="M156,182 C186,186 196,166 186,150" fill="none" stroke="#1E1E30" stroke-width="9" stroke-linecap="round"/>'
    f'<path d="{CAT}" fill="url(#catG)" stroke="{INK}" stroke-width="6" stroke-linejoin="round"/>'
    '<path d="M150,118 C164,130 168,146 166,162" fill="none" stroke="#8A8AB0" stroke-width="5" stroke-linecap="round" opacity=".6"/>'
    '<path d="M104,46 L110,58 M140,46 L134,58" stroke="#C8202A" stroke-width="4" stroke-linecap="round"/>'
    # collar
    '<path d="M104,100 C116,108 134,106 144,96" fill="none" stroke="' + INK + '" stroke-width="14" stroke-linecap="round"/>'
    '<path d="M104,100 C116,108 134,106 144,96" fill="none" stroke="url(#gold)" stroke-width="8" stroke-linecap="round"/>'
    + gem(124, 110, 7, "#C8FFF4", "#1EB8A8", "#065850", INK)
    # eye and earring
    + '<path d="M104,76 C108,70 116,70 120,76 C116,80 108,80 104,76Z" fill="#7AF07A" stroke="' + INK + '" stroke-width="2"/>'
    '<path d="M112,71 L112,81" stroke="' + INK + '" stroke-width="2.5"/>'
    '<circle cx="138" cy="74" r="6" fill="none" stroke="url(#gold)" stroke-width="3"/>'
    '<path d="M90,86 L96,88" stroke="#8A8AB0" stroke-width="3" stroke-linecap="round"/></g>'
    + sparkle(72, 70, .7) + sparkle(184, 70, .6)
)

SYMBOLS = {
    "j": letter("J", TURQ, gem(198, 52, 18, "#FFB0A0", "#C8202A", "#640410", INK)),
    "q": letter("Q", RED, eye_mini(196, 54, .45)),
    "k": letter("K", SAND, gem(198, 52, 18, "#C8FFF4", "#1EB8A8", "#065850", INK)),
    "a": letter("A", LAPIS, star(198, 52, 20, "url(#gold)", "#7A3A00", 4)),
    "book": medallion("square", SAND, book),
    "scarab": medallion("hex", RED, scarab),
    "scroll": medallion("circle", TURQ, papyrus),
    "vase": medallion("shield", DUSK, vase),
    "wild": wild(TURQ, winged_sun),
    "scatter": scatter(DUSK, cat, "BONUS"),
}
