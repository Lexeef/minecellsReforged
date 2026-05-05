package com.github.mim1q.minecells.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;

public class GroundDecorationBlock extends Block {
    private final Shape shape;

    public GroundDecorationBlock(Properties properties, Shape shape) {
        super(properties);
        this.shape = shape;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return this.shape.getShape();
    }

    public enum Shape {
        PILE(Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D)),
        BLOCK_12(Shapes.box(0.125D, 0.0D, 0.125D, 0.875D, 0.75D, 0.875D)),
        BARREL(Block.box(1.5D, 0.0D, 1.5D, 14.5D, 18.0D, 14.5D));

        private final VoxelShape shape;

        Shape(VoxelShape shape) {
            this.shape = shape;
        }

        public VoxelShape getShape() {
            return shape;
        }
    }
}
