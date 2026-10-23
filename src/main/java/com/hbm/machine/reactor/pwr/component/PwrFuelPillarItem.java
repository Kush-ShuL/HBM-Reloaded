package com.hbm.machine.reactor.pwr.component;

import com.hbm.item.BasicBlockItem;
import java.util.List;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;

/** Actual ItemBlockBase -> ITooltipProvider branch; left shift only, source line splitting and colours retained. */
public final class PwrFuelPillarItem extends BlockItem {
    public PwrFuelPillarItem(Block block){super(block,new Properties());}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){
        super.appendHoverText(stack,context,lines,flag);
        if(BasicBlockItem.leftShiftHeld.getAsBoolean())for(String line:Language.getInstance().getOrDefault("tile.pwr_fuel.desc").split("\\$"))lines.add(Component.literal("\u00a7e"+line));
        else lines.add(com.hbm.item.TooltipHints.moreInfo());
    }
}
