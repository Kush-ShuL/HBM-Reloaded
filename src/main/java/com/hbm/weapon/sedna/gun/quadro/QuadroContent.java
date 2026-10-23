package com.hbm.weapon.sedna.gun.quadro;
import java.util.List;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import com.hbm.weapon.sedna.gun.mods.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.*;
public final class QuadroContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredItem<ItemGunBaseNT> QUADRO=ITEMS.register("gun_quadro",QuadroFactory::create);
    private QuadroContent(){}
    public static void register(IEventBus bus,ModContainer container){QuadroSettings.register(container);ITEMS.register(bus);bus.addListener(QuadroContent::setup);}
    private static void setup(FMLCommonSetupEvent event){event.enqueueWork(()->{
        var gun=QUADRO.get();
        SourceWeaponModBindings.definition("weapon_mod_generic",10).addMod(gun,SourceWeaponModBindings.handler(110,WeaponModGenericDamage.class,WeaponModGenericDamage::new));
        SourceWeaponModBindings.definition("weapon_mod_generic",11).addMod(gun,SourceWeaponModBindings.handler(111,WeaponModGenericDurability.class,WeaponModGenericDurability::new));
    });}
    public static List<ItemStack> allStacks(){return List.of(new ItemStack(QUADRO.get()));}
    public static List<ItemStack> indexedItems(){return allStacks();}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("weapon")?allStacks():List.of();}
}
