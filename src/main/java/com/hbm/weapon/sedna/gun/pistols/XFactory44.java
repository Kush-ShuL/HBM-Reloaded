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
public class XFactory44 {

	public static final ResourceLocation scope_lilmac = ResourceLocation.parse("hbm:textures/misc/scope_44.png");

	public static Supplier<Float> GUN_HENRY_DAMAGE = PistolSettings.value("GUN_HENRY_DAMAGE", 10F);
	public static Supplier<Float> GUN_LINCOLN_DAMAGE = PistolSettings.value("GUN_LINCOLN_DAMAGE", 20F);
	public static Supplier<Float> GUN_HEAVY_REVOLVER_DAMAGE = PistolSettings.value("GUN_HEAVY_REVOLVER_DAMAGE", 15F);
	public static Supplier<Float> GUN_LILMAC_DAMAGE = PistolSettings.value("GUN_LILMAC_DAMAGE", 30F);
	public static Supplier<Float> GUN_PROTEGE_DAMAGE = PistolSettings.value("GUN_PROTEGE_DAMAGE", 30F);
	public static Supplier<Float> GUN_HANGMAN_DAMAGE = PistolSettings.value("GUN_HANGMAN_DAMAGE", 25F);
	public static Supplier<Float> GUN_HANGMAN_SMACK_DAMAGE = PistolSettings.value("GUN_HANGMAN_SMACK_DAMAGE", 10F);

	public static BulletConfig m44_bp;
	public static BulletConfig m44_sp;
	public static BulletConfig m44_fmj;
	public static BulletConfig m44_jhp;
	public static BulletConfig m44_ap;
	public static BulletConfig m44_express;
	public static BulletConfig m44_equestrian_pip;
	public static BulletConfig m44_equestrian_mn7;
	
	public static void initConfig() {}

public static BiConsumer<com.hbm.entity.projectile.EntityBulletBaseMK4,HitResult> LAMBDA_BOXCAR=PistolSpecialEntities::boxcar;

public static BiConsumer<com.hbm.entity.projectile.EntityBulletBaseMK4,HitResult> LAMBDA_TORPEDO=PistolSpecialEntities::torpedo;

