package com.hbm.armor.asbestos;

import com.hbm.armor.t51.SourceFullSetArmorItem;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;

/** ModItemsArmor86..89 actually constructs ArmorFSB, not the dormant old ISpecialArmor subclass. */
public final class AsbestosArmorItem extends SourceFullSetArmorItem {
    public AsbestosArmorItem(Holder<ArmorMaterial> material, Type slot) { super(material, slot, 20); }

    @Override
    public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot,
                                            ArmorMaterial.Layer layer, boolean inner) {
        return layer.texture(inner);
    }
}
