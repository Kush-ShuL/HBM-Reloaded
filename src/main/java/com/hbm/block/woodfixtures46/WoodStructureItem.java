package com.hbm.block.woodfixtures46;

import com.hbm.hazard.HbmHazards;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

/** Source ItemBlockBase + BlockEnumMulti's three named, single-texture subtypes. */
public final class WoodStructureItem extends BlockItem {
    private static final String[] NAMES = {"roof", "scaffold", "ceiling"};
    public WoodStructureItem(WoodStructureBlock block) { super(block, new Properties().component(HbmHazards.LEGACY_METADATA.get(), 0)); }
    public static int type(ItemStack stack) { return WoodStructureBlock.type(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(), 0)); }
    public static ItemStack stack(int metadata) {
        var stack = new ItemStack(WoodFixturesContent.STRUCTURE_ITEM.get());
        stack.set(HbmHazards.LEGACY_METADATA.get(), WoodStructureBlock.type(metadata)); return stack;
    }
    @Override public Component getName(ItemStack stack) { return Component.translatable("tile.wood_structure." + NAMES[type(stack)] + ".name"); }
}
