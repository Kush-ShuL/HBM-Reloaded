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
public final class Rifle556Orchestras {
	public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_G3 = (stack, ctx) -> {
		LivingEntity entity = ctx.entity;
		if(entity.level().isClientSide) return;
		GunAnimation type = ItemGunBaseNT.getLastAnim(stack, ctx.configIndex);
		int timer = ItemGunBaseNT.getAnimTimer(stack, ctx.configIndex);
		boolean scoped = stack.getItem() == Rifle556Guns.gun_g3_zebra || XWeaponModManager.hasUpgrade(stack, 0, 202);
		boolean aiming = ItemGunBaseNT.getIsAiming(stack) && !scoped;

		if(type == GunAnimation.CYCLE) {
			if(timer == 0) {
				SpentCasing casing = ctx.config.getReceivers(stack)[0].getMagazine(stack).getCasing(stack, ctx.inventory);
				if(casing != null) CasingEffects.composeEffect(entity.level(), entity, 0.5, aiming ? 0 : -0.125, aiming ? 0 : -0.25D, 0, 0.18, -0.12, 0.01, (float)entity.getRandom().nextGaussian() * 5F, 12.5F + (float)entity.getRandom().nextFloat() * 5F, casing.getName());
				GunNetwork.flash(entity);
			}
		}
		if(type == GunAnimation.CYCLE_DRY) {
			if(timer == 0) GunSounds.play(entity, Rifle556Sounds.GUN_DRY_FIRE, 1F, 0.8F);
			if(timer == 5) GunSounds.play(entity, Rifle556Sounds.GUN_PISTOL_COCK, 1F, 0.9F);

		}
		if(type == GunAnimation.RELOAD) {
			if(timer == 2) GunSounds.play(entity, Rifle556Sounds.GUN_MAG_REMOVE, 1F, 1F);
			if(timer == 4) GunSounds.play(entity, Rifle556Sounds.GUN_REVOLVER_CLOSE, 1F, 0.9F);
			if(timer == 32) GunSounds.play(entity, Rifle556Sounds.GUN_MAG_INSERT, 1F, 1F);
			if(timer == 36) GunSounds.play(entity, Rifle556Sounds.GUN_REVOLVER_CLOSE, 1F, 1F);
		}
		if(type == GunAnimation.INSPECT) {
			if(timer == 2) GunSounds.play(entity, Rifle556Sounds.GUN_MAG_REMOVE, 1F, 1F);
			if(timer == 28) GunSounds.play(entity, Rifle556Sounds.GUN_MAG_INSERT, 1F, 1F);
		}
		if(type == GunAnimation.JAMMED) {
			if(timer == 16) GunSounds.play(entity, Rifle556Sounds.GUN_REVOLVER_CLOSE, 1F, 0.9F);
			if(timer == 20) GunSounds.play(entity, Rifle556Sounds.GUN_REVOLVER_CLOSE, 1F, 1F);
			if(timer == 26) GunSounds.play(entity, Rifle556Sounds.GUN_REVOLVER_CLOSE, 1F, 0.9F);
			if(timer == 30) GunSounds.play(entity, Rifle556Sounds.GUN_REVOLVER_CLOSE, 1F, 1F);
		}
	};
	public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_STG77 = (stack, ctx) -> {
		LivingEntity entity = ctx.entity;
		if(entity.level().isClientSide) return;
		GunAnimation type = ItemGunBaseNT.getLastAnim(stack, ctx.configIndex);
		int timer = ItemGunBaseNT.getAnimTimer(stack, ctx.configIndex);
		boolean aiming = ItemGunBaseNT.getIsAiming(stack);

		if(GunClientSettings.legacyAnimations()) {
			if(type == GunAnimation.CYCLE) {
				if(timer == 0) {
					GunNetwork.flash(entity);
					SpentCasing casing = ctx.config.getReceivers(stack)[0].getMagazine(stack).getCasing(stack, ctx.inventory);
					if(casing != null) CasingEffects.composeEffect(entity.level(), entity, aiming ? 0.125 : 0.125, aiming ? -0.125 : -0.25, aiming ? -0.125 : -0.25D, 0, 0.18, -0.12, 0.01, (float)entity.getRandom().nextGaussian() * 5F, 7.5F + entity.getRandom().nextFloat() * 5F, casing.getName());
				}
				if(timer == 40) GunSounds.play(entity, Rifle556Sounds.GUN_DRY_FIRE, 0.25F, 1.25F);
			}
			if(type == GunAnimation.CYCLE_DRY) {
				if(timer == 0) GunSounds.play(entity, Rifle556Sounds.GUN_DRY_FIRE, 1F, 0.8F);
				if(timer == 5) GunSounds.play(entity, Rifle556Sounds.GUN_PISTOL_COCK, 1F, 0.9F);
				if(timer == 40) GunSounds.play(entity, Rifle556Sounds.GUN_DRY_FIRE, 0.25F, 1.25F);
			}
			if(type == GunAnimation.RELOAD) {
				if(timer == 0) GunSounds.play(entity, Rifle556Sounds.GUN_REVOLVER_CLOSE, 1F, 0.9F);
				if(timer == 10) GunSounds.play(entity, Rifle556Sounds.GUN_MAG_REMOVE, 1F, 1F);
				if(timer == 24) GunSounds.play(entity, Rifle556Sounds.GUN_MAG_INSERT, 1F, 1F);
				if(timer == 34) GunSounds.play(entity, Rifle556Sounds.GUN_REVOLVER_CLOSE, 1F, 1F);
			}
			if(type == GunAnimation.INSPECT) {
				if(timer == 0) GunSounds.play(entity, Rifle556Sounds.GUN_REVOLVER_CLOSE, 1F, 0.9F);
				if(timer == 10) GunSounds.play(entity, Rifle556Sounds.GUN_MAG_SMALL_REMOVE, 1F, 1F);

				if(timer == 114) GunSounds.play(entity, Rifle556Sounds.GUN_MAG_SMALL_INSERT, 1F, 1F);
				if(timer == 124) GunSounds.play(entity, Rifle556Sounds.GUN_REVOLVER_CLOSE, 1F, 1F);
			}
		} else {
			if(type == GunAnimation.CYCLE) {
				if(timer == 0) {
					GunNetwork.flash(entity);
					SpentCasing casing = ctx.config.getReceivers(stack)[0].getMagazine(stack).getCasing(stack, ctx.inventory);
					if(casing != null) CasingEffects.composeEffect(entity.level(), entity, aiming ? 0.125 : 0.25, aiming ? -0.125 : -0.25, aiming ? -0.125 : -0.25D, 0, 0.18, -0.12, 0.01, (float)entity.getRandom().nextGaussian() * 5F, 7.5F + entity.getRandom().nextFloat() * 5F, casing.getName());
				}
				if(timer == 40) GunSounds.play(entity, Rifle556Sounds.GUN_DRY_FIRE, 0.25F, 1.25F);
			}
			if(type == GunAnimation.CYCLE_DRY) {
				if(timer == 0) GunSounds.play(entity, Rifle556Sounds.GUN_DRY_FIRE, 1F, 0.8F);
				if(timer == 5) GunSounds.play(entity, Rifle556Sounds.GUN_PISTOL_COCK, 1F, 0.9F);
				if(timer == 40) GunSounds.play(entity, Rifle556Sounds.GUN_DRY_FIRE, 0.25F, 1.25F);
			}
			if(type == GunAnimation.RELOAD) {
				if(timer == 0) GunSounds.play(entity, Rifle556Sounds.GUN_REVOLVER_CLOSE, 1F, 0.9F);
				if(timer == 16) GunSounds.play(entity, Rifle556Sounds.GUN_MAG_REMOVE, 1F, 1F);
				if(timer == 32) GunSounds.play(entity, Rifle556Sounds.GUN_IMPACT, 0.25F, 1.25F);
				if(timer == 38) GunSounds.play(entity, Rifle556Sounds.GUN_MAG_INSERT, 1F, 1F);
				if(timer == 43) GunSounds.play(entity, Rifle556Sounds.GUN_REVOLVER_CLOSE, 1F, 1F);
			}
			if(type == GunAnimation.INSPECT) {
				if(timer == 0) GunSounds.play(entity, Rifle556Sounds.GUN_REVOLVER_CLOSE, 1F, 0.9F);
				if(timer == 11) GunSounds.play(entity, Rifle556Sounds.GUN_MAG_SMALL_REMOVE, 1F, 1F);

				if(timer == 72) GunSounds.play(entity, Rifle556Sounds.GUN_MAG_SMALL_INSERT, 1F, 1F);
				if(timer == 84) GunSounds.play(entity, Rifle556Sounds.GUN_REVOLVER_CLOSE, 1F, 1F);
			}
		}
	};
}
