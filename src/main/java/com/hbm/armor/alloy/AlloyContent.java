package com.hbm.armor.alloy;

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

/** Deprecated does not mean unregistered: source alloy is still real MobUtil weighted equipment. */
public final class AlloyContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<ArmorMaterial> MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, "hbm");

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> MATERIAL = MATERIALS.register("alloy", () ->
        new ArmorMaterial(Map.of(ArmorItem.Type.HELMET, 3, ArmorItem.Type.CHESTPLATE, 8,
                                 ArmorItem.Type.LEGGINGS, 6, ArmorItem.Type.BOOTS, 3),
                          12, BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.EMPTY),
                          () -> Ingredient.EMPTY,
                          List.of(new ArmorMaterial.Layer(ResourceLocation.parse("hbm:alloy"))), 0, 0));

    public static final DeferredItem<AlloyArmorItem> HELMET = ITEMS.register("alloy_helmet", () -> new AlloyArmorItem(MATERIAL, ArmorItem.Type.HELMET));
    public static final DeferredItem<AlloyArmorItem> PLATE = ITEMS.register("alloy_plate", () -> new AlloyArmorItem(MATERIAL, ArmorItem.Type.CHESTPLATE));
    public static final DeferredItem<AlloyArmorItem> LEGS = ITEMS.register("alloy_legs", () -> new AlloyArmorItem(MATERIAL, ArmorItem.Type.LEGGINGS));
    public static final DeferredItem<AlloyArmorItem> BOOTS = ITEMS.register("alloy_boots", () -> new AlloyArmorItem(MATERIAL, ArmorItem.Type.BOOTS));

    public static void register(IEventBus bus) {
        MATERIALS.register(bus);
        ITEMS.register(bus);
        bus.addListener(AlloyContent::creative);
    }

    private static void creative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.COMBAT))
            for (var item : List.of(HELMET, PLATE, LEGS, BOOTS)) event.accept(item.get());
    }

    private AlloyContent() {}
}
