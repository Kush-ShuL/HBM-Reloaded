package com.hbm.world.structure.cabinet;
import com.hbm.hazard.HbmHazards;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
/** ItemBlockBase's true enum name lookup; all original metadata survive in the item component. */
public final class FilingCabinetItem extends BlockItem {
 public FilingCabinetItem(Block block){super(block,new Properties().component(HbmHazards.LEGACY_METADATA.get(),0));}
 public static int metadata(ItemStack stack){return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0);}
 public static ItemStack stack(int meta){var stack=new ItemStack(FilingCabinetContent.ITEM.get());stack.set(HbmHazards.LEGACY_METADATA.get(),meta);return stack;}
 @Override public Component getName(ItemStack stack){return Component.translatable("tile.filing_cabinet."+(Math.abs(metadata(stack)%2)==1?"steel":"green")+".name");}
}
