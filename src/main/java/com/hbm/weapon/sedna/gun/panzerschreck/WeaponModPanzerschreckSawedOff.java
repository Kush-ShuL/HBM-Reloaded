package com.hbm.weapon.sedna.gun.panzerschreck;
import java.util.function.*;
import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT.*;
import com.hbm.weapon.sedna.gun.factory.*;
import com.hbm.weapon.sedna.gun.mags.*;
import com.hbm.weapon.sedna.gun.pistols.PistolAmmo;
import com.hbm.client.animation.bus.*;
import com.hbm.client.animation.bus.BusAnimationKeyframe.IType;
import net.minecraft.world.item.ItemStack;
import com.hbm.weapon.sedna.gun.mods.*;
import com.hbm.bomb.conventional.grenade.FireStatus;
import com.hbm.damage.LegacyEntityDamage;
import com.hbm.damage.DamageResistanceHandler.DamageClass;
public class WeaponModPanzerschreckSawedOff extends WeaponModBase {

	public WeaponModPanzerschreckSawedOff(int id) {
		super(id, "SHIELD");
	}

	@Override
	public <T> T eval(T base, ItemStack gun, String key, Object parent) {
		if(key == GunConfig.I_DRAWDURATION) return cast(5, base);
		if(key == Receiver.CON_ONFIRE) { return (T) LAMBDA_FIRE; }
		if(key == GunConfig.FUN_ANIMNATIONS) { return (T) LAMBDA_PANZERSCHRECK_ANIMS; }
		return base;
	}

	@SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_PANZERSCHRECK_ANIMS = (stack, type) -> {
		switch(type) {
		case EQUIP: return new BusAnimation().addBus("EQUIP", new BusAnimationSequence().addPos(60, 0, 0, 0).addPos(0, 0, 0, 250, IType.SIN_DOWN));
		}
		return PanzerschreckFactory.LAMBDA_PANZERSCHRECK_ANIMS.apply(stack, type);
	};

	public static BiConsumer<ItemStack, LambdaContext> LAMBDA_FIRE = (stack, ctx) -> {
		Lego.LAMBDA_STANDARD_FIRE.accept(stack, ctx);
		if(ctx.entity != null) {
			FireStatus.data(ctx.entity).fire += 100;
			LegacyEntityDamage.attackEntityFromNT(ctx.entity, BulletConfig.getDamage(ctx.entity, ctx.entity, DamageClass.FIRE), 4F, true, false, 0F, 0F, 0F);
		}
	};
}
