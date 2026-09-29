package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.ai.goal.TimedAuraGoal;
import com.github.mim1q.minecells.registry.MineCellsEntities;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.ParticleUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class MineCellsMonsterEntity extends Monster {
    public static final float ELITE_SCALE = 1.25F;
    private static final String DISAPPEAR_TIME_KEY = "minecells_disappear_time";
    private static final EntityDataAccessor<Boolean> IS_ELITE = SynchedEntityData.defineId(MineCellsMonsterEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ELITE_AURA_CHARGING = SynchedEntityData.defineId(MineCellsMonsterEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ELITE_AURA_RELEASING = SynchedEntityData.defineId(MineCellsMonsterEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> FOR_DISPLAY = SynchedEntityData.defineId(MineCellsMonsterEntity.class, EntityDataSerializers.BOOLEAN);

    @Nullable
    public BlockPos spawnRunePos;
    @Nullable
    private ResourceLocation additionalLootTable;
    private int eliteAttackCooldown = 100;
    private boolean eliteGoalAdded;

    public MineCellsMonsterEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(IS_ELITE, false);
        entityData.define(ELITE_AURA_CHARGING, false);
        entityData.define(ELITE_AURA_RELEASING, false);
        entityData.define(FOR_DISPLAY, false);
    }

    public boolean isForDisplay() {
        return entityData.get(FOR_DISPLAY);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            spawnEliteAuraParticles();
            return;
        }
        eliteAttackCooldown--;
        if (!isRemoved()) {
            long disappearTime = getPersistentData().getLong(DISAPPEAR_TIME_KEY);
            if (disappearTime > 0L && level().getGameTime() >= disappearTime) {
                discard();
            }
        }
    }

    public void setDisappearTime(long disappearTime) {
        getPersistentData().putLong(DISAPPEAR_TIME_KEY, disappearTime);
    }

    public boolean isElite() {
        return entityData.get(IS_ELITE);
    }

    public void setElite(boolean elite) {
        entityData.set(IS_ELITE, elite);
        refreshDimensions();
        if (elite && !eliteGoalAdded && !level().isClientSide && canUseEliteAura()) {
            eliteGoalAdded = true;
            goalSelector.addGoal(0, createEliteAuraGoal());
        }
    }

    private TimedAuraGoal<MineCellsMonsterEntity> createEliteAuraGoal() {
        return new TimedAuraGoal<>(this, settings -> {
            settings.radius = 2.6D;
            settings.defaultCooldown = 80;
            settings.cooldownGetter = () -> eliteAttackCooldown;
            settings.cooldownSetter = value -> eliteAttackCooldown = value + random.nextInt(40);
            settings.stateSetter = (state, value) -> {
                switch (state) {
                    case CHARGE -> entityData.set(ELITE_AURA_CHARGING, value);
                    case RELEASE -> entityData.set(ELITE_AURA_RELEASING, value);
                    default -> {
                    }
                }
            };
            settings.chargeSound = MineCellsSounds.SHOCKER_CHARGE.get();
            settings.releaseSound = MineCellsSounds.SHOCKER_RELEASE.get();
            settings.length = 80;
            settings.actionTick = 40;
            settings.damage = (float) getAttributeValue(Attributes.ATTACK_DAMAGE);
            settings.chance = 0.05F;
        }, Objects::nonNull);
    }

    private void spawnEliteAuraParticles() {
        int amount;
        double speed;
        if (entityData.get(ELITE_AURA_CHARGING)) {
            amount = 5;
            speed = -0.05D;
        } else if (entityData.get(ELITE_AURA_RELEASING)) {
            amount = 20;
            speed = 0.25D;
        } else {
            return;
        }
        Vec3 center = position().add(0.0D, getBbHeight() * 0.5D, 0.0D);
        ParticleUtils.addAura(level(), center, MineCellsParticles.AURA.get(), amount, 2.5D, 0.0D);
        ParticleUtils.addAura(level(), center, MineCellsParticles.AURA.get(), amount, getBbHeight() * 0.5D, speed);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        super.onSyncedDataUpdated(accessor);
        if (IS_ELITE.equals(accessor)) {
            refreshDimensions();
        }
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        EntityDimensions dimensions = super.getDimensions(pose);
        return isElite() ? dimensions.scale(ELITE_SCALE) : dimensions;
    }

    /**
     * Fabric drops the elite bonus table from {@code dropXp}, so it drops even when the normal
     * loot table is skipped (for example {@link #shouldDropLoot()} returning false).
     */
    @Override
    protected void dropExperience() {
        super.dropExperience();
        if (additionalLootTable == null || !(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        DamageSource source = getLastDamageSource() != null ? getLastDamageSource() : damageSources().generic();
        LootTable table = serverLevel.getServer().getLootData().getLootTable(additionalLootTable);
        LootParams.Builder builder = new LootParams.Builder(serverLevel)
            .withParameter(LootContextParams.THIS_ENTITY, this)
            .withParameter(LootContextParams.ORIGIN, position())
            .withParameter(LootContextParams.DAMAGE_SOURCE, source)
            .withOptionalParameter(LootContextParams.KILLER_ENTITY, source.getEntity())
            .withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, source.getDirectEntity());
        if (lastHurtByPlayer != null) {
            builder = builder.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, lastHurtByPlayer).withLuck(lastHurtByPlayer.getLuck());
        }
        table.getRandomItems(builder.create(LootContextParamSets.ENTITY), this::spawnAtLocation);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("isElite", isElite());
        tag.putBoolean("forDisplay", isForDisplay());
        if (spawnRunePos != null) {
            tag.putLong("spawnRunePos", spawnRunePos.asLong());
        }
        if (additionalLootTable != null) {
            tag.putString("additionalLootTable", additionalLootTable.toString());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("spawnRunePos")) {
            spawnRunePos = BlockPos.of(tag.getLong("spawnRunePos"));
        }
        if (tag.contains("additionalLootTable")) {
            additionalLootTable = ResourceLocation.tryParse(tag.getString("additionalLootTable"));
        }
        setElite(tag.getBoolean("isElite"));
        entityData.set(FOR_DISPLAY, tag.getBoolean("forDisplay"));
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.1D, false));
        goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        addDefaultTargetGoals();
    }

    /**
     * Fabric {@code MineCellsEntity#initGoals} targeting: revenge first, then the nearest player
     * without a line-of-sight or reachability check.
     */
    protected void addDefaultTargetGoals() {
        targetSelector.addGoal(0, new HurtByTargetGoal(this));
        addPlayerTargetGoal(1);
    }

    protected void addPlayerTargetGoal(int priority) {
        targetSelector.addGoal(priority, new NearestAttackableTargetGoal<>(this, Player.class, 0, false, false, null));
    }

    /**
     * Mobs whose Fabric {@code initGoals} does not call {@code super} never get the elite aura goal.
     */
    protected boolean canUseEliteAura() {
        return true;
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor level, MobSpawnType reason) {
        int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE, getBlockX(), getBlockZ());
        if (random.nextFloat() < 0.9F || Math.abs(surfaceY - getBlockY()) > 5) {
            return false;
        }
        if (level.getNearestPlayer(this, 64.0D) != null) {
            return false;
        }
        BlockPos below = blockPosition().below();
        BlockState stateBelow = level.getBlockState(below);
        return stateBelow.isFaceSturdy(level, below, Direction.UP) && !stateBelow.is(Blocks.BEDROCK);
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        return 0.0F;
    }

    public static AttributeSupplier.Builder createBaseAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 20.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.25D)
            .add(Attributes.ATTACK_DAMAGE, 3.0D)
            .add(Attributes.FOLLOW_RANGE, 24.0D);
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
        return MineCellsSounds.LEAPING_ZOMBIE_DEATH.get();
    }
}
