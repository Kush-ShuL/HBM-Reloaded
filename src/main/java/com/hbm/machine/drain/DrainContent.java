package com.hbm.machine.drain;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
/** Actual source drain and its original replaceable, nondropping spill layer. */
public final class DrainContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
 public static final DeferredBlock<DrainBlock> BLOCK=BLOCKS.register("machine_drain",DrainBlock::new);
 public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem("machine_drain",BLOCK);
 public static final DeferredBlock<OilSpillBlock> OIL=BLOCKS.register("oil_spill",()->new OilSpillBlock());
 public static final DeferredItem<BlockItem> OIL_ITEM=ITEMS.registerSimpleBlockItem("oil_spill",OIL);
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<DrainEntity>> ENTITY=ENTITIES.register("machine_drain",()->BlockEntityType.Builder.of(DrainEntity::new,BLOCK.get()).build(null));
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):tab.equals("block")?List.of(new ItemStack(OIL_ITEM.get())):List.of();}
 private DrainContent(){}
}
