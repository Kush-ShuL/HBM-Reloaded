package com.hbm.armor.gasmaskmods;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class GasmaskAttachmentsContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredItem<ItemModGasmask> MASK = ITEMS.register("attachment_mask", () -> new ItemModGasmask(false));
    public static final DeferredItem<ItemModGasmask> MONO = ITEMS.register("attachment_mask_mono", () -> new ItemModGasmask(true));
    public static void register(IEventBus bus) { ITEMS.register(bus); }
    public static List<ItemStack> indexedItems() { return List.of(new ItemStack(MASK.get()), new ItemStack(MONO.get())); }
    public static List<ItemStack> creativeStacks(String tab) { return "consumable".equals(tab) ? indexedItems() : List.of(); }
    private GasmaskAttachmentsContent() { }
}
