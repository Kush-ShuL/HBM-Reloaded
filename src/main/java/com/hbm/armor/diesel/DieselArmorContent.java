package com.hbm.armor.diesel;

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
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class DieselArmorContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<ArmorMaterial> MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, "hbm");
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> MATERIAL = MATERIALS.register("dieselsuit", () -> new ArmorMaterial(
        Map.of(ArmorItem.Type.HELMET, 3, ArmorItem.Type.CHESTPLATE, 8, ArmorItem.Type.LEGGINGS, 6, ArmorItem.Type.BOOTS, 3), 0,
        BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.EMPTY), () -> Ingredient.of(HbmContent.item("plate_copper")),
        List.of(new ArmorMaterial.Layer(id("dieselsuit"))), 0, 0));
    public static final DeferredItem<ArmorDiesel> HELMET = ITEMS.register("dieselsuit_helmet", () -> new ArmorDiesel(ArmorItem.Type.HELMET, "dieselsuit_helmet"));
    public static final DeferredItem<ArmorDiesel> PLATE = ITEMS.register("dieselsuit_plate", () -> new ArmorDiesel(ArmorItem.Type.CHESTPLATE, "dieselsuit_plate"));
    public static final DeferredItem<ArmorDiesel> LEGS = ITEMS.register("dieselsuit_legs", () -> new ArmorDiesel(ArmorItem.Type.LEGGINGS, "dieselsuit_legs"));
    public static final DeferredItem<ArmorDiesel> BOOTS = ITEMS.register("dieselsuit_boots", () -> new ArmorDiesel(ArmorItem.Type.BOOTS, "dieselsuit_boots"));
    public static ResourceLocation id(String name) { return ResourceLocation.fromNamespaceAndPath("hbm", name); }
    public static void register(IEventBus bus) { MATERIALS.register(bus); ITEMS.register(bus); bus.addListener(DieselArmorContent::creative); }
    private static void creative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.COMBAT)) indexedItems().forEach(event::accept);
    }
    public static List<ItemStack> indexedItems() { return ITEMS.getEntries().stream().map(item -> new ItemStack(item.get())).toList(); }
    private DieselArmorContent() { }
}
