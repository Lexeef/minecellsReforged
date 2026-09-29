package com.github.mim1q.minecells.command;

import com.github.mim1q.minecells.block.blockentity.SpawnerRuneBlockEntity;
import com.github.mim1q.minecells.registry.MineCellsReloadListeners;
import com.github.mim1q.minecells.world.state.MineCellsData;
import com.github.mim1q.minecells.world.state.PlayerSpecificMineCellsData;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import net.minecraftforge.event.RegisterCommandsEvent;

public final class MineCellsDataCommand {
    private MineCellsDataCommand() {
    }

    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(
            Commands.literal("minecells_data")
                .requires(source -> source.hasPermission(2))
                .executes(MineCellsDataCommand::printWorldSummary)
                .then(Commands.literal("player")
                    .then(Commands.argument("target", EntityArgument.player())
                        .executes(MineCellsDataCommand::printPlayerData)))
                .then(Commands.literal("run")
                    .then(Commands.argument("coords", StringArgumentType.word())
                        .executes(MineCellsDataCommand::printRunData)))
                .then(Commands.literal("set_spawner_rune")
                    .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        .then(Commands.argument("data_id", ResourceLocationArgument.id())
                            .executes(MineCellsDataCommand::setSpawnerRuneData))))
        );
    }

    private static int printWorldSummary(CommandContext<CommandSourceStack> context) {
        MineCellsData data = MineCellsData.get(context.getSource().getLevel());
        context.getSource().sendSuccess(() -> Component.literal("Mine Cells world data:"), false);
        context.getSource().sendSuccess(() -> Component.literal(" runs=" + data.runs.size()), false);
        context.getSource().sendSuccess(() -> Component.literal(" spawner_runes_loaded=" + MineCellsReloadListeners.spawnerRunes().size()), false);
        MineCellsReloadListeners.spawnerRunes().entries().keySet().stream().sorted().limit(5)
            .forEach(id -> context.getSource().sendSuccess(() -> Component.literal("  - " + id), false));
        return 1;
    }

    private static int printPlayerData(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "target");
        PlayerSpecificMineCellsData data = new PlayerSpecificMineCellsData(MineCellsData.get(context.getSource().getLevel()), player);
        context.getSource().sendSuccess(() -> Component.literal("Mine Cells data for " + player.getName().getString() + ":"), false);
        context.getSource().sendSuccess(() -> Component.literal(" runs=" + data.map.size()), false);
        data.map.forEach((key, value) -> {
            context.getSource().sendSuccess(() -> Component.literal(" " + key + " portals=" + value.portals.size() + " activated_spawner_groups=" + value.activatedSpawnerRunes.size()), false);
        });
        return 1;
    }

    private static int printRunData(CommandContext<CommandSourceStack> context) {
        String key = StringArgumentType.getString(context, "coords");
        MineCellsData data = MineCellsData.get(context.getSource().getLevel());
        MineCellsData.RunData run = data.runs.values().stream()
            .filter(entry -> (entry.x + "," + entry.z).equals(key))
            .findFirst()
            .orElse(null);
        if (run == null) {
            context.getSource().sendFailure(Component.literal("No Mine Cells run data for " + key));
            return 0;
        }
        context.getSource().sendSuccess(() -> Component.literal("Run " + key + ": players=" + run.players.size()), false);
        return 1;
    }

    private static int setSpawnerRuneData(CommandContext<CommandSourceStack> context) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "pos");
        ResourceLocation id = ResourceLocationArgument.getId(context, "data_id");
        if (!MineCellsReloadListeners.spawnerRunes().entries().containsKey(id)) {
            context.getSource().sendFailure(Component.literal("Unknown spawner rune data id: " + id));
            return 0;
        }

        var blockEntity = context.getSource().getLevel().getBlockEntity(pos);
        if (!(blockEntity instanceof SpawnerRuneBlockEntity rune)) {
            context.getSource().sendFailure(Component.literal("No Mine Cells spawner rune block entity at " + pos.toShortString()));
            return 0;
        }

        rune.controller.setDataId(context.getSource().getLevel(), pos, id);
        rune.setChanged();
        context.getSource().getLevel().sendBlockUpdated(pos, rune.getBlockState(), rune.getBlockState(), 3);
        context.getSource().sendSuccess(() -> Component.literal("Spawner rune at " + pos.toShortString() + " bound to " + id), true);
        return 1;
    }
}
