package com.github.mim1q.minecells.entity.ai.goal.conjunctivius;

import com.github.mim1q.minecells.entity.boss.ConjunctiviusEntity;
import com.github.mim1q.minecells.registry.MineCellsSounds;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class ConjunctiviusMoveAroundGoal extends Goal {
    protected final ConjunctiviusEntity entity;
    protected double speed = 0.05D;
    private int cooldown = 0;
    private Vec3 targetPos;

    public ConjunctiviusMoveAroundGoal(ConjunctiviusEntity entity) {
        this.entity = entity;
        this.targetPos = this.entity.position();
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return this.entity.isInFullStage()
            && this.entity.getSpawnPos() != null
            && this.entity.getRoomBox() != null;
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public void start() {
        super.start();
        this.targetPos = this.entity.getSpawnPos();
    }

    @Override
    public void tick() {
        entity.moving = isMoving();
        if (cooldown == 0) {
            targetPos = getRandomTargetPosition();
            cooldown = getNextCooldown();
        } else {
            cooldown--;
            if (entity.position().distanceToSqr(targetPos) > 4.0D) {
                entity.setDeltaMovement(entity.getDeltaMovement().add(targetPos.subtract(entity.position()).normalize().scale(speed)));
            }
            if (cooldown == 0 && this.entity.getStage() < 7) {
                entity.playSound(MineCellsSounds.CONJUNCTIVIUS_MOVE.get(), 1.0F, 1.0F);
            }
        }
    }

    @Override
    public void stop() {
        this.cooldown = 0;
        this.entity.moving = false;
        this.targetPos = this.entity.getSpawnPos();
    }

    public boolean isMoving() {
        return this.cooldown == 0;
    }

    private Vec3 getRandomTargetPosition() {
        Vec3 center = this.entity.getSpawnPos();
        double zOffset = entity.getRandom().nextDouble() * entity.getStageAdjustedValue(2.0, 4.0, 8.0, 12.0);
        return center.add(
            -4.0D + this.entity.getRandom().nextDouble() * 8.0D,
            -5.0D + this.entity.getRandom().nextDouble() * 3.0D,
            -1.0D - this.entity.getRandom().nextDouble() * 2.0D - zOffset
        );
    }

    protected int getNextCooldown() {
        return this.entity.getRandom().nextInt(40) + 40;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
