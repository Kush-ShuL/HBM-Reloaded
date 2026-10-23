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
public class XFactory9mm {
	
	public static Supplier<Float> GUN_GREASEGUN_DAMAGE = PistolSettings.value("GUN_GREASEGUN_DAMAGE", 3F);
	public static Supplier<Float> GUN_LONG_PISTOL_DAMAGE = PistolSettings.value("GUN_LONG_PISTOL_DAMAGE", 25F);
	public static Supplier<Float> GUN_UZI_DAMAGE = PistolSettings.value("GUN_UZI_DAMAGE", 3F);
	public static Supplier<Float> GUN_UZI_AKIMBO_LEFT_DAMAGE = PistolSettings.value("GUN_UZI_AKIMBO_LEFT_DAMAGE", 3F);
	public static Supplier<Float> GUN_UZI_AKIMBO_RIGHT_DAMAGE = PistolSettings.value("GUN_UZI_AKIMBO_RIGHT_DAMAGE", 3F);

	public static BulletConfig p9_sp;
	public static BulletConfig p9_fmj;
	public static BulletConfig p9_jhp;
	public static BulletConfig p9_ap;
	
	public static void initConfig() {}

	public static void init() {
		SpentCasing casing9 = new SpentCasing(CasingType.STRAIGHT).setColor(SpentCasing.COLOR_CASE_BRASS).setScale(1F, 1F, 0.75F);
		p9_sp = new BulletConfig("hbm:pistols/p9_sp").setItem(PistolAmmo.P9_SP)
				.setCasing(casing9.clone().register("p9"));
        PistolAmmo.recovery(p9_sp,0,12);
		p9_fmj = new BulletConfig("hbm:pistols/p9_fmj").setItem(PistolAmmo.P9_FMJ).setDamage(0.8F).setThresholdNegation(2F).setArmorPiercing(0.1F)
				.setCasing(casing9.clone().register("p9fmj"));
        PistolAmmo.recovery(p9_fmj,0,12);
		p9_jhp = new BulletConfig("hbm:pistols/p9_jhp").setItem(PistolAmmo.P9_JHP).setDamage(1.5F).setHeadshot(1.5F).setArmorPiercing(-0.25F)
				.setCasing(casing9.clone().register("p9jhp"));
        PistolAmmo.recovery(p9_jhp,0,12);
		p9_ap = new BulletConfig("hbm:pistols/p9_ap").setItem(PistolAmmo.P9_AP).setDoesPenetrate(true).setDamageFalloffByPen(false).setDamage(1.25F).setThresholdNegation(5F).setArmorPiercing(0.15F)
				.setCasing(casing9.clone().setColor(SpentCasing.COLOR_CASE_44).register("p9ap"));
        PistolAmmo.recovery(p9_ap,2,12);

		PistolGuns.gun_greasegun = new ItemGunBaseNT(WeaponQuality.A_SIDE, new GunConfig()
				.dura(3_000).draw(20).inspect(31).crosshair(Crosshair.L_CIRCLE).smoke(LAMBDA_SMOKE)
				.rec(new Receiver(0)
						.dmg(GUN_GREASEGUN_DAMAGE).delay(4).dry(40).auto(true).spread(0.015F).reload(60).jam(55).sound(PistolSounds.GUN_GREASEGUN_FIRE, 1.0F, 1.0F)
						.mag(new MagazineFullReload(0, 30).addConfigs(p9_sp, p9_fmj, p9_jhp, p9_ap))
						.offset(1, -0.0625 * 2.5, -0.25D)
						.setupStandardFire().recoil(LAMBDA_RECOIL_GREASEGUN))
				.setupStandardConfiguration()
				.anim(LAMBDA_GREASEGUN_ANIMS).orchestra(PistolOrchestras.ORCHESTRA_GREASEGUN)
				).setDefaultAmmo(PistolAmmo.P9_SP, 30).setNameMutator(LAMBDA_NAME_GREASEGUN)
				;

		PistolGuns.gun_lag = new ItemGunBaseNT(WeaponQuality.A_SIDE, new GunConfig()
				.dura(1_700).draw(7).inspect(31).crosshair(Crosshair.CIRCLE).smoke(LAMBDA_SMOKE)
				.rec(new Receiver(0)
						.dmg(GUN_LONG_PISTOL_DAMAGE).delay(4).dry(10).spread(0.005F).reload(53).jam(44).sound(PistolSounds.GUN_PISTOL_FIRE, 1.0F, 1.0F)
						.mag(new MagazineFullReload(0, 17).addConfigs(p9_sp, p9_fmj, p9_jhp, p9_ap))
						.offset(1, -0.0625 * 2.5, -0.25D)
						.setupStandardFire().fire(LAMBDA_FIRE_LAG).recoil(LAMBDA_RECOIL_LAG))
				.setupStandardConfiguration()
				.anim(LAMBDA_LAG_ANIMS).orchestra(PistolOrchestras.ORCHESTRA_LAG)
				).setDefaultAmmo(PistolAmmo.P9_JHP, 17).pistolDualWield(true);

		PistolGuns.gun_uzi = new ItemGunBaseNT(WeaponQuality.A_SIDE, new GunConfig()
				.dura(3_000).draw(15).inspect(31).crosshair(Crosshair.CIRCLE).smoke(LAMBDA_SMOKE)
				.rec(new Receiver(0)
						.dmg(GUN_UZI_DAMAGE).delay(2).dry(25).auto(true).spread(0.005F).reload(55).jam(50).sound(PistolSounds.GUN_UZI_FIRE, 1.0F, 1.0F)
						.mag(new MagazineFullReload(0, 30).addConfigs(p9_sp, p9_fmj, p9_jhp, p9_ap))
						.offset(1, -0.0625 * 2.5, -0.25D)
						.setupStandardFire().recoil(LAMBDA_RECOIL_UZI))
				.setupStandardConfiguration()
				.anim(LAMBDA_UZI_ANIMS).orchestra(PistolOrchestras.ORCHESTRA_UZI)
				).setDefaultAmmo(PistolAmmo.P9_SP, 30).setNameMutator(LAMBDA_NAME_UZI)
				;
		PistolGuns.gun_uzi_akimbo = new ItemGunBaseNT(WeaponQuality.B_SIDE,
				new GunConfig().dura(3_000).draw(15).inspect(31).crosshair(Crosshair.CIRCLE).smoke(LAMBDA_SMOKE)
				.rec(new Receiver(0)
						.dmg(GUN_UZI_AKIMBO_LEFT_DAMAGE).spreadHipfire(0F).delay(2).dry(25).auto(true).spread(0.005F).reload(55).jam(50).sound(PistolSounds.GUN_UZI_FIRE, 1.0F, 1.0F)
						.mag(new MagazineFullReload(0, 30).addConfigs(p9_sp, p9_fmj, p9_jhp, p9_ap))
						.offset(1, -0.0625 * 2.5, 0.375D)
						.setupStandardFire().recoil(LAMBDA_RECOIL_UZI))
				.pp(Lego.LAMBDA_STANDARD_CLICK_PRIMARY).pr(Lego.LAMBDA_STANDARD_RELOAD)
				.decider(GunStateDecider.LAMBDA_STANDARD_DECIDER)
				.anim(LAMBDA_UZI_ANIMS).orchestra(PistolOrchestras.ORCHESTRA_UZI_AKIMBO),
				new GunConfig().dura(3_000).draw(15).inspect(31).crosshair(Crosshair.CIRCLE).smoke(LAMBDA_SMOKE)
				.rec(new Receiver(0)
						.dmg(GUN_UZI_AKIMBO_RIGHT_DAMAGE).spreadHipfire(0F).delay(2).dry(25).auto(true).spread(0.005F).reload(55).jam(50).sound(PistolSounds.GUN_UZI_FIRE, 1.0F, 1.0F)
						.mag(new MagazineFullReload(1, 30).addConfigs(p9_sp, p9_fmj, p9_jhp, p9_ap))
						.offset(1, -0.0625 * 2.5, -0.375D)
						.setupStandardFire().recoil(LAMBDA_RECOIL_UZI))
				.ps(Lego.LAMBDA_STANDARD_CLICK_PRIMARY).pr(Lego.LAMBDA_STANDARD_RELOAD)
				.decider(LAMBDA_SECOND_UZI)
				.anim(LAMBDA_UZI_ANIMS).orchestra(PistolOrchestras.ORCHESTRA_UZI_AKIMBO)
				).setDefaultAmmo(PistolAmmo.P9_SP, 60);
	}

