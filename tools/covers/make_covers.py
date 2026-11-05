"""Builds the lobby cover art (one per theme) from each theme's own background and symbols.

Run from the repo root:  python3 tools/covers/make_covers.py [out_dir]
Writes app/src/main/assets/common/lobby/covers/<id>.webp (468x600, matches the 0.78 card).
"""
import glob, json, math, os, sys
from PIL import Image, ImageDraw, ImageFilter, ImageEnhance, ImageChops

W, H = 468, 600
OUT = sys.argv[1] if len(sys.argv) > 1 else "app/src/main/assets/common/lobby/covers"

# hero = biggest symbol in the middle, sides = two fanned behind it (symbol keys; default wild + top premiums)
PICKS = {
    "gems": ("wild", "scatter", None),
    "neon": ("wild", "scatter", None),
    "india": ("wild", "tiger", "peacock"),
    "viking": ("gingergirl", "darkman", "blondegirl"),
    "pirates": ("wild", "pistol", "saber"),
    "wolf": ("wild", "tooth", "compass"),
    "forest": ("wild", "apple", "acorn"),
}


def load(root, rel):
    p = f"{root}/{rel}.webp"
    return Image.open(p).convert("RGBA") if rel and os.path.exists(p) else None


def cover_crop(im, w, h, focus_y=0.5):
    s = max(w / im.width, h / im.height)
    im = im.resize((max(w, int(im.width * s)), max(h, int(im.height * s))), Image.LANCZOS)
    x = (im.width - w) // 2
    y = int((im.height - h) * focus_y)
    return im.crop((x, y, x + w, y + h))


def avg_color(im):
    small = im.resize((16, 16))
    px = [p for p in (small.get_flattened_data() if hasattr(small, 'get_flattened_data') else small.getdata()) if p[3] > 200]
    if not px:
        return (255, 196, 81)
    r, g, b = (sum(c[i] for c in px) / len(px) for i in range(3))
    # push saturation up so the glow reads as a colour, not grey
    m = (r + g + b) / 3
    return tuple(int(max(0, min(255, m + (c - m) * 1.8 + 30))) for c in (r, g, b))


def shadowed(im, blur=14, alpha=170, dy=12):
    pad = blur * 3
    canvas = Image.new("RGBA", (im.width + pad * 2, im.height + pad * 2 + dy), (0, 0, 0, 0))
    sh = Image.new("RGBA", im.size, (0, 0, 0, alpha))
    sh.putalpha(ImageChops.multiply(im.getchannel("A"), Image.new("L", im.size, alpha)))
    canvas.alpha_composite(sh, (pad, pad + dy))
    canvas = canvas.filter(ImageFilter.GaussianBlur(blur))
    canvas.alpha_composite(im, (pad, pad))
    return canvas, pad


def place(base, im, cx, cy, size, angle=0.0):
    im = im.copy()
    im.thumbnail((size, size), Image.LANCZOS)
    if angle:
        im = im.rotate(angle, Image.BICUBIC, expand=True)
    s, pad = shadowed(im)
    base.alpha_composite(s, (int(cx - im.width / 2 - pad), int(cy - im.height / 2 - pad)))


def glow(size, color, alpha):
    g = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(g)
    for i in range(30, 0, -1):
        r = size / 2 * i / 30
        a = int(alpha * (1 - i / 30) ** 1.6)
        d.ellipse((size / 2 - r, size / 2 - r, size / 2 + r, size / 2 + r), fill=color + (a,))
    return g.filter(ImageFilter.GaussianBlur(size / 30))


def rays(color):
    r = Image.new("RGBA", (W * 2, W * 2), (0, 0, 0, 0))
    d = ImageDraw.Draw(r)
    c = W
    for k in range(16):
        a0 = k * 2 * math.pi / 16
        a1 = a0 + math.pi / 30
        d.polygon([(c, c), (c + math.cos(a0) * W * 1.5, c + math.sin(a0) * W * 1.5),
                   (c + math.cos(a1) * W * 1.5, c + math.sin(a1) * W * 1.5)], fill=color + (38,))
    mask = glow(W * 2, (255, 255, 255), 255).getchannel("A")
    r.putalpha(ImageChops.multiply(r.getchannel("A"), mask))
    return r.filter(ImageFilter.GaussianBlur(3))


def make(theme_dir):
    tid = os.path.basename(theme_dir)[len("theme_"):]
    root = f"{theme_dir}/src/main/assets/themes/{tid}"
    t = json.load(open(f"{root}/theme.json"))
    syms = {s["key"]: s for s in t["symbols"] if s.get("image")}
    keys = [s["key"] for s in t["symbols"] if s.get("image")]
    premium = [k for k in keys if k not in ("wild", "scatter")][-2:]
    hero_k, l_k, r_k = PICKS.get(tid, ("wild", premium[-1] if premium else None, premium[0] if len(premium) > 1 else None))
    hero = load(root, syms[hero_k]["image"]) if hero_k in syms else None
    left = load(root, syms[l_k]["image"]) if l_k in syms else None
    right = load(root, syms[r_k]["image"]) if r_k in syms else None

    bg = load(root, t.get("backgroundPortrait")) or load(root, t.get("background"))
    base = Image.new("RGBA", (W, H), (14, 18, 36, 255))
    if bg:
        b = cover_crop(bg, W, H, 0.45).filter(ImageFilter.GaussianBlur(2.5))
        b = ImageEnhance.Brightness(b).enhance(0.62)
        b = ImageEnhance.Color(b).enhance(1.15)
        base.alpha_composite(b)
    accent = avg_color(hero) if hero else (255, 196, 81)
    cy = int(H * 0.44)
    base.alpha_composite(rays(accent), (W // 2 - W, cy - W))
    g = glow(int(W * 1.2), accent, 190)
    base.alpha_composite(g, (W // 2 - g.width // 2, cy - g.height // 2))

    if left:
        place(base, left, W * 0.2, cy + 60, int(W * 0.36), 10)
    if right:
        place(base, right, W * 0.8, cy + 60, int(W * 0.36), -10)
    if hero:
        place(base, hero, W / 2, cy, int(W * 0.6))

    # vignette + bottom fade for the name/feature text the card draws on top
    v = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    vd = ImageDraw.Draw(v)
    for y in range(H):
        top = max(0, 1 - y / (H * 0.22)) * 120
        bot = max(0, (y - H * 0.62) / (H * 0.38)) * 235
        vd.line([(0, y), (W, y)], fill=(7, 11, 22, int(min(255, top + bot))))
    base.alpha_composite(v)
    # thin top sheen
    sheen = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    ImageDraw.Draw(sheen).ellipse((-W * 0.4, -H * 0.55, W * 1.4, H * 0.18), fill=(255, 255, 255, 18))
    base.alpha_composite(sheen.filter(ImageFilter.GaussianBlur(20)))
    os.makedirs(OUT, exist_ok=True)
    base.convert("RGB").save(f"{OUT}/{tid}.webp", "WEBP", quality=86)
    return tid


if __name__ == "__main__":
    for d in sorted(glob.glob("themes/theme_*")):
        print(make(d))
