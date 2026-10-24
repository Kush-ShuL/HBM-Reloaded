package com.hbm.item.rtg;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** One fixed depleted RTG material; active fuel lifetime lives on RtgPelletItem. */
public final class DepletedRtgPelletItem extends Item {
    private final String material;
    public DepletedRtgPelletItem(String material){super(new Properties().stacksTo(64));this.material=java.util.Objects.requireNonNull(material);}
    @Override public Component getName(ItemStack stack){return Component.translatable("item.pellet_rtg_depleted."+material+".name");}
    @Override public boolean hasCraftingRemainingItem(ItemStack stack){return true;}
    @Override public ItemStack getCraftingRemainingItem(ItemStack stack){return new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse("hbm:plate_iron")));}
}
