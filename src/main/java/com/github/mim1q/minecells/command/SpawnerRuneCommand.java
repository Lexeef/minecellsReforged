package com.github.mim1q.minecells.command;

import com.github.mim1q.minecells.entity.nonliving.SpawnerRuneEntity;
import com.github.mim1q.minecells.registry.MineCellsEntities;
import com.github.mim1q.minecells.registry.MineCellsReloadListeners;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.RegisterCommandsEvent;

import java.util.List;

public final class SpawnerRuneCommand {
    private SpawnerRuneCommand() {
    }

    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(
            Commands.literal("minecells:spawnerrune")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("spawn")
                    .then(Commands.argument("id", ResourceLocationArgument.id())
                        .suggests((ctx, builder) -> SharedSuggestionProvider.suggestResource(
                            MineCellsReloadListeners.spawnerRunes().entries().keySet(),
                            builder
                        ))
                        .executes(ctx -> spawn(ctx, ctx.getSource().getPosition()))
                        .then(Commands.argument("pos", Vec3Argument.vec3())
                            .executes(ctx -> spawn(ctx, Vec3Argument.getVec3(ctx, "pos")))
                        )
                    )
                )
                .then(Commands.literal("remove")
                    .executes(SpawnerRuneCommand::remove)
                )
        );
    }

    private static int spawn(CommandContext<CommandSourceStack> ctx, Vec3 pos) {
        ServerLevel level = ctx.getSource().getLevel();
        ResourceLocation id = ResourceLocationArgument.getId(ctx, "id");
        if (!MineCellsReloadListeners.spawnerRunes().entries().containsKey(id)) {
            ctx.getSource().sendFailure(Component.literal("Unknown spawner rune data: " + id));
            return 0;
        }
        SpawnerRuneEntity spawnerRune = MineCellsEntities.SPAWNER_RUNE.get().create(level);
        if (spawnerRune == null) {
            return 0;
        }
        spawnerRune.setPos(pos);
        spawnerRune.controller.setDataId(level, spawnerRune.blockPosition(), id);
        level.addFreshEntity(spawnerRune);
        ctx.getSource().sendSuccess(() -> Component.literal("Spawned spawner rune " + id), true);
        return 1;
    }

    private static int remove(CommandContext<CommandSourceStack> ctx) {
        ServerLevel level = ctx.getSource().getLevel();
        Vec3 pos = ctx.getSource().getPosition();
        List<SpawnerRuneEntity> entities = level.getEntitiesOfClass(SpawnerRuneEntity.class, AABB.ofSize(pos, 3.0D, 3.0D, 3.0D));
        entities.forEach(SpawnerRuneEntity::kill);
        ctx.getSource().sendSuccess(() -> Component.literal("Removed " + entities.size() + " spawner rune(s)"), true);
        return entities.size();
    }
}
