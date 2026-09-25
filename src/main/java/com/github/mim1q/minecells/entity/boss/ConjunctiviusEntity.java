package com.github.mim1q.minecells.entity.boss;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.MineCellsBlockTags;
import com.github.mim1q.minecells.entity.SewersTentacleEntity;
import com.github.mim1q.minecells.entity.ai.goal.TimedActionGoal;
import com.github.mim1q.minecells.entity.ai.goal.TimedAuraGoal;
import com.github.mim1q.minecells.entity.ai.goal.conjunctivius.ConjunctiviusBarrageGoal;
import com.github.mim1q.minecells.entity.ai.goal.conjunctivius.ConjunctiviusMoveAroundGoal;
import com.github.mim1q.minecells.entity.ai.goal.conjunctivius.ConjunctiviusTargetGoal;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsEntities;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.registry.MineCellsStatusEffects;
import com.github.mim1q.minecells.network.MineCellsNetwork;
import com.github.mim1q.minecells.network.s2c.UpdateConjunctiviusBossBarS2CPacket;
import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.util.ParticleUtils;
import com.github.mim1q.minecells.util.ScreenShakeUtils;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import com.github.mim1q.minecells.util.client.ClientUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static com.github.mim1q.minecells.entity.boss.ConjunctiviusEntity.EyeState.SHAKING;

public class ConjunctiviusEntity extends MineCellsBossEntity {

    public final AnimationProperty spikeOffset = new AnimationProperty(5.0F, MathUtils::easeInOutQuad);

