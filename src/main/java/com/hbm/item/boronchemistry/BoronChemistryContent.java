package com.hbm.item.boronchemistry;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The genuine control-tab boron glassware; no second container/remainder backend. */
public final class BoronChemistryContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredItem<BoronChemistrySetItem> SET=ITEMS.register("chemistry_set_boron",BoronChemistrySetItem::new);
    public static void register(IEventBus bus){ITEMS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("control")?List.of(new ItemStack(SET.get())):List.of();}
    private BoronChemistryContent(){}
}