	public static void init() {
		SpentCasing casing44 = new SpentCasing(CasingType.STRAIGHT).setColor(SpentCasing.COLOR_CASE_BRASS).setupSmoke(1F, 0.5D, 60, 20);
		m44_bp = LegendaryGunFactory.m44_bp;
		m44_sp = LegendaryGunFactory.m44_sp;
		m44_fmj = LegendaryGunFactory.m44_fmj;
		m44_jhp = LegendaryGunFactory.m44_jhp;
		m44_ap = LegendaryGunFactory.m44_ap;
		m44_express = LegendaryGunFactory.m44_express;
		m44_equestrian_pip = LegendaryGunFactory.m44_equestrian_pip;
		m44_equestrian_mn7 = new BulletConfig("hbm:pistols/m44_equestrian_mn7").setItem(PistolAmmo.M44_EQUESTRIAN).setDamage(0F).setOnImpact(LAMBDA_TORPEDO)
				.setCasing(casing44.clone().setColor(SpentCasing.COLOR_CASE_EQUESTRIAN).register("m44equestrianMn7"));

		PistolGuns.gun_henry = new ItemGunBaseNT(WeaponQuality.A_SIDE, new GunConfig()
				.dura(300).draw(15).inspect(23).reloadSequential(true).crosshair(Crosshair.CIRCLE).smoke(Lego.LAMBDA_STANDARD_SMOKE)
				.rec(new Receiver(0)
						.dmg(GUN_HENRY_DAMAGE).delay(20).reload(25, 11, 14, 8).jam(45).sound(PistolSounds.GUN_RIFLE_FIRE, 1.0F, 1.0F)
						.mag(new MagazineSingleReload(0, 14).addConfigs(m44_bp, m44_sp, m44_fmj, m44_jhp, m44_ap, m44_express))
						.offset(0.75, -0.0625, -0.1875D)
						.setupStandardFire().recoil(LAMBDA_RECOIL_HENRY))
				.setupStandardConfiguration()
				.anim(LAMBDA_HENRY_ANIMS).orchestra(PistolOrchestras.ORCHESTRA_HENRY)
				).setDefaultAmmo(PistolAmmo.M44_SP, 14);
		PistolGuns.gun_henry_lincoln = new ItemGunBaseNT(WeaponQuality.B_SIDE, new GunConfig()
				.dura(300).draw(15).inspect(23).reloadSequential(true).crosshair(Crosshair.CIRCLE).smoke(Lego.LAMBDA_STANDARD_SMOKE)
				.rec(new Receiver(0)
						.dmg(GUN_LINCOLN_DAMAGE).spreadHipfire(0F).delay(20).reload(25, 11, 14, 8).jam(45).sound(PistolSounds.GUN_RIFLE_FIRE, 1.0F, 1.25F)
						.mag(new MagazineSingleReload(0, 14).addConfigs(m44_bp, m44_sp, m44_fmj, m44_jhp, m44_ap, m44_express))
						.offset(0.75, -0.0625, -0.1875D)
						.setupStandardFire().recoil(LAMBDA_RECOIL_HENRY))
				.setupStandardConfiguration()
				.anim(LAMBDA_HENRY_ANIMS).orchestra(PistolOrchestras.ORCHESTRA_HENRY)
				).setDefaultAmmo(PistolAmmo.M44_JHP, 14);

		PistolGuns.gun_heavy_revolver = new ItemGunBaseNT(WeaponQuality.A_SIDE, new GunConfig()
				.dura(600).draw(10).inspect(23).crosshair(Crosshair.L_CLASSIC).smoke(Lego.LAMBDA_STANDARD_SMOKE)
				.rec(new Receiver(0)
						.dmg(GUN_HEAVY_REVOLVER_DAMAGE).delay(14).reload(46).jam(23).sound(PistolSounds.GUN_HEAVY_REVOLVER_FIRE, 1.0F, 1.0F)
						.mag(new MagazineFullReload(0, 6).addConfigs(m44_bp, m44_sp, m44_fmj, m44_jhp, m44_ap, m44_express))
						.offset(0.75, -0.0625, -0.3125D)
						.setupStandardFire().recoil(LAMBDA_RECOIL_NOPIP))
				.setupStandardConfiguration()
				.anim(LAMBDA_NOPIP_ANIMS).orchestra(PistolOrchestras.ORCHESTRA_NOPIP)
				).setDefaultAmmo(PistolAmmo.M44_SP, 12).setNameMutator(LAMBDA_NAME_NOPIP)
				.pistolDualWield(true);
		
		PistolGuns.gun_heavy_revolver_protege = new ItemGunBaseNT(WeaponQuality.LEGENDARY, new GunConfig()
				.dura(31_000).draw(10).inspect(23).crosshair(Crosshair.L_CLASSIC).smoke(Lego.LAMBDA_STANDARD_SMOKE)
				.rec(new Receiver(0)
						.dmg(GUN_PROTEGE_DAMAGE).delay(14).reload(46).jam(23).sound(PistolSounds.GUN_HEAVY_REVOLVER_FIRE, 1.0F, 0.8F)
						.mag(new MagazineFullReload(0, 6).addConfigs(m44_equestrian_mn7, m44_bp, m44_sp, m44_fmj, m44_jhp, m44_ap, m44_express))
						.offset(0.75, -0.0625, -0.3125D)
						.setupStandardFire().recoil(LAMBDA_RECOIL_NOPIP))
				.setupStandardConfiguration()
				.anim(LAMBDA_LILMAC_ANIMS).orchestra(PistolOrchestras.ORCHESTRA_NOPIP)
				).setDefaultAmmo(PistolAmmo.M44_JHP, 12).pistolDualWield(true);

		PistolGuns.gun_hangman = new ItemGunBaseNT(WeaponQuality.LEGENDARY, new GunConfig()
				.dura(600).draw(10).inspect(31).inspectCancel(false).crosshair(Crosshair.CIRCLE).smoke(Lego.LAMBDA_STANDARD_SMOKE)
				.rec(new Receiver(0)
						.dmg(GUN_HANGMAN_DAMAGE).delay(10).reload(46).jam(23).sound(PistolSounds.GUN_HEAVY_REVOLVER_FIRE, 1.0F, 1.0F)
						.mag(new MagazineFullReload(0, 8).addConfigs(m44_bp, m44_sp, m44_fmj, m44_jhp, m44_ap, m44_express))
						.offset(1, -0.0625 * 2.5, -0.25D)
						.setupStandardFire().recoil(LAMBDA_RECOIL_HANGMAN))
				.setupStandardConfiguration().ps(SMACK_A_FUCKER)
				.anim(LAMBDA_HANGMAN_ANIMS).orchestra(PistolOrchestras.ORCHESTRA_HANGMAN)
				).setDefaultAmmo(PistolAmmo.M44_FMJ, 16);
	}

