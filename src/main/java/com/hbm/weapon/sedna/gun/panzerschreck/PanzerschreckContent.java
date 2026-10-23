package com.hbm.weapon.sedna.gun.panzerschreck;
import java.util.List;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import com.hbm.weapon.sedna.gun.mods.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.*;
public final class PanzerschreckContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredItem<ItemGunBaseNT> PANZERSCHRECK=ITEMS.register("gun_panzerschreck",PanzerschreckFactory::create);
    private PanzerschreckContent(){}
    public static void register(IEventBus bus,ModContainer container){PanzerschreckSettings.register(container);ITEMS.register(bus);bus.addListener(PanzerschreckContent::setup);}
    private static void setup(FMLCommonSetupEvent event){event.enqueueWork(()->{
        var gun=PANZERSCHRECK.get();
        SourceWeaponModBindings.definition("weapon_mod_generic",6).addMod(gun,SourceWeaponModBindings.handler(106,WeaponModGenericDamage.class,WeaponModGenericDamage::new));
        SourceWeaponModBindings.definition("weapon_mod_generic",7).addMod(gun,SourceWeaponModBindings.handler(107,WeaponModGenericDurability.class,WeaponModGenericDurability::new));
        SourceWeaponModBindings.definition("weapon_mod_special",2).addMod(gun,SourceWeaponModBindings.handler(204,WeaponModPanzerschreckSawedOff.class,WeaponModPanzerschreckSawedOff::new));
    });}
    public static List<ItemStack> allStacks(){return List.of(new ItemStack(PANZERSCHRECK.get()));}
    public static List<ItemStack> indexedItems(){return allStacks();}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("weapon")?allStacks():List.of();}
}
