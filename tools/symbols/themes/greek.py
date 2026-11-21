"""Greek: white marble, Aegean blue and gold laurels."""
import math

from symkit import P, medallion, outlined_text, scatter, sparkle, star, suit, wild

INK = "#0A1A3A"

MARBLE = P("#FFFFFF", "#E2E6EE", "#9AA2B6", "#1E2A44")
AEGEAN = P("#BFE6FF", "#2A7AD8", "#0A2A6E", "#061636")
TERRA = P("#FFC8A8", "#D8582E", "#7A1E0A", "#2E0A02")
OLIVE = P("#E8F4B0", "#8AAA30", "#33500C", "#142204")
DEEP = P("#9AD0FF", "#1A5AB8", "#081E52", "#040E2A")

GOLDU = ('<defs><linearGradient id="gL" gradientUnits="userSpaceOnUse" x1="0" y1="60" x2="0" y2="196">'
         '<stop offset="0" stop-color="#FFF7C4"/><stop offset=".4" stop-color="#FFD54A"/><stop offset=".75" stop-color="#D88400"/><stop offset="1" stop-color="#FFE27A"/></linearGradient></defs>')


def marble_bg(id_, c1="#FFFFFF", c2="#E4E7EF", c3="#AEB5C6", vein="#8A92A8"):
    return (f'<defs><radialGradient id="{id_}G" cx=".4" cy=".35" r=".75"><stop offset="0" stop-color="{c1}"/><stop offset=".6" stop-color="{c2}"/><stop offset="1" stop-color="{c3}"/></radialGradient>'
            f'<pattern id="{id_}" patternUnits="userSpaceOnUse" width="256" height="256"><rect width="256" height="256" fill="url(#{id_}G)"/>'
            f'<path d="M20,70 C60,60 80,90 120,84 C150,80 160,50 210,44 M40,190 C80,170 110,196 150,180 C180,168 196,186 240,170 M150,84 C160,110 190,120 230,116 M70,120 C90,136 84,160 100,176" stroke="{vein}" stroke-width="2" fill="none" opacity=".45"/>'
            f'<path d="M30,110 C60,104 70,122 96,120 M170,210 C190,200 210,214 236,204" stroke="{vein}" stroke-width="1.2" fill="none" opacity=".35"/>'
            f'</pattern></defs>')


def laurel(cx=128, cy=126, r=80, n=6, color="url(#gL)", ink="#5A2A00"):
    stems, leaves = [], []
    for side in (-1, 1):
        pts = []
        for i in range(n):
            t = math.radians(18 + i * 21)
            x, y = cx + side * r * math.sin(t), cy + r * math.cos(t)
            pts.append((x, y))
            dx, dy = side * math.cos(t), -math.sin(t)
            nx, ny = side * math.sin(t), math.cos(t)
            phi = math.degrees(math.atan2(dx, -dy))
            lr = 15 - i * .7
            for k in (1, -1):
                lx, ly = x + nx * 8 * k + dx * 6, y + ny * 8 * k + dy * 6
                leaves.append(f'<ellipse cx="{lx:.1f}" cy="{ly:.1f}" rx="{lr * .42:.1f}" ry="{lr:.1f}" transform="rotate({phi + side * 32 * k:.1f} {lx:.1f} {ly:.1f})" fill="{color}" stroke="{ink}" stroke-width="3"/>')
        tx, ty = pts[-1]
        t = math.radians(18 + n * 21)
        leaves.append(f'<ellipse cx="{cx + side * r * math.sin(t):.1f}" cy="{cy + r * math.cos(t):.1f}" rx="5.5" ry="11" transform="rotate({math.degrees(math.atan2(side * math.cos(t), math.sin(t))):.1f} {cx + side * r * math.sin(t):.1f} {cy + r * math.cos(t):.1f})" fill="{color}" stroke="{ink}" stroke-width="3"/>')
        d = "M" + " L".join(f"{x:.1f},{y:.1f}" for x, y in pts)
        stems.append(f'<path d="{d}" stroke="{ink}" stroke-width="7" fill="none" stroke-linecap="round" stroke-linejoin="round"/><path d="{d}" stroke="#E8A82A" stroke-width="3" fill="none" stroke-linecap="round" stroke-linejoin="round"/>')
    tie = (f'<path d="M{cx},{cy + r} L{cx - 16},{cy + r + 14} L{cx - 6},{cy + r + 14}Z M{cx},{cy + r} L{cx + 16},{cy + r + 14} L{cx + 6},{cy + r + 14}Z" fill="#2A7AD8" stroke="{ink}" stroke-width="3" stroke-linejoin="round"/>'
           f'<circle cx="{cx}" cy="{cy + r}" r="7" fill="{color}" stroke="{ink}" stroke-width="3"/>')
    return "".join(stems + leaves) + tie


def glyph_path(d, ink="#3A1A00", w=24):
    return (f'<path d="{d}" fill="none" stroke="{ink}" stroke-width="{w + 14}" stroke-linecap="round" stroke-linejoin="round"/>'
            f'<path d="{d}" fill="none" stroke="url(#gL)" stroke-width="{w}" stroke-linecap="round" stroke-linejoin="round"/>'
            f'<path d="{d}" fill="none" stroke="#FFFFFF" stroke-width="{w * .25:.1f}" stroke-linecap="round" stroke-linejoin="round" opacity=".45" transform="translate(-3 -4)"/>')


def letter_icon(glyph):
    return GOLDU + laurel() + glyph


