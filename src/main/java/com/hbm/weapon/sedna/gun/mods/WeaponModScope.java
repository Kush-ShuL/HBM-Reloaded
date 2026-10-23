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
public class WeaponModScope extends WeaponModBase {

	public WeaponModScope(int id) {
		super(id, "SCOPE");
	}

	@Override
	public <T> T eval(T base, ItemStack gun, String key, Object parent) {

		if(key == GunConfig.O_SCOPETEXTURE) {
			if(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(gun.getItem()).toString().equals("hbm:gun_heavy_revolver")) return (T) net.minecraft.resources.ResourceLocation.parse("hbm:textures/misc/scope_44.png");
			if(((ItemGunBaseNT) gun.getItem()).quality == WeaponQuality.UTILITY) return (T) net.minecraft.resources.ResourceLocation.parse("hbm:textures/misc/scope_tool.png");
			return (T) net.minecraft.resources.ResourceLocation.parse("hbm:textures/misc/scope_bolt.png");
		}
		if(key == GunConfig.B_HIDECROSSHAIR) return cast(true, base); // just in case
		
		return base;
	}
}
