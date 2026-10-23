package com.hbm.weapon.sedna.gun.energy;
import static com.hbm.weapon.sedna.gun.energy.EnergyAnimations.*;
import static com.hbm.weapon.sedna.gun.energy.EnergyBullets.*;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT.*;
import com.hbm.weapon.sedna.gun.mags.*;
import net.minecraft.resources.ResourceLocation;
/** X5846 XFactoryEnergy receiver values, magazine capacities, sounds and real default ammo. */
public final class EnergyFactory {
public static ItemGunBaseNT gun_tesla_cannon(){return new ItemGunBaseNT(WeaponQuality.A_SIDE, new GunConfig()
				.dura(1_000).draw(10).inspect(33).crosshair(Crosshair.CIRCLE)
				.rec(new Receiver(0)
						.dmg(EnergySettings::GUN_TESLA_CANNON_DAMAGE).delay(20).spreadHipfire(1.5F).reload(44).jam(19).sound("hbm:weapon.fire.tesla", 1.0F, 1.0F)
						.mag(new MagazineBelt().addConfigs(energy_tesla, energy_tesla_overcharge, energy_tesla_ir))
						.offset(0.75, 0, -0.375).offsetScoped(0.75, 0, -0.25)
						.setupStandardFire().recoil(LAMBDA_RECOIL_ENERGY))
				.setupStandardConfiguration()
				.anim(LAMBDA_TESLA_ANIMS).orchestra(EnergyOrchestras.ORCHESTRA_TESLA)
				).setDefaultAmmo(EnergyBullets.ammo("CAPACITOR"), 15);}
public static ItemGunBaseNT gun_laser_pistol(){return new ItemGunBaseNT(WeaponQuality.A_SIDE, new GunConfig()
				.dura(500).draw(10).inspect(26).crosshair(Crosshair.CIRCLE)
				.rec(new Receiver(0)
						.dmg(EnergySettings::GUN_LASER_PISTOL_DAMAGE).delay(5).spread(1F).spreadHipfire(1F).reload(45).jam(37).sound("hbm:weapon.fire.laserPistol", 1.0F, 1.0F)
						.mag(new MagazineFullReload(0, 30).addConfigs(energy_las, energy_las_overcharge, energy_las_ir))
						.offset(0.75, -0.0625 * 1.5, -0.1875)
						.setupStandardFire().recoil(LAMBDA_RECOIL_ENERGY))
				.setupStandardConfiguration()
				.anim(LAMBDA_LASER_PISTOL).orchestra(EnergyOrchestras.ORCHESTRA_LASER_PISTOL)
				).setDefaultAmmo(EnergyBullets.ammo("CAPACITOR"), 15) .pistolDualWield(true);}
public static ItemGunBaseNT gun_laser_pistol_pew_pew(){return new ItemGunBaseNT(WeaponQuality.B_SIDE, new GunConfig()
				.dura(500).draw(10).inspect(26).crosshair(Crosshair.CIRCLE)
				.rec(new Receiver(0)
						.dmg(EnergySettings::GUN_PEW_PEW_DAMAGE).rounds(5).delay(10).spread(0.25F).spreadHipfire(1F).reload(45).jam(37).sound("hbm:weapon.fire.laserPistol", 1.0F, 0.8F)
						.mag(new MagazineFullReload(0, 10).addConfigs(energy_las, energy_las_overcharge, energy_las_ir))
						.offset(0.75, -0.0625 * 1.5, -0.1875)
						.setupStandardFire().recoil(LAMBDA_RECOIL_ENERGY))
				.setupStandardConfiguration()
				.anim(LAMBDA_LASER_PISTOL).orchestra(EnergyOrchestras.ORCHESTRA_LASER_PISTOL)
				).setDefaultAmmo(EnergyBullets.ammo("CAPACITOR_OVERCHARGE"), 10) .pistolDualWield(true);}
public static ItemGunBaseNT gun_laser_pistol_morning_glory(){return new ItemGunBaseNT(WeaponQuality.LEGENDARY, new GunConfig()
				.dura(1_500).draw(10).inspect(26).crosshair(Crosshair.CIRCLE)
				.rec(new Receiver(0)
						.dmg(EnergySettings::GUN_MORNING_GLORY_DAMAGE).delay(7).spread(0F).spreadHipfire(0.5F).reload(45).jam(37).sound("hbm:weapon.fire.laserPistol", 1.0F, 1.1F)
						.mag(new MagazineFullReload(0, 20).addConfigs(energy_emerald, energy_emerald_overcharge, energy_emerald_ir))
						.offset(0.75, -0.0625 * 1.5, -0.1875)
						.setupStandardFire().recoil(LAMBDA_RECOIL_ENERGY))
				.setupStandardConfiguration()
				.anim(LAMBDA_LASER_PISTOL).orchestra(EnergyOrchestras.ORCHESTRA_LASER_PISTOL)
				).setDefaultAmmo(EnergyBullets.ammo("CAPACITOR_OVERCHARGE"), 20) .pistolDualWield(true);}
public static ItemGunBaseNT gun_lasrifle(){return new ItemGunBaseNT(WeaponQuality.A_SIDE, new GunConfig()
				.dura(2_000).draw(10).inspect(26).crosshair(Crosshair.CIRCLE).scopeTexture(ResourceLocation.parse("hbm:textures/misc/scope_amat.png"))
				.rec(new Receiver(0)
						.dmg(EnergySettings::GUN_LASER_RIFLE_DAMAGE).delay(8).spreadHipfire(1F).reload(44).jam(36).sound("hbm:weapon.fire.laser", 1.0F, 1.0F)
						.mag(new MagazineFullReload(0, 24).addConfigs(energy_las, energy_las_overcharge, energy_las_ir))
						.offset(0.75, -0.0625 * 1.5, -0.1875)
						.setupStandardFire().recoil(LAMBDA_RECOIL_ENERGY))
				.setupStandardConfiguration()
				.anim(LAMBDA_LASRIFLE).orchestra(EnergyOrchestras.ORCHESTRA_LASRIFLE)
				).setDefaultAmmo(EnergyBullets.ammo("CAPACITOR"), 24);}
}
