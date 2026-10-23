package com.hbm.weapon.sedna.gun.heavypowder;
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
import com.hbm.entity.projectile.EntityBulletBaseMK4;
public class XFactory75Bolt {

	public static Supplier<Float> GUN_BOLTER_DAMAGE=HeavyPowderSettings.value("GUN_BOLTER_DAMAGE",15F);
	
	public static BulletConfig b75;
	public static BulletConfig b75_inc;
	public static BulletConfig b75_exp;
	
	public static void initConfig(){}

public static BiConsumer<EntityBulletBaseMK4,HitResult> LAMBDA_TINY_EXPLODE=(bullet,hit)->{if(hit instanceof EntityHitResult entity&&bullet.tickCount<3&&entity.getEntity()==bullet.getThrower())return;Lego.tinyExplode(bullet,hit,2F);bullet.setDead();};
public static BiConsumer<EntityBulletBaseMK4,HitResult> LAMBDA_INC=(bullet,hit)->{if(hit instanceof EntityHitResult entity&&entity.getEntity() instanceof LivingEntity living){var data=com.hbm.bomb.conventional.grenade.FireStatus.data(living);if(data.phosphorus<300)data.phosphorus=300;}};
public static BiConsumer<EntityBulletBaseMK4,HitResult> LAMBDA_STANDARD_EXPLODE=(bullet,hit)->{Lego.standardExplode(bullet,hit,5F);bullet.setDead();};

	public static void init() {
		SpentCasing casing75 = new SpentCasing(CasingType.STRAIGHT).setColor(SpentCasing.COLOR_CASE_BRASS).setScale(2F, 2F, 1.5F);

		b75 = new BulletConfig("hbm:heavy_powder/b75").setItem(PistolAmmo.B75)
				.setCasing(casing75.clone().register("b75")).setOnImpact(LAMBDA_TINY_EXPLODE);
		b75_inc = new BulletConfig("hbm:heavy_powder/b75_inc").setItem(PistolAmmo.B75_INC).setDamage(0.8F).setArmorPiercing(0.1F)
				.setCasing(casing75.clone().register("b75inc")).setOnImpact(LAMBDA_INC);
		b75_exp = new BulletConfig("hbm:heavy_powder/b75_exp").setItem(PistolAmmo.B75_EXP).setDamage(1.5F).setArmorPiercing(-0.25F)
				.setCasing(casing75.clone().register("b75exp")).setOnImpact(LAMBDA_STANDARD_EXPLODE);

		HeavyPowderGuns.gun_bolter = new ItemGunBaseNT(WeaponQuality.SPECIAL, new GunConfig()
				.dura(3_000).draw(20).inspect(31).crosshair(Crosshair.L_CIRCLE).smoke(LAMBDA_SMOKE)
				.rec(new Receiver(0)
						.dmg(GUN_BOLTER_DAMAGE).delay(2).auto(true).spread(0.005F).reload(40).jam(55).sound(HeavyPowderSounds.GUN_POWDER_FIRE, 1.0F, 1.0F)
						.mag(new MagazineFullReload(0, 30).addConfigs(b75, b75_inc, b75_exp))
						.offset(1, -0.0625 * 2.5, -0.25D)
						.setupStandardFire().recoil(LAMBDA_RECOIL_BOLT))
				.setupStandardConfiguration()
				.anim(LAMBDA_BOLTER_ANIMS).orchestra(HeavyPowderOrchestras.ORCHESTRA_BOLTER)
				).setDefaultAmmo(PistolAmmo.B75, 15);
	}

	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_SMOKE = (stack, ctx) -> {
		Lego.handleStandardSmoke(ctx.entity, stack, 2000, 0.05D, 1.1D, 0);
	};

	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_RECOIL_BOLT = (stack, ctx) -> {
		ItemGunBaseNT.setupRecoil((float) (ctx.getPlayer().getRandom().nextGaussian() * 1.5), (float) (ctx.getPlayer().getRandom().nextGaussian() * 1.5));
	};

	@SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_BOLTER_ANIMS = (stack, type) -> {
		switch(type) {
		case CYCLE: return new BusAnimation()
				.addBus("RECOIL", new BusAnimationSequence().addPos(1, 0, 0, 25).addPos(0, 0, 0, 75));
		case RELOAD: return new BusAnimation()
				.addBus("TILT", new BusAnimationSequence().addPos(1, 0, 0, 250).addPos(1, 0, 0, 1500).addPos(0, 0, 0, 250))
				.addBus("MAG", new BusAnimationSequence().addPos(0, 0, 1, 500).addPos(1, 0, 1, 500).addPos(0, 0, 0, 500));
		case JAMMED: return new BusAnimation()
				.addBus("TILT", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(1, 0, 0, 250).addPos(1, 0, 0, 700).addPos(0, 0, 0, 250))
				.addBus("MAG", new BusAnimationSequence().addPos(0, 0, 0, 750).addPos(0.6, 0, 0, 250).addPos(0, 0, 0, 250));
		}

		return null;
	};
}
