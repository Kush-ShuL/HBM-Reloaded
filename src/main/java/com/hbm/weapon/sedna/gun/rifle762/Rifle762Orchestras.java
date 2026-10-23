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
public final class Rifle762Orchestras {
	public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_CARBINE = (stack, ctx) -> {
		LivingEntity entity = ctx.entity;
		if(entity.level().isClientSide) return;
		GunAnimation type = ItemGunBaseNT.getLastAnim(stack, ctx.configIndex);
		int timer = ItemGunBaseNT.getAnimTimer(stack, ctx.configIndex);
		boolean aiming = ItemGunBaseNT.getIsAiming(stack);

		if(type == GunAnimation.CYCLE) {
			if(timer == 0) GunNetwork.flash(entity);
			if(timer == 1) {
				SpentCasing casing = ctx.config.getReceivers(stack)[0].getMagazine(stack).getCasing(stack, ctx.inventory);
				if(casing != null) CasingEffects.composeEffect(entity.level(), entity, 0.3125, aiming ? 0 : -0.125, aiming ? 0 : -0.25D, 0, 0.21, -0.06, 0.01, -10F + (float)entity.getRandom().nextGaussian() * 2.5F, 2.5F + (float)entity.getRandom().nextGaussian() * 2F, casing.getName(), true, 60, 0.5D, 20);
			}
		}
		if(type == GunAnimation.CYCLE_DRY) {
			if(timer == 2) GunSounds.play(entity, Rifle762Sounds.GUN_DRY_FIRE, 1F, 1F);
			if(timer == 8) GunSounds.play(entity, Rifle762Sounds.GUN_PISTOL_COCK, 1F, 0.8F);
		}
		if(type == GunAnimation.RELOAD) {
			if(timer == 2) GunSounds.play(entity, Rifle762Sounds.GUN_MAG_REMOVE, 1F, 1F);
			if(timer == 26) GunSounds.play(entity, Rifle762Sounds.GUN_MAG_INSERT, 1F, 1F);
		}
		if(type == GunAnimation.RELOAD_END) {
			if(timer == 2) GunSounds.play(entity, Rifle762Sounds.GUN_PISTOL_COCK, 1F, 0.8F);
		}
		if(type == GunAnimation.JAMMED) {
			if(timer == 2) GunSounds.play(entity, Rifle762Sounds.GUN_PISTOL_COCK, 1F, 0.8F);
			if(timer == 31) GunSounds.play(entity, Rifle762Sounds.GUN_PISTOL_COCK, 1F, 0.8F);
		}
		if(type == GunAnimation.INSPECT) {
			if(timer == 6) GunSounds.play(entity, Rifle762Sounds.GUN_REVOLVER_CLOSE, 1F, 1F);
			if(timer == 30) GunSounds.play(entity, Rifle762Sounds.GUN_REVOLVER_CLOSE, 1F, 0.9F);
		}
	};
	public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_MINIGUN = (stack, ctx) -> {
		LivingEntity entity = ctx.entity;
		if(entity.level().isClientSide) return;
		GunAnimation type = ItemGunBaseNT.getLastAnim(stack, ctx.configIndex);
		int timer = ItemGunBaseNT.getAnimTimer(stack, ctx.configIndex);
		boolean aiming = ItemGunBaseNT.getIsAiming(stack);

		if(type == GunAnimation.CYCLE) {
			if(timer == 0) {
				GunNetwork.flash(entity);
				int rounds = XWeaponModManager.hasUpgrade(stack, ctx.configIndex, 208) ? 3 : 1;
				for(int i = 0; i < rounds; i++) {
					SpentCasing casing = ctx.config.getReceivers(stack)[0].getMagazine(stack).getCasing(stack, ctx.inventory);
					if(casing != null) CasingEffects.composeEffect(entity.level(), entity, aiming ? 0.125 : 0.5, aiming ? -0.125 : -0.25, aiming ? -0.25 : -0.5D, 0, 0.18, -0.12, 0.01, (float)entity.getRandom().nextGaussian() * 15F, (float)entity.getRandom().nextGaussian() * 15F, casing.getName());
				}
			}
			if(timer == (XWeaponModManager.hasUpgrade(stack, 0, 207) ? 3 : 1)) GunSounds.play(entity, Rifle762Sounds.GUN_REVOLVER_SPIN, 1F, 0.75F);
		}
		if(type == GunAnimation.CYCLE_DRY) {
			if(timer == 0) GunSounds.play(entity, Rifle762Sounds.GUN_DRY_FIRE, 1F, 0.75F);
			if(timer == 1) GunSounds.play(entity, Rifle762Sounds.GUN_REVOLVER_SPIN, 1F, 0.75F);
		}
		if(type == GunAnimation.RELOAD) {
			if(timer == 0) GunSounds.play(entity, Rifle762Sounds.GUN_REVOLVER_SPIN, 1F, 0.75F);
		}
		if(type == GunAnimation.INSPECT) {
			if(timer == 0) GunSounds.play(entity, Rifle762Sounds.GUN_REVOLVER_SPIN, 1F, 0.75F);
		}
	};
	public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_MINIGUN_DUAL = (stack, ctx) -> {
		LivingEntity entity = ctx.entity;
		if(entity.level().isClientSide) return;
		GunAnimation type = ItemGunBaseNT.getLastAnim(stack, ctx.configIndex);
		int timer = ItemGunBaseNT.getAnimTimer(stack, ctx.configIndex);

		if(type == GunAnimation.CYCLE) {
			if(timer == 0) {
				GunNetwork.flash(entity);
				int index = ctx.configIndex == 0 ? -1 : 1;
				int rounds = XWeaponModManager.hasUpgrade(stack, ctx.configIndex, 208) ? 3 : 1;
				for(int i = 0; i < rounds; i++) {
					SpentCasing casing = ctx.config.getReceivers(stack)[0].getMagazine(stack).getCasing(stack, ctx.inventory);
					if(casing != null) CasingEffects.composeEffect(entity.level(), entity, 0.25, -0.25, -0.5D * index, 0, 0.18, -0.12 * index, 0.01, (float)entity.getRandom().nextGaussian() * 15F, (float)entity.getRandom().nextGaussian() * 15F, casing.getName());
				}
			}
			if(timer == (XWeaponModManager.hasUpgrade(stack, 0, 207) ? 3 : 1)) GunSounds.play(entity, Rifle762Sounds.GUN_REVOLVER_SPIN, 1F, 0.75F);
		}
		if(type == GunAnimation.CYCLE_DRY) {
			if(timer == 0) GunSounds.play(entity, Rifle762Sounds.GUN_DRY_FIRE, 1F, 0.75F);
			if(timer == 1) GunSounds.play(entity, Rifle762Sounds.GUN_REVOLVER_SPIN, 1F, 0.75F);
		}
		if(type == GunAnimation.RELOAD) {
			if(timer == 0) GunSounds.play(entity, Rifle762Sounds.GUN_REVOLVER_SPIN, 1F, 0.75F);
		}
		if(type == GunAnimation.INSPECT) {
			if(timer == 0) GunSounds.play(entity, Rifle762Sounds.GUN_REVOLVER_SPIN, 1F, 0.75F);
		}
	};
	public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_MAS36 = (stack, ctx) -> {
		LivingEntity entity = ctx.entity;
		if(entity.level().isClientSide) return;
		GunAnimation type = ItemGunBaseNT.getLastAnim(stack, ctx.configIndex);
		int timer = ItemGunBaseNT.getAnimTimer(stack, ctx.configIndex);
		boolean aiming = ItemGunBaseNT.getIsAiming(stack) && !XWeaponModManager.hasUpgrade(stack, 0, 202);

		if(type == GunAnimation.EQUIP) {
			if(timer == 10) GunSounds.play(entity, Rifle762Sounds.GUN_LATCH_OPEN, 1F, 1F);
			if(timer == 18) GunSounds.play(entity, Rifle762Sounds.GUN_REVOLVER_CLOSE, 1F, 1F);
		}

		if(type == GunAnimation.CYCLE) {
			if(timer == 0) GunNetwork.flash(entity);
			if(timer == 7) GunSounds.play(entity, Rifle762Sounds.GUN_BOLT_OPEN, 0.5F, 1F);
			if(timer == 16) GunSounds.play(entity, Rifle762Sounds.GUN_BOLT_CLOSE, 0.5F, 1F);
			if(timer == 12) {
				SpentCasing casing = ctx.config.getReceivers(stack)[0].getMagazine(stack).getCasing(stack, ctx.inventory);
				if(casing != null) CasingEffects.composeEffect(entity.level(), entity,
						0.375, aiming ? 0 : -0.125, aiming ? 0 : -0.25D,
						-0.05, 0.2, -0.025,
						0.01, -10F + (float) entity.getRandom().nextGaussian() * 10F, (float) entity.getRandom().nextGaussian() * 12.5F, casing.getName(), true, 60, 0.5D, 10);
			}
		}

		if(type == GunAnimation.CYCLE_DRY) {
			if(timer == 0) GunSounds.play(entity, Rifle762Sounds.GUN_DRY_FIRE, 1F, 0.75F);
			if(timer == 7) GunSounds.play(entity, Rifle762Sounds.GUN_BOLT_OPEN, 0.5F, 1F);
			if(timer == 16) GunSounds.play(entity, Rifle762Sounds.GUN_BOLT_CLOSE, 0.5F, 1F);
		}

		if(type == GunAnimation.RELOAD) {
			if(timer == 0) GunSounds.play(entity, Rifle762Sounds.GUN_BOLT_OPEN, 1F, 1F);
			if(timer == 20) GunSounds.play(entity, Rifle762Sounds.GUN_RIFLE_COCK, 1F, 1F);
			if(timer == 36) GunSounds.play(entity, Rifle762Sounds.GUN_BOLT_CLOSE, 1F, 1F);
		}

		if(type == GunAnimation.JAMMED) {
			if(timer == 5) GunSounds.play(entity, Rifle762Sounds.GUN_BOLT_OPEN, 0.5F, 1F);
			if(timer == 12) GunSounds.play(entity, Rifle762Sounds.GUN_BOLT_CLOSE, 0.5F, 1F);
			if(timer == 16) GunSounds.play(entity, Rifle762Sounds.GUN_BOLT_OPEN, 0.5F, 1F);
			if(timer == 23) GunSounds.play(entity, Rifle762Sounds.GUN_BOLT_CLOSE, 0.5F, 1F);
		}

		if(type == GunAnimation.INSPECT) {
			if(timer == 0) GunSounds.play(entity, Rifle762Sounds.GUN_BOLT_OPEN, 0.5F, 1F);
			if(timer == 17) GunSounds.play(entity, Rifle762Sounds.GUN_BOLT_CLOSE, 0.5F, 1F);
		}
	};
}
