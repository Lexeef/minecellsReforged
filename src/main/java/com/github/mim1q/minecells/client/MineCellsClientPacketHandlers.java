package com.github.mim1q.minecells.client;

import com.github.mim1q.minecells.block.blockentity.SpawnerRuneBlockEntity;
import com.github.mim1q.minecells.client.screen.CellCrafterScreen;
import com.github.mim1q.minecells.client.toast.CellCrafterRecipeToast;
import com.github.mim1q.minecells.entity.nonliving.SpawnerRuneEntity;
import com.github.mim1q.minecells.entity.nonliving.obelisk.ObeliskEntity;
import com.github.mim1q.minecells.recipe.CellForgeRecipe;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.util.ParticleUtils;
import com.github.mim1q.minecells.world.state.PlayerSpecificMineCellsData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Map;

/**
 * Client-side bodies of S2C packets. Packet classes are loaded on the dedicated server too, so they must not
 * reference client classes (even as local variable types) directly.
 */
public final class MineCellsClientPacketHandlers {
    private MineCellsClientPacketHandlers() {
    }

    public static void handleUnlockedCellCrafterRecipes(Map<ResourceLocation, Boolean> unlockedRecipes) {
        Minecraft minecraft = Minecraft.getInstance();
        var newlyUnlocked = MineCellsClientData.updateUnlockedCellCrafterRecipes(unlockedRecipes);
        if (minecraft.screen instanceof CellCrafterScreen cellCrafterScreen) {
            cellCrafterScreen.updateUnlockedRecipes(unlockedRecipes);
        }
        if (minecraft.level != null) {
            for (ResourceLocation recipeId : newlyUnlocked) {
                minecraft.level.getRecipeManager().byKey(recipeId)
                    .filter(CellForgeRecipe.class::isInstance)
                    .map(CellForgeRecipe.class::cast)
                    .ifPresent(recipe -> minecraft.getToasts().addToast(new CellCrafterRecipeToast(recipe)));
            }
        }
    }

    public static void handleElevatorDestroyed(double x, double y, double z) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        Vec3 pos = new Vec3(x, y, z);
        ParticleOptions particle = new BlockParticleOption(ParticleTypes.BLOCK, Blocks.OAK_PLANKS.defaultBlockState());
        AABB box = new AABB(pos.add(-1.0D, 0.0D, -1.0D), pos.add(1.0D, 0.5D, 1.0D));
        ParticleUtils.addInBox(level, particle, box, 25, new Vec3(0.1D, 0.1D, 0.1D));
    }

    public static void handleObeliskActivation(int entityId) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        Entity entity = level.getEntity(entityId);
        if (entity instanceof ObeliskEntity obelisk) {
            obelisk.resetActivatedTicks();
        }
    }

    public static void handleSpawnerRuneUpdate(BlockPos pos, long lastActivationTime, float cooldown) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        if (level.getBlockEntity(pos) instanceof SpawnerRuneBlockEntity spawner) {
            spawner.controller.applyClientUpdate(lastActivationTime, cooldown);
            return;
        }
        AABB box = AABB.ofSize(Vec3.atCenterOf(pos), 1.5D, 1.5D, 1.5D);
        level.getEntitiesOfClass(SpawnerRuneEntity.class, box, entity -> entity.blockPosition().equals(pos))
            .stream()
            .findFirst()
            .ifPresent(entity -> entity.controller.applyClientUpdate(lastActivationTime, cooldown));
    }

    public static void handleExplosion(double x, double y, double z, double radius) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        level.addParticle(MineCellsParticles.EXPLOSION.get(), true, x, y, z, 0.0D, 0.0D, 0.0D);
        level.addParticle(ParticleTypes.EXPLOSION, true, x + 0.01D, y + 0.01D, z + 0.01D, 0.0D, 0.0D, 0.0D);
        RandomSource random = level.random;
        for (int i = 0; i < 20; i++) {
            double vx = (random.nextDouble() - 0.5D) * radius;
            double vy = (random.nextDouble() - 0.5D) * radius;
            double vz = (random.nextDouble() - 0.5D) * radius;
            level.addParticle(ParticleTypes.CRIT, true, x, y, z, vx, vy, vz);
        }
    }

    public static void handleSpawnRuneParticles(AABB box) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        for (int i = 0; i < 10; i++) {
            double x = lerp(level.random.nextDouble(), box.minX, box.maxX);
            double y = lerp(level.random.nextDouble(), box.minY, box.maxY);
            double z = lerp(level.random.nextDouble(), box.minZ, box.maxZ);
            Vec3 velocity = new Vec3(-0.1D, -0.1D, -0.1D).scale(level.random.nextDouble() * 0.5D + 0.5D);
            level.addParticle(MineCellsParticles.SPECKLE.get().get(0xFF6A00), x, y, z, velocity.x, velocity.y, velocity.z);
        }

        AABB innerBox = box.inflate(-0.1D, -0.1D, -0.1D);
        for (int i = 0; i < 10; i++) {
            double x = lerp(level.random.nextDouble(), innerBox.minX, innerBox.maxX);
            double y = lerp(level.random.nextDouble(), innerBox.minY, innerBox.maxY);
            double z = lerp(level.random.nextDouble(), innerBox.minZ, innerBox.maxZ);
            Vec3 velocity = new Vec3(-0.02D, -0.02D, -0.02D).scale(level.random.nextDouble() * 0.5D + 0.5D);
            level.addParticle(ParticleTypes.CLOUD, x, y, z, velocity.x, velocity.y, velocity.z);
        }
    }

    public static void handleShockwave(BlockPos pos, Block block, boolean end) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null) {
            ShockwaveClientEffects.play(pos, block, end, level);
        }
    }

    public static void handleSyncPlayerData(CompoundTag tag) {
        if (Minecraft.getInstance().player != null) {
            MineCellsClientData.setPlayerData(new PlayerSpecificMineCellsData(tag));
        }
    }

    public static void handleOpenDoorwayScreen(BlockPos doorwayPos, BlockPos anchor) {
        com.github.mim1q.minecells.client.gui.screen.DoorwaySelectionScreen.open(doorwayPos, anchor);
    }

    public static void handleAdvancementHints(Map<ResourceLocation, Boolean> completed, boolean replace) {
        com.github.mim1q.minecells.util.SyncedAdvancements.apply(completed, replace);
        com.github.mim1q.minecells.client.renderer.misc.AdvancementHintRenderer.applyServerState(completed, replace);
    }

    public static void handleEffectFlags(int entityId, int flags) {
        ClientEffectFlags.set(entityId, flags);
    }

    private static double lerp(double delta, double min, double max) {
        return min + delta * (max - min);
    }
}
