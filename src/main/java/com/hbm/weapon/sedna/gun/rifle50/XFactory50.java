package com.hbm.weapon.sedna.gun.rifle50;
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
import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.weapon.sedna.gun.pistols.PistolAmmo;
import com.hbm.weapon.sedna.gun.rifle.RifleBallistics;
public class XFactory50 {

	public static Supplier<Float> GUN_ANTI_MATERIEL_DAMAGE=Rifle50Settings.value("GUN_ANTI_MATERIEL_DAMAGE",30F);
	public static Supplier<Float> GUN_SUBTLETY_DAMAGE=Rifle50Settings.value("GUN_SUBTLETY_DAMAGE",50F);
	public static Supplier<Float> GUN_PENANCE_DAMAGE=Rifle50Settings.value("GUN_PENANCE_DAMAGE",45F);
	public static Supplier<Float> GUN_M2_DAMAGE=Rifle50Settings.value("GUN_M2_DAMAGE",7.5F);
	
	public static final ResourceLocation scope = ResourceLocation.parse("hbm:textures/misc/scope_amat.png");
	public static final ResourceLocation scope_thermal = ResourceLocation.parse("hbm:textures/misc/scope_penance.png");

	public static BulletConfig bmg50_sp;
	public static BulletConfig bmg50_fmj;
	public static BulletConfig bmg50_jhp;
	public static BulletConfig bmg50_ap;
	public static BulletConfig bmg50_du;
	public static BulletConfig bmg50_he;
	public static BulletConfig bmg50_sm;
	public static BulletConfig bmg50_black;
	public static BulletConfig bmg50_equestrian;
	
	public static void initConfig(){}

public static BiConsumer<com.hbm.entity.projectile.EntityBulletBaseMK4,HitResult> LAMBDA_BUILDING=(bullet,hit)->{var building=new EntityBuilding(bullet.level());building.setPos(hit.getLocation().add(0,50,0));bullet.level().addFreshEntity(building);bullet.setDead();};

public static BiConsumer<com.hbm.entity.projectile.EntityBulletBaseMK4,HitResult> LAMBDA_STANDARD_EXPLODE=(bullet,hit)->{if(hit instanceof EntityHitResult entity&&bullet.tickCount<3&&entity.getEntity()==bullet.getThrower())return;Lego.tinyExplode(bullet,hit,2F);bullet.setDead();};

