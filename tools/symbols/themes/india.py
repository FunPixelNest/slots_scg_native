"""India: saffron, magenta, emerald and gold."""
import math

from symkit import P, gem, letter, medallion, scatter, sparkle, star, wild


def lgd(id_, *cols, x2="0", y2="1"):
    n = len(cols) - 1
    s = "".join(f'<stop offset="{i / n:.2f}" stop-color="{c}"/>' for i, c in enumerate(cols))
    return f'<defs><linearGradient id="{id_}" x1="0" y1="0" x2="{x2}" y2="{y2}">{s}</linearGradient></defs>'


def rgd(id_, *cols, cx=".38", cy=".32", r=".75"):
    n = len(cols) - 1
    s = "".join(f'<stop offset="{i / n:.2f}" stop-color="{c}"/>' for i, c in enumerate(cols))
    return f'<defs><radialGradient id="{id_}" cx="{cx}" cy="{cy}" r="{r}">{s}</radialGradient></defs>'


def eye(x, y, r=8, iris=None):
    ir = f'<ellipse cx="{x}" cy="{y}" rx="{r}" ry="{r * 1.1:.1f}" fill="{iris}"/>' if iris else ""
    return (f'<ellipse cx="{x}" cy="{y}" rx="{r}" ry="{r * 1.1:.1f}" fill="#1A0E06"/>{ir}'
            + (f'<ellipse cx="{x}" cy="{y}" rx="{r * .45:.1f}" ry="{r * .8:.1f}" fill="#1A0E06"/>' if iris else "")
            + f'<circle cx="{x - r * .3:.1f}" cy="{y - r * .4:.1f}" r="{r * .36:.1f}" fill="#fff"/>')


SAFFRON = P("#FFE6A8", "#FF9A1A", "#A8400A", "#3E1402")
MAGENTA = P("#FFC8EC", "#E0208A", "#6A0440", "#2A0218")
EMERALD = P("#C0FFDC", "#18A860", "#04462A", "#021E12")
ROYAL = P("#C8E4FF", "#2A6AD8", "#0A1E6A", "#040C30")
TURQ = P("#C8FFF8", "#1AB8B0", "#05504E", "#022424")
GOLDP = P("#FFF4B8", "#F2B230", "#8A4C04", "#341802")

