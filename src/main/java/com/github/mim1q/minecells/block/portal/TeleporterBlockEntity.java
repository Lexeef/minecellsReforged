package com.github.mim1q.minecells.block.portal;

import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class TeleporterBlockEntity extends BlockEntity {
    public TeleporterBlockEntity(BlockPos pos, BlockState state) {
        super(MineCellsBlockEntities.TELEPORTER.get(), pos, state);
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition).inflate(2.5D);
    }
}
