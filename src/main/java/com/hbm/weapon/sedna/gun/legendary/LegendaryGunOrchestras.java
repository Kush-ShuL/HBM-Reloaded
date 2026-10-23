package com.hbm.weapon.sedna.gun.legendary;
import java.util.function.*;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT.LambdaContext;
import com.hbm.weapon.sedna.gun.mags.*;
import com.hbm.weapon.sedna.gun.particle.SpentCasing;
import com.hbm.client.animation.bus.*;
import com.hbm.client.animation.bus.BusAnimationKeyframe.IType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.*;
import net.minecraft.world.item.ItemStack;
/** Exact source server ticks, magazine changes, effects and casing emitters. */
public final class LegendaryGunOrchestras {
	public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_MARESLEG_SHORT = (stack, ctx) -> {
		LivingEntity entity = ctx.entity;
		if(entity.level().isClientSide) return;
		GunAnimation type = ItemGunBaseNT.getLastAnim(stack, ctx.configIndex);
		int timer = ItemGunBaseNT.getAnimTimer(stack, ctx.configIndex);
		boolean aiming = ItemGunBaseNT.getIsAiming(stack);

		if(type == GunAnimation.RELOAD) {
			if(timer == 8) GunSounds.play(entity, SourceGunSounds.GUN_REVOLVER_COCK, 1F, 0.8F);
			if(timer == 16) GunSounds.play(entity, SourceGunSounds.GUN_SHOTGUN_LOAD, 1F, 1F);
		}
		if(type == GunAnimation.RELOAD_CYCLE) {
			if(timer == 0) GunSounds.play(entity, SourceGunSounds.GUN_SHOTGUN_LOAD, 1F, 1F);
		}
		if(type == GunAnimation.RELOAD_END) {
			if(timer == 2) GunSounds.play(entity, SourceGunSounds.GUN_REVOLVER_COCK, 1F, 0.7F);
		}
		if(type == GunAnimation.JAMMED) {
			if(timer == 2) GunSounds.play(entity, SourceGunSounds.GUN_REVOLVER_COCK, 1F, 0.7F);
			if(timer == 17) GunSounds.play(entity, SourceGunSounds.GUN_LEVER_COCK, 1F, 0.8F);
			if(timer == 29) GunSounds.play(entity, SourceGunSounds.GUN_LEVER_COCK, 1F, 0.8F);
		}
		if(type == GunAnimation.CYCLE) {
			if(timer == 0) GunNetwork.flash(entity);
			if(timer == 14) {
				SpentCasing casing = ctx.config.getReceivers(stack)[0].getMagazine(stack).getCasing(stack, ctx.inventory);
				if(casing != null) CasingEffects.composeEffect(entity.level(), entity, 0.3125, -0.125, aiming ? -0.125 : -0.375D, 0, -0.08, 0, 0.01, -15F + (float)entity.getRandom().nextGaussian() * 5F, (float)entity.getRandom().nextGaussian() * 2.5F, casing.getName(), true, 60, 0.5D, 20);
			}
			if(timer == 8) GunSounds.play(entity, SourceGunSounds.GUN_LEVER_COCK, 1F, 0.8F);
		}
		if(type == GunAnimation.CYCLE_DRY) {
			if(timer == 2) GunSounds.play(entity, SourceGunSounds.GUN_DRY_FIRE, 1F, 1F);
			if(timer == 8) GunSounds.play(entity, SourceGunSounds.GUN_LEVER_COCK, 1F, 0.8F);
		}
	};
	public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_NOPIP = (stack, ctx) -> {
		LivingEntity entity = ctx.entity;
		if(entity.level().isClientSide) return;
		GunAnimation type = ItemGunBaseNT.getLastAnim(stack, ctx.configIndex);
		int timer = ItemGunBaseNT.getAnimTimer(stack, ctx.configIndex);

		if(type == GunAnimation.RELOAD) {
			if(timer == 3) GunSounds.play(entity, SourceGunSounds.GUN_REVOLVER_COCK, 1F, 1F);
			if(timer == 10) GunSounds.play(entity, SourceGunSounds.GUN_MAG_SMALL_REMOVE, 1F, 1F);
			if(timer == 34) GunSounds.play(entity, SourceGunSounds.GUN_MAG_SMALL_INSERT, 1F, 1F);
			if(timer == 40) GunSounds.play(entity, SourceGunSounds.GUN_REVOLVER_CLOSE, 1F, 1F);

			if(timer == 16) {
				Receiver rec = ctx.config.getReceivers(stack)[0];
				IMagazine mag = rec.getMagazine(stack);
				SpentCasing casing = mag.getCasing(stack, ctx.inventory);
				if(casing != null) for(int i = 0; i < mag.getCapacity(stack); i++) CasingEffects.composeEffect(entity.level(), entity, 0.25, -0.125, -0.125, -0.05, 0, 0, 0.01, -6.5F + (float)entity.getRandom().nextGaussian() * 3F, (float)entity.getRandom().nextGaussian() * 5F, casing.getName());
			}
		}
		if(type == GunAnimation.CYCLE) {
			if(timer == 0) GunNetwork.flash(entity);
			if(timer == 11) GunSounds.play(entity, SourceGunSounds.GUN_REVOLVER_COCK, 1F, 1F);
		}
		if(type == GunAnimation.CYCLE_DRY) {
			if(timer == 2) GunSounds.play(entity, SourceGunSounds.GUN_DRY_FIRE, 1F, 1F);
			if(timer == 11) GunSounds.play(entity, SourceGunSounds.GUN_REVOLVER_COCK, 1F, 1F);
		}
		if(type == GunAnimation.INSPECT) {
			if(timer == 3) GunSounds.play(entity, SourceGunSounds.GUN_REVOLVER_COCK, 1F, 1F);
			if(timer == 16) GunSounds.play(entity, SourceGunSounds.GUN_REVOLVER_CLOSE, 1F, 1F);
		}
	};
	public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_SHREDDER_SEXY = (stack, ctx) -> {
		LivingEntity entity = ctx.entity;
		if(entity.level().isClientSide) return;
		GunAnimation type = ItemGunBaseNT.getLastAnim(stack, ctx.configIndex);
		int timer = ItemGunBaseNT.getAnimTimer(stack, ctx.configIndex);
		boolean aiming = ItemGunBaseNT.getIsAiming(stack);

		if(type == GunAnimation.CYCLE) {
			if(timer == 0) {
				GunNetwork.flash(entity);
				if(ctx.config.getReceivers(stack)[0].getMagazine(stack).getType(stack, null) == LegendaryGunFactory.g12_equestrian_bj) {
					ItemGunBaseNT.setTimer(stack, 0, 20);
				}
			}

			if(timer == 2) {
				SpentCasing casing = ctx.config.getReceivers(stack)[0].getMagazine(stack).getCasing(stack, ctx.inventory);
				if(casing != null) CasingEffects.composeEffect(entity.level(), entity, 0.375, aiming ? -0.0625 : -0.125, aiming ? -0.125 : -0.25D, 0, 0.18, -0.12, 0.01, -10F + (float)entity.getRandom().nextGaussian() * 2.5F, (float)entity.getRandom().nextGaussian() * -20F + 15F, casing.getName(), false, 60, 0.5D, 20);
			}
		}

		if(type == GunAnimation.CYCLE_DRY) {
			if(timer == 0) GunSounds.play(entity, SourceGunSounds.GUN_DRY_FIRE, 1F, 1F);
		}
		if(type == GunAnimation.RELOAD) {
			if(timer == 0) GunSounds.play(entity, SourceGunSounds.GUN_REVOLVER_COCK, 1F, 1F);
			if(timer == 4) GunSounds.play(entity, SourceGunSounds.GUN_REVOLVER_CLOSE, 1F, 0.75F);
			if(timer == 16) GunSounds.play(entity, SourceGunSounds.GUN_MAG_SMALL_REMOVE, 1F, 1F);
			if(timer == 30) GunSounds.play(entity, SourceGunSounds.GUN_MAG_REMOVE, 1F, 1F);
			if(timer == 55) GunSounds.play(entity, SourceGunSounds.GUN_IMPACT, 0.5F, 1F);
			if(timer == 65) GunSounds.play(entity, SourceGunSounds.GUN_MAG_INSERT, 1F, 1F);
			if(timer == 74) GunSounds.play(entity, SourceGunSounds.GUN_MAG_SMALL_INSERT, 1F, 1F);
			if(timer == 88) GunSounds.play(entity, SourceGunSounds.GUN_REVOLVER_CLOSE, 1F, 0.75F);
			if(timer == 100) GunSounds.play(entity, SourceGunSounds.GUN_REVOLVER_COCK, 1F, 1F);

			if(timer == 55) ctx.config.getReceivers(stack)[0].getMagazine(stack).reloadAction(stack, ctx.inventory);
		}

		if(type == GunAnimation.INSPECT) {
			if(timer == 20) GunSounds.play(entity, SourceGunSounds.PLAYER_GULP, 1F, 1F);
			if(timer == 25) GunSounds.play(entity, SourceGunSounds.PLAYER_GULP, 1F, 1F);
			if(timer == 30) GunSounds.play(entity, SourceGunSounds.PLAYER_GULP, 1F, 1F);
			if(timer == 35) GunSounds.play(entity, SourceGunSounds.PLAYER_GULP, 1F, 1F);
			if(timer == 50) GunSounds.play(entity, SourceGunSounds.PLAYER_GROAN, 1F, 1F);
			if(timer == 60) {
				entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 30 * 20, 2));
				entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 30 * 20, 2));
				entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 10 * 20, 0));
			}
		}
	};
}