elephant = (
    rgd("eSkin", "#D8DCE8", "#9AA0B8", "#565C78") + rgd("eEar", "#C8CCDC", "#8A90AA", "#4A5070") + lgd("tusk", "#FFFFFF", "#F0E6C8")
    # ears
    + '<path d="M96,88 C64,64 30,82 34,124 C38,162 70,174 100,150Z" fill="url(#eEar)" stroke="#1A1A2E" stroke-width="7" stroke-linejoin="round"/>'
    '<path d="M160,88 C192,64 226,82 222,124 C218,162 186,174 156,150Z" fill="url(#eEar)" stroke="#1A1A2E" stroke-width="7" stroke-linejoin="round"/>'
    '<path d="M90,98 C68,84 48,96 50,124 C52,150 72,158 92,144Z" fill="#F2A8C8" opacity=".75"/>'
    '<path d="M166,98 C188,84 208,96 206,124 C204,150 184,158 164,144Z" fill="#F2A8C8" opacity=".75"/>'
    + "".join(f'<circle cx="{x}" cy="{y}" r="4" fill="{c}" stroke="#5A0A3A" stroke-width="1.5"/>' for x, y, c in
              [(62, 110, "#FFC83A"), (70, 130, "#E0208A"), (66, 148, "#FFC83A"), (194, 110, "#FFC83A"), (186, 130, "#E0208A"), (190, 148, "#FFC83A")])
    # head
    + '<path d="M128,60 C162,60 176,86 172,116 C170,134 160,146 146,152 L110,152 C96,146 86,134 84,116 C80,86 94,60 128,60Z" fill="url(#eSkin)" stroke="#1A1A2E" stroke-width="7" stroke-linejoin="round"/>'
    # tusks
    '<path d="M110,144 C104,162 92,172 80,174 C86,166 92,156 96,144Z" fill="url(#tusk)" stroke="#1A1A2E" stroke-width="5" stroke-linejoin="round"/>'
    '<path d="M146,144 C152,162 164,172 176,174 C170,166 164,156 160,144Z" fill="url(#tusk)" stroke="#1A1A2E" stroke-width="5" stroke-linejoin="round"/>'
    # trunk
    '<path d="M110,128 C106,160 112,186 128,200 C142,210 166,208 172,192 C164,186 158,194 150,190 C140,184 140,160 146,128Z" fill="url(#eSkin)" stroke="#1A1A2E" stroke-width="7" stroke-linejoin="round"/>'
    '<path d="M114,160 C122,164 134,164 142,160 M116,174 C124,178 134,178 142,174 M122,188 C128,191 136,191 142,188" stroke="#565C78" stroke-width="3" fill="none" stroke-linecap="round"/>'
    + eye(104, 116, 7) + eye(152, 116, 7)
    # headdress
    + '<path d="M128,58 L152,74 L144,104 L128,116 L112,104 L104,74Z" fill="url(#gold)" stroke="#5A2A00" stroke-width="5" stroke-linejoin="round"/>'
    '<path d="M128,66 L144,78 L138,100 L128,108 L118,100 L112,78Z" fill="#E0208A" stroke="#5A2A00" stroke-width="3" stroke-linejoin="round"/>'
    + gem(128, 86, 11, "#B8FFD8", "#18A860", "#04462A")
    + '<path d="M104,74 C96,88 92,100 90,112 M152,74 C160,88 164,100 166,112" stroke="url(#gold)" stroke-width="4" fill="none" stroke-dasharray="1 7" stroke-linecap="round"/>'
    '<circle cx="128" cy="126" r="5" fill="url(#gold)" stroke="#5A2A00" stroke-width="2"/>'
    '<ellipse cx="102" cy="78" rx="10" ry="5" fill="#fff" opacity=".4" transform="rotate(-30 102 78)"/>'
)

monkey = (
    rgd("mFur", "#D8A070", "#9A5A2A", "#4A2408") + rgd("mFace", "#FFF0D8", "#F6CFA0", "#D09A68")
    + '<circle cx="62" cy="122" r="26" fill="url(#mFur)" stroke="#2A1206" stroke-width="7"/><circle cx="62" cy="122" r="14" fill="#F2B0A0"/>'
    '<circle cx="194" cy="122" r="26" fill="url(#mFur)" stroke="#2A1206" stroke-width="7"/><circle cx="194" cy="122" r="14" fill="#F2B0A0"/>'
    '<path d="M128,58 C170,58 188,90 186,126 C184,170 160,196 128,196 C96,196 72,170 70,126 C68,90 86,58 128,58Z" fill="url(#mFur)" stroke="#2A1206" stroke-width="7"/>'
    '<path d="M112,62 C116,46 126,44 128,58 C132,44 146,46 144,62" fill="url(#mFur)" stroke="#2A1206" stroke-width="5" stroke-linejoin="round"/>'
    '<path d="M128,104 C140,86 172,88 172,114 C172,132 160,140 156,148 C160,174 148,190 128,190 C108,190 96,174 100,148 C96,140 84,132 84,114 C84,88 116,86 128,104Z" fill="url(#mFace)" stroke="#2A1206" stroke-width="5" stroke-linejoin="round"/>'
    + eye(110, 118, 9) + eye(146, 118, 9)
    + '<ellipse cx="121" cy="146" rx="3.5" ry="5" fill="#4A2408"/><ellipse cx="135" cy="146" rx="3.5" ry="5" fill="#4A2408"/>'
    '<path d="M108,162 C118,176 138,176 148,162" stroke="#4A2408" stroke-width="5" fill="none" stroke-linecap="round"/>'
    '<path d="M112,164 C120,172 136,172 144,164 C138,168 118,168 112,164Z" fill="#E05A6A"/>'
    '<ellipse cx="96" cy="140" rx="8" ry="5" fill="#FF8A8A" opacity=".5"/><ellipse cx="160" cy="140" rx="8" ry="5" fill="#FF8A8A" opacity=".5"/>'
    '<circle cx="128" cy="96" r="5" fill="#E0208A" stroke="#5A0A3A" stroke-width="2"/>'
    '<ellipse cx="96" cy="84" rx="12" ry="6" fill="#fff" opacity=".3" transform="rotate(-30 96 84)"/>'
)


