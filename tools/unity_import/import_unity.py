#!/usr/bin/env python3
"""Import the Unity "HolidaySlotAsset" project into the native Android project.

Usage:
    python3 tools/unity_import/import_unity.py \
        --unity /path/to/slots_android_new/Assets/HolidaySlotAsset \
        --out   /path/to/slots_scg_native

What it does
  * Converts every PNG (PSDs are skipped) to WebP, keeping the folder layout
    (names sanitised to lower_snake_case so Android can load them).
      - 0_Common + 1_Lobby  -> app/src/main/assets/common/...
      - each theme          -> themes/theme_<id>/src/main/assets/themes/<id>/...
        (packaged into the app, see app/build.gradle.kts)
  * Reads Unity .meta files to keep sprite-sheet frame rects and 9-slice
    borders (sprites.json per theme).
  * Extracts real slot math (reel strips, paytable, scatter pays) from the
    Unity scenes that have it; other themes use the same template mapped onto
    their own symbol art.
  * Picks background / reel frame / symbol / button / popup art per theme by
    naming convention, overridable in overrides.json.
  * Builds a lobby tile per theme, converts audio to OGG and copies fonts.
"""
from __future__ import annotations

import argparse
import json
import os
import re
import shutil
import subprocess
import sys
from concurrent.futures import ProcessPoolExecutor

from PIL import Image, ImageDraw, ImageFilter, ImageFont

sys.path.insert(0, os.path.dirname(__file__))
from unity_scene import build_guid_index, parse_scene  # noqa: E402

HERE = os.path.dirname(os.path.abspath(__file__))
MAX_DIM = 2048
WEBP_QUALITY = 82

THEME_NAMES = {
    "2_ChristmasEve": ("christmas_eve", "Christmas Eve"),
    "3_ValentinesDay": ("valentines_day", "Valentine's Day"),
    "4_Witch": ("witch", "Witch's Cauldron"),
    "5_Modern": ("modern", "Modern Suits"),
    "6_Forest": ("forest", "Enchanted Forest"),
    "7_NeonTheme": ("neon", "Neon Nights"),
    "8_Underwater": ("underwater", "Underwater"),
    "9_WolfTheme": ("wolf", "Wolf Moon"),
    "10_CandyTheme": ("candy", "Candy Land"),
    "11_Pirates": ("pirates", "Pirates"),
    "12_TropicalTheme": ("tropical", "Tropical"),
    "13_GoldMine": ("gold_mine", "Gold Mine"),
    "14_FarmTheme": ("farm", "Happy Farm"),
    "15_MostWanted": ("most_wanted", "Most Wanted"),
    "16_GemsTheme": ("gems", "Gems"),
    "17_Egypt_1": ("egypt", "Egypt"),
    "18_Egypt_2": ("egypt_2", "Egypt II"),
    "19_ChinaTheme": ("china", "China"),
    "20_GreekTheme": ("greek", "Greek Gods"),
    "21_VikingTheme": ("viking", "Vikings"),
    "22_IndianTheme": ("india", "India"),
    "23_ArabicTheme": ("arabian", "Arabian Nights"),
    "24_WildWestTheme": ("wild_west", "Wild West"),
}


# --------------------------------------------------------------------------- utils


def sanitize(seg: str) -> str:
    s = re.sub(r"[^0-9a-zA-Z]+", "_", seg).strip("_").lower()
    return s or "x"


def sanitize_path(rel: str) -> str:
    parts = rel.replace("\\", "/").split("/")
    name, ext = os.path.splitext(parts[-1])
    return "/".join([sanitize(p) for p in parts[:-1]] + [sanitize(name)])


def read_meta(png_path: str) -> dict:
    """Sprite frames (Unity rects, y from bottom) and 9-slice border."""
    meta = png_path + ".meta"
    res = {"frames": [], "border": None}
    if not os.path.exists(meta):
        return res
    with open(meta, encoding="utf-8", errors="ignore") as fh:
        txt = fh.read()
    m = re.search(r"spriteBorder: \{x: ([\d.]+), y: ([\d.]+), z: ([\d.]+), w: ([\d.]+)\}", txt)
    if m:
        l, b, r, t = (float(v) for v in m.groups())
        if l or b or r or t:
            res["border"] = [l, t, r, b]  # left, top, right, bottom
    if re.search(r"spriteMode: 2", txt):
        for sm in re.finditer(
            r"name: (.+)\n\s+rect:\n\s+serializedVersion: \d+\n\s+x: ([\d.]+)\n\s+y: ([\d.]+)\n"
            r"\s+width: ([\d.]+)\n\s+height: ([\d.]+)", txt):
            res["frames"].append([sm.group(1).strip()] + [float(v) for v in sm.groups()[1:]])
    return res


