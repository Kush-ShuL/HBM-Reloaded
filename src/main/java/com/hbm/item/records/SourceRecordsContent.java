package com.hbm.item.records;

import java.util.List;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The three visible original HBM records; existing hidden record_glass is not registered again. */
public final class SourceRecordsContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredItem<SourceRecordItem> LAMBDA_CORE = ITEMS.register("record_lc", () -> new SourceRecordItem("lc"));
    public static final DeferredItem<SourceRecordItem> SECTOR_SWEEP = ITEMS.register("record_ss", () -> new SourceRecordItem("ss"));
    public static final DeferredItem<SourceRecordItem> VORTAL_COMBAT = ITEMS.register("record_vc", () -> new SourceRecordItem("vc"));
    public static void register(IEventBus bus) { ITEMS.register(bus); bus.addListener(SourceRecordsContent::creative); }
    private static void creative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES)) indexedItems().forEach(event::accept);
    }
    public static List<ItemStack> indexedItems() { return List.of(new ItemStack(LAMBDA_CORE.get()), new ItemStack(SECTOR_SWEEP.get()), new ItemStack(VORTAL_COMBAT.get())); }
    private SourceRecordsContent() { }
}
