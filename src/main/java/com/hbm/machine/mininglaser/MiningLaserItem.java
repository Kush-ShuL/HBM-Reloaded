package com.hbm.machine.mininglaser;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
/** Original two unconditional placement tooltip lines. */
public final class MiningLaserItem extends BlockItem {
    public MiningLaserItem(Block block){super(block,new Properties());}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){super.appendHoverText(stack,context,lines,flag);lines.add(Component.translatable("tooltip.hbm.mining_laser.size"));lines.add(Component.translatable("tooltip.hbm.mining_laser.ceiling"));}
}
