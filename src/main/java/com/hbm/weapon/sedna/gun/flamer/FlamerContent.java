package com.hbm.weapon.sedna.gun.flamer;

import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT.LambdaContext;
import com.hbm.weapon.sedna.gun.mods.SourceWeaponModBindings;
import com.hbm.weapon.sedna.gun.mods.WeaponModGenericDamage;
import com.hbm.weapon.sedna.gun.mods.WeaponModGenericDurability;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Actual three flamer gun identities; the already-real chemthrower remains its independent unique module. */
public final class FlamerContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final Map<String,DeferredItem<ItemGunBaseNT>> GUNS=new LinkedHashMap<>();
    public static volatile BiConsumer<ItemStack,LambdaContext> CLIENT_ORCHESTRA=(stack,context)->{};
    static {gun("gun_flamer",FlamerFactory::gun_flamer);gun("gun_flamer_topaz",FlamerFactory::gun_flamer_topaz);gun("gun_flamer_daybreaker",FlamerFactory::gun_flamer_daybreaker);}
    private static void gun(String name,Supplier<ItemGunBaseNT> factory){GUNS.put(name,ITEMS.register(name,factory));}
    public static void register(IEventBus bus,ModContainer container){FlamerBullets.init();ITEMS.register(bus);container.registerConfig(ModConfig.Type.SERVER,FlamerSettings.SPEC,"hbm-flamer-guns-server.toml");bus.addListener(FlamerContent::setup);}
    private static void setup(FMLCommonSetupEvent event){event.enqueueWork(()->{FlamerBullets.bindCasings();Item[] guns={GUNS.get("gun_flamer").get(),GUNS.get("gun_flamer_topaz").get()};
        SourceWeaponModBindings.definition("weapon_mod_generic",4).addMod(guns,SourceWeaponModBindings.handler(104,WeaponModGenericDamage.class,WeaponModGenericDamage::new));
        SourceWeaponModBindings.definition("weapon_mod_generic",5).addMod(guns,SourceWeaponModBindings.handler(105,WeaponModGenericDurability.class,WeaponModGenericDurability::new));
    });}
    public static List<ItemStack> allStacks(){return GUNS.values().stream().map(item->new ItemStack(item.get())).toList();}
    public static List<ItemStack> indexedItems(){return allStacks();}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("weapon")?List.of(new ItemStack(GUNS.get("gun_flamer").get())):List.of();}
    private FlamerContent(){}
}
