"""Pirates: deep sea blue with gold. Reference theme for the kit."""
from symkit import P, coin, gem, letter, medallion, scatter, sparkle, star, suit, wild

PAL = P("#7FE0FF", "#1E7BD6", "#0B2E6E", "#06163A")
RED = P("#FF9A8A", "#E0303A", "#7A0A14", "#3A0408")
GREEN = P("#B4FF8A", "#2FA84A", "#0B4A1E", "#062A10")

pouch = (
    '<path d="M86,110 C70,130 62,160 70,184 C78,206 178,206 186,184 C194,160 186,130 170,110Z" fill="#8A5A2A" stroke="#2A1206" stroke-width="7" stroke-linejoin="round"/>'
    '<path d="M86,110 C70,130 62,160 70,184 C74,194 100,200 118,200 C96,186 90,150 104,112Z" fill="#B07A3A" opacity=".8"/>'
    '<path d="M92,98 C110,108 146,108 164,98 L170,112 C146,122 110,122 86,112Z" fill="#6A3A12" stroke="#2A1206" stroke-width="6" stroke-linejoin="round"/>'
    '<path d="M100,98 C104,74 152,74 156,98" fill="#B07A3A" stroke="#2A1206" stroke-width="6"/>'
    + coin(150, 84, 20) + coin(112, 80, 16) + coin(128, 158, 30)
)

pistol = (
    '<path d="M48,104 L176,92 L186,100 L190,120 L120,128 L114,136 L132,150 L126,160 L96,150 L88,198 C86,208 60,210 56,198 L70,136 L52,132Z" fill="#6A3A12" stroke="#2A1206" stroke-width="7" stroke-linejoin="round"/>'
    '<path d="M60,104 L182,94 L186,104 L62,114Z" fill="url(#silver)" stroke="#2A1206" stroke-width="5" stroke-linejoin="round"/>'
    '<rect x="176" y="90" width="22" height="26" rx="5" fill="url(#gold)" stroke="#2A1206" stroke-width="5"/>'
    '<circle cx="100" cy="122" r="10" fill="url(#gold)" stroke="#2A1206" stroke-width="4"/>'
    '<path d="M70,140 L60,196 C64,202 74,202 78,196 L86,146Z" fill="#B07A3A" opacity=".7"/>'
)

sabers = (
    '<g transform="rotate(-40 128 128)"><rect x="120" y="36" width="16" height="132" rx="6" fill="url(#silver)" stroke="#1A1A2A" stroke-width="5"/>'
    '<path d="M110,168 L146,168 C150,178 106,178 110,168Z" fill="url(#gold)" stroke="#2A1206" stroke-width="5"/>'
    '<rect x="122" y="176" width="12" height="30" rx="4" fill="#6A3A12" stroke="#2A1206" stroke-width="4"/><circle cx="128" cy="212" r="8" fill="url(#gold)" stroke="#2A1206" stroke-width="4"/></g>'
    '<g transform="rotate(40 128 128)"><rect x="120" y="36" width="16" height="132" rx="6" fill="url(#silver)" stroke="#1A1A2A" stroke-width="5"/>'
    '<path d="M110,168 L146,168 C150,178 106,178 110,168Z" fill="url(#gold)" stroke="#2A1206" stroke-width="5"/>'
    '<rect x="122" y="176" width="12" height="30" rx="4" fill="#6A3A12" stroke="#2A1206" stroke-width="4"/><circle cx="128" cy="212" r="8" fill="url(#gold)" stroke="#2A1206" stroke-width="4"/></g>'
)

