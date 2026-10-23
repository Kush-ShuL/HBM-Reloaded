package com.hbm.weapon.sedna.gun.utility;
import java.util.List;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import com.hbm.weapon.sedna.gun.mods.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.*;
public final class ChargeThrowerContent {
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 public static final DeferredItem<ItemGunBaseNT> GUN=ITEMS.register("gun_charge_thrower",ChargeThrowerFactory::create);
 public static void register(IEventBus bus,ModContainer container){ChargeThrowerBullets.init();ITEMS.register(bus);container.registerConfig(ModConfig.Type.SERVER,ChargeThrowerSettings.SPEC,"hbm-charge-thrower.toml");bus.addListener(ChargeThrowerContent::setup);}
 private static void setup(FMLCommonSetupEvent event){event.enqueueWork(()->SourceWeaponModBindings.definition("weapon_mod_special",1).addMod(GUN.get(),SourceWeaponModBindings.handler(202,WeaponModScope.class,WeaponModScope::new)));}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("weapon")?allStacks():List.of();}
 public static List<ItemStack> allStacks(){return List.of(new ItemStack(GUN.get()));}
 private ChargeThrowerContent(){}
}
