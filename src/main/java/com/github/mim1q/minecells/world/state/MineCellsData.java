package com.github.mim1q.minecells.world.state;

import com.github.mim1q.minecells.network.s2c.SyncMineCellsPlayerDataS2CPacket;
import com.github.mim1q.minecells.util.MathUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MineCellsData extends SavedData {
    private static final String DATA_NAME = "minecells_data";

    public final Map<Integer, RunData> runs = new HashMap<>();
    private final List<OwnedRun> ownedRuns = new ArrayList<>();

    public static MineCellsData get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(MineCellsData::load, MineCellsData::new, DATA_NAME);
    }

    public static MineCellsData load(CompoundTag tag) {
        MineCellsData data = new MineCellsData();
        if (tag == null || tag.isEmpty()) {
            return data;
        }
        CompoundTag runsTag = tag.getCompound("runs");
        for (String id : runsTag.getAllKeys()) {
            try {
                int key = Integer.parseInt(id);
                data.runs.put(key, new RunData(runsTag.getCompound(id), data));
            } catch (NumberFormatException ignored) {
                // Skip corrupt keys; SavedData root must remain a valid compound.
            }
        }
        ListTag ownedRunsTag = tag.getList("OwnedRuns", Tag.TAG_COMPOUND);
        for (int i = 0; i < ownedRunsTag.size(); i++) {
            CompoundTag runTag = ownedRunsTag.getCompound(i);
            if (runTag.hasUUID("Owner")) {
                data.ownedRuns.add(new OwnedRun(runTag.getUUID("Owner"), BlockPos.of(runTag.getLong("RunCenter"))));
            }
        }
        return data;
    }

    /**
     * Personal run center of the player, allocated on a 1024-block spiral (Fabric {@code PortalsCC.getOrCreatePortal}).
     */
    public BlockPos getOrCreatePlayerRunCenter(ServerPlayer player) {
        UUID owner = player.getUUID();
        for (OwnedRun run : ownedRuns) {
            if (run.owner().equals(owner)) {
                return run.runCenter();
            }
        }
        Vec3i spiral = MathUtils.getSpiralPosition(ownedRuns.size());
        BlockPos center = new BlockPos(spiral.getX() * 1024, 0, spiral.getZ() * 1024);
        ownedRuns.add(new OwnedRun(owner, center));
        setDirty();
        return center;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        CompoundTag runsTag = new CompoundTag();
        for (Map.Entry<Integer, RunData> entry : runs.entrySet()) {
            runsTag.put(entry.getKey().toString(), entry.getValue().save(new CompoundTag()));
        }
        tag.put("runs", runsTag);
        ListTag ownedRunsTag = new ListTag();
        for (OwnedRun run : ownedRuns) {
            CompoundTag runTag = new CompoundTag();
            runTag.putUUID("Owner", run.owner());
            runTag.putLong("RunCenter", run.runCenter().asLong());
            ownedRunsTag.add(runTag);
        }
        tag.put("OwnedRuns", ownedRunsTag);
        return tag;
    }

    private record OwnedRun(UUID owner, BlockPos runCenter) {
    }

    public static PlayerData getPlayerData(ServerPlayer player, ServerLevel level, BlockPos posOverride) {
        BlockPos pos = posOverride != null ? posOverride : player.blockPosition();
        return get(level).getRun(pos).getPlayerData(player);
    }

    public static void syncCurrentPlayerData(ServerPlayer player, ServerLevel level) {
        SyncMineCellsPlayerDataS2CPacket.send(player, new PlayerSpecificMineCellsData(get(level), player));
    }

    public RunData getRun(int x, int z) {
        for (RunData run : runs.values()) {
            if (run.x == x && run.z == z) {
                return run;
            }
        }
        RunData run = new RunData(x, z, this);
        runs.put(runs.size(), run);
        setDirty();
        return run;
    }

    public RunData getRun(BlockPos pos) {
        return getRun(Math.round(pos.getX() / 1024F), Math.round(pos.getZ() / 1024F));
    }

    public static class RunData {
        public final int x;
        public final int z;
        public final Map<UUID, PlayerData> players = new HashMap<>();
        private final SavedData parent;

        public RunData(int x, int z, SavedData parent) {
            this.x = x;
            this.z = z;
            this.parent = parent;
        }

        public RunData(CompoundTag tag, SavedData parent) {
            this.x = tag.getInt("X");
            this.z = tag.getInt("Z");
            this.parent = parent;
            CompoundTag playersTag = tag.getCompound("Players");
            for (String uuid : playersTag.getAllKeys()) {
                players.put(UUID.fromString(uuid), new PlayerData(playersTag.getCompound(uuid), parent));
            }
        }

        public CompoundTag save(CompoundTag tag) {
            tag.putInt("X", x);
            tag.putInt("Z", z);
            CompoundTag playersTag = new CompoundTag();
            for (Map.Entry<UUID, PlayerData> entry : players.entrySet()) {
                playersTag.put(entry.getKey().toString(), entry.getValue().save(new CompoundTag()));
            }
            tag.put("Players", playersTag);
            return tag;
        }

        public PlayerData getPlayerData(ServerPlayer player) {
            PlayerData existing = players.get(player.getUUID());
            if (existing != null) {
                return existing;
            }
            PlayerData created = new PlayerData(new CompoundTag(), parent);
            players.put(player.getUUID(), created);
            parent.setDirty();
            return created;
        }
    }

    public static class PlayerData {
        public static final PlayerData EMPTY = new PlayerData(new CompoundTag(), null);

        public final Map<ResourceLocation, List<BlockPos>> activatedSpawnerRunes = new HashMap<>();
        public final List<PortalData> portals = new ArrayList<>();
        private final SavedData parent;

        public PlayerData(CompoundTag tag, SavedData parent) {
            this.parent = parent;
            CompoundTag runeTag = tag.getCompound("ActivatedSpawnerRunes");
            for (String id : runeTag.getAllKeys()) {
                ResourceLocation key = ResourceLocation.tryParse(id);
                if (key == null) {
                    continue;
                }
                long[] positions = runeTag.getLongArray(id);
                List<BlockPos> list = new ArrayList<>(positions.length);
                for (long packed : positions) {
                    list.add(BlockPos.of(packed));
                }
                activatedSpawnerRunes.put(key, list);
            }
            CompoundTag portalsTag = tag.getCompound("Portals");
            for (String id : portalsTag.getAllKeys()) {
                portals.add(PortalData.fromTag(portalsTag.getCompound(id)));
            }
        }

        public CompoundTag save(CompoundTag tag) {
            CompoundTag runeTag = new CompoundTag();
            for (Map.Entry<ResourceLocation, List<BlockPos>> entry : activatedSpawnerRunes.entrySet()) {
                runeTag.putLongArray(entry.getKey().toString(), entry.getValue().stream().mapToLong(BlockPos::asLong).toArray());
            }
            tag.put("ActivatedSpawnerRunes", runeTag);
            CompoundTag portalsTag = new CompoundTag();
            for (int i = 0; i < portals.size(); i++) {
                portalsTag.put(Integer.toString(i), portals.get(i).save(new CompoundTag()));
            }
            tag.put("Portals", portalsTag);
            return tag;
        }

        public void addActivatedSpawnerRune(ResourceLocation dimensionId, BlockPos pos) {
            activatedSpawnerRunes.computeIfAbsent(dimensionId, ignored -> new ArrayList<>()).add(pos);
            if (parent != null) {
                parent.setDirty();
            }
        }

        public boolean hasActivatedSpawnerRune(ResourceLocation dimensionId, BlockPos pos) {
            return activatedSpawnerRunes.computeIfAbsent(dimensionId, ignored -> new ArrayList<>()).contains(pos);
        }

        public boolean hasVisitedDimension(ResourceLocation dimensionId) {
            return portals.stream().anyMatch(portal -> portal.fromDimension.equals(dimensionId) || portal.toDimension.equals(dimensionId));
        }

        public void addPortalData(ResourceLocation fromDimension, ResourceLocation toDimension, BlockPos fromPos, BlockPos toPos) {
            portals.removeIf(data ->
                (data.fromDimension.equals(fromDimension) && data.toDimension.equals(toDimension))
                    || (data.fromDimension.equals(toDimension) && data.toDimension.equals(fromDimension))
            );
            portals.add(new PortalData(fromDimension, toDimension, fromPos, toPos));
            if (parent != null) {
                parent.setDirty();
            }
        }

        public Optional<PortalData> getPortalData(ResourceLocation fromDimension, ResourceLocation toDimension) {
            Optional<PortalData> exact = portals.stream()
                .filter(it -> it.fromDimension.equals(fromDimension) && it.toDimension.equals(toDimension))
                .findFirst();
            if (exact.isPresent()) {
                return exact;
            }
            return portals.stream()
                .filter(it -> it.fromDimension.equals(toDimension) && it.toDimension.equals(fromDimension))
                .findFirst()
                .map(it -> new PortalData(it.toDimension, it.fromDimension, it.toPos, it.fromPos));
        }
    }

    public record PortalData(ResourceLocation fromDimension, ResourceLocation toDimension, BlockPos fromPos, BlockPos toPos) {
        public static PortalData fromTag(CompoundTag tag) {
            ResourceLocation from = ResourceLocation.tryParse(tag.getString("FromDimension"));
            ResourceLocation to = ResourceLocation.tryParse(tag.getString("ToDimension"));
            return new PortalData(
                from != null ? from : new ResourceLocation("minecraft", "overworld"),
                to != null ? to : new ResourceLocation("minecraft", "overworld"),
                BlockPos.of(tag.getLong("FromPos")),
                BlockPos.of(tag.getLong("ToPos"))
            );
        }

        public CompoundTag save(CompoundTag tag) {
            tag.putString("FromDimension", fromDimension.toString());
            tag.putString("ToDimension", toDimension.toString());
            tag.putLong("FromPos", fromPos.asLong());
            tag.putLong("ToPos", toPos.asLong());
            return tag;
        }
    }
}
