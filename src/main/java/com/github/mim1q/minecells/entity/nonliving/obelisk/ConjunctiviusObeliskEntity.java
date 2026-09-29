package com.github.mim1q.minecells.entity.nonliving.obelisk;

import com.github.mim1q.minecells.entity.boss.ConjunctiviusEntity;
import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.registry.MineCellsItems;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.util.ParticleUtils;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
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

public class ConjunctiviusObeliskEntity extends BossObeliskEntity {
    private static final ResourceLocation SPAWNER_RUNE = MineCells.id("boss/conjunctivius");

    public ConjunctiviusObeliskEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public Item getActivationItem() {
        return MineCellsItems.CONJUNCTIVIUS_RESPAWN_RUNE.get();
    }

    @Override
    public ResourceLocation getSpawnerRuneDataId() {
        return SPAWNER_RUNE;
    }

    @Override
    public AABB getBox() {
        return new AABB(getX() - 13.0D, getY() - 1.0D, getZ() - 25.0D, getX() + 13.0D, getY() + 25.0D, getZ() + 5.0D);
    }

    @Override
    protected void postProcessEntity(Entity entity) {
        entity.moveTo(getX(), getY() + 10.0D, getZ(), 180.0F, 0.0F);
        if (entity instanceof Mob mob && level() instanceof ServerLevel serverLevel) {
            mob.finalizeSpawn(serverLevel, level().getCurrentDifficultyAt(blockPosition()), MobSpawnType.SPAWNER, null, null);
        }
        if (entity instanceof ConjunctiviusEntity) {
            // anchors/room set in finalizeSpawn
        }
    }

    @Override
    protected void spawnActivationParticles(int activatedTicks) {
        ParticleOptions particle = MineCellsParticles.SPECKLE.get().get(0xFF0000);
        for (int i = 0; i < activatedTicks; i++) {
            float yOff = random.nextFloat() * 10.0F;
            float xOff = random.nextFloat() - 0.5F;
            float zOff = random.nextFloat() - 0.5F;
            ParticleUtils.addParticle(level(), particle, position().add(xOff, yOff, zOff), new Vec3(0.0D, 0.2D, 0.0D));
        }
        if (activatedTicks >= 38) {
            ParticleUtils.addParticle(level(), ParticleTypes.EXPLOSION_EMITTER, position().add(0.0D, 12.5D, 0.0D), Vec3.ZERO);
        }
    }
}
