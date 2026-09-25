package com.github.mim1q.minecells.book;

import com.github.mim1q.minecells.MineCells;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Patchouli-free on purpose: the guidebook is resolved by registry id, so this runs with or without Patchouli.
 */
public final class PatchouliCompat {
    public static final ResourceLocation BOOK_ADVANCEMENT = MineCells.id("book/book");
    public static final ResourceLocation GUIDEBOOK = MineCells.id("minecells_guidebook");
    private static final ResourceLocation GUIDE_BOOK_ITEM = new ResourceLocation("patchouli", "guide_book");

    private PatchouliCompat() {
    }

    public static void onAdvancementEarned(AdvancementEvent.AdvancementEarnEvent event) {
        if (!BOOK_ADVANCEMENT.equals(event.getAdvancement().getId())) {
            return;
        }
        ItemStack book = createGuidebook();
        if (book.isEmpty()) {
            return;
        }
        var player = event.getEntity();
        if (player.addItem(book)) {
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F,
                ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
        } else {
            player.drop(book, false);
        }
    }

    public static ItemStack createGuidebook() {
        Item item = ForgeRegistries.ITEMS.getValue(GUIDE_BOOK_ITEM);
        if (item == null || item == Items.AIR) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = new ItemStack(item);
        stack.getOrCreateTag().putString("patchouli:book", GUIDEBOOK.toString());
        return stack;
    }
}