	public static void init() {
		SpentCasing casing50 = new SpentCasing(CasingType.BOTTLENECK).setColor(SpentCasing.COLOR_CASE_BRASS).setScale(1.5F);
        RifleBallistics.initialize();
        bmg50_sp=RifleBallistics.bmg50_sp;
        bmg50_fmj=RifleBallistics.bmg50_fmj;
        bmg50_jhp=RifleBallistics.bmg50_jhp;
        bmg50_ap=RifleBallistics.bmg50_ap;
        bmg50_du=RifleBallistics.bmg50_du;
        bmg50_he=RifleBallistics.bmg50_he;
        bmg50_sm=RifleBallistics.bmg50_sm;

		bmg50_black = new BulletConfig("hbm:rifle50/bmg50_black").setItem(PistolAmmo.BMG50_BLACK).setWear(5F).setDoesPenetrate(true).setDamageFalloffByPen(false).setSpectral(true).setDamage(1.5F).setHeadshot(3F).setThresholdNegation(30F).setArmorPiercing(0.35F)
				.setCasing(casing50.clone().setColor(SpentCasing.COLOR_CASE_EQUESTRIAN).register("bmg50black"));
		bmg50_equestrian = new BulletConfig("hbm:rifle50/bmg50_equestrian").setItem(PistolAmmo.BMG50_EQUESTRIAN).setDamage(0F).setOnImpact(LAMBDA_BUILDING)
				.setCasing(casing50.clone().setColor(SpentCasing.COLOR_CASE_EQUESTRIAN).register("bmg50equestrian"));

		Rifle50Guns.gun_amat = new ItemGunBaseNT(WeaponQuality.A_SIDE, new GunConfig()
				.dura(350).draw(20).inspect(50).crosshair(Crosshair.CIRCLE).scopeTexture(scope).smoke(LAMBDA_SMOKE)
				.rec(new Receiver(0)
						.dmg(GUN_ANTI_MATERIEL_DAMAGE).delay(25).dry(25).spreadHipfire(0.05F).reload(51).jam(43).sound(Rifle50Sounds.GUN_AMAT_FIRE, 1.0F, 1.0F)
						.mag(new MagazineFullReload(0, 7).addConfigs(bmg50_sp, bmg50_fmj, bmg50_jhp, bmg50_ap, bmg50_du, bmg50_sm, bmg50_he))
						.offset(1, -0.0625 * 1.5, -0.25D)
						.setupStandardFire().recoil(LAMBDA_RECOIL_AMAT))
				.setupStandardConfiguration()
				.anim(LAMBDA_AMAT_ANIMS).orchestra(Rifle50Orchestras.ORCHESTRA_AMAT)
				).setDefaultAmmo(PistolAmmo.BMG50_SP, 7);
		Rifle50Guns.gun_amat_subtlety = new ItemGunBaseNT(WeaponQuality.LEGENDARY, new GunConfig()
				.dura(1_000).draw(20).inspect(50).crosshair(Crosshair.CIRCLE).scopeTexture(scope).smoke(LAMBDA_SMOKE)
				.rec(new Receiver(0)
						.dmg(GUN_SUBTLETY_DAMAGE).delay(25).dry(25).spreadHipfire(0.05F).reload(51).jam(43).sound(Rifle50Sounds.GUN_AMAT_FIRE, 1.0F, 1.0F)
						.mag(new MagazineFullReload(0, 7).addConfigs(bmg50_equestrian, bmg50_sp, bmg50_fmj, bmg50_jhp, bmg50_ap, bmg50_du, bmg50_sm, bmg50_he))
						.offset(1, -0.0625 * 1.5, -0.25D)
						.setupStandardFire().recoil(LAMBDA_RECOIL_AMAT))
				.setupStandardConfiguration()
				.anim(LAMBDA_AMAT_ANIMS).orchestra(Rifle50Orchestras.ORCHESTRA_AMAT)
				).setDefaultAmmo(PistolAmmo.BMG50_JHP, 7);
		Rifle50Guns.gun_amat_penance = new ItemGunBaseNT(WeaponQuality.LEGENDARY, new GunConfig()
				.dura(5_000).draw(20).inspect(50).crosshair(Crosshair.CIRCLE).scopeTexture(scope_thermal).thermalSights(true).smoke(LAMBDA_SMOKE)
				.rec(new Receiver(0)
						.dmg(GUN_PENANCE_DAMAGE).delay(25).dry(25).spreadHipfire(0F).reload(51).jam(43).sound(Rifle50Sounds.GUN_AMAT_SILENCER, 1.0F, 1.0F)
						.mag(new MagazineFullReload(0, 7).addConfigs(bmg50_sp, bmg50_fmj, bmg50_jhp, bmg50_ap, bmg50_du, bmg50_sm, bmg50_he, bmg50_black))
						.offset(1, -0.0625 * 1.5, -0.25D)
						.setupStandardFire().recoil(LAMBDA_RECOIL_AMAT))
				.setupStandardConfiguration()
				.anim(LAMBDA_AMAT_ANIMS).orchestra(Rifle50Orchestras.ORCHESTRA_AMAT)
				).setDefaultAmmo(PistolAmmo.BMG50_JHP, 7);

		Rifle50Guns.gun_m2 = new ItemGunBaseNT(WeaponQuality.A_SIDE, new GunConfig()
				.dura(3_000).draw(10).inspect(31).crosshair(Crosshair.L_CIRCLE).smoke(LAMBDA_SMOKE)
				.rec(new Receiver(0)
						.dmg(GUN_M2_DAMAGE).delay(2).dry(10).auto(true).spread(0.005F).sound(Rifle50Sounds.TURRET_50BMG, 1.0F, 1.0F)
						.mag(new MagazineBelt().addConfigs(bmg50_sp, bmg50_fmj, bmg50_jhp, bmg50_ap, bmg50_du, bmg50_he))
						.offset(1, -0.0625 * 2.5, -0.25D)
						.setupStandardFire().recoil(LAMBDA_RECOIL_M2))
				.setupStandardConfiguration()
				.anim(LAMBDA_M2_ANIMS).orchestra(Rifle50Orchestras.ORCHESTRA_M2)
				).setDefaultAmmo(PistolAmmo.BMG50_FMJ, 25);
	}

	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_SMOKE = (stack, ctx) -> {
		Lego.handleStandardSmoke(ctx.entity, stack, 2000, 0.05D, 1.1D, 0);
	};

	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_RECOIL_AMAT = (stack, ctx) -> {
		ItemGunBaseNT.setupRecoil(12.5F, (float) (ctx.getPlayer().getRandom().nextGaussian() * 1));
	};

	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_RECOIL_M2 = (stack, ctx) -> {
		ItemGunBaseNT.setupRecoil((float) (ctx.getPlayer().getRandom().nextGaussian() * 0.5), (float) (ctx.getPlayer().getRandom().nextGaussian() * 0.5));
	};

