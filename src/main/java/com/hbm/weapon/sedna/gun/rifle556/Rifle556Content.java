package com.hbm.weapon.sedna.gun.rifle556;
import java.util.List;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
public final class Rifle556Content {
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static boolean initialized;
 static{for(String name:List.of("gun_g3","gun_g3_zebra","gun_stg77"))ITEMS.register(name,()->gun(name));}
 private Rifle556Content(){}
 private static ItemGunBaseNT gun(String name){if(!initialized){initialized=true;XFactory556mm.init();}return switch(name){case "gun_g3"->Rifle556Guns.gun_g3;case "gun_g3_zebra"->Rifle556Guns.gun_g3_zebra;case "gun_stg77"->Rifle556Guns.gun_stg77;default->throw new IllegalArgumentException(name);};}
 public static void register(IEventBus bus,ModContainer container){XFactory556mm.initConfig();Rifle556Settings.register(container);ITEMS.register(bus);bus.addListener(Rifle556Content::setup);}
 private static void setup(FMLCommonSetupEvent event){event.enqueueWork(Rifle556Mods::initialize);}
 public static List<ItemStack> allStacks(){return List.of(new ItemStack(Rifle556Guns.gun_g3),new ItemStack(Rifle556Guns.gun_g3_zebra),new ItemStack(Rifle556Guns.gun_stg77));}
 public static List<ItemStack> indexedItems(){return allStacks();}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("weapon")?List.of(new ItemStack(Rifle556Guns.gun_g3),new ItemStack(Rifle556Guns.gun_stg77)):List.of();}
}
