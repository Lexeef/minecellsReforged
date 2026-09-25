package com.github.mim1q.minecells.compat.jei;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.recipe.CellForgeRecipe;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class JeiCellCrafterCategory implements IRecipeCategory<CellForgeRecipe> {
    public static final RecipeType<CellForgeRecipe> TYPE = RecipeType.create(MineCells.MOD_ID, "cell_crafter", CellForgeRecipe.class);
    public static final ResourceLocation ARROW_TEXTURE = MineCells.id("textures/gui/cell_crafter/emi_arrow.png");

    private static final int HEIGHT = 52;
    private static final int MIN_WIDTH = 120;

    private final int width;
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slot;

    public JeiCellCrafterCategory(IGuiHelper guiHelper, List<CellForgeRecipe> recipes) {
        int maxInputs = recipes.stream().mapToInt(recipe -> recipe.ingredients().size()).max().orElse(0);
        this.width = Math.max(maxInputs * 20, MIN_WIDTH);
        this.background = guiHelper.createBlankDrawable(width, HEIGHT);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(MineCellsBlocks.CELL_CRAFTER.get()));
        this.slot = guiHelper.getSlotDrawable();
    }

    @Override
    public RecipeType<CellForgeRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("emi.category.minecells.cell_crafter");
    }

    @Override
    @SuppressWarnings("removal")
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CellForgeRecipe recipe, IFocusGroup focuses) {
        int slotX = width / 2 - recipe.ingredients().size() * 10 + 2;
        for (var entry : recipe.ingredients().entrySet()) {
            builder.addInputSlot(slotX + 1, 1)
                .setBackground(slot, -1, -1)
                .addItemStack(new ItemStack(entry.getKey(), entry.getValue()));
            slotX += 20;
        }
        builder.addOutputSlot(width / 2 - 7, 35)
            .setBackground(slot, -1, -1)
            .addItemStack(recipe.output().copy());
    }

    @Override
    public void draw(CellForgeRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        int arrowWidth = arrowWidth(recipe);
        graphics.blit(ARROW_TEXTURE, width / 2 - 8, 19, 0, 0, arrowWidth, 16, 32, 32);
    }

    @Override
    @SuppressWarnings("removal")
    public List<Component> getTooltipStrings(CellForgeRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (recipe.requiredAdvancement().isEmpty()) {
            return List.of();
        }
        int arrowX = width / 2 - 8;
        if (mouseX < arrowX || mouseX >= arrowX + arrowWidth(recipe) || mouseY < 19 || mouseY >= 35) {
            return List.of();
        }
        String advancementKey = Util.makeDescriptionId("advancements", recipe.requiredAdvancement().get()) + ".description";
        return List.of(
            Component.translatable("block.minecells.cell_crafter.requirement").withStyle(ChatFormatting.RED),
            Component.translatable(advancementKey).withStyle(ChatFormatting.GRAY)
        );
    }

    @Override
    public ResourceLocation getRegistryName(CellForgeRecipe recipe) {
        return recipe.id();
    }

    private static int arrowWidth(CellForgeRecipe recipe) {
        return recipe.requiredAdvancement().isPresent() ? 24 : 16;
    }
}
