package com.hbm.armor.asbestos;

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
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The only original fire proximity set: existing canonical resistance and hazard tables bind these real IDs. */
public final class AsbestosContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<ArmorMaterial> MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, "hbm");
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> MATERIAL = MATERIALS.register("asbestos", () ->
        new ArmorMaterial(Map.of(ArmorItem.Type.HELMET, 1, ArmorItem.Type.CHESTPLATE, 4,
                                 ArmorItem.Type.LEGGINGS, 3, ArmorItem.Type.BOOTS, 1),
                          5, BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.EMPTY),
                          () -> Ingredient.of(HbmContent.item("asbestos_cloth")),
                          List.of(new ArmorMaterial.Layer(ResourceLocation.parse("hbm:asbestos"))), 0, 0));
    public static final DeferredItem<AsbestosArmorItem> HELMET = ITEMS.register("asbestos_helmet", () -> new AsbestosArmorItem(MATERIAL, ArmorItem.Type.HELMET));
    public static final DeferredItem<AsbestosArmorItem> PLATE = ITEMS.register("asbestos_plate", () -> new AsbestosArmorItem(MATERIAL, ArmorItem.Type.CHESTPLATE));
    public static final DeferredItem<AsbestosArmorItem> LEGS = ITEMS.register("asbestos_legs", () -> new AsbestosArmorItem(MATERIAL, ArmorItem.Type.LEGGINGS));
    public static final DeferredItem<AsbestosArmorItem> BOOTS = ITEMS.register("asbestos_boots", () -> new AsbestosArmorItem(MATERIAL, ArmorItem.Type.BOOTS));

    public static void register(IEventBus bus) {
        MATERIALS.register(bus);
        ITEMS.register(bus);
        bus.addListener(AsbestosContent::creative);
    }
    private static void creative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.COMBAT))
            for (var item : List.of(HELMET, PLATE, LEGS, BOOTS)) event.accept(item.get());
    }
    private AsbestosContent() {}
}
