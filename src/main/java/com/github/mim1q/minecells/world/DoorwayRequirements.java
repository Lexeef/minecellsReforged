package com.github.mim1q.minecells.world;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.portal.DoorwayPortalBlock.DoorwayType;
import com.github.mim1q.minecells.util.SyncedAdvancements;
import com.github.mim1q.minecells.world.state.MineCellsData;
import net.minecraft.advancements.Advancement;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public final class DoorwayRequirements {
    public static final ResourceLocation VINE_RUNE_ADVANCEMENT = MineCells.id("vine_rune");

    private DoorwayRequirements() {
    }

    public static boolean isMet(DoorwayType type, Player player, MineCellsData.PlayerData playerData) {
        return switch (type) {
            case OVERWORLD, PRISON -> true;
            case PROMENADE, RAMPARTS, BLACK_BRIDGE -> playerData.hasVisitedDimension(type.dimensionId());
            case INSUFFERABLE_CRYPT -> hasVineRune(player);
        };
    }

    public static List<Component> getTooltips(DoorwayType type) {
        return switch (type) {
            case OVERWORLD, PRISON -> List.of();
            case PROMENADE, RAMPARTS, BLACK_BRIDGE -> List.of(Component.translatable(
                "gui.minecells.doorway_selection.requirement.visit",
                Component.translatable("dimension." + type.dimensionId().getNamespace() + "." + type.dimensionId().getPath())
            ));
            case INSUFFERABLE_CRYPT -> List.of(Component.translatable(
                "gui.minecells.doorway_selection.requirement.advancement." + VINE_RUNE_ADVANCEMENT.getPath()
            ));
        };
    }

    public static boolean hasVineRune(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            Advancement advancement = serverPlayer.server.getAdvancements().getAdvancement(VINE_RUNE_ADVANCEMENT);
            return advancement != null && serverPlayer.getAdvancements().getOrStartProgress(advancement).isDone();
        }
        return player.level().isClientSide && SyncedAdvancements.isCompleted(VINE_RUNE_ADVANCEMENT);
    }
}
