package com.hbm.machine.reactor.pile;

import java.util.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Only the real new MK2 pile_rod; no deprecated rod or research-reactor plate is registered again. */
public final class PileFuelContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredItem<PileFuelItem> ITEM=ITEMS.register("pile_rod",PileFuelItem::new);
    public static void register(IEventBus bus){ITEMS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("control")?Arrays.stream(PileFuelType.values()).map(ITEM.get()::stack).toList():List.of();}
    private PileFuelContent(){}
}
