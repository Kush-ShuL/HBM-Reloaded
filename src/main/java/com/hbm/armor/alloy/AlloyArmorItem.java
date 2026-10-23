package com.hbm.armor.alloy;

import com.hbm.armor.t51.SourceFullSetArmorItem;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;

/** The actual four source ArmorFSB items; inherited full-set identity and legacy durability remain canonical. */
public final class AlloyArmorItem extends SourceFullSetArmorItem {
    public AlloyArmorItem(Holder<ArmorMaterial> material, Type slot) {
        super(material, slot, 40);
    }

    @Override
    public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot,
                                            ArmorMaterial.Layer layer, boolean inner) {
        return layer.texture(inner);
    }
}
