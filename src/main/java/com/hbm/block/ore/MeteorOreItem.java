package com.hbm.block.ore;
import com.hbm.item.FlatSourceItem;
import java.util.Map;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
/** Five physical mineral items place the original single meteor-ore block and its original state. */
public final class MeteorOreItem extends BlockItem implements FlatSourceItem {
 private final int mineral;
 public MeteorOreItem(LegacyOreBlock block,int mineral){super(block,new Properties().rarity(Rarity.valueOf(block.spec().rarity())));if(mineral<0||mineral>=5)throw new IllegalArgumentException("Unknown meteor mineral "+mineral);this.mineral=mineral;}
 @Override public void registerBlocks(Map<Block,Item> mapping,Item item){if(mineral==0)mapping.put(getBlock(),item);}
 @Override public String getDescriptionId(){return "block.hbm.ore_meteor_"+((LegacyOreBlock)getBlock()).spec().variants().get(mineral);}
 @Override protected BlockState getPlacementState(BlockPlaceContext context){var state=super.getPlacementState(context);if(state==null)return null;state=state.setValue(LegacyOreBlock.LEGACY_META,mineral);var properties=context.getItemInHand().get(DataComponents.BLOCK_STATE);return properties==null?state:properties.apply(state);}
}