def convert_one(job):
    src, dst, kind = job
    os.makedirs(os.path.dirname(dst), exist_ok=True)
    try:
        im = Image.open(src)
        im.load()
    except Exception as e:  # corrupt / unsupported
        return src, None, None, str(e)
    orig = im.size
    if im.mode not in ("RGBA", "RGB"):
        im = im.convert("RGBA")
    scale = 1.0
    if max(orig) > MAX_DIM:
        scale = MAX_DIM / max(orig)
        im = im.resize((max(1, round(orig[0] * scale)), max(1, round(orig[1] * scale))), Image.LANCZOS)
    im.save(dst, "WEBP", quality=WEBP_QUALITY, method=4)
    return src, orig, im.size, None


def natural_key(s: str):
    return [int(t) if t.isdigit() else t.lower() for t in re.split(r"(\d+)", s)]


# --------------------------------------------------------------------------- symbols

EXTRA_SYMBOLS = {"bonus", "jackpot", "free_spin", "freespin", "free_spins", "wild_full"}
LOW_CARDS = ["j", "q", "k", "a"]
LOW_OTHER = {"9", "10", "nine", "ten", "heart", "hearts", "diamond", "diamonds", "club", "clubs",
             "spade", "spades", "peaks", "cross", "crosses"}


def classify_symbols(names: list[str]):
    """Return (regular[8 or fewer], wild, scatter) symbol keys, low tier first."""
    keys = {sanitize(n): n for n in names}
    wild = next((k for k in keys if k == "wild"), None)
    scatter = next((k for k in keys if k == "scatter"), None)
    regular = [k for k in keys if k not in (wild, scatter) and k not in EXTRA_SYMBOLS]
    cards = [c for c in LOW_CARDS if c in regular]
    other_low = sorted([k for k in regular if k in LOW_OTHER or k.startswith("rune")], key=natural_key)
    low = (cards + [k for k in other_low if k not in cards])[:4] if len(cards) < 4 else cards
    high = sorted([k for k in regular if k not in low and k not in LOW_OTHER and not k.startswith("rune")],
                  key=natural_key)
    leftovers = sorted([k for k in regular if k not in low and k not in high], key=natural_key)
    while len(low) < 4 and (leftovers or high):
        low.append((leftovers or high).pop(0))
    ordered = low + high + leftovers
    return ordered[:8], wild, scatter


# --------------------------------------------------------------------------- theme art picking


def pick(cands: list[str], include: list[str], exclude: list[str] = (), prefer: list[str] = ()):
    """Pick best matching image path (sanitised, without extension)."""
    best, best_score = None, None
    for c in cands:
        base = c.rsplit("/", 1)[-1]
        if not all(re.search(i, base) for i in include):
            continue
        if any(re.search(e, c) for e in exclude):
            continue
        score = (sum(1 for p in prefer if re.search(p, c)), -c.count("/"), -len(base))
        if best_score is None or score > best_score:
            best, best_score = c, score
    return best


COMMON_EXCLUDE = [r"hover", r"pressed", r"psd", r"_down$", r"night", r"disabled", r"/_?0_"]


