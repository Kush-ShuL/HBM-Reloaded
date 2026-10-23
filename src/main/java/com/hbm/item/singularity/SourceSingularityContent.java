package com.hbm.item.singularity;
import java.util.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
/** Actual three ItemDrop dependencies, not inert crafting substitutes for their source behavior. */
public final class SourceSingularityContent {
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 public static final DeferredItem<SourceSingularityItem> SINGULARITY=ITEMS.register("singularity",()->new SourceSingularityItem(SourceSingularityItem.Kind.SINGULARITY));
 public static final DeferredItem<SourceSingularityItem> BLACK_HOLE=ITEMS.register("black_hole",()->new SourceSingularityItem(SourceSingularityItem.Kind.BLACK_HOLE));
 public static final DeferredItem<SourceSingularityItem> XEN=ITEMS.register("crystal_xen",()->new SourceSingularityItem(SourceSingularityItem.Kind.XEN));
 public static void register(IEventBus bus){ITEMS.register(bus);}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("control")?List.of(new ItemStack(SINGULARITY.get()),new ItemStack(BLACK_HOLE.get()),new ItemStack(XEN.get())):List.of();}
 private SourceSingularityContent(){}
}
