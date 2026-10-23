package com.hbm.weapon.sedna.gun.drill;
import com.hbm.weapon.sedna.gun.mods.WeaponModBase;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.*;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
/** Source adds/removes actual Fortune enchantment when installed; the real server mod-table owns these callbacks. */
public final class WeaponModDrillFortune extends WeaponModBase {
 private final int amount;
 public WeaponModDrillFortune(int id,String slot,int fortune){super(id,slot);setPriority(PRIORITY_ADDITIVE);amount=fortune;}
 @Override public <T>T eval(T base,ItemStack gun,String key,Object parent){return base;}
 @Override public void onInstall(ItemStack gun,ItemStack mod,int index){change(gun,amount);}
 @Override public void onUninstall(ItemStack gun,ItemStack mod,int index){change(gun,-amount);}
 private static void change(ItemStack stack,int delta){var server=ServerLifecycleHooks.getCurrentServer();if(server==null)return;var fortune=server.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE);int amount=Math.max(0,EnchantmentHelper.getItemEnchantmentLevel(fortune,stack)+delta);EnchantmentHelper.updateEnchantments(stack,mutable->mutable.set(fortune,amount));}
}
