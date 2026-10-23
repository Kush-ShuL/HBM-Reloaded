package com.hbm.weapon.sedna.gun.energy;
import java.util.function.BiConsumer;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT.LambdaContext;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
/** Exact original audio timers and muzzle distribution. */
public final class EnergyOrchestras {
public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_TESLA = (stack, ctx) -> {
		LivingEntity entity = ctx.entity;
		if(entity.level().isClientSide) return;
		GunAnimation type = ItemGunBaseNT.getLastAnim(stack, ctx.configIndex);
		int timer = ItemGunBaseNT.getAnimTimer(stack, ctx.configIndex);

		if(type == GunAnimation.CYCLE) {
			if(timer == 2) GunSounds.play(entity, "hbm:weapon.fire.shredderCycle", 0.25F, 1.25F);
		}
		if(type == GunAnimation.CYCLE_DRY) {
			if(timer == 0) GunSounds.play(entity, "hbm:weapon.reload.dryFireClick", 1F, 1F);
			if(timer == 2) GunSounds.play(entity, "hbm:weapon.fire.shredderCycle", 0.25F, 1.25F);
		}
		if(type == GunAnimation.INSPECT) {
			if(timer == 12) GunSounds.play(entity, "hbm:block.squeakyToy", 0.25F, 1F);
		}
	};

public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_LASER_PISTOL = (stack, ctx) -> {
		LivingEntity entity = ctx.entity;
		if(entity.level().isClientSide) return;
		GunAnimation type = ItemGunBaseNT.getLastAnim(stack, ctx.configIndex);
		int timer = ItemGunBaseNT.getAnimTimer(stack, ctx.configIndex);
		
		if(type == GunAnimation.CYCLE) {
			if(timer == 0) GunNetwork.flash(entity);
		}
		if(type == GunAnimation.CYCLE_DRY) {
			if(timer == 0) GunSounds.play(entity, "hbm:weapon.reload.dryFireClick", 1F, 1.5F);
		}

		if(type == GunAnimation.RELOAD) {
			if(timer == 0) GunSounds.play(entity, "hbm:weapon.reload.revolverCock", 1F, 1F);
			if(timer == 10) GunSounds.play(entity, "hbm:weapon.reload.magSmallRemove", 1F, 1.25F);
			if(timer == 34) GunSounds.play(entity, "hbm:weapon.reload.magSmallInsert", 1F, 1.25F);
			if(timer == 40) GunSounds.play(entity, "hbm:weapon.reload.revolverClose", 1F, 1.25F);
		}

		if(type == GunAnimation.JAMMED) {
			if(timer == 10) GunSounds.play(entity, "hbm:weapon.reload.revolverCock", 1F, 1F);
			if(timer == 15) GunSounds.play(entity, "hbm:weapon.reload.revolverClose", 1F, 1.25F);
			if(timer == 30) GunSounds.play(entity, "hbm:weapon.reload.impact", 0.25F, 1.5F);
		}
	};

public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_LASRIFLE = (stack, ctx) -> {
		LivingEntity entity = ctx.entity;
		if(entity.level().isClientSide) return;
		GunAnimation type = ItemGunBaseNT.getLastAnim(stack, ctx.configIndex);
		int timer = ItemGunBaseNT.getAnimTimer(stack, ctx.configIndex);
		
		if(type == GunAnimation.CYCLE) {
			if(timer == 0) GunNetwork.flash(entity);
		}
		if(type == GunAnimation.CYCLE_DRY) {
			if(timer == 0) GunSounds.play(entity, "hbm:weapon.reload.dryFireClick", 1F, 1.5F);
		}

		if(type == GunAnimation.RELOAD) {
			if(timer == 2) GunSounds.play(entity, "hbm:weapon.reload.magSmallRemove", 1F, 1F);
			if(timer == 18) GunSounds.play(entity, "hbm:weapon.reload.impact", 0.25F, 1F);
			if(timer == 30) GunSounds.play(entity, "hbm:weapon.reload.magInsert", 1F, 1F);
			if(timer == 38) GunSounds.play(entity, "hbm:weapon.reload.revolverClose", 1F, 1F);
		}

		if(type == GunAnimation.INSPECT) {
			if(timer == 2) GunSounds.play(entity, "hbm:weapon.reload.magSmallRemove", 1F, 1F);
			if(timer == 12) GunSounds.play(entity, "hbm:weapon.reload.magInsert", 1F, 1F);
			if(timer == 20) GunSounds.play(entity, "hbm:weapon.reload.revolverClose", 1F, 1F);
		}

		if(type == GunAnimation.JAMMED) {
			if(timer == 2) GunSounds.play(entity, "hbm:weapon.reload.magSmallRemove", 1F, 1F);
			if(timer == 22) GunSounds.play(entity, "hbm:weapon.reload.magInsert", 1F, 1F);
			if(timer == 30) GunSounds.play(entity, "hbm:weapon.reload.revolverClose", 1F, 1F);
		}
	};

}
