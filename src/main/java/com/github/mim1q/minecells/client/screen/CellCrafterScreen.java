package com.github.mim1q.minecells.client.screen;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.MineCellsClientData;
import com.github.mim1q.minecells.network.MineCellsNetwork;
import com.github.mim1q.minecells.network.c2s.CellCrafterCraftRequestC2SPacket;
import com.github.mim1q.minecells.network.c2s.RequestUnlockedCellCrafterRecipesC2SPacket;
import com.github.mim1q.minecells.recipe.CellForgeRecipe;
import com.github.mim1q.minecells.registry.MineCellsRecipeTypes;
import com.github.mim1q.minecells.screen.cellcrafter.CellCrafterMenu;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CellCrafterScreen extends AbstractContainerScreen<CellCrafterMenu> {
    private static final ResourceLocation SCREEN_TEXTURE = MineCells.id("textures/gui/cell_crafter/container.png");
    private static final ResourceLocation RECIPES_TEXTURE = MineCells.id("textures/gui/cell_crafter/recipes.png");
    private static final int COLUMNS = 6;
    private static final int ROWS = 5;
    private static final int SEARCH_MAX_LENGTH = 32;

    private final List<DisplayedRecipe> allRecipes = new ArrayList<>();
    private final List<DisplayedRecipe> currentRecipes = new ArrayList<>();
    private CellForgeRecipe selectedRecipe;
    private DisplayedRecipe listSelectedRecipe;
    private CellForgeRecipe.Category selectedCategory = CellForgeRecipe.Category.GEAR;
    private boolean recipeListVisible = false;
    private boolean requestedRecipes = false;
    private String search = "";
    private boolean searchFocused = false;
    private int scrollOffset = 0;

    private List<Component> pendingTooltip = null;
    private ItemStack pendingItemTooltip = ItemStack.EMPTY;

    public CellCrafterScreen(CellCrafterMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 178;
        imageHeight = 160;
    }

    @Override
    protected void init() {
        super.init();
        if (!requestedRecipes) {
            requestedRecipes = true;
            setRecipes(MineCellsClientData.getUnlockedCellCrafterRecipes());
            MineCellsNetwork.CHANNEL.sendToServer(new RequestUnlockedCellCrafterRecipesC2SPacket());
        }
        menu.setSlotsActive(!recipeListVisible);
    }

    public void updateUnlockedRecipes(Map<ResourceLocation, Boolean> unlockedRecipes) {
        setRecipes(unlockedRecipes);
    }

    private void setRecipes(Map<ResourceLocation, Boolean> unlockedRecipes) {
        allRecipes.clear();
        if (minecraft == null || minecraft.level == null) {
            updateRecipes();
            return;
        }
        RecipeManager recipeManager = minecraft.level.getRecipeManager();
        for (CellForgeRecipe recipe : recipeManager.getAllRecipesFor(MineCellsRecipeTypes.CELL_FORGE_RECIPE_TYPE.get())) {
            Boolean unlocked = unlockedRecipes.get(recipe.getId());
            if (unlocked != null) {
                allRecipes.add(new DisplayedRecipe(recipe, unlocked));
            }
        }
        allRecipes.sort((a, b) -> {
            if (a.unlocked() && !b.unlocked()) return -1;
            if (!a.unlocked() && b.unlocked()) return 1;
            int priority = b.recipe().priority() - a.recipe().priority();
            if (priority != 0) return priority;
            return a.name().compareTo(b.name());
        });
        if (listSelectedRecipe != null) {
            ResourceLocation selectedId = listSelectedRecipe.recipe().getId();
            listSelectedRecipe = allRecipes.stream().filter(it -> it.recipe().getId().equals(selectedId)).findFirst().orElse(null);
        }
        updateRecipes();
    }

    private void updateRecipes() {
        currentRecipes.clear();
        for (DisplayedRecipe recipe : allRecipes) {
            boolean matchesSearch = search.isBlank() || (recipe.unlocked() && recipe.name().toLowerCase().contains(search));
            if (matchesSearch && recipe.recipe().category() == selectedCategory) {
                currentRecipes.add(recipe);
            }
        }
        if (!currentRecipes.contains(listSelectedRecipe)) {
            listSelectedRecipe = null;
        }
        scrollOffset = Math.min(scrollOffset, getMaxScrollOffset());
    }

    private int getMaxScrollOffset() {
        return Math.max(0, (int) Math.ceil(currentRecipes.size() / (double) COLUMNS) - ROWS);
    }

    private void scrollUp() {
        if (scrollOffset > 0) {
            scrollOffset--;
        }
    }

    private void scrollDown() {
        if (scrollOffset < getMaxScrollOffset()) {
            scrollOffset++;
        }
    }

    private void clearSearch() {
        search = "";
        searchFocused = false;
        updateRecipes();
    }

    private void toggleRecipeList() {
        recipeListVisible = !recipeListVisible;
        menu.setSlotsActive(!recipeListVisible);
        clearSearch();
    }

    private boolean canCraftSelected() {
        return selectedRecipe != null
            && minecraft != null
            && minecraft.player != null
            && selectedRecipe.matchesInventory(minecraft.player.getInventory(), minecraft.player.level());
    }

    // region Layout

    private int listX() {
        return (width - 200) / 2;
    }

    private int listY() {
        return (height - 176) / 2;
    }

    private TexturedButton viewRecipesButton() {
        return new TexturedButton(leftPos + 8, topPos + 8, 64, 64, SCREEN_TEXTURE, 192, 48, true);
    }

    private TexturedButton craftButton() {
        return new TexturedButton(leftPos + 126, topPos + 34, 16, 16, SCREEN_TEXTURE, 208, 0, canCraftSelected());
    }

    private TexturedButton returnButton() {
        return new TexturedButton(listX() + 52, listY() + 24, 16, 16, RECIPES_TEXTURE, 208, 0, true);
    }

    private TexturedButton applyButton() {
        return new TexturedButton(listX() + 160, listY() + 146, 16, 16, RECIPES_TEXTURE, 224, 0, listSelectedRecipe != null);
    }

    private TexturedButton upButton() {
        return new TexturedButton(listX() + 160, listY() + 44, 16, 16, RECIPES_TEXTURE, 208, 48, scrollOffset > 0);
    }

    private TexturedButton downButton() {
        return new TexturedButton(listX() + 160, listY() + 60, 16, 16, RECIPES_TEXTURE, 224, 48, scrollOffset < getMaxScrollOffset());
    }

    private int categoryY(int index) {
        return listY() + 24 + index * 25;
    }

    private int recipeX(int column) {
        return listX() + 52 + column * 18;
    }

    private int recipeY(int row) {
        return listY() + 43 + row * 18;
    }

    private int searchX() {
        return listX() + 106;
    }

    private int searchY() {
        return listY() + 26;
    }

    // endregion

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        pendingTooltip = null;
        pendingItemTooltip = ItemStack.EMPTY;
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        if (!pendingItemTooltip.isEmpty()) {
            guiGraphics.renderTooltip(font, pendingItemTooltip, mouseX, mouseY);
        } else if (pendingTooltip != null) {
            guiGraphics.renderTooltip(font, pendingTooltip, Optional.empty(), mouseX, mouseY);
        } else if (!recipeListVisible) {
            renderTooltip(guiGraphics, mouseX, mouseY);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        if (recipeListVisible) {
            renderRecipeList(guiGraphics, mouseX, mouseY);
        } else {
            renderMain(guiGraphics, mouseX, mouseY);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }

    private void renderMain(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.blit(SCREEN_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);

        TexturedButton viewRecipes = viewRecipesButton();
        viewRecipes.render(guiGraphics, mouseX, mouseY);
        if (viewRecipes.isHovered(mouseX, mouseY)) {
            pendingTooltip = List.of(Component.translatable("block.minecells.cell_crafter.view_recipes"));
        }

        if (selectedRecipe == null) {
            return;
        }

        guiGraphics.blit(SCREEN_TEXTURE, leftPos + 108, topPos + 34, 224, 0, 16, 16, 256, 256);

        TexturedButton craft = craftButton();
        craft.render(guiGraphics, mouseX, mouseY);
        if (craft.isHovered(mouseX, mouseY)) {
            pendingTooltip = List.of(craft.active
                ? Component.translatable("block.minecells.cell_crafter.craft")
                : Component.translatable("block.minecells.cell_crafter.not_enough_ingredients"));
        }

        int outputX = leftPos + 108;
        int outputY = topPos + 52;
        ItemStack output = selectedRecipe.output();
        guiGraphics.renderItem(output, outputX, outputY);
        guiGraphics.renderItemDecorations(font, output, outputX, outputY);
        if (isInside(mouseX, mouseY, outputX, outputY, 16, 16)) {
            pendingItemTooltip = output;
        }

        renderIngredients(guiGraphics, selectedRecipe, leftPos + 68, topPos + 14, mouseX, mouseY);
    }

    private void renderRecipeList(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int x = listX();
        int y = listY();

        CellForgeRecipe.Category[] categories = CellForgeRecipe.Category.values();
        for (int i = 0; i < categories.length; i++) {
            CellForgeRecipe.Category category = categories[i];
            int tabX = x + 8;
            int tabY = categoryY(i);
            boolean hovered = isInside(mouseX, mouseY, tabX, tabY, 40, 25);
            boolean selected = category == selectedCategory;
            int renderX = selected ? tabX : tabX + 8;
            guiGraphics.blit(RECIPES_TEXTURE, renderX, tabY, 160, hovered || selected ? 25 : 0, 40, 25, 256, 256);
            guiGraphics.renderItem(category.getDisplayStack(), renderX + 12, tabY + 6);
            if (hovered) {
                pendingTooltip = List.of(category.getName());
            }
        }

        guiGraphics.blit(RECIPES_TEXTURE, x + 40, y, 0, 0, 160, 176, 256, 256);
        guiGraphics.drawString(font, selectedCategory.getName(), x + 52, y + 12, 0x000000, false);

        renderSearch(guiGraphics);

        renderButtonWithTooltip(guiGraphics, returnButton(), mouseX, mouseY, "block.minecells.cell_crafter.return");
        renderButtonWithTooltip(guiGraphics, applyButton(), mouseX, mouseY, "block.minecells.cell_crafter.select_recipe");
        renderButtonWithTooltip(guiGraphics, upButton(), mouseX, mouseY, "block.minecells.cell_crafter.scroll_up");
        renderButtonWithTooltip(guiGraphics, downButton(), mouseX, mouseY, "block.minecells.cell_crafter.scroll_down");

        int start = scrollOffset * COLUMNS;
        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                int index = start + row * COLUMNS + column;
                if (index >= currentRecipes.size()) {
                    continue;
                }
                renderRecipe(guiGraphics, currentRecipes.get(index), recipeX(column), recipeY(row), mouseX, mouseY);
            }
        }

        if (listSelectedRecipe != null) {
            renderIngredients(guiGraphics, listSelectedRecipe.recipe(), x + 58, y + 136, mouseX, mouseY);
        }
    }

    private void renderRecipe(GuiGraphics guiGraphics, DisplayedRecipe recipe, int x, int y, int mouseX, int mouseY) {
        boolean hovered = isInside(mouseX, mouseY, x, y, 16, 16);
        if (recipe.unlocked()) {
            ItemStack output = recipe.recipe().output();
            guiGraphics.renderItem(output, x, y);
            guiGraphics.renderItemDecorations(font, output, x, y);
            if (hovered || recipe == listSelectedRecipe) {
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(0.0F, 0.0F, 200.0F);
                guiGraphics.blit(RECIPES_TEXTURE, x - 3, y - 3, 160, 96, 22, 22, 256, 256);
                guiGraphics.pose().popPose();
            }
            if (hovered) {
                pendingItemTooltip = output;
            }
            return;
        }

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, 0.0F, 300.0F);
        guiGraphics.blit(RECIPES_TEXTURE, x, y, 192, 64, 16, 16, 256, 256);
        guiGraphics.pose().popPose();
        if (hovered) {
            List<Component> lines = new ArrayList<>();
            lines.add(Component.translatable("block.minecells.cell_crafter.locked").withStyle(ChatFormatting.RED));
            recipe.recipe().requiredAdvancement().ifPresent(advancement -> lines.add(
                Component.translatable(Util.makeDescriptionId("advancements", advancement) + ".description").withStyle(ChatFormatting.GRAY)
            ));
            pendingTooltip = lines;
        }
    }

    private void renderIngredients(GuiGraphics guiGraphics, CellForgeRecipe recipe, int x, int y, int mouseX, int mouseY) {
        Inventory inventory = minecraft != null && minecraft.player != null ? minecraft.player.getInventory() : null;
        int count = recipe.ingredients().size();
        int currentX = x + (96 - count * 16) / 2;
        for (Map.Entry<Item, Integer> ingredient : recipe.ingredients().entrySet()) {
            ItemStack stack = new ItemStack(ingredient.getKey());
            int required = ingredient.getValue();
            boolean hasEnough = inventory == null || inventory.countItem(ingredient.getKey()) >= required;
            int color = inventory == null ? 0xFFFFFF : hasEnough ? 0x99FFA9 : 0xFF7B7D;

            guiGraphics.renderItem(stack, currentX, y);

            String label = String.valueOf(required);
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.0F, 0.0F, 300.0F);
            guiGraphics.drawString(font, label, currentX + 6 + (16 - font.width(label)) / 2, y + 10, color, true);
            guiGraphics.pose().popPose();

            if (isInside(mouseX, mouseY, currentX, y, 16, 16)) {
                List<Component> lines = new ArrayList<>();
                lines.add(stack.getHoverName().copy().withStyle(stack.getRarity().color).append(" x" + required));
                if (!hasEnough) {
                    lines.add(Component.translatable("block.minecells.cell_crafter.not_enough_of_this_item").withStyle(style -> style.withColor(0xFF0000)));
                }
                pendingTooltip = lines;
            }
            currentX += 16;
        }
    }

    private void renderSearch(GuiGraphics guiGraphics) {
        String text = font.plainSubstrByWidth(search, 70, true);
        guiGraphics.drawString(font, text, searchX(), searchY(), 0x000000, false);
        if (searchFocused && (Util.getMillis() / 300L) % 2L == 0L) {
            guiGraphics.drawString(font, "_", searchX() + font.width(text), searchY(), 0x000000, false);
        }
    }

    private void renderButtonWithTooltip(GuiGraphics guiGraphics, TexturedButton button, int mouseX, int mouseY, String tooltipKey) {
        button.render(guiGraphics, mouseX, mouseY);
        if (button.isHovered(mouseX, mouseY)) {
            pendingTooltip = List.of(Component.translatable(tooltipKey));
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != InputConstants.MOUSE_BUTTON_LEFT) {
            return recipeListVisible || super.mouseClicked(mouseX, mouseY, button);
        }
        if (recipeListVisible) {
            return clickRecipeList(mouseX, mouseY);
        }
        if (viewRecipesButton().isHovered(mouseX, mouseY)) {
            playClickSound();
            toggleRecipeList();
            return true;
        }
        if (selectedRecipe != null) {
            TexturedButton craft = craftButton();
            if (craft.isHovered(mouseX, mouseY)) {
                if (craft.active) {
                    playClickSound();
                    MineCellsNetwork.CHANNEL.sendToServer(new CellCrafterCraftRequestC2SPacket(selectedRecipe.getId(), menu.getBlockPos()));
                }
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean clickRecipeList(double mouseX, double mouseY) {
        searchFocused = isInside(mouseX, mouseY, searchX(), searchY() - 2, 70, 12);
        if (searchFocused) {
            return true;
        }

        if (returnButton().isHovered(mouseX, mouseY)) {
            playClickSound();
            toggleRecipeList();
            return true;
        }
        TexturedButton apply = applyButton();
        if (apply.isHovered(mouseX, mouseY)) {
            if (apply.active) {
                playClickSound();
                selectedRecipe = listSelectedRecipe.recipe();
                toggleRecipeList();
            }
            return true;
        }
        TexturedButton up = upButton();
        if (up.isHovered(mouseX, mouseY)) {
            if (up.active) {
                playClickSound();
                scrollUp();
            }
            return true;
        }
        TexturedButton down = downButton();
        if (down.isHovered(mouseX, mouseY)) {
            if (down.active) {
                playClickSound();
                scrollDown();
            }
            return true;
        }

        CellForgeRecipe.Category[] categories = CellForgeRecipe.Category.values();
        for (int i = 0; i < categories.length; i++) {
            if (isInside(mouseX, mouseY, listX() + 8, categoryY(i), 40, 25)) {
                playClickSound();
                selectedCategory = categories[i];
                scrollOffset = 0;
                clearSearch();
                return true;
            }
        }

        int start = scrollOffset * COLUMNS;
        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                int index = start + row * COLUMNS + column;
                if (index < currentRecipes.size() && isInside(mouseX, mouseY, recipeX(column), recipeY(row), 16, 16)) {
                    DisplayedRecipe recipe = currentRecipes.get(index);
                    if (recipe.unlocked()) {
                        playClickSound();
                        listSelectedRecipe = recipe;
                    }
                    return true;
                }
            }
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (delta > 0) {
            scrollUp();
        } else if (delta < 0) {
            scrollDown();
        }
        return true;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (recipeListVisible && searchFocused) {
            if (SharedConstants.isAllowedChatCharacter(codePoint) && search.length() < SEARCH_MAX_LENGTH) {
                setSearch(search + codePoint);
            }
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (recipeListVisible && searchFocused && keyCode != GLFW.GLFW_KEY_ESCAPE) {
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE && !search.isEmpty()) {
                setSearch(Screen.hasControlDown() ? "" : search.substring(0, search.length() - 1));
            } else if (Screen.isPaste(keyCode)) {
                String pasted = SharedConstants.filterText(minecraft.keyboardHandler.getClipboard());
                setSearch((search + pasted).substring(0, Math.min(SEARCH_MAX_LENGTH, search.length() + pasted.length())));
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void setSearch(String value) {
        search = value;
        scrollOffset = 0;
        updateRecipes();
    }

    @Override
    public void onClose() {
        if (recipeListVisible) {
            toggleRecipeList();
        } else {
            super.onClose();
        }
    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int left, int top, int button) {
        return !recipeListVisible && super.hasClickedOutside(mouseX, mouseY, left, top, button);
    }

    private void playClickSound() {
        if (minecraft != null) {
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
    }

    private static boolean isInside(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    private record DisplayedRecipe(CellForgeRecipe recipe, boolean unlocked) {
        String name() {
            return recipe.output().getItem().getDescription().getString();
        }
    }

    private record TexturedButton(int x, int y, int width, int height, ResourceLocation texture, int u, int v, boolean active) {
        boolean isHovered(double mouseX, double mouseY) {
            return isInside(mouseX, mouseY, x, y, width, height);
        }

        void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
            int renderV = v;
            if (!active) {
                renderV += height * 2;
            } else if (isHovered(mouseX, mouseY)) {
                renderV += height;
            }
            guiGraphics.blit(texture, x, y, u, renderV, width, height, 256, 256);
        }
    }
}
