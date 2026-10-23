package com.hbm.world.structure.dependencies.fusionhatch;
import java.util.List;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
/** Original hidden source registration; machine/energy interfaces would be fabricated for this literal plain Block. */
public final class FusionHatchContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 public static final DeferredBlock<FusionHatchBlock> HATCH=BLOCKS.register("fusion_hatch",FusionHatchBlock::new);
 public static final DeferredItem<BlockItem> HATCH_ITEM=ITEMS.registerSimpleBlockItem("fusion_hatch",HATCH);
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}
 public static List<ItemStack> allStacks(){return List.of(new ItemStack(HATCH_ITEM.get()));}
 public static List<ItemStack> creativeStacks(String tab){return List.of();}
 private FusionHatchContent(){}
}
