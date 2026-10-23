package com.hbm.weapon.sedna.gun.aberrator;
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
import com.hbm.entity.projectile.EntityBulletBeamBase;
public class XFactory35800 {

	public static Supplier<Float> GUN_ABERRATOR_DAMAGE=AberratorSettings.value("GUN_ABERRATOR_DAMAGE",100F);
	public static Supplier<Float> GUN_EOTT_LEFT_DAMAGE=AberratorSettings.value("GUN_EOTT_LEFT_DAMAGE",100F);
	public static Supplier<Float> GUN_EOTT_RIGHT_DAMAGE=AberratorSettings.value("GUN_EOTT_RIGHT_DAMAGE",100F);
	
	public static void initConfig(){}

	public static BulletConfig p35800;
	public static BulletConfig p35800_bl;

public static BiConsumer<EntityBulletBeamBase,HitResult> LAMBDA_BLACK_IMPACT=(bullet,hit)->{if(hit instanceof EntityHitResult entity&&entity.getEntity() instanceof LivingEntity living)com.hbm.bomb.conventional.grenade.FireStatus.data(living).blackFire+=200;if(hit instanceof BlockHitResult){var fire=new com.hbm.bomb.conventional.grenade.LingeringFire(bullet.level()).area(7.5F,2F).duration(200).type(4);fire.setPos(hit.getLocation());bullet.level().addFreshEntity(fire);}BulletConfig.LAMBDA_STANDARD_BEAM_HIT.accept(bullet,hit);};

	public static void init() {

		p35800 = new BulletConfig("hbm:aberrator/p35800").setItem(PistolAmmo.P35_800).setArmorPiercing(0.5F).setThresholdNegation(50F).setBeam().setSpread(0.0F).setLife(3).setRenderRotations(false)
				.setCasing(new SpentCasing(CasingType.STRAIGHT).setColor(0xCEB78E).register("35-800")).setOnBeamImpact(BulletConfig.LAMBDA_STANDARD_BEAM_HIT);
		p35800_bl = new BulletConfig("hbm:aberrator/p35800_bl").setItem(PistolAmmo.P35_800_BL).setArmorPiercing(0.5F).setThresholdNegation(50F).setBeam().setSpread(0.0F).setLife(3).setRenderRotations(false)
				.setCasing(new SpentCasing(CasingType.STRAIGHT).setColor(0xCEB78E).register("35-800")).setOnBeamImpact(LAMBDA_BLACK_IMPACT);

		AberratorGuns.gun_aberrator = new ItemGunBaseNT(WeaponQuality.SECRET, new GunConfig()
				.dura(2_000).draw(10).inspect(26).crosshair(Crosshair.CIRCLE).smoke(Lego.LAMBDA_STANDARD_SMOKE)
				.rec(new Receiver(0)
						.dmg(GUN_ABERRATOR_DAMAGE).delay(13).dry(21).reload(51).sound(AberratorSounds.GUN_ABERRATOR_FIRE, 1.0F, 1.0F)
						.mag(new MagazineFullReload(0, 5).addConfigs(p35800, p35800_bl))
						.offset(0.75, -0.0625 * 1.5, -0.1875)
						.canFire(Lego.LAMBDA_STANDARD_CAN_FIRE).fire(Lego.LAMBDA_NOWEAR_FIRE).recoil(LAMBDA_RECOIL_ABERRATOR))
				.setupStandardConfiguration()
				.anim(LAMBDA_ABERRATOR).orchestra(AberratorOrchestras.ORCHESTRA_ABERRATOR)
				).pistolDualWield(true);

		AberratorGuns.gun_aberrator_eott = new ItemGunBaseNT(WeaponQuality.SECRET,
				new GunConfig().dura(2_000).draw(10).inspect(26).crosshair(Crosshair.CIRCLE).smoke(Lego.LAMBDA_STANDARD_SMOKE)
				.rec(new Receiver(0)
						.dmg(GUN_EOTT_LEFT_DAMAGE).spreadHipfire(0F).delay(13).dry(21).reload(51).sound(AberratorSounds.GUN_ABERRATOR_FIRE, 1.0F, 1.0F)
						.mag(new MagazineFullReload(0, 5).addConfigs(p35800, p35800_bl))
						.offset(0.75, -0.0625 * 1.5, 0.1875)
						.canFire(Lego.LAMBDA_STANDARD_CAN_FIRE).fire(Lego.LAMBDA_NOWEAR_FIRE).recoil(LAMBDA_RECOIL_ABERRATOR))
				.pp(Lego.LAMBDA_STANDARD_CLICK_PRIMARY).pr(Lego.LAMBDA_STANDARD_RELOAD)
				.decider(GunStateDecider.LAMBDA_STANDARD_DECIDER)
				.anim(LAMBDA_ABERRATOR).orchestra(AberratorOrchestras.ORCHESTRA_ABERRATOR),
				new GunConfig().dura(2_000).draw(10).inspect(26).crosshair(Crosshair.CIRCLE).smoke(Lego.LAMBDA_STANDARD_SMOKE)
				.rec(new Receiver(0)
						.dmg(GUN_EOTT_RIGHT_DAMAGE).spreadHipfire(0F).delay(13).dry(21).reload(51).sound(AberratorSounds.GUN_ABERRATOR_FIRE, 1.0F, 1.0F)
						.mag(new MagazineFullReload(1, 5).addConfigs(p35800, p35800_bl))
						.offset(0.75, -0.0625 * 1.5, -0.1875)
						.canFire(Lego.LAMBDA_STANDARD_CAN_FIRE).fire(Lego.LAMBDA_NOWEAR_FIRE).recoil(LAMBDA_RECOIL_ABERRATOR))
				.ps(Lego.LAMBDA_STANDARD_CLICK_PRIMARY).pr(Lego.LAMBDA_STANDARD_RELOAD)
				.decider(GunStateDecider.LAMBDA_STANDARD_DECIDER)
				.anim(LAMBDA_ABERRATOR).orchestra(AberratorOrchestras.ORCHESTRA_ABERRATOR)
				);
	}

	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_RECOIL_ABERRATOR = (stack, ctx) -> {
		ItemGunBaseNT.setupRecoil(10, (float) (ctx.getPlayer().getRandom().nextGaussian() * 1.5));
	};

