"""Character avatars for the profile (drawn here as SVG, rendered to WebP).

python3 tools/covers/avatars.py [out_dir]   (default app/src/main/assets/common/avatars)
"""
import os, shutil, subprocess, sys
from PIL import Image

HERE = os.path.dirname(os.path.abspath(__file__))
REPO = os.path.dirname(os.path.dirname(HERE))
OUT = sys.argv[1] if len(sys.argv) > 1 else os.path.join(REPO, "app/src/main/assets/common/avatars")
S = 256

DEFS = """<defs>
<linearGradient id="gold" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#FFF4B8"/><stop offset=".5" stop-color="#FFD24A"/><stop offset="1" stop-color="#C77A0A"/></linearGradient>
<radialGradient id="shine" cx=".35" cy=".25" r=".7"><stop offset="0" stop-color="#fff" stop-opacity=".55"/><stop offset="1" stop-color="#fff" stop-opacity="0"/></radialGradient>
<filter id="sh" x="-20%" y="-20%" width="140%" height="140%"><feDropShadow dx="0" dy="4" stdDeviation="4" flood-color="#000" flood-opacity=".35"/></filter>
</defs>"""


def bg(c1, c2):
    return (f'<defs><radialGradient id="bg" cx=".5" cy=".35" r=".75"><stop offset="0" stop-color="{c1}"/><stop offset="1" stop-color="{c2}"/></radialGradient></defs>'
            f'<rect width="{S}" height="{S}" fill="url(#bg)"/>')


def body(cloth, trim=None):
    s = f'<path d="M30,256 Q34,200 128,196 Q222,200 226,256Z" fill="{cloth}"/>'
    if trim:
        s += f'<path d="M100,198 L128,240 L156,198" fill="none" stroke="{trim}" stroke-width="10" stroke-linejoin="round"/>'
    return s


def head(skin, blush="#FF8A8A"):
    return (f'<rect x="110" y="168" width="36" height="36" rx="10" fill="{skin}"/>'
            f'<ellipse cx="72" cy="130" rx="12" ry="16" fill="{skin}"/><ellipse cx="184" cy="130" rx="12" ry="16" fill="{skin}"/>'
            f'<ellipse cx="128" cy="126" rx="56" ry="62" fill="{skin}"/>'
            f'<ellipse cx="96" cy="146" rx="11" ry="7" fill="{blush}" opacity=".45"/><ellipse cx="160" cy="146" rx="11" ry="7" fill="{blush}" opacity=".45"/>')


def eyes(color="#2A1A10", lashes=False, y=124):
    s = ""
    for x in (106, 150):
        s += (f'<ellipse cx="{x}" cy="{y}" rx="10" ry="12" fill="#fff"/><circle cx="{x + 1}" cy="{y + 2}" r="7" fill="{color}"/>'
              f'<circle cx="{x + 3}" cy="{y - 1}" r="2.6" fill="#fff"/>')
        if lashes:
            s += f'<path d="M{x - 11},{y - 9} q11,-9 22,0" stroke="#1A0E08" stroke-width="3.5" fill="none" stroke-linecap="round"/>'
    return s


def brows(color, y=104, angry=False):
    if angry:
        return (f'<path d="M94,{y} L118,{y + 6}" stroke="{color}" stroke-width="6" stroke-linecap="round"/>'
                f'<path d="M162,{y} L138,{y + 6}" stroke="{color}" stroke-width="6" stroke-linecap="round"/>')
    return (f'<path d="M95,{y + 3} q11,-7 22,0" stroke="{color}" stroke-width="5" fill="none" stroke-linecap="round"/>'
            f'<path d="M139,{y + 3} q11,-7 22,0" stroke="{color}" stroke-width="5" fill="none" stroke-linecap="round"/>')


