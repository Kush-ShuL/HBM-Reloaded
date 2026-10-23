package com.hbm.armor.cmb;

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

public final class CmbArmorContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<ArmorMaterial> MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, "hbm");
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> MATERIAL = MATERIALS.register("cmb",
        () -> new ArmorMaterial(Map.of(ArmorItem.Type.HELMET, 3, ArmorItem.Type.CHESTPLATE, 8,
            ArmorItem.Type.LEGGINGS, 6, ArmorItem.Type.BOOTS, 3), 50,
            BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.EMPTY),
            () -> Ingredient.of(HbmContent.item("ingot_combine_steel")),
            List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath("hbm", "cmb"))), 0, 0));
    public static final DeferredItem<CmbArmorItem> HELMET = ITEMS.register("cmb_helmet", () -> new CmbArmorItem(ArmorItem.Type.HELMET, "cmb_helmet"));
    public static final DeferredItem<CmbArmorItem> PLATE = ITEMS.register("cmb_plate", () -> new CmbArmorItem(ArmorItem.Type.CHESTPLATE, "cmb_plate"));
    public static final DeferredItem<CmbArmorItem> LEGS = ITEMS.register("cmb_legs", () -> new CmbArmorItem(ArmorItem.Type.LEGGINGS, "cmb_legs"));
    public static final DeferredItem<CmbArmorItem> BOOTS = ITEMS.register("cmb_boots", () -> new CmbArmorItem(ArmorItem.Type.BOOTS, "cmb_boots"));
    public static void register(IEventBus bus) { MATERIALS.register(bus); ITEMS.register(bus); bus.addListener(CmbArmorContent::creative); }
    private static void creative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.COMBAT)) armor().forEach(item -> event.accept(item.get()));
    }
    public static List<DeferredItem<CmbArmorItem>> armor() { return List.of(HELMET, PLATE, LEGS, BOOTS); }
    public static List<ItemStack> indexedItems() { return armor().stream().map(item -> new ItemStack(item.get())).toList(); }
    private CmbArmorContent() { }
}
