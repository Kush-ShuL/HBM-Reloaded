package com.hbm.weapon.sedna.gun.drill;

import com.hbm.fluid.HbmFluids;
import com.hbm.weapon.sedna.gun.Receiver;
import com.hbm.weapon.sedna.gun.mags.IMagazine;
import com.hbm.weapon.sedna.gun.mags.MagazineElectricEngine;
import com.hbm.weapon.sedna.gun.mags.MagazineLiquidEngine;

import net.minecraft.world.item.ItemStack;
import com.hbm.weapon.sedna.gun.mods.*;

public class WeaponModEngine extends WeaponModBase {

	public static final MagazineLiquidEngine ENGINE_DIESEL = new MagazineLiquidEngine(0, 4_000, HbmFluids.DIESEL, HbmFluids.DIESEL_CRACK, HbmFluids.LIGHTOIL);
	public static final MagazineLiquidEngine ENGINE_AVIATION = new MagazineLiquidEngine(0, 4_000, HbmFluids.KEROSENE, HbmFluids.LPG);
	public static final MagazineElectricEngine ENGINE_ELECTRIC = new MagazineElectricEngine(0, 1_000_000);
	public static final MagazineLiquidEngine ENGINE_TURBO = new MagazineLiquidEngine(0, 4_000, HbmFluids.KEROSENE_REFORM, HbmFluids.REFORMATE);

	protected IMagazine mag;
	protected int delay;
	
	public WeaponModEngine(int id) {
		super(id, "ENGINE");
		this.setPriority(PRIORITY_SET);
	}
	
	public WeaponModEngine mag(IMagazine mag) { this.mag = mag; return this; }
	public WeaponModEngine delay(int delay) { this.delay = delay; return this; }

	@Override
	public <T> T eval(T base, ItemStack gun, String key, Object parent) {

		if(key == Receiver.O_MAGAZINE && mag != null) return cast(mag, base);
		if(key == Receiver.I_DELAYAFTERFIRE) return cast((Integer) delay, base);
		
		return base;
	}

	@Override public void onInstall(ItemStack gun, ItemStack mod, int index) { XWeaponModManager.changedMagState(); }
	@Override public void onUninstall(ItemStack gun, ItemStack mod, int index) { XWeaponModManager.changedMagState(); }
}
