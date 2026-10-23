package com.hbm.item.rtg;

import com.hbm.item.EnumMultiItem;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class DepletedRtgPelletItem extends EnumMultiItem {
    public DepletedRtgPelletItem(){super("pellet_rtg_depleted",64,true,List.of("bismuth","mercury","neptunium","lead","zirconium","nickel"),List.of(0,1,2,3,4,5));}
    public ItemStack stack(int metadata){var stack=new ItemStack(this);stack.set(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),metadata);return stack;}
    @Override public boolean hasCraftingRemainingItem(ItemStack stack){return true;}
    @Override public ItemStack getCraftingRemainingItem(ItemStack stack){return new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse("hbm:plate_iron")));}
}