def feather(ang, dist=76, cx=128, cy=142, w=19, h=30):
    x = cx + dist * math.cos(math.radians(ang))
    y = cy + dist * math.sin(math.radians(ang))
    rot = ang + 90
    return (f'<line x1="{cx}" y1="{cy}" x2="{x:.1f}" y2="{y:.1f}" stroke="#2A5A10" stroke-width="3"/>'
            f'<g transform="translate({x:.1f} {y:.1f}) rotate({rot:.1f})">'
            f'<ellipse rx="{w}" ry="{h}" fill="url(#pFeat)" stroke="#06301A" stroke-width="4"/>'
            f'<ellipse cy="-3" rx="{w * .6:.1f}" ry="{h * .55:.1f}" fill="#FFC83A" stroke="#7A4A00" stroke-width="2"/>'
            f'<ellipse cy="-2" rx="{w * .42:.1f}" ry="{h * .38:.1f}" fill="#1AA8A0"/>'
            f'<ellipse cy="-1" rx="{w * .26:.1f}" ry="{h * .24:.1f}" fill="#0A1E6A"/></g>')


peacock = (
    rgd("pFeat", "#9AF0A0", "#2AA84A", "#0A5A2A") + lgd("pBody", "#7AD8FF", "#1A6AD8", "#0A2A7A", x2="1", y2="1")
    + "".join(feather(a) for a in (-190, -164, -138, -112, -86, -60, -34, -8, 16, 168))
    + "".join(feather(a, 46, w=13, h=20) for a in (-165, -140, -115, -90, -65, -40, -15))
    # body
    + '<path d="M128,206 C102,206 96,176 108,156 C114,144 118,126 116,104 C114,86 122,74 134,74 C146,74 150,90 142,102 C134,116 140,134 148,152 C160,180 154,206 128,206Z" fill="url(#pBody)" stroke="#06143A" stroke-width="7" stroke-linejoin="round"/>'
    '<path d="M122,108 C122,128 118,146 112,160" stroke="#fff" stroke-width="5" fill="none" opacity=".5" stroke-linecap="round"/>'
    '<path d="M110,176 C120,184 138,184 148,176 C146,196 138,202 128,202 C116,202 110,194 110,176Z" fill="#18A860" opacity=".6"/>'
    # head details
    '<path d="M144,88 L162,92 L144,98Z" fill="#FFB020" stroke="#5A2A00" stroke-width="3" stroke-linejoin="round"/>'
    '<path d="M126,86 C130,80 140,80 144,86 C138,84 132,86 126,86Z" fill="#fff"/>'
    + eye(136, 88, 4)
    + '<path d="M128,74 L120,52 M134,74 L134,50 M140,74 L148,52" stroke="#06143A" stroke-width="2.5"/>'
    '<circle cx="120" cy="52" r="5" fill="#1A6AD8" stroke="#06143A" stroke-width="2"/><circle cx="134" cy="49" r="5" fill="#1A6AD8" stroke="#06143A" stroke-width="2"/><circle cx="148" cy="52" r="5" fill="#1A6AD8" stroke="#06143A" stroke-width="2"/>'
)

