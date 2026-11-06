"""Lobby icons drawn from scratch (tools/covers/art.py) + italic gold title.

python3 tools/covers/icons3.py <out_dir> [theme ids...]
App icons:  python3 tools/covers/icons3.py app/src/main/assets/common/lobby/covers
"""
import json, os, shutil, subprocess, sys
from PIL import Image

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import art  # noqa: E402

REPO = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
W, H = art.W, art.H
FONT = f"file://{REPO}/app/src/main/res/font/montserrat_black.otf"


def html(tid):
    name = next(e["name"] for e in json.load(open(f"{REPO}/app/src/main/assets/common/themes.json")) if e["id"] == tid)
    return f"""<!doctype html><html><head><style>
@font-face{{font-family:MB;src:url({FONT})}}
html,body{{margin:0;width:{W}px;height:{H}px;overflow:hidden;background:#12081f}}
svg{{position:absolute;left:0;top:-14px}}
#t{{position:absolute;left:0;right:0;bottom:16px;text-align:center;white-space:nowrap;font-family:MB;font-style:italic;
 font-size:76px;line-height:1.1;letter-spacing:-1px;transform:skewX(-8deg)}}
#t span{{position:relative;display:inline-block}}
#t .o{{position:absolute;left:0;top:0;color:#3a1600;-webkit-text-stroke:14px #3a1600;filter:drop-shadow(0 6px 6px rgba(0,0,0,.6))}}
#t .f{{position:relative;background:linear-gradient(180deg,#FFFDE8 0%,#FFF1A8 38%,#F7C543 58%,#E08A10 100%);
 -webkit-background-clip:text;color:transparent;-webkit-text-stroke:2px #FFF6C8}}
</style></head><body>
{art.svg(tid)}
<div style="position:absolute;left:0;right:0;bottom:0;height:34%;background:linear-gradient(180deg,transparent,rgba(18,8,31,.8))"></div>
<div id="t"><span><span class="o">{name}</span><span class="f">{name}</span></span></div>
<script>
const t=document.getElementById('t'),s=t.firstElementChild;let fs=76;
document.fonts.ready.then(()=>{{while(s.offsetWidth>{W - 50} && fs>30){{fs-=2;t.style.fontSize=fs+'px'}}}});
</script></body></html>"""


def render(out, ids):
    tmp = os.path.join(out, "_html")
    os.makedirs(tmp, exist_ok=True)
    for tid in ids:
        open(f"{tmp}/{tid}.html", "w").write(html(tid))
    subprocess.run(["node", os.path.join(os.path.dirname(os.path.abspath(__file__)), "shoot.js"), tmp, str(W), str(H)], check=True)
    for tid in ids:
        Image.open(f"{tmp}/{tid}.png").convert("RGB").save(f"{out}/{tid}.webp", "WEBP", quality=90)
    shutil.rmtree(tmp)


if __name__ == "__main__":
    render(sys.argv[1], sys.argv[2:] or list(art.SCENES))
