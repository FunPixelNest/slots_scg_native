"""Arabian nights: desert gold, turquoise and ruby."""
from symkit import P, gem, lg, rg, medallion, scatter, sparkle, star, suit, wild

INK = "#2A1206"
TURQ = P("#B8FFF4", "#18B8B0", "#065A5E", "#032A2E")
RUBY = P("#FFA8B0", "#D81E3C", "#6A0618", "#340208")
SAND = P("#FFF0B0", "#E8A63A", "#7A4208", "#3A1C04")
PLUM = P("#E8B8FF", "#8A3AC8", "#36105E", "#1A0630")
NIGHT = P("#9AB8FF", "#2A4AB8", "#0C1A5A", "#060C2E")

# ------------------------------------------------------------------ dagger (curved jambiya)
dagger = (
    '<defs>' + lg("blade", [(0, "#FFFFFF"), (.5, "#C9D3E6"), (1, "#6F7A96")], "1", "0") + '</defs>'
    '<g transform="translate(-12,6) rotate(18 128 128)">'
    # blade
    '<path d="M110,134 C102,102 108,74 132,54 C148,42 172,40 186,46 C164,54 152,72 148,96 C146,114 146,126 146,134Z" '
    'fill="url(#blade)" stroke="#141826" stroke-width="6" stroke-linejoin="round"/>'
    '<path d="M126,130 C122,104 126,82 144,64 C152,58 160,54 168,52 C154,64 140,84 136,130Z" fill="#8A96B2" opacity=".75"/>'
    '<path d="M116,124 C112,100 116,80 132,64" stroke="#fff" stroke-width="4" fill="none" stroke-linecap="round" opacity=".9"/>'
    # guard
    '<path d="M88,132 C100,126 156,126 168,132 C172,140 166,148 156,146 L100,146 C90,148 84,140 88,132Z" '
    'fill="url(#gold)" stroke="' + INK + '" stroke-width="6" stroke-linejoin="round"/>'
    # grip
    '<path d="M112,146 L144,146 L140,192 L116,192Z" fill="#7A1020" stroke="' + INK + '" stroke-width="6" stroke-linejoin="round"/>'
    '<path d="M116,160 L140,156 M115,174 L141,170" stroke="url(#gold)" stroke-width="5"/>'
    # pommel
    '<path d="M100,190 C106,186 150,186 156,190 C164,200 156,214 146,214 L110,214 C100,214 92,200 100,190Z" '
    'fill="url(#gold)" stroke="' + INK + '" stroke-width="6" stroke-linejoin="round"/>'
    + gem(128, 139, 11, "#FFB0C0", "#E0204A", "#7A0420", INK)
    + gem(128, 201, 10, "#B8FFF4", "#18B8B0", "#065A5E", INK)
    + '</g>' + sparkle(80, 72, .9)
)

# ------------------------------------------------------------------ ring
ring = (
    # band: outline, gold, inner shadow, highlight
    '<ellipse cx="128" cy="150" rx="62" ry="46" fill="none" stroke="' + INK + '" stroke-width="32"/>'
    '<ellipse cx="128" cy="150" rx="62" ry="46" fill="none" stroke="url(#gold)" stroke-width="20"/>'
    '<path d="M74,156 C80,186 176,186 182,156" fill="none" stroke="#A85A00" stroke-width="6" opacity=".7"/>'
    '<path d="M76,136 C88,116 108,108 120,108" fill="none" stroke="#fff" stroke-width="5" stroke-linecap="round" opacity=".85"/>'
    # setting
    '<path d="M92,108 L164,108 L156,124 L100,124Z" fill="url(#gold)" stroke="' + INK + '" stroke-width="6" stroke-linejoin="round"/>'
    '<path d="M96,108 L108,80 M160,108 L148,80 M128,112 L128,74" stroke="' + INK + '" stroke-width="9" stroke-linecap="round"/>'
    '<path d="M96,108 L108,80 M160,108 L148,80" stroke="url(#gold)" stroke-width="4" stroke-linecap="round"/>'
    + gem(128, 86, 34, "#FFB8C4", "#E0204A", "#7A0420", INK)
    + sparkle(170, 64, 1.0) + sparkle(84, 186, .6)
)

