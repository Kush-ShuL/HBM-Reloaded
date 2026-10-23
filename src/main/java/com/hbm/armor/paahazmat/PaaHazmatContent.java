package com.hbm.armor.paahazmat;

import java.util.List;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class PaaHazmatContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredItem<PaaHazmatMaskItem> HELMET = ITEMS.register("hazmat_paa_helmet", PaaHazmatMaskItem::new);
    public static final DeferredItem<PaaHazmatArmorItem> PLATE = ITEMS.register("hazmat_paa_plate", () -> new PaaHazmatArmorItem(ArmorItem.Type.CHESTPLATE, "hazmat_paa_plate"));
    public static final DeferredItem<PaaHazmatArmorItem> LEGS = ITEMS.register("hazmat_paa_legs", () -> new PaaHazmatArmorItem(ArmorItem.Type.LEGGINGS, "hazmat_paa_legs"));
    public static final DeferredItem<PaaHazmatArmorItem> BOOTS = ITEMS.register("hazmat_paa_boots", () -> new PaaHazmatArmorItem(ArmorItem.Type.BOOTS, "hazmat_paa_boots"));
    public static void register(IEventBus bus) { ITEMS.register(bus); bus.addListener(PaaHazmatContent::creative); }
    private static void creative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.COMBAT)) indexedItems().forEach(event::accept);
    }
    public static List<ItemStack> indexedItems() { return ITEMS.getEntries().stream().map(item -> new ItemStack(item.get())).toList(); }
    private PaaHazmatContent() { }
}
