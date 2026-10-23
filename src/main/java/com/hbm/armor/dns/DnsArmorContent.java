package com.hbm.armor.dns;

import com.hbm.armor.HazmatRegistry;
import com.hbm.exposure.ExposureProtection.Hazard;
import com.hbm.exposure.ExposureProtection;
import com.hbm.exposure.HbmExposure;
import com.hbm.armor.t51.T51Runtime;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
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

public final class DnsArmorContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<ArmorMaterial> MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, "hbm");
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> MATERIAL = MATERIALS.register("dns_armor",
        () -> new ArmorMaterial(Map.of(ArmorItem.Type.HELMET, 3, ArmorItem.Type.CHESTPLATE, 8,
            ArmorItem.Type.LEGGINGS, 6, ArmorItem.Type.BOOTS, 3), 0,
            BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.EMPTY), () -> Ingredient.of(HbmContent.item("plate_armor_dnt")),
            List.of(new ArmorMaterial.Layer(id("dns_armor"))), 0, 0));
    public static final DeferredItem<ArmorDns> HELMET = ITEMS.register("dns_helmet", () -> new ArmorDns(ArmorItem.Type.HELMET, "dns_helmet"));
    public static final DeferredItem<ArmorDns> PLATE = ITEMS.register("dns_plate", () -> new ArmorDns(ArmorItem.Type.CHESTPLATE, "dns_plate"));
    public static final DeferredItem<ArmorDns> LEGS = ITEMS.register("dns_legs", () -> new ArmorDns(ArmorItem.Type.LEGGINGS, "dns_legs"));
    public static final DeferredItem<ArmorDns> BOOTS = ITEMS.register("dns_boots", () -> new ArmorDns(ArmorItem.Type.BOOTS, "dns_boots"));
    public static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath("hbm", path); }
    public static void register(IEventBus bus) { MATERIALS.register(bus); ITEMS.register(bus); bus.addListener(DnsArmorContent::complete); bus.addListener(DnsArmorContent::creative); }
    private static void complete(FMLLoadCompleteEvent event) {
        event.enqueueWork(() -> HazmatRegistry.registerHazards(HELMET.get(), List.of(Hazard.PARTICLE_COARSE,
            Hazard.PARTICLE_FINE, Hazard.GAS_LUNG, Hazard.BACTERIA, Hazard.GAS_BLISTERING, Hazard.GAS_MONOXIDE, Hazard.LIGHT, Hazard.SAND)));
    }
    private static void creative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.COMBAT)) indexedItems().forEach(event::accept);
    }
    public static List<ItemStack> indexedItems() { return ITEMS.getEntries().stream().map(item -> new ItemStack(item.get())).toList(); }
    private DnsArmorContent() { }
}
