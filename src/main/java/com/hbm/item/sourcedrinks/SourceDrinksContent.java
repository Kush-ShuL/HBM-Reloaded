package com.hbm.item.sourcedrinks;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class SourceDrinksContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredItem<ItemMuchoMango> MUCHO_MANGO = ITEMS.register("mucho_mango", ItemMuchoMango::new);
    public static final DeferredItem<ItemBDCL> BDCL = ITEMS.register("bdcl", ItemBDCL::new);
    public static void register(IEventBus bus) { ITEMS.register(bus); }
    public static List<ItemStack> indexedItems() { return List.of(new ItemStack(MUCHO_MANGO.get()), new ItemStack(BDCL.get())); }
    public static List<ItemStack> creativeStacks(String tab) { return "consumable".equals(tab) ? indexedItems() : List.of(); }
    private SourceDrinksContent() { }
}
