package com.hbm.bomb.nuclear;

import com.hbm.item.EnumMultiItem;
import com.hbm.registry.HbmContent;
import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.world.item.ItemStack;

/** Original ItemBreedingRod: all seventeen enum variants and its actual empty-rod crafting container. */
public final class NuclearBreedingRod extends EnumMultiItem {
 public static final List<String> VARIANTS=List.of("lithium","tritium","co","co60","th232","thf","u235","np237","u238","pu238","pu239","rgp","waste","lead","uranium","ra226","ac227");
 private final String id;
 public NuclearBreedingRod(String id){super(id,64,true,VARIANTS,IntStream.range(0,VARIANTS.size()).boxed().toList());this.id=id;}
 @Override public boolean hasCraftingRemainingItem(ItemStack stack){return true;}
 @Override public ItemStack getCraftingRemainingItem(ItemStack stack){return new ItemStack(HbmContent.item(id+"_empty"));}
}
