package com.hbm.item.debugwand;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class DebugWandContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredItem<DebugWandItem> WAND=ITEMS.register("wand_d",DebugWandItem::new);
    public static void register(IEventBus bus){ITEMS.register(bus);com.hbm.world.structure.CraneDebugBuild.register();}
    public static List<ItemStack> indexedItems(){return List.of(new ItemStack(WAND.get()));}
    public static List<ItemStack> creativeStacks(String tab){return "consumable".equals(tab)?indexedItems():List.of();}
    private DebugWandContent(){}
}
