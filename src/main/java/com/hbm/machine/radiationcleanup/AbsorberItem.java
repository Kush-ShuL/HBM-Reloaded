package com.hbm.machine.radiationcleanup;

import com.hbm.hazard.HbmHazards;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;

/** Real ItemBlockBase/IBlockMulti metadata placement and tier names. */
public final class AbsorberItem extends BlockItem {
    public AbsorberItem(AbsorberBlock block){super(block,new Properties().component(HbmHazards.LEGACY_METADATA.get(),0));}
    @Override public String getDescriptionId(ItemStack stack){return "block.hbm.rad_absorber."+AbsorberBlock.tier(RadiationCleanupContent.metadata(stack)).name().toLowerCase(java.util.Locale.ROOT);}
    @Override protected BlockState getPlacementState(BlockPlaceContext context){var state=super.getPlacementState(context);return state==null?null:state.setValue(AbsorberBlock.META,RadiationCleanupContent.metadata(context.getItemInHand())&15);}
}
