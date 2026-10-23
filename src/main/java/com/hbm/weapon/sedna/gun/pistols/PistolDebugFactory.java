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
public final class PistolDebugFactory {
 public static BulletConfig ammo_debug,ammo_debug_shot;
 public static SpentCasing CASING44=new SpentCasing(CasingType.STRAIGHT).setScale(1.5F,1F,1.5F).setColor(SpentCasing.COLOR_CASE_44);
 public static void init(){
  ammo_debug=new BulletConfig("hbm:pistols/ammo_debug").setItem(PistolAmmo.DEBUG).setSpread(.01F).setRicochetAngle(45).setCasing(CASING44.clone().register("DEBUG0"));
  ammo_debug_shot=new BulletConfig("hbm:pistols/ammo_debug_shot").setItem(PistolAmmo.DEBUG).setSpread(.05F).setProjectiles(6).setRicochetAngle(45).setCasing(CASING44.clone().register("DEBUG1"));
PistolGuns.gun_debug = new ItemGunBaseNT(WeaponQuality.DEBUG, new GunConfig()
				.dura(600F).draw(15).inspect(23).crosshair(Crosshair.L_CLASSIC).smoke(Lego.LAMBDA_STANDARD_SMOKE).orchestra(PistolOrchestras.DEBUG_ORCHESTRA)
				.rec(new Receiver(0)
						.dmg(10F).delay(14).reload(46).jam(23).sound("hbm:weapon.44Shoot", 1.0F, 1.0F)
						.mag(new MagazineFullReload(0, 12).addConfigs(ammo_debug))
						.offset(0.75, -0.0625, -0.3125D)
						.canFire(Lego.LAMBDA_STANDARD_CAN_FIRE).fire(Lego.LAMBDA_STANDARD_FIRE),
						new Receiver(1)
						.dmg(5F).delay(14).reload(46).jam(23).sound("hbm:weapon.44Shoot", 1.0F, 1.0F)
						.mag(new MagazineFullReload(1, 12).addConfigs(ammo_debug_shot))
						.offset(0.75, -0.0625, -0.3125D)
						.canFire(Lego.LAMBDA_SECOND_CAN_FIRE).fire(Lego.LAMBDA_SECOND_FIRE))
				.pp(Lego.LAMBDA_STANDARD_CLICK_PRIMARY).ps((stack, ctx) -> { Lego.clickReceiver(stack, ctx, 1); })
				.pr(Lego.LAMBDA_STANDARD_RELOAD).pt(Lego.LAMBDA_TOGGLE_AIM)
				.decider(LAMBDA_DEBUG_DECIDER)
				.anim(LAMBDA_DEBUG_ANIMS)
				);
}
	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_DEBUG_DECIDER = (stack, ctx) -> {
		int index = ctx.configIndex;
		GunState lastState = ItemGunBaseNT.getState(stack, index);
		GunStateDecider.deciderStandardFinishDraw(stack, lastState, index);
		GunStateDecider.deciderStandardClearJam(stack, lastState, index);
		GunStateDecider.deciderStandardReload(stack, ctx, lastState, 0, index);
		GunStateDecider.deciderStandardReload(stack, ctx, lastState, 1, index);
		GunStateDecider.deciderAutoRefire(stack, ctx, lastState, 0, index, () -> { return ItemGunBaseNT.getPrimary(stack, index) && ItemGunBaseNT.getMode(stack, ctx.configIndex) == 0; });
		GunStateDecider.deciderAutoRefire(stack, ctx, lastState, 1, index, () -> { return ItemGunBaseNT.getSecondary(stack, index) && ItemGunBaseNT.getMode(stack, ctx.configIndex) == 0; });
	};
	@SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_DEBUG_ANIMS = (stack, type) -> {
		switch(type) {
		case CYCLE: return new BusAnimation()
				.addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, 0, 50).addPos(0, 0, -3, 50).addPos(0, 0, 0, 250))
				.addBus("HAMMER", new BusAnimationSequence().addPos(0, 0, 1, 50).addPos(0, 0, 1, 400).addPos(0, 0, 0, 200))
				.addBus("DRUM", new BusAnimationSequence().addPos(0, 0, 0, 450).addPos(0, 0, 1, 200));
		case CYCLE_DRY: return new BusAnimation()
				.addBus("HAMMER", new BusAnimationSequence().addPos(0, 0, 1, 50).addPos(0, 0, 1, 300 + 100).addPos(0, 0, 0, 200))
				.addBus("DRUM", new BusAnimationSequence().addPos(0, 0, 0, 450).addPos(0, 0, 1, 200));
		case EQUIP: return new BusAnimation().addBus("ROTATE", new BusAnimationSequence().addPos(-360, 0, 0, 350));
		case RELOAD: return new BusAnimation()
					.addBus("RELAOD_TILT", new BusAnimationSequence().addPos(-15, 0, 0, 100).addPos(65, 0, 0, 100).addPos(45, 0, 0, 50).addPos(0, 0, 0, 200).addPos(0, 0, 0, 1450).addPos(-80, 0, 0, 100).addPos(-80, 0, 0, 100).addPos(0, 0, 0, 200))
					.addBus("RELOAD_CYLINDER", new BusAnimationSequence().addPos(0, 0, 0, 200).addPos(90, 0, 0, 100).addPos(90, 0, 0, 1700).addPos(0, 0, 0, 70))
					.addBus("RELOAD_LIFT", new BusAnimationSequence().addPos(0, 0, 0, 350).addPos(-45, 0, 0, 250).addPos(-45, 0, 0, 350).addPos(-15, 0, 0, 200).addPos(-15, 0, 0, 1050).addPos(0, 0, 0, 100))
					.addBus("RELOAD_JOLT", new BusAnimationSequence().addPos(0, 0, 0, 600).addPos(2, 0, 0, 50).addPos(0, 0, 0, 100))
					.addBus("RELOAD_BULLETS", new BusAnimationSequence().addPos(0, 0, 0, 650).addPos(10, 0, 0, 300).addPos(10, 0, 0, 200).addPos(0, 0, 0, 700))
					.addBus("RELOAD_BULLETS_CON", new BusAnimationSequence().addPos(1, 0, 0, 0).addPos(1, 0, 0, 950).addPos(0, 0, 0, 1 ) );
		case INSPECT:
		case JAMMED: return new BusAnimation()
					.addBus("RELAOD_TILT", new BusAnimationSequence().addPos(-15, 0, 0, 100).addPos(65, 0, 0, 100).addPos(45, 0, 0, 50).addPos(0, 0, 0, 200).addPos(0, 0, 0, 200).addPos(-80, 0, 0, 100).addPos(-80, 0, 0, 100).addPos(0, 0, 0, 200))
					.addBus("RELOAD_CYLINDER", new BusAnimationSequence().addPos(0, 0, 0, 200).addPos(90, 0, 0, 100).addPos(90, 0, 0, 450).addPos(0, 0, 0, 70));
		}

		return null;
	};
}