	public static Function<ItemStack, String> LAMBDA_NAME_GREASEGUN = (stack) -> {
		if(XWeaponModManager.hasUpgrade(stack, 0, 206)) return PistolNames.sourceKey(stack) + "_m3";
		return null;
	};

	public static Function<ItemStack, String> LAMBDA_NAME_UZI = (stack) -> {
		if(XWeaponModManager.hasUpgrade(stack, 0, 201)) return PistolNames.sourceKey(stack) + "_richter";
		return null;
	};

	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_RECOIL_GREASEGUN = (stack, ctx) -> {
		ItemGunBaseNT.setupRecoil(2, (float) (ctx.getPlayer().getRandom().nextGaussian() * 0.5));
	};

	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_RECOIL_LAG = (stack, ctx) -> {
		ItemGunBaseNT.setupRecoil(5, (float) (ctx.getPlayer().getRandom().nextGaussian() * 1.5));
	};

	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_RECOIL_UZI = (stack, ctx) -> {
		ItemGunBaseNT.setupRecoil(1, (float) (ctx.getPlayer().getRandom().nextGaussian() * 0.25));
	};

	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_SECOND_UZI = (stack, ctx) -> {
		int index = ctx.configIndex;
		GunState lastState = ItemGunBaseNT.getState(stack, index);
		GunStateDecider.deciderStandardFinishDraw(stack, lastState, index);
		GunStateDecider.deciderStandardClearJam(stack, lastState, index);
		GunStateDecider.deciderStandardReload(stack, ctx, lastState, 0, index);
		GunStateDecider.deciderAutoRefire(stack, ctx, lastState, 0, index, () -> { return ItemGunBaseNT.getSecondary(stack, index) && ItemGunBaseNT.getMode(stack, ctx.configIndex) == 0; });
	};

	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_FIRE_LAG = (stack, ctx) -> {
		GunAnimation type = ItemGunBaseNT.getLastAnim(stack, ctx.configIndex);
		int timer = ItemGunBaseNT.getAnimTimer(stack, ctx.configIndex);
		Player player = ctx.getPlayer();
		if(player != null && type == GunAnimation.INSPECT && timer > 20 && timer < 60) {
			int index = ctx.configIndex;
			Receiver primary = ctx.config.getReceivers(stack)[0];
			IMagazine mag = primary.getMagazine(stack);
			BulletConfig config = (BulletConfig) mag.getType(stack, ctx.inventory);
			GunStats.bulletFired(player);
			mag.useUpAmmo(stack, ctx.inventory, 1);
			ItemGunBaseNT.setWear(stack, index, Math.min(ItemGunBaseNT.getWear(stack, index) + config.wear, ctx.config.getDurability(stack)));
			GunSounds.play(player,primary.getFireSound(stack),primary.getFireVolume(stack),primary.getFirePitch(stack));
			ItemGunBaseNT.setState(stack, index, GunState.COOLDOWN);
			ItemGunBaseNT.setTimer(stack, index, primary.getDelayAfterFire(stack));
			LegacyEntityDamage.attackEntityFromNT(player, BulletConfig.getDamage(player, player, DamageClass.PHYSICAL), 1_000F, true, false, 1D, 5F, 0F);
		} else {
			Lego.doStandardFire(stack, ctx, GunAnimation.CYCLE, 0, true);
		}
	};

	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_SMOKE = (stack, ctx) -> {
		Lego.handleStandardSmoke(ctx.entity, stack, 2000, 0.05D, 1.1D, ctx.configIndex);
	};

