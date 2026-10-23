package com.hbm.item.bobmazon;

import com.hbm.satellite.item.SatelliteChipItem;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/** Original ItemSatChip's Gerald description, with the shared real freq component. */
public final class GeraldFrequencyChip extends SatelliteChipItem {
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> text, TooltipFlag flag) {
        super.appendHoverText(stack, context, text, flag);
        for (String line : Component.translatable("satchip.gerald.desc").getString().split("\\$")) text.add(Component.literal(line));
    }
}
