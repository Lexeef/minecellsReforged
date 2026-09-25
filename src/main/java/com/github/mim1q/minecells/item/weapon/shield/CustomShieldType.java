package com.github.mim1q.minecells.item.weapon.shield;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.registry.MineCellsStatusEffects;
import com.github.mim1q.minecells.valuecalculators.ValueCalculator;
import com.github.mim1q.minecells.valuecalculators.ValueCalculatorContext;
import com.github.mim1q.minecells.valuecalculators.ValueCalculators;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;

public enum CustomShieldType {
    CUDGEL(0.25F, 20, 2, 6.0F, 90.0F, 90.0F, 5, null),
    RAMPART(0.8F, 20, 2, 6.0F, 100.0F, 90.0F, 5, null),
    ASSAULT(0.6F, 80, 20, 6.0F, 90.0F, 360.0F, 5, null),
    BLOOD(0.5F, 20, 2, 6.0F, 90.0F, 90.0F, 5, null),
    GREED(0.5F, 20, 2, 6.0F, 90.0F, 90.0F, 5, null),
    ICE(0.5F, 20, 2, 6.0F, 90.0F, 90.0F, 5, ParticleTypes.SNOWFLAKE);

    private final ValueCalculator blockDamageReduction;
    private final ValueCalculator cooldown;
    private final ValueCalculator cooldownAfterParry;
    private final ValueCalculator parryDamage;
    private final float blockAngle;
    private final float parryAngle;
    private final int parryTime;
    @Nullable
    private final ParticleOptions particle;

    CustomShieldType(
        float blockDamageReduction,
        int cooldown,
        int cooldownAfterParry,
        float parryDamage,
        float blockAngle,
        float parryAngle,
        int parryTime,
        @Nullable ParticleOptions particle
    ) {
        String path = "shields/" + name().toLowerCase(Locale.ROOT);
        this.blockDamageReduction = ValueCalculators.of(path, "block_damage_reduction", fallback(() -> Defaults.BLOCK_DAMAGE_REDUCTION, blockDamageReduction));
        this.cooldown = ValueCalculators.of(path, "cooldown", fallback(() -> Defaults.COOLDOWN, cooldown));
        this.cooldownAfterParry = ValueCalculators.of(path, "cooldown_after_parry", fallback(() -> Defaults.COOLDOWN_AFTER_PARRY, cooldownAfterParry));
        this.parryDamage = ValueCalculators.of(path, "parry_damage", fallback(() -> Defaults.PARRY_DAMAGE, parryDamage));
        this.blockAngle = blockAngle;
        this.parryAngle = parryAngle;
        this.parryTime = parryTime;
        this.particle = particle;
    }

    private static ToDoubleFunction<ValueCalculatorContext> fallback(Supplier<ValueCalculator> defaultCalculator, double hardcoded) {
        return context -> ValueCalculators.isLoaded() ? defaultCalculator.get().calculate(context) : hardcoded;
    }

    public float getBlockDamageReduction(@Nullable LivingEntity holder, @Nullable ItemStack stack, @Nullable LivingEntity attacker) {
        return (float) blockDamageReduction.calculate(ValueCalculatorContext.of(holder, stack, attacker));
    }

    public int getCooldown(@Nullable LivingEntity holder, @Nullable ItemStack stack, boolean parried) {
        return (int) (parried ? cooldownAfterParry : cooldown).calculate(ValueCalculatorContext.of(holder, stack));
    }

    public float getParryDamage(@Nullable LivingEntity holder, @Nullable ItemStack stack, @Nullable LivingEntity attacker) {
        return (float) parryDamage.calculate(ValueCalculatorContext.of(holder, stack, attacker));
    }

    private static final class Defaults {
        private static final ValueCalculator BLOCK_DAMAGE_REDUCTION = ValueCalculators.of("shields/default", "block_damage_reduction", 0.5D);
        private static final ValueCalculator COOLDOWN = ValueCalculators.of("shields/default", "cooldown", 20.0D);
        private static final ValueCalculator COOLDOWN_AFTER_PARRY = ValueCalculators.of("shields/default", "cooldown_after_parry", 2.0D);
        private static final ValueCalculator PARRY_DAMAGE = ValueCalculators.of("shields/default", "parry_damage", 6.0D);
    }

    public float getBlockAngle() {
        return blockAngle;
    }

    public float getParryAngle() {
        return parryAngle;
    }

    public int getParryTime() {
        return parryTime;
    }

    @Nullable
    public ParticleOptions getParticle() {
        return particle;
    }

