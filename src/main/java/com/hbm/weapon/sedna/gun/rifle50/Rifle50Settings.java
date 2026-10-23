package com.hbm.weapon.sedna.gun.rifle50;
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
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
public final class Rifle50Settings {
    private static final ModConfigSpec.Builder BUILDER=new ModConfigSpec.Builder();
    private static java.util.List<String> configPath(String key){return switch(key){
        case "GUN_ANTI_MATERIEL_DAMAGE" -> java.util.List.of("damage","antiMateriel");
        case "GUN_SUBTLETY_DAMAGE" -> java.util.List.of("damage","subtlety");
        case "GUN_PENANCE_DAMAGE" -> java.util.List.of("damage","penance");
        case "GUN_M2_DAMAGE" -> java.util.List.of("damage","m2");
        default -> throw new IllegalArgumentException("Unknown configuration definition "+key);
    };}
    public static Supplier<Float> value(String key,float value){var config=BUILDER.defineInRange(configPath(key),(double)value,0D,(double)Float.MAX_VALUE);return ()->config.get().floatValue();}
    public static void register(ModContainer container){container.registerConfig(ModConfig.Type.SERVER,BUILDER.build(),"hbm-sedna-50.toml");}
}
