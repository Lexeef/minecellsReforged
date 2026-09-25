package com.github.mim1q.minecells.item;

import com.github.mim1q.minecells.block.blockentity.SpawnerRuneBlockEntity;
import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.github.mim1q.minecells.entity.nonliving.SpawnerRuneEntity;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ResetRuneItem extends Item {
    private static final int RESET_CHUNK_RADIUS = 8;

    public ResetRuneItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (livingEntity instanceof ServerPlayer player && level instanceof ServerLevel serverLevel) {
            resetNearbySpawnerRunes(serverLevel, player.chunkPosition());

            player.getCooldowns().addCooldown(this, 20 * 120);
            level.playSound(null, player.getX(), player.getY(), player.getZ(), MineCellsSounds.TELEPORT_RELEASE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }
        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 40;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged) {
        super.releaseUsing(stack, level, livingEntity, timeCharged);
        if (livingEntity instanceof Player player) {
            player.getCooldowns().addCooldown(this, 40);
        }
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (level.isClientSide) {
            addAura(
                level,
                livingEntity.position().add(0.0D, 1.25D, 0.0D),
                MineCellsParticles.SPECKLE.get().get(0x97FFA7),
                Mth.clamp(20 - remainingUseDuration, 5, 20),
                1.25D,
                -0.05D
            );
        }
        super.onUseTick(level, livingEntity, stack, remainingUseDuration);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(this) || !MineCellsDimension.isMineCellsDimension(level)) {
            return InteractionResultHolder.fail(stack);
        }

        level.playLocalSound(player.getX(), player.getY(), player.getZ(), MineCellsSounds.TELEPORT_CHARGE.get(), SoundSource.PLAYERS, 1.0F, 1.0F, true);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.minecells.reset_rune.tooltip").withStyle(ChatFormatting.GRAY));
    }

    private static void resetNearbySpawnerRunes(ServerLevel level, ChunkPos center) {
        for (int x = center.x - RESET_CHUNK_RADIUS; x <= center.x + RESET_CHUNK_RADIUS; x++) {
            for (int z = center.z - RESET_CHUNK_RADIUS; z <= center.z + RESET_CHUNK_RADIUS; z++) {
                LevelChunk chunk = level.getChunkSource().getChunkNow(x, z);
                if (chunk == null) {
                    continue;
                }
                for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                    if (blockEntity instanceof SpawnerRuneBlockEntity rune) {
                        rune.controller.resetActivation(level, rune.getBlockPos());
                        rune.setChanged();
                    }
                }
            }
        }
        int blockRadius = (RESET_CHUNK_RADIUS + 1) * 16;
        AABB box = new AABB(center.getMinBlockX(), level.getMinBuildHeight(), center.getMinBlockZ(), center.getMaxBlockX() + 1, level.getMaxBuildHeight(), center.getMaxBlockZ() + 1)
            .inflate(blockRadius, 0.0D, blockRadius);
        for (SpawnerRuneEntity rune : level.getEntitiesOfClass(SpawnerRuneEntity.class, box)) {
            rune.controller.resetActivation(level, rune.blockPosition());
        }
    }

    private static void addAura(Level level, Vec3 position, ParticleOptions particle, int amount, double radius, double speed) {
        for (int i = 0; i < amount; i++) {
            Vec3 offset = new Vec3(
                level.random.nextDouble() * 2.0D - 1.0D,
                level.random.nextDouble() * 2.0D - 1.0D,
                level.random.nextDouble() * 2.0D - 1.0D
            ).normalize();
            Vec3 velocity = offset.scale(speed);
            Vec3 finalOffset = offset.scale(radius);
            Vec3 particlePos = position.add(finalOffset);
            level.addParticle(particle, particlePos.x, particlePos.y, particlePos.z, velocity.x, velocity.y, velocity.z);
        }
    }
}
