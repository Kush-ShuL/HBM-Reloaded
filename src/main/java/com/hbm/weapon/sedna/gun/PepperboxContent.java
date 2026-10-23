package com.hbm.weapon.sedna.gun;

import com.hbm.hazard.HbmHazards;
import com.hbm.registry.HbmContent;
import com.hbm.weapon.sedna.gun.factory.PepperboxFactory;
import com.hbm.weapon.sedna.gun.mods.*;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.*;

public final class PepperboxContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredItem<ItemGunBaseNT> PEPPERBOX=ITEMS.register("gun_pepperbox",PepperboxFactory::create);
    public static void register(IEventBus bus,ModContainer container){PepperboxFactory.initBullets();ITEMS.register(bus);GunStats.register(bus);container.registerConfig(ModConfig.Type.SERVER,GunSettings.SPEC,"hbm-sedna-server.toml");container.registerConfig(ModConfig.Type.CLIENT,GunClientSettings.SPEC,"hbm-sedna-client.toml");bus.addListener(PepperboxContent::setup);}
    private static void setup(FMLCommonSetupEvent event){event.enqueueWork(()->{
        new XWeaponModManager.WeaponModDefinition(mod("weapon_mod_generic",0)).addMod(PEPPERBOX.get(),new WeaponModGenericDamage(100));
        new XWeaponModManager.WeaponModDefinition(mod("weapon_mod_generic",1)).addMod(PEPPERBOX.get(),new WeaponModGenericDurability(101));
        new XWeaponModManager.WeaponModDefinition(mod("weapon_mod_special",6)).addMod(PEPPERBOX.get(),new WeaponModChoke(210));
    });}
    private static ItemStack mod(String name,int metadata){var stack=new ItemStack(HbmContent.item(name));stack.set(HbmHazards.LEGACY_METADATA.get(),metadata);return stack;}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("weapon")?List.of(new ItemStack(PEPPERBOX.get())):List.of();}
    private PepperboxContent(){}
}
