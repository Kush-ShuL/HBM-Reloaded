package com.hbm.machine.pa.cooled;
import com.hbm.item.BasicBlockItem;
import com.hbm.item.TooltipHints;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
public final class PaBlockItem extends BlockItem{
 private final PaKind kind;public PaBlockItem(PaBlock block){super(block,new Item.Properties());kind=block.kind;}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flags){super.appendHoverText(stack,context,lines,flags);if(BasicBlockItem.leftShiftHeld.getAsBoolean()){for(String line:Language.getInstance().getOrDefault("tile."+kind.id+".desc").split("\\$"))lines.add(Component.literal(line).withStyle(ChatFormatting.YELLOW));}else lines.add(TooltipHints.moreInfo());}
}
