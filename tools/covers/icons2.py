"""Lobby icons in the 'painted card' style: full-bleed scene, big hero art, italic gold title.

python3 tools/covers/icons2.py <out_dir> [theme ids...]
The card frame (gold rim + glow) and the jackpot chip are drawn by the app on top.
App icons:  python3 tools/covers/icons2.py app/src/main/assets/common/lobby/covers
"""
import base64, io, json, os, shutil, subprocess, sys
from PIL import Image, ImageEnhance, ImageFilter

REPO = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
UNITY_ICONS = os.path.join(os.path.dirname(REPO), "slots_android_new/Assets/HolidaySlotAsset/1_Lobby/Sprites/PNG/GameIcons")
W, H = 600, 504
INNER = (0.1, 0.08, 0.9, 0.74)  # inside a framed character symbol, above its WILD/SCATTER label

# mode: "unity" = original painted lobby icon (crop box in px), "fill" = framed art cropped & feathered
# into the scene, "obj" = cut-out object on the scene. extra = small side symbols.
SPEC = {
    "christmas_eve": ("#3D6FD8", ("unity", "Christmas Icon.png", (54, 125, 338, 364))),
    "valentines_day": ("#FF5FA2", ("unity", "Valentine Icon.png", (54, 85, 338, 324))),
    "witch": ("#9B5CFF", ("unity", "Halloween Icon.png", (54, 125, 338, 364))),
    "modern": ("#E5394F", ("obj", "heart", ["diamond"])),
    "forest": ("#52C46B", ("obj", "chanterelle", ["apple", "acorn"])),
    "neon": ("#FF3D8B", ("obj", "heart", ["clubs", "spades"])),
    "underwater": ("#2EC4D6", ("obj", "octopus", ["crab", "jellyfish"])),
    "wolf": ("#7FB4FF", ("fill", "wild", (0.05, 0.0, 0.95, 0.72))),
    "candy": ("#FF7EB6", ("fill", "wild", (0.0, 0.0, 1.0, 0.78))),
    "pirates": ("#F2A33A", ("fill", "wild", INNER)),
    "tropical": ("#3FD3A0", ("fill", "ananas", (0.06, 0.04, 0.94, 0.96))),
    "gold_mine": ("#F5B041", ("fill", "wild", (0.05, 0.0, 0.95, 0.78))),
    "farm": ("#8BD450", ("obj", "pig", ["hen", "sheep"])),
    "most_wanted": ("#D98A4A", ("fill", "bandit1", (0.0, 0.0, 1.0, 1.0))),
    "gems": ("#B37BFF", ("obj", "chestgold", ["chestsilver"])),
    "egypt": ("#F5C451", ("fill", "wild", INNER)),
    "egypt_2": ("#3FA7FF", ("fill", "wild", INNER)),
    "china": ("#E84A3A", ("obj", "sycee", ["coins", "fan"])),
    "greek": ("#5AA9FF", ("fill", "wild", INNER)),
    "viking": ("#8FB8D8", ("fill", "gingergirl", (0.08, 0.06, 0.92, 0.94))),
    "india": ("#FF9F2E", ("fill", "tiger", (0.08, 0.06, 0.92, 0.94))),
    "arabian": ("#C77DFF", ("obj", "ring", ["shoes", "tombak"])),
    "wild_west": ("#D9904A", ("fill", "wild", INNER)),
}


def uri(im, fmt="PNG"):
    buf = io.BytesIO()
    im.save(buf, fmt, **({"quality": 88} if fmt == "JPEG" else {}))
    return f"data:image/{fmt.lower()};base64," + base64.b64encode(buf.getvalue()).decode()


def theme(tid):
    root = f"{REPO}/themes/theme_{tid}/src/main/assets/themes/{tid}"
    return root, json.load(open(f"{root}/theme.json"))


def sym(root, cfg, key):
    s = next(x for x in cfg["symbols"] if x["key"] == key)
    return Image.open(f"{root}/{s['image']}.webp").convert("RGBA")


def upscale(im, target_w):
    """Art is small (~250px): upscale smoothly and sharpen a little."""
    if im.width < target_w:
        im = im.resize((target_w, int(im.height * target_w / im.width)), Image.LANCZOS)
        im = im.filter(ImageFilter.UnsharpMask(radius=2, percent=60, threshold=2))
    return im


def scene(root, cfg):
    rel = cfg.get("backgroundPortrait") or cfg.get("background")
    im = Image.open(f"{root}/{rel}.webp").convert("RGB")
    s = max(W / im.width, H / im.height) * 1.15
    im = im.resize((int(im.width * s) + 1, int(im.height * s) + 1), Image.LANCZOS)
    x, y = (im.width - W) // 2, int((im.height - H) * 0.4)
    im = im.crop((x, y, x + W, y + H)).filter(ImageFilter.GaussianBlur(1.2))
    im = ImageEnhance.Color(im).enhance(1.35)
    return uri(im, "JPEG")


FONT = f"file://{REPO}/app/src/main/res/font/montserrat_black.otf"


