package com.github.mim1q.minecells.client.renderer.item;

import com.github.mim1q.minecells.config.MineCellsConfig;
import com.github.mim1q.minecells.MineCells;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Items that use one model in GUI / ground / item frames and another one when held.
 */
@Mod.EventBusSubscriber(modid = MineCells.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class HandheldItemModels {
    private static final List<String> SHIELDS = List.of(
        "assault_shield", "bloodthirsty_shield", "cudgel", "greed_shield", "ice_shield", "rampart"
    );
    private static final List<String> WEAPONS = List.of(
        "assassins_dagger", "broadsword", "balanced_blade", "blood_sword", "crowbar", "flint", "hattoris_katana",
        "spite_sword", "cursed_sword", "phaser", "frost_blast", "nutcracker", "tentacle"
    );

    private HandheldItemModels() {
    }

    @SubscribeEvent
    public static void registerAdditional(ModelEvent.RegisterAdditional event) {
        SHIELDS.forEach(name -> event.register(MineCells.id("item/shield_3d/" + name)));
        if (keepOriginalGuiModels()) {
            WEAPONS.forEach(name -> event.register(MineCells.id("item/weapon/" + name)));
        }
    }

    @SubscribeEvent
    public static void modifyBakingResult(ModelEvent.ModifyBakingResult event) {
        Map<ResourceLocation, BakedModel> models = event.getModels();
        for (String name : SHIELDS) {
            wrap(models, name, null, MineCells.id("item/shield_3d/" + name));
        }
        if (keepOriginalGuiModels()) {
            for (String name : WEAPONS) {
                wrap(models, name, MineCells.id("item/weapon/" + name), null);
            }
        }
    }

    private static void wrap(
        Map<ResourceLocation, BakedModel> models,
        String name,
        @Nullable ResourceLocation guiId,
        @Nullable ResourceLocation handId
    ) {
        ModelResourceLocation itemId = new ModelResourceLocation(MineCells.id(name), "inventory");
        BakedModel itemModel = models.get(itemId);
        if (itemModel == null) return;
        BakedModel gui = guiId == null ? itemModel : models.get(guiId);
        BakedModel hand = handId == null ? itemModel : models.get(handId);
        if (gui == null || hand == null) {
            MineCells.LOGGER.warn("Missing handheld model parts for {}", name);
            return;
        }
        models.put(itemId, new HandheldModel(gui, hand));
    }

    private static boolean keepOriginalGuiModels() {
        try {
            return MineCellsConfig.CLIENT.keepOriginalGuiModels.get();
        } catch (IllegalStateException e) {
            return false;
        }
    }

    private static boolean isGuiContext(ItemDisplayContext context) {
        return context == ItemDisplayContext.GUI
            || context == ItemDisplayContext.GROUND
            || context == ItemDisplayContext.FIXED;
    }

    private static final class HandheldModel extends BakedModelWrapper<BakedModel> {
        private final BakedModel gui;
        private final BakedModel hand;
        private final ItemOverrides overrides;

        private HandheldModel(BakedModel gui, BakedModel hand) {
            super(gui);
            this.gui = gui;
            this.hand = hand;
            this.overrides = new HandheldOverrides(this);
        }

        @Override
        public BakedModel applyTransform(ItemDisplayContext context, PoseStack poseStack, boolean applyLeftHandTransform) {
            BakedModel target = isGuiContext(context) ? gui : hand;
            return target.applyTransform(context, poseStack, applyLeftHandTransform);
        }

        @Override
        public ItemOverrides getOverrides() {
            return overrides;
        }
    }

    private static final class HandheldOverrides extends ItemOverrides {
        private final HandheldModel owner;

        private HandheldOverrides(HandheldModel owner) {
            this.owner = owner;
        }

        @Nullable
        @Override
        public BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
            BakedModel gui = owner.gui.getOverrides().resolve(owner.gui, stack, level, entity, seed);
            BakedModel hand = owner.hand.getOverrides().resolve(owner.hand, stack, level, entity, seed);
            if (gui == null) gui = owner.gui;
            if (hand == null) hand = owner.hand;
            if (gui == owner.gui && hand == owner.hand) return owner;
            return new HandheldModel(gui, hand);
        }
    }
}
