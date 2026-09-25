"""Regenerates Mine Cells template pools from the Fabric 2.0.0 datagen (ModTemplatePools.kt) definitions."""
import json
import os
import sys

ROOT = os.path.join(os.path.dirname(__file__), "..", "src", "main", "resources", "data", "minecells")
POOL_DIR = os.path.join(ROOT, "worldgen", "template_pool")
NBT_DIR = os.path.join(ROOT, "structures")

pools = {}


def full(prefix, name):
    return name if ":" in name else f"{prefix}/{name}"


def element(location, processors, terrain):
    if location == "minecraft:empty":
        return {"element_type": "minecraft:empty_pool_element"}
    return {
        "element_type": "minecraft:single_pool_element",
        "location": location,
        "processors": processors or "minecraft:empty",
        "projection": "terrain_matching" if terrain else "rigid",
    }


def add(pool_id, entries, processors=None, terrain=False):
    pools[pool_id] = [(w, element(loc, processors, terrain)) for loc, w in entries]


def single(pool_id, processors=None, terrain=False):
    add(pool_id, [(pool_id, 1)], processors, terrain)


def prefixed(pool_id, pairs, processors=None, terrain=False):
    merged = {}
    for name, w in pairs:
        merged[name] = merged.get(name, 0) + w
    add(pool_id, [(full(pool_id, n), w) for n, w in merged.items()], processors, terrain)


def indexed(pool_id, *weights, processors=None, terrain=False):
    add(pool_id, [(f"{pool_id}/{i}", w) for i, w in enumerate(weights)], processors, terrain)


# common
prefixed("minecells:common/corpse", [("skeleton", 1), ("rotting_corpse", 1), ("corpse", 1)])
prefixed("minecells:common/hanged_corpse", [("skeleton", 1), ("rotting_corpse", 1), ("corpse", 1)])
prefixed("minecells:common/standing_cage", [("cage", 1), ("broken_cage", 1)])
single("minecells:common/support_beam")

# prisoners' quarters
p, path = "minecells:prison/main", "minecells:better_prison"
single(f"{path}/spawn/spawn", p)
single(f"{path}/spawn/outside_near", p)
single(f"{path}/spawn/outside_far", p)
prefixed(f"{path}/stairs", [("centered", 1), ("hole", 1), ("spiral", 1)], p)
prefixed(f"{path}/straight", [("simple", 2), ("room", 3), ("hole", 1), ("platform", 1), ("door", 2)], p)
prefixed(f"{path}/straight/room/layout", [("storage", 1), ("cells", 1), ("towers", 1)], p)
prefixed(f"{path}/turn", [("big_doors", 1), ("small_doors", 1), ("pillar_room", 1), ("hole", 1)], p)
prefixed(f"{path}/curve", [("simple", 1)], p)
prefixed(f"{path}/terminal", [("canteen", 1), ("storage", 2), ("cells", 4)], p)
single(f"{path}/promenade")

# promenade of the condemned
p = "minecells:promenade"
for s in ("spawn", "stairs/spawn", "stairs/end", "end/bottom", "end/top"):
    single(f"minecells:promenade/{s}", p)
indexed("minecells:promenade/chain_pile", 1, 2, 2, 2, terrain=True)
indexed("minecells:promenade/gallows", 1, 2, 2, 2, processors=p)
single("minecells:promenade/king_statue", p, True)
indexed("minecells:promenade/overground_end", 1, 2, 2, processors=p)
indexed("minecells:promenade/overground", 2, 1, 2, 2, 1, processors=p)
indexed("minecells:promenade/overground_top", 1, 2, 3, 1, 2, 2, 2, 2, processors=p)
indexed("minecells:promenade/underground", 3, 3, 2, 2, 3, 4, processors=p)
indexed("minecells:promenade/underground_end", 1, processors=p)
single("minecells:promenade/overground_elevator", p)
single("minecells:promenade/overground_base", p)
for s in ("underground", "bottom", "middle", "top"):
    single(f"minecells:promenade/border_wall/{s}", p)
indexed("minecells:promenade/wall_segment", 28, 2, 2, 1, 1, 1, processors=p)
pp = "minecells:promenade/path"
indexed("minecells:promenade/path/straight", 1, 1, 1, processors=pp, terrain=True)
indexed("minecells:promenade/path/turn", 1, 2, processors=pp, terrain=True)
single("minecells:promenade/path/half", pp, True)
single("minecells:promenade/path/crossroads", pp, True)
for s in ("path/crossroads_post", "path/post/before_crossroads", "path/post/after_crossroads", "path/post/vine_rune"):
    single(f"minecells:promenade/{s}", p)
