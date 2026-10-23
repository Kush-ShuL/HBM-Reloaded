package com.hbm.block.glowingasphalt;

import com.hbm.block.masonry.AsphaltBlock;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/** Source ItemBlockBlastInfo first calls the actual BlockSpeedy tooltip, then appends resistance. */
public final class GlowingAsphaltItem extends BlockItem {
    public GlowingAsphaltItem(AsphaltBlock block,Properties properties){super(block,properties);}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){
        super.appendHoverText(stack,context,lines,flag);
        lines.add(Component.translatable("tooltip.hbm.asphalt.speed").withStyle(ChatFormatting.BLUE));
        lines.add(Component.translatable("tooltip.hbm.meteor_materials.blast_resistance",getBlock().getExplosionResistance()).withStyle(ChatFormatting.GOLD));
    }
}
