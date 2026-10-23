package com.hbm.item.rttyhandheld;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class RttyHandheldContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredItem<RttyHandheldItem> PAGER = ITEMS.register("rtty_pager", () -> new RttyHandheldItem(RttyHandheldItem.Kind.PAGER));
    public static final DeferredItem<RttyHandheldItem> DESIGNATOR = ITEMS.register("rtty_designator", () -> new RttyHandheldItem(RttyHandheldItem.Kind.DESIGNATOR));
    public static void register(IEventBus bus) { ITEMS.register(bus); bus.addListener(RttyHandheldControl::register); bus.addListener(RttyPagerNotice::register); }
    public static List<ItemStack> creativeStacks(String tab) { return tab.equals("consumable") ? indexedItems() : List.of(); }
    public static List<ItemStack> indexedItems() { return List.of(new ItemStack(PAGER.get()), new ItemStack(DESIGNATOR.get())); }
    private RttyHandheldContent() { }
}
