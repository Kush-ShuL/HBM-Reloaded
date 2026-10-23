package com.hbm.machine.assemblyfactory;
import com.hbm.item.BasicBlockItem;
import com.hbm.item.TooltipHints;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
/** Original ITooltipProvider, shared exact left-shift convention and active-locale dollar-separated explanation. */
public final class AssemblyFactoryItem extends BlockItem {
 public AssemblyFactoryItem(AssemblyFactoryBlock block){super(block,new Properties());}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){super.appendHoverText(stack,context,lines,flag);if(BasicBlockItem.leftShiftHeld.getAsBoolean())for(var line:Language.getInstance().getOrDefault("tile.machine_assembly_factory.desc").split("\\$"))lines.add(Component.literal(line).withStyle(ChatFormatting.YELLOW));else lines.add(TooltipHints.moreInfo());}
}
