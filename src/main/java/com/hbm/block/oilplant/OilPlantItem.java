package com.hbm.block.oilplant;
import com.hbm.hazard.HbmHazards;import net.minecraft.network.chat.Component;import net.minecraft.world.item.*;import net.minecraft.world.level.block.Block;
/** Source BlockEnumMulti item names and metadata, with the actual tall block upper-half selector. */
public final class OilPlantItem extends BlockItem {
 private final boolean tall;public OilPlantItem(Block block,boolean tall){super(block,new Item.Properties().component(HbmHazards.LEGACY_METADATA.get(),0));this.tall=tall;}
 public int metadata(ItemStack stack){return Math.abs(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0)%(tall?4:6));}
 @Override public Component getName(ItemStack stack){var names=tall?OilTallPlantBlock.NAMES:OilFlowerBlock.NAMES;return Component.translatable("tile."+(tall?"plant_tall":"plant_flower")+"."+names.get(metadata(stack))+".name");}
}
