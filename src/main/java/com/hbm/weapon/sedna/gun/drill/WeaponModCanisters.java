package com.hbm.weapon.sedna.gun.drill;

import com.hbm.weapon.sedna.gun.Receiver;
import com.hbm.weapon.sedna.gun.mags.MagazineElectricEngine;
import com.hbm.weapon.sedna.gun.mags.MagazineLiquidEngine;

import net.minecraft.world.item.ItemStack;
import com.hbm.weapon.sedna.gun.mods.*;

public class WeaponModCanisters extends WeaponModBase {


	public WeaponModCanisters(int id) {
		super(id, "CANISTERS");
		this.setPriority(PRIORITY_MULT_FINAL);
	}

	@Override
	public <T> T eval(T base, ItemStack gun, String key, Object parent) {
		if(key == Receiver.O_MAGAZINE) {
			if(base instanceof MagazineLiquidEngine) {
				MagazineLiquidEngine original = (MagazineLiquidEngine) base;
				return (T) new MagazineLiquidEngine(original.index, original.capacity * 3, original.acceptedTypes);
			}
			
			if(base instanceof MagazineElectricEngine) {
				MagazineElectricEngine original = (MagazineElectricEngine) base;
				return (T) new MagazineElectricEngine(original.index, original.capacity * 3);
			}
		}
		return base;
	}

	@Override public void onInstall(ItemStack gun, ItemStack mod, int index) { XWeaponModManager.changedMagState(); }
	@Override public void onUninstall(ItemStack gun, ItemStack mod, int index) { XWeaponModManager.changedMagState(); }
}
