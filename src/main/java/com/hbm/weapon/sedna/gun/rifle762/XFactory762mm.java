package com.hbm.weapon.sedna.gun.rifle762;
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
import com.hbm.weapon.sedna.gun.pistols.PistolAmmo;
import com.hbm.weapon.sedna.gun.rifle.RifleBallistics;
public class XFactory762mm {

	public static Supplier<Float> GUN_CARBINE_DAMAGE=Rifle762Settings.value("GUN_CARBINE_DAMAGE",15F);
	public static Supplier<Float> GUN_CARBINE_BAYONET_DAMAGE=Rifle762Settings.value("GUN_CARBINE_BAYONET_DAMAGE",15F);
	public static Supplier<Float> GUN_MINIGUN_DAMAGE=Rifle762Settings.value("GUN_MINIGUN_DAMAGE",6F);
	public static Supplier<Float> GUN_LACUNAE_DAMAGE=Rifle762Settings.value("GUN_LACUNAE_DAMAGE",12F);
	public static Supplier<Float> GUN_MINIGUN_DUAL_LEFT_DAMAGE=Rifle762Settings.value("GUN_MINIGUN_DUAL_LEFT_DAMAGE",6F);
	public static Supplier<Float> GUN_MINIGUN_DUAL_RIGHT_DAMAGE=Rifle762Settings.value("GUN_MINIGUN_DUAL_RIGHT_DAMAGE",6F);
	public static Supplier<Float> GUN_SOUTH_STAR_DAMAGE=Rifle762Settings.value("GUN_SOUTH_STAR_DAMAGE",30F);
	public static Supplier<Float> GUN_SOUTH_STAR_BAYONET_DAMAGE=Rifle762Settings.value("GUN_SOUTH_STAR_BAYONET_DAMAGE",15F);

	public static BulletConfig r762_sp;
	public static BulletConfig r762_fmj;
	public static BulletConfig r762_jhp;
	public static BulletConfig r762_ap;
	public static BulletConfig r762_du;
	public static BulletConfig r762_he;

	public static BulletConfig energy_lacunae;
	public static BulletConfig energy_lacunae_overcharge;
	public static BulletConfig energy_lacunae_ir;
	
	public static void initConfig(){}

public static BiConsumer<com.hbm.entity.projectile.EntityBulletBaseMK4,HitResult> LAMBDA_TINY_EXPLODE=(bullet,hit)->{if(hit instanceof EntityHitResult entity&&bullet.tickCount<3&&entity.getEntity()==bullet.getThrower())return;Lego.tinyExplode(bullet,hit,1.5F);bullet.setDead();};

