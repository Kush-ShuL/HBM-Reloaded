package com.hbm.world.structure.materials;

import com.hbm.block.ore.LegacyOreItem;
import com.hbm.hazard.HbmHazards;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/** Original ItemBlockColoredConcrete + inherited ItemBlockBlastInfo. */
public final class ColoredConcreteItem extends LegacyOreItem {
    public ColoredConcreteItem(ColoredConcreteBlock block){super(block);}
    @Override public Component getName(ItemStack stack){return Component.translatable("tile.concrete_colored."+ColoredConcreteBlock.COLORS.get(Math.floorMod(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0),16))+".name");}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flags){
        super.appendHoverText(stack,context,lines,flags);
        lines.add(Component.translatable("tile.nospawn").withStyle(ChatFormatting.RED));
        lines.add(Component.translatable("tooltip.hbm.meteor_materials.blast_resistance",84F).withStyle(ChatFormatting.GOLD));
    }
}
