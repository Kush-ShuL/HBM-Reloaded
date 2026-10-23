package com.hbm.item.muffler;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Actual source machine upgrade identity, separate from speed/health/radius upgrades. */
public final class MufflerContent {
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 public static final DeferredItem<MufflerItem> MUFFLER=ITEMS.register("upgrade_muffler",MufflerItem::new);
 public static void register(IEventBus bus){ITEMS.register(bus);}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("parts")?List.of(new ItemStack(MUFFLER.get())):List.of();}
 private MufflerContent(){}
}
