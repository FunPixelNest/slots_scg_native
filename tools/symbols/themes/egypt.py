"""Egypt: lapis blue and gold."""
from symkit import P, gem, lg, letter, medallion, rg, scatter, sparkle, star, wild

INK = "#0A0A24"
LAPIS = P("#A8C4FF", "#2A4AC8", "#0C1A6A", "#060C34")
GOLD = P("#FFF6C0", "#FFC21A", "#B05A00", "#3A1A04")
TURQ = P("#C0FFF4", "#20C0B4", "#086058", "#032E2A")
CARN = P("#FFC0A0", "#E0502A", "#7A1A08", "#360A02")
SUNSET = P("#FFE8A8", "#FF9A3A", "#B03A10", "#3A1004")


def ankh_shape(cx, cy, s, fill, inlay=None, sw=None):
    """Ankh centred at (cx,cy), [s] = scale (1.0 = ~150 tall)."""
    sw = sw or 16
    g = (f'<ellipse cx="0" cy="-46" rx="24" ry="30" fill="none" stroke="{{C}}" stroke-width="{{W}}"/>'
         f'<rect x="-58" y="-14" width="116" height="24" rx="6" fill="{{C}}" stroke="{{C}}" stroke-width="{{V}}" stroke-linejoin="round"/>'
         f'<path d="M-12,8 L12,8 L22,74 L-22,74Z" fill="{{C}}" stroke="{{C}}" stroke-width="{{V}}" stroke-linejoin="round"/>')
    out = g.replace("{C}", INK).replace("{W}", str(sw + 14)).replace("{V}", "14")
    body = g.replace("{C}", fill).replace("{W}", str(sw)).replace("{V}", "0")
    inl = ""
    if inlay:
        inl = (f'<ellipse cx="0" cy="-46" rx="24" ry="30" fill="none" stroke="{inlay}" stroke-width="4"/>'
               f'<rect x="-48" y="-5" width="96" height="6" rx="3" fill="{inlay}"/><path d="M-3,14 L3,14 L8,66 L-8,66Z" fill="{inlay}"/>')
    hl = '<path d="M-20,-60 C-18,-70 -10,-76 -2,-77" fill="none" stroke="#fff" stroke-width="4" stroke-linecap="round" opacity=".8"/>'
    return f'<g transform="translate({cx},{cy}) scale({s})">{out}{body}{inl}{hl}</g>'


def eye_of_horus(cx, cy, s, line="url(#gold)", iris="#0C1A6A"):
    g = ('<path d="M-66,-42 C-36,-62 34,-64 70,-50 L68,-36 C34,-48 -30,-46 -64,-28Z" fill="{L}" stroke="{L}" stroke-width="{W}" stroke-linejoin="round"/>'
         '<path d="M-64,-4 C-40,-32 24,-36 64,-10 L84,-6" fill="none" stroke="{L}" stroke-width="{W}" stroke-linecap="round" stroke-linejoin="round"/>'
         '<path d="M-64,-4 C-32,18 30,16 64,-10" fill="none" stroke="{L}" stroke-width="{W}" stroke-linecap="round"/>'
         '<path d="M-22,16 L-26,62" fill="none" stroke="{L}" stroke-width="{W2}" stroke-linecap="round"/>'
         '<path d="M4,16 C12,44 32,60 48,50 C60,42 52,26 40,32 C32,36 36,46 42,44" fill="none" stroke="{L}" stroke-width="{W}" stroke-linecap="round" stroke-linejoin="round"/>')
    eye_fill = '<path d="M-64,-4 C-40,-32 24,-36 64,-10 C30,16 -32,18 -64,-4Z" fill="#FFF8E8"/>'
    iris_ = (f'<circle cx="-2" cy="-8" r="17" fill="{iris}" stroke="{INK}" stroke-width="3"/>'
             '<circle cx="-2" cy="-8" r="8" fill="#000"/><circle cx="-8" cy="-14" r="4.5" fill="#fff"/>')
    out = g.replace("{L}", INK).replace("{W2}", "20").replace("{W}", "16")
    gold = g.replace("{L}", line).replace("{W2}", "10").replace("{W}", "7")
    return f'<g transform="translate({cx},{cy}) scale({s})">{eye_fill}{out}{gold}{iris_}</g>'


# ------------------------------------------------------------------ ankh
ankh = ankh_shape(128, 132, 1.0, "url(#gold)", "#1AA0C8") + sparkle(180, 64, .8) + sparkle(72, 176, .55)

# ------------------------------------------------------------------ eye of Horus
eye = eye_of_horus(124, 124, 1.08) + sparkle(186, 70, .7)

