package com.hbm.item.nuclear.waste;

import com.hbm.bomb.nuclear.programmable.SourceNuclearWasteItem;
import com.hbm.hazard.HbmHazards;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/** Source ItemWasteLong: five isotopes and the unique real ItemNuclearWaste dropped-entity behavior. */
public final class LongLivedWasteItem extends SourceNuclearWasteItem {
    public LongLivedWasteItem() {
        super(new Properties().component(HbmHazards.LEGACY_METADATA.get(), 0));
    }

    public int metadata(ItemStack stack) { return SourceWasteIsotopes.longMetadata(stack); }

    public List<ItemStack> creativeStacks() {
        return SourceWasteIsotopes.stacks(this, SourceWasteIsotopes.LongLived.values().length);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        lines.add(SourceWasteIsotopes.tooltip(SourceWasteIsotopes.LongLived.values()[metadata(stack)].translation));
        super.appendHoverText(stack, context, lines, flag);
    }
}
