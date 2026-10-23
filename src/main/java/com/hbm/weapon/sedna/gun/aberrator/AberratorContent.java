package com.hbm.weapon.sedna.gun.aberrator;
import java.util.List;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
public final class AberratorContent {
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static boolean initialized;
 static{for(String name:List.of("gun_aberrator","gun_aberrator_eott"))ITEMS.register(name,()->gun(name));}
 private AberratorContent(){}
 private static ItemGunBaseNT gun(String name){if(!initialized){initialized=true;XFactory35800.init();}return switch(name){case "gun_aberrator"->AberratorGuns.gun_aberrator;case "gun_aberrator_eott"->AberratorGuns.gun_aberrator_eott;default->throw new IllegalArgumentException(name);};}
 public static void register(IEventBus bus,ModContainer container){XFactory35800.initConfig();AberratorSettings.register(container);ITEMS.register(bus);bus.addListener(AberratorContent::setup);}
 private static void setup(FMLCommonSetupEvent event){event.enqueueWork(AberratorMods::initialize);}
 public static List<ItemStack> allStacks(){return List.of(new ItemStack(AberratorGuns.gun_aberrator),new ItemStack(AberratorGuns.gun_aberrator_eott));}
 public static List<ItemStack> indexedItems(){return allStacks();}
 public static List<ItemStack> creativeStacks(String tab){return List.of();}
}
