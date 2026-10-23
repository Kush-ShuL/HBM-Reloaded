package com.hbm.worldgen.orecave;

import com.hbm.hazard.HbmHazards;
import java.util.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;

/** Actual ItemBlockMulti metadata placement and safe enum naming; not seven substitute items. */
public final class CaveFormationItem extends BlockItem {
    private final String name;
    public CaveFormationItem(CaveFormationBlock block,String name){super(block,new Properties().component(HbmHazards.LEGACY_METADATA.get(),0));this.name=name;}
    public int ordinal(ItemStack stack){return Math.abs(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0)%CaveFormationBlock.VARIANTS.size());}
    @Override protected BlockState getPlacementState(BlockPlaceContext context){var state=super.getPlacementState(context);return state==null?null:state.setValue(CaveFormationBlock.LEGACY_META,context.getItemInHand().getOrDefault(HbmHazards.LEGACY_METADATA.get(),0)&15);}
    @Override public String getDescriptionId(ItemStack stack){return "block.hbm."+name+"."+CaveFormationBlock.VARIANTS.get(ordinal(stack));}
    public List<ItemStack> creativeStacks(){var result=new ArrayList<ItemStack>();for(int metadata=0;metadata<CaveFormationBlock.VARIANTS.size();metadata++){var stack=new ItemStack(this);stack.set(HbmHazards.LEGACY_METADATA.get(),metadata);result.add(stack);}return List.copyOf(result);}
}
