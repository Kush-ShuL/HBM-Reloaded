package com.hbm.armor;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;

/** Source red/grey hoods use their original 32x32 M65 texture, without a yellow overlay. */
public final class HazmatVariantMaskItem extends HazmatMaskItem {
    private final ResourceLocation originalTexture;
    public HazmatVariantMaskItem(Holder<ArmorMaterial> material,String originalTexture){super(material);this.originalTexture=ResourceLocation.fromNamespaceAndPath("hbm",originalTexture);}
    @Override public ResourceLocation getArmorTexture(ItemStack stack,Entity entity,EquipmentSlot slot,ArmorMaterial.Layer layer,boolean inner){return originalTexture;}
}
