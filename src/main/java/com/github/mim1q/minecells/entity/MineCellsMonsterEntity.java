package com.github.mim1q.minecells.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class MineCellsMonsterEntity extends Monster {
    public MineCellsMonsterEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }
}
