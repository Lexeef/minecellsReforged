package com.github.mim1q.minecells.compat.emi;

import com.github.mim1q.minecells.compat.BlockStateIcon;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/** Displays a specific block state (e.g. bloody spikes) as a 3D block in EMI. */
public record EmiBlockIngredient(BlockState state) implements EmiIngredient {
    @Override
    public List<EmiStack> getEmiStacks() {
        return List.of(EmiStack.of(state.getBlock()));
    }

    @Override
    public EmiIngredient copy() {
        return new EmiBlockIngredient(state);
    }

    @Override
    public long getAmount() {
        return 0;
    }

    @Override
    public EmiIngredient setAmount(long amount) {
        return this;
    }

    @Override
    public float getChance() {
        return 1.0F;
    }

    @Override
    public EmiIngredient setChance(float chance) {
        return this;
    }

    @Override
    public void render(GuiGraphics draw, int x, int y, float delta, int flags) {
        BlockStateIcon.render(draw, state, x, y);
    }

    @Override
    public List<ClientTooltipComponent> getTooltip() {
        ItemStack stack = state.getBlock().asItem().getDefaultInstance();
        return Screen.getTooltipFromItem(Minecraft.getInstance(), stack).stream()
            .map(line -> ClientTooltipComponent.create(line.getVisualOrderText()))
            .toList();
    }
}
