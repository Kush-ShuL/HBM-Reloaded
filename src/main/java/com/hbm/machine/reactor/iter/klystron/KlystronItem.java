package com.hbm.machine.reactor.iter.klystron;
import com.hbm.item.BasicBlockItem;
import java.util.List;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
/** Source ITooltipProvider left-shift split description, fulloriginal customitemrenderer registered by client. */
public final class KlystronItem extends BlockItem {
    public KlystronItem(){super(KlystronContent.BLOCK.get(),new Properties());}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){super.appendHoverText(stack,context,lines,flag);if(BasicBlockItem.leftShiftHeld.getAsBoolean())for(String line:Language.getInstance().getOrDefault("tile.fusion_klystron.desc").split("\\$"))lines.add(Component.literal("\u00a7e"+line));else lines.add(com.hbm.item.TooltipHints.moreInfo());}
}
