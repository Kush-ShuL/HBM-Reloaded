package com.hbm.weapon.sedna.gun.flaregun;
import java.util.function.BiConsumer;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT.LambdaContext;
import com.hbm.weapon.sedna.gun.mags.IMagazine;
import com.hbm.weapon.sedna.gun.particle.SpentCasing;
import com.hbm.weapon.sedna.gun.legendary.CasingEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
/** Original reload/cock sounds and the smoking case ejection at source timer4. */
public final class FlaregunOrchestra {private FlaregunOrchestra(){}
	public static BiConsumer<ItemStack, LambdaContext> SOUNDS = (stack, ctx) -> {
		LivingEntity entity = ctx.entity;
		if(entity.level().isClientSide) return;
		GunAnimation type = ItemGunBaseNT.getLastAnim(stack, ctx.configIndex);
		int timer = ItemGunBaseNT.getAnimTimer(stack, ctx.configIndex);
		boolean aiming = ItemGunBaseNT.getIsAiming(stack);

		if(type == GunAnimation.RELOAD) {
			if(timer == 0) GunSounds.play(entity, "hbm:weapon.reload.magSmallRemove", 1F, 0.8F);
			if(timer == 4) {
				IMagazine mag = ctx.config.getReceivers(stack)[0].getMagazine(stack);
				if(mag.getAmountAfterReload(stack) > 0) {
					SpentCasing casing = ctx.config.getReceivers(stack)[0].getMagazine(stack).getCasing(stack, ctx.inventory);
					if(casing != null) CasingEffects.composeEffect(entity.level(), entity, 0.625, -0.125, aiming ? -0.125 : -0.375D, -0.12, 0.18, 0, 0.01, -15F + (float)entity.getRandom().nextGaussian() * 7.5F, (float)entity.getRandom().nextGaussian() * 5F, casing.getName(), true, 60, 0.5D, 20);
					mag.setAmountBeforeReload(stack, 0);
				}
			}
			if(timer == 16) GunSounds.play(entity, "hbm:weapon.reload.insertCanister", 1F, 1F);
			if(timer == 24) GunSounds.play(entity, "hbm:weapon.reload.magSmallInsert", 1F, 1F);
		}
		if(type == GunAnimation.JAMMED) {
			if(timer == 10) GunSounds.play(entity, "hbm:weapon.reload.magSmallRemove", 1F, 0.8F);
			if(timer == 29) GunSounds.play(entity, "hbm:weapon.reload.magSmallInsert", 1F, 1F);
		}
		if(type == GunAnimation.CYCLE) {
			if(timer == 12) GunSounds.play(entity, "hbm:weapon.reload.revolverCock", 1F, 1F);
		}
		if(type == GunAnimation.CYCLE_DRY) {
			if(timer == 2) GunSounds.play(entity, "hbm:weapon.reload.dryFireClick", 1F, 1F);
			if(timer == 12) GunSounds.play(entity, "hbm:weapon.reload.revolverCock", 1F, 1F);
		}
	};

}
