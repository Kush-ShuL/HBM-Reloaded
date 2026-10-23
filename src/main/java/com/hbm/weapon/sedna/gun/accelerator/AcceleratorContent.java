package com.hbm.weapon.sedna.gun.accelerator;
import com.hbm.registry.HbmContent;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.mods.*;
import java.util.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.*;
/** True source Accelerator declarations; client/recipe closure is required before admission. */
public final class AcceleratorContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static boolean initialized;
    public static final Map<String,DeferredItem<ItemGunBaseNT>> GUNS=new LinkedHashMap<>();
    static {for(String name:List.of("gun_tau","gun_coilgun","gun_n_i_4_n_i"))GUNS.put(name,ITEMS.register(name,()->gun(name)));}
    private static ItemGunBaseNT gun(String name){if(!initialized){initialized=true;AcceleratorFactory.init();}return switch(name){case "gun_tau"->AcceleratorGuns.gun_tau;case "gun_coilgun"->AcceleratorGuns.gun_coilgun;case "gun_n_i_4_n_i"->AcceleratorGuns.gun_n_i_4_n_i;default->throw new IllegalArgumentException(name);};}
    public static void register(IEventBus bus,ModContainer container){AcceleratorSettings.register(container);ITEMS.register(bus);bus.addListener(AcceleratorContent::setup);NeoForge.EVENT_BUS.addListener(WeaponCustomizeCommand::register);}
    private static void setup(FMLCommonSetupEvent event){event.enqueueWork(()->{
        for(var bullet:List.of(AcceleratorFactory.tau_uranium,AcceleratorFactory.tau_uranium_charge))bullet.setCasing(new ItemStack(HbmContent.item("plate_lead"),2),16);
        SourceWeaponModBindings.definition("weapon_mod_generic",14).addMod(AcceleratorGuns.gun_tau,SourceWeaponModBindings.handler(114,WeaponModGenericDamage.class,WeaponModGenericDamage::new));
        SourceWeaponModBindings.definition("weapon_mod_generic",15).addMod(AcceleratorGuns.gun_tau,SourceWeaponModBindings.handler(115,WeaponModGenericDurability.class,WeaponModGenericDurability::new));
        SourceWeaponModBindings.definition("weapon_mod_special",16).addMod(AcceleratorGuns.gun_n_i_4_n_i,SourceWeaponModBindings.handler(220,WeaponModNickel.class,id->new WeaponModNickel(id,"COIN1")));
        SourceWeaponModBindings.definition("weapon_mod_special",17).addMod(AcceleratorGuns.gun_n_i_4_n_i,SourceWeaponModBindings.handler(221,WeaponModNickel.class,id->new WeaponModNickel(id,"COIN2")));
    });}
    public static List<ItemStack> indexedItems(){return GUNS.values().stream().map(holder->new ItemStack(holder.get())).toList();}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("weapon")?indexedItems():List.of();}
    private AcceleratorContent(){}
}
