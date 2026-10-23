package com.hbm.item.originalgasmask;

import java.util.List;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class OriginalGasMaskContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredItem<OriginalGasMaskItem> MASK = ITEMS.register("gas_mask", OriginalGasMaskItem::new);
    public static void register(IEventBus bus) { ITEMS.register(bus); bus.addListener(OriginalGasMaskContent::creative); }
    private static void creative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.COMBAT)) event.accept(MASK.get());
    }
    public static List<ItemStack> indexedItems() { return List.of(new ItemStack(MASK.get())); }
    private OriginalGasMaskContent() { }
}
