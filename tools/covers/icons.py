"""Modern lobby icons: theme hero art + custom typography, rendered with Playwright.

python3 tools/covers/icons.py <style A|B> <out_dir> [theme ids...]
A = theme scene behind the art (used in the app), B = flat two-colour gradient.
App icons:  python3 tools/covers/icons.py A app/src/main/assets/common/lobby/covers
"""
import base64, io, json, os, sys
from PIL import Image, ImageDraw, ImageFilter

REPO = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
W, H = 468, 600
PORTRAIT = (0.16, 0.06, 0.84, 0.74)  # square-ish crop of a framed character, above its WILD label

# id: (title, subtitle-line, accent, accent2, hero, [sides]); a symbol is "key" or ("key", crop) -> round badge
SPEC = {
    "christmas_eve": ("Christmas", "EVE", "#E5394F", "#2F7D4F", "bell", ["sock", "candle"]),
    "valentines_day": ("Valentine's", "DAY", "#FF5FA2", "#8E2DE2", "rose", ["lily", "astra"]),
    "witch": ("Witch's", "CAULDRON", "#9B5CFF", "#1BC47D", "hat", ["violetbottle", "broom"]),
    "modern": ("Modern", "SUITS", "#F5C451", "#C2185B", "heart", ["diamond"]),
    "forest": ("Enchanted", "FOREST", "#52C46B", "#C65A1E", "chanterelle", ["apple", "acorn"]),
    "neon": ("Neon", "NIGHTS", "#38D6FF", "#FF3D8B", "heart", ["clubs", "diamonds"]),
    "underwater": ("Under", "WATER", "#2EC4D6", "#7B3FE4", "octopus", ["crab", "jellyfish"]),
    "wolf": ("Wolf", "MOON", "#7FB4FF", "#203A74", ("wild", PORTRAIT), ["compass", "agaric"]),
    "candy": ("Candy", "LAND", "#FF7EB6", "#FFB341", "pink", ["brown", "lily"]),
    "pirates": ("Pirates", "TREASURE", "#F2A33A", "#B3261E", ("wild", PORTRAIT), ["pistol", "saber"]),
    "tropical": ("Tropical", "PARADISE", "#3FD3A0", "#FF8A3D", ("ananas", (0.1, 0.1, 0.9, 0.9)), [("strawberry", (0.1, 0.1, 0.9, 0.9)), ("blueglass", (0.1, 0.1, 0.9, 0.9))]),
    "gold_mine": ("Gold", "MINE", "#F5B041", "#5D4037", "lantern", ["gold", "pickaxe"]),
    "farm": ("Happy", "FARM", "#8BD450", "#F2994A", "pig", ["hen", "pumpkin"]),
    "most_wanted": ("Most", "WANTED", "#D98A4A", "#5B2A12", "bandit1", ["gold", "whiskey"]),
    "gems": ("Gem", "VAULT", "#B37BFF", "#2EC4D6", "chestgold", ["chestsilver"]),
    "egypt": ("Egypt", "QUEEN", "#F5C451", "#1E3A8A", ("wild", PORTRAIT), [("ankh", (0.12, 0.12, 0.88, 0.88)), ("eye", (0.12, 0.12, 0.88, 0.88))]),
    "egypt_2": ("Egypt", "II", "#3FA7FF", "#F5C451", "vase", ["scarab", "book"]),
    "china": ("Fortune", "CHINA", "#E84A3A", "#F5C451", "sycee", ["coins", "fan"]),
    "greek": ("Greek", "GODS", "#5AA9FF", "#F5C451", ("wild", PORTRAIT), [("alpha", (0.1, 0.1, 0.9, 0.9)), ("sigma", (0.1, 0.1, 0.9, 0.9))]),
    "viking": ("Vikings", "", "#8FB8D8", "#B3261E", ("gingergirl", PORTRAIT), [("darkman", PORTRAIT), ("blondeman", PORTRAIT)]),
    "india": ("India", "RICHES", "#FF9F2E", "#1BC47D", ("tiger", (0.1, 0.08, 0.9, 0.92)), [("elephant", (0.1, 0.08, 0.9, 0.92)), ("peacock", (0.1, 0.08, 0.9, 0.92))]),
    "arabian": ("Arabian", "NIGHTS", "#C77DFF", "#F5C451", "ring", ["shoes", "tombak"]),
    "wild_west": ("Wild", "WEST", "#D9904A", "#7A2E1A", ("wild", PORTRAIT), [("hat", (0.1, 0.1, 0.9, 0.9)), ("gun", (0.1, 0.1, 0.9, 0.9))]),
}


