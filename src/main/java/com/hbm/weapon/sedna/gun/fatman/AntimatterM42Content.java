package com.hbm.weapon.sedna.gun.fatman;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import com.hbm.weapon.sedna.gun.mods.*;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.*;
/** Two exact active source identities. Retired clip_fatman remains unregistered. */
public final class AntimatterM42Content {
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 public static final DeferredItem<AntimatterClusterItem> ANTIMATTER=ITEMS.register("pellet_antimatter",AntimatterClusterItem::new);
 public static final DeferredItem<ItemGunBaseNT> FATMAN=ITEMS.register("gun_fatman",FatmanFactory::create);
 public static void register(IEventBus bus,ModContainer container){FatmanSettings.register(container);FatmanBullets.init();ITEMS.register(bus);bus.addListener(AntimatterM42Content::setup);}
 private static void setup(FMLCommonSetupEvent event){event.enqueueWork(()->{var gun=FATMAN.get();SourceWeaponModBindings.definition("weapon_mod_generic",14).addMod(gun,SourceWeaponModBindings.handler(114,WeaponModGenericDamage.class,WeaponModGenericDamage::new));SourceWeaponModBindings.definition("weapon_mod_generic",15).addMod(gun,SourceWeaponModBindings.handler(115,WeaponModGenericDurability.class,WeaponModGenericDurability::new));});}
 public static List<ItemStack> indexedItems(){return List.of(new ItemStack(ANTIMATTER.get()),new ItemStack(FATMAN.get()));}public static List<ItemStack> allStacks(){return indexedItems();}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("control")?List.of(new ItemStack(ANTIMATTER.get())):tab.equals("weapon")?List.of(new ItemStack(FATMAN.get())):List.of();}
 private AntimatterM42Content(){}
}
