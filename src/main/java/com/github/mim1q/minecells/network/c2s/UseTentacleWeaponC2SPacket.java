package com.github.mim1q.minecells.network.c2s;

import com.github.mim1q.minecells.config.MineCellsConfig;
import com.github.mim1q.minecells.entity.nonliving.TentacleWeaponEntity;
import com.github.mim1q.minecells.item.weapon.TentacleItem;
import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.registry.MineCellsItems;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record UseTentacleWeaponC2SPacket(Vec3 targetPos) {
    public static void encode(UseTentacleWeaponC2SPacket packet, FriendlyByteBuf buffer) {
        buffer.writeDouble(packet.targetPos.x);
        buffer.writeDouble(packet.targetPos.y);
        buffer.writeDouble(packet.targetPos.z);
    }

    public static UseTentacleWeaponC2SPacket decode(FriendlyByteBuf buffer) {
        return new UseTentacleWeaponC2SPacket(new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()));
    }

    public static void handle(UseTentacleWeaponC2SPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }

            ItemStack held = player.getMainHandItem();
            double maxDistance = MineCellsConfig.COMMON.baseTentacleMaxDistance.get() + 2.0D;
            if (!(held.getItem() instanceof TentacleItem)
                || packet.targetPos.distanceToSqr(player.position()) > maxDistance * maxDistance) {
                MineCells.LOGGER.warn("Invalid tentacle weapon use packet from player {}", player.getName().getString());
                return;
            }

            TentacleWeaponEntity tentacle = TentacleWeaponEntity.create(player.level(), player, packet.targetPos, held);
            if (tentacle != null) {
                player.level().addFreshEntity(tentacle);
            }
            player.getCooldowns().addCooldown(MineCellsItems.TENTACLE.get(), ((TentacleItem) held.getItem()).getAbilityCooldown(held, player));
        });
        context.setPacketHandled(true);
    }
}
