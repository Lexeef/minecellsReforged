package com.github.mim1q.minecells.client.screen;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.network.MineCellsNetwork;
import com.github.mim1q.minecells.network.c2s.CellCrafterCraftRequestC2SPacket;
import com.github.mim1q.minecells.network.c2s.RequestUnlockedCellCrafterRecipesC2SPacket;
import com.github.mim1q.minecells.recipe.CellForgeRecipe;
import com.github.mim1q.minecells.screen.cellcrafter.CellCrafterMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CellCrafterScreen extends AbstractContainerScreen<CellCrafterMenu> {
    private static final ResourceLocation TEXTURE = MineCells.id("textures/gui/cell_crafter/container.png");
    private static final int RECIPES_PER_PAGE = 6;

    private final List<Button> recipeButtons = new ArrayList<>();
    private List<DisplayedRecipe> filteredRecipes = List.of();
    private Map<ResourceLocation, Boolean> unlockedRecipes = Map.of();
    private CellForgeRecipe.Category currentCategory = CellForgeRecipe.Category.GEAR;
    private int page = 0;
    private DisplayedRecipe selectedRecipe;
    private Button categoryButton;
    private Button previousPageButton;
    private Button nextPageButton;
    private Button craftButton;

    public CellCrafterScreen(CellCrafterMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 166;
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        refreshRecipes();
        MineCellsNetwork.CHANNEL.sendToServer(new RequestUnlockedCellCrafterRecipesC2SPacket());

        categoryButton = addRenderableWidget(Button.builder(currentCategory.getName(), button -> {
            currentCategory = switch (currentCategory) {
                case GEAR -> CellForgeRecipe.Category.DECORATION;
                case DECORATION -> CellForgeRecipe.Category.OTHER;
                case OTHER -> CellForgeRecipe.Category.GEAR;
            };
            page = 0;
            refreshRecipes();
            updateRecipeButtons();
        }).bounds(leftPos + 7, topPos + 5, 90, 20).build());

        previousPageButton = addRenderableWidget(Button.builder(Component.literal("<"), button -> {
            if (page > 0) {
                page--;
                updateRecipeButtons();
            }
        }).bounds(leftPos + 102, topPos + 5, 20, 20).build());

        nextPageButton = addRenderableWidget(Button.builder(Component.literal(">"), button -> {
            if ((page + 1) * RECIPES_PER_PAGE < filteredRecipes.size()) {
                page++;
                updateRecipeButtons();
            }
        }).bounds(leftPos + 126, topPos + 5, 20, 20).build());

        craftButton = addRenderableWidget(Button.builder(Component.translatable("block.minecells.cell_crafter.craft"), button -> {
            if (selectedRecipe != null) {
                MineCellsNetwork.CHANNEL.sendToServer(new CellCrafterCraftRequestC2SPacket(selectedRecipe.recipe().getId(), menu.getBlockPos()));
            }
        }).bounds(leftPos + 103, topPos + 59, 62, 20).build());

        for (int i = 0; i < RECIPES_PER_PAGE; i++) {
            final int index = i;
            Button button = addRenderableWidget(Button.builder(Component.empty(), clicked -> {
                int recipeIndex = page * RECIPES_PER_PAGE + index;
                if (recipeIndex < filteredRecipes.size()) {
                    selectedRecipe = filteredRecipes.get(recipeIndex);
                    updateCraftButton();
                }
            }).bounds(leftPos + 7, topPos + 30 + i * 12, 90, 12).build());
            recipeButtons.add(button);
        }

        updateRecipeButtons();
        updateCraftButton();
    }

    private void refreshRecipes() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            filteredRecipes = List.of();
            return;
        }

        filteredRecipes = minecraft.level.getRecipeManager().getAllRecipesFor(com.github.mim1q.minecells.registry.MineCellsRecipeTypes.CELL_FORGE_RECIPE_TYPE.get()).stream()
            .filter(recipe -> recipe.category() == currentCategory)
            .map(recipe -> new DisplayedRecipe(recipe, unlockedRecipes.getOrDefault(recipe.getId(), false)))
            .sorted(
                Comparator.<DisplayedRecipe>comparingInt(recipe -> recipe.unlocked() ? 0 : 1)
                    .thenComparing((DisplayedRecipe recipe) -> recipe.recipe().priority(), Comparator.reverseOrder())
                    .thenComparing(recipe -> recipe.recipe().getId().toString())
            )
            .toList();

        if (selectedRecipe != null && filteredRecipes.stream().noneMatch(it -> it.recipe().getId().equals(selectedRecipe.recipe().getId()))) {
            selectedRecipe = null;
        }
    }

    public void updateUnlockedRecipes(Map<ResourceLocation, Boolean> unlockedRecipes) {
        this.unlockedRecipes = Map.copyOf(unlockedRecipes);
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }

        filteredRecipes = minecraft.level.getRecipeManager().getAllRecipesFor(com.github.mim1q.minecells.registry.MineCellsRecipeTypes.CELL_FORGE_RECIPE_TYPE.get()).stream()
            .filter(recipe -> recipe.category() == currentCategory)
            .map(recipe -> new DisplayedRecipe(recipe, this.unlockedRecipes.getOrDefault(recipe.getId(), false)))
            .sorted(
                Comparator.<DisplayedRecipe>comparingInt(recipe -> recipe.unlocked() ? 0 : 1)
                    .thenComparing((DisplayedRecipe recipe) -> recipe.recipe().priority(), Comparator.reverseOrder())
                    .thenComparing(recipe -> recipe.recipe().getId().toString())
            )
            .toList();

        if (selectedRecipe != null) {
            selectedRecipe = filteredRecipes.stream()
                .filter(it -> it.recipe().getId().equals(selectedRecipe.recipe().getId()))
                .findFirst()
                .orElse(null);
        }

        page = Math.min(page, Math.max(0, (filteredRecipes.size() - 1) / RECIPES_PER_PAGE));
        updateRecipeButtons();
    }

    private void updateRecipeButtons() {
        categoryButton.setMessage(currentCategory.getName());
        for (int i = 0; i < recipeButtons.size(); i++) {
            int recipeIndex = page * RECIPES_PER_PAGE + i;
            Button button = recipeButtons.get(i);
            if (recipeIndex < filteredRecipes.size()) {
                DisplayedRecipe recipe = filteredRecipes.get(recipeIndex);
                button.visible = true;
                button.active = true;
                Component baseName = recipe.recipe().getResultItem(minecraft.level.registryAccess()).getHoverName();
                button.setMessage(recipe.unlocked()
                    ? baseName
                    : Component.translatable("block.minecells.cell_crafter.locked_recipe", baseName));
            } else {
                button.visible = false;
                button.active = false;
                button.setMessage(Component.empty());
            }
        }

        previousPageButton.active = page > 0;
        nextPageButton.active = (page + 1) * RECIPES_PER_PAGE < filteredRecipes.size();
        updateCraftButton();
    }

    private void updateCraftButton() {
        craftButton.active = selectedRecipe != null
            && selectedRecipe.unlocked()
            && minecraft.player != null
            && selectedRecipe.recipe().matchesInventory(minecraft.player.getInventory(), minecraft.player.level());
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);

        if (selectedRecipe != null) {
            ItemStack output = selectedRecipe.recipe().getResultItem(minecraft.level.registryAccess());
            guiGraphics.renderItem(output, leftPos + 140, topPos + 35);
            guiGraphics.renderItemDecorations(font, output, leftPos + 140, topPos + 35);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, title, titleLabelX, titleLabelY, 0x404040, false);
        guiGraphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0x404040, false);

        if (selectedRecipe != null) {
            int y = 31;
            guiGraphics.drawString(font, Component.translatable("block.minecells.cell_crafter.requirement"), 103, y, 0x46D4FF, false);
            y += 12;
            for (var entry : selectedRecipe.recipe().ingredients().entrySet()) {
                int count = minecraft.player != null ? minecraft.player.getInventory().countItem(entry.getKey()) : 0;
                int color = count >= entry.getValue() ? 0x99FFA9 : 0xFF7B7D;
                guiGraphics.drawString(font, entry.getKey().getDescription().copy().append(" x" + entry.getValue()), 103, y, color, false);
                y += 10;
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        for (int i = 0; i < recipeButtons.size(); i++) {
            int recipeIndex = page * RECIPES_PER_PAGE + i;
            if (recipeIndex >= filteredRecipes.size()) {
                continue;
            }

            Button button = recipeButtons.get(i);
            DisplayedRecipe recipe = filteredRecipes.get(recipeIndex);
            if (button.isHoveredOrFocused() && !recipe.unlocked() && recipe.recipe().requiredAdvancement().isPresent()) {
                ResourceLocation advancementId = recipe.recipe().requiredAdvancement().get();
                guiGraphics.renderTooltip(
                    font,
                    List.of(
                        Component.translatable("block.minecells.cell_crafter.locked"),
                        Component.translatable(Util.makeDescriptionId("advancements", advancementId) + ".description")
                    ),
                    Optional.empty(),
                    mouseX,
                    mouseY
                );
                break;
            }
        }
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private record DisplayedRecipe(CellForgeRecipe recipe, boolean unlocked) {
    }
}
