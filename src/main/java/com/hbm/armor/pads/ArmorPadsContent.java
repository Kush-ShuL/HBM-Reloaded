package com.hbm.armor.pads;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Exact three source tuples; no second armor inventory, charge store or living/hurt listener. */
public final class ArmorPadsContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredItem<ItemModPads> RUBBER = ITEMS.register("pads_rubber", () -> new ItemModPads("pads_rubber", .5F, false));
    public static final DeferredItem<ItemModPads> SLIME = ITEMS.register("pads_slime", () -> new ItemModPads("pads_slime", .25F, false));
    public static final DeferredItem<ItemModPads> STATIC = ITEMS.register("pads_static", () -> new ItemModPads("pads_static", .75F, true));
    public static void register(IEventBus bus) { ITEMS.register(bus); }
    public static List<ItemStack> indexedItems() { return List.of(new ItemStack(RUBBER.get()), new ItemStack(SLIME.get()), new ItemStack(STATIC.get())); }
    public static List<ItemStack> creativeStacks(String tab) { return "consumable".equals(tab) ? indexedItems() : List.of(); }
    private ArmorPadsContent() { }
}
