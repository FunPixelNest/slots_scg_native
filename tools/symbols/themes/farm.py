"""Farm: cute cartoon farm. Sunny greens, sky blue, barn red."""
from symkit import P, letter, medallion, scatter, sparkle, star, suit, wild

INK = "#2A1606"


def lgd(id_, *cols, x2="0", y2="1"):
    n = len(cols) - 1
    s = "".join(f'<stop offset="{i / n:.2f}" stop-color="{c}"/>' for i, c in enumerate(cols))
    return f'<defs><linearGradient id="{id_}" x1="0" y1="0" x2="{x2}" y2="{y2}">{s}</linearGradient></defs>'


def rgd(id_, *cols, cx=".38", cy=".32", r=".75"):
    n = len(cols) - 1
    s = "".join(f'<stop offset="{i / n:.2f}" stop-color="{c}"/>' for i, c in enumerate(cols))
    return f'<defs><radialGradient id="{id_}" cx="{cx}" cy="{cy}" r="{r}">{s}</radialGradient></defs>'


def eye(x, y, r=9):
    return (f'<ellipse cx="{x}" cy="{y}" rx="{r}" ry="{r * 1.2:.1f}" fill="#1A0E06"/>'
            f'<circle cx="{x - r * .3:.1f}" cy="{y - r * .4:.1f}" r="{r * .38:.1f}" fill="#fff"/>')


def leaf(d, ink=INK):
    return (f'<path d="{d}" fill="url(#fLeaf)" stroke="{ink}" stroke-width="5" stroke-linejoin="round"/>')


LEAFG = lgd("fLeaf", "#B6F060", "#4CB030", "#1E6A1A")

SKY = P("#D8F4FF", "#6CC6F0", "#1F6FB0", "#0B2A4A")
GRASS = P("#E6FFB0", "#8ED448", "#3A8A1E", "#16380A")
BARN = P("#FFC0A8", "#D8402E", "#701410", "#2E0604")
SUN = P("#FFF6B8", "#FFC83A", "#C06A08", "#4A2002")
LILAC = P("#F2E0FF", "#B98AE6", "#5A2E9A", "#22103E")
TEAL = P("#D6FFF2", "#5ED6B4", "#1A7A68", "#082E28")

apple = (
    LEAFG + rgd("fApple", "#FFB4A0", "#F0303A", "#8A0A16")
    + '<path d="M128,96 C110,78 66,80 62,120 C58,160 88,204 112,202 C122,201 124,196 128,196 C132,196 134,201 144,202 C168,204 198,160 194,120 C190,80 146,78 128,96Z" fill="url(#fApple)" stroke="#3A0408" stroke-width="7" stroke-linejoin="round"/>'
    '<path d="M84,112 C90,96 104,92 112,96 C100,104 94,118 92,132 C86,130 82,122 84,112Z" fill="#fff" opacity=".75"/>'
    '<ellipse cx="96" cy="150" rx="6" ry="10" fill="#fff" opacity=".4"/>'
    '<path d="M128,98 C127,84 130,72 140,60" stroke="#3A0408" stroke-width="14" fill="none" stroke-linecap="round"/>'
    '<path d="M128,98 C127,84 130,72 140,60" stroke="#8A5228" stroke-width="7" fill="none" stroke-linecap="round"/>'
    + leaf("M136,80 C146,56 176,54 188,62 C180,84 154,92 136,80Z")
    + '<path d="M140,78 C154,70 168,66 182,64" stroke="#1E6A1A" stroke-width="3" fill="none"/>'
)

beet = (
    LEAFG + rgd("fBeet", "#FF9AD0", "#C01A6A", "#4A0228")
    + '<path d="M128,98 C112,82 96,56 84,48" stroke="#2A0414" stroke-width="12" fill="none" stroke-linecap="round"/>'
    '<path d="M128,98 C112,82 96,56 84,48" stroke="#D8306A" stroke-width="5" fill="none" stroke-linecap="round"/>'
    '<path d="M128,98 C144,82 160,56 172,48" stroke="#2A0414" stroke-width="12" fill="none" stroke-linecap="round"/>'
    '<path d="M128,98 C144,82 160,56 172,48" stroke="#D8306A" stroke-width="5" fill="none" stroke-linecap="round"/>'
    + leaf("M100,74 C70,78 54,58 58,38 C82,34 104,50 100,74Z")
    + leaf("M156,74 C186,78 202,58 198,38 C174,34 152,50 156,74Z")
    + leaf("M128,96 C108,78 110,52 128,40 C146,52 148,78 128,96Z")
    + '<path d="M128,92 L128,50 M98,70 L66,44 M158,70 L190,44" stroke="#1E6A1A" stroke-width="3" fill="none"/>'
    '<path d="M128,214 C126,206 128,200 128,196 C116,186 78,170 76,136 C74,108 98,94 128,94 C158,94 182,108 180,136 C178,170 140,186 128,196" fill="url(#fBeet)" stroke="#2A0414" stroke-width="7" stroke-linejoin="round" stroke-linecap="round"/>'
    '<path d="M94,118 C100,106 112,102 120,104 C108,112 102,124 100,136 C94,132 92,126 94,118Z" fill="#fff" opacity=".6"/>'
    '<path d="M110,160 C120,164 136,164 146,160 M118,176 C124,178 132,178 138,176" stroke="#6A0A3A" stroke-width="3" fill="none" stroke-linecap="round"/>'
)

