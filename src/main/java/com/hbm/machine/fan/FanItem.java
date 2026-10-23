package com.hbm.machine.fan;

import com.hbm.item.BasicBlockItem;
import com.hbm.item.TooltipHints;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

/** Source ITooltipProvider's exact left-shift convention; localization follows the active game language. */
public final class FanItem extends BlockItem {
    public FanItem(FanBlock block) { super(block,new Properties()); }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag) {
        super.appendHoverText(stack,context,lines,flag);
        if(BasicBlockItem.leftShiftHeld.getAsBoolean())for(String line:Language.getInstance().getOrDefault("tile.fan.desc").split("\\$"))lines.add(Component.literal(line).withStyle(ChatFormatting.YELLOW));
        else lines.add(TooltipHints.moreInfo());
    }
}
