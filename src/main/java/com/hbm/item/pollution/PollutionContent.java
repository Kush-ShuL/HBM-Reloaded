package com.hbm.item.pollution;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Exactly the actual source pollution detector; existing pollution persistence/config remain canonical. */
public final class PollutionContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredItem<PollutionDetectorItem> DETECTOR=ITEMS.register("pollution_detector",PollutionDetectorItem::new);
    public static void register(IEventBus bus){ITEMS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("consumable")?indexedItems():List.of();}
    public static List<ItemStack> indexedItems(){return List.of(new ItemStack(DETECTOR.get()));}
    private PollutionContent(){}
}
