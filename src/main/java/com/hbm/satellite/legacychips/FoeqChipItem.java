package com.hbm.satellite.legacychips;

import com.hbm.satellite.item.SatelliteChipItem;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/** Source ItemSatChip's actual FOEQ-only tooltip branch, with the existing freq NBT contract. */
public final class FoeqChipItem extends SatelliteChipItem {
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flags) {
        super.appendHoverText(stack, context, lines, flags);
        lines.add(Component.translatable("satchip.foeq"));
    }
}
