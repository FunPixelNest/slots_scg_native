"""Neon: glowing sign tubes on dark rounded plates. Pink, cyan, yellow, green."""
from symkit import FONT_FAMILY, P, SUITS, ribbon, star_pts, svg

INK = "#05030C"
PLATE = P("#2A2450", "#14102C", "#06040F", INK)

PINK = "#FF3CAC"
CYAN = "#22E6FF"
YELLOW = "#FFE53B"
GREEN = "#3DFF7A"
VIOLET = "#B46BFF"
ORANGE = "#FF8A2A"
RED = "#FF3C5E"


def tube(shape, color, w=9):
    """[shape]: SVG element with {A} where attributes go. Glow halo, coloured tube, hot white core."""
    glow = shape.replace("{A}", f'fill="none" stroke="{color}" stroke-width="{w * 2.4:.1f}" stroke-linejoin="round" stroke-linecap="round" filter="url(#glowU)" opacity=".95"')
    body = shape.replace("{A}", f'fill="none" stroke="{color}" stroke-width="{w}" stroke-linejoin="round" stroke-linecap="round"')
    core = shape.replace("{A}", f'fill="none" stroke="#FFFFFF" stroke-width="{w * .36:.1f}" stroke-linejoin="round" stroke-linecap="round" opacity=".9"')
    return glow + body + core


GLOW_U = ('<filter id="glowU" filterUnits="userSpaceOnUse" x="-20" y="-20" width="296" height="296">'
          '<feGaussianBlur stdDeviation="7"/></filter>')


def plate(frame):
    screws = "".join(f'<circle cx="{x}" cy="{y}" r="5" fill="url(#silver)" stroke="{INK}" stroke-width="2"/>'
                     for x, y in [(44, 44), (212, 44), (44, 212), (212, 212)])
    return (f'<rect x="16" y="16" width="224" height="224" rx="40" fill="{INK}" filter="url(#drop)"/>'
            '<rect x="20" y="20" width="216" height="216" rx="36" fill="url(#main)" stroke="#4A4280" stroke-width="3"/>'
            '<rect x="20" y="20" width="216" height="100" rx="36" fill="url(#hl)" opacity=".12"/>'
            + tube('<rect x="34" y="34" width="188" height="188" rx="26" {A}/>', frame, 3.5).replace('opacity=".95"', 'opacity=".5"')
            + screws)


def neon_letter(ch, color, frame):
    size = 168 if len(ch) == 1 else 130
    y = 188 if len(ch) == 1 else 176
    t = f'<text x="128" y="{y}" font-family="{FONT_FAMILY}" font-size="{size}" text-anchor="middle" {{A}}>{ch}</text>'
    return svg(plate(frame) + tube(t, color, 7), PLATE, GLOW_U)


def neon_suit(kind, color, frame):
    s = f'<path d="{SUITS[kind]}" transform="translate(128 130) scale(.74) translate(-128 -128)" {{A}}/>'
    return svg(plate(frame) + tube(s, color, 11), PLATE, GLOW_U)


# ------------------------------------------------------------------ wild / scatter
crown = ('<path d="M70,140 L60,72 L96,100 L128,58 L160,100 L196,72 L186,140Z" {A}/>')
crown_base = '<path d="M72,152 L184,152" {A}/>'
wild_emblem = (
    tube(crown, YELLOW, 9) + tube(crown_base, YELLOW, 9)
    + tube('<circle cx="128" cy="118" r="10" {A}/>', PINK, 7)
    + tube('<circle cx="60" cy="68" r="5" {A}/>', CYAN, 6) + tube('<circle cx="196" cy="68" r="5" {A}/>', CYAN, 6)
    + tube('<circle cx="128" cy="52" r="5" {A}/>', CYAN, 6)
)


def neon_wild():
    p = P("#5A1A6A", "#22082E", "#08020F", INK)
    burst = (f'<polygon points="{star_pts(128, 110, 112, 14, .78)}" fill="url(#mainR)" stroke="{INK}" stroke-width="6" stroke-linejoin="round" filter="url(#drop)"/>'
             + tube(f'<polygon points="{star_pts(128, 110, 100, 14, .8)}" {{A}}/>', PINK, 4))
    return svg(burst + wild_emblem + ribbon("WILD", 168, p, 196, fill="url(#pinkR)"), p, GLOW_U +
               '<linearGradient id="pinkR" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#FF9AD6"/><stop offset=".5" stop-color="#FF2DA0"/><stop offset="1" stop-color="#8A0A5A"/></linearGradient>')


def neon_scatter():
    p = P("#1A3A6A", "#0A1430", "#03060F", INK)
    burst = (f'<polygon points="{star_pts(128, 112, 116, 16, .72)}" fill="url(#main)" stroke="{INK}" stroke-width="6" stroke-linejoin="round" filter="url(#drop)"/>'
             + tube(f'<polygon points="{star_pts(128, 112, 104, 16, .74)}" {{A}}/>', CYAN, 4)
             + f'<circle cx="128" cy="112" r="78" fill="url(#mainR)" stroke="{INK}" stroke-width="5"/>'
             + tube('<circle cx="128" cy="112" r="70" {A}/>', YELLOW, 5))
    em = (tube(f'<polygon points="{star_pts(128, 114, 50, 5, .45)}" {{A}}/>', PINK, 9)
          + tube(f'<polygon points="{star_pts(128, 116, 20, 5, .45)}" {{A}}/>', YELLOW, 5))
    return svg(burst + em + ribbon("BONUS", 172, p, 206, fill="url(#cyanR)", size=42), p, GLOW_U +
               '<linearGradient id="cyanR" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#B8FBFF"/><stop offset=".5" stop-color="#18B8E0"/><stop offset="1" stop-color="#064A6A"/></linearGradient>')


SYMBOLS = {
    "j": neon_letter("J", YELLOW, CYAN),
    "q": neon_letter("Q", PINK, YELLOW),
    "k": neon_letter("K", CYAN, PINK),
    "a": neon_letter("A", GREEN, VIOLET),
    "clubs": neon_suit("club", GREEN, YELLOW),
    "diamonds": neon_suit("diamond", ORANGE, CYAN),
    "heart": neon_suit("heart", RED, PINK),
    "spades": neon_suit("spade", VIOLET, GREEN),
    "wild": neon_wild(),
    "scatter": neon_scatter(),
}