	@SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_GREASEGUN_ANIMS = (stack, type) -> {
		switch(type) {
		case EQUIP: return new BusAnimation()
				.addBus("EQUIP", new BusAnimationSequence().addPos(80, 0, 0, 0).addPos(80, 0, 0, 500).addPos(0, 0, 0, 500, IType.SIN_FULL))
				.addBus("STOCK", new BusAnimationSequence().addPos(0, 0, -4, 0).addPos(0, 0, -4, 200).addPos(0, 0, 0, 300, IType.SIN_FULL));
		case CYCLE: return new BusAnimation()
				.addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, ItemGunBaseNT.getIsAiming(stack) ? -0.25 : -0.5, 50, IType.SIN_DOWN).addPos(0, 0, 0, 100, IType.SIN_FULL))
				.addBus("FLAP", new BusAnimationSequence().addPos(0, 0, 15, 100, IType.SIN_DOWN).addPos(0, 0, -5, 100, IType.SIN_FULL).addPos(0, 0, 0, 50, IType.SIN_FULL));
		case CYCLE_DRY: return new BusAnimation()
				.addBus("LIFT", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(-25, 0, 0, 250, IType.SIN_FULL).addPos(-25, 0, 0, 750).addPos(0, 0, 0, 500, IType.SIN_FULL))
				.addBus("TURN", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(0, 0, -45, 250, IType.SIN_FULL).addPos(0, 0, -45, 750).addPos(0, 0, 0, 500, IType.SIN_FULL))
				.addBus("HANDLE", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(0, 0, 0, 250).addPos(-90, 0, 0, 250, IType.SIN_FULL).addPos(0, 0, 0, 250, IType.SIN_FULL));
		case RELOAD:
			boolean empty = ((ItemGunBaseNT) stack.getItem()).getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack).getAmount(stack, (ItemGunBaseNT.CLIENT==null||ItemGunBaseNT.CLIENT.localPlayer()==null?null:new GunInventory(ItemGunBaseNT.CLIENT.localPlayer()))) <= 0;
			return new BusAnimation()
				.addBus("MAG", new BusAnimationSequence().addPos(0, -8, 0, 250, IType.SIN_UP).addPos(0, -8, 0, 750).addPos(0, 0, 0, 500, IType.SIN_DOWN))
				.addBus("LIFT", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(-25, 0, 0, 250, IType.SIN_FULL).addPos(-25, 0, 0, 1750).addPos(0, 0, 0, 500, IType.SIN_FULL))
				.addBus("TURN", new BusAnimationSequence().addPos(0, 0, 0, 1750).addPos(0, 0, -45, 250, IType.SIN_FULL).addPos(0, 0, -45, 500).addPos(0, 0, 0, 500, IType.SIN_FULL))
				.addBus("HANDLE", new BusAnimationSequence().addPos(0, 0, 0, 2000).addPos(-90, 0, 0, 250, IType.SIN_FULL).addPos(0, 0, 0, 250, IType.SIN_FULL))
				.addBus("BULLET", new BusAnimationSequence().addPos(empty ? 1 : 0, 0, 0, 0).addPos(0, 0, 0, 1000));
		case JAMMED: return new BusAnimation()
				.addBus("LIFT", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(-25, 0, 0, 250, IType.SIN_FULL).addPos(-25, 0, 0, 1500).addPos(0, 0, 0, 500, IType.SIN_FULL))
				.addBus("TURN", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(0, 0, -45, 250, IType.SIN_FULL).addPos(0, 0, -45, 1500).addPos(0, 0, 0, 500, IType.SIN_FULL))
				.addBus("HANDLE", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(0, 0, 0, 250).addPos(-90, 0, 0, 250, IType.SIN_FULL).addPos(0, 0, 0, 250, IType.SIN_FULL).addPos(0, 0, 0, 250).addPos(-90, 0, 0, 250, IType.SIN_FULL).addPos(0, 0, 0, 250, IType.SIN_FULL));
		case INSPECT: return new BusAnimation()
				.addBus("TURN", new BusAnimationSequence().addPos(0, 0, -45, 150).addPos(0, 0, 45, 150).addPos(0, 0, 45, 50).addPos(0, 0, 0, 250).addPos(0, 0, 0, 500).addPos(0, 0, 45, 150).addPos(0, 0, -45, 150).addPos(0, 0, 0, 150))
				.addBus("FLAP", new BusAnimationSequence().addPos(0, 0, 0, 300).addPos(0, 0, 180, 150).addPos(0, 0, 180, 850).addPos(0, 0, 0, 150));
		}

		return null;
	};

	@SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_LAG_ANIMS = (stack, type) -> {
		switch(type) {
		case EQUIP: return new BusAnimation()
				.addBus("EQUIP", new BusAnimationSequence().addPos(-90, 0, 0, 0).addPos(0, 0, 0, 350, IType.SIN_DOWN));
		case CYCLE: return com.hbm.client.weapon.pistols.PistolAnimationAssets.get("lag","Firing");
				//.addBus("HAMMER", new BusAnimationSequence().addPos(0, 0, 25, 50).addPos(0, 0, 25, 50).addPos(0, 0, 0, 100, IType.SIN_DOWN));
		case CYCLE_DRY: return com.hbm.client.weapon.pistols.PistolAnimationAssets.get("lag","Dryfire");
		case RELOAD: return com.hbm.client.weapon.pistols.PistolAnimationAssets.get("lag","Reload");
		case JAMMED: return com.hbm.client.weapon.pistols.PistolAnimationAssets.get("lag","Jam");
		case INSPECT: return com.hbm.client.weapon.pistols.PistolAnimationAssets.get("lag","Inspect")
				.addBus("ADD_TRANS", new BusAnimationSequence().addPos(-4, 0, -3, 500).addPos(-4, 0, -3, 2000).addPos(0, 0, 0, 500))
				.addBus("ADD_ROT", new BusAnimationSequence().addPos(0, -2, 5, 500).addPos(0, -2, 5, 2000).addPos(0, 0, 0, 500));
		}

		return null;
	};

	@SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_UZI_ANIMS = (stack, type) -> {
		switch(type) {
		case EQUIP: return new BusAnimation()
				.addBus("EQUIP", new BusAnimationSequence().addPos(80, 0, 0, 0).addPos(80, 0, 0, 500).addPos(0, 0, 0, 500, IType.SIN_FULL))
				.addBus("STOCKBACK", new BusAnimationSequence().addPos(-200, 0, 0, 0).addPos(0, 0, 0, 500, IType.SIN_FULL))
				.addBus("STOCKFRONT", new BusAnimationSequence().addPos(180, 0, 0, 0).addPos(0, 0, 0, 500, IType.SIN_FULL));
		case CYCLE: return new BusAnimation()
				.addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, ItemGunBaseNT.getIsAiming(stack) ? -0.5 : -0.75, 25, IType.SIN_DOWN).addPos(0, 0, 0, 75, IType.SIN_FULL));
		case CYCLE_DRY: return new BusAnimation()
				.addBus("LIFT", new BusAnimationSequence().addPos(0, 0, 0, 250).addPos(-25, 0, 0, 250, IType.SIN_FULL).addPos(-25, 0, 0, 500).addPos(0, 0, 0, 250, IType.SIN_FULL))
				.addBus("SLIDE", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(0, 0, -2, 150, IType.SIN_FULL).addPos(0, 0, 0, 50, IType.SIN_UP));
		case RELOAD:
			boolean empty = ((ItemGunBaseNT) stack.getItem()).getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack).getAmount(stack, (ItemGunBaseNT.CLIENT==null||ItemGunBaseNT.CLIENT.localPlayer()==null?null:new GunInventory(ItemGunBaseNT.CLIENT.localPlayer()))) <= 0;
			return new BusAnimation()
				.addBus("MAG", new BusAnimationSequence().addPos(0, 0, 0, 250).addPos(0, -10, 0, 250, IType.SIN_UP).addPos(0, -10, 0, 750).addPos(0, 0, 0, 500, IType.SIN_DOWN))
				.addBus("LIFT", new BusAnimationSequence().addPos(-25, 0, 0, 250, IType.SIN_FULL).addPos(-25, 0, 0, 2000).addPos(0, 0, 0, 500, IType.SIN_FULL))
				.addBus("SLIDE", new BusAnimationSequence().addPos(0, 0, 0, 2000).addPos(0, 0, -2, 150, IType.SIN_FULL).addPos(0, 0, 0, 50, IType.SIN_UP))
				.addBus("BULLET", new BusAnimationSequence().addPos(empty ? 0 : 1, 0, 0, 0).addPos(empty ? 0 : 1, 0, 0, 500).addPos(1, 0, 0, 0));
		case JAMMED: return new BusAnimation()
				.addBus("LIFT", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(-25, 0, 0, 250, IType.SIN_FULL).addPos(-25, 0, 0, 1250).addPos(0, 0, 0, 500, IType.SIN_FULL))
				.addBus("SLIDE", new BusAnimationSequence().addPos(0, 0, 0, 1000).addPos(0, 0, -2, 150, IType.SIN_FULL).addPos(0, 0, 0, 50, IType.SIN_UP).addPos(0, 0, 0, 500).addPos(0, 0, -2, 150, IType.SIN_FULL).addPos(0, 0, 0, 50, IType.SIN_UP));
		case INSPECT: return new BusAnimation()
				.addBus("YEET", new BusAnimationSequence().addPos(0, -1, 0, 100).addPos(0, 0, 0, 100, IType.SIN_UP).addPos(0, 12, 0, 350, IType.SIN_DOWN).addPos(0, 0, 0, 350, IType.SIN_UP).addPos(0, -1, 0, 50, IType.SIN_DOWN).addPos(0, 0, 0, 100, IType.SIN_FULL))
				.addBus("SPEEN", new BusAnimationSequence().addPos(0, 0, 0, 250).addPos(-360, 0, 0, 600));
		}
		return null;
	};
}
