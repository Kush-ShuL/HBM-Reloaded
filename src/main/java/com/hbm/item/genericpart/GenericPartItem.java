package com.hbm.item.genericpart;
import com.hbm.item.FlatSourceItem;
import net.minecraft.world.item.Item;
/** One actual ItemGenericPart identity; source ordinal remains internal factory/provenance data, never an ItemStack component. */
public final class GenericPartItem extends Item implements FlatSourceItem {
 public final String sourceVariant;public final int sourceOrdinal;
 public GenericPartItem(String variant,int ordinal){super(new Properties().stacksTo(64));sourceVariant=variant;sourceOrdinal=ordinal;}
}
