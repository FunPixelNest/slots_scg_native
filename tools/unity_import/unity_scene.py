"""Extract slot-machine configuration from Unity scene (.unity) YAML files.

Unity serialises MonoBehaviours as YAML documents separated by `--- !u!114 &<fileID>`.
We only need a handful of fields from SlotController, SlotGroupBehavior and
SlotControls, so this is a small targeted parser rather than a full YAML loader
(Unity's YAML uses custom tags that standard loaders reject).
"""
from __future__ import annotations

import os
import re
import struct
from dataclasses import dataclass, field

DOC_RE = re.compile(r"^--- !u!(\d+) &(-?\d+)", re.M)


def _hex_ints(s: str) -> list[int]:
    """Unity stores int arrays as little-endian 32-bit hex blobs."""
    s = s.strip()
    out = []
    for i in range(0, len(s), 8):
        out.append(struct.unpack("<i", bytes.fromhex(s[i:i + 8]))[0])
    return out


def build_guid_index(root: str) -> dict[str, str]:
    """Map asset GUID -> asset path (relative to root) using .meta files."""
    idx: dict[str, str] = {}
    for dirpath, _, files in os.walk(root):
        for f in files:
            if not f.endswith(".meta"):
                continue
            p = os.path.join(dirpath, f)
            try:
                with open(p, encoding="utf-8", errors="ignore") as fh:
                    for line in fh:
                        if line.startswith("guid:"):
                            idx[line.split()[1].strip()] = os.path.relpath(p[:-5], root)
                            break
            except OSError:
                pass
    return idx


def _script_guid(scripts_root: str, name: str) -> str | None:
    for dirpath, _, files in os.walk(scripts_root):
        if name + ".cs.meta" in files:
            with open(os.path.join(dirpath, name + ".cs.meta")) as fh:
                for line in fh:
                    if line.startswith("guid:"):
                        return line.split()[1].strip()
    return None


@dataclass
class SceneSlot:
    machine_id: str
    icon_guids: list[str]
    icon_blur_guids: list[str]
    wild_substitute: list[bool]
    pay_table: list[dict]
    scatter_pays: list[dict]
    wild_id: int
    scatter_id: int
    use_wild: bool
    use_scatter: bool
    wild_in_first_position: bool
    reel_strips: list[list[int]] = field(default_factory=list)
    max_line_bet: int = 20
    def_line_bet: int = 1


def _split_docs(text: str) -> dict[str, str]:
    docs = {}
    matches = list(DOC_RE.finditer(text))
    for i, m in enumerate(matches):
        end = matches[i + 1].start() if i + 1 < len(matches) else len(text)
        docs[m.group(2)] = text[m.start():end]
    return docs


def _field(doc: str, name: str, default=None):
    m = re.search(rf"^  {name}: (.*)$", doc, re.M)
    return m.group(1).strip() if m else default


def _parse_list_block(doc: str, name: str) -> list[str]:
    """Return raw text of each `- ` item of a top-level list field."""
    m = re.search(rf"^  {name}:\s*\n((?:  [- ].*\n)*)", doc, re.M)
    if not m:
        return []
    block = m.group(1)
    items, cur = [], []
    for line in block.splitlines():
        if line.startswith("  - "):
            if cur:
                items.append("\n".join(cur))
            cur = [line]
        elif line.startswith("    ") and cur:
            cur.append(line)
        else:
            break
    if cur:
        items.append("\n".join(cur))
    return items


def _item_field(item: str, name: str):
    m = re.search(rf"(?:^  - |^    ){name}: (.*)$", item, re.M)
    return m.group(1).strip() if m else None


def _guid(ref: str | None) -> str | None:
    if not ref:
        return None
    m = re.search(r"guid: ([0-9a-f]{32})", ref)
    return m.group(1) if m else None