    public static final EntityDataAccessor<Boolean> DASH_CHARGING = SynchedEntityData.defineId(ConjunctiviusEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> FOR_DISPLAY = SynchedEntityData.defineId(ConjunctiviusEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> DASH_RELEASING = SynchedEntityData.defineId(ConjunctiviusEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> AURA_CHARGING = SynchedEntityData.defineId(ConjunctiviusEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> AURA_RELEASING = SynchedEntityData.defineId(ConjunctiviusEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> BARRAGE_ACTIVE = SynchedEntityData.defineId(ConjunctiviusEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<BlockPos> ANCHOR_TOP = SynchedEntityData.defineId(ConjunctiviusEntity.class, EntityDataSerializers.BLOCK_POS);
    public static final EntityDataAccessor<BlockPos> ANCHOR_LEFT = SynchedEntityData.defineId(ConjunctiviusEntity.class, EntityDataSerializers.BLOCK_POS);
    public static final EntityDataAccessor<BlockPos> ANCHOR_RIGHT = SynchedEntityData.defineId(ConjunctiviusEntity.class, EntityDataSerializers.BLOCK_POS);
    public static final EntityDataAccessor<Integer> STAGE = SynchedEntityData.defineId(ConjunctiviusEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> TARGET_ID = SynchedEntityData.defineId(ConjunctiviusEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Vector3f> DASH_TARGET = SynchedEntityData.defineId(ConjunctiviusEntity.class, EntityDataSerializers.VECTOR3);
    public static final EntityDataAccessor<Integer> TENTACLE_COUNT = SynchedEntityData.defineId(ConjunctiviusEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> MAX_TENTACLE_COUNT = SynchedEntityData.defineId(ConjunctiviusEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Optional<UUID>> BOSSBAR_UUID = SynchedEntityData.defineId(ConjunctiviusEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    private Vec3 spawnPos = Vec3.ZERO;
    private Direction direction;
    private float spawnRot = 180.0F;
    private BoundingBox roomBox = null;
    private int dashCooldown = 0;
    private int auraCooldown = 0;
    public int barrageCooldown = 0;
    public boolean moving = false;
    private int stageTicks = 1;
    private int lastStage = 0;

    private final HashMap<LivingEntity, Integer> hitEntities = new HashMap<>();

    private MathUtils.AnimationEasing eyeEasing = MathUtils::lerp;
    private Vec3 eyeOffset = Vec3.ZERO;
    private Vec3 lastEyeOffset = Vec3.ZERO;

    private int blinkTimer = 0;
    private final AnimationProperty eyeBlink = new AnimationProperty(0.0F, MathUtils::lerp);

    private int lastTargetId = -1;

    public ConjunctiviusEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new ConjunctiviusMoveControl(this);
        this.navigation = new FlyingPathNavigation(this, level);
        this.setNoGravity(true);
        this.noCulling = true;
        this.noPhysics = true;
        this.setYRot(180.0F);
        this.setXRot(0.0F);
        this.yBodyRot = 180.0F;
        this.yBodyRotO = 180.0F;
        this.yRotO = 180.0F;
        this.yHeadRot = 180.0F;
        this.yHeadRotO = 180.0F;
        this.bossBar.setVisible(false);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType spawnReason, @Nullable SpawnGroupData entityData, @Nullable CompoundTag entityNbt) {
        this.spawnPos = Vec3.atCenterOf(this.blockPosition());
        this.roomBox = this.createBox();
        this.direction = Direction.NORTH;
        this.spawnRot = this.direction.toYRot();
        BlockPos topAnchor = this.blockPosition().offset(0, 9, 0);
        BlockPos leftAnchor = this.blockPosition().offset(11, 0, 0);
        BlockPos rightAnchor = this.blockPosition().offset(-11, 0, 0);
        this.setAnchors(topAnchor, leftAnchor, rightAnchor);
        return super.finalizeSpawn(world, difficulty, spawnReason, entityData, entityNbt);
    }

    protected BoundingBox createBox() {
        BlockPos startPos = this.blockPosition();
        return new BoundingBox(
            startPos.getX() - 11, startPos.getY() - 10, startPos.getZ() - 25,
            startPos.getX() + 11, startPos.getY() + 9, startPos.getZ() + 2
        );
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FOR_DISPLAY, false);
        this.entityData.define(DASH_RELEASING, false);
        this.entityData.define(DASH_CHARGING, false);
        this.entityData.define(AURA_RELEASING, false);
        this.entityData.define(AURA_CHARGING, false);
        this.entityData.define(BARRAGE_ACTIVE, false);
        this.entityData.define(ANCHOR_TOP, this.blockPosition());
        this.entityData.define(ANCHOR_LEFT, this.blockPosition());
        this.entityData.define(ANCHOR_RIGHT, this.blockPosition());
        this.entityData.define(STAGE, 0);
        this.entityData.define(TARGET_ID, -1);
        this.entityData.define(DASH_TARGET, new Vector3f(0.0F, 0.0F, 0.0F));
        this.entityData.define(MAX_TENTACLE_COUNT, 0);
        this.entityData.define(TENTACLE_COUNT, 0);
        this.entityData.define(BOSSBAR_UUID, bossBar == null ? Optional.empty() : Optional.of(bossBar.getId()));
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.getRunningGoals().forEach(WrappedGoal::stop);
        this.goalSelector.removeAllGoals(g -> true);

        var auraGoal = new ConjunctiviusAuraGoal(this, s -> {
            s.cooldownGetter = () -> this.auraCooldown;
            s.cooldownSetter = (cooldown) -> this.auraCooldown = cooldown;
            s.stateSetter = this::switchAuraState;
            s.chargeSound = MineCellsSounds.SHOCKER_CHARGE.get();
            s.releaseSound = MineCellsSounds.SHOCKER_RELEASE.get();
            s.soundVolume = 2.0F;
            s.damage = getDamage(1f);
            s.radius = 8.0D;
            s.defaultCooldown = 400;
            s.actionTick = 30;
            s.chance = 0.05F;
            s.length = 60;
        });

        var dashGoal = new ConjunctiviusDashGoal(this, s -> {
            s.cooldownGetter = () -> this.dashCooldown;
            s.cooldownSetter = (cooldown) -> this.dashCooldown = cooldown;
            s.stateSetter = this::switchDashState;
            s.chargeSound = MineCellsSounds.CONJUNCTIVIUS_DASH_CHARGE.get();
            s.releaseSound = MineCellsSounds.CONJUNCTIVIUS_DASH_RELEASE.get();
            s.soundVolume = 2.0F;
            s.defaultCooldown = getStageAdjustedValue(300, 250, 200, 150);
            s.actionTick = getStageAdjustedValue(40, 35, 30, 30);
            s.chance = 0.1F;
            s.length = getStageAdjustedValue(105, 90, 83, 65);
        }, getStageAdjustedValue(50, 45, 40, 30));

        this.goalSelector.addGoal(4, dashGoal);
        this.goalSelector.addGoal(9, auraGoal);
        this.goalSelector.addGoal(10, new ConjunctiviusMoveAroundGoal(this));
        addStageGoals(getStage());

        if (targetSelector.getAvailableGoals().isEmpty()) {
            this.targetSelector.addGoal(0, new ConjunctiviusTargetGoal(this));
            this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Pig.class, false));
        }
    }

    public void addStageGoals(int stage) {
        if (stage >= 3) {
            this.goalSelector.addGoal(2, new ConjunctiviusBarrageGoal.Targeted(this, s -> {
                s.chance = 0.2F;
                s.length = getStageAdjustedValue(60, 80, 100, 120);
                s.interval = getStageAdjustedValue(8, 6, 5, 4);
                s.cooldown = getStageAdjustedValue(20 * 16, 20 * 14, 20 * 12, 20 * 10);
            }));
            this.goalSelector.addGoal(2, new ConjunctiviusBarrageGoal.Around(this, s -> {
                s.chance = 0.05F;
                s.length = getStageAdjustedValue(20, 26, 32, 37);
                s.interval = getStageAdjustedValue(8, 6, 5, 4);
                s.cooldown = 40;
                s.speed = 0.1f;
                s.count = () -> random.nextIntBetweenInclusive(2, 5);
                s.minPause = getStageAdjustedValue(10, 8, 6, 4);
                s.maxPause = getStageAdjustedValue(20, 16, 12, 8);
            }));
        }
    }

    @Override
    public void tick() {
        this.yBodyRot = this.spawnRot;
        this.yBodyRotO = this.spawnRot;
        this.setYRot(this.spawnRot);

        super.tick();

        if (level().isClientSide) {
            calculateEyeOffset();
            if (this.getDashState() != TimedActionGoal.State.IDLE || this.getAuraState() == TimedActionGoal.State.RELEASE) {
                this.spikeOffset.setupTransitionTo(0.0F, 10.0F, MathUtils::easeInQuad);
            } else {
                this.spikeOffset.setupTransitionTo(5.0F, 30.0F, MathUtils::easeInOutQuad);
            }
            this.spawnParticles();

            var blinkTime = this.getBlinkTicks();
            if (blinkTime > 0) {
                this.blinkTimer = Math.max(blinkTime, this.blinkTimer);
            } else {
                this.blinkTimer = Math.max(0, this.blinkTimer - 1);
            }

            if (this.blinkTimer > 0) {
                this.eyeBlink.setupTransitionTo(4.0F, 1.5F);
            } else {
                this.eyeBlink.setupTransitionTo(0.0F, 4.0F);
            }
        } else {
            for (Entity e : level().getEntities(this, this.getBoundingBox().inflate(0.2D))) {
                if (e instanceof LivingEntity livingEntity && !(e instanceof SewersTentacleEntity)) {
                    var lastHit = this.hitEntities.getOrDefault(livingEntity, 0);
                    if (tickCount - lastHit < 40) continue;
                    livingEntity.hurt(level().damageSources().mobAttack(this), getDamage(0.5f));
                    this.hitEntities.put(livingEntity, tickCount);
                }
            }
            BlockPos.betweenClosedStream(this.blockPosition().offset(-3, -4, -3), this.blockPosition().offset(3, 4, 3)).forEach((blockPos) -> {
                if (level().getBlockState(blockPos).is(MineCellsBlockTags.CONJUNCTIVIUS_BREAKABLE)) {
                    level().destroyBlock(blockPos, true);
                }
            });

            if (!level().getBlockState(this.blockPosition().below(2)).isAir()) {
                this.move(MoverType.SELF, new Vec3(0.0D, 0.1D, 0.0D));
            }

            if (this.tickCount % 20 == 0 && this.roomBox != null) {
                entityData.set(BOSSBAR_UUID, Optional.of(this.bossBar.getId()));
                boolean closestPlayerNearby = level().getNearestPlayer(this, 32.0D) != null;
                List<Player> playersInArea = level().getEntitiesOfClass(Player.class, AABB.of(this.roomBox).inflate(2.0D));
                this.bossBar.setVisible(closestPlayerNearby && !playersInArea.isEmpty());

                this.switchStages(this.getStage());

                if (!this.isInFullStage()) {
                    var tentacles = level().getEntitiesOfClass(SewersTentacleEntity.class, AABB.of(roomBox.inflatedBy(10)), Entity::isAlive);
                    if (tentacles.size() != entityData.get(TENTACLE_COUNT)) {
                        this.entityData.set(TENTACLE_COUNT, tentacles.size());
                    }

                    if (this.stageTicks > 30 && tentacles.isEmpty() && this.getStage() != 0) {
                        this.setStage(this.getStage() + 1);
                    } else if (this.getStage() != 0) {
                        this.addEffect(new MobEffectInstance(MineCellsStatusEffects.PROTECTED.get(), 30, 0, false, false));
                    }
                } else {
                    entityData.set(TENTACLE_COUNT, 0);
                    entityData.set(MAX_TENTACLE_COUNT, 0);
                }

                this.updateBossBarForPlayers();
            }
        }

        int stage = this.getStage();
        if (stage != this.lastStage) {
            this.stageTicks = 0;
        }
        this.lastStage = stage;
        this.stageTicks++;
    }

    private void calculateEyeOffset() {
        this.lastEyeOffset = this.eyeOffset;

        Vec3 targetPos = ClientUtil.getClientCameraPos();
        var targetId = this.entityData.get(TARGET_ID);
        if (targetId != -1) {
            var entity = level().getEntity(targetId);
            if (entity != null) targetPos = entity.position();
        }

        Vec3 entityPos = this.position().add(0.0D, 2.5D, 0.0D);
        Vec3 diff = targetPos.subtract(entityPos);
        float rotation = this.yBodyRot;

        Vec3 rotatedDiff = MathUtils.vectorRotateY(diff, rotation * Mth.DEG_TO_RAD + Mth.HALF_PI);
        float xOffset = (float) -rotatedDiff.x;
        float yOffset = (float) -rotatedDiff.y;
        float distance = 1.0F - ((float) rotatedDiff.z - 2.5F) / 30.0F;
        distance = Mth.clamp(distance, 0.25F, 1.0F);

        xOffset *= distance * 0.5F;
        yOffset *= distance * 0.5F;

        if (getEyeState() == SHAKING) {
            xOffset += (this.random.nextFloat() - 0.5F) * 2.0F;
            yOffset += (this.random.nextFloat() - 0.5F) * 2.0F;
            this.eyeEasing = MathUtils::easeOutBack;
        } else {
            this.eyeEasing = MathUtils::lerp;
        }

        xOffset = Mth.clamp(xOffset, -6F, 6F);
        yOffset = Mth.clamp(yOffset, -4F, 4F);
        this.eyeOffset = new Vec3(xOffset, yOffset, 0.0D);
    }

    public Vec3 getEyeOffset(float tickDelta) {
        return MathUtils.interpolateVec(this.lastEyeOffset, this.eyeOffset, tickDelta, this.eyeEasing);
    }

    private int getBlinkTicks() {
        var targetId = this.entityData.get(TARGET_ID);
        if (targetId != lastTargetId) {
            lastTargetId = targetId;
            return 4;
        }
        if (hurtTime == hurtDuration - 1) return 3;
        if (this.tickCount % (20 * 20) == 0) return 5;
        if (this.deathTime > 40 || this.getStage() == 0) return 1;
        return 0;
    }

    public int getEyelidFrame(float progress) {
        return (int) this.eyeBlink.update(progress);
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        super.setTarget(target);
        this.entityData.set(TARGET_ID, target == null ? -1 : target.getId());
    }

    @Override
    protected void tickDeath() {
        if (level().isClientSide) {
            int interval = this.deathTime >= 55 ? 1 : 10;
            if (this.deathTime % interval == 0) {
                level().addParticle(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY() + 2.5D, this.getZ(), 0.0D, 0.0D, 0.0D);
            }
        } else {
            if (this.deathTime == 1) {
                this.playSound(MineCellsSounds.CONJUNCTIVIUS_DYING.get(), 2.0F, 1.0F);
                ScreenShakeUtils.shakeAround((ServerLevel) level(), this.position(), 0.5f, 80, 30, 40, "minecells:conjunctivius_death");
            }
            if (this.deathTime == 60) {
                ScreenShakeUtils.shakeAround((ServerLevel) level(), this.position(), 1f, 40, 30, 40, "minecells:conjunctivius_death");
                this.playSound(MineCellsSounds.CONJUNCTIVIUS_DEATH.get(), 2.0F, 1.0F);
            }
            if (deathTime >= 60) {
                this.remove(RemovalReason.KILLED);
            }
        }
        this.deathTime++;
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
        if (level().isClientSide || level().getServer() == null || this.roomBox == null) {
            return;
        }
        var advancement = level().getServer().getAdvancements().getAdvancement(MineCells.id("conjunctivius"));
        if (advancement == null) {
            return;
        }
        level().getEntitiesOfClass(Player.class, AABB.of(this.getRoomBox().inflatedBy(10))).forEach(player -> {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.getAdvancements().award(advancement, "conjunctivius_killed");
            }
        });
    }

    protected void switchStages(int stage) {
        if (stage == 0 && this.getTarget() != null) {
            this.setStage(1);
            return;
        }
        float healthPercent = this.getHealth() / this.getMaxHealth();
        if (stage == 1 && healthPercent <= 0.8F) {
            this.spawnTentacles();
            this.setStage(2);
            return;
        }
        if (stage == 3 && healthPercent <= 0.55F) {
            this.spawnTentacles();
            this.setStage(4);
            return;
        }
        if (stage == 5 && healthPercent <= 0.3F) {
            this.spawnTentacles();
            this.setStage(6);
        }
    }

    protected void spawnTentacles() {
        if (this.roomBox == null) {
            return;
        }
        int playerAmount = level().getEntitiesOfClass(Player.class, AABB.of(this.roomBox).inflate(4.0D)).size();
        var tentacleCount = 2 + 2 * playerAmount;
        for (int i = 0; i < tentacleCount; i++) {
            SewersTentacleEntity tentacle = MineCellsEntities.SEWERS_TENTACLE.get().create(level());
            if (tentacle != null) {
                tentacle.setVariant(this.getStage() == 1 ? 0 : this.getStage() == 3 ? 1 : 2);
                tentacle.setPos(this.getTentaclePos());
                tentacle.setSpawnedByBoss(true);
                level().addFreshEntity(tentacle);
            }
        }
        entityData.set(MAX_TENTACLE_COUNT, tentacleCount);
        entityData.set(TENTACLE_COUNT, tentacleCount);
        this.updateBossBarForPlayers();
    }

    private void updateBossBarForPlayers() {
        int tentacleCount = this.entityData.get(TENTACLE_COUNT);
        int maxTentacleCount = this.entityData.get(MAX_TENTACLE_COUNT);
        for (ServerPlayer player : this.bossBar.getPlayers()) {
            MineCellsNetwork.sendToPlayer(player, new UpdateConjunctiviusBossBarS2CPacket(this.bossBar.getId(), tentacleCount, maxTentacleCount)
            );
        }
    }

    private Vec3 getTentaclePos() {
        BlockPos center = this.roomBox.getCenter().offset(
            this.random.nextInt(16) - 8,
            0,
            this.random.nextInt(16) - 8
        );
        return Vec3.atCenterOf(center);
    }

    protected void spawnParticles() {
        Vec3 pos = this.position().add(0.0D, this.getBbHeight() * 0.5D, 0.0D);
        if (this.getAuraState() == TimedActionGoal.State.CHARGE) {
            ParticleUtils.addAura(level(), pos, MineCellsParticles.AURA.get(), 2, 7.5D, -0.01D);
        } else if (this.getAuraState() == TimedActionGoal.State.RELEASE) {
            ParticleUtils.addAura(level(), pos, MineCellsParticles.AURA.get(), 50, 7.0D, 0.01D);
            ParticleUtils.addAura(level(), pos, MineCellsParticles.AURA.get(), 10, 1.0D, 0.5D);
        }

        if ((this.getEyeState() == SHAKING || this.tickCount % 5 == 0) && random.nextFloat() < 0.33f) {
            ParticleUtils.addInBox(
                level(),
                ParticleTypes.FALLING_WATER,
                AABB.ofSize(position().add(0.0, 0.25, 0.0), 2.0, 0.5, 2.0),
                this.getEyeState() == SHAKING ? 3 : 1,
                Vec3.ZERO
            );
        }

        int stage = this.getStage();
        if (this.isInFullStage() || stage == 0) {
            return;
        }
        if (this.stageTicks == 1) {
            Vec3 offset = switch (stage) {
                case 2 -> new Vec3(2.2D, -0.85D, 0.0D);
                case 4 -> new Vec3(-2.2D, -0.85D, 0.0D);
                default -> new Vec3(0.0D, 1.75D, 0.0D);
            };
            Vec3 target = switch (stage) {
                case 2 -> Vec3.atCenterOf(this.getRightAnchor());
                case 4 -> Vec3.atCenterOf(this.getLeftAnchor());
                default -> Vec3.atCenterOf(this.getTopAnchor());
            };
            this.spawnChainBreakingParticles(offset, target);
        }
    }

    private void spawnChainBreakingParticles(Vec3 offset, Vec3 target) {
        ParticleOptions particle = new BlockParticleOption(ParticleTypes.BLOCK, MineCellsBlocks.BIG_CHAIN.get().defaultBlockState());
        Vec3 diff = target.subtract(this.position().add(offset));
        double length = diff.length();
        for (int i = 0; i < length; i++) {
            Vec3 pos = this.position().add(offset).add(diff.scale(i / length));
            level().addParticle(particle, pos.x, pos.y, pos.z, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    protected void decrementCooldowns() {
        this.dashCooldown = Math.max(0, this.dashCooldown - 1);
        this.auraCooldown = Math.max(0, this.auraCooldown - 1);
        this.barrageCooldown = Math.max(0, this.barrageCooldown - 1);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypeTags.IS_PROJECTILE)) {
            return super.hurt(source, amount * 0.5F);
        }
        return super.hurt(source, amount);
    }

    protected void switchDashState(TimedActionGoal.State state, boolean value) {
        switch (state) {
            case CHARGE -> this.entityData.set(DASH_CHARGING, value);
            case RELEASE -> this.entityData.set(DASH_RELEASING, value);
            default -> {
            }
        }
    }

    protected void switchAuraState(TimedActionGoal.State state, boolean value) {
        switch (state) {
            case CHARGE -> this.entityData.set(AURA_CHARGING, value);
            case RELEASE -> this.entityData.set(AURA_RELEASING, value);
            default -> {
            }
        }
    }

    public TimedActionGoal.State getDashState() {
        return this.getStateFromTrackedData(DASH_CHARGING, DASH_RELEASING);
    }

    public TimedActionGoal.State getAuraState() {
        return this.getStateFromTrackedData(AURA_CHARGING, AURA_RELEASING);
    }

    protected TimedActionGoal.State getStateFromTrackedData(EntityDataAccessor<Boolean> charging, EntityDataAccessor<Boolean> releasing) {
        if (this.entityData.get(charging)) {
            return TimedActionGoal.State.CHARGE;
        }
        if (this.entityData.get(releasing)) {
            return TimedActionGoal.State.RELEASE;
        }
        return TimedActionGoal.State.IDLE;
    }

    public void setAnchors(BlockPos top, BlockPos left, BlockPos right) {
        this.entityData.set(ANCHOR_TOP, top);
        this.entityData.set(ANCHOR_LEFT, left);
        this.entityData.set(ANCHOR_RIGHT, right);
    }

    public BlockPos getTopAnchor() { return this.entityData.get(ANCHOR_TOP); }
    public BlockPos getLeftAnchor() { return this.entityData.get(ANCHOR_LEFT); }
    public BlockPos getRightAnchor() { return this.entityData.get(ANCHOR_RIGHT); }
    public int getStage() { return this.entityData.get(STAGE); }

    public boolean isInFullStage() {
        int stage = this.getStage();
        return stage == 1 || stage == 3 || stage == 5 || stage == 7;
    }

    public boolean canAttack() {
        return this.isInFullStage() && this.stageTicks > 40;
    }

    public void setStage(int stage) {
        if (stage != this.getStage()) {
            this.playSound(MineCellsSounds.CONJUNCTIVIUS_SHOUT.get(), 2.0F, 1.0F);
            if (level() instanceof ServerLevel serverLevel) {
                ScreenShakeUtils.shakeAround(serverLevel, this.position(), 1f, 50, 20, 40, "minecells:conjunctivius_roar");
            }
            this.entityData.set(STAGE, stage);
            this.registerGoals();
        }
    }

    public EyeState getEyeState() {
        boolean stageBeginning = this.stageTicks > 0 && this.stageTicks < 30;
        if (stageBeginning || !this.isAlive()) {
            return SHAKING;
        }
        if (this.entityData.get(BARRAGE_ACTIVE)) {
            return EyeState.GREEN;
        }
        if (this.getDashState() != TimedActionGoal.State.IDLE) {
            return EyeState.YELLOW;
        }
        return EyeState.PINK;
    }

    public <T> T getStageAdjustedValue(T stage1, T stage3, T stage5, T stage7) {
        int stage = this.getStage();
        return switch (stage) {
            case 3, 4 -> stage3;
            case 5, 6 -> stage5;
            case 7 -> stage7;
            default -> stage1;
        };
    }

    public static AttributeSupplier.Builder createConjunctiviusAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.ATTACK_DAMAGE, 8.0D)
            .add(Attributes.FOLLOW_RANGE, 64.0D)
            .add(Attributes.MAX_HEALTH, 400.0D)
            .add(Attributes.ARMOR, 10.0D)
            .add(Attributes.ARMOR_TOUGHNESS, 6.0D)
            .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
            .add(Attributes.ATTACK_KNOCKBACK, 5.0D);
    }

    public BoundingBox getRoomBox() { return this.roomBox; }
    public Vec3 getSpawnPos() { return this.spawnPos; }

    public Vec3 getDashTarget() {
        Vector3f v = this.entityData.get(DASH_TARGET);
        return new Vec3(v.x(), v.y(), v.z());
    }

    public boolean isForDisplay() { return this.entityData.get(FOR_DISPLAY); }

    @Override
    public boolean isPushable() { return false; }

    @Override
    public boolean causeFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) { return false; }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) { }

    @Override
    public void travel(Vec3 movementInput) {
        if (this.isEffectiveAi() || this.isControlledByLocalInstance()) {
            this.moveRelative(0.02F, movementInput);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.85D));
        }
    }

    @Override
    public void setPos(double x, double y, double z) {
        BoundingBox box = this.getRoomBox();
        if (box == null) {
            super.setPos(x, y, z);
            return;
        }
        super.setPos(
            Mth.clamp(x, box.minX(), box.maxX()),
            Mth.clamp(y, box.minY(), box.maxY()),
            Mth.clamp(z, box.minZ(), box.maxZ())
        );
    }

    @Override
    public boolean onClimbable() { return false; }

    @Override
    public int getMaxHeadYRot() { return 360; }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("dashCooldown", this.dashCooldown);
        nbt.putIntArray("spawnPos", new int[]{(int) this.spawnPos.x, (int) this.spawnPos.y, (int) this.spawnPos.z});
        if (this.roomBox != null) {
            nbt.putIntArray("roomBox", new int[]{
                this.roomBox.minX(), this.roomBox.minY(), this.roomBox.minZ(),
                this.roomBox.maxX(), this.roomBox.maxY(), this.roomBox.maxZ()
            });
        }
        nbt.putFloat("spawnRot", this.spawnRot);
        nbt.putIntArray("anchors", new int[]{
            this.getTopAnchor().getX(), this.getTopAnchor().getY(), this.getTopAnchor().getZ(),
            this.getLeftAnchor().getX(), this.getLeftAnchor().getY(), this.getLeftAnchor().getZ(),
            this.getRightAnchor().getX(), this.getRightAnchor().getY(), this.getRightAnchor().getZ(),
        });
        nbt.putInt("stage", this.entityData.get(STAGE));
        nbt.putInt("stageTicks", this.stageTicks);
        nbt.putBoolean("forDisplay", this.isForDisplay());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.dashCooldown = nbt.getInt("dashCooldown");
        if (nbt.contains("spawnPos") && nbt.getIntArray("spawnPos").length == 3) {
            int[] posArray = nbt.getIntArray("spawnPos");
            this.spawnPos = new Vec3(posArray[0], posArray[1], posArray[2]);
        }
        if (nbt.contains("roomBox") && nbt.getIntArray("roomBox").length == 6) {
            int[] boxArray = nbt.getIntArray("roomBox");
            this.roomBox = new BoundingBox(boxArray[0], boxArray[1], boxArray[2], boxArray[3], boxArray[4], boxArray[5]);
        }
        this.spawnRot = nbt.getFloat("spawnRot");
        this.direction = Direction.fromYRot(this.spawnRot);
        if (nbt.contains("anchors") && nbt.getIntArray("anchors").length == 9) {
            int[] anchors = nbt.getIntArray("anchors");
            this.setAnchors(
                new BlockPos(anchors[0], anchors[1], anchors[2]),
                new BlockPos(anchors[3], anchors[4], anchors[5]),
                new BlockPos(anchors[6], anchors[7], anchors[8])
            );
        }
        this.setStage(nbt.getInt("stage"));
        this.stageTicks = nbt.getInt("stageTicks");
        this.entityData.set(FOR_DISPLAY, nbt.getBoolean("forDisplay"));
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return MineCellsSounds.CONJUNCTIVIUS_HIT.get();
    }

