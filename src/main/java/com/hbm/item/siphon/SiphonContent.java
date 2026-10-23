package com.hbm.item.siphon;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class SiphonContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredItem<SiphonItem> SIPHON=ITEMS.register("siphon",SiphonItem::new);
    public static void register(IEventBus bus){ITEMS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("control")?List.of(new ItemStack(SIPHON.get())):List.of();}
    private SiphonContent(){}
}