    public void onUse(Level level, Player player, InteractionHand hand, ItemStack stack) {
        if (this != ASSAULT || level.isClientSide) {
            return;
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(), MineCellsSounds.LEAPING_ZOMBIE_RELEASE.get(), player.getSoundSource(), 1.0F, 1.0F);

        Vec3 launch = player.getLookAngle()
            .multiply(1.0D, 0.0D, 1.0D)
            .normalize()
            .scale(1.5D)
            .add(0.0D, 0.2D, 0.0D);
        if (Double.isFinite(launch.x) && Double.isFinite(launch.z)) {
            player.setDeltaMovement(launch);
            player.hurtMarked = true;
        }

        stack.hurtAndBreak(1, player, brokenPlayer -> brokenPlayer.broadcastBreakEvent(hand));
    }

    public void onHold(Level level, Player player, int useTicks) {
        if (this != ASSAULT || level.isClientSide || useTicks >= 10) {
            return;
        }

        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(1.0D), target -> target != player)) {
            entity.knockback(0.5D, entity.getX() - player.getX(), entity.getZ() - player.getZ());
            entity.hurt(level.damageSources().playerAttack(player), 8.0F);
        }
    }

    public void onParry(Level level, Player player) {
        if (this == BLOOD) {
            applyBleedingAround(level, player, player.position(), 60, 1);
        }
    }

    public void onMeleeParry(Level level, Player player, LivingEntity attacker, net.minecraft.world.damagesource.DamageSource source) {
        switch (this) {
            case CUDGEL -> attacker.addEffect(new MobEffectInstance(MineCellsStatusEffects.STUNNED.get(), 30, 0, false, false, true));
            case RAMPART -> player.addEffect(new MobEffectInstance(MineCellsStatusEffects.PROTECTED.get(), 30, 0, false, false, true));
            case BLOOD -> {
                applyBleedingAround(level, player, attacker.position(), 60, 1);
                attacker.addEffect(new MobEffectInstance(MineCellsStatusEffects.BLEEDING.get(), 100, 2, false, false, true));
            }
            case ICE -> attacker.addEffect(new MobEffectInstance(MineCellsStatusEffects.FROZEN.get(), 100, 0, false, false, true));
            case GREED -> spawnGreedLoot(player, attacker, source);
            default -> {
            }
        }
    }

    public void onRangedParry(Level level, Player player, LivingEntity attacker, Projectile projectile, net.minecraft.world.damagesource.DamageSource source) {
    }

    public void onBlock(Level level, Player player) {
    }

    public void onMeleeBlock(Level level, Player player, LivingEntity attacker) {
        switch (this) {
            case CUDGEL -> attacker.addEffect(new MobEffectInstance(MineCellsStatusEffects.STUNNED.get(), 5, 0, false, false, true));
            case ICE -> attacker.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30, 0, false, false, true));
            default -> {
            }
        }
    }

    public void onRangedBlock(Level level, Player player, LivingEntity attacker, Projectile projectile) {
    }

    private static void applyBleedingAround(Level level, Player player, Vec3 center, int duration, int amplifier) {
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, AABB.ofSize(center, 8.0D, 8.0D, 8.0D), target ->
            target != player && target.distanceToSqr(center) <= 16.0D
        )) {
            entity.addEffect(new MobEffectInstance(MineCellsStatusEffects.BLEEDING.get(), duration, amplifier, false, false, true));
        }
    }

    private static void spawnGreedLoot(Player player, LivingEntity attacker, net.minecraft.world.damagesource.DamageSource source) {
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        LootTable lootTable = serverLevel.getServer().getLootData().getLootTable(MineCells.id("gameplay/greed_shield_parry"));
        LootParams lootParams = new LootParams.Builder(serverLevel)
            .withParameter(LootContextParams.THIS_ENTITY, attacker)
            .withParameter(LootContextParams.ORIGIN, attacker.position())
            .withParameter(LootContextParams.DAMAGE_SOURCE, source)
            .withOptionalParameter(LootContextParams.KILLER_ENTITY, player)
            .withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, player)
            .create(LootContextParamSets.ENTITY);

        lootTable.getRandomItems(lootParams, stack -> {
            ItemEntity itemEntity = new ItemEntity(serverLevel, attacker.getX(), attacker.getY() + attacker.getBbHeight() * 0.5D, attacker.getZ(), stack);
            itemEntity.setPickUpDelay(20);
            serverLevel.addFreshEntity(itemEntity);
        });
    }
}
