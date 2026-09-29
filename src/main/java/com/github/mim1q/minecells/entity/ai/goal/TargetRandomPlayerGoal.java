package com.github.mim1q.minecells.entity.ai.goal;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class TargetRandomPlayerGoal<E extends Monster> extends Goal {
    private int ticks = 0;
    protected final E entity;
    protected List<Player> targets = new ArrayList<>();
    protected Player currentTarget = null;

    public TargetRandomPlayerGoal(E entity) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        this.targets = this.getTargetablePlayers();
        return !targets.isEmpty();
    }

    @Override
    public boolean canContinueToUse() {
        return this.currentTarget != null && this.currentTarget.isAlive();
    }

    @Override
    public void start() {
        this.ticks = 0;
    }

    @Override
    public void tick() {
        if (this.ticks % 40 == 0) {
            this.targets = this.getTargetablePlayers();
            Player newTarget = this.selectTarget();
            this.entity.setTarget(newTarget);
            this.currentTarget = newTarget;
        }
        this.ticks++;
    }

    @Override
    public void stop() {
        this.entity.setTarget(null);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    protected Player selectTarget() {
        if (this.targets.isEmpty()) {
            return null;
        }
        return this.targets.get(this.entity.getRandom().nextInt(this.targets.size()));
    }

    protected List<Player> getTargetablePlayers() {
        double range = this.entity.getAttributeValue(Attributes.FOLLOW_RANGE) * 2.0D;
        AABB box = AABB.ofSize(this.entity.position(), range, range, range).inflate(2.0D);
        return this.entity.level().getNearbyPlayers(TargetingConditions.forCombat(), this.entity, box);
    }
}
