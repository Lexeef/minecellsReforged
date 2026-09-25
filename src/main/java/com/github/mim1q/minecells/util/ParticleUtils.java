package com.github.mim1q.minecells.util;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class ParticleUtils {
    private ParticleUtils() {
    }

    public static void addParticle(Level level, ParticleOptions particle, Vec3 pos, Vec3 vel) {
        level.addParticle(particle, pos.x, pos.y, pos.z, vel.x, vel.y, vel.z);
    }

    public static void addAura(Level level, Vec3 position, ParticleOptions particle, int amount, double radius, double speed) {
        for (int i = 0; i < amount; i++) {
            Vec3 offset = new Vec3(
                level.random.nextDouble() * 2.0D - 1.0D,
                level.random.nextDouble() * 2.0D - 1.0D,
                level.random.nextDouble() * 2.0D - 1.0D
            ).normalize();
            Vec3 velocity = offset.scale(speed);
            offset = offset.scale(radius);
            addParticle(level, particle, position.add(offset), velocity);
        }
    }

    public static void addInBox(Level level, ParticleOptions effect, AABB box, int amount, Vec3 velScale) {
        for (int i = 0; i < amount; i++) {
            double x = box.minX + level.random.nextDouble() * (box.maxX - box.minX);
            double y = box.minY + level.random.nextDouble() * (box.maxY - box.minY);
            double z = box.minZ + level.random.nextDouble() * (box.maxZ - box.minZ);
            Vec3 pos = new Vec3(x, y, z);
            Vec3 vel = box.getCenter().subtract(pos).normalize().multiply(velScale.x, velScale.y, velScale.z);
            addParticle(level, effect, pos, vel);
        }
    }
}
