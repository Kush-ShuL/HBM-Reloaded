package com.hbm.world.structure.templates.materials;

import com.hbm.block.ore.*;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

/** Original per-enum names; DeadPlant deliberately keeps one common name. */
public final class TemplateMaterialItem extends LegacyOreItem {
    private final boolean noSpawn,blast;
    public TemplateMaterialItem(LegacyOreBlock block,boolean noSpawn,boolean blast){super(block);this.noSpawn=noSpawn;this.blast=blast;}
    @Override public Component getName(ItemStack stack){return Component.translatable("tile."+spec().id()+(spec().multi()&&!spec().id().equals("plant_dead")?"."+spec().variants().get(ordinal(stack)):"")+".name");}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flags){super.appendHoverText(stack,context,tooltip,flags);if(noSpawn)tooltip.add(Component.translatable("tile.nospawn").withStyle(ChatFormatting.RED));if(blast)tooltip.add(Component.translatable("tooltip.hbm.meteor_materials.blast_resistance",spec().explosionResistance()).withStyle(ChatFormatting.GOLD));}
}