def pick_ui(images: list[str], sizes: dict | None = None) -> dict:
    gs = [i for i in images if re.search(r"game_?screen|gui|buttons", i)]
    pu = [i for i in images if re.search(r"pop_?ups?", i)]
    ex = COMMON_EXCLUDE
    ui = {
        "spin": pick(gs, [r"spin"], ex + [r"auto", r"free", r"sheet", r"symbol", r"icon_?spin"], [r"button"]),
        "autoSpin": pick(gs, [r"auto"], ex + [r"stop"], [r"button"]),
        "maxBet": pick(gs, [r"max"], ex, [r"button"]),
        "plus": pick(gs, [r"plus"], ex, [r"button"]),
        "minus": pick(gs, [r"minus"], ex, [r"button"]),
        "menu": pick(gs, [r"menu"], ex + [r"lobby"], [r"button"]),
        "info": pick(gs, [r"info"], ex + [r"panel", r"line", r"plate"], [r"button"]),
        "settings": pick(gs, [r"settings"], ex + [r"panel", r"line", r"plate", r"pop"], [r"button"]),
        "panelBalance": pick(gs, [r"balance|coins"], ex + [r"button", r"icon"], [r"panel"]),
        "panelWin": pick(gs, [r"win"], ex + [r"button", r"line", r"big|mega|huge|sheet|anim"], [r"panel"]),
        "panelBet": pick(gs, [r"bet"], ex + [r"button", r"max"], [r"panel", r"total"]),
        "popupPanel": pick(pu, [r"message|messageplate|small_?message"], ex, [r"panel|plate"]),
        "popupBigWin": pick(pu, [r"big_?win"], ex, [r"panel|plate"]),
        "popupMegaWin": pick(pu, [r"mega"], ex, [r"panel|plate"]),
        "popupHugeWin": pick(pu, [r"huge"], ex, [r"panel|plate"]),
        "popupFreeSpins": pick(pu, [r"free"], ex + [r"symbol"], [r"panel|plate"]),
        "popupJackpot": pick(pu, [r"jackpot"], ex + [r"symbol", r"title", r"icon"], [r"panel|plate|win|pop"]),
        "popupInfo": pick(pu, [r"info|paytable|pay_?table|help"], ex + [r"line|title|button"], [r"panel|plate"]),
        "popupSettings": pick(pu, [r"settings"], ex + [r"line|title|button|icon"], [r"panel|plate|pop"]),
        "buttonLong": pick(pu + gs, [r"long|middle|ok"], ex + [r"icon"], [r"button"]),
        "exit": pick(pu + gs, [r"exit|quit|close"], ex, [r"button"]),
    }
    if sizes:
        # panels behind numbers must be wide strips, not icons or symbols
        for k in ("panelBalance", "panelWin", "panelBet"):
            v = ui.get(k)
            if v and v in sizes and sizes[v][0] < 2 * sizes[v][1]:
                ui[k] = None
    return {k: v for k, v in ui.items() if v}


def pick_background(images: list[str], sizes: dict) -> str | None:
    cands = [i for i in images if re.search(r"(^|/)(background|bkg|bg)[^/]*$", i)
             and not re.search(r"help|pop|fort_?wheel|wheel|anim|symbol|lobby|button|panel", i)]
    land = [c for c in cands if sizes[c][0] >= sizes[c][1]]
    cands = land or cands
    if not cands:
        return None
    def score(c):
        base = c.rsplit("/", 1)[-1]
        return (("night" not in base) + ("darker" not in base) + ("_v" not in base[-3:] and not base.endswith("v")),
                ("day" in base) + ("h" == base[-1]) + ("horizontal" in base) + ("3x5" in base),
                sizes[c][0] * sizes[c][1])
    return max(cands, key=score)


def pick_background_portrait(images: list[str], sizes: dict) -> str | None:
    """Vertical (portrait) background where the Unity theme ships one."""
    cands = [i for i in images if re.search(r"(^|/)(background|bkg|bg)[^/]*$", i)
             and not re.search(r"help|pop|fort_?wheel|wheel|anim|symbol|lobby|button|panel|night", i)
             and sizes[i][1] > sizes[i][0]]
    if not cands:
        return None
    return max(cands, key=lambda c: (("3x5" in c), sizes[c][0] * sizes[c][1]))


def pick_frame(images: list[str], sizes: dict) -> str | None:
    c = [i for i in images if re.search(r"slot_?machine_?3x5|slotmachine3x5", i)]
    if c:
        return sorted(c, key=len)[0]
    c = [i for i in images if re.search(r"(^|/)(reels?|reel_5x3|reel_3x5)$", i) and "button" not in i]
    if c:
        return max(c, key=lambda x: sizes[x][0] * sizes[x][1])
    return None


