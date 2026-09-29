package com.github.mim1q.minecells.entity.nonliving.obelisk;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.registry.MineCellsItems;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ConciergeObeliskEntity extends BossObeliskEntity {
    private static final ResourceLocation SPAWNER_RUNE = MineCells.id("boss/concierge");

    public ConciergeObeliskEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public Item getActivationItem() {
        return MineCellsItems.CONCIERGE_RESPAWN_RUNE.get();
    }

    @Override
    public ResourceLocation getSpawnerRuneDataId() {
        return SPAWNER_RUNE;
    }

    @Override
    public AABB getBox() {
        return AABB.ofSize(position(), 256.0D, 256.0D, 256.0D);
    }

    @Override
    protected void postProcessEntity(Entity entity) {
        Vec3 pos = position().add(getLookAngle().scale(-5.0D));
        entity.moveTo(pos.x, pos.y + 0.5D, pos.z, 0.0F, 0.0F);
        if (entity instanceof Mob mob && level() instanceof ServerLevel serverLevel) {
            mob.finalizeSpawn(serverLevel, level().getCurrentDifficultyAt(blockPosition()), MobSpawnType.NATURAL, null, null);
        }
    }
}
