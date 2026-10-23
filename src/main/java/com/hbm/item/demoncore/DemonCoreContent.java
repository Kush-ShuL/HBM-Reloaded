package com.hbm.item.demoncore;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Only the missing source ItemDemonCore is new; the real plain closed core remains canonical. */
public final class DemonCoreContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredItem<ItemDemonCore> OPEN=ITEMS.register("demon_core_open",ItemDemonCore::new);
    public static void register(IEventBus bus){ITEMS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("nuke")?List.of(new ItemStack(OPEN.get())):List.of();}
    private DemonCoreContent(){}
}