	@SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_ABERRATOR = (stack, type) -> {
		boolean aim = ItemGunBaseNT.getIsAiming(stack);
		int ammo = ((ItemGunBaseNT) stack.getItem()).getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack).getAmount(stack, null);
		switch(type) {
		case EQUIP: return new BusAnimation()
				.addBus("EQUIP", new BusAnimationSequence().addPos(360, 0, 0, 0).addPos(0, 0, 0, 500, IType.SIN_FULL))
				.addBus("RISE", new BusAnimationSequence().addPos(0, -3, 0, 0).addPos(0, 0, 0, 500, IType.SIN_FULL));
		case CYCLE: return new BusAnimation()
				.addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, 0, 50).addPos(aim ? -15 : -25, 0, 0, 100, IType.SIN_DOWN).addPos(0, 0, 0, 500, IType.SIN_FULL))
				.addBus("SIGHT", new BusAnimationSequence().addPos(0, 0, 0, 50).addPos(aim ? 5 : 15, 0, 0, 100, IType.SIN_DOWN).addPos(0, 0, 0, 250, IType.SIN_FULL))
				.addBus("SLIDE", new BusAnimationSequence().addPos(0, 0, 0, 50).addPos(0, 0, -1.125, 50, IType.SIN_DOWN).addPos(0, 0, -1.125, 50).addPos(0, 0, 0, 150, IType.SIN_UP))
				.addBus(ammo <= 1 ? "NULL" : "BULLET", new BusAnimationSequence().addPos(0, 0, 0, 150).addPos(0, 0.375, 1.125, 150, IType.SIN_UP))
				.addBus("HAMMER", new BusAnimationSequence().addPos(45, 0, 0, 50).addPos(-45, 0, -1.125, 50, IType.SIN_DOWN).addPos(-20, 0, -1.125, 50).addPos(0, 0, 0, 150, IType.SIN_UP));
		case CYCLE_DRY: return new BusAnimation()
				.addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, 0, 700).addPos(-5, 0, 0, 100, IType.SIN_FULL).addPos(0, 0, 0, 250, IType.SIN_FULL))
				.addBus("SLIDE", new BusAnimationSequence().addPos(0, 0, 0, 550).addPos(0, 0, -1.125, 150, IType.SIN_FULL).addPos(0, 0, -1.125, 50).addPos(0, 0, 0, 150, IType.SIN_UP))
				.addBus("HAMMER", new BusAnimationSequence().addPos(45, 0, 0, 50).addPos(45, 0, 0, 500).addPos(-45, 0, -1.125, 150, IType.SIN_FULL).addPos(-20, 0, -1.125, 50).addPos(0, 0, 0, 150, IType.SIN_UP));
		case RELOAD: return new BusAnimation()
				.addBus("ROLL", new BusAnimationSequence().addPos(0, 0, 20, 150, IType.SIN_FULL).addPos(0, 0, 20, 50).addPos(0, 0, -45, 150, IType.SIN_UP).addPos(0, 0, 0, 150, IType.SIN_FULL))
				.addBus("MAG", new BusAnimationSequence().addPos(0, 0, 0, 350).addPos(0, -2, 0, 0).addPos(-15, -5, 0, 350).addPos(-15, 0, 0, 0).addPos(-15, 0, 0, 700).addPos(3, 3, 0, 0).addPos(0, -2, 0, 250, IType.SIN_DOWN).addPos(0, -2, 0, 50).addPos(0, 0, 0, 150, IType.SIN_DOWN))
				.addBus("MAGROLL", new BusAnimationSequence().addPos(0, 0, 0, 350).addPos(0, 0, -180, 250).addPos(0, 0, 0, 0))
				.addBus("EQUIP", new BusAnimationSequence().addPos(0, 0, 0, 750).addPos(5, 0, 0, 150, IType.SIN_FULL).addPos(-190, 0, 0, 500, IType.SIN_FULL).addPos(-190, 0, 0, 450).addPos(-360, 0, 0, 350, IType.SIN_DOWN).addPos(0, 0, 0, 0))
				.addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, 0, 2350).addPos(-5, 0, 0, 100, IType.SIN_FULL).addPos(0, 0, 0, 250, IType.SIN_FULL))
				.addBus("SLIDE", new BusAnimationSequence().addPos(0, 0, 0, 2200).addPos(0, 0, -1.125, 150, IType.SIN_FULL).addPos(0, 0, -1.125, 50).addPos(0, 0, 0, 150, IType.SIN_UP))
				.addBus("HAMMER", new BusAnimationSequence().addPos(0, 0, 0, 2250).addPos(-45, 0, -1.125, 100, IType.SIN_FULL).addPos(-20, 0, -1.125, 50).addPos(0, 0, 0, 150, IType.SIN_UP))
				.addBus("BULLET", new BusAnimationSequence().addPos(ammo > 0 ? 0 : -100, 0, 0, 0).addPos(ammo > 0 ? 0 : -100, 0, 0, 2400).addPos(0, 0, 0, 0).addPos(0, 0.375, 1.125, 150, IType.SIN_UP));
		case INSPECT: return new BusAnimation()
				.addBus("EQUIP", new BusAnimationSequence().addPos(0, 0, 0, 0).addPos(-720, 0, 0, 1000, IType.SIN_FULL).addPos(-720, 0, 0, 250).addPos(0, 0, 0, 1000, IType.SIN_FULL));
		}

		return null;
	};
}