def smile(open_=True, lip="#C2185B"):
    if open_:
        return ('<path d="M108,156 Q128,180 148,156 Q128,164 108,156Z" fill="#7A1020"/>'
                '<path d="M114,160 Q128,168 142,160" stroke="#fff" stroke-width="4" fill="none"/>')
    return f'<path d="M110,158 Q128,172 146,158" stroke="{lip}" stroke-width="6" fill="none" stroke-linecap="round"/>'


def nose(skin_dark):
    return f'<path d="M126,132 q-6,14 4,16" stroke="{skin_dark}" stroke-width="4" fill="none" stroke-linecap="round"/>'


def beard(color, long=False):
    if long:
        return (f'<path d="M74,128 Q76,232 128,240 Q180,232 182,128 Q170,170 128,172 Q86,170 74,128Z" fill="{color}"/>'
                f'<path d="M106,150 Q128,140 150,150 Q140,160 128,156 Q116,160 106,150Z" fill="{color}"/>')
    return (f'<path d="M76,132 Q80,196 128,200 Q176,196 180,132 Q168,168 128,170 Q88,168 76,132Z" fill="{color}"/>'
            f'<path d="M104,152 Q128,142 152,152 Q140,160 128,157 Q116,160 104,152Z" fill="{color}"/>')


SKIN, SKIN_D = "#FFD2B0", "#D99A72"
TAN, TAN_D = "#E8A878", "#B87048"
BROWN, BROWN_D = "#B9774E", "#7A4A2E"


def king():
    return (bg("#FFE08A", "#C2700E") + body("#B3122A", "#FFD24A") + '<path d="M60,214 Q128,236 196,214 L200,232 Q128,252 56,232Z" fill="#fff"/>'
            + head(SKIN) + beard("#8A5A2B") + eyes() + brows("#6B3E1F") + nose(SKIN_D) + smile(False, "#6B1E10")
            + '<g filter="url(#sh)"><path d="M70,78 L80,28 L104,56 L128,20 L152,56 L176,28 L186,78Z" fill="url(#gold)"/>'
              '<rect x="68" y="72" width="120" height="16" rx="5" fill="url(#gold)"/>'
              '<circle cx="128" cy="44" r="7" fill="#E5394F"/><circle cx="94" cy="62" r="5" fill="#38D6FF"/><circle cx="162" cy="62" r="5" fill="#22C55E"/></g>')


def queen():
    return (bg("#FFB3D6", "#9D1747") + body("#7B3FE4", "#FFD24A")
            + '<path d="M66,120 Q60,40 128,40 Q196,40 190,120 L200,210 Q176,190 170,140 L86,140 Q80,190 56,210Z" fill="#6B3E1F"/>'
            + head(SKIN) + eyes("#2E6B3A", lashes=True) + brows("#5A3416") + nose(SKIN_D) + smile(False, "#D81B60")
            + '<path d="M72,100 Q100,62 150,66 Q130,78 128,96 Q110,80 72,100Z" fill="#6B3E1F"/>'
            + '<g filter="url(#sh)"><path d="M88,64 L98,34 L114,54 L128,28 L142,54 L158,34 L168,64Z" fill="url(#gold)"/>'
              '<circle cx="128" cy="40" r="6" fill="#FF5FA2"/></g>'
            + '<circle cx="74" cy="148" r="6" fill="url(#gold)"/><circle cx="182" cy="148" r="6" fill="url(#gold)"/>')


def pirate():
    return (bg("#7FE0FF", "#1D4FB8") + body("#E5394F") + '<path d="M30,256 Q34,200 128,196 Q222,200 226,256" fill="none" stroke="#fff" stroke-width="10" stroke-dasharray="14 14"/>'
            + head(TAN) + beard("#2A1A10") + eyes() + brows("#1A0E08", angry=True) + nose(TAN_D) + smile(True)
            + '<path d="M90,108 L176,100" stroke="#111" stroke-width="5"/><ellipse cx="150" cy="124" rx="16" ry="14" fill="#111"/>'
            + '<g filter="url(#sh)"><path d="M44,96 Q128,10 212,96 Q170,80 128,84 Q86,80 44,96Z" fill="#1A1A24"/>'
              '<path d="M50,94 Q128,70 206,94" stroke="url(#gold)" stroke-width="6" fill="none"/>'
              '<circle cx="128" cy="62" r="11" fill="#fff"/><path d="M120,74 l16,0" stroke="#fff" stroke-width="5"/></g>')


