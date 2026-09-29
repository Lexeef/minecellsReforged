package com.github.mim1q.minecells.compat.rei;

import com.github.mim1q.minecells.compat.BlockStateIcon;
import com.github.mim1q.minecells.compat.DoorwayRecipeExamples;
import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.recipe.CellForgeRecipe;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsItems;
import com.github.mim1q.minecells.registry.MineCellsRecipeTypes;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.entry.renderer.EntryRenderer;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Tooltip;
import me.shedaniel.rei.api.client.gui.widgets.TooltipContext;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCustomShapelessDisplay;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.Util;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@REIPluginClient
public class MineCellsReiPlugin implements REIClientPlugin {
    public static final ResourceLocation ARROW_TEXTURE = MineCells.id("textures/gui/cell_crafter/emi_arrow.png");

    private static final CategoryIdentifier<CellCrafterDisplay> CELL_CRAFTER_CATEGORY_ID = CategoryIdentifier.of(MineCells.id("cell_crafter"));
    private static final CategoryIdentifier<BloodBottleDisplay> BLOOD_BOTTLE_CATEGORY_ID = CategoryIdentifier.of(MineCells.id("blood_bottle"));

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new CellCrafterCategory());
        registry.addWorkstations(CELL_CRAFTER_CATEGORY_ID, EntryStacks.of(MineCellsBlocks.CELL_CRAFTER.get()));
        registry.add(new BloodBottleCategory());
        registry.addWorkstations(BLOOD_BOTTLE_CATEGORY_ID, EntryStacks.of(MineCellsBlocks.SPIKES.get()));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.getRecipeManager()
            .getAllRecipesFor(MineCellsRecipeTypes.CELL_FORGE_RECIPE_TYPE.get())
            .stream()
            .sorted(Comparator
                .comparingInt((CellForgeRecipe recipe) -> recipe.category().ordinal())
                .thenComparing((a, b) -> b.priority() - a.priority()))
            .forEach(recipe -> registry.add(new CellCrafterDisplay(recipe)));

        registry.add(new BloodBottleDisplay());

        for (DoorwayRecipeExamples.Example example : DoorwayRecipeExamples.allExamples()) {
            registry.add(DefaultCustomShapelessDisplay.simple(
                example.inputs().stream().map(EntryIngredients::of).toList(),
                List.of(EntryIngredients.of(example.output())),
                Optional.of(example.id())
            ));
        }
    }

    private static class CellCrafterCategory implements DisplayCategory<CellCrafterDisplay> {
        @Override
        public CategoryIdentifier<? extends CellCrafterDisplay> getCategoryIdentifier() {
            return CELL_CRAFTER_CATEGORY_ID;
        }

        @Override
        public Component getTitle() {
            return Component.translatable("emi.category.minecells.cell_crafter");
        }

        @Override
        public Renderer getIcon() {
            return EntryStacks.of(MineCellsBlocks.CELL_CRAFTER.get());
        }

        @Override
        public int getDisplayWidth(CellCrafterDisplay display) {
            return Math.max(display.getInputEntries().size() * 20 + 10, 150);
        }

        @Override
        public List<Widget> setupDisplay(CellCrafterDisplay display, Rectangle bounds) {
            List<Widget> widgets = new ArrayList<>();
            widgets.add(Widgets.createRecipeBase(bounds));

            int startX = bounds.x;
            int startY = bounds.y + 5;
            int centerX = startX + bounds.width / 2;

            int slotX = centerX - display.getInputEntries().size() * 10 + 2;
            for (EntryIngredient input : display.getInputEntries()) {
                widgets.add(Widgets.createSlot(new Point(slotX, startY)).entries(input).markInput());
                slotX += 20;
            }

            boolean hasAdvancement = display.recipe.requiredAdvancement().isPresent();
            int arrowWidth = hasAdvancement ? 24 : 16;
            widgets.add(Widgets.createTexturedWidget(ARROW_TEXTURE, centerX - 8, startY + 21, 0, 0, arrowWidth, 16, arrowWidth, 16, 32, 32));
            if (hasAdvancement) {
                String advancementKey = Util.makeDescriptionId("advancements", display.recipe.requiredAdvancement().get()) + ".description";
                widgets.add(Widgets.createTooltip(
                    new Rectangle(centerX - 8, startY + 21, arrowWidth, 16),
                    Component.translatable("block.minecells.cell_crafter.requirement").withStyle(ChatFormatting.RED),
                    Component.translatable(advancementKey).withStyle(ChatFormatting.GRAY)
                ));
            }

            widgets.add(Widgets.createSlot(new Point(centerX - 8, startY + 40)).entries(display.getOutputEntries().get(0)).markOutput());
            return widgets;
        }
    }

    private static class BloodBottleCategory implements DisplayCategory<BloodBottleDisplay> {
        @Override
        public CategoryIdentifier<? extends BloodBottleDisplay> getCategoryIdentifier() {
            return BLOOD_BOTTLE_CATEGORY_ID;
        }

        @Override
        public Component getTitle() {
            return Component.translatable("category.minecells.blood_bottle");
        }

        @Override
        public Renderer getIcon() {
            return EntryStacks.of(MineCellsItems.BLOOD_BOTTLE.get());
        }

        @Override
        public int getDisplayHeight() {
            return 36;
        }

        @Override
        public List<Widget> setupDisplay(BloodBottleDisplay display, Rectangle bounds) {
            List<Widget> widgets = new ArrayList<>();
            widgets.add(Widgets.createRecipeBase(bounds));

            int centerX = bounds.getCenterX();
            int y = bounds.getCenterY() - 8;
            widgets.add(Widgets.createSlot(new Point(centerX - 53, y)).entries(display.getInputEntries().get(0)).markInput());
            widgets.add(Widgets.createLabel(new Point(centerX - 28, y + 4), Component.literal("+")).noShadow().color(0xFF404040, 0xFFBBBBBB));
            widgets.add(Widgets.createSlot(new Point(centerX - 19, y)).entries(display.getInputEntries().get(1)).markInput());
            widgets.add(Widgets.createArrow(new Point(centerX + 3, y)));
            widgets.add(Widgets.createResultSlotBackground(new Point(centerX + 37, y)));
            widgets.add(Widgets.createSlot(new Point(centerX + 37, y)).entries(display.getOutputEntries().get(0)).disableBackground().markOutput());
            return widgets;
        }
    }

    private static class CellCrafterDisplay extends BasicDisplay {
        public final CellForgeRecipe recipe;

        public CellCrafterDisplay(CellForgeRecipe recipe) {
            super(
                recipe.ingredients().entrySet().stream()
                    .map(entry -> EntryIngredients.of(new ItemStack(entry.getKey(), entry.getValue())))
                    .toList(),
                List.of(EntryIngredients.of(recipe.output().copy())),
                Optional.of(recipe.id())
            );
            this.recipe = recipe;
        }

        @Override
        public CategoryIdentifier<?> getCategoryIdentifier() {
            return CELL_CRAFTER_CATEGORY_ID;
        }
    }

    private static class BloodBottleDisplay extends BasicDisplay {
        public BloodBottleDisplay() {
            super(
                List.of(
                    EntryIngredient.of(bloodySpikesStack()),
                    EntryIngredients.of(Items.GLASS_BOTTLE)
                ),
                List.of(EntryIngredients.of(MineCellsItems.BLOOD_BOTTLE.get())),
                Optional.of(MineCells.id("blood_bottle"))
            );
        }

        @Override
        public CategoryIdentifier<?> getCategoryIdentifier() {
            return BLOOD_BOTTLE_CATEGORY_ID;
        }

        private static EntryStack<ItemStack> bloodySpikesStack() {
            return EntryStacks.of(MineCellsBlocks.SPIKES.get()).withRenderer(new EntryRenderer<ItemStack>() {
                @Override
                public void render(EntryStack<ItemStack> entry, GuiGraphics graphics, Rectangle bounds, int mouseX, int mouseY, float delta) {
                    BlockStateIcon.render(graphics, BlockStateIcon.bloodySpikes(), bounds.getCenterX() - 8, bounds.getCenterY() - 8);
                }

                @Override
                public Tooltip getTooltip(EntryStack<ItemStack> entry, TooltipContext context) {
                    return EntryStacks.of(MineCellsBlocks.SPIKES.get()).getTooltip(context);
                }
            });
        }
    }
}
