package com.github.mim1q.minecells.compat.emi;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.recipe.CellForgeRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EmiCellCrafterRecipeDisplay implements EmiRecipe {
    public static final ResourceLocation ARROW_TEXTURE = MineCells.id("textures/gui/cell_crafter/emi_arrow.png");

    private final CellForgeRecipe recipe;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;

    public EmiCellCrafterRecipeDisplay(CellForgeRecipe recipe) {
        this.recipe = recipe;
        this.inputs = recipe.ingredients().entrySet().stream()
            .map(entry -> (EmiIngredient) EmiStack.of(new ItemStack(entry.getKey(), entry.getValue())))
            .toList();
        this.outputs = List.of(EmiStack.of(recipe.output()));
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return MineCellsEmiPlugin.cellCrafterCategory();
    }

    @Override
    @Nullable
    public ResourceLocation getId() {
        return recipe.id();
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return inputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return outputs;
    }

    @Override
    public int getDisplayWidth() {
        return Math.max(inputs.size() * 20, 24);
    }

    @Override
    public int getDisplayHeight() {
        return 52;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        int widgetX = getDisplayWidth() / 2 - inputs.size() * 10 + 2;
        for (EmiIngredient input : inputs) {
            widgets.addSlot(input, widgetX, 0);
            widgetX += 20;
        }

        boolean hasAdvancement = recipe.requiredAdvancement().isPresent();
        int arrowWidth = hasAdvancement ? 24 : 16;
        var arrow = widgets.addTexture(ARROW_TEXTURE, getDisplayWidth() / 2 - 8, 19, arrowWidth, 16, 0, 0, arrowWidth, 16, 32, 32);
        if (hasAdvancement) {
            String advancementKey = Util.makeDescriptionId("advancements", recipe.requiredAdvancement().get()) + ".description";
            List<ClientTooltipComponent> tooltip = List.of(
                ClientTooltipComponent.create(Component.translatable("block.minecells.cell_crafter.requirement").withStyle(ChatFormatting.RED).getVisualOrderText()),
                ClientTooltipComponent.create(Component.translatable(advancementKey).withStyle(ChatFormatting.GRAY).getVisualOrderText())
            );
            arrow.tooltip((mouseX, mouseY) -> tooltip);
        }
        widgets.addSlot(outputs.get(0), getDisplayWidth() / 2 - 8, 34).recipeContext(this);
    }
}
