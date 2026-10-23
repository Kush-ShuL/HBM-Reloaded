package com.hbm.item.potatos;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class PotatosContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredItem<ItemPotatos> POTATOS = ITEMS.register("battery_potatos", ItemPotatos::new);
    public static void register(IEventBus bus) { ITEMS.register(bus); }
    public static List<ItemStack> creativeStacks(String tab) {
        // ItemBattery.getSubItems: chargeRate0 omits empty, dischargeRate100 adds full.
        return tab.equals("control") ? List.of(POTATOS.get().fullStack()) : List.of();
    }
    private PotatosContent() {}
}
