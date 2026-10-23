package com.hbm.item.bobmazondeps;

import com.hbm.hazard.HbmHazards;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;

/** Source snowglobe ItemBlockMeta name is shared; only IBlockMulti plushies format their type in the name. */
public final class TrinketItem extends BlockItem {
 public final boolean snowglobe;
 public TrinketItem(Block block,boolean snowglobe){super(block,new Properties().component(HbmHazards.LEGACY_METADATA.get(),0));this.snowglobe=snowglobe;}
 @Override public Component getName(ItemStack stack){if(snowglobe)return Component.translatable("tile.snowglobe.name");var type=TrinketTypes.Plushie.metadata(BobmazonDependenciesContent.metadata(stack));return type==TrinketTypes.Plushie.NONE?Component.translatable("item.hbm.plushie.none"):Component.translatable("tile.plushie.name",Component.translatableWithFallback(type.key("name"),type.label));}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){if(!snowglobe){var type=TrinketTypes.Plushie.metadata(BobmazonDependenciesContent.metadata(stack));if(type.inscription!=null)lines.add(Component.translatableWithFallback(type.key("inscription"),type.inscription));}}
}
