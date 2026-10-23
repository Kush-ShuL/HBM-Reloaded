package com.hbm.item.dye;
import com.hbm.hazard.HbmHazards;import net.minecraft.network.chat.Component;import net.minecraft.world.item.*;
/** Genuine source ItemChemicalDye: immutable base sprite plus a source-RGB tinted second pass. */
public class ChemicalDyeItem extends Item {
 private final String id;
 public ChemicalDyeItem(){this("chemical_dye",new Properties());}
 protected ChemicalDyeItem(String id,Properties properties){super(properties.component(HbmHazards.LEGACY_METADATA.get(),0));this.id=id;}
 public static int metadata(ItemStack stack){return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue());}
 public static ChemicalDyeType type(ItemStack stack){return ChemicalDyeType.from(metadata(stack));}
 public ItemStack stack(ChemicalDyeType type){var stack=new ItemStack(this);stack.set(HbmHazards.LEGACY_METADATA.get(),type.ordinal());return stack;}
 @Override public String getDescriptionId(ItemStack stack){return "item.hbm."+id+"."+type(stack).serializedName();}
 @Override public Component getName(ItemStack stack){return Component.translatable("item."+id+"."+type(stack).serializedName()+".name");}
 public static int color(ItemStack stack,int pass){return 0xff000000|(pass==1?type(stack).color:0xffffff);}
}