alpha = letter_icon(outlined_text("A", 128, 170, 112, "url(#gL)", "#3A1A00", 16))
beta = letter_icon(outlined_text("B", 128, 170, 112, "url(#gL)", "#3A1A00", 16))
psi = letter_icon(glyph_path("M128,78 L128,180 M90,84 C90,136 102,146 128,146 C154,146 166,136 166,84 M108,182 L148,182", w=20))
sigma = letter_icon(glyph_path("M160,82 L96,82 L132,130 L96,178 L162,178", w=22))

helmet = (
    '<defs><linearGradient id="crest" x1="0" y1="0" x2="1" y2="1"><stop offset="0" stop-color="#FF8A6A"/><stop offset=".5" stop-color="#E0202A"/><stop offset="1" stop-color="#7A0410"/></linearGradient>'
    '<radialGradient id="helm" cx=".4" cy=".3" r=".8"><stop offset="0" stop-color="#FFF7C4"/><stop offset=".45" stop-color="#FFC83A"/><stop offset="1" stop-color="#A85A00"/></radialGradient></defs>'
    # crest
    '<path d="M58,112 C44,62 86,26 138,26 C180,26 206,48 210,80 C188,58 160,52 134,60 C102,60 80,80 74,112Z" fill="url(#crest)" stroke="#2A0404" stroke-width="7" stroke-linejoin="round"/>'
    '<path d="M70,96 C70,70 90,50 116,42 M86,86 C92,68 108,56 128,50 M140,40 C164,40 186,50 198,64 M102,74 C110,64 124,58 138,56" stroke="#7A0410" stroke-width="3" fill="none" stroke-linecap="round"/>'
    '<path d="M72,64 C88,44 112,34 138,34" stroke="#fff" stroke-width="5" fill="none" opacity=".45" stroke-linecap="round"/>'
    # shell
    '<path d="M64,160 C72,144 74,130 72,114 C72,82 96,60 130,60 C166,60 192,84 192,118 L192,150 C192,164 184,172 172,176 L156,176 C150,162 140,152 128,150 C112,148 96,154 86,166Z" fill="url(#helm)" stroke="#3A1A00" stroke-width="7" stroke-linejoin="round"/>'
    '<path d="M74,120 C96,104 152,100 192,114" stroke="#A85A00" stroke-width="7" fill="none"/>'
    '<path d="M74,120 C96,104 152,100 192,114" stroke="#FFF0A0" stroke-width="2.5" fill="none"/>'
    '<path d="M150,120 C162,112 182,112 192,118 L192,128 C180,130 166,132 154,134Z" fill="#1A0E04"/>'
    '<path d="M192,128 L180,132 L172,176 L184,174 C190,168 192,160 192,150Z" fill="#1A0E04"/>'
    '<path d="M86,96 C96,78 112,70 128,68" stroke="#fff" stroke-width="7" fill="none" opacity=".55" stroke-linecap="round"/>'
    '<circle cx="104" cy="138" r="5" fill="#A85A00"/><circle cx="120" cy="134" r="5" fill="#A85A00"/><circle cx="136" cy="134" r="5" fill="#A85A00"/>'
)

temple = (
    '<defs><linearGradient id="mrb" x1="0" y1="0" x2="1" y2="0"><stop offset="0" stop-color="#C8CEDC"/><stop offset=".35" stop-color="#FFFFFF"/><stop offset="1" stop-color="#9AA2B6"/></linearGradient>'
    '<linearGradient id="mrbV" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#FFFFFF"/><stop offset="1" stop-color="#B8C0D0"/></linearGradient></defs>'
    '<path d="M62,92 L128,52 L194,92Z" fill="url(#mrbV)" stroke="#1E2A44" stroke-width="6" stroke-linejoin="round"/>'
    '<path d="M86,86 L128,62 L170,86Z" fill="#2A7AD8" stroke="#1E2A44" stroke-width="3" stroke-linejoin="round"/>'
    '<circle cx="128" cy="76" r="6" fill="url(#gold)" stroke="#5A2A00" stroke-width="2"/>'
    '<rect x="62" y="92" width="132" height="16" fill="url(#mrbV)" stroke="#1E2A44" stroke-width="5"/>'
    '<path d="M68,100 L188,100" stroke="url(#gold)" stroke-width="4"/>'
    + "".join(f'<rect x="{x}" y="108" width="18" height="50" fill="url(#mrb)" stroke="#1E2A44" stroke-width="4"/>'
              f'<path d="M{x + 6},112 L{x + 6},154 M{x + 12},112 L{x + 12},154" stroke="#9AA2B6" stroke-width="1.5"/>'
              f'<rect x="{x - 3}" y="106" width="24" height="7" rx="2" fill="url(#mrbV)" stroke="#1E2A44" stroke-width="3"/>' for x in (72, 100, 138, 166))
    + '<rect x="58" y="156" width="140" height="10" fill="url(#mrbV)" stroke="#1E2A44" stroke-width="4"/>'
    '<rect x="50" y="164" width="156" height="10" fill="url(#mrbV)" stroke="#1E2A44" stroke-width="4"/>'
    + sparkle(186, 60, .8)
)

ACC = sparkle(196, 54, .9)

SYMBOLS = {
    "club": suit("club", OLIVE),
    "diamond": suit("diamond", AEGEAN),
    "heart": suit("heart", TERRA),
    "spade": suit("spade", MARBLE),
    "alpha": medallion("circle", MARBLE, marble_bg("mbA") + alpha, bg="url(#mbA)"),
    "beta": medallion("hex", AEGEAN, beta),
    "psi": medallion("shield", MARBLE, marble_bg("mbP") + psi, bg="url(#mbP)"),
    "sigma": medallion("square", DEEP, sigma),
    "wild": wild(AEGEAN, helmet),
    "scatter": scatter(DEEP, temple, "BONUS"),
}