def html(tid):
    root, cfg = theme(tid)
    accent, (mode, a, b) = SPEC[tid]
    name = next(e["name"] for e in json.load(open(f"{REPO}/app/src/main/assets/common/themes.json")) if e["id"] == tid)
    layers = ""
    if mode == "unity":
        im = Image.open(os.path.join(UNITY_ICONS, a)).convert("RGBA").crop(b)
        im = upscale(im, W)
        layers = (f'<div style="position:absolute;inset:0;background:url({uri(im.convert("RGB"), "JPEG")}) center/cover;'
                  f'filter:saturate(1.15) contrast(1.05)"></div>')
    else:
        layers = f"""<div style="position:absolute;inset:-4px;background:url({scene(root, cfg)}) center/cover"></div>
<div style="position:absolute;inset:0;background:radial-gradient(48% 52% at 50% 44%, {accent}cc, {accent}33 55%, transparent 75%)"></div>
<div class="rays"></div>"""
        if mode == "fill":
            im = sym(root, cfg, a)
            c = b
            im = im.crop((int(c[0] * im.width), int(c[1] * im.height), int(c[2] * im.width), int(c[3] * im.height)))
            im = upscale(im, 560)
            ar = im.height / im.width
            w = min(420, int(470 / ar))
            layers += (f'<img src="{uri(im)}" style="position:absolute;left:50%;top:6px;width:{w}px;transform:translateX(-50%);'
                       f'-webkit-mask:radial-gradient(closest-side,#000 72%,transparent 100%);'
                       f'filter:drop-shadow(0 0 30px {accent})">')
        else:
            for i, k in enumerate(b[:2]):
                s = upscale(sym(root, cfg, k), 300)
                bb = s.getbbox(); s = s.crop(bb) if bb else s
                left = i == 0
                layers += (f'<img src="{uri(s)}" style="position:absolute;height:150px;bottom:120px;{"left:18px" if left else "right:18px"};'
                           f'transform:rotate({-12 if left else 12}deg);filter:drop-shadow(0 12px 14px rgba(0,0,0,.6))">')
            s = upscale(sym(root, cfg, a), 560)
            bb = s.getbbox(); s = s.crop(bb) if bb else s
            ar = s.height / s.width
            hgt = min(370, int(420 * ar))
            layers += (f'<img src="{uri(s)}" style="position:absolute;left:50%;top:{max(10, 200 - hgt // 2)}px;height:{hgt}px;transform:translateX(-50%);'
                       f'filter:drop-shadow(0 20px 22px rgba(0,0,0,.6)) drop-shadow(0 0 26px {accent})">')
    sparkles = "".join(
        f'<div class="sp" style="left:{x}%;top:{y}%;transform:scale({s})"></div>'
        for x, y, s in [(8, 12, 1), (88, 9, .8), (14, 52, .6), (92, 44, .9), (76, 22, .5), (24, 26, .5)])
    return f"""<!doctype html><html><head><style>
@font-face{{font-family:MB;src:url({FONT})}}
html,body{{margin:0;width:{W}px;height:{H}px;overflow:hidden;background:#12081f}}
.rays{{position:absolute;left:50%;top:42%;width:1000px;height:1000px;margin:-500px 0 0 -500px;
 background:repeating-conic-gradient(rgba(255,255,255,.13) 0 5deg,transparent 5deg 20deg);
 -webkit-mask:radial-gradient(circle,#000 0,transparent 55%)}}
.sp{{position:absolute;width:18px;height:18px;background:radial-gradient(circle,#fff 0 2px,rgba(255,255,255,.0) 9px);}}
.sp:before,.sp:after{{content:"";position:absolute;left:8px;top:-6px;width:2px;height:30px;background:linear-gradient(transparent,#fff,transparent)}}
.sp:after{{transform:rotate(90deg)}}
#t{{position:absolute;left:0;right:0;bottom:18px;text-align:center;white-space:nowrap;font-family:MB;font-style:italic;
 font-size:76px;line-height:1.1;letter-spacing:-1px;transform:skewX(-8deg)}}
#t span{{position:relative;display:inline-block}}
#t .o{{position:absolute;left:0;top:0;color:#3a1600;-webkit-text-stroke:14px #3a1600;filter:drop-shadow(0 6px 6px rgba(0,0,0,.6))}}
#t .f{{position:relative;background:linear-gradient(180deg,#FFFDE8 0%,#FFF1A8 38%,#F7C543 58%,#E08A10 100%);
 -webkit-background-clip:text;color:transparent;-webkit-text-stroke:2px #FFF6C8}}
</style></head><body>
{layers}
{sparkles}
<div style="position:absolute;left:0;right:0;bottom:0;height:42%;background:linear-gradient(180deg,transparent,rgba(18,8,31,.88))"></div>
<div id="t"><span><span class="o">{name}</span><span class="f">{name}</span></span></div>
<script>
const t=document.getElementById('t'),s=t.firstElementChild;let fs=76;
document.fonts.ready.then(()=>{{while(s.offsetWidth>{W - 50} && fs>30){{fs-=2;t.style.fontSize=fs+'px'}}}});
</script>
</body></html>"""


def render(out, ids):
    tmp = os.path.join(out, "_html")
    os.makedirs(tmp, exist_ok=True)
    for tid in ids:
        open(f"{tmp}/{tid}.html", "w").write(html(tid))
    subprocess.run(["node", os.path.join(os.path.dirname(os.path.abspath(__file__)), "shoot.js"), tmp, str(W), str(H)], check=True)
    for tid in ids:
        Image.open(f"{tmp}/{tid}.png").convert("RGB").save(f"{out}/{tid}.webp", "WEBP", quality=90)
        print(tid)
    shutil.rmtree(tmp)


if __name__ == "__main__":
    render(sys.argv[1], sys.argv[2:] or list(SPEC))
