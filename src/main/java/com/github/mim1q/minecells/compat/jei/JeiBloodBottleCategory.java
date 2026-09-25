package com.github.mim1q.minecells.compat.jei;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.compat.BlockStateIcon;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/** Using a glass bottle on bloody spikes fills it with blood. */
public class JeiBloodBottleCategory implements IRecipeCategory<JeiBloodBottleCategory.Recipe> {
    public static final RecipeType<Recipe> TYPE = RecipeType.create(MineCells.MOD_ID, "blood_bottle", Recipe.class);

    private static final int WIDTH = 108;
    private static final int HEIGHT = 20;

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slot;

    public JeiBloodBottleCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(MineCellsItems.BLOOD_BOTTLE.get()));
        this.slot = guiHelper.getSlotDrawable();
    }

    @Override
    public RecipeType<Recipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("category.minecells.blood_bottle");
    }

    @Override
    @SuppressWarnings("removal")
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public int getWidth() {
        return WIDTH;
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
    public void setRecipe(IRecipeLayoutBuilder builder, Recipe recipe, IFocusGroup focuses) {
        builder.addInputSlot(2, 2)
            .setBackground(slot, -1, -1)
            .addItemStack(new ItemStack(MineCellsBlocks.SPIKES.get()))
            .setCustomRenderer(VanillaTypes.ITEM_STACK, BloodySpikesRenderer.INSTANCE);
        builder.addInputSlot(34, 2)
            .setBackground(slot, -1, -1)
            .addItemStack(new ItemStack(Items.GLASS_BOTTLE));
        builder.addOutputSlot(90, 2)
            .setBackground(slot, -1, -1)
            .addItemStack(new ItemStack(MineCellsItems.BLOOD_BOTTLE.get()));
    }

    @Override
    public void draw(Recipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        graphics.drawString(Minecraft.getInstance().font, "+", 23, 6, 0xFF404040, false);
        graphics.blit(JeiCellCrafterCategory.ARROW_TEXTURE, 60, 2, 0, 0, 16, 16, 32, 32);
    }

    @Override
    public ResourceLocation getRegistryName(Recipe recipe) {
        return MineCells.id("blood_bottle");
    }

    public enum Recipe {
        INSTANCE
    }

    private enum BloodySpikesRenderer implements IIngredientRenderer<ItemStack> {
        INSTANCE;

        @Override
        public void render(GuiGraphics graphics, ItemStack stack) {
            BlockStateIcon.render(graphics, BlockStateIcon.bloodySpikes(), 0, 0);
        }

        @Override
        @SuppressWarnings("removal")
        public List<Component> getTooltip(ItemStack stack, TooltipFlag flag) {
            return Screen.getTooltipFromItem(Minecraft.getInstance(), stack);
        }
    }
}
