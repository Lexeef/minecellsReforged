package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.github.mim1q.minecells.item.CellHolderItem;
import com.github.mim1q.minecells.util.MineCellsCombatHelper;
import com.github.mim1q.minecells.util.TeleportUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Forge replacements for Fabric's status-effect mixins: {@code LivingEntityMixin} (frozen cleared by damage),
 * {@code MobEntityMixin} (frozen/stunned mobs stop their AI), {@code ServerPlayerEntityMixin} (Assassin's Strength
 * consumed by attacking, suffocation fix), {@code BlockItemMixin} / {@code PlayerEntityMixin} (Disarmed blocks
 * placing and breaking) and {@code PlayerInventoryMixin} (monster cells go straight into a Cell Holder).
 */
@Mod.EventBusSubscriber(modid = MineCells.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class MineCellsEffectEvents {
    private static final int SUFFOCATION_FIX_TICKS = 60;
    private static final Set<Player> ATTACKED_THIS_TICK = Collections.newSetFromMap(new WeakHashMap<>());

    private MineCellsEffectEvents() {
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Mob mob) || mob.level().isClientSide || !MineCellsCombatHelper.isFrozenOrStunned(mob)) {
            return;
        }
        mob.goalSelector.disableControlFlag(Goal.Flag.MOVE);
        mob.goalSelector.disableControlFlag(Goal.Flag.LOOK);
        mob.goalSelector.disableControlFlag(Goal.Flag.JUMP);
        mob.targetSelector.disableControlFlag(Goal.Flag.TARGET);
        mob.getNavigation().stop();
        mob.setSpeed(0.0F);
        mob.setZza(0.0F);
        mob.setXxa(0.0F);
        mob.setJumping(false);
        mob.setYHeadRot(mob.yHeadRotO);
        mob.setYRot(mob.yRotO);
        mob.setXRot(mob.xRotO);
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) {
            return;
        }
        if (event.getSource().getDirectEntity() instanceof Mob attacker && attacker == event.getSource().getEntity()
            && MineCellsCombatHelper.isFrozenOrStunned(attacker)) {
            event.setCanceled(true);
            return;
        }
        if (!event.getSource().is(DamageTypeTags.IS_FREEZING)) {
            entity.removeEffect(MineCellsStatusEffects.FROZEN.get());
        }
        if (entity instanceof ServerPlayer player && shouldApplySuffocationFix(player, event)) {
            event.setCanceled(true);
            applySuffocationFix(player);
        }
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        if (!event.getEntity().level().isClientSide) {
            ATTACKED_THIS_TICK.add(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || ATTACKED_THIS_TICK.isEmpty()) {
            return;
        }
        for (Player player : ATTACKED_THIS_TICK.toArray(Player[]::new)) {
            player.removeEffect(MineCellsStatusEffects.ASSASSINS_STRENGTH.get());
        }
        ATTACKED_THIS_TICK.clear();
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (isDisarmed(event.getEntity()) && event.getItemStack().getItem() instanceof BlockItem) {
            event.setUseItem(Event.Result.DENY);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (isDisarmed(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (isDisarmed(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (isDisarmed(event.getPlayer())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof Player player && isDisarmed(player)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onItemPickup(EntityItemPickupEvent event) {
        Player player = event.getEntity();
        ItemEntity itemEntity = event.getItem();
        ItemStack stack = itemEntity.getItem();
        if (player.level().isClientSide || !stack.is(MineCellsItems.MONSTER_CELL.get()) || itemEntity.hasPickUpDelay()) {
            return;
        }
        for (ItemStack slotStack : player.getInventory().items) {
            if (slotStack.is(MineCellsItems.CELL_HOLDER.get())) {
                int count = stack.getCount();
                CellHolderItem.setCellCount(slotStack, CellHolderItem.getCellCount(slotStack) + count);
                slotStack.setPopTime(5);
                player.take(itemEntity, count);
                itemEntity.discard();
                event.setCanceled(true);
                return;
            }
        }
    }

    public static boolean isDisarmed(Player player) {
        return player != null && player.hasEffect(MineCellsStatusEffects.DISARMED.get());
    }

    private static boolean shouldApplySuffocationFix(ServerPlayer player, LivingAttackEvent event) {
        return player.tickCount < SUFFOCATION_FIX_TICKS
            && event.getSource().is(DamageTypes.IN_WALL)
            && player.level().getGameRules().getBoolean(MineCellsGameRules.SUFFOCATION_FIX)
            && !player.isCreative()
            && !player.isSpectator()
            && MineCellsDimension.isMineCellsDimension(player.level());
    }

    private static void applySuffocationFix(ServerPlayer player) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            return;
        }
        ServerLevel targetLevel = server.getLevel(player.getRespawnDimension());
        if (targetLevel == null) {
            targetLevel = server.overworld();
        }
        BlockPos spawnPos = player.getRespawnPosition();
        if (spawnPos == null) {
            spawnPos = targetLevel.getSharedSpawnPos();
        }
        TeleportUtils.teleportToDimension(player, targetLevel, Vec3.atCenterOf(spawnPos), player.getRespawnAngle());
        MineCells.LOGGER.info("Teleporting {} to their spawnpoint", player.getName().getString());
        player.sendSystemMessage(Component.literal("[Mine Cells] ").withStyle(ChatFormatting.RED)
            .append(Component.translatable("chat.minecells.suffocation_fix_message").withStyle(ChatFormatting.WHITE)));
    }
}
