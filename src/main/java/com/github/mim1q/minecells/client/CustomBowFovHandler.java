package com.github.mim1q.minecells.client;

import com.github.mim1q.minecells.item.weapon.bow.CustomBowItem;
import com.github.mim1q.minecells.MineCells;

import net.minecraft.world.item.ItemStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MineCells.MOD_ID, value = Dist.CLIENT)
public final class CustomBowFovHandler {
    private CustomBowFovHandler() {
    }

    @SubscribeEvent
    public static void onComputeFov(ComputeFovModifierEvent event) {
        ItemStack stack = event.getPlayer().getUseItem();
        if (event.getPlayer().isUsingItem() && stack.getItem() instanceof CustomBowItem bow) {
            event.setNewFovModifier(bow.getFovMultiplier(event.getPlayer(), stack));
        }
    }
}
