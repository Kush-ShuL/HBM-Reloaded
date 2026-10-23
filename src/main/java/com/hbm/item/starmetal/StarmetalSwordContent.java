package com.hbm.item.starmetal;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

public final class StarmetalSwordContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredItem<StarmetalSwordItem> SWORD=ITEMS.register("starmetal_sword",StarmetalSwordItem::new);
    public static void register(IEventBus bus){ITEMS.register(bus);bus.addListener((BuildCreativeModeTabContentsEvent event)->{if(event.getTabKey()==CreativeModeTabs.COMBAT)event.accept(SWORD.get());});}
    // Original ItemSword constructor's vanilla combat tab is retained.
    public static List<ItemStack> indexedItems(){return List.of(new ItemStack(SWORD.get()));}
    private StarmetalSwordContent(){}
}