    public enum EyeState {
        SHAKING(-1), PINK(0), YELLOW(1), GREEN(2), BLUE(3);
        public final int index;
        EyeState(int index) { this.index = index; }
    }

    protected static class ConjunctiviusMoveControl extends MoveControl {
        public ConjunctiviusMoveControl(ConjunctiviusEntity entity) { super(entity); }

        @Override
        public void tick() {
            if (this.operation == Operation.MOVE_TO) {
                Vec3 vec3d = new Vec3(this.wantedX - this.mob.getX(), this.wantedY - this.mob.getY(), this.wantedZ - this.mob.getZ());
                double d = vec3d.length();
                vec3d = vec3d.normalize();
                if (!this.willCollide(vec3d, Mth.ceil(d))) {
                    this.operation = Operation.WAIT;
                }
            }
        }

        private boolean willCollide(Vec3 direction, int steps) {
            AABB box = this.mob.getBoundingBox();
            box = new AABB(box.minX, this.mob.getY() - 1.0D, box.minZ, box.maxX, box.maxY, box.maxZ);
            for (int i = 1; i < steps; ++i) {
                box = box.move(direction);
                if (!this.mob.level().noCollision(this.mob, box)) {
                    return false;
                }
            }
            return true;
        }
    }

    protected static class ConjunctiviusDashGoal extends TimedActionGoal<ConjunctiviusEntity> {
        private Vec3 startPos;
        private Vec3 targetPos;
        private final int restTime;