	public static Function<ItemStack, String> LAMBDA_NAME_NOPIP = (stack) -> {
		if(XWeaponModManager.hasUpgrade(stack, 0, 202)) return PistolNames.sourceKey(stack) + "_scoped";
		return null;
	};

	public static BiConsumer<ItemStack, LambdaContext> SMACK_A_FUCKER = (stack, ctx) -> {
		if(ItemGunBaseNT.getState(stack, ctx.configIndex) == GunState.IDLE || ItemGunBaseNT.getLastAnim(stack, ctx.configIndex) == GunAnimation.CYCLE) {
			ItemGunBaseNT.setIsAiming(stack, false);
			ItemGunBaseNT.setState(stack, ctx.configIndex, GunState.DRAWING);
			ItemGunBaseNT.setTimer(stack, ctx.configIndex, ctx.config.getInspectDuration(stack));
			ItemGunBaseNT.playAnimation(ctx.getPlayer(), stack, GunAnimation.INSPECT, ctx.configIndex);
		}
	};

	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_RECOIL_HENRY = (stack, ctx) -> {
		ItemGunBaseNT.setupRecoil(5, (float) (ctx.getPlayer().getRandom().nextGaussian() * 1));
	};

	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_RECOIL_NOPIP = (stack, ctx) -> {
		ItemGunBaseNT.setupRecoil(10, (float) (ctx.getPlayer().getRandom().nextGaussian() * 1.5));
	};

	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_RECOIL_HANGMAN = (stack, ctx) -> {
		ItemGunBaseNT.setupRecoil(5, (float) (ctx.getPlayer().getRandom().nextGaussian() * 1));
	};

