package com.hbm.armor.paahazmat;

import com.hbm.armor.t51.SourceFullSetArmorItem;
import com.hbm.entity.mob.fbi.PaaContent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;

/** Source ArmorHazmat shares the exact original PAA material, including mixed FSB identity. */
public class PaaHazmatArmorItem extends SourceFullSetArmorItem {
    private final String name;
    public PaaHazmatArmorItem(Type slot, String name) { super(PaaContent.MATERIAL, slot, 75); this.name = name; }
    @Override public Component getName(ItemStack stack) { return Component.translatable("item." + name + ".name"); }
    @Override public ResourceLocation getArmorTexture(ItemStack stack, Entity wearer, EquipmentSlot slot,
        ArmorMaterial.Layer layer, boolean inner) {
        return ResourceLocation.fromNamespaceAndPath("hbm", "textures/armor/hazmat_paa_" + (slot == EquipmentSlot.LEGS ? 2 : 1) + ".png");
    }
}
