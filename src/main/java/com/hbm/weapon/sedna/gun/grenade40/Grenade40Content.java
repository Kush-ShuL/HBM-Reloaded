package com.hbm.weapon.sedna.gun.grenade40;
import java.util.List;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
public final class Grenade40Content {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static boolean initialized;
    static { ITEMS.register("gun_congolake",()->gun(false)); ITEMS.register("gun_mk108",()->gun(true)); }
    private Grenade40Content(){}
    private static ItemGunBaseNT gun(boolean mk108){if(!initialized){initialized=true;XFactory40mm.init();}return mk108?Grenade40Guns.gun_mk108:Grenade40Guns.gun_congolake;}
    public static void register(IEventBus bus,ModContainer container){XFactory40mm.initConfig();Grenade40Settings.register(container);ITEMS.register(bus);bus.addListener(Grenade40Content::setup);}
    private static void setup(FMLCommonSetupEvent event){event.enqueueWork(()->{Grenade40Ammo.initializeCasings();Grenade40Mods.initialize();});}
    public static List<ItemStack> allStacks(){return List.of(new ItemStack(Grenade40Guns.gun_congolake),new ItemStack(Grenade40Guns.gun_mk108));}
    public static List<ItemStack> indexedItems(){return allStacks();}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("weapon")?allStacks():List.of();}
}
