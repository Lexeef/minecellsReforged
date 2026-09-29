package com.github.mim1q.minecells.item.weapon.bow;

import com.github.mim1q.minecells.effect.BleedingMobEffect;
import com.github.mim1q.minecells.entity.damage.MineCellsDamageSource;
import com.github.mim1q.minecells.entity.nonliving.projectile.CustomArrowEntity;
import com.github.mim1q.minecells.registry.MineCellsItems;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsStatusEffects;
import com.github.mim1q.minecells.util.MineCellsExplosion;
import com.github.mim1q.minecells.valuecalculators.ValueCalculator;
import com.github.mim1q.minecells.valuecalculators.ValueCalculatorContext;
import com.github.mim1q.minecells.valuecalculators.ValueCalculators;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Stats come from {@code data/minecells/value_calculators/ranged/<name>.json}; missing equations fall back to
 * {@code ranged/default}, and the hardcoded values below are only used before any data pack has loaded.
 */
public class CustomArrowType {
    private static final HashMap<String, CustomArrowType> ARROW_TYPES = new HashMap<>();
    private static final ValueCalculator GLOBAL_EXTRA_DAMAGE = ValueCalculators.of("ranged/global", "global_extra_damage", context -> {
        int power = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, context.stack());
        Double base = context.variable("BASE_DAMAGE");
        Double crit = context.variable("CRIT_DAMAGE");
        return ((base == null ? 0.0D : base) + (crit == null ? 0.0D : crit)) * 0.2D * power;
    });

    public static final CustomArrowType DEFAULT = create("default", it -> {
        it.damage = 5.0F;
        it.critDamage = 0.0F;
        it.drawTimeSeconds = 1.0F;
        it.speed = 2.0F;
        it.spread = 1.0F;
        it.cooldownSeconds = 0.0F;
        it.quickChargeReduction = 0.2F;
    });

    public static final CustomArrowType MARKSMAN = create("marksman", it -> {
        it.damage = 5.0F;
        it.critDamage = 11.0F;
        it.drawTimeSeconds = 1.2F;
        it.speed = 3.0F;
        it.spread = 1.0F;
        it.quickChargeReduction = 0.33F;
        it.shouldCrit = context -> context.shotFromPos.distanceToSqr(context.hitPos) > 24 * 24;
    });

    public static final CustomArrowType INFANTRY = create("infantry", it -> {
        it.damage = 6.0F;
        it.critDamage = 6.0F;
        it.speed = 2.0F;
        it.shouldCrit = context -> context.shotFromPos.distanceToSqr(context.hitPos) < 10 * 10;
    });

    public static final CustomArrowType ICE = create("ice", it -> {
        it.damage = 4.0F;
        it.speed = 2.0F;
        it.spread = 1.0F;
        it.onEntityHit = context -> context.target.addEffect(
            new MobEffectInstance(MineCellsStatusEffects.FROZEN.get(), 100)
        );
        it.particle = ParticleTypes.SNOWFLAKE;
        it.ammo = () -> MineCellsItems.ICE_ARROW.get();
    });

    public static final CustomArrowType EXPLOSIVE_BOLT = create("explosive_bolt", it -> {
        it.damage = 0.0F;
        it.speed = 2.0F;
        it.spread = 1.0F;
        it.onBlockHit = context -> {
            MineCellsExplosion.explode(context.world, context.arrow, context.shooter, context.hitPos, 10.0F, 4.0F, Objects::nonNull);
            context.arrow.discard();
        };
        it.onEntityHit = context -> {
            MineCellsExplosion.explode(context.world, context.arrow, context.shooter, context.hitPos, 10.0F, 4.0F, Objects::nonNull);
            context.arrow.discard();
        };
        it.particle = ParticleTypes.SMOKE;
        it.ammo = () -> MineCellsItems.EXPLOSIVE_BOLT.get();
    });

    public static final CustomArrowType QUICK = create("quick", it -> {
        it.damage = 5.0F;
        it.drawTimeSeconds = 0.3F;
        it.speed = 2.2F;
        it.spread = 3.0F;
        it.damageSourceFactory = (level, arrow, shooter) -> MineCellsDamageSource.HEAVY_BOLT.get(level, arrow, shooter);
    });

    public static final CustomArrowType NERVES_OF_STEEL = create("nerves_of_steel", it -> {
        it.damage = 5.0F;
        it.critDamage = 9.0F;
        it.drawTimeSeconds = 0.5F;
        it.speed = 2.0F;
        it.spread = 1.0F;
        it.shouldCrit = context -> context.bow().getOrCreateTag().getBoolean("crit");
    });

    public static final CustomArrowType HEAVY_BOLT = create("heavy_bolt", it -> {
        it.damage = 5.0F;
        it.critDamage = 0.0F;
        it.drawTimeSeconds = 1.6F;
        it.speed = 0.6F;
        it.spread = 45.0F;
        it.maxAge = 15;
        it.quickChargeReduction = 0.2F;
        it.damageSourceFactory = (level, arrow, shooter) -> MineCellsDamageSource.HEAVY_BOLT.get(level, arrow, shooter);
    });

    public static final CustomArrowType MULTIPLE_NOCKS = create("multiple_nocks", it -> {
        it.damage = 5.0F;
        it.speed = 2.0F;
        it.spread = 1.0F;
    });

    public static final CustomArrowType ENDLESS = create("endless", it -> {
        it.damage = 5.0F;
        it.speed = 2.0F;
        it.spread = 1.0F;
        it.ammo = () -> null;
    });

    public static final CustomArrowType FIREBRANDS = create("firebrands", it -> {
        it.damage = 4.0F;
        it.drawTimeSeconds = 0.0F;
        it.speed = 1.0F;
        it.cooldownSeconds = 1.0F;
        it.onEntityHit = context -> context.target.setSecondsOnFire(5);
        it.onBlockHit = context -> {
            placeFire(context);
            context.arrow.discard();
        };
        it.particle = ParticleTypes.FLAME;
    });

    public static final CustomArrowType THROWING_KNIFE = create("throwing_knife", it -> {
        it.damage = 4.0F;
        it.drawTimeSeconds = 0.0F;
        it.speed = 1.75F;
        it.spread = 0.5F;
        it.cooldownSeconds = 0.25F;
        it.onEntityHit = context -> BleedingMobEffect.apply(context.target, 20 * 4);
        it.ammo = () -> MineCellsItems.THROWING_KNIFE.get();
        it.particle = MineCellsParticles.DROP.get().get(0xDD3000);
        it.damageSourceFactory = (level, arrow, shooter) -> MineCellsDamageSource.HEAVY_BOLT.get(level, arrow, shooter);
    });

    private static void placeFire(ArrowBlockHitContext context) {
        BlockPos firePos = context.hitBlockPos().relative(context.hitFace());
        BlockPlaceContext placeContext = new BlockPlaceContext(
            context.shooter(),
            context.shooter().getUsedItemHand(),
            context.bow(),
            new BlockHitResult(context.hitPos(), context.hitFace(), context.hitBlockPos(), false)
        );
        var fireState = Blocks.FIRE.getStateForPlacement(placeContext);
        if (fireState != null && context.world.getBlockState(firePos).canBeReplaced()) {
            context.world.setBlock(firePos, fireState, 3);
        }
    }

    private final String name;
    private final String translationKey;
    private float damage = 5.0F;
    private float critDamage = 0.0F;
    private float drawTimeSeconds = 1.0F;
    private float speed = 2.0F;
    private float spread = 1.0F;
    private float cooldownSeconds = 0.0F;
    private float quickChargeReduction = 0.2F;
    private int maxAge = 60 * 20;
    private ParticleOptions particle = null;
    private Consumer<ArrowEntityHitContext> onEntityHit = context -> {
    };
    private Consumer<ArrowBlockHitContext> onBlockHit = context -> {
    };
    private Function<ArrowEntityHitContext, Boolean> shouldCrit = context -> false;
    private DamageSourceFactory damageSourceFactory = (level, arrow, shooter) -> level.damageSources().mobProjectile(arrow, shooter);
    private Supplier<Item> ammo = () -> Items.ARROW;
    private ValueCalculator damageCalculator;
    private ValueCalculator critDamageCalculator;
    private ValueCalculator drawTimeCalculator;
    private ValueCalculator speedCalculator;
    private ValueCalculator spreadCalculator;
    private ValueCalculator cooldownCalculator;

    private CustomArrowType(String name) {
        this.name = name;
        this.translationKey = "entity.minecells.custom_arrow." + name;
        if (!"default".equals(name) && DEFAULT != null) {
            this.damage = DEFAULT.damage;
            this.critDamage = DEFAULT.critDamage;
            this.drawTimeSeconds = DEFAULT.drawTimeSeconds;
            this.speed = DEFAULT.speed;
            this.spread = DEFAULT.spread;
            this.cooldownSeconds = DEFAULT.cooldownSeconds;
            this.quickChargeReduction = DEFAULT.quickChargeReduction;
        }
    }

    public void onEntityHit(ArrowEntityHitContext context) {
        onEntityHit.accept(context);
    }

    public void onBlockHit(ArrowBlockHitContext context) {
        onBlockHit.accept(context);
    }

    private void initCalculators() {
        String path = "ranged/" + name;
        boolean base = "default".equals(name);
        damageCalculator = ValueCalculators.of(path, "damage", fallback(base, () -> DEFAULT.damageCalculator, context -> damage));
        critDamageCalculator = ValueCalculators.of(path, "crit_damage", fallback(base, () -> DEFAULT.critDamageCalculator, context -> critDamage));
        drawTimeCalculator = ValueCalculators.of(path, "draw_time", fallback(base, () -> DEFAULT.drawTimeCalculator, context ->
            drawTimeSeconds - quickChargeReduction * EnchantmentHelper.getItemEnchantmentLevel(Enchantments.QUICK_CHARGE, context.stack())
        ));
        speedCalculator = ValueCalculators.of(path, "speed", fallback(base, () -> DEFAULT.speedCalculator, context -> speed));
        spreadCalculator = ValueCalculators.of(path, "spread", fallback(base, () -> DEFAULT.spreadCalculator, context -> spread));
        cooldownCalculator = ValueCalculators.of(path, "cooldown", fallback(base, () -> DEFAULT.cooldownCalculator, context -> cooldownSeconds));
    }

    private static ToDoubleFunction<ValueCalculatorContext> fallback(
        boolean base,
        Supplier<ValueCalculator> defaultCalculator,
        ToDoubleFunction<ValueCalculatorContext> hardcoded
    ) {
        if (base) {
            return hardcoded;
        }
        return context -> ValueCalculators.isLoaded() ? defaultCalculator.get().calculate(context) : hardcoded.applyAsDouble(context);
    }

    public int getDrawTime(@Nullable LivingEntity user, @Nullable ItemStack stack) {
        return Math.max(0, (int) (20.0D * drawTimeCalculator.calculate(ValueCalculatorContext.of(user, stack))));
    }

    public boolean shouldCrit(ArrowEntityHitContext context) {
        return shouldCrit.apply(context);
    }

    public float getDamage(@Nullable LivingEntity holder, @Nullable ItemStack bow, @Nullable LivingEntity target) {
        return (float) damageCalculator.calculate(ValueCalculatorContext.of(holder, bow, target));
    }

    public float getAdditionalCritDamage(@Nullable LivingEntity holder, @Nullable ItemStack bow, @Nullable LivingEntity target) {
        return (float) critDamageCalculator.calculate(ValueCalculatorContext.of(holder, bow, target));
    }

    public float getGlobalExtraDamage(@Nullable LivingEntity holder, @Nullable ItemStack bow, @Nullable LivingEntity target, float baseDamage, float additionalCrit) {
        return (float) GLOBAL_EXTRA_DAMAGE.calculate(
            ValueCalculatorContext.of(holder, bow, target).with("BASE_DAMAGE", baseDamage).with("CRIT_DAMAGE", additionalCrit)
        );
    }

    public String getName() {
        return name;
    }

    public ParticleOptions getParticle() {
        return particle;
    }

    public float getSpeed(@Nullable LivingEntity holder, @Nullable ItemStack bow) {
        return (float) speedCalculator.calculate(ValueCalculatorContext.of(holder, bow));
    }

    public int getMaxAge() {
        return maxAge;
    }

    public float getSpread(@Nullable LivingEntity holder, @Nullable ItemStack bow) {
        return (float) spreadCalculator.calculate(ValueCalculatorContext.of(holder, bow));
    }

    public int getCooldown(@Nullable LivingEntity holder, @Nullable ItemStack bow) {
        return (int) (cooldownCalculator.calculate(ValueCalculatorContext.of(holder, bow)) * 20.0D);
    }

    public Optional<Item> getAmmoItem() {
        return Optional.ofNullable(ammo.get());
    }

    public DamageSource getDamageSource(Level level, CustomArrowEntity arrow, LivingEntity shooter) {
        return damageSourceFactory.create(level, arrow, shooter);
    }

    public Component getTranslation() {
        return Component.translatable(translationKey);
    }

    protected static CustomArrowType create(String name, Consumer<CustomArrowType> setup) {
        CustomArrowType arrowType = new CustomArrowType(name);
        setup.accept(arrowType);
        arrowType.initCalculators();
        ARROW_TYPES.put(name, arrowType);
        return arrowType;
    }

    protected static CustomArrowType create(String name) {
        return create(name, it -> {
        });
    }

    public static CustomArrowType get(String name) {
        return ARROW_TYPES.getOrDefault(name, DEFAULT);
    }

    public static Set<String> getAllNames() {
        return ARROW_TYPES.keySet();
    }

    @FunctionalInterface
    public interface DamageSourceFactory {
        DamageSource create(Level level, CustomArrowEntity arrow, LivingEntity shooter);
    }

    public record ArrowEntityHitContext(
        ServerLevel world,
        ItemStack bow,
        Player shooter,
        LivingEntity target,
        Vec3 shotFromPos,
        Vec3 hitPos,
        CustomArrowEntity arrow
    ) {
    }

    public record ArrowBlockHitContext(
        ServerLevel world,
        ItemStack bow,
        Player shooter,
        Vec3 shotFromPos,
        BlockPos hitBlockPos,
        Vec3 hitPos,
        Direction hitFace,
        CustomArrowEntity arrow
    ) {
    }
}
