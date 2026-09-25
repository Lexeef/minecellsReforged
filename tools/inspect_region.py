"""Prints block palettes and structure data of saved chunks: inspect_region.py <region_dir> <chunkX> <chunkZ> [minSectionY maxSectionY]."""
import io
import os
import struct
import sys
import zlib


def read_tag(buf, tag_type):
    if tag_type == 1:
        return struct.unpack(">b", buf.read(1))[0]
    if tag_type == 2:
        return struct.unpack(">h", buf.read(2))[0]
    if tag_type == 3:
        return struct.unpack(">i", buf.read(4))[0]
    if tag_type == 4:
        return struct.unpack(">q", buf.read(8))[0]
    if tag_type == 5:
        return struct.unpack(">f", buf.read(4))[0]
    if tag_type == 6:
        return struct.unpack(">d", buf.read(8))[0]
    if tag_type == 7:
        n = struct.unpack(">i", buf.read(4))[0]
        return buf.read(n)
    if tag_type == 8:
        n = struct.unpack(">H", buf.read(2))[0]
        return buf.read(n).decode("utf-8", "replace")
    if tag_type == 9:
        inner = buf.read(1)[0]
        n = struct.unpack(">i", buf.read(4))[0]
        return [read_tag(buf, inner) for _ in range(n)]
    if tag_type == 10:
        out = {}
        while True:
            t = buf.read(1)[0]
            if t == 0:
                return out
            name = read_tag(buf, 8)
            out[name] = read_tag(buf, t)
    if tag_type == 11:
        n = struct.unpack(">i", buf.read(4))[0]
        return struct.unpack(f">{n}i", buf.read(4 * n))
    if tag_type == 12:
        n = struct.unpack(">i", buf.read(4))[0]
        return struct.unpack(f">{n}q", buf.read(8 * n))
    raise ValueError(tag_type)


def load_chunk(region_dir, cx, cz):
    path = os.path.join(region_dir, f"r.{cx >> 5}.{cz >> 5}.mca")
    if not os.path.isfile(path):
        return None
    with open(path, "rb") as fh:
        data = fh.read()
    idx = 4 * ((cx & 31) + (cz & 31) * 32)
    loc = struct.unpack(">I", data[idx:idx + 4])[0]
    offset, count = loc >> 8, loc & 0xFF
    if offset == 0:
        return None
    start = offset * 4096
    length = struct.unpack(">I", data[start:start + 4])[0]
    comp = data[start + 4]
    raw = data[start + 5:start + 4 + length]
    raw = zlib.decompress(raw) if comp == 2 else raw
    buf = io.BytesIO(raw)
    t = buf.read(1)[0]
    read_tag(buf, 8)
    return read_tag(buf, t)


def main():
    region_dir, cx, cz = sys.argv[1], int(sys.argv[2]), int(sys.argv[3])
    lo, hi = (int(sys.argv[4]), int(sys.argv[5])) if len(sys.argv) > 5 else (-100, 100)
    chunk = load_chunk(region_dir, cx, cz)
    if chunk is None:
        print(f"chunk {cx},{cz}: not saved")
        return
    print(f"chunk {cx},{cz}: status={chunk.get('Status')}")
    structures = chunk.get("structures", {})
    starts = structures.get("starts", {})
    for key, start in starts.items():
        if start.get("id") != "INVALID":
            print(f"  start {key}: pieces={len(start.get('Children', []))}")
    for key, refs in structures.get("References", {}).items():
        print(f"  refs {key}: {len(refs)}")
    for section in chunk.get("sections", []):
        y = section.get("Y")
        if y is None or not lo <= y <= hi:
            continue
        palette = section.get("block_states", {}).get("palette", [])
        names = sorted({p.get("Name") for p in palette})
        air = "minecraft:air" in names or "minecraft:cave_air" in names
        print(f"  section y={y} ({y * 16}..{y * 16 + 15}): {len(names)} block types, air={air}: {names[:12]}{' ...' if len(names) > 12 else ''}")


if __name__ == "__main__":
    main()
