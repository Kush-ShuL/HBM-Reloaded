package com.hbm.machine.groundwater;

import com.hbm.item.BasicBlockItem;
import com.hbm.item.TooltipHints;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

/** Original ITooltipProvider description, with each original dollar-separated source line localized separately. */
public final class GroundwaterPumpItem extends BlockItem {
    private final GroundwaterPumpKind kind;
    public GroundwaterPumpItem(GroundwaterPumpBlock block){super(block,new Item.Properties());kind=block.kind;}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){
        super.appendHoverText(stack,context,lines,flag);
        if(BasicBlockItem.leftShiftHeld.getAsBoolean())for(int i=0;i<3;i++)lines.add(Component.translatable("tooltip.hbm.groundwater."+kind.id+"."+i).withStyle(ChatFormatting.YELLOW));
        else lines.add(TooltipHints.moreInfo());
    }
}
