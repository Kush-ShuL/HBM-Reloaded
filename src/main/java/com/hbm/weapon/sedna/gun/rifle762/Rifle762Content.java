package com.hbm.weapon.sedna.gun.rifle762;
import java.util.List;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import com.hbm.registry.HbmContent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
public final class Rifle762Content {
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static boolean initialized;
 static{for(String name:List.of("gun_carbine","gun_minigun","gun_minigun_lacunae","gun_minigun_dual","gun_mas36"))ITEMS.register(name,()->gun(name));}
 private Rifle762Content(){}
 private static ItemGunBaseNT gun(String name){if(!initialized){initialized=true;XFactory762mm.init();}return switch(name){case "gun_carbine"->Rifle762Guns.gun_carbine;case "gun_minigun"->Rifle762Guns.gun_minigun;case "gun_minigun_lacunae"->Rifle762Guns.gun_minigun_lacunae;case "gun_minigun_dual"->Rifle762Guns.gun_minigun_dual;case "gun_mas36"->Rifle762Guns.gun_mas36;default->throw new IllegalArgumentException(name);};}
 public static void register(IEventBus bus,ModContainer container){XFactory762mm.initConfig();Rifle762Settings.register(container);ITEMS.register(bus);bus.addListener(Rifle762Content::setup);}
 private static void setup(FMLCommonSetupEvent event){event.enqueueWork(()->{Rifle762Mods.initialize();for(var bullet:List.of(XFactory762mm.energy_lacunae,XFactory762mm.energy_lacunae_overcharge,XFactory762mm.energy_lacunae_ir))bullet.setCasing(new ItemStack(HbmContent.item("ingot_polymer"),2),4*40);});}
 public static List<ItemStack> allStacks(){return List.of(new ItemStack(Rifle762Guns.gun_carbine),new ItemStack(Rifle762Guns.gun_minigun),new ItemStack(Rifle762Guns.gun_minigun_lacunae),new ItemStack(Rifle762Guns.gun_minigun_dual),new ItemStack(Rifle762Guns.gun_mas36));}
 public static List<ItemStack> indexedItems(){return allStacks();}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("weapon")?List.of(new ItemStack(Rifle762Guns.gun_carbine),new ItemStack(Rifle762Guns.gun_minigun)):List.of();}
}
