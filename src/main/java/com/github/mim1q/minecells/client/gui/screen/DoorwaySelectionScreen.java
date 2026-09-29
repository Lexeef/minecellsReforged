package com.github.mim1q.minecells.client.gui.screen;

import com.github.mim1q.minecells.block.portal.DoorwayPortalBlock.DoorwayType;
import com.github.mim1q.minecells.client.MineCellsClientData;
import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.network.c2s.UpdateDoorwayC2SPacket;
import com.github.mim1q.minecells.network.MineCellsNetwork;
import com.github.mim1q.minecells.world.DoorwayRequirements;
import com.github.mim1q.minecells.world.state.MineCellsData;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DoorwaySelectionScreen extends Screen {
    private static final DoorwayType[] DIMENSIONS = {
        DoorwayType.PRISON,
        DoorwayType.PROMENADE,
        DoorwayType.RAMPARTS,
        DoorwayType.INSUFFERABLE_CRYPT,
        DoorwayType.BLACK_BRIDGE
    };
    private static final int BUTTON_SIZE = 32;
    private static final int BUTTON_GAP = 4;
    private static final int BODY_HEIGHT = 150;
    private static final int FLAG_WIDTH = 48;
    private static final int FLAG_MARGIN = 16;
    private static final int TEXT_WIDTH = 200;
    private static final int TOTAL_HEIGHT = 25 + BUTTON_SIZE + 4 + BODY_HEIGHT + 4 + BUTTON_SIZE;

    private final BlockPos doorwayPos;
    private final BlockPos anchor;
    @Nullable
    private DoorwayType selected;
    private final List<IconButton> dimensionButtons = new ArrayList<>();
    private final List<IconButton> exitButtons = new ArrayList<>();
    private List<FormattedCharSequence> descriptionLines = List.of();
    private int top;

    public DoorwaySelectionScreen(BlockPos doorwayPos, BlockPos anchor) {
        super(Component.translatable("gui.minecells.doorway_selection.title"));
        this.doorwayPos = doorwayPos;
        this.anchor = anchor;
    }

    public static void open(BlockPos doorwayPos, BlockPos anchor) {
        Minecraft.getInstance().setScreen(new DoorwaySelectionScreen(doorwayPos, anchor));
    }

    @Override
    protected void init() {
        dimensionButtons.clear();
        exitButtons.clear();
        top = Math.max(4, (height - TOTAL_HEIGHT) / 2);

        LocalPlayer player = Minecraft.getInstance().player;
        MineCellsData.PlayerData playerData = MineCellsClientData.getPlayerData().get(anchor);
        int dimensionsWidth = DIMENSIONS.length * BUTTON_SIZE + (DIMENSIONS.length - 1) * BUTTON_GAP;
        int x = width / 2 - dimensionsWidth / 2;
        int dimensionsY = top + 25;
        for (DoorwayType type : DIMENSIONS) {
            boolean locked = player == null || !DoorwayRequirements.isMet(type, player, playerData);
            IconButton button = new IconButton(
                x, dimensionsY,
                MineCells.id("textures/gui/doorway_selection/dimension/" + type.dimensionId().getPath() + ".png"),
                createDimensionTooltip(type, locked),
                () -> select(type)
            );
            button.active = !locked;
            button.selected = type == selected;
            dimensionButtons.add(addRenderableWidget(button));
            x += BUTTON_SIZE + BUTTON_GAP;
        }

        int exitWidth = 3 * BUTTON_SIZE + 2 * BUTTON_GAP;
        int exitX = width / 2 - exitWidth / 2;
        int exitY = dimensionsY + BUTTON_SIZE + 4 + BODY_HEIGHT + 4;
        exitButtons.add(addRenderableWidget(createExitButton("apply_only_me", exitX, exitY, () -> apply(true))));
        exitButtons.add(addRenderableWidget(createExitButton("apply_everyone", exitX + BUTTON_SIZE + BUTTON_GAP, exitY, () -> apply(false))));
        IconButton close = addRenderableWidget(createExitButton("close", exitX + 2 * (BUTTON_SIZE + BUTTON_GAP), exitY, this::onClose));
        close.active = true;
        updateBody();
    }

    private IconButton createExitButton(String name, int x, int y, Runnable action) {
        IconButton button = new IconButton(
            x, y,
            MineCells.id("textures/gui/doorway_selection/" + name + ".png"),
            List.of(Component.translatable("gui.minecells.doorway_selection.buttons." + name)),
            action
        );
        button.active = selected != null;
        return button;
    }

    private static List<Component> createDimensionTooltip(DoorwayType type, boolean locked) {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(getDimensionName(type));
        if (type == DoorwayType.BLACK_BRIDGE || type == DoorwayType.INSUFFERABLE_CRYPT) {
            tooltip.add(Component.translatable("gui.minecells.doorway_selection.boss").withStyle(ChatFormatting.RED));
        }
        if (locked) {
            tooltip.add(Component.translatable("gui.minecells.doorway_selection.locked").withStyle(ChatFormatting.GRAY));
            DoorwayRequirements.getTooltips(type).forEach(line -> tooltip.add(line.copy().withStyle(ChatFormatting.GRAY)));
        }
        return tooltip;
    }

    private static MutableComponent getDimensionName(DoorwayType type) {
        ResourceLocation id = type.dimensionId();
        MineCellsDimension dimension = MineCellsDimension.of(id);
        int color = dimension != null ? dimension.getColor() : type.getColor();
        return Component.translatable("dimension." + id.getNamespace() + "." + id.getPath())
            .withStyle(style -> style.withBold(true).withColor(color));
    }

    private static String getFlagName(DoorwayType type) {
        return switch (type) {
            case PROMENADE -> "promenade_of_the_condemned";
            case RAMPARTS -> "ramparts";
            case BLACK_BRIDGE -> "black_bridge";
            case INSUFFERABLE_CRYPT -> "insufferable_crypt";
            default -> "torn_kings_crest";
        };
    }

    private void select(DoorwayType type) {
        selected = type;
        for (int i = 0; i < DIMENSIONS.length; i++) {
            dimensionButtons.get(i).selected = DIMENSIONS[i] == type;
        }
        exitButtons.forEach(button -> button.active = true);
        updateBody();
    }

    private void updateBody() {
        if (selected == null) {
            descriptionLines = List.of();
            return;
        }
        String description = Component.translatable("book.minecells.entries.dimensions." + selected.dimensionId().getPath() + ".description")
            .getString()
            .replace("<br><br>", " ");
        descriptionLines = font.split(Component.literal(description), TEXT_WIDTH);
    }

    private void apply(boolean onlyOwnerCanEnter) {
        if (selected == null) {
            return;
        }
        MineCellsNetwork.CHANNEL.sendToServer(new UpdateDoorwayC2SPacket(doorwayPos, selected, onlyOwnerCanEnter));
        onClose();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        graphics.drawCenteredString(font, title, width / 2, top + 4, 0xFFFFFF);
        renderBody(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        for (IconButton button : dimensionButtons) {
            if (button.isHovered()) {
                graphics.renderComponentTooltip(font, button.tooltip, mouseX, mouseY);
                return;
            }
        }
        for (IconButton button : exitButtons) {
            if (button.isHovered()) {
                graphics.renderComponentTooltip(font, button.tooltip, mouseX, mouseY);
                return;
            }
        }
    }

    private void renderBody(GuiGraphics graphics) {
        if (selected == null) {
            return;
        }
        int bodyY = top + 25 + BUTTON_SIZE + 4;
        int bodyWidth = FLAG_WIDTH + FLAG_MARGIN + TEXT_WIDTH;
        int flagX = width / 2 - bodyWidth / 2;
        int textX = flagX + FLAG_WIDTH + FLAG_MARGIN;

        ResourceLocation flag = MineCells.id("textures/blockentity/banner/" + getFlagName(selected) + ".png");
        float scale = FLAG_WIDTH / 16.0F;
        graphics.pose().pushPose();
        graphics.pose().translate(flagX, bodyY + 3, 0.0F);
        graphics.pose().scale(scale, scale, 1.0F);
        graphics.blit(flag, 0, 0, 2.0F, 2.0F, 16, 2, 64, 64);
        graphics.blit(flag, 0, 2, 0.0F, 16.0F, 16, 48, 64, 64);
        graphics.pose().popPose();

        graphics.drawCenteredString(font, getDimensionName(selected), textX + TEXT_WIDTH / 2, bodyY + 10, 0xFFFFFF);
        int lineY = bodyY + 28;
        int maxLines = (BODY_HEIGHT - 28) / (font.lineHeight + 1);
        for (int i = 0; i < descriptionLines.size() && i < maxLines; i++) {
            graphics.drawString(font, descriptionLines.get(i), textX, lineY, 0xFFFFFF, true);
            lineY += font.lineHeight + 1;
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private static final class IconButton extends AbstractButton {
        private final ResourceLocation texture;
        private final List<Component> tooltip;
        private final Runnable action;
        private boolean selected;

        private IconButton(int x, int y, ResourceLocation texture, List<Component> tooltip, Runnable action) {
            super(x, y, BUTTON_SIZE, BUTTON_SIZE, tooltip.isEmpty() ? Component.empty() : tooltip.get(0));
            this.texture = texture;
            this.tooltip = tooltip;
            this.action = action;
        }

        @Override
        public void onPress() {
            action.run();
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            int v = !active ? 0 : (selected || isHoveredOrFocused()) ? 64 : 32;
            graphics.blit(texture, getX(), getY(), 0.0F, v, BUTTON_SIZE, BUTTON_SIZE, 32, 96);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {
            defaultButtonNarrationText(output);
        }
    }
}
