package com.hbm.weapon.sedna.gun.energy;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.mods.*;
import com.hbm.weapon.sedna.gun.energy.mods.*;
import com.hbm.weapon.sedna.gun.energy.bag.*;
import java.util.*;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.*;
/** Only the true XFactoryEnergy guns and their actual ammunition-bag dependency. */
public final class EnergyContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final Map<String,DeferredItem<ItemGunBaseNT>> GUNS=new LinkedHashMap<>();
    static {gun("gun_tesla_cannon",EnergyFactory::gun_tesla_cannon);gun("gun_laser_pistol",EnergyFactory::gun_laser_pistol);gun("gun_laser_pistol_pew_pew",EnergyFactory::gun_laser_pistol_pew_pew);gun("gun_laser_pistol_morning_glory",EnergyFactory::gun_laser_pistol_morning_glory);gun("gun_lasrifle",EnergyFactory::gun_lasrifle);}
    private static void gun(String name,Supplier<ItemGunBaseNT> factory){GUNS.put(name,ITEMS.register(name,factory));}
    public static final DeferredItem<AmmoBagItem> AMMO_BAG=AmmoBagContent.AMMO_BAG,AMMO_BAG_INFINITE=AmmoBagContent.AMMO_BAG_INFINITE;
    public static final DeferredHolder<MenuType<?>,MenuType<AmmoBagMenu>> AMMO_BAG_MENU=AmmoBagContent.AMMO_BAG_MENU;
    public static void register(IEventBus bus,ModContainer container){AmmoBagContent.register(bus);EnergyBullets.init();ITEMS.register(bus);container.registerConfig(ModConfig.Type.SERVER,EnergySettings.SPEC,"hbm-energy-guns-server.toml");bus.addListener(EnergyContent::setup);}
    private static void setup(FMLCommonSetupEvent event){event.enqueueWork(()->{EnergyBullets.bindCasings();
        bindGeneric(12,112,113,new Item[]{GUNS.get("gun_tesla_cannon").get()});
        bindGeneric(14,114,115,new Item[]{GUNS.get("gun_laser_pistol").get(),GUNS.get("gun_laser_pistol_pew_pew").get()});
        bindGeneric(16,116,117,new Item[]{GUNS.get("gun_lasrifle").get()});
        SourceWeaponModBindings.definition("weapon_mod_special",13).addMod(GUNS.get("gun_lasrifle").get(),SourceWeaponModBindings.handler(216,WeaponModLasShotgun.class,WeaponModLasShotgun::new));
        SourceWeaponModBindings.definition("weapon_mod_special",14).addMod(GUNS.get("gun_lasrifle").get(),SourceWeaponModBindings.handler(217,WeaponModLasCapacitor.class,WeaponModLasCapacitor::new));
        SourceWeaponModBindings.definition("weapon_mod_special",15).addMod(GUNS.get("gun_lasrifle").get(),SourceWeaponModBindings.handler(218,WeaponModLasAuto.class,WeaponModLasAuto::new));
    });}
    private static void bindGeneric(int meta,int damageId,int durabilityId,Item[] guns){SourceWeaponModBindings.definition("weapon_mod_generic",meta).addMod(guns,SourceWeaponModBindings.handler(damageId,WeaponModGenericDamage.class,WeaponModGenericDamage::new));SourceWeaponModBindings.definition("weapon_mod_generic",meta+1).addMod(guns,SourceWeaponModBindings.handler(durabilityId,WeaponModGenericDurability.class,WeaponModGenericDurability::new));}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("weapon")?List.of("gun_tesla_cannon","gun_laser_pistol","gun_lasrifle").stream().map(name->new ItemStack(GUNS.get(name).get())).toList():List.of();}
    public static List<ItemStack> allStacks(){return GUNS.values().stream().map(item->new ItemStack(item.get())).toList();}
    public static List<ItemStack> indexedItems(){return allStacks();}
    private EnergyContent(){}
}