cannon = (
    '<path d="M60,70 C90,60 130,52 186,80 L180,128 C130,120 96,124 70,128Z" fill="#3A3F52" stroke="#10121C" stroke-width="7" stroke-linejoin="round"/>'
    '<path d="M64,78 C100,68 140,64 182,88 L180,100 C140,82 100,84 66,92Z" fill="#7A8098" opacity=".8"/>'
    '<ellipse cx="184" cy="104" rx="14" ry="26" fill="#1A1C28" stroke="#10121C" stroke-width="6"/>'
    '<rect x="70" y="60" width="12" height="74" rx="5" fill="url(#gold)" stroke="#2A1206" stroke-width="4"/>'
    '<path d="M60,190 L196,190 L186,152 L70,152Z" fill="#8A5A2A" stroke="#2A1206" stroke-width="6" stroke-linejoin="round"/>'
    '<circle cx="88" cy="188" r="22" fill="#6A3A12" stroke="#2A1206" stroke-width="6"/><circle cx="168" cy="188" r="22" fill="#6A3A12" stroke="#2A1206" stroke-width="6"/>'
    '<circle cx="88" cy="188" r="7" fill="url(#gold)"/><circle cx="168" cy="188" r="7" fill="url(#gold)"/>'
    '<circle cx="214" cy="80" r="14" fill="#E8EEF8" opacity=".9"/><circle cx="226" cy="98" r="10" fill="#E8EEF8" opacity=".8"/>'
)

skull = (
    '<path d="M70,74 C90,40 166,40 186,74 L206,78 C200,64 182,40 128,38 C74,40 56,64 50,78Z" fill="#1A1A2A" stroke="#06060C" stroke-width="5"/>'
    '<path d="M60,82 C84,52 172,52 196,82 L188,96 L68,96Z" fill="#1A1A2A" stroke="#06060C" stroke-width="6" stroke-linejoin="round"/>'
    '<path d="M70,90 C80,82 176,82 186,90" stroke="url(#gold)" stroke-width="6" fill="none"/>'
    + star(128, 70, 12, "url(#gold)", "#7A3A00", 2)
    + '<path d="M84,100 C84,90 172,90 172,100 C178,124 170,138 160,142 L156,158 L100,158 L96,142 C86,138 78,124 84,100Z" fill="#FFF6E0" stroke="#2A1A10" stroke-width="6" stroke-linejoin="round"/>'
    '<ellipse cx="108" cy="116" rx="13" ry="12" fill="#2A1A10"/><ellipse cx="148" cy="116" rx="13" ry="12" fill="#2A1A10"/>'
    '<path d="M128,126 L120,138 L136,138Z" fill="#2A1A10"/><path d="M108,150 L108,158 M118,150 L118,158 M128,150 L128,158 M138,150 L138,158 M148,150 L148,158" stroke="#2A1A10" stroke-width="3"/>'
)

compass = (
    '<circle cx="128" cy="112" r="56" fill="#F2E3B8" stroke="#5A3A12" stroke-width="6"/>'
    '<circle cx="128" cy="112" r="46" fill="none" stroke="#B07A3A" stroke-width="3" stroke-dasharray="4 6"/>'
    '<polygon points="128,62 138,112 128,162 118,112" fill="#E0303A" stroke="#5A0A0A" stroke-width="3"/>'
    '<polygon points="78,112 128,102 178,112 128,122" fill="#2A3A6A" stroke="#0A1A3A" stroke-width="3"/>'
    '<circle cx="128" cy="112" r="8" fill="url(#gold)" stroke="#5A3A12" stroke-width="3"/>'
    + sparkle(176, 70, .8)
)

SYMBOLS = {
    "cross": suit("club", GREEN),
    "diamonds": suit("diamond", PAL),
    "hearts": suit("heart", RED),
    "peaks": suit("spade", P("#C8A8FF", "#6A3AD6", "#2A0A6E", "#14063A")),
    "bag": medallion("circle", P("#FFE7A0", "#C8862A", "#5A2A06", "#2A1206"), pouch),
    "pistol": medallion("shield", P("#FFB0A0", "#C83A2A", "#5A0A0A", "#2A0404"), pistol),
    "saber": medallion("hex", P("#C8F0FF", "#3A8AC8", "#0A2A5A", "#06142A"), sabers),
    "smoke": medallion("square", P("#D8C8FF", "#6A4AC8", "#22105A", "#100630"), cannon),
    "wild": wild(PAL, skull),
    "scatter": scatter(P("#9AF0FF", "#1AA6C8", "#0A3A5A", "#06202E"), compass, "BONUS"),
}
