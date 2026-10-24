package com.hbm.worldgen.orecave;

import java.util.Map;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** Fixed source material/shape identity; the original block palette is unchanged. */
public final class CaveFormationItem extends BlockItem {
    private final String name;
    private final int variant;
    public CaveFormationItem(CaveFormationBlock block,String name,int variant){super(block,new Properties());if(variant<0||variant>=CaveFormationBlock.VARIANTS.size())throw new IllegalArgumentException("Unknown cave formation variant "+variant);this.name=name;this.variant=variant;}
    @Override public void registerBlocks(Map<Block,Item> mapping,Item item){if(variant==0)mapping.put(getBlock(),item);}
    @Override public String getDescriptionId(){return "item.hbm."+name+"_"+CaveFormationBlock.VARIANTS.get(variant);}
    @Override protected BlockState getPlacementState(BlockPlaceContext context){
        var state=super.getPlacementState(context);if(state==null)return null;
        state=state.setValue(CaveFormationBlock.LEGACY_META,variant);
        // 克隆原始7..15方块状态时，原生方块状态组件保留其真实放置和掉落语义。
        var properties=context.getItemInHand().get(DataComponents.BLOCK_STATE);return properties==null?state:properties.apply(state);
    }
}
