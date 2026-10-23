package com.hbm.armor.dnt;

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

public final class DntArmorContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<ArmorMaterial> MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, "hbm");
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> MATERIAL = MATERIALS.register("dnt",
        () -> new ArmorMaterial(Map.of(ArmorItem.Type.HELMET, 1, ArmorItem.Type.CHESTPLATE, 1,
            ArmorItem.Type.LEGGINGS, 1, ArmorItem.Type.BOOTS, 1), 0,
            BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.EMPTY),
            () -> Ingredient.of(HbmContent.item("ingot_dineutronium")),
            List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath("hbm", "dnt"))), 0, 0));
    public static final DeferredItem<DntArmorItem> HELMET = ITEMS.register("dnt_helmet", () -> new DntArmorItem(ArmorItem.Type.HELMET, "dnt_helmet"));
    public static final DeferredItem<DntArmorItem> PLATE = ITEMS.register("dnt_plate", () -> new DntArmorItem(ArmorItem.Type.CHESTPLATE, "dnt_plate"));
    public static final DeferredItem<DntArmorItem> LEGS = ITEMS.register("dnt_legs", () -> new DntArmorItem(ArmorItem.Type.LEGGINGS, "dnt_legs"));
    public static final DeferredItem<DntArmorItem> BOOTS = ITEMS.register("dnt_boots", () -> new DntArmorItem(ArmorItem.Type.BOOTS, "dnt_boots"));
    public static void register(IEventBus bus) { MATERIALS.register(bus); ITEMS.register(bus); bus.addListener(DntArmorContent::creative); }
    private static void creative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.COMBAT)) armor().forEach(item -> event.accept(item.get()));
    }
    public static List<DeferredItem<DntArmorItem>> armor() { return List.of(HELMET, PLATE, LEGS, BOOTS); }
    public static List<ItemStack> indexedItems() { return armor().stream().map(item -> new ItemStack(item.get())).toList(); }
    private DntArmorContent() { }
}