# ------------------------------------------------------------------ slippers
SHOE = (
    '<path d="M54,178 L54,152 C54,132 70,124 88,130 L106,136 C128,144 168,150 188,130 C196,122 200,110 196,100 '
    'C212,108 214,134 200,154 C186,172 158,184 128,186 L64,186 C58,186 54,184 54,178Z" fill="{F}" stroke="' + INK + '" stroke-width="7" stroke-linejoin="round"/>'
    '<path d="M196,100 C190,88 200,78 208,84 C214,90 206,98 200,94" fill="none" stroke="' + INK + '" stroke-width="10" stroke-linecap="round"/>'
    '<path d="M196,100 C190,88 200,78 208,84 C214,90 206,98 200,94" fill="none" stroke="url(#gold)" stroke-width="5" stroke-linecap="round"/>'
    '<path d="M60,140 C68,124 96,124 110,140 C96,150 74,150 60,140Z" fill="#2A0610" stroke="' + INK + '" stroke-width="5" stroke-linejoin="round"/>'
    '<path d="M56,176 L128,176 C160,174 188,162 202,146" fill="none" stroke="url(#gold)" stroke-width="7" stroke-linecap="round"/>'
    '<path d="M112,140 C130,150 160,152 180,140" fill="none" stroke="url(#gold)" stroke-width="5" stroke-linecap="round" stroke-dasharray="2 9"/>'
    '<path d="M70,160 C90,154 120,156 140,160" fill="none" stroke="#fff" stroke-width="4" stroke-linecap="round" opacity=".45"/>'
)
shoes = (
    '<defs>' + lg("shoeA", [(0, "#FF8A9A"), (.6, "#D01E3C"), (1, "#6A0618")])
    + lg("shoeB", [(0, "#7AF0E4"), (.6, "#129A94"), (1, "#05484C")]) + '</defs>'
    '<g transform="translate(-6,-34) scale(.94)">' + SHOE.replace("{F}", "url(#shoeB)") + '</g>'
    '<g transform="translate(6,8)">' + SHOE.replace("{F}", "url(#shoeA)")
    + gem(150, 160, 9, "#FFF6B0", "#FFC21A", "#A86A00", INK) + '</g>'
)

# ------------------------------------------------------------------ tombak (goblet drum)
tombak = (
    '<defs>' + lg("wood", [(0, "#5A2A0A"), (.3, "#C07A3A"), (.55, "#E8A860"), (1, "#5A2A0A")], "1", "0") + '</defs>'
    '<path d="M66,74 C66,122 106,132 112,150 L112,168 C112,180 90,190 86,204 L170,204 C166,190 144,180 144,168 '
    'L144,150 C150,132 190,122 190,74Z" fill="url(#wood)" stroke="' + INK + '" stroke-width="7" stroke-linejoin="round"/>'
    # inlay band
    '<path d="M72,100 C90,108 166,108 184,100 L176,116 C150,124 106,124 80,116Z" fill="url(#gold)" stroke="' + INK + '" stroke-width="4" stroke-linejoin="round"/>'
    '<path d="M92,112 L100,104 L108,114 L116,106 L124,116 L132,106 L140,116 L148,106 L156,114 L164,104" fill="none" stroke="#0A6A6E" stroke-width="4" stroke-linejoin="round"/>'
    '<path d="M112,158 L144,158" stroke="url(#gold)" stroke-width="8"/>'
    '<path d="M112,158 L144,158" stroke="' + INK + '" stroke-width="2" opacity=".5"/>'
    + gem(128, 136, 10, "#B8FFF4", "#18B8B0", "#065A5E", INK)
    # bottom opening
    + '<ellipse cx="128" cy="204" rx="42" ry="9" fill="#2A1206" stroke="' + INK + '" stroke-width="5"/>'
    '<ellipse cx="128" cy="204" rx="42" ry="9" fill="none" stroke="url(#gold)" stroke-width="3"/>'
    # drum head
    '<ellipse cx="128" cy="74" rx="64" ry="18" fill="#F6E6C0" stroke="' + INK + '" stroke-width="7"/>'
    '<ellipse cx="128" cy="74" rx="56" ry="13" fill="none" stroke="#D8B880" stroke-width="3"/>'
    '<ellipse cx="112" cy="70" rx="22" ry="5" fill="#fff" opacity=".8"/>'
    '<path d="M80,138 C92,150 104,150 108,152" stroke="#fff" stroke-width="4" fill="none" opacity=".35"/>'
)