	@SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_HENRY_ANIMS = (stack, type) -> {
		switch(type) {
		case EQUIP: return new BusAnimation()
				.addBus("EQUIP", new BusAnimationSequence().addPos(-90, 0, 0, 0).addPos(0, 0, -3, 350, IType.SIN_DOWN))
				.addBus("SIGHT", new BusAnimationSequence().addPos(80, 0, 0, 0).addPos(80, 0, 0, 500).addPos(0, 0, -3, 250, IType.SIN_DOWN));
		case CYCLE: return new BusAnimation()
				.addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, 0, 50).addPos(0, 0, -1, 50).addPos(0, 0, 0, 250))
				.addBus("SIGHT", new BusAnimationSequence().addPos(35, 0, 0, 100, IType.SIN_DOWN).addPos(0, 0, 0, 100, IType.SIN_FULL))
				.addBus("LEVER", new BusAnimationSequence().addPos(0, 0, 0, 600).addPos(-90, 0, 0, 200).addPos(0, 0, 0, 200))
				.addBus("TURN", new BusAnimationSequence().addPos(0, 0, 0, 600).addPos(0, 0, 45, 200, IType.SIN_DOWN).addPos(0, 0, 0, 200, IType.SIN_UP))
				.addBus("HAMMER", new BusAnimationSequence().addPos(30, 0, 0, 50).addPos(30, 0, 0, 550).addPos(0, 0, 0, 200));
		case CYCLE_DRY: return new BusAnimation()
				.addBus("LEVER", new BusAnimationSequence().addPos(0, 0, 0, 600).addPos(-90, 0, 0, 200).addPos(0, 0, 0, 200))
				.addBus("TURN", new BusAnimationSequence().addPos(0, 0, 0, 600).addPos(0, 0, 45, 200, IType.SIN_DOWN).addPos(0, 0, 0, 200, IType.SIN_UP))
				.addBus("HAMMER", new BusAnimationSequence().addPos(30, 0, 0, 50).addPos(30, 0, 0, 550).addPos(0, 0, 0, 200));
		case RELOAD: return new BusAnimation()
				.addBus("LIFT", new BusAnimationSequence().addPos(-60, 0, 0, 400, IType.SIN_FULL))
				.addBus("TWIST", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(0, 0, -90, 200, IType.SIN_FULL))
				.addBus("BULLET", new BusAnimationSequence().addPos(0, 0, 0, 700).addPos(3, 0, -6, 0).addPos(0, 0, 1, 300, IType.SIN_FULL).addPos(0, 0, 0, 250, IType.SIN_FULL));
		case RELOAD_CYCLE: return new BusAnimation()
				.addBus("LIFT", new BusAnimationSequence().addPos(-60, 0, 0, 0))
				.addBus("TWIST", new BusAnimationSequence().addPos(0, 0, -90, 0))
				.addBus("BULLET", new BusAnimationSequence().addPos(3, 0, -6, 0).addPos(0, 0, 1, 300, IType.SIN_FULL).addPos(0, 0, 0, 250, IType.SIN_FULL));
		case RELOAD_END:
			boolean empty = ((ItemGunBaseNT) stack.getItem()).getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack).getAmountBeforeReload(stack) <= 0;
			return new BusAnimation()
				.addBus("LIFT", new BusAnimationSequence().addPos(-60, 0, 0, 0).addPos(-60, 0, 0, 300).addPos(0, 0, 0, 400, IType.SIN_FULL))
				.addBus("TWIST", new BusAnimationSequence().addPos(0, 0, -90, 0).addPos(0, 0, 0, 200, IType.SIN_FULL))
				.addBus("LEVER", new BusAnimationSequence().addPos(0, 0, 0, 700).addPos(empty ? -90 : 0, 0, 0, 200).addPos(0, 0, 0, 200))
				.addBus("TURN", new BusAnimationSequence().addPos(0, 0, 0, 700).addPos(0, 0, empty ? 45 : 0, 200, IType.SIN_DOWN).addPos(0, 0, 0, 200, IType.SIN_UP));
		case JAMMED: return new BusAnimation()
				.addBus("LIFT", new BusAnimationSequence().addPos(-60, 0, 0, 0).addPos(-60, 0, 0, 300).addPos(0, 0, 0, 400, IType.SIN_FULL))
				.addBus("TWIST", new BusAnimationSequence().addPos(0, 0, -90, 0).addPos(0, 0, 0, 200, IType.SIN_FULL))
				.addBus("LEVER", new BusAnimationSequence().addPos(0, 0, 0, 700).addPos(-90, 0, 0, 200).addPos(0, 0, 0, 200).addPos(0, 0, 0, 500).addPos(-90, 0, 0, 200).addPos(0, 0, 0, 200).addPos(0, 0, 0, 200).addPos(-90, 0, 0, 200).addPos(0, 0, 0, 200))
				.addBus("TURN", new BusAnimationSequence().addPos(0, 0, 0, 700).addPos(0, 0, 45, 200, IType.SIN_DOWN).addPos(0, 0, 0, 200, IType.SIN_UP).addPos(0, 0, 0, 500).addPos(0, 0, 45, 200, IType.SIN_FULL).addPos(0, 0, 45, 600).addPos(0, 0, 0, 200, IType.SIN_FULL));
		case INSPECT: return new BusAnimation()
				.addBus("YEET", new BusAnimationSequence().addPos(0, 2, 0, 200, IType.SIN_DOWN).addPos(0, 0, 0, 200, IType.SIN_UP))
				.addBus("ROLL", new BusAnimationSequence().addPos(0, 0, 360, 400));
		}

		return null;
	};

