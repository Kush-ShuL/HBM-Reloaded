package com.hbm.armor.blackjack;

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

public final class BlackjackContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<ArmorMaterial> MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, "hbm");
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> MATERIAL = MATERIALS.register("blackjack", () -> new ArmorMaterial(
            Map.of(ArmorItem.Type.HELMET, 3, ArmorItem.Type.CHESTPLATE, 8, ArmorItem.Type.LEGGINGS, 6, ArmorItem.Type.BOOTS, 3), 0,
            BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.EMPTY), () -> Ingredient.of(HbmContent.item("plate_armor_lunar")),
            List.of(new ArmorMaterial.Layer(id("blackjack"))), 0, 0));
    public static final DeferredItem<ArmorBlackjack> HELMET = ITEMS.register("bj_helmet", () -> new ArmorBlackjack(ArmorItem.Type.HELMET, false));
    public static final DeferredItem<ArmorBlackjack> PLATE = ITEMS.register("bj_plate", () -> new ArmorBlackjack(ArmorItem.Type.CHESTPLATE, false));
    public static final DeferredItem<ArmorBlackjack> JETPACK = ITEMS.register("bj_plate_jetpack", () -> new ArmorBlackjack(ArmorItem.Type.CHESTPLATE, true));
    public static final DeferredItem<ArmorBlackjack> LEGS = ITEMS.register("bj_legs", () -> new ArmorBlackjack(ArmorItem.Type.LEGGINGS, false));
    public static final DeferredItem<ArmorBlackjack> BOOTS = ITEMS.register("bj_boots", () -> new ArmorBlackjack(ArmorItem.Type.BOOTS, false));
    public static final DeferredItem<ItemPancake> PANCAKE = ITEMS.register("pancake", ItemPancake::new);
    public static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath("hbm", path); }
    public static List<DeferredItem<ArmorBlackjack>> armor() { return List.of(HELMET, PLATE, JETPACK, LEGS, BOOTS); }
    public static void register(IEventBus bus) { MATERIALS.register(bus); ITEMS.register(bus); bus.addListener(BlackjackContent::creative); }
    private static void creative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.COMBAT)) armor().forEach(item -> event.accept(item.get()));
    }
    public static List<ItemStack> indexedItems() { return List.of(new ItemStack(HELMET.get()), new ItemStack(PLATE.get()), new ItemStack(JETPACK.get()), new ItemStack(LEGS.get()), new ItemStack(BOOTS.get()), new ItemStack(PANCAKE.get())); }
    public static List<ItemStack> creativeStacks(String tab) { return "consumable".equals(tab) ? List.of(new ItemStack(PANCAKE.get())) : List.of(); }
    private BlackjackContent() { }
}
