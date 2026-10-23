package com.hbm.weapon.sedna.gun.drill;
import com.hbm.weapon.sedna.gun.mods.SourceWeaponModBindings;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.*;
/** One actual gun; all eleven attachment items are the existing source EnumModSpecial metadata, never duplicate registrations. */
public final class DrillContent {
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 public static final DeferredItem<ItemGunDrill> GUN=ITEMS.register("gun_drill",XFactoryDrill::create);
 public static void register(IEventBus bus,ModContainer container){ITEMS.register(bus);container.registerConfig(ModConfig.Type.SERVER,DrillSettings.SPEC,"hbm-drill-gun-server.toml");bus.addListener((FMLCommonSetupEvent event)->event.enqueueWork(DrillContent::bind));}
 private static void bind(){var gun=GUN.get();
  SourceWeaponModBindings.definition("weapon_mod_special",18).addMod(gun,SourceWeaponModBindings.handler(222,WeaponModDrill.class,id->new WeaponModDrill(id).damage(1.25F).dt(3).pierce(.15F).harvest(3)));
  SourceWeaponModBindings.definition("weapon_mod_special",19).addMod(gun,SourceWeaponModBindings.handler(223,WeaponModDrill.class,id->new WeaponModDrill(id).damage(1.5F).dt(5).pierce(.2F).aoe(2).harvest(3)));
  SourceWeaponModBindings.definition("weapon_mod_special",20).addMod(gun,SourceWeaponModBindings.handler(224,WeaponModDrill.class,id->new WeaponModDrill(id).damage(2).dt(7.5F).pierce(.2F).reach(2).aoe(3).harvest(4)));
  SourceWeaponModBindings.definition("weapon_mod_special",21).addMod(gun,SourceWeaponModBindings.handler(225,WeaponModDrill.class,id->new WeaponModDrill(id).damage(3).dt(10).pierce(.25F).reach(2).aoe(3).harvest(5)));
  SourceWeaponModBindings.definition("weapon_mod_special",22).addMod(gun,SourceWeaponModBindings.handler(226,WeaponModEngine.class,id->new WeaponModEngine(id).mag(WeaponModEngine.ENGINE_DIESEL).delay(15)));
  SourceWeaponModBindings.definition("weapon_mod_special",23).addMod(gun,SourceWeaponModBindings.handler(227,WeaponModEngine.class,id->new WeaponModEngine(id).mag(WeaponModEngine.ENGINE_AVIATION).delay(10)));
  SourceWeaponModBindings.definition("weapon_mod_special",24).addMod(gun,SourceWeaponModBindings.handler(228,WeaponModEngine.class,id->new WeaponModEngine(id).mag(WeaponModEngine.ENGINE_ELECTRIC).delay(15)));
  SourceWeaponModBindings.definition("weapon_mod_special",25).addMod(gun,SourceWeaponModBindings.handler(229,WeaponModEngine.class,id->new WeaponModEngine(id).mag(WeaponModEngine.ENGINE_TURBO).delay(5)));
  SourceWeaponModBindings.definition("weapon_mod_special",26).addMod(gun,SourceWeaponModBindings.handler(230,WeaponModDrillFortune.class,id->new WeaponModDrillFortune(id,"MAGNET",2)));
  SourceWeaponModBindings.definition("weapon_mod_special",27).addMod(gun,SourceWeaponModBindings.handler(231,WeaponModDrillFortune.class,id->new WeaponModDrillFortune(id,"SIFTER",1)));
  SourceWeaponModBindings.definition("weapon_mod_special",28).addMod(gun,SourceWeaponModBindings.handler(232,WeaponModCanisters.class,WeaponModCanisters::new));
 }
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("weapon")?List.of(new ItemStack(GUN.get())):List.of();}private DrillContent(){}
}