def parse_scene(scene_path: str, scripts_root: str) -> list[SceneSlot]:
    with open(scene_path, encoding="utf-8", errors="ignore") as fh:
        text = fh.read()
    docs = _split_docs(text)
    g_ctrl = _script_guid(scripts_root, "SlotController")
    g_group = _script_guid(scripts_root, "SlotGroupBehavior")
    g_controls = _script_guid(scripts_root, "SlotControls")

    def docs_of(guid):
        return {k: d for k, d in docs.items() if f"m_Script: {{fileID: 11500000, guid: {guid}" in d}

    slots: list[SceneSlot] = []
    controls_docs = docs_of(g_controls)
    for doc in docs_of(g_ctrl).values():
        icons = _parse_list_block(doc, "slotIcons")
        pay = []
        for it in _parse_list_block(doc, "payTable"):
            line = _hex_ints(_item_field(it, "line") or "")
            pay.append({
                "line": line[:5] + [-1] * (5 - len(line[:5])),
                "pay": int(_item_field(it, "pay") or 0),
                "freeSpins": int(_item_field(it, "freeSpins") or 0),
                "payMult": int(_item_field(it, "payMult") or 1),
                "freeSpinsMult": int(_item_field(it, "freeSpinsMult") or 1),
            })
        scat = []
        for it in _parse_list_block(doc, "scatterPayTable"):
            scat.append({
                "count": int(_item_field(it, "scattersCount") or 0),
                "pay": int(_item_field(it, "pay") or 0),
                "freeSpins": int(_item_field(it, "freeSpins") or 0),
                "payMult": int(_item_field(it, "payMult") or 1),
                "freeSpinsMult": int(_item_field(it, "freeSpinsMult") or 1),
            })
        slot = SceneSlot(
            machine_id=_field(doc, "machineID", ""),
            icon_guids=[_guid(_item_field(i, "iconSprite")) for i in icons],
            icon_blur_guids=[_guid(_item_field(i, "iconBlur")) for i in icons],
            wild_substitute=[(_item_field(i, "useWildSubstitute") or "0") == "1" for i in icons],
            pay_table=pay,
            scatter_pays=scat,
            wild_id=int(_field(doc, "wild_id", -1)),
            scatter_id=int(_field(doc, "scatter_id", -1)),
            use_wild=_field(doc, "useWild", "0") == "1",
            use_scatter=_field(doc, "useScatter", "0") == "1",
            wild_in_first_position=_field(doc, "useWildInFirstPosition", "0") == "1",
        )
        # reel groups, in the order referenced by slotGroupsBeh
        group_ids = re.findall(r"\{fileID: (-?\d+)\}", "\n".join(_parse_list_block(doc, "slotGroupsBeh")))
        for gid in group_ids:
            gdoc = docs.get(gid)
            if gdoc and g_group in gdoc:
                slot.reel_strips.append(_hex_ints(_field(gdoc, "symbOrder", "")))
        if not slot.reel_strips:  # fall back: every group in the scene, file order
            for gdoc in docs_of(g_group).values():
                slot.reel_strips.append(_hex_ints(_field(gdoc, "symbOrder", "")))
        for cdoc in controls_docs.values():
            slot.max_line_bet = int(_field(cdoc, "maxLineBet", slot.max_line_bet))
            slot.def_line_bet = int(_field(cdoc, "defLineBet", slot.def_line_bet))
        slots.append(slot)
    return slots


if __name__ == "__main__":
    import json
    import sys
    root = sys.argv[1]
    scripts = os.path.join(root, "0_Common", "Scripts")
    guids = build_guid_index(root)
    for dirpath, _, files in os.walk(root):
        for f in files:
            if f.endswith(".unity"):
                for s in parse_scene(os.path.join(dirpath, f), scripts):
                    d = s.__dict__.copy()
                    d["icons"] = [guids.get(g) for g in s.icon_guids]
                    del d["icon_guids"], d["icon_blur_guids"]
                    print(f, json.dumps(d))
