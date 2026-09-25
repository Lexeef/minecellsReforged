package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.ai.goal.JumpBackGoal;
import com.github.mim1q.minecells.entity.ai.goal.WalkTowardsTargetGoal;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class UndeadArcherEntity extends MineCellsMonsterEntity {
    private static final EntityDataAccessor<Boolean> SHOOT_CHARGING = SynchedEntityData.defineId(UndeadArcherEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SHOOT_RELEASING = SynchedEntityData.defineId(UndeadArcherEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> SHOOT_COOLDOWN = SynchedEntityData.defineId(UndeadArcherEntity.class, EntityDataSerializers.INT);

    public final AnimationProperty handsUpProgess = new AnimationProperty(0.0F);
    public final AnimationProperty pullProgress = new AnimationProperty(0.0F);
    private int jumpbackCooldown = 0;

    public UndeadArcherEntity(EntityType<? extends UndeadArcherEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new JumpBackGoal<>(this, s -> {
            s.minDistance = 8.0D;
            s.sideStrength = 0.66D;
            s.defaultCooldown = 10;
            s.actionTick = 5;
            s.length = 10;
            s.chance = 0.3F;
            s.cooldownGetter = () -> jumpbackCooldown;
            s.cooldownSetter = ticks -> jumpbackCooldown = ticks;
        }, it -> !it.isShootCharging() && !it.isShootReleasing()));
        goalSelector.addGoal(1, new UndeadArcherShootGoal(this, 15, 25, 0.5F));
        goalSelector.addGoal(2, new WalkTowardsTargetGoal(this, 1.0D, false, 8.5D));
        goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 12.0F));
        goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        addDefaultTargetGoals();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(SHOOT_CHARGING, false);
        entityData.define(SHOOT_RELEASING, false);
        entityData.define(SHOOT_COOLDOWN, 0);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag tag) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData, tag);
        setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
        setLeftHanded(false);
        return data;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            if (isShootCharging()) {
                handsUpProgess.setupTransitionTo(1.0F, 10.0F);
                pullProgress.setupTransitionTo(1.0F, 20.0F);
            } else {
                handsUpProgess.setupTransitionTo(0.0F, 5.0F);
                pullProgress.setupTransitionTo(0.0F, 5.0F);
            }
        } else {
            --jumpbackCooldown;
            int cooldown = getShootCooldown();
            if (cooldown > 0) {
                setShootCooldown(cooldown - 1);
            }
        }
    }

    @Override
    public int getMaxFallDistance() {
        return 3;
    }

    public boolean isShootCharging() {
        return entityData.get(SHOOT_CHARGING);
    }

    public void setShootCharging(boolean charging) {
        setAggressive(charging);
        entityData.set(SHOOT_CHARGING, charging);
    }

    public boolean isShootReleasing() {
        return entityData.get(SHOOT_RELEASING);
    }

    public void setShootReleasing(boolean releasing) {
        entityData.set(SHOOT_RELEASING, releasing);
    }

    public int getShootCooldown() {
        return entityData.get(SHOOT_COOLDOWN);
    }

    public void setShootCooldown(int ticks) {
        entityData.set(SHOOT_COOLDOWN, ticks);
    }

    public int getShootMaxCooldown() {
        return 20;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("shootCooldown", getShootCooldown());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setShootCooldown(tag.getInt("shootCooldown"));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return MineCellsMonsterEntity.createBaseAttributes()
            .add(Attributes.MAX_HEALTH, 20.0D)
            .add(Attributes.ARMOR, 3.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.23D)
            .add(Attributes.ATTACK_DAMAGE, 4.0D)
            .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    private static final class UndeadArcherShootGoal extends Goal {
        private final UndeadArcherEntity entity;
        private final int actionTick;
        private final int lengthTicks;
        private final float chance;
        private LivingEntity target;
        private int ticks;

        private UndeadArcherShootGoal(UndeadArcherEntity entity, int actionTick, int lengthTicks, float chance) {
            this.entity = entity;
            this.actionTick = actionTick;
            this.lengthTicks = lengthTicks;
            this.chance = chance;
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity currentTarget = entity.getTarget();
            return currentTarget != null
                && entity.getMainHandItem().is(Items.BOW)
                && entity.getShootCooldown() == 0
                && entity.getSensing().hasLineOfSight(currentTarget)
                && entity.getRandom().nextFloat() < chance;
        }

        @Override
        public void start() {
            target = entity.getTarget();
            entity.setShootCharging(true);
            ticks = 0;
            entity.playSound(MineCellsSounds.BOW_CHARGE.get(), 1.0F, 1.0F);
        }

        @Override
        public boolean canContinueToUse() {
            return ticks < lengthTicks && target != null && target.isAlive();
        }

        @Override
        public void stop() {
            entity.setShootCharging(false);
            entity.setShootReleasing(false);
            entity.setShootCooldown(entity.getShootMaxCooldown());
            entity.stopUsingItem();
            target = null;
        }

        @Override
        public void tick() {
            if (target != null) {
                entity.getLookControl().setLookAt(target);
                entity.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 0.01D);
                if (ticks == actionTick) {
                    entity.playSound(MineCellsSounds.BOW_RELEASE.get(), 1.0F, 1.0F);
                    shoot(target);
                }
            }
            if (entity.isShootReleasing()) {
                entity.stopUsingItem();
            } else if (!entity.isUsingItem()) {
                entity.startUsingItem(ProjectileUtil.getWeaponHoldingHand(entity, Items.BOW));
            }
            ticks++;
        }

        private void shoot(LivingEntity target) {
            entity.setShootCharging(false);
            entity.setShootReleasing(true);
            Arrow arrow = new Arrow(entity.level(), entity);
            double dx = target.getX() - entity.getX();
            double dy = target.getY(0.33D) - arrow.getY();
            double dz = target.getZ() - entity.getZ();
            double horizontal = Math.sqrt(dx * dx + dz * dz);
            arrow.shoot(dx, dy + horizontal * 0.2D, dz, 1.6F, 1.0F);
            entity.level().addFreshEntity(arrow);
        }
    }
}
