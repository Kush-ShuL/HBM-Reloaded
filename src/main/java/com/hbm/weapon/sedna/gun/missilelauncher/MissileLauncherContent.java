package com.hbm.weapon.sedna.gun.missilelauncher;
import java.util.List;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import com.hbm.weapon.sedna.gun.mods.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.*;
public final class MissileLauncherContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredItem<ItemGunBaseNT> MISSILE_LAUNCHER=ITEMS.register("gun_missile_launcher",MissileLauncherFactory::create);
    private MissileLauncherContent(){}
    public static void register(IEventBus bus,ModContainer container){MissileLauncherSettings.register(container);ITEMS.register(bus);bus.addListener(MissileLauncherContent::setup);}
    private static void setup(FMLCommonSetupEvent event){event.enqueueWork(()->{
        var gun=MISSILE_LAUNCHER.get();
        SourceWeaponModBindings.definition("weapon_mod_generic",12).addMod(gun,SourceWeaponModBindings.handler(112,WeaponModGenericDamage.class,WeaponModGenericDamage::new));
        SourceWeaponModBindings.definition("weapon_mod_generic",13).addMod(gun,SourceWeaponModBindings.handler(113,WeaponModGenericDurability.class,WeaponModGenericDurability::new));
    });}
    public static List<ItemStack> allStacks(){return List.of(new ItemStack(MISSILE_LAUNCHER.get()));}
    public static List<ItemStack> indexedItems(){return allStacks();}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("weapon")?allStacks():List.of();}
}
