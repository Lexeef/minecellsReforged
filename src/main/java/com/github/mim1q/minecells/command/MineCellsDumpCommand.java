package com.github.mim1q.minecells.command;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.world.state.MineCellsData;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.JsonOps;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class MineCellsDumpCommand {
    private MineCellsDumpCommand() {
    }

    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("minecells:dump").requires(source -> source.hasPermission(2))
            .then(Commands.literal("portals").executes(MineCellsDumpCommand::dumpPortals))
        );
    }

    private static int dumpPortals(CommandContext<CommandSourceStack> ctx) {
        CompoundTag tag = MineCellsData.get(ctx.getSource().getLevel()).save(new CompoundTag());
        Path path = FMLPaths.GAMEDIR.get().resolve("minecells_dumps/portals.json");
        try {
            JsonElement json = NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, tag);
            Files.createDirectories(path.getParent());
            Files.writeString(path, new GsonBuilder().setPrettyPrinting().create().toJson(json), StandardCharsets.UTF_8);
        } catch (Exception e) {
            MineCells.LOGGER.error("Failed to dump portals data", e);
            ctx.getSource().sendFailure(Component.literal("Failed to dump portals data: " + e.getMessage()));
            return 0;
        }
        ctx.getSource().sendSuccess(() -> Component.literal("Dumped portals data to " + path.toAbsolutePath()), false);
        return 1;
    }
}
