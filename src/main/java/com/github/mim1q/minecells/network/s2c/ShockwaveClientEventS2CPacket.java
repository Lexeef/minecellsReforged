package com.github.mim1q.minecells.network.s2c;

import com.github.mim1q.minecells.client.ShockwaveClientEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ShockwaveClientEventS2CPacket {
    private final ResourceLocation blockId;
    private final BlockPos blockPos;
    private final boolean end;

    public ShockwaveClientEventS2CPacket(Block block, BlockPos blockPos, boolean end) {
        this(ForgeRegistries.BLOCKS.getKey(block), blockPos, end);
    }

    public ShockwaveClientEventS2CPacket(ResourceLocation blockId, BlockPos blockPos, boolean end) {
        this.blockId = blockId;
        this.blockPos = blockPos;
        this.end = end;
    }

    public static void encode(ShockwaveClientEventS2CPacket packet, FriendlyByteBuf buf) {
        buf.writeResourceLocation(packet.blockId);
        buf.writeBlockPos(packet.blockPos);
        buf.writeBoolean(packet.end);
    }

    public static ShockwaveClientEventS2CPacket decode(FriendlyByteBuf buf) {
        return new ShockwaveClientEventS2CPacket(buf.readResourceLocation(), buf.readBlockPos(), buf.readBoolean());
    }

    public static void handle(ShockwaveClientEventS2CPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            ClientLevel level = minecraft.level;
            if (level == null) {
                return;
            }
            Block block = ForgeRegistries.BLOCKS.getValue(packet.blockId);
            if (block != null) {
                ShockwaveClientEffects.play(packet.blockPos, block, packet.end, level);
            }
        });
        context.setPacketHandled(true);
    }
}
