package com.github.mim1q.minecells.client;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.registry.MineCellsCreativeTabs;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.Util;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ContainerScreenEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.gui.CreativeTabsScreenPage;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.List;

@Mod.EventBusSubscriber(modid = MineCells.MOD_ID, value = Dist.CLIENT)
public final class MineCellsCreativeInventoryHandler {
    private static final ResourceLocation DISCORD_ICON = MineCells.id("textures/gui/button/discord.png");
    private static final ResourceLocation KOFI_ICON = MineCells.id("textures/gui/button/kofi.png");
    private static final int TITLE_COLOR = 4210752;

    private static final List<GroupButton> BUTTONS = new ArrayList<>();
    @Nullable
    private static Field selectedTabField;
    private static boolean selectedTabFieldResolved;

    private MineCellsCreativeInventoryHandler() {
    }

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof CreativeModeInventoryScreen screen)) {
            return;
        }
        BUTTONS.clear();
        int left = screen.getGuiLeft();
        int top = screen.getGuiTop();

        for (int i = 0; i < MineCellsCreativeTabs.SUB_TABS.size(); i++) {
            var subTab = MineCellsCreativeTabs.SUB_TABS.get(i);
            int index = i;
            Component tooltip = Component.empty()
                .append(subTab.title())
                .append("\n")
                .append(Component.translatable("itemGroup.minecells.minecells.select_hint"));
            var button = new GroupButton(left - 27, top + 10 + i * 30, 32, subTab.icon().get(), null, tooltip, b -> selectSubTab(screen, index));
            button.subTabIndex = index;
            addButton(event, button);
        }

        addButton(event, linkButton(screen, left + 198, top + 10, new ItemStack(Items.BOOK), null, "wiki", "https://mim1q.dev/minecells"));
        addButton(event, linkButton(screen, left + 198, top + 40, ItemStack.EMPTY, DISCORD_ICON, "discord", "https://discord.gg/6TjQbSjbuB"));
        addButton(event, linkButton(screen, left + 198, top + 70, ItemStack.EMPTY, KOFI_ICON, "kofi", "https://ko-fi.com/mim1q"));
        updateButtons();
    }

    @SubscribeEvent
    public static void onScreenRender(ScreenEvent.Render.Pre event) {
        if (event.getScreen() instanceof CreativeModeInventoryScreen) {
            updateButtons();
        }
    }

    @SubscribeEvent
    public static void onBackground(ContainerScreenEvent.Render.Background event) {
        if (!(event.getContainerScreen() instanceof CreativeModeInventoryScreen screen) || !isMineCellsSelected()) {
            return;
        }
        GuiGraphics graphics = event.getGuiGraphics();
        CreativeTabsScreenPage page = screen.getCurrentPage();
        CreativeModeTab selected = MineCellsCreativeTabs.MINECELLS.get();
        for (CreativeModeTab tab : page.getVisibleTabs()) {
            if (tab != selected) {
                renderTabHeader(graphics, screen, page, tab);
            }
        }
    }

    // The header part that overlaps the panel is left out, so the panel keeps covering it as in vanilla.
    private static void renderTabHeader(GuiGraphics graphics, CreativeModeInventoryScreen screen, CreativeTabsScreenPage page, CreativeModeTab tab) {
        boolean top = page.isTop(tab);
        int column = page.getColumn(tab);
        int x = screen.getGuiLeft() + (tab.isAlignedRight() ? screen.getXSize() - 27 * (7 - column) + 1 : 27 * column);
        int y = screen.getGuiTop();
        int v = 0;
        if (top) {
            y -= 28;
        } else {
            v += 64;
            y += screen.getYSize() - 4;
        }

        RenderSystem.enableBlend();
        if (top) {
            graphics.blit(MineCellsCreativeTabs.TABS_IMAGE, x, y, column * 26, v, 26, 28);
        } else {
            graphics.blit(MineCellsCreativeTabs.TABS_IMAGE, x, y + 4, column * 26, v + 4, 26, 28);
        }

        graphics.pose().pushPose();
        graphics.pose().translate(0.0F, 0.0F, 100.0F);
        int iconX = x + 5;
        int iconY = y + 8 + (top ? 1 : -1);
        ItemStack icon = tab.getIconItem();
        graphics.renderItem(icon, iconX, iconY);
        graphics.renderItemDecorations(Minecraft.getInstance().font, icon, iconX, iconY);
        graphics.pose().popPose();
    }

    @SubscribeEvent
    public static void onForeground(ContainerScreenEvent.Render.Foreground event) {
        if (!(event.getContainerScreen() instanceof CreativeModeInventoryScreen) || !isMineCellsSelected()) {
            return;
        }
        GuiGraphics graphics = event.getGuiGraphics();
        graphics.drawString(Minecraft.getInstance().font, MineCellsCreativeTabs.currentTitle(), 8, 6, TITLE_COLOR, false);
    }

    private static void addButton(ScreenEvent.Init.Post event, GroupButton button) {
        BUTTONS.add(button);
        event.addListener(button);
    }

    private static void updateButtons() {
        boolean visible = isMineCellsSelected();
        for (GroupButton button : BUTTONS) {
            button.visible = visible;
            button.active = visible;
            if (button.subTabIndex >= 0) {
                button.selected = MineCellsCreativeTabs.isSubTabActive(button.subTabIndex);
            }
        }
    }

    private static void selectSubTab(CreativeModeInventoryScreen screen, int index) {
        if (Screen.hasShiftDown()) {
            MineCellsCreativeTabs.toggleSubTab(index);
        } else {
            MineCellsCreativeTabs.selectSingleSubTab(index);
        }
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return;
        }
        MineCellsCreativeTabs.MINECELLS.get().buildContents(new CreativeModeTab.ItemDisplayParameters(
            player.connection.enabledFeatures(),
            minecraft.options.operatorItemsTab().get() && player.canUseGameMasterBlocks(),
            player.level().registryAccess()
        ));
        screen.init(minecraft, screen.width, screen.height);
    }

    private static GroupButton linkButton(Screen parent, int x, int y, ItemStack icon, @Nullable ResourceLocation iconTexture, String name, String url) {
        Component tooltip = Component.translatable("itemGroup.minecells.minecells.button." + name);
        return new GroupButton(x, y, 0, icon, iconTexture, tooltip, b -> {
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.setScreen(new ConfirmLinkScreen(confirmed -> {
                if (confirmed) {
                    Util.getPlatform().openUri(url);
                }
                minecraft.setScreen(parent);
            }, url, true));
        });
    }

    private static boolean isMineCellsSelected() {
        if (!selectedTabFieldResolved) {
            selectedTabFieldResolved = true;
            for (Field field : CreativeModeInventoryScreen.class.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) && field.getType() == CreativeModeTab.class) {
                    field.setAccessible(true);
                    selectedTabField = field;
                    break;
                }
            }
        }
        if (selectedTabField == null) {
            return false;
        }
        try {
            return selectedTabField.get(null) == MineCellsCreativeTabs.MINECELLS.get();
        } catch (IllegalAccessException e) {
            return false;
        }
    }

    private static final class GroupButton extends AbstractButton {
        private final int baseU;
        private final ItemStack icon;
        @Nullable
        private final ResourceLocation iconTexture;
        private final Consumer<GroupButton> action;
        private int subTabIndex = -1;
        private boolean selected;

        private GroupButton(int x, int y, int baseU, ItemStack icon, @Nullable ResourceLocation iconTexture, Component tooltip, Consumer<GroupButton> action) {
            super(x, y, 24, 24, tooltip);
            this.baseU = baseU;
            this.icon = icon;
            this.iconTexture = iconTexture;
            this.action = action;
            this.setTooltip(Tooltip.create(tooltip));
        }

        @Override
        public void onPress() {
            this.action.accept(this);
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            RenderSystem.enableDepthTest();
            int v = this.isHoveredOrFocused() || this.selected ? this.height : 0;
            graphics.blit(MineCellsCreativeTabs.BUTTONS, this.getX(), this.getY(), this.baseU, v, this.width, this.height, 64, 64);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            if (this.iconTexture != null) {
                graphics.blit(this.iconTexture, this.getX() + 4, this.getY() + 4, 0, 0, 16, 16, 16, 16);
            } else if (!this.icon.isEmpty()) {
                graphics.renderItem(this.icon, this.getX() + 4, this.getY() + 4);
            }
            RenderSystem.disableBlend();
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {
            this.defaultButtonNarrationText(output);
        }
    }
}
