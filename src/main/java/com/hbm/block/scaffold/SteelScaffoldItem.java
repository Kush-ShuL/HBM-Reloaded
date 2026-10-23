package com.hbm.block.scaffold;

import com.hbm.hazard.HbmHazards;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

public final class SteelScaffoldItem extends BlockItem {
    public SteelScaffoldItem(SteelScaffoldBlock block) { super(block, new Properties().component(HbmHazards.LEGACY_METADATA.get(), 0)); }
    public static int color(ItemStack stack) { return SteelScaffoldBlock.color(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(), 0)); }
    @Override public Component getName(ItemStack stack) { return Component.translatable("tile.steel_scaffold.name"); }
}
