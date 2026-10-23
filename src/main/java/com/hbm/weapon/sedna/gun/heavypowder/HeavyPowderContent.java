package com.hbm.weapon.sedna.gun.heavypowder;
import java.util.List;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import com.hbm.registry.HbmContent;
import com.hbm.hazard.HbmHazards;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
public final class HeavyPowderContent {
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static boolean initialized;
 static{for(String name:List.of("gun_bolter","gun_double_barrel","gun_double_barrel_sacred_dragon","gun_autoshotgun_heretic"))ITEMS.register(name,()->gun(name));}
 private HeavyPowderContent(){}
 private static ItemGunBaseNT gun(String name){if(!initialized){initialized=true;XFactory75Bolt.init();XFactory10ga.init();}return switch(name){case "gun_bolter"->HeavyPowderGuns.gun_bolter;case "gun_double_barrel"->HeavyPowderGuns.gun_double_barrel;case "gun_double_barrel_sacred_dragon"->HeavyPowderGuns.gun_double_barrel_sacred_dragon;case "gun_autoshotgun_heretic"->HeavyPowderGuns.gun_autoshotgun_heretic;default->throw new IllegalArgumentException(name);};}
 public static void register(IEventBus bus,ModContainer container){XFactory75Bolt.initConfig();XFactory10ga.initConfig();HeavyPowderSettings.register(container);ITEMS.register(bus);bus.addListener(HeavyPowderContent::setup);}
 private static void setup(FMLCommonSetupEvent event){event.enqueueWork(()->{HeavyPowderMods.initialize();for(var bullet:List.of(XFactory10ga.g10,XFactory10ga.g10_shrapnel,XFactory10ga.g10_du,XFactory10ga.g10_slug,XFactory10ga.g10_explosive)){var casing=com.hbm.item.casing.FlatCasingContent.stack(6,1);bullet.setCasing(casing,4);}});}
 public static List<ItemStack> allStacks(){return List.of(new ItemStack(HeavyPowderGuns.gun_bolter),new ItemStack(HeavyPowderGuns.gun_double_barrel),new ItemStack(HeavyPowderGuns.gun_double_barrel_sacred_dragon),new ItemStack(HeavyPowderGuns.gun_autoshotgun_heretic));}
 public static List<ItemStack> indexedItems(){return allStacks();}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("weapon")?List.of(new ItemStack(HeavyPowderGuns.gun_bolter),new ItemStack(HeavyPowderGuns.gun_double_barrel)):List.of();}
}
