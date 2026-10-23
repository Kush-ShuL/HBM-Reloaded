package com.hbm.weapon.sedna.gun.mods;
import com.hbm.weapon.sedna.gun.rifle556.*;
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
public class WeapnModG3SawedOff extends WeaponModBase {

	public WeapnModG3SawedOff(int id) {
		super(id, "SHIELD");
	}

	@Override
	public <T> T eval(T base, ItemStack gun, String key, Object parent) {
		if(key == GunConfig.I_DRAWDURATION) return cast(5, base);
		if(key == GunConfig.FUN_ANIMNATIONS) return (T) LAMBDA_G3_ANIMS;
		return base;
	}

	@SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_G3_ANIMS = (stack, type) -> {
		switch(type) {
			case EQUIP: return new BusAnimation().addBus("EQUIP", new BusAnimationSequence().addPos(45, 0, 0, 0).addPos(0, 0, 0, 250, IType.SIN_FULL));
		}
		return XFactory556mm.LAMBDA_G3_ANIMS.apply(stack, type);
	};
}