	public static void init() {
        RifleBallistics.initialize();
        r762_sp=RifleBallistics.r762_sp;
        r762_fmj=RifleBallistics.r762_fmj;
        r762_jhp=RifleBallistics.r762_jhp;
        r762_ap=RifleBallistics.r762_ap;
        r762_du=RifleBallistics.r762_du;
        r762_he=RifleBallistics.r762_he;

		energy_lacunae = new BulletConfig("hbm:rifle762/energy_lacunae").setItem(PistolAmmo.CAPACITOR).setupDamageClass(DamageClass.LASER).setBeam().setReloadCount(40).setSpread(0.0F).setLife(5).setRenderRotations(false).setOnBeamImpact(BulletConfig.LAMBDA_STANDARD_BEAM_HIT);
		energy_lacunae_overcharge = new BulletConfig("hbm:rifle762/energy_lacunae_overcharge").setItem(PistolAmmo.CAPACITOR_OVERCHARGE).setupDamageClass(DamageClass.LASER).setBeam().setReloadCount(40).setSpread(0.0F).setLife(5).setRenderRotations(false).setDoesPenetrate(true).setOnBeamImpact(BulletConfig.LAMBDA_STANDARD_BEAM_HIT);
		energy_lacunae_ir = new BulletConfig("hbm:rifle762/energy_lacunae_ir").setItem(PistolAmmo.CAPACITOR_IR).setupDamageClass(DamageClass.FIRE).setBeam().setReloadCount(40).setSpread(0.0F).setLife(5).setRenderRotations(false).setOnBeamImpact(com.hbm.weapon.sedna.gun.energy.EnergyBullets.INFRARED);

		Rifle762Guns.gun_carbine = new ItemGunBaseNT(WeaponQuality.A_SIDE, new GunConfig()
				.dura(3_000).draw(10).inspect(31).reloadSequential(true).crosshair(Crosshair.CIRCLE).smoke(LAMBDA_SMOKE)
				.rec(new Receiver(0)
						.dmg(GUN_CARBINE_DAMAGE).delay(5).dry(15).spread(0.0F).reload(30, 0, 15, 0).jam(60).sound(Rifle762Sounds.GUN_POWDER_FIRE, 1.0F, 1.0F)
						.mag(new MagazineFullReload(0, 14).addConfigs(r762_sp, r762_fmj, r762_jhp, r762_ap, r762_du, r762_he))
						.offset(1, -0.0625 * 2.5, -0.25D)
						.setupStandardFire().recoil(LAMBDA_RECOIL_CARBINE))
				.setupStandardConfiguration()
				.anim(LAMBDA_CARBINE_ANIMS).orchestra(Rifle762Orchestras.ORCHESTRA_CARBINE)
				).setDefaultAmmo(PistolAmmo.R762_SP, 14);

		Rifle762Guns.gun_minigun = new ItemGunBaseNT(WeaponQuality.A_SIDE, new GunConfig()
				.dura(50_000).draw(20).inspect(20).crosshair(Crosshair.L_CIRCLE).smoke(LAMBDA_SMOKE)
				.rec(new Receiver(0)
						.dmg(GUN_MINIGUN_DAMAGE).delay(1).auto(true).dry(15).spread(0.01F).sound(Rifle762Sounds.GUN_MINIGUN_FIRE, 1.0F, 1.0F)
						.mag(new MagazineBelt().addConfigs(r762_sp, r762_fmj, r762_jhp, r762_ap, r762_du, r762_he))
						.offset(1, -0.0625 * 2.5, -0.25D)
						.setupStandardFire().recoil(LAMBDA_RECOIL_MINIGUN))
				.setupStandardConfiguration()
				.anim(LAMBDA_MINIGUN_ANIMS).orchestra(Rifle762Orchestras.ORCHESTRA_MINIGUN)
				).setDefaultAmmo(PistolAmmo.R762_FMJ, 30);
		Rifle762Guns.gun_minigun_lacunae = new ItemGunBaseNT(WeaponQuality.LEGENDARY, new GunConfig()
				.dura(50_000).draw(20).inspect(20).crosshair(Crosshair.L_CIRCLE)
				.rec(new Receiver(0)
						.dmg(GUN_LACUNAE_DAMAGE).delay(1).auto(true).dry(15).reload(15).spread(0.01F).sound(Rifle762Sounds.GUN_LASER_GATLING_FIRE, 1.0F, 1.0F)
						.mag(new MagazineFullReload(0, 200).addConfigs(energy_lacunae, energy_lacunae_overcharge, energy_lacunae_ir))
						.offset(1, -0.0625 * 2.5, -0.25D)
						.setupStandardFire().recoil(LAMBDA_RECOIL_LACUNAE))
				.setupStandardConfiguration()
				.anim(LAMBDA_MINIGUN_ANIMS).orchestra(Rifle762Orchestras.ORCHESTRA_MINIGUN)
				).setDefaultAmmo(PistolAmmo.CAPACITOR, 15);
		Rifle762Guns.gun_minigun_dual = new ItemGunBaseNT(WeaponQuality.DEBUG,
				new GunConfig()
				.dura(50_000).draw(20).inspect(20).crosshair(Crosshair.L_CIRCLE).smoke(LAMBDA_SMOKE)
				.rec(new Receiver(0)
						.dmg(GUN_MINIGUN_DUAL_LEFT_DAMAGE).delay(1).auto(true).dry(15).spread(0.01F).sound(Rifle762Sounds.GUN_MINIGUN_FIRE, 1.0F, 1.0F)
						.mag(new MagazineBelt().addConfigs(r762_sp, r762_fmj, r762_jhp, r762_ap, r762_du, r762_he))
						.offset(1, -0.0625 * 2.5, 0.25D)
						.setupStandardFire().recoil(LAMBDA_RECOIL_MINIGUN))
				.pp(Lego.LAMBDA_STANDARD_CLICK_PRIMARY).pr(Lego.LAMBDA_STANDARD_RELOAD)
				.decider(GunStateDecider.LAMBDA_STANDARD_DECIDER)
				.anim(LAMBDA_MINIGUN_ANIMS).orchestra(Rifle762Orchestras.ORCHESTRA_MINIGUN_DUAL),
				new GunConfig()
				.dura(50_000).draw(20).inspect(20).crosshair(Crosshair.L_CIRCLE).smoke(LAMBDA_SMOKE)
				.rec(new Receiver(0)
						.dmg(GUN_MINIGUN_DUAL_RIGHT_DAMAGE).delay(1).auto(true).dry(15).spread(0.01F).sound(Rifle762Sounds.GUN_MINIGUN_FIRE, 1.0F, 1.0F)
						.mag(new MagazineBelt().addConfigs(r762_sp, r762_fmj, r762_jhp, r762_ap, r762_du, r762_he))
						.offset(1, -0.0625 * 2.5, -0.25D)
						.setupStandardFire().recoil(LAMBDA_RECOIL_MINIGUN))
				.ps(Lego.LAMBDA_STANDARD_CLICK_PRIMARY).pr(Lego.LAMBDA_STANDARD_RELOAD)
				.decider(LAMBDA_SECOND_MINIGUN)
				.anim(LAMBDA_MINIGUN_ANIMS).orchestra(Rifle762Orchestras.ORCHESTRA_MINIGUN_DUAL)
				).setDefaultAmmo(PistolAmmo.R762_SP, 50);

		Rifle762Guns.gun_mas36 = new ItemGunBaseNT(WeaponQuality.LEGENDARY, new GunConfig()
				.dura(5_000).draw(20).inspect(31).reloadSequential(true).crosshair(Crosshair.CIRCLE).smoke(LAMBDA_SMOKE)
				.rec(new Receiver(0)
						.dmg(GUN_SOUTH_STAR_DAMAGE).delay(25).dry(25).spread(0.0F).reload(43).jam(43).sound(Rifle762Sounds.GUN_HEAVY_RIFLE_FIRE, 1.0F, 1.0F)
						.mag(new MagazineFullReload(0, 7).addConfigs(r762_sp, r762_fmj, r762_jhp, r762_ap, r762_du, r762_he))
						.offset(1, -0.0625 * 1.5, -0.25D)
						.setupStandardFire().recoil(LAMBDA_RECOIL_CARBINE))
				.setupStandardConfiguration()
				.anim(LAMBDA_MAS36_ANIMS).orchestra(Rifle762Orchestras.ORCHESTRA_MAS36)
				).setDefaultAmmo(PistolAmmo.R762_AP, 14);
	}

	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_SECOND_MINIGUN = (stack, ctx) -> {
		int index = ctx.configIndex;
		GunState lastState = ItemGunBaseNT.getState(stack, index);
		GunStateDecider.deciderStandardFinishDraw(stack, lastState, index);
		GunStateDecider.deciderStandardClearJam(stack, lastState, index);
		GunStateDecider.deciderStandardReload(stack, ctx, lastState, 0, index);
		GunStateDecider.deciderAutoRefire(stack, ctx, lastState, 0, index, () -> { return ItemGunBaseNT.getSecondary(stack, index) && ItemGunBaseNT.getMode(stack, ctx.configIndex) == 0; });
	};

	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_SMOKE = (stack, ctx) -> {
		Lego.handleStandardSmoke(ctx.entity, stack, 1500, 0.075D, 1.1D, 0);
	};

	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_RECOIL_CARBINE = (stack, ctx) -> {
		ItemGunBaseNT.setupRecoil(5, (float) (ctx.getPlayer().getRandom().nextGaussian() * 0.5));
	};

	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_RECOIL_MINIGUN = (stack, ctx) -> {
		ItemGunBaseNT.setupRecoil((float) (ctx.getPlayer().getRandom().nextGaussian() * 0.5), (float) (ctx.getPlayer().getRandom().nextGaussian() * 0.5));
	};

	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_RECOIL_LACUNAE = (stack, ctx) -> { };

