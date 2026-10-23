package com.hbm.weapon.sedna.gun.rifle556;
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
public class XFactory556mm {

	public static final ResourceLocation scope = ResourceLocation.parse("hbm:textures/misc/scope_bolt.png");

	public static Supplier<Float> GUN_ASSAULT_RIFLE_DAMAGE=Rifle556Settings.value("GUN_ASSAULT_RIFLE_DAMAGE",5F);
	public static Supplier<Float> GUN_ZEBRA_DAMAGE=Rifle556Settings.value("GUN_ZEBRA_DAMAGE",7.5F);
	public static Supplier<Float> GUN_STG77_DAMAGE=Rifle556Settings.value("GUN_STG77_DAMAGE",10F);

	public static BulletConfig r556_sp;
	public static BulletConfig r556_fmj;
	public static BulletConfig r556_jhp;
	public static BulletConfig r556_ap;

	public static BulletConfig r556_inc_sp;
	public static BulletConfig r556_inc_fmj;
	public static BulletConfig r556_inc_jhp;
	public static BulletConfig r556_inc_ap;
	
	public static void initConfig(){}

 public static BiConsumer<com.hbm.entity.projectile.EntityBulletBaseMK4,HitResult> INCENDIARY=(bullet,hit)->{if(hit instanceof EntityHitResult entity&&entity.getEntity() instanceof LivingEntity living){var data=com.hbm.bomb.conventional.grenade.FireStatus.data(living);if(data.phosphorus<300)data.phosphorus=300;}};

	public static void init() {
        RifleBallistics.initialize();
        r556_sp=RifleBallistics.r556_sp;
        r556_fmj=RifleBallistics.r556_fmj;
        r556_jhp=RifleBallistics.r556_jhp;
        r556_ap=RifleBallistics.r556_ap;
        r556_inc_sp=RifleBallistics.r556_inc_sp;
        r556_inc_fmj=RifleBallistics.r556_inc_fmj;
        r556_inc_jhp=RifleBallistics.r556_inc_jhp;
        r556_inc_ap=RifleBallistics.r556_inc_ap;

		Rifle556Guns.gun_g3 = new ItemGunBaseNT(WeaponQuality.A_SIDE, new GunConfig()
				.dura(3_000).draw(10).inspect(33).crosshair(Crosshair.CIRCLE).smoke(LAMBDA_SMOKE)
				.rec(new Receiver(0)
						.dmg(5F).delay(2).auto(true).dry(15).reload(50).jam(47).sound(Rifle556Sounds.GUN_ASSAULT_FIRE, 1.0F, 1.0F)
						.mag(new MagazineFullReload(0, 30).addConfigs(r556_sp, r556_fmj, r556_jhp, r556_ap))
						.offset(1, -0.0625 * 2.5, -0.25D)
						.setupStandardFire().recoil(LAMBDA_RECOIL_G3))
				.setupStandardConfiguration().ps(Lego.LAMBDA_STANDARD_CLICK_SECONDARY)
				.anim(LAMBDA_G3_ANIMS).orchestra(Rifle556Orchestras.ORCHESTRA_G3)
				).setDefaultAmmo(PistolAmmo.R556_SP, 30).setNameMutator(LAMBDA_NAME_G3);
		Rifle556Guns.gun_g3_zebra = new ItemGunBaseNT(WeaponQuality.B_SIDE, new GunConfig()
				.dura(6_000).draw(10).inspect(33).crosshair(Crosshair.CIRCLE).smoke(LAMBDA_SMOKE).scopeTexture(scope)
				.rec(new Receiver(0)
						.dmg(GUN_ZEBRA_DAMAGE).delay(2).auto(true).dry(15).spreadHipfire(0.01F).reload(50).jam(47).sound(Rifle556Sounds.GUN_RIFLE_SILENCER, 1.0F, 1.0F)
						.mag(new MagazineFullReload(0, 30).addConfigs(r556_inc_sp, r556_inc_fmj, r556_inc_jhp, r556_inc_ap))
						.offset(1, -0.0625 * 2.5, -0.25D)
						.setupStandardFire().recoil(LAMBDA_RECOIL_ZEBRA))
				.setupStandardConfiguration().ps(Lego.LAMBDA_STANDARD_CLICK_SECONDARY)
				.anim(LAMBDA_G3_ANIMS).orchestra(Rifle556Orchestras.ORCHESTRA_G3)
				).setDefaultAmmo(PistolAmmo.R556_JHP, 30).setNameMutator(LAMBDA_NAME_G3);

		Rifle556Guns.gun_stg77 = new ItemGunBaseNT(WeaponQuality.A_SIDE, new GunConfig()
				.dura(3_000).draw(10).inspect(125).crosshair(Crosshair.CIRCLE).scopeTexture(scope).smoke(LAMBDA_SMOKE)
				.rec(new Receiver(0)
						.dmg(GUN_STG77_DAMAGE).delay(2).dry(15).auto(true).reload(46).jam(0).sound(Rifle556Sounds.GUN_ASSAULT_FIRE, 1.0F, 1.0F)
						.mag(new MagazineFullReload(0, 30).addConfigs(r556_sp, r556_fmj, r556_jhp, r556_ap))
						.offset(1, -0.0625 * 2.5, -0.25D)
						.setupStandardFire().recoil(LAMBDA_RECOIL_STG))
				.pp(Lego.LAMBDA_STANDARD_CLICK_PRIMARY).ps(Lego.LAMBDA_STANDARD_CLICK_PRIMARY).pr(Lego.LAMBDA_STANDARD_RELOAD).pt(Lego.LAMBDA_TOGGLE_AIM)
				.decider(LAMBDA_STG77_DECIDER)
				.anim(LAMBDA_STG77_ANIMS).orchestra(Rifle556Orchestras.ORCHESTRA_STG77)
				).setDefaultAmmo(PistolAmmo.R556_FMJ, 30);
	}

