package com.github.mim1q.minecells.network.c2s;

import com.github.mim1q.minecells.block.blockentity.CellCrafterBlockEntity;
import com.github.mim1q.minecells.recipe.CellForgeRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record CellCrafterCraftRequestC2SPacket(ResourceLocation recipeId, BlockPos blockPos) {
    public static void encode(CellCrafterCraftRequestC2SPacket packet, FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(packet.recipeId);
        buffer.writeBlockPos(packet.blockPos);
    }

    public static CellCrafterCraftRequestC2SPacket decode(FriendlyByteBuf buffer) {
        return new CellCrafterCraftRequestC2SPacket(buffer.readResourceLocation(), buffer.readBlockPos());
    }

    public static void handle(CellCrafterCraftRequestC2SPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }

            if (!(player.level().getBlockEntity(packet.blockPos) instanceof CellCrafterBlockEntity cellCrafter)) {
                return;
            }

            player.server.getRecipeManager().byKey(packet.recipeId)
                .filter(CellForgeRecipe.class::isInstance)
                .map(CellForgeRecipe.class::cast)
                .ifPresent(recipe -> tryCraft(player, cellCrafter, recipe));
        });
        context.setPacketHandled(true);
    }

    private static void tryCraft(ServerPlayer player, CellCrafterBlockEntity cellCrafter, CellForgeRecipe recipe) {
        if (!recipe.isUnlockedFor(player)) {
            return;
        }

        if (player.distanceToSqr(
            cellCrafter.getBlockPos().getX() + 0.5D,
            cellCrafter.getBlockPos().getY() + 0.5D,
            cellCrafter.getBlockPos().getZ() + 0.5D
        ) > 64.0D) {
            return;
        }

        if (!recipe.matchesInventory(player.getInventory(), player.level())) {
            return;
        }

        recipe.consumeIngredients(player.getInventory());
        ItemStack output = recipe.getResultItem(player.level().registryAccess()).copy();
        cellCrafter.addStack(output);
        cellCrafter.setCooldown(10);
    }
}
