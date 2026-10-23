package com.hbm.weapon.sedna.gun.grenade40;
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
public final class Grenade40Orchestras {
	public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_CONGOLAKE = (stack, ctx) -> {
		LivingEntity entity = ctx.entity;
		if(entity.level().isClientSide) return;
		GunAnimation type = ItemGunBaseNT.getLastAnim(stack, ctx.configIndex);
		int timer = ItemGunBaseNT.getAnimTimer(stack, ctx.configIndex);
		boolean aiming = ItemGunBaseNT.getIsAiming(stack);

		if(type == GunAnimation.CYCLE) {
			if(timer == 0) GunNetwork.flash(entity);
			if(timer == 15) {
				IMagazine mag = ctx.config.getReceivers(stack)[0].getMagazine(stack);
				SpentCasing casing = mag.getCasing(stack, ctx.inventory);
				if(casing != null) CasingEffects.composeEffect(entity.level(), entity, 0.625, aiming ? -0.0625 : -0.25, aiming ? 0 : -0.375D, 0, 0.18, 0.12, 0.01, -5F + (float)entity.getRandom().nextGaussian() * 3.5F, -10F + entity.getRandom().nextFloat() * 5F, casing.getName(), true, 60, 0.5D, 20);
			}
		}
		if(type == GunAnimation.RELOAD || type == GunAnimation.RELOAD_CYCLE) {
			if(timer == 0) GunSounds.play(entity, Grenade40Sounds.GUN_GRENADE_RELOAD, 1F, 1F);
		}
		if(type == GunAnimation.INSPECT) {
			if(timer == 9) GunSounds.play(entity, Grenade40Sounds.GUN_GRENADE_OPEN, 1F, 1F);
			if(timer == 27) GunSounds.play(entity, Grenade40Sounds.GUN_GRENADE_CLOSE, 1F, 1F);
		}
	};
	public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_MK108 = (stack, ctx) -> {
		LivingEntity entity = ctx.entity;
		if(entity.level().isClientSide) return;
		GunAnimation type = ItemGunBaseNT.getLastAnim(stack, ctx.configIndex);
		int timer = ItemGunBaseNT.getAnimTimer(stack, ctx.configIndex);
		boolean aiming = ItemGunBaseNT.getIsAiming(stack);

		if(type == GunAnimation.CYCLE) {
			if(timer == 0) {
				GunNetwork.flash(entity);
			}

			if(timer == 2) {
				SpentCasing casing = ctx.config.getReceivers(stack)[0].getMagazine(stack).getCasing(stack, ctx.inventory);
				if(casing != null) CasingEffects.composeEffect(entity.level(), entity, 0.5, aiming ? -0.125 : -0.3125, aiming ? -0.375 : -0.3125D, 0, 0.18, -0.12, 0.01, -10F + (float)entity.getRandom().nextGaussian() * 2.5F, (float)entity.getRandom().nextGaussian() * -20F + 15F, casing.getName(), true, 60, 0.5D, 10);
			}
		}
		
		if(type == GunAnimation.CYCLE_DRY) {
			if(timer == 0) GunSounds.play(entity, Grenade40Sounds.GUN_DRY_FIRE, 1F, 0.75F);
		}
		
		if(type == GunAnimation.RELOAD) {
			if(timer == 0) GunSounds.play(entity, Grenade40Sounds.GUN_REVOLVER_CLOSE, 1F, 0.65F);
			if(timer == 10) GunSounds.play(entity, Grenade40Sounds.GUN_MAG_SMALL_REMOVE, 1F, 0.75F);
			if(timer == 40) GunSounds.play(entity, Grenade40Sounds.GUN_MAG_REMOVE, 1F, 0.75F);
			if(timer == 60) GunSounds.play(entity, Grenade40Sounds.GUN_IMPACT, 0.5F, 1F);
			if(timer == 90) GunSounds.play(entity, Grenade40Sounds.GUN_MAG_INSERT, 1F, 0.75F);
			if(timer == 100) GunSounds.play(entity, Grenade40Sounds.GUN_MAG_SMALL_INSERT, 1F, 0.75F);
			if(timer == 125) GunSounds.play(entity, Grenade40Sounds.GUN_REVOLVER_CLOSE, 1F, 0.65F);

			if(timer == 60) ctx.config.getReceivers(stack)[0].getMagazine(stack).reloadAction(stack, ctx.inventory);
		}
		
		if(type == GunAnimation.INSPECT) {
			int yeetHorizontal = 750;
			int untilImpact = yeetHorizontal * 9 / 15;
			int delay = 250;

			for(int i = 0; i < 3; i++) {
				if(timer == (untilImpact + delay * i) / 50) GunSounds.play(entity, Grenade40Sounds.GUN_IMPACT, 0.5F, 1.5F);
			}
		}
	};
}
