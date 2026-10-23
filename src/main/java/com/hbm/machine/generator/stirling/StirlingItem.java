package com.hbm.machine.generator.stirling;
import com.hbm.hazard.HbmHazards;import com.hbm.item.BasicBlockItem;import java.util.List;import net.minecraft.ChatFormatting;import net.minecraft.network.chat.Component;import net.minecraft.world.item.*;import net.minecraft.world.level.block.Block;
/** Actual ItemBlockMulti damage1 means missing cog, same name and normal source creativevariant0. */
public final class StirlingItem extends BlockItem {
 public final StirlingKind kind;public StirlingItem(Block block,StirlingKind kind){super(block,new Properties().component(HbmHazards.LEGACY_METADATA.get(),0));this.kind=kind;}
 public static boolean broken(ItemStack stack){return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0)==1;}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){super.appendHoverText(stack,context,lines,flag);if(BasicBlockItem.leftShiftHeld.getAsBoolean())for(var line:Component.translatable("tile."+kind.id+".desc").getString().split("\\$"))lines.add(Component.literal(line).withStyle(ChatFormatting.YELLOW));else lines.add(com.hbm.item.TooltipHints.moreInfo());}
}
