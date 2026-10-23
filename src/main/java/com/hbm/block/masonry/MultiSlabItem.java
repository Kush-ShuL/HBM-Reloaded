package com.hbm.block.masonry;

import com.hbm.hazard.HbmHazards;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

public final class MultiSlabItem extends BlockItem {
    private final String sourceName;
    public MultiSlabItem(LegacyMultiSlabBlock block,String sourceName) { super(block,new Properties()); this.sourceName=sourceName; }
    @Override public Component getName(ItemStack stack) { return Component.translatable("tile."+sourceName+"."+((LegacyMultiSlabBlock)getBlock()).materialName(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0))+".name"); }
}
