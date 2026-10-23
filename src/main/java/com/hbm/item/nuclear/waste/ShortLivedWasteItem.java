package com.hbm.item.nuclear.waste;

import com.hbm.hazard.HbmHazards;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/** Source ItemWasteShort extends Item directly, with ordinary item-entity lifetime and damage. */
public final class ShortLivedWasteItem extends Item {
    public ShortLivedWasteItem() {
        super(new Properties().component(HbmHazards.LEGACY_METADATA.get(), 0));
    }

    public int metadata(ItemStack stack) { return SourceWasteIsotopes.shortMetadata(stack); }

    public List<ItemStack> creativeStacks() {
        return SourceWasteIsotopes.stacks(this, SourceWasteIsotopes.ShortLived.values().length);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        lines.add(SourceWasteIsotopes.tooltip(SourceWasteIsotopes.ShortLived.values()[metadata(stack)].translation));
        super.appendHoverText(stack, context, lines, flag);
    }
}
