package com.hbm.item.canteen;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The sole active X5846 ItemCanteen registration. */
public final class CanteenContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredItem<ItemCanteen> VODKA = ITEMS.register("canteen_vodka", ItemCanteen::new);
    private CanteenContent() {}
    public static void register(IEventBus bus) { ITEMS.register(bus); }
    public static List<ItemStack> creativeStacks(String tab) {
        return tab.equals("consumable") ? List.of(new ItemStack(VODKA.get())) : List.of();
    }
}
