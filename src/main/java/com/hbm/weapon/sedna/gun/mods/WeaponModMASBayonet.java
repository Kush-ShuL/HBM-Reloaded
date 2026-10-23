package com.hbm.weapon.sedna.gun.mods;
import com.hbm.weapon.sedna.gun.rifle762.*;
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
public class WeaponModMASBayonet extends WeaponModBase {

	public WeaponModMASBayonet(int id) {
		super(id, "BAYONET");
	}

	@Override
	public <T> T eval(T base, ItemStack gun, String key, Object parent) {
		if(key == GunConfig.FUN_ANIMNATIONS) return (T) LAMBDA_MAS36_ANIMS;
		if(key == GunConfig.I_INSPECTDURATION) return cast(30, base);
		if(key == GunConfig.CON_ONPRESSSECONDARY) return (T) com.hbm.weapon.sedna.gun.pistols.XFactory44.SMACK_A_FUCKER;
		if(key == GunConfig.CON_ORCHESTRA) return (T) ORCHESTRA_MAS36;
		if(key == GunConfig.I_INSPECTCANCEL) return cast(false, base);
		return base;
	}

	public static BiConsumer<ItemStack, LambdaContext> ORCHESTRA_MAS36 = (stack, ctx) -> {
		LivingEntity entity = ctx.entity;
		if(entity.level().isClientSide) return;
		GunAnimation type = ItemGunBaseNT.getLastAnim(stack, ctx.configIndex);
		int timer = ItemGunBaseNT.getAnimTimer(stack, ctx.configIndex);

		if(type == GunAnimation.INSPECT) {

			if(timer == 15 && ctx.getPlayer() != null) {
                Rifle762Melee.stab(ctx,XFactory762mm.GUN_SOUTH_STAR_BAYONET_DAMAGE.get());
			}
			return;
		}

		Rifle762Orchestras.ORCHESTRA_MAS36.accept(stack, ctx);
	};

	@SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_MAS36_ANIMS = (stack, type) -> {
		switch(type) {
		case INSPECT: return new BusAnimation()
				.addBus("STAB", new BusAnimationSequence().addPos(0, 1, -2, 250, IType.SIN_DOWN).hold(250).addPos(0, 1, 5, 250, IType.SIN_UP).hold(250).addPos(0, 0, 0, 500, IType.SIN_FULL));
		}

		return XFactory762mm.LAMBDA_MAS36_ANIMS.apply(stack, type);
	};
}
