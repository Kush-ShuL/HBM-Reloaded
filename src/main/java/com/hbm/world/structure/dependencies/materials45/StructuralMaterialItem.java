package com.hbm.world.structure.dependencies.materials45;
import com.hbm.hazard.HbmHazards;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
/** Source ItemBlockBlastInfo for platemetal/concrete only; original glass has ordinary BlockItem. */
public final class StructuralMaterialItem extends BlockItem {
 public StructuralMaterialItem(Block block){super(block,new Properties());}
 @Override public Component getName(ItemStack stack){return getBlock()instanceof PlatemetalBlock?Component.translatable("tile.platemetal."+PlatemetalBlock.NAMES.get(PlatemetalBlock.rectify(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0)))+".name"):super.getName(stack);}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flags){super.appendHoverText(stack,context,tooltip,flags);tooltip.add(Component.translatable("tooltip.hbm.structural_material.blast_resistance",getBlock().getExplosionResistance()).withStyle(ChatFormatting.GOLD));}
}
