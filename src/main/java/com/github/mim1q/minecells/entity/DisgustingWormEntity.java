package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.nonliving.projectile.DisgustingWormEggEntity;
import com.github.mim1q.minecells.registry.MineCellsEntities;
import com.github.mim1q.minecells.registry.MineCellsSounds;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class DisgustingWormEntity extends MineCellsMonsterEntity {
    private int soundCountdown = 0;

    public DisgustingWormEntity(EntityType<? extends DisgustingWormEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new MeleeAttackGoal(this, 1.75D, false));
        goalSelector.addGoal(2, new RandomStrollGoal(this, 1.0D));
        goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        goalSelector.addGoal(8, new RandomStrollGoal(this, 1.0D));
        goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        addDefaultTargetGoals();
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && isAggressive()) {
            if (soundCountdown > 0) {
                soundCountdown--;
            } else {
                level().playSound(null, getX(), getY(), getZ(), MineCellsSounds.DISGUSTING_WORM_ATTACK.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
                soundCountdown = 60 + random.nextInt(100);
            }
        }
    }

    @Override
    public void die(DamageSource damageSource) {
        if (!level().isClientSide) {
            for (int i = 0; i < 6; i++) {
                Vec3 velocity = new Vec3(random.nextDouble() - 0.5D, 0.7D + random.nextDouble() * 0.5D, random.nextDouble() - 0.5D).scale(0.3D);
                DisgustingWormEggEntity egg = new DisgustingWormEggEntity(MineCellsEntities.DISGUSTING_WORM_EGG.get(), level());
                egg.setPos(position());
                egg.shoot(velocity);
                egg.setFuse(15 + i * 5 + random.nextInt(5));
                level().addFreshEntity(egg);
            }
        }
        super.die(damageSource);
    }

    @Override
    protected boolean shouldDropLoot() {
        return false;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return MineCellsSounds.DISGUSTING_WORM_DEATH.get();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return MineCellsMonsterEntity.createBaseAttributes()
            .add(Attributes.MOVEMENT_SPEED, 0.2D)
            .add(Attributes.FOLLOW_RANGE, 16.0D)
            .add(Attributes.MAX_HEALTH, 15.0D)
            .add(Attributes.ATTACK_DAMAGE, 12.0D)
            .add(Attributes.ATTACK_KNOCKBACK, 0.5D);
    }
}
