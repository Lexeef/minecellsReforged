package com.github.mim1q.minecells.entity.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class WalkTowardsTargetGoal extends MeleeAttackGoal {
    protected final double minDistance;

    public WalkTowardsTargetGoal(PathfinderMob mob, double speed, boolean pauseWhenMobIdle, double minDistance) {
        super(mob, speed, pauseWhenMobIdle);
        this.minDistance = minDistance;
    }

    public WalkTowardsTargetGoal(PathfinderMob mob, double speed, boolean pauseWhenMobIdle) {
        this(mob, speed, pauseWhenMobIdle, 0.0D);
    }

    @Override
    public boolean canUse() {
        return super.canUse() && this.mob.distanceTo(this.mob.getTarget()) >= minDistance;
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && this.mob.distanceTo(this.mob.getTarget()) >= minDistance;
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity target, double squaredDistance) {
    }
}
