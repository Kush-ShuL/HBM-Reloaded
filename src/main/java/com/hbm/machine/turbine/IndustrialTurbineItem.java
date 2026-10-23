package com.hbm.machine.turbine;
import com.hbm.item.BasicBlockItem;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
public final class IndustrialTurbineItem extends BlockItem {
    public IndustrialTurbineItem(Block block,Properties properties){super(block,properties);}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){super.appendHoverText(stack,context,lines,flag);if(BasicBlockItem.leftShiftHeld.getAsBoolean()){for(String line:Component.translatable("tile.machine_industrial_turbine.desc").getString().split("\\$"))lines.add(Component.literal(line).withStyle(ChatFormatting.YELLOW));}else lines.add(Component.translatable("tooltip.hbm.industrial_turbine.more_info"));}
}
