package com.hbm.weapon.sedna.gun.pistols;
import java.util.*;
import java.util.function.*;
import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT.*;
import com.hbm.weapon.sedna.gun.factory.*;
import com.hbm.weapon.sedna.gun.mags.*;
import com.hbm.weapon.sedna.gun.particle.SpentCasing;
import com.hbm.weapon.sedna.gun.particle.SpentCasing.CasingType;
import com.hbm.client.animation.bus.*;
import com.hbm.client.animation.bus.BusAnimationKeyframe.IType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import com.hbm.damage.LegacyEntityDamage;
import com.hbm.damage.DamageResistanceHandler.DamageClass;
import com.hbm.weapon.sedna.gun.mods.XWeaponModManager;
import com.hbm.weapon.sedna.gun.legendary.CasingEffects;
import com.hbm.weapon.sedna.gun.legendary.LegendaryGunFactory;
import com.hbm.weapon.sedna.gun.legendary.LegendaryGunAnimations;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.*;
public class XFactory45 {

	public static BulletConfig p45_sp;
	public static BulletConfig p45_fmj;
	public static BulletConfig p45_jhp;
	public static BulletConfig p45_ap;
	public static BulletConfig p45_du;
	
	public static void init() {
		SpentCasing casing9 = new SpentCasing(CasingType.STRAIGHT).setColor(SpentCasing.COLOR_CASE_BRASS).setScale(1F, 1F, 0.75F);
		p45_sp = new BulletConfig("hbm:pistols/p45_sp").setItem(PistolAmmo.P45_SP)
				.setCasing(casing9.clone().register("p45"));
        PistolAmmo.recovery(p45_sp,0,8);
		p45_fmj = new BulletConfig("hbm:pistols/p45_fmj").setItem(PistolAmmo.P45_FMJ).setDamage(0.8F).setThresholdNegation(2F).setArmorPiercing(0.1F)
				.setCasing(casing9.clone().register("p45fmj"));
        PistolAmmo.recovery(p45_fmj,0,8);
		p45_jhp = new BulletConfig("hbm:pistols/p45_jhp").setItem(PistolAmmo.P45_JHP).setDamage(1.5F).setHeadshot(1.5F).setArmorPiercing(-0.25F)
				.setCasing(casing9.clone().register("p45jhp"));
        PistolAmmo.recovery(p45_jhp,0,8);
		p45_ap = new BulletConfig("hbm:pistols/p45_ap").setItem(PistolAmmo.P45_AP).setDoesPenetrate(true).setDamageFalloffByPen(false).setDamage(1.25F).setThresholdNegation(5F).setArmorPiercing(0.15F)
				.setCasing(casing9.clone().setColor(SpentCasing.COLOR_CASE_44).register("p45ap"));
        PistolAmmo.recovery(p45_ap,2,8);
		p45_du = new BulletConfig("hbm:pistols/p45_du").setItem(PistolAmmo.P45_DU).setDoesPenetrate(true).setDamageFalloffByPen(false).setDamage(2.5F).setThresholdNegation(15F).setArmorPiercing(0.25F)
				.setCasing(casing9.clone().setColor(SpentCasing.COLOR_CASE_44).register("p45du"));
        PistolAmmo.recovery(p45_du,2,8);
	}
}
