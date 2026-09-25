package com.github.mim1q.minecells.command;

import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.github.mim1q.minecells.structure.grid.SpecialPointIds;
import com.github.mim1q.minecells.util.TeleportUtils;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.RegisterCommandsEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;

public final class MineCellsTeleportCommand {
    private MineCellsTeleportCommand() {
    }

    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(
            Commands.literal("minecells:tp")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("dimension", StringArgumentType.string())
                    .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(
                        Arrays.stream(MineCellsDimension.values()).map(dimension -> dimension.id().getPath()),
                        builder
                    ))
                    .executes(teleport(false, false, false))
                    .then(Commands.argument("to_exit", BoolArgumentType.bool())
                        .executes(teleport(false, false, true))
                        .then(Commands.argument("player", EntityArgument.player())
                            .executes(teleport(true, false, true))
                            .then(Commands.argument("position", BlockPosArgument.blockPos())
                                .executes(teleport(true, true, true))
                            )
                        )
                    )
                )
        );
    }

    private static Command<CommandSourceStack> teleport(boolean specifiedPlayer, boolean specifiedPosition, boolean specifiedToExit) {
        return ctx -> {
            Optional<MineCellsDimension> dimension = getDimension(StringArgumentType.getString(ctx, "dimension"));
            if (dimension.isEmpty()) {
                ctx.getSource().sendFailure(Component.literal("Invalid dimension"));
                return 0;
            }
            ServerPlayer player = specifiedPlayer ? EntityArgument.getPlayer(ctx, "player") : ctx.getSource().getPlayerOrException();
            BlockPos position = specifiedPosition ? BlockPosArgument.getBlockPos(ctx, "position") : null;
            boolean toExit = specifiedToExit && BoolArgumentType.getBool(ctx, "to_exit");
            return teleportPlayer(ctx.getSource(), dimension.get(), player, position, toExit ? SpecialPointIds.EXIT : SpecialPointIds.ENTRANCE);
        };
    }

    private static int teleportPlayer(
        CommandSourceStack source,
        MineCellsDimension dimension,
        ServerPlayer player,
        @Nullable BlockPos positionOverride,
        ResourceLocation specialPoint
    ) throws CommandSyntaxException {
        ServerLevel originLevel = source.getLevel();
        ServerLevel destination = dimension.getLevel(originLevel);
        if (destination == null) {
            source.sendFailure(Component.literal("Dimension is not loaded: " + dimension.id()));
            return 0;
        }

        Vec3 target;
        float yaw;
        if (dimension == MineCellsDimension.OVERWORLD) {
            BlockPos respawn = player.getRespawnPosition();
            if (respawn != null && player.getRespawnDimension() == Level.OVERWORLD) {
                target = Vec3.atBottomCenterOf(respawn);
                yaw = player.getRespawnAngle();
            } else {
                target = Vec3.atBottomCenterOf(destination.getSharedSpawnPos());
                yaw = 0.0F;
            }
        } else {
            BlockPos origin = positionOverride != null ? positionOverride : player.blockPosition();
            target = dimension.getTeleportPosition(origin, originLevel, specialPoint);
            yaw = dimension.getTeleportYaw(origin, originLevel, specialPoint);
        }

        TeleportUtils.teleportToDimension(player, destination, target, yaw);
        source.sendSuccess(() -> Component.literal("Teleported " + player.getScoreboardName() + " to " + dimension.id()), true);
        return 1;
    }

    private static Optional<MineCellsDimension> getDimension(String name) {
        return Arrays.stream(MineCellsDimension.values())
            .filter(dimension -> dimension.id().getPath().equals(name))
            .findFirst();
    }
}
