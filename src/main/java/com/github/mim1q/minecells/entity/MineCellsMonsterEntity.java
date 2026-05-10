package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.registry.MineCellsEntities;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class MineCellsMonsterEntity extends Monster {
    public MineCellsMonsterEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.1D, false));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.9D));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(7, new LookAtPlayerGoal(this, LivingEntity.class, 6.0F));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createBaseAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 20.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.25D)
            .add(Attributes.ATTACK_DAMAGE, 3.0D)
            .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (getType() == MineCellsEntities.BUZZCUTTER.get() || getType() == MineCellsEntities.SWEEPER.get()) {
            return MineCellsSounds.FLY_FLY.get();
        }
        return super.getAmbientSound();
    }

    @Override
    protected SoundEvent getDeathSound() {
        if (getType() == MineCellsEntities.LEAPING_ZOMBIE.get()) {
            return MineCellsSounds.LEAPING_ZOMBIE_DEATH.get();
        }
        if (getType() == MineCellsEntities.SHOCKER.get()) {
            return MineCellsSounds.SHOCKER_DEATH.get();
        }
        if (getType() == MineCellsEntities.DISGUSTING_WORM.get()) {
            return MineCellsSounds.DISGUSTING_WORM_DEATH.get();
        }
        if (getType() == MineCellsEntities.KAMIKAZE.get()) {
            return MineCellsSounds.KAMIKAZE_DEATH.get();
        }
        if (getType() == MineCellsEntities.SEWERS_TENTACLE.get()) {
            return MineCellsSounds.SEWERS_TENTACLE_DEATH.get();
        }
        return super.getDeathSound();
    }
}
