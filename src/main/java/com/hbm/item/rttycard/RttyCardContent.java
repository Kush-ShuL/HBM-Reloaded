package com.hbm.item.rttycard;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The source card only existed with its computer mod; the modern host is native CC:Tweaked. */
public final class RttyCardContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredItem<RttyCardItem> CARD = ITEMS.register("rtty_card", RttyCardItem::new);
    public static void register(IEventBus bus) {
        if (!available()) return;
        ITEMS.register(bus);
        com.hbm.compat.computercraft.rtty.RttyCardUpgrades.register(bus);
    }
    public static boolean available() { return ModList.get().isLoaded("computercraft"); }
    public static List<ItemStack> indexedItems() { return available() ? List.of(new ItemStack(CARD.get())) : List.of(); }
    public static List<ItemStack> creativeStacks(String tab) { return "machine".equals(tab) ? indexedItems() : List.of(); }
    private RttyCardContent() { }
}
