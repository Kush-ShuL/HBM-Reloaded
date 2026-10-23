package com.hbm.armor.cobalt;

import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Actual source cobalt quartet; the existing resistance and radiation tables bind these exact identities. */
public final class CobaltArmorContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<ArmorMaterial> MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, "hbm");

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> MATERIAL = MATERIALS.register("cobalt", () ->
        new ArmorMaterial(Map.of(ArmorItem.Type.HELMET, 3, ArmorItem.Type.CHESTPLATE, 8,
                                 ArmorItem.Type.LEGGINGS, 6, ArmorItem.Type.BOOTS, 3),
                          60, BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.EMPTY),
                          () -> Ingredient.of(com.hbm.registry.HbmContent.item("ingot_cobalt")),
                          List.of(new ArmorMaterial.Layer(ResourceLocation.parse("hbm:cobalt"))), 0, 0));

    public static final DeferredItem<CobaltArmorItem> HELMET = ITEMS.register("cobalt_helmet", () -> new CobaltArmorItem(MATERIAL, ArmorItem.Type.HELMET));
    public static final DeferredItem<CobaltArmorItem> PLATE = ITEMS.register("cobalt_plate", () -> new CobaltArmorItem(MATERIAL, ArmorItem.Type.CHESTPLATE));
    public static final DeferredItem<CobaltArmorItem> LEGS = ITEMS.register("cobalt_legs", () -> new CobaltArmorItem(MATERIAL, ArmorItem.Type.LEGGINGS));
    public static final DeferredItem<CobaltArmorItem> BOOTS = ITEMS.register("cobalt_boots", () -> new CobaltArmorItem(MATERIAL, ArmorItem.Type.BOOTS));

    public static void register(IEventBus bus) {
        MATERIALS.register(bus);
        ITEMS.register(bus);
        bus.addListener(CobaltArmorContent::creative);
    }

    private static void creative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.COMBAT))
            for (var item : List.of(HELMET, PLATE, LEGS, BOOTS)) event.accept(item.get());
    }

    private CobaltArmorContent() {}
}