	@SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_AMAT_ANIMS = (stack, type) -> {
		double turn = -60;
		double pullAmount = -2.5;
		double side = 4;
		double down = -2;
		double detach = 0.5;
		double apex = 7;

		switch(type) {
		case EQUIP: return new BusAnimation()
				.addBus("EQUIP", new BusAnimationSequence().addPos(45, 0, 0, 0).addPos(0, 0, 0, 500, IType.SIN_FULL))
				.addBus("BIPOD", new BusAnimationSequence().hold(500).addPos(80, 0, 0, 350).addPos(80, 25, 0, 150));
		case CYCLE: return new BusAnimation()
				.addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, -0.5, 50, IType.SIN_DOWN).addPos(0, 0, 0, 100, IType.SIN_FULL))
				.addBus("BOLT_TURN", new BusAnimationSequence().hold(250).addPos(0, 0, turn, 150).hold(700).addPos(0, 0, 0, 150))
				.addBus("BOLT_PULL", new BusAnimationSequence().hold(350).addPos(0, 0, pullAmount, 250, IType.SIN_UP).hold(250).addPos(0, 0, 0, 200, IType.LINEAR))
				.addBus("LIFT", new BusAnimationSequence().hold(600).addPos(-3, 0, 0, 150, IType.SIN_DOWN).hold(300).addPos(0, 0, 0, 250, IType.SIN_FULL));
		case CYCLE_DRY: return new BusAnimation()
				.addBus("BOLT_TURN", new BusAnimationSequence().hold(250).addPos(0, 0, turn, 150).hold(700).addPos(0, 0, 0, 150))
				.addBus("BOLT_PULL", new BusAnimationSequence().hold(350).addPos(0, 0, pullAmount, 250, IType.SIN_UP).hold(250).addPos(0, 0, 0, 200, IType.LINEAR))
				.addBus("LIFT", new BusAnimationSequence().hold(600).addPos(-3, 0, 0, 150, IType.SIN_DOWN).hold(300).addPos(0, 0, 0, 250, IType.SIN_FULL));
		case RELOAD: return new BusAnimation()
				.addBus("MAG", new BusAnimationSequence().addPos(0, -10, 0, 350, IType.SIN_UP).addPos(0, 0, 0, 650, IType.SIN_UP))
				.addBus("LIFT", new BusAnimationSequence().hold(1000).addPos(-2, 0, 0, 150, IType.SIN_DOWN).addPos(0, 0, 0, 250, IType.SIN_FULL).hold(450).addPos(-3, 0, 0, 150, IType.SIN_DOWN).hold(300).addPos(0, 0, 0, 250, IType.SIN_FULL))
				.addBus("BOLT_TURN", new BusAnimationSequence().hold(1500).addPos(0, 0, turn, 150).hold(700).addPos(0, 0, 0, 150))
				.addBus("BOLT_PULL", new BusAnimationSequence().hold(1600).addPos(0, 0, pullAmount, 250, IType.SIN_UP).hold(250).addPos(0, 0, 0, 200, IType.LINEAR));
		case JAMMED: return new BusAnimation()
				.addBus("LIFT", new BusAnimationSequence().hold(250).addPos(-15, 0, 0, 500, IType.SIN_FULL).holdUntil(1650).addPos(0, 0, 0, 500, IType.SIN_FULL))
				.addBus("BOLT_TURN", new BusAnimationSequence().hold(250).addPos(0, 0, turn, 150).holdUntil(1250).addPos(0, 0, 0, 150))
				.addBus("BOLT_PULL", new BusAnimationSequence().hold(350).addPos(0, 0, pullAmount, 250, IType.SIN_UP).addPos(0, 0, 0, 200, IType.LINEAR).addPos(0, 0, pullAmount, 250, IType.SIN_UP).addPos(0, 0, 0, 200, IType.LINEAR));
		case INSPECT: return new BusAnimation()
				.addBus("SCOPE_THROW", new BusAnimationSequence().addPos(0, detach, 0, 100, IType.SIN_FULL).addPos(side, down, 0, 500, IType.SIN_FULL).addPos(side, down - 0.5, 0, 100).addPos(side, apex, 0, 350, IType.SIN_FULL).addPos(side, down - 0.5, 0, 350, IType.SIN_DOWN).addPos(side, down, 0, 100).hold(250).addPos(0, detach, 0, 500, IType.SIN_FULL).addPos(0, 0, 0, 250, IType.SIN_FULL))
				.addBus("SCOPE_SPIN", new BusAnimationSequence().hold(700).addPos(-360, 0, 0, 700));
		}

		return null;
	};

	@SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_M2_ANIMS = (stack, type) -> {
		switch(type) {
		case EQUIP: return new BusAnimation()
				.addBus("EQUIP", new BusAnimationSequence().addPos(80, 0, 0, 0).addPos(0, 0, 0, 500, IType.SIN_FULL));
		case CYCLE: return new BusAnimation()
				.addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, -0.25, 25).addPos(0, 0, 0, 75));
		}

		return null;
	};
}
