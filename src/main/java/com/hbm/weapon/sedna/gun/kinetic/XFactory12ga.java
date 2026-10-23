package com.hbm.weapon.sedna.gun.kinetic;
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
public final class XFactory12ga {
 public static final Supplier<Float> GUN_MARES_LEG_DAMAGE=KineticSettings.value("GUN_MARES_LEG_DAMAGE",16F);
 public static final Supplier<Float> GUN_MARES_LEG_AKIMBO_LEFT_DAMAGE=KineticSettings.value("GUN_MARES_LEG_AKIMBO_LEFT_DAMAGE",16F);
 public static final Supplier<Float> GUN_MARES_LEG_AKIMBO_RIGHT_DAMAGE=KineticSettings.value("GUN_MARES_LEG_AKIMBO_RIGHT_DAMAGE",16F);
 public static final Supplier<Float> GUN_LIBERATOR_DAMAGE=KineticSettings.value("GUN_LIBERATOR_DAMAGE",16F);
 public static final Supplier<Float> GUN_SPAS_DAMAGE=KineticSettings.value("GUN_SPAS_DAMAGE",32F);
 public static final Supplier<Float> GUN_AUTOSHOTGUN_DAMAGE=KineticSettings.value("GUN_AUTOSHOTGUN_DAMAGE",48F);
 public static final Supplier<Float> GUN_SHREDDER_DAMAGE=KineticSettings.value("GUN_SHREDDER_DAMAGE",50F);
 public static final BulletConfig g12_bp=LegendaryGunFactory.g12_bp;
 public static final BulletConfig g12_bp_magnum=LegendaryGunFactory.g12_bp_magnum;
 public static final BulletConfig g12_bp_slug=LegendaryGunFactory.g12_bp_slug;
 public static final BulletConfig g12=LegendaryGunFactory.g12;
 public static final BulletConfig g12_slug=LegendaryGunFactory.g12_slug;
 public static final BulletConfig g12_flechette=LegendaryGunFactory.g12_flechette;
 public static final BulletConfig g12_magnum=LegendaryGunFactory.g12_magnum;
 public static final BulletConfig g12_explosive=LegendaryGunFactory.g12_explosive;
 public static final BulletConfig g12_phosphorus=LegendaryGunFactory.g12_phosphorus;
 public static BulletConfig g12_sub,g12_shredder;
 public static BulletConfig g12_sub_slug,g12_shredder_slug;
 public static BulletConfig g12_sub_flechette,g12_shredder_flechette;
 public static BulletConfig g12_sub_magnum,g12_shredder_magnum;
 public static BulletConfig g12_sub_explosive,g12_shredder_explosive;
 public static BulletConfig g12_sub_phosphorus,g12_shredder_phosphorus;

