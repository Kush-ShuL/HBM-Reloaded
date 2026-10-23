package com.hbm.weapon.sedna.gun.grenade40;
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
import com.hbm.registry.HbmContent;
import com.hbm.hazard.HbmHazards;
public final class Grenade40Ammo {
 public static final GunAmmo G40_HE=GunAmmo.standard(53);
 public static final GunAmmo G40_HEAT=GunAmmo.standard(54);
 public static final GunAmmo G40_DEMO=GunAmmo.standard(55);
 public static final GunAmmo G40_INC=GunAmmo.standard(56);
 public static final GunAmmo G40_PHOSPHORUS=GunAmmo.standard(57);
 public static void initializeCasings(){for(BulletConfig bullet:new BulletConfig[]{XFactory40mm.g40_he,XFactory40mm.g40_heat,XFactory40mm.g40_demo,XFactory40mm.g40_inc,XFactory40mm.g40_phosphorus}){var casing=com.hbm.item.casing.FlatCasingContent.stack(1,1);bullet.setCasing(casing,4);}}
}