        public ConjunctiviusDashGoal(ConjunctiviusEntity entity, Consumer<TimedActionSettings> settings, int restTime) {
            super(entity, settings, it -> it.canAttack() && !it.moving && it.getTarget() != null);
            this.setFlags(EnumSet.of(Flag.MOVE));
            this.startPos = entity.position();
            this.targetPos = entity.position();
            this.restTime = restTime;
        }

        @Override
        public void start() {
            super.start();
            startPos = this.entity.position();
            var target = this.entity.getTarget();
            targetPos = target == null ? startPos : target.position();
            entity.getEntityData().set(DASH_TARGET, new Vector3f((float) targetPos.x, (float) targetPos.y + 1, (float) targetPos.z));
        }

        @Override
        protected void release() {
            var delta = (this.ticks() - this.actionTick) / (float) (this.length - this.actionTick - restTime);
            var tickPos = MathUtils.interpolateVec(startPos, targetPos, delta, MathUtils::easeInOutCubic);

            if (this.ticks() <= this.length - restTime + 2) {
                for (Entity e : entity.level().getEntities(entity, entity.getBoundingBox().inflate(0.75))) {
                    if (e instanceof LivingEntity livingEntity) {
                        livingEntity.hurt(entity.level().damageSources().mobAttack(entity), entity.getDamage(2f));
                        entity.hitEntities.put(livingEntity, entity.tickCount);
                    }
                }
            }

            if (this.ticks() == this.length - restTime && this.entity.level() instanceof ServerLevel serverWorld) {
                serverWorld.sendParticles(ParticleTypes.EXPLOSION_EMITTER, targetPos.x, targetPos.y, targetPos.z, 2, 2.0, 2.0, 2.0, 0.0);
                ScreenShakeUtils.shakeAround(serverWorld, tickPos, 1f, 30, 20, 40D, "minecells:conjunctivius_smash");
                serverWorld.playSound(null, entity.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 2.0F, 1.0F);
            }

            this.entity.setPos(tickPos.x, tickPos.y, tickPos.z);
        }

        @Override
        public void stop() {
            super.stop();
            if (this.entity.getSpawnPos() != null) {
                this.entity.setDeltaMovement(this.entity.getSpawnPos().subtract(this.entity.position()).normalize());
            }
        }
    }

    protected static class ConjunctiviusAuraGoal extends TimedAuraGoal<ConjunctiviusEntity> {
        public ConjunctiviusAuraGoal(ConjunctiviusEntity entity, Consumer<TimedAuraSettings> settings) {
            super(entity, settings, null);
        }

        @Override
        public boolean canUse() {
            return super.canUse()
                && this.entity.canAttack()
                && !this.entity.moving
                && this.entity.dashCooldown > this.length
                && !this.entity.level().getEntitiesOfClass(Player.class, this.entity.getBoundingBox().inflate(6.0D)).isEmpty();
        }
    }
}
