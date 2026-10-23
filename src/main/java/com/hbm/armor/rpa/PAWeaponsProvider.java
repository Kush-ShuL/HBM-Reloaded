package com.hbm.armor.rpa;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
/** Original providers are read from the actual worn chest, never inferred from a set name. */
public interface PAWeaponsProvider {
 PowerArmorMelee getMeleeComponent(Player player);PowerArmorRanged getRangedComponent(Player player);
 static PowerArmorMelee melee(Player player){return player!=null&&player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof PAWeaponsProvider provider?provider.getMeleeComponent(player):null;}
 static PowerArmorRanged ranged(Player player){return player!=null&&player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof PAWeaponsProvider provider?provider.getRangedComponent(player):null;}
}
