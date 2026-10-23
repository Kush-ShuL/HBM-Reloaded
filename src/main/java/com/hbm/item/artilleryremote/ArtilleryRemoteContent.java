package com.hbm.item.artilleryremote;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ArtilleryRemoteContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredItem<ArtilleryRemoteItem> REMOTE=ITEMS.register("designator_arty_range",ArtilleryRemoteItem::new);
    public static void register(IEventBus bus){ITEMS.register(bus);}
    public static List<ItemStack> indexedItems(){return List.of(new ItemStack(REMOTE.get()));}
    public static List<ItemStack> creativeStacks(String tab){return "missile".equals(tab)?indexedItems():List.of();}
    private ArtilleryRemoteContent(){}
}
