package com.hbm.item.nuclear.waste;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Eight actual source registrations; existing generic/vitrified waste and RBMK pellets stay canonical. */
public final class IsotopicWasteContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredItem<LongLivedWasteItem> LONG = ITEMS.register("nuclear_waste_long", LongLivedWasteItem::new);
    public static final DeferredItem<LongLivedWasteItem> LONG_TINY = ITEMS.register("nuclear_waste_long_tiny", LongLivedWasteItem::new);
    public static final DeferredItem<ShortLivedWasteItem> SHORT = ITEMS.register("nuclear_waste_short", ShortLivedWasteItem::new);
    public static final DeferredItem<ShortLivedWasteItem> SHORT_TINY = ITEMS.register("nuclear_waste_short_tiny", ShortLivedWasteItem::new);
    public static final DeferredItem<LongLivedWasteItem> LONG_DEPLETED = ITEMS.register("nuclear_waste_long_depleted", LongLivedWasteItem::new);
    public static final DeferredItem<LongLivedWasteItem> LONG_DEPLETED_TINY = ITEMS.register("nuclear_waste_long_depleted_tiny", LongLivedWasteItem::new);
    public static final DeferredItem<ShortLivedWasteItem> SHORT_DEPLETED = ITEMS.register("nuclear_waste_short_depleted", ShortLivedWasteItem::new);
    public static final DeferredItem<ShortLivedWasteItem> SHORT_DEPLETED_TINY = ITEMS.register("nuclear_waste_short_depleted_tiny", ShortLivedWasteItem::new);
    public static final List<DeferredItem<LongLivedWasteItem>> LONG_ITEMS = List.of(LONG, LONG_TINY, LONG_DEPLETED, LONG_DEPLETED_TINY);
    public static final List<DeferredItem<ShortLivedWasteItem>> SHORT_ITEMS = List.of(SHORT, SHORT_TINY, SHORT_DEPLETED, SHORT_DEPLETED_TINY);

    public static void register(IEventBus bus) { ITEMS.register(bus); }

    public static List<ItemStack> allStacks() {
        var result = new ArrayList<ItemStack>();
        LONG.get().creativeStacks().forEach(result::add);
        LONG_TINY.get().creativeStacks().forEach(result::add);
        SHORT.get().creativeStacks().forEach(result::add);
        SHORT_TINY.get().creativeStacks().forEach(result::add);
        LONG_DEPLETED.get().creativeStacks().forEach(result::add);
        LONG_DEPLETED_TINY.get().creativeStacks().forEach(result::add);
        SHORT_DEPLETED.get().creativeStacks().forEach(result::add);
        SHORT_DEPLETED_TINY.get().creativeStacks().forEach(result::add);
        return List.copyOf(result);
    }

    public static List<ItemStack> creativeStacks(String tab) { return "parts".equals(tab) ? allStacks() : List.of(); }
    private IsotopicWasteContent() {}
}
