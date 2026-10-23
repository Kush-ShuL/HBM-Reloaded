package com.hbm.item.analysis;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Exactly the source consumable-tab analysis tool; no separate network or scanner registration. */
public final class AnalysisToolContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredItem<AnalysisToolItem> TOOL=ITEMS.register("analysis_tool",AnalysisToolItem::new);
    public static void register(IEventBus bus){ITEMS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("consumable")?List.of(new ItemStack(TOOL.get())):List.of();}
    private AnalysisToolContent(){}
}
