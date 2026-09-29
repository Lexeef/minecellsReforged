package com.github.mim1q.minecells.util;

import com.github.mim1q.minecells.network.MineCellsNetwork;
import com.github.mim1q.minecells.network.s2c.ExplosionS2CPacket;
import com.github.mim1q.minecells.registry.MineCellsSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.network.PacketDistributor;

import java.util.function.Predicate;
import java.util.List;

/**
 * Forge port of Fabric MineCells explosion (entity damage only, no block destruction).
 */
public final class MineCellsExplosion {
    private MineCellsExplosion() {
    }

    public static void explode(
        ServerLevel level,
        Entity source,
        LivingEntity attacker,
        Vec3 position,
        float power,
        float radius
    ) {
        explode(
            level,
            source,
            attacker,
            position,
            power,
            radius,
            entity -> entity instanceof Player || entity instanceof TamableAnimal
        );
    }

    public static void explode(
        ServerLevel level,
        Entity source,
        LivingEntity attacker,
        Vec3 position,
        float power,
        float radius,
        Predicate<Entity> damagePredicate
    ) {
        level.playSound(
            null,
            BlockPos.containing(position),
            MineCellsSounds.EXPLOSION.get(),
            SoundSource.HOSTILE,
            1.0F,
            1.0F
        );
        damageEntities(level, source, attacker, position, power, radius, damagePredicate);
        ScreenShakeUtils.shakeAround(
            level,
            position,
            1.0F,
            20,
            radius,
            radius * 3.0F,
            "minecells:explosion"
        );
        BlockPos blockPos = BlockPos.containing(position);
        MineCellsNetwork.CHANNEL.send(
            PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(blockPos)),
            new ExplosionS2CPacket(position.x, position.y, position.z, radius)
        );
    }

    private static void damageEntities(
        ServerLevel level,
        Entity source,
        LivingEntity attacker,
        Vec3 pos,
        float power,
        float radius,
        Predicate<Entity> damagePredicate
    ) {
        AABB box = new AABB(
            pos.x - radius,
            pos.y - radius,
            pos.z - radius,
            pos.x + radius,
            pos.y + radius,
            pos.z + radius
        );
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, box, damagePredicate);
        for (LivingEntity entity : entities) {
            float distance = Mth.sqrt((float) entity.distanceToSqr(pos));
            if (distance <= radius) {
                float damage = power * getDamagePercentage(distance, radius);
                entity.hurt(level.damageSources().explosion(source, attacker), damage);
            }
        }
    }

    /** Full damage for the inner half of the radius, then linear falloff to zero. */
    private static float getDamagePercentage(float distance, float radius) {
        float minRadius = radius * 0.5F;
        if (distance <= minRadius) {
            return 1.0F;
        }
        return 1.0F - (distance - minRadius) / (radius - minRadius);
    }
}