	@SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_CARBINE_ANIMS = (stack, type) -> {
		int ammo = ((ItemGunBaseNT) stack.getItem()).getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack).getAmount(stack, (ItemGunBaseNT.CLIENT==null||ItemGunBaseNT.CLIENT.localPlayer()==null?null:new GunInventory(ItemGunBaseNT.CLIENT.localPlayer())));
		switch(type) {
		case EQUIP: return new BusAnimation()
				.addBus("EQUIP", new BusAnimationSequence().addPos(45, 0, 0, 0).addPos(0, 0, 0, 500, IType.SIN_FULL));
		case CYCLE: return new BusAnimation()
				.addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, ItemGunBaseNT.getIsAiming(stack) ? -0.25 : -0.5, 50, IType.SIN_DOWN).addPos(0, 0, 0, 100, IType.SIN_FULL))
				.addBus("SLIDE", new BusAnimationSequence().addPos(0, 0, -1, 50, IType.SIN_DOWN).addPos(0, 0, 0, 100, IType.SIN_UP))
				.addBus(ammo <= 1 ? "NULL" : "REL", new BusAnimationSequence().addPos(0, 0, 0.25, 50).addPos(0, 0.125, 1.25, 100, IType.SIN_UP));
		case CYCLE_DRY: return new BusAnimation()
				.addBus("SLIDE", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(0, 0, -1, 100, IType.SIN_DOWN).addPos(0, 0, -1, 50).addPos(0, 0, 0, 100, IType.SIN_UP));
		case RELOAD: return new BusAnimation()
				.addBus("MAG", new BusAnimationSequence().addPos(0, -4, 0, 250, IType.SIN_UP).addPos(0, -4, 0, 750).addPos(0, 0, 0, 500, IType.SIN_DOWN))
				.addBus("LIFT", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(-25, 0, 0, 250, IType.SIN_FULL).addPos(-25, 0, 0, 1000))
				.addBus("BULLET", new BusAnimationSequence().addPos(ammo == 0 ? 1 : 0, 0, 0, 0).addPos(0, 0, 0, 1000));
		case RELOAD_END: return new BusAnimation()
				.addBus("LIFT", new BusAnimationSequence().addPos(-25, 0, 0, 0).addPos(-25, 0, 0, 750).addPos(0, 0, 0, 500, IType.SIN_FULL))
				.addBus("SLIDE", new BusAnimationSequence().addPos(0, 0, 0, 250).addPos(0, 0, -1, 100, IType.SIN_DOWN).addPos(0, 0, -1, 50).addPos(0, 0, 0, 100, IType.SIN_UP))
				.addBus("REL", new BusAnimationSequence().addPos(0, 0, 0, 250).addPos(0, 0, 0.25, 150).addPos(0, 0.125, 1.25, 100, IType.SIN_UP));
		case JAMMED: return new BusAnimation()
				.addBus("LIFT", new BusAnimationSequence().addPos(-25, 0, 0, 0).addPos(-25, 0, 0, 750).addPos(0, 0, 0, 500, IType.SIN_FULL).addPos(0, 0, 0, 250).addPos(-25, 0, 0, 250, IType.SIN_FULL).addPos(-25, 0, 0, 750).addPos(0, 0, 0, 500, IType.SIN_FULL))
				.addBus("SLIDE", new BusAnimationSequence().addPos(0, 0, 0, 250).addPos(0, 0, -1, 100, IType.SIN_DOWN).addPos(0, 0, -1, 50).addPos(0, 0, -0.25, 100, IType.SIN_UP).addPos(0, 0, -0.25, 1250).addPos(0, 0, -1, 100, IType.SIN_DOWN).addPos(0, 0, -1, 50).addPos(0, 0, 0, 100, IType.SIN_UP))
				.addBus("REL", new BusAnimationSequence().addPos(0, 0, 0, 250).addPos(0, 0, 0.25, 150).addPos(0, 0.125, 1, 100, IType.SIN_UP).addPos(0, 0.125, 1, 1250).addPos(0, 0.125, 0.25, 100, IType.SIN_DOWN).addPos(0, 0.125, 1, 100, IType.SIN_UP));
		case INSPECT: return new BusAnimation()
				.addBus("LIFT", new BusAnimationSequence().addPos(-25, 0, 0, 250, IType.SIN_FULL).addPos(-25, 0, 0, 1500).addPos(0, 0, 0, 500, IType.SIN_FULL))
				.addBus("SLIDE", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(0, 0, -0.75, 150, IType.SIN_DOWN).addPos(0, 0, -0.75, 1000).addPos(0, 0, 0, 100, IType.SIN_UP))
				.addBus(ammo == 0 ? "NULL" : "REL", new BusAnimationSequence().addPos(0, 0.125, 1.25, 0).addPos(0, 0.125, 1.25, 500).addPos(0, 0.125, 0.5, 150, IType.SIN_DOWN).addPos(0, 0.125, 0.5, 1000).addPos(0, 0.125, 1.25, 100, IType.SIN_UP));
		}

		return null;
	};

	@SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_MINIGUN_ANIMS = (stack, type) -> {
		switch(type) {
		case EQUIP: return new BusAnimation()
				.addBus("EQUIP", new BusAnimationSequence().addPos(45, 0, 0, 0).addPos(0, 0, 0, 1000, IType.SIN_FULL));
		case CYCLE: return new BusAnimation()
				.addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, ItemGunBaseNT.getIsAiming(stack) ? -0.25 : -0.5, 0).addPos(0, 0, ItemGunBaseNT.getIsAiming(stack) ? -0.25 : -0.5, 100).addPos(0, 0, 0, 150, IType.SIN_FULL))
				.addBus("ROTATE", new BusAnimationSequence().addPos(0, 0, 60, 50).addPos(0, 0, 720, 1000, IType.SIN_DOWN));
		case CYCLE_DRY: return new BusAnimation()
				.addBus("ROTATE", new BusAnimationSequence().addPos(0, 0, 60, 50).addPos(0, 0, 720, 1000, IType.SIN_DOWN));
		case RELOAD: return new BusAnimation()
				.addBus("EQUIP", new BusAnimationSequence().addPos(-15, 0, 0, 250, IType.SIN_DOWN).addPos(0, 0, 0, 500, IType.SIN_FULL))
				.addBus("ROTATE", new BusAnimationSequence().addPos(0, 0, 60, 50).addPos(0, 0, 720, 1000, IType.SIN_DOWN));
		case INSPECT: return new BusAnimation()
				.addBus("EQUIP", new BusAnimationSequence().addPos(3, 0, 0, 150, IType.SIN_DOWN).addPos(0, 0, 0, 100, IType.SIN_FULL))
				.addBus("ROTATE", new BusAnimationSequence().addPos(0, 0, -720, 1000, IType.SIN_DOWN));
		}

		return null;
	};

	@SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_MAS36_ANIMS = (stack, type) -> {
		int mag = ((ItemGunBaseNT) stack.getItem()).getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack).getAmount(stack, (ItemGunBaseNT.CLIENT==null||ItemGunBaseNT.CLIENT.localPlayer()==null?null:new GunInventory(ItemGunBaseNT.CLIENT.localPlayer())));
		double turn = -90;
		double pullAmount = ItemGunBaseNT.getIsAiming(stack) ? -1F : -1.5D;
		switch(type) {
		case EQUIP: return new BusAnimation()
				.addBus("STOCK", new BusAnimationSequence().setPos(-158, 0, 0).hold(500).addPos(0, 0, 0, 500, IType.SIN_FULL))
				.addBus("EQUIP", new BusAnimationSequence().setPos(45, 0, 0).addPos(0, 0, 0, 500, IType.SIN_FULL).hold(500).addPos(1, 0, 0, 100, IType.SIN_DOWN).addPos(0, 0, 0, 100, IType.SIN_FULL));
		case CYCLE: return new BusAnimation()
				.addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, -0.5, 50, IType.SIN_DOWN).addPos(0, 0, 0, 100, IType.SIN_FULL))
				.addBus("BOLT_TURN", new BusAnimationSequence().hold(250).addPos(0, 0, turn, 150).hold(700).addPos(0, 0, 0, 150))
				.addBus("BOLT_PULL", new BusAnimationSequence().hold(350).addPos(0, 0, pullAmount, 250, IType.SIN_UP).hold(250).addPos(0, 0, 0, 200, IType.LINEAR))
				.addBus("LIFT", new BusAnimationSequence().hold(600).addPos(-3, 0, 0, 150, IType.SIN_DOWN).hold(300).addPos(0, 0, 0, 250, IType.SIN_FULL))
				.addBus("BULLET", mag <= 1 ? new BusAnimationSequence().setPos(-100, 0, 0) : new BusAnimationSequence().hold(850).addPos(0, 0.1875, 1.5, 200, IType.LINEAR));
		case CYCLE_DRY: return new BusAnimation()
				.addBus("BOLT_TURN", new BusAnimationSequence().hold(250).addPos(0, 0, turn, 150).hold(700).addPos(0, 0, 0, 150))
				.addBus("BOLT_PULL", new BusAnimationSequence().hold(350).addPos(0, 0, pullAmount, 250, IType.SIN_UP).hold(250).addPos(0, 0, 0, 200, IType.LINEAR))
				.addBus("LIFT", new BusAnimationSequence().hold(600).addPos(-3, 0, 0, 150, IType.SIN_DOWN).hold(300).addPos(0, 0, 0, 250, IType.SIN_FULL))
				.addBus("BULLET", new BusAnimationSequence().setPos(-100, 0, 0));
		case RELOAD: return new BusAnimation()
				.addBus("BOLT_TURN", new BusAnimationSequence().addPos(0, 0, turn, 150).holdUntil(2000).addPos(0, 0, 0, 150))
				.addBus("BOLT_PULL", new BusAnimationSequence().hold(100).addPos(0, 0, -1.5D, 250, IType.SIN_UP).holdUntil(1800).addPos(0, 0, 0, 200, IType.LINEAR))
				.addBus("BULLET", new BusAnimationSequence().setPos(-100, 0, 0).holdUntil(1200).setPos(0, 0, 0).hold(600).addPos(0, 0.1875, 1.5, 200, IType.LINEAR))
				.addBus("LIFT", new BusAnimationSequence().hold(200).addPos(30, 0, 0, 500, IType.SIN_FULL).holdUntil(1200).addPos(0, 0, 0, 500, IType.SIN_FULL))
				.addBus("SHOW_CLIP", new BusAnimationSequence().setPos(1, 1, 1))
				.addBus("CLIP", new BusAnimationSequence().setPos(2, -3, 0).hold(250).addPos(0.5, 1, 0, 500, IType.SIN_DOWN).addPos(0, 0, 0, 250, IType.SIN_FULL).hold(400).addPos(-0.5, 0.5, 0, 150).addPos(-3, -3, 0, 250, IType.SIN_UP))
				.addBus("BULLETS", new BusAnimationSequence().setPos(2, -3, 0).hold(250).addPos(0.5, 1, 0, 500, IType.SIN_DOWN).addPos(0, 0, 0, 250, IType.SIN_FULL).hold(150).addPos(0, -1.5, 0, 250, IType.SIN_DOWN));
		case JAMMED: return new BusAnimation()
				.addBus("LIFT", new BusAnimationSequence().hold(250).addPos(-15, 0, 0, 500, IType.SIN_FULL).holdUntil(1650).addPos(0, 0, 0, 500, IType.SIN_FULL))
				.addBus("BOLT_TURN", new BusAnimationSequence().hold(250).addPos(0, 0, turn, 150).holdUntil(1250).addPos(0, 0, 0, 150))
				.addBus("BOLT_PULL", new BusAnimationSequence().hold(350).addPos(0, 0, pullAmount, 250, IType.SIN_UP).addPos(0, 0, 0, 200, IType.LINEAR).addPos(0, 0, pullAmount, 250, IType.SIN_UP).addPos(0, 0, 0, 200, IType.LINEAR));
		case INSPECT: return new BusAnimation()
				.addBus("LIFT", new BusAnimationSequence().hold(350).addPos(-3, 0, 0, 150, IType.SIN_DOWN).holdUntil(1050).addPos(0, 0, 0, 250, IType.SIN_FULL))
				.addBus("BOLT_TURN", new BusAnimationSequence().addPos(0, 0, turn, 150).holdUntil(1050).addPos(0, 0, 0, 150))
				.addBus("BOLT_PULL", new BusAnimationSequence().hold(100).addPos(0, 0, -1D, 250, IType.SIN_UP).hold(500).addPos(0, 0, 0, 200, IType.LINEAR))
				.addBus("BULLET", mag == 0 ? new BusAnimationSequence().setPos(-100, 0, 0) : new BusAnimationSequence().setPos(0, 0.1875, 1.5).hold(100).addPos(0, 0.125, 0.5, 250, IType.SIN_UP).hold(500).addPos(0, 0.1875, 1.5, 200, IType.LINEAR));
		}

		return null;
	};
}
