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
public class WeaponModTestFirerate extends WeaponModBase {
	
	public WeaponModTestFirerate(int id, String... slots) {
		super(id, slots);
		this.setPriority(PRIORITY_MULT_FINAL);
	}

	@Override
	public <T> T eval(T base, ItemStack gun, String key, Object parent) {

		if(parent instanceof Receiver && key == Receiver.I_DELAYAFTERFIRE && base instanceof Integer) {
			return cast(Math.max((Integer) base / 2, 1), base);
		}
		
		return base;
	}
}
