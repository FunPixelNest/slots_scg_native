"""
Switch themes between the original symbols and the new ones in symbols_v2/.

  python3 tools/symbols/apply.py            # use the new symbols for every theme that has a full set
  python3 tools/symbols/apply.py --revert   # go back to the original symbols

Nothing is deleted: the original images stay where they are, and the first switch saves the
original config as theme.original.json next to theme.json (the revert copies it back).
The new symbols are still images, so their blur and animation entries are dropped.
"""
import json, os, shutil, sys

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", ".."))


def theme_dirs():
    base = os.path.join(ROOT, "themes")
    for d in sorted(os.listdir(base)):
        tid = d[len("theme_"):]
        yield tid, os.path.join(base, d, "src/main/assets/themes", tid)


def main(revert=False):
    done, skipped = [], []
    for tid, folder in theme_dirs():
        cfg_p = os.path.join(folder, "theme.json")
        orig_p = os.path.join(folder, "theme.original.json")
        if revert:
            if os.path.exists(orig_p):
                shutil.copyfile(orig_p, cfg_p)
                done.append(tid)
            continue
        src = orig_p if os.path.exists(orig_p) else cfg_p
        cfg = json.load(open(src))
        keys = [s["key"] for s in cfg["symbols"]]
        missing = [k for k in keys if not os.path.exists(os.path.join(folder, "symbols_v2", f"{k}.webp"))]
        if missing:
            skipped.append(f"{tid} (missing {', '.join(missing)})")
            continue
        if not os.path.exists(orig_p):
            shutil.copyfile(cfg_p, orig_p)
        for s in cfg["symbols"]:
            s["image"] = f"symbols_v2/{s['key']}"
            s.pop("blur", None)
            s.pop("anim", None)
        json.dump(cfg, open(cfg_p, "w"), indent=1, ensure_ascii=False)
        done.append(tid)
    print(("reverted: " if revert else "new symbols: ") + ", ".join(done))
    if skipped:
        print("skipped: " + "; ".join(skipped))


if __name__ == "__main__":
    main("--revert" in sys.argv)
