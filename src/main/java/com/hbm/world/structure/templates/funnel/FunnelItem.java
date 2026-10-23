package com.hbm.world.structure.templates.funnel;
import java.util.List;
import java.util.function.BooleanSupplier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
/** ITooltipProvider's original left-shift-only, dollar-separated yellow information. */
public final class FunnelItem extends BlockItem {
    public static BooleanSupplier leftShift=()->false;
    public FunnelItem(Block block){super(block,new Item.Properties());}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flag){super.appendHoverText(stack,context,tooltip,flag);if(leftShift.getAsBoolean())for(String line:Component.translatable("tile.machine_funnel.desc").getString().split("\\$"))tooltip.add(Component.literal(line).withStyle(ChatFormatting.YELLOW));else tooltip.add(Component.translatable("tooltip.hbm.funnel.more_info",Component.literal("LSHIFT").withStyle(ChatFormatting.YELLOW,ChatFormatting.ITALIC)).withStyle(ChatFormatting.DARK_GRAY,ChatFormatting.ITALIC));}
}
