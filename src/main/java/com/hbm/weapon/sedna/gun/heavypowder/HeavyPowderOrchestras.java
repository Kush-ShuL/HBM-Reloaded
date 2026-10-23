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
public final class HeavyPowderOrchestras {
	public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_BOLTER = (stack, ctx) -> {
		LivingEntity entity = ctx.entity;
		if(entity.level().isClientSide) return;
		GunAnimation type = ItemGunBaseNT.getLastAnim(stack, ctx.configIndex);
		int timer = ItemGunBaseNT.getAnimTimer(stack, ctx.configIndex);
		boolean aiming = ItemGunBaseNT.getIsAiming(stack);

		if(type == GunAnimation.CYCLE) {
			if(timer == 1) {
				SpentCasing casing = ctx.config.getReceivers(stack)[0].getMagazine(stack).getCasing(stack, ctx.inventory);
				if(casing != null) CasingEffects.composeEffect(entity.level(), entity, 0.5, aiming ? 0 : -0.125, aiming ? -0.0625 : -0.25D, 0, 0.18, -0.12, 0.01, -10F + (float)entity.getRandom().nextGaussian() * 5F, 10F + entity.getRandom().nextFloat() * 10F, casing.getName());
			}
		}

		if(type == GunAnimation.RELOAD) {
			if(timer == 5) GunSounds.play(entity, HeavyPowderSounds.GUN_MAG_REMOVE, 1F, 1F);
			if(timer == 26) GunSounds.play(entity, HeavyPowderSounds.GUN_MAG_INSERT, 1F, 1F);
		}
	};
	public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_DOUBLE_BARREL = (stack, ctx) -> {
		LivingEntity entity = ctx.entity;
		if(entity.level().isClientSide) return;
		GunAnimation type = ItemGunBaseNT.getLastAnim(stack, ctx.configIndex);
		int timer = ItemGunBaseNT.getAnimTimer(stack, ctx.configIndex);

		if(type == GunAnimation.RELOAD) {
			if(timer == 5) GunSounds.play(entity, HeavyPowderSounds.GUN_REVOLVER_COCK, 1F, 0.75F);
			if(timer == 19) GunSounds.play(entity, HeavyPowderSounds.GUN_MAG_SMALL_INSERT, 1F, 0.9F);
			if(timer == 29) GunSounds.play(entity, HeavyPowderSounds.GUN_REVOLVER_CLOSE, 1F, 0.8F);

			if(timer == 12) {
				IMagazine mag = ctx.config.getReceivers(stack)[0].getMagazine(stack);
				int toEject = mag.getAmountAfterReload(stack) - mag.getAmount(stack, ctx.inventory);
				SpentCasing casing = mag.getCasing(stack, ctx.inventory);
				if(casing != null) for(int i = 0; i < toEject; i++) CasingEffects.composeEffect(entity.level(), entity, 0, -0.1875, -0.375D, -0.24, 0.18, 0, 0.01, -20F + (float)entity.getRandom().nextGaussian() * 5F, (float)entity.getRandom().nextGaussian() * 2.5F, casing.getName(), true, 60, 0.5D, 20);
			}
		}

		if(type == GunAnimation.INSPECT) {
			if(timer == 5) GunSounds.play(entity, HeavyPowderSounds.GUN_REVOLVER_COCK, 1F, 0.75F);
			if(timer == 19) GunSounds.play(entity, HeavyPowderSounds.GUN_REVOLVER_CLOSE, 1F, 0.8F);
		}
		if(type == GunAnimation.CYCLE) {
			if(timer == 0) GunNetwork.flash(entity);
		}
		if(type == GunAnimation.CYCLE_DRY) {
			if(timer == 2) GunSounds.play(entity, HeavyPowderSounds.GUN_DRY_FIRE, 1F, 1F);
		}
	};
 public static BiConsumer<ItemStack,LambdaContext> ORCHESTRA_SHREDDER_SEXY=com.hbm.weapon.sedna.gun.legendary.LegendaryGunOrchestras.ORCHESTRA_SHREDDER_SEXY;
}
