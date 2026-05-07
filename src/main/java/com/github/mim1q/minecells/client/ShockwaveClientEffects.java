package com.github.mim1q.minecells.client;

import com.github.mim1q.minecells.registry.MineCellsBlocks;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

public final class ShockwaveClientEffects {
    private ShockwaveClientEffects() {
    }

    public static void play(BlockPos pos, net.minecraft.world.level.block.Block block, boolean end, ClientLevel level) {
        if (block == MineCellsBlocks.SHOCKWAVE_FLAME.get() || block == MineCellsBlocks.SHOCKWAVE_FLAME_PLAYER.get()) {
            if (end) {
                playFlameEnd(level, pos);
            } else {
                playFlameStart(level, pos);
            }
        }
    }

    private static void playFlameStart(ClientLevel level, BlockPos pos) {
        level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 0.15F, 0.6F + level.random.nextFloat() * 0.4F, true);
        addAura(level, Vec3.atBottomCenterOf(pos), 2, 1.0D, 0.02D, ParticleTypes.FLAME);
        for (int i = 0; i < 2; i++) {
            level.addParticle(
                ParticleTypes.FLAME,
                pos.getX() + level.random.nextFloat(),
                pos.getY() + level.random.nextFloat(),
                pos.getZ() + level.random.nextFloat(),
                0.0D,
                0.1D + level.random.nextFloat() * 0.2D,
                0.0D
            );
        }
    }

    private static void playFlameEnd(ClientLevel level, BlockPos pos) {
        level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.1F, 0.6F + level.random.nextFloat() * 0.4F, true);
        Vec3 center = Vec3.atBottomCenterOf(pos);
        addAura(level, center, 2, 0.7D, 0.05D, ParticleTypes.FLAME);
        addAura(level, center, 3, 0.75D, 0.02D, ParticleTypes.SMOKE);
    }

    private static void addAura(ClientLevel level, Vec3 position, int amount, double radius, double speed, net.minecraft.core.particles.ParticleOptions particle) {
        for (int i = 0; i < amount; i++) {
            Vec3 offset = new Vec3(
                level.random.nextDouble() * 2.0D - 1.0D,
                level.random.nextDouble() * 2.0D - 1.0D,
                level.random.nextDouble() * 2.0D - 1.0D
            ).normalize();
            Vec3 velocity = offset.scale(speed);
            Vec3 finalOffset = offset.scale(radius);
            Vec3 pos = position.add(finalOffset);
            level.addParticle(particle, pos.x, pos.y, pos.z, velocity.x, velocity.y, velocity.z);
        }
    }
}
