package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class MineCellsCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MineCells.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MINECELLS = CREATIVE_TABS.register("minecells", () -> CreativeModeTab.builder()
        .title(Component.translatable("itemGroup.minecells.minecells"))
        .icon(() -> new ItemStack(MineCellsItems.MONSTER_CELL.get()))
        .displayItems((parameters, output) -> MineCellsItems.ITEMS.getEntries().forEach(item -> output.accept(item.get())))
        .build());

    private MineCellsCreativeTabs() {
    }

    public static void register(IEventBus eventBus) {
        CREATIVE_TABS.register(eventBus);
    }
}
