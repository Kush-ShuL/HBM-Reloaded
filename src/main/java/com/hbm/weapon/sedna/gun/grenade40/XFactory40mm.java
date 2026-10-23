package com.hbm.weapon.sedna.gun.grenade40;
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
public final class XFactory40mm {
 public static final Supplier<Float> GUN_CONGO_LAKE_DAMAGE=Grenade40Settings.value("GUN_CONGO_LAKE_DAMAGE",20F);
 public static final Supplier<Float> GUN_MK108_DAMAGE=Grenade40Settings.value("GUN_MK108_DAMAGE",25F);
 public static BulletConfig g40_he;
 public static BulletConfig g40_heat;
 public static BulletConfig g40_demo;
 public static BulletConfig g40_inc;
 public static BulletConfig g40_phosphorus;
 public static void initConfig(){}
 public static void init(){
g40_he = new BulletConfig("hbm:40mm/g40_he").setLife(200).setVel(2F).setGrav(0.035D).setItem(Grenade40Ammo.G40_HE).setOnImpact(Grenade40Impacts::standard).setCasing(new SpentCasing(CasingType.STRAIGHT).setColor(0x777777).setScale(2, 2F, 1.5F).register("g40"));
g40_heat = new BulletConfig("hbm:40mm/g40_heat").setLife(200).setVel(2F).setGrav(0.035D).setItem(Grenade40Ammo.G40_HEAT).setOnImpact(Grenade40Impacts::heat).setDamage(0.5F).setCasing(new SpentCasing(CasingType.STRAIGHT).setColor(0x5E6854).setScale(2, 2F, 1.5F).register("g40heat"));
g40_demo = new BulletConfig("hbm:40mm/g40_demo").setLife(200).setVel(2F).setGrav(0.035D).setItem(Grenade40Ammo.G40_DEMO).setOnImpact(Grenade40Impacts::demolition).setDamage(0.75F).setCasing(new SpentCasing(CasingType.STRAIGHT).setColor(0xE30000).setScale(2, 2F, 1.5F).register("g40demo"));
g40_inc = new BulletConfig("hbm:40mm/g40_inc").setLife(200).setVel(2F).setGrav(0.035D).setItem(Grenade40Ammo.G40_INC).setOnImpact(Grenade40Impacts::incendiary).setDamage(0.75F).setCasing(new SpentCasing(CasingType.STRAIGHT).setColor(0xE86F20).setScale(2, 2F, 1.5F).register("g40inc"));
g40_phosphorus = new BulletConfig("hbm:40mm/g40_phosphorus").setLife(200).setVel(2F).setGrav(0.035D).setItem(Grenade40Ammo.G40_PHOSPHORUS).setOnImpact(Grenade40Impacts::phosphorus).setDamage(0.75F).setCasing(new SpentCasing(CasingType.STRAIGHT).setColor(0xC8C8C8).setScale(2, 2F, 1.5F).register("g40phos"));
Grenade40Guns.gun_congolake = new ItemGunBaseNT(WeaponQuality.A_SIDE, new GunConfig()
				.dura(400).draw(7).inspect(39).reloadSequential(true).reloadChangeType(true).crosshair(Crosshair.L_CIRCUMFLEX).smoke(LAMBDA_SMOKE)
				.rec(new Receiver(0)
						.dmg(GUN_CONGO_LAKE_DAMAGE).delay(24).reload(16, 16, 16, 0).jam(0).sound(Grenade40Sounds.GUN_CONGO_FIRE, 1.0F, 1.0F)
						.mag(new MagazineSingleReload(0, 4).addConfigs(g40_he, g40_heat, g40_demo, g40_inc, g40_phosphorus))
						.offset(0.75, -0.0625, -0.1875D)
						.setupStandardFire().recoil(LAMBDA_RECOIL_GL))
				.setupStandardConfiguration()
				.anim(LAMBDA_CONGOLAKE_ANIMS).orchestra(Grenade40Orchestras.ORCHESTRA_CONGOLAKE)
				).setDefaultAmmo(Grenade40Ammo.G40_HE, 8);
Grenade40Guns.gun_mk108 = new ItemGunBaseNT(WeaponQuality.A_SIDE, new GunConfig()
				.dura(5_000).draw(20).inspect(65).crosshair(Crosshair.L_CIRCUMFLEX).hideCrosshair(false)
				.rec(new Receiver(0)
						.dmg(GUN_MK108_DAMAGE).delay(10).auto(true).dryfireAfterAuto(true).reload(135).jam(25).sound(Grenade40Sounds.GUN_MK108_FIRE, 1.0F, 1.0F)
						.mag(new MagazineFullReload(0, 30).addConfigs(g40_he, g40_heat, g40_demo, g40_inc, g40_phosphorus))
						.offset(0.75, -0.125, -0.125)
						.setupStandardFire().recoil(LAMBDA_RECOIL_MK108))
				.setupStandardConfiguration()
				.anim(LAMBDA_MK108_ANIMS).orchestra(Grenade40Orchestras.ORCHESTRA_MK108)
				).setDefaultAmmo(Grenade40Ammo.G40_HE, 50);
}
	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_SMOKE = (stack, ctx) -> {
		Lego.handleStandardSmoke(ctx.entity, stack, 1500, 0.025D, 1.05D, 0);
	};
	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_RECOIL_GL = (stack, ctx) -> {
		ItemGunBaseNT.setupRecoil(10, (float) (ctx.getPlayer().getRandom().nextGaussian() * 1.5));
	};
	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_RECOIL_MK108 = (stack, ctx) -> {
		ItemGunBaseNT.setupRecoil((float) (ctx.getPlayer().getRandom().nextGaussian() * 1.0) + 1F, (float) (ctx.getPlayer().getRandom().nextGaussian()));
	};
	@SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_CONGOLAKE_ANIMS = (stack, type) -> {
		int ammo = ((ItemGunBaseNT) stack.getItem()).getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack).getAmount(stack, (ItemGunBaseNT.CLIENT==null||ItemGunBaseNT.CLIENT.localPlayer()==null?null:new GunInventory(ItemGunBaseNT.CLIENT.localPlayer())));
		switch(type) {
		case EQUIP: return com.hbm.client.weapon.pistols.PistolAnimationAssets.get("congolake","Equip");
		case CYCLE: return com.hbm.client.weapon.pistols.PistolAnimationAssets.get("congolake",ammo <= 1 ? "FireEmpty" : "Fire");
		case RELOAD: return com.hbm.client.weapon.pistols.PistolAnimationAssets.get("congolake",ammo == 0 ? "ReloadEmpty": "ReloadStart");
		case RELOAD_CYCLE: return com.hbm.client.weapon.pistols.PistolAnimationAssets.get("congolake","Reload");
		case RELOAD_END: return com.hbm.client.weapon.pistols.PistolAnimationAssets.get("congolake","ReloadEnd");
		case JAMMED: return com.hbm.client.weapon.pistols.PistolAnimationAssets.get("congolake","Jammed");
		case INSPECT: return com.hbm.client.weapon.pistols.PistolAnimationAssets.get("congolake","Inspect");
		}

