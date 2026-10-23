package com.hbm.item.archineer;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ArchineerWrenchContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredItem<ArchineerWrenchItem> WRENCH=ITEMS.register("wrench_archineer",ArchineerWrenchItem::new);
    public static void register(IEventBus bus){ITEMS.register(bus);bus.addListener(ArchineerWrenchContent::setup);}
    private static void setup(FMLCommonSetupEvent event){event.enqueueWork(()->WRENCH.get().registerSourceType());}
    public static List<ItemStack> indexedItems(){return List.of(new ItemStack(WRENCH.get()));}
    public static List<ItemStack> creativeStacks(String tab){return "control".equals(tab)?indexedItems():List.of();}
    private ArchineerWrenchContent(){}
}