def theme(tid):
    root = f"{REPO}/themes/theme_{tid}/src/main/assets/themes/{tid}"
    return root, json.load(open(f"{root}/theme.json"))


def sym_png(root, cfg, spec, size):
    key, crop = (spec, None) if isinstance(spec, str) else spec
    s = next(x for x in cfg["symbols"] if x["key"] == key)
    im = Image.open(f"{root}/{s['image']}.webp").convert("RGBA")
    round_badge = crop is not None
    if crop:
        w, h = im.size
        im = im.crop((int(crop[0] * w), int(crop[1] * h), int(crop[2] * w), int(crop[3] * h)))
        side = min(im.size)
        im = im.crop(((im.width - side) // 2, 0, (im.width - side) // 2 + side, side)).resize((size, size), Image.LANCZOS)
        m = Image.new("L", (size * 4, size * 4), 0)
        ImageDraw.Draw(m).ellipse((0, 0, size * 4, size * 4), fill=255)
        im.putalpha(m.resize((size, size), Image.LANCZOS))
    else:
        bb = im.getbbox()
        im = im.crop(bb) if bb else im
        im.thumbnail((size, size), Image.LANCZOS)
    buf = io.BytesIO()
    im.save(buf, "PNG")
    return "data:image/png;base64," + base64.b64encode(buf.getvalue()).decode(), round_badge


def bg_uri(root, cfg):
    rel = cfg.get("backgroundPortrait") or cfg.get("background")
    im = Image.open(f"{root}/{rel}.webp").convert("RGB")
    s = max(W / im.width, H / im.height)
    im = im.resize((int(im.width * s) + 1, int(im.height * s) + 1), Image.LANCZOS)
    x, y = (im.width - W) // 2, int((im.height - H) * 0.45)
    im = im.crop((x, y, x + W, y + H)).filter(ImageFilter.GaussianBlur(6))
    buf = io.BytesIO()
    im.save(buf, "JPEG", quality=85)
    return "data:image/jpeg;base64," + base64.b64encode(buf.getvalue()).decode()


FONT = f"file://{REPO}/app/src/main/res/font/montserrat_black.otf"
FONT_B = f"file://{REPO}/app/src/main/res/font/montserrat_bold.otf"


def html(tid, style):
    root, cfg = theme(tid)
    _t, _s, a1, a2, hero, sides = SPEC[tid]
    # the icon shows the machine's real name: first word big, the rest as a spaced subtitle
    name = next(e["name"] for e in json.load(open(f"{REPO}/app/src/main/assets/common/themes.json")) if e["id"] == tid)
    title, _, sub = name.partition(" ")
    sub = sub.upper()
    hero_uri, hero_round = sym_png(root, cfg, hero, 300)
    side_imgs = [sym_png(root, cfg, s, 170) for s in sides[:2]]
    badge = "border-radius:50%;box-shadow:0 0 0 5px rgba(255,255,255,.9),0 0 0 9px {a},0 18px 40px rgba(0,0,0,.6);"
    hero_css = badge.format(a=a1) if hero_round else "filter:drop-shadow(0 22px 26px rgba(0,0,0,.55)) drop-shadow(0 0 30px " + a1 + "88);"
    sides_html = ""  # drawn first so the hero overlaps them
    for i, (u, rnd) in enumerate(side_imgs):
        left = i == 0
        css = badge.format(a=a2) if rnd else "filter:drop-shadow(0 14px 16px rgba(0,0,0,.55));"
        sides_html += (f'<img src="{u}" style="position:absolute;width:{116 if rnd else 132}px;top:{262 if rnd else 250}px;'
                       f'{"left:10px" if left else "right:10px"};transform:rotate({-12 if left else 12}deg);{css}">')
    if style == "A":
        back = f"""<div style="position:absolute;inset:0;background:url({bg_uri(root, cfg)}) center/cover;filter:saturate(1.2) brightness(.6)"></div>
<div style="position:absolute;inset:0;background:radial-gradient(60% 45% at 50% 40%, {a1}cc, transparent 70%),linear-gradient(180deg,{a2}55,transparent 40%,#070B16 92%)"></div>"""
    else:
        back = f"""<div style="position:absolute;inset:0;background:linear-gradient(160deg,{a1} 0%,{a2} 100%)"></div>
<div style="position:absolute;inset:0;background:repeating-linear-gradient(135deg,rgba(255,255,255,.07) 0 18px,transparent 18px 36px)"></div>
<div style="position:absolute;inset:0;background:radial-gradient(55% 40% at 50% 40%, rgba(255,255,255,.55), transparent 70%)"></div>
<div style="position:absolute;left:0;right:0;bottom:0;height:46%;background:linear-gradient(180deg,transparent,rgba(7,11,22,.85))"></div>"""
    tsize = 64 if len(title) <= 7 else (54 if len(title) <= 9 else 46)
    return f"""<!doctype html><html><head><style>
@font-face{{font-family:MB;src:url({FONT})}} @font-face{{font-family:MS;src:url({FONT_B})}}
html,body{{margin:0;width:{W}px;height:{H}px;overflow:hidden;background:#070B16}}
.t{{font-family:MB;font-size:{tsize}px;line-height:1;letter-spacing:-1px;
 background:linear-gradient(180deg,#FFFFFF 0%,#FFF3C4 45%,#F5C451 60%,#C8871C 100%);-webkit-background-clip:text;color:transparent;
 filter:drop-shadow(0 3px 0 #4a2a00) drop-shadow(0 8px 14px rgba(0,0,0,.7));text-align:center}}
.s{{font-family:MS;font-size:20px;letter-spacing:9px;color:#fff;text-align:center;margin-top:8px;
 text-shadow:0 2px 8px rgba(0,0,0,.8)}}
.rays{{position:absolute;left:50%;top:40%;width:900px;height:900px;margin:-450px 0 0 -450px;
 background:repeating-conic-gradient(rgba(255,255,255,.10) 0 6deg,transparent 6deg 22.5deg);
 -webkit-mask:radial-gradient(circle,#000 0,transparent 60%)}}
</style></head><body>
{back}
<div class="rays"></div>
{sides_html}
<img src="{hero_uri}" style="position:absolute;left:50%;top:{92 if hero_round else 70}px;width:{240 if hero_round else 270}px;transform:translateX(-50%);{hero_css}">
<div style="position:absolute;left:0;right:0;bottom:{44 if sub else 64}px">
 <div class="t">{title}</div>{f'<div class="s">{sub}</div>' if sub else ''}
</div>
<div style="position:absolute;inset:0;box-shadow:inset 0 0 0 2px rgba(255,255,255,.08)"></div>
</body></html>"""


def render(style, out, ids):
    """Writes one HTML page per theme, screenshots them with Node Playwright, saves WebP."""
    import subprocess
    tmp = os.path.join(out, "_html")
    os.makedirs(tmp, exist_ok=True)
    for tid in ids:
        open(f"{tmp}/{tid}.html", "w").write(html(tid, style))
    subprocess.run(["node", os.path.join(os.path.dirname(os.path.abspath(__file__)), "shoot.js"), tmp, str(W), str(H)], check=True)
    for tid in ids:
        Image.open(f"{tmp}/{tid}.png").convert("RGB").save(f"{out}/{tid}.webp", "WEBP", quality=88)
        print(tid)
    import shutil
    shutil.rmtree(tmp)


if __name__ == "__main__":
    ids = sys.argv[3:] or list(SPEC)
    render(sys.argv[1], sys.argv[2], ids)
