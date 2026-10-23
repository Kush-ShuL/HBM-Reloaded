package com.hbm.weapon.sedna.gun.mods;
import com.hbm.weapon.sedna.gun.pistols.*;
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
public class WeaponModGreasegun extends WeaponModBase {

	public WeaponModGreasegun(int id) {
		super(id, "FURNITURE");
		this.setPriority(PRIORITY_ADDITIVE);
	}

	@Override
	public <T> T eval(T base, ItemStack gun, String key, Object parent) {
		if(key == GunConfig.F_DURABILITY) return cast((Float) base * 3F, base);
		if(key == Receiver.F_BASEDAMAGE) return cast((Float) base + 2F, base);
		if(key == Receiver.F_SPREADINNATE) return cast(0F, base);
		if(key == Receiver.I_DELAYAFTERFIRE) return cast((Integer) base / 2, base);
		if(key == GunConfig.CON_ORCHESTRA) return (T) ORCHESTRA_GREASEGUN;
		return base;
	}

	public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_GREASEGUN = (stack, ctx) -> {
		LivingEntity entity = ctx.entity;
		if(entity.level().isClientSide) return;
		GunAnimation type = ItemGunBaseNT.getLastAnim(stack, ctx.configIndex);
		int timer = ItemGunBaseNT.getAnimTimer(stack, ctx.configIndex);
		boolean aiming = ItemGunBaseNT.getIsAiming(stack);

		if(type == GunAnimation.CYCLE) {
			if(timer == 1) {
				SpentCasing casing = ctx.config.getReceivers(stack)[0].getMagazine(stack).getCasing(stack, ctx.inventory);
				if(casing != null) CasingEffects.composeEffect(entity.level(), entity, 0.55, aiming ? 0 : -0.125, aiming ? 0 : -0.25D, 0, 0.18, -0.12, 0.01, -7.5F + (float)entity.getRandom().nextGaussian() * 5F, 12F + (float)entity.getRandom().nextGaussian() * 5F, casing.getName());
			}
			return;
		}
		PistolOrchestras.ORCHESTRA_GREASEGUN.accept(stack, ctx);
	};
}
