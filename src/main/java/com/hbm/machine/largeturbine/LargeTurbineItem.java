package com.hbm.machine.largeturbine;
import com.hbm.item.BasicBlockItem;
import com.hbm.item.TooltipHints;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
public final class LargeTurbineItem extends BlockItem {
 public LargeTurbineItem(LargeTurbineBlock block){super(block,new Properties());}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){super.appendHoverText(stack,context,lines,flag);if(BasicBlockItem.leftShiftHeld.getAsBoolean())for(String line:Component.translatable("tile.machine_large_turbine.desc").getString().split("\\$"))lines.add(Component.literal(line).withStyle(ChatFormatting.YELLOW));else lines.add(TooltipHints.moreInfo());}
}
