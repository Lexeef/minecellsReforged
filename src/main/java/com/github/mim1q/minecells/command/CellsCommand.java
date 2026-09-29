package com.github.mim1q.minecells.command;

import com.github.mim1q.minecells.util.PlayerCells;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import net.minecraftforge.event.RegisterCommandsEvent;

public final class CellsCommand {
    private CellsCommand() {
    }

    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("cells").requires(source -> source.hasPermission(2))
            .then(Commands.literal("set")
                .then(Commands.argument("player", EntityArgument.player())
                    .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                        .executes(CellsCommand::setCells))))
            .then(Commands.literal("give")
                .then(Commands.argument("player", EntityArgument.player())
                    .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                        .executes(CellsCommand::giveCells))))
            .then(Commands.literal("take")
                .then(Commands.argument("player", EntityArgument.player())
                    .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                        .executes(CellsCommand::takeCells))))
            .then(Commands.literal("get")
                .then(Commands.argument("player", EntityArgument.player())
                    .executes(CellsCommand::getCells)))
        );
    }

    private static int setCells(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        int cells = IntegerArgumentType.getInteger(ctx, "amount");
        PlayerCells.set(player, cells);
        ctx.getSource().sendSuccess(() -> Component.literal("Set " + player.getName().getString() + "'s cells to " + cells), false);
        return 1;
    }

    private static int giveCells(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        int cells = IntegerArgumentType.getInteger(ctx, "amount");
        PlayerCells.set(player, PlayerCells.get(player) + cells);
        ctx.getSource().sendSuccess(() -> Component.literal("Gave " + cells + " cells to " + player.getName().getString()), false);
        return 1;
    }

    private static int takeCells(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        int cells = IntegerArgumentType.getInteger(ctx, "amount");
        int current = PlayerCells.get(player);
        if (current < cells) {
            ctx.getSource().sendFailure(Component.literal(player.getName().getString() + " doesn't have enough cells"));
            return 0;
        }
        PlayerCells.set(player, current - cells);
        ctx.getSource().sendSuccess(() -> Component.literal("Took " + cells + " cells from " + player.getName().getString()), false);
        return 1;
    }

    private static int getCells(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        int cells = PlayerCells.get(player);
        ctx.getSource().sendSuccess(() -> Component.literal(player.getName().getString() + " has " + cells + " cells"), false);
        return cells;
    }
}