# ------------------------------------------------------------------ wild: magic lamp
lamp = (
    '<path d="M66,122 C40,104 34,140 66,138" fill="none" stroke="' + INK + '" stroke-width="14" stroke-linecap="round"/>'
    '<path d="M66,122 C40,104 34,140 66,138" fill="none" stroke="url(#gold)" stroke-width="7" stroke-linecap="round"/>'
    '<path d="M100,148 L156,148 L166,166 L90,166Z" fill="url(#gold)" stroke="' + INK + '" stroke-width="6" stroke-linejoin="round"/>'
    '<path d="M62,122 C68,100 108,94 128,96 C150,94 174,100 184,114 L214,88 C222,82 230,90 224,98 L190,132 '
    'C178,146 152,152 128,152 C100,152 72,144 62,122Z" fill="url(#gold)" stroke="' + INK + '" stroke-width="7" stroke-linejoin="round"/>'
    '<path d="M70,124 C90,138 150,142 182,128" fill="none" stroke="#A85A00" stroke-width="5" opacity=".7"/>'
    '<path d="M78,110 C92,102 112,100 124,102" fill="none" stroke="#fff" stroke-width="5" stroke-linecap="round" opacity=".9"/>'
    '<path d="M98,98 C98,74 152,74 152,98Z" fill="url(#gold)" stroke="' + INK + '" stroke-width="6" stroke-linejoin="round"/>'
    '<circle cx="125" cy="72" r="9" fill="url(#gold)" stroke="' + INK + '" stroke-width="5"/>'
    + gem(124, 126, 11, "#FFB0C0", "#E0204A", "#7A0420", INK)
    # magic smoke
    + '<path d="M222,84 C214,70 226,58 216,46 C208,36 190,42 196,54" fill="none" stroke="#E8D0FF" stroke-width="7" stroke-linecap="round" opacity=".9"/>'
    + sparkle(184, 48, .9) + sparkle(74, 70, .7)
)

# ------------------------------------------------------------------ scatter: palace under the crescent
palace = (
    '<defs>' + lg("dome", [(0, "#FFF4C0"), (.5, "#FFC83A"), (1, "#B86A00")], "1", "0") + '</defs>'
    # crescent moon
    '<g transform="translate(161,63) scale(.8) translate(-169,-73)"><path d="M172,46 C150,50 142,78 158,94 C170,104 188,100 196,92 C176,94 162,78 168,62 C170,54 172,50 172,46Z" fill="url(#gold)" stroke="' + INK + '" stroke-width="4" stroke-linejoin="round"/></g>'
    + star(96, 58, 7, "#FFF6B0", INK, 2) + sparkle(190, 130, .5)
    # minarets
    + '<rect x="66" y="114" width="20" height="54" fill="#F4E4C4" stroke="' + INK + '" stroke-width="5"/>'
    '<path d="M62,116 C62,102 76,92 76,86 C76,92 90,102 90,116Z" fill="url(#dome)" stroke="' + INK + '" stroke-width="5" stroke-linejoin="round"/>'
    '<rect x="170" y="114" width="20" height="54" fill="#F4E4C4" stroke="' + INK + '" stroke-width="5"/>'
    '<path d="M166,116 C166,102 180,92 180,86 C180,92 194,102 194,116Z" fill="url(#dome)" stroke="' + INK + '" stroke-width="5" stroke-linejoin="round"/>'
    # main hall
    '<rect x="90" y="122" width="76" height="46" fill="#FFF0D4" stroke="' + INK + '" stroke-width="5"/>'
    '<path d="M116,168 L116,148 C116,138 140,138 140,148 L140,168Z" fill="#0C2A6A" stroke="' + INK + '" stroke-width="4"/>'
    '<path d="M98,140 C98,132 108,132 108,140 L108,154 L98,154Z M148,140 C148,132 158,132 158,140 L158,154 L148,154Z" fill="#0C2A6A"/>'
    # onion dome
    '<path d="M92,124 C84,104 112,92 122,78 C126,72 128,66 128,58 C128,66 130,72 134,78 C144,92 172,104 164,124Z" fill="url(#dome)" stroke="' + INK + '" stroke-width="6" stroke-linejoin="round"/>'
    '<path d="M104,116 C100,104 112,96 120,90" fill="none" stroke="#fff" stroke-width="4" stroke-linecap="round" opacity=".8"/>'
    '<circle cx="128" cy="54" r="5" fill="url(#gold)" stroke="' + INK + '" stroke-width="3"/>'
    '<rect x="60" y="164" width="136" height="8" rx="3" fill="url(#gold)" stroke="' + INK + '" stroke-width="4"/>'
)

SYMBOLS = {
    "crosses": suit("club", TURQ),
    "diamonds": suit("diamond", SAND),
    "hearts": suit("heart", RUBY),
    "spades": suit("spade", PLUM),
    "dagger": medallion("shield", RUBY, dagger),
    "ring": medallion("circle", TURQ, ring),
    "shoes": medallion("hex", PLUM, shoes),
    "tombak": medallion("square", P("#9AF0E0", "#14968E", "#05403E", "#021E1E"), tombak),
    "wild": wild(PLUM, lamp),
    "scatter": scatter(NIGHT, palace, "BONUS"),
}
