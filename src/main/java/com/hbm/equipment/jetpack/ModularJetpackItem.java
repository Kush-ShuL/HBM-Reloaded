package com.hbm.equipment.jetpack;

import com.hbm.armor.ArmorModItem;
import com.hbm.armor.ArmorModStorage;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/** The upstream modular pack currently has no flight callback. */
public final class ModularJetpackItem extends ArmorModItem {
    public ModularJetpackItem() { super(ArmorModStorage.CHEST, false, true, false, false); }
    @Override public EquipmentSlot getEquipmentSlot(ItemStack stack) { return EquipmentSlot.CHEST; }
    @Override public boolean canEquip(ItemStack stack, EquipmentSlot slot, LivingEntity entity) { return slot == EquipmentSlot.CHEST; }
}
