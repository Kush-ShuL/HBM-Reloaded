package com.hbm.item.sourceapple;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class SourceApplesContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredItem<SourceMetalAppleItem> LEAD = ITEMS.register("apple_lead",
            () -> new SourceMetalAppleItem(SourceMetalAppleItem.Kind.LEAD));
    public static final DeferredItem<SourceMetalAppleItem> SCHRABIDIUM = ITEMS.register("apple_schrabidium",
            () -> new SourceMetalAppleItem(SourceMetalAppleItem.Kind.SCHRABIDIUM));
    public static final DeferredItem<SourceMetalAppleItem> EUPHEMIUM = ITEMS.register("apple_euphemium",
            () -> new SourceMetalAppleItem(SourceMetalAppleItem.Kind.EUPHEMIUM));

    public static void register(IEventBus bus) { ITEMS.register(bus); }
    public static List<SourceMetalAppleItem> subtypedItems() { return List.of(LEAD.get(), SCHRABIDIUM.get()); }
    public static List<ItemStack> creativeStacks(String tab) {
        // ItemAppleEuphemium explicitly setCreativeTab(null); its real recipe remains obtainable.
        if (!tab.equals("consumable")) return List.of();
        return subtypedItems().stream().flatMap(item -> IntStream.range(0, 3).mapToObj(item::stack)).toList();
    }
    public static List<ItemStack> indexedItems() {
        return Stream.concat(creativeStacks("consumable").stream(), Stream.of(EUPHEMIUM.get().stack(0))).toList();
    }
    private SourceApplesContent() { }
}
