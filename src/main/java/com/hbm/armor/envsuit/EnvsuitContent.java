package com.hbm.armor.envsuit;

import com.hbm.armor.HazmatRegistry;
import com.hbm.exposure.ExposureProtection.Hazard;
import com.hbm.registry.HbmContent;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class EnvsuitContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<ArmorMaterial> MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, "hbm");
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> MATERIAL = MATERIALS.register("envsuit",
        () -> new ArmorMaterial(Map.of(ArmorItem.Type.HELMET, 3, ArmorItem.Type.CHESTPLATE, 8,
            ArmorItem.Type.LEGGINGS, 6, ArmorItem.Type.BOOTS, 3), 10,
            BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.EMPTY), () -> Ingredient.of(HbmContent.item("plate_armor_hev")),
            List.of(new ArmorMaterial.Layer(id("envsuit"))), 0, 0));
    public static final DeferredItem<ArmorEnvsuit> HELMET = ITEMS.register("envsuit_helmet", () -> new ArmorEnvsuit(ArmorItem.Type.HELMET, "envsuit_helmet"));
    public static final DeferredItem<ArmorEnvsuit> PLATE = ITEMS.register("envsuit_plate", () -> new ArmorEnvsuit(ArmorItem.Type.CHESTPLATE, "envsuit_plate"));
    public static final DeferredItem<ArmorEnvsuit> LEGS = ITEMS.register("envsuit_legs", () -> new ArmorEnvsuit(ArmorItem.Type.LEGGINGS, "envsuit_legs"));
    public static final DeferredItem<ArmorEnvsuit> BOOTS = ITEMS.register("envsuit_boots", () -> new ArmorEnvsuit(ArmorItem.Type.BOOTS, "envsuit_boots"));
    public static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath("hbm", path); }
    public static void register(IEventBus bus) { MATERIALS.register(bus); ITEMS.register(bus); bus.addListener(EnvsuitContent::complete); bus.addListener(EnvsuitContent::creative); }
    private static void complete(FMLLoadCompleteEvent event) {
        event.enqueueWork(() -> HazmatRegistry.registerHazards(HELMET.get(), List.of(Hazard.PARTICLE_COARSE,
            Hazard.PARTICLE_FINE, Hazard.GAS_LUNG, Hazard.BACTERIA, Hazard.GAS_BLISTERING, Hazard.GAS_MONOXIDE, Hazard.LIGHT, Hazard.SAND)));
    }
    private static void creative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.COMBAT)) indexedItems().forEach(event::accept);
    }
    public static List<ItemStack> indexedItems() { return ITEMS.getEntries().stream().map(item -> new ItemStack(item.get())).toList(); }
    private EnvsuitContent() { }
}