 public static void initConfig(){}
 public static void init(){
 BulletConfig[] all={g12_bp,g12_bp_magnum,g12_bp_slug,g12,g12_slug,g12_flechette,g12_magnum,g12_explosive,g12_phosphorus};
 g12_sub=KineticImpacts.submunition("standard",g12);
 g12_shredder=KineticImpacts.beam("standard",g12,g12_sub);
 g12_sub_slug=KineticImpacts.submunition("slug",g12_slug);
 g12_shredder_slug=KineticImpacts.beam("slug",g12_slug,g12_sub_slug);
 g12_sub_flechette=KineticImpacts.submunition("flechette",g12_flechette);
 g12_shredder_flechette=KineticImpacts.beam("flechette",g12_flechette,g12_sub_flechette);
 g12_sub_magnum=KineticImpacts.submunition("magnum",g12_magnum);
 g12_shredder_magnum=KineticImpacts.beam("magnum",g12_magnum,g12_sub_magnum);
 g12_sub_explosive=KineticImpacts.submunition("explosive",g12_explosive);
 g12_shredder_explosive=KineticImpacts.beam("explosive",g12_explosive,g12_sub_explosive);
 g12_sub_phosphorus=KineticImpacts.submunition("phosphorus",g12_phosphorus);
 g12_shredder_phosphorus=KineticImpacts.beam("phosphorus",g12_phosphorus,g12_sub_phosphorus);
KineticGuns.gun_maresleg = new ItemGunBaseNT(WeaponQuality.A_SIDE, new GunConfig()
				.dura(600).draw(10).inspect(39).reloadSequential(true).crosshair(Crosshair.L_CIRCLE).smoke(Lego.LAMBDA_STANDARD_SMOKE)
				.rec(new Receiver(0)
						.dmg(GUN_MARES_LEG_DAMAGE).delay(20).reload(22, 10, 13, 0).jam(24).sound(KineticSounds.GUN_SHOTGUN_FIRE, 1.0F, 1.0F)
						.mag(new MagazineSingleReload(0, 6).addConfigs(all))
						.offset(0.75, -0.0625, -0.1875)
						.setupStandardFire().recoil(LAMBDA_RECOIL_MARESLEG))
				.setupStandardConfiguration()
				.anim(LAMBDA_MARESLEG_ANIMS).orchestra(KineticOrchestras.ORCHESTRA_MARESLEG)
				).setDefaultAmmo(KineticAmmo.G12, 12).setNameMutator(LAMBDA_NAME_MARESLEG)
				;
KineticGuns.gun_maresleg_akimbo = new ItemGunBaseNT(WeaponQuality.B_SIDE,
				new GunConfig().dura(600).draw(5).inspect(39).reloadSequential(true).crosshair(Crosshair.L_CIRCLE).smoke(Lego.LAMBDA_STANDARD_SMOKE)
				.rec(new Receiver(0)
						.dmg(GUN_MARES_LEG_AKIMBO_LEFT_DAMAGE).spreadHipfire(0F).spreadAmmo(1.35F).delay(20).reload(22, 10, 13, 0).jam(24).sound(KineticSounds.GUN_SHOTGUN_FIRE, 1.0F, 1.0F)
						.mag(new MagazineSingleReload(0, 6).addConfigs(all))
						.offset(0.75, -0.0625, 0.1875D)
						.setupStandardFire().recoil(LAMBDA_RECOIL_MARESLEG))
				.pp(Lego.LAMBDA_STANDARD_CLICK_PRIMARY).pr(Lego.LAMBDA_STANDARD_RELOAD)
				.decider(GunStateDecider.LAMBDA_STANDARD_DECIDER)
				.anim(LAMBDA_MARESLEG_SHORT_ANIMS).orchestra(KineticOrchestras.ORCHESTRA_MARESLEG_AKIMBO),
				new GunConfig().dura(600).draw(5).inspect(39).reloadSequential(true).crosshair(Crosshair.L_CIRCLE).smoke(Lego.LAMBDA_STANDARD_SMOKE)
				.rec(new Receiver(0)
						.dmg(GUN_MARES_LEG_AKIMBO_RIGHT_DAMAGE).spreadHipfire(0F).spreadAmmo(1.35F).delay(20).reload(22, 10, 13, 0).jam(24).sound(KineticSounds.GUN_SHOTGUN_FIRE, 1.0F, 1.0F)
						.mag(new MagazineSingleReload(1, 6).addConfigs(all))
						.offset(0.75, -0.0625, -0.1875)
						.setupStandardFire().recoil(LAMBDA_RECOIL_MARESLEG))
				.ps(Lego.LAMBDA_STANDARD_CLICK_PRIMARY).pr(Lego.LAMBDA_STANDARD_RELOAD)
				.decider(GunStateDecider.LAMBDA_STANDARD_DECIDER)
				.anim(LAMBDA_MARESLEG_SHORT_ANIMS).orchestra(KineticOrchestras.ORCHESTRA_MARESLEG_AKIMBO)
				).setDefaultAmmo(KineticAmmo.G12, 24);
KineticGuns.gun_liberator = new ItemGunBaseNT(WeaponQuality.A_SIDE, new GunConfig()
				.dura(200).draw(20).inspect(21).reloadSequential(true).crosshair(Crosshair.L_CIRCLE).smoke(Lego.LAMBDA_STANDARD_SMOKE)
				.rec(new Receiver(0)
						.dmg(GUN_LIBERATOR_DAMAGE).delay(20).rounds(4).reload(25, 15, 7, 0).jam(45).sound(KineticSounds.GUN_LIBERATOR_FIRE, 1.0F, 1.0F)
						.mag(new MagazineSingleReload(0, 4).addConfigs(all))
						.offset(0.75, -0.0625, -0.1875)
						.setupStandardFire().recoil(LAMBDA_RECOIL_LIBERATOR))
				.setupStandardConfiguration()
				.anim(LAMBDA_LIBERATOR_ANIMS).orchestra(KineticOrchestras.ORCHESTRA_LIBERATOR)
				).setDefaultAmmo(KineticAmmo.G12, 12);
KineticGuns.gun_spas12 = new ItemGunBaseNT(WeaponQuality.A_SIDE, new GunConfig()
				.dura(600).draw(20).inspect(39).reloadSequential(true).reloadChangeType(true).crosshair(Crosshair.L_CIRCLE).smoke(Lego.LAMBDA_STANDARD_SMOKE)
				.rec(new Receiver(0)
						.dmg(GUN_SPAS_DAMAGE).spreadHipfire(0F).delay(20).reload(5, 10, 10, 10, 0).jam(36).sound(KineticSounds.GUN_SPAS_FIRE, 1.0F, 1.0F)
						.mag(new MagazineSingleReload(0, 8).addConfigs(all))
						.offset(0.75, -0.0625, -0.1875)
						.setupStandardFire().recoil(LAMBDA_RECOIL_MARESLEG))
				.setupStandardConfiguration().ps(LAMBDA_SPAS_SECONDARY).pt(null)
				.anim(LAMBDA_SPAS_ANIMS).orchestra(KineticOrchestras.ORCHESTRA_SPAS)
				).setDefaultAmmo(KineticAmmo.G12, 16);
KineticGuns.gun_autoshotgun = new ItemGunBaseNT(WeaponQuality.A_SIDE, new GunConfig()
				.dura(2_000).draw(10).inspect(33).reloadSequential(true).crosshair(Crosshair.L_CIRCLE).smoke(Lego.LAMBDA_STANDARD_SMOKE)
				.rec(new Receiver(0)
						.dmg(GUN_AUTOSHOTGUN_DAMAGE).delay(10).auto(true).autoAfterDry(true).dryfireAfterAuto(true).reload(44).jam(19).sound(KineticSounds.GUN_SHREDDER_FIRE, 1.0F, 1.0F)
						.mag(new MagazineFullReload(0, 20).addConfigs(all))
						.offset(0.75, -0.125, -0.25)
						.setupStandardFire().recoil(LAMBDA_RECOIL_AUTOSHOTGUN))
				.setupStandardConfiguration()
				.anim(LAMBDA_SHREDDER_ANIMS).orchestra(KineticOrchestras.ORCHESTRA_SHREDDER)
				).setDefaultAmmo(KineticAmmo.G12, 20);
KineticGuns.gun_autoshotgun_shredder = new ItemGunBaseNT(WeaponQuality.B_SIDE, new GunConfig()
				.dura(2_000).draw(10).inspect(33).reloadSequential(true).crosshair(Crosshair.L_CIRCLE).smoke(Lego.LAMBDA_STANDARD_SMOKE)
				.rec(new Receiver(0)
						.dmg(GUN_SHREDDER_DAMAGE).delay(10).auto(true).autoAfterDry(true).dryfireAfterAuto(true).reload(44).jam(19).sound(KineticSounds.GUN_SHREDDER_FIRE, 1.0F, 1.0F)
						.mag(new MagazineBelt().addConfigs(g12_shredder, g12_shredder_slug, g12_shredder_flechette, g12_shredder_magnum, g12_shredder_explosive, g12_shredder_phosphorus))
						.offset(0.75, -0.125, -0.25)
						.setupStandardFire().recoil(LAMBDA_RECOIL_AUTOSHOTGUN))
				.setupStandardConfiguration()
				.anim(LAMBDA_SHREDDER_ANIMS).orchestra(KineticOrchestras.ORCHESTRA_SHREDDER)
				).setDefaultAmmo(KineticAmmo.G12, 20);
}
	public static Function<ItemStack, String> LAMBDA_NAME_MARESLEG = (stack) -> {
		if(XWeaponModManager.hasUpgrade(stack, 0, 203)) return KineticNames.sourceKey(stack) + "_short";
		return null;
	};
	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_RECOIL_MARESLEG = (stack, ctx) -> {
		ItemGunBaseNT.setupRecoil(10, (float) (ctx.getPlayer().getRandom().nextGaussian() * 1.5));
	};
	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_RECOIL_LIBERATOR = (stack, ctx) -> {
		ItemGunBaseNT.setupRecoil(5, (float) (ctx.getPlayer().getRandom().nextGaussian() * 1.5));
	};
	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_RECOIL_AUTOSHOTGUN = (stack, ctx) -> {
		ItemGunBaseNT.setupRecoil((float) (ctx.getPlayer().getRandom().nextGaussian() * 1.5) + 1.5F, (float) (ctx.getPlayer().getRandom().nextGaussian() * 0.5));
	};
	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_SPAS_SECONDARY = (stack, ctx) -> {
		LivingEntity entity = ctx.entity;
		Player player = ctx.getPlayer();
		Receiver rec = ctx.config.getReceivers(stack)[0];
		int index = ctx.configIndex;
		GunState state = ItemGunBaseNT.getState(stack, index);
		if(state == GunState.IDLE) {
			if(rec.getCanFire(stack).apply(stack, ctx)) {
				rec.getOnFire(stack).accept(stack, ctx);
				int remaining = rec.getRoundsPerCycle(stack);
				int timeFired = 1;
				for(int i = 0; i < remaining; i++) {
					if(rec.getCanFire(stack).apply(stack, ctx)) {
						rec.getOnFire(stack).accept(stack, ctx);
						timeFired++;
					}
				}
				if(rec.getFireSound(stack) != null) GunSounds.play(entity,rec.getFireSound(stack),rec.getFireVolume(stack),rec.getFirePitch(stack)*(timeFired>1?.9F:1F));
				ItemGunBaseNT.setState(stack, index, GunState.COOLDOWN);
				ItemGunBaseNT.setTimer(stack, index, 20);
			} else {
				if(rec.getDoesDryFire(stack)) {
					ItemGunBaseNT.playAnimation(player, stack, GunAnimation.CYCLE_DRY, index);
					ItemGunBaseNT.setState(stack, index, GunState.DRAWING);
					ItemGunBaseNT.setTimer(stack, index, rec.getDelayAfterDryFire(stack));
				}
			}
		}
		if(state == GunState.RELOADING) {
			ItemGunBaseNT.setReloadCancel(stack, true);
		}
	};
	@SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_LIBERATOR_ANIMS = (stack, type) -> {
		int ammo = ((ItemGunBaseNT) stack.getItem()).getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack).getAmount(stack, (ItemGunBaseNT.CLIENT==null||ItemGunBaseNT.CLIENT.localPlayer()==null?null:new GunInventory(ItemGunBaseNT.CLIENT.localPlayer())));
		switch(type) {
		case EQUIP: return new BusAnimation()
				.addBus("EQUIP", new BusAnimationSequence().addPos(60, 0, 0, 0).addPos(0, 0, 0, 500, IType.SIN_DOWN));
		case CYCLE: return new BusAnimation()
				.addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, -2.5, 50, IType.SIN_DOWN).addPos(0, 0, 0, 350, IType.SIN_FULL));
		case CYCLE_DRY: return new BusAnimation();
		case RELOAD: if(ammo == 0) return new BusAnimation()
				.addBus("LATCH", new BusAnimationSequence().addPos(15, 0, 0, 100))
				.addBus("BREAK", new BusAnimationSequence().addPos(0, 0, 0, 100).addPos(60, 0, 0, 350, IType.SIN_DOWN))
				.addBus("SHELL1", new BusAnimationSequence().addPos(2, -4, -2, 0).addPos(2, -4, -2, 400).addPos(0, 0, -2, 450, IType.SIN_FULL).addPos(0, 0, 0, 50, IType.SIN_UP))
				.addBus("SHELL2", new BusAnimationSequence().addPos(2, -4, -2, 0))
				.addBus("SHELL3", new BusAnimationSequence().addPos(2, -4, -2, 0))
				.addBus("SHELL4", new BusAnimationSequence().addPos(2, -4, -2, 0));
			if(ammo == 1) return new BusAnimation()
					.addBus("LATCH", new BusAnimationSequence().addPos(15, 0, 0, 100))
					.addBus("BREAK", new BusAnimationSequence().addPos(0, 0, 0, 100).addPos(60, 0, 0, 350, IType.SIN_DOWN))
					.addBus("SHELL1", new BusAnimationSequence().addPos(0, 0, 0, 0))
					.addBus("SHELL2", new BusAnimationSequence().addPos(2, -4, -2, 0).addPos(2, -4, -2, 400).addPos(0, 0, -2, 450, IType.SIN_FULL).addPos(0, 0, 0, 50, IType.SIN_UP))
					.addBus("SHELL3", new BusAnimationSequence().addPos(2, -4, -2, 0))
					.addBus("SHELL4", new BusAnimationSequence().addPos(2, -4, -2, 0));
			if(ammo == 2) return new BusAnimation()
					.addBus("LATCH", new BusAnimationSequence().addPos(15, 0, 0, 100))
					.addBus("BREAK", new BusAnimationSequence().addPos(0, 0, 0, 100).addPos(60, 0, 0, 350, IType.SIN_DOWN))
					.addBus("SHELL1", new BusAnimationSequence().addPos(0, 0, 0, 0))
					.addBus("SHELL2", new BusAnimationSequence().addPos(0, 0, 0, 0))
					.addBus("SHELL3", new BusAnimationSequence().addPos(2, -4, -2, 0).addPos(2, -4, -2, 400).addPos(0, 0, -2, 450, IType.SIN_FULL).addPos(0, 0, 0, 50, IType.SIN_UP))
					.addBus("SHELL4", new BusAnimationSequence().addPos(2, -4, -2, 0));
			if(ammo == 3) return new BusAnimation()
					.addBus("LATCH", new BusAnimationSequence().addPos(15, 0, 0, 100))
					.addBus("BREAK", new BusAnimationSequence().addPos(0, 0, 0, 100).addPos(60, 0, 0, 350, IType.SIN_DOWN))
					.addBus("SHELL1", new BusAnimationSequence().addPos(0, 0, 0, 0))
					.addBus("SHELL2", new BusAnimationSequence().addPos(0, 0, 0, 0))
					.addBus("SHELL3", new BusAnimationSequence().addPos(0, 0, 0, 0))
					.addBus("SHELL4", new BusAnimationSequence().addPos(2, -4, -2, 0).addPos(2, -4, -2, 400).addPos(0, 0, -2, 450, IType.SIN_FULL).addPos(0, 0, 0, 50, IType.SIN_UP));
		case RELOAD_CYCLE:
			if(ammo == 0) return new BusAnimation()
					.addBus("LATCH", new BusAnimationSequence().addPos(15, 0, 0, 0))
					.addBus("BREAK", new BusAnimationSequence().addPos(60, 0, 0, 0))
					.addBus("SHELL1", new BusAnimationSequence().addPos(0, 0, 0, 0))
					.addBus("SHELL2", new BusAnimationSequence().addPos(2, -4, -2, 0).addPos(0, 0, -2, 450, IType.SIN_FULL).addPos(0, 0, 0, 50, IType.SIN_UP))
					.addBus("SHELL3", new BusAnimationSequence().addPos(2, -4, -2, 0))
					.addBus("SHELL4", new BusAnimationSequence().addPos(2, -4, -2, 0));
			if(ammo == 1) return new BusAnimation()
					.addBus("LATCH", new BusAnimationSequence().addPos(15, 0, 0, 0))
					.addBus("BREAK", new BusAnimationSequence().addPos(60, 0, 0, 0))
					.addBus("SHELL1", new BusAnimationSequence().addPos(0, 0, 0, 0))
					.addBus("SHELL2", new BusAnimationSequence().addPos(0, 0, 0, 0))
					.addBus("SHELL3", new BusAnimationSequence().addPos(2, -4, -2, 0).addPos(0, 0, -2, 450, IType.SIN_FULL).addPos(0, 0, 0, 50, IType.SIN_UP))
					.addBus("SHELL4", new BusAnimationSequence().addPos(2, -4, -2, 0));
			if(ammo == 2) return new BusAnimation()
					.addBus("LATCH", new BusAnimationSequence().addPos(15, 0, 0, 0))
					.addBus("BREAK", new BusAnimationSequence().addPos(60, 0, 0, 0))
					.addBus("SHELL1", new BusAnimationSequence().addPos(0, 0, 0, 0))
					.addBus("SHELL2", new BusAnimationSequence().addPos(0, 0, 0, 0))
					.addBus("SHELL3", new BusAnimationSequence().addPos(0, 0, 0, 0))
					.addBus("SHELL4", new BusAnimationSequence().addPos(2, -4, -2, 0).addPos(0, 0, -2, 450, IType.SIN_FULL).addPos(0, 0, 0, 50, IType.SIN_UP));
			return null;
		case RELOAD_END: return new BusAnimation()
				.addBus("LATCH", new BusAnimationSequence().addPos(15, 0, 0, 0).addPos(15, 0, 0, 250).addPos(0, 0, 0, 50))
				.addBus("BREAK", new BusAnimationSequence().addPos(60, 0, 0, 0).addPos(0, 0, 0, 250, IType.SIN_UP))
				.addBus(ammo >= 0 ? "SHELL1" : "NULL", new BusAnimationSequence().addPos(0, 0, 0, 0))
				.addBus(ammo >= 1 ? "SHELL2" : "NULL", new BusAnimationSequence().addPos(0, 0, 0, 0))
				.addBus(ammo >= 2 ? "SHELL3" : "NULL", new BusAnimationSequence().addPos(0, 0, 0, 0))
				.addBus(ammo >= 3 ? "SHELL4" : "NULL", new BusAnimationSequence().addPos(0, 0, 0, 0))
				.addBus(ammo < 0 ? "SHELL1" : "NULL", new BusAnimationSequence().addPos(2, -8, -2, 0))
				.addBus(ammo < 1 ? "SHELL2" : "NULL", new BusAnimationSequence().addPos(2, -8, -2, 0))
				.addBus(ammo < 2 ? "SHELL3" : "NULL", new BusAnimationSequence().addPos(2, -8, -2, 0))
				.addBus(ammo < 3 ? "SHELL4" : "NULL", new BusAnimationSequence().addPos(2, -8, -2, 0));
		case JAMMED: return new BusAnimation()
				.addBus("LATCH", new BusAnimationSequence().addPos(15, 0, 0, 0).addPos(15, 0, 0, 250).addPos(0, 0, 0, 50).addPos(0, 0, 0, 550).addPos(15, 0, 0, 100).addPos(15, 0, 0, 600).addPos(0, 0, 0, 50))
				.addBus("BREAK", new BusAnimationSequence().addPos(60, 0, 0, 0).addPos(0, 0, 0, 250, IType.SIN_UP).addPos(0, 0, 0, 600).addPos(45, 0, 0, 250, IType.SIN_DOWN).addPos(45, 0, 0, 300).addPos(0, 0, 0, 150, IType.SIN_UP))
				.addBus(ammo >= 0 ? "SHELL1" : "NULL", new BusAnimationSequence().addPos(0, 0, 0, 0))
				.addBus(ammo >= 1 ? "SHELL2" : "NULL", new BusAnimationSequence().addPos(0, 0, 0, 0))
				.addBus(ammo >= 2 ? "SHELL3" : "NULL", new BusAnimationSequence().addPos(0, 0, 0, 0))
				.addBus(ammo >= 3 ? "SHELL4" : "NULL", new BusAnimationSequence().addPos(0, 0, 0, 0))
				.addBus(ammo < 0 ? "SHELL1" : "NULL", new BusAnimationSequence().addPos(2, -8, -2, 0))
				.addBus(ammo < 1 ? "SHELL2" : "NULL", new BusAnimationSequence().addPos(2, -8, -2, 0))
				.addBus(ammo < 2 ? "SHELL3" : "NULL", new BusAnimationSequence().addPos(2, -8, -2, 0))
				.addBus(ammo < 3 ? "SHELL4" : "NULL", new BusAnimationSequence().addPos(2, -8, -2, 0));
		case INSPECT: return new BusAnimation()
				.addBus("LATCH", new BusAnimationSequence().addPos(15, 0, 0, 100).addPos(15, 0, 0, 1100).addPos(0, 0, 0, 50))
				.addBus("BREAK", new BusAnimationSequence().addPos(0, 0, 0, 100).addPos(60, 0, 0, 350, IType.SIN_DOWN).addPos(60, 0, 0, 500).addPos(0, 0, 0, 250, IType.SIN_UP))
				.addBus(ammo > 0 ? "SHELL1" : "NULL", new BusAnimationSequence().addPos(0, 0, 0, 0))
				.addBus(ammo > 1 ? "SHELL2" : "NULL", new BusAnimationSequence().addPos(0, 0, 0, 0))
				.addBus(ammo > 2 ? "SHELL3" : "NULL", new BusAnimationSequence().addPos(0, 0, 0, 0))
				.addBus(ammo > 3 ? "SHELL4" : "NULL", new BusAnimationSequence().addPos(0, 0, 0, 0))
				.addBus(ammo < 1 ? "SHELL1" : "NULL", new BusAnimationSequence().addPos(2, -8, -2, 0))
				.addBus(ammo < 2 ? "SHELL2" : "NULL", new BusAnimationSequence().addPos(2, -8, -2, 0))
				.addBus(ammo < 3 ? "SHELL3" : "NULL", new BusAnimationSequence().addPos(2, -8, -2, 0))
				.addBus(ammo < 4 ? "SHELL4" : "NULL", new BusAnimationSequence().addPos(2, -8, -2, 0));
		}

		return null;
	};
	@SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_SPAS_ANIMS = (stack, type) -> {
		switch(type) {
		case EQUIP: return new BusAnimation()
				.addBus("EQUIP", new BusAnimationSequence().addPos(-60, 0, 0, 0).addPos(0, 0, -3, 500, IType.SIN_DOWN));
		case CYCLE: return com.hbm.client.weapon.pistols.PistolAnimationAssets.get("spas12","Fire");
		case CYCLE_DRY: return com.hbm.client.weapon.pistols.PistolAnimationAssets.get("spas12","FireDry");
		case ALT_CYCLE: return com.hbm.client.weapon.pistols.PistolAnimationAssets.get("spas12","FireAlt");
		case RELOAD:
			boolean empty = ((ItemGunBaseNT) stack.getItem()).getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack).getAmount(stack, (ItemGunBaseNT.CLIENT==null||ItemGunBaseNT.CLIENT.localPlayer()==null?null:new GunInventory(ItemGunBaseNT.CLIENT.localPlayer()))) <= 0;
			return com.hbm.client.weapon.pistols.PistolAnimationAssets.get("spas12",empty ? "ReloadEmptyStart" : "ReloadStart");
		case RELOAD_CYCLE: return com.hbm.client.weapon.pistols.PistolAnimationAssets.get("spas12","Reload");
		case RELOAD_END: return com.hbm.client.weapon.pistols.PistolAnimationAssets.get("spas12","ReloadEnd");
		case JAMMED: return com.hbm.client.weapon.pistols.PistolAnimationAssets.get("spas12","Jammed");
		case INSPECT: return com.hbm.client.weapon.pistols.PistolAnimationAssets.get("spas12","Inspect");
		}

		return null;
	};
	@SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_SHREDDER_ANIMS = (stack, type) -> {
		switch(type) {
		case EQUIP: return new BusAnimation()
				.addBus("EQUIP", new BusAnimationSequence().addPos(60, 0, 0, 0).addPos(0, 0, 0, 500, IType.SIN_DOWN));
		case CYCLE: return new BusAnimation()
				.addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, -1, 50, IType.SIN_DOWN).addPos(0, 0, 0, 150, IType.SIN_FULL))
				.addBus("CYCLE", new BusAnimationSequence().addPos(0, 0, 0, 150).addPos(0, 0, 18, 100));
		case CYCLE_DRY: return new BusAnimation()
				.addBus("CYCLE", new BusAnimationSequence().addPos(0, 0, 0, 150).addPos(0, 0, 18, 100));
		case RELOAD: return new BusAnimation()
				.addBus("MAG", new BusAnimationSequence().addPos(0, -8, 0, 250, IType.SIN_UP).addPos(0, -8, 0, 1000).addPos(0, 0, 0, 300))
				.addBus("LIFT", new BusAnimationSequence().addPos(0, 0, 0, 750).addPos(-25, 0, 0, 300, IType.SIN_FULL).addPos(-25, 0, 0, 500).addPos(-27, 0, 0, 100, IType.SIN_DOWN).addPos(-25, 0, 0, 100, IType.SIN_FULL).addPos(-25, 0, 0, 150).addPos(0, 0, 0, 300, IType.SIN_FULL));
		case JAMMED: return new BusAnimation()
				.addBus("MAG", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(0, -2, 0, 150, IType.SIN_UP).addPos(0, 0, 0, 100))
				.addBus("LIFT", new BusAnimationSequence().addPos(0, 0, 0, 750).addPos(-2, 0, 0, 100, IType.SIN_DOWN).addPos(0, 0, 0, 100, IType.SIN_FULL));
		case INSPECT: return new BusAnimation()
				.addBus("MAG", new BusAnimationSequence()
						.addPos(0, -1, 0, 150).addPos(6, -1, 0, 150).addPos(6, 12, 0, 350, IType.SIN_DOWN).addPos(6, -2, 0, 350, IType.SIN_UP).addPos(6, -1, 0, 50)
						.addPos(6, -1, 0, 100).addPos(0, -1, 0, 150, IType.SIN_FULL).addPos(0, 0, 0, 150, IType.SIN_UP))
				.addBus("SPEEN", new BusAnimationSequence().addPos(0, 0, 0, 300).addPos(360, 0, 0, 700))
				.addBus("LIFT", new BusAnimationSequence().addPos(0, 0, 0, 1450).addPos(-2, 0, 0, 100, IType.SIN_DOWN).addPos(0, 0, 0, 100, IType.SIN_FULL));
		}

		return null;
	};
 public static BiFunction<ItemStack,GunAnimation,BusAnimation> LAMBDA_MARESLEG_ANIMS=LegendaryGunAnimations.LAMBDA_MARESLEG_ANIMS;
 public static BiFunction<ItemStack,GunAnimation,BusAnimation> LAMBDA_MARESLEG_SHORT_ANIMS=LegendaryGunAnimations.LAMBDA_MARESLEG_SHORT_ANIMS;
}
