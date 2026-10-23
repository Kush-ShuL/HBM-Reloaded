package com.hbm.block.sellafield;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
/** Original ItemBlockNamedMeta: raw metadata naming and placement, no invented subtype tooltip. */
public final class SellafieldItem extends BlockItem {
    public SellafieldItem(SellafieldBlock block){super(block,new Properties());}
    @Override public String getDescriptionId(ItemStack stack){return "block.hbm.sellafield."+SellafieldContent.metadata(stack);}
    @Override protected BlockState getPlacementState(BlockPlaceContext context){BlockState state=super.getPlacementState(context);return state==null?null:state.setValue(SellafieldBlock.META,SellafieldContent.metadata(context.getItemInHand())&15);}
}