def cowboy():
    return (bg("#FFC27A", "#B5541E") + body("#3A6FB0") + '<path d="M96,200 L128,226 L160,200 L150,236 L106,236Z" fill="#E5394F"/>'
            + head(SKIN) + '<path d="M96,150 Q128,140 160,150" stroke="#6B3E1F" stroke-width="8" fill="none" stroke-linecap="round"/>'
            + eyes("#3A6FB0") + brows("#6B3E1F") + nose(SKIN_D) + smile(True)
            + '<g filter="url(#sh)"><ellipse cx="128" cy="86" rx="100" ry="20" fill="#8A5A2B"/>'
              '<path d="M78,86 Q72,30 104,34 Q128,46 152,34 Q184,30 178,86Z" fill="#A86A34"/>'
              '<rect x="78" y="72" width="100" height="10" fill="#5A3416"/><circle cx="128" cy="77" r="5" fill="url(#gold)"/></g>')


def viking():
    return (bg("#BFD7F0", "#2A4A6E") + body("#7A4A2E") + '<path d="M30,256 Q34,200 128,196 Q222,200 226,256Z" fill="none" stroke="#C9A66B" stroke-width="6"/>'
            + head(SKIN) + beard("#E07A2E", long=True)
            + '<path d="M110,236 l-6,16 M146,236 l6,16" stroke="url(#gold)" stroke-width="8" stroke-linecap="round"/>'
            + eyes("#2E6BB0") + brows("#B5541E", angry=True) + nose(SKIN_D)
            + '<g filter="url(#sh)"><path d="M74,104 Q74,44 128,42 Q182,44 182,104Z" fill="#9AA8B8"/><rect x="72" y="96" width="112" height="14" rx="5" fill="url(#gold)"/>'
              '<path d="M78,82 Q34,64 38,16 Q58,58 90,66Z" fill="#FFF3D6"/><path d="M178,82 Q222,64 218,16 Q198,58 166,66Z" fill="#FFF3D6"/>'
              '<rect x="122" y="44" width="12" height="56" fill="url(#gold)"/></g>')


def witch():
    return (bg("#C9A0FF", "#3A1068") + body("#2A1060")
            + '<path d="M70,110 Q56,190 50,214 Q80,200 84,150 M186,110 Q200,190 206,214 Q176,200 172,150" fill="#E5394F" stroke="#E5394F" stroke-width="20" stroke-linecap="round"/>'
            + head("#B6F2B0", "#4CAF50") + eyes("#7B3FE4", lashes=True) + brows("#3A1068") + nose("#6FBF6A") + smile(False, "#3A1068")
            + '<g filter="url(#sh)"><ellipse cx="128" cy="86" rx="96" ry="18" fill="#2A1060"/>'
              '<path d="M80,84 L136,6 Q150,0 152,18 L176,84Z" fill="#4A1C9A"/><rect x="82" y="68" width="92" height="12" fill="#F5C451"/>'
              '<rect x="120" y="66" width="16" height="16" fill="none" stroke="#3A1068" stroke-width="4"/></g>')


