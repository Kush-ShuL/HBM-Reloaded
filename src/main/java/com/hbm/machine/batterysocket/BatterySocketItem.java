package com.hbm.machine.batterysocket;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public final class BatterySocketItem extends BlockItem {
    public BatterySocketItem() {super(BatterySocketContent.BLOCK.get(),new Properties());}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flags) {
        if(BatterySocketContent.detailedTooltip.getAsBoolean()) for(String line:Component.translatable("tile.machine_battery_socket.desc").getString().split("\\$"))tooltip.add(Component.literal(line).withStyle(ChatFormatting.YELLOW));
        else tooltip.add(com.hbm.item.TooltipHints.moreInfo());
    }
}
