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
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.hbm.weapon.sedna.gun.mods.*;
public final class PistolContent {
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 public static final net.neoforged.neoforge.registries.DeferredItem<net.minecraft.world.item.Item> DEBUG_AMMO=ITEMS.register("ammo_debug",()->new net.minecraft.world.item.Item(new net.minecraft.world.item.Item.Properties()));
 private static boolean initialized;
 static {
        ITEMS.register("gun_light_revolver",()->gun("gun_light_revolver"));
        ITEMS.register("gun_light_revolver_atlas",()->gun("gun_light_revolver_atlas"));
        ITEMS.register("gun_light_revolver_dani",()->gun("gun_light_revolver_dani"));
        ITEMS.register("gun_am180",()->gun("gun_am180"));
        ITEMS.register("gun_star_f",()->gun("gun_star_f"));
        ITEMS.register("gun_star_f_akimbo",()->gun("gun_star_f_akimbo"));
        ITEMS.register("gun_greasegun",()->gun("gun_greasegun"));
        ITEMS.register("gun_lag",()->gun("gun_lag"));
        ITEMS.register("gun_uzi",()->gun("gun_uzi"));
        ITEMS.register("gun_uzi_akimbo",()->gun("gun_uzi_akimbo"));
        ITEMS.register("gun_henry",()->gun("gun_henry"));
        ITEMS.register("gun_henry_lincoln",()->gun("gun_henry_lincoln"));
        ITEMS.register("gun_heavy_revolver",()->gun("gun_heavy_revolver"));
        ITEMS.register("gun_heavy_revolver_protege",()->gun("gun_heavy_revolver_protege"));
        ITEMS.register("gun_hangman",()->gun("gun_hangman"));
        ITEMS.register("gun_debug",()->gun("gun_debug"));
}
 private static ItemGunBaseNT gun(String name){if(!initialized){initialized=true;XFactory357.init();XFactory22lr.init();XFactory45.init();XFactory9mm.init();XFactory44.init();PistolDebugFactory.init();}return switch(name){case "gun_light_revolver" -> PistolGuns.gun_light_revolver;
case "gun_light_revolver_atlas" -> PistolGuns.gun_light_revolver_atlas;
case "gun_light_revolver_dani" -> PistolGuns.gun_light_revolver_dani;
case "gun_am180" -> PistolGuns.gun_am180;
case "gun_star_f" -> PistolGuns.gun_star_f;
case "gun_star_f_akimbo" -> PistolGuns.gun_star_f_akimbo;
case "gun_greasegun" -> PistolGuns.gun_greasegun;
case "gun_lag" -> PistolGuns.gun_lag;
case "gun_uzi" -> PistolGuns.gun_uzi;
case "gun_uzi_akimbo" -> PistolGuns.gun_uzi_akimbo;
case "gun_henry" -> PistolGuns.gun_henry;
case "gun_henry_lincoln" -> PistolGuns.gun_henry_lincoln;
case "gun_heavy_revolver" -> PistolGuns.gun_heavy_revolver;
case "gun_heavy_revolver_protege" -> PistolGuns.gun_heavy_revolver_protege;
case "gun_hangman" -> PistolGuns.gun_hangman;
case "gun_debug" -> PistolGuns.gun_debug;
default->throw new IllegalArgumentException(name);};}
 public static void register(IEventBus bus,ModContainer container){XFactory357.initConfig();XFactory22lr.initConfig();XFactory9mm.initConfig();XFactory44.initConfig();PistolSettings.register(container);PistolSpecialEntities.register(bus);ITEMS.register(bus);bus.addListener(PistolContent::setup);}
 private static void setup(FMLCommonSetupEvent event){event.enqueueWork(()->{
  PistolAmmo.initializeCasings();
  PistolModBindings.initialize();
 });}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("weapon")?List.of(new ItemStack(PistolGuns.gun_light_revolver),new ItemStack(PistolGuns.gun_am180),new ItemStack(PistolGuns.gun_star_f),new ItemStack(PistolGuns.gun_greasegun),new ItemStack(PistolGuns.gun_lag),new ItemStack(PistolGuns.gun_uzi),new ItemStack(PistolGuns.gun_henry),new ItemStack(PistolGuns.gun_heavy_revolver)):List.of();}
 public static List<ItemStack> allStacks(){return List.of(new ItemStack(PistolGuns.gun_light_revolver),new ItemStack(PistolGuns.gun_light_revolver_atlas),new ItemStack(PistolGuns.gun_light_revolver_dani),new ItemStack(PistolGuns.gun_am180),new ItemStack(PistolGuns.gun_star_f),new ItemStack(PistolGuns.gun_star_f_akimbo),new ItemStack(PistolGuns.gun_greasegun),new ItemStack(PistolGuns.gun_lag),new ItemStack(PistolGuns.gun_uzi),new ItemStack(PistolGuns.gun_uzi_akimbo),new ItemStack(PistolGuns.gun_henry),new ItemStack(PistolGuns.gun_henry_lincoln),new ItemStack(PistolGuns.gun_heavy_revolver),new ItemStack(PistolGuns.gun_heavy_revolver_protege),new ItemStack(PistolGuns.gun_hangman),new ItemStack(PistolGuns.gun_debug));}
 public static List<ItemStack> indexedItems(){var stacks=new ArrayList<ItemStack>(allStacks());stacks.add(new ItemStack(DEBUG_AMMO.get()));return stacks;}
}
