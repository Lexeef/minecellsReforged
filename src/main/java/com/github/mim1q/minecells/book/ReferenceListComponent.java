package com.github.mim1q.minecells.book;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.button.GuiButtonEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.WeakHashMap;
import java.util.function.UnaryOperator;

/**
 * Referenced by class name from {@code patchouli_books/minecells_guidebook/en_us/templates/references.json}.
 * Only ever loaded by Patchouli.
 */
@SuppressWarnings("unused")
public class ReferenceListComponent implements ICustomComponent {
    private static final Set<ReferenceListComponent> DISPLAYED = Collections.newSetFromMap(new WeakHashMap<>());

    private IVariable content;

    private transient GuiBook bookGui = null;
    private transient Map<Integer, String> titles = new TreeMap<>();
    private transient List<ResourceLocation> references = new ArrayList<>();
    private transient List<AbstractWidget> buttons = new ArrayList<>();
    private transient int x = 0;
    private transient int y = 0;
    private transient int pageNum = 0;

    @Override
    public void build(int componentX, int componentY, int pageNum) {
        x = componentX;
        y = componentY;
        this.pageNum = pageNum;
    }

    @Override
    public void onDisplayed(IComponentRenderContext context) {
        removeButtons();
        if (!(context.getGui() instanceof GuiBook gui)) {
            return;
        }
        bookGui = gui;

        List<BookEntry> entries = references.stream()
            .map(it -> gui.book.getContents().entries.get(it))
            .filter(Objects::nonNull)
            .toList();

        int buttonY = y + 5;
        for (int i = 0; i < entries.size(); i++) {
            if (titles.get(i) != null) {
                buttonY += 20;
            }
            BookEntry entry = entries.get(i);
            GuiButtonEntry button = new GuiButtonEntry(gui, x, buttonY, entry, it -> context.navigateToEntry(entry.getId(), 0, false));
            context.addWidget(button, pageNum);
            buttons.add(button);
            buttonY += 10;
        }
        DISPLAYED.add(this);
    }

    @Override
    public void render(GuiGraphics graphics, IComponentRenderContext context, float pticks, int mouseX, int mouseY) {
        int space = -3;
        var font = Minecraft.getInstance().font;
        for (Map.Entry<Integer, String> title : titles.entrySet()) {
            graphics.drawString(font, Component.translatable(title.getValue()), x, y + space + title.getKey() * 10, 0x404040, false);
            space += 20;
        }
    }

    @Override
    public void onVariablesAvailable(UnaryOperator<IVariable> lookup) {
        titles.clear();
        references.clear();
        int i = 0;
        for (IVariable variable : lookup.apply(content).asList()) {
            String entry = variable.asString();
            if (entry.startsWith("# ")) {
                titles.put(i, entry.substring(2));
            } else {
                references.add(new ResourceLocation(entry));
                i++;
            }
        }
    }

    private void removeButtons() {
        if (bookGui != null && !buttons.isEmpty()) {
            bookGui.removeDrawablesIn(buttons);
        }
        buttons.clear();
    }

    /**
     * Widgets added through {@link IComponentRenderContext#addWidget} are not removed by Patchouli when the
     * page is turned (Fabric clears them with a mixin on {@code GuiBookEntry.onPageChanged}).
     */
    static void onScreenRender(Screen screen) {
        Iterator<ReferenceListComponent> iterator = DISPLAYED.iterator();
        while (iterator.hasNext()) {
            ReferenceListComponent component = iterator.next();
            if (component.bookGui != screen || component.bookGui.getSpread() != component.pageNum / 2) {
                component.removeButtons();
                iterator.remove();
            }
        }
    }
}
