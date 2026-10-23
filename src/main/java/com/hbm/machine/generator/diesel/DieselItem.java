package com.hbm.machine.generator.diesel;
import com.hbm.fluid.FluidTraits.FuelGrade;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
public final class DieselItem extends BlockItem {
    public DieselItem(Block block,Properties properties){super(block,properties);}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){lines.add(Component.translatable("tooltip.hbm.diesel.efficiency").withStyle(ChatFormatting.YELLOW));for(var grade:FuelGrade.values()){Double efficiency=DieselConfig.efficiency.get(grade);if(efficiency==null)continue;String suffix=switch(grade){case AERO->"aviation";case GAS->"gaseous";default->grade.name().toLowerCase(java.util.Locale.ROOT);};lines.add(Component.literal("-").append(Component.translatable("hbmfluid.trait.fuel."+suffix)).append(": ").withStyle(ChatFormatting.YELLOW).append(Component.literal((int)(efficiency*100)+"%").withStyle(ChatFormatting.RED)));}}
}
