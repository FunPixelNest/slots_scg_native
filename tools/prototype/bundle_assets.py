"""Bundles our own game art into assets.js for the redesign prototype (data URIs).

python3 tools/prototype/bundle_assets.py <out assets.js>
"""
import base64, io, json, os, sys
from PIL import Image

REPO = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
FEATURED = ["pirates", "christmas_eve", "neon", "gems", "farm", "modern", "witch"]


def uri(im, fmt="WEBP", q=80):
    b = io.BytesIO()
    im.save(b, fmt, quality=q)
    return f"data:image/{fmt.lower()};base64," + base64.b64encode(b.getvalue()).decode()


def cover_crop(im, w, h):
    s = max(w / im.width, h / im.height)
    im = im.resize((max(w, round(im.width * s)), max(h, round(im.height * s))), Image.LANCZOS)
    x, y = (im.width - w) // 2, (im.height - h) // 2
    return im.crop((x, y, x + w, y + h))


def main(out):
    index = sorted(json.load(open(f"{REPO}/app/src/main/assets/common/themes.json")), key=lambda e: e["order"])
    data = {"lobby": [], "themes": {}, "avatars": []}
    for e in index:
        cov = Image.open(f"{REPO}/app/src/main/assets/common/{e['tile']}.webp").convert("RGB")
        cov.thumbnail((300, 300))
        data["lobby"].append({"id": e["id"], "name": e["name"], "order": e["order"], "cover": uri(cov, q=78)})
    for tid in FEATURED:
        root = f"{REPO}/themes/theme_{tid}/src/main/assets/themes/{tid}"
        cfg = json.load(open(f"{root}/theme.json"))
        syms = []
        for s in cfg["symbols"]:
            im = Image.open(f"{root}/{s['image']}.webp").convert("RGBA")
            im.thumbnail((128, 128), Image.LANCZOS)
            syms.append(uri(im, q=82))
        bg = Image.open(f"{root}/{cfg.get('backgroundPortrait') or cfg['background']}.webp").convert("RGB")
        data["themes"][tid] = {
            "name": cfg["name"], "sym": syms, "wild": cfg["wildId"], "scatter": cfg["scatterId"],
            "bg": uri(cover_crop(bg, 390, 844), q=70),
        }
    for i in range(12):
        im = Image.open(f"{REPO}/app/src/main/assets/common/avatars/a{i:02d}.webp").convert("RGB")
        im.thumbnail((112, 112))
        data["avatars"].append(uri(im, q=80))
    with open(out, "w") as f:
        f.write("window.ASSETS=" + json.dumps(data, separators=(",", ":")) + ";\n")
    print(out, round(os.path.getsize(out) / 1e6, 2), "MB")


if __name__ == "__main__":
    main(sys.argv[1])