def pharaoh():
    return (bg("#FFE08A", "#1E3A8A") + body("#1E3A8A", "#FFD24A")
            + '<path d="M66,108 L52,226 L104,206 L104,140 L152,140 L152,206 L204,226 L190,108Z" fill="#FFD24A"/>'
            + '<path d="M66,108 L52,226 M78,108 L66,222 M178,108 L190,222 M190,108 L204,226" stroke="#1E3A8A" stroke-width="6"/>'
            + head(BROWN, "#E57373") + eyes() + '<path d="M94,124 l-10,2 M162,124 l10,2" stroke="#111" stroke-width="4"/>'
            + brows("#111") + nose(BROWN_D) + smile(False, "#5A1E10") + '<rect x="120" y="178" width="16" height="30" rx="4" fill="#3A6FB0"/>'
            + '<g filter="url(#sh)"><path d="M66,112 Q66,52 128,48 Q190,52 190,112 Q128,96 66,112Z" fill="url(#gold)"/>'
              '<path d="M76,74 Q128,64 180,74 M70,94 Q128,84 186,94" stroke="#1E3A8A" stroke-width="7" fill="none"/>'
              '<path d="M120,60 Q128,36 136,60Z" fill="#22C55E"/></g>')


def santa():
    return (bg("#9EC3FF", "#1D3F7A") + body("#D81B30") + '<rect x="30" y="226" width="196" height="16" fill="#fff"/>'
            + head(SKIN) + beard("#FFFFFF", long=True) + '<path d="M104,150 Q128,138 152,150 Q140,162 128,156 Q116,162 104,150Z" fill="#F0F0F0"/>'
            + eyes("#2E6BB0") + brows("#fff") + nose(SKIN_D) + '<circle cx="128" cy="140" r="9" fill="#FF8A8A"/>'
            + '<circle cx="92" cy="122" r="17" fill="none" stroke="#C9A66B" stroke-width="4"/><circle cx="164" cy="122" r="17" fill="none" stroke="#C9A66B" stroke-width="4"/>'
            + '<g filter="url(#sh)"><path d="M70,88 Q100,20 180,42 Q212,60 214,120 Q196,70 186,88Z" fill="#D81B30"/>'
              '<rect x="64" y="78" width="128" height="22" rx="11" fill="#fff"/><circle cx="214" cy="122" r="14" fill="#fff"/></g>')


def wizard():
    return (bg("#7FB4FF", "#1A1060") + body("#2E4FB8", "#F5C451")
            + head(SKIN) + beard("#EDEDED", long=True) + eyes("#3A6FB0") + brows("#fff") + nose(SKIN_D)
            + '<g filter="url(#sh)"><ellipse cx="128" cy="90" rx="90" ry="16" fill="#1E3A8A"/>'
              '<path d="M82,90 Q100,40 128,6 Q140,0 146,14 L174,90Z" fill="#2E4FB8"/>'
              '<polygon points="118,50 122,60 132,60 124,66 127,76 118,70 109,76 112,66 104,60 114,60" fill="url(#gold)"/>'
              '<path d="M146,34 a8,8 0 1,0 8,14 a6,6 0 1,1 -8,-14Z" fill="url(#gold)"/></g>')


def leprechaun():
    return (bg("#9CF2B0", "#0E6B3A") + body("#1E8A44", "#FFD24A")
            + head(SKIN) + beard("#E0602E") + eyes("#2E8A44") + brows("#E0602E") + nose(SKIN_D) + smile(True)
            + '<g filter="url(#sh)"><ellipse cx="128" cy="88" rx="84" ry="14" fill="#14612E"/>'
              '<path d="M84,88 L92,24 L164,24 L172,88Z" fill="#1E8A44"/><rect x="88" y="64" width="80" height="14" fill="#1A1A1A"/>'
              '<rect x="116" y="61" width="24" height="20" rx="3" fill="none" stroke="url(#gold)" stroke-width="5"/>'
              '<g transform="translate(172,46)"><circle cx="0" cy="-6" r="6" fill="#7CF29A"/><circle cx="-6" cy="0" r="6" fill="#7CF29A"/><circle cx="6" cy="0" r="6" fill="#7CF29A"/></g></g>')


