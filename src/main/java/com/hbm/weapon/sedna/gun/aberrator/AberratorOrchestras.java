package com.hbm.weapon.sedna.gun.aberrator;
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
import com.hbm.entity.projectile.EntityBulletBeamBase;
public final class AberratorOrchestras {
	public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_ABERRATOR = (stack, ctx) -> {
		LivingEntity entity = ctx.entity;
		if(entity.level().isClientSide) return;
		GunAnimation type = ItemGunBaseNT.getLastAnim(stack, ctx.configIndex);
		int timer = ItemGunBaseNT.getAnimTimer(stack, ctx.configIndex);
		boolean aiming = ItemGunBaseNT.getIsAiming(stack);

		if(type == GunAnimation.RELOAD) {
			if(timer == 5) GunSounds.play(entity, AberratorSounds.GUN_MAG_SMALL_REMOVE, 1F, 0.75F);
			if(timer == 32) GunSounds.play(entity, AberratorSounds.GUN_MAG_SMALL_INSERT, 1F, 0.75F);
			if(timer == 42) GunSounds.play(entity, AberratorSounds.GUN_PISTOL_COCK, 1F, 0.75F);
		}

		if(type == GunAnimation.CYCLE) {
			if(timer == 0) GunNetwork.flash(entity);
			if(timer == 1) {
				int cba = (stack.getItem() == AberratorGuns.gun_aberrator_eott && ctx.configIndex == 0) ? -1 : 1;
				SpentCasing casing = ctx.config.getReceivers(stack)[0].getMagazine(stack).getCasing(stack, ctx.inventory);
				if(casing != null) CasingEffects.composeEffect(entity.level(), entity, 0.5, aiming ? 0 : -0.125, aiming ? -0.0625 : -0.25D * cba, -0.05, 0.25, -0.05 * cba, 0.01, -10F + (float)entity.getRandom().nextGaussian() * 10F, (float)entity.getRandom().nextGaussian() * 12.5F, casing.getName());
			}
		}

		if(type == GunAnimation.CYCLE_DRY) {
			if(timer == 1) GunSounds.play(entity, AberratorSounds.GUN_DRY_FIRE, 1F, 0.75F);
			if(timer == 9) GunSounds.play(entity, AberratorSounds.GUN_PISTOL_COCK, 1F, 0.75F);
		}
	};
}
