package com.hbm.weapon.sedna.gun.flaregun;
import java.util.List;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import com.hbm.weapon.sedna.gun.mods.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.*;
public final class FlaregunContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredItem<ItemGunBaseNT> FLAREGUN=ITEMS.register("gun_flaregun",FlaregunFactory::create);
    private FlaregunContent(){}
    public static void register(IEventBus bus,ModContainer container){FlaregunSettings.register(container);ITEMS.register(bus);bus.addListener(FlaregunContent::setup);}
    private static void setup(FMLCommonSetupEvent event){event.enqueueWork(()->{
        FlaregunBullets.initializeCasings();var gun=FLAREGUN.get();
        SourceWeaponModBindings.definition("weapon_mod_generic",2).addMod(gun,SourceWeaponModBindings.handler(102,WeaponModGenericDamage.class,WeaponModGenericDamage::new));
        SourceWeaponModBindings.definition("weapon_mod_generic",3).addMod(gun,SourceWeaponModBindings.handler(103,WeaponModGenericDurability.class,WeaponModGenericDurability::new));
    });}
    public static List<ItemStack> allStacks(){return List.of(new ItemStack(FLAREGUN.get()));}
    public static List<ItemStack> indexedItems(){return allStacks();}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("weapon")?allStacks():List.of();}
}
