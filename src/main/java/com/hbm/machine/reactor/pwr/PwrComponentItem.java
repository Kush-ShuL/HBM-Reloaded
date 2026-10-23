package com.hbm.machine.reactor.pwr;

import com.hbm.item.BasicBlockItem;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;

/** Source ItemBlockBase/ITooltipProvider left-shift lore. */
public final class PwrComponentItem extends BlockItem {
    private final String key;
    public PwrComponentItem(Block block,String sourceId){super(block,new Properties());key="tile."+sourceId+".desc";}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){if(BasicBlockItem.leftShiftHeld.getAsBoolean())for(String line:Language.getInstance().getOrDefault(key).split("\\$"))lines.add(Component.literal(line).withStyle(ChatFormatting.YELLOW));else lines.add(Component.translatable("tooltip.hbm.hold_shift").withStyle(ChatFormatting.DARK_GRAY,ChatFormatting.ITALIC));}
}
