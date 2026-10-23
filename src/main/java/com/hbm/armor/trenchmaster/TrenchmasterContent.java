package com.hbm.armor.trenchmaster;

import com.hbm.armor.HazmatRegistry;
import com.hbm.exposure.ExposureProtection.Hazard;
import com.hbm.registry.HbmContent;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.*;

/** Actual four ModItemsArmor319..333 items, with their own original HBM_TRENCH material identity. */
public final class TrenchmasterContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<ArmorMaterial> MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, "hbm");
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> MATERIAL = MATERIALS.register("trenchmaster", () -> new ArmorMaterial(
            Map.of(ArmorItem.Type.HELMET, 3, ArmorItem.Type.CHESTPLATE, 8, ArmorItem.Type.LEGGINGS, 6, ArmorItem.Type.BOOTS, 3), 0,
            BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.EMPTY), () -> Ingredient.of(HbmContent.item("plate_iron")),
            List.of(new ArmorMaterial.Layer(id("trenchmaster"))), 0, 0));
    public static final DeferredItem<ArmorTrenchmaster> HELMET = ITEMS.register("trenchmaster_helmet", () -> new ArmorTrenchmaster(MATERIAL, ArmorItem.Type.HELMET));
    public static final DeferredItem<ArmorTrenchmaster> PLATE = ITEMS.register("trenchmaster_plate", () -> new ArmorTrenchmaster(MATERIAL, ArmorItem.Type.CHESTPLATE));
    public static final DeferredItem<ArmorTrenchmaster> LEGS = ITEMS.register("trenchmaster_legs", () -> new ArmorTrenchmaster(MATERIAL, ArmorItem.Type.LEGGINGS));
    public static final DeferredItem<ArmorTrenchmaster> BOOTS = ITEMS.register("trenchmaster_boots", () -> new ArmorTrenchmaster(MATERIAL, ArmorItem.Type.BOOTS));

    public static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath("hbm", path); }
    public static void register(IEventBus bus) {
        MATERIALS.register(bus);
        ITEMS.register(bus);
        bus.addListener(TrenchmasterContent::complete);
        bus.addListener(TrenchmasterContent::creative);
        NeoForge.EVENT_BUS.addListener(TrenchmasterRuntime::tick);
    }
    private static void complete(FMLLoadCompleteEvent event) {
        event.enqueueWork(() -> {
            // The original cloneStats shares effects/radiation but does NOT clone hazard classes.
            HazmatRegistry.registerHazards(HELMET.get(), List.of(Hazard.PARTICLE_COARSE, Hazard.PARTICLE_FINE,
                    Hazard.GAS_LUNG, Hazard.BACTERIA, Hazard.GAS_BLISTERING, Hazard.GAS_MONOXIDE, Hazard.LIGHT, Hazard.SAND));
        });
    }
    private static void creative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.COMBAT))
            for (var item : List.of(HELMET, PLATE, LEGS, BOOTS)) event.accept(item.get());
    }
    private TrenchmasterContent() {}
}