tiger = (
    rgd("tFur", "#FFD090", "#FF8A1A", "#B8460A") + rgd("tWhite", "#FFFFFF", "#FFF4E4", "#E8D4B8")
    # ears
    + '<circle cx="78" cy="78" r="24" fill="url(#tFur)" stroke="#2A1004" stroke-width="7"/><circle cx="80" cy="80" r="12" fill="#2A1004"/><circle cx="82" cy="82" r="7" fill="#FFF4E4"/>'
    '<circle cx="178" cy="78" r="24" fill="url(#tFur)" stroke="#2A1004" stroke-width="7"/><circle cx="176" cy="80" r="12" fill="#2A1004"/><circle cx="174" cy="82" r="7" fill="#FFF4E4"/>'
    # head with cheek fluff
    '<path d="M128,62 C170,62 190,90 190,120 L204,132 L190,140 L200,156 L182,160 C172,186 152,198 128,198 C104,198 84,186 74,160 L56,156 L66,140 L52,132 L66,120 C66,90 86,62 128,62Z" fill="url(#tFur)" stroke="#2A1004" stroke-width="7" stroke-linejoin="round"/>'
    # stripes
    '<path d="M128,64 L122,80 L128,92 L134,80Z M106,68 C108,78 112,84 116,88 C108,86 102,80 100,70Z M150,68 C148,78 144,84 140,88 C148,86 154,80 156,70Z" fill="#2A1004"/>'
    '<path d="M68,124 C80,124 90,128 96,134 C86,134 76,132 68,130Z M70,144 C80,142 88,144 94,148 C86,150 78,150 72,150Z M188,124 C176,124 166,128 160,134 C170,134 180,132 188,130Z M186,144 C176,142 168,144 162,148 C170,150 178,150 184,150Z" fill="#2A1004"/>'
    # white muzzle
    '<path d="M128,132 C146,132 166,144 166,162 C166,182 148,192 128,192 C108,192 90,182 90,162 C90,144 110,132 128,132Z" fill="url(#tWhite)" stroke="#2A1004" stroke-width="4"/>'
    '<ellipse cx="94" cy="112" rx="18" ry="10" fill="#FFF4E4" opacity=".7"/><ellipse cx="162" cy="112" rx="18" ry="10" fill="#FFF4E4" opacity=".7"/>'
    + eye(102, 112, 9, "#9AD83A") + eye(154, 112, 9, "#9AD83A")
    + '<path d="M114,142 L142,142 C142,152 134,158 128,160 C122,158 114,152 114,142Z" fill="#E05A6A" stroke="#2A1004" stroke-width="4" stroke-linejoin="round"/>'
    '<path d="M128,160 L128,170 M128,170 C122,178 112,178 106,172 M128,170 C134,178 144,178 150,172" stroke="#2A1004" stroke-width="4" fill="none" stroke-linecap="round"/>'
    '<circle cx="108" cy="162" r="2" fill="#2A1004"/><circle cx="100" cy="168" r="2" fill="#2A1004"/><circle cx="148" cy="162" r="2" fill="#2A1004"/><circle cx="156" cy="168" r="2" fill="#2A1004"/>'
    '<ellipse cx="118" cy="146" rx="5" ry="2.5" fill="#fff" opacity=".7"/>'
)

