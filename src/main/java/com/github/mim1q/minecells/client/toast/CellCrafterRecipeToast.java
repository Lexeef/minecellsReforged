package com.github.mim1q.minecells.client.toast;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.recipe.CellForgeRecipe;

import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class CellCrafterRecipeToast implements Toast {
    private static final ResourceLocation TEXTURE = MineCells.id("textures/gui/toast/background.png");
    private final CellForgeRecipe recipe;

    public CellCrafterRecipeToast(CellForgeRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public Visibility render(GuiGraphics guiGraphics, ToastComponent toastComponent, long timeSinceLastVisible) {
        Minecraft minecraft = Minecraft.getInstance();
        RegistryAccess registryAccess = minecraft.level != null ? minecraft.level.registryAccess() : RegistryAccess.EMPTY;
        ItemStack output = recipe.getResultItem(registryAccess).copy();
        output.setCount(1);

        guiGraphics.blit(TEXTURE, 0, 0, 0, 0, width(), height(), 256, 256);
        guiGraphics.drawString(minecraft.font, Component.translatable("toast.minecells.recipe_unlocked"), 30, 5, 0xFFEE4D, true);
        guiGraphics.drawString(minecraft.font, output.getHoverName(), 30, 15, 0xFFFFFF, true);
        guiGraphics.renderItem(output, 8, 6);

        return timeSinceLastVisible >= 5000L ? Visibility.HIDE : Visibility.SHOW;
    }
}
