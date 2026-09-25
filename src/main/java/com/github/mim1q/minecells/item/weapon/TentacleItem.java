package com.github.mim1q.minecells.item.weapon;

import com.github.mim1q.minecells.config.MineCellsSyncedConfig;
import com.github.mim1q.minecells.item.weapon.interfaces.WeaponWithAbility;
import com.github.mim1q.minecells.network.MineCellsNetwork;
import com.github.mim1q.minecells.network.c2s.UseTentacleWeaponC2SPacket;
import com.github.mim1q.minecells.registry.MineCellsEntities;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.valuecalculators.ValueCalculator;
import com.github.mim1q.minecells.valuecalculators.ValueCalculators;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class TentacleItem extends CustomMeleeWeaponItem implements WeaponWithAbility {
    public static final ValueCalculator ABILITY_DAMAGE = ValueCalculators.of("melee/conjunctivius_tentacle", "ability_damage", 4.0D);
    private static final ValueCalculator ABILITY_COOLDOWN = ValueCalculators.of("melee/conjunctivius_tentacle", "ability_cooldown", 0.5D);

    @OnlyIn(Dist.CLIENT)
    private HitResult clientHitResult;

    public TentacleItem(Properties properties) {
        super("conjunctivius_tentacle", 8.0D, 1.2D, 0.0F, 6.0F, properties.rarity(Rarity.EPIC));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResultHolder.pass(stack);
        }

        if (level.isClientSide) {
            HitResult hit = clientHitResult;
            if (hit == null || hit.getType() == HitResult.Type.MISS) {
                return InteractionResultHolder.pass(stack);
            }

            Vec3 target = hit.getLocation();
            if (hit.getType() == HitResult.Type.ENTITY) {
                Entity targetEntity = ((EntityHitResult) hit).getEntity();
                target = targetEntity.position().add(0.0D, targetEntity.getBbHeight() / 2.0D, 0.0D);
            } else if (hit.getType() == HitResult.Type.BLOCK) {
                target = Vec3.atCenterOf(((BlockHitResult) hit).getBlockPos());
            }

            level.playLocalSound(player.getX(), player.getY(), player.getZ(), MineCellsSounds.TENTACLE_CHARGE.get(), SoundSource.PLAYERS, 1.0F, 1.0F, false);
            MineCellsNetwork.CHANNEL.sendToServer(new UseTentacleWeaponC2SPacket(target));
            return InteractionResultHolder.success(stack);
        }

        return InteractionResultHolder.pass(stack);
    }

    @Override
    public ValueCalculator getAbilityDamageCalculator() {
        return ABILITY_DAMAGE;
    }

    @Override
    public ValueCalculator getAbilityCooldownCalculator() {
        return ABILITY_COOLDOWN;
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    public HitResult getClientHitResult() {
        return clientHitResult;
    }

    @Override
    public boolean canCrit(ItemStack stack, @Nullable LivingEntity target, LivingEntity attacker) {
        return attacker.getPassengers().stream().anyMatch(passenger -> passenger.getType() == MineCellsEntities.TENTACLE_WEAPON.get());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide || !(entity instanceof Player player)) {
            return;
        }
        if (!player.isLocalPlayer() || player.getCooldowns().isOnCooldown(this)) {
            return;
        }

        updateClientHitResult(player);
    }

    @OnlyIn(Dist.CLIENT)
    private void updateClientHitResult(Player player) {
        double maxDistance = MineCellsSyncedConfig.baseTentacleMaxDistance();
        double minDistance = 3.0D;
        Vec3 eye = player.getEyePosition(1.0F);
        Vec3 targetPos = eye.add(player.getViewVector(0.5F).scale(maxDistance));
        AABB searchBox = AABB.ofSize(eye, maxDistance * 2.0D, maxDistance * 2.0D, maxDistance * 2.0D);

        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
            player,
            eye,
            targetPos,
            searchBox,
            candidate -> candidate != null && candidate.isPickable() && candidate.isAlive(),
            maxDistance * maxDistance
        );
        if (entityHit != null
            && entityHit.getType() != HitResult.Type.MISS
            && entityHit.getLocation().distanceToSqr(eye) >= minDistance * minDistance) {
            clientHitResult = entityHit;
            return;
        }

        BlockHitResult blockHit = player.level().clip(new ClipContext(
            eye,
            targetPos,
            ClipContext.Block.COLLIDER,
            ClipContext.Fluid.NONE,
            player
        ));
        if (blockHit.getType() != HitResult.Type.MISS
            && blockHit.getLocation().distanceToSqr(player.position()) > minDistance * minDistance) {
            clientHitResult = blockHit;
        } else {
            clientHitResult = null;
        }
    }
}
