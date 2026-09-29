package com.github.mim1q.minecells.command;

import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.github.mim1q.minecells.structure.grid.GridBasedStructureUtils;
import com.github.mim1q.minecells.structure.grid.GridPiecesGenerator.RoomGridGenerator.SpecialPoint;
import com.github.mim1q.minecells.util.MathUtils;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

import net.minecraftforge.event.RegisterCommandsEvent;

import java.util.Arrays;
import java.util.Optional;

public final class SpecialPointCommand {
    private SpecialPointCommand() {
    }

    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("minecells:special_point")
            .requires(source -> source.hasPermission(2))
            .then(Commands.argument("id", ResourceLocationArgument.id())
                .executes(find(false, false, false))
                .then(Commands.argument("dimension", StringArgumentType.string())
                    .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(
                        Arrays.stream(MineCellsDimension.values()).map(dimension -> dimension.id().getPath()),
                        builder
                    ))
                    .executes(find(true, false, false))
                    .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        .executes(find(true, true, false))
                        .then(Commands.argument("clear", BoolArgumentType.bool())
                            .executes(find(true, true, true))))))
        );

        event.getDispatcher().register(Commands.literal("minecells:clear_special_point_cache")
            .requires(source -> source.hasPermission(2))
            .executes(ctx -> {
                GridBasedStructureUtils.clearSpecialPointsCache();
                ctx.getSource().sendSuccess(() -> Component.literal("Cleared special point cache"), true);
                return 1;
            }));
    }

    private static Command<CommandSourceStack> find(boolean hasDimension, boolean hasPos, boolean hasClear) {
        return ctx -> {
            ResourceLocation id = ResourceLocationArgument.getId(ctx, "id");
            Optional<MineCellsDimension> dimension = hasDimension
                ? getDimension(StringArgumentType.getString(ctx, "dimension"))
                : Optional.ofNullable(MineCellsDimension.of(ctx.getSource().getLevel()));
            if (dimension.isEmpty() || dimension.get() == MineCellsDimension.OVERWORLD) {
                ctx.getSource().sendFailure(Component.literal("Invalid dimension"));
                return 0;
            }

            BlockPos searchPos = hasPos
                ? BlockPosArgument.getBlockPos(ctx, "pos")
                : BlockPos.containing(ctx.getSource().getPosition());

            if (!hasClear || BoolArgumentType.getBool(ctx, "clear")) {
                GridBasedStructureUtils.clearSpecialPointsCache();
                ctx.getSource().sendSuccess(() -> Component.literal("Cleared special point cache"), true);
            }

            return findSpecialPoint(ctx, dimension.get(), id, searchPos);
        };
    }

    private static int findSpecialPoint(CommandContext<CommandSourceStack> ctx, MineCellsDimension dimension, ResourceLocation id, BlockPos searchPos) {
        ServerLevel level = dimension.getLevel(ctx.getSource().getLevel());
        if (level == null) {
            ctx.getSource().sendFailure(Component.literal("Dimension is not loaded: " + dimension.id()));
            return 0;
        }
        Optional<SpecialPoint> point = GridBasedStructureUtils.getSpecialPoint(level, searchPos, dimension, id);
        if (point.isEmpty()) {
            ctx.getSource().sendFailure(Component.literal("Not found"));
            return 0;
        }
        BlockPos pos = new BlockPos(point.get().offset().offset(MathUtils.getClosestMultiplePosition(searchPos, 1024)));
        String dimensionKey = dimension.id().toString();
        String command = "/execute in " + dimensionKey + " run tp @s " + pos.getX() + " " + pos.getY() + " " + pos.getZ();
        ctx.getSource().sendSuccess(() -> Component.literal(
                "Found " + id + " at " + pos.toShortString() + ", rotation: " + point.get().facing() + " in " + dimensionKey
            ).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command))),
            false
        );
        return 1;
    }

    private static Optional<MineCellsDimension> getDimension(String name) {
        return Arrays.stream(MineCellsDimension.values())
            .filter(dimension -> dimension.id().getPath().equals(name))
            .findFirst();
    }
}
