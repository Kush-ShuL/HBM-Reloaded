package com.hbm.armor.charms;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ArmorCharmsContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredItem<ItemModBathwater> BATHWATER = ITEMS.register("bathwater", () -> new ItemModBathwater(false));
    public static final DeferredItem<ItemModBathwater> BATHWATER_MK2 = ITEMS.register("bathwater_mk2", () -> new ItemModBathwater(true));
    public static final DeferredItem<ItemModMedal> MEDAL = ITEMS.register("medal_liquidator", ItemModMedal::new);
    public static void register(IEventBus bus) { ITEMS.register(bus); }
    public static List<ItemStack> indexedItems() { return List.of(new ItemStack(BATHWATER.get()), new ItemStack(BATHWATER_MK2.get()), new ItemStack(MEDAL.get())); }
    public static List<ItemStack> creativeStacks(String tab) { return "consumable".equals(tab) ? indexedItems() : List.of(); }
    private ArmorCharmsContent() { }
}
