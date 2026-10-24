package com.hbm.weapon.sedna.gun.legendary;
import com.hbm.hazard.HbmHazards;
import com.hbm.machine.rtg.RtgLootCrateBlock;
import com.hbm.registry.HbmContent;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import com.hbm.weapon.sedna.gun.mods.*;
import java.util.List;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.*;
/** Admit only together with the real vehicle impact callbacks and the unchanged complete redList. */
public final class LegendaryGunsContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    public static final DeferredItem<ItemGunBaseNT> LILMAC=ITEMS.register("gun_heavy_revolver_lilmac",LegendaryGunFactory::lilmac),BROKEN=ITEMS.register("gun_maresleg_broken",LegendaryGunFactory::broken),SEXY=ITEMS.register("gun_autoshotgun_sexy",LegendaryGunFactory::sexy);
    public static final DeferredBlock<RtgLootCrateBlock> CRATE=BLOCKS.register("crate_red",()->new RtgLootCrateBlock("redList",false));
    public static final DeferredItem<BlockItem> CRATE_ITEM=ITEMS.registerSimpleBlockItem(CRATE);
    public static void register(IEventBus bus,ModContainer container){LegendaryGunFactory.initBullets();BLOCKS.register(bus);ITEMS.register(bus);container.registerConfig(ModConfig.Type.SERVER,LegendaryGunSettings.SPEC,"hbm-sedna-legendary.toml");bus.addListener(LegendaryGunsContent::setup);}
    private static void setup(FMLCommonSetupEvent event){event.enqueueWork(()->{LegendaryGunFactory.initCasingItems();LegendaryImpacts.install((bullet,hit)->{com.hbm.weapon.specialvehicle.VehicleImpacts.spawnBoxcar(bullet.level(),hit.getLocation());bullet.discard();},(bullet,hit)->{com.hbm.weapon.specialvehicle.VehicleImpacts.spawnBoat(bullet.level(),hit.getLocation());bullet.discard();});var definition=SourceWeaponModBindings.definition("weapon_mod_special",6);var choke=XWeaponModManager.idToMod.get(210);if(choke==null)choke=new WeaponModChoke(210);definition.addMod(SEXY.get(),choke);});}
    public static List<ItemStack> variants(){return List.of(new ItemStack(LILMAC.get()),new ItemStack(BROKEN.get()),new ItemStack(SEXY.get()));}
    public static List<ItemStack> indexedItems(){var result=new java.util.ArrayList<ItemStack>(variants());result.add(new ItemStack(CRATE_ITEM.get()));return result;}
    /** Expose every registered legendary gun in the weapon tab, including the other source gun families. */
    public static List<ItemStack> creativeStacks(String tab){
        if(!tab.equals("weapon"))return List.of();
        return net.minecraft.core.registries.BuiltInRegistries.ITEM.stream()
            .filter(item->item instanceof ItemGunBaseNT gun&&gun.quality==ItemGunBaseNT.WeaponQuality.LEGENDARY)
            .map(ItemStack::new).toList();
    }
    private LegendaryGunsContent(){}
}