cow = (
    rgd("fCow", "#FFFFFF", "#F2EEE8", "#C8BCB0") + rgd("fMuz", "#FFE0E6", "#F7A8B8", "#C86A80")
    # horns
    + '<path d="M92,86 C80,76 76,60 84,48 C92,58 100,68 108,76Z" fill="#FFF0C8" stroke="#2A1A10" stroke-width="6" stroke-linejoin="round"/>'
    '<path d="M164,86 C176,76 180,60 172,48 C164,58 156,68 148,76Z" fill="#FFF0C8" stroke="#2A1A10" stroke-width="6" stroke-linejoin="round"/>'
    # ears
    '<path d="M86,104 C66,94 48,100 42,110 C52,124 72,126 90,118Z" fill="#F2EEE8" stroke="#2A1A10" stroke-width="6" stroke-linejoin="round"/>'
    '<path d="M170,104 C190,94 208,100 214,110 C204,124 184,126 166,118Z" fill="#F2EEE8" stroke="#2A1A10" stroke-width="6" stroke-linejoin="round"/>'
    '<path d="M80,108 C68,106 58,108 54,112 C62,118 74,118 84,114Z" fill="#F7A8B8"/>'
    '<path d="M176,108 C188,106 198,108 202,112 C194,118 182,118 172,114Z" fill="#F7A8B8"/>'
    # head
    '<path d="M128,72 C168,72 182,100 180,132 C178,160 168,176 128,176 C88,176 78,160 76,132 C74,100 88,72 128,72Z" fill="url(#fCow)" stroke="#2A1A10" stroke-width="7"/>'
    '<path d="M86,96 C96,80 120,78 122,92 C124,110 102,120 92,128 C84,120 80,106 86,96Z" fill="#2E2420"/>'
    '<path d="M164,140 C176,136 180,124 176,114 C166,116 158,128 164,140Z" fill="#2E2420"/>'
    '<path d="M116,74 C120,62 128,62 130,72 C134,64 142,66 140,76Z" fill="#2E2420" stroke="#2A1A10" stroke-width="3"/>'
    + eye(108, 124, 9) + eye(148, 124, 9)
    # muzzle
    + '<ellipse cx="128" cy="168" rx="50" ry="32" fill="url(#fMuz)" stroke="#2A1A10" stroke-width="7"/>'
    '<ellipse cx="110" cy="164" rx="8" ry="10" fill="#7A2A3A"/><ellipse cx="146" cy="164" rx="8" ry="10" fill="#7A2A3A"/>'
    '<path d="M112,184 C120,190 136,190 144,184" stroke="#7A2A3A" stroke-width="4" fill="none" stroke-linecap="round"/>'
    '<ellipse cx="108" cy="154" rx="14" ry="5" fill="#fff" opacity=".6"/>'
    '<ellipse cx="102" cy="96" rx="10" ry="5" fill="#fff" opacity=".25"/>'
)

