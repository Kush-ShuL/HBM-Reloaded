package com.hbm.weapon.sedna.gun.pistols;
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
public final class PistolSettings {
    private static final ModConfigSpec.Builder BUILDER=new ModConfigSpec.Builder();
    private static java.util.List<String> configPath(String key){return switch(key){
        case "GUN_AM180_DAMAGE" -> java.util.List.of("damage","am180");
        case "GUN_STARF_DAMAGE" -> java.util.List.of("damage","starf");
        case "GUN_STARF_AKIMBO_LEFT_DAMAGE" -> java.util.List.of("damage","starfAkimboLeft");
        case "GUN_STARF_AKIMBO_RIGHT_DAMAGE" -> java.util.List.of("damage","starfAkimboRight");
        case "GUN_LIGHT_REVOLVER_DAMAGE" -> java.util.List.of("damage","lightRevolver");
        case "GUN_ATLAS_DAMAGE" -> java.util.List.of("damage","atlas");
        case "GUN_DANI_CELESTIAL_DAMAGE" -> java.util.List.of("damage","daniCelestial");
        case "GUN_DANI_LUNAR_DAMAGE" -> java.util.List.of("damage","daniLunar");
        case "GUN_HENRY_DAMAGE" -> java.util.List.of("damage","henry");
        case "GUN_LINCOLN_DAMAGE" -> java.util.List.of("damage","lincoln");
        case "GUN_HEAVY_REVOLVER_DAMAGE" -> java.util.List.of("damage","heavyRevolver");
        case "GUN_LILMAC_DAMAGE" -> java.util.List.of("damage","lilmac");
        case "GUN_PROTEGE_DAMAGE" -> java.util.List.of("damage","protege");
        case "GUN_HANGMAN_DAMAGE" -> java.util.List.of("damage","hangman");
        case "GUN_HANGMAN_SMACK_DAMAGE" -> java.util.List.of("damage","hangmanSmack");
        case "GUN_GREASEGUN_DAMAGE" -> java.util.List.of("damage","greasegun");
        case "GUN_LONG_PISTOL_DAMAGE" -> java.util.List.of("damage","longPistol");
        case "GUN_UZI_DAMAGE" -> java.util.List.of("damage","uzi");
        case "GUN_UZI_AKIMBO_LEFT_DAMAGE" -> java.util.List.of("damage","uziAkimboLeft");
        case "GUN_UZI_AKIMBO_RIGHT_DAMAGE" -> java.util.List.of("damage","uziAkimboRight");
        default -> throw new IllegalArgumentException("Unknown configuration definition "+key);
    };}
    public static Supplier<Float> value(String key,float value){var config=BUILDER.defineInRange(configPath(key),(double)value,0D,(double)Float.MAX_VALUE);return ()->config.get().floatValue();}
    public static void register(ModContainer container){container.registerConfig(ModConfig.Type.SERVER,BUILDER.build(),"hbm-sedna-pistols.toml");}
}
