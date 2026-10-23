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
public class XFactory357 {

	// yup this works
	public static Supplier<Float> GUN_LIGHT_REVOLVER_DAMAGE = PistolSettings.value("GUN_LIGHT_REVOLVER_DAMAGE", 7.5F);
	public static Supplier<Float> GUN_ATLAS_DAMAGE = PistolSettings.value("GUN_ATLAS_DAMAGE", 12.5F);
	public static Supplier<Float> GUN_DANI_CELESTIAL_DAMAGE = PistolSettings.value("GUN_DANI_CELESTIAL_DAMAGE", 15F);
	public static Supplier<Float> GUN_DANI_LUNAR_DAMAGE = PistolSettings.value("GUN_DANI_LUNAR_DAMAGE", 15F);

	public static BulletConfig m357_bp;
	public static BulletConfig m357_sp;
	public static BulletConfig m357_fmj;
	public static BulletConfig m357_jhp;
	public static BulletConfig m357_ap;
	public static BulletConfig m357_express;
	
	public static void initConfig() {}

	public static void init() {
		m357_bp = new BulletConfig("hbm:pistols/m357_bp").setItem(PistolAmmo.M357_BP).setDamage(0.75F).setBlackPowder(true);
        PistolAmmo.recovery(m357_bp,0,16);
		m357_sp = new BulletConfig("hbm:pistols/m357_sp").setItem(PistolAmmo.M357_SP);
        PistolAmmo.recovery(m357_sp,0,8);
		m357_fmj = new BulletConfig("hbm:pistols/m357_fmj").setItem(PistolAmmo.M357_FMJ).setDamage(0.8F).setThresholdNegation(2F).setArmorPiercing(0.1F);
        PistolAmmo.recovery(m357_fmj,0,8);
		m357_jhp = new BulletConfig("hbm:pistols/m357_jhp").setItem(PistolAmmo.M357_JHP).setDamage(1.5F).setHeadshot(1.5F).setArmorPiercing(-0.25F);
        PistolAmmo.recovery(m357_jhp,0,8);
		m357_ap = new BulletConfig("hbm:pistols/m357_ap").setItem(PistolAmmo.M357_AP).setDoesPenetrate(true).setDamageFalloffByPen(false).setDamage(1.25F).setThresholdNegation(5F).setArmorPiercing(0.15F);
        PistolAmmo.recovery(m357_ap,2,8);
		m357_express = new BulletConfig("hbm:pistols/m357_express").setItem(PistolAmmo.M357_EXPRESS).setDoesPenetrate(true).setDamage(1.5F).setThresholdNegation(2F).setArmorPiercing(0.1F).setWear(1.5F);
        PistolAmmo.recovery(m357_express,0,8);

		PistolGuns.gun_light_revolver = new ItemGunBaseNT(WeaponQuality.A_SIDE, new GunConfig()
				.dura(300).draw(4).inspect(23).crosshair(Crosshair.CIRCLE).smoke(Lego.LAMBDA_STANDARD_SMOKE)
				.rec(new Receiver(0)
						.dmg(GUN_LIGHT_REVOLVER_DAMAGE).delay(11).reload(55).jam(45).sound(PistolSounds.GUN_PISTOL_FIRE, 1.0F, 1.0F)
						.mag(new MagazineFullReload(0, 6).addConfigs(m357_bp, m357_sp, m357_fmj, m357_jhp, m357_ap, m357_express))
						.offset(0.75, -0.0625, -0.3125D)
						.setupStandardFire().recoil(LAMBDA_RECOIL_ATLAS))
				.setupStandardConfiguration()
				.anim(LAMBDA_ATLAS_ANIMS).orchestra(PistolOrchestras.ORCHESTRA_ATLAS)
				).setDefaultAmmo(PistolAmmo.M357_SP, 12).pistolDualWield(true);
		PistolGuns.gun_light_revolver_atlas = new ItemGunBaseNT(WeaponQuality.B_SIDE, new GunConfig()
				.dura(300).draw(4).inspect(23).crosshair(Crosshair.CIRCLE).smoke(Lego.LAMBDA_STANDARD_SMOKE)
				.rec(new Receiver(0)
						.dmg(GUN_ATLAS_DAMAGE).delay(11).reload(55).jam(45).sound(PistolSounds.GUN_PISTOL_FIRE, 1.0F, 1.0F)
						.mag(new MagazineFullReload(0, 6).addConfigs(m357_bp, m357_sp, m357_fmj, m357_jhp, m357_ap, m357_express))
						.offset(0.75, -0.0625, -0.3125D)
						.setupStandardFire().recoil(LAMBDA_RECOIL_ATLAS))
				.setupStandardConfiguration()
				.anim(LAMBDA_ATLAS_ANIMS).orchestra(PistolOrchestras.ORCHESTRA_ATLAS)
				).setDefaultAmmo(PistolAmmo.M357_JHP, 12).pistolDualWield(true);
		PistolGuns.gun_light_revolver_dani = new ItemGunBaseNT(WeaponQuality.LEGENDARY,
				new GunConfig().dura(30_000).draw(20).inspect(23).crosshair(Crosshair.CIRCLE).smoke(Lego.LAMBDA_STANDARD_SMOKE)
				.rec(new Receiver(0)
						.dmg(GUN_DANI_CELESTIAL_DAMAGE).spreadHipfire(0F).delay(11).reload(55).jam(45).sound(PistolSounds.GUN_PISTOL_FIRE, 1.0F, 1.1F)
						.mag(new MagazineFullReload(0, 6).addConfigs(m357_bp, m357_sp, m357_fmj, m357_jhp, m357_ap, m357_express))
						.offset(0.75, -0.0625, 0.3125D)
						.setupStandardFire().recoil(LAMBDA_RECOIL_DANI))
				.pp(Lego.LAMBDA_STANDARD_CLICK_PRIMARY).pr(Lego.LAMBDA_STANDARD_RELOAD)
				.decider(GunStateDecider.LAMBDA_STANDARD_DECIDER)
				.anim(LAMBDA_DANI_ANIMS).orchestra(PistolOrchestras.ORCHESTRA_DANI),
				new GunConfig().dura(30_000).draw(20).inspect(23).crosshair(Crosshair.CIRCLE).smoke(Lego.LAMBDA_STANDARD_SMOKE)
				.rec(new Receiver(0)
						.dmg(GUN_DANI_LUNAR_DAMAGE).spreadHipfire(0F).delay(11).reload(55).jam(45).sound(PistolSounds.GUN_PISTOL_FIRE, 1.0F, 0.9F)
						.mag(new MagazineFullReload(1, 6).addConfigs(m357_bp, m357_sp, m357_fmj, m357_jhp, m357_ap, m357_express))
						.offset(0.75, -0.0625, -0.3125D)
						.setupStandardFire().recoil(LAMBDA_RECOIL_DANI))
				.ps(Lego.LAMBDA_STANDARD_CLICK_PRIMARY).pr(Lego.LAMBDA_STANDARD_RELOAD)
				.decider(GunStateDecider.LAMBDA_STANDARD_DECIDER)
				.anim(LAMBDA_DANI_ANIMS).orchestra(PistolOrchestras.ORCHESTRA_DANI)
				).setDefaultAmmo(PistolAmmo.M357_EXPRESS, 24);
	}

	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_RECOIL_ATLAS = (stack, ctx) -> {
		ItemGunBaseNT.setupRecoil(10, (float) (ctx.getPlayer().getRandom().nextGaussian() * 1.5));
	};

	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_RECOIL_DANI = (stack, ctx) -> {
		ItemGunBaseNT.setupRecoil(5, (float) (ctx.getPlayer().getRandom().nextGaussian() * 0.75));
	};