	public static Function<ItemStack, String> LAMBDA_NAME_G3 = (stack) -> {
		if(XWeaponModManager.hasUpgrade(stack, 0, 201) &&
				XWeaponModManager.hasUpgrade(stack, 0, 205) &&
				XWeaponModManager.hasUpgrade(stack, 0, 212) &&
				XWeaponModManager.hasUpgrade(stack, 0, 202)) return Rifle556Names.sourceKey(stack) + "_infiltrator";
		if(!XWeaponModManager.hasUpgrade(stack, 0, 205) &&
				XWeaponModManager.hasUpgrade(stack, 0, 211)) return Rifle556Names.sourceKey(stack) + "_a3";
		return null;
	};

	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_SMOKE = (stack, ctx) -> {
		Lego.handleStandardSmoke(ctx.entity, stack, 1500, 0.075D, 1.1D, 0);
	};

	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_STG77_DECIDER = (stack, ctx) -> {
		int index = ctx.configIndex;
		GunState lastState = ItemGunBaseNT.getState(stack, index);
		GunStateDecider.deciderStandardFinishDraw(stack, lastState, index);
		GunStateDecider.deciderStandardClearJam(stack, lastState, index);
		GunStateDecider.deciderStandardReload(stack, ctx, lastState, 0, index);
		GunStateDecider.deciderAutoRefire(stack, ctx, lastState, 0, index, () -> { return ItemGunBaseNT.getSecondary(stack, index); });
	};

	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_RECOIL_G3 = (stack, ctx) -> {
		ItemGunBaseNT.setupRecoil((float) (ctx.getPlayer().getRandom().nextGaussian() * 0.25), (float) (ctx.getPlayer().getRandom().nextGaussian() * 0.25));
	};

	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_RECOIL_ZEBRA = (stack, ctx) -> {
		ItemGunBaseNT.setupRecoil((float) (ctx.getPlayer().getRandom().nextGaussian() * 0.125), (float) (ctx.getPlayer().getRandom().nextGaussian() * 0.125));
	};

	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_RECOIL_STG = (stack, ctx) -> { };

