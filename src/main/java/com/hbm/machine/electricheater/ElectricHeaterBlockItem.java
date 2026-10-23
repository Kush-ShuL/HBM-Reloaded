package com.hbm.machine.electricheater;
import com.hbm.item.BasicBlockItem;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
/** Original HeaterOven ITooltipProvider's left-shift description and exact hint. */
public final class ElectricHeaterBlockItem extends BlockItem {
 public ElectricHeaterBlockItem(Block block,Properties properties){super(block,properties);}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){super.appendHoverText(stack,context,lines,flag);if(BasicBlockItem.leftShiftHeld.getAsBoolean()){for(String line:Language.getInstance().getOrDefault("tile.heater_electric.desc").split("\\$"))lines.add(Component.literal(line).withStyle(ChatFormatting.YELLOW));}else lines.add(Component.translatable("tooltip.hbm.electric_heater.more_info"));}
}