public static BiFunction<ItemStack,GunAnimation,BusAnimation> LAMBDA_NOPIP_ANIMS=LegendaryGunAnimations.LAMBDA_NOPIP_ANIMS;
public static BiFunction<ItemStack,GunAnimation,BusAnimation> LAMBDA_LILMAC_ANIMS=LegendaryGunAnimations.LAMBDA_LILMAC_ANIMS;

	@SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_HANGMAN_ANIMS = (stack, type) -> {
		switch(type) {
		case EQUIP: return new BusAnimation().addBus("EQUIP", new BusAnimationSequence().addPos(60, 0, 0, 0).addPos(0, 0, 0, 500, IType.SIN_DOWN));
		case CYCLE: return new BusAnimation()
				.addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, 0, 50).addPos(0, 0, -3, 50).addPos(0, 0, 0, 250));
		case RELOAD: return new BusAnimation()
				.addBus("LID", new BusAnimationSequence().addPos(0, 0, -90, 250).addPos(0, 0, -90, 1500).addPos(0, 0, 0, 250))
				.addBus("MAG", new BusAnimationSequence().addPos(0, 0, 0, 250).addPos(0, -10, 0, 250, IType.SIN_UP).addPos(0, -10, 0, 500).addPos(0, 0, 0, 350, IType.SIN_FULL))
				.addBus("BULLETS", new BusAnimationSequence().addPos(1, 1, 1, 0).addPos(0, 0, 0, 500))
				.addBus("EQUIP", new BusAnimationSequence().addPos(-15, 0, 0, 500, IType.SIN_FULL).addPos(-15, 0, 0, 850).addPos(-25, 0, 0, 100, IType.SIN_DOWN).addPos(0, 0, 0, 350, IType.SIN_FULL))
				.addBus("ROLL", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(0, 0, 25, 250, IType.SIN_FULL).addPos(0, 0, 25, 1000).addPos(0, 0, 0, 250, IType.SIN_FULL));
		case INSPECT: return new BusAnimation()
				.addBus("TURN", new BusAnimationSequence().addPos(0, 170, 0, 500, IType.SIN_UP).addPos(0, 170, 0, 550).addPos(0, 0, 0, 500, IType.SIN_FULL))
				.addBus("ROLL", new BusAnimationSequence().addPos(0, 0, 110, 500, IType.SIN_FULL).addPos(0, 0, 110, 550).addPos(0, 0, 0, 500, IType.SIN_FULL))
				.addBus("SMACK", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(0, 0, 1, 150, IType.SIN_DOWN).addPos(0, 0, -3, 150, IType.SIN_UP).addPos(0, 0, 0, 350, IType.SIN_FULL));
		case JAMMED: return new BusAnimation()
				.addBus("LID", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(0, 0, -90, 250).addPos(0, 0, -90, 300).addPos(0, 0, 0, 250))
				.addBus("MAG", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(0, 0, 0, 250).addPos(0, -3, 0, 150, IType.SIN_UP).addPos(0, 0, 0, 150, IType.SIN_FULL))
				.addBus("EQUIP", new BusAnimationSequence().addPos(0, 0, 0, 1000).addPos(-10, 0, 0, 100, IType.SIN_DOWN).addPos(0, 0, 0, 350, IType.SIN_FULL))
				.addBus("ROLL", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(0, 0, 25, 250, IType.SIN_FULL).addPos(0, 0, 25, 300).addPos(0, 0, 0, 250, IType.SIN_FULL));
		}

		return null;
	};
}
