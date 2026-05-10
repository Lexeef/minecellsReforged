package com.github.mim1q.minecells.network.s2c;

import com.github.mim1q.minecells.registry.MineCellsRecipeTypes;
import com.github.mim1q.minecells.recipe.CellForgeRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SendUnlockedCellCrafterRecipesS2CPacket {
    private final Map<ResourceLocation, Boolean> unlockedRecipes;

    public SendUnlockedCellCrafterRecipesS2CPacket(ServerPlayer player) {
        this.unlockedRecipes = new LinkedHashMap<>();
        var recipes = player.server.getRecipeManager().getAllRecipesFor(MineCellsRecipeTypes.CELL_FORGE_RECIPE_TYPE.get());
        for (CellForgeRecipe recipe : recipes) {
            unlockedRecipes.put(recipe.getId(), recipe.isUnlockedFor(player));
        }
    }

    public SendUnlockedCellCrafterRecipesS2CPacket(Map<ResourceLocation, Boolean> unlockedRecipes) {
        this.unlockedRecipes = unlockedRecipes;
    }

    public static void encode(SendUnlockedCellCrafterRecipesS2CPacket packet, FriendlyByteBuf buffer) {
        buffer.writeMap(packet.unlockedRecipes, FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::writeBoolean);
    }

    public static SendUnlockedCellCrafterRecipesS2CPacket decode(FriendlyByteBuf buffer) {
        return new SendUnlockedCellCrafterRecipesS2CPacket(
            buffer.readMap(FriendlyByteBuf::readResourceLocation, FriendlyByteBuf::readBoolean)
        );
    }

    public static void handle(SendUnlockedCellCrafterRecipesS2CPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            try {
                Class<?> handlerClass = Class.forName("com.github.mim1q.minecells.client.MineCellsClientPacketHandlers");
                handlerClass.getMethod("handleUnlockedCellCrafterRecipes", Map.class).invoke(null, packet.unlockedRecipes);
            } catch (ReflectiveOperationException exception) {
                throw new RuntimeException("Failed to handle unlocked Cell Crafter recipes on the client", exception);
            }
        });
        context.setPacketHandled(true);
    }

    public static void send(ServerPlayer player) {
        com.github.mim1q.minecells.network.MineCellsNetwork.CHANNEL.send(
            PacketDistributor.PLAYER.with(() -> player),
            new SendUnlockedCellCrafterRecipesS2CPacket(player)
        );
    }
}
