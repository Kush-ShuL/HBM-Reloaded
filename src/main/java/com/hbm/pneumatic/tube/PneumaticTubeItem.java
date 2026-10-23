package com.hbm.pneumatic.tube;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
public final class PneumaticTubeItem extends BlockItem {
 public static java.util.function.BooleanSupplier LEFT_SHIFT=()->false;private final String id;public PneumaticTubeItem(Block block,String id){super(block,new Properties());this.id=id;}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flag){if(id.equals("pipe_anchor_pneumatic")){tooltip.add(Component.translatable("tooltip.hbm.pneumatic.anchor.single",Component.translatable("tooltip.hbm.pneumatic.anchor.single_value").withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GOLD));tooltip.add(Component.translatable("tooltip.hbm.pneumatic.anchor.range",Component.literal("10m").withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GOLD));return;}if(LEFT_SHIFT.getAsBoolean()){for(String line:Component.translatable("tile."+id+".desc").getString().split("\\$",-1))tooltip.add(Component.literal(line).withStyle(ChatFormatting.YELLOW));}else tooltip.add(Component.translatable("gui.hbm.pneumatic.shift",Component.literal("LSHIFT").withStyle(ChatFormatting.YELLOW,ChatFormatting.ITALIC)).withStyle(ChatFormatting.DARK_GRAY,ChatFormatting.ITALIC));}
}
