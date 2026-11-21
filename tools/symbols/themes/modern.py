"""Modern: sleek casino, black + chrome with electric blue and hot pink neon gradients."""
from symkit import P, SUITS, gem, letter, sparkle, svg, wild, scatter

INK = "#07070F"
CHROME = P("#FFFFFF", "#A9B4C8", "#3A4258", INK)
BLUE = P("#BFF4FF", "#1E9BFF", "#0A1F8A", INK)
PINK = P("#FFD0F0", "#FF2DA0", "#6A0646", INK)
VIOLET = P("#E8D0FF", "#9A4BFF", "#2E0A7A", INK)
CYAN = P("#D8FFFA", "#18E0D0", "#05505A", INK)
GOLDP = P("#FFF6C8", "#FFC020", "#8A4600", INK)


def chip(cx, cy, r, c, ink=INK):
    """Small casino chip accent."""
    dashes = "".join(
        f'<rect x="{cx - r * .13:.1f}" y="{cy - r:.1f}" width="{r * .26:.1f}" height="{r * .3:.1f}" fill="#fff" transform="rotate({a} {cx} {cy})"/>'
        for a in range(0, 360, 60))
    return (f'<g filter="url(#drop)"><circle cx="{cx}" cy="{cy}" r="{r + 3}" fill="{ink}"/>'
            f'<circle cx="{cx}" cy="{cy}" r="{r}" fill="{c}"/>{dashes}'
            f'<circle cx="{cx}" cy="{cy}" r="{r * .62:.1f}" fill="{c}" stroke="#fff" stroke-width="{r * .1:.1f}"/>'
            f'<ellipse cx="{cx - r * .3:.1f}" cy="{cy - r * .38:.1f}" rx="{r * .4:.1f}" ry="{r * .2:.1f}" fill="#fff" opacity=".5"/></g>')


def acc_gem(c1, c2, c3):
    return f'<g filter="url(#drop)">{gem(214, 46, 22, c1, c2, c3, INK)}</g>'


def chrome_suit(kind, p, glow, accent=""):
    """Glossy suit with a chrome rim and a soft neon halo."""
    d = SUITS[kind]
    inner = (f'<path d="{d}" fill="{glow}" opacity=".7" filter="url(#glow)"/>'
             f'<g filter="url(#drop)"><path d="{d}" fill="{p["ink"]}" stroke="{p["ink"]}" stroke-width="20" stroke-linejoin="round"/></g>'
             f'<path d="{d}" fill="url(#silver)" stroke="url(#silver)" stroke-width="10" stroke-linejoin="round"/>'
             f'<path d="{d}" fill="url(#mainR)"/>'
             f'<path d="{d}" fill="url(#shine)" opacity=".8"/>')
    return svg(inner + accent, p)


# ------------------------------------------------------------------ emblems
bolt_d = "M146,34 L84,118 L120,118 L104,176 L172,88 L134,88 L152,34Z"
wild_emblem = (
    '<g transform="translate(128 104) scale(1.18) translate(-128 -104)"><defs><linearGradient id="bolt" x1="0" y1="0" x2="1" y2="1"><stop offset="0" stop-color="#FFFFFF"/>'
    '<stop offset=".35" stop-color="#7FE8FF"/><stop offset=".7" stop-color="#1E7BFF"/><stop offset="1" stop-color="#FF2DA0"/></linearGradient></defs>'
    f'<path d="{bolt_d}" fill="#FF2DA0" opacity=".9" filter="url(#glow)"/>'
    f'<path d="{bolt_d}" fill="{INK}" stroke="{INK}" stroke-width="16" stroke-linejoin="round" filter="url(#drop)"/>'
    f'<path d="{bolt_d}" fill="url(#bolt)" stroke="url(#silver)" stroke-width="5" stroke-linejoin="round"/>'
    '<path d="M146,40 L96,108 L110,108 L148,46Z" fill="#fff" opacity=".6"/>'
    + '</g>' + sparkle(66, 66, .9) + sparkle(194, 146, .7, "#FFD0F0")
)

big_gem = (
    '<defs><linearGradient id="gA" x1="0" y1="0" x2="1" y2="1"><stop offset="0" stop-color="#E8FBFF"/><stop offset="1" stop-color="#3AB8FF"/></linearGradient>'
    '<linearGradient id="gB" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#5AA0FF"/><stop offset="1" stop-color="#0A1F8A"/></linearGradient>'
    '<linearGradient id="gC" x1="0" y1="0" x2="1" y2="1"><stop offset="0" stop-color="#FF8AD0"/><stop offset="1" stop-color="#8A0A5A"/></linearGradient></defs>'
    f'<path d="M84,86 L172,86 L194,110 L128,172 L62,110Z" fill="{INK}" stroke="{INK}" stroke-width="14" stroke-linejoin="round" filter="url(#drop)"/>'
    '<path d="M84,86 L172,86 L194,110 L62,110Z" fill="url(#gA)"/>'
    '<path d="M62,110 L194,110 L128,172Z" fill="url(#gB)"/>'
    '<path d="M104,110 L128,172 L152,110Z" fill="url(#gC)" opacity=".85"/>'
    '<path d="M84,86 L104,110 L128,86 L152,110 L172,86" fill="none" stroke="#fff" stroke-width="3" stroke-linejoin="round" opacity=".9"/>'
    '<path d="M62,110 L194,110" stroke="#fff" stroke-width="3" opacity=".8"/>'
    '<path d="M90,92 L118,92 L102,106Z" fill="#fff" opacity=".85"/>'
    + sparkle(176, 80, 1.1) + sparkle(78, 150, .7) + sparkle(182, 150, .6, "#FFD0F0")
)

SYMBOLS = {
    "j": letter("J", CYAN, acc_gem("#D8FFFA", "#18E0D0", "#05505A")),
    "q": letter("Q", PINK, acc_gem("#FFD0F0", "#FF2DA0", "#6A0646")),
    "k": letter("K", BLUE, acc_gem("#BFF4FF", "#1E9BFF", "#0A1F8A")),
    "a": letter("A", GOLDP, acc_gem("#FFF6C8", "#FFC020", "#8A4600")),
    "9": letter("9", VIOLET, chip(216, 44, 20, "#9A4BFF")),
    "10": letter("10", CHROME, chip(220, 40, 20, "#FF2DA0")),
    "diamond": chrome_suit("diamond", BLUE, "#1E9BFF", sparkle(160, 84, 1.1)),
    "heart": chrome_suit("heart", PINK, "#FF2DA0", sparkle(78, 82, 1.0)),
    "wild": wild(P("#FF8AD0", "#C0168A", "#2A0A3A", INK), wild_emblem),
    "scatter": scatter(P("#5A6A8A", "#1A2238", "#05070F", INK), big_gem, "BONUS"),
}