def find_symbol_dir(theme_rel_images: list[str]) -> str | None:
    dirs = {}
    for i in theme_rel_images:
        d = i.rsplit("/", 1)[0]
        if d.endswith("/symbols") and not re.search(r"blur|pop|psd|paytable", d):
            dirs.setdefault(d, 0)
            dirs[d] += 1
    return max(dirs, key=dirs.get) if dirs else None


def find_blur(images: list[str], sym_key: str) -> str | None:
    for i in images:
        d, base = i.rsplit("/", 1)
        if "blur" in d or "blur" in base:
            b = base.replace("_blurred", "").replace("_blur", "").replace("blurred", "").replace("blur", "")
            if sanitize(b) == sym_key:
                return i
    return None


def find_anim(images: list[str], sprites: dict, sym_key: str):
    """Sprite sheet ('<sym>_sheet' or similar with frames) or a numbered frame folder."""
    for i in images:
        base = i.rsplit("/", 1)[-1]
        if i in sprites and sprites[i].get("frames"):
            stem = re.sub(r"_?(sheet|symb|anim|01_0|_0)$", "", base)
            stem = re.sub(r"_?(sheet|symb)$", "", stem)
            if stem == sym_key or stem == sym_key + "s" or stem.rstrip("s") == sym_key.rstrip("s"):
                return {"sheet": i}
    folders = {}
    for i in images:
        d, base = i.rsplit("/", 1)
        if "anim" in d and d.rsplit("/", 1)[-1].rstrip("s") == sym_key.rstrip("s"):
            folders.setdefault(d, []).append(i)
    for d, files in folders.items():
        # only real frame sequences (name_01, name_02, ...): folders of loose body parts
        # (rigged Unity animations) are not playable as frames
        groups = {}
        for f in files:
            m = re.match(r"^(.*?)_?(\d+)$", f.rsplit("/", 1)[-1])
            if m:
                groups.setdefault(m.group(1), []).append(f)
        seq = max(groups.values(), key=len, default=[])
        if len(seq) >= 4:
            return {"frames": sorted(seq, key=natural_key)}
    return None


# --------------------------------------------------------------------------- lobby tile


