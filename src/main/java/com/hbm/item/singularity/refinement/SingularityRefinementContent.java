package com.hbm.item.singularity.refinement;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
/** The three remaining genuine ItemDrop singularities, with their real existing vortex implementations. */
public final class SingularityRefinementContent {
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 public static final DeferredItem<SingularityRefinementItem> COUNTER_RESONANT=ITEMS.register("singularity_counter_resonant",()->new SingularityRefinementItem(SingularityRefinementItem.Kind.COUNTER));
 public static final DeferredItem<SingularityRefinementItem> SUPER_HEATED=ITEMS.register("singularity_super_heated",()->new SingularityRefinementItem(SingularityRefinementItem.Kind.HEATED));
 public static final DeferredItem<SingularityRefinementItem> SPARK=ITEMS.register("singularity_spark",()->new SingularityRefinementItem(SingularityRefinementItem.Kind.SPARK));
 public static void register(IEventBus bus){ITEMS.register(bus);}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("control")?List.of(new ItemStack(COUNTER_RESONANT.get()),new ItemStack(SUPER_HEATED.get()),new ItemStack(SPARK.get())):List.of();}
 private SingularityRefinementContent(){}
}
