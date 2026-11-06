"""Puts painted icons (from any image generator) into the game.

Drop files named <theme id>.png/.jpg/.webp into tools/covers/painted/, then:
    python3 tools/covers/import_painted.py
Each image is centre-cropped to 600x504, gets the italic gold title, and is written to
app/src/main/assets/common/lobby/covers/<id>.webp. Themes without a painted file keep their icon.
"""
import base64, io, json, os, shutil, subprocess
from PIL import Image

HERE = os.path.dirname(os.path.abspath(__file__))
REPO = os.path.dirname(os.path.dirname(HERE))
SRC = os.path.join(HERE, "painted")
OUT = os.path.join(REPO, "app/src/main/assets/common/lobby/covers")
W, H = 600, 504
FONT = f"file://{REPO}/app/src/main/res/font/montserrat_black.otf"


def crop(im):
    im = im.convert("RGB")
    s = max(W / im.width, H / im.height)
    im = im.resize((round(im.width * s), round(im.height * s)), Image.LANCZOS)
    x, y = (im.width - W) // 2, (im.height - H) // 2
    return im.crop((x, y, x + W, y + H))


def page(img_uri, name):
    return f"""<!doctype html><html><head><style>
@font-face{{font-family:MB;src:url({FONT})}}
html,body{{margin:0;width:{W}px;height:{H}px;overflow:hidden}}
#t{{position:absolute;left:0;right:0;bottom:16px;text-align:center;white-space:nowrap;font-family:MB;font-style:italic;
 font-size:76px;line-height:1.1;letter-spacing:-1px;transform:skewX(-8deg)}}
#t span{{position:relative;display:inline-block}}
#t .o{{position:absolute;left:0;top:0;color:#3a1600;-webkit-text-stroke:14px #3a1600;filter:drop-shadow(0 6px 6px rgba(0,0,0,.6))}}
#t .f{{position:relative;background:linear-gradient(180deg,#FFFDE8 0%,#FFF1A8 38%,#F7C543 58%,#E08A10 100%);
 -webkit-background-clip:text;color:transparent;-webkit-text-stroke:2px #FFF6C8}}
</style></head><body>
<img src="{img_uri}" style="position:absolute;inset:0;width:{W}px;height:{H}px">
<div style="position:absolute;left:0;right:0;bottom:0;height:34%;background:linear-gradient(180deg,transparent,rgba(18,8,31,.75))"></div>
<div id="t"><span><span class="o">{name}</span><span class="f">{name}</span></span></div>
<script>const t=document.getElementById('t'),s=t.firstElementChild;let fs=76;
document.fonts.ready.then(()=>{{while(s.offsetWidth>{W - 50}&&fs>30){{fs-=2;t.style.fontSize=fs+'px'}}}});</script>
</body></html>"""


def main():
    names = {e["id"]: e["name"] for e in json.load(open(f"{REPO}/app/src/main/assets/common/themes.json"))}
    tmp = os.path.join(HERE, "_painted_html")
    os.makedirs(tmp, exist_ok=True)
    done = []
    for f in sorted(os.listdir(SRC)) if os.path.isdir(SRC) else []:
        tid, ext = os.path.splitext(f)
        if tid not in names or ext.lower() not in (".png", ".jpg", ".jpeg", ".webp"):
            continue
        buf = io.BytesIO()
        crop(Image.open(os.path.join(SRC, f))).save(buf, "JPEG", quality=92)
        uri = "data:image/jpeg;base64," + base64.b64encode(buf.getvalue()).decode()
        open(f"{tmp}/{tid}.html", "w").write(page(uri, names[tid]))
        done.append(tid)
    if done:
        subprocess.run(["node", os.path.join(HERE, "shoot.js"), tmp, str(W), str(H)], check=True)
        for tid in done:
            Image.open(f"{tmp}/{tid}.png").convert("RGB").save(f"{OUT}/{tid}.webp", "WEBP", quality=90)
    shutil.rmtree(tmp)
    missing = sorted(set(names) - set(done))
    print(f"imported {len(done)}: {', '.join(done) or '-'}")
    if missing:
        print(f"no painted file yet ({len(missing)}): {', '.join(missing)}")


if __name__ == "__main__":
    main()
