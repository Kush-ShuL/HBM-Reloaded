package com.hbm.block.light.reinforced;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/** Original ItemBlockBlastInfo for both source item identities. */
public final class ReinforcedLampItem extends BlockItem {
    public ReinforcedLampItem(ReinforcedLampBlock block){super(block,new Properties());}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){
        super.appendHoverText(stack,context,lines,flag);
        lines.add(Component.translatable("tooltip.hbm.meteor_materials.blast_resistance",getBlock().getExplosionResistance()).withStyle(ChatFormatting.GOLD));
    }
}