def mermaid():
    return (bg("#7FF0FF", "#0A6E8A") + body("#2EC4D6")
            + '<path d="M70,112 Q52,40 128,36 Q204,40 186,112 Q206,180 196,232 Q172,210 170,140 L86,140 Q84,210 60,232 Q50,180 70,112Z" fill="#FF5A3A"/>'
            + head(SKIN) + eyes("#0A8AA8", lashes=True) + brows("#C2410C") + nose(SKIN_D) + smile(False, "#E91E63")
            + '<path d="M72,104 Q96,58 164,72 Q120,74 108,100 Q96,84 72,104Z" fill="#FF5A3A"/>'
            + '<g filter="url(#sh)"><path d="M150,70 l8,-14 l8,14 l12,-8 l-4,16Z" fill="#FFB3CF"/><circle cx="164" cy="68" r="5" fill="#fff"/></g>'
            + '<circle cx="100" cy="210" r="7" fill="#fff" opacity=".8"/><circle cx="156" cy="214" r="7" fill="#fff" opacity=".8"/>')


def showgirl():
    return (bg("#FF8AD0", "#7A0E5A") + body("#111111") + '<path d="M60,230 Q128,206 196,230" stroke="url(#gold)" stroke-width="8" fill="none"/>'
            + '<path d="M70,110 Q60,40 128,36 Q196,40 186,110 Q200,150 190,180 Q170,150 168,124 L88,124 Q86,150 66,180 Q56,150 70,110Z" fill="#1A1010"/>'
            + head(TAN) + brows("#1A1010") + nose(TAN_D) + smile(False, "#D81B60")
            + '<g filter="url(#sh)"><rect x="84" y="110" width="88" height="26" rx="13" fill="#111"/>'
              '<ellipse cx="106" cy="123" rx="18" ry="12" fill="#FF3D8B"/><ellipse cx="150" cy="123" rx="18" ry="12" fill="#FF3D8B"/>'
              '<ellipse cx="100" cy="118" rx="6" ry="3" fill="#fff" opacity=".6"/><ellipse cx="144" cy="118" rx="6" ry="3" fill="#fff" opacity=".6"/></g>'
            + '<g filter="url(#sh)"><path d="M128,56 Q100,10 88,30 Q104,40 120,62Z" fill="#FF5FA2"/><path d="M128,56 Q156,10 168,30 Q152,40 136,62Z" fill="#B45CFF"/>'
              '<circle cx="128" cy="62" r="10" fill="url(#gold)"/></g>')


CHARACTERS = [king, queen, pirate, cowboy, viking, witch, pharaoh, santa, wizard, leprechaun, mermaid, showgirl]
NAMES = ["King", "Queen", "Pirate", "Cowboy", "Viking", "Witch", "Pharaoh", "Santa", "Wizard", "Leprechaun", "Mermaid", "Showgirl"]


def main():
    tmp = os.path.join(OUT, "_html")
    os.makedirs(tmp, exist_ok=True)
    for i, f in enumerate(CHARACTERS):
        svg = f'<svg xmlns="http://www.w3.org/2000/svg" width="{S}" height="{S}" viewBox="0 0 {S} {S}">{DEFS}{f()}<rect width="{S}" height="{S}" fill="url(#shine)"/></svg>'
        open(f"{tmp}/a{i:02d}.html", "w").write(f'<!doctype html><html><body style="margin:0">{svg}</body></html>')
    subprocess.run(["node", os.path.join(HERE, "shoot.js"), tmp, str(S), str(S)], check=True)
    for i in range(len(CHARACTERS)):
        Image.open(f"{tmp}/a{i:02d}.png").convert("RGB").save(f"{OUT}/a{i:02d}.webp", "WEBP", quality=90)
    shutil.rmtree(tmp)
    print(f"{len(CHARACTERS)} avatars -> {OUT}")


if __name__ == "__main__":
    main()