hen = (
    rgd("fHen", "#FFFFFF", "#FFF4E0", "#E0C8A0") + rgd("fComb", "#FF9A8A", "#E8202A", "#7A0408")
    # comb
    + '<path d="M100,78 C92,58 106,46 116,56 C116,40 140,38 140,54 C148,42 166,50 160,68 C170,70 168,84 156,86 L104,88Z" fill="url(#fComb)" stroke="#3A0404" stroke-width="6" stroke-linejoin="round"/>'
    # wings
    '<path d="M76,136 C56,140 50,160 58,176 C70,174 82,164 86,150Z" fill="#F2DDB6" stroke="#2A1A10" stroke-width="6" stroke-linejoin="round"/>'
    '<path d="M180,136 C200,140 206,160 198,176 C186,174 174,164 170,150Z" fill="#F2DDB6" stroke="#2A1A10" stroke-width="6" stroke-linejoin="round"/>'
    # body
    '<path d="M128,72 C170,72 186,108 184,144 C182,184 160,204 128,204 C96,204 74,184 72,144 C70,108 86,72 128,72Z" fill="url(#fHen)" stroke="#2A1A10" stroke-width="7"/>'
    '<path d="M98,180 C108,190 120,192 128,192 M112,172 C118,178 124,180 128,180" stroke="#E0C8A0" stroke-width="4" fill="none" stroke-linecap="round"/>'
    + eye(108, 118, 9) + eye(148, 118, 9)
    + '<ellipse cx="94" cy="140" rx="10" ry="6" fill="#FF8AA0" opacity=".6"/><ellipse cx="162" cy="140" rx="10" ry="6" fill="#FF8AA0" opacity=".6"/>'
    # wattle
    '<path d="M120,148 C114,164 120,176 128,176 C136,176 142,164 136,148Z" fill="url(#fComb)" stroke="#3A0404" stroke-width="5" stroke-linejoin="round"/>'
    # beak
    '<path d="M110,136 L128,128 L146,136 L128,154Z" fill="#FFB020" stroke="#5A2A02" stroke-width="5" stroke-linejoin="round"/>'
    '<path d="M110,136 L146,136" stroke="#5A2A02" stroke-width="3"/>'
    '<path d="M116,134 L128,130 L138,134Z" fill="#FFE080"/>'
    '<ellipse cx="100" cy="92" rx="12" ry="6" fill="#fff" opacity=".9"/>'
)

pig = (
    rgd("fPig", "#FFE4EA", "#FFA8BC", "#D8607E") + rgd("fSnout", "#FFD0DC", "#FF8AA6", "#C84A6A")
    # ears
    + '<path d="M84,96 C74,74 70,56 76,48 C92,52 108,68 114,84Z" fill="url(#fPig)" stroke="#4A1020" stroke-width="7" stroke-linejoin="round"/>'
    '<path d="M172,96 C182,74 186,56 180,48 C164,52 148,68 142,84Z" fill="url(#fPig)" stroke="#4A1020" stroke-width="7" stroke-linejoin="round"/>'
    '<path d="M88,86 C82,72 80,62 82,58 C92,62 100,70 104,80Z" fill="#E8708E"/>'
    '<path d="M168,86 C174,72 176,62 174,58 C164,62 156,70 152,80Z" fill="#E8708E"/>'
    # head
    '<path d="M128,76 C176,76 192,108 190,140 C188,180 160,200 128,200 C96,200 68,180 66,140 C64,108 80,76 128,76Z" fill="url(#fPig)" stroke="#4A1020" stroke-width="7"/>'
    '<path d="M120,80 C122,72 128,70 130,78 C134,70 140,72 138,80" stroke="#4A1020" stroke-width="4" fill="none" stroke-linecap="round"/>'
    + eye(102, 124, 9) + eye(154, 124, 9)
    + '<ellipse cx="86" cy="158" rx="12" ry="7" fill="#FF6A8A" opacity=".5"/><ellipse cx="170" cy="158" rx="12" ry="7" fill="#FF6A8A" opacity=".5"/>'
    # snout
    '<ellipse cx="128" cy="156" rx="34" ry="24" fill="url(#fSnout)" stroke="#4A1020" stroke-width="7"/>'
    '<ellipse cx="116" cy="156" rx="7" ry="10" fill="#7A1A34"/><ellipse cx="140" cy="156" rx="7" ry="10" fill="#7A1A34"/>'
    '<ellipse cx="118" cy="142" rx="12" ry="4" fill="#fff" opacity=".6"/>'
    '<path d="M112,186 C120,192 136,192 144,186" stroke="#7A1A34" stroke-width="4" fill="none" stroke-linecap="round"/>'
    '<ellipse cx="96" cy="96" rx="14" ry="7" fill="#fff" opacity=".55" transform="rotate(-20 96 96)"/>'
)

