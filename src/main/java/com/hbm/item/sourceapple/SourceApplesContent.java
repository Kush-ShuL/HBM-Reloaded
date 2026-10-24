package com.hbm.item.sourceapple;

import java.util.List;
import java.util.stream.Stream;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class SourceApplesContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final List<DeferredItem<SourceMetalAppleItem>> VARIANTS = registerVariants();
    private static List<DeferredItem<SourceMetalAppleItem>> registerVariants() {
        var items = new java.util.ArrayList<DeferredItem<SourceMetalAppleItem>>();
        for (var kind : List.of(SourceMetalAppleItem.Kind.LEAD, SourceMetalAppleItem.Kind.SCHRABIDIUM)) {
            for (int variant = 0; variant < 3; variant++) {
                int fixedVariant = variant;
                String id = kind.id + "_" + List.of("nugget", "ingot", "block").get(variant);
                items.add(ITEMS.register(id, () -> new SourceMetalAppleItem(kind, fixedVariant)));
            }
        }
        return List.copyOf(items);
    }
    public static final DeferredItem<SourceMetalAppleItem> EUPHEMIUM = ITEMS.register("apple_euphemium",
            () -> new SourceMetalAppleItem(SourceMetalAppleItem.Kind.EUPHEMIUM));

    public static void register(IEventBus bus) { ITEMS.register(bus); }
    public static List<SourceMetalAppleItem> fixedItems() { return VARIANTS.stream().map(DeferredItem::get).toList(); }
    public static List<ItemStack> creativeStacks(String tab) {
        // ItemAppleEuphemium explicitly setCreativeTab(null); its real recipe remains obtainable.
        if (!tab.equals("consumable")) return List.of();
        return fixedItems().stream().map(ItemStack::new).toList();
    }
    public static List<ItemStack> indexedItems() {
        return Stream.concat(creativeStacks("consumable").stream(), Stream.of(new ItemStack(EUPHEMIUM.get()))).toList();
    }
    private SourceApplesContent() { }
}