def make_tile(theme_out: str, bg: str | None, symbols: list[str], title_img: str | None, name: str, dst: str,
              font_path: str | None):
    W, H = 392, 588  # same card format as the Unity lobby GameIcons
    tile = Image.new("RGBA", (W, H), (20, 20, 40, 255))
    if bg:
        b = Image.open(os.path.join(theme_out, bg + ".webp")).convert("RGBA")
        r = max(W / b.width, H / b.height)
        b = b.resize((int(b.width * r) + 1, int(b.height * r) + 1), Image.LANCZOS)
        tile.paste(b, ((W - b.width) // 2, (H - b.height) // 2))
    shade = Image.new("RGBA", (W, H), (0, 0, 0, 60))
    tile = Image.alpha_composite(tile, shade)
    picks = symbols[:3]
    # one large symbol on top, two below it
    slots = [(W * 0.5, H * 0.27, 200), (W * 0.27, H * 0.58, 150), (W * 0.73, H * 0.58, 150)]
    for n, s in enumerate(picks):
        im = Image.open(os.path.join(theme_out, s + ".webp")).convert("RGBA")
        cx, cy, sz = slots[n]
        im.thumbnail((sz, sz), Image.LANCZOS)
        x = int(cx - im.width / 2)
        y = int(cy - im.height / 2)
        glow = im.split()[3].filter(ImageFilter.GaussianBlur(8))
        g = Image.new("RGBA", im.size, (0, 0, 0, 160))
        g.putalpha(glow)
        tile.alpha_composite(g, (x + 3, y + 5))
        tile.alpha_composite(im, (x, y))
    if title_img:
        t = Image.open(os.path.join(theme_out, title_img + ".webp")).convert("RGBA")
        t.thumbnail((W - 30, 140), Image.LANCZOS)
        tile.alpha_composite(t, ((W - t.width) // 2, H - t.height - 14))
    else:
        d = ImageDraw.Draw(tile)
        d.rectangle([0, H - 64, W, H], fill=(0, 0, 0, 150))
        try:
            font = ImageFont.truetype(font_path, 34) if font_path else ImageFont.load_default()
        except OSError:
            font = ImageFont.load_default()
        tw = d.textlength(name, font=font)
        d.text(((W - tw) / 2 + 2, H - 52 + 2), name, font=font, fill=(0, 0, 0, 200))
        d.text(((W - tw) / 2, H - 52), name, font=font, fill=(255, 220, 120, 255))
    os.makedirs(os.path.dirname(dst), exist_ok=True)
    tile.convert("RGB").save(dst, "WEBP", quality=85)


# --------------------------------------------------------------------------- main


def collect_pngs(folder: str) -> list[str]:
    out = []
    for dp, ds, fs in os.walk(folder):
        ds[:] = [d for d in ds if "psd" not in d.lower()]
        for f in fs:
            if f.lower().endswith(".png"):
                out.append(os.path.join(dp, f))
    return out


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--unity", required=True, help="path to Assets/HolidaySlotAsset")
    ap.add_argument("--out", required=True, help="path to the native project root")
    ap.add_argument("--only", nargs="*", help="limit to these Unity theme folders")
    ap.add_argument("--skip-images", action="store_true", help="reuse already converted images")
    args = ap.parse_args()
    U, O = os.path.abspath(args.unity), os.path.abspath(args.out)

    overrides = json.load(open(os.path.join(HERE, "overrides.json")))
    template = json.load(open(os.path.join(HERE, "math_template.json")))

    common_out = os.path.join(O, "app/src/main/assets/common")
    folders = [f for f in THEME_NAMES if not args.only or f in args.only]

    # ---------------- images
    jobs, owners = [], {}
    def plan(src_folder, out_root, prefix=""):
        for p in collect_pngs(os.path.join(U, src_folder)):
            rel = os.path.relpath(p, os.path.join(U, src_folder))
            key = (prefix + sanitize_path(rel)).strip("/")
            dst = os.path.join(out_root, key + ".webp")
            jobs.append((p, dst, "img"))
            owners[p] = (out_root, key)
    plan("0_Common/Sprites", common_out, "ui/")
    plan("0_Common/Part_confetti", common_out, "fx/confetti/")
    plan("0_Common/Part_stars", common_out, "fx/stars/")
    plan("1_Lobby/Sprites/PNG", common_out, "lobby/")
    theme_out = {}
    for f in folders:
        tid = THEME_NAMES[f][0]
        root = os.path.join(O, f"themes/theme_{tid}/src/main/assets/themes/{tid}")
        theme_out[f] = root
        plan(f, root)

    sizes_by_src = {}
    if args.skip_images:
        for src, dst, _ in jobs:
            if os.path.exists(dst):
                sizes_by_src[src] = (Image.open(src).size, Image.open(dst).size)
    else:
        for f in folders:
            if os.path.isdir(theme_out[f]):
                shutil.rmtree(theme_out[f])
        with ProcessPoolExecutor() as ex:
            for n, (src, orig, new, err) in enumerate(ex.map(convert_one, jobs, chunksize=8)):
                if err:
                    print("skip", src, err, file=sys.stderr)
                    continue
                sizes_by_src[src] = (orig, new)
                if n % 500 == 0:
                    print(f"  images {n}/{len(jobs)}", flush=True)

    # ---------------- sprite metadata (frames / 9-slice)
    def sprite_meta(src):
        if src not in sizes_by_src:
            return None
        m = read_meta(src)
        (ow, oh), (nw, nh) = sizes_by_src[src]
        s = nw / ow
        out = {"w": nw, "h": nh}
        if m["border"]:
            out["border"] = [round(v * s, 1) for v in m["border"]]
        if len(m["frames"]) > 1:
            fr = []
            for name, x, y, w, h in sorted(m["frames"], key=lambda r: natural_key(r[0])):
                fr.append([round(x * s), round((oh - y - h) * s), round(w * s), round(h * s)])
            out["frames"] = fr
        return out if len(out) > 2 else None

    # ---------------- scenes
    guid_index = build_guid_index(U)
    scene_slots = {}
    for dp, _, fs in os.walk(U):
        for fn in fs:
            if fn.endswith(".unity") and "Lobby" not in fn:
                theme_folder = os.path.relpath(dp, U).split(os.sep)[0]
                for s in parse_scene(os.path.join(dp, fn), os.path.join(U, "0_Common/Scripts")):
                    # first (non-Hold) machine wins
                    if theme_folder not in scene_slots or "Hold" in scene_slots[theme_folder][1]:
                        scene_slots[theme_folder] = (s, fn)

    font_for_tiles = None
    for dp, _, fs in os.walk(os.path.join(U, "1_Lobby/Fonts")):
        for fn in fs:
            if fn.upper() == "MONTSERRAT-BLACK.OTF":
                font_for_tiles = os.path.join(dp, fn)

    index = []
    for order, f in enumerate(folders):
        tid, tname = THEME_NAMES[f]
        ov = overrides.get(tid, {})
        root = theme_out[f]
        srcs = [p for p in owners if owners[p][0] == root and p in sizes_by_src]
        key_of = {p: owners[p][1] for p in srcs}
        images = sorted(key_of.values())
        sizes = {key_of[p]: sizes_by_src[p][1] for p in srcs}

        sprites = {}
        for p in srcs:
            m = sprite_meta(p)
            if m:
                sprites[key_of[p]] = m

        sym_dir = ov.get("symbolDir") or find_symbol_dir(images)
        sym_images = {i.rsplit("/", 1)[-1]: i for i in images if i.rsplit("/", 1)[0] == sym_dir}

        source = "generated"
        if f in scene_slots:
            s, _ = scene_slots[f]
            keys = []
            for g in s.icon_guids:
                p = guid_index.get(g, "")
                keys.append(sanitize(os.path.splitext(os.path.basename(p))[0]))
            regular = [k for i, k in enumerate(keys) if i not in (s.wild_id, s.scatter_id)]
            wild = keys[s.wild_id] if 0 <= s.wild_id < len(keys) else None
            scatter = keys[s.scatter_id] if 0 <= s.scatter_id < len(keys) else None
            math = {"reelStrips": s.reel_strips, "payTable": s.pay_table, "scatterPays": s.scatter_pays,
                    "wildInFirstPosition": s.wild_in_first_position,
                    "wildSubstitute": s.wild_substitute,
                    "bet": {"defaultLineBet": s.def_line_bet, "maxLineBet": s.max_line_bet}}
            order_keys = keys
            source = f"unity-scene:{scene_slots[f][1]}"
        else:
            regular, wild, scatter = classify_symbols(list(sym_images))
            regular = ov.get("regular", regular)
            wild = ov.get("wild", wild)
            scatter = ov.get("scatter", scatter)
            order_keys = regular + [wild, scatter]
            math = json.loads(json.dumps(template))

        # map template ids onto available symbols
        n_reg = len(regular)
        tmpl_wild, tmpl_scat = 8, 9
        id_map = {}
        for i in range(8):
            id_map[i] = i if i < n_reg else i % max(1, n_reg)
        id_map[tmpl_wild] = n_reg if wild else None
        id_map[tmpl_scat] = (n_reg + (1 if wild else 0)) if scatter else None
        if f not in scene_slots:
            strips = []
            for strip in math["reelStrips"]:
                st = []
                for v in strip:
                    m = id_map.get(v)
                    st.append(m if m is not None else 0)
                strips.append(st)
            math["reelStrips"] = strips
            pt = []
            for p in math["payTable"]:
                line = [(id_map.get(v) if v >= 0 else -1) for v in p["line"]]
                if any(v is None for v in line):
                    continue
                pt.append({**p, "line": line})
            math["payTable"] = pt
            if not scatter:
                math["scatterPays"] = []
            math["wildSubstitute"] = [True] * n_reg + ([False] if wild else []) + ([False] if scatter else [])
            order_keys = regular + ([wild] if wild else []) + ([scatter] if scatter else [])

        symbols = []
        for k in order_keys:
            img = sym_images.get(k)
            if not img:
                print(f"!! {tid}: symbol image '{k}' not found in {sym_dir}", file=sys.stderr)
            sym = {"key": k, "name": k.replace("_", " ").title(), "image": img}
            b = find_blur(images, k)
            if b:
                sym["blur"] = b
            a = ov.get("anims", {}).get(k) or find_anim(images, sprites, k)
            if a:
                sym["anim"] = a
            symbols.append(sym)

        wild_id = order_keys.index(wild) if wild in order_keys else -1
        scatter_id = order_keys.index(scatter) if scatter in order_keys else -1
        bg = ov.get("background") or pick_background(images, sizes)
        bg_portrait = ov.get("backgroundPortrait") or pick_background_portrait(images, sizes)
        frame = ov.get("frame", pick_frame(images, sizes))
        reel_bg = ov.get("reelBackground")
        if frame and frame in sizes and sizes[frame][0] < sizes[frame][1] * 0.8:
            # a single reel column, not a whole 5x3 frame: draw it behind every reel
            reel_bg, frame = frame, None
        title = ov["title"] if "title" in ov else next((i for i in images if re.search(r"(game_?title|logo)$", i)), None)
        ui = pick_ui(images, sizes)
        ui.update(ov.get("ui", {}))

        theme = {
            "id": tid, "name": tname, "order": order, "unityFolder": f, "source": source,
            "reels": 5, "rows": 3, "mode": "ways",
            "background": bg, "backgroundPortrait": bg_portrait, "frame": frame, "reelBackground": reel_bg, "title": title,
            "symbols": symbols, "wildId": wild_id, "scatterId": scatter_id,
            **math,
            "ui": ui,
            "uiLabeled": ov.get("uiLabeled", []),
            "fonts": ov.get("fonts", []),
        }
        with open(os.path.join(root, "theme.json"), "w") as fh:
            json.dump(theme, fh, indent=1)
        with open(os.path.join(root, "sprites.json"), "w") as fh:
            json.dump(sprites, fh, separators=(",", ":"))
        with open(os.path.join(root, "images.json"), "w") as fh:
            json.dump(images, fh, separators=(",", ":"))

        tile_rel = f"lobby/tiles/{tid}.webp"
        icon_src = ov.get("lobbyIcon")
        if icon_src:
            src_path = os.path.join(U, icon_src)
            im = Image.open(src_path).convert("RGBA")
            im.thumbnail((480, 480), Image.LANCZOS)
            os.makedirs(os.path.join(common_out, "lobby/tiles"), exist_ok=True)
            im.save(os.path.join(common_out, tile_rel), "WEBP", quality=88)
        else:
            make_tile(root, bg, [s["image"] for s in symbols if s["image"]][4:7] or
                      [s["image"] for s in symbols if s["image"]], title, tname,
                      os.path.join(common_out, tile_rel), font_for_tiles)
        size_bytes = sum(os.path.getsize(os.path.join(dp, x)) for dp, _, fs in os.walk(root) for x in fs)
        # image keys never carry the extension (the app appends ".webp")
        index.append({"id": tid, "name": tname, "order": order, "tile": tile_rel[:-len(".webp")],
                      "pack": f"theme_{tid}", "sizeBytes": size_bytes, "source": source})
        print(f"theme {tid:16s} symbols={len(symbols)} wild={wild} scatter={scatter} bg={bool(bg)} "
              f"frame={bool(frame)} ui={len(ui)} {source}")

    if not args.only:
        with open(os.path.join(common_out, "themes.json"), "w") as fh:
            json.dump(index, fh, indent=1)

        # ---------------- audio + fonts
        audio_out = os.path.join(common_out, "audio")
        os.makedirs(audio_out, exist_ok=True)
        for dp, _, fs in os.walk(U):
            for fn in fs:
                if fn.lower().endswith(".wav"):
                    dst = os.path.join(audio_out, sanitize(os.path.splitext(fn)[0]) + ".ogg")
                    subprocess.run(["ffmpeg", "-y", "-loglevel", "error", "-i", os.path.join(dp, fn),
                                    "-c:a", "libvorbis", "-q:a", "4", dst], check=True)
        font_out = os.path.join(O, "app/src/main/res/font")
        os.makedirs(font_out, exist_ok=True)
        for dp, _, fs in os.walk(U):
            for fn in fs:
                if fn.lower().endswith((".ttf", ".otf")):
                    name, ext = os.path.splitext(fn)
                    shutil.copy(os.path.join(dp, fn), os.path.join(font_out, sanitize(name) + ext.lower()))


if __name__ == "__main__":
    main()
