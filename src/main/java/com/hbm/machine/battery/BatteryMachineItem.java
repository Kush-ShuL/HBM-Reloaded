package com.hbm.machine.battery;

import com.hbm.item.battery.BatteryItem;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;

/** Persistent energy and original storage information remain visible on drops. */
public final class BatteryMachineItem extends BlockItem {
    private final BatteryTier tier;

    public BatteryMachineItem(BatteryBlock block) {
        super(block, new Properties());
        tier = block.tier();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        long power = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag()
                .getCompound("persistent").getLong("power");
        tooltip.add(Component.literal("Stores up to " + BatteryItem.shortNumber(tier.capacity()) + "HE").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.literal("Charge speed: " + BatteryItem.shortNumber(tier.receiverSpeed()) + "HE").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.literal("Discharge speed: " + BatteryItem.shortNumber(tier.providerSpeed()) + "HE").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.literal(BatteryItem.shortNumber(power) + "/" + BatteryItem.shortNumber(tier.capacity()) + "HE").withStyle(ChatFormatting.YELLOW));
    }
}
