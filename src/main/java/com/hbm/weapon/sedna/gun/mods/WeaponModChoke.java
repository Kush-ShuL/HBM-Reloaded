package com.hbm.weapon.sedna.gun.mods;

import com.hbm.weapon.sedna.gun.Receiver;

import net.minecraft.world.item.ItemStack;

public class WeaponModChoke extends WeaponModBase {

	public WeaponModChoke(int id) {
		super(id, "BARREL");
	}

	@Override
	public <T> T eval(T base, ItemStack gun, String key, Object parent) {
		if(key == Receiver.F_SPREADAMMO) { return cast((Float) base * 0.5F, base); }
		
		return base;
	}
}
