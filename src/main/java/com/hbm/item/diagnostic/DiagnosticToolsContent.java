package com.hbm.item.diagnostic;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The two actual source consumable-tab tools; neither identity is present in BasicContent. */
public final class DiagnosticToolsContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredItem<ColtanCompassItem> COLTAN = ITEMS.register("coltan_tool", ColtanCompassItem::new);
    public static final DeferredItem<DigammaDiagnosticItem> DIGAMMA = ITEMS.register("digamma_diagnostic", DigammaDiagnosticItem::new);

    public static void register(IEventBus bus) { ITEMS.register(bus); }
    public static List<ItemStack> creativeStacks(String tab) {
        return tab.equals("consumable") ? List.of(new ItemStack(COLTAN.get()), new ItemStack(DIGAMMA.get())) : List.of();
    }
    private DiagnosticToolsContent() { }
}