	@SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_G3_ANIMS = (stack, type) -> {
		boolean empty = ((ItemGunBaseNT) stack.getItem()).getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack).getAmount(stack, (ItemGunBaseNT.CLIENT==null||ItemGunBaseNT.CLIENT.localPlayer()==null?null:new GunInventory(ItemGunBaseNT.CLIENT.localPlayer()))) <= 0;
		switch(type) {
		case EQUIP: return new BusAnimation()
				.addBus("EQUIP", new BusAnimationSequence().addPos(45, 0, 0, 0).addPos(0, 0, 0, 500, IType.SIN_FULL));
		case CYCLE: return new BusAnimation()
				.addBus("BOLT", new BusAnimationSequence().addPos(0, 0, 0, 20).addPos(0, 0, -4.5, 40).addPos(0, 0, 0, 40))
				.addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, (ItemGunBaseNT.getIsAiming(stack) || !XWeaponModManager.hasUpgrade(stack, 0, 205)) ? -0.25 : -0.75, 25, IType.SIN_DOWN).addPos(0, 0, 0, 75, IType.SIN_FULL));
		case CYCLE_DRY: return new BusAnimation()
				.addBus("BOLT", new BusAnimationSequence().addPos(0, 0, 0, 250).addPos(0, 0, -0.3125, 100).hold(25).addPos(0, 0, -2.75, 130).hold(50).addPos(0, 0, -2.4375, 50).addPos(0, 0, 0, 85))
				.addBus("PLUG", new BusAnimationSequence().addPos(0, 0, 0, 250).hold(125).addPos(0, 0, -2.4375, 130).hold(100).addPos(0, 0, 0, 85))
				.addBus("HANDLE", new BusAnimationSequence().addPos(0, 0, 0, 250).addPos(0, 90, 0, 100).hold(25).hold(180).addPos(0, 0, 0, 50))
				.addBus("LIFT", new BusAnimationSequence().addPos(0, 0, 0, 400).addPos(-1, 0, 0, 100, IType.SIN_DOWN).addPos(0, 0, 0, 100, IType.SIN_FULL));
		case RELOAD:
			return new BusAnimation()
				.addBus("MAG", new BusAnimationSequence()
						.addPos(0, -8, 0, 250, IType.SIN_UP)	//250
						.addPos(0, -8, 0, 1050)					//1300
						.addPos(0, 0, 0, 250))					//1550
				.addBus("BOLT", new BusAnimationSequence()
						.addPos(0, 0, 0, 200)					//200
						.addPos(0, 0, -0.3125, 100)				//300
						.hold(10)								//310
						.addPos(0, 0, -3.25, 200)				//510
						.holdUntil(1875)						//1875
						.addPos(0, 0, -2.9375, 50)				//1925
						.addPos(0, 0, 0, 100))					//2025
				.addBus("PLUG", new BusAnimationSequence()
						.addPos(0, 0, 0, 310)					//310
						.addPos(0, 0, -2.9375, 200)				//510
						.holdUntil(1925)						//1925
						.addPos(0, 0, 0, 100))					//2025
				.addBus("HANDLE", new BusAnimationSequence()
						.addPos(0, 0, 0, 200)					//200
						.addPos(0, 90, 0, 100)					//300
						.hold(210)								//510
						.addPos(0, 90, 45, 75)					//685
						.holdUntil(1775)						//1775
						.addPos(0, 90, 0, 100)					//1875
						.addPos(0, 0, 0, 50))					//1925
				.addBus("LIFT", new BusAnimationSequence()
						.addPos(0, 0, 0, 750)					//750
						.addPos(-25, 0, 0, 500, IType.SIN_FULL)	//1250
						.holdUntil(1550)						//1550
						.addPos(-26, 0, 0, 100, IType.SIN_DOWN)	//1650
						.addPos(-25, 0, 0, 100, IType.SIN_FULL)	//1750
						.holdUntil(2000)						//2000
						.addPos(0, 0, 0, 500, IType.SIN_FULL))	//3500
				.addBus("BULLET", new BusAnimationSequence().addPos(empty ? 1 : 0, 0, 0, 0).addPos(0, 0, 0, 1000));
		case INSPECT: return new BusAnimation()
				.addBus("MAG", new BusAnimationSequence()
						.addPos(0, -1, 0, 150)					//150
						.addPos(2, -1, 0, 150)					//300
						.addPos(2, 8, 0, 350, IType.SIN_DOWN)	//650
						.addPos(2, -2, 0, 350, IType.SIN_UP)	//1000
						.addPos(2, -1, 0, 50)					//1050
						.addPos(2, -1, 0, 100)					//1150
						.addPos(0, -1, 0, 150, IType.SIN_FULL)	//1300
						.addPos(0, 0, 0, 150, IType.SIN_UP))	//1450
				.addBus("SPEEN", new BusAnimationSequence().addPos(0, 0, 0, 300).addPos(0, 360, 360, 700))
				.addBus("LIFT", new BusAnimationSequence().addPos(0, 0, 0, 1450).addPos(-2, 0, 0, 100, IType.SIN_DOWN).addPos(0, 0, 0, 100, IType.SIN_FULL))
				.addBus("BULLET", new BusAnimationSequence().addPos(empty ? 1 : 0, 0, 0, 0));
		case JAMMED: return new BusAnimation()
				.addBus("LIFT", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(-25, 0, 0, 250, IType.SIN_FULL).addPos(-25, 0, 0, 1250).addPos(0, 0, 0, 350, IType.SIN_FULL))
				.addBus("BOLT", new BusAnimationSequence().addPos(0, 0, 0, 1000).addPos(0, 0, -3.25, 150).addPos(0, 0, 0, 100).addPos(0, 0, 0, 250).addPos(0, 0, -3.25, 150).addPos(0, 0, 0, 100))
				.addBus("PLUG", new BusAnimationSequence().addPos(0, 0, 0, 1000).addPos(0, 0, -3.25, 150).addPos(0, 0, 0, 100).addPos(0, 0, 0, 250).addPos(0, 0, -3.25, 150).addPos(0, 0, 0, 100));
		}

		return null;
	};

	@SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_STG77_ANIMS = (stack, type) -> {
		if(GunClientSettings.legacyAnimations()) {
			switch(type) {
			case EQUIP: return new BusAnimation()
					.addBus("EQUIP", new BusAnimationSequence().addPos(45, 0, 0, 0).addPos(0, 0, 0, 500, IType.SIN_FULL));
			case CYCLE: return new BusAnimation()
					.addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, ItemGunBaseNT.getIsAiming(stack) ? -0.125 : -0.375, 25, IType.SIN_DOWN).addPos(0, 0, 0, 75, IType.SIN_FULL))
					.addBus("SAFETY", new BusAnimationSequence().addPos(0.25, 0, 0, 0).addPos(0.25, 0, 0, 2000).addPos(0, 0, 0, 50));
			case CYCLE_DRY: return new BusAnimation()
					.addBus("BOLT", new BusAnimationSequence().addPos(0, 0, 0, 250).addPos(0, 0, -2, 150).addPos(0, 0, 0, 100, IType.SIN_UP))
					.addBus("SAFETY", new BusAnimationSequence().addPos(0.25, 0, 0, 0).addPos(0.25, 0, 0, 2000).addPos(0, 0, 0, 50));
			case RELOAD: return new BusAnimation()
					.addBus("BOLT", new BusAnimationSequence().addPos(0, 0, -2, 150).addPos(0, 0, -2, 1600).addPos(0, 0, 0, 100, IType.SIN_UP))
					.addBus("HANDLE", new BusAnimationSequence().addPos(0, 0, 0, 150).addPos(0, 0, 20, 50).addPos(0, 0, 20, 1500).addPos(0, 0, 0, 50))
					.addBus("LIFT", new BusAnimationSequence().addPos(0, 0, 0, 200).addPos(-2, 0, 0, 100, IType.SIN_DOWN).addPos(0, 0, 0, 100, IType.SIN_FULL));
			case INSPECT: return new BusAnimation()
					.addBus("BOLT", new BusAnimationSequence().addPos(0, 0, -2, 150).addPos(0, 0, -2, 6100).addPos(0, 0, 0, 100, IType.SIN_UP))
					.addBus("HANDLE", new BusAnimationSequence().addPos(0, 0, 0, 150).addPos(0, 0, 20, 50).addPos(0, 0, 20, 6000).addPos(0, 0, 0, 50))
					.addBus("INSPECT_LEVER", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(0, 0, -10, 100).addPos(0, 0, -10, 100).addPos(0, 0, 0, 100))
					.addBus("INSPECT_BARREL", new BusAnimationSequence().addPos(0, 0, 0, 600).addPos(0, 0, 20, 150).addPos(0, 0, 0, 400).addPos(0, 0, 0, 500).addPos(15, 0, 0, 500).addPos(15, 0, 0, 2000).addPos(0, 0, 0, 500).addPos(0, 0, 0, 500).addPos(0, 0, 20, 200).addPos(0, 0, 20, 400).addPos(0, 0, 0, 150))
					.addBus("INSPECT_MOVE", new BusAnimationSequence().addPos(0, 0, 0, 750).addPos(0, 0, 6, 1000).addPos(2, 0, 3, 500, IType.SIN_FULL).addPos(2, 0.75, 0, 500, IType.SIN_FULL).addPos(2, 0.75, 0, 1000).addPos(2, 0, 3, 500, IType.SIN_FULL).addPos(0, 0, 6, 500).addPos(0, 0, 0, 1000))
					.addBus("INSPECT_GUN", new BusAnimationSequence().addPos(0, 0, 0, 1750).addPos(15, 0, -70, 500, IType.SIN_FULL).addPos(15, 0, -70, 1500).addPos(0, 0, 0, 500, IType.SIN_FULL));
			}
		} else {
			switch(type) {
			case EQUIP: return new BusAnimation()
					.addBus("EQUIP", new BusAnimationSequence().addPos(45, 0, 0, 0).addPos(0, 0, 0, 500, IType.SIN_FULL));
			case CYCLE: return new BusAnimation()
					.addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, ItemGunBaseNT.getIsAiming(stack) ? -0.125 : -0.375, 25, IType.SIN_DOWN).addPos(0, 0, 0, 75, IType.SIN_FULL))
					.addBus("SAFETY", new BusAnimationSequence().addPos(0.25, 0, 0, 0).addPos(0.25, 0, 0, 2000).addPos(0, 0, 0, 50));
			case CYCLE_DRY: return com.hbm.client.weapon.pistols.PistolAnimationAssets.get("stg77","FireDry");
			case RELOAD: return com.hbm.client.weapon.pistols.PistolAnimationAssets.get("stg77","Reload");
			case INSPECT: return com.hbm.client.weapon.pistols.PistolAnimationAssets.get("stg77","Inspect");
			}
		}


		return null;
	};
}
