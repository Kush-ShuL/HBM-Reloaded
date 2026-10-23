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
public final class KineticOrchestras {
 public static final BiConsumer<ItemStack,LambdaContext> ORCHESTRA_MARESLEG_SHORT=com.hbm.weapon.sedna.gun.legendary.LegendaryGunOrchestras.ORCHESTRA_MARESLEG_SHORT;
	public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_MARESLEG = (stack, ctx) -> {
		LivingEntity entity = ctx.entity;
		if(entity.level().isClientSide) return;
		GunAnimation type = ItemGunBaseNT.getLastAnim(stack, ctx.configIndex);
		int timer = ItemGunBaseNT.getAnimTimer(stack, ctx.configIndex);
		boolean aiming = ItemGunBaseNT.getIsAiming(stack);

		if(type == GunAnimation.RELOAD) {
			if(timer == 8) GunSounds.play(entity, KineticSounds.GUN_REVOLVER_COCK, 1F, 0.8F);
			if(timer == 16) GunSounds.play(entity, KineticSounds.GUN_SHOTGUN_LOAD, 1F, 1F);
		}
		if(type == GunAnimation.RELOAD_CYCLE) {
			if(timer == 0) GunSounds.play(entity, KineticSounds.GUN_SHOTGUN_LOAD, 1F, 1F);
		}
		if(type == GunAnimation.RELOAD_END) {
			if(timer == 2) GunSounds.play(entity, KineticSounds.GUN_REVOLVER_COCK, 1F, 0.7F);
		}
		if(type == GunAnimation.JAMMED) {
			if(timer == 2) GunSounds.play(entity, KineticSounds.GUN_REVOLVER_COCK, 1F, 0.7F);
			if(timer == 17) GunSounds.play(entity, KineticSounds.GUN_LEVER_COCK, 1F, 0.8F);
			if(timer == 29) GunSounds.play(entity, KineticSounds.GUN_LEVER_COCK, 1F, 0.8F);
		}
		if(type == GunAnimation.CYCLE) {
			if(timer == 0) GunNetwork.flash(entity);
			if(timer == 14) {
				SpentCasing casing = ctx.config.getReceivers(stack)[0].getMagazine(stack).getCasing(stack, ctx.inventory);
				if(casing != null) CasingEffects.composeEffect(entity.level(), entity, 0.3125, -0.125, aiming ? -0.125 : -0.375D, 0, 0.18, -0.12, 0.01, -10F + (float)entity.getRandom().nextGaussian() * 5F, (float)entity.getRandom().nextGaussian() * 2.5F, casing.getName(), true, 60, 0.5D, 20);
			}
			if(timer == 8) GunSounds.play(entity, KineticSounds.GUN_LEVER_COCK, 1F, 0.8F);
		}
		if(type == GunAnimation.CYCLE_DRY) {
			if(timer == 2) GunSounds.play(entity, KineticSounds.GUN_DRY_FIRE, 1F, 1F);
			if(timer == 8) GunSounds.play(entity, KineticSounds.GUN_LEVER_COCK, 1F, 0.8F);
		}
	};
	public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_MARESLEG_AKIMBO = (stack, ctx) -> {
		LivingEntity entity = ctx.entity;
		if(entity.level().isClientSide) return;
		GunAnimation type = ItemGunBaseNT.getLastAnim(stack, ctx.configIndex);
		int timer = ItemGunBaseNT.getAnimTimer(stack, ctx.configIndex);
		boolean aiming = ItemGunBaseNT.getIsAiming(stack);

		if(type == GunAnimation.CYCLE) {
			if(timer == 0) GunNetwork.flash(entity);
			if(timer == 14) {
				int offset = ctx.configIndex == 0 ? -1 : 1;
				SpentCasing casing = ctx.config.getReceivers(stack)[0].getMagazine(stack).getCasing(stack, ctx.inventory);
				if(casing != null) CasingEffects.composeEffect(entity.level(), entity, 0.3125, -0.125, aiming ? -0.125 * offset : -0.375D * offset, 0, -0.08, 0, 0.01, -15F + (float)entity.getRandom().nextGaussian() * 5F, (float)entity.getRandom().nextGaussian() * 2.5F, casing.getName(), true, 60, 0.5D, 20);
			}
			if(timer == 8) GunSounds.play(entity, KineticSounds.GUN_LEVER_COCK, 1F, 0.8F);
			return;
		}

		ORCHESTRA_MARESLEG_SHORT.accept(stack, ctx);
	};
	public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_LIBERATOR = (stack, ctx) -> {
		LivingEntity entity = ctx.entity;
		if(entity.level().isClientSide) return;
		GunAnimation type = ItemGunBaseNT.getLastAnim(stack, ctx.configIndex);
		int timer = ItemGunBaseNT.getAnimTimer(stack, ctx.configIndex);
		
		if(type == GunAnimation.CYCLE) {
			if(timer == 0) GunNetwork.flash(entity);
		}
		if(type == GunAnimation.RELOAD) {
			if(timer == 0) GunSounds.play(entity, KineticSounds.GUN_REVOLVER_COCK, 1F, 0.75F);
			if(timer == 4) {
				IMagazine mag = ctx.config.getReceivers(stack)[0].getMagazine(stack);
				int toEject = mag.getAmountAfterReload(stack) - mag.getAmount(stack, ctx.inventory);
				SpentCasing casing = mag.getCasing(stack, ctx.inventory);
				if(casing != null) for(int i = 0; i < toEject; i++) CasingEffects.composeEffect(entity.level(), entity, 0.625, -0.1875, -0.375D, -0.12, 0.18, 0, 0.01, -15F + (float)entity.getRandom().nextGaussian() * 7.5F, (float)entity.getRandom().nextGaussian() * 5F, casing.getName(), true, 60, 0.5D, 20);
			}
			if(timer == 15) GunSounds.play(entity, KineticSounds.GUN_MAG_SMALL_INSERT, 1F, 1F);
		}
		if(type == GunAnimation.RELOAD_CYCLE) {
			if(timer == 5) GunSounds.play(entity, KineticSounds.GUN_MAG_SMALL_INSERT, 1F, 1F);
		}
		if(type == GunAnimation.RELOAD_END) {
			if(timer == 2) GunSounds.play(entity, KineticSounds.GUN_REVOLVER_CLOSE, 1F, 0.9F);
		}
		if(type == GunAnimation.JAMMED) {
			if(timer == 2) GunSounds.play(entity, KineticSounds.GUN_REVOLVER_CLOSE, 1F, 0.9F);
			if(timer == 12) GunSounds.play(entity, KineticSounds.GUN_REVOLVER_COCK, 1F, 0.75F);
			if(timer == 26) GunSounds.play(entity, KineticSounds.GUN_REVOLVER_CLOSE, 1F, 0.9F);
		}
		if(type == GunAnimation.CYCLE_DRY) {
			if(timer == 0) GunSounds.play(entity, KineticSounds.GUN_DRY_FIRE, 1F, 1F);
		}
		if(type == GunAnimation.INSPECT) {
			if(timer == 0) GunSounds.play(entity, KineticSounds.GUN_REVOLVER_COCK, 1F, 0.75F);
			IMagazine mag = ctx.config.getReceivers(stack)[0].getMagazine(stack);
			int toEject = mag.getAmountAfterReload(stack) - mag.getAmount(stack, ctx.inventory);
			if(timer == 4 && toEject > 0) {
				SpentCasing casing = mag.getCasing(stack, ctx.inventory);
				if(casing != null) for(int i = 0; i < toEject; i++) CasingEffects.composeEffect(entity.level(), entity, 0.625, -0.1875, -0.375D, -0.12, 0.18, 0, 0.01, -15F * (float)entity.getRandom().nextGaussian() * 7.5F, (float)entity.getRandom().nextGaussian() * 5F, casing.getName(), true, 60, 0.5D, 20);
				mag.setAmountAfterReload(stack, 0);
			}
			if(timer == 20) GunSounds.play(entity, KineticSounds.GUN_REVOLVER_CLOSE, 1F, 0.9F);
		}
	};
	public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_SPAS = (stack, ctx) -> {
		LivingEntity entity = ctx.entity;
		if(entity.level().isClientSide) return;
		GunAnimation type = ItemGunBaseNT.getLastAnim(stack, ctx.configIndex);
		int timer = ItemGunBaseNT.getAnimTimer(stack, ctx.configIndex);
		boolean aiming = ItemGunBaseNT.getIsAiming(stack);

		if(type == GunAnimation.CYCLE || type == GunAnimation.ALT_CYCLE) {
			if(timer == 0) GunNetwork.flash(entity);
			if(timer == 8) GunSounds.play(entity, KineticSounds.GUN_SHOTGUN_COCK, 1F, 1F);
			if(timer == 10) {
				SpentCasing casing = ctx.config.getReceivers(stack)[0].getMagazine(stack).getCasing(stack, ctx.inventory); //turns out there's a reason why stovepipes look like that
				if(casing != null) CasingEffects.composeEffect(entity.level(), entity, 0.375, aiming ? 0 : -0.125, aiming ? 0 : -0.25D, 0, 0.18, -0.12, 0.01, -3F + (float)entity.getRandom().nextGaussian() * 2.5F, -15F + entity.getRandom().nextFloat() * -5F, casing.getName());
			}
		}
		if(type == GunAnimation.CYCLE_DRY) {
			if(timer == 0) GunSounds.play(entity, KineticSounds.GUN_DRY_FIRE, 1F, 1F);
			if(timer == 8) GunSounds.play(entity, KineticSounds.GUN_SHOTGUN_COCK, 1F, 1F);
		}
		if(type == GunAnimation.RELOAD) {
			IMagazine mag = ctx.config.getReceivers(stack)[0].getMagazine(stack);
			if(mag.getAmount(stack, ctx.inventory) == 0) {
				if(timer == 0) GunSounds.play(entity, KineticSounds.GUN_REVOLVER_COCK, 1F, 1F);
				if(timer == 7) GunSounds.play(entity, KineticSounds.GUN_REVOLVER_CLOSE, 1F, 1F);
			}
			if(timer == 5) GunSounds.play(entity, KineticSounds.GUN_SHOTGUN_LOAD, 1F, 1F);
		}
		if(type == GunAnimation.RELOAD_CYCLE) {
			if(timer == 5) GunSounds.play(entity, KineticSounds.GUN_SHOTGUN_LOAD, 1F, 1F);
		}
		if(type == GunAnimation.INSPECT) {
			if(timer == 5) GunSounds.play(entity, KineticSounds.GUN_SHOTGUN_OPEN, 1F, 1F);
			if(timer == 18) GunSounds.play(entity, KineticSounds.GUN_SHOTGUN_CLOSE, 1F, 1F);
		}
		if(type == GunAnimation.JAMMED) {
			if(timer == 18) GunSounds.play(entity, KineticSounds.GUN_WHACK, 1F, 1F);
			if(timer == 25) GunSounds.play(entity, KineticSounds.GUN_WHACK, 1F, 1F);
			if(timer == 29) GunSounds.play(entity, KineticSounds.GUN_SHOTGUN_CLOSE, 1F, 1F);
		}
	};
	public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_SHREDDER = (stack, ctx) -> {
		LivingEntity entity = ctx.entity;
		if(entity.level().isClientSide) return;
		GunAnimation type = ItemGunBaseNT.getLastAnim(stack, ctx.configIndex);
		int timer = ItemGunBaseNT.getAnimTimer(stack, ctx.configIndex);

		if(type == GunAnimation.CYCLE) {
			if(timer == 0) GunNetwork.flash(entity);
			if(timer == 2) GunSounds.play(entity, KineticSounds.GUN_SHREDDER_CYCLE, 0.25F, 1.5F);
		}
		if(type == GunAnimation.CYCLE_DRY) {
			if(timer == 0) GunSounds.play(entity, KineticSounds.GUN_DRY_FIRE, 1F, 1F);
			if(timer == 2) GunSounds.play(entity, KineticSounds.GUN_SHREDDER_CYCLE, 0.25F, 1.5F);
		}
		if(type == GunAnimation.RELOAD) {
			if(timer == 2) GunSounds.play(entity, KineticSounds.GUN_MAG_REMOVE, 1F, 1F);
			if(timer == 32) GunSounds.play(entity, KineticSounds.GUN_MAG_INSERT, 1F, 1F);
		}
		if(type == GunAnimation.INSPECT) {
			if(timer == 2) GunSounds.play(entity, KineticSounds.GUN_MAG_REMOVE, 1F, 1F);
			if(timer == 28) GunSounds.play(entity, KineticSounds.GUN_MAG_INSERT, 1F, 1F);
		}
	};
}
