package com.hbm.item.bobmazondeps;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;

/** Source snowglobe ItemBlockMeta name is shared; only IBlockMulti plushies format their type in the name. */
public final class TrinketItem extends BlockItem {
 public final boolean snowglobe;
 public final int sourceType;
 public TrinketItem(Block block,boolean snowglobe,int sourceType){super(block,new Properties());this.snowglobe=snowglobe;if(sourceType<0||sourceType>=(snowglobe?TrinketTypes.Snowglobe.values().length:TrinketTypes.Plushie.values().length))throw new IllegalArgumentException("Not an actual source collectible type: "+sourceType);this.sourceType=sourceType;}
 @Override public String getDescriptionId(){return "block.hbm."+(snowglobe?"snowglobe_"+TrinketTypes.Snowglobe.values()[sourceType].name().toLowerCase(java.util.Locale.ROOT):"plushie_"+TrinketTypes.Plushie.values()[sourceType].name().toLowerCase(java.util.Locale.ROOT));}
 @Override public void registerBlocks(java.util.Map<Block,Item> mapping,Item item){if(sourceType==0)super.registerBlocks(mapping,item);}
 @Override public Component getName(ItemStack stack){if(snowglobe)return Component.translatable("tile.snowglobe.name");var type=TrinketTypes.Plushie.metadata(BobmazonDependenciesContent.metadata(stack));return type==TrinketTypes.Plushie.NONE?Component.translatable("item.hbm.plushie.none"):Component.translatable("tile.plushie.name",Component.translatableWithFallback(type.key("name"),type.label));}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){if(!snowglobe){var type=TrinketTypes.Plushie.metadata(BobmazonDependenciesContent.metadata(stack));if(type.inscription!=null)lines.add(Component.translatableWithFallback(type.key("inscription"),type.inscription));}}
}
