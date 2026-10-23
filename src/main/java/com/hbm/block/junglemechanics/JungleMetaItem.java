package com.hbm.block.junglemechanics;
import com.hbm.hazard.HbmHazards;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
/** Source ItemTrapBlock/ItemGlyphBlock: rawmetadata placement and original per-variant tooltip, now localized. */
public final class JungleMetaItem extends BlockItem {
 public JungleMetaItem(Block block){super(block,new Properties().component(HbmHazards.LEGACY_METADATA.get(),0));}
 public static int metadata(ItemStack stack){return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0)&15;}
 @Override protected BlockState getPlacementState(BlockPlaceContext context){var state=super.getPlacementState(context);return state==null?null:state.setValue(JungleMechanicsContent.META,metadata(context.getItemInHand()));}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flag){int meta=metadata(stack);tooltip.add(Component.translatable(getBlock() instanceof JungleTrapBlock?"tooltip.hbm.jungle_trap."+JungleTrapBlock.Trap.get(meta).name().toLowerCase(java.util.Locale.ROOT):"tooltip.hbm.jungle_glyph."+meta));}
}
