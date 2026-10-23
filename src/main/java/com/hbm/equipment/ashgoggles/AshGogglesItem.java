package com.hbm.equipment.ashgoggles;

import com.hbm.equipment.vaultclothing.VaultArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Real ArmorAshGlasses: iron helmet and its own ModelGlasses, not a gas mask. */
public final class AshGogglesItem extends VaultArmorItem {
    public AshGogglesItem() { super(ArmorMaterials.IRON, Type.HELMET, 15, "ashglasses", "textures/armor/goggles.png"); }
    @Override public boolean isValidRepairItem(ItemStack armor, ItemStack ingredient) { return ingredient.is(Items.IRON_INGOT); }
}
