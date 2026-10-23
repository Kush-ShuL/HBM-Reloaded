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
public final class Rifle50Orchestras {
	public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_AMAT = (stack, ctx) -> {
		LivingEntity entity = ctx.entity;
		if(entity.level().isClientSide) return;
		GunAnimation type = ItemGunBaseNT.getLastAnim(stack, ctx.configIndex);
		int timer = ItemGunBaseNT.getAnimTimer(stack, ctx.configIndex);
		boolean aiming = ItemGunBaseNT.getIsAiming(stack);

		if(type == GunAnimation.EQUIP) {
			if(timer == 10) GunSounds.play(entity, Rifle50Sounds.GUN_REVOLVER_COCK, 0.5F, 1.25F);
			if(timer == 15) GunSounds.play(entity, Rifle50Sounds.GUN_REVOLVER_CLOSE, 0.5F, 1.25F);
		}

		if(type == GunAnimation.CYCLE) {
			if(timer == 0) GunNetwork.flash(entity);
			if(timer == 7) GunSounds.play(entity, Rifle50Sounds.GUN_BOLT_OPEN, 0.5F, 1F);
			if(timer == 16) GunSounds.play(entity, Rifle50Sounds.GUN_BOLT_CLOSE, 0.5F, 1F);
			if(timer == 12) {
				SpentCasing casing = ctx.config.getReceivers(stack)[0].getMagazine(stack).getCasing(stack, ctx.inventory);
				if(casing != null) CasingEffects.composeEffect(entity.level(), entity,
						0.375, aiming ? 0 : -0.125, -0.25D,
						-0.05, 0.2, -0.025,
						0.01, -10F + (float) entity.getRandom().nextGaussian() * 10F, (float) entity.getRandom().nextGaussian() * 12.5F, casing.getName(), true, 60, 0.5D, 10);
			}
		}

		if(type == GunAnimation.CYCLE_DRY) {
			if(timer == 0) GunSounds.play(entity, Rifle50Sounds.GUN_DRY_FIRE, 1F, 0.75F);
			if(timer == 7) GunSounds.play(entity, Rifle50Sounds.GUN_BOLT_OPEN, 0.5F, 1F);
			if(timer == 16) GunSounds.play(entity, Rifle50Sounds.GUN_BOLT_CLOSE, 0.5F, 1F);
		}

		if(type == GunAnimation.RELOAD) {
			if(timer == 2) GunSounds.play(entity, Rifle50Sounds.GUN_MAG_REMOVE, 1F, 1F);
			if(timer == 20) GunSounds.play(entity, Rifle50Sounds.GUN_MAG_INSERT, 1F, 1F);
			if(timer == 32) GunSounds.play(entity, Rifle50Sounds.GUN_BOLT_OPEN, 0.5F, 1F);
			if(timer == 41) GunSounds.play(entity, Rifle50Sounds.GUN_BOLT_CLOSE, 0.5F, 1F);
		}

		if(type == GunAnimation.JAMMED) {
			if(timer == 5) GunSounds.play(entity, Rifle50Sounds.GUN_BOLT_OPEN, 0.5F, 1F);
			if(timer == 12) GunSounds.play(entity, Rifle50Sounds.GUN_BOLT_CLOSE, 0.5F, 1F);
			if(timer == 16) GunSounds.play(entity, Rifle50Sounds.GUN_BOLT_OPEN, 0.5F, 1F);
			if(timer == 23) GunSounds.play(entity, Rifle50Sounds.GUN_BOLT_CLOSE, 0.5F, 1F);
		}

		if(type == GunAnimation.INSPECT) {
			if(timer == 0) GunSounds.play(entity, Rifle50Sounds.GUN_REVOLVER_COCK, 0.5F, 1F);
			if(timer == 45) GunSounds.play(entity, Rifle50Sounds.GUN_REVOLVER_CLOSE, 0.5F, 1F);
		}
	};
	public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_M2 = (stack, ctx) -> {
		LivingEntity entity = ctx.entity;
		if(entity.level().isClientSide) return;
		GunAnimation type = ItemGunBaseNT.getLastAnim(stack, ctx.configIndex);
		int timer = ItemGunBaseNT.getAnimTimer(stack, ctx.configIndex);
		boolean aiming = ItemGunBaseNT.getIsAiming(stack);

		if(type == GunAnimation.EQUIP) {
			if(timer == 0) GunSounds.play(entity, Rifle50Sounds.TURRET_CIWS_RELOAD, 1F, 1F);
		}

		if(type == GunAnimation.CYCLE) {
			if(timer == 0) {
				GunNetwork.flash(entity);
				SpentCasing casing = ctx.config.getReceivers(stack)[0].getMagazine(stack).getCasing(stack, ctx.inventory);
				if(casing != null) CasingEffects.composeEffect(entity.level(), entity, 0.375, aiming ? 0 : -0.125, aiming ? 0 : -0.3125D, 0, 0.06, -0.18, 0.01, (float)entity.getRandom().nextGaussian() * 20F, 12.5F + (float)entity.getRandom().nextGaussian() * 7.5F, casing.getName());
			}
		}
	};
}