		return null;
	};
	@SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_MK108_ANIMS = (stack, type) -> {
		switch(type) {
		case EQUIP: return new BusAnimation()
				.addBus("EQUIP", new BusAnimationSequence().setPos(45, 0, 0).addPos(0, 0, 0, 1000, IType.SIN_DOWN));
		case CYCLE:
			int amount = ((ItemGunBaseNT) stack.getItem()).getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack).getAmount(stack, null);
			return new BusAnimation()
				.addBus("RECOIL", new BusAnimationSequence().hold(50).addPos(0, 0, -0.25, 100, IType.SIN_DOWN).addPos(0, 0, 0, 150, IType.SIN_FULL))
				.addBus("BARREL", new BusAnimationSequence().addPos(0, 0, -1, 100, IType.SIN_DOWN).addPos(0, 0, 0, 250, IType.SIN_FULL))
				.addBus("CYCLE", new BusAnimationSequence().addPos(0, 0, 0, 100).addPos(1, 0, 0, 150))
				.addBus("SHELLS", new BusAnimationSequence().setPos(amount - 1, 0, 0));
		case CYCLE_DRY: return new BusAnimation()
				.addBus("HAMMER", new BusAnimationSequence().addPos(15, 0, 0, 50).addPos(15, 0, 0, 550).addPos(0, 0, 0, 100));
		case RELOAD: return new BusAnimation()
				.addBus("LIFT", new BusAnimationSequence().addPos(10, 0, 0, 500, IType.SIN_FULL).holdUntil(1250).addPos(-50, 0, 0, 750, IType.SIN_FULL).holdUntil(5500).addPos(0, 0, 0, 500, IType.SIN_FULL).hold(500).addPos(1, 0, 0, 100, IType.SIN_UP).addPos(0, 0, 0, 150, IType.SIN_FULL))
				.addBus("LID", new BusAnimationSequence().addPos(60, 0, 0, 500, IType.SIN_FULL).holdUntil(6000).addPos(0, 0, 0, 500, IType.SIN_UP))
				.addBus("BELT", new BusAnimationSequence().setPos(1, 0, 0).hold(500).addPos(0, 0, 0, 750, IType.SIN_UP).holdUntil(4500).addPos(1, 0, 0, 750, IType.SIN_UP))
				.addBus("DRUM", new BusAnimationSequence().hold(2000).addPos(2.5, 0, 0, 500, IType.SIN_DOWN).addPos(2.5, -2, -8, 500, IType.SIN_UP).setPos(4, -3, -8).addPos(2.5, 0, 0, 1000, IType.SIN_FULL).addPos(0, 0, 0, 500, IType.SIN_UP));
		case JAMMED: return new BusAnimation()
				.addBus("LID", new BusAnimationSequence().hold(250).addPos(45, 0, 0, 500, IType.SIN_FULL).addPos(0, 0, 0, 250, IType.SIN_UP))
				.addBus("LIFT", new BusAnimationSequence().hold(1000).addPos(1, 0, 0, 100, IType.SIN_UP).addPos(0, 0, 0, 150, IType.SIN_FULL));
		case INSPECT:
			int yeetHorizontal = 750;
			int untilImpact = yeetHorizontal * 9 / 15;
			int delay = 250;
			int height = 6;
			int arcUp = untilImpact * 5 / 8;
			int arcDown = untilImpact * 3 / 8;
			return new BusAnimation()
					.addBus("LIFT", new BusAnimationSequence().hold(untilImpact).addPos(1, 0, 0, 50, IType.SIN_UP).addPos(0, 0, 0, 100, IType.SIN_FULL).hold(delay - 150).addPos(1, 0, 0, 50, IType.SIN_UP).addPos(0, 0, 0, 100, IType.SIN_FULL).hold(delay - 150).addPos(1, 0, 0, 50, IType.SIN_UP).addPos(0, 0, 0, 100, IType.SIN_FULL))
					.addBus("GRENH1", new BusAnimationSequence().setPos(9, 0, 0).addPos(-6, 0, 0, yeetHorizontal))
					.addBus("GRENV1", new BusAnimationSequence().setPos(0, -2, 0).addPos(0, height, 0, arcUp, IType.SIN_DOWN).addPos(0, 2, 0, arcDown, IType.SIN_UP).addPos(0, 3, 0, yeetHorizontal - untilImpact, IType.SIN_DOWN))
					.addBus("GRENS1", new BusAnimationSequence().addPos(360 * 2, 0, 0, untilImpact).setPos(0, 0, 0).addPos(360 * 1, 0, 0, yeetHorizontal - untilImpact))
					.addBus("GRENH2", new BusAnimationSequence().setPos(9, 0, 0).hold(delay).addPos(-6, 0, 0, yeetHorizontal))
					.addBus("GRENV2", new BusAnimationSequence().setPos(0, -2, 0).hold(delay).addPos(0, height, 0, arcUp, IType.SIN_DOWN).addPos(0, 2, 0, arcDown, IType.SIN_UP).addPos(0, 3, 0, yeetHorizontal - untilImpact, IType.SIN_DOWN))
					.addBus("GRENS2", new BusAnimationSequence().hold(delay).addPos(360 * 2, 0, 0, untilImpact).setPos(0, 0, 0).addPos(360 * 1, 0, 0, yeetHorizontal - untilImpact))
					.addBus("GRENH3", new BusAnimationSequence().setPos(9, 0, 0).hold(delay * 2).addPos(-6, 0, 0, yeetHorizontal))
					.addBus("GRENV3", new BusAnimationSequence().setPos(0, -2, 0).hold(delay * 2).addPos(0, height, 0, arcUp, IType.SIN_DOWN).addPos(0, 2, 0, arcDown, IType.SIN_UP).addPos(0, 3, 0, yeetHorizontal - untilImpact, IType.SIN_DOWN))
					.addBus("GRENS3", new BusAnimationSequence().hold(delay * 2).addPos(360 * 2, 0, 0, untilImpact).setPos(0, 0, 0).addPos(360 * 1, 0, 0, yeetHorizontal - untilImpact));
		}
		return null;
	};
}
