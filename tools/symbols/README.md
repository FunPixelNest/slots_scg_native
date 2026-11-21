# Slot symbols

Every theme now uses original symbols drawn in code (`tools/symbols/themes/<id>.py`, shared parts in `symkit.py`).

- New images: `themes/theme_<id>/src/main/assets/themes/<id>/symbols_v2/<key>.webp`
- Original images: untouched, in their original folders
- Original config backup: `theme.original.json` next to each `theme.json`

Commands (from the repo root):

    python3 tools/symbols/render.py [theme ...]   # redraw symbols (needs Node + Playwright)
    python3 tools/symbols/apply.py                # use the new symbols
    python3 tools/symbols/apply.py --revert       # go back to the original symbols

Contact sheets for review: `tools/symbols/out/<id>.png`, all themes in `tools/symbols/out/all.png`.
