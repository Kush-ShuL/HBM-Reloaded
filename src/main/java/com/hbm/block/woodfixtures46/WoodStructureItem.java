package com.hbm.block.woodfixtures46;

import java.util.Map;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** Three fixed source forms share the original world block and its palette. */
public final class WoodStructureItem extends BlockItem {
    private static final String[] NAMES = {"roof", "scaffold", "ceiling"};
    private final int type;
    public WoodStructureItem(WoodStructureBlock block,int type){super(block,new Properties());if(type<0||type>=NAMES.length)throw new IllegalArgumentException("Unknown wood structure type "+type);this.type=type;}
    public int type(){return type;}
    public static ItemStack stack(int metadata){return new ItemStack(WoodFixturesContent.structureItem(WoodStructureBlock.type(metadata)));}
    @Override public void registerBlocks(Map<Block,Item> mapping,Item item){if(type==0)mapping.put(getBlock(),item);}
    @Override public String getDescriptionId(){return "item.hbm.wood_structure_"+NAMES[type];}
    @Override protected BlockState getPlacementState(BlockPlaceContext context){
        var state=super.getPlacementState(context);if(state==null)return null;
        state=state.setValue(WoodStructureBlock.META,type);
        // 原生状态组件仅表示明确提供的方块状态，三个固定形态由注册物品决定。
        var properties=context.getItemInHand().get(DataComponents.BLOCK_STATE);return properties==null?state:properties.apply(state);
    }
}
