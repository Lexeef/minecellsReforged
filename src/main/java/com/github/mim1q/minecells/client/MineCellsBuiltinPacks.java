package com.github.mim1q.minecells.client;

import com.github.mim1q.minecells.MineCells;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.ModList;

import java.nio.file.Files;
import java.nio.file.Path;

@Mod.EventBusSubscriber(modid = MineCells.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class MineCellsBuiltinPacks {
    private MineCellsBuiltinPacks() {
    }

    @SubscribeEvent
    public static void addPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.CLIENT_RESOURCES) {
            return;
        }
        Path path = ModList.get().getModFileById(MineCells.MOD_ID).getFile().findResource("resourcepacks/ribcages");
        if (!Files.exists(path)) {
            MineCells.LOGGER.warn("Built-in resource pack 'ribcages' not found");
            return;
        }
        Component title = Component.literal("[Mine Cells]").withStyle(ChatFormatting.RED)
            .append(Component.literal(" Ribcages").withStyle(ChatFormatting.GOLD));
        Pack pack = Pack.readMetaAndCreate(
            "builtin/" + MineCells.MOD_ID + "_ribcages",
            title,
            false,
            id -> new PathPackResources(id, path, true),
            PackType.CLIENT_RESOURCES,
            Pack.Position.TOP,
            PackSource.BUILT_IN
        );
        if (pack != null) {
            event.addRepositorySource(consumer -> consumer.accept(pack));
        }
    }
}
