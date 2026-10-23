package com.hbm.block.sourceslab;

import com.hbm.hazard.HbmHazards;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

/** ItemModSlab retains the exact source meta-specific unlocalized name. */
public final class SourceStoneSlabItem extends BlockItem {
    public SourceStoneSlabItem(SourceStoneSlabBlock block) { super(block, new Properties()); }
    @Override public Component getName(ItemStack stack) {
        int variant = ((SourceStoneSlabBlock)getBlock()).material(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(), 0));
        return Component.translatable("tile.stones_slab.lightstone." + (variant + 1) + ".name");
    }
}
