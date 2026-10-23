package com.hbm.machine.industrialboiler;
import com.hbm.item.BasicBlockItem;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
/** Original HeaterOven ITooltipProvider's left-shift description and exact hint. */
public final class IndustrialBoilerBlockItem extends BlockItem {
 public IndustrialBoilerBlockItem(Block block,Properties properties){super(block,properties);}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){super.appendHoverText(stack,context,lines,flag);if(BasicBlockItem.leftShiftHeld.getAsBoolean()){for(String line:Language.getInstance().getOrDefault("tile.machine_industrial_boiler.desc").split("\\$"))lines.add(Component.literal(line).withStyle(ChatFormatting.YELLOW));}else lines.add(Component.translatable("tooltip.hbm.industrial_boiler.more_info"));}
}
