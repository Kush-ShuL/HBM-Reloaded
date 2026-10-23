package com.hbm.weapon.sedna.gun.energy.mods;
import com.hbm.weapon.sedna.gun.mods.WeaponModBase;

import com.hbm.weapon.sedna.gun.GunConfig;
import com.hbm.weapon.sedna.gun.Receiver;

import net.minecraft.world.item.ItemStack;

public class WeaponModLasAuto extends WeaponModBase {

	public WeaponModLasAuto(int id) {
		super(id, "RECEIVER");
		this.setPriority(PRIORITY_SET);
	}

	@Override
	public <T> T eval(T base, ItemStack gun, String key, Object parent) {
		if(key == Receiver.F_BASEDAMAGE) return cast((Float) base * 0.66F, base);
		if(key == Receiver.B_REFIREONHOLD) return cast(true, base);
		if(key == Receiver.I_DELAYAFTERFIRE) return cast(5, base);
		if(key == GunConfig.O_SCOPETEXTURE) return cast(null, base);
		return base;
	}
}