	@SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_ATLAS_ANIMS = (stack, type) -> {
		switch(type) {
		case EQUIP: return new BusAnimation()
				.addBus("EQUIP", new BusAnimationSequence().addPos(-90, 0, 0, 0).addPos(0, 0, 0, 350, IType.SIN_DOWN));
		case CYCLE: return new BusAnimation()
				.addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, 0, 50).addPos(0, 0, -3, 50).addPos(0, 0, 0, 250))
				.addBus("HAMMER", new BusAnimationSequence().addPos(0, 0, 1, 50).addPos(0, 0, 1, 300).addPos(0, 0, 0, 200))
				.addBus("DRUM", new BusAnimationSequence().addPos(0, 0, 0, 250).addPos(0, 0, 1, 200));
		case CYCLE_DRY: return new BusAnimation()
				.addBus("HAMMER", new BusAnimationSequence().addPos(0, 0, 1, 50).addPos(0, 0, 1, 200).addPos(0, 0, 0, 200))
				.addBus("DRUM", new BusAnimationSequence().addPos(0, 0, 0, 250).addPos(0, 0, 1, 200));
		case RELOAD: return new BusAnimation()
				.addBus("LATCH", new BusAnimationSequence().addPos(0, 0, 90, 300).addPos(0, 0, 90, 2000).addPos(0, 0, 0, 150))
				.addBus("FRONT", new BusAnimationSequence().addPos(0, 0, 0, 200).addPos(0, 0, 45, 150).addPos(0, 0, 45, 2000).addPos(0, 0, 0, 75))
				.addBus("RELOAD_ROT", new BusAnimationSequence().addPos(0, 0, 0, 300).addPos(60, 0, 0, 500).addPos(60, 0, 0, 500).addPos(0, -90, -90, 0).addPos(0, -90, -90, 600).addPos(0, 0, 0, 300).addPos(0, 0, 0, 100).addPos(-45, 0, 0, 50).addPos(-45, 0, 0, 100).addPos(0, 0, 0, 300))
				.addBus("RELOAD_MOVE", new BusAnimationSequence().addPos(0, 0, 0, 300).addPos(0, -15, 0, 1000).addPos(0, 0, 0, 450))
				.addBus("DRUM_PUSH", new BusAnimationSequence().addPos(0, 0, 0, 1600).addPos(0, 0, -5, 0).addPos(0, 0, 0, 300));
		case INSPECT: return new BusAnimation()
				.addBus("LATCH", new BusAnimationSequence().addPos(0, 0, 90, 300).addPos(0, 0, 90, 1000).addPos(0, 0, 0, 150))
				.addBus("FRONT", new BusAnimationSequence().addPos(0, 0, 0, 200).addPos(0, 0, 45, 150).addPos(0, 0, 45, 1000).addPos(0, 0, 0, 75))
				.addBus("RELOAD_ROT", new BusAnimationSequence().addPos(0, 0, 0, 300).addPos(45, 0, 0, 500, IType.SIN_FULL).addPos(45, 0, 0, 500).addPos(-45, 0, 0, 50).addPos(-45, 0, 0, 100).addPos(0, 0, 0, 300))
				.addBus("RELOAD_MOVE", new BusAnimationSequence().addPos(0, 0, 0, 300).addPos(0, -2.5, 0, 500, IType.SIN_FULL).addPos(0, -2.5, 0, 500).addPos(0, 0, 0, 350));
		case JAMMED: return new BusAnimation()
				.addBus("LATCH", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(0, 0, 90, 300).addPos(0, 0, 90, 1000).addPos(0, 0, 0, 150))
				.addBus("FRONT", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(0, 0, 0, 200).addPos(0, 0, 45, 150).addPos(0, 0, 45, 1000).addPos(0, 0, 0, 75))
				.addBus("RELOAD_ROT", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(0, 0, 0, 300).addPos(45, 0, 0, 500, IType.SIN_FULL).addPos(45, 0, 0, 500).addPos(-45, 0, 0, 50).addPos(-45, 0, 0, 100).addPos(0, 0, 0, 300))
				.addBus("RELOAD_MOVE", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(0, 0, 0, 300).addPos(0, -2.5, 0, 500, IType.SIN_FULL).addPos(0, -2.5, 0, 500).addPos(0, 0, 0, 350));
		}

		return null;
	};

	@SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_DANI_ANIMS = (stack, type) -> {
		switch(type) {
			case EQUIP: return new BusAnimation().addBus("EQUIP", new BusAnimationSequence().addPos(360 * 3, 0, 0, 1000, IType.SIN_DOWN));
		}
		return LAMBDA_ATLAS_ANIMS.apply(stack, type);
	};
}
