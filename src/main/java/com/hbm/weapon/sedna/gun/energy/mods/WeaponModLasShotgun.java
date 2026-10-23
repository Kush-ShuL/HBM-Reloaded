package com.hbm.weapon.sedna.gun.energy.mods;
import com.hbm.weapon.sedna.gun.mods.WeaponModBase;

import com.hbm.weapon.sedna.gun.Crosshair;
import com.hbm.weapon.sedna.gun.GunConfig;
import com.hbm.weapon.sedna.gun.Receiver;

import net.minecraft.world.item.ItemStack;

public class WeaponModLasShotgun extends WeaponModBase {

	public WeaponModLasShotgun(int id) {
		super(id, "BARREL");
		this.setPriority(PRIORITY_MULTIPLICATIVE);
	}

	@Override
	public <T> T eval(T base, ItemStack gun, String key, Object parent) {
		if(key == Receiver.F_BASEDAMAGE) return cast((Float) base * 0.35F, base);
		if(key == Receiver.F_SPLITPROJECTILES) return cast((Float) base * 3F, base);
		if(key == Receiver.F_SPREADINNATE) return cast((Float) base + 3F, base);
		if(key == Receiver.F_SPREADHIPFIRE) return cast(0F, base);
		if(key == GunConfig.O_CROSSHAIR) return cast(Crosshair.L_CIRCLE, base);
		return base;
	}
}
