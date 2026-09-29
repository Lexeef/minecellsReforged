package com.github.mim1q.minecells.entity.ai.goal;

import static com.github.mim1q.minecells.util.MathUtils.vectorRotateY;

import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.MathUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;

import java.util.EnumSet;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class JumpBackGoal<E extends Mob> extends TimedActionGoal<E> {
    private final JumpBackSettings settings;

    public JumpBackGoal(E entity, JumpBackSettings settings, Predicate<E> predicate) {
        super(entity, settings, predicate);
        this.settings = settings;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public JumpBackGoal(E entity, Consumer<JumpBackSettings> settings, Predicate<E> predicate) {
        this(entity, TimedActionSettings.edit(new JumpBackSettings(), settings), predicate);
    }

    @Override
    public boolean canUse() {
        if (!super.canUse()) {
            return false;
        }
        if (entity.getTarget() == null) {
            return false;
        }
        if (entity.getTarget().distanceToSqr(entity) > settings.minDistance * settings.minDistance) {
            return false;
        }

        var posBehind = entity.position().add(entity.getViewVector(1.0F).multiply(-1.0D, 0.0D, -1.0D).normalize());
        return !entity.level().getBlockState(BlockPos.containing(posBehind)).canOcclude();
    }

    @Override
    protected void runAction() {
        if (this.entity.getTarget() == null) {
            return;
        }

        playSound(MineCellsSounds.LEAPING_ZOMBIE_RELEASE.get());
        entity.lookAt(this.entity.getTarget(), 360.0F, 360.0F);

        var targetDirection = entity.getTarget().position().subtract(entity.position()).multiply(1.0D, 0.0D, 1.0D).normalize();
        var sideDirection = vectorRotateY(targetDirection, MathUtils.radians(90.0F));
        var sideStrength = settings.sideStrength * 0.5D + entity.getRandom().nextFloat() * settings.sideStrength * 0.5D;
        if (entity.getRandom().nextBoolean()) {
            sideStrength *= -1.0D;
        }

        entity.setDeltaMovement(
            targetDirection
                .scale(-settings.backStrength)
                .add(sideDirection.scale(sideStrength))
                .add(0.0D, settings.upStrength, 0.0D)
        );
    }

    public static class JumpBackSettings extends TimedActionSettings {
        public double minDistance = 5.0D;
        public double backStrength = 0.75D;
        public double upStrength = 0.33D;
        public double sideStrength = 0.0D;
    }
}
