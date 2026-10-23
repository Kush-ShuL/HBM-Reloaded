package com.hbm.armor.dnt;

import com.hbm.armor.t51.SourceFullSetArmorItem;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;

/** The source unpowered HBM_DNT_LOLOLOL quartet, distinct from ArmorDNT/DNS. */
public final class DntArmorItem extends SourceFullSetArmorItem {
    private final String name;
    public DntArmorItem(Type slot, String name) { super(DntArmorContent.MATERIAL, slot, 3); this.name = name; }
    @Override public Component getName(ItemStack stack) { return Component.translatable("item." + name + ".name"); }
    @Override public ResourceLocation getArmorTexture(ItemStack stack, Entity wearer, EquipmentSlot slot,
        ArmorMaterial.Layer layer, boolean inner) {
        return ResourceLocation.fromNamespaceAndPath("hbm", "textures/armor/dnt_" + (slot == EquipmentSlot.LEGS ? 2 : 1) + ".png");
    }
}
