package com.hbm.weapon.sedna.gun.mods;
import com.hbm.weapon.sedna.gun.kinetic.*;
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
public class WeaponModSawedOff extends WeaponModBase {

	public WeaponModSawedOff(int id) {
		super(id, "BARREL");
	}

	@Override
	public <T> T eval(T base, ItemStack gun, String key, Object parent) {

		if(key == Receiver.F_SPREADINNATE) { return cast(Math.max(0.025F, (Float) base), base); }
		if(key == Receiver.F_SPREADAMMO) { return cast((Float) base * 1.5F, base); }
		if(key == Receiver.F_BASEDAMAGE) { return cast((Float) base * 1.35F, base); }
		
		if(gun.getItem() == KineticGuns.gun_maresleg) {
			if(key == GunConfig.FUN_ANIMNATIONS) return (T) XFactory12ga.LAMBDA_MARESLEG_SHORT_ANIMS;
			if(key == GunConfig.I_DRAWDURATION) return cast(5, base);
		}
		
		return base;
	}
}
