package com.hbm.compat.computercraft.rtty;

import com.hbm.item.rttycard.RttyCardContent;
import dan200.computercraft.api.upgrades.UpgradeBase;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.ItemStack;

/** CC's default UpgradeBase discards components: preserve the actual physical card on install/remove. */
interface RttyCardData extends UpgradeBase {
    @Override default boolean isItemSuitable(ItemStack stack) { return stack.is(RttyCardContent.CARD.get()); }
    @Override default DataComponentPatch getUpgradeData(ItemStack stack) { return stack.getComponentsPatch(); }
    @Override default ItemStack getUpgradeItem(DataComponentPatch data) {
        ItemStack stack = getCraftingItem().copy();
        stack.applyComponents(data);
        return stack;
    }
}
