package com.hbm.hazard;

import java.util.List;
import net.minecraft.world.item.ItemStack;

/** Real portable inventory items expose their stored stacks to the source shielding rules. */
public interface HazardInventoryItem {
    List<ItemStack> hazardContents(ItemStack stack,int maximumSlots);
}
