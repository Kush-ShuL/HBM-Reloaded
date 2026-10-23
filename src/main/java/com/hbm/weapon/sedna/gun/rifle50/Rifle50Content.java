package com.hbm.weapon.sedna.gun.rifle50;
import java.util.List;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
public final class Rifle50Content {
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static boolean initialized;
 static{for(String name:List.of("gun_amat","gun_amat_subtlety","gun_amat_penance","gun_m2"))ITEMS.register(name,()->gun(name));}
 private Rifle50Content(){}
 private static ItemGunBaseNT gun(String name){if(!initialized){initialized=true;XFactory50.init();}return switch(name){case "gun_amat"->Rifle50Guns.gun_amat;case "gun_amat_subtlety"->Rifle50Guns.gun_amat_subtlety;case "gun_amat_penance"->Rifle50Guns.gun_amat_penance;case "gun_m2"->Rifle50Guns.gun_m2;default->throw new IllegalArgumentException(name);};}
 public static void register(IEventBus bus,ModContainer container){XFactory50.initConfig();Rifle50Settings.register(container);Rifle50Entities.register(bus);ITEMS.register(bus);bus.addListener(Rifle50Content::setup);}
 private static void setup(FMLCommonSetupEvent event){event.enqueueWork(Rifle50Mods::initialize);}
 public static List<ItemStack> allStacks(){return List.of(new ItemStack(Rifle50Guns.gun_amat),new ItemStack(Rifle50Guns.gun_amat_subtlety),new ItemStack(Rifle50Guns.gun_amat_penance),new ItemStack(Rifle50Guns.gun_m2));}
 public static List<ItemStack> indexedItems(){return allStacks();}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("weapon")?List.of(new ItemStack(Rifle50Guns.gun_amat),new ItemStack(Rifle50Guns.gun_m2)):List.of();}
}
