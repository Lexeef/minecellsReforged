package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.entity.damage.MineCellsDamageSource;
import com.github.mim1q.minecells.item.weapon.interfaces.CrittingWeapon;
import com.github.mim1q.minecells.item.weapon.shield.CustomShieldItem;
import com.github.mim1q.minecells.item.weapon.shield.CustomShieldType;
import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.util.MineCellsCombatHelper;
import com.github.mim1q.minecells.util.ScreenShakeUtils;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSwapItemsEvent;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber(modid = MineCells.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class MineCellsCombatEvents {
    private static final int SHIELD_REPEAT_IMMUNITY_TICKS = 10;
    private static final Map<Player, ShieldBlockRecord> LAST_SHIELD_BLOCKS = new WeakHashMap<>();
    private static final Set<Player> SHIELD_REDUCED_HITS = Collections.newSetFromMap(new WeakHashMap<>());

    private record ShieldBlockRecord(int tick, LivingEntity attacker) {
    }

    private MineCellsCombatEvents() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onSwapHands(LivingSwapItemsEvent.Hands event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        ItemStack mainHand = event.getItemSwappedToMainHand();
        ItemStack offHand = event.getItemSwappedToOffHand();
        for (ItemStack stack : new ItemStack[] { mainHand, offHand }) {
            if (stack.getItem() instanceof CustomShieldItem shield) {
                player.getCooldowns().addCooldown(shield, shield.getShieldType().getCooldown(player, mainHand, false));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) {
            return;
        }

        Player player = event.player;
        MineCellsCombatHelper.tickBalancedBlade(player);
        MineCellsCombatHelper.tickTemporaryInvulnerability(player);

        if (isHolding(player, MineCellsItems.CURSED_SWORD.get())) {
            player.addEffect(new net.minecraft.world.effect.MobEffectInstance(MineCellsStatusEffects.CURSED.get(), 30, 0, false, false, true));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingAttack(LivingAttackEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (MineCellsCombatHelper.hasTemporaryInvulnerability(player)) {
            event.setCanceled(true);
            return;
        }
        if (player.level().isClientSide) {
            return;
        }
        ShieldBlockRecord record = LAST_SHIELD_BLOCKS.get(player);
        if (record != null
            && record.attacker() != null
            && record.attacker() == event.getSource().getEntity()
            && player.tickCount - record.tick() < SHIELD_REPEAT_IMMUNITY_TICKS
        ) {
            event.setCanceled(true);
            return;
        }
        if (SHIELD_REDUCED_HITS.contains(player)) {
            return;
        }
        tryHandleShield(event, player);
    }

    /**
     * Fabric cancels LivingEntity#isBlocking for CustomShieldItem so vanilla shield math
     * does not fight the custom parry/block handler. Mirror that via ShieldBlockEvent.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onShieldBlock(ShieldBlockEvent event) {
        if (event.getEntity().getUseItem().getItem() instanceof CustomShieldItem) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();

        if (!target.level().isClientSide && shouldTriggerCursedDeath(target, event.getSource())) {
            event.setCanceled(true);
            MineCellsCombatHelper.setProcessingCursedDamage(target, true);
            try {
                target.level().playSound(null, target.getX(), target.getY(), target.getZ(), MineCellsSounds.CURSE_DEATH.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                target.hurt(MineCellsDamageSource.CURSED.get(target.level(), event.getSource().getEntity()), 2048.0F);
            } finally {
                MineCellsCombatHelper.setProcessingCursedDamage(target, false);
            }
            return;
        }

        if (target instanceof Player player && SHIELD_REDUCED_HITS.contains(player)) {
            return;
        }
        event.setAmount(getAdjustedDamage(event.getAmount(), target, event.getSource(), true));
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        MineCellsCombatHelper.recordDamageTime(event.getEntity());
        if (event.getEntity() instanceof Player player) {
            MineCellsCombatHelper.resetBalancedBlade(player);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        if (player.level().isClientSide || event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }

        ItemStack stack = player.getMainHandItem();
        if (!stack.is(MineCellsItems.CROWBAR.get())) {
            return;
        }

        var state = player.level().getBlockState(event.getPos());
        if (!state.is(BlockTags.WOODEN_DOORS)) {
            return;
        }

        if (player.level().getBlockState(event.getPos().below()).is(BlockTags.WOODEN_DOORS)) {
            player.level().destroyBlock(event.getPos().below(), false, player);
        }
        player.level().destroyBlock(event.getPos(), false, player);
        stack.getOrCreateTag().putLong("lastDoorBreakTime", player.level().getGameTime());
        event.setCanceled(true);
    }

    private static boolean shouldTriggerCursedDeath(LivingEntity entity, net.minecraft.world.damagesource.DamageSource source) {
        if (MineCellsCombatHelper.isProcessingCursedDamage(entity) || source.is(MineCellsDamageSource.CURSED.key)) {
            return false;
        }
        return entity.hasEffect(MineCellsStatusEffects.CURSED.get()) || isHolding(entity, MineCellsItems.CURSED_SWORD.get());
    }

    private static void tryHandleShield(LivingAttackEvent event, Player player) {
        if (!player.isUsingItem()) {
            return;
        }

        ItemStack activeStack = player.getUseItem();
        if (!(activeStack.getItem() instanceof CustomShieldItem shieldItem)) {
            return;
        }

        CustomShieldType shieldType = shieldItem.getShieldType();
        int useTicks = activeStack.getUseDuration() - player.getUseItemRemainingTicks();
        boolean isParry = useTicks <= shieldItem.getParryTime();
        float maxAngle = isParry ? shieldType.getParryAngle() : shieldType.getBlockAngle();
        if (CustomShieldItem.getAngleDifference(player, event.getSource()) > maxAngle) {
            return;
        }

        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), net.minecraft.sounds.SoundEvents.SHIELD_BLOCK, player.getSoundSource(), 1.0F, 1.0F);
        activeStack.hurtAndBreak(1, player, brokenPlayer -> brokenPlayer.broadcastBreakEvent(player.getUsedItemHand()));

        float incomingDamage = getAdjustedDamage(event.getAmount(), player, event.getSource(), false);
        if (isParry) {
            shieldType.onParry(player.level(), player);
            applyShieldImpact(event.getSource(), player, activeStack, shieldType, true);
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), MineCellsSounds.CRIT.get(), player.getSoundSource(), 1.0F, 1.0F);
            CustomShieldItem.setParried(activeStack, true);
            event.setCanceled(true);
            return;
        }

        event.setCanceled(true);
        LivingEntity blockedAttacker = event.getSource().getEntity() instanceof LivingEntity living ? living : null;
        float reducedDamage = incomingDamage * (1.0F - shieldType.getBlockDamageReduction(player, activeStack, blockedAttacker));
        SHIELD_REDUCED_HITS.add(player);
        try {
            player.hurt(event.getSource(), reducedDamage);
        } finally {
            SHIELD_REDUCED_HITS.remove(player);
        }
        shieldType.onBlock(player.level(), player);
        applyShieldImpact(event.getSource(), player, activeStack, shieldType, false);
    }

    private static void applyShieldImpact(net.minecraft.world.damagesource.DamageSource source, Player player, ItemStack shieldStack, CustomShieldType shieldType, boolean isParry) {
        LAST_SHIELD_BLOCKS.put(player, new ShieldBlockRecord(
            player.tickCount,
            source.getDirectEntity() instanceof LivingEntity direct ? direct : null
        ));
        spawnShieldParticles(player, shieldType.getParticle(), isParry);
        if (player instanceof ServerPlayer serverPlayer) {
            ScreenShakeUtils.shakePlayer(
                serverPlayer,
                1.0F,
                20,
                isParry ? "minecells:shield_parry" : "minecells:shield_block"
            );
        }

        if (!(source.getEntity() instanceof LivingEntity attacker)) {
            return;
        }

        if (source.getDirectEntity() instanceof Projectile projectile) {
            if (isParry) {
                shieldType.onRangedParry(player.level(), player, attacker, projectile, source);
            } else {
                shieldType.onRangedBlock(player.level(), player, attacker, projectile);
            }
            return;
        }

        if (isParry) {
            attacker.setDeltaMovement(Vec3.ZERO);
            attacker.knockback(1.0D, attacker.getX() - player.getX(), attacker.getZ() - player.getZ());
            attacker.hurt(player.level().damageSources().playerAttack(player), shieldType.getParryDamage(player, shieldStack, attacker));
            shieldType.onMeleeParry(player.level(), player, attacker, source);
        } else {
            shieldType.onMeleeBlock(player.level(), player, attacker);
        }
    }

    private static void spawnShieldParticles(Player player, ParticleOptions particle, boolean isParry) {
        if (particle == null || !(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        Vec3 particlePos = player.position()
            .add(0.0D, player.getBbHeight() * 0.5D, 0.0D)
            .add(player.getLookAngle().normalize().scale(0.5D));
        serverLevel.sendParticles(
            particle,
            particlePos.x,
            particlePos.y,
            particlePos.z,
            isParry ? 10 : 3,
            0.2D,
            0.2D,
            0.2D,
            0.1D
        );
    }

    private static float getAdjustedDamage(float baseDamage, LivingEntity target, net.minecraft.world.damagesource.DamageSource source, boolean playCritSound) {
        if (!(source.getEntity() instanceof Player player)
            || source.getDirectEntity() != player
            || !source.is(net.minecraft.world.damagesource.DamageTypes.PLAYER_ATTACK)
        ) {
            return baseDamage;
        }

        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof CrittingWeapon crittingWeapon)) {
            return baseDamage;
        }

        float damage = baseDamage + crittingWeapon.getExtraDamage(stack, target, player);
        if (!crittingWeapon.canCrit(stack, target, player)) {
            return damage;
        }

        if (playCritSound && crittingWeapon.shouldPlayCritSound(stack, target, player)) {
            target.level().playSound(null, player.getX(), player.getY(), player.getZ(), MineCellsSounds.CRIT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }
        return damage + crittingWeapon.getAdditionalCritDamage(stack, target, player);
    }

    private static boolean isHolding(LivingEntity entity, net.minecraft.world.item.Item item) {
        return entity.getMainHandItem().is(item) || entity.getOffhandItem().is(item);
    }
}
