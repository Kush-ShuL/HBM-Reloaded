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
/** Exact source sound/ejection/magazine ticks. */
public final class PistolOrchestras {
	public static BiConsumer<ItemStack, LambdaContext> DEBUG_ORCHESTRA = (stack, ctx) -> {
		LivingEntity entity = ctx.entity;
		if(entity.level().isClientSide) return;
		GunAnimation type = ItemGunBaseNT.getLastAnim(stack, ctx.configIndex);
		int timer = ItemGunBaseNT.getAnimTimer(stack, ctx.configIndex);

		if(type == GunAnimation.RELOAD) {
			if(timer == 3) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_COCK, 1F, 1F);
			if(timer == 10) GunSounds.play(entity, PistolSounds.GUN_MAG_SMALL_REMOVE, 1F, 1F);
			if(timer == 34) GunSounds.play(entity, PistolSounds.GUN_MAG_SMALL_INSERT, 1F, 1F);
			if(timer == 40) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_CLOSE, 1F, 1F);

			if(timer == 16) {
				Receiver rec = ctx.config.getReceivers(stack)[0];
				IMagazine mag = rec.getMagazine(stack);
				SpentCasing casing = mag.getCasing(stack, ctx.inventory);
				if(casing != null) for(int i = 0; i < mag.getCapacity(stack); i++) CasingEffects.composeEffect(entity.level(), entity, 0.25, -0.125, -0.125, -0.05, 0, 0, 0.01, casing.getName());
			}
		}
		if(type == GunAnimation.CYCLE) {
			if(timer == 0) GunNetwork.flash(entity);
			if(timer == 11) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_COCK, 1F, 1F);
		}
		if(type == GunAnimation.CYCLE_DRY) {
			if(timer == 2) GunSounds.play(entity, PistolSounds.GUN_DRY_FIRE, 1F, 1F);
			if(timer == 11) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_COCK, 1F, 1F);
		}
		if(type == GunAnimation.INSPECT) {
			if(timer == 3) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_COCK, 1F, 1F);
			if(timer == 16) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_CLOSE, 1F, 1F);
		}
	};
	public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_AM180 = (stack, ctx) -> {
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
					if(casing != null) CasingEffects.composeEffect(entity.level(), entity, 0.4375, aiming ? 0 : -0.125, aiming ? 0 : -0.25D, 0, -0.06, 0, 0.01, (float)entity.getRandom().nextGaussian() * 10F, (float)entity.getRandom().nextGaussian() * 10F, casing.getName());
				}
			}
			if(type == GunAnimation.CYCLE_DRY) {
				if(timer == 0) GunSounds.play(entity, PistolSounds.GUN_DRY_FIRE, 1F, 1F);
				if(timer == 6) GunSounds.play(entity, PistolSounds.GUN_PISTOL_COCK, 1F, 0.9F);
			}
			if(type == GunAnimation.RELOAD) {
				if(timer == 2) GunSounds.play(entity, PistolSounds.GUN_MAG_REMOVE, 1F, 1F);
				if(timer == 20) GunSounds.play(entity, PistolSounds.GUN_IMPACT, 0.25F, 1F);
				if(timer == 32) GunSounds.play(entity, PistolSounds.GUN_MAG_INSERT, 1F, 1F);
				if(timer == 40) GunSounds.play(entity, PistolSounds.GUN_PISTOL_COCK, 1F, 0.9F);
			}
			if(type == GunAnimation.JAMMED) {
				if(timer == 15) GunSounds.play(entity, PistolSounds.GUN_PISTOL_COCK, 1F, 0.8F);
			}
			if(type == GunAnimation.INSPECT) {
				if(timer == 2) GunSounds.play(entity, PistolSounds.GUN_MAG_REMOVE, 1F, 1F);
				if(timer == 35) GunSounds.play(entity, PistolSounds.GUN_MAG_INSERT, 1F, 1F);
			}
		} else {
			if(type == GunAnimation.CYCLE) {
				if(timer == 0) {
					GunNetwork.flash(entity);
					SpentCasing casing = ctx.config.getReceivers(stack)[0].getMagazine(stack).getCasing(stack, ctx.inventory);
					if(casing != null) CasingEffects.composeEffect(entity.level(), entity, 0.4375, aiming ? 0 : -0.125, aiming ? 0 : -0.25D, 0, -0.06, 0, 0.01, (float)entity.getRandom().nextGaussian() * 10F, (float)entity.getRandom().nextGaussian() * 10F, casing.getName());
				}
			}
			if(type == GunAnimation.CYCLE_DRY) {
				if(timer == 0) GunSounds.play(entity, PistolSounds.GUN_DRY_FIRE, 1F, 1F);
				if(timer == 6) GunSounds.play(entity, PistolSounds.GUN_PISTOL_COCK, 1F, 0.9F);
			}
			if(type == GunAnimation.RELOAD) {
				if(timer == 6) GunSounds.play(entity, PistolSounds.GUN_MAG_REMOVE, 1F, 1F);
				if(timer == 26) GunSounds.play(entity, PistolSounds.GUN_IMPACT, 0.25F, 1F);
				if(timer == 48) GunSounds.play(entity, PistolSounds.GUN_MAG_INSERT, 1F, 1F);
				if(timer == 54) GunSounds.play(entity, PistolSounds.GUN_PISTOL_COCK, 1F, 0.9F);
			}
			if(type == GunAnimation.JAMMED) {
				if(timer == 6) GunSounds.play(entity, PistolSounds.GUN_PISTOL_COCK, 1F, 0.8F);
				if(timer == 20) GunSounds.play(entity, PistolSounds.GUN_PISTOL_COCK, 1F, 1.0F);
			}
			if(type == GunAnimation.INSPECT) {
				if(timer == 6) GunSounds.play(entity, PistolSounds.GUN_MAG_REMOVE, 1F, 1F);
				if(timer == 53) GunSounds.play(entity, PistolSounds.GUN_MAG_INSERT, 1F, 1F);
			}
		}
	};
	public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_ATLAS = (stack, ctx) -> {
		LivingEntity entity = ctx.entity;
		if(entity.level().isClientSide) return;
		GunAnimation type = ItemGunBaseNT.getLastAnim(stack, ctx.configIndex);
		int timer = ItemGunBaseNT.getAnimTimer(stack, ctx.configIndex);

		if(type == GunAnimation.RELOAD) {
			if(timer == 2) GunSounds.play(entity, PistolSounds.GUN_MAG_SMALL_REMOVE, 1F, 1F);
			if(timer == 36) GunSounds.play(entity, PistolSounds.GUN_MAG_SMALL_INSERT, 1F, 1F);
			if(timer == 44) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_CLOSE, 1F, 1F);
		}
		if(type == GunAnimation.CYCLE) {
			if(timer == 0) GunNetwork.flash(entity);
			if(timer == 5) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_COCK, 1F, 0.9F);
		}
		if(type == GunAnimation.CYCLE_DRY) {
			if(timer == 2) GunSounds.play(entity, PistolSounds.GUN_DRY_FIRE, 1F, 1F);
			if(timer == 5) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_COCK, 1F, 0.9F);
		}
		if(type == GunAnimation.INSPECT) {
			if(timer == 2) GunSounds.play(entity, PistolSounds.GUN_MAG_SMALL_REMOVE, 1F, 1F);
			if(timer == 24) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_CLOSE, 1F, 1F);
		}
		if(type == GunAnimation.JAMMED) {
			if(timer == 12) GunSounds.play(entity, PistolSounds.GUN_MAG_SMALL_REMOVE, 1F, 1F);
			if(timer == 34) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_CLOSE, 1F, 1F);
		}
	};
	public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_DANI = (stack, ctx) -> {
		LivingEntity entity = ctx.entity;
		if(entity.level().isClientSide) return;
		GunAnimation type = ItemGunBaseNT.getLastAnim(stack, ctx.configIndex);
		int timer = ItemGunBaseNT.getAnimTimer(stack, ctx.configIndex);

		if(type == GunAnimation.RELOAD) {
			if(timer == 2) GunSounds.play(entity, PistolSounds.GUN_MAG_SMALL_REMOVE, 1F, 1F);
			if(timer == 36) GunSounds.play(entity, PistolSounds.GUN_MAG_SMALL_INSERT, 1F, 1F);
			if(timer == 44) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_CLOSE, 1F, 1F);
		}
		if(type == GunAnimation.CYCLE) {
			if(timer == 0) GunNetwork.flash(entity);
			if(timer == 5) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_COCK, 1F, 0.9F);
		}
		if(type == GunAnimation.CYCLE_DRY) {
			if(timer == 2) GunSounds.play(entity, PistolSounds.GUN_DRY_FIRE, 1F, 1F);
			if(timer == 5) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_COCK, 1F, 0.9F);
		}
		if(type == GunAnimation.INSPECT) {
			if(timer == 2) GunSounds.play(entity, PistolSounds.GUN_MAG_SMALL_REMOVE, 1F, 1F);
			if(timer == 24) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_CLOSE, 1F, 1F);
		}
		if(type == GunAnimation.JAMMED) {
			if(timer == 12) GunSounds.play(entity, PistolSounds.GUN_MAG_SMALL_REMOVE, 1F, 1F);
			if(timer == 34) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_CLOSE, 1F, 1F);
		}
	};
	public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_GREASEGUN = (stack, ctx) -> {
		LivingEntity entity = ctx.entity;
		if(entity.level().isClientSide) return;
		GunAnimation type = ItemGunBaseNT.getLastAnim(stack, ctx.configIndex);
		int timer = ItemGunBaseNT.getAnimTimer(stack, ctx.configIndex);
		boolean aiming = ItemGunBaseNT.getIsAiming(stack);

		if(type == GunAnimation.EQUIP) {
			if(timer == 5) GunSounds.play(entity, PistolSounds.GUN_LATCH_OPEN, 1F, 1F);
		}
		if(type == GunAnimation.CYCLE) {
			if(timer == 0) GunNetwork.flash(entity);
			if(timer == 2) {
				SpentCasing casing = ctx.config.getReceivers(stack)[0].getMagazine(stack).getCasing(stack, ctx.inventory);
				if(casing != null) CasingEffects.composeEffect(entity.level(), entity, 0.55, aiming ? 0 : -0.125, aiming ? 0 : -0.25D, 0, 0.18, -0.12, 0.01, -7.5F + (float)entity.getRandom().nextGaussian() * 5F, 12F + (float)entity.getRandom().nextGaussian() * 5F, casing.getName());
			}
		}
		if(type == GunAnimation.CYCLE_DRY) {
			if(timer == 0) GunSounds.play(entity, PistolSounds.GUN_DRY_FIRE, 1F, 0.8F);
			if(timer == 11) GunSounds.play(entity, PistolSounds.GUN_PISTOL_COCK, 1F, 0.8F);

		}
		if(type == GunAnimation.RELOAD) {
			if(timer == 2) GunSounds.play(entity, PistolSounds.GUN_MAG_REMOVE, 1F, 1F);
			if(timer == 24) GunSounds.play(entity, PistolSounds.GUN_MAG_INSERT, 1F, 1F);
			if(timer == 36) GunSounds.play(entity, PistolSounds.GUN_PISTOL_COCK, 1F, 0.8F);
		}
		if(type == GunAnimation.INSPECT) {
			if(timer == 5) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_COCK, 1F, 0.8F);
			if(timer == 26) GunSounds.play(entity, PistolSounds.GUN_MAG_SMALL_INSERT, 1F, 1.25F);
		}
		if(type == GunAnimation.JAMMED) {
			if(timer == 11) GunSounds.play(entity, PistolSounds.GUN_PISTOL_COCK, 1F, 0.8F);
			if(timer == 26) GunSounds.play(entity, PistolSounds.GUN_PISTOL_COCK, 1F, 0.8F);
		}
	};
	public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_HANGMAN = (stack, ctx) -> {
		LivingEntity entity = ctx.entity;
		if(entity.level().isClientSide) return;
		GunAnimation type = ItemGunBaseNT.getLastAnim(stack, ctx.configIndex);
		int timer = ItemGunBaseNT.getAnimTimer(stack, ctx.configIndex);
		
		if(type == GunAnimation.CYCLE) {
			if(timer == 0) GunNetwork.flash(entity);
		}
		if(type == GunAnimation.CYCLE_DRY) {
			if(timer == 0) GunSounds.play(entity, PistolSounds.GUN_DRY_FIRE, 1F, 1F);
		}

		if(type == GunAnimation.RELOAD) {

			if(timer == 0) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_COCK, 1F, 0.8F);
			if(timer == 5) GunSounds.play(entity, PistolSounds.GUN_MAG_SMALL_REMOVE, 1F, 0.8F);
			if(timer == 25) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_CLOSE, 1F, 1F);
			if(timer == 35) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_COCK, 1F, 0.75F);

			if(timer == 10) {
				Receiver rec = ctx.config.getReceivers(stack)[0];
				IMagazine mag = rec.getMagazine(stack);
				SpentCasing casing = mag.getCasing(stack, ctx.inventory);
				if(casing != null) for(int i = 0; i < mag.getCapacity(stack); i++) CasingEffects.composeEffect(entity.level(), entity, 0.25, -0.25, -0.125, -0.05, 0, 0, 0.01, -6.5F + (float)entity.getRandom().nextGaussian() * 3F, (float)entity.getRandom().nextGaussian() * 5F, casing.getName());
			}
		}

		if(type == GunAnimation.INSPECT && timer == 16) PistolMelee.smack(ctx,XFactory44.GUN_HANGMAN_SMACK_DAMAGE.get());

		if(type == GunAnimation.JAMMED) {
			if(timer == 10) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_COCK, 1F, 0.8F);
			if(timer == 15) GunSounds.play(entity, PistolSounds.GUN_MAG_SMALL_REMOVE, 1F, 0.8F);
			if(timer == 20) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_CLOSE, 1F, 1F);
			if(timer == 25) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_COCK, 1F, 0.75F);
		}
	};
	public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_HENRY = (stack, ctx) -> {
		LivingEntity entity = ctx.entity;
		if(entity.level().isClientSide) return;
		GunAnimation type = ItemGunBaseNT.getLastAnim(stack, ctx.configIndex);
		int timer = ItemGunBaseNT.getAnimTimer(stack, ctx.configIndex);
		boolean aiming = ItemGunBaseNT.getIsAiming(stack);

		if(type == GunAnimation.RELOAD) {
			if(timer == 8) GunSounds.play(entity, PistolSounds.GUN_MAG_SMALL_REMOVE, 1F, 1F);
			if(timer == 16) GunSounds.play(entity, PistolSounds.GUN_MAG_SMALL_INSERT, 1F, 1F);
		}
		if(type == GunAnimation.RELOAD_CYCLE) {
			if(timer == 0) GunSounds.play(entity, PistolSounds.GUN_MAG_SMALL_INSERT, 1F, 1F);
		}
		if(type == GunAnimation.RELOAD_END) {
			if(timer == 0) GunSounds.play(entity, PistolSounds.GUN_MAG_SMALL_REMOVE, 1F, 0.9F);
			if(timer == 12 && ctx.config.getReceivers(stack)[0].getMagazine(stack).getAmountBeforeReload(stack) <= 0) GunSounds.play(entity, PistolSounds.GUN_LEVER_COCK, 1F, 1F);
		}
		if(type == GunAnimation.JAMMED) {
			if(timer == 0) GunSounds.play(entity, PistolSounds.GUN_MAG_SMALL_REMOVE, 1F, 0.9F);
			if(timer == 12) GunSounds.play(entity, PistolSounds.GUN_LEVER_COCK, 1F, 1F);
			if(timer == 36) GunSounds.play(entity, PistolSounds.GUN_LEVER_COCK, 1F, 1F);
			if(timer == 44) GunSounds.play(entity, PistolSounds.GUN_LEVER_COCK, 1F, 1F);
		}
		if(type == GunAnimation.CYCLE) {
			if(timer == 0) GunNetwork.flash(entity);
			if(timer == 14) {
				SpentCasing casing = ctx.config.getReceivers(stack)[0].getMagazine(stack).getCasing(stack, ctx.inventory);
				if(casing != null) CasingEffects.composeEffect(entity.level(), entity, 0.5, -0.125, aiming ? -0.125 : -0.375D, 0, 0.12, -0.12, 0.01, -7.5F + (float)entity.getRandom().nextGaussian() * 5F, (float)entity.getRandom().nextGaussian() * 1.5F, casing.getName(), true, 60, 0.5D, 20);
			}
			if(timer == 12) GunSounds.play(entity, PistolSounds.GUN_LEVER_COCK, 1F, 1F);
		}
		if(type == GunAnimation.CYCLE_DRY) {
			if(timer == 2) GunSounds.play(entity, PistolSounds.GUN_DRY_FIRE, 1F, 1F);
			if(timer == 12) GunSounds.play(entity, PistolSounds.GUN_LEVER_COCK, 1F, 1F);
		}
	};
	public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_LAG = (stack, ctx) -> {
		LivingEntity entity = ctx.entity;
		if(entity.level().isClientSide) return;
		GunAnimation type = ItemGunBaseNT.getLastAnim(stack, ctx.configIndex);
		int timer = ItemGunBaseNT.getAnimTimer(stack, ctx.configIndex);
		boolean aiming = ItemGunBaseNT.getIsAiming(stack);

		if(type == GunAnimation.CYCLE) {
			if(timer == 0) GunNetwork.flash(entity);
			if(timer == 1) {
				SpentCasing casing = ctx.config.getReceivers(stack)[0].getMagazine(stack).getCasing(stack, ctx.inventory);
				if(casing != null) CasingEffects.composeEffect(entity.level(), entity, 0.375, aiming ? 0 : -0.0625, aiming ? 0 : -0.25D, 0, 0.18, -0.12, 0.01, -10F + (float)entity.getRandom().nextGaussian() * 5F, 10F + entity.getRandom().nextFloat() * 10F, casing.getName());
			}
		}
		if(type == GunAnimation.CYCLE_DRY) {
			if(timer == 0) GunSounds.play(entity, PistolSounds.GUN_DRY_FIRE, 1F, 1F);
			if(timer == 8) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_COCK, 1F, 1F);

		}
		if(type == GunAnimation.RELOAD) {
			if(timer == 8) GunSounds.play(entity, PistolSounds.GUN_MAG_REMOVE, 1F, 1F);
			if(timer == 26) GunSounds.play(entity, PistolSounds.GUN_MAG_INSERT, 1F, 1F);
			if(timer == 40) GunSounds.play(entity, PistolSounds.GUN_PISTOL_COCK, 1F, 1F);
		}
		if(type == GunAnimation.JAMMED) {
			if(timer == 8) GunSounds.play(entity, PistolSounds.GUN_MAG_REMOVE, 1F, 1F);
			if(timer == 20) GunSounds.play(entity, PistolSounds.GUN_IMPACT, 0.5F, 1.6F);
			if(timer == 36) GunSounds.play(entity, PistolSounds.GUN_MAG_INSERT, 1F, 1F);
		}
	};
	public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_NOPIP = (stack, ctx) -> {
		LivingEntity entity = ctx.entity;
		if(entity.level().isClientSide) return;
		GunAnimation type = ItemGunBaseNT.getLastAnim(stack, ctx.configIndex);
		int timer = ItemGunBaseNT.getAnimTimer(stack, ctx.configIndex);

		if(type == GunAnimation.RELOAD) {
			if(timer == 3) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_COCK, 1F, 1F);
			if(timer == 10) GunSounds.play(entity, PistolSounds.GUN_MAG_SMALL_REMOVE, 1F, 1F);
			if(timer == 34) GunSounds.play(entity, PistolSounds.GUN_MAG_SMALL_INSERT, 1F, 1F);
			if(timer == 40) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_CLOSE, 1F, 1F);

			if(timer == 16) {
				Receiver rec = ctx.config.getReceivers(stack)[0];
				IMagazine mag = rec.getMagazine(stack);
				SpentCasing casing = mag.getCasing(stack, ctx.inventory);
				if(casing != null) for(int i = 0; i < mag.getCapacity(stack); i++) CasingEffects.composeEffect(entity.level(), entity, 0.25, -0.125, -0.125, -0.05, 0, 0, 0.01, -6.5F + (float)entity.getRandom().nextGaussian() * 3F, (float)entity.getRandom().nextGaussian() * 5F, casing.getName());
			}
		}
		if(type == GunAnimation.CYCLE) {
			if(timer == 0) GunNetwork.flash(entity);
			if(timer == 11) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_COCK, 1F, 1F);
		}
		if(type == GunAnimation.CYCLE_DRY) {
			if(timer == 2) GunSounds.play(entity, PistolSounds.GUN_DRY_FIRE, 1F, 1F);
			if(timer == 11) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_COCK, 1F, 1F);
		}
		if(type == GunAnimation.INSPECT) {
			if(timer == 3) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_COCK, 1F, 1F);
			if(timer == 16) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_CLOSE, 1F, 1F);
		}
	};
	public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_STAR_F = (stack, ctx) -> {
		LivingEntity entity = ctx.entity;
		if(entity.level().isClientSide) return;
		GunAnimation type = ItemGunBaseNT.getLastAnim(stack, ctx.configIndex);
		int timer = ItemGunBaseNT.getAnimTimer(stack, ctx.configIndex);
		boolean aiming = ItemGunBaseNT.getIsAiming(stack);

		if(type == GunAnimation.CYCLE) {
			if(timer == 0) {
				SpentCasing casing = ctx.config.getReceivers(stack)[0].getMagazine(stack).getCasing(stack, ctx.inventory);
				if(casing != null) CasingEffects.composeEffect(entity.level(), entity, 0.3125, aiming ? 0 : -0.125, aiming ? 0 : -0.1875D, 0, 0.18, -0.12, 0.01, (float)entity.getRandom().nextGaussian() * 5F, 12.5F + (float)entity.getRandom().nextFloat() * 5F, casing.getName());
				GunNetwork.flash(entity);
			}
		}
		if(type == GunAnimation.CYCLE_DRY) {
			if(timer == 0) GunSounds.play(entity, PistolSounds.GUN_DRY_FIRE, 1F, 0.9F);
			if(timer == 5) GunSounds.play(entity, PistolSounds.GUN_PISTOL_COCK, 1F, 1.1F);

		}
		if(type == GunAnimation.RELOAD) {
			if(timer == 5) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_CLOSE, 1F, 1F);
			if(timer == 5) GunSounds.play(entity, PistolSounds.GUN_MAG_REMOVE, 1F, 1F);
			if(timer == 22) GunSounds.play(entity, PistolSounds.GUN_MAG_INSERT, 1F, 1F);
			if(timer == 30) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_CLOSE, 1F, 1.1F);
		}
		if(type == GunAnimation.JAMMED) {
			if(timer == 15) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_CLOSE, 1F, 1F);
			if(timer == 19) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_CLOSE, 1F, 1.1F);
			if(timer == 23) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_CLOSE, 1F, 1F);
			if(timer == 27) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_CLOSE, 1F, 1.1F);
		}
		if(type == GunAnimation.INSPECT) {
			if(timer == 7) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_CLOSE, 1F, 1F);
			if(timer == 30) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_CLOSE, 1F, 1.1F);
		}
	};
	public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_STAR_F_AKIMBO = (stack, ctx) -> {
		LivingEntity entity = ctx.entity;
		if(entity.level().isClientSide) return;
		GunAnimation type = ItemGunBaseNT.getLastAnim(stack, ctx.configIndex);
		int timer = ItemGunBaseNT.getAnimTimer(stack, ctx.configIndex);
		boolean aiming = ItemGunBaseNT.getIsAiming(stack);

		if(type == GunAnimation.CYCLE) {
			if(timer == 0) {
				int side = ctx.configIndex == 0 ? -1 : 1;
				SpentCasing casing = ctx.config.getReceivers(stack)[0].getMagazine(stack).getCasing(stack, ctx.inventory);
				if(casing != null) CasingEffects.composeEffect(entity.level(), entity, 0.3125, aiming ? 0 : -0.125, aiming ? 0 : -0.1875D * side, 0, 0.18, -0.12 * side, 0.01, (float)entity.getRandom().nextGaussian() * 5F, 12.5F + (float)entity.getRandom().nextFloat() * 5F, casing.getName());
				GunNetwork.flash(entity);
			}
		}
		if(type == GunAnimation.CYCLE_DRY) {
			if(timer == 0) GunSounds.play(entity, PistolSounds.GUN_DRY_FIRE, 1F, 0.9F);
			if(timer == 5) GunSounds.play(entity, PistolSounds.GUN_PISTOL_COCK, 1F, 1.1F);

		}
		if(type == GunAnimation.RELOAD) {
			if(timer == 5) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_CLOSE, 1F, 1F);
			if(timer == 5) GunSounds.play(entity, PistolSounds.GUN_MAG_REMOVE, 1F, 1F);
			if(timer == 22) GunSounds.play(entity, PistolSounds.GUN_MAG_INSERT, 1F, 1F);
			if(timer == 30) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_CLOSE, 1F, 1.1F);
		}
		if(type == GunAnimation.JAMMED) {
			if(timer == 15) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_CLOSE, 1F, 1F);
			if(timer == 19) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_CLOSE, 1F, 1.1F);
			if(timer == 23) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_CLOSE, 1F, 1F);
			if(timer == 27) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_CLOSE, 1F, 1.1F);
		}
		if(type == GunAnimation.INSPECT) {
			if(timer == 7) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_CLOSE, 1F, 1F);
			if(timer == 30) GunSounds.play(entity, PistolSounds.GUN_REVOLVER_CLOSE, 1F, 1.1F);
		}
	};
	public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_UZI = (stack, ctx) -> {
		LivingEntity entity = ctx.entity;
		if(entity.level().isClientSide) return;
		GunAnimation type = ItemGunBaseNT.getLastAnim(stack, ctx.configIndex);
		int timer = ItemGunBaseNT.getAnimTimer(stack, ctx.configIndex);
		boolean aiming = ItemGunBaseNT.getIsAiming(stack);

		if(type == GunAnimation.EQUIP) {
			if(timer == 8) GunSounds.play(entity, PistolSounds.GUN_LATCH_OPEN, 1F, 1.25F);
		}
		if(type == GunAnimation.CYCLE) {
			if(timer == 0) GunNetwork.flash(entity);
			if(timer == 1) {
				SpentCasing casing = ctx.config.getReceivers(stack)[0].getMagazine(stack).getCasing(stack, ctx.inventory);
				if(casing != null) CasingEffects.composeEffect(entity.level(), entity, 0.375, aiming ? 0 : -0.125, aiming ? 0 : -0.25D, 0, 0.18, -0.12, 0.01, -2.5F + (float)entity.getRandom().nextGaussian() * 5F, 10F + (float)entity.getRandom().nextFloat() * 15F, casing.getName());
			}
		}
		if(type == GunAnimation.CYCLE_DRY) {
			if(timer == 0) GunSounds.play(entity, PistolSounds.GUN_DRY_FIRE, 1F, 1F);
			if(timer == 8) GunSounds.play(entity, PistolSounds.GUN_PISTOL_COCK, 1F, 1F);

		}
		if(type == GunAnimation.RELOAD) {
			if(timer == 4) GunSounds.play(entity, PistolSounds.GUN_MAG_REMOVE, 1F, 1F);
			if(timer == 26) GunSounds.play(entity, PistolSounds.GUN_MAG_INSERT, 1F, 1F);
			if(timer == 36) GunSounds.play(entity, PistolSounds.GUN_PISTOL_COCK, 1F, 1F);
		}
		if(type == GunAnimation.JAMMED) {
			if(timer == 17) GunSounds.play(entity, PistolSounds.GUN_PISTOL_COCK, 1F, 1F);
			if(timer == 31) GunSounds.play(entity, PistolSounds.GUN_PISTOL_COCK, 1F, 1F);
		}
	};
	public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_UZI_AKIMBO = (stack, ctx) -> {
		LivingEntity entity = ctx.entity;
		if(entity.level().isClientSide) return;
		GunAnimation type = ItemGunBaseNT.getLastAnim(stack, ctx.configIndex);
		int timer = ItemGunBaseNT.getAnimTimer(stack, ctx.configIndex);

		if(type == GunAnimation.EQUIP) {
			if(timer == 8) GunSounds.play(entity, PistolSounds.GUN_LATCH_OPEN, 1F, 1.25F);
		}
		if(type == GunAnimation.CYCLE) {
			if(timer == 0) GunNetwork.flash(entity);
			if(timer == 1) {
				int mult = ctx.configIndex == 0 ? -1 : 1;
				SpentCasing casing = ctx.config.getReceivers(stack)[0].getMagazine(stack).getCasing(stack, ctx.inventory);
				if(casing != null) CasingEffects.composeEffect(entity.level(), entity, 0.375, -0.125, -0.375D * mult, 0, 0.18, -0.12 * mult, 0.01, -2.5F + (float)entity.getRandom().nextGaussian() * 5F, (10F + (float)entity.getRandom().nextFloat() * 15F) * mult, casing.getName());
			}
		}
		if(type == GunAnimation.CYCLE_DRY) {
			if(timer == 0) GunSounds.play(entity, PistolSounds.GUN_DRY_FIRE, 1F, 1F);
			if(timer == 8) GunSounds.play(entity, PistolSounds.GUN_PISTOL_COCK, 1F, 1F);

		}
		if(type == GunAnimation.RELOAD) {
			if(timer == 4) GunSounds.play(entity, PistolSounds.GUN_MAG_REMOVE, 1F, 1F);
			if(timer == 26) GunSounds.play(entity, PistolSounds.GUN_MAG_INSERT, 1F, 1F);
			if(timer == 36) GunSounds.play(entity, PistolSounds.GUN_PISTOL_COCK, 1F, 1F);
		}
		if(type == GunAnimation.JAMMED) {
			if(timer == 17) GunSounds.play(entity, PistolSounds.GUN_PISTOL_COCK, 1F, 1F);
			if(timer == 31) GunSounds.play(entity, PistolSounds.GUN_PISTOL_COCK, 1F, 1F);
		}
	};
}
