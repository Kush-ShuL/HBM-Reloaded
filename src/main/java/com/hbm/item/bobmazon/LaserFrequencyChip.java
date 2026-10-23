package com.hbm.item.bobmazon;

import com.hbm.satellite.item.SatelliteChipItem;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/** Original ItemSatChip frequency plus laser description; the chip is not a satellite. */
public final class LaserFrequencyChip extends SatelliteChipItem {
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context,
                               List<Component> text, TooltipFlag flag) {
        super.appendHoverText(stack, context, text, flag);
        text.add(Component.translatable("satchip.laser"));
    }
}
