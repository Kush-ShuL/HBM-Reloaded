package com.hbm.world.structure.templates.heatboiler;
import com.hbm.hazard.HbmHazards;
import com.hbm.item.BasicBlockItem;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
/** Actual IBlockMulti keeps raw item metadata (1 renders/places the burst form) and source left-shift description. */
public final class HeatBoilerItem extends BlockItem {
    public HeatBoilerItem(Block block){super(block,new Properties().component(HbmHazards.LEGACY_METADATA.get(),0));}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){super.appendHoverText(stack,context,lines,flag);if(BasicBlockItem.leftShiftHeld.getAsBoolean())for(String line:Component.translatable("tile.machine_boiler.desc").getString().split("\\$"))lines.add(Component.literal(line).withStyle(ChatFormatting.YELLOW));else lines.add(Component.translatable("tooltip.hbm.heat_boiler.more_info",Component.literal("LSHIFT").withStyle(ChatFormatting.YELLOW,ChatFormatting.ITALIC)).withStyle(ChatFormatting.DARK_GRAY,ChatFormatting.ITALIC));}
}