palace = (
    lgd("pal", "#FFFFFF", "#F4E8D8", "#D8C0A0") + lgd("dome", "#FFF7C4", "#FFC83A", "#C07000", x2="1", y2="0")
    # minarets
    + "".join(f'<rect x="{x - 8}" y="92" width="16" height="74" fill="url(#pal)" stroke="#3A1A06" stroke-width="5"/>'
              f'<path d="M{x - 11},92 C{x - 11},76 {x},70 {x},62 C{x},70 {x + 11},76 {x + 11},92Z" fill="url(#dome)" stroke="#3A1A06" stroke-width="4" stroke-linejoin="round"/>'
              f'<rect x="{x - 11}" y="114" width="22" height="6" fill="#E0208A" stroke="#3A1A06" stroke-width="3"/>' for x in (66, 190))
    # main dome
    + '<path d="M96,104 C80,86 96,66 116,56 C124,52 128,44 128,34 C128,44 132,52 140,56 C160,66 176,86 160,104Z" fill="url(#dome)" stroke="#3A1A06" stroke-width="6" stroke-linejoin="round"/>'
    '<path d="M110,94 C104,82 112,70 124,62" stroke="#fff" stroke-width="5" fill="none" opacity=".6" stroke-linecap="round"/>'
    '<circle cx="128" cy="30" r="5" fill="url(#gold)" stroke="#3A1A06" stroke-width="3"/>'
    # hall
    '<rect x="80" y="100" width="96" height="66" fill="url(#pal)" stroke="#3A1A06" stroke-width="6"/>'
    '<rect x="76" y="98" width="104" height="10" rx="3" fill="#E0208A" stroke="#3A1A06" stroke-width="4"/>'
    '<path d="M112,166 L112,136 C112,124 120,118 128,114 C136,118 144,124 144,136 L144,166Z" fill="#7A1A5A" stroke="#3A1A06" stroke-width="4" stroke-linejoin="round"/>'
    '<path d="M88,140 L88,124 C88,118 92,116 96,114 C100,116 104,118 104,124 L104,140Z M152,140 L152,124 C152,118 156,116 160,114 C164,116 168,118 168,124 L168,140Z" fill="#18A860" stroke="#3A1A06" stroke-width="3"/>'
)


def petal(ang, length, width, fill, cx=128, cy=140):
    return (f'<g transform="rotate({ang} {cx} {cy})"><path d="M{cx},{cy} C{cx - width},{cy - length * .4} {cx - width * .6},{cy - length * .85} {cx},{cy - length} '
            f'C{cx + width * .6},{cy - length * .85} {cx + width},{cy - length * .4} {cx},{cy}Z" fill="{fill}" stroke="#5A0434" stroke-width="4" stroke-linejoin="round"/>'
            f'<path d="M{cx},{cy - 8} L{cx},{cy - length * .85}" stroke="#fff" stroke-width="2" opacity=".5"/></g>')


lotus = (
    lgd("lot", "#FFFFFF", "#FFA8D8", "#E0208A") + lgd("lot2", "#FFE0F0", "#F070B8", "#B0105E")
    + '<ellipse cx="128" cy="158" rx="64" ry="14" fill="#18A860" stroke="#04301A" stroke-width="5"/>'
    + petal(-72, 66, 22, "url(#lot2)") + petal(72, 66, 22, "url(#lot2)")
    + petal(-48, 76, 24, "url(#lot)") + petal(48, 76, 24, "url(#lot)")
    + petal(-22, 84, 24, "url(#lot)") + petal(22, 84, 24, "url(#lot)")
    + petal(0, 90, 26, "url(#lot)")
    + '<ellipse cx="128" cy="144" rx="22" ry="8" fill="url(#gold)" stroke="#5A2A00" stroke-width="3"/>'
    + sparkle(180, 70, .8) + sparkle(76, 80, .6)
)

SYMBOLS = {
    "j": letter("J", SAFFRON, gem(204, 54, 20, "#FFC8EC", "#E0208A", "#6A0440")),
    "q": letter("Q", MAGENTA, gem(204, 54, 20, "#C0FFDC", "#18A860", "#04462A")),
    "k": letter("K", EMERALD, gem(204, 54, 20, "#FFE6A8", "#FF9A1A", "#A8400A")),
    "a": letter("A", ROYAL, star(206, 52, 20, "url(#gold)", "#5A2A00", 3, 8, .5)),
    "elephant": medallion("circle", MAGENTA, elephant),
    "monkey": medallion("hex", EMERALD, monkey),
    "peacock": medallion("shield", GOLDP, peacock),
    "tiger": medallion("square", ROYAL, tiger),
    "wild": wild(MAGENTA, palace),
    "scatter": scatter(TURQ, lotus, "BONUS"),
}