pumpkin = (
    LEAFG + rgd("fPump", "#FFD890", "#FF8A1A", "#A03A02") + rgd("fPump2", "#FFC870", "#F07A10", "#8A3002")
    + '<path d="M132,82 C128,66 134,54 146,46" stroke="#1A3008" stroke-width="16" fill="none" stroke-linecap="round"/>'
    '<path d="M132,82 C128,66 134,54 146,46" stroke="#5A8A2A" stroke-width="8" fill="none" stroke-linecap="round"/>'
    '<ellipse cx="84" cy="140" rx="30" ry="56" fill="url(#fPump2)" stroke="#4A1A02" stroke-width="6"/>'
    '<ellipse cx="172" cy="140" rx="30" ry="56" fill="url(#fPump2)" stroke="#4A1A02" stroke-width="6"/>'
    '<ellipse cx="106" cy="140" rx="30" ry="60" fill="url(#fPump)" stroke="#4A1A02" stroke-width="6"/>'
    '<ellipse cx="150" cy="140" rx="30" ry="60" fill="url(#fPump)" stroke="#4A1A02" stroke-width="6"/>'
    '<ellipse cx="128" cy="140" rx="28" ry="62" fill="url(#fPump)" stroke="#4A1A02" stroke-width="6"/>'
    '<path d="M60,140 C60,100 90,80 128,80 C166,80 196,100 196,140 C196,182 166,202 128,202 C90,202 60,182 60,140Z" fill="none" stroke="#4A1A02" stroke-width="7"/>'
    '<ellipse cx="118" cy="108" rx="8" ry="20" fill="#fff" opacity=".55"/>'
    '<ellipse cx="82" cy="124" rx="5" ry="14" fill="#fff" opacity=".35"/>'
    + leaf("M140,80 C150,64 178,62 190,72 C178,90 156,92 140,80Z")
    + '<path d="M118,84 C104,82 100,70 108,64 C116,60 120,70 112,72" stroke="#2A5A10" stroke-width="4" fill="none" stroke-linecap="round"/>'
)


def wool(cx, cy, r):
    return (f'<circle cx="{cx}" cy="{cy}" r="{r}" fill="url(#fWool)" stroke="#5A4A40" stroke-width="5"/>')


sheep = (
    rgd("fWool", "#FFFFFF", "#F4F0EA", "#CFC6BA") + rgd("fSface", "#7A6A64", "#4A3C38", "#241A18")
    + "".join(wool(x, y, 22) for x, y in [(92, 84), (128, 72), (164, 84), (186, 116), (182, 152), (162, 180), (128, 190), (94, 180), (74, 152), (70, 116)])
    + '<circle cx="128" cy="132" r="56" fill="url(#fWool)"/>'
    # ears
    '<path d="M98,118 C80,112 62,118 56,128 C66,140 86,138 100,130Z" fill="url(#fSface)" stroke="#1A1010" stroke-width="5" stroke-linejoin="round"/>'
    '<path d="M158,118 C176,112 194,118 200,128 C190,140 170,138 156,130Z" fill="url(#fSface)" stroke="#1A1010" stroke-width="5" stroke-linejoin="round"/>'
    # face
    '<path d="M128,98 C152,98 160,118 158,142 C156,170 146,184 128,184 C110,184 100,170 98,142 C96,118 104,98 128,98Z" fill="url(#fSface)" stroke="#1A1010" stroke-width="6"/>'
    '<circle cx="114" cy="132" r="11" fill="#fff"/><circle cx="142" cy="132" r="11" fill="#fff"/>'
    + eye(116, 134, 6) + eye(140, 134, 6)
    + '<path d="M120,164 L128,170 L136,164" stroke="#F0B0B8" stroke-width="4" fill="none" stroke-linecap="round" stroke-linejoin="round"/>'
    '<ellipse cx="128" cy="158" rx="6" ry="4" fill="#F0B0B8"/>'
    # top tuft
    + wool(110, 100, 15) + wool(128, 94, 16) + wool(146, 100, 15)
    + '<ellipse cx="88" cy="78" rx="8" ry="5" fill="#fff"/><ellipse cx="124" cy="66" rx="8" ry="5" fill="#fff"/>'
)