pools["minecells:promenade/path/building"] = [
    (3, element("minecells:promenade/path/building/0", p, False)),
    (2, element("minecells:promenade/path/building/1", p, True)),
    (1, element("minecells:promenade/overground_top/7", p, False)),
]
single("minecells:promenade/special/vine_rune", p)

# insufferable crypt
p = "minecells:brick_decay"
for s in ("spawn", "elevator_shaft", "boss_room"):
    single(f"minecells:insufferable_crypt/{s}", p)

# ramparts
p = "minecells:promenade"
for s in ("spawn", "spawn_end", "base", "bottom", "bottom_end", "end"):
    single(f"minecells:ramparts/{s}", p)
prefixed("minecells:ramparts/pole", [("0", 1), ("1", 1), ("2", 2), ("3", 1), ("minecraft:empty", 4)])
indexed("minecells:ramparts/elevator_shaft", 4, 1, 4, 2, processors=p)
prefixed("minecells:ramparts/top", [
    ("flat", 6), ("floating_platforms", 2), ("room", 3), ("shooting_range", 2),
    ("two_sweepers", 1), ("wooden_over_spikes", 2), ("wooden_platform", 3)], p)
indexed("minecells:ramparts/top_entry", 1, 1, processors=p)
indexed("minecells:ramparts/room_entry", 1, 1, processors=p)
indexed("minecells:ramparts/room_exit", 1, processors=p)
indexed("minecells:ramparts/room_end", 1, processors=p)
prefixed("minecells:ramparts/secret_room", [("0", 1), ("1", 1), ("empty", 20)], p)
prefixed("minecells:ramparts/room", [
    ("corridor", 7), ("stacked_corridor", 5), ("corridor_with_alcove", 5),
    ("stacked_corridor", 5), ("gated_corridor", 3), ("wooden_see_through", 2)], p)
indexed("minecells:ramparts/tower/room", 3, 3, 3, 1, 2, processors=p)
indexed("minecells:ramparts/tower/top", 1, 1, processors=p)
for s in ("tower/bottom", "tower/base", "end_tower/entrance", "end_tower/elevator_shaft", "end_tower/exit"):
    single(f"minecells:ramparts/{s}", p)
prefixed("minecells:ramparts/tower/entry_room", [
    ("double", 3), ("double_bookshelves", 2), ("ranged_platforms", 2), ("minecells:ramparts/tower/room/3", 1)], p)
prefixed("minecells:ramparts/platform", [("bridge", 1), ("broken_bridge", 1), ("islands", 1)], p)
prefixed("minecells:ramparts/platform_up", [("bridge", 1), ("islands", 1)], p)

# black bridge
for half in ("bottom", "top"):
    for i in range(4):
        single(f"minecells:black_bridge/{half}{i}", p)


def canon(obj):
    return json.dumps(obj, sort_keys=True)


missing_nbt, created, changed = [], [], []
for pool_id, entries in pools.items():
    for _, el in entries:
        loc = el.get("location")
        if loc and not os.path.isfile(os.path.join(NBT_DIR, loc.split(":", 1)[1] + ".nbt")):
            missing_nbt.append(loc)
    data = {
        "name": pool_id,
        "fallback": "minecraft:empty",
        "elements": [{"weight": w, "element": el} for w, el in entries],
    }
    target = os.path.join(POOL_DIR, pool_id.split(":", 1)[1] + ".json")
    if os.path.isfile(target):
        with open(target, encoding="utf-8-sig") as fh:
            old = json.load(fh)
        old_set = sorted(canon(e) for e in old.get("elements", []))
        new_set = sorted(canon(e) for e in data["elements"])
        if old_set == new_set and old.get("fallback") == data["fallback"]:
            continue
        changed.append(pool_id)
        if "--overwrite" not in sys.argv:
            continue
    else:
        created.append(pool_id)
    if "--write" in sys.argv:
        os.makedirs(os.path.dirname(target), exist_ok=True)
        with open(target, "w", encoding="utf-8", newline="\n") as fh:
            json.dump(data, fh, indent=2)
            fh.write("\n")

print("missing nbt:", missing_nbt)
print("created:", created)
print("changed:", changed)
