package com.hbm.item.sourcesnacks;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class SourceSnacksContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredItem<ItemCottonCandy> COTTON_CANDY = ITEMS.register("cotton_candy", ItemCottonCandy::new);
    public static final DeferredItem<ItemTemFlakes> TEM_FLAKES = ITEMS.register("tem_flakes", ItemTemFlakes::new);
    public static void register(IEventBus bus) { ITEMS.register(bus); }
    public static List<ItemStack> indexedItems() {
        return List.of(new ItemStack(COTTON_CANDY.get()), TEM_FLAKES.get().stack(0), TEM_FLAKES.get().stack(1), TEM_FLAKES.get().stack(2));
    }
    public static List<ItemStack> creativeStacks(String tab) { return "consumable".equals(tab) ? indexedItems() : List.of(); }
    private SourceSnacksContent() { }
}