tomato = (
    LEAFG + rgd("fTom", "#FFB0A0", "#F22A20", "#8A0606")
    + '<path d="M128,86 C176,82 200,110 198,144 C196,184 164,204 128,204 C92,204 60,184 58,144 C56,110 80,82 128,86Z" fill="url(#fTom)" stroke="#3A0404" stroke-width="7"/>'
    '<path d="M90,108 C98,96 112,94 118,98 C106,106 98,118 96,132 C88,128 86,118 90,108Z" fill="#fff" opacity=".75"/>'
    '<path d="M100,190 C116,198 140,198 156,190" stroke="#B01010" stroke-width="4" fill="none" opacity=".6" stroke-linecap="round"/>'
    '<path d="M128,98 L112,112 L114,96 L92,96 L110,86 L100,72 L120,80 L128,64 L136,80 L156,72 L146,86 L164,96 L142,96 L144,112Z" fill="url(#fLeaf)" stroke="#14400A" stroke-width="5" stroke-linejoin="round"/>'
    '<path d="M128,84 C128,72 130,62 138,54" stroke="#14400A" stroke-width="12" fill="none" stroke-linecap="round"/>'
    '<path d="M128,84 C128,72 130,62 138,54" stroke="#5AB030" stroke-width="5" fill="none" stroke-linecap="round"/>'
)

barn = (
    lgd("fBarnR", "#FF8A6A", "#D0302A", "#7A1010") + lgd("fRoof", "#7A4A3A", "#4A2A22")
    + '<path d="M60,104 L128,50 L196,104 L196,164 L60,164Z" fill="url(#fBarnR)" stroke="#2A0604" stroke-width="7" stroke-linejoin="round"/>'
    '<path d="M48,108 L128,42 L208,108 L196,116 L128,60 L60,116Z" fill="#FFFFFF" stroke="#2A0604" stroke-width="6" stroke-linejoin="round"/>'
    '<rect x="102" y="114" width="52" height="50" fill="#8A1A14" stroke="#2A0604" stroke-width="5"/>'
    '<path d="M102,114 L154,164 M154,114 L102,164" stroke="#fff" stroke-width="7"/>'
    '<rect x="102" y="114" width="52" height="50" fill="none" stroke="#fff" stroke-width="5"/>'
    '<circle cx="128" cy="88" r="12" fill="#FFE07A" stroke="#2A0604" stroke-width="5"/>'
    '<path d="M128,80 L128,96 M120,88 L136,88" stroke="#2A0604" stroke-width="3"/>'
    '<path d="M70,112 L70,158 M186,112 L186,158" stroke="#fff" stroke-width="5" opacity=".7"/>'
)

tractor = (
    lgd("fTrac", "#9AF070", "#3AA82A", "#145A12") + lgd("fCab", "#BFEFFF", "#5CB8E8")
    + '<path d="M78,150 L78,116 L118,116 L122,82 L164,82 L170,116 L182,118 L186,150Z" fill="url(#fTrac)" stroke="#0A2A06" stroke-width="6" stroke-linejoin="round"/>'
    '<path d="M128,90 L158,90 L162,116 L126,116Z" fill="url(#fCab)" stroke="#0A2A06" stroke-width="4" stroke-linejoin="round"/>'
    '<rect x="88" y="98" width="10" height="20" rx="3" fill="#555" stroke="#0A2A06" stroke-width="4"/>'
    '<circle cx="93" cy="92" r="5" fill="#ddd" opacity=".8"/><circle cx="86" cy="82" r="6" fill="#ddd" opacity=".6"/>'
    '<path d="M84,124 L116,124" stroke="#fff" stroke-width="5" opacity=".5" stroke-linecap="round"/>'
    '<circle cx="160" cy="150" r="32" fill="#2A2A2A" stroke="#0A0A0A" stroke-width="6"/>'
    '<circle cx="160" cy="150" r="16" fill="#FFC83A" stroke="#5A3002" stroke-width="5"/>'
    '<circle cx="160" cy="150" r="27" fill="none" stroke="#555" stroke-width="4" stroke-dasharray="6 6"/>'
    '<circle cx="94" cy="160" r="20" fill="#2A2A2A" stroke="#0A0A0A" stroke-width="6"/>'
    '<circle cx="94" cy="160" r="9" fill="#FFC83A" stroke="#5A3002" stroke-width="4"/>'
    + sparkle(178, 70, .7)
)

SYMBOLS = {
    "apple": medallion("circle", SKY, apple),
    "beet": medallion("hex", GRASS, beet),
    "cow": medallion("shield", GRASS, cow),
    "hen": medallion("circle", SUN, hen),
    "pig": medallion("square", SKY, pig),
    "pumpkin": medallion("hex", TEAL, pumpkin),
    "sheep": medallion("square", LILAC, sheep),
    "tomato": medallion("diamond", SKY, f'<g transform="translate(128 134) scale(.8) translate(-128 -134)">{tomato}</g>'),
    "wild": wild(SKY, barn),
    "scatter": scatter(BARN, tractor, "BONUS"),
}
