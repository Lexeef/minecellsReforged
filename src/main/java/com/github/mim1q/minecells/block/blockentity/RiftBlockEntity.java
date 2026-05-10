package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class RiftBlockEntity extends MineCellsBlockEntity {
    public RiftBlockEntity(BlockPos pos, BlockState state) {
        super(MineCellsBlockEntities.RIFT.get(), pos, state);
    }

    public float getRotation(float partialTick) {
        return level == null ? 0.0F : (level.getGameTime() + partialTick) * 0.5F;
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide) {
            return;
        }

        Vec3 center = Vec3.atBottomCenterOf(pos).add(0.0D, 0.5D, 0.0D);
        float theta = level.getGameTime() * 0.1F;
        float rotation = (float) Math.toRadians(getRotation(0.0F));

        for (int i = 0; i < 2; i++) {
            double xzPos = Math.cos(theta) * 0.51D;
            Vec3 offset = new Vec3(
                xzPos * Math.cos(rotation),
                Math.sin(theta) * 1.02D,
                xzPos * -Math.sin(rotation)
            );
            Vec3 velocity = offset.normalize().scale(level.random.nextFloat() * 0.01F);
            level.addParticle(
                MineCellsParticles.SPECKLE.get().get(0x33DDFF),
                center.x + offset.x,
                center.y + offset.y,
                center.z + offset.z,
                velocity.x,
                velocity.y,
                velocity.z
            );
            theta += Mth.PI;
        }
    }
}
