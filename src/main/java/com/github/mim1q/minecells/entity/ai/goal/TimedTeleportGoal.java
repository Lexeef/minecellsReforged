package com.github.mim1q.minecells.entity.ai.goal;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Monster;

import java.util.EnumSet;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class TimedTeleportGoal<E extends Monster> extends TimedActionGoal<E> {
    private Entity target;

    public TimedTeleportGoal(E entity, TimedTeleportSettings settings, Predicate<E> predicate) {
        super(entity, settings, predicate);
        if (settings.shouldStandStill) {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }
    }

    public TimedTeleportGoal(E entity, Consumer<TimedTeleportSettings> settingsConsumer, Predicate<E> predicate) {
        this(entity, TimedActionSettings.edit(new TimedTeleportSettings(), settingsConsumer), predicate);
    }

    @Override
    public boolean canUse() {
        this.target = this.entity.getTarget();
        return this.target != null
            && this.target.isAlive()
            && this.target.isAttackable()
            && (this.entity.distanceTo(this.target) > 10.0D || !this.entity.hasLineOfSight(this.target))
            && super.canUse();
    }

    @Override
    protected void runAction() {
        if (this.target != null) {
            this.entity.teleportTo(this.target.getX(), this.target.getY(), this.target.getZ());
        }
    }

    public static class TimedTeleportSettings extends TimedActionSettings {
        public boolean shouldStandStill = false;
    }
}