# ------------------------------------------------------------------ lotus
PET = "#FFF8FF"
lotus = (
    '<defs>' + lg("pet", [(0, "#FFFFFF"), (.5, "#FFC8E4"), (1, "#E85AA0")])
    + lg("petB", [(0, "#E0F0FF"), (.6, "#7AB0FF"), (1, "#2A4AC8")])
    + lg("sep", [(0, "#9AF08A"), (1, "#1E7A3A")]) + '</defs>'
    # stem and water
    '<path d="M128,166 C126,182 130,194 128,206" fill="none" stroke="' + INK + '" stroke-width="14" stroke-linecap="round"/>'
    '<path d="M128,166 C126,182 130,194 128,206" fill="none" stroke="#2E9A4A" stroke-width="7" stroke-linecap="round"/>'
    '<path d="M70,196 C86,188 98,204 114,196 M142,196 C158,188 170,204 186,196" fill="none" stroke="#A8E0FF" stroke-width="5" stroke-linecap="round"/>'
    # outer petals
    '<path d="M128,158 C90,164 60,146 48,116 C80,114 110,130 128,158Z" fill="url(#petB)" stroke="' + INK + '" stroke-width="6" stroke-linejoin="round"/>'
    '<path d="M128,158 C166,164 196,146 208,116 C176,114 146,130 128,158Z" fill="url(#petB)" stroke="' + INK + '" stroke-width="6" stroke-linejoin="round"/>'
    # side petals
    '<path d="M128,156 C94,146 76,112 82,70 C106,90 124,120 128,156Z" fill="url(#pet)" stroke="' + INK + '" stroke-width="6" stroke-linejoin="round"/>'
    '<path d="M128,156 C162,146 180,112 174,70 C150,90 132,120 128,156Z" fill="url(#pet)" stroke="' + INK + '" stroke-width="6" stroke-linejoin="round"/>'
    # centre petal
    '<path d="M128,46 C154,80 156,126 128,156 C100,126 102,80 128,46Z" fill="url(#pet)" stroke="' + INK + '" stroke-width="6" stroke-linejoin="round"/>'
    '<path d="M128,62 C140,84 142,118 128,144 M92,90 C102,108 114,126 122,146 M164,90 C154,108 142,126 134,146" fill="none" stroke="#E85AA0" stroke-width="2.5" opacity=".6"/>'
    '<path d="M118,74 C114,86 112,98 112,110" fill="none" stroke="#fff" stroke-width="4" stroke-linecap="round" opacity=".9"/>'
    # sepal cup
    '<path d="M90,148 C98,174 158,174 166,148 C150,160 106,160 90,148Z" fill="url(#sep)" stroke="' + INK + '" stroke-width="6" stroke-linejoin="round"/>'
    '<path d="M100,160 L156,160" stroke="url(#gold)" stroke-width="4"/>'
    + sparkle(190, 74, .7) + sparkle(66, 80, .55)
)

# ------------------------------------------------------------------ shen ring
shen = (
    '<defs>' + rg("sun", [(0, "#FFE8A0"), (.5, "#FF6A2A"), (1, "#B01A08")], cx=".4", cy=".35", r=".7") + '</defs>'
    '<circle cx="128" cy="106" r="40" fill="url(#sun)" stroke="' + INK + '" stroke-width="5"/>'
    '<ellipse cx="114" cy="92" rx="12" ry="7" fill="#fff" opacity=".6" transform="rotate(-30 114 92)"/>'
    '<path d="M100,158 C76,146 66,128 66,106 C66,72 94,46 128,46 C162,46 190,72 190,106 C190,128 180,146 156,158" fill="none" stroke="' + INK + '" stroke-width="28" stroke-linecap="round"/>'
    '<path d="M100,158 C76,146 66,128 66,106 C66,72 94,46 128,46 C162,46 190,72 190,106 C190,128 180,146 156,158" fill="none" stroke="url(#gold)" stroke-width="16" stroke-linecap="round"/>'
    '<path d="M100,158 C76,146 66,128 66,106 C66,72 94,46 128,46 C162,46 190,72 190,106 C190,128 180,146 156,158" fill="none" stroke="#B86200" stroke-width="16" stroke-dasharray="3 9" opacity=".7"/>'
    # tie
    '<path d="M100,150 L100,176 M156,150 L156,176" stroke="' + INK + '" stroke-width="22" stroke-linecap="round"/>'
    '<path d="M100,150 L100,176 M156,150 L156,176" stroke="url(#gold)" stroke-width="10" stroke-linecap="round"/>'
    # base bar
    '<rect x="66" y="170" width="124" height="24" rx="6" fill="url(#gold)" stroke="' + INK + '" stroke-width="7"/>'
    '<rect x="74" y="176" width="108" height="5" rx="2" fill="#1AA0C8"/>'
    '<path d="M78,98 C80,80 92,64 108,56" fill="none" stroke="#fff" stroke-width="4" stroke-linecap="round" opacity=".8"/>'
    + sparkle(192, 60, .7)
)

