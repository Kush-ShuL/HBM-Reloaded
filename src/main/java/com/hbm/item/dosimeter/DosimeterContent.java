package com.hbm.item.dosimeter;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The real source consumable-tab item; no duplicate Geiger, radiation or HUD backend. */
public final class DosimeterContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredItem<DosimeterItem> DOSIMETER = ITEMS.register("dosimeter", DosimeterItem::new);

    public static void register(IEventBus bus) { ITEMS.register(bus); }
    public static List<ItemStack> creativeStacks(String tab) {
        return tab.equals("consumable") ? List.of(new ItemStack(DOSIMETER.get())) : List.of();
    }
    private DosimeterContent() {}
}
