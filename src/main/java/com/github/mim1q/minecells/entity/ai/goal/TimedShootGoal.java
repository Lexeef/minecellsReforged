package com.github.mim1q.minecells.entity.ai.goal;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class TimedShootGoal<E extends Monster> extends TimedActionGoal<E> {
    private Entity target;
    private final BiFunction<Vec3, Vec3, Entity> projectileCreator;

    public TimedShootGoal(E entity, TimedShootSettings settings, Predicate<E> predicate) {
        super(entity, settings, predicate);
        this.projectileCreator = settings.projectileCreator;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public TimedShootGoal(E entity, Consumer<TimedShootSettings> settingsConsumer, Predicate<E> predicate) {
        this(entity, TimedActionSettings.edit(new TimedShootSettings(), settingsConsumer), predicate);
    }

    @Override
    public boolean canUse() {
        this.target = this.entity.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }
        return super.canUse();
    }

    @Override
    public void tick() {
        if (this.target != null) {
            this.entity.getLookControl().setLookAt(this.target);
        }
        super.tick();
    }

    @Override
    protected void runAction() {
        if (this.target != null && this.projectileCreator != null) {
            Entity projectile = projectileCreator.apply(
                this.entity.position(),
                this.target.position().add(0.0D, this.target.getBbHeight() * 0.5D, 0.0D)
            );
            if (projectile != null) {
                this.entity.level().addFreshEntity(projectile);
            }
        }
    }

    public static class TimedShootSettings extends TimedActionSettings {
        public BiFunction<Vec3, Vec3, Entity> projectileCreator = null;
    }
}
