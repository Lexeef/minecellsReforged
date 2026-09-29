package com.github.mim1q.minecells.entity.ai.goal.conjunctivius;

import com.github.mim1q.minecells.entity.ai.goal.TargetRandomPlayerGoal;
import com.github.mim1q.minecells.entity.boss.ConjunctiviusEntity;

import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class ConjunctiviusTargetGoal extends TargetRandomPlayerGoal<ConjunctiviusEntity> {
    public ConjunctiviusTargetGoal(ConjunctiviusEntity entity) {
        super(entity);
    }

    @Override
    protected List<Player> getTargetablePlayers() {
        if (this.entity.getRoomBox() == null) {
            return List.of();
        }
        AABB box = AABB.of(this.entity.getRoomBox().inflatedBy(1));
        return this.entity.level().getNearbyPlayers(TargetingConditions.forCombat().ignoreLineOfSight(), this.entity, box);
    }
}
