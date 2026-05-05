package com.github.mim1q.minecells.item;

import com.github.mim1q.minecells.block.FlagBlock;
import com.github.mim1q.minecells.client.renderer.item.FlagBlockItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class FlagBlockItem extends BlockItem {
    private final FlagBlock flagBlock;

    public FlagBlockItem(FlagBlock flagBlock, Properties properties) {
        super(flagBlock, properties);
        this.flagBlock = flagBlock;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private FlagBlockItemRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = new FlagBlockItemRenderer(flagBlock);
                }
                return renderer;
            }
        });
    }
}