# ------------------------------------------------------------------ wild: pharaoh mask
nemes = "M128,30 C88,30 70,54 68,86 L56,156 C70,166 92,168 104,160 L104,124 L152,124 L152,160 C164,168 186,166 200,156 L188,86 C186,54 168,30 128,30Z"
pharaoh = (
    '<defs><clipPath id="nemes"><path d="' + nemes + '"/></clipPath>'
    + rg("face", [(0, "#FFF4C0"), (.55, "#FFC83A"), (1, "#B86A00")], cx=".4", cy=".35", r=".75") + '</defs>'
    f'<path d="{nemes}" fill="url(#gold)" stroke="{INK}" stroke-width="7" stroke-linejoin="round"/>'
    '<g clip-path="url(#nemes)">'
    + "".join(f'<rect x="40" y="{y}" width="176" height="9" fill="#1E3AB0"/>' for y in range(44, 170, 18))
    + '</g>'
    f'<path d="{nemes}" fill="url(#shine)" opacity=".35"/>'
    # face
    '<path d="M98,74 C98,64 158,64 158,74 L158,118 C158,140 144,154 128,154 C112,154 98,140 98,118Z" fill="url(#face)" stroke="' + INK + '" stroke-width="6" stroke-linejoin="round"/>'
    '<rect x="94" y="62" width="68" height="14" rx="4" fill="url(#gold)" stroke="' + INK + '" stroke-width="5"/>'
    # eyes with kohl
    '<path d="M104,96 C110,88 120,88 124,96 C118,100 110,100 104,96Z M132,96 C136,88 146,88 152,96 C146,100 138,100 132,96Z" fill="#fff" stroke="' + INK + '" stroke-width="4" stroke-linejoin="round"/>'
    '<circle cx="114" cy="95" r="3.5" fill="' + INK + '"/><circle cx="142" cy="95" r="3.5" fill="' + INK + '"/>'
    '<path d="M124,96 L130,100 M152,96 L158,100 M102,86 C110,82 118,82 124,86 M132,86 C138,82 146,82 154,86" stroke="' + INK + '" stroke-width="4" stroke-linecap="round"/>'
    '<path d="M128,100 L124,120 L132,120" fill="none" stroke="#B86A00" stroke-width="3" stroke-linejoin="round"/>'
    '<path d="M118,132 C124,136 132,136 138,132" fill="none" stroke="#8A3A00" stroke-width="4" stroke-linecap="round"/>'
    # beard
    '<path d="M120,152 L136,152 L134,170 L122,170Z" fill="url(#gold)" stroke="' + INK + '" stroke-width="5" stroke-linejoin="round"/>'
    # uraeus
    '<path d="M128,62 C120,54 120,42 128,38 C136,42 136,54 128,62Z" fill="url(#gold)" stroke="' + INK + '" stroke-width="4"/>'
    + gem(128, 48, 5, "#FFB0A0", "#E0302A", "#7A0A08", INK)
)

# ------------------------------------------------------------------ scatter: pyramids at sunset
pyramids = (
    '<defs><clipPath id="sky"><circle cx="128" cy="112" r="76"/></clipPath>'
    + lg("sand", [(0, "#FFE08A"), (1, "#C8862A")]) + '</defs>'
    '<g clip-path="url(#sky)">'
    '<circle cx="158" cy="72" r="24" fill="#FFF4C0" stroke="#E86A00" stroke-width="4"/>'
    '<circle cx="158" cy="72" r="34" fill="#FFF4C0" opacity=".3"/>'
    # small pyramid
    '<polygon points="164,104 206,168 122,168" fill="#E8A040" stroke="' + INK + '" stroke-width="5" stroke-linejoin="round"/>'
    '<polygon points="164,104 206,168 170,168" fill="#A8601A"/>'
    # big pyramid
    '<polygon points="104,60 172,168 36,168" fill="#FFD27A" stroke="' + INK + '" stroke-width="6" stroke-linejoin="round"/>'
    '<polygon points="104,60 172,168 116,168" fill="#C07A2A"/>'
    '<path d="M78,128 L130,128 M66,148 L148,148 M90,108 L118,108" stroke="#A8601A" stroke-width="3" opacity=".6"/>'
    '<polygon points="104,60 114,76 94,76" fill="url(#gold)" stroke="' + INK + '" stroke-width="3" stroke-linejoin="round"/>'
    # dunes
    '<path d="M30,170 C70,150 110,170 150,160 C180,152 200,160 230,156 L230,200 L30,200Z" fill="url(#sand)" stroke="' + INK + '" stroke-width="5"/>'
    '</g>'
    + sparkle(86, 64, .7)
)

SYMBOLS = {
    "j": letter("J", LAPIS, ankh_shape(198, 54, .26, "url(#gold)", sw=16)),
    "q": letter("Q", GOLD, gem(198, 52, 18, "#A8C4FF", "#2A4AC8", "#0C1A6A", INK)),
    "k": letter("K", TURQ, eye_of_horus(196, 54, .3)),
    "a": letter("A", CARN, star(198, 52, 20, "url(#gold)", "#7A3A00", 4)),
    "ankh": medallion("square", LAPIS, ankh),
    "eye": medallion("circle", LAPIS, eye),
    "lotus": medallion("hex", TURQ, lotus),
    "shen": medallion("shield", CARN, shen),
    "wild": wild(LAPIS, pharaoh),
    "scatter": scatter(SUNSET, pyramids, "BONUS"),
}
