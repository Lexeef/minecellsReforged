package com.github.mim1q.minecells.entity.ai.goal;

import static com.github.mim1q.minecells.util.MathUtils.radians;

import com.github.mim1q.minecells.entity.nonliving.ShockwavePlacer;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.util.MathUtils;

import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ShockwaveGoal<E extends Monster> extends TimedActionGoal<E> {
    private final BlockState shockwaveBlock;
    private final float shockwaveDamage;
    private final int shockwaveRadius;
    private final ShockwaveType shockwaveType;
    private final float shockwaveInterval;

    private ShockwaveGoal(E entity, ShockwaveGoalSettings settings, Predicate<E> predicate) {
        super(entity, settings, predicate);
        this.shockwaveBlock = settings.shockwaveBlock.defaultBlockState();
        this.shockwaveDamage = settings.shockwaveDamage;
        this.shockwaveRadius = settings.shockwaveRadius;
        this.shockwaveType = settings.shockwaveType;
        this.shockwaveInterval = settings.shockwaveInterval;
    }

    public ShockwaveGoal(E entity, Consumer<ShockwaveGoalSettings> settingsConsumer, Predicate<E> predicate) {
        this(entity, ShockwaveGoalSettings.edit(new ShockwaveGoalSettings(), settingsConsumer), predicate);
        setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
    }

    @Override
    protected void runAction() {
        if (entity.getTarget() == null) {
            return;
        }
        Vec3 offset = MathUtils.vectorRotateY(new Vec3(-0.75D, 0.2D, -0.9D), radians(entity.yBodyRot));
        ShockwavePlacer placer;
        if (shockwaveType == ShockwaveType.LINE) {
            Vec3 targetPos = entity.getTarget().position();
            placer = ShockwavePlacer.createLine(
                entity.level(),
                entity.position().add(offset),
                new Vec3(targetPos.x, entity.getY(), targetPos.z),
                shockwaveInterval,
                shockwaveBlock,
                entity.getUUID(),
                shockwaveDamage
            );
        } else {
            placer = ShockwavePlacer.createCircle(
                entity.level(),
                entity.position().add(offset),
                shockwaveRadius,
                shockwaveInterval,
                shockwaveBlock,
                entity.getUUID(),
                shockwaveDamage
            );
        }
        entity.level().addFreshEntity(placer);
    }

    @Override
    public boolean canUse() {
        return super.canUse() && entity.getTarget() != null;
    }

    @Override
    public void start() {
        super.start();
        var target = this.entity.getTarget();
        if (target == null) {
            return;
        }
        this.entity.lookAt(target, 180.0F, 180.0F);
        this.entity.getNavigation().stop();
        this.entity.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 1.0D);
    }

    public static class ShockwaveGoalSettings extends TimedActionGoal.TimedActionSettings {
        public Block shockwaveBlock = MineCellsBlocks.SHOCKWAVE_FLAME.get();
        public int shockwaveRadius = 12;
        public ShockwaveType shockwaveType = ShockwaveType.LINE;
        public float shockwaveDamage = 6.0F;
        public float shockwaveInterval = 1.0F;
    }

    public enum ShockwaveType {
        LINE,
        CIRCLE
    }
}
