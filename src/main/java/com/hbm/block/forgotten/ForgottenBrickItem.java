package com.hbm.block.forgotten;

import com.hbm.block.ore.LegacyOreItem;
import net.minecraft.world.item.ItemStack;

/** Reuses the sole raw metadata BlockItem/placement/drop component. Original IBlockMulti names every subtype identically. */
public final class ForgottenBrickItem extends LegacyOreItem {
    public ForgottenBrickItem(ForgottenBrickBlock block){super(block);}
    @Override public String getDescriptionId(ItemStack stack){return getBlock().getDescriptionId();}
}
