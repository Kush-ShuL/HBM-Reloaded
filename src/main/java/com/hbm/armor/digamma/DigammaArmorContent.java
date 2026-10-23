package com.hbm.armor.digamma;

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

public final class DigammaArmorContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<ArmorMaterial> MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, "hbm");
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> MATERIAL = MATERIALS.register("digamma_armor",
        () -> new ArmorMaterial(Map.of(ArmorItem.Type.HELMET, 3, ArmorItem.Type.CHESTPLATE, 8,
            ArmorItem.Type.LEGGINGS, 6, ArmorItem.Type.BOOTS, 3), 0,
            BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.EMPTY), () -> Ingredient.of(HbmContent.item("plate_armor_fau")),
            List.of(new ArmorMaterial.Layer(id("digamma_armor"))), 0, 0));
    public static final DeferredItem<ArmorDigamma> HELMET = ITEMS.register("fau_helmet", () -> new ArmorDigamma(ArmorItem.Type.HELMET, "fau_helmet"));
    public static final DeferredItem<ArmorDigamma> PLATE = ITEMS.register("fau_plate", () -> new ArmorDigamma(ArmorItem.Type.CHESTPLATE, "fau_plate"));
    public static final DeferredItem<ArmorDigamma> LEGS = ITEMS.register("fau_legs", () -> new ArmorDigamma(ArmorItem.Type.LEGGINGS, "fau_legs"));
    public static final DeferredItem<ArmorDigamma> BOOTS = ITEMS.register("fau_boots", () -> new ArmorDigamma(ArmorItem.Type.BOOTS, "fau_boots"));
    public static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath("hbm", path); }
    public static void register(IEventBus bus) { MATERIALS.register(bus); ITEMS.register(bus); bus.addListener(DigammaArmorContent::complete); bus.addListener(DigammaArmorContent::creative); }
    private static void complete(FMLLoadCompleteEvent event) {
        event.enqueueWork(() -> HazmatRegistry.registerHazards(HELMET.get(), List.of(Hazard.PARTICLE_COARSE,
            Hazard.PARTICLE_FINE, Hazard.GAS_LUNG, Hazard.BACTERIA, Hazard.GAS_BLISTERING, Hazard.GAS_MONOXIDE, Hazard.LIGHT, Hazard.SAND)));
    }
    private static void creative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.COMBAT)) indexedItems().forEach(event::accept);
    }
    public static List<ItemStack> indexedItems() { return ITEMS.getEntries().stream().map(item -> new ItemStack(item.get())).toList(); }
    private DigammaArmorContent() { }
}
